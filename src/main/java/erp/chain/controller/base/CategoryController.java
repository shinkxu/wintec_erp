package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.CategoryService;
import erp.chain.service.o2o.VipService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 菜品分类
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/20
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/category","/baseWebService","/baseWS"})
public class CategoryController{
    @Autowired
    private CategoryService categoryService;
    /**
     * 分页查询商户分类
     * @return
     */
    @RequestMapping("/queryCatList")
    public String queryCatList() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
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
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.queryCatList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据id查询分类
     * @return
     */
    @RequestMapping("/queryCatById")
    public String queryCatById() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.queryCatById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据id修改分类名称
     * @return
     */
    @RequestMapping("/updateCatName")
    public String updateCatName() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("catName")==null||params.get("catName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数catName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("catName").length() > 20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("分类名称不能超过20个字符，添加失败！");
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
            apiRest = categoryService.updateCatName(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改分类积分
     * */
    @RequestMapping("/updateCatScore")
    public String updateCatScore(){
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
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("scoreValue") != null && !"".equals(params.get("scoreValue"))) {
                try {
                    BigInteger scoreValue = new BigInteger(params.get("scoreValue").toString());
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
                    BigDecimal scorePercent = new BigDecimal(params.get("scorePercent").toString());
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
            categoryService.updateCatScore(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 增加分类
     * @return
     */
    @RequestMapping(value = {"/addCategory","/saveCat"})
    public String addCategory() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("catName")==null||params.get("catName").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数catName不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("parentId")==null||params.get("parentId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数parentId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (!params.get("parentId").equals("-1")) {
                if (params.get("categoryType")!=null&&!params.get("categoryType").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("非一级分类不能添加是否原料属性，请确认参数准确性！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if (params.get("catName").length() > 20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("分类名称不能超过20个字符，添加失败！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.addCategory(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除分类
     * @return
     */
    @RequestMapping("delCat")
    public String delCat() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            /*if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.delCat(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取分类编码
     * @return
     */
    @RequestMapping("/getCatCode")
    public String getCatCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("parentId")==null||params.get("parentId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数parentId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(!params.get("parentId").equals("-1")){
                if (params.get("oneCode")==null||params.get("oneCode").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("添加二级分类时，一级分类编码oneCode不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = categoryService.getCatCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/getNoChildCategoryList")
    public String getNoChildCategoryList() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.getNoChildCategoryList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取分类树数据
     * @return
     */
    @RequestMapping("/getCatTree")
    public String getCatTree(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("havePackage")!=null&&!params.get("havePackage").equals("")) {
                if(!params.get("havePackage").equals("1")){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数havePackage只能输入1！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if (params.get("haveMaterial")!=null&&!params.get("haveMaterial").equals("")) {
                if(!params.get("haveMaterial").equals("1")){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数haveMaterial只能输入1！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = categoryService.getCatTree(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @Autowired
    VipService vipService;
    /**
     * 查询商户下会员数量（portal）
     * @return
     */
    @RequestMapping(value = "/vipCount")
    public String vipCount(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.vipCount(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分类排序
     * @return
     */
    @RequestMapping(value = "/orderCategory")
    public String orderCategory(){
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
            apiRest = categoryService.orderCategory(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询一级分类排序
     * @return
     */
    @RequestMapping(value = "/listOrderCategory")
    public String listOrderCategory(){
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
            apiRest = categoryService.listOrderCategory(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询二级分类排序
     * @return
     */
    @RequestMapping(value = "/listOrderSecondCategory")
    public String listOrderSecondCategory(){
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
            if (params.get("firstCatId")==null||params.get("firstCatId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数firstCatId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = categoryService.listOrderSecondCategory(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
