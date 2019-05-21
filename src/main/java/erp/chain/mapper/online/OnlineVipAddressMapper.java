package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineVipAddress;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Mapper
public interface OnlineVipAddressMapper {
    long insert(OnlineVipAddress onlineVipAddress);
    long update(OnlineVipAddress onlineVipAddress);
    OnlineVipAddress find(SearchModel searchModel);
    List<OnlineVipAddress> findAll(SearchModel searchModel);
}
