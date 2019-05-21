package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.GoodsProduceRelationService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 加工配方
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/14
 */
@Controller
@ResponseBody
@RequestMapping("/goodsProduceRelation")
public class GoodsProduceRelationController{
    @Autowired
    private GoodsProduceRelationService goodsProduceRelationService;

    /**
     * 查询加工商品
     * @return
     */
    @RequestMapping("/queryProduceGoods")
    public String queryProduceGoods(){
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
            apiRest = goodsProduceRelationService.queryProduceGoods(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询加工商品组合明细
     * @return
     */
    @RequestMapping("/getRelationById")
    public String getRelationById(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("produceGoodsId")==null||params.get("produceGoodsId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数produceGoodsId不能为空！");
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
            apiRest = goodsProduceRelationService.getRelationById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 保存明细
     * @return
     */
    @RequestMapping("/saveGoodsRelation")
    public String saveGoodsRelation(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("produceGoodsId")==null||params.get("produceGoodsId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数produceGoodsId不能为空！");
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
            if (params.get("relation")==null||params.get("relation").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数relation不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = goodsProduceRelationService.saveGoodsRelation(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 删除加工配方商品
     * @return
     */
    @RequestMapping("/doDeleteRelationById")
    public String doDeleteRelationById(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("produceGoodsId")==null||params.get("produceGoodsId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数produceGoodsId不能为空！");
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
            apiRest = goodsProduceRelationService.doDeleteRelationById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
