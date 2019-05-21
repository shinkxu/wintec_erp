package erp.chain.controller.setting;

import com.saas.common.util.LogUtil;
import erp.chain.service.setting.BranchTableService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 桌台
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/2
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/branchTable","/settingWebService"})
public class BranchTableController{
    @Autowired
    private BranchTableService branchTableService;

    /**
     * 查询桌台区域列表
     * @return
     */
    @RequestMapping("/queryAreaList")
    public String queryAreaList(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.queryAreaList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改桌台区域
     * @return
     */
    @RequestMapping("/addOrUpdateArea")
    public String addOrUpdateArea(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name")==null||params.get("name").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("name").length()>20) {
                apiRest.setIsSuccess(false);
                apiRest.setError("区域名称不能超过20个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.addOrUpdateArea(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取区域编码
     * @return
     */
    @RequestMapping("/getAreaCode")
    public String getAreaCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.getAreaCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除桌台区域
     * @return
     */
    @RequestMapping(value = {"/delArea","/delBranchArea"})
    public String delArea(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.delArea(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询桌台列表
     * @return
     */
    @RequestMapping("/queryTableList")
    public String queryTableList(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.queryTableList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增或修改桌台
     * @return
     */
    @RequestMapping("/addOrUpdateTable")
    public String addOrUpdateTable(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
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
            if(params.get("name").length()>5){
                apiRest.setIsSuccess(false);
                apiRest.setError("名字的长度不能超过5个字符！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("num")==null||params.get("num").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数num不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("code")==null||params.get("code").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数code不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[1-9][0-9]*");
            java.util.regex.Matcher match=pattern.matcher(params.get("num"));
            if(!((match.matches()&&Integer.valueOf(params.get("num"))<21&&Integer.valueOf(params.get("num"))>0))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数num必须为1-20之间的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("status")==null||params.get("status").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数status不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            java.util.regex.Pattern pattern2=java.util.regex.Pattern.compile("[0-9]*");
            java.util.regex.Matcher match2=pattern2.matcher(params.get("status"));
            if(!((match2.matches()&&(params.get("status").equals("0")||params.get("status").equals("1"))))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数status必须为0或1的整数！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                if (params.get("areaId")==null||params.get("areaId").equals("")) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增桌台时，参数areaId不能为空！！");
                    return BeanUtils.toJsonStr(apiRest);
                }
            }
            apiRest = branchTableService.addOrUpdateTable(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 获取桌台编码
     * @return
     */
    @RequestMapping("/getTableCode")
    public String getTableCode(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.getTableCode(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除桌台区域
     * @return
     */
    @RequestMapping("/delTable")
    public String delTable(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("branchId")==null||params.get("branchId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = branchTableService.delTable(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
