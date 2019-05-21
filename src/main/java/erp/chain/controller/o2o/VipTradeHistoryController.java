package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.VipTradeHistoryService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/18.
 */
@Controller
@ResponseBody
@RequestMapping("/vipTradeHistory")
public class VipTradeHistoryController {
    @Autowired
    private VipTradeHistoryService vipTradeHistoryService;

    /**
     * 查询消费历史记录列表
     * @return
     */
    @RequestMapping("vipTradeHistoryList")
    public String vipTradeHistoryList(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipTradeHistoryService.vipTradeList(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

}
