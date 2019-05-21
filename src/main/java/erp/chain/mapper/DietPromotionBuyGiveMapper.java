package erp.chain.mapper;


import erp.chain.domain.DietPromotionBuyGive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionBuyGiveMapper extends BaseMapper {
    List<DietPromotionBuyGive> dietPromotionBuyGiveList(Map params);
    int update(Map params);
    int insert(Map params);
    List<HashMap<String,Object>> queryBuyGiveDetailList(BigInteger dietPromotionId);
    DietPromotionBuyGive findByCondition(Map params);
}