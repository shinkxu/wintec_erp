package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.OnlinePayInfoService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/1/10
 */
@Controller
@ResponseBody
@RequestMapping("/onlinePayInfo")
public class OnlinePayInfoController{
    @Autowired
    private OnlinePayInfoService onlinePayInfoService;


    @RequestMapping("/saveInfo")
    public String saveInfo(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest= onlinePayInfoService.saveInfo(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
