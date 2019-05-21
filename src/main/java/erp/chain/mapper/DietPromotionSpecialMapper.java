package erp.chain.mapper;


import erp.chain.domain.DietPromotionSpecial;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionSpecialMapper extends BaseMapper {
    List<DietPromotionSpecial> dietPromotionSpecialList(Map params);
    int update(Map params);
    int insert(Map params);
    List<HashMap<String,Object>> querySpecialDetailList(BigInteger dietPromotionId);
    DietPromotionSpecial findByCondition(Map params);
}