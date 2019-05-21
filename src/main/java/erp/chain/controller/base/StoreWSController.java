package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.StoreWSService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 零售库存相关pos接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/23
 */
@Controller
@ResponseBody
@RequestMapping("/storeWS")
public class StoreWSController{
    @Autowired
    private StoreWSService storeWSService;
    /**
     * 保存采购单
     * @param "orderType":"采购单类型：1进货2退货 not null"
     */
    @RequestMapping("/saveStoreOrder")
    public String saveStoreOrder() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.saveStoreOrder(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 分页查询机构采购单
     */
    @RequestMapping(value = "/queryOrderPages")
    public String queryOrderPages() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.queryOrderPages(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 保存盘点单
     * @author hxh
     */
    @RequestMapping("/saveCheckOrder")
    public String saveCheckOrder() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.saveCheckOrder(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 分页查询盘点单
     */
    @RequestMapping(value = "/queryCheckPagers")
    public String queryCheckPagers() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.queryCheckOrderPages(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 分页查询库存
     */
    @RequestMapping(value = "/queryStorePager")
    public String queryStorePager() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.queryStorePagerWS(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 库存流水
     */
    @RequestMapping(value = "/queryStoreAccountPager")
    public String queryStoreAccountPager() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=storeWSService.queryStoreAccountPager(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 打包
     */
    @RequestMapping(value = "/goodsPackageIn")
    public String goodsPackageIn() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            storeWSService.saveConvertGoodsStoreOrder(params,1);
            rest.setIsSuccess(true);
            rest.setMessage("打包操作成功！");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 拆包
     */
    @RequestMapping(value = "/goodsPackageOut")
    public String goodsPackageOut() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            storeWSService.saveConvertGoodsStoreOrder(params,2);
            rest.setIsSuccess(true);
            rest.setMessage("拆包操作成功！");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 采购进退货分页查询
     * @param tenantId不能为空且是整数
     * @param tenantId不能为空且是整数
     * @author genghui
     *//*
    def queryOrderPages() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId

            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            def result = saleService.queryOrderPages(new BigInteger(tenantId), new BigInteger(branchId), params)
            rest.isSuccess = true
            rest.data = result
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.error = e.message
        }
        render rest as JSON
    }
    *//**
     * 盘点单查询
     * @param tenantId不能为空且是整数
     * @param tenantId不能为空且是整数
     * @author genghui
     *//*
    def queryCheckPagers() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId

            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            def result = checkOrderDetailService.queryCheckPages(new BigInteger(tenantId), new BigInteger(branchId), params)
            rest.isSuccess = true
            rest.data = result
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.error = e.message
        }
        render rest as JSON
    }

    *//**
     * 库存查询
     * @param tenantId不能为空且是整数
     * @param tenantId不能为空且是整数
     * @author genghui
     *//*
    def queryStorePager() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId
            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            def result = storeService.queryStoreList(new BigInteger(tenantId), new BigInteger(branchId), params)
            rest.isSuccess = true
            rest.data = result
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.error = e.message
        }
        render rest as JSON
    }

    *//**
     * 库存流水查询
     * @param tenantId不能为空且是整数
     * @param tenantId不能为空且是整数
     * @author genghui
     *//*
    def queryStoreAccountPager() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId
            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            def result = storeAccountService.queryStoreAccountPager(params, new BigInteger(tenantId), new BigInteger(branchId))
            rest.isSuccess = true
            rest.data = result
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.error = e.message
        }
        render rest as JSON
    }

    *//**
     * 打包
     * @author genghui
     *//*
    def goodsPackageIn() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId
            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            storeService.goodsPackageIn(new BigInteger(tenantId), new BigInteger(branchId), params)
            rest.message = "操作成功"
            rest.isSuccess = true
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.message = "操作失败"
            rest.error = e.message
        }
        String res = rest as JSON
        render res
    }

    *//**
     * 拆包
     * @author genghui
     *//*
    def goodsPackageOut() {
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def tenantId = params.tenantId
            def branchId = params.branchId
            Args.isInteger(tenantId, "tenantId不能为空且是整数")
            Args.isInteger(branchId, "branchId不能为空且是整数")
            storeService.goodsPackageOut(new BigInteger(tenantId), new BigInteger(branchId), params)
            rest.message = "操作成功"
            rest.isSuccess = true
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.message = "操作失败"
            rest.error = e.message
        }
        String res = rest as JSON
        render res
    }
    *//**
     * @param id 商品id不能为空  商品id是整数
     * @param tenantId 商户号不能为空 商品id是整数
     * @author genghui
     * @since 1.5.0
     *//*
    def getStoreInfoByGoodId(){
        ApiRest rest = new ApiRest(isSuccess: false)
        Map<String,String> params= AppHandler.params();
        try {
            def id=params.id
            def tenantId=params.tenantId
            def branchId=params.branchId
            Args.notNull(tenantId,"商户号不能为空！")
            Args.notNull(branchId,"机构不能为空！")
            Args.isInteger(tenantId,"商户号是整数！")
            Args.isInteger(branchId,"机构是整数！")
            Args.notNull(id,"商品id不能为空！")
            Args.isInteger(id,"商品id是整数！")
            def result = storeService.getStoreInfoByGoodId(new BigInteger(tenantId),new BigInteger(branchId),new BigInteger(id))
            rest.message = "查询成功"
            rest.isSuccess = true
            rest.data = result
        } catch (Exception e) {
            LogUtil.logError(e, params)
            rest.error = e.message
        }
        String res = rest as JSON
        render res
    }*/
}
