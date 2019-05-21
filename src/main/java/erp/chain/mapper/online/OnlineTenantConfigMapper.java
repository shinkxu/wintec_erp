package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineTenantConfig;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by liuyandong on 2018-04-13.
 */
@Mapper
public interface OnlineTenantConfigMapper {
    long insert(OnlineTenantConfig onlineTenantConfig);

    long update(OnlineTenantConfig onlineTenantConfig);

    OnlineTenantConfig find(SearchModel searchModel);

    List<OnlineTenantConfig> findAll(SearchModel searchModel);
}
