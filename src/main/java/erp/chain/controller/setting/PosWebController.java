package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.setting.PosWebService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.ApplicationHandler;
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
 * pos设置
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/29
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/pos","/settingWebService"})
public class PosWebController{
    private static final String POS_WEB_CONTROLLER_SIMPLE_NAME = "PosWebController";
    @Autowired
    private PosWebService posWebService;
    /**
     * 查询POS列表
     * @return
     */
    @RequestMapping("/queryPosList")
    public String queryPosList() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("page")==null||params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows")==null||params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            apiRest = posWebService.queryPosList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据ID查询POS
     * @return
     */
    @RequestMapping("/queryPosById")
    public String queryPosById() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posWebService.queryPosById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改POS
     * @return
     */
    @RequestMapping("/addOrUpdatePos")
    public String addOrUpdatePos() {
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
            if (params.get("password")==null||params.get("password").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数password不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match=pattern.matcher(params.get("password"));
            if(!((match.matches()&&params.get("password").length()==6))){
                apiRest.setIsSuccess(false);
                apiRest.setError("password必须是6位数字！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("status")==null||params.get("status").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数status不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (!(params.get("status").equals("0")||params.get("status").equals("1"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数status取值为0或1！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                if (params.get("tenantCode")==null||params.get("tenantCode").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增POS时，参数tenantCode不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = posWebService.addOrUpdatePos(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 生成单位编码
     * @return
     */
    @RequestMapping("/getPosCode")
    public String getPosCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            BigInteger tenantId=BigInteger.valueOf(Long.parseLong(params.get("tenantId")));
            apiRest = posWebService.getPosCode(tenantId);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 注销POS
     * @return
     */
    @RequestMapping("/cancelPos")
    public String cancelPos() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posWebService.cancelPos(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 自动拉取重置
     * @return
     */
    @RequestMapping("/disableAutoPull")
    public String disableAutoPull() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("posId")==null||params.get("posId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数posId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posWebService.disableAutoPull(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除POS
     * @return
     */
    @RequestMapping("/delPos")
    public String delPos() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posWebService.delPos(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/disableOrEnablePos")
    @ResponseBody
    public String disableOrEnablePos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");

            String posId = requestParameters.get("posId");
            Validate.notEmpty(posId, "参数(posId)不能为空！");

            String status = requestParameters.get("status");
            Validate.notEmpty(status, "参数(status)不能为空！");
            Validate.isTrue("0".equals(status) || "1".equals(status), "参数错误，status 只能为0或1");
            String userName = requestParameters.get("userName");
            String opFrom = requestParameters.get("opFrom");
            apiRest = posWebService.disableOrEnablePos(userName,opFrom,BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)), BigInteger.valueOf(Long.valueOf(posId)), Integer.valueOf(status));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "禁用或启用POS失败", POS_WEB_CONTROLLER_SIMPLE_NAME, "queryUserRolesByUserId", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 获取编码
     * @return
     */
    @RequestMapping("/getDeviceCodes")
    public String getDeviceCodes() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posWebService.getDeviceCodes(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
