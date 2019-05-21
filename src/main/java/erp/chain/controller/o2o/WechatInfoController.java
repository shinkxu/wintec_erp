package erp.chain.controller.o2o;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import erp.chain.domain.o2o.WxCard;
import erp.chain.mapper.CardMapper;
import erp.chain.service.o2o.CardService;
import erp.chain.service.o2o.WechatInfoService;
import erp.chain.utils.AESUtils;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.O2OApi;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/27
 */
@Controller
@ResponseBody
@RequestMapping("/wechatInfo")
public class WechatInfoController{
    @Autowired
    private WechatInfoService wechatInfoService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CardMapper cardMapper;
    /**
     * 查询微信配置信息
     * @return
     */
    @RequestMapping("/wechatInfo")
    public String wechatInfo() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
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
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 创建微信会员卡
     * @return
     */
    @RequestMapping(value = "/createWxVipCard")
    public String createWxVipCard(){
        ApiRest rest = new ApiRest();
        try {
            //查询微信公众号配置
            Map<String,String> params = AppHandler.params();
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

            //查询商户信息
            ApiRest rest3 = SaaSApi.findTenantById(new BigInteger(params.get("tenantId")));
            Map tenant = (Map)((Map)rest3.getData()).get("tenant");

            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if(token != null){
                    Map<String,String> paramsInfo = new HashMap<>();
                    JSONObject memberCard = new JSONObject();
                    memberCard.put("background_pic_url","https://mmbiz.qlogo.cn/mmbiz/");

                    JSONObject baseInfo = new JSONObject();
                    baseInfo.put("logo_url","http://mmbiz.qpic.cn/mmbiz/iaL1LJM1mF9aRKPZ/0");
                    if(params.get("branchName") != null){
                        baseInfo.put("brand_name",params.get("branchName"));
                    } else {
                        baseInfo.put("brand_name","");
                    }
                    baseInfo.put("code_type","CODE_TYPE_QRCODE");
                    baseInfo.put("title","微信会员卡");
                    baseInfo.put("color","Color010");
                    baseInfo.put("notice","提示：使用时向服务员出示此券");
                    if(tenant != null && tenant.get("phoneNumber") != null){
                        baseInfo.put("service_phone",tenant.get("phoneNumber"));
                    } else {
                        baseInfo.put("service_phone","");
                    }
                    baseInfo.put("description","描述：不可与其他优惠同享");
                    baseInfo.put("date_info","{\"type\":\"DATE_TYPE_PERMANENT\"}");
                    baseInfo.put("sku","{\"quantity\":50000000}");
                    /**
                     * 每人可领券的数量限制，建议会员卡每人限领一张
                     */
                    baseInfo.put("get_limit",1);
                    baseInfo.put("use_custom_code",false);
                    baseInfo.put("can_give_friend",true);
                    /**
                     * 机构位置ID。调用POI机构管理接口获取机构位置ID。
                     */
//            baseInfo.put("location_id_list","[123,12321]");
                    baseInfo.put("custom_url_name","立即使用");
                    baseInfo.put("custom_url","http://weixin.qq.com");
                    baseInfo.put("custom_url_sub_title","6个汉字tips");
                    baseInfo.put("promotion_url_name","营销入口1");
                    baseInfo.put("promotion_url","http://www.qq.com");
                    baseInfo.put("need_push_on_view",true);

                    memberCard.put("base_info",baseInfo);
                    /**
                     * 卡券高级信息（非必填）
                     */
//            JSONObject advancedInfo = new JSONObject();
                    /**
                     *显示积分，填写true或false，如填写true，积分相关字段均为必填
                     *若设置为true则后续不可以被关闭。
                     */
                    memberCard.put("supply_bonus",false);
                    /**
                     * 若设置为true则后续不可以被关闭。该字段须开通储值功能后方可使用
                     */
                    memberCard.put("supply_balance",false);
                    /**
                     * 会员卡特权说明,限制1024汉字。
                     */
                    memberCard.put("prerogative","会员卡特权说明,限制1024汉字。");
                    /**
                     * 设置为true时用户领取会员卡后系统自动将其激活，无需调用激活接口
                     */
//                    memberCard.put("auto_activate",false);
                    /**
                     * 自定义会员信息类目，会员卡激活后显示,包含name_type(name)和url字段
                     */
//                    memberCard.put("custom_field1","{\n" +
//                            "                \"name_type\": \"FIELD_NAME_TYPE_LEVEL\",\n" +
//                            "                \"url\": \"http://www.qq.com\"\n" +
//                            "            }");
                    /**
                     * 激活会员卡的url。
                     */

                    memberCard.put("wx_activate", true);
                    memberCard.put("wx_activate_after_submit", true);
                    memberCard.put("wx_activate_after_submit_url", "http://fenjiu.vip.smartpos.top/zd1/ct/frontTenantVip/checkVip");

//                    memberCard.put("activate_url", "https://qq.com");
                    /**
                     * 自定义会员信息类目，会员卡激活后显示。
                     */
                    memberCard.put("custom_cell1","{\n" +
                            "                \"name\": \"使用入口2\",\n" +
                            "                \"tips\": \"激活后显示\",\n" +
                            "                \"url\": \"http://www.xxx.com\"\n" +
                            "            }");
                    /**
                     * 积分规则。
                     */
                    memberCard.put("bonus_rule","{\n" +
                            "                \"cost_money_unit\": 100,\n" +
                            "                \"increase_bonus\": 1,\n" +
                            "                \"max_increase_bonus\": 200,\n" +
                            "                \"init_increase_bonus\": 10,\n" +
                            "                \"cost_bonus_unit\": 5,\n" +
                            "                \"reduce_money\": 100,\n" +
                            "                \"least_money_to_use_bonus\": 1000,\n" +
                            "                \"max_reduce_bonus\": 50\n" +
                            "            }");
                    memberCard.put("discount",10);

                    JSONObject card = new JSONObject();
                    card.put("card_type","MEMBER_CARD");
                    card.put("member_card",memberCard);

                    JSONObject postJson = new JSONObject();
                    postJson.put("card",card);

                    String url = "https://api.weixin.qq.com/card/create?access_token=" + token;

                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",postJson.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("errmsg"))){
                            rest.setIsSuccess(true);
                            rest.setMessage("创建会员卡成功，请等待审核结果，审核需要三个工作日。");
                            rest.setData(map.get("card_id"));
                            params.put("cardId", map.get("card_id").toString());
                            params.put("checkStatus","2");
                            cardService.addWxCard(params);
                        } else {
                            rest.setIsSuccess(false);
                            rest.setMessage("创建会员卡失败！" + map.get("errmsg"));
                            LogUtil.logError("创建会员卡失败：" + map.get("errmsg"));
                        }
                    }
                }
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("请前往微信设置页面配置微信公众号信息！");
            }

        } catch (Exception e) {
            LogUtil.logError("创建微信会员卡异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 生成微信会员领取二维码
     * @return
     */
    @RequestMapping("/createWxVipQrcode")
    public String createWxVipQrcode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            //查询微信公众号配置
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
            Map<String,String> paramsInfo = new HashMap<>();
            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if (token != null) {
                    JSONObject postJson = new JSONObject();
                    postJson.put("action_name","QR_CARD");

                    JSONObject card = new JSONObject();
                    if(params.get("cardId") != null){
                        card.put("card_id",params.get("cardId"));
                    }
                    /**
                     * 维码，生成的二维码随机分配一个code，领取后不可再次扫描。填写true或false。默认false
                     */
                    card.put("is_unique_code",false);
                    /**
                     * outer_id字段升级版本，字符串类型，用户首次领卡时，会通过领取事件推送给商户；
                     * 对于会员卡的二维码，用户每次扫码打开会员卡后点击任何url，会将该值拼入url中，方便开发者定位扫码来源
                     */
                    card.put("outer_str","12b");

                    JSONObject actionInfo = new JSONObject();
                    actionInfo.put("card",card);

                    postJson.put("action_info",actionInfo);

                    String url = "https://api.weixin.qq.com/card/qrcode/create?access_token=" + token;
                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",postJson.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("errmsg")) && map.get("url") != null){
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("查询二维码链接成功！");
                            apiRest.setData(map.get("show_qrcode_url"));
                            params.put("cardId", params.get("cardId"));
                            params.put("qrCode",map.get("show_qrcode_url").toString());
                            cardService.updateWxCard(params);
                        } else {
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("查询二维码链接成失败！");
                        }
                    }
                }
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("请先创建会员卡！");
            }

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 查询微信卡券详情
     * @return
     */
    @RequestMapping("/getWxCardDetail")
    public String getWxCardDetail(){
        String appid = "wx1e7e3a6947c427a0";
        String secret = "c971261c05893acd2e229a768df64e46";
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Map<String,String> paramsInfo = new HashMap<>();
            ApiRest baseRest = O2OApi.askBaseAccessToken(appid, secret);
            if(Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if (token != null) {

                    JSONObject postJson = new JSONObject();
                    postJson.put("card_id","pb5T8v0YxNxxQgjY2gWRdhPBZEl8");

                    String url = "https://api.weixin.qq.com/card/get?access_token=" + token;
                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",postJson.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                    }
                }
            }

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 开通支付即会员
     * @return
     */
    @RequestMapping("/payGiftCard")
    public String payGiftCard(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            //查询微信公众号配置
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
            ApiRest apiRest1 = wechatInfoService.wechatInfo(params);
            Map wechatInfo = null;
            if(apiRest1.getIsSuccess() && apiRest1.getData() != null){
                List list = (List)((Map)apiRest1.getData()).get("rows");
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

            Map<String,String> paramsInfo = new HashMap<>();
            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if (token != null) {

                    JSONObject postJson = new JSONObject();
                    JSONObject ruleInfo = new JSONObject();
                    ruleInfo.put("type","RULE_TYPE_PAY_MEMBER_CARD");
                    JSONObject baseInfo = new JSONObject();
                    if(wechatInfo.get("mchId") != null){
                        baseInfo.put("mchid_list","["+ wechatInfo.get("mchId") + "]");
                    }
                    baseInfo.put("begin_time",1480317217);
                    baseInfo.put("end_time",1580317217);
                    ruleInfo.put("base_info",baseInfo);

                    JSONObject memberRule = new JSONObject();
                    memberRule.put("card_id",params.get("cardId"));
                    /**
                     * 单次消费送会员卡的金额下限
                     ，以分为单位
                     */
                    memberRule.put("least_cost",1);
                    /**
                     * 单次消费送会员卡的金额上限
                     ，以分为单位
                     */
                    memberRule.put("max_cost",100000);
                    /**
                     * 自定义领取卡券页面
                     */
//                    memberRule.put("jump_url","www.qq.com");
                    ruleInfo.put("member_rule",memberRule);
                    postJson.put("rule_info",ruleInfo);
                    String url = "https://api.weixin.qq.com/card/paygiftcard/add?access_token=" + token;
                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",postJson.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("errmsg")) && map.get("rule_id") != null){
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("开通支付即会员成功！");
                            apiRest.setData(map.get("succ_mchid_list"));
                            params.put("payGiftCard", "1");
                            if(map.get("rule_id") != null){
                                params.put("ruleId",map.get("rule_id").toString());
                            }
                            cardService.updateWxCard(params);
                        } else {
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("开通支付即会员失败:" + map.get("errmsg"));
                            LogUtil.logError("开通支付即会员失败：" + rest1.getData());
                        }

                    }
                }
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("开通支付即会员失败：微信公众号信息配置不正确！");
            }

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 关闭支付即会员
     * @return
     */
    @RequestMapping("/stopPayGiftCard")
    public String stopPayGiftCard(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            //查询微信公众号配置
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
            if (params.get("cardId")==null||params.get("cardId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数cardId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            ApiRest apiRest1 = wechatInfoService.wechatInfo(params);
            Map wechatInfo = null;
            if(apiRest1.getIsSuccess() && apiRest1.getData() != null){
                List list = (List)((Map)apiRest1.getData()).get("rows");
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
            Map<String,String> paramsInfo = new HashMap<>();
            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if (token != null) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("cardId",params.get("cardId"));
                    WxCard wxCard = cardMapper.findWxCard(map1);
                    if(wxCard != null && wxCard.getRuleId() != null){
                        JSONObject postJson = new JSONObject();
                        postJson.put("rule_id",wxCard.getRuleId());
                        String url = "https://api.weixin.qq.com/card/paygiftcard/delete?access_token=" + token;
                        paramsInfo.put("baseUrl",url);
                        paramsInfo.put("body",postJson.toString());

                        ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                        String result = "";
                        if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                            result = rest1.getData().toString();

                            ObjectMapper objectMapper = new ObjectMapper();
                            Map map = objectMapper.readValue(result, Map.class);
                            if("ok".equals(map.get("errmsg"))){
                                apiRest.setIsSuccess(true);
                                apiRest.setMessage("关闭支付即会员成功！");
                                wxCard.setPayGiftCard(false);
                                cardMapper.updateWxCard(wxCard);
                                LogUtil.logInfo(map.toString());
                            } else {
                                apiRest.setIsSuccess(false);
                                apiRest.setMessage("关闭支付即会员失败:" + map.get("errmsg"));
                            }

                        }
                    }
                }
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("关闭支付即会员失败：微信公众号信息配置不正确！");
            }

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取群发卡券content
     * @return
     */
    @RequestMapping("/getSendContent")
    public String getSendContent(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            //查询微信公众号配置
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
            if (params.get("cardId")==null||params.get("cardId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数cardId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            ApiRest apiRest1 = wechatInfoService.wechatInfo(params);
            Map wechatInfo = null;
            if(apiRest1.getIsSuccess() && apiRest1.getData() != null){
                List list = (List)((Map)apiRest1.getData()).get("rows");
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

            Map<String,String> paramsInfo = new HashMap<>();
            if(baseRest != null && Constants.REST_RESULT_SUCCESS.equals(baseRest.getResult())) {
                String token = String.valueOf(baseRest.getData());
                if (token != null) {

                    JSONObject postJson = new JSONObject();
                    postJson.put("card_id",params.get("cardId"));
                    String url = "https://api.weixin.qq.com/card/mpnews/gethtml?access_token=" + token;
                    paramsInfo.put("baseUrl",url);
                    paramsInfo.put("body",postJson.toString());

                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlBody", paramsInfo);
                    String result = "";
                    if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("errmsg")) && map.get("content") != null){
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("获取群发content成功！");
                            apiRest.setData(map.get("content"));
                        } else {
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("获取群发content失败:" + map.get("errmsg"));
                            LogUtil.logError("获取群发content失败：" + rest1.getData());
                        }

                    }
                }
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("获取群发content失败：微信公众号信息配置不正确！");
            }

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }



    /**
     * 查询微信会员卡信息（库存）
     * @return
     */
    @RequestMapping("/getWxCard")
    public String getWxCard(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null){
                apiRest = cardService.getWxCard(params);
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("tenantId不能为空！");
            }
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }



    /**
     * 加密
     * @return
     */
    @RequestMapping("/testAES")
    public String testAES(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String moon = "月亮那么大！";
//            byte[] key = Base64.encodeInteger(new BigInteger("7788"));
            String mi = AESUtils.encrypt(moon,"3322");

            System.out.println(mi);

            String moon1 = AESUtils.decrypt(mi,"3322");

            System.out.println(moon1);

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


}
