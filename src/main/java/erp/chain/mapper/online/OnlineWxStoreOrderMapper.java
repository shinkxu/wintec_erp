package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineWxStoreOrder;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2018-05-14.
 */
@Mapper
public interface OnlineWxStoreOrderMapper {
    long insert(OnlineWxStoreOrder onlineWxStoreOrder);

    long update(OnlineWxStoreOrder onlineWxStoreOrder);

    OnlineWxStoreOrder find(SearchModel searchModel);
}
