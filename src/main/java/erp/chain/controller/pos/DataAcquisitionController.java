package erp.chain.controller.pos;

import com.saas.common.util.LogUtil;
import erp.chain.common.Constants;
import erp.chain.service.DataAcquisitionService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * Created by liuyandong on 2017/4/20.
 */
@Controller
@RequestMapping(value = "/dataAcquisition")
public class DataAcquisitionController {
    private static final String DATA_ACQUISITION_CONTROLLER_SIMPLE_NAME = "DataAcquisitionController";
    @Autowired
    private DataAcquisitionService dataAcquisitionService;

    @RequestMapping(value = "/graspDataSynchronization")
    @ResponseBody
    public String graspDataSynchronization() {
        ApiRest apiRest = new ApiRest();
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = dataAcquisitionService.graspDataSynchronization();
        } catch (Exception e) {
            LogUtil.logError(String.format(Constants.LOGGER_ERROR_FORMAT, "数据同步失败", DATA_ACQUISITION_CONTROLLER_SIMPLE_NAME, "graspDataSynchronization", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
