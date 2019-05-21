package erp.chain.utils;

import com.saas.common.util.CacheUtils;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.beans.weixin.ComponentAccessToken;
import erp.chain.beans.weixin.WeiXinAuthorizerInfo;
import erp.chain.beans.weixin.WeiXinAuthorizerToken;
import erp.chain.common.Constants;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.online.OnlineVip;
import erp.chain.model.weixin.SendTemplateMessageModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-23.
 */
public class WeiXinUtils {
    private static final String WEI_XIN_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static final String WEI_XIN_API_URL = "https://api.weixin.qq.com";

    public static String generateAuthorizeUrl(String appId, String scope, String redirectUri, String state) throws UnsupportedEncodingException {
        return generateAuthorizeUrl(appId, scope, redirectUri, state, null);
    }

    public static String generateAuthorizeUrl(String appId, String scope, String redirectUri, String state, String componentAppId) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(scope)) {
            scope = "snsapi_base";
        }
        StringBuilder authorizeUrl = new StringBuilder(WEI_XIN_AUTHORIZE_URL);
        authorizeUrl.append("?").append("appid=").append(appId);
        authorizeUrl.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, Constants.CHARSET_UTF8));
        authorizeUrl.append("&response_type=code");
        authorizeUrl.append("&scope=").append(scope);
        authorizeUrl.append("&connect_redirect=1");
        if (StringUtils.isNotBlank(state)) {
            authorizeUrl.append("&state=");
            if (state.length() > 128) {
                authorizeUrl.append(state.substring(0, 128));
            } else {
                authorizeUrl.append(state);
            }
        }
        if (StringUtils.isNotBlank(componentAppId)) {
            authorizeUrl.append("&component_appid=").append(componentAppId);
        }
        authorizeUrl.append("&#wechat_redirect");
        return authorizeUrl.toString();
    }

    public static Map<String, String> obtainWechatInfo(String userId) {
        Map<String, String> listWechatInfoParameters = new HashMap<String, String>();
        listWechatInfoParameters.put("sysUserId", userId);
        String listWechatInfoResult = SaaSApi.listWechatInfo(listWechatInfoParameters);
        JSONObject listWechatInfoResultJsonObject = JSONObject.fromObject(listWechatInfoResult);
        boolean isSuccess = listWechatInfoResultJsonObject.getBoolean("isSuccess");
        ValidateUtils.isTrue(isSuccess, listWechatInfoResultJsonObject.optString("error"));

        JSONObject jsonMapJsonObject = listWechatInfoResultJsonObject.getJSONObject("jsonMap");
        JSONArray wechatInfoJsonArray = jsonMapJsonObject.getJSONArray("rows");
        ValidateUtils.isTrue(CollectionUtils.isNotEmpty(wechatInfoJsonArray), "未配置微信公众号！");
        JSONObject wechatInfoJsonObject = wechatInfoJsonArray.getJSONObject(0);

        String appId = wechatInfoJsonObject.getString("appId");
        String secret = wechatInfoJsonObject.getString("appSecret");
        String originalId = wechatInfoJsonObject.getString("originalId");
        Map<String, String> wechatInfo = new HashMap<String, String>();
        wechatInfo.put("appId", appId);
        wechatInfo.put("secret", secret);
        wechatInfo.put("originalId", originalId);
        return wechatInfo;
    }

    public static Map<String, String> obtainWechatInfo(String userId, String tenantId, String tenantCode) {
        Map<String, String> obtainWechatInfoRequestParameters = new HashMap<String, String>();
        if (StringUtils.isNotBlank(userId)) {
            obtainWechatInfoRequestParameters.put("userId", userId);
        }
        if (StringUtils.isNotBlank(tenantId)) {
            obtainWechatInfoRequestParameters.put("tenantId", tenantId);
        }
        if (StringUtils.isNotBlank(tenantCode)) {
            obtainWechatInfoRequestParameters.put("tenantCode", tenantCode);
        }
        saas.api.common.ApiRest obtainWechatInfoApiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "wechat", "obtainWechatInfo", obtainWechatInfoRequestParameters);
        ValidateUtils.isTrue(obtainWechatInfoApiRest.getIsSuccess(), obtainWechatInfoApiRest.getError());

        Map<String, Object> data = (Map<String, Object>) obtainWechatInfoApiRest.getData();

        Map<String, String> wechatInfo = null;
        if (MapUtils.isNotEmpty(data)) {
            String appId = MapUtils.getString(data, "appId");
            String secret = MapUtils.getString(data, "appSecret");
            String originalId = MapUtils.getString(data, "originalId");
            String id = MapUtils.getString(data, "id");
            wechatInfo = new HashMap<String, String>();
            wechatInfo.put("appId", appId);
            wechatInfo.put("secret", secret);
            wechatInfo.put("originalId", originalId);
            wechatInfo.put("id", id);
        }
        return wechatInfo;
    }

    /*public static void updateMemberBonusSafe(String userId, String tenantId, String tenantCode, String code, String cardId, Integer bonus, Integer addBonus, String recordBonus) {
        try {
            updateMemberBonus(userId, tenantId, tenantCode, code, cardId, bonus, addBonus, recordBonus);
        } catch (Exception e) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("tenantId", tenantId);
            parameters.put("code", code);
            parameters.put("cardId", cardId);
            parameters.put("bonus", bonus);
            parameters.put("addBonus", addBonus);
            parameters.put("recordBonus", recordBonus);
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "更新微信会员卡积分失败", WeiXinUtils.class.getName(), "updateMemberBonusSafe", e.getClass().getName(), e.getMessage(), parameters));
        }
    }

    public static void updateMemberBonusSafe(Vip vip, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            int bonus = 0;
            if (vip.getRemainingScore() != null) {
                bonus = vip.getRemainingScore().intValue();
            }
            updateMemberBonusSafe(null, vip.getTenantId().toString(), null, userCardCode, cardId, bonus, null, recordBonus);
        }
    }

    public static void updateMemberBonusSafe(Vip vip, int addBonus, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            int bonus = 0;
            if (vip.getRemainingScore() != null) {
                bonus = vip.getRemainingScore().intValue() + addBonus;
            }
            updateMemberBonusSafe(null, vip.getTenantId().toString(), null, userCardCode, cardId, bonus, null, recordBonus);
        }
    }

    public static void updateMemberBonus(String userId, String tenantId, String tenantCode, String code, String cardId, Integer bonus, Integer addBonus, String recordBonus) {
        Map<String, String> wechatInfo = obtainWechatInfo(userId, tenantId, tenantCode);
        String appId = wechatInfo.get("appId");
        String secret = wechatInfo.get("secret");

        saas.api.common.ApiRest askBaseAccessTokenApiRest = O2OApi.askBaseAccessToken(appId, secret);
        ValidateUtils.isTrue(askBaseAccessTokenApiRest.getIsSuccess(), askBaseAccessTokenApiRest.getError());

        String accessToken = askBaseAccessTokenApiRest.getData().toString();
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("code", code);
        requestBody.put("card_id", cardId);
        if (bonus != null) {
            requestBody.put("bonus", bonus);
        }
        if (addBonus != null) {
            requestBody.put("add_bonus", addBonus);
        }
        if (StringUtils.isNotBlank(recordBonus)) {
            requestBody.put("record_bonus", recordBonus);
        }

        String url = "https://api.weixin.qq.com/card/membercard/updateuser?access_token=" + accessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        JSONObject resultJsonObject = JSONObject.fromObject(result);
        ValidateUtils.isTrue(resultJsonObject.getInt("errcode") == 0, resultJsonObject.getString("errmsg"));
    }*/

    public static void updateMemberBonusSafe(String tenantId, String code, String cardId, Integer bonus, Integer addBonus, String recordBonus) {
        try {
            updateMemberBonus(tenantId, code, cardId, bonus, addBonus, recordBonus);
        } catch (Exception e) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("tenantId", tenantId);
            parameters.put("code", code);
            parameters.put("cardId", cardId);
            parameters.put("bonus", bonus);
            parameters.put("addBonus", addBonus);
            parameters.put("recordBonus", recordBonus);
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "更新微信会员卡积分失败", WeiXinUtils.class.getName(), "updateMemberBonusSafe", e.getClass().getName(), e.getMessage(), parameters));
        }
    }

    public static void updateMemberBonusSafe(Vip vip, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            int bonus = 0;
            if (vip.getRemainingScore() != null) {
                bonus = vip.getRemainingScore().intValue();
            }
            updateMemberBonusSafe(vip.getTenantId().toString(), userCardCode, cardId, bonus, null, recordBonus);
        }
    }

    public static void updateMemberBonusSafe(Vip vip, int addBonus, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            int bonus = 0;
            if (vip.getRemainingScore() != null) {
                bonus = vip.getRemainingScore().intValue() + addBonus;
            }
            updateMemberBonusSafe(vip.getTenantId().toString(), userCardCode, cardId, bonus, null, recordBonus);
        }
    }

    public static void updateMemberBonus(String tenantId, String code, String cardId, Integer bonus, Integer addBonus, String recordBonus) throws IOException {
        String authorizerAccessToken = obtainAuthorizerAccessToken(tenantId);
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("code", code);
        requestBody.put("card_id", cardId);
        if (bonus != null) {
            requestBody.put("bonus", bonus);
        }
        if (addBonus != null) {
            requestBody.put("add_bonus", addBonus);
        }
        if (StringUtils.isNotBlank(recordBonus)) {
            requestBody.put("record_bonus", recordBonus);
        }

        String url = "https://api.weixin.qq.com/card/membercard/updateuser?access_token=" + authorizerAccessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        JSONObject resultJsonObject = JSONObject.fromObject(result);
        ValidateUtils.isTrue(resultJsonObject.getInt("errcode") == 0, resultJsonObject.getString("errmsg"));
    }

    public static String obtainRecordBonus(int bookType) {
        String recordBonus = null;
        if (bookType == 1) {
            recordBonus = "消费积分变动";
        } else if (bookType == 2) {
            recordBonus = "使用积分";
        } else if (bookType == 3) {
            recordBonus = "赠送积分";
        } else if (bookType == 4) {
            recordBonus = "积分清零";
        } else if (bookType == 5) {
            recordBonus = "退卡";
        } else if (bookType == 6) {
            recordBonus = "冲正扣减";
        }
        return recordBonus;
    }

    /*public static void sendConsumptionRemindTemplate(BigInteger vipId, String branchName, BigDecimal consumptionAmount, Integer createPoint) {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId()) || consumptionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Map<String, String> wechatInfo = obtainWechatInfo(null, onlineVip.getTenantId().toString(), null);
        String originalId = MapUtils.getString(wechatInfo, "originalId");

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String first = "您好，您已消费成功。";
        String keyword1 = decimalFormat.format(consumptionAmount) + "元";
        String keyword2 = decimalFormat.format(consumptionAmount) + "元";
        String keyword3 = decimalFormat.format(onlineVip.getVipStore()) + "元";
        String keyword4 = branchName;
        String remark = null;
        if (createPoint > 0) {
            remark = "本次消费产生" + createPoint + "个积分。感谢您的光临，并欢迎您对我们的服务提出宝贵意见。";
        } else {
            remark = "感谢您的光临，并欢迎您对我们的服务提出宝贵意见。";
        }
        String[] templateKeys = {first, keyword1, keyword2, keyword3, keyword4, remark};
        ApiRest apiRest = O2OApi.sendTemplate(onlineVip.getOriginalId(), originalId, "XFTX", templateKeys, "");
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError() + "" + apiRest.getMessage());
    }*/

    public static void sendConsumptionRemindTemplate(BigInteger vipId, String branchName, BigDecimal consumptionAmount, Integer createPoint) throws IOException {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId()) || consumptionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String first = "您好，您已消费成功。";
        String keyword1 = decimalFormat.format(consumptionAmount) + "元";
        String keyword2 = decimalFormat.format(consumptionAmount) + "元";
        String keyword3 = decimalFormat.format(onlineVip.getVipStore()) + "元";
        String keyword4 = branchName;
        String remark = null;
        if (createPoint > 0) {
            remark = "本次消费产生" + createPoint + "个积分。感谢您的光临，并欢迎您对我们的服务提出宝贵意见。";
        } else {
            remark = "感谢您的光临，并欢迎您对我们的服务提出宝贵意见。";
        }

        SendTemplateMessageModel sendTemplateMessageModel = new SendTemplateMessageModel();
        sendTemplateMessageModel.setOpenId(onlineVip.getOriginalId());
        sendTemplateMessageModel.setTemplateId(obtainTemplateId(onlineVip.getTenantId().toString(), "XFTX"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("first", buildTemplateDataItem(first, null));
        data.put("keyword1", buildTemplateDataItem(keyword1, null));
        data.put("keyword2", buildTemplateDataItem(keyword2, null));
        data.put("keyword3", buildTemplateDataItem(keyword3, null));
        data.put("keyword4", buildTemplateDataItem(keyword4, null));
        data.put("remark", buildTemplateDataItem(remark, null));
        sendTemplateMessageModel.setData(data);

        sendTemplateMessage(obtainAuthorizerAccessToken(onlineVip.getTenantId()), sendTemplateMessageModel);
    }

    public static void sendConsumptionRemindTemplateSafe(BigInteger vipId, String branchName, BigDecimal consumptionAmount, Integer createPoint) {
        try {
            sendConsumptionRemindTemplate(vipId, branchName, consumptionAmount, createPoint);
        } catch (Exception e) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("vipId", vipId);
            parameters.put("branchName", branchName);
            parameters.put("consumptionAmount", consumptionAmount);
            parameters.put("createPoint", createPoint);
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "发送消费提醒模板消息失败", WeiXinUtils.class.getName(), "sendConsumptionTemplateSafe", e.getClass().getName(), e.getMessage(), parameters));
        }
    }

    /*public static void sendRechargeTemplate(BigInteger vipId, String branchName, Date rechargeTime, BigDecimal rechargeAmount, BigDecimal giveAmount, BigDecimal availableBalance, Integer givePoint) {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId())) {
            return;
        }

        Map<String, String> wechatInfo = obtainWechatInfo(null, onlineVip.getTenantId().toString(), null);
        String originalId = MapUtils.getString(wechatInfo, "originalId");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String first = "尊敬的会员，您的会员卡已充值成功";
        String keyword1 = branchName;
        String keyword2 = simpleDateFormat.format(rechargeTime);
        String keyword3 = decimalFormat.format(rechargeAmount);
        String keyword4 = decimalFormat.format(giveAmount);
        String keyword5 = decimalFormat.format(availableBalance);
        String remark = null;
        if (givePoint > 0) {
            remark = "本次充值赠送" + givePoint + "个积分。如有疑问，请咨询商家客服";
        } else {
            remark = "如有疑问，请咨询商家客服";
        }

        String[] templateKeys = {first, keyword1, keyword2, keyword3, keyword4, keyword5, remark};
        ApiRest apiRest = O2OApi.sendTemplate(onlineVip.getOriginalId(), originalId, "CZTZ", templateKeys, "");
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError() + "" + apiRest.getMessage());
    }*/

    public static void sendRechargeTemplate(BigInteger vipId, String branchName, Date rechargeTime, BigDecimal rechargeAmount, BigDecimal giveAmount, BigDecimal availableBalance, Integer givePoint) throws IOException {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId())) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String first = "尊敬的会员，您的会员卡已充值成功";
        String keyword1 = branchName;
        String keyword2 = simpleDateFormat.format(rechargeTime);
        String keyword3 = decimalFormat.format(rechargeAmount);
        String keyword4 = decimalFormat.format(giveAmount);
        String keyword5 = decimalFormat.format(availableBalance);
        String remark = null;
        if (givePoint > 0) {
            remark = "本次充值赠送" + givePoint + "个积分。如有疑问，请咨询商家客服";
        } else {
            remark = "如有疑问，请咨询商家客服";
        }

        SendTemplateMessageModel sendTemplateMessageModel = new SendTemplateMessageModel();
        sendTemplateMessageModel.setOpenId(onlineVip.getOriginalId());
        sendTemplateMessageModel.setTemplateId(obtainTemplateId(onlineVip.getTenantId().toString(), "CZTZ"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("first", buildTemplateDataItem(first, null));
        data.put("keyword1", buildTemplateDataItem(keyword1, null));
        data.put("keyword2", buildTemplateDataItem(keyword2, null));
        data.put("keyword3", buildTemplateDataItem(keyword3, null));
        data.put("keyword4", buildTemplateDataItem(keyword4, null));
        data.put("keyword5", buildTemplateDataItem(keyword5, null));
        data.put("remark", buildTemplateDataItem(remark, null));
        sendTemplateMessageModel.setData(data);

        sendTemplateMessage(obtainAuthorizerAccessToken(onlineVip.getTenantId()), sendTemplateMessageModel);
    }

    public static void sendRechargeTemplateSafe(BigInteger vipId, String branchName, Date rechargeTime, BigDecimal rechargeAmount, BigDecimal giveAmount, BigDecimal availableBalance, Integer givePoint) {
        try {
            sendRechargeTemplate(vipId, branchName, rechargeTime, rechargeAmount, giveAmount, availableBalance, givePoint);
        } catch (Exception e) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("vipId", vipId);
            parameters.put("branchName", branchName);
            parameters.put("rechargeTime", rechargeTime);
            parameters.put("rechargeAmount", rechargeAmount);
            parameters.put("giveAmount", giveAmount);
            parameters.put("availableBalance", availableBalance);
            parameters.put("givePoint", givePoint);
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "发送会员充值通知模板消息失败", WeiXinUtils.class.getName(), "sendRechargeTemplateSafe", e.getClass().getName(), e.getMessage(), parameters));
        }
    }

    /*public static void sendPointChangeTemplate(BigInteger vipId, int changeAmount, int type) {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId()) || changeAmount == 0 || (StringUtils.isNotBlank(onlineVip.getCardId()) && StringUtils.isNotBlank(onlineVip.getUserCardCode()))) {
            return;
        }

        Map<String, String> wechatInfo = obtainWechatInfo(null, onlineVip.getTenantId().toString(), null);
        String originalId = MapUtils.getString(wechatInfo, "originalId");

        String first = null;
        if (type == 1) {
            first = "您好，您的会员卡积分余额发生了变动";
        } else if (type == 2) {
            first = "您好，您的会员卡积分已被清零";
        }
        String keyword1 = StringUtils.isNotBlank(onlineVip.getVipName()) ? onlineVip.getVipName() : "无";
        String keyword2 = onlineVip.getVipCode();
        String keyword3 = "积分余额";
        if (changeAmount > 0) {
            keyword3 = keyword3 + "增加了" + changeAmount + "分";
        } else {
            keyword3 = keyword3 + "减少了" + changeAmount * -1 + "分";
        }
        String keyword4 = String.valueOf(onlineVip.getRemainingScore().intValue()) + "分";
        String remark = "如有疑问，请咨询商家客服";

        String[] templateKeys = {first, keyword1, keyword2, keyword3, keyword4, remark};

        ApiRest apiRest = O2OApi.sendTemplate(onlineVip.getOriginalId(), originalId, "JFBG", templateKeys, "");
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError() + "" + apiRest.getMessage());
    }*/

    public static void sendPointChangeTemplate(BigInteger vipId, int changeAmount, int type) throws IOException {
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (StringUtils.isBlank(onlineVip.getOriginalId()) || changeAmount == 0 || (StringUtils.isNotBlank(onlineVip.getCardId()) && StringUtils.isNotBlank(onlineVip.getUserCardCode()))) {
            return;
        }

        String first = null;
        if (type == 1) {
            first = "您好，您的会员卡积分余额发生了变动";
        } else if (type == 2) {
            first = "您好，您的会员卡积分已被清零";
        }
        String keyword1 = StringUtils.isNotBlank(onlineVip.getVipName()) ? onlineVip.getVipName() : "无";
        String keyword2 = onlineVip.getVipCode();
        String keyword3 = "积分余额";
        if (changeAmount > 0) {
            keyword3 = keyword3 + "增加了" + changeAmount + "分";
        } else {
            keyword3 = keyword3 + "减少了" + changeAmount * -1 + "分";
        }
        String keyword4 = String.valueOf(onlineVip.getRemainingScore().intValue()) + "分";
        String remark = "如有疑问，请咨询商家客服";

        SendTemplateMessageModel sendTemplateMessageModel = new SendTemplateMessageModel();
        sendTemplateMessageModel.setOpenId(onlineVip.getOriginalId());
        sendTemplateMessageModel.setTemplateId(obtainTemplateId(onlineVip.getTenantId().toString(), "JFBG"));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("first", buildTemplateDataItem(first, null));
        data.put("keyword1", buildTemplateDataItem(keyword1, null));
        data.put("keyword2", buildTemplateDataItem(keyword2, null));
        data.put("keyword3", buildTemplateDataItem(keyword3, null));
        data.put("keyword4", buildTemplateDataItem(keyword4, null));
        data.put("remark", buildTemplateDataItem(remark, null));
        sendTemplateMessageModel.setData(data);

        sendTemplateMessage(obtainAuthorizerAccessToken(onlineVip.getTenantId()), sendTemplateMessageModel);
    }

    public static void sendPointChangeTemplateSafe(BigInteger vipId, int changeAmount, int type) {
        try {
            sendPointChangeTemplate(vipId, changeAmount, type);
        } catch (Exception e) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("vipId", vipId);
            parameters.put("changeAmount", changeAmount);
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "发送积分变更模板消息失败", WeiXinUtils.class.getName(), "sendPointChangeTemplateSafe", e.getClass().getName(), e.getMessage(), parameters));
        }
    }

    public static WeiXinAuthorizerToken obtainWeiXinAuthorizerToken(String componentAppId, String authorizerAppId) {
        String weiXinAuthorizerTokenJson = CacheUtils.hget(Constants.KEY_WEI_XIN_AUTHORIZER_TOKENS, componentAppId + "_" + authorizerAppId);
        ValidateUtils.notNull(weiXinAuthorizerTokenJson, "未检索到授权信息！");
        WeiXinAuthorizerToken weiXinAuthorizerToken = GsonUtils.fromJson(weiXinAuthorizerTokenJson, WeiXinAuthorizerToken.class);
        return weiXinAuthorizerToken;
    }

    public static ComponentAccessToken obtainComponentAccessToken(String componentAppId, String componentAppSecret) {
        String componentAccessTokenJson = CacheUtils.hget(Constants.KEY_WEI_XIN_COMPONENT_ACCESS_TOKEN, componentAppId);
        boolean isRetrieveComponentAccessToken = false;

        ComponentAccessToken componentAccessToken = null;
        if (StringUtils.isNotBlank(componentAccessTokenJson)) {
            componentAccessToken = GsonUtils.fromJson(componentAccessTokenJson, ComponentAccessToken.class);
            if ((System.currentTimeMillis() - componentAccessToken.getFetchTime().getTime()) / 1000 >= componentAccessToken.getExpiresIn()) {
                isRetrieveComponentAccessToken = true;
            }
        } else {
            isRetrieveComponentAccessToken = true;
        }

        if (isRetrieveComponentAccessToken) {
            String componentVerifyTicket = CacheUtils.hget(Constants.KEY_WEI_XIN_COMPONENT_VERIFY_TICKET, componentAppId);
            ValidateUtils.notNull(componentVerifyTicket, "component_verify_ticket 不存在！");
            String url = WEI_XIN_API_URL + "/cgi-bin/component/api_component_token";
            Map<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("component_appid", componentAppId);
            requestBody.put("component_appsecret", componentAppSecret);
            requestBody.put("component_verify_ticket", componentVerifyTicket);
            String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
            Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
            ValidateUtils.isTrue(!resultMap.containsKey("errcode"), MapUtils.getString(resultMap, "errmsg"));

            componentAccessToken = new ComponentAccessToken();
            componentAccessToken.setComponentAccessToken(MapUtils.getString(resultMap, "component_access_token"));
            componentAccessToken.setExpiresIn(MapUtils.getIntValue(resultMap, "expires_in"));
            componentAccessToken.setFetchTime(new Date());
            CacheUtils.hset(Constants.KEY_WEI_XIN_COMPONENT_ACCESS_TOKEN, componentAppId, GsonUtils.toJson(componentAccessToken));
        }

        return componentAccessToken;
    }

    public static Map<String, Object> jsCode2Session(String appId, String secret, String jsCode) {
        String url = WEI_XIN_API_URL + "/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + jsCode;
        String result = OutUtils.doGet(url, null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        ValidateUtils.isTrue(!resultMap.containsKey("errcode"), MapUtils.getString(resultMap, "errmsg"));
        return resultMap;
    }

    public static Map<String, Object> jsCode2Session(String appId, String jsCode, String componentAppId, String componentAccessToken) {
        String url = WEI_XIN_API_URL + "/sns/component/jscode2session?appid=" + appId + "&js_code=" + jsCode + "&grant_type=authorization_code&component_appid=" + componentAppId + "&component_access_token=" + componentAccessToken;
        String result = OutUtils.doGet(url, null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        ValidateUtils.isTrue(!resultMap.containsKey("errcode"), MapUtils.getString(resultMap, "errmsg"));
        return resultMap;
    }

    public static Map<String, Object> delMaterial(String mediaId, String accessToken) {
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("media_id", mediaId);

        String url = WEI_XIN_API_URL + "/cgi-bin/material/del_material?access_token=" + accessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);

        ValidateUtils.isTrue(MapUtils.getIntValue(resultMap, "errcode") == 0, MapUtils.getString(resultMap, "errmsg"));
        return resultMap;
    }

    public static WeiXinAuthorizerInfo obtainWeiXinAuthorizerInfo(String tenantId) {
        Map<String, String> obtainWeiXinAuthorizerInfoParams = new HashMap<String, String>();
        obtainWeiXinAuthorizerInfoParams.put("tenantId", tenantId.toString());
        ApiRest obtainWeiXinAuthorizerInfoApiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "tenant", "obtainWeiXinAuthorizerInfo", obtainWeiXinAuthorizerInfoParams);
        ValidateUtils.isTrue(obtainWeiXinAuthorizerInfoApiRest.getIsSuccess(), obtainWeiXinAuthorizerInfoApiRest.getError());

        Map<String, Object> data = (Map<String, Object>) obtainWeiXinAuthorizerInfoApiRest.getData();
        if (MapUtils.isNotEmpty(data)) {
            String appId = MapUtils.getString(data, "authorizerAppId");
            String originalId = MapUtils.getString(data, "originalId");
            String nickName = MapUtils.getString(data, "nickName");
            WeiXinAuthorizerInfo weiXinAuthorizerInfo = new WeiXinAuthorizerInfo();
            weiXinAuthorizerInfo.setAuthorizerAppId(appId);
            weiXinAuthorizerInfo.setOriginalId(originalId);
            weiXinAuthorizerInfo.setNickName(nickName);
            return weiXinAuthorizerInfo;
        } else {
            return null;
        }
    }

    public static WeiXinAuthorizerInfo obtainWeiXinAuthorizerInfo(BigInteger tenantId) {
        return obtainWeiXinAuthorizerInfo(tenantId.toString());
    }

    public static Map<String, Object> addNews(String accessToken, List<Map<String, Object>> articles) {
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("articles", articles);

        String url = WEI_XIN_API_URL + "/cgi-bin/material/add_news?access_token=" + accessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);

        ValidateUtils.isTrue(!resultMap.containsKey("errcode"), MapUtils.getString(resultMap, "errmsg"));
        return resultMap;
    }

    public static Map<String, Object> updateNews(String accessToken, String mediaId, int index, Map<String, Object> article) {
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("media_id", mediaId);
        requestBody.put("index", index);
        requestBody.put("articles", article);

        String url = WEI_XIN_API_URL + "/cgi-bin/material/update_news?access_token=" + accessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);

        ValidateUtils.isTrue(MapUtils.getIntValue(resultMap, "errcode") == 0, MapUtils.getString(resultMap, "errmsg"));
        return resultMap;
    }

    public static String obtainAuthorizerAccessToken(BigInteger tenantId) throws IOException {
        return obtainAuthorizerAccessToken(tenantId.toString());
    }

    public static String obtainAuthorizerAccessToken(String tenantId) throws IOException {
        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String authorizerAppId = weiXinAuthorizerInfo.getAuthorizerAppId();

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);
        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, authorizerAppId);
        return weiXinAuthorizerToken.getAuthorizerAccessToken();
    }

    public static Map<String, Object> sendTemplateMessage(String accessToken, SendTemplateMessageModel sendTemplateMessageModel) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("touser", sendTemplateMessageModel.getOpenId());
        body.put("template_id", sendTemplateMessageModel.getTemplateId());
        body.put("url", sendTemplateMessageModel.getUrl());

        Map<String, Object> miniProgram = sendTemplateMessageModel.getMiniProgram();
        if (MapUtils.isNotEmpty(miniProgram)) {
            body.put("miniprogram", miniProgram);
        }
        body.put("data", sendTemplateMessageModel.getData());

        String color = sendTemplateMessageModel.getColor();
        if (StringUtils.isNotBlank(color)) {
            body.put("data", color);
        }
        String _url = WEI_XIN_API_URL + "/cgi-bin/message/template/send?access_token=" + accessToken;
        String result = OutUtils.doPost(_url, GsonUtils.toJson(body), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        int errcode = MapUtils.getIntValue(resultMap, "errcode");
        ValidateUtils.isTrue(errcode == 0, MapUtils.getString(resultMap, "errmsg"));

        return resultMap;
    }

    public static String obtainTemplateId(String tenantId, String type) {
        Map<String, Object> templateInfo = obtainWxReplyTemplate(tenantId, type);
        ValidateUtils.notNull(templateInfo, "未配置模板消息！");
        return MapUtils.getString(templateInfo, "code");
    }

    public static Map<String, Object> obtainWxReplyTemplate(String tenantId, String type) {
        Map<String, String> obtainWxReplyTemplateRequestParameters = new HashMap<String, String>();
        obtainWxReplyTemplateRequestParameters.put("tenantId", tenantId);
        obtainWxReplyTemplateRequestParameters.put("type", type);

        ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "wechat", "obtainWxReplyTemplate", obtainWxReplyTemplateRequestParameters);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

        return (Map<String, Object>) apiRest.getData();
    }

    public static Map<String, Object> sendMpNewsToAll(String accessToken, Map<String, Object> filter, Map<String, Object> mpnews, int sendIgnoreReprint) {
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("filter", filter);
        requestBody.put("mpnews", mpnews);
        requestBody.put("msgtype", "mpnews");
        requestBody.put("send_ignore_reprint", sendIgnoreReprint);

        String url = WEI_XIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=" + accessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        int errcode = MapUtils.getIntValue(resultMap, "errcode");
        ValidateUtils.isTrue(errcode == 0, MapUtils.getString(resultMap, "errmsg"));

        return resultMap;
    }

    public static Map<String, Object> buildTemplateDataItem(String value, String color) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", value);

        if (StringUtils.isNotBlank(color)) {
            map.put("color", color);
        }
        return map;
    }
}
