package erp.chain.mapper.online;

import erp.chain.domain.o2o.VipSpecialPromotion;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2018-05-14.
 */
@Mapper
public interface VipSpecialPromotionMapper {
    VipSpecialPromotion find(SearchModel searchModel);
}
