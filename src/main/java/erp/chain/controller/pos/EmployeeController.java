package erp.chain.controller.pos;

import com.saas.common.util.LogUtil;
import erp.chain.controller.BaseController;
import erp.chain.service.EmployeeService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Controller
public class EmployeeController extends BaseController {

    @Autowired
    EmployeeService employeeService;

    @RequestMapping(value = "/public/employee/find")
    public
    @ResponseBody
    Object find(String business, BigInteger tenantId, BigInteger branchId, String employeeCode) {
        if (StringUtils.isEmpty(business)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            r.setData(employeeService.find(tenantId, branchId, employeeCode));
            r.setIsSuccess(true);
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setMessage("员工查询异常");
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("EmployeeController.find({},{},{}) - {}", tenantId, branchId, employeeCode, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/public/employee/saveCard")
    public
    @ResponseBody
    Object saveCard(String business, BigInteger id, BigInteger cardId, String cardCode) {
        if (StringUtils.isEmpty(business) || id == null || cardId == null || StringUtils.isEmpty(cardCode)) {
            return ApiRest.INVALID_PARAMS_ERROR;
        }
        ApiRest r = new ApiRest();
        try {
            int result = employeeService.saveCard(id, cardId, cardCode);
            if (result > 0) {
                r.setIsSuccess(true);
            } else if (result == 0) {
                r.setIsSuccess(false);
                r.setMessage("无效员工信息");
            } else if (result == -1) {
                r.setIsSuccess(false);
                r.setMessage("员工卡保存失败");
            }
        } catch (Exception e) {
            r.setIsSuccess(false);
            r.setMessage("员工卡保存异常");
            r.setError(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
            log.error("EmployeeController.saveCard({},{},{}) - {}", id, cardId, cardCode, r.getError());
        }
        return r;
    }

    @RequestMapping(value = "/public/checkPhoneRole")
    public
    @ResponseBody
    String checkPhoneRole() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        if (params.get("tenantId") == null || params.get("tenantId").equals("")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("参数tenantId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if (params.get("branchId") == null || params.get("tenantId").equals("")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("参数branchId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        if (params.get("userId") == null || params.get("userId").equals("")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("参数userId不能为空！");
            return BeanUtils.toJsonStr(apiRest);
        }
        try {
            apiRest = employeeService.checkPhoneRole(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }


}
