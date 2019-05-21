package erp.chain.mapper.o2o;

import erp.chain.domain.StorageBill;
import erp.chain.domain.o2o.VipStoreRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/21.
 */
@Mapper
public interface VipStoreRuleMapper {
    List<VipStoreRule> vipStoreRuleList(Map<String,Object> params);
    VipStoreRule findByCondition(Map params);
    VipStoreRule findOldRule(Map params);
    int insert(Map params);
    int insert(VipStoreRule vipStoreRule);
    int update(Map params);
    int update(VipStoreRule vipStoreRule);



    List<Map> queryStoreStatements(Map params);
    Long queryStoreStatementsSum(Map params);
    List<Map> queryStoreOrder(Map params);
    List<Map> queryStoreOrderSum(Map params);
    int saveStorageBill(StorageBill storageBill);
    StorageBill queryBillById(Map params);
    List<Map> queryBillInfoById(Map params);
    List<Map> queryStoreByBillId(Map params);
    List<Map> queryStoreByBillIdSum(Map params);
    int updateStorageBill(Map params);
    int updateVipStoreBillId(Map params);
    int clearVipStoreBillId(Map params);
}
