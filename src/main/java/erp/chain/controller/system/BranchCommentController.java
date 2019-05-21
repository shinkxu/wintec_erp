package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.BranchCommentService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 店铺评价
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2019/1/17
 */
@Controller
@ResponseBody
@RequestMapping("/branchComment")
public class BranchCommentController{

    @Autowired
    private BranchCommentService branchCommentService;

    @RequestMapping("/getBranchComment")
    public String getBranchComment(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
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
            apiRest = branchCommentService.getBranchComment(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/initBranchComment")
    public String initBranchComment(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
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
            /*if(StringUtils.isEmpty(params.get("saleCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数saleCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            apiRest = branchCommentService.initBranchComment(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/addOrUpdateComment")
    public String addOrUpdateComment(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
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
            if(StringUtils.isEmpty(params.get("commentId"))){
                if(StringUtils.isEmpty(params.get("customerName"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数customerName不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if(StringUtils.isEmpty(params.get("customerSex"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数customerSex不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if(StringUtils.isEmpty(params.get("customerScore"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数customerScore不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if(StringUtils.isEmpty(params.get("comment"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数comment不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }else{
                if(StringUtils.isEmpty(params.get("extraComment"))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("参数extraComment不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = branchCommentService.addOrUpdateComment(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
