package erp.chain.controller.system;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.service.system.WebServiceTenantMonitorService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Map;

/**
 * 统计商户数接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/13
 */
@Controller
@ResponseBody
@RequestMapping("/tenantMonitorWebService")
public class TenantMonitorWebServiceController{
    @Autowired
    private WebServiceTenantMonitorService webServiceTenantMonitorService;

    /**
     * 统计机构数量(saas-business 调用)
     * @return
     */
    @RequestMapping("/statisticsBranchCount")
    public String statisticsBranchCount() {
        Map<String,String> params= AppHandler.params();
        ApiRest apiRest = new ApiRest();
        try {
            String tenantType = params.get("tenantType");
            apiRest = webServiceTenantMonitorService.statisticsBranchCount(tenantType);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 统计机构数量(saas-business 调用)
     * @return
     */
    @RequestMapping("/doBranchChartSearch")
    public String doBranchChartSearch() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String beginDateString = params.get("beginDateString");
            String endDateString = params.get("endDateString");
            String flag = params.get("flag");
            String branchType = params.get("branchType");
            apiRest = webServiceTenantMonitorService.doBranchChartSearch(beginDateString, endDateString, flag, branchType);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 标记测试商户
     */
    @RequestMapping("/markTestTenant")
    public String markTestTenant() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notNull(tenantId, "商户ID不能为空！");
            apiRest = webServiceTenantMonitorService.markTestTenant(BigInteger.valueOf(Long.valueOf(tenantId)));
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 统计机构数量
     */
    @RequestMapping("/queryBranchCount")
    public String queryBranchCount() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantIds = params.get("tenantIds");
            Validate.notNull(tenantIds, "商户ID不能为空！");
            apiRest = webServiceTenantMonitorService.queryBranchCount(tenantIds);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError(se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
