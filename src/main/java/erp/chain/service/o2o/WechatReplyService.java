package erp.chain.service.o2o;

import com.saas.common.util.LogUtil;
import erp.chain.domain.o2o.*;
import erp.chain.mapper.o2o.WechatMenuMapper;
import erp.chain.mapper.o2o.WechatReplyMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WechatReplyService{
    @Autowired
    private WechatReplyMapper wechatReplyMapper;
    @Autowired
    private WechatMenuMapper wechatMenuMapper;

    /**
     * 查询素材
     *
     * @params 1.tenantId  商户id
     * 2.mediaType 素材类型
     * @author szq
     */
    public ApiRest listMaterial(Map params){
        ApiRest rest = new ApiRest();
        List<WxResourceMedia> wxResourceMediaList = wechatReplyMapper.getList(params);
        rest.setData(wxResourceMediaList);
        rest.setIsSuccess(true);
        rest.setMessage("素材查询成功");
        return rest;
    }

    /**
     * 查询图文素材
     *
     * @params
     * @author szq
     */
    public ApiRest listPhotoArticle(Map params){
        ApiRest rest = new ApiRest();
        List<Map> list = wechatReplyMapper.getMapList(params);
        rest.setData(list);
        rest.setIsSuccess(true);
        rest.setMessage("图文素材查询成功");
        return rest;
    }

    /**
     * 增加消息回复
     *
     * @author szq
     */
    public ApiRest addMessage(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger messageType = BigInteger.valueOf(Long.parseLong((String)params.get("messageType")));
        String content = params.get("content") == null ? "" : params.get("content").toString();
        WxReplyMessage wxReplyMessage;
        List<WxReplyMessage> wxReplyMessageList = wechatReplyMapper.getWxReplyMessage(tenantId, messageType);
        if(wxReplyMessageList.size() > 0){
            wxReplyMessage = wxReplyMessageList.get(0);
            wxReplyMessage.setContent(content);
            int i = wechatReplyMapper.replyMessageUpdate(wxReplyMessage);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("修改自动回复消息失败！");
                return rest;
            }
        }
        else{
            wxReplyMessage = new WxReplyMessage();
            wxReplyMessage.setTenantId(tenantId);
            wxReplyMessage.setMessageType(messageType);
            wxReplyMessage.setContent(content);
            int i = wechatReplyMapper.replyMessageInsert(wxReplyMessage);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("新增自动回复消息失败！");
                return rest;
            }
        }
        rest.setData(wxReplyMessage);
        rest.setMessage("信息回复添加成功");
        rest.setIsSuccess(true);
        return rest;
    }

    /**
     * 修改图文消息
     *
     * @params 1.id   图文消息id
     * @author szq
     */
    public ApiRest editPhotoArticle(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger sysUserId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        //String digest = params.get("digest").toString();

        //查询wxWechatInfo列表
        String jsonResult = "";
        params.put("sysUserId", sysUserId);
        params.put("methodKey", "listWechatInfo");
        Map<String, String> mapList = new HashMap<>();
        mapList.put("sysUserId", sysUserId.toString());
        mapList.put("methodKey", "listWechatInfo");
        jsonResult = SaaSApi.listWechatInfo(mapList);
        JSONObject j = JSONObject.fromObject(jsonResult);
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        JSONArray bb = (JSONArray)jj.get("rows");
        if(bb != null && bb.size() > 0){
            Map pMap = new HashMap();
            params.put("author", ((Map)bb.get(0)).get("name"));
            pMap.put("appid", ((Map)bb.get(0)).get("appId"));
            pMap.put("secret", ((Map)bb.get(0)).get("appSecret"));
            pMap.put("author", ((Map)bb.get(0)).get("name"));
            String mediaId = params.get("mediaId") == null ? null : params.get("mediaId").toString();
            String thumbMediaId = StringUtils.trimToNull(params.get("thumbMediaId").toString());
            String title = StringUtils.trimToNull(params.get("title").toString());
            String content = StringUtils.trimToNull(params.get("content").toString());
            String textContent = StringUtils.trimToNull(params.get("textContent")==null?"":params.get("textContent").toString());
            String index = StringUtils.trimToNull(params.get("index").toString());
            String contentSourceUrl = params.get("contentSourceUrl") == null ? null : params.get("contentSourceUrl").toString();
            String showCoverPic = StringUtils.trimToNull(params.get("showCoverPic").toString());
            if(((Map)bb.get(0)).get("name") == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("author不能为空");
                return apiRest;
            }
            if(textContent != null){
                if(textContent.length() >= 10000){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("正文字数应该小于10000");
                    return apiRest;
                }
            }
            if(content.equals("<p><br></p>")){
                apiRest.setIsSuccess(false);
                apiRest.setError("正文不能为空");
                return apiRest;
            }
            if(content.length()>20000){
                apiRest.setIsSuccess(false);
                apiRest.setError("内容应少于20000字符");
                return apiRest;
            }
            pMap.put("mediaId", mediaId);
            pMap.put("thumbMediaId", thumbMediaId);
            pMap.put("index", index);
            pMap.put("title", title);
            pMap.put("digest", params.get("digest"));
            pMap.put("content", content);
            pMap.put("contentSourceUrl", contentSourceUrl);
            pMap.put("showCoverPic", showCoverPic);
            apiRest = ProxyApi.proxyPost("out", "wechat", "editPhotoArticle", pMap);
            if(apiRest.getIsSuccess()){
                apiRest = savePhotoArticleMessage(params);
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询微信信息失败！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 推送完图文消息后对信息的保存
     *
     * @author szq
     */
    public ApiRest savePhotoArticleMessage(Map params){
        ApiRest rest = new ApiRest();
        Map<String, Object> map = new HashMap<String, Object>();
        WxNewsArticle wxNewsArticle = null;
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String thumbMediaId = (String)params.get("thumbMediaId");
        WxResourceMedia wxResourceMedia = wechatReplyMapper.findResourceMediaById(thumbMediaId, tenantId);
        String pictureUrl = null;
        if(wxResourceMedia != null){
            pictureUrl = wxResourceMedia.getUrl();
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            wxNewsArticle = wechatReplyMapper.findNewsArticleById(id, tenantId);
            if(wxNewsArticle != null){
                wxNewsArticle.setLastUpdateAt(new Date());
                wxNewsArticle.setLastUpdateBy("System");
                wxNewsArticle.setMediaId(params.get("mediaId").toString());
                wxNewsArticle.setAuthor(params.get("author").toString());
                wxNewsArticle.setTenantId(tenantId);
                wxNewsArticle.setContent(params.get("content").toString());
                wxNewsArticle.setContentSourceUrl(params.get("contentSourceUrl") == null ? null : params.get("contentSourceUrl").toString());
                wxNewsArticle.setTitle(params.get("title").toString());
                wxNewsArticle.setThumbMediaId(((String)params.get("thumbMediaId")));
                wxNewsArticle.setDigest(params.get("digest").toString());
                wxNewsArticle.setShowCoverPic(Integer.valueOf(params.get("showCoverPic").toString())==1);
                int i = wechatReplyMapper.newsArticleUpdate(wxNewsArticle);
                if(i <= 0){
                    rest.setIsSuccess(false);
                    rest.setError("保存图文消息失败！");
                    return rest;
                }
                map.put("wxNewsArticle", wxNewsArticle);
                map.put("pictureUrl", pictureUrl);
                rest.setData(map);
                rest.setMessage("图文信息修改成功");
                rest.setIsSuccess(true);
            }
        }
        else{
            wxNewsArticle = new WxNewsArticle();
            wxNewsArticle.setLastUpdateAt(new Date());
            wxNewsArticle.setLastUpdateBy("System");
            wxNewsArticle.setCreateAt(new Date());
            wxNewsArticle.setCreateBy("System");
            wxNewsArticle.setMediaId(params.get("photoArticleMediaId").toString());
            wxNewsArticle.setAuthor(params.get("author").toString());
            wxNewsArticle.setTenantId(tenantId);
            wxNewsArticle.setContent(params.get("content").toString());
            wxNewsArticle.setContentSourceUrl(params.get("contentSourceUrl") == null ? null : params.get("contentSourceUrl").toString());
            wxNewsArticle.setTitle(params.get("title").toString());
            wxNewsArticle.setThumbMediaId(((String)params.get("thumbMediaId")));
            wxNewsArticle.setDigest(params.get("digest").toString());
            wxNewsArticle.setShowCoverPic(Boolean.parseBoolean(params.get("showCoverPic").toString()));
            int i = wechatReplyMapper.newsArticleInsert(wxNewsArticle);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("保存图文消息失败！");
                return rest;
            }
            map.put("wxNewsArticle", wxNewsArticle);
            map.put("pictureUrl", pictureUrl);
            rest.setData(map);
            rest.setMessage("图文信息保存成功");
            rest.setIsSuccess(true);
        }
        return rest;
    }

    /**
     * 上传永久图文消息
     *
     * @author szq
     */
    public ApiRest pushPhotoArticle(Map params){
        ApiRest apiRest = new ApiRest();
        //查询wxWechatInfo列表
        String jsonResult = "";
        BigInteger sysUserId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        params.put("sysUserId", sysUserId);
        params.put("methodKey", "listWechatInfo");
        Map<String, String> mapList = new HashMap<>();
        mapList.put("sysUserId", sysUserId.toString());
        mapList.put("methodKey", "listWechatInfo");
        jsonResult = SaaSApi.listWechatInfo(mapList);
        JSONObject j = JSONObject.fromObject(jsonResult);
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        JSONArray bb = (JSONArray)jj.get("rows");
        //获得appid、secret、name
        if(bb != null && bb.size() > 0){
            Map pMap = new HashMap();
            params.put("author", ((Map)bb.get(0)).get("name"));
            pMap.put("appid", ((Map)bb.get(0)).get("appId"));
            pMap.put("secret", ((Map)bb.get(0)).get("appSecret"));
            pMap.put("author", ((Map)bb.get(0)).get("name"));
            String thumbMediaId = StringUtils.trimToNull(params.get("thumbMediaId").toString());
            String title = StringUtils.trimToNull(params.get("title").toString());
            String content = StringUtils.trimToNull(params.get("content").toString());
            String textContent = StringUtils.trimToNull(params.get("textContent")==null?"":params.get("textContent").toString());
            String contentSourceUrl = params.get("contentSourceUrl") == null ? null : params.get("contentSourceUrl").toString();
            String showCoverPic = StringUtils.trimToNull(params.get("showCoverPic").toString());
            if(((Map)bb.get(0)).get("name") == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("author不能为空");
                return apiRest;
            }
            if(textContent != null){
                if(textContent.length() >= 10000){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("正文字数应该小于10000");
                    return apiRest;
                }
            }
            if(content.equals("<p><br></p>")){
                apiRest.setIsSuccess(false);
                apiRest.setError("正文不能为空");
                return apiRest;
            }
            pMap.put("thumbMediaId", thumbMediaId);
            pMap.put("title", title);
            pMap.put("digest", params.get("digest"));
            pMap.put("content", content);
            pMap.put("contentSourceUrl", contentSourceUrl);
            pMap.put("showCoverPic", showCoverPic);
            apiRest = ProxyApi.proxyPost("out", "wechat", "pushPhotoArticle", pMap);
            if(apiRest.getIsSuccess()){
                params.put("photoArticleMediaId", ((Map)apiRest.getData()).get("mediaId"));
                apiRest = savePhotoArticleMessage(params);
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询微信信息失败！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 推送消息
     *
     * @param params
     * @return
     */
    public ApiRest pushMessage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger sysUserId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        //查询wxWechatInfo列表
        String jsonResult = "";
        params.put("sysUserId", sysUserId);
        params.put("methodKey", "listWechatInfo");
        Map<String, String> mapList = new HashMap<String, String>();
        mapList.put("sysUserId", sysUserId.toString());
        mapList.put("methodKey", "listWechatInfo");
        jsonResult = SaaSApi.listWechatInfo(mapList);
        JSONObject j = JSONObject.fromObject(jsonResult);
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        JSONArray bb = (JSONArray)jj.get("rows");
        //获得appid、secret、originalId
        if(bb != null && bb.size() > 0){
            Map pMap = new HashMap();
            pMap.put("appid", ((Map)bb.get(0)).get("appId"));
            pMap.put("secret", ((Map)bb.get(0)).get("appSecret"));
            pMap.put("mediaId", params.get("mediaId"));
            apiRest = ProxyApi.proxyPost("out", "wechat", "pushMessage", pMap);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询微信信息失败！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 删除永久图文消息
     *
     * @param params
     * @return
     */
    public ApiRest deletePhotoArticle(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger sysUserId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        //查询wxWechatInfo列表
        String jsonResult = "";
        params.put("sysUserId", sysUserId);
        params.put("methodKey", "listWechatInfo");
        Map<String, String> mapList = new HashMap<String, String>();
        mapList.put("sysUserId", sysUserId.toString());
        mapList.put("methodKey", "listWechatInfo");
        jsonResult = SaaSApi.listWechatInfo(mapList);
        JSONObject j = JSONObject.fromObject(jsonResult);
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        JSONArray bb = (JSONArray)jj.get("rows");
        //获得appid、secret、originalId
        if(bb != null && bb.size() > 0){
            Map pMap = new HashMap();
            pMap.put("appid", ((Map)bb.get(0)).get("appId"));
            pMap.put("secret", ((Map)bb.get(0)).get("appSecret"));
            pMap.put("mediaId", params.get("mediaId"));
            WxMenu wxMenu = wechatMenuMapper.findWxMenuByMediaId(tenantId, params.get("mediaId").toString());
            if(wxMenu != null){
                apiRest.setIsSuccess(false);
                apiRest.setError("图文素材已经被微信菜单使用，不能删除！");
                return apiRest;
            }
            else{
                apiRest = ProxyApi.proxyPost("out", "wechat", "deletePhotoArticle", pMap);
                if(apiRest.getIsSuccess()){
                    apiRest = deletePhotoArticleLocal(params);
                }
            }
        }
        return apiRest;
    }

    /**
     * 删除永久图文本地存储
     *
     * @author szq
     */
    public ApiRest deletePhotoArticleLocal(Map params){
        ApiRest rest = new ApiRest();
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        WxNewsArticle wxNewsArticle = wechatReplyMapper.findNewsArticleById(id, tenantId);
        if(wxNewsArticle != null){
            wxNewsArticle.setDeleted(true);
            int i = wechatReplyMapper.newsArticleUpdate(wxNewsArticle);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("永久图文删除失败！");
                return rest;
            }
            rest.setData(wxNewsArticle);
            rest.setIsSuccess(true);
            rest.setMessage("永久图文删除成功");
        }
        else{
            rest.setIsSuccess(false);
            rest.setError("永久图文不存在或已被删除！");
        }
        return rest;
    }

    /**
     * 批量删除永久图片
     *
     * @author szq
     */
    public ApiRest deletePhotoLocal(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        net.sf.json.JSONArray jArray = net.sf.json.JSONArray.fromObject(params.get("list"));
        for(int i = 0; i < jArray.size(); i++){
            String mediaId = ((Map)jArray.get(i)).get("mediaId").toString();
            String imgId = ((Map)jArray.get(i)).get("imgId").toString();
            List<WxNewsArticle> wxNewsArticles = wechatReplyMapper.getNewArticle(tenantId, mediaId);
            if(wxNewsArticles.size() > 0){
                rest.setIsSuccess(false);
                rest.setMessage("永久图片已经被使用，不能删除");
                return rest;
            }
            WxResourceMedia wxResourceMedia = wechatReplyMapper.findResourceMediaById(imgId, tenantId);
            if(wxResourceMedia != null){
                Map sMap = new HashMap();
                sMap.put("appid", params.get("appId").toString());
                sMap.put("secret", params.get("secret").toString());
                sMap.put("mediaId", mediaId);
                ApiRest apiRest = ProxyApi.proxyPost("out", "wechat", "deletePhotoArticle", sMap);
                if(!apiRest.getIsSuccess()){
                    return apiRest;
                }
                wxResourceMedia.setDeleted(true);
                int j = wechatReplyMapper.resourceMediaUpdate(wxResourceMedia);
                if(j <= 0){
                    rest.setIsSuccess(false);
                    rest.setError("永久图片删除失败！");
                    return rest;
                }
                rest.setData(wxResourceMedia);
                rest.setIsSuccess(true);
                rest.setMessage("永久图片删除成功");
            }
            else{
                rest.setIsSuccess(false);
                rest.setError("永久图片不存在或已被删除");
            }
        }
        return rest;
    }

    /**
     * 保存图片所在为新服务器url
     *
     * @params 1.mediaId not null
     * 2.url   可以为空
     * 3.type   可以为空
     * @author szq
     */
    public ApiRest saveUrl(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        WxResourceMedia wxResourceImage = new WxResourceMedia();
        wxResourceImage.setTenantId(tenantId);
        if(params.get("url") != null && !params.get("url").equals("")){
            wxResourceImage.setUrl(params.get("url").toString());
        }
        wxResourceImage.setMediaId(params.get("mediaId").toString());
        if(params.get("type") != null && !params.get("type").equals("")){
            BigInteger type = BigInteger.valueOf(Long.parseLong((String)params.get("type")));
            wxResourceImage.setMediaType(type);
        }
        wxResourceImage.setLastUpdateBy("System");
        wxResourceImage.setLastUpdateAt(new Date());
        wxResourceImage.setCreateBy("System");
        wxResourceImage.setCreateAt(new Date());
        int i = wechatReplyMapper.resourceMediaInsert(wxResourceImage);
        if(i <= 0){
            rest.setIsSuccess(false);
            rest.setError("保存图片失败！");
            return rest;
        }
        rest.setData(wxResourceImage);
        rest.setIsSuccess(true);
        rest.setMessage("保存图片成功");
        return rest;
    }

    /**
     * 增加关键词回复
     *
     * @params 1.tenantId 商户id  不能为空
     * 2.content   消息内容  不能为空
     * 3.ruleName  规则名称  不能为空
     * 4.keyWord    关键词   不能为空
     */
    public ApiRest addKeywordMessage(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        WxReplyMessage wxReplyMessage = null;
        List<WxReplyMessage> wxReplyMessageList = wechatReplyMapper.getWxReplyMessage(tenantId, BigInteger.valueOf(3));
        if(wxReplyMessageList.size() > 0){
            wxReplyMessage = wxReplyMessageList.get(0);
        }
        else{
            wxReplyMessage = new WxReplyMessage();
            wxReplyMessage.setTenantId(tenantId);
            wxReplyMessage.setMessageType(BigInteger.valueOf(3));
            wxReplyMessage.setContent("");
            int i = wechatReplyMapper.replyMessageInsert(wxReplyMessage);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("保存消息回复失败！");
                return rest;
            }
        }

        WxReplyKeyRule wxReplyKeyRule1 = null;
        WxReplyKeyRule wxReplyKeyRule = wechatReplyMapper.getReplyKeyRuleByRuleName(tenantId, params.get("ruleName").toString());
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            if(wxReplyKeyRule != null && !Objects.equals(wxReplyKeyRule.getId(), id)){
                rest.setError("规则名称已经存在");
                rest.setIsSuccess(false);
                return rest;
            }
            wxReplyKeyRule1 = wechatReplyMapper.getReplyKeyRuleById(tenantId, id);
            wxReplyKeyRule1.setRuleName(params.get("ruleName").toString());
            wxReplyKeyRule1.setKeyWord(params.get("keyWord").toString());
            wxReplyKeyRule1.setContent(params.get("content").toString());
            wxReplyKeyRule1.setMessageId(wxReplyMessage.getId());
            wxReplyKeyRule1.setLastUpdateAt(new Date());
            wxReplyKeyRule1.setLastUpdateBy("System");
            int i = wechatReplyMapper.replyKeyRuleUpdate(wxReplyKeyRule1);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("信息回复保存失败！");
                return rest;
            }
            rest.setData(wxReplyKeyRule1);
            rest.setMessage("信息回复保存成功");
            rest.setIsSuccess(true);
            return rest;
        }
        else{
            if(wxReplyKeyRule != null){
                rest.setError("规则名称已经存在");
                rest.setIsSuccess(false);
                return rest;
            }
            wxReplyKeyRule1 = new WxReplyKeyRule();
            wxReplyKeyRule1.setTenantId(tenantId);
            wxReplyKeyRule1.setRuleName(params.get("ruleName").toString());
            wxReplyKeyRule1.setKeyWord(params.get("keyWord").toString());
            wxReplyKeyRule1.setContent(params.get("content").toString());
            wxReplyKeyRule1.setMessageId(wxReplyMessage.getId());
            wxReplyKeyRule1.setCreateAt(new Date());
            wxReplyKeyRule1.setCreateBy("System");
            wxReplyKeyRule1.setLastUpdateAt(new Date());
            wxReplyKeyRule1.setLastUpdateBy("System");
            int i = wechatReplyMapper.replyKeyRuleInsert(wxReplyKeyRule1);
            if(i <= 0){
                rest.setIsSuccess(false);
                rest.setError("信息回复保存失败！");
                return rest;
            }
            rest.setData(wxReplyKeyRule1);
            rest.setMessage("信息回复保存成功");
            rest.setIsSuccess(true);
            return rest;
        }
    }

    /**
     * 删除消息回复
     *
     * @author szq
     */
    public ApiRest delMessage(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));

        if("3".equals(params.get("type"))){
            WxReplyKeyRule wxReplyKeyRule = wechatReplyMapper.getReplyKeyRuleById(tenantId, id);
            if(wxReplyKeyRule == null){
                rest.setError("信息回复不存在或已被删除");
                rest.setIsSuccess(false);
                return rest;
            }
            else{
                wxReplyKeyRule.setDeleted(true);
                wxReplyKeyRule.setLastUpdateAt(new Date());
                wxReplyKeyRule.setLastUpdateBy("System");
                int i = wechatReplyMapper.replyKeyRuleUpdate(wxReplyKeyRule);
                if(i <= 0){
                    rest.setError("信息回复删除失败");
                    rest.setIsSuccess(false);
                    return rest;
                }
                rest.setMessage("信息回复删除成功");
                rest.setIsSuccess(true);
                return rest;
            }
        }
        else{
            WxReplyMessage wxReplyMessage = wechatReplyMapper.getById(tenantId, id);
            if(wxReplyMessage == null){
                rest.setError("信息回复不存在或已被删除");
                rest.setIsSuccess(false);
                return rest;
            }
            else{
                wxReplyMessage.setContent("");
                int i = wechatReplyMapper.replyMessageUpdate(wxReplyMessage);
                if(i <= 0){
                    rest.setError("信息回复删除失败");
                    rest.setIsSuccess(false);
                    return rest;
                }
                rest.setMessage("信息回复删除成功");
                rest.setIsSuccess(true);
                return rest;
            }
        }
    }

    /**
     * 查询消息
     *
     * @return
     * @params 1.tenantId not null
     * 2.messageType  消息类型  1：关注回复 2：消息自动回复 3：关键词回复
     * @author szq
     */
    public ApiRest listMessage(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        List<WxReplyKeyRule> wxReplyKeyRuleList;
        List<WxReplyMessage> wxReplyMessageList;
        if("3".equals(params.get("messageType"))){
            //String sql="select * from wx_reply_key_rule where tenant_id="+tenantId+" and is_deleted=0";
            wxReplyKeyRuleList = wechatReplyMapper.getKeyRuleList(tenantId);
            rest.setData(wxReplyKeyRuleList);
        }
        else{
            BigInteger messageType = BigInteger.valueOf(Long.parseLong((String)params.get("messageType")));
            wxReplyMessageList = wechatReplyMapper.getWxReplyMessage(tenantId, messageType);
            rest.setData(wxReplyMessageList);
        }
        rest.setMessage("消息内容查询成功");
        rest.setIsSuccess(true);
        return rest;
    }

    /**
     * 根据图文消息的mediaId查询图文消息
     *
     * @return
     * @params 1.mediaId
     * 2.tenantId
     * @author szq
     */
    public ApiRest findPhotoArticleByMediaId(Map params){
        ApiRest rest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String mediaId = params.get("mediaId").toString();
        WxNewsArticle wxNewsArticle = wechatReplyMapper.findNewsArticleByMediaId(mediaId, tenantId);
        rest.setData(wxNewsArticle);
        rest.setIsSuccess(true);
        rest.setMessage("图文素材查询成功");
        return rest;
    }
}
