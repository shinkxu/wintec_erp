package erp.chain.controller.o2o;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.o2o.AlipayVipService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * Created by liuyandong on 2018-01-22.
 */
@Controller
@RequestMapping(value = "/alipayVip")
public class AlipayVipController {
    private static final String ALIPAY_VIP_CONTROLLER_SIMPLE_NAME = "AlipayVipController";

    @Autowired
    private AlipayVipService alipayVipService;

    @RequestMapping(value = "/createCardTemplate")
    @ResponseBody
    public String createCardTemplate() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");

            apiRest = alipayVipService.createCardTemplate(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "创建支付宝会员卡模板失败", ALIPAY_VIP_CONTROLLER_SIMPLE_NAME, "createCardTemplate", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/obtainAlipayMarketingCardTemplate")
    @ResponseBody
    public String obtainAlipayMarketingCardTemplate() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");

            apiRest = alipayVipService.obtainAlipayMarketingCardTemplate(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取支付宝会员卡模板失败", ALIPAY_VIP_CONTROLLER_SIMPLE_NAME, "obtainAlipayMarketingCardTemplate", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping(value = "/obtainVipInfo")
    @ResponseBody
    public String obtainVipInfo() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notEmpty(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notEmpty(branchId, "参数(branchId)不能为空！");

            String vipCode = requestParameters.get("vipCode");
            Validate.notEmpty(vipCode, "参数(vipCode)不能为空！");

            apiRest = alipayVipService.obtainVipInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), vipCode);
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "获取会员信息失败", ALIPAY_VIP_CONTROLLER_SIMPLE_NAME, "obtainVipInfo", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
