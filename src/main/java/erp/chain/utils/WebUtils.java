package erp.chain.utils;

import erp.chain.common.Constants;
import saas.api.ProxyApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyandong on 2017/6/20.
 */
public class WebUtils {
    public static class RequestMethod {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }

    public static String callMiddlewareBySaasGateway(String controllerName, String actionName, Map<String, String> params, String requestMethod) {
        String result = null;
        switch (requestMethod) {
            case RequestMethod.GET:
                result = ProxyApi.proxyGetOriginal(Constants.SERVICE_NAME_GATEWAY, controllerName, actionName, params);
                break;
            case RequestMethod.POST:
                result = ProxyApi.proxyPostOriginal(Constants.SERVICE_NAME_GATEWAY, controllerName, actionName, params);
                break;
        }
        return result;
    }

    public static String callOutSystemBySaasGateway(String requestUrl, Map<String, String> parameters, String requestMethod) throws IOException {
        String paramsJson = GsonUntil.toJson(parameters);

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("url", requestUrl);
        requestParameters.put("paramsJson", paramsJson);

        String result = null;
        switch (requestMethod) {
            case RequestMethod.GET:
                result = ProxyApi.proxyGetOriginal(Constants.SERVICE_NAME_GATEWAY, Constants.CONTROLLER_NAME_PROXY, null, requestParameters);
                break;
            case RequestMethod.POST:
                result = ProxyApi.proxyPostOriginal(Constants.SERVICE_NAME_GATEWAY, Constants.CONTROLLER_NAME_PROXY, null, requestParameters);
                break;
        }
        return result;
    }
}
