package erp.chain.controller.online;

import com.saas.common.util.LogUtil;
import erp.chain.annotations.ApiRestAction;
import erp.chain.common.Constants;
import erp.chain.model.online.onlinevip.*;
import erp.chain.service.online.OnlineVipService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.XMLUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * Created by liuyandong on 2018-04-13.
 */
@Controller
@RequestMapping(value = "/onlineVip")
public class OnlineVipController {
    private static final String ONLINE_VIP_CONTROLLER_SIMPLE_NAME = "OnlineVipController";
    @Autowired
    private OnlineVipService onlineVipService;

    /**
     * 获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainVipInfoModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "obtainVipInfo", error = "获取会员信息失败")
    public String obtainVipInfo() {
        return null;
    }

    /**
     * 获取会员地址
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipAddress", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainVipAddressModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "obtainVipAddress", error = "获取会员地址失败")
    public String obtainVipAddress() {
        return null;
    }

    /**
     * 保存会员地址
     *
     * @return
     */
    @RequestMapping(value = "/saveVipAddress", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipAddressModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "saveVipAddress", error = "保存会员地址失败")
    public String saveVipAddress() {
        return null;
    }

    /**
     * 查询储值规则
     *
     * @return
     */
    @RequestMapping(value = "/listStoreRules", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListStoreRulesModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "listStoreRules", error = "查询储值规则失败")
    public String listStoreRules() {
        return null;
    }

    /**
     * 更新会员积分（此方法仅用于测试）
     *
     * @return
     */
    @RequestMapping(value = "/updateMemberBonus", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = UpdateMemberBonusModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "updateMemberBonus", error = "更新会员积分失败")
    public String updateMemberBonus() {
        return null;
    }

    /**
     * 保存储值订单
     *
     * @return
     */
    @RequestMapping(value = "/saveStoreOrder")
    @ResponseBody
    @ApiRestAction(modelClass = SaveStoreOrderModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "saveStoreOrder", error = "保存储值订单失败")
    public String saveStoreOrder() {
        return null;
    }

    /**
     * 储值订单付款
     *
     * @return
     */
    @RequestMapping(value = "/doPayStoreOrder")
    @ResponseBody
    @ApiRestAction(modelClass = DoPayStoreOrderModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "doPayStoreOrder", error = "储值订单付款失败")
    public String doPayStoreOrder() {
        return null;
    }

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
                ApiRest apiRest = onlineVipService.handlePayCallback(1, requestParameters);
                Validate.isTrue(apiRest.getIsSuccess(), apiRest.getError());
                returnValue = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                returnValue = "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
            }
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "处理微信支付回调失败", ONLINE_VIP_CONTROLLER_SIMPLE_NAME, "weiXinPayCallback", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            returnValue = "<xml><return_code><![CDATA[FAILURE]]></return_code></xml>";
        }
        return returnValue;
    }

    /**
     * 分页查询储值规则
     *
     * @return
     */
    @RequestMapping(value = "/listVipStoreRuleDetails")
    @ResponseBody
    @ApiRestAction(modelClass = ListVipStoreRuleDetailsModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "listVipStoreRuleDetails", error = "查询储值规则失败")
    public String listVipStoreRuleDetails() {
        return null;
    }

    /**
     * 分页查询次卡方案
     *
     * @return
     */
    @RequestMapping(value = "/listCardTypes")
    @ResponseBody
    @ApiRestAction(modelClass = ListCardTypesModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "listCardTypes", error = "查询次卡方案失败")
    public String listCardTypes() {
        return null;
    }

    /**
     * 查询会员优惠券
     *
     * @return
     */
    @RequestMapping(value = "/listCards")
    @ResponseBody
    @ApiRestAction(modelClass = ListCardsModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "listCards", error = "查询卡券失败")
    public String listCards() {
        return null;
    }

    /**
     * 查询卡券活动
     *
     * @return
     */
    @RequestMapping(value = "/listFestivals")
    @ResponseBody
    @ApiRestAction(modelClass = ListFestivalsModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "listFestivals", error = "查询卡券活动失败")
    public String listFestivals() {
        return null;
    }

    /**
     * 领取优惠券
     *
     * @return
     */
    @RequestMapping(value = "/receiveCoupon")
    @ResponseBody
    @ApiRestAction(modelClass = ReceiveCouponModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "receiveCoupon", error = "领取优惠券失败")
    public String receiveCoupon() {
        return null;
    }

    /**
     * 发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/sendAuthCode")
    @ResponseBody
    @ApiRestAction(modelClass = SendAuthCodeModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "sendAuthCode", error = "发送验证码失败")
    public String sendAuthCode() {
        return null;
    }

    /**
     * 认证会员
     *
     * @return
     */
    @RequestMapping(value = "/verifiedVip", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = VerifiedVipModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "verifiedVip", error = "认证会员信息失败")
    public String verifiedVip() {
        return null;
    }

    /**
     * 获取商户配置
     *
     * @return
     */
    @RequestMapping(value = "/obtainTenantConfig", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainTenantConfigModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "obtainTenantConfig", error = "获取商户配置失败")
    public String obtainTenantConfig() {
        return null;
    }

    /**
     * 查询会员消费历史
     *
     * @return
     */
    @RequestMapping(value = "/queryConsumeHistory", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = QueryConsumeHistoryModel.class, serviceClass = OnlineVipService.class, serviceMethodName = "queryConsumeHistory", error = "查询消费历史失败")
    public String queryConsumeHistory() {
        return null;
    }
}
