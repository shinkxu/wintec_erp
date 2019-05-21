package erp.chain.controller.o2o;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.CardService;
import erp.chain.service.o2o.WechatInfoService;
import erp.chain.service.o2o.WechatReplyService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.O2OApi;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信小心
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/18
 */
@Controller
@ResponseBody
@RequestMapping("wechatReply")
public class WechatReplyController{
    @Autowired
    private WechatReplyService wechatReplyService;
    @Autowired
    private WechatInfoService wechatInfoService;
    @Autowired
    private CardService cardService;

    /**
     * 获取模版消息列表
     */
    @RequestMapping("/getTemplateList")
    public String getTemplateList() {
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("wxWechatOriginalId")==null||params.get("wxWechatOriginalId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("wxWechatOriginalId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = ProxyApi.proxyPost("bs", "wechat", "listWxReplyTemplate", params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 保存、更新模板消息
     * @return
     */
    @RequestMapping("/saveOrUpdateTemplate")
    public String saveOrUpdateTemplate() {
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("data")==null||params.get("data").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("data不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = ProxyApi.proxyPost("bs", "wechat", "saveAllWxReplyTemplate", params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 保存图片所在为新服务器url
     * @params
     * 1.mediaId not null
     * 2.url   可以为空
     * 3.type   可以为空
     * @author szq
     */
    @RequestMapping("/saveUrl")
    public String saveUrl() {
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.saveUrl(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError("保存图片所在为新服务器url: " + e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 更新会员卡背景
     * @return
     */
    @RequestMapping("/saveUrlForVc")
    public String saveUrlForVc() {
        ApiRest rest = new ApiRest();
        try {
            Map<String,String> params = AppHandler.params();
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                rest.setIsSuccess(false);
                rest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(rest);
            }
            if(params.get("url")==null||params.get("url").equals("")){
                rest.setIsSuccess(false);
                rest.setError("url不能为空！");
                return BeanUtils.toJsonStr(rest);
            }
            if(params.get("cardId")==null||params.get("cardId").equals("")){
                rest.setIsSuccess(false);
                rest.setError("cardId不能为空！");
                return BeanUtils.toJsonStr(rest);
            }
            //查询微信公众号配置
            ApiRest apiRest = new ApiRest();
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatInfoService.wechatInfo(params);
            Map wechatInfo = null;
            if(apiRest.getIsSuccess() && apiRest.getData() != null){
                List list = (List)((Map)apiRest.getData()).get("rows");
                if(list != null && list.size() > 0){
                    wechatInfo = (Map)list.get(0);
                }
            }
            ApiRest baseRest = null;
            if(wechatInfo != null){
                String appId = wechatInfo.get("appId").toString();
                String secret = wechatInfo.get("appSecret").toString();
                baseRest = O2OApi.askBaseAccessToken(appId, secret);
            }
            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if(token != null){
                    Map<String,String> paramsInfo = new HashMap<>();
                    JSONObject memberCard = new JSONObject();
                    memberCard.put("background_pic_url",params.get("url"));
                    JSONObject baseInfo = new JSONObject();
//                    baseInfo.put("title","微信会员卡");
                    memberCard.put("base_info",baseInfo);

                    JSONObject card = new JSONObject();
                    card.put("card_id",params.get("cardId"));
                    card.put("member_card",memberCard);

                    String url = "https://api.weixin.qq.com/card/update?access_token=" + token;

                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",card.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("errmsg"))){
                            rest.setIsSuccess(true);
                            rest.setMessage("更新会员卡成功。");
                            cardService.updateWxCard(params);
                        } else {
                            rest.setIsSuccess(false);
                            rest.setMessage("更新会员卡失败！" + map.get("errmsg"));
                            LogUtil.logError("更新会员卡失败：" + map.get("errmsg"));
                        }
                    }
                }
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("请前往微信设置页面配置微信公众号信息！");
            }

        } catch (Exception e) {
            LogUtil.logError("更新会员卡异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);

    }

    /**
     * 上传永久图文消息
     * @author szq
     */
    @RequestMapping("/pushPhotoArticle")
    public String pushPhotoArticle(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("userId")==null||params.get("userId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("digest")==null||params.get("digest").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("正文不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if(params.get("thumbMediaId")==null||params.get("thumbMediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("封面图片不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("title")==null||params.get("title").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("title不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("content")==null||params.get("content").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("content不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("contentSourceUrl")==null||params.get("contentSourceUrl").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("contentSourceUrl不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if(params.get("showCoverPic")==null||params.get("showCoverPic").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("showCoverPic不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("digest").length()>=200){
                apiRest.setIsSuccess(false);
                apiRest.setError("摘要字数应该小于200！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=wechatReplyService.pushPhotoArticle(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError("上传永久图文消息异常: " + e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 发送消息，群发接口
     * @author szq
     */
    @RequestMapping("/pushMessage")
    public String pushMessage(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("userId")==null||params.get("userId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=wechatReplyService.pushMessage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError("群发图文消息异常:" + e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询素材
     * @params
     * 1.tenantId  商户id
     * 2.mediaType 素材类型
     * @author szq
     */
    @RequestMapping("/listMaterial")
    public String listMaterial(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.listMaterial(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询图文素材
     * @params
     * @author  szq
     */
    @RequestMapping("/listPhotoArticle")
    public String listPhotoArticle(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.listPhotoArticle(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 增加消息回复
     * @params
     * 1.tenantId 商户id  不能为空
     * 2.messageType 消息类型  不能为空
     * 3.content   消息内容  不能为空
     * 4.
     * @author szq
     * @return
     */
    @RequestMapping("/addMessage")
    public String addMessage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("content")==null||params.get("content").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("消息内容不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if(params.get("messageType")==null||params.get("messageType").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }else{
                if(!(params.get("messageType").equals("1")||params.get("messageType").equals("2"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("messageType必须是1、2！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = wechatReplyService.addMessage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改图文消息
     * @params
     * 1.id   图文消息id
     * @author szq
     */
    @RequestMapping("/editPhotoArticle")
    public String editPhotoArticle(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("userId")==null||params.get("userId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id")==null||params.get("id").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("digest")==null||params.get("digest").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("正文不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("thumbMediaId")==null||params.get("thumbMediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("封面图片不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("index")==null||params.get("index").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("index不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("content")==null||params.get("content").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("content不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("title")==null||params.get("title").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("title不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("contentSourceUrl")==null||params.get("contentSourceUrl").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("contentSourceUrl不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if(params.get("showCoverPic")==null||params.get("showCoverPic").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("showCoverPic不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("digest").length()>=200){
                apiRest.setIsSuccess(false);
                apiRest.setError("摘要字数应该小于200！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=wechatReplyService.editPhotoArticle(params);
        } catch(Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError("修改图文消息异常:" + e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 批量删除永久图片
     * @params
     * 1.id
     * 2.thumbMediaId
     * @author szq
     */
    @RequestMapping("/deletePhoto")
    public String deletePhoto(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("list")==null||params.get("list").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("list不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.deletePhotoLocal(params);
        } catch(Exception e) {
            LogUtil.logError(e,params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除永久图文
     * @params
     * 1.id
     * 2.mediaId
     * @author szq
     */
    @RequestMapping("/deletePhotoArticle")
    public String deletePhotoArticle(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("userId")==null||params.get("userId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id")==null||params.get("id").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=wechatReplyService.deletePhotoArticle(params);
        } catch(Exception e) {
            LogUtil.logError("删除永久图文素材异常:" + e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 增加关键词回复
     * @params
     * 1.tenantId 商户id  不能为空
     * 2.content   消息内容  不能为空
     * 3.ruleName  规则名称  不能为空
     * 4.keyWord    关键词   不能为空
     */
    @RequestMapping("/addKeywordMessage")
    public String addKeywordMessage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("content")==null||params.get("content").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("消息内容不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("ruleName")==null||params.get("ruleName").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("规则名称不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("keyWord")==null||params.get("keyWord").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("关键词不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.addKeywordMessage(params);
        } catch (Exception e) {
            LogUtil.logError("增加关键词回复:" + e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除消息回复
     */
    @RequestMapping("/delMessage")
    public String delMessage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id")==null||params.get("id").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("type")==null||params.get("type").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.delMessage(params);
        } catch (Exception e) {
            LogUtil.logError("删除关键词:" + e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询消息
     * @params
     * 1.tenantId not null
     * 2.messageType  消息类型  1：关注回复 2：消息自动回复 3：关键词回复
     * @author szq
     * @return
     */
    @RequestMapping("/listMessage")
    public String listMessage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("messageType")==null||params.get("messageType").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("messageType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(!(params.get("messageType").equals("1")||params.get("messageType").equals("2")||params.get("messageType").equals("3"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("messageType必须是1、2、3！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.listMessage(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据图文消息的mediaId查询图文消息
     * @params
     * 1.mediaId
     * 2.tenantId
     * @author szq
     * @return
     */
    @RequestMapping("/findPhotoArticleByMediaId")
    public String findPhotoArticleByMediaId(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId")==null||params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("mediaId")==null||params.get("mediaId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("图文消息的mediaId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatReplyService.findPhotoArticleByMediaId(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

}
