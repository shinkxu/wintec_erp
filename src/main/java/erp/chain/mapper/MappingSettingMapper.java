package erp.chain.mapper;

import erp.chain.domain.GoodsMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by lipeng on 2017/5/8.
 */

@Mapper
public interface MappingSettingMapper extends BaseMapper {

    /**
     * 分页查询商品对应关系
     */
//    List<Map> listGoodsMapping(Map params);

    Long listGoodsMappingSum(Map params);

    /**
     * 分页显示第三方商品（没有与goods表映射的产品）
     */
//    List<Map> listThirdGoods(Map params);

    Long listThirdGoodsSum(Map params);

    /**
     * 删除取消关联的数据
     */
//    int deleteGoodsMapping(Map params);

    /**
     * 保存商品对应关系
     */
//    int saveGoodsMapping(@Param("goodsMappings") List goodsMappings);

    /**
     * 分页查询机构对应关系
     */
    List<Map> listBranchMapping(Map params);

    Long listBranchMappingSum(Map params);

    /**
     * 分页查询第三方机构（仅显示没有对应关系的机构）
     */
    List<Map> listThirdBranch(Map params);

    Long listThirdBranchSum(Map params);

    /**
     * 删除取消关联数据
     */
    int deleteBranchMapping(Map params);

    /**
     * 保存机构对应关系
     */
    int saveBranchMapping(@Param("branchMappings") List branchMappings);

    List<Map<String, Object>> listGoodsMapping(@Param("tenantId") BigInteger tenantId,
                                               @Param("tenantCode") String tenantCode,
                                               @Param("branchId") BigInteger branchId,
                                               @Param("distributionCenterId") BigInteger distributionCenterId,
                                               @Param("goodsCodeOrName") String goodsCodeOrName,
                                               @Param("offset") int offset,
                                               @Param("maxResults") int maxResults,
                                               @Param("onlySelf") int onlySelf);

    long queryGoodsMappingCount(@Param("tenantId") BigInteger tenantId,
                                @Param("tenantCode") String tenantCode,
                                @Param("branchId") BigInteger branchId,
                                @Param("distributionCenterId") BigInteger distributionCenterId,
                                @Param("goodsCodeOrName") String goodsCodeOrName,
                                @Param("onlySelf") int onlySelf);

    List<Map<String, Object>> listThirdGoods(@Param("tenantId") BigInteger tenantId,
                                             @Param("tenantCode") String tenantCode,
                                             @Param("distributionCenterId") BigInteger distributionCenterId,
                                             @Param("addGoodsIds") String addGoodsIds,
                                             @Param("deleteGoodsIds") String deleteGoodsIds,
                                             @Param("goodsCodeOrName") String goodsCodeOrName,
                                             @Param("offset") int offset,
                                             @Param("maxResults") int maxResults);

    long queryThirdGoodsCount(@Param("tenantId") BigInteger tenantId,
                             @Param("tenantCode") String tenantCode,
                             @Param("distributionCenterId") BigInteger distributionCenterId,
                             @Param("addGoodsIds") String addGoodsIds,
                             @Param("deleteGoodsIds") String deleteGoodsIds,
                             @Param("goodsCodeOrName") String goodsCodeOrName);
    long deleteGoodsMapping(@Param("tenantId") BigInteger tenantId, @Param("distributionCenterId") BigInteger distributionCenterId, @Param("ourGoodsIds") List<BigInteger> ourGoodsIds);
    long saveGoodsMapping(@Param("goodsMappings") List<GoodsMapping> goodsMappings);
}

