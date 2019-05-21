package erp.chain.controller.pos;

import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import erp.chain.controller.BaseController;
import erp.chain.service.DataService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.CommonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
@Controller
public class DataController extends BaseController {

    @Autowired
    private DataService dataService;


    @RequestMapping(value = "/api/data", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> get(String acn, String json, BigInteger posId, String business, String barcode, Integer goodsType, String queryStr, String rows, String page) {
        Map<String, Object> resultMap = null;
//        /*
//           erp-chain 数据库不分业态
//         */
       //business = "1";

        if (StringUtils.isEmpty(acn) || posId == null) {
            resultMap = CommonUtils.InvalidParamsError(null);
        } else if (Constants.POS_DATA_CHECK.equals(acn.trim().toLowerCase())) {
            resultMap = dataService.checkData(json, posId, business);
        } else if (Constants.POS_DATA_DOWNLOAD.equals(acn.trim().toLowerCase())) {
            resultMap = dataService.downloadData(json, posId, business);
        } else if ("simuDownload".equals(acn.trim())) {
            resultMap = dataService.simuDownloadData(json, posId, business);
        } else if ("simuDownloadGzip".equals(acn.trim())) {
            resultMap = dataService.simuDownloadDataGzip(json, posId, business);
        } else if ("pagableWithZipDownLoad".equals(acn.trim())){
            resultMap = dataService.pagableWithZipDownLoad(page, rows, json, posId, business);
        } else if ("flush".equals(acn.trim())) {//清空同步数据缓存
            resultMap = dataService.flush(posId, business);
        } else if ("findLibGoodsByBarcode".equals(acn.trim())) {
            if (StringUtils.isEmpty(barcode) || goodsType == null) {
                return CommonUtils.InvalidParamsError(null);
            }
            ApiRest r = SaaSApi.findLibGoodsByBarcode(barcode, goodsType);
            resultMap = (Map<String, Object>)JSONObject.toBean(JSONObject.fromObject(r), Map.class);
        } else if ("findLibGoodsByGoodsName".equals(acn.trim())) {
            if (StringUtils.isEmpty(queryStr) || goodsType == null) {
                return CommonUtils.InvalidParamsError(null);
            }
            ApiRest r = SaaSApi.findLibGoodsByGoodsName(queryStr, goodsType);
            resultMap = (Map<String, Object>)JSONObject.toBean(JSONObject.fromObject(r), Map.class);
        } else {
            resultMap = CommonUtils.InvalidParamsError(null);
        }

        return resultMap;
    }

    @RequestMapping(value = "/api/data", method = RequestMethod.POST)
    public @ResponseBody  Map<String, Object> post(String acn, @RequestBody String json, BigInteger posId) throws UnsupportedEncodingException{
        Map<String, Object> resultMap = null;
        if (StringUtils.isEmpty(acn) || posId == null) {
            resultMap = CommonUtils.InvalidParamsError(null);
        } else if (Constants.POS_DATA_UPLOAD.equals(acn.trim().toLowerCase())) {
            String[] parString=json.split("&");
            for(String s:parString){
                if(s.substring(0,4).equals("json")){
                    json= URLDecoder.decode(s.substring(5,s.length()),"UTF-8");
                }
            }
            resultMap = dataService.uploadData(json, posId);
        } else {
            resultMap = CommonUtils.InvalidParamsError(null);
        }

        return resultMap;
    }
    @RequestMapping(value = "/api/res", method = RequestMethod.GET)
    public @ResponseBody  String get() {
        Map<String,String> params = AppHandler.params();
        ApiRest apiRest=new ApiRest();
        try{
            apiRest=dataService.getRes(params);
        }
        catch(Exception e){
            LogUtil.logError("api/res-"+e.getMessage());
            apiRest.setError("api/res-"+e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }



}
