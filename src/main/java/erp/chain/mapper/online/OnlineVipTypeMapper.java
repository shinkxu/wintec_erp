package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineVipType;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2018-04-13.
 */
@Mapper
public interface OnlineVipTypeMapper {
    OnlineVipType find(SearchModel searchModel);
}
