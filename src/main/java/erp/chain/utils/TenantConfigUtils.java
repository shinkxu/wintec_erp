package erp.chain.utils;

import erp.chain.common.Constants;
import erp.chain.domain.online.OnlineTenantConfig;
import erp.chain.domain.system.TenantConfig;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2019-04-25.
 */
public class TenantConfigUtils {
    public static OnlineTenantConfig obtainTenantConfig(BigInteger tenantId, String name) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("name", Constants.SQL_OPERATION_SYMBOL_EQUALS, name);
        return DatabaseHelper.find(OnlineTenantConfig.class, searchModel);
    }
}
