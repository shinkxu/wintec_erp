package erp.chain.controller.o2o;

import com.saas.common.util.CacheUtils;
import com.saas.common.util.Common;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.service.o2o.ElemeService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.ElemeUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by liuyandong on 2017/5/16.
 */
@Controller
@RequestMapping(value = "/eleme")
public class ElemeController {
    private static final String ELEME_CONTROLLER_SIMPLE_NAME = "ElemeController";
    @Autowired
    private ElemeService elemeService;

    @RequestMapping(value = "/tenantAuthorize")
    @ResponseBody
    public String tenantAuthorize() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));

            Map<String, String> checkTenantIsAuthorizedParameters = new HashMap<String, String>();
            checkTenantIsAuthorizedParameters.put("tenantId", tenantId);
            checkTenantIsAuthorizedParameters.put("branchId", branchId);
            checkTenantIsAuthorizedParameters.put("flag", Constants.STANDARD_EDITION);
            checkTenantIsAuthorizedParameters.put("elemeAccountType", branch.getElemeAccountType().toString());
            ApiRest checkTenantIsAuthorizedRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "eleme", "checkTenantIsAuthorized", checkTenantIsAuthorizedParameters);
            Validate.isTrue(checkTenantIsAuthorizedRest.getIsSuccess(), checkTenantIsAuthorizedRest.getError());

            String authorizeUrl = null;
            String data = (String) checkTenantIsAuthorizedRest.getData();
            if ("1".equals(data)) {
                authorizeUrl = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_O2O, PropertyUtils.getDefault("partition.code")) + "/eleme/bindingRestaurant?tenantId=" + tenantId + "&branchId=" + branchId;
            } else if ("0".equals(data)) {
                String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
                String state = tenantId + "Z" + branchId + "Z" + branch.getElemeAccountType();
                String scope = "all";
                authorizeUrl = ElemeUtils.generateAuthorizeUrl(appKey, state, scope);
            }
            apiRest = new ApiRest();
            apiRest.setMessage("操作成功！");
            apiRest.setData(authorizeUrl);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "商户授权失败", ELEME_CONTROLLER_SIMPLE_NAME, "tenantAuthorize", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/bindingRestaurant")
    public ModelAndView bindingRestaurant() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("eleme/bindingRestaurant");
            modelAndView.addObject("tenantId", tenantId);
            modelAndView.addObject("branchId", branchId);
            return modelAndView;
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "进入机构绑定页面失败", ELEME_CONTROLLER_SIMPLE_NAME, "bindingRestaurant", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
        }
        return null;
    }

    @RequestMapping(value = "/doBindingRestaurant")
    @ResponseBody
    public String doBindingRestaurant() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String shopId = requestParameters.get("shopId");
            Validate.notNull(shopId, "参数(shopId)不能为空！");

            apiRest = elemeService.doBindingRestaurant(tenantId, branchId, Long.valueOf(shopId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "绑定机构失败", ELEME_CONTROLLER_SIMPLE_NAME, "doBindingRestaurant", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取商户账号信息
     */
    @RequestMapping(value = "/getUser")
    @ResponseBody
    public String getUser() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");
            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.user.getUser", tenantId, branchId, branch.getElemeAccountType().toString(), null);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取商户账号信息失败", ELEME_CONTROLLER_SIMPLE_NAME, "getUser", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取订单
     */
    @RequestMapping(value = "/getOrder")
    @ResponseBody
    public String getOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.getOrder", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取订单失败", ELEME_CONTROLLER_SIMPLE_NAME, "getOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 批量获取订单
     */
    @RequestMapping(value = "/mgetOrders")
    @ResponseBody
    public String mgetOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderIds = requestParameters.get("orderIds");
            Validate.notNull(orderIds, "参数(orderIds)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            List<DietOrderInfo> dietOrderInfos = elemeService.findDietOrderInfoByIds(bigIntegerTenantId, bigIntegerBranchId, orderIds.split(","));

            List<String> ids = new ArrayList<String>();
            for (DietOrderInfo dietOrderInfo : dietOrderInfos) {
                ids.add(dietOrderInfo.getOrderCode().substring(1));
            }

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderIds", ids);
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.mgetOrders", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "批量获取订单失败", ELEME_CONTROLLER_SIMPLE_NAME, "mgetOrders", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 确认订单
     */
    @RequestMapping(value = "/confirmOrder")
    @ResponseBody
    public String confirmOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            /*BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.confirmOrderLite", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);*/

            apiRest = elemeService.confirmOrder(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), NumberUtils.createBigInteger(orderId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "确认订单失败", ELEME_CONTROLLER_SIMPLE_NAME, "confirmOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        LogUtil.logInfo("确认订单：" + BeanUtils.toJsonStr(apiRest));
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "/cancelOrder")
    @ResponseBody
    public String cancelOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            String type = requestParameters.get("type");
            Validate.isTrue(StringUtils.isNotBlank(type), "参数(type)不能为空！");
            Validate.isTrue(cancelReasons.contains(type), "参数(type)必须为" + cancelReasons.toString() + "中的一个！");

            String remark = requestParameters.get("remark");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            parameters.put("type", type);
            if (StringUtils.isNotBlank(remark)) {
                parameters.put("remark", remark);
            }
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.cancelOrderLite", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "取消订单失败", ELEME_CONTROLLER_SIMPLE_NAME, "cancelOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 同意退单/取消单
     */
    @RequestMapping(value = "/agreeRefund")
    @ResponseBody
    public String agreeRefund() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.agreeRefundLite", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "同意退单失败", ELEME_CONTROLLER_SIMPLE_NAME, "agreeRefund", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 不同意退单/取消单
     */
    @RequestMapping(value = "/disagreeRefund")
    @ResponseBody
    public String disagreeRefund() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            String reason = requestParameters.get("reason");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            if (StringUtils.isNotBlank(reason)) {
                parameters.put("reason", reason);
            }
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.disagreeRefundLite", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "不同意退单失败", ELEME_CONTROLLER_SIMPLE_NAME, "disagreeRefund", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取订单配送记录
     *
     * @return
     */
    @RequestMapping(value = "/getDeliveryStateRecord")
    @ResponseBody
    public String getDeliveryStateRecord() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.getDeliveryStateRecord", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取订单配送记录失败", ELEME_CONTROLLER_SIMPLE_NAME, "getDeliveryStateRecord", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 批量获取订单最新配送记录
     */
    @RequestMapping(value = "/batchGetDeliveryStates")
    @ResponseBody
    public String batchGetDeliveryStates() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderIds = requestParameters.get("orderIds");
            Validate.notNull(orderIds, "参数(orderIds)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            List<DietOrderInfo> dietOrderInfos = elemeService.findDietOrderInfoByIds(bigIntegerTenantId, bigIntegerBranchId, orderIds.split(","));
            List<String> ids = new ArrayList<String>();
            for (DietOrderInfo dietOrderInfo : dietOrderInfos) {
                ids.add(dietOrderInfo.getOrderCode().substring(1));
            }

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderIds", ids);
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.batchGetDeliveryStates", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "批量获取订单最新配送记录失败", ELEME_CONTROLLER_SIMPLE_NAME, "batchGetDeliveryStates", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/orderCallback")
    @ResponseBody
    public String orderCallback() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {

            String elemeRequestBody = requestParameters.get("elemeRequestBody");
            Validate.notNull(elemeRequestBody, "参数(elemeRequestBody)不能为空！");

            JSONObject jsonObject = JSONObject.fromObject(elemeRequestBody);
            String requestId = jsonObject.getString("requestId");
            int type = jsonObject.getInt("type");
            BigInteger shopId = BigInteger.valueOf(jsonObject.getLong("shopId"));
            String message = jsonObject.getString("message");
            switch (type) {
                case 10: // 订单生效，订单状态为1(已下单)，如果为在线支付，付款状态为1(已支付)，付款方式为7(饿了么在线支付)，如果为货到付款，付款状态为0(未支付)，付款方式为3(货到付款)
                    apiRest = elemeService.saveElemeOrder(shopId, message, requestId, type);
                    break;
                case 12: // 商户接单，将订单状态修改为2(已接单,提交厨房)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 2, type);
                    break;
                case 14: // 订单被取消，将订单状态修改为9(已拒绝)，如果付款状态为1(已付款)，将付款状态修改为3(已退款)
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 9, 1, 3, type);
                    break;
                case 15: // 订单置为无效，将订单状态修改为9(已拒绝)，如果付款状态为1(已付款)，将付款状态修改为3(已退款)
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 9, 1, 3, type);
                    break;
                case 17: // 订单强制无效，将订单状态修改为9(已拒绝)，如果付款状态为1(已付款)，将付款状态修改为3(已退款)
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 9, 1, 3, type);
                    break;
                case 18: // 订单完结
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 6, 0, 1, type);
                    break;
                case 20: // 用户申请取消单，将订单状态修改为10(用户撤单)，如果付款状态为1(已付款)，将付款状态改为2(申请退款)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 1, 2, type);
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 2, type);
                    apiRest = elemeService.handleUserApplyCancelOrder(shopId, message, requestId, 10, 2, type);
                    break;
                case 21: //用户取消取消单申请，将订单状态修改为2(已接单,提交厨房)，如果付款状态为2(申请退款)，将状态改为1(已支付)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 2, 1, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 22: // 商户拒绝取消订单，将订单状态修改为2(已接单,提交厨房)，若果付款状态为2(申请退款)，将状态改为1(已支付)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 2, 1, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 23: // 商户同意取消订单，将订单状态修改为10(用户撤单)，若果付款状态为2(申请退款)，将状态改为3(已退款)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 2, 3, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 3, type);
                    break;
                case 24: // 用户申请仲裁取消订单，将订单状态修改为10(用户撤单)，如果付款状态为1(已付款)，将付款状态改为2(申请退款)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 1, 2, type);
                    apiRest = new ApiRest();
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("处理成功！");
                    break;
                case 25: // 客服仲裁取消单申请有效，将订单状态修改为10(用户撤单)，若果付款状态为2(申请退款)，将状态改为3(已退款)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 2, 3, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 3, type);
                    break;
                case 26: // 客服仲裁取消单申请无效，将订单状态修改为2(已接单,提交厨房)，若果付款状态为2(申请退款)，将状态改为1(已支付)
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 2, 1, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 30: // 用户申请退单，将订单状态修改为10(用户撤单)，将付款状态修改为2(申请退款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 2, type);
//                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 2, type);
                    apiRest = elemeService.handleUserApplyCancelOrder(shopId, message, requestId, 10, 2, type);
                    break;
                case 31: // 用户取消退单，将订单状态修改为2(已接单,提交厨房)，将付款状态修改为1(已支付)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 1, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 32: // 商户拒绝退单，将订单状态修改为2(已接单,提交厨房)，将付款状态修改为4(商户拒绝退款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 4, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 33: // 商户同意退单，将订单状态修改为10(用户撤单)，将付款状态修改为3(已退款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 3, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 3, type);
                    break;
                case 34: // 用户申请仲裁，将付款状态修改为2(申请退款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 1, type);
                    apiRest = new ApiRest();
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("处理成功！");
                    break;
                case 35: // 客服仲裁退单有效，将订单状态修改为10(用户撤单)，将付款状态修改为3(已退款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 3, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 10, 3, type);
                    break;
                case 36: // 客服仲裁退单无效，将订单状态修改为2(已接单,提交厨房)，将付款状态修改为1(已付款)
//                    apiRest = elemeService.handlePayStatusChange(shopId, message, requestId, 4, type);
                    apiRest = elemeService.handleOrderStatusAndPayStatusChange(shopId, message, requestId, 2, 1, type);
                    break;
                case 45: // 用户催单
                    apiRest = elemeService.handleUserReminder(shopId, message, requestId, type);
                    break;
                case 46: // 商家回复用户催单
                    apiRest = elemeService.handleSellerReplyUserReminder(shopId, message, requestId, type);
                    break;
                case 51: // 订单待分配配送商，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 52: // 订单待分配配送员，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 53: // 配送员取餐中，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 54: // 配送员已到店，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 55: // 配送员配送中，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 56: // 配送成功，将订单状态修改为4(已派送)--
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 4, type);
                    break;
                case 57: // 配送取消，商户取消，将订单状态修改为12(配送取消)
//                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    apiRest = new ApiRest();
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("处理成功！");
                    break;
                case 58: // 配送取消，用户取消，将订单状态修改为12(配送取消)
//                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    apiRest = new ApiRest();
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("处理成功！");
                    break;
                case 59: // 配送取消，物流系统取消，将订单状态修改为12(配送取消)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    break;
                case 60: //  配送失败，呼叫配送晚，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 61: //  配送失败，餐厅出餐问题，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 62: //  配送失败，商户中断配送，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 63: //  配送失败，用户不接电话，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 64: //  配送失败，用户退单，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 65: //  配送失败，用户地址错误，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 66: //   配送失败，超出服务范围，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 67: //  配送失败，骑手标记异常，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 68: //   配送失败，系统自动标记异常，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 69: //  配送失败，其他异常，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 70: //  配送失败，超时标记异常，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 71: //  自行配送，将订单状态修改为3(正在派送)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 3, type);
                    break;
                case 72: //  不再配送，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 11, type);
                    break;
                case 73: //  物流拒单，仅支持在线支付，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    break;
                case 74: //  物流拒单，超出服务范围，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    break;
                case 75: //  物流拒单，请求配送晚，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    break;
                case 76: //  物流拒单，系统异常，将订单状态修改为11(配送失败)
                    apiRest = elemeService.handleOrderStatusChange(shopId, message, requestId, 12, type);
                    break;
            }
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "订单回调处理失败", ELEME_CONTROLLER_SIMPLE_NAME, "orderCallback", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 配送异常或者物流拒单后选择自行配送
     */
    @RequestMapping(value = "/deliveryBySelf")
    @ResponseBody
    public String deliveryBySelf() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.deliveryBySelf", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "配送异常或者物流拒单后选择自行配送失败", ELEME_CONTROLLER_SIMPLE_NAME, "deliveryBySelf", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(true);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 配送异常或者物流拒单后选择不再配送
     */
    @RequestMapping(value = "/noMoreDelivery")
    @ResponseBody
    public String noMoreDelivery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.noMoreDelivery", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "配送异常或者物流拒单后选择不再配送失败", ELEME_CONTROLLER_SIMPLE_NAME, "noMoreDelivery", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 订单确认送达
     *
     * @return
     */
    @RequestMapping(value = "/receivedOrder")
    @ResponseBody
    public String receivedOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.receivedOrder", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "订单确认送达失败", ELEME_CONTROLLER_SIMPLE_NAME, "receivedOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 回复催单
     */
    @RequestMapping(value = "/replyReminder")
    @ResponseBody
    public String replyReminder() {
        return null;
    }

    /**
     * 呼叫配送
     */
    @RequestMapping(value = "/callDelivery")
    @ResponseBody
    public String callDelivery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            String fee = requestParameters.get("fee");
            Integer feeInteger = null;
            if (StringUtils.isNotBlank(fee)) {
                try {
                    feeInteger = Integer.valueOf(fee);
                    Validate.isTrue(feeInteger >= 1 && feeInteger <= 8);
                } catch (Exception e) {
                    throw new RuntimeException("消费只能为1-8之间的整数！");
                }
            }

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            if (feeInteger != null) {
                parameters.put("fee", feeInteger);
            }
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.callDelivery", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "呼叫配送失败", ELEME_CONTROLLER_SIMPLE_NAME, "callDelivery", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/cancelDelivery")
    @ResponseBody
    public String cancelDelivery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String orderId = requestParameters.get("orderId");
            Validate.notNull(orderId, "参数(orderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);

            DietOrderInfo dietOrderInfo = elemeService.findDietOrderInfoById(bigIntegerTenantId, bigIntegerBranchId, NumberUtils.createBigInteger(orderId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
            apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.cancelDelivery", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "取消呼叫配送失败", ELEME_CONTROLLER_SIMPLE_NAME, "cancelDelivery", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/checkIsBind")
    @ResponseBody
    public String checkIsBind() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            apiRest = elemeService.checkIsBind(tenantId, branchId);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "检查是否绑定失败", ELEME_CONTROLLER_SIMPLE_NAME, "checkIsBind", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/batchSettingsExpire")
    @ResponseBody
    public String batchSettingsExpire() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String pattern = requestParameters.get("pattern");
            Validate.notNull(pattern, "参数(pattern)不能为空");

            String seconds = requestParameters.get("seconds");
            Validate.notNull(seconds, "参数(seconds)不能为空！");

            Integer integerSeconds = Integer.valueOf(seconds);

            Jedis jedis = CacheUtils.getJedis();
            Set<String> keys = jedis.keys(pattern);
            for (String key : keys) {
                jedis.expire(key, integerSeconds);
            }
            CacheUtils.returnResource(jedis);
            apiRest = new ApiRest();
            apiRest.setData(keys);
            apiRest.setMessage("操作成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "批量设置过期时间失败", ELEME_CONTROLLER_SIMPLE_NAME, "batchSettingsExpire", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/queryItemByPage ")
    @ResponseBody
    public String queryItemByPage() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            long limit = 20;
            String length = requestParameters.get("length");
            if (StringUtils.isNotBlank(length)) {
                limit = Long.valueOf(length);
            }
            long offset = 0;
            String start = requestParameters.get("start");
            if (StringUtils.isNotBlank(start)) {
                offset = Long.valueOf(start);
            }

            apiRest = elemeService.queryItemByPage(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), limit, offset);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "分页获取店铺下的商品失败", ELEME_CONTROLLER_SIMPLE_NAME, "queryPage", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/listGoodses")
    @ResponseBody
    public String listGoodses() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            BigInteger bigIntegerCategoryId = null;
            if (StringUtils.isNotBlank(categoryId)) {
                bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);
            }

            String goodsId = requestParameters.get("goodsId");
            BigInteger bigIntegerGoodsId = null;
            if (StringUtils.isNotBlank(goodsId)) {
                bigIntegerGoodsId = NumberUtils.createBigInteger(goodsId);
            }

            String codeOrName = requestParameters.get("codeOrName");

            apiRest = elemeService.listGoodses(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), bigIntegerCategoryId, bigIntegerGoodsId, codeOrName);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "分页获取店铺下的商品失败", ELEME_CONTROLLER_SIMPLE_NAME, "queryPage", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/saveGoodsMapping ")
    @ResponseBody
    public String saveGoodsMapping() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String elemeGoodsId = requestParameters.get("elemeGoodsId");
            Validate.notNull(elemeGoodsId, "参数(elemeGoodsId)不能为空！");

            String ourGoodsId = requestParameters.get("ourGoodsId");
            Validate.notNull(ourGoodsId, "参数(ourGoodsId)不能为空！");

            apiRest = elemeService.saveGoodsMapping(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), NumberUtils.createBigInteger(elemeGoodsId), NumberUtils.createBigInteger(ourGoodsId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "分页获取店铺下的商品失败", ELEME_CONTROLLER_SIMPLE_NAME, "queryPage", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/removeItem")
    @ResponseBody
    public String removeItem() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String elemeGoodsId = requestParameters.get("elemeGoodsId");
            Validate.notNull(elemeGoodsId, "参数(elemeGoodsId)不能为空！");

            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("itemId", Long.valueOf(elemeGoodsId));
            ApiRest removeItemApiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.product.item.removeItem", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
            Validate.isTrue(removeItemApiRest.getIsSuccess(), removeItemApiRest.getError());

            apiRest = new ApiRest();
            apiRest.setMessage("删除商品成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "删除商品失败", ELEME_CONTROLLER_SIMPLE_NAME, "removeItem", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    private static List<String> cancelReasons = new ArrayList<String>() {
        {
            add("others");
            add("fakeOrder");
            add("contactUserFailed");
            add("foodSoldOut");
            add("restaurantClosed");
            add("distanceTooFar");
            add("restaurantTooBusy");
            add("forceRejectOrder");
            add("deliveryFault");
            add("notSatisfiedDeliveryRequirement");
        }
    };

    @RequestMapping(value = "/countElemeGoods")
    @ResponseBody
    public String countElemeGoods() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            long total = 0;
            int index = 0;
            while (true) {
                Map<String, Object> queryPage = new HashMap<String, Object>();
                queryPage.put("shopId", branch.getShopId());
                queryPage.put("offset", index++ * 300);
                queryPage.put("limit", 300);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("queryPage", queryPage);
                ApiRest queryItemByPageApiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.product.item.queryItemByPage", tenantId.toString(), branchId.toString(), branch.getElemeAccountType().toString(), parameters);
                Validate.isTrue(queryItemByPageApiRest.getIsSuccess(), queryItemByPageApiRest.getError());
                Map<String, Object> data = (Map<String, Object>) queryItemByPageApiRest.getData();
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");
                int size = result.size();
                total = total + size;
                if (size != 300) {
                    break;
                }
            }
            apiRest = new ApiRest();
            apiRest.setData(total);
            apiRest.setMessage("查询饿了么菜品总数成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询饿了么菜品总数失败", ELEME_CONTROLLER_SIMPLE_NAME, "countElemeGoods", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/getShop")
    @ResponseBody
    public String getShop() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));

            String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
            String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("shopId", branch.getShopId());
            ApiRest getShopApiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.shop.getShop", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
            Validate.isTrue(getShopApiRest.getIsSuccess(), getShopApiRest.getError());

            apiRest = new ApiRest();
            apiRest.setData(getShopApiRest.getData());
            apiRest.setMessage("查询店铺信息成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询店铺信息失败", ELEME_CONTROLLER_SIMPLE_NAME, "getShop", e.getClass().getSimpleName(), e.getMessage(), requestParameters.toString()));
            apiRest = new ApiRest();
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
