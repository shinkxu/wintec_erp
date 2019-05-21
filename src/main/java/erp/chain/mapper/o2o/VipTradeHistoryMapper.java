package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.VipTradeHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/1/18.
 */
@Mapper
public interface VipTradeHistoryMapper {
    List<VipTradeHistory> vipTradeList(Map params);
    int insert(VipTradeHistory vipTradeHistory);
    VipTradeHistory findByCondition(Map params);
    List<VipTradeHistory> findListByCondition(Map params);
    int update1(VipTradeHistory vipTradeHistory);
}
