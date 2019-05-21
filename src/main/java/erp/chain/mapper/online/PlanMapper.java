package erp.chain.mapper.online;

import erp.chain.domain.online.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-06-27.
 */
@Mapper
public interface PlanMapper {
    List<Plan> findAllPlanInfos(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("planGroupIds") List<BigInteger> planGroupIds);
}
