package erp.chain.controller.pos;

import com.saas.common.Constants;
import erp.chain.controller.BaseController;
import erp.chain.domain.Pos;
import erp.chain.service.IcService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumx on 2016/11/15.
 */
@Controller
public class IcController extends BaseController {

    @Autowired
    IcService icService;

    @RequestMapping(value = "/api/ic", method = RequestMethod.GET)
    public @ResponseBody Object get(String acn, BigInteger posId, String data) {
        Map<String, String> resultMap = null;

        if (StringUtils.isEmpty(acn) || posId == null) {
            return CommonUtils.InvalidParamsError(null);
        }
        try {
            Pos pos = findPos(posId);
            if ("RSA".equals(acn.trim())) {
                resultMap = icService.rsa(pos.getId().toString(), pos.getTenantCode());
            } else if ("AES".equals(acn.trim())) {
                resultMap = icService.aes(pos.getId().toString(), pos.getPosCode(), pos.getTenantCode(), data);
            } else if ("verifyAuthCard".equals(acn.trim())) {
                resultMap = icService.verifyCard(posId.toString(), data, erp.chain.common.Constants.CARD_TYPE_AUTH, pos.getTenantId().toString(), pos.getTenantCode(), pos.getBranchId().toString(), pos.getBranchCode(), pos.getPosCode());
            } else if ("verifyUserCard".equals(acn.trim())) {
                resultMap = icService.verifyCard(posId.toString(), data, erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP, pos.getTenantId().toString(), pos.getTenantCode(), pos.getBranchId().toString(), pos.getBranchCode(), pos.getPosCode());
            } else if ("findCardByHolderId".equals(acn.trim())) {
                resultMap = icService.findCardByHolderId(4,posId.toString(), data, pos.getTenantId().toString(), pos.getTenantCode(), pos.getBranchId().toString(), pos.getBranchCode(), pos.getPosCode());
            } else if ("findCardByEmpId".equals(acn.trim())) {
                resultMap = icService.findCardByHolderId(2,posId.toString(), data, pos.getTenantId().toString(), pos.getTenantCode(), pos.getBranchId().toString(), pos.getBranchCode(), pos.getPosCode());
            } else if ("findMasterCard".equals(acn.trim())){
                resultMap = icService.findMasterCard(pos);
            } else {
                return CommonUtils.InvalidParamsError(null);
            }
        } catch (Exception e) {
            resultMap = new HashMap<>();
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "IC服务异常");
            resultMap.put("Error", String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("IcController.get() - {}", resultMap.get("Error"));
        }

        return resultMap;
    }

    @RequestMapping(value = "/api/ic", method = RequestMethod.POST)
    public @ResponseBody Object post(String acn, BigInteger posId, String data) {
        Map<String, String> resultMap = null;

        if (StringUtils.isEmpty(acn) || posId == null) {
            return CommonUtils.InvalidParamsError(null);
        }
        try {
            Pos pos = findPos(posId);
            if ("initMasterCard".equals(acn.trim())) {
                resultMap = icService.initCard(pos, data, "initMasterCard");
            } else if("initMasterCardNoAuthCode".equals(acn.trim())){
                resultMap = icService.initCard(pos, data, "initMasterCardNoAuthCode");
            } else if ("initAuthCard".equals(acn.trim())) {
                resultMap = icService.initCard(pos, data, "initAuthCard");
            } else if ("initUserCard".equals(acn.trim())) {
                resultMap = icService.initCard(pos, data, "initUserCard");
            } else if ("initEmployeeCard".equals(acn.trim())) {
                resultMap = icService.initCard(pos, data, "initEmployeeCard");
            } else if ("cardRecord".equals(acn.trim())) {
                resultMap = icService.cardRecord(pos.getId().toString(), pos.getTenantCode(), data);
            } else if ("deleCard".equals(acn.trim())) {
                resultMap = icService.deleCard(pos.getId().toString(), data, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode());
            } else if ("deleCardNew".equals(acn.trim())) {
                resultMap = icService.deleCardNew(pos.getId().toString(), data, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode());
            } else if ("updateCardFaceNum".equals(acn.trim())) {
                resultMap = icService.updateCardFaceNum(pos.getId().toString(), pos.getTenantCode(), data);
            } else {
                return CommonUtils.InvalidParamsError(null);
            }
        } catch (Exception e) {
            resultMap = new HashMap<>();
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "IC服务异常");
            resultMap.put("Error", String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("IcController.post() - {}", resultMap.get("Error"));
        }

        return resultMap;
    }

    @RequestMapping(value = "/ic/findCardByCode", method = RequestMethod.GET)
    public @ResponseBody Object findCardByCode() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest=icService.findCardByCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询卡信息异常");
            log.error("IcController.findCardByCode()"+e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

}
