package erp.chain.controller.pos;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.controller.BaseController;
import erp.chain.service.PosService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Controller
public class PosController extends BaseController {

    @Autowired
    PosService posService;

    @RequestMapping(value = "/public/pos/find")
    public @ResponseBody Object find(HttpServletRequest request) {
        ApiRest r = new ApiRest();
        try {
            r.setData(posService.find(request.getParameterMap()));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("PosController.find({}) - {}", request.getParameterMap(), r.getError());
        }

        return r;
    }



    @RequestMapping(value = "/public/pos/register")
    public @ResponseBody Object register(BigInteger posId, String token, String deviceCode) {
        if (posId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(deviceCode)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r = posService.register(posId, token, deviceCode);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("PosController.find({},{},{}) - {}", posId, token, deviceCode, r.getError());
        }

        return r;
    }

    @RequestMapping(value = "/public/vip/find")
    public @ResponseBody Object findVip(BigInteger id) {
        if (id == null) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(posService.findVip(id));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("PosController.findVip({}) - {}", id, r.getError());
        }

        return null;
    }
    @RequestMapping(value = "/posWebService/getMaxSaleCode")
    public @ResponseBody String getMaxSaleCode(BigInteger posId, String business) {
        ApiRest r = new ApiRest();
        try {
            r=posService.getMaxSaleCodeByPos(posId,business);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("PosController.getMaxSaleCode({}{}) - {}", posId,business, r.getError());
        }
        return erp.chain.utils.BeanUtils.toJsonStr(r);
    }
    @RequestMapping(value = "/posWebService/initPos")
    public @ResponseBody String initPos() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            BigInteger posId = BigInteger.valueOf(Long.valueOf(params.get("posId")));
            Validate.notNull(posId, "posId is not null");
            String deviceCode = StringUtils.trimToNull(params.get("deviceCode"));
            Validate.notNull(deviceCode, "deviceCode is not null");
            BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
            Validate.notNull(tenantId, "tenantId is not null");
            BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
            Validate.notNull(branchId, "branchId is not null");
            apiRest = posService.initPos(tenantId, branchId, posId, deviceCode);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化失败:initPos服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/posWebService/autoInitPos")
    public @ResponseBody String autoInitPos() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String deviceCode = StringUtils.trimToNull(params.get("deviceCode"));
            Validate.notNull(deviceCode, "deviceCode is not null");
            BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
            Validate.notNull(tenantId, "tenantId is not null");
            BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
            Validate.notNull(branchId, "branchId is not null");
            String tenantCode = params.get("tenantCode");
            Validate.notNull(tenantCode, "tenantCode is not null");
            apiRest = posService.autoInitPos(tenantCode,tenantId, branchId, deviceCode);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化失败:initPos服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/posWebService/clearSaleData")
    public @ResponseBody String clearSaleData() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = posService.clearSaleData(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("清空数据失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("清空数据失败:clearSaleData服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/posWebService/getUserPosInfo")
    public @ResponseBody String getUserPosInfo() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("deviceCode")==null||params.get("deviceCode").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数deviceCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posService.getUserPosInfo(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:getUserPosInfo服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 上传pos系统配置
     * @return
     */
    @RequestMapping(value = "/posWebService/uploadPosSysConfig")
    public @ResponseBody String uploadPosSysConfig() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantCode")==null||params.get("tenantCode").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("employeeId")==null||params.get("employeeId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数employeeId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("data")==null||params.get("data").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数data不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("type")==null||params.get("type").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posService.uploadPosSysConfig(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:getUserPosInfo服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 下发pos系统配置
     * @return
     */
    @RequestMapping(value = "/posWebService/recoverPosSysConfig")
    public @ResponseBody String recoverPosSysConfig() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("recoverCode")==null||params.get("recoverCode").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数recoverCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("type")==null||params.get("type").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = posService.recoverPosSysConfig(params);
        } catch (ServiceException se) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:" + se.getMessage());
            LogUtil.logError(se, params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败:getUserPosInfo服务异常");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
