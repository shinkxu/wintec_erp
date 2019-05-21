package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.PosStoreOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by wangms on 2017/5/22.
 */
@Mapper
public interface PosStoreOrderMapper {
    int insert(PosStoreOrder posStoreOrder);
}
