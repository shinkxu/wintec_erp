package erp.chain.controller.pos;

import erp.chain.controller.BaseController;
import erp.chain.service.SetService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;

/**
 * Created by xumx on 2016/11/8.
 */
@Controller
public class SetController extends BaseController {

    @Autowired
    SetService setService;

    @RequestMapping(value = "/api/set", method = RequestMethod.POST)
    public @ResponseBody Object post(String acn, BigInteger posId, String oldPass, String newPass, BigInteger userId, String name, String version) {
        ApiRest r;
        if (StringUtils.isEmpty(acn) || posId == null) {
            r = ApiRest.INVALID_PARAMS_ERROR;
        } else if ("resetPassword".equals(acn.trim())) {
            r = new ApiRest();
            int i = setService.resetPassword(posId, oldPass, newPass, userId);
            if (i > 0) {
                r.setIsSuccess(true);
            } else {
                r.setIsSuccess(false);
                r.setMessage("修改密码失败");
            }
        } else if ("version".equals(acn.trim())) {
            r = new ApiRest();
            int i = setService.version(posId, name, version);
            if (i > 0) {
                r.setIsSuccess(true);
            } else {
                r.setIsSuccess(false);
                r.setMessage("App版本保存失败");
            }
        } else {
            r = ApiRest.INVALID_PARAMS_ERROR;
        }
        return r;
    }

}
