package erp.chain.controller.online;

import com.saas.common.util.PropertyUtils;
import erp.chain.annotations.ApiRestAction;
import erp.chain.beans.weixin.ComponentAccessToken;
import erp.chain.common.Constants;
import erp.chain.model.online.onlineweixin.*;
import erp.chain.service.online.OnlineWeiXinService;
import erp.chain.utils.AESUtils;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.WeiXinUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import saas.api.common.ApiRest;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.security.Security;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-11.
 */
@Controller
@RequestMapping(value = "/onlineWeiXin")
public class OnlineWeiXinController {
    private static final String ONLINE_WEI_XIN_CONTROLLER_SIMPLE_NAME = "OnlineWeiXinController";
    @Autowired
    private OnlineWeiXinService onlineWeiXinService;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /*@RequestMapping(value = "/jsCode2Session")
    @ResponseBody
    @ApiRestAction(error = "获取登录凭证失败")
    public String jsCode2Session() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        JsCode2SessionModel jsCode2SessionModel = ApplicationHandler.instantiateObject(JsCode2SessionModel.class, requestParameters);
        jsCode2SessionModel.validateAndThrow();

        Map<String, String> jsCode2SessionRequestParameters = new HashMap<String, String>();
        jsCode2SessionRequestParameters.put("appId", jsCode2SessionModel.getAppId());
        jsCode2SessionRequestParameters.put("secret", jsCode2SessionModel.getSecret());
        jsCode2SessionRequestParameters.put("jsCode", jsCode2SessionModel.getJsCode());

        ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "weiXin", "jsCode2Session", jsCode2SessionRequestParameters);
        Validate.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        return BeanUtils.toJsonStr(apiRest);
    }*/

    @RequestMapping(value = "/jsCode2Session")
    @ResponseBody
    @ApiRestAction(error = "获取登录凭证失败")
    public String jsCode2Session() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        JsCode2SessionModel jsCode2SessionModel = ApplicationHandler.instantiateObject(JsCode2SessionModel.class, requestParameters);
        jsCode2SessionModel.validateAndThrow();

        String appId = jsCode2SessionModel.getAppId();
        String jsCode = jsCode2SessionModel.getJsCode();

        Map<String, Object> result = null;
        if ("wxe0f11d2285a25b76".equals(appId)) {
            result = WeiXinUtils.jsCode2Session(appId, "fd95d721e0b959e03a530b90e087676d", jsCode);
        } else if ("wxc845fbe88dd15d15".equals(appId)) {
            result = WeiXinUtils.jsCode2Session(appId, "1dbe02a3be32241edb78f08fbff95b09", jsCode);
        } else {
            String componentAppId = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);
            String componentAppSecret = PropertyUtils.getDefault(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_SECRET);
            ComponentAccessToken componentAccessToken = WeiXinUtils.obtainComponentAccessToken(componentAppId, componentAppSecret);

            result = WeiXinUtils.jsCode2Session(appId, jsCode, componentAppId, componentAccessToken.getComponentAccessToken());
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(result);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取登录凭证成功！");
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/decryptData", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(error = "数据解密失败")
    public String decryptData() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        DecryptDataModel decryptDataModel = ApplicationHandler.instantiateObject(DecryptDataModel.class, requestParameters);
        decryptDataModel.validateAndThrow();

        byte[] bytes = AESUtils.decrypt(Base64.decodeBase64(decryptDataModel.getEncryptedData()), Base64.decodeBase64(decryptDataModel.getSessionKey()), Base64.decodeBase64(decryptDataModel.getIv()), AESUtils.ALGORITHM_AES_CBC_PKCS7PADDING, AESUtils.PROVIDER_NAME_BC);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(new String(bytes, Constants.CHARSET_UTF8));
        apiRest.setMessage("数据解密成功！");
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 创建微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/createMemberCard", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(error = "创建微信会员卡失败")
    public String createMemberCard(HttpServletRequest httpServletRequest) throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String brandName = requestParameters.get("brandName");
        String color = requestParameters.get("color");
        String title = requestParameters.get("title");
        CreateMemberCardModel createMemberCardModel = ApplicationHandler.instantiateObject(CreateMemberCardModel.class, requestParameters);
        createMemberCardModel.setBrandName(URLDecoder.decode(brandName, Constants.CHARSET_UTF8));
        createMemberCardModel.setColor(URLDecoder.decode(color, Constants.CHARSET_UTF8));
        createMemberCardModel.setTitle(URLDecoder.decode(title, Constants.CHARSET_UTF8));
        createMemberCardModel.validateAndThrow();

        Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传会员卡logo！");
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

        MultipartFile logoFile = multipartHttpServletRequest.getFile("logo");
        Validate.notNull(logoFile, "请上传会员卡logo！");

        MultipartFile backgroundPicFile = multipartHttpServletRequest.getFile("backgroundPic");
        Validate.isTrue(backgroundPicFile != null || StringUtils.isNotBlank(createMemberCardModel.getColor()), "请设置会员卡封面！");
        return BeanUtils.toJsonStr(onlineWeiXinService.createMemberCard(createMemberCardModel, backgroundPicFile, logoFile));
    }

    /**
     * 删除卡券
     *
     * @return
     */
    @RequestMapping(value = "/deleteCard", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "deleteCard", error = "删除卡券失败")
    public String deleteCard() {
        return null;
    }

    @RequestMapping(value = "/activateMemberCard", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = ActivateMemberCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "activateMemberCard", error = "激活会员卡失败")
    public String activateMemberCard() {
        return null;
    }

    @RequestMapping(value = "/openPayGiftCard", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = OpenPayGiftCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "openPayGiftCard", error = "开通支付即会员失败")
    public String openPayGiftCard() {
        return null;
    }

    /**
     * 获取微信会员卡信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainWxCard", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainWxCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "obtainWxCard", error = "获取微信会员卡失败")
    public String obtainWxCard() {
        return null;
    }

    /**
     * 更新微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/updateMemberCard")
    @ResponseBody
    @ApiRestAction(modelClass = UpdateMemberCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "updateMemberCard", error = "更新会员卡失败")
    public String updateMemberCard() {
        return null;
    }

    /**
     * 解析地理位置
     *
     * @return
     */
    @RequestMapping(value = "/geographyCoder", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = GeographyCoderModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "geographyCoder", error = "解析地理位置失败")
    public String geographyCoder() {
        return null;
    }

    /**
     * 推送菜单
     *
     * @return
     */
    @RequestMapping(value = "/pushMenu", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiRestAction(modelClass = PushMenuModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "pushMenu", error = "推送菜单失败")
    public String pushMenu() {
        return null;
    }

    @RequestMapping(value = "/listMiniProgramItems", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListMiniProgramItemsModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "listMiniProgramItems", error = "查询小程序指定功能失败")
    public String listMiniProgramItems() {
        return null;
    }

    @RequestMapping(value = "/obtainWxReplyMessage", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainWxReplyMessageModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "obtainWxReplyMessage", error = "获取微信消息失败")
    public String obtainWxReplyMessage() {
        return null;
    }

    @RequestMapping(value = "/obtainReplyContent", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainReplyContentModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "obtainReplyContent", error = "获取回复内容失败")
    public String obtainReplyContent() {
        return null;
    }

    @RequestMapping(value = "/deleteWxNewsArticle", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiRestAction(modelClass = DeleteWxNewsArticleModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "deleteWxNewsArticle", error = "删除图文消息失败")
    public String deleteWxNewsArticle() {
        return null;
    }

    @RequestMapping(value = "/deleteWxResourceMedia", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiRestAction(modelClass = DeleteWxResourceMediaModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "deleteWxResourceMedia", error = "删除素材失败")
    public String deleteWxResourceMedia() {
        return null;
    }

    @RequestMapping(value = "/addWxNewsArticle", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = AddWxNewsArticleModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "addWxNewsArticle", error = "新增图文消息失败")
    public String addWxNewsArticle() {
        return null;
    }

    @RequestMapping(value = "/updateWxNewsArticle", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = UpdateWxNewsArticleModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "updateWxNewsArticle", error = "更新图文消息失败")
    public String updateWxNewsArticle() {
        return null;
    }

    @RequestMapping(value = "/listWxReplyTemplates", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ListWxReplyTemplatesModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "listWxReplyTemplates", error = "获取模板消息失败")
    public String listWxReplyTemplates() {
        return null;
    }

    @RequestMapping(value = "/pushMessage", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = PushMessageModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "pushMessage", error = "群发消息失败")
    public String pushMessage() {
        return null;
    }

    @RequestMapping(value = "/obtainMessageContent", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainMessageContentModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "obtainMessageContent", error = "群发消息失败")
    public String obtainMessageContent() {
        return null;
    }

    /**
     * 删除支付即会员
     *
     * @return
     */
    @RequestMapping(value = "/deletePayGiftCard", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = DeletePayGiftCardModel.class, serviceClass = OnlineWeiXinService.class, serviceMethodName = "deletePayGiftCard", error = "删除支付即会员失败")
    public String deletePayGiftCard() {
        return null;
    }
}
