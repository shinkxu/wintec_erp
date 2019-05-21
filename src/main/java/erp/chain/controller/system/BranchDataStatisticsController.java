package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.system.BranchDataStatisticsService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2017/7/17.
 */
@Controller
@RequestMapping(value = "/branchDataStatistics")
public class BranchDataStatisticsController {
    private static final String BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME = "BranchDataStatisticsController";
    @Autowired
    private BranchDataStatisticsService branchDataStatisticsService;

    @RequestMapping(value = "/listBranches")
    @ResponseBody
    public String listBranches() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = branchDataStatisticsService.listBranches(ApplicationHandler.getRequestParameters());
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "scanCodeOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/findBranchInfoById")
    @ResponseBody
    public String findBranchInfoById() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            apiRest = branchDataStatisticsService.findBranchInfoById(BigInteger.valueOf(Long.valueOf(branchId)));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "findBranchInfoById", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/statisticsBranchCount")
    @ResponseBody
    public String statisticsBranchCount() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantIds = requestParameters.get("tenantIds");
            Validate.notNull(tenantIds, "参数(tenantIds)不能为空！");

            String[] tenantIdArray = tenantIds.split(",");
            List<BigInteger> bigIntegerTenantIds = new ArrayList<BigInteger>();
            for (String tenantId : tenantIdArray) {
                bigIntegerTenantIds.add(BigInteger.valueOf(Long.valueOf(tenantId)));
            }

            apiRest = branchDataStatisticsService.statisticsBranchCount(bigIntegerTenantIds);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "statisticsBranchCount", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/statisticsAgentBranchCount", method = RequestMethod.POST)
    @ResponseBody
    public String statisticsAgentBranchCount() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantIds = requestParameters.get("tenantIds");
            Validate.notNull(tenantIds, "参数(tenantIds)不能为空！");

            String[] tenantIdArray = tenantIds.split(",");
            List<BigInteger> bigIntegerTenantIds = new ArrayList<BigInteger>();
            for (String tenantId : tenantIdArray) {
                bigIntegerTenantIds.add(BigInteger.valueOf(Long.valueOf(tenantId)));
            }

            Date today = new Date();
            Date yesterday = DateUtils.addDays(today, -1);
            SimpleDateFormat yyyyMMddSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String yesterdayString = yyyyMMddSimpleDateFormat.format(yesterday);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date yesterdayStartTime = simpleDateFormat.parse(yesterdayString + " 00:00:00");
            Date yesterdayEndTime = simpleDateFormat.parse(yesterdayString + " 23:59:59");

            Date thisMonthStartDate = DateUtils.setDays(today, 1);
            Date thisMonthEndDate = DateUtils.addDays(DateUtils.addMonths(thisMonthStartDate, 1), -1);
            Date thisMonthStartTime = simpleDateFormat.parse(yyyyMMddSimpleDateFormat.format(thisMonthStartDate) + " 00:00:00");
            Date thisMonthEndTime = simpleDateFormat.parse(yyyyMMddSimpleDateFormat.format(thisMonthEndDate) + " 23:59:59");

            apiRest = branchDataStatisticsService.statisticsAgentBranchCount(bigIntegerTenantIds, yesterdayStartTime, yesterdayEndTime, thisMonthStartTime, thisMonthEndTime);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "statisticsBranchCount", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/statisticsActiveBranches")
    @ResponseBody
    public String statisticsActiveBranches() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantIds = requestParameters.get("tenantIds");
            Validate.notNull(tenantIds, "参数(tenantIds)不能为空！");

            String branchIds = requestParameters.get("branchIds");
            Validate.notNull(branchIds, "参数(branchIds)不能为空！");

            apiRest = branchDataStatisticsService.statisticsActiveBranches(tenantIds, branchIds);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "统计活跃机构失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "statisticsBranchCount", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/findAllBranches")
    @ResponseBody
    public String findAllBranches() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            apiRest = branchDataStatisticsService.findAllBranches(NumberUtils.createBigInteger(tenantId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询机构数据失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "findAllBranches", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/obtainBranchInfos")
    @ResponseBody
    public String obtainBranchInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String lastPullTime = requestParameters.get("lastPullTime");
            Validate.notNull(lastPullTime, "参数(lastPullTime)不能为空！");

            String reacquire = requestParameters.get("reacquire");
            Validate.notNull(reacquire, "参数(reacquire)不能为空！");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            apiRest = branchDataStatisticsService.obtainBranchInfos(simpleDateFormat.parse(lastPullTime), Boolean.parseBoolean(reacquire));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取所有机构信息失败", BRANCH_DATA_STATISTICS_CONTROLLER_SIMPLE_NAME, "obtainBranchInfos", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
