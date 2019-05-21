package erp.chain.mapper.supply.store;

import erp.chain.domain.supply.store.PsOrder;
import erp.chain.domain.supply.store.PsOrderDetail;
import erp.chain.model.supply.store.QueryPsOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * 配送出入库单
 */
@Mapper
public interface PsOrderMapper {
    int savePsOrder(PsOrder order);

    int savePsOrderDetail(@Param("details") List<PsOrderDetail> details,@Param("orderId") BigInteger orderId);

    long psOrderCount(QueryPsOrderModel model);

    List<HashMap<String,Object>> queryPsOrderPager(QueryPsOrderModel model);
    List<HashMap<String,Object>> queryPsOrderDetailList(@Param("orderId") BigInteger orderId);

    int updatePsOrder(PsOrder order);

    int delPsOrderDetails(@Param("orderId") BigInteger orderId);
    int delOrder(@Param("orderId") BigInteger orderId, @Param("version") Long version,@Param("empId") BigInteger empId);

    int auditOrder(@Param("orderId")BigInteger orderId,@Param("version") Long version, @Param("empId")BigInteger empId);
}
