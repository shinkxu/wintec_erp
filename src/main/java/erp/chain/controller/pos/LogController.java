package erp.chain.controller.pos;

import erp.chain.controller.BaseController;
import erp.chain.service.LogService;
import erp.chain.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by xumx on 2016/11/8.
 */
@Controller
public class LogController extends BaseController {

    @Autowired
    LogService logService;

    @RequestMapping(value = "/api/log", method = RequestMethod.POST)
    public @ResponseBody Object post(String acn, BigInteger posId, MultipartFile file) {
        Map<String, Object> resultMap = null;
        if (StringUtils.isEmpty(acn)) {
            resultMap = CommonUtils.InvalidParamsError(null);
        } else if ("console".equals(acn)) {
            resultMap = logService.console(posId, file);
        } else {
            resultMap = CommonUtils.InvalidParamsError(null);
        }
        return resultMap;
    }

}
