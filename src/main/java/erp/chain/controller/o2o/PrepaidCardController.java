package erp.chain.controller.o2o;

import com.saas.common.util.LogUtil;
import erp.chain.annotations.ApiRestAction;
import erp.chain.domain.o2o.PrepaidCardInfo;
import erp.chain.model.o2o.prepaidCard.ListPrepaidCardModel;
import erp.chain.service.o2o.PrepaidCardService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 面值卡信息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/5
 */
@RestController
public class PrepaidCardController{
    @Autowired
    private PrepaidCardService prepaidCardService;

    @RequestMapping(value = "/prepaidCard/prepaidCardList", method = RequestMethod.GET)
    @ApiRestAction(modelClass = ListPrepaidCardModel.class, serviceClass = PrepaidCardService.class, serviceMethodName = "prepaidCardList", error = "查询面值卡信息异常")
    public String prepaidCardList(){
        return null;
    }
    @RequestMapping(value = "/prepaidCard/addPrepaidCard", method = RequestMethod.POST)
    @ApiRestAction(modelClass = PrepaidCardInfo.class, serviceClass = PrepaidCardService.class, serviceMethodName = "addPrepaidCard", error = "制卡异常")
    public String addPrepaidCard(){
        return null;
    }
    @RequestMapping(value = "/prepaidCard/batchCreateCard", method = RequestMethod.POST)
    public String batchCreateCard(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("cards"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("cards不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("typeId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("typeId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.batchCreateCard(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("批量制卡异常！");
            LogUtil.logError("PrepaidCardController-batchCreateCard",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/prepaidCard/getUsedCode", method = RequestMethod.GET)
    public String getUsedCode(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.getUsedCode(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询已用卡号异常！");
            LogUtil.logError("PrepaidCardController-getUsedCode",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/prepaidCard/restoreCard", method = RequestMethod.POST)
    public String restoreCard(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("cardId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("cardId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("typeId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("typeId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.restoreCard(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("回收重制异常！");
            LogUtil.logError("PrepaidCardController-restoreCard",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/prepaidCard/saleOrRefund", method = RequestMethod.POST)
    public String saleOrRefund(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("isRefund"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("isRefund不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("cardCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("cardCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("paymentId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("paymentId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("saleCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("saleCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("amount"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("amount不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("opId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("opId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("createBy"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("createBy不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.saleOrRefund(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("操作异常！");
            LogUtil.logError("PrepaidCardController-saleOrRefund",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/prepaidCard/getPreCardInfoByCode", method = RequestMethod.GET)
    public String getPreCardInfoByCode(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("cardCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("cardCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.getPreCardInfoByCode(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("操作异常！");
            LogUtil.logError("PrepaidCardController-saleOrRefund",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping(value = "/prepaidCard/prepaidCardAccountList", method = RequestMethod.GET)
    public String prepaidCardAccountList(){
        ApiRest apiRest=new ApiRest();
        Map<String,String> params= AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest=prepaidCardService.prepaidCardAccountList(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("批量制卡异常！");
            LogUtil.logError("PrepaidCardController-prepaidCardAccountList",params,e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
