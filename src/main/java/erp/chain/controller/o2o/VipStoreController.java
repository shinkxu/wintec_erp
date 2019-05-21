package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.domain.App;
import erp.chain.service.o2o.VipStoreService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangms on 2017/2/9.
 */
@Controller
@ResponseBody
@RequestMapping(value={"/vipStore","/vipSettingWS"})
public class VipStoreController {
    @Autowired
    private VipStoreService vipStoreService;
    /**
     * 保存储存规则明细，如果已存在储存则修改并且增加明细，否则增加并且增加明细
     * @author genghui
     */
    @RequestMapping("/saveVipStoreRuleDetail")
    public String saveVipStoreRuleDetail() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tId = StringUtils.trimToNull(params.get("tenantId"));
            String bId = StringUtils.trimToNull(params.get("branchId"));
            Args.notNull(bId, "branchId not null");
            Args.notNull(tId, "tenantId not null");
            Args.isInteger(bId,"branchId应该是整数");
            Args.isInteger(tId,"tenantId应该是整数");
            Map rests=vipStoreService.saveVipStoreRuleDetail(new BigInteger(tId), new BigInteger(bId), params);
            rest.setData(rests);
            rest.setIsSuccess(true);
            rest.setMessage("添加成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage("添加失败");
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 新增储值规则明细
     * @return
     */
    @RequestMapping("/addRuleDetail")
    public String addRuleDetail(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("ruleId") != null){
                result = vipStoreService.addOrModifyVipStoreRuleDetails(params);
            } else {
                result.setSuccess("1");
                result.setMsg("储值规则id错误！");
            }

        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，新增失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 删除储值规则明细
     * @return
     */
    @RequestMapping(value = {"/delRuleDetail","/delVipStoreRuleDetail"})
    public String delRuleDetail(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("ids") != null && !"".equals(params.get("ids"))){
                int num = vipStoreService.delVipStoreRuleDetailPc(params);
                if(num>0){
                    rest.setIsSuccess(true);
                    rest.setMessage("会员储值规则删除成功");
                }
                rest.setData(num);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("ids不能为空");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
       return BeanUtils.toJsonStr(rest);
    }

    /**
     * 批量修改
     * @return
     */
    @RequestMapping(value = {"/saveAll","/saveVipStoreRule"})
    public String saveAll(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            if(params.get("id") != null){
                result = vipStoreService.saveVipStoreRule(params);
            } else {
                result.setSuccess("1");
                result.setMsg("id错误！");
            }

        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，新增失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 修改储值规则详情（不支持批量修改）
     * @return
     */
    @RequestMapping("/updateVipStoreDetail")
    public String updateVipStoreDetail(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("id"), "id必须为整数");
            Args.notNull(params.get("payLimit"), "payLimit不能为空");
            Args.notNull(params.get("presentLimit"), "presentLimit不能为空");
            result = vipStoreService.updateVipStoreDetail(params);
        } catch (Exception e){
            result.setIsSuccess(false);
            result.setSuccess("1");
            result.setMsg("出现异常，新增失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 储值规则分店管理初始化
     * @return
     */
    @RequestMapping("/initVipStoreRuleBranch")
    public String initVipStoreRuleBranch(){
        ApiRest result = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreService.initVipStoreRuleBranch(params);
        } catch (Exception e){
            result.setIsSuccess(false);
            result.setError("出现异常，新增失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 储值对账结算单
     * */
    @RequestMapping("/queryStoreStatements")
    public String queryStoreStatements(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.queryStoreStatements(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过条件查询储值消费单据
     * */
    @RequestMapping("/queryStoreOrder")
    public String queryStoreOrder(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.queryStoreOrder(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增或修改处置对账结算单
     * */
    @RequestMapping("/addOrUpdateStatement")
    public String addOrUpdateStatement(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.addOrUpdateStatement(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据ID查询结算单
     * */
    @RequestMapping("/queryBillById")
    public String queryBillById(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setError("参数id不能为空");
                apiRest.setIsSuccess(false);
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.queryBillById(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据ID查询结算单
     * */
    @RequestMapping("/queryStoreByBillId")
    public String queryStoreByBillId(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("billId") == null || "".equals(params.get("billId"))){
                apiRest.setError("参数billId不能为空");
                apiRest.setIsSuccess(false);
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.queryStoreByBillId(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改结算单状态
     * */
    @RequestMapping("/updateStatus")
    public String updateStatus(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.updateStatus(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除单据
     * */
    @RequestMapping("deleteOrder")
    public String deleteOrder(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreService.deleteOrder(params);
        } catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            return BeanUtils.toJsonStr(apiRest);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
