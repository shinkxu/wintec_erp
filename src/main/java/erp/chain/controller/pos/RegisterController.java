package erp.chain.controller.pos;

import erp.chain.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xumx on 2016/10/31.
 */
@Controller
public class RegisterController extends BaseController {

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public @ResponseBody String index(String pos_code, String pos_password, String branch_code, String tenant_code, String device_code) {
        return "ok";
    }

}
