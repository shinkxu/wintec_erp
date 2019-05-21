package erp.chain.service;

import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.Pos;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xumx on 2016/11/8.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class MessageService extends BaseService {

    public Map<String, Object> checkMessage(BigInteger posId) {
        Map<String, Object> resultMap = new HashMap<>();
        String msgPosId = Constants.POS_MESSAGE_REDIS_PREFIX + (posId!=null?posId:0);
        Long dataCount = PartitionCacheUtils.llen(msgPosId);
        if (dataCount.intValue() == 0) {
            resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message","无数据");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        } else {
            JSONArray jsonArr = new JSONArray();
            String data = PartitionCacheUtils.lpop(msgPosId);
            while (StringUtils.isNotEmpty(data)) {
                jsonArr.add(data);
                data = PartitionCacheUtils.lpop(msgPosId);
            }
            resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message","有数据");
            resultMap.put("NewData",Constants.POS_NEW_DATA);
            resultMap.put("Data",jsonArr);
        }
        return resultMap;
    }

    public Map<String, Object> sendMessage(String code, String message, BigInteger posId) {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject jsonMap = new JSONObject();
        jsonMap.put("code",code);
        jsonMap.put("message",message);
        jsonMap.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (posId == null) {
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","消息发送失败");
            return resultMap;
        }
        String msgPosId = Constants.POS_MESSAGE_REDIS_PREFIX + posId;
        if (code.equals("订单消息")) {
            PartitionCacheUtils.lpush(msgPosId, jsonMap.toString());
        } else {
            PartitionCacheUtils.rpush(msgPosId, jsonMap.toString());
        }
        resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
        resultMap.put("Message","消息发送成功");
        return resultMap;
    }
    private boolean isjson(String string){
        try {
            JSONObject jsonStr= JSONObject.fromObject(string);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
    public ApiRest posMessage(Map params){
        ApiRest apiRest=new ApiRest();
        try {
            if(params.get("message")!=null&&!params.get("message").toString().equals("")){
                try {
                    if(isjson(params.get("message").toString())){
                        String key=PropertyUtils.getDefault("partition.code")+"_"+PropertyUtils.getDefault("topic.message.erp");
                        PartitionCacheUtils.lpush(key,params.get("message").toString());
                    }
                } catch (IOException e) {
                    log.error("Pos发布消息异常 - {} - {}", e.getClass().getSimpleName(), e.getMessage());
                }
                return new ApiRest(true,"放入消息回执队列成功！");
            }else{
                if ("pos".equals(params.get("clientType"))) {
                    String tenantId = params.get("tenantId").toString();
                    String branchId = params.get("branchId").toString();
                    String tenantType = params.get("tenantType").toString();
                    BigInteger posId=BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
                    Map pr=new HashMap();
                    pr.put("id",posId);
                    pr.put("tenantId",tenantId);
                    Pos pos=posMapper.queryPosById(pr);
                    String appName ="";
                    if(pos!=null&&pos.getAppName()!=null&&!pos.getAppName().equals("")){
                        appName = pos.getAppName();
                    }
                    List<String> messageList = new ArrayList<>();

                    String queueKey = PropertyUtils.getDefault("partition.code")+"_"+PropertyUtils.getDefault("erp.message.queue") +tenantId+"_";

                    String key = queueKey + ",_*";
                    Set<String> keys = PartitionCacheUtils.keys(key);
                    traversalMessage(keys, messageList, params.get("id").toString(), tenantType, appName);

                    key = queueKey + "*,"+branchId+",*_*";
                    keys = PartitionCacheUtils.keys(key);
                    traversalMessage(keys, messageList, params.get("id").toString(), tenantType, appName);

                    apiRest.setIsSuccess(true);
                    apiRest.setMessage(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    if (!messageList.isEmpty()) {
                        apiRest.setData(messageList);
                    }
                } else {
                    apiRest.setIsSuccess(false);
                }
            }
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询消息失败！");
            LogUtil.logError(e.getMessage());
        }
        return apiRest;
    }

    private void traversalMessage(Set<String> keys, List<String> messageList, String posId, String tenantType, String appName) {
        if (keys != null && !keys.isEmpty()) {
            for (String k : keys) {
                Map<String, String> messageMap = PartitionCacheUtils.hgetAll(k);
                String sent = messageMap.get("sent");
                String exclude = messageMap.get("exclude");
                if ((StringUtils.isNotEmpty(sent) && sent.contains(","+posId+",")) || (StringUtils.isNotEmpty(exclude) && exclude.contains(","+posId+","))) {
                    continue;
                }
                if (messageMap.get("tenantType") == null || tenantType.equals(messageMap.get("tenantType"))) {
                    if (messageMap.get("posType") == null || StringUtils.isEmpty(appName) || appName.equals(messageMap.get("posType"))) {
                        messageList.add(messageMap.get("data"));
                        if (StringUtils.isEmpty(sent)) {
                            sent = ",";
                        }
                        sent += (posId + ",");
                        PartitionCacheUtils.hset(k, "sent", sent);
                        continue;
                    }
                }
                if (StringUtils.isEmpty(exclude)) {
                    exclude = ",";
                }
                exclude += (posId + ",");
                PartitionCacheUtils.hset(k, "exclude", exclude);
            }
        }
    }

}
