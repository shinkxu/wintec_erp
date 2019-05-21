package erp.chain.controller.supply.store;

import com.saas.common.util.CacheUtils;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.supply.store.StoreCompute;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.model.supply.store.QueryStoreAccountModel;
import erp.chain.model.supply.store.QueryStoreModel;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.service.supply.store.StoreService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.io.IOException;

/**
 * 库存相关
 */
@Controller
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private StoreComputeService storeComputeService;
    @Autowired
    private StoreService storeService;

    private final Log logger = LogFactory.getLog(getClass());


    /**
     * 计算库存
     */
    @ResponseBody
    @RequestMapping("/executeStoreCompute")
    public String executeStoreCompute() {
        String json = "";
        ApiRest apiRest = new ApiRest();
        apiRest.setIsSuccess(true);
        String key="";
        String errorKey="";
        while (true) {
            try {
                key=PropertyUtils.getDefault("redis_topics");
                errorKey=PropertyUtils.getDefault("redis_topics")+"_error";
                json = PartitionCacheUtils.rpop(key);
                //没有库存单结束本次计算
                if (json == null || json.isEmpty()) {
                    break;
                }
                //LogUtil.logInfo("处理库存的数据"+json);
                StoreCompute compute = BeanUtils.toBean(json, StoreCompute.class);

                StoreCompute.executeStoreCompute(compute,storeComputeService,logger);

            } catch (Exception e) {
                logger.error("计算库存错误：" + e.getMessage(), e);
                LogUtil.logInfo("计算库存错误信息"+e.getMessage());
                PartitionCacheUtils.lpush(errorKey,json);
                apiRest.setIsSuccess(false);
                apiRest.setError(e.getMessage());
                break;
            }
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @ResponseBody
    @RequestMapping("/executeStoreComputeError")
    public String executeStoreComputeError() {
        String json = "";
        ApiRest apiRest = new ApiRest();
        apiRest.setIsSuccess(true);
        String key="";
        String errorKey="";
        while (true) {
            try {
                key=PropertyUtils.getDefault("redis_topics");
                errorKey=PropertyUtils.getDefault("redis_topics")+"_error";
                json = PartitionCacheUtils.rpop(errorKey);
                //没有库存单结束本次计算
                if (json == null || json.isEmpty()) {
                    break;
                }
                //LogUtil.logInfo("处理库存的数据Error"+json);
                StoreCompute compute = BeanUtils.toBean(json, StoreCompute.class);

                StoreCompute.executeStoreCompute(compute,storeComputeService,logger);

            } catch (Exception e) {
                logger.error("计算库存错误：" + e.getMessage(), e);
                LogUtil.logInfo("计算库存错误信息Error"+e.getMessage());
                PartitionCacheUtils.lpush(key,json);
                apiRest.setIsSuccess(false);
                apiRest.setError(e.getMessage());
                break;
            }
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询库存流水
     */
    @ResponseBody
    @RequestMapping("/queryStoreAccountPager")
    public String queryStoreAccountPager(){
        ApiRest apiRest = new ApiRest();
        QueryStoreAccountModel model = AppHandler.bindParam(QueryStoreAccountModel.class);
//        model.tenantId = new BigInteger("439");
        if (!model.validate()){
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeService.queryStoreAccountPager(model).toMap());
        apiRest.setIsSuccess(true);
        apiRest.setResult("SUCCESS");
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构库存流水
     */
    @ResponseBody
    @RequestMapping("/queryStorePager")
    public String queryStorePager(){
        ApiRest apiRest = new ApiRest();
        QueryStoreModel model = AppHandler.bindParam(QueryStoreModel.class);
//        model.tenantId = new BigInteger("439");
        if("null".equals(model.getCatIds())){
            model.setCatIds(null);
        }
        if (!model.validate()){
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(storeService.queryStorePager(model).toMap());
        apiRest.setIsSuccess(true);
        apiRest.setResult("SUCCESS");
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 分页查询指定机构商品库存信息(所有机构商品)
     */
    @ResponseBody
    @RequestMapping("/queryGoodsStorePager")
    public String queryGoodsStorePager(){
        ApiRest apiRest = new ApiRest();
        QueryGoodsStoreModel model = AppHandler.bindParam(QueryGoodsStoreModel.class);
//        model.tenantId = new BigInteger("439");
//        model.rootBranchId = model.branchId;
        if (!model.validate()){
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(storeService.queryGoodsStorePager(model).toMap());
        apiRest.setIsSuccess(true);
        apiRest.setResult("SUCCESS");
        return BeanUtils.toJsonStr(apiRest);
    }
























    @ResponseBody
    @RequestMapping("/addData")
    public String addData() throws IOException {
        for (int x = 0; x <= 8; x++) {
            String json = "{\"tenantId\":439,\"branchId\":17,\"goodsId\":866,\"price\":2222,\"quantity\":2,\"code\":\"RK160503000002\",\"occurType\":3,\"billCreateTime\":\"2016-05-03 11:13:25\"}";
//            String json = "{\"tenantId\":1348,\"branchId\":898,\"goodsId\":897,\"price\":10,\"quantity\":2,\"code\":\"RK160503000002\",\"occurType\":3,\"billCreateTime\":\"2016-05-03 11:13:25\"}"
            CacheUtils.lpush(PropertyUtils.getDefault("redis_topics"), json);
        }
        return "ok";
    }



}
