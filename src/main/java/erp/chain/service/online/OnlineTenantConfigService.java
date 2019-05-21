package erp.chain.service.online;

import erp.chain.common.Constants;
import erp.chain.domain.online.OnlineTenantConfig;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.online.OnlineTenantConfigMapper;
import erp.chain.utils.SearchModel;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-20.
 */
@Service
public class OnlineTenantConfigService {
    @Autowired
    private OnlineTenantConfigMapper onlineTenantConfigMapper;

    public OnlineTenantConfig checkTenantConfig(BigInteger tenantId, String name, String desc) {
        SearchModel tenantConfigSearchModel = new SearchModel(true);
        tenantConfigSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        tenantConfigSearchModel.addSearchCondition("name", Constants.SQL_OPERATION_SYMBOL_EQUALS, name);
        OnlineTenantConfig onlineTenantConfig = onlineTenantConfigMapper.find(tenantConfigSearchModel);
        if (onlineTenantConfig != null) {
            Validate.isTrue(Integer.parseInt(onlineTenantConfig.getValue()) < Integer.parseInt(onlineTenantConfig.getMaxValue()), "最多添加" + onlineTenantConfig.getMaxValue() + "条" + desc + "！");
        } else {
            onlineTenantConfig = new OnlineTenantConfig();
            onlineTenantConfig.setName(name);
            onlineTenantConfig.setValue("0");
            onlineTenantConfig.setMaxValue("99999");
            onlineTenantConfig.setTenantId(tenantId);
            onlineTenantConfig.setCreateAt(new Date());
            onlineTenantConfig.setLastUpdateAt(new Date());
            onlineTenantConfigMapper.insert(onlineTenantConfig);
        }
        return onlineTenantConfig;
    }
}
