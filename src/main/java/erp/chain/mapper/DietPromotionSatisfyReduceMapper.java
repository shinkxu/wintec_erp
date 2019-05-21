package erp.chain.mapper;


import erp.chain.domain.DietPromotionSatisfyReduce;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionSatisfyReduceMapper extends BaseMapper {
    List<DietPromotionSatisfyReduce> dietPromotionSatisfyReduceList(Map params);
    int update(Map params);
    List<HashMap<String,Object>> querySatisfyReduceDetailList(BigInteger dietPromotionId);
    DietPromotionSatisfyReduce findByCondition(Map params);
}