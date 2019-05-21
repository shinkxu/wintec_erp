package erp.chain.controller.pos;

import com.saas.common.util.PartitionCacheUtils;
import erp.chain.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumx on 2016/11/1.
 */
@Controller
public class CacheController extends BaseController {

    @RequestMapping(value = "/api/cache/set")
    public @ResponseBody ApiRest set(String key, String value, String expire, String cover) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();

        try {
            if ("0".equals(cover)) {
                String v = PartitionCacheUtils.get(key);
                if (v != null) {
                    r.setIsSuccess(false);
                    return r;
                }
            }
            if (StringUtils.isNotEmpty(expire) && StringUtils.isNumeric(expire)) {
                r.setData(PartitionCacheUtils.set(key, value, Integer.valueOf(expire)));
            } else {
                r.setData(PartitionCacheUtils.set(key, value));
            }
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.set({},{},{},{}) - {}", key, value, expire, cover, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/get")
    public @ResponseBody ApiRest get(String key) {
        if (StringUtils.isEmpty(key)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(PartitionCacheUtils.get(key));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.get({}) - {}", key, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/mget")
    public @ResponseBody ApiRest mget(String keys) {
        if (StringUtils.isEmpty(keys)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            String[] keyArr = keys.split(",");
            r.setData(PartitionCacheUtils.mget(keyArr));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.mget({}) - {}", keys, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/del")
    public @ResponseBody ApiRest del(String keys) {
        if (StringUtils.isEmpty(keys)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            String[] keyArr = keys.split(",");
            r.setData(PartitionCacheUtils.del(keyArr));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.del({}) - {}", keys, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/hget")
    public @ResponseBody ApiRest hget(String key, String field) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(PartitionCacheUtils.hget(key, field));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.hget({},{}) - {}", key, field, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/hset")
    public @ResponseBody ApiRest hset(String key, String field, String value, String expire) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field) || StringUtils.isEmpty(value)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            if (StringUtils.isNotEmpty(expire) && StringUtils.isNumeric(expire)) {
                r.setData(PartitionCacheUtils.hset(key, field, value, Integer.valueOf(expire)));
            } else {
                r.setData(PartitionCacheUtils.hset(key, field, value));
            }
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.hset({},{},{},{}) - {}", key, field, value, expire, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/hmset")
    public @ResponseBody ApiRest hmset(String key, String map, String expire) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(map)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            Map json = new ObjectMapper().readValue(map, Map.class);
            Map<String, String> jsonMap = new HashMap<>();
            for (Object k : json.keySet()) {
                if (k != null && json.get(k) != null) {
                    jsonMap.put(k.toString(), json.get(k).toString());
                }
            }
            if (!jsonMap.isEmpty()) {
                if (StringUtils.isNotEmpty(expire) && StringUtils.isNumeric(expire)) {
                    r.setData(PartitionCacheUtils.hmsetExpire(key, jsonMap, Integer.valueOf(expire)));
                } else {
                    r.setData(PartitionCacheUtils.hmset(key, jsonMap));
                }
            }
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.hmset({},{},{}) - {}", key, map, expire, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/hgetAll")
    public @ResponseBody ApiRest hgetAll(String key) {
        if (StringUtils.isEmpty(key)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(PartitionCacheUtils.hgetAll(key));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.hgetAll({}) - {}", key, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/api/cache/hmdel")
    public @ResponseBody ApiRest hmdel(String key, String fields) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(fields)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(PartitionCacheUtils.hmdel(key, fields.split(",")));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("CacheController.hmdel({},{}) - {}", key, fields, r.getError());
        }
        return r;
    }

}
