package erp.chain.service.online;

import com.saas.common.util.*;
import erp.chain.beans.weixin.WeiXinAuthorizerInfo;
import erp.chain.beans.weixin.WeiXinAuthorizerToken;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.o2o.*;
import erp.chain.domain.online.MiniProgramItem;
import erp.chain.domain.online.OnlineTenantConfig;
import erp.chain.domain.online.OnlineVip;
import erp.chain.domain.online.OnlineVipType;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.o2o.VipBookMapper;
import erp.chain.mapper.o2o.WxCardMapper;
import erp.chain.mapper.online.OnlineTenantConfigMapper;
import erp.chain.mapper.online.OnlineVipMapper;
import erp.chain.mapper.online.OnlineVipTypeMapper;
import erp.chain.mapper.online.VipSpecialPromotionMapper;
import erp.chain.model.online.onlineweixin.*;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.service.o2o.DietPromotionService;
import erp.chain.service.system.SmsService;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2018-04-19.
 */
@Service
public class OnlineWeiXinService {
    @Autowired
    private OnlineVipMapper onlineVipMapper;
    @Autowired
    private OnlineTenantConfigMapper onlineTenantConfigMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private OnlineVipTypeMapper onlineVipTypeMapper;
    @Autowired
    private OnlineTenantConfigService onlineTenantConfigService;
    @Autowired
    private WxCardMapper wxCardMapper;
    @Autowired
    private VipSpecialPromotionMapper vipSpecialPromotionMapper;
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private CardCouponsService cardCouponsService;

    private static final String[] COLOR_VALUES = {"#63b359", "#2c9f67", "#509fc9", "#5885cf", "#9062c0", "#d09a45", "#e4b138", "#ee903c", "#f08500", "#a9d92d", "#dd6549", "#cc463d", "#cf3e36", "#5E6671"};
    private static final String[] COLOR_NAMES = {"Color010", "Color020", "Color030", "Color040", "Color050", "Color060", "Color070", "Color080", "Color081", "Color082", "Color090", "Color100", "Color101", "Color102"};

    /**
     * 基于公众号的方式创建会员卡
     *
     * @param createMemberCardModel
     * @param backgroundPicFile
     * @param logoFile
     * @return
     * @throws IOException
     */
    /*@Transactional(rollbackFor = Exception.class)
    public ApiRest createMemberCard(CreateMemberCardModel createMemberCardModel, MultipartFile backgroundPicFile, MultipartFile logoFile) throws IOException {
        BigInteger userId = createMemberCardModel.getUserId();
        BigInteger tenantId = createMemberCardModel.getTenantId();
        BigInteger branchId = createMemberCardModel.getBranchId();
        Map<String, String> wechatInfo = WeiXinUtils.obtainWechatInfo(null, tenantId.toString(), null);
        String appId = wechatInfo.get("appId");
        String secret = wechatInfo.get("secret");
        String originalId = wechatInfo.get("originalId");

        ApiRest askBaseAccessTokenApiRest = O2OApi.askBaseAccessToken(appId, secret);
        ValidateUtils.isTrue(askBaseAccessTokenApiRest.getIsSuccess(), askBaseAccessTokenApiRest.getError());

        String accessToken = askBaseAccessTokenApiRest.getData().toString();

        String backgroundPicUrl = null;
        if (backgroundPicFile != null) {
            Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
            uploadBackgroundPicRequestParameters.put("buffer", backgroundPicFile);
            uploadBackgroundPicRequestParameters.put("_access_token", accessToken);

            String uploadBackgroundPicResult = OutUtils.doPostFile("https://api.weixin.qq.com/cgi-bin/media/uploadimg", uploadBackgroundPicRequestParameters, null);
            JSONObject uploadBackgroundPicResultJsonObject = JSONObject.fromObject(uploadBackgroundPicResult);
            ValidateUtils.isTrue(!uploadBackgroundPicResultJsonObject.has("errcode"), uploadBackgroundPicResultJsonObject.optString("errmsg"));
            backgroundPicUrl = uploadBackgroundPicResultJsonObject.getString("url");
        }

        String logoUrl = null;
        Map<String, Object> uploadLogoRequestParameters = new HashMap<String, Object>();
        uploadLogoRequestParameters.put("buffer", logoFile);
        uploadLogoRequestParameters.put("_access_token", accessToken);

        String uploadLogoResult = OutUtils.doPostFile("https://api.weixin.qq.com/cgi-bin/media/uploadimg", uploadLogoRequestParameters, null);
        JSONObject uploadLogoResultJsonObject = JSONObject.fromObject(uploadLogoResult);
        ValidateUtils.isTrue(!uploadLogoResultJsonObject.has("errcode"), uploadLogoResultJsonObject.optString("errmsg"));
        logoUrl = uploadLogoResultJsonObject.getString("url");


        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("logo_url", logoUrl);
        baseInfo.put("code_type", "CODE_TYPE_BARCODE");

        Map<String, Object> swipeCard = new HashMap<String, Object>();
        swipeCard.put("is_swipe_card", true);

        Map<String, Object> payInfo = new HashMap<String, Object>();
        payInfo.put("swipe_card", swipeCard);

        baseInfo.put("pay_info", payInfo);
        baseInfo.put("brand_name", createMemberCardModel.getBrandName());
        baseInfo.put("title", createMemberCardModel.getTitle());

        String color = createMemberCardModel.getColor();
        baseInfo.put("color", COLOR_NAMES[ArrayUtils.indexOf(COLOR_VALUES, color)]);
        baseInfo.put("notice", "使用时向服务员出示此券");
        baseInfo.put("description", "不可与其他优惠同享");

        Map<String, Object> dateInfo = new HashMap<String, Object>();
        dateInfo.put("type", "DATE_TYPE_PERMANENT");
        baseInfo.put("date_info", dateInfo);

        Map<String, Object> sku = new HashMap<String, Object>();
        sku.put("quantity", 50000000);
        baseInfo.put("sku", sku);
        baseInfo.put("get_limit", 1);
        baseInfo.put("use_custom_code", false);
        baseInfo.put("can_give_friend", false);

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String outsideServiceDomain = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT, partitionCode);
        String customUrl = WeiXinUtils.generateAuthorizeUrl(appId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/myCoupon?orignalId=" + originalId, null);

        baseInfo.put("custom_url_name", "我的优惠券");
        baseInfo.put("custom_url", customUrl);
        baseInfo.put("custom_url_sub_title", "我的优惠券");

        String promotionUrl = WeiXinUtils.generateAuthorizeUrl(appId, "snsapi_base", outsideServiceDomain + "/vip/index?tenantId=" + tenantId + "&orignalId=" + originalId, null);
        baseInfo.put("promotion_url_name", "我的订单");
        baseInfo.put("promotion_url", promotionUrl);
        baseInfo.put("promotion_url_sub_title", "我的订单");

        Map<String, Object> advancedInfo = new HashMap<String, Object>();

        Map<String, Object> memberCard = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(backgroundPicUrl)) {
            memberCard.put("background_pic_url", backgroundPicUrl);
        }

        String customCell1Url = WeiXinUtils.generateAuthorizeUrl(appId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/getCoupon?orignalId=" + originalId, null);
        Map<String, Object> customCell1 = new HashMap<String, Object>();
        customCell1.put("name", "领取优惠券");
        customCell1.put("tips", "领取优惠券");
        customCell1.put("url", customCell1Url);
        memberCard.put("custom_cell1", customCell1);
        memberCard.put("base_info", baseInfo);
        memberCard.put("advanced_info", advancedInfo);
        memberCard.put("supply_bonus", true);
        memberCard.put("supply_balance", false);
        memberCard.put("prerogative", "微信会员卡");
        memberCard.put("auto_activate", false);

        String customField1Url = WeiXinUtils.generateAuthorizeUrl(appId, "", outsideServiceDomain + "/frontTenantVip/showVipStore?orignalId=" + originalId, null);
        Map<String, Object> customField1 = new HashMap<String, Object>();
        customField1.put("name", "快速充值");
        customField1.put("url", customField1Url);
        memberCard.put("custom_field1", customField1);

        String customField2Url = WeiXinUtils.generateAuthorizeUrl(appId, "", outsideServiceDomain + "/frontTenantVip/index?orignalId=" + originalId, null);
        Map<String, Object> customField2 = new HashMap<String, Object>();
        customField2.put("name", "储值余额");
        customField2.put("url", customField2Url);
        memberCard.put("custom_field2", customField2);

        memberCard.put("wx_activate", true);
        memberCard.put("wx_activate_after_submit", true);
        memberCard.put("wx_activate_after_submit_url", outsideServiceDomain + "/vip/inputVerificationCode?userId=" + userId + "&tenantId=" + tenantId + "&branchId=" + branchId);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEMBER_CARD");
        card.put("member_card", memberCard);

        Map<String, Object> createMemberCardRequestParameters = new HashMap<String, Object>();
        createMemberCardRequestParameters.put("card", card);

        String createMemberCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/create?access_token=" + accessToken, GsonUtils.toJson(createMemberCardRequestParameters), null);
        JSONObject createMemberCardResultJsonObject = JSONObject.fromObject(createMemberCardResult);
        ValidateUtils.isTrue(createMemberCardResultJsonObject.getInt("errcode") == 0, createMemberCardResultJsonObject.optString("errmsg"));

        String cardId = createMemberCardResultJsonObject.getString("card_id");

        Map<String, Object> activateUserFormRequestParameters = new HashMap<String, Object>();
        activateUserFormRequestParameters.put("card_id", cardId);

        Map<String, Object> requiredForm = new HashMap<String, Object>();
        requiredForm.put("can_modify", false);

        List<Map<String, Object>> richFieldList = new ArrayList<Map<String, Object>>();

        requiredForm.put("rich_field_list", richFieldList);
        requiredForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_NAME", "USER_FORM_INFO_FLAG_SEX", "USER_FORM_INFO_FLAG_MOBILE"});
        activateUserFormRequestParameters.put("required_form", requiredForm);

        Map<String, Object> optionalForm = new HashMap<String, Object>();
        optionalForm.put("can_modify", false);
        optionalForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_BIRTHDAY"});
        activateUserFormRequestParameters.put("optional_form", optionalForm);

        String activateUserFormResult = OutUtils.doPost("https://api.weixin.qq.com/card/membercard/activateuserform/set?access_token=" + accessToken, GsonUtils.toJson(activateUserFormRequestParameters), null);
        JSONObject activateUserFormResultJsonObject = JSONObject.fromObject(activateUserFormResult);
        ValidateUtils.isTrue(activateUserFormResultJsonObject.getInt("errcode") == 0, activateUserFormResultJsonObject.optString("errmsg"));

        Map<String, Object> cardInfo = new HashMap<String, Object>();
        cardInfo.put("card_id", cardId);

        Map<String, Object> actionInfo = new HashMap<String, Object>();
        actionInfo.put("card", cardInfo);

        Map<String, Object> createQrCodeRequestParameters = new HashMap<String, Object>();
        createQrCodeRequestParameters.put("action_name", "QR_CARD");
        createQrCodeRequestParameters.put("action_info", actionInfo);
        String createQrCodeResult = OutUtils.doPost("https://api.weixin.qq.com/card/qrcode/create?access_token=" + accessToken, GsonUtils.toJson(createQrCodeRequestParameters), null);
        JSONObject createQrCodeResultJsonObject = JSONObject.fromObject(createQrCodeResult);
        ValidateUtils.isTrue(createQrCodeResultJsonObject.getInt("errcode") == 0, createQrCodeResultJsonObject.optString("errmsg"));

        String url = createQrCodeResultJsonObject.getString("url");

        WxCard wxCard = new WxCard();
        wxCard.setTenantId(tenantId);
        wxCard.setBranchId(branchId);
        wxCard.setCardId(cardId);
        wxCard.setQrCode(url);
        wxCard.setCheckStatus(2);
        wxCard.setPayGiftCard(false);
        wxCard.setDeleted(false);
        wxCard.setBackgroundPicUrl(backgroundPicUrl);
        wxCard.setLogoUrl(logoUrl);
        wxCard.setBrandName(createMemberCardModel.getBrandName());
        wxCard.setColor(color);
        wxCard.setTitle(createMemberCardModel.getTitle());
        wxCard.setSubscribePush(false);

        wxCardMapper.insert(wxCard);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("创建会员卡成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }*/

    /**
     * 基于开放平台的方式创建会员卡
     *
     * @param createMemberCardModel
     * @param backgroundPicFile
     * @param logoFile
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest createMemberCard(CreateMemberCardModel createMemberCardModel, MultipartFile backgroundPicFile, MultipartFile logoFile) throws IOException {
        BigInteger userId = createMemberCardModel.getUserId();
        BigInteger tenantId = createMemberCardModel.getTenantId();
        BigInteger branchId = createMemberCardModel.getBranchId();

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String authorizerAppId = weiXinAuthorizerInfo.getAuthorizerAppId();
        String originalId = weiXinAuthorizerInfo.getOriginalId();

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);
        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, authorizerAppId);
        String authorizerAccessToken = weiXinAuthorizerToken.getAuthorizerAccessToken();

        String backgroundPicUrl = null;
        if (backgroundPicFile != null) {
            Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
            uploadBackgroundPicRequestParameters.put("buffer", backgroundPicFile);
            uploadBackgroundPicRequestParameters.put("_access_token", authorizerAccessToken);

            String uploadBackgroundPicResult = OutUtils.doPostFile("https://api.weixin.qq.com/cgi-bin/media/uploadimg", uploadBackgroundPicRequestParameters, null);
            JSONObject uploadBackgroundPicResultJsonObject = JSONObject.fromObject(uploadBackgroundPicResult);
            ValidateUtils.isTrue(!uploadBackgroundPicResultJsonObject.has("errcode"), uploadBackgroundPicResultJsonObject.optString("errmsg"));
            backgroundPicUrl = uploadBackgroundPicResultJsonObject.getString("url");
        }

        String logoUrl = null;
        Map<String, Object> uploadLogoRequestParameters = new HashMap<String, Object>();
        uploadLogoRequestParameters.put("buffer", logoFile);
        uploadLogoRequestParameters.put("_access_token", authorizerAccessToken);

        String uploadLogoResult = OutUtils.doPostFile("https://api.weixin.qq.com/cgi-bin/media/uploadimg", uploadLogoRequestParameters, null);
        JSONObject uploadLogoResultJsonObject = JSONObject.fromObject(uploadLogoResult);
        ValidateUtils.isTrue(!uploadLogoResultJsonObject.has("errcode"), uploadLogoResultJsonObject.optString("errmsg"));
        logoUrl = uploadLogoResultJsonObject.getString("url");


        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("logo_url", logoUrl);
        baseInfo.put("code_type", "CODE_TYPE_BARCODE");

        Map<String, Object> swipeCard = new HashMap<String, Object>();
        swipeCard.put("is_swipe_card", true);

        Map<String, Object> payInfo = new HashMap<String, Object>();
        payInfo.put("swipe_card", swipeCard);

        baseInfo.put("pay_info", payInfo);
        baseInfo.put("brand_name", createMemberCardModel.getBrandName());
        baseInfo.put("title", createMemberCardModel.getTitle());

        String color = createMemberCardModel.getColor();
        baseInfo.put("color", COLOR_NAMES[ArrayUtils.indexOf(COLOR_VALUES, color)]);
        baseInfo.put("notice", "使用时向服务员出示此券");
        baseInfo.put("description", "不可与其他优惠同享");

        Map<String, Object> dateInfo = new HashMap<String, Object>();
        dateInfo.put("type", "DATE_TYPE_PERMANENT");
        baseInfo.put("date_info", dateInfo);

        Map<String, Object> sku = new HashMap<String, Object>();
        sku.put("quantity", 50000000);
        baseInfo.put("sku", sku);
        baseInfo.put("get_limit", 1);
        baseInfo.put("use_custom_code", false);
        baseInfo.put("can_give_friend", false);

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String outsideServiceDomain = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT, partitionCode);

        String customUrl = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/myCoupon?orignalId=" + originalId, null, componentAppId);

        baseInfo.put("custom_url_name", "我的优惠券");
        baseInfo.put("custom_url", customUrl);
        baseInfo.put("custom_url_sub_title", "我的优惠券");

        String promotionUrl = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/vip/index?tenantId=" + tenantId + "&orignalId=" + originalId, null, componentAppId);
        baseInfo.put("promotion_url_name", "我的订单");
        baseInfo.put("promotion_url", promotionUrl);
        baseInfo.put("promotion_url_sub_title", "我的订单");

        Map<String, Object> advancedInfo = new HashMap<String, Object>();

        Map<String, Object> memberCard = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(backgroundPicUrl)) {
            memberCard.put("background_pic_url", backgroundPicUrl);
        }

        String customCell1Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/getCoupon?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customCell1 = new HashMap<String, Object>();
        customCell1.put("name", "领取优惠券");
        customCell1.put("tips", "领取优惠券");
        customCell1.put("url", customCell1Url);
        memberCard.put("custom_cell1", customCell1);
        memberCard.put("base_info", baseInfo);
        memberCard.put("advanced_info", advancedInfo);
        memberCard.put("supply_bonus", true);
        memberCard.put("supply_balance", false);
        memberCard.put("prerogative", "微信会员卡");
        memberCard.put("auto_activate", false);

        String customField1Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/showVipStore?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customField1 = new HashMap<String, Object>();
        customField1.put("name", "快速充值");
        customField1.put("url", customField1Url);
        memberCard.put("custom_field1", customField1);

        String customField2Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/index?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customField2 = new HashMap<String, Object>();
        customField2.put("name", "储值余额");
        customField2.put("url", customField2Url);
        memberCard.put("custom_field2", customField2);

        memberCard.put("wx_activate", true);
        memberCard.put("wx_activate_after_submit", true);
        memberCard.put("wx_activate_after_submit_url", outsideServiceDomain + "/vip/inputVerificationCode?userId=" + userId + "&tenantId=" + tenantId + "&branchId=" + branchId);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEMBER_CARD");
        card.put("member_card", memberCard);

        Map<String, Object> createMemberCardRequestParameters = new HashMap<String, Object>();
        createMemberCardRequestParameters.put("card", card);

        String createMemberCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/create?access_token=" + authorizerAccessToken, GsonUtils.toJson(createMemberCardRequestParameters), null);
        JSONObject createMemberCardResultJsonObject = JSONObject.fromObject(createMemberCardResult);
        ValidateUtils.isTrue(createMemberCardResultJsonObject.getInt("errcode") == 0, createMemberCardResultJsonObject.optString("errmsg"));

        String cardId = createMemberCardResultJsonObject.getString("card_id");

        Map<String, Object> activateUserFormRequestParameters = new HashMap<String, Object>();
        activateUserFormRequestParameters.put("card_id", cardId);

        Map<String, Object> requiredForm = new HashMap<String, Object>();
        requiredForm.put("can_modify", false);

        List<Map<String, Object>> richFieldList = new ArrayList<Map<String, Object>>();

        requiredForm.put("rich_field_list", richFieldList);
        requiredForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_NAME", "USER_FORM_INFO_FLAG_SEX", "USER_FORM_INFO_FLAG_MOBILE"});
        activateUserFormRequestParameters.put("required_form", requiredForm);

        Map<String, Object> optionalForm = new HashMap<String, Object>();
        optionalForm.put("can_modify", false);
        optionalForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_BIRTHDAY"});
        activateUserFormRequestParameters.put("optional_form", optionalForm);

        String activateUserFormResult = OutUtils.doPost("https://api.weixin.qq.com/card/membercard/activateuserform/set?access_token=" + authorizerAccessToken, GsonUtils.toJson(activateUserFormRequestParameters), null);
        JSONObject activateUserFormResultJsonObject = JSONObject.fromObject(activateUserFormResult);
        ValidateUtils.isTrue(activateUserFormResultJsonObject.getInt("errcode") == 0, activateUserFormResultJsonObject.optString("errmsg"));

        Map<String, Object> cardInfo = new HashMap<String, Object>();
        cardInfo.put("card_id", cardId);

        Map<String, Object> actionInfo = new HashMap<String, Object>();
        actionInfo.put("card", cardInfo);

        Map<String, Object> createQrCodeRequestParameters = new HashMap<String, Object>();
        createQrCodeRequestParameters.put("action_name", "QR_CARD");
        createQrCodeRequestParameters.put("action_info", actionInfo);
        String createQrCodeResult = OutUtils.doPost("https://api.weixin.qq.com/card/qrcode/create?access_token=" + authorizerAccessToken, GsonUtils.toJson(createQrCodeRequestParameters), null);
        JSONObject createQrCodeResultJsonObject = JSONObject.fromObject(createQrCodeResult);
        ValidateUtils.isTrue(createQrCodeResultJsonObject.getInt("errcode") == 0, createQrCodeResultJsonObject.optString("errmsg"));

        String url = createQrCodeResultJsonObject.getString("url");

        WxCard wxCard = new WxCard();
        wxCard.setTenantId(tenantId);
        wxCard.setBranchId(branchId);
        wxCard.setCardId(cardId);
        wxCard.setQrCode(url);
        wxCard.setCheckStatus(2);
        wxCard.setPayGiftCard(false);
        wxCard.setDeleted(false);
        wxCard.setBackgroundPicUrl(backgroundPicUrl);
        wxCard.setLogoUrl(logoUrl);
        wxCard.setBrandName(createMemberCardModel.getBrandName());
        wxCard.setColor(color);
        wxCard.setTitle(createMemberCardModel.getTitle());
        wxCard.setSubscribePush(false);

        wxCardMapper.insert(wxCard);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("创建会员卡成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 基于公众号的删除会员卡
     *
     * @param deleteCardModel
     * @return
     */
    /*public ApiRest deleteCard(DeleteCardModel deleteCardModel) {
        BigInteger userId = deleteCardModel.getUserId();
        BigInteger tenantId = deleteCardModel.getTenantId();
        BigInteger branchId = deleteCardModel.getBranchId();
        String cardId = deleteCardModel.getCardId();

        Map<String, Object> deleteCardRequestParameters = new HashMap<String, Object>();
        deleteCardRequestParameters.put("card_id", cardId);

        Map<String, String> wechatInfo = WeiXinUtils.obtainWechatInfo(null, tenantId.toString(), null);
        String appId = wechatInfo.get("appId");
        String secret = wechatInfo.get("secret");

        ApiRest apiRest = O2OApi.askBaseAccessToken(appId, secret);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

        String accessToken = apiRest.getData().toString();

        Map<String, String> doPostRequestParameters = new HashMap<String, String>();
        doPostRequestParameters.put("url", "https://api.weixin.qq.com/card/delete?access_token=" + accessToken);
        doPostRequestParameters.put("requestBody", GsonUtils.toJson(deleteCardRequestParameters));

        ApiRest doPostApiRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "proxy", "doPost", doPostRequestParameters);
        ValidateUtils.isTrue(doPostApiRest.getIsSuccess(), doPostApiRest.getError());
        return doPostApiRest;
    }*/

    /**
     * 基于开放平台的删除会员卡
     *
     * @param deleteCardModel
     * @return
     */
    public ApiRest deleteCard(DeleteCardModel deleteCardModel) throws IOException {
        BigInteger userId = deleteCardModel.getUserId();
        BigInteger tenantId = deleteCardModel.getTenantId();
        BigInteger branchId = deleteCardModel.getBranchId();
        String cardId = deleteCardModel.getCardId();

        Map<String, Object> deleteCardRequestParameters = new HashMap<String, Object>();
        deleteCardRequestParameters.put("card_id", cardId);

        String authorizerAccessToken = WeiXinUtils.obtainAuthorizerAccessToken(tenantId);

        String url = "https://api.weixin.qq.com/card/delete?access_token=" + authorizerAccessToken;
        String result = OutUtils.doPost(url, GsonUtils.toJson(deleteCardRequestParameters), null);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(JacksonUtils.readValueAsMap(result, String.class, Object.class));
        apiRest.setMessage("删除会员卡成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 基于公众号的激活会员卡
     *
     * @param activateMemberCardModel
     * @return
     * @throws ParseException
     */
    /*@Transactional(rollbackFor = Exception.class)
    public ApiRest activateMemberCard(ActivateMemberCardModel activateMemberCardModel) throws ParseException {
        BigInteger tenantId = activateMemberCardModel.getTenantId();
        BigInteger branchId = activateMemberCardModel.getBranchId();
        BigInteger userId = activateMemberCardModel.getUserId();
        String cardId = activateMemberCardModel.getCardId();
        String openid = activateMemberCardModel.getOpenid();
        String code = activateMemberCardModel.getCode();
        String name = activateMemberCardModel.getName();
        String phone = activateMemberCardModel.getPhone();
        String sex = activateMemberCardModel.getSex();
        String birthday = activateMemberCardModel.getBirthday();

        Map<String, String> wechatInfo = WeiXinUtils.obtainWechatInfo(null, tenantId.toString(), null);
        String appId = wechatInfo.get("appId");
        String secret = wechatInfo.get("secret");

        ApiRest askBaseAccessTokenApiRest = O2OApi.askBaseAccessToken(appId, secret);
        ValidateUtils.isTrue(askBaseAccessTokenApiRest.getIsSuccess(), askBaseAccessTokenApiRest.getError());

        String accessToken = askBaseAccessTokenApiRest.getData().toString();

        SearchModel wxCardSearchModel = new SearchModel(true);
        wxCardSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        wxCardSearchModel.addSearchCondition("card_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardId);
        WxCard wxCard = wxCardMapper.find(wxCardSearchModel);
        ValidateUtils.notNull(wxCard, "微信会员卡不存在！");

        Date date = new Date();
        OnlineVip onlineVip = null;
        List<OnlineVip> onlineVips = onlineVipMapper.findAllVipInfos(tenantId, openid, phone);
        if (CollectionUtils.isNotEmpty(onlineVips)) {
            int size = onlineVips.size();
            if (size == 0) {

            } else if (size == 1) {
                onlineVip = onlineVips.get(0);
            } else {
                ValidateUtils.isTrue(false, "绑定会员出错，请联系管理员！");
            }
        }

        if (onlineVip == null) {
            OnlineTenantConfig onlineTenantConfig = onlineTenantConfigService.checkTenantConfig(tenantId, SysConfig.SYS_VIP_NUM, "会员");
            Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
            ValidateUtils.notNull(headquartersBranch, "没有查询到该商户下的总店！");

            BigInteger headquartersBranchId = headquartersBranch.getId();

            SearchModel vipTypeSearchModel = new SearchModel(true);
            vipTypeSearchModel.addSearchCondition("is_online_default", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            vipTypeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            OnlineVipType onlineVipType = onlineVipTypeMapper.find(vipTypeSearchModel);
            ValidateUtils.notNull(onlineVipType, "未检索到默认会员类型！");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

            int value = Integer.parseInt(onlineTenantConfig.getValue());
            String nextValue = String.valueOf(value + 1);
            while (nextValue.length() < 5) {
                nextValue = "0" + nextValue;
            }
            String vipCode = simpleDateFormat.format(date) + nextValue;
            onlineVip = new OnlineVip();
            onlineVip.setTenantId(tenantId);
            onlineVip.setBranchId(headquartersBranchId);
            onlineVip.setOriginalId(openid);
            onlineVip.setTypeId(onlineVipType.getId());
            onlineVip.setVipCode(vipCode);
            onlineVip.setVipName(name);
            onlineVip.setSex("男".equals(sex) ? 1 : 2);
            if (StringUtils.isNotBlank(birthday)) {
                onlineVip.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            }
            onlineVip.setPhone(phone);
            onlineVip.setStatus(0);
            onlineVip.setRegSource("2");
            onlineVip.setUserCardCode(code);
            onlineVip.setCardId(cardId);
            onlineVip.setCreateAt(date);
            onlineVip.setLastUpdateAt(date);
            onlineVip.setDeleted(false);
            onlineVipMapper.insert(onlineVip);
            SearchModel vipSpecialPromotionSearchModel = new SearchModel(true);
            vipSpecialPromotionSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            vipSpecialPromotionSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, headquartersBranchId);
            vipSpecialPromotionSearchModel.addSearchCondition("promotion_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            VipSpecialPromotion vipSpecialPromotion = vipSpecialPromotionMapper.find(vipSpecialPromotionSearchModel);
            LogUtil.logInfo("活动详情" + BeanUtils.toJsonStr(vipSpecialPromotion));
            if (vipSpecialPromotion != null) {
                BigDecimal addScore = vipSpecialPromotion.getAddScore();
                if (BigInteger.ONE.compareTo(onlineVipType.getToSavePoints()) == 0 && addScore != null && addScore.compareTo(BigDecimal.ZERO) != 0) {
                    onlineVip.setRemainingScore(addScore);
                    onlineVip.setSumScore(addScore);

                    VipBook vipBook = new VipBook();

                    //记录积分台帐
                    vipBook.setVipId(onlineVip.getId());
                    vipBook.setTenantId(tenantId);
                    vipBook.setBranchId(branchId);
                    vipBook.setBookType(3);
                    vipBook.setOperateBy("新注册会员活动");
                    vipBook.setOperateAt(date);
                    vipBook.setVipCode(vipCode);
                    vipBook.setTotalScore(BigDecimal.ZERO);
                    vipBook.setVipScore(addScore);
                    vipBook.setCreateAt(date);
                    vipBook.setLastUpdateAt(date);
                    vipBook.setStoreFrom("2");
                    vipBook.setPaymentCode("ZCHY");
                    vipBookMapper.insert(vipBook);

                    if (onlineVip.getPhone() != null && !onlineVip.getPhone().equals("")) {
                        DecimalFormat sd = new DecimalFormat("#");
                        Map smsMap = new HashMap();
                        smsMap.put("type", 3);
                        smsMap.put("tenantId", onlineVip.getTenantId().toString());
                        smsMap.put("number", onlineVip.getPhone());
                        smsMap.put("branchId", onlineVip.getBranchId().toString());
                        smsMap.put("content", ("注册赠送###" + sd.format(vipSpecialPromotion.getAddScore()) + "###" + sd.format(onlineVip.getRemainingScore())));
                        ApiRest ssr = smsService.sendSMS(smsMap);
                        if (!ssr.getIsSuccess()) {
                            LogUtil.logInfo("发送消费短信失败！vipId=" + onlineVip.getId());
                        }
                    }
                    onlineVipMapper.update(onlineVip);
                    WeiXinUtils.sendPointChangeTemplateSafe(onlineVip.getId(), vipSpecialPromotion.getAddScore().intValue(), 1);
                }
                //赠送优惠券
                if (StringUtils.isNotEmpty(vipSpecialPromotion.getPrizeDietPromotionIds())) {
                    Map<String, String> map3 = new HashMap<>();
                    map3.put("tenantId", onlineVip.getTenantId().toString());
                    map3.put("cardId", vipSpecialPromotion.getPrizeDietPromotionIds());
                    map3.put("vipId", onlineVip.getId().toString());
                    map3.put("channel", "register_wechat");
                    map3.put("branchId", onlineVip.getBranchId().toString());
                    cardCouponsService.sendCardToVip(map3);
                }
            }
            onlineTenantConfig.setValue(String.valueOf(value + 1));
            onlineTenantConfig.setLastUpdateAt(date);
            onlineTenantConfigMapper.update(onlineTenantConfig);
        } else {
            onlineVip.setOriginalId(openid);
            onlineVip.setPhone(phone);
            onlineVip.setVipName(name);
            onlineVip.setSex("男".equals(sex) ? 1 : 2);
            if (StringUtils.isNotBlank(birthday)) {
                onlineVip.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            }
            onlineVip.setUserCardCode(code);
            onlineVip.setCardId(cardId);
            onlineVipMapper.update(onlineVip);
        }

        Map<String, Object> activateMemberCardRequestParameters = new HashMap<String, Object>();
        activateMemberCardRequestParameters.put("membership_number", onlineVip.getVipCode());
        activateMemberCardRequestParameters.put("code", code);
        activateMemberCardRequestParameters.put("card_id", cardId);

        BigDecimal remainingScore = onlineVip.getRemainingScore();
        if (remainingScore == null) {
            remainingScore = BigDecimal.ZERO;
        }

        activateMemberCardRequestParameters.put("init_bonus", remainingScore.intValue());
        activateMemberCardRequestParameters.put("init_custom_field_value1", "充值");
        activateMemberCardRequestParameters.put("init_custom_field_value2", "查询");

        String activateMemberCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/membercard/activate?access_token=" + accessToken, GsonUtils.toJson(activateMemberCardRequestParameters), null);
        JSONObject activateMemberCardResultJsonObject = JSONObject.fromObject(activateMemberCardResult);
        ValidateUtils.isTrue(activateMemberCardResultJsonObject.getInt("errcode") == 0, activateMemberCardResultJsonObject.optString("errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineVip);
        apiRest.setMessage("激活成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }*/

    /**
     * 基于开放平台的激活会员卡
     *
     * @param activateMemberCardModel
     * @return
     * @throws ParseException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest activateMemberCard(ActivateMemberCardModel activateMemberCardModel) throws ParseException, IOException {
        BigInteger tenantId = activateMemberCardModel.getTenantId();
        BigInteger branchId = activateMemberCardModel.getBranchId();
        BigInteger userId = activateMemberCardModel.getUserId();
        String cardId = activateMemberCardModel.getCardId();
        String openid = activateMemberCardModel.getOpenid();
        String code = activateMemberCardModel.getCode();
        String name = activateMemberCardModel.getName();
        String phone = activateMemberCardModel.getPhone();
        String sex = activateMemberCardModel.getSex();
        String birthday = activateMemberCardModel.getBirthday();

        SearchModel wxCardSearchModel = new SearchModel(true);
        wxCardSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        wxCardSearchModel.addSearchCondition("card_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardId);
        WxCard wxCard = wxCardMapper.find(wxCardSearchModel);
        ValidateUtils.notNull(wxCard, "微信会员卡不存在！");

        Date date = new Date();
        OnlineVip onlineVip = null;
        List<OnlineVip> onlineVips = onlineVipMapper.findAllVipInfos(tenantId, openid, phone);
        if (CollectionUtils.isNotEmpty(onlineVips)) {
            int size = onlineVips.size();
            if (size == 0) {

            } else if (size == 1) {
                onlineVip = onlineVips.get(0);
            } else {
                ValidateUtils.isTrue(false, "绑定会员出错，请联系管理员！");
            }
        }

        boolean isNew = false;

        OnlineVipType onlineVipType = null;
        BigInteger headquartersBranchId = null;
        if (onlineVip == null) {
            OnlineTenantConfig onlineTenantConfig = onlineTenantConfigService.checkTenantConfig(tenantId, SysConfig.SYS_VIP_NUM, "会员");
            Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
            ValidateUtils.notNull(headquartersBranch, "没有查询到该商户下的总店！");

            headquartersBranchId = headquartersBranch.getId();

            SearchModel vipTypeSearchModel = new SearchModel(true);
            vipTypeSearchModel.addSearchCondition("is_online_default", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            vipTypeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            onlineVipType = onlineVipTypeMapper.find(vipTypeSearchModel);
            ValidateUtils.notNull(onlineVipType, "未检索到默认会员类型！");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

            int value = Integer.parseInt(onlineTenantConfig.getValue());
            String nextValue = String.valueOf(value + 1);
            while (nextValue.length() < 5) {
                nextValue = "0" + nextValue;
            }
            String vipCode = simpleDateFormat.format(date) + nextValue;
            onlineVip = new OnlineVip();
            onlineVip.setTenantId(tenantId);
            onlineVip.setBranchId(headquartersBranchId);
            onlineVip.setOriginalId(openid);
            onlineVip.setTypeId(onlineVipType.getId());
            onlineVip.setVipCode(vipCode);
            onlineVip.setVipName(name);
            onlineVip.setSex("男".equals(sex) ? 1 : 2);
            if (StringUtils.isNotBlank(birthday)) {
                onlineVip.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            }
            onlineVip.setPhone(phone);
            onlineVip.setStatus(0);
            onlineVip.setRegSource("2");
            onlineVip.setUserCardCode(code);
            onlineVip.setCardId(cardId);
            onlineVip.setCreateAt(date);
            onlineVip.setLastUpdateAt(date);
            onlineVip.setDeleted(false);
            onlineVipMapper.insert(onlineVip);

            onlineTenantConfig.setValue(String.valueOf(value + 1));
            onlineTenantConfig.setLastUpdateAt(date);
            onlineTenantConfigMapper.update(onlineTenantConfig);

            isNew = true;
        } else {
            if (StringUtils.isBlank(onlineVip.getPhone())) {
                isNew = true;
            }
            onlineVip.setOriginalId(openid);
            onlineVip.setPhone(phone);
            onlineVip.setVipName(name);
            onlineVip.setSex("男".equals(sex) ? 1 : 2);
            if (StringUtils.isNotBlank(birthday)) {
                onlineVip.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            }
            onlineVip.setUserCardCode(code);
            onlineVip.setCardId(cardId);
            onlineVipMapper.update(onlineVip);
        }

        // 开始处理赠送积分和优惠券
        if (isNew) {
            if (onlineVipType == null) {
                SearchModel vipTypeSearchModel = new SearchModel(true);
                vipTypeSearchModel.addSearchCondition("is_online_default", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
                vipTypeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                onlineVipType = onlineVipTypeMapper.find(vipTypeSearchModel);
                ValidateUtils.notNull(onlineVipType, "未检索到默认会员类型！");
            }

            if (headquartersBranchId == null) {
                Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
                ValidateUtils.notNull(headquartersBranch, "没有查询到该商户下的总店！");

                headquartersBranchId = headquartersBranch.getId();
            }

            SearchModel vipSpecialPromotionSearchModel = new SearchModel(true);
            vipSpecialPromotionSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            vipSpecialPromotionSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, headquartersBranchId);
            vipSpecialPromotionSearchModel.addSearchCondition("promotion_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            VipSpecialPromotion vipSpecialPromotion = vipSpecialPromotionMapper.find(vipSpecialPromotionSearchModel);
            LogUtil.logInfo("活动详情" + BeanUtils.toJsonStr(vipSpecialPromotion));
            if (vipSpecialPromotion != null) {
                BigDecimal addScore = vipSpecialPromotion.getAddScore();
                if (BigInteger.ONE.compareTo(onlineVipType.getToSavePoints()) == 0 && addScore != null && addScore.compareTo(BigDecimal.ZERO) != 0) {
                    onlineVip.setRemainingScore(addScore);
                    onlineVip.setSumScore(addScore);

                    VipBook vipBook = new VipBook();

                    //记录积分台帐
                    vipBook.setVipId(onlineVip.getId());
                    vipBook.setTenantId(tenantId);
                    vipBook.setBranchId(branchId);
                    vipBook.setBookType(3);
                    vipBook.setOperateBy("新注册会员活动");
                    vipBook.setOperateAt(date);
                    vipBook.setVipCode(onlineVip.getVipCode());
                    vipBook.setTotalScore(BigDecimal.ZERO);
                    vipBook.setVipScore(addScore);
                    vipBook.setCreateAt(date);
                    vipBook.setLastUpdateAt(date);
                    vipBook.setStoreFrom("2");
                    vipBook.setPaymentCode("ZCHY");
                    vipBookMapper.insert(vipBook);

                    if (onlineVip.getPhone() != null && !onlineVip.getPhone().equals("")) {
                        DecimalFormat sd = new DecimalFormat("#");
                        Map smsMap = new HashMap();
                        smsMap.put("type", 3);
                        smsMap.put("tenantId", onlineVip.getTenantId().toString());
                        smsMap.put("number", onlineVip.getPhone());
                        smsMap.put("branchId", onlineVip.getBranchId().toString());
                        smsMap.put("content", ("注册赠送###" + sd.format(vipSpecialPromotion.getAddScore()) + "###" + sd.format(onlineVip.getRemainingScore())));
                        ApiRest ssr = smsService.sendSMS(smsMap);
                        if (!ssr.getIsSuccess()) {
                            LogUtil.logInfo("发送消费短信失败！vipId=" + onlineVip.getId());
                        }
                    }
                    onlineVipMapper.update(onlineVip);
                    WeiXinUtils.sendPointChangeTemplateSafe(onlineVip.getId(), vipSpecialPromotion.getAddScore().intValue(), 1);
                }
                //赠送优惠券
                if (StringUtils.isNotEmpty(vipSpecialPromotion.getPrizeDietPromotionIds())) {
                    Map<String, String> map3 = new HashMap<>();
                    map3.put("tenantId", onlineVip.getTenantId().toString());
                    map3.put("cardId", vipSpecialPromotion.getPrizeDietPromotionIds());
                    map3.put("vipId", onlineVip.getId().toString());
                    map3.put("channel", "register_wechat");
                    map3.put("branchId", onlineVip.getBranchId().toString());
                    cardCouponsService.sendCardToVip(map3);
                }
            }
        }

        Map<String, Object> activateMemberCardRequestParameters = new HashMap<String, Object>();
        activateMemberCardRequestParameters.put("membership_number", onlineVip.getVipCode());
        activateMemberCardRequestParameters.put("code", code);
        activateMemberCardRequestParameters.put("card_id", cardId);

        BigDecimal remainingScore = onlineVip.getRemainingScore();
        if (remainingScore == null) {
            remainingScore = BigDecimal.ZERO;
        }

        activateMemberCardRequestParameters.put("init_bonus", remainingScore.intValue());
        activateMemberCardRequestParameters.put("init_custom_field_value1", "充值");
        activateMemberCardRequestParameters.put("init_custom_field_value2", "查询");

        String authorizerAccessToken = WeiXinUtils.obtainAuthorizerAccessToken(tenantId);
        String activateMemberCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/membercard/activate?access_token=" + authorizerAccessToken, GsonUtils.toJson(activateMemberCardRequestParameters), null);
        JSONObject activateMemberCardResultJsonObject = JSONObject.fromObject(activateMemberCardResult);
        ValidateUtils.isTrue(activateMemberCardResultJsonObject.getInt("errcode") == 0, activateMemberCardResultJsonObject.optString("errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineVip);
        apiRest.setMessage("激活成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 基于公众号的开通支付即会员
     *
     * @param openPayGiftCardModel
     * @return
     */
    /*@Transactional(rollbackFor = Exception.class)
    public ApiRest openPayGiftCard(OpenPayGiftCardModel openPayGiftCardModel) {
        BigInteger tenantId = openPayGiftCardModel.getTenantId();
        BigInteger branchId = openPayGiftCardModel.getBranchId();
        BigInteger userId = openPayGiftCardModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        WxCard wxCard = wxCardMapper.find(searchModel);
        ValidateUtils.notNull(wxCard, "请先创建会员卡！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        ValidateUtils.notNull(headquartersBranch, "未检索到总部机构！");
        String headquartersBranchId = headquartersBranch.getId().toString();

        Map<String, String> findPayAccountRequestParameters = new HashMap<String, String>();
        findPayAccountRequestParameters.put("tenantId", tenantId.toString());
        findPayAccountRequestParameters.put("branchId", headquartersBranchId);
        findPayAccountRequestParameters.put("headquartersBranchId", headquartersBranchId);

        ApiRest findPayAccountRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "pay", "findPayAccount", findPayAccountRequestParameters);
        ValidateUtils.isTrue(findPayAccountRest.getIsSuccess(), findPayAccountRest.getError());
        Map<String, Object> payAccountMap = (Map<String, Object>) findPayAccountRest.getData();
        String mchId = MapUtils.getString(payAccountMap, "wechatPaySubMchid");
        ValidateUtils.notNull(mchId, "未配置微信支付账号，不能开通支付即会员！");

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        List<String> mchidList = new ArrayList<String>();
        mchidList.add(mchId);
        baseInfo.put("mchid_list", mchidList);

        baseInfo.put("begin_time", System.currentTimeMillis() / 1000);
        baseInfo.put("end_time", DateUtils.addYears(new Date(), 100).getTime() / 1000);

        Map<String, Object> memberRule = new HashMap<String, Object>();
        memberRule.put("card_id", wxCard.getCardId());
        memberRule.put("least_cost", 0);
        memberRule.put("max_cost", Long.MAX_VALUE);

        Map<String, Object> ruleInfo = new HashMap<String, Object>();
        ruleInfo.put("type", "RULE_TYPE_PAY_MEMBER_CARD");
        ruleInfo.put("base_info", baseInfo);
        ruleInfo.put("member_rule", memberRule);

        Map<String, Object> payGiftCardRequestBody = new HashMap<String, Object>();
        payGiftCardRequestBody.put("rule_info", ruleInfo);

        Map<String, String> wechatInfo = WeiXinUtils.obtainWechatInfo(null, tenantId.toString(), null);
        String appId = wechatInfo.get("appId");
        String secret = wechatInfo.get("secret");

        ApiRest askBaseAccessTokenApiRest = O2OApi.askBaseAccessToken(appId, secret);
        ValidateUtils.isTrue(askBaseAccessTokenApiRest.getIsSuccess(), askBaseAccessTokenApiRest.getError());

        String accessToken = askBaseAccessTokenApiRest.getData().toString();

        String payGiftCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/paygiftcard/add?access_token=" + accessToken, GsonUtils.toJson(payGiftCardRequestBody), null);
        JSONObject payGiftCardResultJsonObject = JSONObject.fromObject(payGiftCardResult);
        ValidateUtils.isTrue(payGiftCardResultJsonObject.getInt("errcode") == 0, payGiftCardResultJsonObject.optString("errmsg"));

        wxCard.setRuleId(payGiftCardResultJsonObject.getString("rule_id"));
        wxCard.setPayGiftCard(true);
        wxCardMapper.update(wxCard);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("开通支付即会员成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }*/

    /**
     * 基于开放平台的开通支付即会员
     *
     * @param openPayGiftCardModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest openPayGiftCard(OpenPayGiftCardModel openPayGiftCardModel) throws IOException {
        BigInteger tenantId = openPayGiftCardModel.getTenantId();
        BigInteger branchId = openPayGiftCardModel.getBranchId();
        BigInteger userId = openPayGiftCardModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        WxCard wxCard = wxCardMapper.find(searchModel);
        ValidateUtils.notNull(wxCard, "请先创建会员卡！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        ValidateUtils.notNull(headquartersBranch, "未检索到总部机构！");
        String headquartersBranchId = headquartersBranch.getId().toString();

        Map<String, String> findPayAccountRequestParameters = new HashMap<String, String>();
        findPayAccountRequestParameters.put("tenantId", tenantId.toString());
        findPayAccountRequestParameters.put("branchId", headquartersBranchId);
        findPayAccountRequestParameters.put("headquartersBranchId", headquartersBranchId);

        ApiRest findPayAccountRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "pay", "findPayAccount", findPayAccountRequestParameters);
        ValidateUtils.isTrue(findPayAccountRest.getIsSuccess(), findPayAccountRest.getError());
        Map<String, Object> payAccountMap = (Map<String, Object>) findPayAccountRest.getData();
        String mchId = MapUtils.getString(payAccountMap, "wechatPaySubMchid");
        ValidateUtils.notNull(mchId, "未配置微信支付账号，不能开通支付即会员！");

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        List<String> mchidList = new ArrayList<String>();
        mchidList.add(mchId);
        baseInfo.put("mchid_list", mchidList);

        baseInfo.put("begin_time", System.currentTimeMillis() / 1000);
        baseInfo.put("end_time", DateUtils.addYears(new Date(), 100).getTime() / 1000);

        Map<String, Object> memberRule = new HashMap<String, Object>();
        memberRule.put("card_id", wxCard.getCardId());
        memberRule.put("least_cost", 0);
        memberRule.put("max_cost", Long.MAX_VALUE);

        Map<String, Object> ruleInfo = new HashMap<String, Object>();
        ruleInfo.put("type", "RULE_TYPE_PAY_MEMBER_CARD");
        ruleInfo.put("base_info", baseInfo);
        ruleInfo.put("member_rule", memberRule);

        Map<String, Object> payGiftCardRequestBody = new HashMap<String, Object>();
        payGiftCardRequestBody.put("rule_info", ruleInfo);

        String authorizerAccessToken = WeiXinUtils.obtainAuthorizerAccessToken(tenantId);

        String payGiftCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/paygiftcard/add?access_token=" + authorizerAccessToken, GsonUtils.toJson(payGiftCardRequestBody), null);
        JSONObject payGiftCardResultJsonObject = JSONObject.fromObject(payGiftCardResult);
        ValidateUtils.isTrue(payGiftCardResultJsonObject.getInt("errcode") == 0, payGiftCardResultJsonObject.optString("errmsg"));

        JSONArray failMchIdList = payGiftCardResultJsonObject.getJSONArray("fail_mchid_list");
        if (CollectionUtils.isNotEmpty(failMchIdList)) {
            JSONObject item = failMchIdList.getJSONObject(0);
            int errcode = item.getInt("errcode");
            String error = null;
            if (errcode == 72004) {
                error = "支付账号被其它支付即会员规则占用，规则ID为：" + item.getString("occupy_rule_id");
            }

            ValidateUtils.isTrue(false, error);
        }

        wxCard.setRuleId(payGiftCardResultJsonObject.getString("rule_id"));
        wxCard.setPayGiftCard(true);
        wxCardMapper.update(wxCard);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("开通支付即会员成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainWxCard(ObtainWxCardModel obtainWxCardModel) {
        BigInteger tenantId = obtainWxCardModel.getTenantId();
        BigInteger branchId = obtainWxCardModel.getBranchId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        WxCard wxCard = wxCardMapper.find(searchModel);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(wxCard);
        apiRest.setMessage("获取微信会员卡成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest geographyCoder(GeographyCoderModel geographyCoderModel) {
        String longitude = geographyCoderModel.getLongitude();
        String latitude = geographyCoderModel.getLatitude();

        String result = OutUtils.doGet("http://apis.map.qq.com/ws/geocoder/v1/?location=" + latitude + "," + longitude + "&key=DBPBZ-RTBR3-BAL3B-Y7GN2-N5V3Z-ELBYM", null);
        JSONObject resultJsonObject = JSONObject.fromObject(result);

        int status = resultJsonObject.getInt("status");
        ValidateUtils.isTrue(status == 0, resultJsonObject.getString("message"));

        ApiRest apiRest = new ApiRest();
        apiRest.setData(resultJsonObject);
        apiRest.setMessage("解析地理位置成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest pushMenu(PushMenuModel pushMenuModel) throws IOException {
        BigInteger tenantId = pushMenuModel.getTenantId();

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        String appId = weiXinAuthorizerInfo.getAuthorizerAppId();
        String originalId = weiXinAuthorizerInfo.getOriginalId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.setOrderBy("rank ASC");
        List<WxMenu> wxMenus = DatabaseHelper.findAll(WxMenu.class, searchModel);
        List<WxMenu> firstLevelMenus = new ArrayList<WxMenu>();
        Map<BigInteger, List<WxMenu>> secondLevelMenuMap = new HashMap<BigInteger, List<WxMenu>>();
        for (WxMenu wxMenu : wxMenus) {
            BigInteger parentId = wxMenu.getParentId();
            if (parentId.compareTo(BigInteger.ZERO) == 0) {
                firstLevelMenus.add(wxMenu);
            } else {
                List<WxMenu> secondLevelMenus = secondLevelMenuMap.get(parentId);
                if (CollectionUtils.isEmpty(secondLevelMenus)) {
                    secondLevelMenus = new ArrayList<WxMenu>();
                    secondLevelMenuMap.put(parentId, secondLevelMenus);
                }
                secondLevelMenus.add(wxMenu);
            }
        }

        List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
        for (WxMenu wxMenu : firstLevelMenus) {
            Map<String, Object> menu = buildMenu(wxMenu, tenantId);
            List<WxMenu> secondLevelMenus = secondLevelMenuMap.get(wxMenu.getId());

            if (CollectionUtils.isNotEmpty(secondLevelMenus)) {
                List<Map<String, Object>> childMenus = new ArrayList<Map<String, Object>>();
                for (WxMenu secondLevelMenu : secondLevelMenus) {
                    childMenus.add(buildMenu(secondLevelMenu, tenantId));
                }
                menu.put("sub_button", childMenus);
            }
            menus.add(menu);
        }

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("button", menus);

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, appId);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + weiXinAuthorizerToken.getAuthorizerAccessToken();
        String result = OutUtils.doPost(url, GsonUtils.toJson(requestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        int errcode = MapUtils.getIntValue(resultMap, "errcode");
        ValidateUtils.isTrue(errcode == 0, MapUtils.getString(resultMap, "errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("菜单推送成功");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public Map<String, Object> buildMenu(WxMenu wxMenu, String appId, String originalId) throws IOException {
        Map<String, Object> menu = new HashMap<String, Object>();
        menu.put("name", wxMenu.getName());

        int menuType = wxMenu.getMenuType();
        BigInteger rank = wxMenu.getRank();
        menu.put("key", "url" + rank);
        if (menuType == 1) {
            menu.put("type", "click");
        } else if (menuType == 2) {
            menu.put("type", "view_limited");
            menu.put("media_id", wxMenu.getMediaId());
        } else if (menuType == 3) {
            menu.put("type", "view");
            menu.put("url", wxMenu.getUrl());
        } else if (menuType == 4) {
            //跳转内部链接
            WxMenuItem wxMenuItem = DatabaseHelper.find(WxMenuItem.class, wxMenu.getItemId());
            menu.put("type", "view");
            menu.put("url", combineUrl(wxMenuItem, appId, originalId));
        } else if (menuType == 5) {
            MiniProgramItem miniProgramItem = DatabaseHelper.find(MiniProgramItem.class, wxMenu.getMiniProgramItemId());
            menu.put("appid", wxMenu.getMiniProgramAppId());
            menu.put("pagepath", miniProgramItem.getPagePath());
            menu.put("type", "miniprogram");
            String url = wxMenu.getUrl();
            if (StringUtils.isNotBlank(url)) {
                menu.put("url", url);
            }
        } else if (menuType == 6) {
            menu.put("appid", wxMenu.getMiniProgramAppId());
            menu.put("type", "miniprogram");
            menu.put("pagepath", wxMenu.getPagePath());
            String url = wxMenu.getUrl();
            if (StringUtils.isNotBlank(url)) {
                menu.put("url", url);
            }
        }
        return menu;
    }

    public String combineUrl(WxMenuItem wxMenuItem, String appId, String originalId) throws IOException {
        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String deployEnv = PropertyUtils.getDefault(Constants.DEPLOY_ENV);
        StringBuilder url = new StringBuilder();
        String domainName = null;
        if (StringUtils.isNotBlank(partitionCode)) {
            domainName = CacheUtils.hget(SessionConstants.KEY_SERVICE_DOMAIN, SessionConstants.KEY_OUTSIDE_SERVICE_NAME + deployEnv + "_" + Constants.SERVICE_NAME_CT + "_" + partitionCode);
        } else {
            domainName = Common.getOutsideServiceDomain(com.saas.common.Constants.SERVICE_NAME_CT);
        }
        url.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=");
        String redirectUrl = "";
        if (wxMenuItem.getParams() != null) {
            redirectUrl = domainName + "/" + wxMenuItem.getController() + "/" + wxMenuItem.getAction() + "?orignalId=" + originalId + "&" + wxMenuItem.getParams();
        } else {
            redirectUrl = domainName + "/" + wxMenuItem.getController() + "/" + wxMenuItem.getAction() + "?orignalId=" + originalId;
        }
        url.append(URLEncoder.encode(redirectUrl, Constants.CHARSET_UTF8));

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);
        url.append("&component_appid=").append(componentAppId);
        url.append("&response_type=code&scope=snsapi_base&#wechat_redirect");
        return url.toString();
    }

    /**
     * 构建菜单，短链接
     *
     * @param wxMenu
     * @return
     * @throws IOException
     */
    public Map<String, Object> buildMenu(WxMenu wxMenu, BigInteger tenantId) throws IOException {
        Map<String, Object> menu = new HashMap<String, Object>();
        menu.put("name", wxMenu.getName());

        int menuType = wxMenu.getMenuType();
        BigInteger rank = wxMenu.getRank();
        menu.put("key", "url" + rank);
        if (menuType == 1) {
            menu.put("type", "click");
        } else if (menuType == 2) {
            menu.put("type", "view_limited");
            menu.put("media_id", wxMenu.getMediaId());
        } else if (menuType == 3) {
            menu.put("type", "view");
            menu.put("url", wxMenu.getUrl());
        } else if (menuType == 4) {
            //跳转内部链接
            WxMenuItem wxMenuItem = DatabaseHelper.find(WxMenuItem.class, wxMenu.getItemId());
            menu.put("type", "view");
            menu.put("url", combineUrl(wxMenuItem, tenantId));
        } else if (menuType == 5) {
            MiniProgramItem miniProgramItem = DatabaseHelper.find(MiniProgramItem.class, wxMenu.getMiniProgramItemId());
            menu.put("appid", wxMenu.getMiniProgramAppId());
            menu.put("pagepath", miniProgramItem.getPagePath());
            menu.put("type", "miniprogram");
            String url = wxMenu.getUrl();
            if (StringUtils.isNotBlank(url)) {
                menu.put("url", url);
            }
        } else if (menuType == 6) {
            menu.put("appid", wxMenu.getMiniProgramAppId());
            menu.put("type", "miniprogram");
            menu.put("pagepath", wxMenu.getPagePath());
            String url = wxMenu.getUrl();
            if (StringUtils.isNotBlank(url)) {
                menu.put("url", url);
            }
        }
        return menu;
    }

    /**
     * 拼接URL，短链接
     *
     * @param wxMenuItem
     * @return
     * @throws IOException
     */
    public String combineUrl(WxMenuItem wxMenuItem, BigInteger tenantId) throws IOException {
        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String outsideServiceDomain = PartitionUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_CT, partitionCode);
        StringBuilder url = new StringBuilder(outsideServiceDomain);
        url.append("/").append(Constants.CT_CONTROLLER_NAME_AUTH).append("/").append(Constants.CT_ACTION_NAME_INDEX).append("?tenantId=").append(tenantId).append("&type=").append(wxMenuItem.getKey());

        String params = wxMenuItem.getParams();
        if (StringUtils.isNotBlank(params)) {
            url.append("&").append(params);
        }
        return url.toString();
    }

    @Transactional(readOnly = true)
    public ApiRest listMiniProgramItems(ListMiniProgramItemsModel listMiniProgramItemsModel) {
        SearchModel searchModel = new SearchModel(true);
        List<MiniProgramItem> miniProgramItems = DatabaseHelper.findAll(MiniProgramItem.class, searchModel);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(miniProgramItems);
        apiRest.setMessage("查询小程序功能成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainWxReplyMessage(ObtainWxReplyMessageModel obtainWxReplyMessageModel) {
        BigInteger tenantId = obtainWxReplyMessageModel.getTenantId();
        BigInteger messageType = obtainWxReplyMessageModel.getMessageType();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("message_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, messageType);
        WxReplyMessage wxReplyMessage = DatabaseHelper.find(WxReplyMessage.class, searchModel);

        Object data = null;
        if (Constants.BIG_INTEGER_THREE.compareTo(messageType) == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("wxReplyMessage", wxReplyMessage);
            if (wxReplyMessage != null) {
                SearchModel wxReplyKeyRuleSearchModel = new SearchModel(true);
                wxReplyKeyRuleSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                wxReplyKeyRuleSearchModel.addSearchCondition("message_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, wxReplyMessage.getId());

                List<WxReplyKeyRule> wxReplyKeyRules = DatabaseHelper.findAll(WxReplyKeyRule.class, wxReplyKeyRuleSearchModel);
                map.put("wxReplyKeyRules", wxReplyKeyRules);
            }
            data = map;
        } else {
            data = wxReplyMessage;
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取微信消息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainReplyContent(ObtainReplyContentModel obtainReplyContentModel) {
        BigInteger tenantId = obtainReplyContentModel.getTenantId();
        String content = obtainReplyContentModel.getContent();

        String replyContent = obtainKeywordReplyContent(tenantId, content);

        if (StringUtils.isBlank(replyContent)) {
            replyContent = obtainAutoReplyContent(tenantId);
        }

        if (StringUtils.isBlank(replyContent)) {
            replyContent = "您发送的是文本消息！";
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(replyContent);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取回复内容成功！");
        return apiRest;
    }

    public String obtainKeywordReplyContent(BigInteger tenantId, String content) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("message_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, Constants.BIG_INTEGER_THREE);
        WxReplyMessage wxReplyMessage = DatabaseHelper.find(WxReplyMessage.class, searchModel);

        if (wxReplyMessage != null) {
            SearchModel wxReplyKeyRuleSearchModel = new SearchModel(true);
            wxReplyKeyRuleSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            wxReplyKeyRuleSearchModel.addSearchCondition("message_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, wxReplyMessage.getId());

            List<WxReplyKeyRule> wxReplyKeyRules = DatabaseHelper.findAll(WxReplyKeyRule.class, wxReplyKeyRuleSearchModel);
            for (WxReplyKeyRule wxReplyKeyRule : wxReplyKeyRules) {
                String[] keywords = wxReplyKeyRule.getKeyWord().split(" ");
                for (String keyword : keywords) {
                    if (content.contains(keyword)) {
                        return wxReplyKeyRule.getContent();
                    }
                }
            }
        }
        return null;
    }

    public String obtainAutoReplyContent(BigInteger tenantId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("message_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, Constants.BIG_INTEGER_TWO);
        WxReplyMessage wxReplyMessage = DatabaseHelper.find(WxReplyMessage.class, searchModel);

        if (wxReplyMessage != null) {
            return wxReplyMessage.getContent();
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteWxNewsArticle(DeleteWxNewsArticleModel deleteWxNewsArticleModel) throws IOException {
        BigInteger tenantId = deleteWxNewsArticleModel.getTenantId();
        BigInteger id = deleteWxNewsArticleModel.getId();
        String mediaId = deleteWxNewsArticleModel.getMediaId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("media_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, mediaId);
        long count = DatabaseHelper.count(WxMenu.class, searchModel);
        ValidateUtils.notNull(count == 0, "图文素材已经被微信菜单使用，不能删除！");

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, weiXinAuthorizerInfo.getAuthorizerAppId());
        WeiXinUtils.delMaterial(mediaId, weiXinAuthorizerToken.getAuthorizerAccessToken());


        SearchModel wxNewsArticleSearchModel = new SearchModel(true);
        wxNewsArticleSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        wxNewsArticleSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, id);
        WxNewsArticle wxNewsArticle = DatabaseHelper.find(WxNewsArticle.class, wxNewsArticleSearchModel);

        if (wxNewsArticle != null) {
            wxNewsArticle.setIsDeleted(true);
            DatabaseHelper.update(wxNewsArticle);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("永久图文删除成功！");
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteWxResourceMedia(DeleteWxResourceMediaModel deleteWxResourceMediaModel) throws IOException {
        BigInteger tenantId = deleteWxResourceMediaModel.getTenantId();
        List<DeleteWxResourceMediaModel.MediaInfo> mediaInfos = deleteWxResourceMediaModel.getMediaInfos();

        List<String> mediaIds = new ArrayList<String>();
        List<BigInteger> wxResourceMediaIds = new ArrayList<BigInteger>();
        for (DeleteWxResourceMediaModel.MediaInfo mediaInfo : mediaInfos) {
            mediaIds.add(mediaInfo.getMediaId());
            wxResourceMediaIds.add(mediaInfo.getImgId());
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("media_id", Constants.SQL_OPERATION_SYMBOL_IN, mediaIds);
        long count = DatabaseHelper.count(WxNewsArticle.class, searchModel);
        ValidateUtils.isTrue(count == 0, "素材已经被使用，不能删除");

        SearchModel wxResourceMediaSearchModel = new SearchModel(true);
        wxResourceMediaSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        wxResourceMediaSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, wxResourceMediaIds);
        List<WxResourceMedia> wxResourceMedias = DatabaseHelper.findAll(WxResourceMedia.class, wxResourceMediaSearchModel);

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, weiXinAuthorizerInfo.getAuthorizerAppId());
        String accessToken = weiXinAuthorizerToken.getAuthorizerAccessToken();

        for (WxResourceMedia wxResourceMedia : wxResourceMedias) {
            WeiXinUtils.delMaterial(wxResourceMedia.getMediaId(), accessToken);
            wxResourceMedia.setIsDeleted(true);
            DatabaseHelper.update(wxResourceMedia);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除素材成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest addWxNewsArticle(AddWxNewsArticleModel addWxNewsArticleModel) throws IOException {
        BigInteger tenantId = addWxNewsArticleModel.getTenantId();
        String title = addWxNewsArticleModel.getTitle();
        String thumbMediaId = addWxNewsArticleModel.getThumbMediaId();
        String digest = addWxNewsArticleModel.getDigest();
        String showCoverPic = addWxNewsArticleModel.getShowCoverPic();
        String content = addWxNewsArticleModel.getContent();
        String contentSourceUrl = addWxNewsArticleModel.getContentSourceUrl();

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String author = weiXinAuthorizerInfo.getNickName();

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, weiXinAuthorizerInfo.getAuthorizerAppId());

        Map<String, Object> article = new HashMap<String, Object>();
        article.put("title", title);
        article.put("thumb_media_id", thumbMediaId);
        article.put("author", author);
        article.put("digest", digest);
        article.put("show_cover_pic", showCoverPic);
        article.put("content", content);
        article.put("content_source_url", contentSourceUrl);

        List<Map<String, Object>> articles = new ArrayList<Map<String, Object>>();
        articles.add(article);

        Map<String, Object> result = WeiXinUtils.addNews(weiXinAuthorizerToken.getAuthorizerAccessToken(), articles);

        WxNewsArticle wxNewsArticle = new WxNewsArticle();
        wxNewsArticle.setMediaId(MapUtils.getString(result, "media_id"));
        wxNewsArticle.setAuthor(author);
        wxNewsArticle.setTenantId(tenantId);
        wxNewsArticle.setContent(content);
        wxNewsArticle.setContentSourceUrl(contentSourceUrl);
        wxNewsArticle.setTitle(title);
        wxNewsArticle.setThumbMediaId(thumbMediaId);
        wxNewsArticle.setDigest(digest);
        wxNewsArticle.setShowCoverPic(Boolean.parseBoolean(showCoverPic));

        wxNewsArticle.setCreateAt(new Date());
        wxNewsArticle.setCreateBy("System");
        wxNewsArticle.setLastUpdateAt(new Date());
        wxNewsArticle.setLastUpdateBy("System");
        DatabaseHelper.insert(wxNewsArticle);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("wxNewsArticle", wxNewsArticle);
        data.put("pictureUrl", "");

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("新增图文信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest updateWxNewsArticle(UpdateWxNewsArticleModel updateWxNewsArticleModel) throws IOException {
        BigInteger id = updateWxNewsArticleModel.getId();
        Integer index = updateWxNewsArticleModel.getIndex();
        BigInteger tenantId = updateWxNewsArticleModel.getTenantId();
        String title = updateWxNewsArticleModel.getTitle();
        String thumbMediaId = updateWxNewsArticleModel.getThumbMediaId();
        String digest = updateWxNewsArticleModel.getDigest();
        String showCoverPic = updateWxNewsArticleModel.getShowCoverPic();
        String content = updateWxNewsArticleModel.getContent();
        String contentSourceUrl = updateWxNewsArticleModel.getContentSourceUrl();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, id);
        WxNewsArticle wxNewsArticle = DatabaseHelper.find(WxNewsArticle.class, searchModel);
        ValidateUtils.notNull(wxNewsArticle, "图文消息不存在！");

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        String author = weiXinAuthorizerInfo.getNickName();

        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, weiXinAuthorizerInfo.getAuthorizerAppId());

        Map<String, Object> article = new HashMap<String, Object>();
        article.put("title", title);
        article.put("thumb_media_id", thumbMediaId);
        article.put("author", author);
        article.put("digest", digest);
        article.put("show_cover_pic", showCoverPic);
        article.put("content", content);
        article.put("content_source_url", contentSourceUrl);

        WeiXinUtils.updateNews(weiXinAuthorizerToken.getAuthorizerAccessToken(), wxNewsArticle.getMediaId(), index, article);

        wxNewsArticle.setAuthor(author);
        wxNewsArticle.setContent(content);
        wxNewsArticle.setContentSourceUrl(contentSourceUrl);
        wxNewsArticle.setTitle(title);
        wxNewsArticle.setThumbMediaId(thumbMediaId);
        wxNewsArticle.setDigest(digest);
        wxNewsArticle.setShowCoverPic(Boolean.parseBoolean(showCoverPic));

        wxNewsArticle.setCreateAt(new Date());
        wxNewsArticle.setCreateBy("System");
        wxNewsArticle.setLastUpdateAt(new Date());
        wxNewsArticle.setLastUpdateBy("System");
        DatabaseHelper.update(wxNewsArticle);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("wxNewsArticle", wxNewsArticle);
        data.put("pictureUrl", "");

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("修改图文信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest listWxReplyTemplates(ListWxReplyTemplatesModel listWxReplyTemplatesModel) {
        BigInteger tenantId = listWxReplyTemplatesModel.getTenantId();

        Map<String, String> listWxReplyTemplatesRequestParameters = new HashMap<String, String>();
        listWxReplyTemplatesRequestParameters.put("tenantId", tenantId.toString());

        ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "wechat", "listWxReplyTemplates", listWxReplyTemplatesRequestParameters);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        return apiRest;
    }

    public ApiRest pushMessage(PushMessageModel pushMessageModel) throws IOException {
        BigInteger tenantId = pushMessageModel.getTenantId();
        String mediaId = pushMessageModel.getMediaId();

        String authorizerAccessToken = WeiXinUtils.obtainAuthorizerAccessToken(tenantId);

        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put("is_to_all", true);

        Map<String, Object> mpnews = new HashMap<String, Object>();
        mpnews.put("media_id", mediaId);

        WeiXinUtils.sendMpNewsToAll(authorizerAccessToken, filter, mpnews, 0);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("群发消息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainMessageContent(ObtainMessageContentModel obtainMessageContentModel) {
        BigInteger tenantId = obtainMessageContentModel.getTenantId();
        BigInteger rank = obtainMessageContentModel.getRank();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("rank", Constants.SQL_OPERATION_SYMBOL_EQUALS, rank);
        WxMenu wxMenu = DatabaseHelper.find(WxMenu.class, searchModel);
        String messageContent = "";
        if (wxMenu != null) {
            messageContent = wxMenu.getMsgContent();
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(messageContent);
        apiRest.setMessage("获取消息内容成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest updateMemberCard(UpdateMemberCardModel updateMemberCardModel) throws IOException {
        BigInteger tenantId = updateMemberCardModel.getTenantId();
        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinAuthorizerInfo(tenantId);
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检索到授权信息！");

        SearchModel wxCardSearchModel = new SearchModel(true);
        wxCardSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        WxCard wxCard = DatabaseHelper.find(WxCard.class, wxCardSearchModel);
        ValidateUtils.notNull(wxCard, "未创建微信会员卡！");

        Map<String, Object> updateMemberCardRequestBody = new HashMap<String, Object>();
        updateMemberCardRequestBody.put("card_id", wxCard.getCardId());

        String authorizerAppId = weiXinAuthorizerInfo.getAuthorizerAppId();
        String originalId = weiXinAuthorizerInfo.getOriginalId();
        String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String outsideServiceDomain = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT, partitionCode);

        Map<String, Object> memberCard = new HashMap<String, Object>();
        String customField1Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/showVipStore?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customField1 = new HashMap<String, Object>();
        customField1.put("name", "快速充值");
        customField1.put("url", customField1Url);
        memberCard.put("custom_field1", customField1);

        String customField2Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/index?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customField2 = new HashMap<String, Object>();
        customField2.put("name", "储值余额");
        customField2.put("url", customField2Url);
        memberCard.put("custom_field2", customField2);

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        String customUrl = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/myCoupon?orignalId=" + originalId, null, componentAppId);

        baseInfo.put("custom_url_name", "我的优惠券");
        baseInfo.put("custom_url", customUrl);
        baseInfo.put("custom_url_sub_title", "我的优惠券");

        String promotionUrl = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/vip/index?tenantId=" + tenantId + "&orignalId=" + originalId, null, componentAppId);
        baseInfo.put("promotion_url_name", "我的订单");
        baseInfo.put("promotion_url", promotionUrl);
        baseInfo.put("promotion_url_sub_title", "我的订单");

        String customCell1Url = WeiXinUtils.generateAuthorizeUrl(authorizerAppId, "snsapi_base", outsideServiceDomain + "/frontTenantVip/getCoupon?orignalId=" + originalId, null, componentAppId);
        Map<String, Object> customCell1 = new HashMap<String, Object>();
        customCell1.put("name", "领取优惠券");
        customCell1.put("tips", "领取优惠券");
        customCell1.put("url", customCell1Url);

        memberCard.put("custom_cell1", customCell1);
        memberCard.put("base_info", baseInfo);

        updateMemberCardRequestBody.put("member_card", memberCard);

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, authorizerAppId);
        String url = "https://api.weixin.qq.com/card/update?access_token=" + weiXinAuthorizerToken.getAuthorizerAccessToken();

        String result = OutUtils.doPost(url, GsonUtils.toJson(updateMemberCardRequestBody), null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        int errcode = MapUtils.getIntValue(resultMap, "errcode");
        ValidateUtils.isTrue(errcode == 0, MapUtils.getString(resultMap, "errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setData(wxCard);
        apiRest.setMessage("更新会员卡成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 删除支付即会员
     *
     * @param deletePayGiftCardModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deletePayGiftCard(DeletePayGiftCardModel deletePayGiftCardModel) throws IOException {
        BigInteger tenantId = deletePayGiftCardModel.getTenantId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        WxCard wxCard = wxCardMapper.find(searchModel);
        ValidateUtils.notNull(wxCard, "请先创建会员卡！");

        String ruleId = wxCard.getRuleId();
        ValidateUtils.isTrue(StringUtils.isNotBlank(ruleId), "未开通支付即会员！");

        Map<String, Object> deletePayGiftCardRequestBody = new HashMap<String, Object>();
        deletePayGiftCardRequestBody.put("rule_id", wxCard.getRuleId());

        String authorizerAccessToken = WeiXinUtils.obtainAuthorizerAccessToken(tenantId);

        String deletePayGiftCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/paygiftcard/delete?access_token=" + authorizerAccessToken, GsonUtils.toJson(deletePayGiftCardRequestBody), null);

        JSONObject deletePayGiftCardResultJsonObject = JSONObject.fromObject(deletePayGiftCardResult);
        ValidateUtils.isTrue(deletePayGiftCardResultJsonObject.getInt("errcode") == 0, deletePayGiftCardResultJsonObject.optString("errmsg"));

        wxCard.setRuleId(null);
        wxCard.setPayGiftCard(false);
        wxCardMapper.update(wxCard);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除支付即会员成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
