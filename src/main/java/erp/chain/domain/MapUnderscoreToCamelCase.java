package erp.chain.domain;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * map下划线转为驼峰模式
 */
public class MapUnderscoreToCamelCase<k, v> extends HashMap<k, v> {
    @SuppressWarnings("unchecked")
    @Override
    public v put(k key, v value) {
        if (key instanceof String) {
            key = (k) underscoreToCamel((String) key);
        }
        return super.put(key, value);
    }

    /**
     * 下划线转为驼峰
     */
    private static String underscoreToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            //String.valueOf(Character.toUpperCase(sb.charAt(position)));
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

}
