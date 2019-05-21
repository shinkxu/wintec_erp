package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.WxCard;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2018-04-23.
 */
@Mapper
public interface WxCardMapper {
    long insert(WxCard wxCard);

    long update(WxCard wxCard);

    WxCard find(SearchModel searchModel);
}
