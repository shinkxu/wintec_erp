package erp.chain.mapper;


import erp.chain.domain.DietPromotionBuyGive;
import erp.chain.domain.DietPromotionFestival;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionFestivalMapper extends BaseMapper {
    List<DietPromotionFestival> dietPromotionFestivalList(Map params);
    int update(Map params);
    int update2(DietPromotionFestival dietPromotionFestival);
    int insert(Map params);
    DietPromotionFestival findByPromotionId(Map params);
}