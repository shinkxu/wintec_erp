package erp.chain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.chain.common.Constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JacksonUtils {
    private static ConcurrentHashMap<String, ObjectMapper> objectMapperMap = new ConcurrentHashMap<String, ObjectMapper>();

    private static ObjectMapper obtainObjectMapper(String datePattern) {
        if (!objectMapperMap.contains(datePattern)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat(datePattern));
            objectMapperMap.put(datePattern, objectMapper);
        }
        return objectMapperMap.get(datePattern);
    }

    public static String writeValueAsString(Object object) {
        return writeValueAsString(object, Constants.DEFAULT_DATE_PATTERN);
    }

    public static String writeValueAsString(Object object, String datePattern) {
        String jsonString = null;
        try {
            jsonString = obtainObjectMapper(datePattern).writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonString;
    }

    public static <T> T readValue(String content, Class<T> clazz) {
        return readValue(content, clazz, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <T> T readValue(String content, Class<T> clazz, String datePattern) {
        T t = null;
        try {
            t = obtainObjectMapper(datePattern).readValue(content, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    public static <T> List<T> readValueAsList(String content, Class<T> elementClass) {
        return readValueAsList(content, elementClass, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <T> List<T> readValueAsList(String content, Class<T> elementClass, String datePattern) {
        List<T> list = null;
        try {
            ObjectMapper objectMapper = obtainObjectMapper(datePattern);
            list = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static <T> Set<T> readValueAsSet(String content, Class<T> elementClass) {
        return readValueAsSet(content, elementClass, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <T> Set<T> readValueAsSet(String content, Class<T> elementClass, String datePattern) {
        Set<T> set = null;
        try {
            ObjectMapper objectMapper = obtainObjectMapper(datePattern);
            set = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(Set.class, elementClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return set;
    }

    public static <K, V> Map<K, V> readValueAsMap(String content, Class<K> keyClass, Class<V> valueClass) {
        return readValueAsMap(content, keyClass, valueClass, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <K, V> Map<K, V> readValueAsMap(String content, Class<K> keyClass, Class<V> valueClass, String datePattern) {
        Map<K, V> map = null;
        try {
            ObjectMapper objectMapper = obtainObjectMapper(datePattern);
            map = objectMapper.readValue(content, objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
