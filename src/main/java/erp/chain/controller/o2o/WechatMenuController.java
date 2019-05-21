package erp.chain.controller.o2o;

import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.WechatMenuService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 微信菜单
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/28
 */
@Controller
@ResponseBody
@RequestMapping("/wechatMenu")
public class WechatMenuController{
    @Autowired
    private WechatMenuService wechatMenuService;

    /**
     * 查询微信菜单
     * @return
     */
    @RequestMapping("/listWxMenu")
    public String listWxMenu(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.listWxMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询一级菜单及其二级菜单
     * @return
     */
    @RequestMapping("/findWxMenuById")
    public String findWxMenuById(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("menuId")==null||params.get("menuId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数menuId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.findWxMenuById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除微信菜单
     * @return
     */
    @RequestMapping("/delWxMenu")
    public String delWxMenu(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("menuId")==null||params.get("menuId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数menuId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.delWxMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增或修改微信菜单
     * @return
     */
    @RequestMapping("/addOrUpdateWxMenu")
    public String addOrUpdateWxMenu(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数name不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("menuType")==null||params.get("menuType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数menuType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            /*if(params.get("menuType").equals("1")){
                if (params.get("msgContent")==null) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未传参数msgContent！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if(params.get("menuType").equals("2")){
                if (params.get("mediaId")==null||params.get("mediaId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未传参数mediaId！");
                    return BeanUtils.toJsonStr(apiRest);
                }
                if (params.get("url")==null||params.get("url").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未传参数url！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if(params.get("menuType").equals("3")){
                if (params.get("chainedAddress")==null||params.get("chainedAddress").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未传参数chainedAddress！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }*/
            if(params.get("menuType").equals("4")){
                if (params.get("itemId")==null||params.get("itemId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未传参数itemId！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            if (params.get("menuId")==null||params.get("menuId").equals("")) {
                if (params.get("parentId")==null||params.get("parentId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增菜单时，参数parentId不能为空！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = wechatMenuService.addOrUpdateWxMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 清空一级菜单内容
     * @return
     */
    @RequestMapping("/emptyWxMenuOneContent")
    public String emptyWxMenuOneContent(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("menuId")==null||params.get("menuId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数menuId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.emptyWxMenuOneContent(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查看跳转功能的名称
     * @return
     */
    @RequestMapping("/listMenuItemName")
    public String listMenuItemName(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = wechatMenuService.listMenuItemName();
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 修改菜单排序
     * @author
     */
    @RequestMapping("/editRank")
    public String editRank(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rankArr")==null||params.get("rankArr").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rankArr不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.editRank(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 推送
     * @author
     */
    @RequestMapping("/pushMenu")
    public String pushMenu(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("userId")==null||params.get("userId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = wechatMenuService.pushMenu(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
