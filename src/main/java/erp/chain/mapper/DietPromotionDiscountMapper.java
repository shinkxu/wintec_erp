package erp.chain.mapper;


import erp.chain.domain.DietPromotionDiscount;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionDiscountMapper extends BaseMapper {
    List<DietPromotionDiscount> dietPromotionDiscountList(Map params);
    int update(Map params);
    int insert(Map params);
    List<HashMap<String,Object>> queryDiscountDetailList(BigInteger dietPromotionId);
    DietPromotionDiscount findByCondition(Map params);
}