package erp.chain.controller.o2o;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.o2o.DietOrderInfoMapper;
import erp.chain.service.o2o.MeituanService;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;
import saas.api.util.ApiWebUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 美团
 * Created by wangms on 2017/3/13.
 */
@Controller
@ResponseBody
@RequestMapping("/meiTuan")
public class MeiTuanController{
    @Autowired
    private MeituanService meituanService;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    private static final String MEI_TUAN_SERVICE_URL = ConfigurationUtils.getConfigurationSafe(Constants.MEI_TUAN_SERVICE_URL);

    /**
     * 机构绑定回调
     */
    @RequestMapping("/branchCallback")
    public String branchCallback(){
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try{
            LogUtil.logInfo("美团机构绑定回调参数：" + requestParameters);
            String ePoiId = requestParameters.get("ePoiId");

            String appAuthToken = requestParameters.get("appAuthToken");
            String businessId = requestParameters.get("businessId");
            String meiTuanPoiId = requestParameters.get("poiId");
            String meiTuanPoiName = requestParameters.get("poiName");
            meituanService.combineBranch(ePoiId.substring(ePoiId.indexOf("Z") + 1), appAuthToken, businessId, meiTuanPoiId, meiTuanPoiName);
        }
        catch(Exception e){
            LogUtil.logError("美团机构绑定回调异常:" + e.getMessage());
        }
        return "{data:\"success\"} ";
    }

    /**
     * 机构解绑回调
     */
    @RequestMapping("/branchUnbundingCallback")
    public String branchUnbundingCallback(){
        Map<String, String> params = AppHandler.params();
        try{
            LogUtil.logInfo("美团机构解绑回调!");
            LogUtil.logInfo("ePoiId:" + params.get("ePoiId"));
            LogUtil.logInfo("businessId:" + params.get("businessId"));
            String ePoiId = params.get("ePoiId");
            meituanService.unbundingBranch(ePoiId.substring(ePoiId.indexOf("Z") + 1), params.get("businessId"));
        }
        catch(Exception e){
            LogUtil.logError("美团机构绑定回调异常:" + e.getMessage());
        }
        return "{data:\"success\"} ";
    }

    /**
     * 接收订单(回调类)
     */
    @RequestMapping("/receiveOrder")
    public String receiveOrder(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团订单***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("order") != null){
                LogUtil.logInfo("接收美团订单!" + params.get("order"));
                String developerId = params.get("developerId");
                String ePoiId = params.get("ePoiId");
                String order = params.get("order");
                order = removeNonBmpUnicode(order);
                if(order != null){
                    order = order.replace("@", "");
                    order = order.replace("#", "");
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(order, Map.class);
                if(map != null && map.get("orderId") != null){
                    meituanService.mapDietOrder(map);
                }
            }
            else{
                LogUtil.logInfo("接收美团订单：order为空。(心跳检测)");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }

    public static String removeNonBmpUnicode(String str){
        if(str == null){
            return null;
        }
        str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
        return str;
    }

    /**
     * 接收订单已确认(回调类)
     */
    @RequestMapping("/receiveOrderConfirm")
    public String receiveOrderConfirm(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团订单确认***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("order") != null){
                LogUtil.logInfo("接收美团订单确认!" + params.get("order"));
                String order = params.get("order");
                if(order != null){
                    order = order.replace("@", "");
                    order = order.replace("#", "");
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(order, Map.class);
                LogUtil.logInfo("美团订单转Map" + GsonUntil.toJson(map));
                if(map != null && map.get("orderId") != null){
                    meituanService.orderConfirm(map);
                }
            }
            else{
                LogUtil.logInfo("接收美团订单确认：order为空。(心跳检测)");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }

    /**
     * 接收订单取消(回调类)
     */
    @RequestMapping("/receiveOrderCancel")
    public String receiveOrderCancel(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团取消订单通知***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("orderCancel") != null){
                LogUtil.logInfo("接收美团订单取消!");
                String developerId = params.get("developerId");
                String ePoiId = params.get("ePoiId");
                String orderCancel = params.get("orderCancel");
                LogUtil.logError("美团取消订单通知:" + orderCancel);
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(orderCancel, Map.class);
                if(map.get("orderId") != null){
                    //修改订单状态
                    meituanService.cancelOrder("M" + map.get("orderId"));
                }
            }
            else{
                LogUtil.logError("接收美团取消订单异常：orderCancel为空。");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }

    /**
     * 接收订单配送状态(回调类)
     */
    @RequestMapping("/receiveShippingStatus")
    public String receiveShippingStatus(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团订单配送状态***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("shippingStatus") != null){
                String developerId = params.get("developerId");
                String ePoiId = params.get("ePoiId");
                String shippingStatus = params.get("shippingStatus");
                LogUtil.logError("美团订单配送状态:" + shippingStatus);
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(shippingStatus, Map.class);
                if(map.get("orderId") != null){
                    //修改订单状态
                    meituanService.receiveShippingStatus("M" + map.get("orderId"), map);
                }
            }
            else{
                LogUtil.logError("接收美团订单配送状态：shippingStatus为空。");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }


    /**
     * 接收订单退款(回调类)
     */
    @RequestMapping("/receiveOrderRefund")
    public String receiveOrderRefund(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团订单退款通知***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("orderRefund") != null){
                String developerId = params.get("developerId");
                String ePoiId = params.get("ePoiId");
                String orderRefund = params.get("orderRefund");
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(orderRefund, Map.class);
                String reason = null;
                if(map.get("reason") != null){
                    reason = map.get("reason").toString();
                }
                if(map.get("orderId") != null){
                    if("reject".equals(map.get("notifyType"))){
                        //修改支付状态 4:拒绝退款 订单状态 10：用户撤单
                        meituanService.updatePayStatus("M" + map.get("orderId"), 4, 10, reason);
                    }
                    else if("agree".equals(map.get("notifyType"))){
                        //修改支付状态 3:已退款退款 订单状态 10：用户撤单
                        meituanService.updatePayStatus("M" + map.get("orderId"), 3, 10, reason);
                    }
                    else if("cancelRefund".equals(map.get("notifyType"))){
                        //先查询订单状态
                        Branch branch = meituanService.getBranchInfo(ePoiId);
                        //默认2已接单
                        String orderStatus = "2";
                        if(branch != null){
                            String status = queryOrder(branch.getMeituanToken(), map.get("orderId").toString());
                            if(status != null){
                                if("3".equals(status)){
                                    orderStatus = "1";
                                }
                                else if("4".equals(status)){
                                    orderStatus = "2";
                                }
                                else if("6".equals(status)){
                                    orderStatus = "4";
                                }
                            }
                        }
                        //用户取消申请退款
                        meituanService.updatePayStatus("M" + map.get("orderId"), 1, Integer.parseInt(orderStatus), reason);
                    }
                    else if("apply".equals(map.get("notifyType"))){
                        meituanService.updatePayStatus("M" + map.get("orderId"), 2, 10, reason);
                    }

                }
                LogUtil.logInfo("美团订单退款通知:" + orderRefund);
            }
            else{
                LogUtil.logError("接收美团订单退款通知异常：orderRefund为空。");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }

    /**
     * 根据订单Id查询订单
     */
    public String queryOrder(String appAuthToken, String mOrderId){
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String timestamp = String.valueOf(new Date().getTime());
            //认领机构返回的token【一店一token】
            paramsInfo.put("appAuthToken", appAuthToken);
            //交互数据的编码
            paramsInfo.put("charset", "UTF-8");
            //订单号
            paramsInfo.put("orderId", mOrderId);
            //当前请求的时间戳【单位是秒】
            paramsInfo.put("timestamp", timestamp);
            //请求的数字签名
            String sign = SHA1Utils.SHA1(paramsInfo);
            paramsInfo.put("sign", sign);
            String baseUrl = MEI_TUAN_SERVICE_URL + "/waimai/order/queryById";
            Map pMap = new HashMap();
            pMap.put("baseUrl", baseUrl);
            pMap.put("paramMap", paramsInfo);
            String result = "";
            ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
            if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                result = rest1.getData().toString();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(result, Map.class);
            if(map.get("data") != null){
                return ((Map)map.get("data")).get("status").toString();
            }
        }
        catch(Exception e){
            LogUtil.logError(e.getMessage());
        }
        return null;
    }

    public Map queryOrderAll(String appAuthToken, String mOrderId){
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String timestamp = String.valueOf(new Date().getTime());
            //认领机构返回的token【一店一token】
            paramsInfo.put("appAuthToken", appAuthToken);
            //交互数据的编码
            paramsInfo.put("charset", "UTF-8");
            //订单号
            paramsInfo.put("orderId", mOrderId);
            //当前请求的时间戳【单位是秒】
            paramsInfo.put("timestamp", timestamp);
            //请求的数字签名
            String sign = SHA1Utils.SHA1(paramsInfo);
            paramsInfo.put("sign", sign);
            String baseUrl = MEI_TUAN_SERVICE_URL + "/waimai/order/queryById";
            Map pMap = new HashMap();
            pMap.put("baseUrl", baseUrl);
            pMap.put("paramMap", paramsInfo);
            String result = ApiWebUtils.doPost(baseUrl, paramsInfo, 12000, 12000);//ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
            JSONObject json = JSONObject.fromObject(result);
            if(json != null && json.get("data") != null){
                return (Map)json.get("data");
            }
            /*ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(result, Map.class);
            if(map.get("data") != null){
                return ((Map)map.get("data"));
            }*/
        }
        catch(Exception e){
            LogUtil.logError(e.getMessage());
        }
        return null;
    }

    /**
     * 纠正income
     *
     * @return
     */
    @RequestMapping("/correctIncome")
    public String correctIncome(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********纠正income***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            Map<String, Object> map = new HashMap<>();
            List<DietOrderInfo> orderInfos = dietOrderInfoMapper.selectOrderForIncome(map);
            if(orderInfos != null && orderInfos.size() > 0){

                for(DietOrderInfo dietOrderInfo : orderInfos){
                    String ePoiId = dietOrderInfo.getBranchId().toString();
                    Branch branch = meituanService.getBranchInfo(ePoiId);
                    if(branch != null && branch.getMeituanToken() != null && !branch.getMeituanToken().equals("")){
                        Map meituanOrder = queryOrderAll(branch.getMeituanToken(), dietOrderInfo.getOrderCode().substring(1));
                        //记录店铺实收金额
                        if(meituanOrder != null && meituanOrder.get("poiReceiveDetail") != null){
                            ObjectMapper objectMapper = new ObjectMapper();
                            Map poiReceiveDetail = objectMapper.readValue(meituanOrder.get("poiReceiveDetail").toString(), Map.class);
                            if(poiReceiveDetail.get("wmPoiReceiveCent") != null){
                                dietOrderInfo.setIncome(new BigDecimal(poiReceiveDetail.get("wmPoiReceiveCent").toString()).multiply(new BigDecimal("0.01")));
                                dietOrderInfo.setLastUpdateAt(new Date());
                                dietOrderInfoMapper.update(dietOrderInfo);
                            }
                        }

                    }
                }

            }

        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }


    /**
     * 接收订单完成(回调类)
     */
    @RequestMapping("/receiveOrderFinish")
    public String receiveOrderFinish(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收美团订单完成通知***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            if(params.get("order") != null){
                String developerId = params.get("developerId");
                String ePoiId = params.get("ePoiId");
                String order = params.get("order");
                LogUtil.logInfo("美团订单完成通知:" + order);
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(order, Map.class);
                if(map.get("orderId") != null){
                    //修改订单状态
                    meituanService.updatePayStatus("M" + map.get("orderId"), -1, 6, null);
                }

            }
            else{
                LogUtil.logError("接收美团订单完成通知异常：order为空。");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }

    /**
     * 接受闪惠订单推送
     *
     * @return
     */
    @RequestMapping("/receiveShanhuiOrder")
    public String receiveShanhuiOrder(){
        Map<String, String> params = AppHandler.params();
        LogUtil.logInfo("*********接收闪惠订单推送***********");
        String backWord = "{\"data\":\"OK\"}";
        try{
            String developerId = params.get("developerId");
            if(params.get("params") != null){
                String mParams = params.get("params");
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(mParams, Map.class);
                LogUtil.logInfo("接收闪惠订单推送:" + mParams);
                //处理闪惠订单状态
                if(map.get("vendorOrderId") != null){
//                    meituanService.dealShanhuiOrder(map);
                }

            }
            else{
                LogUtil.logError("接收闪惠订单推送异常：params为空。");
            }
        }
        catch(Exception e){
            LogUtil.logError(e, params);
        }
        return backWord;
    }
//
//    /**
//     * 心跳：通用上报接口
//     */
//    def meituanHeartbeat(){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,String> paramsInfo = new HashMap<String,String>();
//            def ePoiId = params.branchId + "Z" + params.tenantId;
//            def posId = params.posId;
//            def developerId = "100139";
//            if(ePoiId != null && posId != null){
//                String timestamp = String.valueOf(new Date().getTime());
//                //当前请求的时间戳【单位是毫秒】
//
//                JSONObject data = new JSONObject();
//                data.put("ePoiId",ePoiId);
//                data.put("developerId",developerId);
//                data.put("time",timestamp);
//                data.put("posId",posId);
//
//                paramsInfo.put("data",data.toString());
//
//                //请求的数字签名
//                String sign = SHA1Utils.SHA1(paramsInfo);
//                paramsInfo.put("sign",sign);
//                StringBuffer baseUrl = new StringBuffer("http://heartbeat.meituan.com/pos/heartbeat?");
//                String result = "";
//                Map pMap = new HashMap();
//                pMap.put("baseUrl",combineParamsAsUrl(baseUrl.toString(),paramsInfo));
//                ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//                if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                    result = rest1.data;
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Map map = objectMapper.readValue(result, Map);
//                    if("成功".equals(map.get("message"))){
//                        rest.isSuccess = true;
//                        //修改订单状态 2:已确认
//                        rest.message = "（心跳）通用上报成功!";
//                    }else{
//                        rest.isSuccess = false;
//                        rest.message = map.get("error").message;
//                    }
//                } else {
//                    rest.isSuccess = false;
//                    rest.message = "访问http失败!";
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "机构id或posId不存在!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("（心跳）通用上报异常:" + e.message);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }
//    /**
//     * 补充数据上报接口
//     */
//    def meituanHeartbeatExtra(){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,String> paramsInfo = new HashMap<String,String>();
//            def ePoiId = params.branchId + "Z" + params.tenantId;
//            def posId = params.posId;
//            def developerId = "100139";
//            if(ePoiId != null && posId != null){
//                String timestamp = String.valueOf(new Date().getTime());
//                //当前请求的时间戳【单位是毫秒】
//
//                JSONObject data = new JSONObject();
//                data.put("ePoiId",ePoiId);
//                data.put("developerId",developerId);
//                data.put("time",timestamp);
//                data.put("posId",posId);
//
//                paramsInfo.put("data",data.toString());
//
//                //请求的数字签名
//                String sign = SHA1Utils.SHA1(paramsInfo);
//                paramsInfo.put("sign",sign);
//                StringBuffer baseUrl = new StringBuffer("http://heartbeat.meituan.com/pos/heartbeat?");
//                String result = "";
//                Map pMap = new HashMap();
//                pMap.put("baseUrl",combineParamsAsUrl(baseUrl.toString(),paramsInfo));
//                ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//                if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                    result = rest1.data;
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Map map = objectMapper.readValue(result, Map);
//                    if("成功".equals(map.get("message"))){
//                        rest.isSuccess = true;
//                        //修改订单状态 2:已确认
//                        rest.message = "（心跳）通用上报成功!";
//                    }else{
//                        rest.isSuccess = false;
//                        rest.message = map.get("error").message;
//                    }
//                } else {
//                    rest.isSuccess = false;
//                    rest.message = "访问http失败!";
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "机构id或posId不存在!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("（心跳）通用上报异常:" + e.message);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }


    /**
     * 机构置营业
     *
     * @return
     */
    @RequestMapping("/openBranch")
    public String openBranch(){
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/poi/open?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("OK".equals(map.get("data")) || "ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        //修改订单状态 2:已确认
                        rest.setMessage("机构置营业成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("机构置营业异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

//    /**
//     * 根据流水号查询订单
//     */
//    def queryByDaySeq(){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,String> paramsInfo = new HashMap<String,String>();
//            //认领机构返回的token【一店一token】
//            String appAuthToken = "";
//            paramsInfo.put("appAuthToken",appAuthToken);
//            //交互数据的编码
//            paramsInfo.put("charset","UTF-8")
//            //当前请求的时间戳【单位是秒】
//            paramsInfo.put("timestamp","0");
//            //机构号
//            String ePoiId = "";
//            paramsInfo.put("ePoiId",ePoiId);
//            //机构下的订单流水号
//            paramsInfo.put("daySeq","");
//            //日期【yyyy-MM-dd】
//            paramsInfo.put("date","");
//            //请求的数字签名
//            String sign = SHA1Utils.SHA1(paramsInfo);
//            paramsInfo.put("sign",sign);
//            String baseUrl = "http://api.open.cater.meituan.com/waimai/order/queryByDaySeq";
//            Map pMap = new HashMap();
//            pMap.put("baseUrl",baseUrl);
//            pMap.put("paramMap",paramsInfo);
//            String result = "";
//            ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//            if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                result = rest1.data;
//            }
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map map = objectMapper.readValue(result, Map);
//            Set keys = map.keySet();
//            Iterator iterator = keys.iterator();
//            Object key = iterator.next();
//            if(key == "error"){
//                rest.isSuccess = false
//                rest.message = "根据流水号查询订单失败";
//            }else{
//                meituanService.mapDietOrder(map);
//                rest.isSuccess = true;
//                rest.message = "根据流水号查询订单成功";
//            }
//        } catch (Exception e) {
//            LogUtil.logError(e, params);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }

    /**
     * 商家确认订单
     *
     * @return
     */
    @RequestMapping("/confirmOrder")
    public String confirmOrder(){
        LogUtil.logInfo("*************商家确认订单：开始时间：" + System.currentTimeMillis());
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(mOrderId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //订单Id（传入的是diet_order_info表中的Id，根据这个Id查询美团的订单Id）
                paramsInfo.put("orderId", mOrderId);
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/confirm?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                LogUtil.logInfo("*************商家确认订单：开始调用美团接口：" + System.currentTimeMillis());
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        //修改订单状态 2:已确认
                        meituanService.editOrderStaus("M" + mOrderId, 2);
                        rest.setMessage("商家确认订单操作成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
                LogUtil.logInfo("*************商家确认订单：结束时间：" + System.currentTimeMillis());
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("商家确认订单异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 商家取消订单
     * 取消原因
     * 1001	系统取消，超时未确认
     * 1002	系统取消，在线支付订单30分钟未支付
     * 1101	用户取消，在线支付中取消
     * 1102	用户取消，商家确认前取消
     * 1103	用户取消，用户退款取消
     * 1201	客服取消，用户下错单
     * 1202	客服取消，用户测试
     * 1203	客服取消，重复订单
     * 1204	客服取消，其他原因
     * 1301	其他原因
     * 2001	商家超时接单【商家取消时填写】
     * 2002	非顾客原因修改订单
     * 2003	非客户原因取消订单
     * 2004	配送延迟
     * 2005	售后投诉
     * 2006	用户要求取消
     * 2007	其他原因（未传code，默认为此）
     */
    @RequestMapping("/cancelOrder")
    public String cancelOrder(){
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(mOrderId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //订单Id（传入的是diet_order_info表中的Id，根据这个Id查询美团的订单Id）
                String reasonCode = "2007";
                if(params.get("reasonCode") != null){
                    reasonCode = params.get("reasonCode");
                }
                String reason = "其他原因!";
                if(params.get("reason") != null){
                    reason = params.get("reason");
                }
                paramsInfo.put("orderId", mOrderId);
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //取消原因码
                paramsInfo.put("reasonCode", reasonCode);
                //取消原因
                paramsInfo.put("reason", reason);
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/cancel?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        //修改订单状态 9:已拒绝
                        meituanService.editOrderStaus("M" + mOrderId, 9);
                        rest.setMessage("商家取消订单成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("商家取消订单异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 商家发配送【商家自配送场景】
     */
    @RequestMapping("/delivering")
    public String delivering(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            DietOrderInfo mOrderId = meituanService.getOrderInfo(params.get("orderId"));
            if(mOrderId != null && branch != null){
                //只有状态为2：已接单的订单可以配送 （用户申请退款商户拒绝的情况下也可以配送）
                if(mOrderId.getOrderStatus() == 2 || (mOrderId.getOrderStatus() == 10 && mOrderId.getPayStatus() == 4)){
                    paramsInfo.put("appAuthToken", branch.getMeituanToken());
                    //交互数据的编码
                    paramsInfo.put("charset", "UTF-8");
                    String timestamp = String.valueOf(new Date().getTime());
                    //订单Id（传入的是diet_order_info表中的Id，根据这个Id查询美团的订单Id）
                    String courierName = "配送员";
                    if(params.get("courierName") != null){
                        courierName = params.get("courierName");
                    }
                    String courierPhone = "18611111111";
                    if(params.get("courierPhone") != null){
                        courierPhone = params.get("courierPhone");
                    }
                    if(mOrderId.getOrderCode() != null){
                        paramsInfo.put("orderId", mOrderId.getOrderCode().substring(1, mOrderId.getOrderCode().length()));
                    }
                    //当前请求的时间戳【单位是秒】
                    paramsInfo.put("timestamp", timestamp);
                    //配送人名称
                    paramsInfo.put("courierName", courierName);
                    //courierPhone
                    paramsInfo.put("courierPhone", courierPhone);
                    String sign = SHA1Utils.SHA1(paramsInfo);
                    paramsInfo.put("sign", sign);
                    StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/delivering?");
                    String result = "";
                    Map pMap = new HashMap();
                    pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                    if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("data"))){
                            rest.setIsSuccess(true);
                            rest.setMessage("商家发配送成功!");
                            //修改订单状态 3:正在派送
                            meituanService.editOrderStaus(mOrderId.getOrderCode(), 3, courierName, courierPhone);
                        }
                        else{
                            rest.setIsSuccess(false);
                            rest.setMessage(((Map)map.get("error")).get("message").toString());
                        }
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("访问http失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("已完成、已取消或正在申请退款的订单不能配送!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("订单或机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("商家发配送异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 订单已送达【商家自配送场景】
     */
    @RequestMapping("/delivered")
    public String delivered(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            DietOrderInfo mOrderId = meituanService.getOrderInfo(params.get("orderId"));
            if(mOrderId != null && branch != null){
                //只有状态为3：配送中的订单可以进行送达确认
                if(mOrderId.getOrderStatus() == 3){
                    paramsInfo.put("appAuthToken", branch.getMeituanToken());
                    //交互数据的编码
                    paramsInfo.put("charset", "UTF-8");
                    String timestamp = String.valueOf(new Date().getTime());
                    //订单Id（传入的是diet_order_info表中的Id，根据这个Id查询美团的订单Id）
                    if(mOrderId.getOrderCode() != null){
                        paramsInfo.put("orderId", mOrderId.getOrderCode().substring(1, mOrderId.getOrderCode().length()));
                    }
                    //当前请求的时间戳【单位是秒】
                    paramsInfo.put("timestamp", timestamp);
                    String sign = SHA1Utils.SHA1(paramsInfo);
                    paramsInfo.put("sign", sign);
                    StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/delivered?");
                    String result = "";
                    Map pMap = new HashMap();
                    pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                    ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                    if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if("ok".equals(map.get("data"))){
                            rest.setIsSuccess(true);
                            rest.setMessage("商家自配已送达!");
                            //修改订单状态 4:已派送
                            meituanService.updatePayStatus(mOrderId.getOrderCode(), 1, 4, null);
                        }
                        else{
                            rest.setIsSuccess(false);
                            rest.setMessage(((Map)map.get("error")).get("message").toString());
                        }
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("访问http失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("已完成、已取消或正在申请退款的订单不能配送!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("订单或机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("商家自配已送达异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 下发美团专送【美团专送且非自动下发场景调用】
     */
    @RequestMapping("/dispatchShip")
    public String dispatchShip(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(mOrderId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //订单号
                paramsInfo.put("orderId", mOrderId);
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/dispatchShip?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        rest.setMessage("下发美团专送操作成功");
                        //修改订单状态 3:派送中
                        meituanService.editOrderStaus("M" + mOrderId, 3);
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("下发美团专送异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 订单同意退款
     */
    @RequestMapping("/agreeRefund")
    public String agreeRefund(){
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(mOrderId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //原因
                paramsInfo.put("reason", params.get("reason"));
                //订单号
                paramsInfo.put("orderId", mOrderId);
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/agreeRefund?");
                paramsInfo.put("sign", sign);
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        rest.setMessage("订单同意退款成功");
                        //修改支付状态 3:退款成功 订单状态 10：用户撤单
                        meituanService.updatePayStatus("M" + mOrderId, 3, 10, null);
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("订单同意退款:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 订单拒绝退款
     */
    @RequestMapping("/rejectRefund")
    public String rejectRefund(){
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(mOrderId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //订单号
                paramsInfo.put("orderId", mOrderId);
                //原因
                paramsInfo.put("reason", params.get("reason"));
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/rejectRefund?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        rest.setMessage("订单拒绝退款成功");
                        //修改支付状态 4:商户拒绝退款 订单状态 10：用户撤单
                        meituanService.updatePayStatus("M" + mOrderId, 4, 10, null);
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("订单拒绝退款:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

//    /**
//     * 取消美团配送订单
//     */
//    def cancelDispatch(){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,String> paramsInfo = new HashMap<String,String>();
//            //认领机构返回的token【一店一token】
//            String appAuthToken = params.appAuthToken;
//            paramsInfo.put("appAuthToken",appAuthToken);
//            //交互数据的编码
//            paramsInfo.put("charset","UTF-8");
//            //订单号
//            def mOrderId = meituanService.getOrderId(params.orderId);
//            if(mOrderId){
//                paramsInfo.put("orderId",mOrderId);
//                //当前请求的时间戳【单位是秒】
//                String timestamp = String.valueOf(new Date().getTime());
//                paramsInfo.put("timestamp",timestamp);
//                //请求的数字签名
//                String sign = SHA1Utils.SHA1(paramsInfo);
//                StringBuffer baseUrl = new StringBuffer("http://api.open.cater.meituan.com/waimai/order/cancelDispatch?");
//                baseUrl.append("appAuthToken=" + appAuthToken);
//                baseUrl.append("&charset=UTF-8&timestamp=" + timestamp);
//                baseUrl.append("&sign=" + sign);
//                baseUrl.append("&orderId=" + mOrderId);
//                Map pMap = new HashMap();
//                pMap.put("baseUrl",baseUrl.toString());
//                String result = "";
//                ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//                if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                    result = rest1.data;
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Map map = objectMapper.readValue(result, Map);
//                    if(map.get("data") == "ok"){
//                        rest.isSuccess = true
//                        rest.message = "取消美团配送订单操作成功";
//                    }else{
//                        rest.isSuccess = false;
//                        rest.message = map.get("error").message;
//                    }
//                }else {
//                    rest.isSuccess = false;
//                    rest.message = "访问http失败!";
//                }
//            }else{
//                rest.isSuccess = false;
//                rest.message = "订单不存在!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError(e, params);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }

    /**
     * 众包配送场景－查询配送费
     */
    @RequestMapping("/queryZbShippingFee")
    public String queryZbShippingFee(){
        Map<String, String> params = AppHandler.params();
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(branch != null && mOrderId != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);

                paramsInfo.put("orderIds", mOrderId);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/queryZbShippingFee?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlByGet", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setData(map.get("data"));
                        rest.setMessage("查询配送费成功");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("查询配送费:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 众包配送预下单
     */
    @RequestMapping("/prepareZbDispatch")
    public String prepareZbDispatch(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(branch != null && mOrderId != null && params.get("shippingFee") != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);

                paramsInfo.put("orderId", mOrderId);
                //配送费
                paramsInfo.put("shippingFee", params.get("shippingFee"));
                //小费(格式：5.0)
                if(params.get("tipAmount") != null){
                    paramsInfo.put("tipAmount", params.get("tipAmount"));
                }
                else{
                    paramsInfo.put("tipAmount", 0.0);
                }
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/prepareZbDispatch?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("预下单成功");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("预下单:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 众包配送确认下单（预下单失败后，再调用确认下单接口）
     */
    @RequestMapping("/confirmZbDispatch")
    public String confirmZbDispatch(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(branch != null && mOrderId != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);

                paramsInfo.put("orderId", mOrderId);
                //小费(格式：5.0)
                if(params.get("tipAmount") != null){
                    paramsInfo.put("tipAmount", params.get("tipAmount"));
                }
                else{
                    paramsInfo.put("tipAmount", 0.0);
                }
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/confirmZbDispatch?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("众包配送确认下单成功");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("预下单:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 众包配送场景－配送单加小费（从发配送到骑手抢单前，这段时间都可以追加小费，可多次调用，小费只能增，不能减）
     */
    @RequestMapping("/updateZbDispatchTip")
    public String updateZbDispatchTip(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String mOrderId = meituanService.getOrderId(params.get("orderId"));
            if(branch != null && mOrderId != null && params.get("tipAmount") != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                //当前请求的时间戳【单位是秒】
                String timestamp = String.valueOf(new Date().getTime());
                paramsInfo.put("timestamp", timestamp);

                paramsInfo.put("orderId", mOrderId);
                //小费(格式：5.0)
                paramsInfo.put("tipAmount", params.get("tipAmount"));
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/order/confirmZbDispatch?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("加小费成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("预下单:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 根据机构id查询菜品基础信息(包含美团的菜品ID)
     *
     * @return
     */
    @RequestMapping("/queryBaseListByBranchId")
    public String queryBaseListByBranchId(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, String> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);  //"200845Z214633"
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/queryBaseListByEPoiId?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null && !"".equals(map.get("data"))){
                        List list = (List)map.get("data");
                        if(list != null && list.size() > 0){
                            Map<String, Object> dMap = new HashMap<>();
                            dMap.put("total", list.size());
                            dMap.put("rows", list);
                            rest.setData(dMap);
                        }
                        rest.setIsSuccess(true);
                        rest.setMessage("查询美团商品完成!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("branchId不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)查询商品基础信息:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 根据ERP的机构id查询机构下的菜品【不包含美团的菜品Id】（可分页）
     *
     * @return
     */
    @RequestMapping("/queryListByEPoiId")
    public String queryListByEPoiId(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);  //"200845Z214633"
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/queryBaseListByEPoiId?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null && !"".equals(map.get("data"))){
                        List list = (List)map.get("data");
                        if(list != null && list.size() > 0){
                            Map<String, Object> dMap = meituanService.getMeituanGoodsData(list, params);
                            if(Integer.valueOf(dMap.get("total").toString()) > 0){
                                List returnList = (List)dMap.get("rows");
                                List resultList = new ArrayList();
                                Integer page = 1;
                                Integer rows = 20;
                                if(StringUtils.isNotEmpty(params.get("rows")) && StringUtils.isNotEmpty(params.get("page"))){//数据分页
                                    page = Integer.valueOf(params.get("page"));
                                    rows = Integer.valueOf(params.get("rows"));
                                }
                                Integer offset = (page - 1) * rows;
                                for(int i = offset; i < (offset + rows); i++){
                                    if(i < returnList.size()){
                                        resultList.add(returnList.get(i));
                                    }
                                    else{
                                        continue;
                                    }
                                }
                                Map resultMap = new HashMap();
                                resultMap.put("rows", resultList);
                                if(params.get("categoryName") != null || params.get("goodsName") != null){
                                    resultMap.put("total", returnList.size());
                                }
                                else{
                                    resultMap.put("total", list.size());
                                }
                                rest.setData(resultMap);
                            }else{
                                Map<String, Object> s = new HashMap<>();
                                s.put("rows", new ArrayList<>());
                                s.put("total", 0);
                                rest.setData(s);
                            }
                        }
                        else{
                            Map<String, Object> dMap = new HashMap<>();
                            dMap.put("rows", new ArrayList<>());
                            dMap.put("total", 0);
                            rest.setData(dMap);
                        }
                        rest.setIsSuccess(true);
                        rest.setMessage("查询美团商品完成!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("branchId不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)查询商品基础信息:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询机构信息
     *
     * @return
     */
    @RequestMapping("/queryPoiInfo")
    public String queryPoiInfo(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiIds", branch.getTenantId() + "Z" + ePoiId);  //"200845Z214633"
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/poi/queryPoiInfo?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlByGet", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null && !"".equals(map.get("data"))){
                        rest.setData(map.get("data"));
                        rest.setIsSuccess(true);
                        rest.setMessage("查询美团机构信息成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("branchId不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)美团机构信息:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }


//    /**
//     * 根据机构id查询菜品基础信息(包含美团的菜品ID)
//     * @return
//     */
//    @RequestMapping("/queryBaseListByBranchId")
//    public String queryBaseListByBranchId(){
//        ApiRest rest = new ApiRest();
//        Map<String,String> params= AppHandler.params();
//        try {
//            Map<String,String> paramsInfo = new HashMap<>();
//            String ePoiId = params.get("branchId");
//            Branch branch = meituanService.getBranchInfo(ePoiId);
//            if(ePoiId != null && branch != null){
//                paramsInfo.put("appAuthToken",branch.getMeituanToken());
//                //交互数据的编码
//                paramsInfo.put("charset","UTF-8");
//                String timestamp = String.valueOf(new Date().getTime());
//                //erp机构id
//                paramsInfo.put("ePoiId",branch.getTenantId() + "Z" + ePoiId);
//                //当前请求的时间戳【单位是秒】
//                paramsInfo.put("timestamp",timestamp);
//                //请求的数字签名
//                String sign = SHA1Utils.SHA1(paramsInfo);
//                paramsInfo.put("sign",sign);
//                StringBuffer baseUrl = new StringBuffer("http://api.open.cater.meituan.com/waimai/dish/queryBaseListByEPoiId?");
//                String result = "";
//                Map pMap = new HashMap();
//                pMap.put("baseUrl",SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
//                ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//                if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
//                    result = rest1.getData().toString();
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Map map = objectMapper.readValue(result, Map.class);
//                    if(map.get("data") != null){
//                        JSONArray dishMappings = meituanService.mappingGoods((List)map.get("data"),branch);
//                        if(dishMappings.size() > 0){
//                            //进行菜品映射
//                            mappingGoods(ePoiId,dishMappings);
//                        }
//                    }
//                    rest.isSuccess = true;
//                    rest.message = "查询、映射美团菜品完成!";
//                } else {
//                    rest.isSuccess = false;
//                    rest.message = "访问http失败!";
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "branchId不存在!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("(美团)查询菜品基础信息:" + e.message);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }
//
//    /**
//     * 建立菜品映射
//     * @return
//     */
//    def mappingGoods(String ePoiId,JSONArray dishMappings){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,Object> paramsInfo = new HashMap<String,Object>();
//
//            Branch branch = meituanService.getBranchInfo(ePoiId);
//            if(ePoiId!= null && branch != null){
//                paramsInfo.put("appAuthToken",branch.meituanToken);
//                //交互数据的编码
//                paramsInfo.put("charset","UTF-8");
//                String timestamp = String.valueOf(new Date().getTime());
//                //erp机构id
//                paramsInfo.put("ePoiId",branch.getTenantId() + "Z" + ePoiId);
//                //当前请求的时间戳【单位是秒】
//                paramsInfo.put("timestamp",timestamp);
//                paramsInfo.put("dishMappings",dishMappings);
//                //请求的数字签名
//                String sign = SHA1Utils.SHA1(paramsInfo);
//                paramsInfo.put("sign",sign);
//                StringBuffer baseUrl = new StringBuffer("http://api.open.cater.meituan.com/waimai/dish/mapping?");
//                String result = "";
//                Map pMap = new HashMap();
//                pMap.put("baseUrl",combineParamsAsUrl(baseUrl.toString(),paramsInfo));
//                ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//                if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                    result = rest1.data;
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Map map = objectMapper.readValue(result, Map);
//                    if(map.get("data").equals("OK")){
//                        rest.isSuccess = true;
//                        rest.message = "建立菜品映射成功!";
//                    }else{
//                        rest.isSuccess = false;
//                        rest.message = "建立菜品映射失败!";
//                    }
//                } else {
//                    rest.isSuccess = false;
//                    rest.message = "访问http失败!";
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "branchId不存在!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("(美团)建立菜品映射异常:" + e.message);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }

    /**
     * 建立菜品映射
     *
     * @return
     */
    @RequestMapping("/mappingGoods")
    public String mappingGoods(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            JSONArray dishMappings = null;
            if(params.get("branchId") != null && params.get("dishId") != null && params.get("goodsCode") != null){
                dishMappings = meituanService.mappingGoodsSingle(params.get("branchId"), params.get("dishId"), params.get("goodsCode"));
            }
            Map<String, Object> paramsInfo = new HashMap<>();
            String tenantId = params.get("tenantId");
            Branch branch = meituanService.getBranchInfo(params.get("branchId"));
            if(branch != null && dishMappings != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + params.get("branchId"));
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                paramsInfo.put("dishMappings", dishMappings);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/mapping?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("OK".equals(map.get("data"))){
                        if(StringUtils.isNotEmpty(params.get("oldGoodsId"))){
                            goodsMapper.deleteMeituanGoodsToBranch(tenantId, branch.getId().toString(), params.get("oldGoodsId"));
                        }
                        meituanService.addMeituanGoodsToBranch(params.get("branchId"), params.get("goodsCode"), branch.getTenantId());
                        rest.setIsSuccess(true);
                        rest.setMessage("建立商品映射成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("建立商品映射失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("branchId不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)建立商品映射异常:" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 删除菜品
     *
     * @return
     */
    @RequestMapping("/deleteGoods")
    public String deleteGoods(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            //认领机构返回的token【一店一token】
            String tenantId = params.get("tenantId");
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String eDishCode = params.get("eDishCode");
            if(ePoiId != null && branch != null && eDishCode != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //erp方菜品id
                paramsInfo.put("eDishCode", eDishCode);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/delete?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        goodsMapper.deleteMeituanGoodsToBranch(tenantId, ePoiId, params.get("goodsId"));
                        rest.setIsSuccess(true);
                        rest.setMessage("删除商品成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("未映射的商品不允许删除!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)删除商品异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 批量上传/更新菜品
     *
     * @return
     */
    @RequestMapping("/batchUpload")
    public String batchUpload(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");

            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());

                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //构建菜品json
                JSONArray dishes = meituanService.combineDishJson(params.get("ids"), branch);
                paramsInfo.put("dishes", dishes);
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/batchUpload");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", baseUrl.toString());
                pMap.put("paramMap", BeanUtils.toJsonStr(paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                LogUtil.logInfo("返回结果：" + rest1);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null && "ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        rest.setMessage("批量更新商品成功!");
                        meituanService.saveMeituanGoodsBranch(dishes, branch.getId());
                    }
                    else{
                        Map errorMap = (Map)map.get("error");
                        String mesStr = errorMap.get("message").toString();
                        rest.setError(mesStr);
                        rest.setIsSuccess(false);
                        return BeanUtils.toJsonStr(rest);
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)批量上传商品异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }
//    /**
//     * 查询菜品分类
//     * @return
//     */
//    def queryCatList(){
//        ApiRest rest = new ApiRest();
//        try {
//            Map<String,Object> paramsInfo = new HashMap<String,Object>();
//            //认领机构返回的token【一店一token】
//            String appAuthToken = params.appAuthToken;
//            paramsInfo.put("appAuthToken",appAuthToken);
//            //交互数据的编码
//            paramsInfo.put("charset","UTF-8");
//            String timestamp = String.valueOf(new Date().getTime());
//            //当前请求的时间戳【单位是秒】
//            paramsInfo.put("timestamp",timestamp);
//            //请求的数字签名
//            String sign = SHA1Utils.SHA1(paramsInfo);
//            paramsInfo.put("sign",sign);
//            StringBuffer baseUrl = new StringBuffer("http://api.open.cater.meituan.com/waimai/dish/queryCatList?");
//            String result = "";
//            Map pMap = new HashMap();
//            pMap.put("baseUrl",combineParamsAsUrl(baseUrl.toString(),paramsInfo));
//            ApiRest rest1 = ProxyApi.proxyPost("out","wechat","connectOutUrl",pMap);
//            if (Constants.REST_RESULT_SUCCESS.equals(rest1.result)) {
//                result = rest1.data;
//                ObjectMapper objectMapper = new ObjectMapper();
//                Map map = objectMapper.readValue(result, Map);
//                if(map.get("data")){
//                    rest.isSuccess = true;
//                    rest.message = "查询菜品分类成功!";
//                    //TODO mapper
//                }else{
//                    rest.isSuccess = false;
//                    rest.message = "菜品分类数据为空!";
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "访问http失败!";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("(美团)查询菜品分类异常:" + e.message);
//            rest.message = e.message;
//            rest.isSuccess = false;
//        }
//        render rest as JSON;
//    }

    /**
     * 上传菜品图片
     */
    @RequestMapping("/uploadImage")
    public String uploadImage(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                if(params.get("imagePath") != null && params.get("imageName") != null){
                    //认领机构返回的token【一店一token】
                    paramsInfo.put("appAuthToken", branch.getMeituanToken());
                    //交互数据的编码
                    paramsInfo.put("charset", "UTF-8");
                    String timestamp = String.valueOf(new Date().getTime());
                    //当前请求的时间戳【单位是秒】
                    paramsInfo.put("timestamp", timestamp);
                    String sign = SHA1Utils.SHA1(paramsInfo);
                    paramsInfo.put("sign", sign);
                    //请求的数字签名
                    StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/image/upload?");
                    String result = "";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("imageName", params.get("imageName"));
                    paramMap.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);
                    paramMap.put("file", new File(params.get("imagePath")));
                    paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                    ApiRest rest1 = ProxyApi.proxyPostFile("out", "wechat", "connectOutUrlFile", paramMap);
                    if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                        result = rest1.getData().toString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map map = objectMapper.readValue(result, Map.class);
                        if(map.get("data") != null){
                            rest.setIsSuccess(true);
                            rest.setMessage("上传商品图片成功!");
                            rest.setData(map.get("data"));
                        }
                        else{
                            rest.setIsSuccess(false);
                            rest.setMessage("上传商品图片失败!");
                        }
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("访问http失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("图片路径、图片名称不能为空!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)上传图片异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询闪惠支付订单
     */
    @RequestMapping("/queryShanhuiOrderById")
    public String queryShanhuiOrderById(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null && params.get("orderId") != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                paramsInfo.put("orderId", params.get("orderId"));
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/shanhui/order/queryById?");
                String result = "";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlByGet", paramMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("查询闪惠订单成功!");
                        rest.setData(map.get("data"));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("查询闪惠订单失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)查询闪惠订单异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 确认闪惠支付订单
     */
    @RequestMapping("/confirmShanhuiOrder")
    public String confirmShanhuiOrder(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null && params.get("orderId") != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                paramsInfo.put("orderId", params.get("orderId"));
                paramsInfo.put("eOrderId", params.get("eOrderId"));
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/shanhui/order/confirm?");
                String result = "";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", paramMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("确认闪惠订单成功!");
                        rest.setData(map.get("data"));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("确认闪惠订单失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)确认闪惠订单异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 闪惠支付订单退款
     */
    @RequestMapping("/refundShanhuiOrder")
    public String refundShanhuiOrder(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null && params.get("orderId") != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                paramsInfo.put("orderId", params.get("orderId"));
                paramsInfo.put("eOrderId", params.get("eOrderId"));
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/shanhui/order/confirm?");
                String result = "";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", paramMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("闪惠支付订单退款成功!");
                        rest.setData(map.get("data"));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("闪惠支付订单退款失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)闪惠支付订单退款:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * B扫C：提交第三方订单
     */
    @RequestMapping("/createShanhuiOrder")
    public String createShanhuiOrder(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //用户app支付码
                paramsInfo.put("authCode", params.get("authCode"));
                //第三方ERP订单号
                paramsInfo.put("eOrderId", params.get("eOrderId"));
                //原价
                paramsInfo.put("originalAmount", params.get("originalAmount"));
                //不可优惠价
                paramsInfo.put("noDiscountAmount", params.get("noDiscountAmount"));
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/shanhui/order/create?");
                String result = "";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", paramMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("提交第三方订单成功!");
                        rest.setData(map.get("data"));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("提交第三方订单失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)闪惠提交第三方订单:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * B扫C：凭第三方订单号查询
     */
    @RequestMapping("/queryByEorderId")
    public String queryByEorderId(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            if(ePoiId != null && branch != null){
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                //第三方ERP订单号
                paramsInfo.put("eOrderId", params.get("eOrderId"));
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/shanhui/order/queryByEorderId?");
                String result = "";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrlByGet", paramMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if(map.get("data") != null){
                        rest.setIsSuccess(true);
                        rest.setMessage("凭第三方订单号查询成功!");
                        rest.setData(map.get("data"));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("凭第三方订单号查询失败!");
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("机构不存在!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)凭第三方订单号查询:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 修改菜品价格
     *
     * @return
     */
    @RequestMapping("/updatePrice")
    public String updatePrice(){
        ApiRest rest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            Map<String, Object> paramsInfo = new HashMap<>();
            //认领机构返回的token【一店一token】
            String ePoiId = params.get("branchId");
            Branch branch = meituanService.getBranchInfo(ePoiId);
            String eDishCode = params.get("eDishCode");
            if(ePoiId != null && branch != null && eDishCode != null && params.get("price") != null){
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //erp机构id
                paramsInfo.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                paramsInfo.put("dishSkuPrices", meituanService.combineDishSkuPrices(eDishCode, params));
                //请求的数字签名
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/dish/updatePrice?");
                String result = "";
                Map pMap = new HashMap();
                pMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPost("out", "wechat", "connectOutUrl", pMap);
                if(Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())){
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if("ok".equals(map.get("data"))){
                        rest.setIsSuccess(true);
                        rest.setMessage("修改商品价格成功!");
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage(((Map)map.get("error")).get("message").toString());
                    }
                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("访问http失败!");
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("未映射的商品不允许修改!");
            }
        }
        catch(Exception e){
            LogUtil.logError("(美团)修改商品价格异常:" + e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    private static final String MEI_TUAN_CONTROLLER_SIMPLE_NAME = "MeiTuanController";

    @RequestMapping(value = "privacyDowngradeCallback")
    @ResponseBody
    public String privacyDowngradeCallback(){
        String returnValue = "{\"data\":\"OK\"}";
        final Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try{
            meituanService.handlePrivacyDowngradeCallback(requestParameters);
        }
        catch(Exception e){
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "处理隐私号降级回调失败", MEI_TUAN_CONTROLLER_SIMPLE_NAME, "privacyDowngradeCallback", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
        }
        return returnValue;
    }
}
