package erp.chain.mapper.online;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-08-01.
 */
@Mapper
public interface OnlineDietPromotionMapper {
    List<Map<String, Object>> listFestivals(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    List<Map<String, Object>> statisticsEffectiveBranches(@Param("dietPromotionIds") List<BigInteger> dietPromotionIds);
}
