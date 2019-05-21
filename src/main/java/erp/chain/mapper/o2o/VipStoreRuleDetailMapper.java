package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.VipStoreRuleDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/21.
 */
@Mapper
public interface VipStoreRuleDetailMapper {
    List<VipStoreRuleDetails> vipStoreRuleDetailsList(Map params);
    List<Map> storeRuleDetailsList(Map params);
    int insert(Map params);
    int insert(VipStoreRuleDetails vipStoreRuleDetails);
    int update(Map params);
    int update(VipStoreRuleDetails vipStoreRuleDetails);
    VipStoreRuleDetails findByCondition(Map params);
    long findByCondition2(Map params);
}
