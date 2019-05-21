package erp.chain.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by liuyandong on 2019-03-29.
 */
public class BaiDuMapUtils {
    public static Map<String, Object> geoConv(String coords, String ak, int from, int to, String sn, String output) {
        String url = "http://api.map.baidu.com/geoconv/v1/?coords=" + coords + "&from=" + from + "&to=" + to + "&ak=" + ak;
        if (StringUtils.isNotBlank(sn)) {
            url += "&sn=" + sn;
        }

        if (StringUtils.isNotBlank(output)) {
            url += "&output=" + output;
        }

        String result = OutUtils.doGet(url, null);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);

        int status = MapUtils.getIntValue(resultMap, "status");
        ValidateUtils.isTrue(status == 0, MapUtils.getString(resultMap, "message"));
        return resultMap;
    }
}
