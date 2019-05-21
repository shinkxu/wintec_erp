package erp.chain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.common.util.CacheUtils;
import com.saas.common.util.Common;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import saas.api.ProxyApi;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by liuyandong on 2017/5/16.
 */
public class ElemeUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getAccessToken(String tenantId, String branchId, String elemeAccountType) {
        String tokenJson = null;
        if ("1".equals(elemeAccountType)) {
            tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKEN, Constants.KEY_ELEME_TOKEN + "_" + tenantId);
        } else if ("2".equals(elemeAccountType)) {
            tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKEN, Constants.KEY_ELEME_TOKEN + "_" + tenantId + "_" + branchId);
        }
        JSONObject jsonObject = JSONObject.fromObject(tokenJson);
        return jsonObject.getString("access_token");
    }

    public static saas.api.common.ApiRest callElemeSystem(String appKey, String secret, String action, String tenantId, String branchId, String elemeAccountType, Map<String, Object> params) throws Exception {
        Long timestamp = System.currentTimeMillis() / 1000;
        String accessToken = getAccessToken(tenantId, branchId, elemeAccountType);
        Map<String, Object> metas = constructMetas(appKey, timestamp);
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        String signature = generateSignature(appKey, secret, timestamp, action, accessToken, params);

        String requestBody = constructRequestBody(action, accessToken, metas, params, signature);
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("requestBody", requestBody);
        return ProxyApi.proxyPost("out", "eleme", "callElemeSystem", requestParameters);
    }

    public static String generateSignature(String appKey, String secret, long timestamp, String action, String accessToken, Map<String, Object> params) throws Exception {
        Map<String, Object> sorted = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        sorted.put("app_key", appKey);
        sorted.put("timestamp", timestamp);
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            stringBuffer.append(entry.getKey()).append("=").append(objectMapper.writeValueAsString(entry.getValue()));
        }
        return DigestUtils.md5Hex(String.format("%s%s%s%s", action, accessToken, stringBuffer, secret)).toUpperCase();
    }

    public static Map<String, Object> constructMetas(String appKey, Long timestamp) {
        Map<String, Object> metas = new HashMap<String, Object>();
        metas.put("app_key", appKey);
        metas.put("timestamp", timestamp);
        return metas;
    }

    public static String constructRequestBody(String action, String accessToken, Map<String, Object> metas, Map<String, Object> params, String signature) throws Exception {
        Map<String, Object> requestBody = new HashMap<String, Object>();
        String requestId = UUID.randomUUID().toString();
        requestBody.put("id", requestId);
        requestBody.put("action", action);
        requestBody.put("token", accessToken);
        requestBody.put("metas", metas);
        requestBody.put("params", params);
        requestBody.put("signature", signature);
        requestBody.put("nop", "1.0.0");
        return objectMapper.writeValueAsString(requestBody);
    }

    public static String generateAuthorizeUrl(String appKey, String state, String scope) throws IOException {
        String authorizeUrl = PropertyUtils.getDefault(Constants.ELEME_SERVER_URL) + "/authorize";
        String redirectUrl = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT) + "/eleme/authorizeCallback";
        return String.format("%s?response_type=%s&client_id=%s&state=%s&redirect_uri=%s&scope=%s", authorizeUrl, "code", appKey, state, URLEncoder.encode(redirectUrl, "UTF-8"), scope);
    }
}
