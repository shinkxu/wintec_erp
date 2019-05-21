package erp.chain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xumx on 2016/10/31.
 */
@Controller
public class IndexController extends BaseController {

    @RequestMapping(value = "/")
    public @ResponseBody String index() {
        return "Hello World";
    }
}
