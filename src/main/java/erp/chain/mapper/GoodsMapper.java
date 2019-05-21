package erp.chain.mapper;


import erp.chain.domain.Goods;
import erp.chain.domain.ShopGoods;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import net.sf.json.JSONArray;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper extends BaseMapper {
    List<Map> queryGoodsList(Map params);

    Long queryGoodsListSum(Map params);

    List<Map> queryGoodsWithStoreInfoList(Map params);

    Long queryGoodsWithStoreInfoListSum(Map params);

    Goods findGoodsByIdAndTenantId(Map params);
    Goods findGoodsByIdN(Map params);

    Goods findByCondition(Map params);
    List<Goods> findListByCondition(Map params);
    List<Map> findParentIdByCode(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsCode") String goodsCode);

    int insert(Goods goods);

    int update(Goods goods);

    int saveList(@Param("goodsList")List<Goods> goodsList);

    String getGoodsCode(Map params);
    String getInputMaxCode(@Param("tenantId") BigInteger tenantId);

    List<Map> findGoodsWithSpec(Map params);

    /**
     * 统计菜品库存
     *
     * @param model
     * @return
     */
    List<Map> sumGoodsStore(QueryGoodsStoreModel model);

    List<Map> countGroupGoods(QueryGoodsStoreModel model);

    List<Map> countMenuGoods(QueryGoodsStoreModel model);

    List<Map> countDiet(QueryGoodsStoreModel model);

    List<Map> countRelation(QueryGoodsStoreModel model);

    List<Map> querySpecGoodsIds(@Param("tenantId") BigInteger tenantId, @Param("ids") String ids);


    /**
     * 查询总部机构商品
     *
     * @param goodsIds 商品id集合，如果为null或空查询所有
     * @author hxh
     */
    List<Map<String, Object>> queryRootBranchGoodsInfo( @Param("ids") BigInteger[] goodsIds);

    /**
     * 批量设置菜品公共属性
     * */
    int batchSetup(Map params);
    int updateRelated(Map params);

    /**
     * 查询子规格
     * */
    List<Map> querySpecGoods(Map params);

    /**
     * 删除子规格
     * */
    int deleteSpec(Map params);

    Goods findByIdAndTenantIdAndBranchIdAndGoodsTypeNotEqual(@Param("goodsId")String goodsId,@Param("tenantId")String tenantId,@Param("branchId")String branchId,@Param("goodsType")String goodsType);

    String findMaxGoodsCodeByTenantIdAndCategoryCode(@Param("tenantId") BigInteger tenantId, @Param("categoryCode") String categoryCode);

    Goods findByTenantIdAndBranchIdGoodsNameAndIsDeleted(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsName") String goodsName);

    int deleteGoodsOrder(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("goodsJsonArr") JSONArray goodsJsonArr);
    int insertGoodsOrder(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("catId") BigInteger catId,@Param("goodsJsonArr") JSONArray goodsJsonArr);
    int updateLastUpdateAt(@Param("goodsJsonArr") JSONArray goodsJsonArr);
    int deleteGoodsOrderByCatId(@Param("tenantId") BigInteger tenantId,@Param("goodsId") BigInteger goodsId);
    List<Map> listOrderGoods(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("catId") BigInteger catId,@Param("onlySelf") int onlySelf);
    List<Map> listOrderPackage(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("catId") BigInteger catId,@Param("onlySelf") int onlySelf);

    /*判断称重PLU是否被占用*/
    int isPluUsed(Map params);
    List<Map> getWeighGoods(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId);

    /*根据商品部分信息查询完整信息*/
    List<Map> queryGoodsInfo(Map params);

    /*查询商品基本信息和库存*/
    List<Map> queryGoodsStoreInfo(Map params);
    List<Map> queryGoodsStoreInfoByBarCode(Map params);

    /*查询商品是否在套餐中被使用*/
    int getGroupsByGoodsId(@Param("tenantId")BigInteger tenantId,@Param("goodsId")BigInteger goodsId);

    /*根据关联ID查询套餐信息*/
    List<Map> queryPackageByGoodsId(Map params);

    List<Map<String, Object>> findElemeMappingGoodses(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("elemeGoodsIds") List<BigInteger> elemeGoodsIds);
    List<Map<String, Object>> listGoodses(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("categoryId") BigInteger categoryId, @Param("goodsId") BigInteger goodsId, @Param("codeOrName") String codeOrName);
    List<Map<String, Object>> findAllElemeGoodsInfos(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("elemeGoodsIds") List<BigInteger> elemeGoodsIds);

    /*根据编码查询、修改商品*/
    List<Goods> findGoodsByCode(Map params);

    /*修改商品单位名称和分类名称*/
    int updateUnitOrCategory(Map params);

    void deleteGoodsScore(@Param("tenantId") BigInteger tenantId,@Param("goodsId") BigInteger goodsId);
    void saveGoodsScore(Map params);
    Map getGoodsScoreInfo(Map params);

    //批量删除
    Long delGoods(Map params);
    Long delStoreOrderDetailOfGoods(Map params);
    Long delLyStoreOrderDetailOfGoods(Map params);
    Long delCheckOrderDetailOfGoods(Map params);
    Long delSyStoreOrderDetailOfGoods(Map params);
    Long delPsStoreOrderDetailOfGoods(Map params);

    //检验条码
    List<Map> checkBarCode(Map params);

    //检验条码(包含库存管理条件)
    List<Map> doCheckBarCode(Map params);

    //电子秤
    List<Map> scaleList();
    Map scaleById(Map prams);

    //排序相关
    List<Map> orderedGoods(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,@Param("catId")BigInteger catId);
    List<Goods> getGoodsByCatId(@Param("tenantId")BigInteger tenantId,@Param("catId")BigInteger catId);
    int deleteGoodsOrderV2(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId);
    int insertGoodsOrderV2(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("goodsJsonArr") JSONArray goodsJsonArr);

    int deleteMeituanGoodsToBranch(@Param("tenantId")String tenantId,@Param("branchId")String branchId,@Param("goodsId")String goodsId);

    //积分商城
    List<ShopGoods> shopGoodsList(Map params);
    int shopGoodsCount(Map params);
    int insertShopGoods(ShopGoods shopGoods);
    int isUsedName(@Param("tenantId") BigInteger tenantId, @Param("goodsName") String goodsName);
    int updateShopGoods(ShopGoods shopGoods);
    ShopGoods findShopGoodsById(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("id") BigInteger id);
    int delShopGoods(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("ids") String ids);

    int updatePrice(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("info") Map info);
}