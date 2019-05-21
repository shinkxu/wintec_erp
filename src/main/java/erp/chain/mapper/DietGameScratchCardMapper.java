package erp.chain.mapper;


import erp.chain.domain.DietGameScratchCard;
import erp.chain.domain.DietPromotionPayback;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DietGameScratchCardMapper extends BaseMapper {
    List<DietGameScratchCard> dietGameScratchCardList(Map params);
    int update(Map params);
    int insert(Map params);
    DietGameScratchCard findByDietPromotionId(Map params);
}