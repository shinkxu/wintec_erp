package erp.chain.controller.base;

import com.saas.common.util.LogUtil;
import erp.chain.service.base.PublicWSService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/22
 */
@Controller
@ResponseBody
@RequestMapping("/publicWS")
public class PublicWSController{
    @Autowired
    private PublicWSService publicWSService;
    /**
     * 条形码生成器
     * @author hxh
     */
    @RequestMapping("/barCreator")
    public String barCreator() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest=publicWSService.barCreator(params);
        } catch (Exception e) {
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(rest);
    }
}
