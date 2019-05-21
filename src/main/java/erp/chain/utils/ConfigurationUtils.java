package erp.chain.utils;


import com.saas.common.util.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by liuyandong on 2018-08-18.
 */
public class ConfigurationUtils {
    public static String getConfiguration(String configurationKey) throws IOException {
        String configurationValue = PropertyUtils.getDefault(configurationKey);
        return configurationValue;
    }

    public static String getConfigurationSafe(String configurationKey) {
        try {
            return getConfiguration(configurationKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getConfiguration(String configurationKey, String defaultValue) throws IOException {
        String configurationValue = getConfiguration(configurationKey);
        if (StringUtils.isNotBlank(configurationValue)) {
            return configurationValue;
        }
        return defaultValue;
    }
}
