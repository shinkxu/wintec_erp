package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.DietPromotionService;
import erp.chain.utils.AppHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by songzhiqiang on 2017/1/18.
 */
@Controller
@ResponseBody
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private DietPromotionService dietPromotionService;
    /**
     * 查询促销活动
     */
    @RequestMapping("/queryDietpromotionList")
    public ResultJSON queryDietpromotionList(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return resultJSON;
            }
            resultJSON = dietPromotionService.queryDietpromotionList(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return resultJSON;
    }

    /**
     * 查询卡券促销活动和卡券游戏促销活动列表
     */
    @RequestMapping("queryCardCoupnseAndGame")
    public ResultJSON queryCardCoupnseAndGame(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return resultJSON;
            }
            resultJSON = dietPromotionService.queryCardCoupnseAndGame(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return resultJSON;
    }

    /**
     * 删除促销活动
     */
    @RequestMapping("/delDietPromotion")
    public ResultJSON delDietPromotion(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String id = params.get("id");
            if(StringUtils.isBlank(id)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数id不能为空！");
                return resultJSON;
            }
            resultJSON = dietPromotionService.delDietPromotion(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return resultJSON;
    }
    /**
     * 检查促销活动
     */
    @RequestMapping("/checkClickStop")
    public ResultJSON checkClickStop(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try{
            String id = params.get("id");
            if(StringUtils.isBlank(id)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数id不能为空！");
                return resultJSON;
            }
            resultJSON = dietPromotionService.checkClickStop(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return resultJSON;
    }
    /**
     * 终止活动
     */
    @RequestMapping("/clickStop")
    public ResultJSON clickStop(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try{
            String id = params.get("id");
            if(StringUtils.isBlank(id)){
                resultJSON.setIsSuccess(false);
                resultJSON.setMsg("参数id不能为空！");
                return resultJSON;
            }
            resultJSON = dietPromotionService.clickStop(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return resultJSON;
    }
}
