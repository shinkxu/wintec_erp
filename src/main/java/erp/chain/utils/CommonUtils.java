package erp.chain.utils;

import com.saas.common.Constants;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/7/25.
 */
public class CommonUtils {

    public static String DomainName2TableName(String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return null;
        }
        StringBuilder tableName = new StringBuilder();
        char c, firstE = 'A', lastE = 'Z';
        for (int i = 0; i < domainName.length(); i++) {
            c = domainName.charAt(i);
            if (c >= firstE && c <= lastE) {
                if (i != 0) {
                    tableName.append("_");
                }
                tableName.append((char)(c + 32));
            } else {
                tableName.append(c);
            }
        }
        return tableName.toString();
    }

    public static String TableName2DomainName(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        String[] tableNames = tableName.split("_");
        StringBuilder domainName = new StringBuilder();
        for (int i = 0; i < tableNames.length; i++) {
            String s = tableNames[i];
            if (i == 0) {
                domainName.append(s);
            } else {
                domainName.append(UpperCaseFirstLetter(s));
            }
        }
        return domainName.toString();
    }

    public static String UpperCaseFirstLetter(String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        }
        byte[] letters = word.getBytes();
        letters[0] = (byte)((char)letters[0]-'a'+'A');
        return new String(letters);
    }

    public static Map DomainMap(Map tableMap) {
        if (tableMap == null || tableMap.isEmpty()) {
            return tableMap;
        }
        Map newMap = new HashMap();
        for (Object k : tableMap.keySet()) {
            newMap.put(TableName2DomainName(k.toString()), tableMap.get(k));
        }
        return newMap;
    }

    public static List<Map> DomainList(List<Map> tableList) {
        if (tableList == null || tableList.isEmpty()) {
            return tableList;
        }
        List<Map> newList = new ArrayList<>();
        for (Map tableMap : tableList) {
            newList.add(DomainMap(tableMap));
        }
        return newList;
    }

    public static Map<String, Object> InvalidParamsError(Map<String, Object> resultMap) {
        if (resultMap == null) {
            resultMap = new HashMap<>();
        }
        resultMap.put("Result", Constants.REST_RESULT_FAILURE);
        resultMap.put("Message","参数错误");
        resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        return resultMap;
    }

    public static Map ObjectToMap(Object o) throws IllegalAccessException {
        if (o == null) {
            return null;
        }
        Map map = new HashMap();
        Object v;
        for (Field f : o.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            v = f.get(o);
            if (v != null) {
                map.put(f.getName(), v);
            }
        }
        return map;
    }
    public static String formatOrderField(String field){
        StringBuilder ss = new StringBuilder();
        char[] charArray = field.toCharArray();
        for(int i = 0; i < charArray.length; i++){
            if(charArray[i] >= 'A' && charArray[i] <= 'Z'){
                ss.append("_").append(charArray[i]);
            }
            else{
                ss.append(charArray[i]);
            }
        }
        field = ss.toString().toLowerCase();
        return field;
    }
}
