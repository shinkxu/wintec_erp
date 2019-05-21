package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/17.
 */
@Controller
@ResponseBody
@RequestMapping("/cardCoupons")
public class CardCouponsController {
    @Autowired
    private CardCouponsService cardCouponsService;
    /**
     * 查询卡券
     */
    @RequestMapping("/qryCardList")
    public String qryCardList(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(resultJSON);
            }
            resultJSON = cardCouponsService.qryCardList(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(resultJSON);
    }


    /**
     * 查询卡券
     */
    @RequestMapping("/qryCardReport")
    public String qryCardReport(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(resultJSON);
            }
            resultJSON = cardCouponsService.qryCardReport(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(resultJSON);
    }

    /**
     * 查询会员卡券
     */
    @RequestMapping("/vipCardList")
    public String vipCardList(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(resultJSON);
            }
            resultJSON = cardCouponsService.vipCardList(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(resultJSON);
    }

    /**
     * 查询会员卡券
     */
    @RequestMapping("/getVipCardByCode")
    public String getVipCardByCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            String cardCode = params.get("cardCode");
            String vipId = params.get("vipId");
            if(StringUtils.isBlank(tenantId)){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数tenantId不能为空！");
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isBlank(cardCode)){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数cardCode不能为空！");
                apiRest.setError("参数cardCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = cardCouponsService.getVipCardByCode(params);
        } catch(Exception e) {
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage("操作失败!");
            apiRest.setError("操作失败!");
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /**
     * 保存卡券
     */
    @RequestMapping("/saveCardCoupons")
    public ResultJSON saveCardCoupons(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String tenantId = params.get("tenantId");
            if("1".equals(params.get("openSendRule"))){
                if (StringUtils.isBlank(tenantId)){
                    resultJSON.setSuccess("1");
                    resultJSON.setMsg("参数tenantId不能为空！");
                    return resultJSON;
                }
                String[] forCustomerType = new String[3];
                forCustomerType[0] = "0";
                forCustomerType[1] = "1";
                forCustomerType[2] = "2";
                Args.isIn(params.get("forCustomerType"), forCustomerType, "请选择适用对象");
                Args.isIn(params.get("scope"), forCustomerType, "请选择活动范围");
                Args.notNull(params.get("weekInsert"), "星期不能为空");
                if(params.get("startDate") == null){
                    params.put("startDate",format.format(new Date()));
                }
                if(params.get("endDate") == null){
                    params.put("endDate","2099-12-31");
                }
            }
            if("-1".equals(params.get("limitGoodsIds"))){
                params.put("limitGoodsIds","");
            }
            if("-1".equals(params.get("limitGoodsNames"))){
                params.put("limitGoodsNames","");
            }
            resultJSON = cardCouponsService.saveCardCoupons(params);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg(e.getMessage());
        }
        return resultJSON;
    }

    /**
     * 删除卡券
     */
    @RequestMapping("/delCardCoupons")
    public ResultJSON delCardCoupons(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String id = params.get("id");
            if (StringUtils.isBlank(id)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数id不能为空！");
                return resultJSON;
            }
            params.put("isDeleted","1");
            resultJSON = cardCouponsService.delCardCoupons(params);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败");
        }
        return resultJSON;
    }
    /**卡券调整后方法**/


    @RequestMapping(value = "/addOrUpdateCardCoupons")
    public String addOrUpdateCardCoupons(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params=AppHandler.params();
        try{
            apiRest=cardCouponsService.addOrUpdateCardCoupons(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("保存优惠券异常！");
            LogUtil.logInfo("保存优惠券信息异常！"+e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/getCardCouponsById")
    public String getCardCouponsById(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params=AppHandler.params();
        try{
            apiRest=cardCouponsService.getCardCouponsById(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询优惠券异常！");
            LogUtil.logInfo("查询优惠券异常！"+e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 创建发券活动
     */
    @RequestMapping("/saveSendCouponsActivity")
    public String saveSendCouponsActivity(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest = cardCouponsService.saveSendCouponsActivity(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("创建发券活动异常！");
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询发券活动
     */
    @RequestMapping("/listMarketingOrder")
    public String listMarketingOrder(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = cardCouponsService.listMarketingOrder(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询礼品活动异常！");
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 群发详情
     */
    @RequestMapping("/orderDetail")
    public String orderDetail(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("orderId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数orderId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = cardCouponsService.orderDetail(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询礼品活动异常！");
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询优惠券报表
     */
    @RequestMapping("/cardCouponsAccount")
    public String cardCouponsAccount(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("acn"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数acn不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = cardCouponsService.cardCouponsAccount(params);
        }catch (Exception e) {
            LogUtil.logError(e,params);
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询优惠券报表成功！");
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
