package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.o2o.VipType;
import erp.chain.service.o2o.VipAddressService;
import erp.chain.service.o2o.VipGradeService;
import erp.chain.service.o2o.VipService;
import erp.chain.service.o2o.VipStoreHistoryService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/18.
 */
@Controller
@ResponseBody
@RequestMapping("/vip")
public class VipController {
    @Autowired
    private VipService vipService;
    @Autowired
    private VipAddressService vipAddressService;
    @Autowired
    private VipGradeService vipGradeService;
    @Autowired
    private VipStoreHistoryService vipStoreHistoryService;

    /**
     * 查询会员列表
     */
    @RequestMapping("/qryVipList")
    public String qryVipList() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(StringUtils.isNotBlank(params.get("tenantId"))){
                result = vipService.qryVipList(params);
            } else {
                result.setSuccess("1");
                result.setMsg("tenantId不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg( "操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 查询会员列表(精准营销)
     */
    @RequestMapping("/qryVipListForMarketing")
    public String qryVipListForMarketing() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(StringUtils.isNotBlank(params.get("tenantId"))){
                result = vipService.qryVipListForMarketing(params);
            } else {
                result.setSuccess("1");
                result.setMsg("tenantId不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg( "操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 查询会员导入数据列表
     */
    @RequestMapping("/qryVipImportList")
    public String qryVipImportList() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank("branchType")){
                result = vipService.qryVipImportList(params);
            } else {
                result.setSuccess("1");
                result.setMsg("tenantId、branchType不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg( "操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /***
     * 查询该商户下已注册手机号
     * */
    @RequestMapping("/queryRegisteredPhone")
    public String queryRegisteredPhone(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.queryRegisteredPhone(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存导入会员数据
     * */
    @RequestMapping("/saveVipsImport")
    public String saveVipsImport(){
        ApiRest apiRest = new ApiRest();
        Map<String, String> params = AppHandler.params();
        try{
            String tenantId = params.get("tenantId");
            String branchId = params.get("branchId");
            if(!StringUtils.isNotBlank(branchId)){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isNotBlank(tenantId)){
                apiRest = vipService.saveVipsImport(params);
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
            }
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询会员导入数据列表
     */
    @RequestMapping("/saveVipImport")
    public String saveVipImport() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isNotBlank(tenantId)){
                rest = vipService.saveVipImport(params);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("tenantId不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("操作失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 删除导入数据
     */
    @RequestMapping("/delVipImportById")
    public String delVipImportById() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isNotBlank(tenantId)){
                result = vipService.delVipImportById(params);
            } else {
                result.setSuccess("1");
                result.setMsg("会员删除失败");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("会员删除失败");
        }
        return BeanUtils.toJsonStr(result);
    }

    @RequestMapping("/confirmImport")
    public String confirmImport() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isNotBlank(tenantId)){
                rest = vipService.confirmImport(params);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("tenantId不能为空。");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("操作失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 删除会员
     */
    @RequestMapping("/delVips")
    public String delVips() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.delVips(params.get("ids"));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 新增会员信息
     */
    @RequestMapping("/addVip")
    public String addVip() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.saveVip(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 保存会员信息
     */
    @RequestMapping("/saveVip")
    public String saveVip() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.saveVip(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 修改会员生日及邮箱
     */
    @RequestMapping("/updateVip")
    public String updateVip() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.updateVip(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 编辑会员
     */
    @RequestMapping("/eidtVip")
    public String eidtVip() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.qryVipById(new BigInteger(params.get("id")), params.get("tenantId"), params.get("branchId"), null);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 验证会员编码是否重复
     */
    @RequestMapping("/uniqueCode")
    public String uniqueCode() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.uniqueCode(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 验证会员手机唯一性
     */
    @RequestMapping("/uniquePhone")
    public String uniquePhone() {
        ResultJSON resultJSON = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                resultJSON.setSuccess("1");
                resultJSON.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(resultJSON);
            }
            resultJSON = vipService.uniquePhone(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            resultJSON.setSuccess("1");
            resultJSON.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(resultJSON);
    }
    /**
     * 获取会员收货地址列表
     */
    @RequestMapping("/qryVipAddr")
    public String qryVipAddr() {
        ResultJSON result =  new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.qryVipAddressList(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 编辑收货地址
     */
    @RequestMapping("/eidtAddr")
    public String eidtAddr() {
        ResultJSON result =  new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("id") != null){
                result = vipAddressService.qryVipAddrById(new BigInteger(params.get("id")));
            } else {
                result.setSuccess("1");
                result.setMsg("id不能为空!");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 添加收货地址
     */
    @RequestMapping("/addAddr")
    public String addAddr() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveVipAddr(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 保存收货地址
     */
    @RequestMapping("/saveAddr")
    public String saveAddr() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveVipAddr(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 删除收货地址
     */
    @RequestMapping("/delAddr")
    public String delAddr() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.delVipAdds(params.get("ids"));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 设置默认收货地址
     */
    @RequestMapping("/saveDefaultAddr")
    public String saveDefaultAddr() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveDefaultAddr(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 会员微信绑定
     */
    @RequestMapping("/bindChat")
    public String bindChat() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.bindChat(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("绑定失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 手机号获取会员信息
     */
    @RequestMapping("/findVipByPhone")
    public String findVipByPhone() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.findVipByPhone(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("查询失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 微信号获取会员信息
     */
    @RequestMapping("/findVipByOpenId")
    public String findVipByOpenId() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.findVipByOpenId(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("查询失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 获取会员等级数据
     */
    @RequestMapping("/queryVipType")
    public List<VipType> queryVipType() {
        List<VipType> vipTypeList = new ArrayList<>();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null){ // && params.get("branchId") != null && params.get("isBranchManagementVip") != null
                if(params.get("isBranchManagementVip") == null){
                    params.put("isBranchManagementVip","false");
                }
                vipTypeList = vipGradeService.queryVipTypeByVip(params);
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
        }
        return vipTypeList;
    }

    /**
     * 删除时 判断被删除的机构与本机构是否一致
     */
    @RequestMapping("/branchIdIsmyBranchId")
    public String branchIdIsmyBranchId() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("branchId") != null){
                result = vipService.qryVipById(new BigInteger(params.get("id")),null,null,null);
                Branch delBranch = (Branch)result.getObject();
                if(delBranch != null){
                    BigInteger delBranchId = delBranch.getId();
                    if (!delBranchId.equals(params.get("branchId"))) {
                        result.setSuccess("false");
                        result.setMsg("不可以修改其他机构的");
                    }
                }
            } else {
                result.setSuccess("false");
                result.setMsg("branchId不能为空");
            }
        }  catch (Exception e) {
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 冲正
     * @return
     */
    @RequestMapping("/correctStoreHistory")
    public String correctStoreHistory() {
        Map<String,Object> result = new HashMap<>();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.put("isSuccess",false);
                result.put("success","1");
                result.put("msg","参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipStoreHistoryService.correctStoreHistory(params);

        } catch (Exception e) {
            result.put("isSuccess",false);
            result.put("success","1");
            result.put("msg","冲正失败！");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 冲正
     * @return
     */
    @RequestMapping("/correctStoreHistoryOnlinePay")
    public String correctStoreHistoryOnlinePay() {
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("type")==null||params.get("type").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipStoreHistoryService.correctStoreHistoryOnlinePay(params);

        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError("冲正失败！");
            apiRest.setMessage("冲正失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 会员充值
     * @return
     */
    @RequestMapping("/addStoreHistory")
    public String addStoreHistory() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipStoreHistoryService.addStoreHistory(params);

        } catch (Exception e) {
            result.setSuccess("1");
            result.setMsg("充值失败！");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 根据充值金额计算赠送金额
     * @return
     */
    @RequestMapping("/linkage")
    public String linkage() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(StringUtils.isBlank(params.get("tenantId"))){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.linkage(params);
        } catch (Exception e) {
            result.setSuccess("false");
            result.setMsg("充值失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 根据商户id查询所有会员的消费历史
     * @return
     */
    @RequestMapping("/listVipTrade")
    public String listVipTrade() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.listVipTrade(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("false");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 根据商户id查询所有会员的储值历史
     * @return
     */
    @RequestMapping("/listVipStore")
    public String listVipStore() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params = AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if (StringUtils.isBlank(tenantId)) {
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipService.listVipStore(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("false");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 判断该账号是否为会员
     */
    @RequestMapping("/isVip")
    public String isVip() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.isVip(params);
        } catch (Exception e) {
            result.setSuccess("false");
            result.setIsSuccess(false);
            result.setMsg(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 获得新增会员的编码
     */
    @RequestMapping("/getNextVipCode")
    public String getNextVipCode() {
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"tenantId不能必须为整数");
            result = vipService.getNextVipCode(params);
        } catch (Exception e) {
            result.setIsSuccess(false);
            result.setMsg(e.getMessage());
            result.setSuccess("false");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 增加、扣减积分
     */
    @RequestMapping("/addVipScore")
    public String addVipScore() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.addVipScore(params);
        } catch (Exception e) {
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
            rest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 会员卡退卡
     */
    @RequestMapping("/refundCard")
    public String refundCard() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.refundCard(params);
        } catch (Exception e) {
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 会员储值退款
     */
    @RequestMapping("/refundVipStore")
    public String refundVipStore() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.refundVipStore(params);
        } catch (Exception e) {
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 新增、修改会员特殊活动
     */
    @RequestMapping("/addOrUpdateSpecialPromotion")
    public String addOrUpdateSpecialPromotion() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"),"tenantId不能为空");
            Args.notNull(params.get("branchId"), "branchId不能为空");
            Args.notNull(params.get("promotionType"), "promotionType不能为空");
            rest = vipService.addOrUpdateSpecialPromotion(params);
        } catch (Exception e) {
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询会员特殊活动
     */
    @RequestMapping("/qrySpecialPromotion")
    public String qrySpecialPromotion() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.notNull(params.get("tenantId"),"tenantId不能为空");
            Args.notNull(params.get("promotionType"), "promotionType不能为空");
            rest = vipService.qrySpecialPromotion(params);
        } catch (Exception e) {
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 会员自动升级（定时任务）
     * @return
     */
    @RequestMapping("/autoUpgrade")
    public String autoUpgrade(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.autoUpgrade(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("清空积分（定时任务）!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询会员标签组
     * */
    @RequestMapping("/queryLabelGroup")
    public String queryLabelGroup(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            apiRest = vipService.queryLabelGroup(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增标签组
     * */
    @RequestMapping("/addOrUpdateLabelGroup")
    public String addOrUpdateLabelGroup(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            apiRest = vipService.addOrUpdateLabelGroup(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除标签组
     * */
    @RequestMapping("/delLabelGroup")
    public String delLabelGroup(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.delLabelGroup(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询会员标签列表
     * */
    @RequestMapping("/queryLabelList")
    public String queryLabelList(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            apiRest = vipService.queryLabelList(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询会员标签表
     * */
    @RequestMapping("/queryLabelGroupTable")
    public String queryLabelGroupTable(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            apiRest = vipService.queryLabelGroupTable(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增标签
     * */
    @RequestMapping("/addLabel")
    public String addLabel(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            if(params.get("groupId") == null || "".equals(params.get("groupId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.addLabel(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取标签Code
     * */
    @RequestMapping("/getLabelCode")
    public String getLabelCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            if(params.get("groupId") == null || "".equals(params.get("groupId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数groupId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.getLabelCode(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除标签
     * */
    @RequestMapping("/delLabel")
    public String delLabel(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.delLabel(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 生成标签组树图
     * */
    @RequestMapping("/loadLabelGroupTree")
    public String loadLabelGroupTree(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
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
            apiRest = vipService.loadLabelGroupTree(params);
        }catch (Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /************************************米雅会员对接begin**********************************************/
    /**
     * 根据手机号查询会员信息
     * @return
     */
    @RequestMapping(value = "/miyaVipFind",method = RequestMethod.POST)
    public String miyaVipFind(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("mobile"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mobile不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.miyaVipFind(params);
        }catch (Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 新增会员信息
     * @return
     */
    @RequestMapping(value = "/miyaAddVip",method = RequestMethod.POST)
    public String miyaAddVip(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("mobile"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mobile不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("vipName"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数vipName不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("sex"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mobile不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("regSource"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数regSource不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.miyaAddVip(params);
        }catch (Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据手机号修改会员信息
     * @return
     */
    @RequestMapping(value = "/miyaUpdateVip",method = RequestMethod.POST)
    public String miyaUpdateVip(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try{
            if(StringUtils.isEmpty(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(StringUtils.isEmpty(params.get("mobile"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数mobile不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.miyaUpdateVip(params);
        }catch (Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /************************************米雅会员对接end************************************************/

    @RequestMapping("/vipStatement")
    public String vipStatement(){
        ApiRest apiRest = vipService.vipStatement();
        return BeanUtils.toJsonStr(apiRest);
    }
}
