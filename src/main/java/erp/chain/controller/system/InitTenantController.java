package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.InitTenantService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 初始化
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/3
 */
@Controller
@ResponseBody
@RequestMapping(value={"/initTenant","/publicWebService"})
public class InitTenantController{
    @Autowired
    private InitTenantService initTenantService;
    @Autowired
    private TenantConfigService tenantConfigService;
    /**
     * 初始化商户信息
     * @return
     */
    @RequestMapping("/inittenant")
    public String inittenant(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest= initTenantService.initTenant(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 读取菜单
     * @return
     */
    @RequestMapping("/getPrivileges")
    public String getPrivileges(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("business")==null||params.get("business").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数business不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= initTenantService.getPrivileges(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 读取菜单
     * @return
     */
    @RequestMapping("/getInitInfo")
    public String getInitInfo(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("businessId")==null||params.get("businessId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数businessId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("type")==null||params.get("type").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= initTenantService.getInitInfo(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 初始化商户登录数据
     * @return
     */
    @RequestMapping("/tenantLoginInit")
    public String tenantLoginInit(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("businessId")==null||params.get("businessId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数businessId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= initTenantService.tenantLoginInit(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 初始化商户登录数据
     * @return
     */
    @RequestMapping("/tenantLoginInit2")
    public String tenantLoginInit2(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= initTenantService.tenantLoginInit2(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 读取商户配置
     * @return
     */
    @RequestMapping("/getTenantConfig")
    public String getTenantConfig(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= tenantConfigService.getTenantConfig(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 读取商户配置
     * @return
     */
    @RequestMapping("/updateTenantConfig")
    public String updateTenantConfig(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("value")==null||params.get("value").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数value不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= tenantConfigService.updateTenantConfig(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 清除门店数据
     * @return
     */
    @RequestMapping("/clearBranchData")
    public String clearBranchData(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (StringUtils.isEmpty(params.get("tenantId"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (StringUtils.isEmpty(params.get("branchId"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (StringUtils.isEmpty(params.get("userName"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (StringUtils.isEmpty(params.get("opFrom"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数opFrom不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (StringUtils.isEmpty(params.get("type"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest= initTenantService.clearBranchData(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
