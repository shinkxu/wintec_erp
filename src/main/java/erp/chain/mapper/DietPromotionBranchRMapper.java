package erp.chain.mapper;


import erp.chain.domain.DietPromotionBranchR;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionBranchRMapper extends BaseMapper {
    List<DietPromotionBranchR> dietPromotionBranchRList(Map params);
    int update(Map params);
    int insert(Map params);
    DietPromotionBranchR findByBranchIdAndDietPromotionId(@Param("branchId") BigInteger branchId, @Param("dietPromotionId")  BigInteger dietPromotionId);
}
