package erp.chain.mapper.supply.store;

import erp.chain.domain.supply.store.GoodsStoreInfo;
import erp.chain.domain.supply.store.Store;
import erp.chain.domain.supply.store.StoreAccount;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.model.supply.store.QueryStoreAccountModel;
import erp.chain.model.supply.store.QueryStoreModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存dao
 */
@Mapper
public interface StoreMapper {
    /**
     * 查询机构商品库存信息。
     * 商品不存在，则不返回。
     * 商品不存在库存，则只返回商品信息
     */
    public List<GoodsStoreInfo> queryGoodsStoreInfoList(@Param("tId") BigInteger tId,@Param("isStore") boolean isStore,
                                                        @Param("bId") BigInteger bId, @Param("goodsIds") String[] goodsIds);

    /**
     * 保存库存
     */
    public int saveStore(Store store);

    /**
     * 修改库存
     * @return 0-版本过期
     */
    public int updateStore(Store store);

    /**
     * 保存库存流水
     */
    public int saveStoreAccount(StoreAccount account);

    /**
     * 更新商品进货价
     */
    public int updateGoodsPurchasingPrice(@Param("goodsId") BigInteger goodsId, @Param("purchasingPrice")BigDecimal purchasingPrice);

    /**
     * 分页查询库存流水
     */
    public List<HashMap<String,Object>> queryStoreAccountPager(QueryStoreAccountModel model);
    /**
     * 统计查询库存流水
     */
    public Map<String,Object> queryStoreAccountCount(QueryStoreAccountModel model);
    /**
     * 统计机构库存
     */
    public long queryStoreCount(QueryStoreModel model);
    /**
     * 分页查询机构库存
     */
    public List<HashMap<String,Object>> queryStorePager(QueryStoreModel model);

    /**
     *机构库存footer
     * */
    public List<Map> queryStoreFooter(QueryStoreModel model);

    /**
     * 统计商品库存
     */
    long queryGoodsStoreCount(QueryGoodsStoreModel model);
    /**
     * 分页查询商品库存
     */
    List<HashMap<String,Object>> queryGoodsStorePager(QueryGoodsStoreModel model);
    List<Map> queryOrderPages(Map params);
    long queryOrderPagesCount(Map params);
    List<Map> queryCheckOrderPages(Map params);
    long queryCheckOrderPagesCount(Map params);
    List<Map> queryStorePagerWS(Map params);
    List<Map> queryStorePagerWSCount(Map params);
    List<Map> queryStoreAccountPagerWS(Map params);
    List<Map> queryStoreAccountPagerWSCount(Map params);

    Store selectByGoodsId(Map map);
}
