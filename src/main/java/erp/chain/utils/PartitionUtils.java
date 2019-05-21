package erp.chain.utils;

import com.saas.common.util.CacheUtils;
import com.saas.common.util.SessionConstants;
import erp.chain.common.Constants;

/**
 * Created by liuyandong on 2017/6/23.
 */
public class PartitionUtils {
    public static String getOutsideServiceDomain(String serviceName, String partitionCode) {
        String deployEnv = ConfigurationUtils.getConfigurationSafe(Constants.DEPLOY_ENV);
        String field = SessionConstants.KEY_OUTSIDE_SERVICE_NAME + deployEnv + "_" + serviceName + "_" + partitionCode;
        return CacheUtils.hget(SessionConstants.KEY_SERVICE_DOMAIN, field);
    }

    public static String getServiceDomain(String serviceName, String partitionCode) {
        String deployEnv = ConfigurationUtils.getConfigurationSafe(Constants.DEPLOY_ENV);
        String field = SessionConstants.KEY_SERVICE_NAME + deployEnv + "_" + serviceName + "_" + partitionCode;
        return CacheUtils.hget(SessionConstants.KEY_SERVICE_DOMAIN, field);
    }
}
