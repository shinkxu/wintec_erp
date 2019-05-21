package erp.chain.controller.system;

import com.alibaba.fastjson.JSON;
import com.saas.common.Constants;
import com.saas.common.util.CacheUtils;
import com.saas.common.util.GzipUtils;
import com.saas.common.util.LogUtil;
import com.saas.common.util.MD5Utils;
import erp.chain.domain.Area;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.o2o.VipStoreRuleDetails;
import erp.chain.mapper.AreaMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.service.DataService;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.service.o2o.VipService;
import erp.chain.service.setting.OrganizationService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 测试接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/6/28
 */
@Controller
@ResponseBody
@RequestMapping("/testData")
public class TestDataController{

    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private CardCouponsService cardCouponsService;

    @RequestMapping("/getRedisObject")
    public String getRedisObject(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String key=params.get("key");
            Object obj=CacheUtils.getObject(MD5Utils.stringMD5(key));
            apiRest.setIsSuccess(true);
            apiRest.setData(obj);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/sendSms")
    public String sendSms(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String numberList= params.get("numberList");
            String templeId=params.get("templeId");
            String message="";
            int count=0;
            int noCount=0;
            for(String number:numberList.split(",")){
                Map map=new HashMap();
                map.put("tenantId",null);
                map.put("number",number);
                map.put("template",templeId);
                map.put("content","1###2");
                apiRest= ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT,"sms","sendByTemplate",map);
                if(apiRest.getIsSuccess()){
                    count++;
                }else{
                    noCount++;
                    message+=(number+",");
                }
            }
            apiRest.setIsSuccess(true);
            if(noCount>0){
                apiRest.setMessage(count+"个号码发送成功！"+noCount+"个号码发送失败！失败号码为："+(message.substring(0,message.length()-1)));
            }else{
                apiRest.setMessage(count+"个号码发送成功！");
            }
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/replaceData")
    public String replaceData(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String key=params.get("key");
            key=key.replace(" ","");
            apiRest.setIsSuccess(true);
            apiRest.setData(key);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/decompress")
    public String decompress(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String key=params.get("key");
            String json =  new String(GzipUtils.decompress(Base64.decodeBase64(key)), "utf-8");
            apiRest.setIsSuccess(true);
            apiRest.setData(json);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/testDate")
    public String testDate(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");

            Date today = new Date();

            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.DAY_OF_MONTH, 45);// 今天+45天

            Date endDate = c.getTime();
            String limitDate=s.format(endDate);
            apiRest.setIsSuccess(true);
            apiRest.setData(limitDate);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/testSaveData")
    public String testSaveData(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            VipStoreRuleDetails vipStoreRuleDetails=new VipStoreRuleDetails();
            apiRest.setIsSuccess(true);
            apiRest.setData(vipStoreRuleDetails);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/testRandomCode")
    public String testRandomCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String type=params.get("type");
            int length=Integer.valueOf(params.get("length"));
            String code = tenantConfigService.getToday(type,length);
            apiRest.setData(code);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @Autowired
    private DataService dataService;
    @RequestMapping("/testCompressJson")
    public String testCompressJson(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            BigInteger posId=BigInteger.valueOf(Long.valueOf(params.get("posId")));
            Map<String,Object> map=dataService.simuDownloadDataGzip(params.get("json"),posId,"1");
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/testDepressJson")
    public String testDepressJson(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String dataStr=new String(GzipUtils.decompress(Base64.decodeBase64(params.get("str"))), "utf-8");
            apiRest.setData(GsonUntil.toJson(dataStr));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/depressJson")
    public String depressJson(@RequestBody String str) throws Exception{
        String dataStr = new String(GzipUtils.decompress(Base64.decodeBase64(str)), "utf-8");
        return dataStr;
    }
    @RequestMapping("/testWords")
    public String testWords(String key){
        java.util.regex.Pattern pat = java.util.regex.Pattern.compile("^[A-Za-z0-9]+$");
        java.util.regex.Matcher match = pat.matcher(key);
        if(match.matches()){
            return "true";
        }else{
            return "false";
        }
    }

    @RequestMapping("/resetCouponsData")
    public String resetCouponsData(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest=cardCouponsService.resetCouponsData(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/testMod")
    public String testMod(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            BigInteger aaa=BigInteger.valueOf(20003000).mod(BigInteger.valueOf(10000000));
            apiRest.setData(aaa);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/testWebService")
    public String testWebService() throws IOException, ServiceException, ClassNotFoundException{
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<tran_info>" +
                "<tran_info_sale>" +
                "<market_id>370102134</market_id>" +
                "<market_name>济南龙锦大市场</market_name>" +
                "<sale_date>2018-10-26 18:11:12</sale_date>" +
                "<retailer_id>3701021340007</retailer_id>" +
                "<retailer_name>邓敏</retailer_name>" +
                "<tran_id>37010213400070000088</tran_id>" +
                "<plu>11011</plu>" +
                "<plu_name>马铃薯</plu_name>" +
                "<goods_code>01211</goods_code>" +
                "<goods_name>马铃薯(土豆)</goods_name>" +
                "<weight>1</weight>" +
                "<price>2</price>" +
                "<scales_ip>192.168.0.90</scales_ip>" +
                "<scales_id>123</scales_id>" +
                "<batch_id>3701021340000288</batch_id>" +
                "<goods_type>1</goods_type>" +
                "</tran_info_sale>" +
                "</tran_info>";
        String url = "http://112.230.202.74:8081/vmarket/services/LsServices.LsServices?wsdl";
        Service service=new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(url);
        call.setOperationName(new QName("http://loushang.ws","putTransData"));
        //call.addParameter(new QName("http://webservice.rpc.other.web.demo.g4studio.org/", "xmlContent"), XMLType.XSD_STRING, Class.forName("java.lang.String"), ParameterMode.IN );
        //call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
        System.out.println("推送的xml---"+xml);
        String str=(String) call.invoke(new Object[] {xml});
        System.out.println(str+"----------------推送成功");
        String json= XML.toJSONObject(str).toString();
        String xml1=XMLUtils.jsonToXML(json);
        return "";
    }

    @Autowired
    private VipMapper vipMapper;
    @RequestMapping("sendWxInfo")
    public void sendWxInfo(){
        Vip vip=vipMapper.findByIdAndTenantIdAndBranchId(BigInteger.valueOf(39005),BigInteger.valueOf(201005),BigInteger.valueOf(1121));
        WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), 3, 1);
    }

    @RequestMapping("testUrl")
    public void testUrl() throws IOException{
        String url="http://localhost:7000/login/posLogin";
        String ctype = "application/x-www-form-urlencoded";
        String query = "aaa="+URLEncoder.encode("大苏打奥","UTF-8") +"&ttt="+URLEncoder.encode("特太多他","UTF-8");
        byte[] content = query.getBytes("UTF-8");
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            try {
                conn = getConnection(new URL(url), "POST", ctype, null);
                conn.setConnectTimeout(1000000);
                conn.setReadTimeout(1000000);
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsString(conn, getResponseCharset(ctype, "UTF-8"));
            } catch (IOException e) {
                throw e;
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
    private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,application/json");
        conn.setRequestProperty("Content-Type", ctype);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }
    private static String getResponseCharset(String ctype ,String defaultCharset) {
        String charset = defaultCharset;
        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        return charset;
    }
    protected static String getResponseAsString(HttpURLConnection conn, String defaultCharset) throws IOException {
        String charset = getResponseCharset(conn.getContentType(), defaultCharset);
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }
    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    /*@RequestMapping("testTime")
    public void testTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        for(int i=1;i<=100;i++){
            System.out.println("时间："+format.format(new Date()));
            System.out.println("总毫秒为:"+System.currentTimeMillis());
        }
    }*/

    @RequestMapping("testGeCode")
    public String testGeCode(){
        List<String> list = cardCouponsService.cardCodeArray("aaa",2,10);
        return BeanUtils.toJsonStr(list);
    }
    @Autowired
    private VipService vipService;
    @RequestMapping("testWeekAndDate")
    public void testWeekAndDate(String date,BigInteger tenantId,BigInteger branchId) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Integer a=vipService.getVipDayScoreTimes(tenantId,branchId,sdf.parse(date));
        System.out.println("倍数"+a);
    }
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    OrganizationService organizationService;
    @RequestMapping("/testArea")
    public String testArea(){
        return organizationService.getChildAreaBranchIds(BigInteger.valueOf(201255),BigInteger.valueOf(1188));
    }

    private String checkArea(List result,String parentId){
        Map<String,Object> map=new HashMap<>();
        map.put("tenantId",201255);
        map.put("parentId",parentId);
        //result.add(parentId);
        List<Area> list=areaMapper.queryAreaList(map);
        for(Area a : list){
            result.add(a.getId());
            checkArea(result,a.getId().toString());
        }
        return BeanUtils.toJsonStr(result);

    }
    @RequestMapping("/testJson")
    public String testJson(String json){
        Map map = JSON.parseObject(json,Map.class);
        return null;
    }
}
