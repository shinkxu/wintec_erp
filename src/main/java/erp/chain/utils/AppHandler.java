package erp.chain.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统帮助类型
 *
 * @author hxh
 */
public class AppHandler {

    /**
     * 获取当前 HttpServletRequest
     */
    public static HttpServletRequest request() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取当前 HttpServletResponse
     */
    public static HttpServletResponse response() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getResponse();
    }

    /**
     * 获取当前 HttpSession
     */
    public static HttpSession session() {
        return request().getSession();
    }

    /**
     * 获取当前 ParameterMap
     */
    public static Map parameterMap() {
        return request().getParameterMap();
    }

    /**
     * 获取当前 getParameterValues
     */
    public static String[] paramValue(String name) {
        return request().getParameterValues(name);
    }

    /**
     * 转换参数，并且trimToNull
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> params() {
        Map<String, String> params = new LinkedHashMap<>(parameterMap().size());
        for (Object obj : parameterMap().entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) obj;
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String val;
                if (value.getClass().isArray()) {
                    val = org.springframework.util.StringUtils.arrayToCommaDelimitedString((Object[]) value);
                } else {
                    val = value.toString();
                }
                params.put(name, StringUtils.trimToNull(val));
            } else {
                params.put(name, null);
            }
        }
        return params;
    }

    /**
     * 绑定参数到bean,参数为json格式
     *
     * @throws: BindParamsException
     */
    public static <T> T bind(Class<T> beanClass) {
        try {
            String body = body("UTF-8");
            return JSON.parseObject(body, beanClass);
        } catch (Exception e) {
            throw new BindParamsException(e.getMessage(), e);
        }
    }

    /**
     * 绑定参数到bean数组,参数为json格式
     *
     * @throws BindParamsException 绑定参数错误
     */
    public static <T> List<T> bindList(Class<T> beanClass) {
        try {
            String body = body("UTF-8");
            return JSON.parseArray(body, beanClass);
        } catch (Exception e) {
            throw new BindParamsException(e.getMessage(), e);
        }
    }

    /**
     * 绑定参数到bean,参数为k-v格式
     *
     * @throws: BindParamsException 绑定参数错误
     */
    public static <T> T bindParam(Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            BeanUtils.populate(bean, params());
            return bean;
        } catch (InstantiationException | IllegalAccessException ie) {
            throw new IllegalArgumentException(ie.getMessage(), ie);
        } catch (Exception e) {
            throw new BindParamsException(e.getMessage(), e);
        }
    }

    /**
     * 获取request的body
     *
     * @throws: java.lang.IllegalArgumentException 绑定参数错误
     */
    public static String body(String charSet) {
        InputStream inputStream = null;
        try {
             inputStream = request().getInputStream();

            StringBuilder xmlMsg = new StringBuilder();
            byte[] b = new byte[1024];
            for (int n; (n = inputStream.read(b)) != -1; ) {
                xmlMsg.append(new String(b, 0, n, charSet != null ? charSet : "UTF-8"));
            }
            return xmlMsg.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
//        finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                }
//            }
//        }
    }
    public static String body() {
        BufferedReader reader = null;
        try {
            reader = request().getReader();
            StringBuilder msg = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                msg.append(inputLine);
            }
            return msg.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                }
//            }
        }
    }
}