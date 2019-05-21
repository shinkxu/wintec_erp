package erp.chain.utils;

import erp.chain.common.Constants;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import saas.api.ProxyApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-23.
 */
public class OutUtils {
    public static String doGet(String url, Map<String, String> headers) {
        Map<String, String> doGetRequestParameters = new HashMap<String, String>();
        doGetRequestParameters.put("url", url);
        if (MapUtils.isNotEmpty(headers)) {
            doGetRequestParameters.put("headers", GsonUtils.toJson(headers));
        }
        saas.api.common.ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "proxy", "doGet", doGetRequestParameters);
        Validate.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        return apiRest.getData().toString();
    }

    public static String doPost(String url, String requestBody, Map<String, String> headers) {
        Map<String, String> doPostRequestParameters = new HashMap<String, String>();
        doPostRequestParameters.put("url", url);
        doPostRequestParameters.put("requestBody", requestBody);
        if (MapUtils.isNotEmpty(headers)) {
            doPostRequestParameters.put("headers", GsonUtils.toJson(headers));
        }
        saas.api.common.ApiRest apiRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "proxy", "doPost", doPostRequestParameters);
        Validate.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        return apiRest.getData().toString();
    }

    public static String doPostFile(String url, Map<String, Object> requestParameters, Map<String, String> headers) {
        Map<String, Object> doPostRequestParameters = new HashMap<String, Object>(requestParameters);
        doPostRequestParameters.put("url", url);
        if (MapUtils.isNotEmpty(headers)) {
            doPostRequestParameters.put("headers", GsonUtils.toJson(headers));
        }
        saas.api.common.ApiRest apiRest = ProxyApi.proxyPostFile(Constants.SERVICE_NAME_OUT, "proxy", "doPostFile", doPostRequestParameters);
        Validate.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        return apiRest.getData().toString();
    }
}
