package erp.chain.controller.pos;


import com.saas.common.Constants;
import com.saas.common.util.PropertyUtils;
import erp.chain.controller.BaseController;
import erp.chain.domain.App;
import erp.chain.domain.Branch;
import erp.chain.domain.Pos;
import erp.chain.domain.Tenant;
import erp.chain.mapper.BranchMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;
import saas.api.util.ApiBaseServiceUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
@Controller
public class AppController extends BaseController {
    @Autowired
    private BranchMapper branchMapper;

    @RequestMapping(value = "/api/app")
    public @ResponseBody Map<String, Object> index(String acn, BigInteger posId) {
        Map<String, Object> resultMap = null;
        if (StringUtils.isEmpty(acn) || posId == null) {
            resultMap = new HashMap<>();
            resultMap.put("Result", Constants.REST_RESULT_SUCCESS);
            resultMap.put("Message","参数错误");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        } else if ("android".equals(acn.trim().toLowerCase())) {
            resultMap = getApp(1, posId);
        } else if ("windows".equals(acn.trim().toLowerCase())) {
            resultMap = getApp(2, posId);
        } else if ("androidUpdate".equals(acn.trim().toLowerCase())) {
            resultMap = getApp(9, posId);
        } else if ("windowsUpdate".equals(acn.trim().toLowerCase())) {
            resultMap = getApp(10, posId);
        } else if ("aerp".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(4, posId);
        } else if ("fpos".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(7, posId);
        } else if ("mpos".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(8, posId);
        } else if ("aposScaleUpper".equals(acn.trim().toLowerCase())){
            resultMap = getUpdateApp(12, posId);
        } else {
            resultMap = new HashMap<>();
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","参数错误");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        }
        return resultMap;
    }
    @RequestMapping(value = "/api/appUpdate")
    public @ResponseBody Map<String, Object> appUpdate(String acn,BigInteger posId) {
        Map<String, Object> resultMap = null;
        if (StringUtils.isEmpty(acn)) {
            resultMap = new HashMap<>();
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","参数错误");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        } else if ("aerp".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(4, posId);
        } else if ("fpos".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(7, posId);
        } else if ("mpos".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(8, posId);
        } else if ("aposScaleUpper".equals(acn.trim().toLowerCase())) {
            resultMap = getUpdateApp(12, posId);
        } else {
            resultMap = new HashMap<>();
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","参数错误");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
        }
        return resultMap;
    }

    private Map<String, Object> getUpdateApp(Integer type,BigInteger posId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Integer versionType = 3;
            App app = null;
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("type", type.toString());
            paramsMap.put("versionType", versionType.toString());
            paramsMap.put("isDeleted", "false");
            if(posId!=null){
                Pos pos = findPos(posId);
                Branch branch=branchMapper.findBranchByTenantIdAndBranchId(pos.getTenantId(),pos.getBranchId());
                if(branch==null){
                    throw new Exception("查询机构失败！");
                }
                if(type == 4){
                    if(StringUtils.isEmpty(branch.getAerpVersion())){
                        paramsMap.put("isLatest", "true");
                    }
                    else{
                        paramsMap.put("versionNo", branch.getAerpVersion());
                    }
                }
                else if(type == 7){
                    if(StringUtils.isEmpty(branch.getFposVersion())){
                        paramsMap.put("isLatest", "true");
                    }
                    else{
                        paramsMap.put("versionNo", branch.getFposVersion());
                    }
                }
                else if(type == 8){
                    if(StringUtils.isEmpty(branch.getMposVersion())){
                        paramsMap.put("isLatest", "true");
                    }
                    else{
                        paramsMap.put("versionNo", branch.getMposVersion());
                    }
                }
                else if(type == 12){
                    if(StringUtils.isEmpty(branch.getAposScaleVersion())){
                        paramsMap.put("isLatest", "true");
                    }
                    else{
                        paramsMap.put("versionNo", branch.getAposScaleVersion());
                    }
                }
            }else{
                paramsMap.put("isLatest", "true");
            }
            ApiRest rest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "app", "find", paramsMap);
            if (rest.getIsSuccess()) {
                app = ApiBaseServiceUtils.MapToObject((Map)rest.getData(), App.class);
            }
            if (app != null) {
                String downloadService = PropertyUtils.getDefault("app.download.service");
                resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
                resultMap.put("Version",app.getVersionNo());
                resultMap.put("MD5",app.getMd5());
                resultMap.put("ForceUpdate",app.getIsForceUpdate());
                if (type == 4 || type == 7 || type == 8 || type == 12) {
                    resultMap.put("Download",downloadService+app.getFilePath());
                }
            } else {
                resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message","已是最新版本");
            }

        } catch (Exception e) {
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","app查询异常");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
            resultMap.put("Message",e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("AppController.getUpdateApp({},{}) - {} - {}", type, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }

    private Map<String, Object> getApp(Integer type, BigInteger posId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Pos pos = findPos(posId);

            String env = PropertyUtils.getDefault("deploy.env");
            if (env == null) {
                env = "www";
            }

            Tenant tenant = null;
            ApiRest rest = SaaSApi.findTenantById(pos.getTenantId());
            if (rest.getIsSuccess()) {
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
            } else {
                throw new Exception("查询商户信息失败");
            }
            Integer versionType = 0;
            if ("test".equals(env)) {
                versionType = 1;
            } else {
                if (tenant.isBate()) {
                    versionType = 2;
                } else {
                    versionType = 3;
                }
            }
            Branch branch=branchMapper.findBranchByTenantIdAndBranchId(pos.getTenantId(),pos.getBranchId());
            if(branch==null){
                throw new Exception("查询机构失败！");
            }
            App app = null;
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("type", type.toString());
            paramsMap.put("versionType", versionType.toString());
            paramsMap.put("isDeleted", "false");
            paramsMap.put("business", tenant.getBusiness1());
            if (StringUtils.isEmpty(branch.getAppVersion())) {
                paramsMap.put("isLatest", "true");
            } else {
                paramsMap.put("versionNo", branch.getAppVersion());
            }
            rest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "app", "find", paramsMap);
            if (rest.getIsSuccess()) {
                app = ApiBaseServiceUtils.MapToObject((Map)rest.getData(), App.class);
            }
            if (app != null) {
                String downloadService = PropertyUtils.getDefault("app.download.service");
                resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
                resultMap.put("Version",app.getVersionNo());
                resultMap.put("MD5",app.getMd5());
                resultMap.put("ForceUpdate",app.getIsForceUpdate());
                if (type == 1 || type == 2 || type == 9 || type == 10) {
                    resultMap.put("Download",downloadService+app.getFilePath());
                }
            } else {
                resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
                resultMap.put("Message","已是最新版本");
            }

        } catch (Exception e) {
            resultMap.put("Result",Constants.REST_RESULT_FAILURE);
            resultMap.put("Message","app查询异常");
            resultMap.put("NewData",Constants.POS_NO_NEW_DATA);
            resultMap.put("Message",e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("AppController.getApp({},{}) - {} - {}", type, posId, e.getClass().getSimpleName(), e.getMessage());
        }
        return resultMap;
    }


}
