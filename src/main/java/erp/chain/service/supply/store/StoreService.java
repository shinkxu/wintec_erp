package erp.chain.service.supply.store;

import erp.chain.model.Pager;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.model.supply.store.QueryStoreAccountModel;
import erp.chain.model.supply.store.QueryStoreModel;

import java.util.List;
import java.util.Map;

/**
 * 库存业务
 */
public interface StoreService {

    /**
     * 分页查询库存流水
     */
    public Pager queryStoreAccountPager(QueryStoreAccountModel model);
    /**
     * 分页查询机构库存
     */
    public Pager queryStorePager(QueryStoreModel model);

    /**
     * 分页查询商品库存
     */
    public Pager queryGoodsStorePager(QueryGoodsStoreModel model);

}
