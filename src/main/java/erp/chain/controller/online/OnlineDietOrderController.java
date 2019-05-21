package erp.chain.controller.online;

import com.saas.common.util.LogUtil;
import erp.chain.annotations.ApiRestAction;
import erp.chain.common.Constants;
import erp.chain.model.online.onlinedietorder.*;
import erp.chain.model.online.onlinegoods.SaveSelfHelpShoppingOrderModel;
import erp.chain.service.online.OnlineDietOrderService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.WeiXinUtils;
import erp.chain.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Controller
@RequestMapping(value = "/onlineDietOrder")
public class OnlineDietOrderController {
    private static final String ONLINE_DIET_ORDER_CONTROLLER_SIMPLE_NAME = "OnlineDietOrderController";
    @Autowired
    private OnlineDietOrderService onlineDietOrderService;

    @RequestMapping(value = "/saveDietOrder", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveDietOrderModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "saveDietOrder", error = "保存订单失败")
    public String saveDietOrder() throws Exception {
        return null;
    }

    /**
     * 发起支付
     *
     * @return
     */
    @RequestMapping(value = "/doPay", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = DoPayModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "doPay", error = "发起支付失败")
    public String doPay() {
        return null;
    }

    /**
     * 处理微信支付回调
     *
     * @return
     */
    @RequestMapping(value = "/weiXinPayCallback")
    @ResponseBody
    public String weiXinPayCallback() {
        String returnValue = null;
        Map<String, String> requestParameters = null;
        try {
            requestParameters = XMLUtils.xmlToMap(ApplicationHandler.getHttpServletRequest().getInputStream());
            String resultCode = requestParameters.get("result_code");
            String returnCode = requestParameters.get("return_code");
            if ("SUCCESS".equals(resultCode) && "SUCCESS".equals(returnCode)) {
                onlineDietOrderService.handlePayCallback(1, requestParameters);
                returnValue = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                returnValue = "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
            }
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "处理微信支付回调失败", ONLINE_DIET_ORDER_CONTROLLER_SIMPLE_NAME, "weiXinPayCallback", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            returnValue = "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
        }
        return returnValue;
    }

    /**
     * 查询订单列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "list", error = "查询订单列表失败")
    public String list() {
        return null;
    }

    /**
     * 查询订单详情
     *
     * @return
     */
    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = OrderDetailModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "orderDetail", error = "查询订单详情失败")
    public String orderDetail() {
        return null;
    }

    /**
     * 保存自助购订单
     *
     * @return
     */
    @RequestMapping(value = "/saveSelfHelpShoppingOrder", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveSelfHelpShoppingOrderModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "saveSelfHelpShoppingOrder", error = "保存自助购订单失败")
    public String saveSelfHelpShoppingOrder() {
        return null;
    }

    /**
     * 获取桌台信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainTableInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainTableInfoModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "obtainTableInfo", error = "获取桌台信息失败")
    public String obtainTableInfo() {
        return null;
    }

    /**
     * 获取自助购订单信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainSelfHelpShoppingOrder", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainSelfHelpShoppingOrderModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "obtainSelfHelpShoppingOrder", error = "获取订单信息失败")
    public String obtainSelfHelpShoppingOrder() {
        return null;
    }

    /**
     * 核验自助购订单
     *
     * @return
     */
    @RequestMapping(value = "/verifySelfHelpShoppingOrder", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = VerifySelfHelpShoppingOrderModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "verifySelfHelpShoppingOrder", error = "验证自助购订单失败")
    public String verifySelfHelpShoppingOrder() {
        return null;
    }

    /**
     * 查询自助购订单列表
     *
     * @return
     */
    @RequestMapping(value = "/listSelfHelpShoppingOrders", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListSelfHelpShoppingOrdersModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "listSelfHelpShoppingOrders", error = "查询自助购订单列表失败")
    public String listSelfHelpShoppingOrders() {
        return null;
    }

    /**
     * 取消自助购订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelSelfHelpShoppingOrder", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = CancelSelfHelpShoppingOrderMode.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "cancelSelfHelpShoppingOrder", error = "取消自助购订单列表失败")
    public String cancelSelfHelpShoppingOrder() {
        return null;
    }

    /**
     * 测试写流水（此方法只用于测试）
     *
     * @return
     */
    @RequestMapping(value = "/writeSaleFlow", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = WriteSaleFlowModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "writeSaleFlow", error = "写入流水失败")
    public String writeSaleFlow() {
        return null;
    }

    /**
     * 自动完成订单，并写入流水和库存队列
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/autoFinishOrder", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(error = "自动完成订单失败")
    public String autoFinishOrder() throws IOException {
        return BeanUtils.toJsonStr(onlineDietOrderService.autoFinishOrder());
    }

    @RequestMapping(value = "/sendConsumptionRemindTemplate", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(error = "发送模板消息失败")
    public String sendStoreChangeTemplate() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String vipId = requestParameters.get("vipId");
//        WeiXinUtils.sendConsumptionRemindTemplateSafe(BigInteger.valueOf(Long.valueOf(vipId)), "总部", BigDecimal.valueOf(Double.valueOf("1000")), 10);
//        WeiXinUtils.sendRechargeTemplateSafe(BigInteger.valueOf(Long.valueOf(vipId)), "总部", new Date(), BigDecimal.valueOf(Double.valueOf(100)), BigDecimal.valueOf(Double.valueOf(10)), BigDecimal.valueOf(Double.valueOf(300)), 100);
        WeiXinUtils.sendPointChangeTemplateSafe(BigInteger.valueOf(Long.valueOf(vipId)), 3000, 1);
        return Constants.REST_RESULT_SUCCESS;
    }

    /**
     * 查询线上订单列表
     *
     * @return
     */
    @RequestMapping(value = "/listOnlineOrders", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListOnlineOrdersModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "listOnlineOrders", error = "查询线上订单列表失败")
    public String listOnlineOrders() {
        return null;
    }

    /**
     * 查询线上订单列表
     *
     * @return
     */
    @RequestMapping(value = "/listOfflineOrders", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListOfflineOrdersModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "listOfflineOrders", error = "查询线下订单列表失败")
    public String listOfflineOrders() {
        return null;
    }

    @RequestMapping(value = "/offlineOrderDetail", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = OfflineOrderDetailModel.class, serviceClass = OnlineDietOrderService.class, serviceMethodName = "offlineOrderDetail", error = "查询线下订单明细失败")
    public String offlineOrderDetail() {
        return null;
    }
}
