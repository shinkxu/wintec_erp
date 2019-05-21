package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.VipStoreHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/17.
 */
@Mapper
public interface VipStoreHistoryMapper {
    List<VipStoreHistory> vipStoreList(Map params);
    VipStoreHistory findByCondition(Map params);
    List<VipStoreHistory> findListByCondition(Map params);
    int update(Map params);
    int update1(VipStoreHistory vipStoreHistory);
    int insert(VipStoreHistory vipStoreHistory);
    VipStoreHistory findByStoreCode(Map params);
}
