package erp.chain.controller.base;

import com.saas.common.util.Common;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.App;
import erp.chain.service.base.GoodsService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import saas.api.common.ApiRest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 菜品档案
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/24
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/goods","/baseWebService","/baseWS"})
public class GoodsController{
    @Autowired
    private GoodsService goodsService;
    /**
     * 查询goods列表
     * @return
     */
    @RequestMapping("/queryGoodsList")
    public String queryGoodsList() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.queryGoodsList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询商户带库存信息菜品档案（分页）
     * @return
     */
    @RequestMapping("/queryGoodsWithStoreInfoList")
    public String queryGoodsWithStoreInfoList() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("page")==null||params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows")==null||params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.queryGoodsWithStoreInfoList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据id查询菜品档案
     * @return
     */
    @RequestMapping("/queryGoodsById")
    public String queryGoodsById() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.queryGoodsById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除菜品档案
     * @return
     */
    @RequestMapping("/delGoods")
    public String delGoods() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.delGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取编码
     * @return
     */
    @RequestMapping("/getGoodsCode")
    public String getGoodsCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("categoryId")==null||params.get("categoryId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数categoryId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.getGoodsCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改菜品积分信息
     * @return
     */
    @RequestMapping(value = {"/updateGoodsScore"})
    public String updateGoodsScore() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("scoreValue") != null && !"".equals(params.get("scoreValue"))) {
                try {
                    BigInteger scoreValue = new BigInteger(params.get("scoreValue"));
                    if(scoreValue.intValue()<0 || scoreValue.intValue() > 9999){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("请输入0~9999的整数！");
                        return BeanUtils.toJsonStr(apiRest);
                    }
                } catch (Exception e){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("请输入0~9999的整数！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if (params.get("scorePercent") != null && !"".equals(params.get("scorePercent"))) {
                try {
                    BigDecimal scorePercent = new BigDecimal(params.get("scorePercent"));
                    if(scorePercent.intValue()<0 || scorePercent.intValue() > 100){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("请输入0.00~100.00的数字！");
                        return BeanUtils.toJsonStr(apiRest);
                    }
                } catch (Exception e){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("请输入0.00~100.00的数字！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = goodsService.updateGoodsScore(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改菜品信息
     * @return
     */
    @RequestMapping(value = {"/addOrUpdateGoods","/addGoods","/updateGoods"})
    public String addOrUpdateGoods() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("goods")==null||params.get("goods").equals("")){
                if (params.get("categoryId")==null||params.get("categoryId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数categoryId不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("goodsName")==null||params.get("goodsName").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsName不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                /*if (params.get("mnemonic")==null||params.get("mnemonic").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数mnemonic不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }*/
                if (params.get("goodsUnitId")==null||params.get("goodsUnitId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsUnitId不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }

                if (params.get("salePrice")==null||params.get("salePrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数salePrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("vipPrice")==null||params.get("vipPrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数vipPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("shippingPrice1")==null||params.get("shippingPrice1").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数shippingPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("purchasingPrice")==null||params.get("purchasingPrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数purchasingPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("goodsStatus")==null||params.get("goodsStatus").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsStatus不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isForPoints")==null||params.get("isForPoints").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isForPoints不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isTakeout")==null||params.get("isTakeout").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isTakeout不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isStore")==null||params.get("isStore").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isStore不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isPricetag")==null||params.get("isPricetag").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isPricetag不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isRecommended")==null||params.get("isRecommended").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isRecommended不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isNewgood")==null||params.get("isNewgood").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isNewgood不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isChangeprice")==null||params.get("isChangeprice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数combinationType不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if(params.get("id")!=null&&!params.get("id").equals("")){
                    if (params.get("userName")==null||params.get("userName").equals("")) {
                        apiRest.setIsSuccess(false);
                        apiRest.setError("修改时，参数userName不能为空！");
                        return BeanUtils.toJsonStr(apiRest);
                    }
                    if (params.get("opFrom")==null||params.get("opFrom").equals("")) {
                        apiRest.setIsSuccess(false);
                        apiRest.setError("修改时，参数opFrom不能为空！");
                        return BeanUtils.toJsonStr(apiRest);
                    }
                }else{
                    if (params.get("combinationType")==null||params.get("combinationType").equals("")) {
                        apiRest.setIsSuccess(false);
                        apiRest.setError("参数combinationType不能为空！");
                        return BeanUtils.toJsonStr(apiRest);
                    }
                }
            }
            apiRest = goodsService.addOrUpdateGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 导入菜品
     * @return
     */
    @RequestMapping("/saveGoodsLise")
    public String saveGoodsLise() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goodsList")==null||params.get("goodsList").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsList不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.saveGoodsList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                System.out.println(stackTraceElement.getClassName() + stackTraceElement.getLineNumber());
            }
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取编码
     * @return
     */
    @RequestMapping("/saveGoods")
    public String saveGoods() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("goods")==null||params.get("goods").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goods不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.saveGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/upLoadPicture")
    public String upLoadPicture(MultipartFile file) throws IOException{
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        if (file==null) {
            apiRest.setIsSuccess(false);
            apiRest.setError("文件上传错误！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if (params.get("relativePath")==null||params.get("relativePath").equals("")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("路径参数错误！");
            return BeanUtils.toJsonStr(apiRest);
        }
        String imageType = file.getContentType();
        if (imageType.equals("image/jpg") ||imageType.equals("image/jpeg") || imageType.equals("image/gif") || imageType.equals("image/png") || imageType.equals("image/bmp")) {
            int i = 1;
            long l = file.getSize();
            if (l < i * 1024 * 1024) {
                String imageFileName = Common.getNowCorrect2Millisecond() + "" ;//+imageFile.getOriginalFilename();
                String path = PropertyUtils.getDefault("picture_share_directory");
                String filePath = path + URLDecoder.decode(params.get("relativePath"), "UTF-8");
                File targetImagePathFile = new File(filePath);
                if (!targetImagePathFile.exists()) {
                    targetImagePathFile.mkdirs();
                }
                //创建文件
                File ImageFile = new File(filePath + "/" + imageFileName + "." + imageType.split("/")[1]);
                file.transferTo(ImageFile);
                Map map = new HashMap();
                map.put("dbpath", params.get("relativePath") + "/" + imageFileName + "." + imageType.split("/")[1]);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("上传图片成功！");
                apiRest.setData(map);
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setError("上传图片大小不得超过1M！");
            }
        } else {
            apiRest.setIsSuccess(false);
            apiRest.setError("上传图片只能为jpg、gif、png、bmp格式！");
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 批量设置菜品公共属性
     * */
    @RequestMapping("/batchSetup")
    public String batchSetup (){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(params.get("tenantId") == null || params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || params.get("branchId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
        apiRest = goodsService.batchSetup(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除子规格
     * */
    @RequestMapping("/deleteSpec")
    public String deleteSpec(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || params.get("tenantId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || params.get("branchId").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("specId") == null || "".equals(params.get("specId"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("specId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.deleteSpec(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 添加商品档案-并添加到现有菜牌中（POS）
     * */
    @RequestMapping("/addGoodsAndMenu")
    public String addGoodsAndMenu(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("goods")==null||params.get("goods").equals("")){
                if (params.get("categoryId")==null||params.get("categoryId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数categoryId不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("goodsName")==null||params.get("goodsName").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsName不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("goodsUnitId")==null||params.get("goodsUnitId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsUnitId不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }

                if (params.get("salePrice")==null||params.get("salePrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数salePrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("vipPrice")==null||params.get("vipPrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数vipPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("shippingPrice1")==null||params.get("shippingPrice1").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数shippingPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("purchasingPrice")==null||params.get("purchasingPrice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数purchasingPrice不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("goodsStatus")==null||params.get("goodsStatus").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数goodsStatus不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isForPoints")==null||params.get("isForPoints").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isForPoints不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isTakeout")==null||params.get("isTakeout").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isTakeout不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isStore")==null||params.get("isStore").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isStore不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isPricetag")==null||params.get("isPricetag").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isPricetag不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isRecommended")==null||params.get("isRecommended").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isRecommended不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isNewgood")==null||params.get("isNewgood").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数isNewgood不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("isChangeprice")==null||params.get("isChangeprice").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数combinationType不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("combinationType")==null||params.get("combinationType").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数combinationType不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = goodsService.addGoodsAndMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 商品排序
     * @return
     */
    @RequestMapping(value = "/orderGoods")
    public String orderGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("catId")==null||params.get("catId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数catId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.orderGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询商品排序
     * @return
     */
    @RequestMapping(value = "/listOrderGoods")
    public String listOrderGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("catId")==null||params.get("catId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数catId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.listOrderGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据商品条码查询商品档案信息（标准库）
     * */
    @RequestMapping(value="/queryByBarCode")
    public String queryByBarCode(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            apiRest = goodsService.queryByBarCode(params);
        }catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询机构可用的称重并且助记码长度是5纯数字的商品档案
     * @return
     */
    @RequestMapping(value = "/getWeighGoods")
    public String getWeighGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.getWeighGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 条码查询
     * */
    @RequestMapping("/queryGoodsByBarCode")
    public String queryGoodsByBarCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("barCode") == null || "".equals(params.get("barCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数barCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.queryGoodsByBarCode(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 检验条码是否已存在
     * */
    @RequestMapping("/checkBarCode")
    public String checkBarCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("barCode") == null || "".equals(params.get("barCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数barCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.checkBarCode(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询电子秤信息
     * */
    @RequestMapping("/scaleList")
    public String scaleList(){
        ApiRest apiRest = new ApiRest();
        try{
            apiRest = goodsService.scaleList();
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, null);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据ID查询电子秤
     * */
    @RequestMapping("/scaleById")
    public String scaleById(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.scaleById(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * en13规则生成商品条码
     * */
    @RequestMapping("/getBarCode")
    public String getBarCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.getBarCode(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    //以下为积分商城-商品档案所用接口

    /**
     * 查询商品档案列表
     * */
    @RequestMapping("/shopGoodsList")
    public String shopGoodsList(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return  BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.shopGoodsList(params);
       } catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
       }
       return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据ID查询商品档案
     * */
    @RequestMapping("/queryShopGoodsById")
    public String queryShopGoodsById(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.queryShopGoodsById(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增或修改商品档案
     * */
    @RequestMapping("/addOrUpdateShopGoods")
    public String addOrUpdateShopGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.addOrUpdateShopGoods(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除商品档案
     * */
    @RequestMapping("/delShopGoods")
    public String delShopGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("ids") == null || "".equals(params.get("ids"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.delShopGoods(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据条码查询商品信息
     * */
    @RequestMapping("/doSelectGoodsByBarCode")
    @ResponseBody
    public String doSelectGoodsByBarCode(){
      ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("barCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数barCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.doSelectGoodsByBarCode(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
      return BeanUtils.toJsonStr(apiRest);
     }

    /**
     * 批量改价（APP用）
     * */
    @RequestMapping("/updatePrice")
    public String updatePrice(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsService.updatePrice(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

}
