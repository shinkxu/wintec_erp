package erp.chain.controller.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.service.base.MenuService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 菜牌
 * 删除了copyMenu接口，若需要，补充
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/24
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/menu","/baseWebService","publicWebService"})
public class MenuController{
    @Autowired
    private MenuService menuService;

    /**
     * 查询菜品单位（分页）
     * @return
     */
    @RequestMapping("/queryMenu")
    public String queryMenu() {
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
            /*if (params.get("branchId")==null|| "".equals(params.get("branchId"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            /*if (params.get("branchType")==null|| "".equals(params.get("branchType"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            /*if (params.get("isManageGoods")==null|| "".equals(params.get("isManageGoods"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isManageGoods不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            apiRest = menuService.queryMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据菜牌ID查询菜牌以及详情
     * @return
     */
    @RequestMapping("/findMenuDetail")
    public String findMenuDetail() {
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
            apiRest = menuService.findMenuDetail(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除菜牌以及详情
     * @return
     */
    @RequestMapping("/delMenu")
    public String delMenu() {
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
            apiRest = menuService.delMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取菜牌编码
     * @return
     */
    @RequestMapping("/getMenuCode")
    public String getMenuCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = menuService.getMenuCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改菜牌以及明细
     * @return
     */
    @RequestMapping("/addOrUpdateMenu")
    public String addOrUpdateMenu() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchIds")==null||params.get("branchIds").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchIds不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("cronStartAt")==null||params.get("cronStartAt").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数cronStartAt不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("cronEndAt")==null||params.get("cronEndAt").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数cronEndAt不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("goods")==null||params.get("goods").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数goods不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("name").length()>20){
                apiRest.setIsSuccess(false);
                apiRest.setError("name的长度不能超过20个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("status")==null||params.get("status").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数status不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("menuType")==null||params.get("menuType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数menuType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("createBranchId") == null || "".equals(params.get("createBranchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数createBranchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            apiRest = menuService.addOrUpdateMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询商户机构下的菜牌数据
     * tenantId
     * branchId
     */
    @RequestMapping(value = "/queryMenuInfo")
    public @ResponseBody String queryMenuInfo() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        LogUtil.logInfo("接口queryMenuInfo请求参数:"+params);
        try{
            apiRest= menuService.queryMenuInfo(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌数据失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌数据失败,服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询商户机构下的菜牌数据
     * tenantId
     * branchId
     */
    @RequestMapping(value = "/queryMenuInfoPackage")
    public @ResponseBody String queryMenuInfoPackage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        LogUtil.logInfo("接口queryMenuInfo请求参数:"+params);
        try{
            apiRest= menuService.queryMenuInfoPackage(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌数据失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌数据失败,服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导出电子秤商品
     * */
    @RequestMapping("/exportWeighGoods")
    public String exportWeighGoods(){
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
            apiRest = menuService.exportWeighGoods(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 更新菜牌明细价格
     * 1.保留原菜牌基本信息
     * 2.更新价格明细+不更新
     * */
    @RequestMapping("/updateMenuDetailPrice")
    public String updateMenuDetailPrice(){
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
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = menuService.updateMenuDetailPrice(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
