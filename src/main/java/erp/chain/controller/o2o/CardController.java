package erp.chain.controller.o2o;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.CardService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.GsonUntil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡管理
 * Created by wangms on 2017/9/26.
 */
@Controller
@ResponseBody
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;


    /**
     * 新增卡方案
     * @return
     */
    @RequestMapping("/addCardType")
    public String addCardType(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("typeName"), "方案名称不能为空！");
            Args.notNull(params.get("cardKind"), "方案种类不能为空！");
            if("3".equals(params.get("cardKind"))){
                if(params.get("tmGoodsId") == null || "".equals(params.get("tmGoodsId"))){
                    rest.setIsSuccess(false);
                    rest.setMessage("商品名称不能为空，请选择商品！");
                    return BeanUtils.toJsonStr(rest);
                }
            }
            if("2".equals(params.get("cardKind"))){
                if(params.get("denomination") == null || "".equals(params.get("denomination"))){
                    rest.setIsSuccess(false);
                    rest.setMessage("面值卡面值不能为空");
                    return BeanUtils.toJsonStr(rest);
                }
            }
            rest = cardService.addOrUpdateCardType(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("新增卡方案!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 修改卡方案
     * @return
     */
    @RequestMapping("/updateCardType")
    public String updateCardType(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("id"), "id不能为空！");
            rest = cardService.addOrUpdateCardType(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("新增卡方案!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询卡方案列表
     * @return
     */
    @RequestMapping("/listCardTypes")
    public String listCardTypes(){
        ResultJSON rest = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            rest = cardService.listCardTypes(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMsg("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 预付卡开卡、制卡
     * @return
     */
    @RequestMapping("/createCardAccount")
    public String createCardAccount(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("cardCode"), "cardCode不能为空！");
            Args.notNull(params.get("cardTypeId"), "cardTypeId不能为空！");
            rest = cardService.createCardAccount(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("新增卡方案!");
        }
        return BeanUtils.toJsonStr(rest);
    }


    /**
     * 查询卡账户列表
     * @return
     */
    @RequestMapping("/listCardAccounts")
    public String listCardAccounts(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            rest = cardService.listCardAccounts(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询卡账户列表失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询卡账户
     * @return
     */
    @RequestMapping("/listCardAccountsForErp")
    public String listCardAccountsForErp(){
        ResultJSON rest = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            rest = cardService.listCardAccounts1(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMsg("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }


    /**
     * 查询卡台帐
     * @return
     */
    @RequestMapping("/listCardBook")
    public String listCardBook(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            rest = cardService.listCardBook(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 充值接口
     * @return
     */
    @RequestMapping("/storeForCard")
    public String storeForCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("cardCode"), "cardCode不能为空！");
            rest = cardService.storeForCard(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 退卡(非会员卡)
     * @return
     */
    @RequestMapping("/refundOtherCard")
    public String refundOtherCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("cardCode"), "cardCode不能为空！");
            rest = cardService.refundOtherCard(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 次卡查询
     * @return
     */
    @RequestMapping("/findTimesCardByVipId")
    public String findTimesCardByVipId(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("vipId"), "vipId不能为空！");
            rest = cardService.findTimesCardByVipId(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 次卡统计数据查询
     * @return
     */
    @RequestMapping("/qryTimesCardData")
    public String qryTimesCardData(){
        ResultJSON rest = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            rest = cardService.qryTimesCardData(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMsg("查询卡方案失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }


    /**
     * 次卡充值（购买次卡）
     * @return
     */
    @RequestMapping("/createTimesCard")
    public String createTimesCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("vipId"), "vipId不能为空！");
            Args.notNull(params.get("typeId"), "typeId不能为空！");
            rest = cardService.createTimesCard(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("次卡充值失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 修改卡账户（次卡使用，次卡退卡等）
     * @return
     */
    @RequestMapping("/updateCardAccount")
    public String updateCardAccount(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("data"), "data不能为空！");
            //业务类型 business 1:次卡退卡 2次卡使用
            Args.notNull(params.get("business"), "business不能为空！");
            rest = cardService.updateCardAccount(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("次卡更新异常：" + e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 创建卡(app)
     * @return
     */
    @RequestMapping("/createCard")
    public String createCard(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"), "tenantId不能为空！");
            Args.notNull(params.get("branchId"), "branchId不能为空！");
            Args.notNull(params.get("vipId"), "branchId不能为空！");
            Args.notNull(params.get("code"), "卡号不能为空！");
            Args.notNull(params.get("tenantCode"), "tenantCode不能为空！");
            Args.notNull(params.get("branchCode"), "branchCode不能为空！");
            rest = cardService.createCard(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("创建卡失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 会员生日（定时任务）
     * @return
     */
    @RequestMapping("/vipBirthdayThread")
    public String vipBirthdayThread(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = cardService.vipBirthdayThread(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("会员生日（定时任务）!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 清空积分（定时任务）
     * @return
     */
    @RequestMapping("/clearScoreThread")
    public String clearScoreThread(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = cardService.clearScoreThread(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("清空积分（定时任务）!");
        }
        return BeanUtils.toJsonStr(rest);
    }
}
