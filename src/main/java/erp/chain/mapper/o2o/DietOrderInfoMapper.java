package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.DietOrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/21.
 */
@Mapper
public interface DietOrderInfoMapper {
    List<DietOrderInfo> select(Map params);
    BigDecimal sumTotalAmount(Map params);
    Long countInfo(Map params);
    DietOrderInfo findByCondition(Map params);
    int update(DietOrderInfo dietOrderInfo);
    int insert(DietOrderInfo dietOrderInfo);
    DietOrderInfo findByTenantIdAndBranchIdAndId(@Param("tenantId") BigInteger tenantId,
                                                 @Param("branchId")BigInteger branchId,
                                                 @Param("id")BigInteger id);
    List<DietOrderInfo> findAllByTenantIdAndBranchIdAndIdInList(@Param("tenantId") BigInteger tenantId,
                                                                @Param("branchId") BigInteger branchId,
                                                                @Param("ids") List<BigInteger> ids);
    DietOrderInfo findByTenantIdAndBranchIdAndOrderCode(@Param("tenantId") BigInteger tenantId,
                                                        @Param("branchId") BigInteger branchId,
                                                        @Param("orderCode") String orderCode);
    List<DietOrderInfo> selectOrderForIncome(Map params);
}
