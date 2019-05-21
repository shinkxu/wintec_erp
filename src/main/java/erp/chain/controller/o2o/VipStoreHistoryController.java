package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.VipStoreHistoryService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/16.
 */
@Controller
@ResponseBody
@RequestMapping("/vipStoreHistory")
public class VipStoreHistoryController {
    @Autowired
    private VipStoreHistoryService vipStoreHistoryService;

    /**
     * 查询储值历史记录列表
     * @return
     */
    @RequestMapping("/vipStoreHistoryList")
    public String vipStoreHistoryList(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreHistoryService.vipStoreList(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

}
