package erp.chain.mapper.supply;

import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.domain.supply.BranchRequireGoodsSetting;
import erp.chain.model.supply.QueryBranchRequireGoodsSettingModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * 机构要货关系
 *
 * @author hefuzi 2016-12-15
 */
@Mapper
public interface BranchRequireGoodsSettingMapper {
    /**
     * 分页查询-
     */
    public List<MapUnderscoreToCamelCase> queryPager(QueryBranchRequireGoodsSettingModel model);

    /**
     * 统计查询-
     */
    public long queryCount(QueryBranchRequireGoodsSettingModel model);

    /**
     * List查询-
     */
    public List<BranchRequireGoodsSetting> queryList(QueryBranchRequireGoodsSettingModel model);

    /**
     * id查询-
     */
    public BranchRequireGoodsSetting get(@Param("id") BigInteger id);
    /**
     * id查询-
     */
    public MapUnderscoreToCamelCase getInfo(@Param("id") BigInteger id);

    /**
     * id删除-
     *
     * @return 0-失败(版本过期或不存在) 1-成功
     */
    public int delete(@Param("id") BigInteger id, @Param("empId") BigInteger empId);

    /**
     * 批量保存-
     */
    public int saveList(@Param("list") List<BranchRequireGoodsSetting> list);

    int updatePrice(@Param("id") BigInteger id, @Param("empId") BigInteger empId,
                    @Param("version") BigInteger version, @Param("price") BigDecimal price);

    Long countDetail(@Param("tenantId")BigInteger  bigInteger,@Param("branchId")BigInteger branchId,
                    @Param("distributionCenterId")BigInteger distributionCenterId,@Param("goodsId")BigInteger goodsId);
    List<BranchRequireGoodsSetting> findAllByGoodsIdInListAndBranchIdAndTenantIdAndDistributionCenterId(@Param("goodsIds")List<BigInteger> goodsIds, @Param("branchId")BigInteger branchId, @Param("tenantId")BigInteger tenantId, @Param("distributionCenterId")BigInteger distributionCenterId);
}