package erp.chain.mapper;


import erp.chain.domain.DietPromotionFestival;
import erp.chain.domain.DietPromotionPayback;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionPaybackMapper extends BaseMapper {
    List<DietPromotionPayback> dietPromotionPaybackList(Map params);
    int update(Map params);
    int insert(Map params);
    DietPromotionPayback findDietPromotionPaybackByDietPromotionId(Map params);
}