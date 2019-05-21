package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.setting.MappingSettingService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by lipeng on 2017/5/8.
 */

@Controller
@ResponseBody
@RequestMapping(value = {"/mappingSetting","/settingWebService","/baseWS"})
public class MappingSettingController {
    private static final String MAPPING_SETTING_CONTROLLER_SIMPLE_NAME = "MappingSettingController";

    @Autowired
    private MappingSettingService mappingSettingService;
    /**
     * 分页显示商品对应关系
     * @return
     * */
    /*@RequestMapping("/listGoodsMapping")
    public String listGoodsMapping(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.listGoodsMapping(params);
        return BeanUtils.toJsonStr(apiRest);
    }*/

    @RequestMapping(value = "/listGoodsMapping")
    @ResponseBody
    public String listGoodsMapping() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String tenantCode = requestParameters.get("tenantCode");
            Validate.notNull(tenantCode, "参数(tenantCode)不能为空！");

            String distributionCenterId = requestParameters.get("branchId");
            Validate.notNull(distributionCenterId, "参数(branchId)不能为空！");

            String goodsCodeOrName = requestParameters.get("goodsCodeOrName");

            String page = requestParameters.get("page");
            if (StringUtils.isBlank(page)) {
                page = "1";
            }

            String rows = requestParameters.get("rows");
            if (StringUtils.isBlank(rows)) {
                rows = "20";
            }
            int onlySelf = 0;
            if(requestParameters.get("onlySelf") != null && !"".equals(requestParameters.get("onlySelf"))){
                onlySelf = Integer.parseInt(requestParameters.get("onlySelf").toString());
            }
            apiRest = mappingSettingService.listGoodsMapping(BigInteger.valueOf(Long.valueOf(tenantId)), tenantCode, BigInteger.valueOf(Long.valueOf(distributionCenterId)), Integer.valueOf(page), Integer.valueOf(rows), goodsCodeOrName,onlySelf);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询产品信息失败", MAPPING_SETTING_CONTROLLER_SIMPLE_NAME, "listGoodsMapping", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页显示第三方商品（没有与goods表映射的产品）
     * @return
     * */
    /*@RequestMapping("/listThirdGoods")
    public String listThirdGoods(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("tenantCode") == null || "".equals(params.get("tenantCode"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantCode不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.listThirdGoods(params);
        return BeanUtils.toJsonStr(apiRest);
    }*/

    @RequestMapping(value = "/listThirdGoods")
    @ResponseBody
    public String listThirdGoods() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String tenantCode = requestParameters.get("tenantCode");
            Validate.notNull(tenantCode, "参数(tenantCode)不能为空！");

            String distributionCenterId = requestParameters.get("branchId");
            Validate.notNull(distributionCenterId, "参数(branchId)不能为空！");

            String addGoodsIds = requestParameters.get("addGoodsIds");
            String deleteGoodIds = requestParameters.get("deleteGoodIds");
            String goodsCodeOrName = requestParameters.get("goodsCodeOrName");

            String page = requestParameters.get("page");
            if (StringUtils.isBlank(page)) {
                page = "1";
            }

            String rows = requestParameters.get("rows");
            if (StringUtils.isBlank(rows)) {
                rows = "20";
            }
            apiRest = mappingSettingService.listThirdGoods(BigInteger.valueOf(Long.valueOf(tenantId)), tenantCode, BigInteger.valueOf(Long.valueOf(distributionCenterId)), addGoodsIds, deleteGoodIds, goodsCodeOrName, Integer.valueOf(page), Integer.valueOf(rows));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "查询产品信息失败", MAPPING_SETTING_CONTROLLER_SIMPLE_NAME, "listThirdGoods", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存商品对应关系
     * @return
     * */
    /*@RequestMapping("/saveGoodsMapping")
    public String saveGoodsMapping(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.saveGoodsMapping(params);
        return BeanUtils.toJsonStr(apiRest);
    }*/

    @RequestMapping(value = "/saveGoodsMapping")
    @ResponseBody
    public String saveGoodsMapping() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String distributionCenterId = requestParameters.get("branchId");
            Validate.notNull(distributionCenterId, "参数(branchId)不能为空！");

            String paramsJson = requestParameters.get("paramsJson");
            Validate.notNull(paramsJson, "参数(paramsJson)不能为空！");

            apiRest = mappingSettingService.saveGoodsMapping(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(distributionCenterId)), paramsJson);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "保存商品对应关系失败", MAPPING_SETTING_CONTROLLER_SIMPLE_NAME, "saveGoodsMapping", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页显示机构对应关系
     * @return
     * */
    @RequestMapping("/listBranchMapping")
    public String listBranchMapping(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("tenantCode") == null || "".equals(params.get("tenantCode"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantCode不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.listBranchMapping(params);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页显示第三方机构（没有与本地机构对应的机构）
     * @return
     * */
    @RequestMapping("/listThirdBranch")
    public String listThirdBranch(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals("tenantId")){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("tenantCode") == null || "".equals(params.get("tenantCode"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantCode不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("branchId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.listThirdBranch(params);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存机构对应关系
     * @return
     * */
    @RequestMapping("/saveBranchMapping")
    public String saveBranchMapping(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if(params.get("branchId") == null || "".equals(params.get("distributionCenterId"))){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("参数distributionCenterId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest = mappingSettingService.saveBranchMapping(params);
        return BeanUtils.toJsonStr(apiRest);
    }
}
