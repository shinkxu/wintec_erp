package erp.chain.controller.report;

import erp.chain.model.report.GiveAndReturnPagerModel;
import erp.chain.service.report.GiveAndReturnService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

/**
 * 赠送退菜报表
 *
 * @author hefuzi 2016-11-29
 */
@Controller
@ResponseBody
@RequestMapping("/giveAndReturn")
public class GiveAndReturnController {
    @Autowired
    private GiveAndReturnService giveAndReturnService;

    /**
     * 分页查询
     */
    @RequestMapping("/queryPager")
    public String queryPager() {
        ApiRest rest = new ApiRest();
        GiveAndReturnPagerModel model = AppHandler.bindParam(GiveAndReturnPagerModel.class);
        if (!model.validate()) {
            rest.setData(model.getErrors());
            return BeanUtils.toJsonStr(rest);
        }
        rest.setData(giveAndReturnService.queryPager(model).toMap());
        return BeanUtils.toJsonStr(rest);
    }
}
