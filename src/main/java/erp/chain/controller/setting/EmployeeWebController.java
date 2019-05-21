package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.setting.EmployeeWebService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Map;

/**
 * 后台员工
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/1
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/employee","/settingWebService","/baseWS","publicWebService"})
public class EmployeeWebController{
    private static final String EMPLOYEE_WEB_CONTROLLER_SIMPLE_NAME = "EmployeeWebController";
    @Autowired
    private EmployeeWebService employeeWebService;
    /**
     * 查询员工档案
     * @return
     */
    @RequestMapping(value = {"/queryEmployee"})
    public String queryEmployee() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.queryEmployee(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据ID查询员工档案
     * @return
     */
    @RequestMapping("/queryEmployeeById")
    public String queryEmployeeById() {
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
            apiRest = employeeWebService.queryEmployeeById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改员工档案
     * @return
     */
    @RequestMapping(value = {"/addOrUpdateEmployee","/addOrUpdateEmp","/updateEmp"})
    public String addOrUpdateEmployee() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("state")==null||params.get("state").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数state不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if (params.get("roleIds")==null||params.get("roleIds").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数roleIds不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name").length()>20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("员工名字不能超过20个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("discountRate")==null||params.get("discountRate").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountRate不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match = pattern.matcher(params.get("discountRate"));
            if (!match.matches()||!(Integer.valueOf(params.get("discountRate"))>=0&&Integer.valueOf(params.get("discountRate"))<101)) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountRate必须为0-100之间的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("discountAmount")==null||params.get("discountAmount").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountAmount不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match2 = pattern2.matcher(params.get("discountAmount"));
            if (!match2.matches()||!(Integer.valueOf(params.get("discountAmount"))>=0&&Integer.valueOf(params.get("discountAmount"))<10000)) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountAmount必须为0-9999之间的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.addOrUpdateEmployee(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改员工档案
     * @return
     */
    @RequestMapping(value = {"/saveEmp"})
    public String saveEmp() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("state")==null||params.get("state").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数state不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if (params.get("roleIds")==null||params.get("roleIds").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数roleIds不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name").length()>20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("员工名字不能超过20个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("discountRate")==null||params.get("discountRate").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountRate不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match = pattern.matcher(params.get("discountRate"));
            if (!match.matches()||!(Integer.valueOf(params.get("discountRate"))>=0&&Integer.valueOf(params.get("discountRate"))<101)) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountRate必须为0-100之间的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("discountAmount")==null||params.get("discountAmount").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountAmount不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match2 = pattern2.matcher(params.get("discountAmount"));
            if (!match2.matches()||!(Integer.valueOf(params.get("discountAmount"))>=0&&Integer.valueOf(params.get("discountAmount"))<10000)) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discountAmount必须为0-9999之间的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.saveEmp(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除员工/导购员
     * @return
     */
    @RequestMapping("/delEmployee")
    public String delEmployee() {
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
            apiRest = employeeWebService.delEmployee(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除员工档案
     * @return
     */
    @RequestMapping("/delEmp")
    public String delEmp() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("ids")==null||params.get("ids").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数ids不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.delEmp(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取编码
     * @return
     */
    @RequestMapping("/getEmployeeCode")
    public String getEmployeeCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.getEmployeeCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 重置密码
     * @return
     */
    @RequestMapping(value = {"/resetPassword","/resetEmpPwd"})
    public String resetPassword() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.resetPassword(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取员工信息
     * @return
     */
    @RequestMapping("/getEmployee")
    public String getEmployee() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.getEmployee(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改pos密码
     * @return
     */
    @RequestMapping("/doChangePOSPwd")
    public String doChangePOSPwd() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("oldPass")==null||params.get("oldPass").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数oldPass不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("newPass")==null||params.get("newPass").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数newPass不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.doChangePOSPwd(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 修改pos密码
     * @return
     */
    @RequestMapping("/resetPasswordForLocal")
    public String resetPasswordForLocal() {
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
            if (params.get("empId")==null||params.get("empId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数empId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.resetPasswordForLocal(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 解绑手机
     * @return
     */
    @RequestMapping("/removeMobileBind")
    public String removeMobileBind() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }

            apiRest = employeeWebService.removeMobileBind(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 解绑手机
     * @return
     */
    @RequestMapping("/bindMobile")
    public String bindMobile() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("number")==null||params.get("number").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数number不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("authCode")==null||params.get("authCode").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数authCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("sessionId")==null||params.get("sessionId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数sessionId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }

            apiRest = employeeWebService.bindMobile(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询员工档案
     * @return
     */
    @RequestMapping(value = {"/queryEmpPager"})
    public String queryEmpPager() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.queryEmpPager(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    @RequestMapping(value = "/queryUserRolesByUserId")
    @ResponseBody
    public String queryUserRolesByUserId() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String userId = requestParameters.get("userId");
            Validate.notNull(userId, "参数(userId)不能为空！");
            apiRest = employeeWebService.queryUserRolesByUserId(BigInteger.valueOf(Long.valueOf(userId)));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询角色失败", EMPLOYEE_WEB_CONTROLLER_SIMPLE_NAME, "queryUserRolesByUserId", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改个人信息
     * @return
     */
    @RequestMapping(value = {"/doAccountSetting"})
    public String doAccountSetting(){
        ApiRest r=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            r=employeeWebService.doAccountSetting(params);
        } catch (Exception e){
            r.setIsSuccess(false);
            r.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(r);
    }

    /**
     * 设置员工权限区域
     * */
    @RequestMapping("/areaAuthority")
    public String areaAuthority(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.areaAuthority(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导购员销售提成明细
     * */
    @RequestMapping("/guidePercentageSale")
    public String guidePercentageSale(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.guidePercentageSale(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导购员充值提成明细
     * */
    @RequestMapping("/guidePercentageStore")
    public String guidePercentageStore(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.guidePercentageStore(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导购员提成汇总
     * */
    @RequestMapping("/guideSummary")
    public String guideSummary(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.guideSummary(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导购员销售提成明细
     * */
    @RequestMapping("/guideSaleJob")
    public String guideSaleJob(){
        ApiRest apiRest = new ApiRest();
        try{
            apiRest = employeeWebService.guideSaleJob();
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询导购员
     */
    @RequestMapping("/queryGuide")
    public String queryGuide(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest =employeeWebService.queryGuiDe(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询导购员失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增导购员
     */

    @RequestMapping("/addGuide")
    public String addGuide(){
        ApiRest apiRest =new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("branchId") == null || params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }

            if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("saleRate") == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数saleRate不能为空！");
                return BeanUtils.toJsonStr(apiRest);

            }
            if(params.get("storeRate") == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数storeRate不能为空！");
                return BeanUtils.toJsonStr(apiRest);

            }
            apiRest = employeeWebService.addGuide(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("增加导购员失败");
            LogUtil.logError(e, params);
        }
        return  BeanUtils.toJsonStr(apiRest) ;
    }
    /**
     * 修改导购员
     */
    @RequestMapping("/updateGuide")
    public String  updateGuide(){
        ApiRest apiRest =new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("id") == null || params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest  =employeeWebService.updateGuide(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("修改导购员失败");
            LogUtil.logError(e, params);

        }

        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/changeEmpInfo")
    public String changeEmpInfo(){
        ApiRest apiRest =new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            if (params.get("id") == null || params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("newName") == null || params.get("newName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("newName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest  =employeeWebService.changeEmpInfo(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("修改导购员失败");
            LogUtil.logError(e, params);

        }

        return BeanUtils.toJsonStr(apiRest);
    }
}
