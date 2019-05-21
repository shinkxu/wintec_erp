package erp.chain.service.system;

import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.UniversalMapper;
import erp.chain.mapper.report.ReportMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2017/7/17.
 */
@Service
public class BranchDataStatisticsService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UniversalMapper universalMapper;

    @Transactional(readOnly = true)
    public ApiRest listBranches(Map<String, String> parameters) {
        String queryStr = parameters.get("queryStr");
        String startDate = parameters.get("startDate");
        String endDate = parameters.get("endDate");
        String isEffective = parameters.get("isEffective");

        String branchIds = parameters.get("branchIds");
        List<BigInteger> bigIntegerBranchIds = null;
        if (StringUtils.isNotBlank(branchIds)) {
            bigIntegerBranchIds = new ArrayList<BigInteger>();
            String[] branchIdArray = branchIds.split(",");
            for (String branchId : branchIdArray) {
                bigIntegerBranchIds.add(BigInteger.valueOf(Long.valueOf(branchId)));
            }
        }

        String tenantIds = parameters.get("tenantIds");
        List<BigInteger> bigIntegerTenantIds = null;
        if (StringUtils.isNotBlank(tenantIds)) {
            bigIntegerTenantIds = new ArrayList<BigInteger>();
            String[] tenantIdArray = tenantIds.split(",");
            for (String tenantId : tenantIdArray) {
                bigIntegerTenantIds.add(BigInteger.valueOf(Long.valueOf(tenantId)));
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        String pagination = parameters.get("pagination");
        if ("true".equals(pagination)) {
            int pageNumber = 1;
            String page = parameters.get("page");
            if (StringUtils.isNotBlank(page)) {
                pageNumber = Integer.valueOf(page);
            }

            int maxResult = 20;
            String rows = parameters.get("rows");
            if (StringUtils.isNotBlank(rows)) {
                maxResult = Integer.valueOf(rows);
            }

            int offset = (pageNumber - 1) * maxResult;
            int count = branchMapper.count(queryStr,isEffective, startDate, endDate, parameters.get("sqlOperationSymbol"), bigIntegerBranchIds, bigIntegerTenantIds);
            data.put("total", count);
            if (count > 0) {
                List<Map<String, Object>> branches=branchMapper.listBranches(queryStr,isEffective, startDate, endDate, parameters.get("sqlOperationSymbol"), bigIntegerBranchIds, bigIntegerTenantIds, offset, maxResult);
                data.put("rows", branchData(branches));
            } else {
                data.put("rows", new ArrayList<Map<String, Object>>());
            }
        } else {
            int count = branchMapper.count(queryStr, isEffective,startDate, endDate, parameters.get("sqlOperationSymbol"), bigIntegerBranchIds, bigIntegerTenantIds);
            List<Map<String, Object>> branches=branchMapper.listBranches(queryStr,isEffective, startDate, endDate, parameters.get("sqlOperationSymbol"), bigIntegerBranchIds, bigIntegerTenantIds, null, null);
            data.put("total", count);
            data.put("rows", branchData(branches));
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest findBranchInfoById(BigInteger branchId) {
        Map<String, Object> branchInfo = branchMapper.findBranchInfoById(branchId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branchInfo);
        apiRest.setMessage("查询成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest statisticsBranchCount(List<BigInteger> tenantIds) {
        List<Map<String, Object>> branchCount = branchMapper.statisticsBranchCount(tenantIds);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branchCount);
        apiRest.setMessage("查询成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest statisticsAgentBranchCount(List<BigInteger> tenantIds, Date yesterdayStartTime, Date yesterdayEndTime, Date thisMonthStartTime, Date thisMonthEndTime) {
        List<Map<String, Object>> agentBranchCount = branchMapper.statisticsAgentBranchCount(tenantIds, yesterdayStartTime, yesterdayEndTime, thisMonthStartTime, thisMonthEndTime);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("cumulativeBranchCount", agentBranchCount.get(0).get("branchCount"));
        data.put("yesterdayBranchCount", agentBranchCount.get(1).get("branchCount"));
        data.put("thisMonthBranchCount", agentBranchCount.get(2).get("branchCount"));
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询成功");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    final private static Integer BRANCH_ACTIVE_DAYS=15;
    @Transactional(readOnly = true)
    public List<Map<String,Object>> branchData(List<Map<String,Object>> params){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String today=sdf.format(new Date());
        String yesterday=reportMapper.getDate(today,-1);
        String beginDay=reportMapper.getDate(yesterday,-1*BRANCH_ACTIVE_DAYS);
        String startDate=beginDay+" 00:00:00";
        String endDate=yesterday+" 23:59:59";

        String tenantIds="";
        String branchIds="";
        Map tenantMap=new HashMap();
        for(Map map:params){
            tenantMap.put(map.get("tenantId").toString(),map.get("tenantId").toString());
            branchIds+=(map.get("id").toString()+",");
        }
        if(tenantMap.size()>0){
            for(Object o:tenantMap.keySet()){
                tenantIds+=(tenantMap.get(o)+",");
            }
        }
        if(tenantIds.length()>0&&branchIds.length()>0){
            tenantIds=tenantIds.substring(0,tenantIds.length()-1);
            branchIds=branchIds.substring(0,branchIds.length()-1);
            List<Map> branchGroupList=reportMapper.branchActiveData(tenantIds,branchIds,startDate,endDate);
            for(Map map:params){
                map.put("isActive",0);
                for(Map item:branchGroupList){
                    if((map.get("tenantId").toString().equals(item.get("tenantId").toString()))&&
                            (map.get("id").toString().equals(item.get("branchId").toString()))){
                        map.put("isActive",1);
                    }
                }
            }
        }
        return params;
    }

    @Transactional(readOnly = true)
    public ApiRest statisticsActiveBranches(String tenantIds, String branchIds) throws ParseException {
        List<BigInteger> bigIntegerTenantIds = new ArrayList<BigInteger>();
        List<BigInteger> bigIntegerBranchIds = new ArrayList<BigInteger>();
        String[] tenantIdArray = tenantIds.split(",");
        for (String tenantId : tenantIdArray) {
            bigIntegerTenantIds.add(NumberUtils.createBigInteger(tenantId));
        }

        String[] branchIdArray = branchIds.split(",");
        for (String branchId : branchIdArray) {
            bigIntegerBranchIds.add(NumberUtils.createBigInteger(branchId));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date startDate = simpleDateFormat.parse(simpleDateFormat.format(DateUtils.addDays(date, -1 * BRANCH_ACTIVE_DAYS)).substring(0, 10) + " 00:00:00");
        Date endDate = simpleDateFormat.parse(simpleDateFormat.format(DateUtils.addDays(date, -1)).substring(0, 10) + " 23:59:59");
        List<Map<BigInteger, BigInteger>> activeBranches = reportMapper.statisticsActiveBranches(bigIntegerTenantIds, bigIntegerBranchIds, startDate, endDate);
        Map resultMap=new HashMap();
        List<Map> branches=branchMapper.statisticsBranches(bigIntegerTenantIds,bigIntegerBranchIds);
        resultMap.put("branches",branches);
        List<String> data = new ArrayList<String>();
        for (Map<BigInteger, BigInteger> activeBranch : activeBranches) {
            data.add(activeBranch.get("tenantId") + "_" + activeBranch.get("branchId"));
        }
        resultMap.put("data",data);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(resultMap);
        apiRest.setMessage("统计活跃机构成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest findAllBranches(BigInteger tenantId) {
        List<Map<String, Object>> branchInfos = branchMapper.findAllByTenantId(tenantId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branchInfos);
        apiRest.setMessage("查询机构信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取所有机构信息
     *
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainBranchInfos(Date lastPullTime, boolean reacquire) {
        String findInsertBranchSql = "SELECT tenant_id, id AS branch_id, code, name, branch_type, status, create_at AS create_time, effective_date, is_effective FROM branch WHERE create_at >= #{lastPullTime} AND is_deleted = 0";
        Map<String, Object> findInsertBranchParameters = new HashMap<String, Object>();
        findInsertBranchParameters.put("sql", findInsertBranchSql);
        findInsertBranchParameters.put("lastPullTime", lastPullTime);
        List<Map<String, Object>> insertBranchInfos = universalMapper.executeQuery(findInsertBranchParameters);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("insertBranchInfos", insertBranchInfos);
        if (!reacquire) {
            String findUpdateBranchSql = "SELECT tenant_id, id AS branch_id, code, name, branch_type, status, create_at AS create_time, effective_date, is_effective, is_deleted FROM branch WHERE create_at <= #{lastPullTime} AND last_update_at >= #{lastPullTime}";
            Map<String, Object> findUpdateBranchParameters = new HashMap<String, Object>();
            findUpdateBranchParameters.put("sql", findUpdateBranchSql);
            findUpdateBranchParameters.put("lastPullTime", lastPullTime);
            List<Map<String, Object>> updateBranchInfos = universalMapper.executeQuery(findUpdateBranchParameters);
            data.put("updateBranchInfos", updateBranchInfos);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取所有机构信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
