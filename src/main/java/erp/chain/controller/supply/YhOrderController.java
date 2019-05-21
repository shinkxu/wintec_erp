package erp.chain.controller.supply;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.model.supply.*;
import erp.chain.model.supply.store.*;
import erp.chain.service.BranchService;
import erp.chain.service.supply.YhOrderService;
import erp.chain.utils.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 要货相关
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/yhOrder", "chainPSSWebService"})
public class YhOrderController {
    private static final String YH_ORDER_CONTROLLER_SIMPLE_NAME = "YhOrderController";
    @Autowired
    private YhOrderService yhOrderService;
    @Autowired
    BranchService branchService;

    /**
     * 分页查询要货商品信息
     */
    @RequestMapping("/queryYhGoodsPager")
    public String queryYhGoodsPager() {
        ApiRest apiRest = new ApiRest();
        QueryYhGoodsModel model = AppHandler.bindParam(QueryYhGoodsModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(yhOrderService.queryYhGoodsPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存要货单
     */
    @RequestMapping(value = "/saveYhOrder", method = RequestMethod.POST)
    public String saveYhOrder() {
        ApiRest apiRest = new ApiRest();
        YhOrderModel model;
        try {
            model = AppHandler.bind(YhOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest.setData(yhOrderService.saveYhOrder(model));
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改要货单
     */
    @RequestMapping(value = "/updateYhOrder", method = RequestMethod.POST)
    public String updateYhOrder() {
        ApiRest apiRest = new ApiRest();
        YhUpdateOrderModel model;
        try {
            model = AppHandler.bind(YhUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            yhOrderService.updateYhOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (ResourceNotExistException | OptimisticLockingFailureException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询要货单
     */
    @RequestMapping(value = "/queryYhOrderPager")
    public String queryYhOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryYhOrderModel model = AppHandler.bindParam(QueryYhOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(yhOrderService.queryYhOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询单据信息
     */
    @RequestMapping(value = "/queryYhOrderInfo")
    public String queryYhOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("yhBranchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "yhBranchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(yhOrderService.queryYhOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 审核要货单
     */
    @RequestMapping(value = "/auditYhOrder")
    public String auditYhOrder() {
        ApiRest apiRest = new ApiRest();
        AuditOrderModel model = AppHandler.bindParam(AuditOrderModel.class);
        try {
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            yhOrderService.auditOrder(model);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除要货单
     */
    @RequestMapping(value = "/delYhOrder")
    public String delYhOrder() {
        ApiRest apiRest = new ApiRest();
        DelOrderModel model = AppHandler.bindParam(DelOrderModel.class);
        try {
            model.type = 1;
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            model.type = null;
            yhOrderService.delYhOrder(model);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存配送出入库单
     */
    @RequestMapping(value = "/savePsOrder", method = RequestMethod.POST)
    public String savePsOrder() {
        ApiRest apiRest = new ApiRest();
        PsOrderModel model;
        try {
            model = AppHandler.bind(PsOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            yhOrderService.savePsOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改ps
     */
    @RequestMapping(value = "/updatePsOrder", method = RequestMethod.POST)
    public String updatePsOrder() {
        ApiRest apiRest = new ApiRest();
        PsUpdateOrderModel model;
        try {
            model = AppHandler.bind(PsUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            yhOrderService.updatePsOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (ResourceNotExistException | OptimisticLockingFailureException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除ps
     */
    @RequestMapping(value = "/delPsOrder")
    public String delPsOrder() {
        ApiRest apiRest = new ApiRest();
        DelOrderModel model = AppHandler.bindParam(DelOrderModel.class);
        try {
            model.type = 1;
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            model.type = null;
            yhOrderService.delPsOrder(model);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 审核配送出入库单
     */
    @RequestMapping(value = "/auditPsOrder")
    public String auditPsOrder() {
        ApiRest apiRest = new ApiRest();
        AuditOrderModel model = AppHandler.bindParam(AuditOrderModel.class);
        try {
            model.type = 1;
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            model.type = null;
            yhOrderService.auditPsOrder(model);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询配送出入库单
     */
    @RequestMapping("/queryPsOrderPager")
    public String queryPsOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryPsOrderModel model = AppHandler.bindParam(QueryPsOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(yhOrderService.queryPsGoodsPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询单据信息
     */
    @RequestMapping(value = "/queryPsOrderInfo")
    public String queryPsOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(yhOrderService.queryPsOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询要货商品汇总
     */
    @RequestMapping(value = "/queryYhGoodsSumPager")
    public String queryYhGoodsSumPager() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        QueryYhGoodsSumModel model = AppHandler.bindParam(QueryYhGoodsSumModel.class);
        if(params.get("noPaging")!=null&&params.get("noPaging").equals("1")){
            model.setPage(1);
            model.setRows(Integer.MAX_VALUE);
        }
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(yhOrderService.queryYhGoodsSumPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询机构可以购买的产品及库存数量
     * @return
     */
    @RequestMapping(value = "/listRequireGoods")
    public String listRequireGoods() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notNull(params.get("tenantId"), "参数(tenantId)不能为空！");

            Validate.notNull(params.get("branchId"), "参数(branchId)不能为空！");

            Validate.notNull(params.get("tenantCode"), "参数(tenantCode)不能为空！");

            apiRest.setData(yhOrderService.listRequireGoods(params));
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询成功！");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存要货单
     * @return
     */
    @RequestMapping(value = "/saveRequireGoodsOrder")
    public String saveRequireGoodsOrder() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notNull(params.get("tenantId"), "参数(tenantId)不能为空！");

            Validate.notNull(params.get("branchId"), "参数(branchId)不能为空！");

            Validate.notNull(params.get("paramsJson"), "参数(paramsJson)不能为空！");

            Validate.notNull(params.get("userName"), "参数(userName)不能为空！");

            Validate.notNull(params.get("userId"), "参数(userId)不能为空！");

            Validate.notNull(params.get("tenantCode"), "参数(tenantCode)不能为空！");

            apiRest.setData(yhOrderService.saveRequiredGoodsOrder(params));
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询成功！");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/findRequireGoodsOrderById")
    public String findRequireGoodsOrderById() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try {
            Validate.notNull(params.get("tenantId"), "参数(tenantId)不能为空！");

            Validate.notNull(params.get("branchId"), "参数(branchId)不能为空！");

            Validate.notNull(params.get("id"), "参数(id)不能为空！");

            apiRest.setData(yhOrderService.findRequireGoodsOrderById(params));
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询成功！");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/doPay")
    public String doPay(HttpServletRequest request) {
        saas.api.common.ApiRest apiRest = null;
        Map<String, String> params = AppHandler.params();
        try {
            String requireGoodsOrderId = params.get("requireGoodsOrderId");
            Validate.notNull(requireGoodsOrderId, "参数(requireGoodsOrderId)不能为空！");

            String paidType = params.get("paidType");
            Validate.notNull(paidType, "支付类型不能为空！");

            String submitBy = params.get("userId");
            Validate.notNull(submitBy, "用户ID不能为空");

            String tenantId = params.get("tenantId");
            Validate.notNull(tenantId, "商户ID不能为空！");

            String branchId = params.get("branchId");
            Validate.notNull(branchId, "机构ID不能为空！");

            String openid = params.get("openid");
            Validate.notNull(openid, "参数openid不能为空！");
            String ipAddress = request.getRemoteAddr();

            apiRest = yhOrderService.payRequireGoodsOrderById(BigInteger.valueOf(Long.valueOf(requireGoodsOrderId)), Integer.valueOf(paidType), submitBy, BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)), ipAddress, openid);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/wxPayCallBack")
    public String wxPayCallBack(HttpServletRequest request) {
        saas.api.common.ApiRest apiRest = null;
        Map<String, String> requestParameters = null;
        try {
            requestParameters = XMLUtils.xmlToMap(request.getInputStream());
            handleWeiXinPayCallback(requestParameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "微信支付回调处理失败", YH_ORDER_CONTROLLER_SIMPLE_NAME, "wxPayCallBack", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new saas.api.common.ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        if (apiRest.getIsSuccess()) {
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } else {
            return "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
        }
    }

    @RequestMapping(value = "/wxPayCallBack/{requireGoodsOrderId}")
    public String wxPayCallBackNew(@PathVariable("requireGoodsOrderId") String requireGoodsOrderId, HttpServletRequest request) {
        saas.api.common.ApiRest apiRest = null;
        Map<String, String> requestParameters = null;
        try {
            requestParameters = XMLUtils.xmlToMap(request.getInputStream());
            requestParameters.put("requireGoodsOrderId", requireGoodsOrderId);
            handleWeiXinPayCallback(requestParameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "微信支付回调处理失败", YH_ORDER_CONTROLLER_SIMPLE_NAME, "wxPayCallBack", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new saas.api.common.ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        if (apiRest.getIsSuccess()) {
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } else {
            return "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
        }
    }

    private saas.api.common.ApiRest handleWeiXinPayCallback(Map<String, String> requestParameters) throws ParseException {
        LogUtil.logInfo("要货单微信支付回调参数：" + BeanUtils.toJsonStr(requestParameters));
        saas.api.common.ApiRest apiRest = null;
        if (requestParameters != null && "SUCCESS".equals(requestParameters.get("result_code")) && "SUCCESS".equals(requestParameters.get("return_code"))) {
            apiRest = yhOrderService.changeRequireGoodsOrderStatus(requestParameters, 2);
        } else {
            Validate.isTrue(false, "微信支付回调处理失败");
        }
        return apiRest;
    }

    @RequestMapping(value = "/aliPayCallBack/{requireGoodsOrderId}")
    public String aliPayCallBackNew(@PathVariable("requireGoodsOrderId") String requireGoodsOrderId) {
        saas.api.common.ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            requestParameters.put("requireGoodsOrderId", requireGoodsOrderId);
            apiRest = handleAliPayCallback(requestParameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "支付宝支付回调处理失败", YH_ORDER_CONTROLLER_SIMPLE_NAME, "aliPayCallBack", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new saas.api.common.ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        if (apiRest.getIsSuccess()) {
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }

    @RequestMapping(value = "/aliPayCallBack")
    public String aliPayCallBack() {
        saas.api.common.ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = handleAliPayCallback(requestParameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "支付宝支付回调处理失败", YH_ORDER_CONTROLLER_SIMPLE_NAME, "aliPayCallBack", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new saas.api.common.ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        if (apiRest.getIsSuccess()) {
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }

    private saas.api.common.ApiRest handleAliPayCallback(Map<String, String> requestParameters) throws ParseException {
        LogUtil.logInfo("要货单支付宝支付回调参数：" + BeanUtils.toJsonStr(requestParameters));
        String tradeStatus = requestParameters.get("trade_status");
        saas.api.common.ApiRest apiRest = null;
        if ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)) {
            apiRest = yhOrderService.changeRequireGoodsOrderStatus(requestParameters, 1);
        } else {
            Validate.isTrue(false, "支付宝支付回调处理失败！");
        }
        return apiRest;
    }


    /**
     * 保存要货单
     */
    @RequestMapping(value = "/saveYhOrderNew", method = RequestMethod.POST)
    public String saveYhOrderNew() {
        ApiRest apiRest = new ApiRest();
        try {
            YhOrderModel yhOrderModel = AppHandler.bind(YhOrderModel.class);
            Validate.isTrue(yhOrderModel.validate(), yhOrderModel.getErrors().toString());
            apiRest.setData(yhOrderService.saveYhOrderNew(yhOrderModel));
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询最近一次要货单
     * */
    @RequestMapping("/queryLastYhOrder")
    public String queryLastYhOrder(){
        saas.api.common.ApiRest apiRest = new ApiRest();
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
            if(params.get("distributionCenterId") == null || "".equals(params.get("distributionCenterId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数distributionCenterId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = yhOrderService.queryLastYhOrder(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询指定配送中心信息
     * */
    @RequestMapping("/queryDistributionCenterInfo")
    public String queryDistributionCenterInfo(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            List<Branch> list = branchService.findBranchByTenantId(params);
            apiRest.setData(list);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询配送中心成功");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询机构要货模版商品
     * */
    @RequestMapping("/queryYhStencilGoods")
    public String queryYhStencilGoods(){
        saas.api.common.ApiRest apiRest = new ApiRest();
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
            if(params.get("distributionCenterId") == null || "".equals(params.get("distributionCenterId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数distributionCenterId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = yhOrderService.queryYhStencilGoods(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
