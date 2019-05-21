package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineDietOrderInfo;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Mapper
public interface OnlineDietOrderInfoMapper {
    long insert(OnlineDietOrderInfo onlineDietOrderInfo);
    long update(OnlineDietOrderInfo onlineDietOrderInfo);
    OnlineDietOrderInfo find(SearchModel searchModel);
    List<OnlineDietOrderInfo> findAll(SearchModel searchModel);
    long count(SearchModel searchModel);
    List<OnlineDietOrderInfo> findAllPaged(SearchModel searchModel);
}
