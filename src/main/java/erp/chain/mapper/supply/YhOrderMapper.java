package erp.chain.mapper.supply;

import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.domain.supply.YhOrder;
import erp.chain.domain.supply.YhOrderDetail;
import erp.chain.model.supply.QueryYhGoodsModel;
import erp.chain.model.supply.QueryYhGoodsSumModel;
import erp.chain.model.supply.QueryYhOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 要货相关
 */
@Mapper
public interface YhOrderMapper {
    long queryYhGoodsCount(@Param("model") QueryYhGoodsModel model, @Param("goodsIds") String[] goodsIds, @Param("yhSetting") boolean yhSetting);

    long yhSettingCount(@Param("yhBranchId") BigInteger yhBranchId, @Param("psBranchId") BigInteger psBranchId);

    List<HashMap<String, Object>> queryYhGoodsPager(@Param("model") QueryYhGoodsModel model, @Param("goodsIds") String[] goodsIds,
                                                    @Param("yhSetting") boolean yhSetting);

    int saveYhOrder(YhOrder order);

    int saveYhOrderDetail(@Param("details") List<YhOrderDetail> details, @Param("orderId") BigInteger orderId);

    long yhOrderCount(QueryYhOrderModel model);

    List<HashMap<String, Object>> queryYhOrderPager(QueryYhOrderModel model);
    List<HashMap<String, Object>> queryYhOrderSum(QueryYhOrderModel model);

    List<HashMap<String, Object>> queryYhOrderDetailList(@Param("orderId") BigInteger orderId);

    List<HashMap<String, Object>> queryYhOrderDetailListTotal(@Param("orderId") BigInteger orderId);

    boolean isAutomaticExaminationOfBranch(@Param("psBranchId") BigInteger psBranchId);

    int auditOrder(@Param("orderId") BigInteger orderId, @Param("auditStatus") Integer auditStatus,
                   @Param("version") Long version, @Param("empId") BigInteger empId,@Param("auditAt") String auditAt);

    int delOrderDetail(@Param("orderId") BigInteger orderId);

    int delOrder(@Param("orderId") BigInteger orderId, @Param("version") Long version, @Param("empId") BigInteger empId);

    int updateYhOrder(@Param("orderId") BigInteger orderId, @Param("totalAmount") BigDecimal totalAmount,
                      @Param("version") Long version, @Param("empId") BigInteger empId);

    MapUnderscoreToCamelCase<String,Object> queryYhGoodsSumCount(QueryYhGoodsSumModel model);

    List<HashMap<String, Object>> queryYhGoodsSumPager(QueryYhGoodsSumModel model);

    List<Map<String, Object>> listRequireGoodsByDockingExternalSystem(Map<String, Object> params);
    List<Map<String, Object>> listRequireGoods(Map<String, Object> params);
    Map<String, Object> queryDaqGraspBtype(@Param("tenantId") BigInteger tenantId, @Param("ourBranchId") BigInteger ourBranchId, @Param("distributionCenterId") BigInteger distributionCenterId, @Param("tenantCode") String tenantCode);
    String queryDaqGraspPtypeNameByTypeId(@Param("typeId") String typeId, @Param("tenantCode") String tenantCode);
    List<Map<String, Object>> queryRequireGoodsOrderDetails(@Param("tenantId") BigInteger tenantId, @Param("tenantCode") String tenantCode, @Param("distributionCenterId") BigInteger distributionCenterId, @Param("requireGoodsOrderId") BigInteger requireGoodsOrderId);
    YhOrder findByIdAndTenantIdAndBranchId(@Param("id") BigInteger id, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    List<Map<String, Object>> queryRequireGoodsOrderDetailsByRequireGoodsOrderId(@Param("requireGoodsOrderId") BigInteger requireGoodsOrderId);
    YhOrder findByRequireGoodsOrderNo(@Param("requireGoodsOrderNo") String requireGoodsOrderNo);
    int updateRequireGoodsOrder(YhOrder yhOrder);

    YhOrder findByRequireGoodsOrderNoAndId(@Param("requireGoodsOrderNo") String requireGoodsOrderNo, @Param("requireGoodsOrderId") BigInteger requireGoodsOrderId);

    List<Map> queryLastYhOrder(Map params);
}
