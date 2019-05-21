package erp.chain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import erp.chain.common.Constants;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2017/3/23.
 */
public class GsonUtils {
    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return Boolean.FALSE;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    String value = in.nextString();
                    if ("true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    private static Gson instantiateGson(String datePattern, boolean serializeNulls) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(datePattern);
        gsonBuilder.disableHtmlEscaping();
        if (serializeNulls) {
            gsonBuilder.serializeNulls();
        }
        gsonBuilder.registerTypeAdapter(Boolean.class, booleanAsIntAdapter);
        gsonBuilder.registerTypeAdapter(boolean.class, booleanAsIntAdapter);
        return gsonBuilder.create();
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz, String datePattern) {
        Gson gson = instantiateGson(datePattern, true);
        Type type = new TypeToken<ArrayList<JsonElement>>() {
        }.getType();
        List<JsonElement> jsonElements = gson.fromJson(jsonString, type);
        List<T> list = new ArrayList<T>();
        for (JsonElement jsonElement : jsonElements) {
            list.add(gson.fromJson(jsonElement, clazz));
        }
        return list;
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return jsonToList(jsonString, clazz, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<K> keyClass, Class<V> valueClass, String datePattern) {
        Gson gson = instantiateGson(datePattern, true);
        Type type = new TypeToken<Map<JsonElement, JsonElement>>() {
        }.getType();
        Map<JsonElement, JsonElement> tempMap = gson.fromJson(jsonString, type);

        Map<K, V> map = new HashMap<K, V>();
        for (Map.Entry<JsonElement, JsonElement> entry : tempMap.entrySet()) {
            map.put(gson.fromJson(entry.getKey(), keyClass), gson.fromJson(entry.getValue(), valueClass));
        }
        return map;
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<K> keyClass, Class<V> valueClass) {
        return jsonToMap(jsonString, keyClass, valueClass, Constants.DEFAULT_DATE_PATTERN);
    }

    public static String toJson(Object object) {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN, true).toJson(object);
    }

    public static String toJson(Object object, String datePattern) {
        return instantiateGson(datePattern, true).toJson(object);
    }

    public static String toJson(Object object, boolean serializeNulls) {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN, serializeNulls).toJson(object);
    }

    public static String toJson(Object object, String datePattern, boolean serializeNulls) {
        return instantiateGson(datePattern, serializeNulls).toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN, true).fromJson(jsonString, clazz);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern, true).fromJson(jsonString, clazz);
    }

    public static <T> T fromJson(JsonElement jsonElement, Class<T> clazz) {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN, true).fromJson(jsonElement, clazz);
    }

    public static <T> T fromJson(JsonElement jsonElement, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern, true).fromJson(jsonElement, clazz);
    }

    public static <T> T fromJson(JSONObject jsonObject, Class<T> clazz) {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN, true).fromJson(jsonObject.toString(), clazz);
    }

    public static <T> T fromJson(JSONObject jsonObject, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern, true).fromJson(jsonObject.toString(), clazz);
    }
}
