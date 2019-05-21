package erp.chain.controller.pos;

import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import erp.chain.controller.BaseController;
import erp.chain.service.MessageService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Controller
public class MessageController extends BaseController {

    @Autowired
    MessageService messageService;

    @RequestMapping(value = "/api/message", method = RequestMethod.GET)
    public @ResponseBody Object get(String acn, BigInteger posId) throws Exception{
        Map<String, Object> resultMap = null;
        Map<String,String> params = AppHandler.params();
        if (StringUtils.isEmpty(acn) || posId == null) {
            resultMap = CommonUtils.InvalidParamsError(null);
        } else if (Constants.POS_DATA_CHECK.equals(acn.trim().toLowerCase())) {
            resultMap = messageService.checkMessage(posId);
        } else if ("status".equals(acn.trim().toLowerCase())) {
            resultMap = new HashMap<>();
            String tenantId = params.get("tenantId");
            String branchId = params.get("branchId");
            String isPullLog = PartitionCacheUtils.get("PullLog_"+tenantId+"_"+branchId+"_"+posId);
            resultMap.put("isPullLog",0);
            if(StringUtils.isNotEmpty(isPullLog)){
                resultMap.put("isPullLog",1);
            }
            resultMap.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            resultMap.put("Result",Constants.REST_RESULT_SUCCESS);
        } else {
            resultMap = CommonUtils.InvalidParamsError(null);
        }

        return resultMap;
    }

    @RequestMapping(value = "/api/message", method = RequestMethod.POST)
    public @ResponseBody Object post(BigInteger posId, String code, String message) {
        if (StringUtils.isEmpty(code) || posId == null || StringUtils.isEmpty(message)) {
            return CommonUtils.InvalidParamsError(null);
        }
        return messageService.sendMessage(code, message, posId);
    }
    @RequestMapping(value = "/api/posMessage", method = RequestMethod.GET)
    public @ResponseBody String posMessage() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = messageService.posMessage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
