package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineDietOrderDetail;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Mapper
public interface OnlineDietOrderDetailMapper {
    long insert(OnlineDietOrderDetail onlineDietOrderDetail);
    long insertAll(List<OnlineDietOrderDetail> onlineDietOrderDetails);
    long update(OnlineDietOrderDetail onlineDietOrderDetail);
    OnlineDietOrderDetail find(SearchModel searchModel);
    List<OnlineDietOrderDetail> findAll(SearchModel searchModel);
}
