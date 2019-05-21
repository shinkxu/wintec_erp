package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.domain.o2o.VipType;
import erp.chain.service.o2o.VipGradeService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/12.
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/vipGrade","/vipSettingWS"})
public class VipGradeController {
    @Autowired
    private VipGradeService vipGradeService;
    /**
     * 会员类型的增加或修改
     * @params tenantId not null
     * @return 保存或修改成功之后会员类型
     * @author szq
     */
    @RequestMapping("/addOrModifyVipType")
    public String addOrModifyVipType() {
        Map<String,String> params= AppHandler.params();
        saas.api.common.ApiRest rest = new saas.api.common.ApiRest();
        String tenantId = StringUtils.trimToNull(params.get("tenantId"));
        String discountRate = StringUtils.trimToNull(params.get("discountRate"));
        try {
            Args.isInteger(tenantId, "tenantId应该为整数");
            if (params.get("id") != null) {
                //修改会员类型
                Args.isInteger(params.get("id"), "Id应该为整数");
                VipType vipType = new VipType(params);
                VipType type =vipGradeService.updateVipTypeIgnoreOnlineDefault(vipType);
                rest.setIsSuccess(true);
                rest.setData(type);
                rest.setMessage("修改会员类型成功");
            } else {
                //增加会员类型
                VipType vipType = new VipType(params);
                VipType type = vipGradeService.saveOfflineVipType(vipType);
                rest.setIsSuccess(true);
                rest.setData(type);
                rest.setMessage("增加会员类型成功");
            }
        } catch (Exception e) {
            rest.setIsSuccess(false);
            LogUtil.logError(e, params);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 修改商户下的会员类型积分
     */
    @RequestMapping("/updateIntegralRule")
    public String updateIntegralRule() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        String tenantId = StringUtils.trimToNull(params.get("tenantId"));
        String pointsFactor = StringUtils.trimToNull(params.get("pointsFactor"));
        String scoreUsage = StringUtils.trimToNull(params.get("scoreUsage"));
        try {
            Args.isInteger(tenantId, "tenantId应该为整数");
            Args.notNull(pointsFactor, "pointsFactor不能为空");
            Args.isInteger(scoreUsage, "scoreUsage应该为整数");
            vipGradeService.updateIntegralRule(new BigInteger(tenantId), new BigInteger(scoreUsage), new BigDecimal(pointsFactor));
            rest.setIsSuccess(true);
            rest.setData(vipGradeService.queryIntegralRule(new BigInteger(tenantId)));
            rest.setMessage("修改会员积分成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 获取商户积分规则
     * @params tenantId not null
     * @return '{"pointsFactor":"积分折算系数-多少元等于1积分","scoreUsage":"设置X积分等于1元，可用于前台消费"}'
     * @author szq
     */
    @RequestMapping("/queryIntegralRule")
    public String queryIntegralRule() {
        saas.api.common.ApiRest rest = new saas.api.common.ApiRest();
        Map<String,String> params= AppHandler.params();
        String tenantId = StringUtils.trimToNull(params.get("tenantId"));
        try {
            Args.isInteger(tenantId, "tenantId应该为整数");
            Map map = vipGradeService.queryIntegralRuleBranch(params);
            rest.setData(map);
            rest.setIsSuccess(true);
            rest.setMessage("查询商户积分规则成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询会员类型
     * @params tenantId not null
     * @return List < VipType >,size>=1
     * @author szq
     */
    @RequestMapping("/queryVipTypeList")
    public String queryVipTypeList() {
        Map<String,String> params= AppHandler.params();
        saas.api.common.ApiRest rest=new saas.api.common.ApiRest();
        try {
            rest = vipGradeService.queryVipTypeList(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询会员等级列表
     */
    @RequestMapping("/qryVipGradeList")
    public String qryVipGradeList(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipGradeService.qryVipGradeList(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 查询会员自动升级类型
     */
    @RequestMapping("/qryUpgradeType")
    public String qryUpgradeType(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                rest.setIsSuccess(false);
                rest.setMessage("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(rest);
            }
            rest = vipGradeService.qryUpgradeType(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询失败！");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 保存会员等级信息
     */
    @RequestMapping("/saveVipType")
    public String saveVipType(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipGradeService.saveVipType(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 保存所有会员等级信息
     */
    @RequestMapping("/saveAll")
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
            result = vipGradeService.saveVipTypes(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 修改会员积分规则
     */
    @RequestMapping("/updateVipScoreRule")
    public String updateVipScoreRule(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipGradeService.updateVipScoreRule(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 编辑会员等级
     */
    @RequestMapping("/eidtVipType")
    public String eidtVipType(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipGradeService.qryVipTypeById(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 删除会员类型
     */
    @RequestMapping(value={"/delVipsType","/delVipType"})
    public String delVipsType(){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        Map<String,String> params= AppHandler.params();
        try {
            String ids = StringUtils.trimToNull(params.get("ids"));
            String[] idList = ids.split(",");
            String tenantId = params.get("tenantId");
            rest = vipGradeService.delVipTypes(idList,tenantId);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 删除未保存的会员类型
     */
    @RequestMapping("/delNewType")
    public String delNewType(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipGradeService.delNewType(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 验证会员等级编码是否重复
     */
    @RequestMapping("/uniqueTypeCode")
    public String uniqueTypeCode(){
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        String tenantId = params.get("tenantId");
        try {
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(resultJSON);
            }
            resultJSON = vipGradeService.uniqueTypeCode(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(resultJSON);
    }
    @RequestMapping("/findVipTypeByPhone")
    public String findVipTypeByPhone(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("phone") != null && params.get("tenantId") != null){
                rest.setData(vipGradeService.findVipTypeByPhone(params));
                rest.setIsSuccess(true);
                rest.setMessage("会员类型查询成功");
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("电话不能为空");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 设置固定折扣
     * */
    @RequestMapping("/setOrUpdateBranchDiscount")
    public String setOrUpdateBranchDiscount(){
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
            if(params.get("discount") == null || "".equals(params.get("discount"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数discount不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipGradeService.setOrUpdateBranchDiscount(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("/queryDiscountById")
    public String queryDiscountById(){
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
            if(params.get("typeId") == null || "".equals(params.get("typeId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数typeId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipGradeService.queryDiscountById(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
