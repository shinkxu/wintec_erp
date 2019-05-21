package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.VipAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/1/20.
 */
@Mapper
public interface VipAddressMapper {
    List<VipAddress> vipAddressList(Map params);
    VipAddress findByCondition(Map params);
    int update(Map params);
    int insert(Map params);
    int insert1(VipAddress vipAddress);
}
