package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineVipStoreRuleDetail;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-05-11.
 */
@Mapper
public interface OnlineVipStoreRuleDetailMapper {
    long insert(OnlineVipStoreRuleDetail onlineVipStoreRuleDetail);

    long update(OnlineVipStoreRuleDetail onlineVipStoreRuleDetail);

    OnlineVipStoreRuleDetail find(SearchModel searchModel);

    List<OnlineVipStoreRuleDetail> findAll(SearchModel searchModel);
}
