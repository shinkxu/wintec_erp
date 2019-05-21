package erp.chain.service;

import com.saas.common.Constants;
import com.saas.common.util.GzipUtils;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.*;
import erp.chain.domain.Package;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.o2o.VipType;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.PaymentMapper;
import erp.chain.service.o2o.VipBookService;
import erp.chain.service.setting.RoleService;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xumx on 2016/11/4.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class DataService extends BaseService{

    @Autowired
    private RoleService roleService;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipBookService vipBookService;
    @Autowired
    private PaymentMapper paymentMapper;

    public Map<String, Object> checkData(String json, BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(json) || StringUtils.isEmpty(business)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            Map<String, String> jsonMap = JsonUtils.json2Map(json);
            String rPosId = Constants.POS_DATA_REDIS_PREFIX + business + "_" + posId;
            String status = PartitionCacheUtils.hget(rPosId, "status");

            if(StringUtils.isEmpty(status)){//无缓存数据
                //向Redis中放入数据
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Map<String, String> rPosMap = new HashMap<>();
                rPosMap.put("status", Constants.POS_CHECK_STATUS_CHECK);
                rPosMap.put("last_query_time", dateFormat.format(new Date()));
                List<String> list = new ArrayList<>();
                for(String key : jsonMap.keySet()){
                    list.add(key);
                }
                rPosMap.put("table_name", JsonUtils.list2json(list));
                PartitionCacheUtils.hmset(rPosId, rPosMap);
                //向查询服务发送消息
                Map<String, Object> map = new HashMap<>();
                map.put("pos_id", rPosId);
                map.put("table_timestamp", jsonMap);
                map.put("business", business);
                try{
                    PartitionCacheUtils.publish(PropertyUtils.getDefault("topic.redis.data.query"), JSONObject.fromObject(map).toString());
                }
                catch(Exception e){
                    PartitionCacheUtils.hset(rPosId, "status", Constants.POS_CHECK_STATUS_NO_DATA);
                    resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                    resultMap.put("Message", "无数据");
                    resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                    log.error("POS-{}-{} query data: querying. error: {}", posId, business, e.getClass().getSimpleName() + " - " + e.getMessage());
                    return resultMap;
                }
                //返回结果
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message", "数据查询中");
                resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                log.info("POS-{}-{} query data: querying.", posId, business);
            }
            else if(Constants.POS_CHECK_STATUS_CHECK.equals(status)){//正在查询
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message", "数据查询中");
                resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                String lastQueryTimeStr = PartitionCacheUtils.hget(rPosId, "last_query_time");
                Date lastQueryTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastQueryTimeStr);
                if(new Date().getTime() - lastQueryTime.getTime() > 30 * 60 * 1000){
                    PartitionCacheUtils.hset(rPosId, "status", Constants.POS_CHECK_STATUS_NO_DATA);
                }
                log.info("POS-{}-{} query data: querying.", posId, business);
            }
            else if(Constants.POS_CHECK_STATUS_DATA.equals(status)){//有数据
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message", "有数据");
                resultMap.put("NewData", Constants.POS_NEW_DATA);
                log.info("POS-{}-{} query data: have data.", posId, business);
            }
            else if(Constants.POS_CHECK_STATUS_NO_DATA.equals(status)){//无数据
                //控制访问间隔时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastQueryTimeStr = PartitionCacheUtils.hget(rPosId, "last_query_time");
                if(lastQueryTimeStr != null){
                    Date lastQueryTime = dateFormat.parse(lastQueryTimeStr);
                    Date now = new Date();
                    if(now.getTime() - lastQueryTime.getTime() < 1000 * 60 * 0.5){
                        resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                        resultMap.put("Message", "无数据");
                        resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                        log.info("POS-{}-{} query data: no data.", posId, business);
                    }
                    else{
                        //向查询服务发送消息
                        Map<String, Object> map = new HashMap<>();
                        map.put("pos_id", rPosId);
                        map.put("table_timestamp", jsonMap);
                        map.put("business", business);
                        try{
                            PartitionCacheUtils.publish(PropertyUtils.getDefault("topic.redis.data.query"), JSONObject.fromObject(map).toString());
                        }
                        catch(Exception e){
                            PartitionCacheUtils.hset(rPosId, "status", Constants.POS_CHECK_STATUS_NO_DATA);
                            resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                            resultMap.put("Message", "无数据");
                            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                            return resultMap;
                        }
                        //覆盖Redis中数据
                        Map<String, String> rPosMap = new HashMap<String, String>();
                        rPosMap.put("status", Constants.POS_CHECK_STATUS_CHECK);
                        rPosMap.put("last_query_time", dateFormat.format(now));
                        List<String> list = new ArrayList<>();
                        for(String key : jsonMap.keySet()){
                            list.add(key);
                        }
                        rPosMap.put("table_name", JsonUtils.list2json(list));
                        PartitionCacheUtils.hmset(rPosId, rPosMap);
                        //返回结果
                        resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                        resultMap.put("Message", "数据查询中");
                        resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                        log.info("POS-{}-{} query data: querying.", posId, business);
                    }
                }
                else{
                    resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                    resultMap.put("Message", "无数据");
                    resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                    log.info("POS-{}-{} query data: no data.", posId, business);
                }
            }
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据查询异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.checkData({},{},{}) - {} - {}", json, posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    public Map<String, Object> downloadData(String json, BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(business)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            String rPosId = Constants.POS_DATA_REDIS_PREFIX + business + "_" + posId;
            String status = PartitionCacheUtils.hget(rPosId, "status");

            if(StringUtils.isEmpty(status) || !Constants.POS_CHECK_STATUS_DATA.equals(status)){
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message", "无数据");
                resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                return resultMap;
            }
            String tableName = null;
            String beginIndex = null;
            String endIndex = null;
            String nextTimestamp = null;
            //获取json中数据，如果无数据，说明是第一次访问
            if(StringUtils.isNotEmpty(json)){
                Map<String, String> m = JsonUtils.json2Map(json);
                tableName = m.get("LastTableName");
                beginIndex = m.get("BeginIndex");
                endIndex = m.get("EndIndex");
            }
            log.info("POS-{}-{} download data, tableName: {}, beginIndex: {}, endIndex: {}", posId, business, tableName, beginIndex, endIndex);
            String tableNamesStr = PartitionCacheUtils.hget(rPosId, "table_name");
            //判断是否缓存中是否记录表名
            if(StringUtils.isEmpty(tableNamesStr)){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无数据");
                resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                PartitionCacheUtils.hset(rPosId, "status", Constants.POS_CHECK_STATUS_NO_DATA);
                return resultMap;
            }
            //缓存中记录的表名
            List<String> tableNames = JsonUtils.json2List(tableNamesStr);
            //下发数据集合
            List<String> dataRows = new ArrayList<>();
            boolean haveData = false;
            boolean nextDownload = false;
            for(int j = 0; j < tableNames.size(); j++){
                String tn = tableNames.get(j);
                //cache中的key
                String dataKey = rPosId + "." + tn;
                //tableName为空说明是第一次访问，json参数无数据
                if(StringUtils.isEmpty(tableName) && dataRows.size() == 0){
                    nextDownload = true;
                    //当前表的数据量
                    String countStr = PartitionCacheUtils.hget(dataKey, "count");
                    //判断是否为空或0，无新数据的表count为0
                    if(StringUtils.isNotEmpty(countStr)){
                        int count = Integer.valueOf(countStr);
                        if(count > 0){
                            //缓存中表数据
                            String rowsStr = PartitionCacheUtils.hget(dataKey, "rows");
                            String rowsStatusStr = PartitionCacheUtils.hget(dataKey, "rows_status");
                            JSONArray rows = JsonUtils.str2JsonArr(rowsStr);
                            List<String> rowsStatus = JsonUtils.json2List(rowsStatusStr);
                            //记录下发数据量
                            int rowsCount = 0;
                            for(int i = 0; i < rows.size(); i++){
                                //数据量满时跳出循环
                                if(rowsCount >= Constants.POS_DATA_MAX_ROWS){
                                    break;
                                }
                                //将未下发数据添加到下发集合中
                                if(rowsStatus.get(i).equals("0")){
                                    if(StringUtils.isEmpty(beginIndex)){
                                        beginIndex = String.valueOf(i + 1);
                                    }
                                    dataRows.add(rows.get(i).toString());
                                    rowsCount++;
                                    endIndex = String.valueOf(i + 1);
                                }
                            }
                            if(dataRows.size() > 0){
                                if(Integer.valueOf(endIndex) < rows.size()){
                                    haveData = true;
                                }
                                tableName = tn;
                                nextTimestamp = PartitionCacheUtils.hget(dataKey, "nextTimestamp");
                                //break;
                            }
                        }
                    }
                }
                else if(StringUtils.isNotEmpty(tableName) && tableName.equals(tn) && dataRows.size() == 0){
                    nextDownload = true;
                    String countStr = PartitionCacheUtils.hget(dataKey, "count");
                    if(StringUtils.isNotEmpty(countStr)){
                        int count = Integer.valueOf(countStr);
                        if(count > 0){
                            String rowsStr = PartitionCacheUtils.hget(dataKey, "rows");
                            String rowsStatusStr = PartitionCacheUtils.hget(dataKey, "rows_status");
                            JSONArray rows = JsonUtils.str2JsonArr(rowsStr);
                            List<String> rowsStatus = JsonUtils.json2List(rowsStatusStr);
                            //修改上次下发成功数据的状态
                            if(StringUtils.isNotEmpty(beginIndex) && StringUtils.isNotEmpty(endIndex)){
                                for(int i = Integer.valueOf(beginIndex) - 1; i < Integer.valueOf(endIndex) && i < rowsStatus.size(); i++){
                                    rowsStatus.set(i, "1");
                                }
                                PartitionCacheUtils.hset(dataKey, "rows_status", JsonUtils.list2json(rowsStatus));
                            }
                            //当前表数据未全部下发
                            if((StringUtils.isNotEmpty(endIndex) && count > Integer.valueOf(endIndex)) || (StringUtils.isEmpty(endIndex))){
                                beginIndex = null;
                                int rowsCount = 0;
                                if(StringUtils.isEmpty(endIndex)){
                                    endIndex = "0";
                                }
                                for(int i = Integer.valueOf(endIndex); i < rows.size(); i++){
                                    if(rowsCount >= Constants.POS_DATA_MAX_ROWS){
                                        break;
                                    }
                                    //将未下发数据添加到下发集合中
                                    if(rowsStatus.get(i).equals("0")){
                                        if(StringUtils.isEmpty(beginIndex)){
                                            beginIndex = String.valueOf(i + 1);
                                        }
                                        dataRows.add(rows.get(i).toString());
                                        rowsCount++;
                                        endIndex = String.valueOf(i + 1);
                                    }
                                }
                                if(dataRows.size() > 0){
                                    //不是当前表最后一条数据或最后一张表，则判断为还有数据
                                    if(Integer.valueOf(endIndex) < rows.size()){//|| (j+1) < tableNames.size()
                                        haveData = true;
                                    }
                                    tableName = tn;
                                    nextTimestamp = PartitionCacheUtils.hget(dataKey, "nextTimestamp");
                                    //break;
                                }
                            }
                        }
                    }
                    if(dataRows.size() == 0){
                        int n = j + 1;
                        if(n < tableNames.size()){
                            tableName = tableNames.get(n);
                        }
                        beginIndex = null;
                        endIndex = null;
                    }
                }
                else if(nextDownload){
                    String rowsStatusStr = PartitionCacheUtils.hget(dataKey, "rows_status");
                    List<String> rowsStatus = JsonUtils.json2List(rowsStatusStr);
                    for(String key : rowsStatus){
                        if(key.equals("0")){
                            haveData = true;
                        }
                    }
                }
            }
            resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message", "获取数据成功");
            if(dataRows.size() > 0){
                resultMap.put("Data", dataRows);
                resultMap.put("TableName", tableName);
                resultMap.put("BeginIndex", beginIndex);
                resultMap.put("EndIndex", endIndex);
                resultMap.put("Timestamp", nextTimestamp);
            }
            if(haveData){
                resultMap.put("NewData", Constants.POS_NEW_DATA);
            }
            else{
                resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                PartitionCacheUtils.hset(rPosId, "status", Constants.POS_CHECK_STATUS_NO_DATA);
                for(String tn : tableNames){
                    String dataKey = rPosId + "." + tn;
                    PartitionCacheUtils.del(dataKey);
                }
            }

        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据下载异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.downloadData({},{},{}) - {} - {}", json, posId, business, e.getClass().getSimpleName(), e.getMessage());
        }

        return resultMap;
    }

    public Map<String, Object> simuDownloadData(String json, BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(business)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            Pos pos = findPos(posId);
            JSONObject jsonObj = JsonUtils.str2Json(json);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            log.info("POS-{}-{} synchronized download data, json: {}", posId, business, json);
            for(Object key : jsonObj.keySet()){
                log.debug("POS-{}-{} synchronized download data, table: {}，start", posId, business, key);
                String tableName = (String)key;
                String timastamp = (String)jsonObj.get(key);
                if(timastamp == null || timastamp.equals("null")){
                    continue;
                }
                Date fromDate = null;
                Date toDate = null;
                if(timastamp.trim().length() == 10){
                    fromDate = dateFormat.parse(timastamp + " 00:00:00");
                    toDate = dateFormat.parse(timastamp + " 23:59:59");
                }
                else{
                    fromDate = dateFormat.parse(timastamp);
                    toDate = new Date();
                }
                Map<String, String> map = null;

                if("1".equals(business) || "5".equals(business) || "6".equals(business)){
                    map = query(tableName, dateFormat.format(fromDate), dateFormat.format(toDate), pos);
                }
                else if("2".equals(business)){
                    map = querySql(tableName, fromDate, toDate, pos, business);
                }
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("TableName", tableName);
                resultMap.put("Timestamp", dateFormat.format(toDate));
                if(map == null){
                    resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                }
                else{
                    resultMap.put("NewData", Constants.POS_NEW_DATA);
                    resultMap.put("Data", new ObjectMapper().readValue(map.get("rows"), List.class));
                }
                log.debug("POS-{}-{} synchronized download data, table: {}，end", posId, business, key);
                break;
            }
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据下载异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.simuDownloadData({},{},{}) - {} - {}", json, posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    //部分表格数据下发进行压缩
    public Map<String, Object> simuDownloadDataGzip(String json, BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(business)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            Pos pos = findPos(posId);
            JSONObject jsonObj = JsonUtils.str2Json(json);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            log.info("POS-{}-{} synchronized download data, json: {}", posId, business, json);
            for(Object key : jsonObj.keySet()){
                log.debug("POS-{}-{} synchronized download data, table: {}，start", posId, business, key);
                String tableName = (String)key;
                String timastamp = (String)jsonObj.get(key);
                if(timastamp == null || timastamp.equals("null")){
                    continue;
                }
                Date fromDate = null;
                Date toDate = null;
                if(timastamp.trim().length() == 10){
                    fromDate = dateFormat.parse(timastamp + " 00:00:00");
                    toDate = dateFormat.parse(timastamp + " 23:59:59");
                }
                else{
                    fromDate = dateFormat.parse(timastamp);
                    toDate = new Date();
                }
                Map<String, String> map = null;

                if("1".equals(business) || "5".equals(business) || "6".equals(business)){
                    map = query(tableName, DateFormatUtils.format(fromDate, "yyyy-MM-dd") + " 00:00:00", DateFormatUtils.format(toDate, "yyyy-MM-dd") + " 23:59:59", pos);
                }
                else if("2".equals(business)){
                    map = querySql(tableName, fromDate, toDate, pos, business);
                }
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("TableName", tableName);
                resultMap.put("Timestamp", dateFormat.format(toDate));
                if(map == null){
                    resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                }
                else{
                    List dataList = new ObjectMapper().readValue(map.get("rows"), List.class);
                    String dataJson = GsonUntil.toJson(dataList);
                    String dataStr = Base64.encodeBase64String(GzipUtils.compress(dataJson.getBytes("utf-8")));
                    resultMap.put("NewData", Constants.POS_NEW_DATA);
                    resultMap.put("Data", dataStr);
                }
                log.debug("POS-{}-{} synchronized download data, table: {}，end", posId, business, key);
                break;
            }
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据下载异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.simuDownloadDataGzip({},{},{}) - {} - {}", json, posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    public Map<String, Object> pagableWithZipDownLoad(String page, String rows, String json, BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(business) || StringUtils.isEmpty(page) || StringUtils.isEmpty(rows)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            Pos pos = findPos(posId);
            JSONObject jsonObj = JsonUtils.str2Json(json);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            log.info("POS-{}-{} synchronized download data, json: {}", posId, business, json);
            for(Object key : jsonObj.keySet()){
                log.debug("POS-{}-{} synchronized download data, table: {}，start", posId, business, key);
                String tableName = (String)key;
                String timastamp = (String)jsonObj.get(key);
                if(timastamp == null || timastamp.equals("null")){
                    continue;
                }
                Date fromDate = null;
                Date toDate = null;
                if(timastamp.trim().length() == 10){
                    fromDate = dateFormat.parse(timastamp + " 00:00:00");
                    toDate = dateFormat.parse(timastamp + " 23:59:59");
                }
                else{
                    fromDate = dateFormat.parse(timastamp);
                    toDate = new Date();
                }
                Map<String, String> map = null;

                if("1".equals(business) || "5".equals(business) || "6".equals(business)){
                    map = queryPagableWithZip(tableName, dateFormat.format(fromDate), dateFormat.format(toDate), pos, page, rows);
                }
                resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
                resultMap.put("TableName", tableName);
                resultMap.put("Timestamp", dateFormat.format(toDate));
                if(map == null){
                    resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
                    resultMap.put("Flag", 0);
                }
                else{
                    List dataList = new ObjectMapper().readValue(map.get("rows"), List.class);
                    String dataJson = GsonUntil.toJson(dataList);
                    String dataStr = Base64.encodeBase64String(GzipUtils.compress(dataJson.getBytes("utf-8")));
                    Integer count = Integer.valueOf(map.get("count"));
                    Integer totalPage = 0;
                    if(count % Integer.valueOf(rows) == 0){
                        totalPage = (int)count / Integer.valueOf(rows);
                    }
                    else{
                        totalPage = (int)count / Integer.valueOf(rows) + 1;
                    }
                    //当前页跟最大页是否一致，若一致，则不需要继续更新，返回0；若不一致，需要继续更新，返回1
                    Integer flag = (Integer.valueOf(page).equals(totalPage) ? 0 : 1);
                    resultMap.put("NewData", Constants.POS_NEW_DATA);
                    resultMap.put("Data", dataStr);
                    resultMap.put("Count", count);
                    resultMap.put("Page", page);
                    resultMap.put("Rows", rows);
                    resultMap.put("TotalPage", totalPage);
                    resultMap.put("Flag", flag);
                }
                log.debug("POS-{}-{} synchronized download data, table: {}，end", posId, business, key);
                break;
            }
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据下载异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.simuDownloadData({},{},{}) - {} - {}", json, posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    public Map<String, String> query(String tableName, String fromDate, String toDate, Pos pos) throws IOException{
        log.debug("POS-{}-{} query - {} - {} - {} - start", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
        BigInteger tenantId = pos.getTenantId();
        BigInteger branchId = pos.getBranchId();
        List rows = null;
        Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        if(tableName.equals(Goods.class.getSimpleName())){/**【Goods】*/
            if(branch1.getIsUseHqGoods() == 0){
                rows = select(String.format("select g.*, go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId, tenantId));
            }
            else{
                rows = select(String.format("select g.*, go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and g.branch_id = %s ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId));
            }
        }
        else if(tableName.equals("ExternalGoodsMapping")){/**【Goods】*/
            rows = select(String.format("select * from external_goods_mapping where tenant_id=%s and branch_id=%s and is_deleted=0 ", tenantId, branchId));
        }
        else if(tableName.equals(GoodsSpecR.class.getSimpleName())){/**【GoodsSpecR】*/
            /*rows = select(String.format("select " +
                    "gsr.tenant_id," +
                    "gsr.goods_id," +
                    "gs.id goods_spec_id " +
                    "from goods_spec_r gsr " +
                    "INNER JOIN spec_group sg ON sg.id=gsr.spec_group_id " +
                    "LEFT JOIN goods_spec gs ON sg.id=gs.group_id " +
                    "where gsr.tenant_id = %s", tenantId));*/
            rows = select(String.format("SELECT goods_spec_r.tenant_id, goods_spec_r.goods_id, goods_spec.id AS goods_spec_id " +
                    "FROM goods_spec_r " +
                    "INNER JOIN spec_group ON spec_group.id = goods_spec_r.spec_group_id " +
                    "LEFT JOIN goods_spec ON spec_group.id = goods_spec.group_id " +
                    "LEFT JOIN goods ON goods.id = goods_spec_r.goods_id " +
                    "WHERE goods_spec_r.tenant_id = %s " +
                    "AND goods.tenant_id = %s AND (goods.branch_id = %s OR goods.branch_id = (SELECT id FROM branch WHERE tenant_id = %s AND branch_type = 0))", tenantId, tenantId, branchId, tenantId));
            int i = 1;
            for(Map<String, Object> row : (List<Map<String, Object>>)rows){
                row.put("id", i);
                i++;
            }

        }
        else if(tableName.equals(Package.class.getSimpleName())){/**【Package】*/
            //Package表数据合并到Goods表中，GoodsType=2的数据是原Package数据，pos为了不进行代码改动，下发时依然下发为Package表数据。xumx 2015-10-12
            if(branch1.getIsUseHqGoods() == 0){
                rows = select(String.format("select g.*,go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId, tenantId));
            }
            else{
                rows = select(String.format("select g.*,go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and g.branch_id = %s  ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId));
            }
            rows = convertGoodsToPackage(rows);

        }
        else if(tableName.equals(Menu.class.getSimpleName())){/**【Menu】*/
            rows = select(String.format("select m.* from menu m, branch_menu_r r where r.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2", tenantId, branchId));

        }
        else if(tableName.equals(MenuGoods.class.getSimpleName())){/**【MenuGoods】*/
            rows = select(String.format("select g.* from menu_goods g, menu m, branch_menu_r r where r.menu_id = m.id and g.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2", tenantId, branchId));

        }
        else if(tableName.equals(DietPromotion.class.getSimpleName())){/**【DietPromotion】*/
            rows = select(String.format("select p.id,p.tenant_id,p.create_branch_id,p.promotion_code,p.promotion_name,p.promotion_type,p.start_date,p.end_date,p.start_time,p.end_time," +
                    "IF(p.apply_to_mon=0,'0','1') apply_to_mon,IF(p.apply_to_tue=0,'0','1') apply_to_tue,IF(p.apply_to_wed=0,'0','1') apply_to_wed," +
                    "IF(p.apply_to_thu=0,'0','1') apply_to_thu,IF(p.apply_to_fri=0,'0','1') apply_to_fri,IF(p.apply_to_sat=0,'0','1') apply_to_sat," +
                    "IF(p.apply_to_sun=0,'0','1') apply_to_sun,p.scope,p.for_customer_type,p.mem_grade_id,p.memo,IF(p.promotion_status=0,'0', '1') promotion_status," +
                    "p.is_use,p.is_superposition,p.create_by,p.create_at,p.audite_by,p.audite_at,p.last_update_by,p.last_update_at,p.is_deleted," +
                    "p.version,p.for_vip_type,p.effective_interval from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and p.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
        }
        else if(tableName.equals(DietPromotionBranchR.class.getSimpleName())){/**【DietPromotionBranchR】*/
            rows = select(String.format("select dpbr.* from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionBuyGive.class.getSimpleName())){/**【DietPromotionBuyGive】*/
            rows = select(String.format("select dpbg.* from diet_promotion_buy_give as dpbg, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpbg.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbg.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionDiscount.class.getSimpleName())){/**【DietPromotionDiscount】*/
            rows = select(String.format("select dpd.* from diet_promotion_discount as dpd, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpd.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpd.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionGoodsR.class.getSimpleName())){/**【DietPromotionGoodsR】*/
            rows = select(String.format("select dpgr.* from diet_promotion_goods_r as dpgr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpgr.diet_promotion_Id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpgr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionSatisfyReduce.class.getSimpleName())){/**【DietPromotionSatisfyReduce】*/
            rows = select(String.format("select dpsr.* from diet_promotion_satisfy_reduce as dpsr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpsr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpsr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionSpecial.class.getSimpleName())){
            rows = select(String.format("select dps.* from diet_promotion_special as dps, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dps.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dps.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(DietPromotionTotalReduce.class.getSimpleName())){
            rows = select(String.format("select dptr.* from diet_promotion_total_reduce as dptr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dptr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dptr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));

        }
        else if(tableName.equals(EmployeePosR.class.getSimpleName())){/**【EmployeePosR】*/
            rows = select(String.format("select epr.* from employee_pos_r as epr, employee as e where epr.employee_id = e.id and e.tenant_id = %s and e.branch_id = %s", tenantId, branchId));

        }
        else if(tableName.equals(PosConfig.class.getSimpleName())){/**【PosConfig】*/
            rows = select(String.format("select * from pos_config where last_update_at between '%s' and '%s' and tenant_id = %s and pos_id = %s", fromDate, toDate, tenantId, pos.getId()));

        }
        else if(tableName.equals(Category.class.getSimpleName())){/**【Category】*/
            rows = select(String.format("SELECT * FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.category_type=0 AND t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate));//不下发材料
            for(Object obj : rows){
                Map map = (Map)obj;
                map.put("is_deleted", (map.get("is_deleted").toString().equals("1") || map.get("is_deleted").toString().equals("true")));
                map.put("is_store", (map.get("is_store").toString().equals("1") || map.get("is_store").toString().equals("true")));
                map.put("is_discount", (map.get("is_discount").toString().equals("1") || map.get("is_discount").toString().equals("true")));
            }

        }
        else if(tableName.equals("CategoryV2")){/**【Category】*/
            rows = select(String.format("SELECT * FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate));//所有都下发
            for(Object obj : rows){
                Map map = (Map)obj;
                map.put("is_deleted", (map.get("is_deleted").toString().equals("1") || map.get("is_deleted").toString().equals("true")));
                map.put("is_store", (map.get("is_store").toString().equals("1") || map.get("is_store").toString().equals("true")));
                map.put("is_discount", (map.get("is_discount").toString().equals("1") || map.get("is_discount").toString().equals("true")));
            }

        }
        else if(tableName.equals(GoodsSpec.class.getSimpleName())){
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and (branch_id = %s or branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false))", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, tenantId));

        }
        else if(tableName.equals(GoodsUnit.class.getSimpleName())
                || tableName.equals(GroupGoods.class.getSimpleName())
                || tableName.equals(PackageGroup.class.getSimpleName())
                || tableName.equals(Package.class.getSimpleName() + "0.3")
                || tableName.equals(GiveBackReason.class.getSimpleName())
        ){
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId));

        }
        else if(tableName.equals(Payment.class.getSimpleName())){
            rows = select(String.format("SELECT t.id,t.payment_code,t.payment_name,t.payment_status,t.currency_id,t.create_by,t.create_at,t.last_update_by,t.last_update_at,t.tenant_id,t.branch_id,IF(t.is_score=0,'false','true') is_score," +
                            "t.fix_value,t.fix_num,t.payment_type,t.is_voucher,t.version,t.local_id,t.is_change,t.is_memo,t.is_sale,IF(t.is_deleted=0,'false','true') is_deleted,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM ((SELECT tenant_payment_id id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted, tenant_id,branch_id,is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment_branch WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'" +
                            ")UNION ALL(SELECT id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted,tenant_id,branch_id, is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'))t"
                    , tenantId, branchId, fromDate, toDate, tenantId, branchId, fromDate, toDate));
        }
        else if(tableName.equals(VipType.class.getSimpleName())){/**【Vip】*//**【VipType】*/
            ApiRest r = SaaSApi.findTenantById(tenantId);
            if(r.getIsSuccess()){
                String isBranchManagementVip = ((Map)((Map)r.getData()).get("tenant")).get("isBranchManagementVip").toString();
                if(isBranchManagementVip.equals("true") || isBranchManagementVip.equals("1")){
                    rows = select(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points," +
                            "tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and tb.branch_id = bd.branch_id and tb.id = bd.type_id and bd.is_deleted = 0" +
                            " where tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s and tb.branch_id=%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
                }
                else{
                    rows = select(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points," +
                            "tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and bd.branch_id=%s and tb.id = bd.type_id and bd.is_deleted = 0 " +
                            " where tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s", CommonUtils.DomainName2TableName(tableName), branchId, fromDate, toDate, tenantId));
                }
            }
        }
        else if(tableName.equals(Vip.class.getSimpleName())){/**【Vip】*//**【VipType】*/
            //查询会员分组
            String bIds = "";
            if(branchId != null && tenantId != null){
                Map<String, Object> param = new HashMap<>();
                param.put("id", branchId);
                param.put("tenantId", tenantId);
                Branch branch = branchMapper.find(param);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    param.remove("id");
                    param.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(param);
                    if(branches != null && branches.size() > 0){
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                    }
                }
            }
            if(bIds.length() > 0){
                rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id in (%s)", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, bIds));
            }
            else{
                rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id in (%s)", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
            }
        }
        else if(tableName.equals(Branch.class.getSimpleName())){
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
        }
        else if(tableName.equals(Employee.class.getSimpleName())
                || tableName.equals(PosAuthority.class.getSimpleName())
                || tableName.equals(BranchTable.class.getSimpleName())
                || tableName.equals(BranchArea.class.getSimpleName())
        ){
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));

        }
        else if(tableName.equals(Sale.class.getSimpleName())
                || tableName.equals(SaleDetail.class.getSimpleName())
                || tableName.equals(SaleGoodsSpec.class.getSimpleName())
                || tableName.equals(SalePayment.class.getSimpleName())
        ){
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s and is_deleted = false", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));

        }
        else if(tableName.equals(DietPromotionCategory.class.getSimpleName())){/**【DietPromotionCategory】*/
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s'", CommonUtils.DomainName2TableName(tableName), fromDate, toDate));

        }
        else if(tableName.equals(UserLog.class.getSimpleName())){/**【UserLog】*/
            rows = select(String.format("select * from %s where tenant_id = %s and branch_id = %s", CommonUtils.DomainName2TableName(tableName), tenantId, branchId));

        }
        else if(tableName.equals("Res") || tableName.equals("res")){/**【Res】*/
            rows = res(pos.getTenantId(), pos.getBranchId());

        }
        else if(tableName.equals("RetrospectBranchR")){
            rows = select(String.format("select rbr.*,rp.ws_url,rp.code,rp.name from retrospect_branch_r rbr LEFT JOIN retrospect_platform rp ON rbr.platform_id=rp.id AND rbr.tenant_id=rp.tenant_id AND rp.is_deleted=0 where rbr.last_update_at between '%s' and '%s' and rbr.tenant_id = %s and rbr.branch_id = %s", fromDate, toDate, tenantId, branchId));
        }
        else if(tableName.equals("RetrospectGoodsInfo")){
            rows = select(String.format("select * from retrospect_goods_info where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s", fromDate, toDate, tenantId, branchId));
        }
        else{
            log.error("Pos-{}-1 数据同步，未知的表名 {}", pos.getId(), tableName);
        }
        if(rows != null && rows.size() > 0){
            ObjectMapper objectMapper = new ObjectMapper();
            rows = CommonUtils.DomainList(rows);
            Map<String, String> dataMap = new HashMap<String, String>();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataMap.put("timestamp", fromDate);
            dataMap.put("nextTimestamp", toDate);
            dataMap.put("count", String.valueOf(rows.size()));
            dataMap.put("rows", (BeanUtils.toJsonStr(rows)));
            List<String> rowsStatus = new ArrayList<>();
            for(int i = 0; i < rows.size(); i++){
                rowsStatus.add("0");
            }
            dataMap.put("rows_status", objectMapper.writeValueAsString(rowsStatus));
            log.debug("POS-{}-{} query - {} - {} - {} - end", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
            return dataMap;
        }
        log.debug("POS-{}-{} query - {} - {} - {} - end", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
        return null;
    }


    public Map<String, String> queryPagableWithZip(String tableName, String fromDate, String toDate, Pos pos, String page, String pageRows) throws IOException{
        log.debug("POS-{}-{} query - {} - {} - {} - start", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
        BigInteger tenantId = pos.getTenantId();
        BigInteger branchId = pos.getBranchId();
        Integer pRows = Integer.valueOf(pageRows);
        Integer offset = (Integer.parseInt(page) - 1) * pRows;
        List rows = null;
        List countRows = null;
        Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        if(tableName.equals(Goods.class.getSimpleName())){/**【Goods】*/
            if(branch1.getIsUseHqGoods() == 0){
                countRows = select(String.format("select count(1)count from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId, tenantId));
                rows = select(String.format("select g.*, go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC limit %s,%s", branchId, tenantId, fromDate, toDate, branchId, tenantId, offset, pRows));
            }
            else{
                countRows = select(String.format("select count(1)count from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and g.branch_id = %s ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId));
                rows = select(String.format("select g.*, go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id=%s where g.tenant_id = %s and g.goods_type = 1 and g.last_update_at between '%s' and '%s' and g.branch_id = %s ORDER BY go.order_id,g.id ASC  limit %s,%s", branchId, tenantId, fromDate, toDate, branchId, offset, pRows));
            }
        }
        else if(tableName.equals("ExternalGoodsMapping")){/**【Goods】*/
            countRows = select(String.format("select count(1)count from external_goods_mapping where tenant_id=%s and branch_id=%s and is_deleted=0", tenantId, branchId));
            rows = select(String.format("select * from external_goods_mapping where tenant_id=%s and branch_id=%s and is_deleted=0 limit %s,%s", tenantId, branchId, offset, pRows));
        }
        else if(tableName.equals(GoodsSpecR.class.getSimpleName())){/**【GoodsSpecR】*/
            countRows = select(String.format("SELECT count(1)count " +
                    "FROM goods_spec_r " +
                    "INNER JOIN spec_group ON spec_group.id = goods_spec_r.spec_group_id " +
                    "LEFT JOIN goods_spec ON spec_group.id = goods_spec.group_id " +
                    "LEFT JOIN goods ON goods.id = goods_spec_r.goods_id " +
                    "WHERE goods_spec_r.tenant_id = %s " +
                    "AND goods.tenant_id = %s AND (goods.branch_id = %s OR goods.branch_id = (SELECT id FROM branch WHERE tenant_id = %s AND branch_type = 0))", tenantId, tenantId, branchId, tenantId));
            rows = select(String.format("SELECT goods_spec_r.tenant_id, goods_spec_r.goods_id, goods_spec.id AS goods_spec_id " +
                    "FROM goods_spec_r " +
                    "INNER JOIN spec_group ON spec_group.id = goods_spec_r.spec_group_id " +
                    "LEFT JOIN goods_spec ON spec_group.id = goods_spec.group_id " +
                    "LEFT JOIN goods ON goods.id = goods_spec_r.goods_id " +
                    "WHERE goods_spec_r.tenant_id = %s " +
                    "AND goods.tenant_id = %s AND (goods.branch_id = %s OR goods.branch_id = (SELECT id FROM branch WHERE tenant_id = %s AND branch_type = 0)) limit %s,%s", tenantId, tenantId, branchId, tenantId, offset, pRows));
            int i = 1;
            for(Map<String, Object> row : (List<Map<String, Object>>)rows){
                row.put("id", i);
                i++;
            }

        }
        else if(tableName.equals(VipType.class.getSimpleName())){/**【Vip】*//**【VipType】*/
            ApiRest r = SaaSApi.findTenantById(tenantId);
            if(r.getIsSuccess()){
                String isBranchManagementVip = ((Map)((Map)r.getData()).get("tenant")).get("isBranchManagementVip").toString();
                if(isBranchManagementVip.equals("true") || isBranchManagementVip.equals("1")){
                    countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id=%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
                    rows = select(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points," +
                            "tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and tb.branch_id = bd.branch_id and tb.id = bd.type_id and bd.is_deleted = 0 " +
                            " where tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s and tb.branch_id=%s limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, offset, pRows));
                }
                else{
                    countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId));
                    rows = select(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points," +
                            "tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and bd.branch_id=%s and tb.id = bd.type_id and bd.is_deleted = 0 " +
                            " where tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s limit %s,%s", CommonUtils.DomainName2TableName(tableName), branchId, fromDate, toDate, tenantId, offset, pRows));
                }
            }
        }
        else if(tableName.equals(Package.class.getSimpleName())){/**【Package】*/
            //Package表数据合并到Goods表中，GoodsType=2的数据是原Package数据，pos为了不进行代码改动，下发时依然下发为Package表数据。xumx 2015-10-12
            if(branch1.getIsUseHqGoods() == 0){
                countRows = select(String.format("select count(1)count from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId, tenantId));
                rows = select(String.format("select g.*,go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and (g.branch_id = %s or g.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) ORDER BY go.order_id,g.id ASC limit %s,%s", branchId, tenantId, fromDate, toDate, branchId, tenantId, offset, pRows));
            }
            else{
                countRows = select(String.format("select count(1)count from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and g.branch_id = %s  ORDER BY go.order_id,g.id ASC", branchId, tenantId, fromDate, toDate, branchId));
                rows = select(String.format("select g.*,go.order_id from goods g LEFT JOIN goods_order go ON go.tenant_id = g.tenant_id AND g.id = go.goods_id AND go.branch_id =%s where g.tenant_id = %s and g.goods_type = 2 and g.last_update_at between '%s' and '%s' and g.branch_id = %s  ORDER BY go.order_id,g.id ASC limit %s,%s", branchId, tenantId, fromDate, toDate, branchId, offset, pRows));
            }
            rows = convertGoodsToPackage(rows);

        }
        else if(tableName.equals(Menu.class.getSimpleName())){/**【Menu】*/
            countRows = select(String.format("select count(1)count from menu m, branch_menu_r r where r.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2", tenantId, branchId));
            rows = select(String.format("select m.* from menu m, branch_menu_r r where r.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2 limit %s,%s", tenantId, branchId, offset, pRows));
        }
        else if(tableName.equals(MenuGoods.class.getSimpleName())){/**【MenuGoods】*/
            countRows = select(String.format("select count(1)count from menu_goods g, menu m, branch_menu_r r where r.menu_id = m.id and g.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2", tenantId, branchId));
            rows = select(String.format("select g.* from menu_goods g, menu m, branch_menu_r r where r.menu_id = m.id and g.menu_id = m.id and m.tenant_id = %s and r.branch_id = %s and m.menu_type <> 2 limit %s,%s", tenantId, branchId, offset, pRows));

        }
        else if(tableName.equals(DietPromotion.class.getSimpleName())){/**【DietPromotion】*/
            countRows = select(String.format("select count(1)count from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and p.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select p.id,p.tenant_id,p.create_branch_id,p.promotion_code,p.promotion_name,p.promotion_type,p.start_date,p.end_date,p.start_time,p.end_time," +
                    "IF(p.apply_to_mon=0,'0','1') apply_to_mon,IF(p.apply_to_tue=0,'0','1') apply_to_tue,IF(p.apply_to_wed=0,'0','1') apply_to_wed," +
                    "IF(p.apply_to_thu=0,'0','1') apply_to_thu,IF(p.apply_to_fri=0,'0','1') apply_to_fri,IF(p.apply_to_sat=0,'0','1') apply_to_sat," +
                    "IF(p.apply_to_sun=0,'0','1') apply_to_sun,p.scope,p.for_customer_type,p.mem_grade_id,p.memo,IF(p.promotion_status=0,'0', '1') promotion_status," +
                    "p.is_use,p.is_superposition,p.create_by,p.create_at,p.audite_by,p.audite_at,p.last_update_by,p.last_update_at,p.is_deleted," +
                    "p.version,p.for_vip_type,p.effective_interval from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and p.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));
        }
        else if(tableName.equals(DietPromotionBranchR.class.getSimpleName())){/**【DietPromotionBranchR】*/
            countRows = select(String.format("select count(1)count from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dpbr.* from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbr.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionBuyGive.class.getSimpleName())){/**【DietPromotionBuyGive】*/
            countRows = select(String.format("select count(1)count from diet_promotion_buy_give as dpbg, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpbg.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbg.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dpbg.* from diet_promotion_buy_give as dpbg, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpbg.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbg.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionDiscount.class.getSimpleName())){/**【DietPromotionDiscount】*/
            countRows = select(String.format("select count(1)count from diet_promotion_discount as dpd, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpd.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpd.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dpd.* from diet_promotion_discount as dpd, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpd.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpd.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionGoodsR.class.getSimpleName())){/**【DietPromotionGoodsR】*/
            countRows = select(String.format("select count(1)count from diet_promotion_goods_r as dpgr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpgr.diet_promotion_Id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpgr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dpgr.* from diet_promotion_goods_r as dpgr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpgr.diet_promotion_Id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpgr.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionSatisfyReduce.class.getSimpleName())){/**【DietPromotionSatisfyReduce】*/
            countRows = select(String.format("select count(1)count from diet_promotion_satisfy_reduce as dpsr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpsr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpsr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dpsr.* from diet_promotion_satisfy_reduce as dpsr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpsr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpsr.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionSpecial.class.getSimpleName())){
            countRows = select(String.format("select count(1)count from diet_promotion_special as dps, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dps.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dps.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dps.* from diet_promotion_special as dps, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dps.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dps.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(DietPromotionTotalReduce.class.getSimpleName())){
            countRows = select(String.format("select count(1)count from diet_promotion_total_reduce as dptr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dptr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dptr.last_update_at between '%s' and '%s' and p.scope <> 2", tenantId, branchId, fromDate, toDate));
            rows = select(String.format("select dptr.* from diet_promotion_total_reduce as dptr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dptr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dptr.last_update_at between '%s' and '%s' and p.scope <> 2 limit %s,%s", tenantId, branchId, fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(EmployeePosR.class.getSimpleName())){/**【EmployeePosR】*/
            countRows = select(String.format("select count(1)count from employee_pos_r as epr, employee as e where epr.employee_id = e.id and e.tenant_id = %s and e.branch_id = %s", tenantId, branchId));
            rows = select(String.format("select epr.* from employee_pos_r as epr, employee as e where epr.employee_id = e.id and e.tenant_id = %s and e.branch_id = %s limit %s,%s", tenantId, branchId, offset, pRows));

        }
        else if(tableName.equals(PosConfig.class.getSimpleName())){/**【PosConfig】*/
            countRows = select(String.format("select count(1)count from pos_config where last_update_at between '%s' and '%s' and tenant_id = %s and pos_id = %s", fromDate, toDate, tenantId, pos.getId()));
            rows = select(String.format("select * from pos_config where last_update_at between '%s' and '%s' and tenant_id = %s and pos_id = %s limit %s,%s", fromDate, toDate, tenantId, pos.getId(), offset, pRows));

        }
        else if(tableName.equals(Category.class.getSimpleName())){/**【Category】*/
            countRows = select(String.format("SELECT count(1)count FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.category_type=0 AND t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate));//不下发材料
            rows = select(String.format("SELECT * FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.category_type=0 AND t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC limit %s,%s", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate, offset, pRows));//不下发材料
            for(Object obj : rows){
                Map map = (Map)obj;
                map.put("is_deleted", (map.get("is_deleted").toString().equals("1") || map.get("is_deleted").toString().equals("true")));
                map.put("is_store", (map.get("is_store").toString().equals("1") || map.get("is_store").toString().equals("true")));
                map.put("is_discount", (map.get("is_discount").toString().equals("1") || map.get("is_discount").toString().equals("true")));
            }

        }
        else if(tableName.equals("CategoryV2")){/**【Category】*/
            countRows = select(String.format("SELECT count(1)count FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate));//所有都下发
            rows = select(String.format("SELECT * FROM ((SELECT c.*, co.order_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s) " +
                    "UNION ALL(SELECT ca.*, NULL order_id FROM category ca WHERE tenant_id = %s AND ca.id NOT IN (SELECT co.cat_id FROM category c LEFT JOIN category_order co ON c.id = co.cat_id " +
                    "AND c.tenant_id = co.tenant_id WHERE c.tenant_id = %s AND co.branch_id = %s))) t WHERE t.last_update_at BETWEEN '%s' AND '%s' ORDER BY t.order_id,t.id ASC limit %s,%s", tenantId, branchId, tenantId, tenantId, branchId, fromDate, toDate, offset, pRows));//所有都下发
            for(Object obj : rows){
                Map map = (Map)obj;
                map.put("is_deleted", (map.get("is_deleted").toString().equals("1") || map.get("is_deleted").toString().equals("true")));
                map.put("is_store", (map.get("is_store").toString().equals("1") || map.get("is_store").toString().equals("true")));
                map.put("is_discount", (map.get("is_discount").toString().equals("1") || map.get("is_discount").toString().equals("true")));
            }

        }
        else if(tableName.equals(GoodsSpec.class.getSimpleName())){
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s and (branch_id = %s or branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false))", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, tenantId));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and (branch_id = %s or branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = false)) limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, tenantId, offset, pRows));

        }
        else if(tableName.equals(GoodsUnit.class.getSimpleName())
                || tableName.equals(GroupGoods.class.getSimpleName())
                || tableName.equals(PackageGroup.class.getSimpleName())
                || tableName.equals(Package.class.getSimpleName() + "0.3")
                || tableName.equals(GiveBackReason.class.getSimpleName())
        ){
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, offset, pRows));

        }
        else if(tableName.equals(Payment.class.getSimpleName())){
            countRows = select(String.format("SELECT count(1)count FROM ((SELECT tenant_payment_id id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted, tenant_id,branch_id,is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment_branch WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'" +
                            ")UNION ALL(SELECT id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted,tenant_id,branch_id, is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'))t"
                    , tenantId, branchId, fromDate, toDate, tenantId, branchId, fromDate, toDate));
            rows = select(String.format("SELECT t.id,t.payment_code,t.payment_name,t.payment_status,t.currency_id,t.create_by,t.create_at,t.last_update_by,t.last_update_at,t.tenant_id,t.branch_id,IF(t.is_score=0,'false','true') is_score," +
                            "t.fix_value,t.fix_num,t.payment_type,t.is_voucher,t.version,t.local_id,t.is_change,t.is_memo,t.is_sale,IF(t.is_deleted=0,'false','true') is_deleted,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM ((SELECT tenant_payment_id id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted, tenant_id,branch_id,is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment_branch WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'" +
                            ")UNION ALL(SELECT id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted,tenant_id,branch_id, is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox, order_number FROM payment WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'))t limit %s,%s"
                    , tenantId, branchId, fromDate, toDate, tenantId, branchId, fromDate, toDate, offset, pRows));
        }
        else if(tableName.equals(Branch.class.getSimpleName())){
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s and id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and id = %s limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, offset, pRows));
        }
        else if(tableName.equals(Employee.class.getSimpleName())
                || tableName.equals(PosAuthority.class.getSimpleName())
                || tableName.equals(BranchTable.class.getSimpleName())
                || tableName.equals(BranchArea.class.getSimpleName())
        ){
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, offset, pRows));

        }
        else if(tableName.equals(Sale.class.getSimpleName())
                || tableName.equals(SaleDetail.class.getSimpleName())
                || tableName.equals(SaleGoodsSpec.class.getSimpleName())
                || tableName.equals(SalePayment.class.getSimpleName())
        ){
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s and is_deleted = false", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s and is_deleted = false limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, tenantId, branchId, offset, pRows));

        }
        else if(tableName.equals(DietPromotionCategory.class.getSimpleName())){/**【DietPromotionCategory】*/
            countRows = select(String.format("select count(1)count from %s where last_update_at between '%s' and '%s'", CommonUtils.DomainName2TableName(tableName), fromDate, toDate));
            rows = select(String.format("select * from %s where last_update_at between '%s' and '%s' limit %s,%s", CommonUtils.DomainName2TableName(tableName), fromDate, toDate, offset, pRows));

        }
        else if(tableName.equals(UserLog.class.getSimpleName())){/**【UserLog】*/
            countRows = select(String.format("select count(1)count from %s where tenant_id = %s and branch_id = %s", CommonUtils.DomainName2TableName(tableName), tenantId, branchId));
            rows = select(String.format("select * from %s where tenant_id = %s and branch_id = %s limit %s,%s", CommonUtils.DomainName2TableName(tableName), tenantId, branchId, offset, pRows));

        }
        else if(tableName.equals("Res") || tableName.equals("res")){/**【Res】*/
            rows = res(pos.getTenantId(), pos.getBranchId());

        }
        else if(tableName.equals("RetrospectBranchR")){
            countRows = select(String.format("select count(1)count from retrospect_branch_r where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s", fromDate, toDate, tenantId, branchId));
            rows = select(String.format("select rbr.*,rp.ws_url,rp.code,rp.name from retrospect_branch_r rbr LEFT JOIN retrospect_platform rp ON rbr.platform_id=rp.id AND rbr.tenant_id=rp.tenant_id AND rp.is_deleted=0 where rbr.last_update_at between '%s' and '%s' and rbr.tenant_id = %s and rbr.branch_id = %s limit %s,%s", fromDate, toDate, tenantId, branchId, offset, pRows));
        }
        else if(tableName.equals("RetrospectGoodsInfo")){
            countRows = select(String.format("select count(1)count from retrospect_goods_info where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s", fromDate, toDate, tenantId, branchId));
            rows = select(String.format("select * from retrospect_goods_info where last_update_at between '%s' and '%s' and tenant_id = %s and branch_id = %s limit %s,%s", fromDate, toDate, tenantId, branchId, offset, pRows));
        }
        else{
            log.error("Pos-{}-1 数据同步，未知的表名 {}", pos.getId(), tableName);
        }
        if(rows != null && rows.size() > 0){
            ObjectMapper objectMapper = new ObjectMapper();
            rows = CommonUtils.DomainList(rows);
            String count = String.valueOf(rows.size());
            if(countRows != null && countRows.size() > 0){
                count = ((Map)countRows.get(0)).get("count").toString();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataMap.put("timestamp", fromDate);
            dataMap.put("nextTimestamp", toDate);
            dataMap.put("count", count);
            dataMap.put("rows", (BeanUtils.toJsonStr(rows)));
            List<String> rowsStatus = new ArrayList<>();
            for(int i = 0; i < rows.size(); i++){
                rowsStatus.add("0");
            }
            dataMap.put("rows_status", objectMapper.writeValueAsString(rowsStatus));
            log.debug("POS-{}-{} query - {} - {} - {} - end", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
            return dataMap;
        }
        log.debug("POS-{}-{} query - {} - {} - {} - end", pos.getId(), pos.getTenantCode(), tableName, fromDate, toDate);
        return null;
    }

    public List<Map> convertGoodsToPackage(List<Map> oldList){
        if(oldList == null || oldList.isEmpty()){
            return oldList;
        }
        List<Map> newList = new ArrayList<>();
        for(Map m : oldList){
            m.put("package_code", m.get("goods_code"));
            m.put("package_name", m.get("goods_name"));
            m.put("status", m.get("goods_status"));
            m.remove("goods_code");
            m.remove("goods_name");
            m.remove("goods_status");
            newList.add(m);
        }
        return newList;
    }

    public ApiRest getRes(Map params) throws Exception{
        ApiRest apiRest = new ApiRest();
        BigInteger posId = BigInteger.valueOf(Long.valueOf(params.get("posId").toString()));
        Pos pos = findPos(posId);
        Map list = resRetail(pos.getTenantId(), pos.getBranchId());
        apiRest.setData(list);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("权限查询成功！");
        LogUtil.logDebug("api/res-getRes查询成功！");
        return apiRest;
    }

    public Map resRetail(BigInteger tenantId, BigInteger branchId){
        List list = new ArrayList();
        Map rMap = new HashMap();
        List<Map<String, Object>> eList = select(String.format("select * from employee where tenant_id = %s and branch_id = %s and is_deleted = false", tenantId, branchId));
        if(eList != null){
            Map<String, String> euMap = new HashMap<String, String>();
            StringBuffer buffer = new StringBuffer();
            for(Map m : eList){
                euMap.put(m.get("user_id").toString(), m.get("id").toString());
                buffer.append(m.get("user_id").toString()).append(",");
            }
            String userIds = buffer.toString();
            if(userIds.endsWith(",")){
                userIds = userIds.substring(0, userIds.length() - 1);
            }
            if(StringUtils.isNotEmpty(userIds)){
                Map<String, List<String>> resMap = roleService.getUserPosRes(userIds, tenantId, branchId);
                for(Object k : resMap.keySet()){
                    //list.add(resMap.get(k));
                    rMap.put(euMap.get(k.toString()), resMap.get(k));
                }
//                ApiRest rest = AuthApi.posRes(userIds);
//                if (rest.getIsSuccess()) {
//                    Object obj = rest.getData();
//                    if (obj != null && !obj.toString().equals("")) {
//                        Map resMap = (Map) obj;
//                        Map rMap = null;
//                        for (Object k : resMap.keySet()) {
//                            rMap = new HashMap();
//                            rMap.put(euMap.get(k.toString()), resMap.get(k));
//                            list.add(rMap);
//                        }
//                    }
//                }
            }
        }
        return rMap;
    }

    public List res(BigInteger tenantId, BigInteger branchId){
        List list = new ArrayList();
        List<Map<String, Object>> eList = select(String.format("select * from employee where tenant_id = %s and branch_id = %s and is_deleted = false", tenantId, branchId));
        if(eList != null){
            Map<String, String> euMap = new HashMap<String, String>();
            StringBuffer buffer = new StringBuffer();
            for(Map m : eList){
                euMap.put(m.get("user_id").toString(), m.get("id").toString());
                buffer.append(m.get("user_id").toString()).append(",");
            }
            String userIds = buffer.toString();
            if(userIds.endsWith(",")){
                userIds = userIds.substring(0, userIds.length() - 1);
            }
            if(StringUtils.isNotEmpty(userIds)){
                Map<String, List<String>> resMap = roleService.getUserPosRes(userIds, tenantId, branchId);
                Map rMap = null;
                for(Object k : resMap.keySet()){
                    rMap = new HashMap();
                    rMap.put(euMap.get(k.toString()), resMap.get(k));
                    list.add(rMap);
                }
//                ApiRest rest = AuthApi.posRes(userIds);
//                if (rest.getIsSuccess()) {
//                    Object obj = rest.getData();
//                    if (obj != null && !obj.toString().equals("")) {
//                        Map resMap = (Map) obj;
//                        Map rMap = null;
//                        for (Object k : resMap.keySet()) {
//                            rMap = new HashMap();
//                            rMap.put(euMap.get(k.toString()), resMap.get(k));
//                            list.add(rMap);
//                        }
//                    }
//                }
            }
        }
        return list;
    }

    public Map<String, String> querySql(String tableName, Date fromDate, Date toDate, Pos pos, String business) throws IOException{
        tableName = tableName.trim().toLowerCase();
        List<String> saasTables = new ArrayList<>();
        List<String> noUpdateDateTables = new ArrayList<>();
        List<String> noTenantIdTables = new ArrayList<>(Arrays.asList("goods_library", "diet_promotion_category"));
        List<String> noBranchIdTables = new ArrayList<>(Arrays.asList("category", "goods", "goods_bar", "goods_unit", "group_goods", "pack_goods", "package", "package_group", "pos_config", "goods_library", "branch", "diet_promotion_category", "vip", "vip_type"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDateStr = dateFormat.format(fromDate);
        String toDateStr = dateFormat.format(toDate);

        StringBuilder sql = new StringBuilder(String.format("select tb.* from %s tb where 1=1 ", tableName));

        if("package".equals(tableName)){
            sql = new StringBuilder("select tb.* from goods tb where 1=1 ");
        }

        if(!noUpdateDateTables.contains(tableName)){
            sql.append(String.format("and tb.last_update_at between '%s' and '%s' ", fromDateStr, toDateStr));
        }
        if(!noTenantIdTables.contains(tableName)){
            sql.append(String.format("and tb.tenant_id = %s ", pos.getTenantId()));
        }
        if(!noBranchIdTables.contains(tableName)){
            sql.append(String.format("and tb.branch_id = %s ", pos.getBranchId()));
        }
        if("category".equals(tableName)){
            //sql.append("and tb.category_type = 0 ");
        }
        else if("goods".equals(tableName)){
            sql.append(String.format("and tb.goods_type = 1 and (tb.branch_id = %s or tb.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = 0)) ", pos.getBranchId(), pos.getTenantId()));
        }
        else if("package".equals(tableName)){
            sql.append(String.format("and tb.goods_type = 2 and (tb.branch_id = %s or tb.branch_id = (select max(b.id) from branch b where b.tenant_id = %s and b.code = '000' and b.is_deleted = 0)) ", pos.getBranchId(), pos.getTenantId()));
        }
        else if("vip_type".equals(tableName)){/**【Vip】*//**【VipType】*/
            ApiRest r = SaaSApi.findTenantById(pos.getTenantId());
            if(r.getIsSuccess()){
                String isBranchManagementVip = ((Map)((Map)r.getData()).get("tenant")).get("isBranchManagementVip").toString();
                if(isBranchManagementVip.equals("true") || isBranchManagementVip.equals("1")){
                    sql = new StringBuilder(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points,tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and tb.branch_id = bd.branch_id and tb.id = bd.type_id and bd.is_deleted = 0" +
                            " where 1=1 and tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s and tb.branch_id=%s", tableName, fromDateStr, toDateStr, pos.getTenantId(), pos.getBranchId()));
                }
                else{
                    sql = new StringBuilder(String.format("select tb.id,tb.tenant_id,tb.type_code,tb.type_name,tb.preferential_policy,tb.points_factor,tb.score_usage," +
                            "tb.mem_price_used,IFNULL(bd.discount_rate,tb.discount_rate) discount_rate,tb.to_save_points,tb.is_package_disc,tb.is_promotion_disc,tb.is_online_default,tb.create_by,tb.create_at," +
                            "tb.last_update_by,tb.last_update_at,tb.is_deleted,tb.version,tb.branch_id,tb.allow_refund,tb.deposit,tb.auto_upgrade,tb.curr_level,tb.upgrade_limit," +
                            "tb.score_type,tb.upgrade_type from %s tb left join branch_discount bd on tb.tenant_id = bd.tenant_id and bd.branch_id=%s and tb.id = bd.type_id and bd.is_deleted = 0" +
                            " where 1=1 and tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s", tableName, pos.getBranchId(), fromDateStr, toDateStr, pos.getTenantId()));
                }
            }
        }
        else if("vip".equals(tableName)){/**【Vip】*//**【VipType】*/
            //查询会员分组
            String bIds = "";
            if(pos.getBranchId() != null && pos.getTenantId() != null){
                Map<String, Object> param = new HashMap<>();
                param.put("id", pos.getBranchId());
                param.put("tenantId", pos.getTenantId());
                Branch branch = branchMapper.find(param);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    param.remove("id");
                    param.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(param);
                    if(branches != null && branches.size() > 0){
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                    }
                }
            }
            if(bIds.length() > 0){
                sql = new StringBuilder(String.format("select tb.* from %s tb where 1=1 and tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s and tb.branch_id in (%s)", tableName, fromDateStr, toDateStr, pos.getTenantId(), bIds));
            }
            else{
                sql = new StringBuilder(String.format("select tb.* from %s tb where 1=1 and tb.last_update_at between '%s' and '%s' and tb.tenant_id = %s and tb.branch_id = %s", tableName, fromDateStr, toDateStr, pos.getTenantId(), pos.getBranchId()));
            }
        }
        else if("branch".equals(tableName)){
            sql.append(String.format("and tb.id = %s ", pos.getBranchId()));
        }
        else if("payment".equals(tableName)){
            sql = new StringBuilder(String.format("SELECT * FROM ((SELECT tenant_payment_id id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted, tenant_id,branch_id,is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox FROM payment_branch WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'" +
                            ")UNION ALL(SELECT id,payment_code,payment_name,payment_status,currency_id,create_by,create_at,last_update_by,last_update_at,is_deleted,tenant_id,branch_id, is_score,fix_value,fix_num,payment_type,is_voucher,version,local_id,is_change,is_memo,is_sale,if(is_store=0,false,true) is_store,if(is_open_cashbox=0,false,true) is_open_cashbox FROM payment WHERE tenant_id = %s AND branch_id=%s AND last_update_at between '%s' and '%s'))t"
                    , pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr, pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion".equals(tableName)){
            sql = new StringBuilder(String.format("select p.id,p.tenant_id,p.create_branch_id,p.promotion_code,p.promotion_name,p.promotion_type,p.start_date,p.end_date,p.start_time,p.end_time," +
                    "IF(p.apply_to_mon=0,'0','1') apply_to_mon,IF(p.apply_to_tue=0,'0','1') apply_to_tue,IF(p.apply_to_wed=0,'0','1') apply_to_wed," +
                    "IF(p.apply_to_thu=0,'0','1') apply_to_thu,IF(p.apply_to_fri=0,'0','1') apply_to_fri,IF(p.apply_to_sat=0,'0','1') apply_to_sat," +
                    "IF(p.apply_to_sun=0,'0','1') apply_to_sun,p.scope,p.for_customer_type,p.mem_grade_id,p.memo,IF(p.promotion_status=0,'0', '1') promotion_status," +
                    "p.is_use,p.is_superposition,p.create_by,p.create_at,p.audite_by,p.audite_at,p.last_update_by,p.last_update_at,p.is_deleted," +
                    "p.version,p.for_vip_type,p.effective_interval from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and p.last_update_at between '%s' and '%s' and p.scope <> 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion_branch_r".equals(tableName)){
            sql = new StringBuilder(String.format("select dpbr.* from diet_promotion_branch_r as dpbr, diet_promotion as p where p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbr.last_update_at between '%s' and '%s' and p.scope <> 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion_buy_give".equals(tableName)){
            sql = new StringBuilder(String.format("select dpbg.* from diet_promotion_buy_give as dpbg, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpbg.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpbg.last_update_at between '%s' and '%s' and p.scope <> 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion_discount".equals(tableName)){
            sql = new StringBuilder(String.format("select dpd.* from diet_promotion_discount as dpd, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpd.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpd.last_update_at between '%s' and '%s' and p.scope <> 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion_goods_r".equals(tableName)){
            sql = new StringBuilder(String.format("select dpgr.* from diet_promotion_goods_r as dpgr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpgr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpgr.last_update_at between '%s' and '%s' and p.scope <> 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("diet_promotion_satisfy_reduce".equals(tableName)){
            sql = new StringBuilder(String.format("select dpsr.* from diet_promotion_satisfy_reduce as dpsr, diet_promotion as p, diet_promotion_branch_r as dpbr where p.id = dpsr.diet_promotion_id and p.id = dpbr.diet_promotion_id and p.tenant_id = %s and dpbr.branch_id = %s and dpsr.last_update_at between '%s' and '%s' and p.scope != 2", pos.getTenantId(), pos.getBranchId(), fromDateStr, toDateStr));
        }
        else if("employee_pos_r".equals(tableName)){
            sql = new StringBuilder(String.format("select epr.* from employee_pos_r as epr, employee as e where epr.employee_id = e.id and e.tenant_id = %s and e.branch_id = %s", pos.getTenantId(), pos.getBranchId()));
        }
        else if("pos_config".equals(tableName)){
            sql.append(String.format("and tb.pos_id = %s ", pos.getId()));
        }

        List rows;
        if(tableName.equals("res")){
            rows = res(pos.getTenantId(), pos.getBranchId());
        }
        else{
            rows = select(sql.toString());
        }
        if(rows != null){
            Map<String, String> dataMap = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            dataMap.put("timestamp", fromDateStr);
            dataMap.put("nextTimestamp", toDateStr);
            dataMap.put("count", String.valueOf(rows.size()));
            dataMap.put("rows", BeanUtils.toJsonStr(rows));
            List<String> rowsStatus = new ArrayList<>();
            for(int i = 0; i < rows.size(); i++){
                rowsStatus.add("0");
            }
            dataMap.put("rows_status", objectMapper.writeValueAsString(rowsStatus));
            return dataMap;
        }
        return null;
    }

    public Map<String, Object> flush(BigInteger posId, String business){
        Map<String, Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(business)){
            return CommonUtils.InvalidParamsError(null);
        }
        try{
            String rPosId = Constants.POS_DATA_REDIS_PREFIX + business + "_" + posId;
            Set<String> set = PartitionCacheUtils.keys(rPosId);
            if(set != null && set.size() > 0){
                for(String k : set){
                    PartitionCacheUtils.del(k);
                }
            }
            resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message", "缓存已清空");
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Error", e.getMessage());
            log.error("DataService.flush({},{}) - {} - {}", posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> uploadData(String json, BigInteger posId){
        if(StringUtils.isEmpty(json)){
            return CommonUtils.InvalidParamsError(null);
        }
        Map<String, Object> resultMap = new HashMap<>();
        try{
            log.info("POS-{} upload data, json: {}", posId, json);
            Pos pos = findPos(posId);
            String redisKey = Constants.POS_TABLE_REDIS_PREFIX + pos.getAccessToken();
            Map<String, String> posMap = PartitionCacheUtils.hgetAll(redisKey);
            if(posMap == null || posMap.size() == 0){
                posMap = new HashMap<String, String>();
                posMap.put("posId", String.valueOf(pos.getId()));
                posMap.put("branchId", pos.getBranchId() != null ? String.valueOf(pos.getBranchId()) : "0");
                posMap.put("tenantId", pos.getTenantId() != null ? String.valueOf(pos.getTenantId()) : "0");
                posMap.put("posCode", pos.getTenantId() != null ? pos.getPosCode() : "0");
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(new Date());
                gc.add(Calendar.DATE, 1);
                gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE), 5, 0, 0);
                PartitionCacheUtils.hmsetPexpireAt(redisKey, posMap, gc.getTime().getTime());
            }
            BigInteger branchId = BigInteger.valueOf(Long.valueOf(posMap.get("branchId")));
            BigInteger tenantId = BigInteger.valueOf(Long.valueOf(posMap.get("tenantId")));
            String posCode = posMap.get("posCode");
            JSONObject jsonTables = JsonUtils.str2Json(json);
            List<Object> tableList = new ArrayList<>();
            List<String> saleList = new ArrayList<>();
            List<String> scoreGoodsList = new ArrayList<>();
            List<String> guideSaleList = new ArrayList<>();
            List<String> vipScoreList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, Set<String>> delData = new HashMap<>();
            Set<String> delSet = null;
            for(Object k : jsonTables.keySet()){
                String tableName = k.toString();
                JSONArray tableArray = (JSONArray)jsonTables.get(k);
                delSet = new HashSet<>();
                for(int i = 0; i < tableArray.size(); i++){
                    JSONObject tableJson = tableArray.getJSONObject(i);
                    Map tableMap = (Map)JSONObject.toBean(tableJson, Map.class);
                    tableMap.put("posId", posId);
                    tableMap.put("branchId", branchId);
                    tableMap.put("tenantId", tenantId);
                    tableMap.put("posCode", posCode);
                    if(StringUtils.isNotEmpty((String)tableMap.get("saleCode"))){
                        delSet.add((String)tableMap.get("saleCode"));
                    }
                    if(tableMap.get("createAt") == null){
                        tableMap.put("createAt", new Date());
                    }
                    if(tableMap.get("lastUpdateAt") == null){
                        tableMap.put("lastUpdateAt", new Date());
                    }
                    for(Object entry : tableMap.entrySet()){
                        if(!(((Map.Entry)entry).getValue() instanceof Date)){
                            tableMap.put(((Map.Entry)entry).getKey(), ((Map.Entry)entry).getValue().toString());
                        }
                        //System.out.println("key= " + ((Map.Entry)entry).getKey() + " and value= " + ((Map.Entry)entry).getValue());
                    }
                    /*if(tableMap.get("localId")!=null){
                        tableMap.put("localId",null);
                    }*/
                    Object tableObj = null;
                    if(tableName.equals(Sale.class.getSimpleName())){
                        //tableMap.put("guideId","723");
                        if(tableMap.get("tableOpenAt") != null && !tableMap.get("tableOpenAt").toString().trim().equals("")){
                            Long t = Long.valueOf((String)tableMap.get("tableOpenAt"));
                            tableMap.put("tableOpenAt", new Date(t));
                        }
                        if(tableMap.get("payAt") != null && !tableMap.get("payAt").toString().trim().equals("")){
                            Date checkoutAt = dateFormat.parse((String)tableMap.get("payAt"));
                            tableMap.put("checkoutAt", checkoutAt);
                            tableMap.put("createAt", checkoutAt);
                        }
                        if(tableMap.get("tableId") == null || tableMap.get("tableId").toString().trim().equals("")){
                            tableMap.put("tableId", BigInteger.ZERO);
                        }
                        if(tableMap.get("vipId") == null || tableMap.get("vipId").toString().trim().equals("")){
                            tableMap.put("vipId", BigInteger.ZERO);
                        }
                        if(tableMap.get("guideId") == null || tableMap.get("guideId").toString().trim().equals("")){
                            tableMap.put("guideId", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("guideId", tableMap.get("guideId"));
                        }
                        tableMap.put("lastUpdateAt", new Date());
                        if(tableMap.get("localId") != null && !tableMap.get("localId").equals("null")){
                            delSet.remove(tableMap.get("saleCode"));
                            String localSign = pos.getId() + "-" + tableMap.get("localId");
                            tableMap.remove("localId");
                            tableMap.put("localSign", localSign);
                            List l = select(String.format("select * from sale where sale_code='%s' and is_deleted = false and tenant_id = %s and branch_id = %s and local_sign = '%s'", tableMap.get("saleCode"), pos.getTenantId(), pos.getBranchId(), localSign));
                            if(l != null && !l.isEmpty()){
                                List sa = select(String.format("select * from sale where sale_code='%s' and is_deleted = false and tenant_id = %s and branch_id = %s and local_sign = '%s' and checkout_at='%s'", tableMap.get("saleCode"), pos.getTenantId(), pos.getBranchId(), localSign, tableMap.get("payAt")));
                                if(sa != null && !sa.isEmpty()){
                                    continue;
                                }
                                else{
                                    delSet.add(tableMap.get("saleCode").toString());
                                }
                            }
                        }
                        else{
                            delSet.remove(tableMap.get("saleCode"));
                            List sa = select(String.format("select * from sale where sale_code='%s' and is_deleted = false and tenant_id = %s and branch_id = %s and checkout_at='%s'", tableMap.get("saleCode"), pos.getTenantId(), pos.getBranchId(), tableMap.get("payAt")));
                            if(sa != null && !sa.isEmpty()){
                                continue;
                            }
                            else{
                                delSet.add(tableMap.get("saleCode").toString());
                            }
                        }
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), Sale.class);
                    }
                    else if(tableName.equals(SaleDetail.class.getSimpleName())){
                        //tableMap.put("guideId","723");
                        if(tableMap.get("payAt") != null && !tableMap.get("payAt").toString().trim().equals("")){
                            Date checkoutAt = dateFormat.parse((String)tableMap.get("payAt"));
                            tableMap.put("createAt", checkoutAt);
                        }
                        if(tableMap.get("createBy") == null){
                            tableMap.put("createBy", tableMap.get("cashier"));
                        }
                        if(tableMap.get("promotionId") == null || tableMap.get("promotionId").toString().trim().equals("")){
                            tableMap.put("promotionId", BigInteger.ZERO);
                        }
                        if(tableMap.get("vip_id") != null && !tableMap.get("vip_id").toString().trim().equals("")){
                            tableMap.put("vipId", tableMap.get("vip_id"));
                        }
                        else if(tableMap.get("vipId") != null && !tableMap.get("vipId").toString().trim().equals("")){
                            tableMap.put("vipId", tableMap.get("vipId"));
                        }
                        else{
                            tableMap.put("vipId", BigInteger.ZERO);
                        }
                        if(tableMap.get("cashier") == null || tableMap.get("cashier").toString().trim().equals("")){
                            tableMap.put("cashier", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("cashier", tableMap.get("cashier"));
                        }
                        if(tableMap.get("guideId") == null || tableMap.get("guideId").toString().trim().equals("")){
                            tableMap.put("guideId", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("guideId", tableMap.get("guideId"));
                        }
                        if(tableMap.get("batchId") == null || tableMap.get("batchId").toString().trim().equals("")){
                            tableMap.put("batchId", "");
                        }
                        else{
                            tableMap.put("batchId", tableMap.get("batchId"));
                        }
                        if(tableMap.get("batchGoodsCode") == null || tableMap.get("batchGoodsCode").toString().trim().equals("")){
                            tableMap.put("batchGoodsCode", "");
                        }
                        else{
                            tableMap.put("batchGoodsCode", tableMap.get("batchGoodsCode"));
                        }
                        tableMap.put("lastUpdateAt", new Date());
                        if(tableMap.get("localId") != null && !tableMap.get("localId").equals("null")){
                            delSet.remove(tableMap.get("saleCode"));
                            String localSign = pos.getId() + "-" + tableMap.get("localId");
                            tableMap.remove("localId");
                            tableMap.put("localSign", localSign);
                            List l = select(String.format("select * from sale_detail where sale_code='%s' and is_deleted = false and tenant_id = %s and branch_id = %s and local_sign = '%s'", tableMap.get("saleCode"), pos.getTenantId(), pos.getBranchId(), localSign));
                            if(l != null && !l.isEmpty()){
                                continue;
                            }
                        }
                        else{
                            delSet.remove(tableMap.get("saleCode"));
                        }
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SaleDetail.class);
                    }
                    else if(tableName.equals(SaleDetailAbnormal.class.getSimpleName())){
                        if(tableMap.get("payAt") != null && !tableMap.get("payAt").toString().trim().equals("")){
                            Date checkoutAt = dateFormat.parse((String)tableMap.get("payAt"));
                            tableMap.put("createAt", checkoutAt);
                        }
                        if(tableMap.get("createBy") == null){
                            tableMap.put("createBy", tableMap.get("cashier"));
                        }
                        if(tableMap.get("promotionId") == null || tableMap.get("promotionId").toString().trim().equals("")){
                            tableMap.put("promotionId", BigInteger.ZERO);
                        }
                        if(tableMap.get("vip_id") != null && !tableMap.get("vip_id").toString().trim().equals("")){
                            tableMap.put("vipId", tableMap.get("vip_id"));
                        }
                        else if(tableMap.get("vipId") != null && !tableMap.get("vipId").toString().trim().equals("")){
                            tableMap.put("vipId", tableMap.get("vipId"));
                        }
                        else{
                            tableMap.put("vipId", BigInteger.ZERO);
                        }
                        if(tableMap.get("cashier") == null || tableMap.get("cashier").toString().trim().equals("")){
                            tableMap.put("cashier", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("cashier", tableMap.get("cashier"));
                        }
                        if(tableMap.get("guideId") == null || tableMap.get("guideId").toString().trim().equals("")){
                            tableMap.put("guideId", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("guideId", tableMap.get("guideId"));
                        }
                        if(tableMap.get("batchId") == null || tableMap.get("batchId").toString().trim().equals("")){
                            tableMap.put("batchId", "");
                        }
                        else{
                            tableMap.put("batchId", tableMap.get("batchId"));
                        }
                        if(tableMap.get("batchGoodsCode") == null || tableMap.get("batchGoodsCode").toString().trim().equals("")){
                            tableMap.put("batchGoodsCode", "");
                        }
                        else{
                            tableMap.put("batchGoodsCode", tableMap.get("batchGoodsCode"));
                        }
                        tableMap.put("lastUpdateAt", new Date());
                        delSet.remove(tableMap.get("saleCode"));
                        if(tableMap.get("localId") != null && !tableMap.get("localId").equals("null")){
                            String localSign = pos.getId() + "-" + tableMap.get("localId");
                            tableMap.remove("localId");
                            tableMap.put("localSign", localSign);
                        }
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SaleDetailAbnormal.class);
                    }
                    else if(tableName.equals(SaleGoodsSpec.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SaleGoodsSpec.class);
                    }
                    else if(tableName.equals(SalePayment.class.getSimpleName())){
                        //tableMap.put("guideId","723");
                        if(tableMap.get("createAt") != null){
                            if(tableMap.get("createAt") instanceof String){
                                tableMap.put("paymentAt", dateFormat.parse((String)tableMap.get("createAt")));
                                tableMap.put("createAt", dateFormat.parse((String)tableMap.get("createAt")));
                            }
                            else{
                                tableMap.put("paymentAt", tableMap.get("createAt"));
                                tableMap.put("createAt", tableMap.get("createAt"));
                            }
                        }
                        if(tableMap.get("vipId") == null || tableMap.get("vipId").toString().trim().equals("")){
                            tableMap.put("vipId", BigInteger.ZERO);
                        }
                        if(tableMap.get("guideId") == null || tableMap.get("guideId").toString().trim().equals("")){
                            tableMap.put("guideId", BigInteger.ZERO);
                        }
                        else{
                            tableMap.put("guideId", tableMap.get("guideId"));
                        }
                        tableMap.put("lastUpdateAt", new Date());
                        if(tableMap.get("localId") != null && !tableMap.get("localId").equals("null")){
                            delSet.remove(tableMap.get("saleCode"));
                            String localSign = pos.getId() + "-" + tableMap.get("localId");
                            tableMap.remove("localId");
                            tableMap.put("localSign", localSign);
                            List l = select(String.format("select * from sale_payment where sale_code='%s' and is_deleted = false and tenant_id = %s and branch_id = %s and local_sign = '%s'", tableMap.get("saleCode"), pos.getTenantId(), pos.getBranchId(), localSign));
                            if(l != null && !l.isEmpty()){
                                continue;
                            }
                        }
                        else{
                            delSet.remove(tableMap.get("saleCode"));
                        }
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SalePayment.class);
                    }
                    else if(tableName.equals(DietPromotion.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotion.class);
                    }
                    else if(tableName.equals(DietPromotionBranchR.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionBranchR.class);
                    }
                    else if(tableName.equals(DietPromotionBuyGive.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionBuyGive.class);
                    }
                    else if(tableName.equals(DietPromotionCategory.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionCategory.class);
                    }
                    else if(tableName.equals(DietPromotionDiscount.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionDiscount.class);
                    }
                    else if(tableName.equals(DietPromotionGoodsR.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionGoodsR.class);
                    }
                    else if(tableName.equals(DietPromotionSatisfyReduce.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionSatisfyReduce.class);
                    }
                    else if(tableName.equals(DietPromotionSpecial.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionSpecial.class);
                    }
                    else if(tableName.equals(DietPromotionTotalReduce.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), DietPromotionTotalReduce.class);
                    }
                    else if(tableName.equals(PosConfig.class.getSimpleName())){
                        if(tableMap.get("config") != null){
                            tableMap.put("config", tableJson.get("config").toString());
                        }
                        if(tableMap.get("createAt") != null && !tableMap.get("createAt").toString().trim().equals("")){
                            tableMap.put("createAt", dateFormat.parse((String)tableMap.get("createAt")));
                        }
                        PosConfig pc = GsonUtils.fromJson(GsonUtils.toJson(tableMap), PosConfig.class);
                        pc.setId(null);

                        List<Map<String, Object>> pcList = select(String.format("select * from pos_config where tenant_id = %s and pos_id = %s And package_name = '%s' and is_deleted = %s", pc.getTenantId(), pc.getPosId(), pc.getPackageName(), false));

                        if(pcList != null && pcList.size() == 1){
                            PosConfig opc = GsonUtils.fromJson(GsonUtils.toJson(CommonUtils.DomainMap(pcList.get(0))), PosConfig.class);
                            opc.setPosConfgVersion(pc.getPosConfgVersion());
                            opc.setConfig(pc.getConfig());
                            opc.setLsDirty(pc.getLsDirty());
                            opc.setLastUpdateAt(pc.getLastUpdateAt());
                            opc.setLastUpdateBy(pc.getLastUpdateBy());
                            opc.setDeleted(pc.isDeleted());
                            pc = opc;
                            opc = null;
                        }
                        else if(pcList != null && pcList.size() > 1){
                            for(Map opc : pcList){
                                update(String.format("update pos_config set is_deleted = true where id = %s", opc.get("id"), false));
                            }
                        }
                        pcList = null;
                        tableObj = pc;
                    }
                    else if(tableName.equals(UserLog.class.getSimpleName())){
                        tableObj = GsonUtils.fromJson(GsonUtils.toJson(tableMap), UserLog.class);
                    }
                    else{
                        log.warn("[上传流水数据] POS-{}，未知表名：{}", posId, tableName);
                    }
                    /*if (tableObj != null) {
                        tableList.add(tableObj);
                    }*/
                    if(delSet.size() > 0){
                        try{
                            update(String.format("update %s set is_deleted = true where tenant_id = %s and branch_id = %s and sale_code = '%s'", CommonUtils.DomainName2TableName(tableName), tenantId, branchId, tableMap.get("saleCode")));
                        }
                        catch(Exception se){
                            LogUtil.logInfo("update流水重复处理，数据：" + tableObj.getClass().getSimpleName() + "参数Map:---" + CommonUtils.ObjectToMap(tableObj));
                        }
                    }
                    if(tableObj != null){
                        try{
                            sqlSession.insert(String.format("erp.chain.mapper.%sMapper.insert", tableObj.getClass().getSimpleName()), CommonUtils.ObjectToMap(tableObj));
                            if(tableName.equals(SaleDetail.class.getSimpleName())){
                                SaleDetail detail = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SaleDetail.class);
                                /** 发送流水消息 开始 */
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("tenantId", detail.getTenantId());
                                map.put("branchId", detail.getBranchId());
                                map.put("goodsId", detail.getGoodsId());
                                map.put("price", detail.getSalePrice());
                                map.put("quantity", detail.getQuantity());
                                map.put("createBy", detail.getCreateBy());
                                map.put("localSign", detail.getLocalSign());
                                if(detail.isRefund() == 1){
                                    map.put("occurType", 5);
                                }
                                else{
                                    map.put("occurType", 1);
                                }
                                map.put("code", detail.getSaleCode());
                                if(tableMap.get("payAt") != null && !tableMap.get("payAt").toString().trim().equals("")){
                                    map.put("billCreateTime", tableMap.get("payAt"));
                                }
                                else{
                                    map.put("billCreateTime", dateFormat.format(new Date()));
                                }
                                String saleJson = JSONObject.fromObject(map).toString();
                                if(saleList != null && !saleList.contains(saleJson)){
                                    saleList.add(saleJson);
                                }
                                /** 发送流水消息 结束 */
                                if(tableMap.get("vipId") != null && !tableMap.get("vipId").toString().trim().equals("") && !tableMap.get("vipId").toString().trim().equals("0")){
                                    map.put("vipId", tableMap.get("vipId"));
                                    map.put("isFreeOfCharge", detail.getIsFreeOfCharge());
                                    map.remove("occurType");
                                    map.remove("code");
                                    map.remove("billCreateTime");
                                    map.remove("price");
                                    map.put("tradeCode", detail.getSaleCode());
                                    map.put("isRefund", detail.isRefund());
                                    map.put("scoreType", "goods");
                                    map.put("amount", detail.getReceivedAmount());
                                    map.put("empId", detail.getCashier());
                                    map.put("tradeDate", dateFormat.format(detail.getCreateAt()));
                                    map.put("localSign", detail.getLocalSign());
                                    String scoreJson = JSONObject.fromObject(map).toString();
                                    if(scoreGoodsList != null && !scoreGoodsList.contains(scoreJson)){
                                        scoreGoodsList.add(scoreJson);
                                    }
                                }
                                if(tableMap.get("guideId") != null && !tableMap.get("guideId").toString().trim().equals("") && !tableMap.get("guideId").toString().trim().equals("0") && !tableMap.get("guideId").toString().trim().equals("-1")){
                                    Map guideMap = tableMap;
                                    guideMap.put("createAt", dateFormat.format(detail.getCreateAt()));
                                    guideMap.put("lastUpdateAt", dateFormat.format(detail.getLastUpdateAt()));
                                    guideMap.put("localSign", detail.getLocalSign());
                                    String guideJson = JSONObject.fromObject(guideMap).toString();
                                    if(guideSaleList != null && !guideSaleList.contains(guideJson)){
                                        guideSaleList.add(guideJson);
                                    }
                                }
                            }
                            else if(tableName.equals(SalePayment.class.getSimpleName())){
                                SalePayment salePayment = GsonUtils.fromJson(GsonUtils.toJson(tableMap), SalePayment.class);
                                BigInteger paymentId = BigInteger.valueOf(Long.valueOf(tableMap.get("paymentId").toString()));
                                Payment payment = paymentMapper.getPaymentByIdAll(paymentId, tenantId);
                                if(payment != null){
                                    Map m = new HashMap();
                                    m.put("vipId", tableMap.get("scoreVipId") == null ? tableMap.get("vipId") : tableMap.get("scoreVipId"));
                                    BigDecimal amount = BigDecimal.valueOf(Double.valueOf(tableMap.get("amount").toString())).abs();
                                    if(tableMap.get("isRefund").equals("true") || tableMap.get("isRefund").equals("1")){
                                        m.put("amount", amount.multiply(BigDecimal.valueOf(-1)));
                                    }
                                    else{
                                        m.put("amount", amount);
                                    }
                                    m.put("tradeCode", tableMap.get("saleCode"));
                                    m.put("paymentCode", payment.getPaymentCode());
                                    m.put("paymentId", payment.getId());
                                    m.put("isRefund", tableMap.get("isRefund"));
                                    m.put("branchId", pos.getBranchId());
                                    m.put("empId", tableMap.get("cashier"));
                                    m.put("tenantId", pos.getTenantId());
                                    m.put("tradeDate", dateFormat.format(salePayment.getPaymentAt()));
                                    m.put("isLongAmount", salePayment.getIsLongAmount() ? 1 : 0);
                                    m.put("localSign", salePayment.getLocalSign());
                                    String payScoreJson = new ObjectMapper().writeValueAsString(m);
                                    if(vipScoreList != null && !vipScoreList.contains(payScoreJson)){
                                        vipScoreList.add(payScoreJson);
                                    }
                                }
                            }

                        }
                        catch(Exception e){
                            LogUtil.logInfo("流水重复处理，数据：" + tableObj.getClass().getSimpleName() + "参数Map:---" + CommonUtils.ObjectToMap(tableObj));
                        }
                    }
                }
                /*if (delSet.size() > 0) {
                    delData.put(tableName, delSet);
                }*/
            }
            /*for (Map.Entry e : delData.entrySet()) {
                String tableName = (String) e.getKey();
                Set saleCodeSet = (Set) e.getValue();
                for(Object o : saleCodeSet){
                    try{
                        update(String.format("update %s set is_deleted = true where sale_code = '%s' and tenant_id = %s and branch_id = %s", CommonUtils.DomainName2TableName(tableName), o, tenantId, branchId));
                    }catch(Exception se){
                        LogUtil.logInfo("update流水重复处理，数据："+o.getClass().getSimpleName()+"参数Map:---"+CommonUtils.ObjectToMap(o));
                    }
                }
            }*/
            /*ObjectMapper objectMapper = new ObjectMapper();
            for(Object o : tableList){
                try{
                    sqlSession.insert(String.format("erp.chain.mapper.%sMapper.insert", o.getClass().getSimpleName()), CommonUtils.ObjectToMap(o));
                }catch(Exception e){
                    LogUtil.logInfo("流水重复处理，数据："+o.getClass().getSimpleName()+"参数Map:---"+CommonUtils.ObjectToMap(o));
                }
            }*/
            resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message", "流水数据上传成功");
            try{
                String topic = null;
                topic = PropertyUtils.getDefault("redis_topics");
                if(StringUtils.isNotEmpty(topic) && !saleList.isEmpty()){
                    Long l = PartitionCacheUtils.lpush(topic, (String[])saleList.toArray(new String[saleList.size()]));
                    if(l == null){
                        throw new Exception("库存计算发送失败");
                    }
                }
                if(!vipScoreList.isEmpty()){
                    String t = PropertyUtils.getDefault("topic.vip.score");
                    Long l = PartitionCacheUtils.lpush(t, (String[])vipScoreList.toArray(new String[vipScoreList.size()]));
                    if(l == null){
                        throw new Exception("积分计算发送失败");
                    }
                }
                if(!scoreGoodsList.isEmpty()){
                    String t = PropertyUtils.getDefault("topic.vip.score");
                    Long l = PartitionCacheUtils.lpush(t, (String[])scoreGoodsList.toArray(new String[scoreGoodsList.size()]));
                    if(l == null){
                        throw new Exception("商品积分计算发送失败");
                    }
                }
                if(!guideSaleList.isEmpty()){
                    String t = PropertyUtils.getDefault("topic.guide.sale");
                    Long l = PartitionCacheUtils.lpush(t, (String[])guideSaleList.toArray(new String[guideSaleList.size()]));
                    if(l == null){
                        throw new Exception("导购员销售信息队列写入失败");
                    }
                }
            }
            catch(Exception e){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "数据上传异常");
                LogUtil.logError("流水消息发送失败 - POS-" + posId + "--" + e.getMessage());
                throw e;
            }
        }
        catch(Exception e){
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "数据上传异常");
            resultMap.put("Error", e.getClass().getSimpleName() + " - " + e.getMessage());
            resultMap.put("NewData", Constants.POS_NO_NEW_DATA);
            log.error("DataService.checkData({},{}) - {} - {}", json, posId, e.getClass().getSimpleName(), e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return resultMap;
    }
}
