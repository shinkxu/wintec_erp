package erp.chain.utils;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.exceptions.CustomException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import saas.api.common.ApiRest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2017/4/19.
 */
public class ApplicationHandler {
    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getHttpServletRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static ServletContext getServletContext() {
        return getHttpServletRequest().getServletContext();
    }

    public static Map<String, String> getRequestParameters() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, String> requestParameters = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> requestParameter : parameterMap.entrySet()) {
            requestParameters.put(requestParameter.getKey(), StringUtils.trimToNull(StringUtils.join(requestParameter.getValue(), ",")));
        }
        return requestParameters;
    }

    public static ServletContext servletContext;
    public static ApplicationContext applicationContext = null;

    public static ServletContext obtainServletContext() {
        if (servletContext == null) {
            servletContext = getServletContext();
        }
        return servletContext;
    }

    public static ApplicationContext obtainApplicationContext() {
        if (applicationContext == null) {
            applicationContext = WebApplicationContextUtils.getWebApplicationContext(obtainServletContext());
        }
        return applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return obtainApplicationContext().getBean(beanClass);
    }

    public static Validator obtainValidator() {
//        return obtainApplicationContext().getBean(Validator.class);
        return (Validator) obtainApplicationContext().getBean("validator");
    }

    public static String obtainParameterErrorMessage(String parameterName) {
        return String.format(Constants.PARAMETER_ERROR_MESSAGE_PATTERN, parameterName);
    }

    public static <T> T instantiateObject(Class<T> objectClass, Map<String, String> parameters) throws Exception {
        return instantiateObject(objectClass, parameters, Constants.DEFAULT_DATE_PATTERN, "");
    }

    public static <T> T instantiateObject(Class<T> objectClass, Map<String, String> parameters, String prefix) throws Exception {
        return instantiateObject(objectClass, parameters, Constants.DEFAULT_DATE_PATTERN, prefix);
    }

    public static <T> T instantiateObject(Class<T> objectClass, Map<String, String> parameters, String datePattern, String prefix) throws Exception {
        T object = objectClass.newInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers)) {
                continue;
            }
            String fieldName = field.getName();
            String fieldValue = parameters.get(prefix + fieldName);
            if (StringUtils.isBlank(fieldValue)) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            Class<?> fieldClass = field.getType();
            if (fieldClass == Byte.class || fieldClass == byte.class) {
                field.set(object, Byte.valueOf(fieldValue));
            } else if (fieldClass == Short.class || fieldClass == short.class) {
                field.set(object, Short.valueOf(fieldValue));
            } else if (fieldClass == Integer.class || fieldClass == int.class) {
                field.set(object, Integer.valueOf(fieldValue));
            } else if (fieldClass == Long.class || fieldClass == long.class) {
                field.set(object, Long.valueOf(fieldValue));
            } else if (fieldClass == Float.class || fieldClass == float.class) {
                field.set(object, Float.valueOf(fieldValue));
            } else if (fieldClass == Double.class || fieldClass == double.class) {
                field.set(object, Double.valueOf(fieldValue));
            } else if (fieldClass == Character.class || fieldClass == char.class) {
                field.set(object, fieldValue.charAt(0));
            } else if (fieldClass == String.class) {
                field.set(object, fieldValue);
            } else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
                field.set(object, Boolean.valueOf(fieldValue));
            } else if (fieldClass == Date.class) {
                field.set(object, simpleDateFormat.parse(fieldValue));
            } else if (fieldClass == BigInteger.class) {
                field.set(object, BigInteger.valueOf(Long.valueOf(fieldValue)));
            } else if (fieldClass == BigDecimal.class) {
                field.set(object, BigDecimal.valueOf(Double.valueOf(fieldValue)));
            } else if (fieldClass == List.class) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    field.set(object, buildArrayList(((ParameterizedType) type).getActualTypeArguments()[0], fieldValue, ",", datePattern));
                }
            } else {
                field.set(object, GsonUtils.fromJson(fieldValue, fieldClass));
            }
        }
        return object;
    }

    public static List<Byte> buildByteArrayList(String fieldValue, String separator) {
        List<Byte> list = new ArrayList<Byte>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Byte.valueOf(str));
        }
        return list;
    }

    public static List<Short> buildShortArrayList(String fieldValue, String separator) {
        List<Short> list = new ArrayList<Short>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Short.valueOf(str));
        }
        return list;
    }

    public static List<Integer> buildIntegerArrayList(String fieldValue, String separator) {
        List<Integer> list = new ArrayList<Integer>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Integer.valueOf(str));
        }
        return list;
    }

    public static List<Long> buildLongArrayList(String fieldValue, String separator) {
        List<Long> list = new ArrayList<Long>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Long.valueOf(str));
        }
        return list;
    }

    public static List<Float> buildFloatArrayList(String fieldValue, String separator) {
        List<Float> list = new ArrayList<Float>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Float.valueOf(str));
        }
        return list;
    }

    public static List<Double> buildDoubleArrayList(String fieldValue, String separator) {
        List<Double> list = new ArrayList<Double>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Double.valueOf(str));
        }
        return list;
    }

    public static List<Character> buildCharacterArrayList(String fieldValue, String separator) {
        List<Character> list = new ArrayList<Character>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(str.charAt(0));
        }
        return list;
    }

    public static List<Boolean> buildBooleanArrayList(String fieldValue, String separator) {
        List<Boolean> list = new ArrayList<Boolean>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(Boolean.valueOf(str));
        }
        return list;
    }

    public static List<BigInteger> buildBigIntegerArrayList(String fieldValue, String separator) {
        List<BigInteger> list = new ArrayList<BigInteger>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(BigInteger.valueOf(Long.valueOf(str)));
        }
        return list;
    }

    public static List<BigDecimal> buildBigDecimalArrayList(String fieldValue, String separator) {
        List<BigDecimal> list = new ArrayList<BigDecimal>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(BigDecimal.valueOf(Double.valueOf(str)));
        }
        return list;
    }

    public static List<Date> buildDateArrayList(String fieldValue, String separator, String datePattern) {
        List<Date> list = new ArrayList<Date>();
        String[] array = fieldValue.split(separator);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        try {
            for (String str : array) {
                list.add(simpleDateFormat.parse(str));
            }
        } catch (Exception e) {
            throw new CustomException(e);
        }
        return list;
    }

    public static List<String> buildStringArrayList(String fieldValue, String separator) {
        List<String> list = new ArrayList<String>();
        String[] array = fieldValue.split(separator);
        for (String str : array) {
            list.add(str);
        }
        return list;
    }

    private static Object buildArrayList(Type type, String fieldValue, String separator, String datePattern) {
        List<? extends Object> list = null;
        if (type == Byte.class) {
            list = buildByteArrayList(fieldValue, separator);
        } else if (type == Short.class) {
            list = buildShortArrayList(fieldValue, separator);
        } else if (type == Integer.class) {
            list = buildIntegerArrayList(fieldValue, separator);
        } else if (type == Long.class) {
            list = buildLongArrayList(fieldValue, separator);
        } else if (type == Float.class) {
            list = buildFloatArrayList(fieldValue, separator);
        } else if (type == Double.class) {
            list = buildFloatArrayList(fieldValue, separator);
        } else if (type == Character.class) {
            list = buildCharacterArrayList(fieldValue, separator);
        } else if (type == Boolean.class) {
            list = buildBooleanArrayList(fieldValue, separator);
        } else if (type == BigInteger.class) {
            list = buildBigIntegerArrayList(fieldValue, separator);
        } else if (type == BigDecimal.class) {
            list = buildBigDecimalArrayList(fieldValue, separator);
        } else if (type == Date.class) {
            list = buildDateArrayList(fieldValue, separator, datePattern);
        } else if (type == String.class) {
            list = buildStringArrayList(fieldValue, separator);
        } else {
            list = GsonUtils.jsonToList(fieldValue, (Class<? extends Object>) type, datePattern);
        }
        return list;
    }

    public static void notNull(Object object, String parameterName) {
        ValidateUtils.notNull(object, obtainParameterErrorMessage(parameterName));
    }

    public static void isTrue(boolean expression, String parameterName) {
        ValidateUtils.isTrue(expression, obtainParameterErrorMessage(parameterName));
    }

    public static void notEmpty(Object[] array, String parameterName) {
        ValidateUtils.notEmpty(array, obtainParameterErrorMessage(parameterName));
    }

    public static void notEmpty(Collection collection, String parameterName) {
        ValidateUtils.notEmpty(collection, obtainParameterErrorMessage(parameterName));
    }

    public static void notEmpty(Map map, String parameterName) {
        ValidateUtils.notEmpty(map, obtainParameterErrorMessage(parameterName));
    }

    public static void notEmpty(String string, String parameterName) {
        ValidateUtils.notEmpty(string, obtainParameterErrorMessage(parameterName));
    }

    public static void notEmpty(String[]... valueAndNames) {
        for (String[] valueAndName : valueAndNames) {
            notEmpty(valueAndName[0], valueAndName[1]);
        }
    }

    public static void inArray(Object[] array, Object value, String name) {
        ValidateUtils.inArray(array, value, "参数(" + name + ")只能为【" + StringUtils.join(array, "，") + "】中的一个！");
    }

    public static void notBlank(String string, String parameterName) {
        ValidateUtils.notBlank(string, obtainParameterErrorMessage(parameterName));
    }

    public static String getRemoteAddress() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        String remoteAddress = httpServletRequest.getHeader("X-Real-IP");
        if (StringUtils.isBlank(remoteAddress)) {
            remoteAddress = httpServletRequest.getHeader("X-Forwarded-For");
            if (StringUtils.isBlank(remoteAddress)) {
                remoteAddress = httpServletRequest.getRemoteAddr();
            }
        }
        return remoteAddress;
    }

    public static String callMethod(MethodCaller methodCaller, String errorMessage, String controllerSimpleName, String methodName, Map<String, String> requestParameters) {
        ApiRest apiRest = null;
        try {
            apiRest = methodCaller.call();
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, errorMessage, controllerSimpleName, methodName, e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    public static <T> T clone(Class<T> beanClass, Object originalBean) {
        try {
            T t = beanClass.newInstance();
            org.apache.commons.beanutils.BeanUtils.copyProperties(t, originalBean);
            return t;
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static Map<String, Object> toMap(Object object) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers)) {
                    continue;
                }
                ReflectionUtils.makeAccessible(field);
                map.put(field.getName(), ReflectionUtils.getField(field, object));
            }
        }
        return map;
    }

    public static String obtainLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
