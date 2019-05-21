package erp.chain.controller.online;

import erp.chain.annotations.ApiRestAction;
import erp.chain.model.online.onlineconfig.ListModel;
import erp.chain.model.online.onlineconfig.ObtainTabBarsModel;
import erp.chain.model.online.onlineconfig.SaveHomePageConfigModel;
import erp.chain.service.online.OnlineConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liuyandong on 2018-04-19.
 */
@Controller
@RequestMapping(value = "/onlineConfig")
public class OnlineConfigController {
    @RequestMapping(value = "/saveHomePageConfig")
    @ResponseBody
    @ApiRestAction(modelClass = SaveHomePageConfigModel.class, serviceClass = OnlineConfigService.class, serviceMethodName = "saveHomePageConfig", error = "保存首页配置失败")
    public String saveHomePageConfig() {
        return null;
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    @ApiRestAction(modelClass = ListModel.class, serviceClass = OnlineConfigService.class, serviceMethodName = "list", error = "获取列表失败")
    public String list() {
        return null;
    }

    @RequestMapping(value = "/obtainTabBars")
    @ResponseBody
    @ApiRestAction(modelClass = ObtainTabBarsModel.class, serviceClass = OnlineConfigService.class, serviceMethodName = "obtainTabBars", error = "获取底部导航失败")
    public String obtainTabBars() {
        return null;
    }
}