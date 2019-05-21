package erp.chain.mapper;


import erp.chain.domain.DietPromotionTotalReduce;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionTotalReduceMapper extends BaseMapper {
    List<DietPromotionTotalReduce> dietPromotionTotalReduceList(Map params);
    List<HashMap<String,Object>> dietPromotionTotalReduceList1(Map params);
    int update(Map params);
    int insert(Map params);
    DietPromotionTotalReduce findDietPromotionTotalReduceByDietPromotionId(Map params);
    DietPromotionTotalReduce findByDietPromotionId(@Param("dietPromotionId") BigInteger dietPromotionId);
}