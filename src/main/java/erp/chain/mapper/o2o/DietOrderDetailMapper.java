package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.DietOrderDetail;
import erp.chain.domain.o2o.Vip;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/21.
 */
@Mapper
public interface DietOrderDetailMapper {
    List<DietOrderDetail> select(Map params);
    DietOrderDetail findByCondition(Map params);
    int insert(DietOrderDetail dietOrderDetail);
    int update(Map params);
    List<DietOrderDetail> findDetailByCondition(Map params);
}
