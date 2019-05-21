package erp.chain.mapper;

import erp.chain.domain.o2o.DietOrderPromotionR;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/4/18.
 */
@Mapper
public interface DietOrderPromotionRMapper {
    List<DietOrderPromotionR> select(Map params);
}
