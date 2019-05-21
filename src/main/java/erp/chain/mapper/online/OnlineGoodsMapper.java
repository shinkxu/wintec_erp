package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineGoods;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-10.
 */
@Mapper
public interface OnlineGoodsMapper {
    OnlineGoods find(SearchModel searchModel);

    List<OnlineGoods> findAll(SearchModel searchModel);

    Map<String, Object> findEffectiveMenu(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("menuType") int menuType);

    List<Map<String, Object>> listCornerGoodsInfos(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("menuId") BigInteger menuId);

    List<Map<String, Object>> listGoodsInfos(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("menuId") BigInteger menuId, @Param("goodsIds") List<BigInteger> goodsIds);

    List<Map<String, Object>> findPackageInfos(@Param("packageIds") List<BigInteger> packageIds);

    List<Map<String, Object>> findPackageGroupInfos(@Param("packageIds") List<BigInteger> packageIds);

    Map<String, Object> findGoodsInfo(@Param("tenantId") BigInteger tenantId,
                                      @Param("branchId") BigInteger branchId,
                                      @Param("headquartersBranchId") BigInteger headquartersBranchId,
                                      @Param("menuId") BigInteger menuId,
                                      @Param("barCode") String barCode,
                                      @Param("weighPlu") Integer weighPlu,
                                      @Param("isUseHqGoods") int isUseHqGoods);

    List<Map<String, Object>> findAllGoodsInfos(@Param("tenantId") BigInteger tenantId,
                                                @Param("branchId") BigInteger branchId,
                                                @Param("headquartersBranchId") BigInteger headquartersBranchId,
                                                @Param("menuId") BigInteger menuId,
                                                @Param("barCode") String barCode,
                                                @Param("weighPlu") Integer weighPlu,
                                                @Param("goodsName") String goodsName,
                                                @Param("isUseHqGoods") int isUseHqGoods);
}
