package erp.chain.mapper;


import erp.chain.domain.DietPromotionAmount;
import erp.chain.domain.DietPromotionSpecial;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionAmountMapper extends BaseMapper {
    List<DietPromotionAmount> dietPromotionAmountList(Map params);
    int update(Map params);
    int insert(Map params);
    DietPromotionAmount findByCondition(Map params);
    List<HashMap<String,Object>> queryAmountDetailList(BigInteger dietPromotionId);
}