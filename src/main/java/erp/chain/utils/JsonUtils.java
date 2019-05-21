package erp.chain.utils;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JsonUtils {

    public static Map<String, String> json2Map(String jsonStr) {
        return json2Map(str2Json(jsonStr));
    }

    public static Map<String, String> json2Map(JSONObject json) {
        return (Map<String, String>) JSONObject.toBean(json, LinkedHashMap.class);
    }

    public static JSONObject str2Json(String jsonStr) {
        return JSONObject.fromObject(jsonStr);
    }

    public static List<String> json2List(String jsonStr) {
        return json2List(str2JsonArr(jsonStr));
    }

    public static List<String> json2List(JSONArray json) {
        List<String> list = new ArrayList<String>();
        list.addAll(JSONArray.toCollection(json));
        return list;
    }

    public static String list2json(List<String> list) {
        return JSONArray.fromObject(list).toString();
    }

    public static JSONArray str2JsonArr(String jsonStr) {
        return JSONArray.fromObject(jsonStr);
    }

}