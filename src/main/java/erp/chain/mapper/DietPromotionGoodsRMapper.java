package erp.chain.mapper;

import erp.chain.domain.DietPromotionGoodsR;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/19.
 */
@Mapper
public interface DietPromotionGoodsRMapper extends BaseMapper {
    List<DietPromotionGoodsR> dietPromotionGoodsRList(Map params);
    int update(Map params);
    int insert(Map params);
}
