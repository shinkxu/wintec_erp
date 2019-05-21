package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.service.setting.EmployeeWebService;
import erp.chain.service.setting.OrganizationService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by liuyandong on 2016/11/25.
 */
@RestController
@ResponseBody
@RequestMapping(value = {"/organization","/settingWebService","/branch","/publicWebService"})
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private EmployeeWebService employeeWebService;

    /**
     * 获取员工信息
     * @return
     */
    @RequestMapping("/findEmployeeByUser")
    public String getEmployee() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = employeeWebService.findEmployeeByUser(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/listBranches","/queryBranch"})
    public String listBranches() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.listBranches(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/listAreas","/queryAreaPager","/queryArea"})
    public String listAreas() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.listAreas(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/changeAreaGroup")
     public String changeAreaGroup() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.changeAreaGroup(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/changeAreaGroupAll")
    public String changeAreaGroupAll() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.changeAreaGroupAll(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/getVipManageValue")
    public String getVipManageValue() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.getVipManageValue(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/getBranchAreaList"})
    public String getBranchAreaList() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.getBranchAreaList(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/getBranchList"})
    public String getBranchList() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            apiRest = organizationService.getBranchList(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = {"/addOrUpdateArea","/addOrUpdateBArea"})
    public String addOrUpdateArea() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            Validate.notEmpty(params.get("name"), "参数(name)不能为空！");
            if (StringUtils.isBlank(params.get("id"))) {
                Validate.notEmpty(params.get("parentId"), "参数(parentId)不能为空！");
            }
            apiRest = organizationService.addOrUpdateArea(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/deleteArea","/delBArea"})
    public String deleteArea() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String areaId = params.get("areaId");
            if(params.get("id")!=null&&!params.get("id").equals("")){
                areaId=params.get("id");
            }
            Validate.notEmpty(areaId, "参数(areaId)不能为空！");
            apiRest = organizationService.deleteArea(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(areaId)));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/findBranchById")
    public String findBranchById() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = params.get("branchId");
            if(params.get("id")!=null&&!params.get("id").equals("")){
                branchId=params.get("id");
            }
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");
            apiRest = organizationService.findBranchById(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/updateBranch")
    public String updateBranch(){
        ApiRest apiRest=new ApiRest();
        Map<String, String> params = AppHandler.params();
         if(params.get("tenantId")==null){
             apiRest.setMessage("参数tenantId不能为空！");
             apiRest.setIsSuccess(false);
             return BeanUtils.toJsonStr(apiRest);
         }
        if(params.get("branchId")==null){
            apiRest.setMessage("参数branchId不能为空！");
            apiRest.setIsSuccess(false);
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("storeInformationName")==null){
            apiRest.setMessage("门店名称不能为空！");
            apiRest.setIsSuccess(false);
            return BeanUtils.toJsonStr(apiRest);
        }
        try{
            organizationService.updateBranch(params);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改成功");
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("修改失败");
        }
            return  BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/addOrUpdateBranch")
    public String addOrUpdateBranch() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notEmpty(params.get("userName"), "参数(userName)不能为空！");
            Validate.notEmpty(params.get("tenantId"), "参数(tenantId)不能为空！");
            Validate.notEmpty(params.get("name"), "参数(name)不能为空！");
            Validate.notEmpty(params.get("code"), "参数(code)不能为空！");

            String branchType = params.get("branchType");
            Validate.notEmpty(branchType, "参数(branchType)不能为空！");
            Validate.isTrue("0".equals(branchType) ||"1".equals(branchType) || "2".equals(branchType) || "3".equals(branchType) || "4".equals(branchType), "参数(branchType)只能为0、1、2、3、4中的一个！");

            String isDockingExternalSystem = params.get("isDockingExternalSystem");
            if ("true".equals(isDockingExternalSystem)) {
                Validate.notEmpty(params.get("externalSystem"), "参数(externalSystem)不能为空！");
            }
            Validate.notEmpty(params.get("areaId"), "参数(areaId)不能为空！");

            if ("1".equals(branchType)) {

            } else {
                Validate.notEmpty(params.get("shippingPriceType"), "参数(shippingPriceType)不能为空！");

                String isTinyhall = params.get("isTinyhall");
                Validate.notEmpty(isTinyhall, "参数(isTinyhall)不能为空！");
                Validate.isTrue("true".equals(isTinyhall) || "false".equals(isTinyhall), "参数(isTinyhall)只能为true或false");
                if ("true".equals(isTinyhall)) {
                    String takeoutStatus = params.get("takeoutStatus");
                    Validate.notEmpty(takeoutStatus, "参数(takeoutStatus)不能为空！");
                    Validate.isTrue("1".equals(takeoutStatus) || "2".equals(takeoutStatus), "参数(takeoutStatus)只能为1或2！");

                    Validate.notEmpty("address", "参数(address)不能为空！");

                    Validate.notEmpty("geolocation", "参数(geolocation)不能为空！");

                    String[] parameterNames = {"isBuffet", "isTakeout", "isInvite", "isAllowPayLater", "wxPay", "arPay", "storePay", "taPay"};
                    for (String parameterName : parameterNames) {
                        String parameterValue = params.get(parameterName);
                        Validate.notEmpty(parameterValue, "参数(" + parameterName + ")不能为空！");
                        Validate.isTrue("true".equals(parameterValue) || "false".equals(parameterValue), "参数(" + parameterName + ")的值只能为true或false");
                    }
                    /*Validate.notEmpty(params.get("address"), "参数(address)不能为空！");
                    Validate.notEmpty(params.get("geolocation"), "参数(geolocation)不能为空！");
                    Validate.notEmpty(params.get("isBuffet"), "参数(isBuffet)不能为空！");
                    Validate.notEmpty(params.get("isTakeout"), "参数(isTakeout)不能为空！");

                    Validate.notEmpty(params.get("isInvite"), "参数(isInvite)不能为空！");
                    Validate.notEmpty(params.get("isAllowPayLater"), "参数(isAllowPayLater)不能为空！");
                    Validate.notEmpty(params.get("wxPay"), "参数(wxPay)不能为空！");
                    Validate.notEmpty(params.get("arPay"), "参数(arPay)不能为空！");

                    Validate.notEmpty(params.get("storePay"), "参数(storePay)不能为空！");
                    Validate.notEmpty(params.get("taPay"), "参数(taPay)不能为空！");*/
                }
            }
            String startTakeoutTime = params.get("startTakeoutTime");
            Validate.notEmpty(startTakeoutTime, "营业时间不能为空！");

            String endTakeoutTime = params.get("endTakeoutTime");
            Validate.notEmpty(endTakeoutTime, "参数(endTakeoutTime)不能为空！");
            params.put("takeoutTime", startTakeoutTime + "-" + endTakeoutTime);

            apiRest = organizationService.addOrUpdateBranch(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/generateNextBranchCode")
    public String generateNextBranchCode() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");
            apiRest = organizationService.generateNextBranchCode(new BigInteger(tenantId));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = {"/deleteBranch","/delBranch"})
    public String deleteBranch() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = params.get("branchId");
            if(params.get("id")!=null&&!params.get("id").equals("")){
                branchId=params.get("id");
            }
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");

            String userName = params.get("userName");
            if(userName==null||userName.equals("")){
                userName="System";
            }
            //Validate.notNull(userName, "参数(userName)不能为空！");
            apiRest = organizationService.deleteBranch(new BigInteger(tenantId), new BigInteger(branchId), userName);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/generateNextAreaCode")
    public String generateNextAreaCode() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String parentId = params.get("parentId");
            Validate.notEmpty(parentId, "参数(parentId)不能为空！");

            apiRest = organizationService.generateNextAreaCode(new BigInteger(tenantId), new BigInteger(parentId));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/listBranchAreaTree")
    public String listBranchAreaTree() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String distributionCenterId = params.get("distributionCenterId");
            Validate.notEmpty(distributionCenterId, "参数(distributionCenterId)不能为空！");
            apiRest = organizationService.listBranchAreaTree(new BigInteger(tenantId), new BigInteger(distributionCenterId),params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/listBranchAreaTreeAll")
    public String listBranchAreaTreeAll() {
        ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");
            apiRest = organizationService.listBranchAreaTreeAll(new BigInteger(tenantId),params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/findBranchByUser")
    private String findBranchByUser() {
        ApiRest r = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                r.setIsSuccess(false);
                r.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(r);
            }
            if (params.get("userId")==null||params.get("userId").equals("")) {
                r.setIsSuccess(false);
                r.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(r);
            }
            r=organizationService.findBranchByUser(params);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setMessage("查询机构信息失败！");
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            LogUtil.logError("查询机构信息失败OrganizationController.findBranchByUser" + params + "-" + e.getMessage());
        }
        return BeanUtils.toJsonStr(r);
    }
    @RequestMapping(value = "/getMainBranch" )
    private String getMainBranch() {
        ApiRest r = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                r.setIsSuccess(false);
                r.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(r);
            }
            r=organizationService.getMainBranch(params);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setMessage("查询总部机构失败！");
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            LogUtil.logError("查询总部机构失败OrganizationController.getMainBranch" + params + "-" + e.getMessage());
        }
        return BeanUtils.toJsonStr(r);
    }

    @RequestMapping(value = "/initEmployee")
    @ResponseBody
    public String initEmployee() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = employeeWebService.initEmployee(requestParameters);
        } catch (Exception e) {
            LogUtil.logError("初始化员工失败：OrganizationController.initEmployee-" + requestParameters.toString() + "-" + e.getMessage());
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/removeMobileBind")
    @ResponseBody
    public String removeMobileBind() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String userId = requestParameters.get("userId");
            Validate.notNull(userId, "参数(userId)不能为空！");

            apiRest = employeeWebService.removeMobileBind(BigInteger.valueOf(Long.valueOf(userId)));
        } catch (Exception e) {
            LogUtil.logError(String.format("%s：%s.%s-%s-%s", "解除手机绑定失败", this.getClass().getSimpleName(), "removeMobileBind", requestParameters.toString(), e.getMessage()));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value="/queryAreasList")
    public String queryAreasList(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = organizationService.queryAreasList(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询区域列表失败");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value="/branchJob")
    public String branchJob(){
        ApiRest apiRest = new ApiRest();
        try{
            organizationService.branchJob();
            apiRest.setIsSuccess(true);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("定时任务执行失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value="/savePosSlaves")
    public String savePosSlaves(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            apiRest=organizationService.savePosSlaves(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("更新pos从机数失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/checkIsHasMall")
    public String checkIsHasMall(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            apiRest = organizationService.checkIsHasMall(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/addSmsCount")
    public String addSmsCount(){
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
            if(params.get("addSmsCount") == null || "".equals(params.get("addSmsCount"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数addSmsCount不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = organizationService.addSmsCount(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/updateBranchSmsUseWay")
    public String updateBranchSmsUseWay(){
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
            apiRest = organizationService.updateBranchSmsUseWay(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 是否启用会员对账功能
     * */
    @RequestMapping("/isUseVipStatement")
    public String isUseVipStatement(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("isUseVipStatement") == null || "".equals(params.get("isUseVipStatement"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isUseVipStatement不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = organizationService.isUseVipStatement(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/queryIsUseVipStatement")
    public String queryIsUseVipStatement(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = organizationService.queryIsUseVipStatement(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
