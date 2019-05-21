package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineVipStoreRule;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-05-11.
 */
@Mapper
public interface OnlineVipStoreRuleMapper {
    long insert(OnlineVipStoreRule onlineVipStoreRule);

    OnlineVipStoreRule find(SearchModel searchModel);

    List<OnlineVipStoreRule> findAll(SearchModel searchModel);
}
