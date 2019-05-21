package erp.chain.mapper;


import erp.chain.domain.DietGamePrizeItem;
import erp.chain.domain.DietGameScratchCard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DietGamePrizeItemMapper extends BaseMapper {
    List<DietGamePrizeItem> dietGamePrizeItemList(Map params);
    int update(Map params);
    int update2(DietGamePrizeItem dietGamePrizeItem);
    int insert(Map params);
    DietGamePrizeItem findById(Map params);
}