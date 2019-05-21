package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.PackageService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 套餐
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/9
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/package","/baseWebSerivce"})
public class PackageController{
    @Autowired
    private PackageService packageService;
    /**
     * 查询套餐列表
     * @return
     */
    @RequestMapping("/queryPackage")
    public String queryPackage() {
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
            apiRest = packageService.queryPackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询套餐详情
     * @return
     */
    @RequestMapping("/packageDetail")
    public String packageDetail() {
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
            apiRest = packageService.packageDetail(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除套餐以及详情
     * @return
     */
    @RequestMapping("/delPackage")
    public String delPackage() {
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
            apiRest = packageService.delPackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取套餐编码
     * @return
     */
    @RequestMapping("/getPackageCode")
    public String getPackageCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = packageService.getPackageCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改套餐
     * @return
     */
    @RequestMapping(value = {"/addOrUpdatePackage","/addPackage","/updatePackage"})
    public String addOrUpdatePackage() {
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
            if (params.get("mnemonic")==null||params.get("mnemonic").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mnemonic不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goodsName")==null||params.get("goodsName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goodsStatus")==null||params.get("goodsStatus").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsStatus不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isDsc")==null||params.get("isDsc").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isDsc不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isTakeout")==null||params.get("isTakeout").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isTakeout不能为空！");
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
            if (params.get("groupGoods")==null||params.get("groupGoods").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupGoods不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = packageService.addOrUpdatePackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改套餐
     * @return
     */
    @RequestMapping(value = {"/addOrUpPackage"})
    public String addOrUpPackage() {
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
            if (params.get("mnemonic")==null||params.get("mnemonic").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mnemonic不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goodsName")==null||params.get("goodsName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goodsStatus")==null||params.get("goodsStatus").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goodsStatus不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isDsc")==null||params.get("isDsc").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isDsc不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isTakeout")==null||params.get("isTakeout").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isTakeout不能为空！");
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
            if (params.get("groupGoods")==null||params.get("groupGoods").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupGoods不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = packageService.addOrUpPackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询套餐详情
     * @return
     */
    @RequestMapping("/getPackageDetail")
    public String getPackageDetail() {
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
            apiRest = packageService.getPackageDetail(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 套餐排序
     * @return
     */
    @RequestMapping(value = "/orderPackage")
    public String orderPackage(){
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
            apiRest = packageService.orderPackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询套餐排序
     * @return
     */
    @RequestMapping(value = "/listOrderPackage")
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
            apiRest = packageService.listOrderPackage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
