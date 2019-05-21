package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.AdvertisementService;
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
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/11/2
 */
@Controller
@ResponseBody
@RequestMapping("/advertisement")
public class AdvertisementController{
    @Autowired
    private AdvertisementService advertisementService;

    @RequestMapping("/listAdBanner")
    public String listAdBanner(){
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
            apiRest = advertisementService.listAdBanner(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/listAdContent")
    public String listAdContent(){
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
            apiRest = advertisementService.listAdContent(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/bannerDetail")
    public String bannerDetail(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("bannerId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数bannerId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.bannerDetail(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/contentDetail")
    public String contentDetail(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("contentId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数contentId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.contentDetail(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/deleteBanner")
    public String deleteBanner(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("bannerId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数bannerId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.deleteBanner(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/deleteContent")
    public String deleteContent(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("contentId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数contentId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.deleteContent(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/addOrUpdateBanner")
    public String addOrUpdateBanner(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("name"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("description"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数description不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("bannerType"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数bannerType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("displayMode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数displayMode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("width"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数width不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("height"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数height不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("active"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数active不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("sortCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数sortCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("empId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数empId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.addOrUpdateBanner(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/addOrUpdateContent")
    public String addOrUpdateContent(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("name"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("mediaType"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mediaType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("resourceUrl"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数resourceUrl不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("beginDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数beginDate不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("endDate"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数endDate不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if (StringUtils.isEmpty(params.get("plays"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数plays不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (StringUtils.isEmpty(params.get("hits"))) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数hits不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }*/
            if(StringUtils.isEmpty(params.get("sortCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数sortCode不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("empId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数empId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            apiRest = advertisementService.addOrUpdateContent(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/loadBranchAdContent")
    public String loadBranchAdContent(){
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
            apiRest = advertisementService.loadBranchAdContent(params);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
