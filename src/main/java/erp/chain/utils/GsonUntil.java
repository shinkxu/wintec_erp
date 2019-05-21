package erp.chain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.saas.common.util.LogUtil;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 谷歌JSON包GSON工具类的相关封装
 */
public class GsonUntil {
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 将json格式的字符串转成对象实例
     * @param jsonStr json字符串
     * @param clasz 要转换生的对象
     * @param <T>
     * @return
     */
    public static <T>T jsonAsModel(String jsonStr,Class<T> clasz){
        T t = null;
        try {
            if(StringUtils.isBlank(jsonStr)){
                return null;
            }
            Gson gson = instanceGson();
            t = gson.fromJson(jsonStr, clasz);
        } catch (Exception e) {
            LogUtil.logError("Gson解析对象失败<" + clasz.getSimpleName() + ">:" + jsonStr);
        }
        return t;

    }

    /**
     * 将json格式的字符串转成集合实例
     * @param jsonStr json字符串
     * @param clasz 集合内的对象
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonAsList(String jsonStr, Class<T> clasz) {
        List<T> list = new ArrayList<T>();
        try {
            if(StringUtils.isBlank(jsonStr)){
                return null;
            }
            Gson gson = instanceGson();
            list = gson.fromJson(jsonStr, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            LogUtil.logError("Gson解析List对象失败<" + clasz.getSimpleName() + ">:" + jsonStr);
        }
        return list;
    }

    /**
     * 将对象转换成json字符串
     * @param object
     * @return
     */
    public static String objectToJson(Object object){
        String resultStr = null;
        try {
            if(null != object){
                Gson gson = instanceGson();
                resultStr = gson.toJson(object);
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
        }
        return resultStr;
    }

    /**
     * 创建Gson对象,默认时间格式yyyy-MM-dd
     */
    private static Gson instanceGson(){
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    /*******************************************************************************************************
     ***************************Gson工具包，2017年4月20日13:36:39(刘艳东)*************************************
     *******************************************************************************************************/
    private static Gson instantiateGson(String datePattern) {
        return new GsonBuilder().setDateFormat(datePattern).serializeNulls().create();
    }

    public static String toJson(Object object, String datePattern) {
        return instantiateGson(datePattern).toJson(object);
    }

    public static String toJson(Object object) {
        Gson gson = instantiateGson(DEFAULT_DATE_PATTERN);
        return gson.toJson(object);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern).fromJson(jsonString, clazz);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        return jsonToObject(jsonString, clazz, DEFAULT_DATE_PATTERN);
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz, String datePattern) {
        Gson gson = instantiateGson(datePattern);
        Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
        List<JsonObject> jsonObjects = gson.fromJson(jsonString, type);
        List<T> list = new ArrayList<T>();
        for (JsonObject jsonObject : jsonObjects) {
            list.add(gson.fromJson(jsonObject, clazz));
        }
        return list;
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return jsonToList(jsonString, clazz, DEFAULT_DATE_PATTERN);
    }
}
