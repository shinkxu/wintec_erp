package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.GoodsSpecService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 菜品口味
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/23
 */
@Controller
@ResponseBody
@RequestMapping("/goodsSpec")
public class GoodsSpecController{
    @Autowired
    private GoodsSpecService goodsSpecService;

    /**
     * 口味列表
     * @return
     */
    @RequestMapping("/goodsSpecList")
    public String goodsSpecList() {
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
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsSpecService.goodsSpecList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 生成口味编码
     * @return
     */
    @RequestMapping("/getSpecCode")
    public String getSpecCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("groupId")==null||params.get("groupId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsSpecService.getSpecCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改口味
     * @return
     */
    @RequestMapping("/addOrUpdateSpec")
    public String addOrUpdateSpec() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("groupId")==null||params.get("groupId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("property")==null||params.get("property").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数property不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("property").length()>20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("口味名称不能超过20个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("price")==null||params.get("price").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数price不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsSpecService.addOrUpdateSpec(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改口味
     * @return
     */
    @RequestMapping("/delGoodsSpec")
    public String delGoodsSpec() {
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
            apiRest = goodsSpecService.delGoodsSpec(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
