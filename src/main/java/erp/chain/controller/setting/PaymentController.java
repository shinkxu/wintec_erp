package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.service.setting.PaymentService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付方式
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/2
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/payment","/settingWebService","/baseWS"})
public class PaymentController{
    @Autowired
    private PaymentService paymentService;
    /**
     * 支付方式列表
     * @return
     */
    @RequestMapping("/queryPayment")
    public String queryPayment(){
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
            apiRest = paymentService.queryPayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 支付方式列表(包含已删除的支付方式)为支付流水支付方式创建
     * @return
     */
    @RequestMapping("/queryPaymentListByPayment")
    public String queryPaymentListByPayment(){
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
            apiRest = paymentService.queryPaymentListByPayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据id查询支付方式
     * @return
     */
    @RequestMapping(value = {"/getPaymentById","/queryPaymentById"})
    public String getPaymentById(){
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
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.getPaymentById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改支付方式
     * @return
     */
    @RequestMapping("/addOrUpdatePayment")
    public String addOrUpdatePayment(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("paymentName")==null||params.get("paymentName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数paymentName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("paymentStatus")==null||params.get("paymentStatus").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数paymentStatus不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isScore")==null||params.get("isScore").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isScore不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isVoucher")==null||params.get("isVoucher").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isVoucher不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || params.get("branchId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (!(params.get("paymentStatus").equals("0")  || params.get("paymentStatus").equals("1"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数paymentStatus只能为0或1！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (!(params.get("isScore").equals("0")  || params.get("isScore").equals("1"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isScore只能为0或1！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (!(params.get("isVoucher").equals("0")  || params.get("isVoucher").equals("1"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isVoucher只能为0或1！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("isVoucher").equals("1")){//使用代金券
                java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("[0-9]*");
                if(params.get("fixNum") == null||params.get("fixNum").equals("")){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("isVoucher为1时，参数fixNum必须是非空正整数（可以为0）！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                java.util.regex.Matcher match2 = pattern2.matcher(params.get("fixNum"));
                if(params.get("fixNum") == null || params.get("fixNum").equals("") || !match2.matches() || Integer.valueOf(params.get("fixNum")) < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("isVoucher为1时，参数fixNum必须是非空正整数（可以为0）！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if(params.get("fixValue") == null || params.get("fixValue").equals("") || BigDecimal.valueOf(Double.valueOf(params.get("fixNum"))).compareTo(BigDecimal.ZERO) == -1){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("isVoucher为1时，参数fixValue必须大于0！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = paymentService.addOrUpdatePayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改支付方式
     * @return
     */
    @RequestMapping("/savePayment")
    public String savePayment(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("paymentName")==null||params.get("paymentName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数paymentName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("paymentStatus")==null||params.get("paymentStatus").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数paymentStatus不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isScore")==null||params.get("isScore").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isScore不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || params.get("branchId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.saveOrUpdatePayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改支付方式
     * @return
     */
    @RequestMapping(value={"/isEnablePayment","scoreOrNot"})
    public String isEnablePayment(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || params.get("branchId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || params.get("id").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.isEnablePayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取支付方式编码
     * @return
     */
    @RequestMapping("/getPaymentCode")
    public String getPaymentCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.getPaymentCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除支付方式
     * @return
     */
    @RequestMapping("/delPayment")
    public String delPayment(){
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
            apiRest = paymentService.delPayment(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除支付方式
     * @return
     */
    @RequestMapping("/deletePayments")
    public String deletePayments(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("ids")==null||params.get("ids").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数ids不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.deletePayments(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询可用于储值的支付方式
     * */
    @RequestMapping("/queryPaymentForStore")
    public String queryPaymentForStore(){
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
            apiRest = paymentService.queryPaymentForStore(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 支付方式排序
     * */
    @RequestMapping("/savePaymentSort")
    public String savePaymentSort(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if (params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = paymentService.savePaymentSort(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询门店支付方式
     */
    @RequestMapping("/listBranchsPayment")
    public String  listBranchsPayment(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        if(params.get("tenantId")==null){
            apiRest.setMessage("tenantId不能为空");
            apiRest.setIsSuccess(false);
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId")==null){
            apiRest.setMessage("branchId不能为空");
            apiRest.setIsSuccess(false);
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest= paymentService.listBranchsPayment(params);

        return BeanUtils.toJsonStr(apiRest);
    }
}
