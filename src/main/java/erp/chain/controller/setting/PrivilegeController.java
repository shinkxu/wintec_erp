package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.service.setting.PrivilegeService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 权限
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/14
 */
@Controller
@ResponseBody
@RequestMapping("/privilege")
public class PrivilegeController{
    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 获取权限
     * @return
     */
    @RequestMapping("/getPrivilege")
    public String getPrivilege(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = privilegeService.getPrivilege(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取角色权限
     * @return
     */
    @RequestMapping("/getRolePrivilege")
    public String getRolePrivilege(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("roleId")==null||params.get("roleId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数roleId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = privilegeService.getRolePrivilege(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 保存角色权限
     * @return
     */
    @RequestMapping("/saveRolePrivilege")
    public String saveRolePrivilege(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("roleId")==null||params.get("roleId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数roleId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("privileges")==null||params.get("privileges").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数privileges不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = privilegeService.saveRolePrivilege(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
