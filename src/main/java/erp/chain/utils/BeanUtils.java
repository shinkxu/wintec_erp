package erp.chain.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.Validate;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Bean工具类
 */
public class BeanUtils {
    private static SerializerFeature[] defaultSerializerFeatures = new SerializerFeature[]{
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.DisableCircularReferenceDetect
    };

    static {
        try {
            //修改org.apache.commons.beanutils默认值为null
            ConvertUtilsBean convertUtilsBean = BeanUtilsBean.getInstance().getConvertUtils();
            Method method = convertUtilsBean.getClass().getDeclaredMethod("register", boolean.class, boolean.class, int.class);
            method.setAccessible(true);
            method.invoke(convertUtilsBean, false, true, -1);
            ConvertUtils.register(new ObjectToDateConverter(), Date.class);
        } catch (Exception e) {
            throw new RuntimeException("org.apache.commons.beanutils默认值错误:" + e.getMessage());
        }
    }

    /**
     * 对bean属性按字典排序表示为LinkedHashMap（只表示有getter方法的属性）
     *
     * @param bean not null
     * @return 根据属性名称按字典排序构建LinkedHashMap集合
     */
    public static LinkedHashMap<String, Object> propertyToMap(Object bean) {
        Validate.notNull(bean, "bean转换map错误：bean is null");
        LinkedHashMap<String, Object> orderMap;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();

            Map<String, Object> noOrderMap = new HashMap<>(pd.length - 1);

            orderMap = new LinkedHashMap<>();
            List<String> pNames = new ArrayList<>(pd.length - 1);
            for (PropertyDescriptor p : pd) {
                String proName = p.getName();
                //过滤class属性
                if (!proName.equals("class")) {
                    Method getter = p.getReadMethod();
                    if (getter != null) {
                        Object value = getter.invoke(bean);
                        pNames.add(proName);
                        noOrderMap.put(proName, value);
                    }
                }
            }
            //排序
            Collections.sort(pNames);
            for (String name : pNames) {
                orderMap.put(name, noOrderMap.get(name));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("bean转换map错误:" + e.getMessage());
        }
        return orderMap;
    }

    /**
     * json字符串转换Java bean
     */
    public static <T> T toBean(String json, Class<T> beanClass) {
        return JSON.parseObject(json, beanClass);
    }

    /**
     * json字符串转换JavaBean数组
     */
    public static <T> List<T> toList(String json, Class<T> beanClass) {
        return JSON.parseArray(json, beanClass);
    }

    /**
     * Object转换成json字符串
     */
    public static String toJsonStr(Object obj) {
        return JSONObject.toJSONString(obj, defaultSerializerFeatures);
    }
    //

    /**
     * 调用org.apache.commons.beanutils.BeanUtils的populate方法
     * 静态代码块已重新设置了类型转换器的默认值为null。
     * 使用此方法代替以确保转换器的默认值始终为null
     */
    public static void populate(Object bean, Map<String, ?> properties)
            throws IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        // Loop through the property name/value pairs to be set
        for (final Map.Entry<String, ? extends Object> entry : properties.entrySet()) {
            // Identify the property name and value(s) to be assigned
            final String name = entry.getKey();
            if (name == null) {
                continue;
            }
            try {
                beanUtilsBean.getPropertyUtils().getResolver().getIndex(name);
            } catch (IllegalArgumentException e) {
                continue;
            }
            // Perform the assignment for this property
            beanUtilsBean.setProperty(bean, name, entry.getValue());

        }
    }

//    public static void main(String[] a) {
//        Message message = new Message();
////        BeanUtils.propertyToMap(message);
//        String json = "[{toUserName:\"dwew\",date:\"2014-10-12 10:30:40\"},{toUserName:\"dwew\",date:\"2014-10-12 10:30:40\"}]";
//        BeanUtils.toList(json, message.getClass());
//    }
}
