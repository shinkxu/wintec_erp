package erp.chain.controller.o2o;

import com.saas.common.Constants;
import com.saas.common.ResultJSON;
import com.saas.common.util.Common;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.domain.o2o.Vip;
import erp.chain.service.BranchService;
import erp.chain.service.o2o.*;
import erp.chain.utils.AppHandler;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/21.
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/vipInterface","vipSettingWS"})
public class VipInterfaceController {
    @Autowired
    private VipService vipService;
    @Autowired
    private VipAddressService vipAddressService;
    @Autowired
    private VipStoreHistoryService vipStoreHistoryService;
    @Autowired
    private VipStoreService vipStoreService;
    @Autowired
    private VipBookService vipBookService;
//    DietPromotionRedPaperService dietPromotionRedPaperService
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private DietOrderInfoService dietOrderInfoService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private CardCouponsService cardCouponsService;
//    WechatinfoService wechatinfoService
//
    /**
     * 查询会员列表
     */
    @RequestMapping("/qryVipList")
    public String qryVipList(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.qryVipListNoPage(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result,List.class));
    }
    /**
     * 删除会员
     */
    @RequestMapping("/delVips")
    public String delVips(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.delVips(params.get("ids"));
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 新增会员信息
     */
    @RequestMapping("/addVip")
    public String addVip(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.saveVip(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 保存会员信息
     */
    @RequestMapping("/saveVip")
    public String saveVip(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.saveVip(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 查询会员
     */
    @RequestMapping("/qryVipById")
    public String qryVipById(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("id") != null && !"".equals(params.get("id")) && params.get("tenantId") != null && !"".equals(params.get("tenantId"))){
                result = vipService.qryVipById(new BigInteger(params.get("id")), params.get("tenantId"), params.get("branchId"),"0");
            } else {
                result.setSuccess("1");
                result.setMsg("会员id,tenantId不能为空!");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 获取会员收货地址列表
     */
    @RequestMapping("/qryVipAddr")
    public String qryVipAddr(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.qryVipAddressListNoPage(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, List.class));
    }
    /**
     * 编辑收货地址
     */
    @RequestMapping("/qryAddrById")
    public String qryAddrById(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.qryVipAddrById(new BigInteger(params.get("id")));
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 添加收货地址
     */
    @RequestMapping("/addAddr")
    public String addAddr(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveVipAddr(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 保存收货地址
     */
    @RequestMapping("/saveAddr")
    public String saveAddr(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveVipAddr(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 删除收货地址
     */
    @RequestMapping("/delAddr")
    public String delAddr(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.delVipAdds(params.get("ids"));
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 设置默认收货地址
     */
    @RequestMapping("/saveDefaultAddr")
    public String saveDefaultAddr(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipAddressService.saveDefaultAddr(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 会员微信绑定
     */
    @RequestMapping("/bindChat")
    public String bindChat(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.bindChat(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 手机号获取会员信息
     */
    @RequestMapping("/findVipByPhone")
    public String findVipByPhone(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.findVipByPhone(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 微信号获取会员信息
     */
    @RequestMapping("/findVipByOpenId")
    public String findVipByOpenId(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.findVipByOpenId(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * pos用会员储值
     * @return
     */
    @RequestMapping("/vipStore")
    public String vipStore(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                rest.setIsSuccess(false);
                rest.setMessage("参数tenantId不能为空！");
            }
            rest = vipStoreHistoryService.vipStoreForPos(params);
        } catch (Exception e){
            rest.setIsSuccess(false);
            rest.setMessage("出现异常，储值失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * pos用会员储值（加入token，防止重复提交）
     * @return
     */
    @RequestMapping("/vipStoreV2")
    public String vipStoreV2(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(parseRest(result, null));
            }
            result = vipStoreHistoryService.vipStoreForPosV2(params);
        } catch (Exception e){
            LogUtil.logError("vipStoreV2-"+e.getMessage());
            result.setSuccess("1");
            result.setMsg("出现异常，储值失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * pos储值消费退款
     * @return
     */
    @RequestMapping("/vipStoreCorrectForPos")
    public String vipStoreCorrectForPos(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipStoreHistoryService.vipStoreCorrectForPos(params);
        } catch (Exception e){
            rest.setMessage("出现异常，充值失败。");
            rest.setIsSuccess(false);
            LogUtil.logError("pos储值消费退款异常!" + e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询储值规则接口
     * @return
     */
    @RequestMapping("/qryStoreRuleWeb")
    public String qryStoreRuleWeb(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreService.qryStoreRuleDetailsByTenantCode(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 查询储值规则接口
     * @return
     */
    @RequestMapping("/qryVipStoreRule")
    public String qryVipStoreRule(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreService.qryVipStoreRule(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 获取指定商户和机构的储值规则
     */
    @RequestMapping("/queryVipStoreRule")
    public String queryVipStoreRule() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tId = StringUtils.trimToNull(params.get("tenantId"));
            String bId = StringUtils.trimToNull(params.get("branchId"));
            Args.notNull(bId, "branchId not null");
            Args.notNull(tId, "tenantId not null");
            Args.isInteger(bId,"branchId应该是整数");
            Args.isInteger(tId,"tenantId应该是整数");
            Map res = vipStoreService.queryVipStoreRule(new BigInteger(tId), new BigInteger(bId));
            rest.setData(res);
            rest.setIsSuccess(true);
            rest.setMessage("查询成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("查询失败");
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询储值规则接口
     * @return
     */
    @RequestMapping("/qryStoreRule")
    public String qryStoreRule(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipStoreService.qryStoreRuleDetailsByTenantCode1(params);
        } catch (Exception e){
            rest.setIsSuccess(false);
            rest.setMessage("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 根据tenantId查询储值规则接口
     * @return
     * @author szq
     */
    @RequestMapping("/qryStoreRuleByTenantId")
    public String qryStoreRuleByTenantId(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreService.qryStoreRuleByTenantId(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 会员流水上传（pos用）
     */
    @RequestMapping("/uploadVipGlide")
    public String uploadVipGlide(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipBookService.uploadVipGlide(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，上传失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 会员流水上传（pos用V2）
     */
    @RequestMapping("/uploadVipGlideV2")
    public String uploadVipGlideV2(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipBookService.uploadVipGlideV2(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，上传失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 会员流水上传（微餐厅用）
     */
    @RequestMapping("/uploadVipGlideForCt")
    public String uploadVipGlideForCt(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipBookService.uploadVipGlideForCt(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，上传失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }

    /**
     * pos上传结果查询
     */
    @RequestMapping("/queryUploadResult")
    public String queryUploadResult(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipStoreHistoryService.queryUploadResult(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }
    /**
     * 微信用查询会员等级以获取积分规则
     * @param
     */
    @RequestMapping("/queryVipType")
    public String queryVipType(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipService.queryVipType(params);
        } catch (Exception e){
            result.setSuccess("1");
            result.setMsg("出现异常，查询失败!");
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(parseRest(result, null));
    }


    private ApiRest parseRest(ResultJSON resultJSON,Class clasz){
        ApiRest rest = new ApiRest();
        rest.setClazz(clasz);
        rest.setMessage(resultJSON.getMsg());
        if(resultJSON.getSuccess().equals("0")){
            rest.setResult(Constants.REST_RESULT_SUCCESS);
            rest.setData(resultJSON.getObject());
        }else{
            rest.setResult(Constants.REST_RESULT_FAILURE);
            rest.setError(resultJSON.getMsg());
        }
        return rest;
    }

    /**
     * 用于微餐厅注册会员
     * 查询当前商户 线上注册默认会员类型
     * @return
     */
    @RequestMapping("/qryDefaultVipType")
    public String qryDefaultVipType(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
            }
            result = vipService.qryDefaultVipType(params);
        } catch (Exception e){
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 验证同一个商户下会员电话是否重复
     */
    @RequestMapping("/qryUniquePhone")
    public String qryUniquePhone(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                rest.setIsSuccess(false);
                rest.setMessage("参数tenantId不能为空！");
            }
            ResultJSON result = vipService.qryUniquePhone(params);
            rest.setIsSuccess(result.getIsSuccess());
            rest.setMessage(result.getMsg());
            rest.setData(result.getObject());
        } catch (Exception e){
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 自助餐厅撤销取餐后将订单状态由已完成更新为未完成
     * @author szq
     */
    @RequestMapping("/changeOrderStatus")
    public String changeOrderStatus(){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        Map<String,String> params= AppHandler.params();
        try{
            String dietOrderInfoId = params.get("dietOrderInfoId");
            String orderStatus = params.get("orderStatus");
            DietOrderInfo data = dietOrderInfoService.changeOrderStatus(new BigInteger(dietOrderInfoId),Integer.parseInt(orderStatus));
            if(data != null){
                rest.setData(data);
                rest.setMessage("订单状态更改成功");
                rest.setIsSuccess(true);
            }
        }catch(Exception e){
            LogUtil.logError(e,params);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 根据消费金额计算赠送的积分
     * @author szq
     * @params not null 参数：
     * 1.vipId  应该为整数
     * 2.moneytary 消费金额 not null
     * @return data= vip
     *
     */
    @RequestMapping("/calculateScores")
    public String calculateScores(){
        ApiRest rest = new ApiRest();
        rest.setIsSuccess(false);
        Map<String,String> params= AppHandler.params();
        try{
            String vipId = params.get("vipId");
            String moneytary = params.get("moneytary");
            String paymentCode = params.get("orderCode");

            Vip vip = vipService.calculateScores(new BigInteger(vipId),new BigDecimal(moneytary),paymentCode);
            rest.setData(vip);
            rest.setMessage("积分更改成功");
            rest.setIsSuccess(true);
        }catch(Exception e){
            LogUtil.logError(e,params);
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 获取会员类型
     */
    @RequestMapping("/findVipType")
    public String findVipType(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                rest.setIsSuccess(false);
                rest.setMessage("参数tenantId不能为空！");
            }
            ResultJSON result = vipService.findVipType(params);
            if(result != null){
                rest.setIsSuccess(result.getIsSuccess());
                rest.setMessage(result.getMsg());
                rest.setData(result.getObject());
            }
        } catch (Exception e){
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
            rest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取机构促销活动摘要
     * @params
     * 1.tenantId 商户id not null
     * 2.branchId 机构id not null
     * @author szq
     * @return map
     */
    @RequestMapping("/findDietPromotionAbstract")
    public String findDietPromotionAbstract(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null && params.get("branchId") != null){
                Map<String,String> map = dietPromotionService.qryDietPromotionAbstract(params);
                rest.setIsSuccess(true);
                rest.setMessage("成功获取机构促销活动摘要");
                rest.setData(map);
            }
        } catch (Exception e){
            LogUtil.logError(e,params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 微餐厅认证会员信息
     * @params
     * 1.id
     * 2.vipName  姓名
     * 3.sex   性别
     * 4.birthday 生日
     * @author szq
     * @return
     */
    @RequestMapping("/confirmVipInfo")
    public String confirmVipInfo () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.confirmVipInfo(params);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("出现异常，查询失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 根据会员编码或电话查询会员信息
     * @params
     * 1.code code传入会员编码 或code传入电话号码
     * 2.tenantId 商户id
     * @author szq
     */
    @RequestMapping("/findVipByCondition")
    public String findVipByCondition () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("code") != null && params.get("tenantId") != null){
                rest.setData(vipService.findVipByCondition(params));
                rest.setIsSuccess(true);
                rest.setMessage("会员信息查询成功");
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("code、tenantId不能为空！");
            }
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 根据会员编码或电话或卡号查询会员信息（pos查询会员流程简化接口）
     * 手机号或编码优先查询，卡号优先级靠后
     *
     * @author ac
     */
    @RequestMapping("/findVipForPos")
    public String findVipForPos () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("code") != null && params.get("tenantId") != null){
                rest.setData(vipService.findVipForPos(params));
                rest.setIsSuccess(true);
                rest.setMessage("会员信息查询成功");
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("code、tenantId不能为空！");
            }
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 新增会员接口（pos新增会员流程简化接口）
     * 有卡开卡和创建会员一起处理
     * 无卡只增加会员
     *
     * @author ac
     */
    @RequestMapping("/saveVipForPos")
    public String saveVipForPos () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.saveVipForPos(params);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 储值支付pos取消账单时发起退款处理(for POS)
     *
     * @author ac
     */
    @RequestMapping("/cancelVipTrade")
    public String cancelVipTrade () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.cancelVipTrade(params);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 微餐厅查询机构列表
     * @return
     */
    @RequestMapping("/listBranchForCt")
    public String listBranchForCt(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null){
                rest = branchService.findBranchForCt(params);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("查询失败，商户号为空！");
            }
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage("出现异常，查询失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询正在生效的卡券活动列表
     * @author szq
     * @params
     * 1.tenantId 商户id not null
     * 2.branchId 机构id 可以为空
     * @return data:List<DietPromotionFestivalVo>
     */
    @RequestMapping("/listCardPromotions")
    public String listCardPromotions () {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
//            rest.setData(cardCouponsService.listCardPromotions(params));
            rest.setIsSuccess(true);
            rest.setMessage("卡券活动列表查询成功");
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 微餐厅注册默认会员接口
     */
    @RequestMapping("/registerDefaultVipForCt")
    public String registerDefaultVipForCt(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.registerDefaultVipForCt(params);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询订单的促销明细
     */
    @RequestMapping("/qryOrderPromotion")
    public String qryOrderPromotion(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = dietPromotionService.qryOrderPromotion(params);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询消费记录（消费、储值）
     */
    @RequestMapping("/qryConsumeHistory")
    public String qryConsumeHistory(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipStoreHistoryService.qryConsumeHistory(params);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 上传交易记录(微餐厅)
     */
    @RequestMapping("/uploadTradeHistory")
    public String uploadTradeHistory(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipStoreHistoryService.uploadTradeHistory(params);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 处理会员积分(job)
     */
    @RequestMapping("/dealVipScore")
    public String dealVipScore(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.dealVipScore();
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 处理会员积分(job)
     */
    @RequestMapping("/dealVipScoreError")
    public String dealVipScoreError(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.dealVipScoreError();
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 会员认证(与pos实体卡)合并会员信息
     * @return
     */
    @RequestMapping("/vipCombine")
    public String vipCombine(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.vipCombine(params);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 根据tenantId、会员类型查询积分规则
     * @params
     * 1.tenantId
     * 2.vipTypeId
     * @author szq
     * @return ScoreRuleVo
     */
    @RequestMapping("/qryVipScoreRule")
    public String qryVipScoreRule(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.isInteger(params.get("vipTypeId"),"会员类型id必须为整数");
            BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
            BigInteger vipTypeId=BigInteger.valueOf(Long.valueOf(params.get("vipTypeId")));
            rest = vipService.qryVipScoreRule(tenantId, vipTypeId);
        } catch (Exception e ) {
            LogUtil.logError(e,params);
            rest.setIsSuccess(false);
            rest.setError(e.getMessage());
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 获取微信二维码
     */
    @RequestMapping("/getWechatQrcode")
    public String getWechatQrcode(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Map pMap = new HashMap();
            pMap.put("tenantId", params.get("tenantId"));
            //sceneId不传，则默认为1
            pMap.put("sceneId",params.get("sceneId"));
            rest = ProxyApi.proxyPost("out", "wechat", "createQrCode", pMap);
        } catch (Exception e ) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取机构二维码
     * @author szq
     * @return
     */
    @RequestMapping("/getBranchTwoCode")
    public String getBranchTwoCode(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.isInteger(params.get("branchId"), "商户id必须为整数");
            params.put("url",Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT) + "/frontTenantLookMenu/index?tenantId=" + params.get("tenantId") + "&branchId=" + params.get("branchId")+ "&isBuffet=" + params.get("isBuffet") + "&isInvite=" + params.get("isInvite"));
//            rest = wechatinfoService.getBranchTwoCode(params);
        } catch (Exception e) {
            LogUtil.logError("获取二维码异常：" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage("获取二维码异常");
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 修改储存规则明细，修改储存规则并且修改明细
     * @author genghui
     */
    @RequestMapping("/updateVipStoreRuleDetail")
    public String updateVipStoreRuleDetail() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String tId = StringUtils.trimToNull(params.get("tenantId"));
            String bId = StringUtils.trimToNull(params.get("branchId"));
            Args.notNull(bId, "branchId not null");
            Args.notNull(tId, "tenantId not null");
            Args.isInteger(bId,"branchId应该是整数");
            Args.isInteger(tId,"tenantId应该是整数");
            Map result=vipStoreService.updateVipStoreRuleDetail(new BigInteger(tId), new BigInteger(bId), params);
            rest.setData(result);
            rest.setIsSuccess(true);
            rest.setMessage("修改成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("修改失败");
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 查询会员统计数据
     * @return
     */
    @RequestMapping("/qryVipStatistics")
    public String qryVipStatistics(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("tenantId") != null){
                rest = vipService.qryVipStatistics(params);
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("查询失败，商户号为空！");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setIsSuccess(false);
            rest.setMessage("出现异常，查询失败!");
        }
        return BeanUtils.toJsonStr(rest);
    }


    /**
     * 查询消费历史
     * @params
     * 1.phoneOrCode  可以为空
     * 2.page 页码 not null、
     * 3.rows 每页显示的数据条数 not null
     * 4.startTime 开始时间  可以为空
     * 5.endTime   结束时间  可以为空
     * 6.branchId  机构  可以为空
     * @author szq
     * @return data（map("total", total;"rows", list）
     */
    @RequestMapping("/findVipTradeHistory")
    public String findVipTradeHistory() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"), "商户id必须为整数");
            Args.notNull(params.get("page"),"页码不能为空");
            Args.notNull(params.get("rows"), "rows不能为空");
            if(params.get("phoneOrCode") != null) {
                params.put("queryStr",params.get("phoneOrCode"));
            } else {
                params.put("queryStr","");
            }
            if (params.get("startTime") != null) {
                params.put("startDate",params.get("startTime"));
            }
            if (params.get("endTime") != null) {
                params.put("endDate",params.get("endTime"));
            }
            rest.setData(vipService.listVipTrade(params));
            rest.setMessage("会员消费历史查询成功");
            rest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 查询储值历史
     * @params
     * 1.phoneOrCode  可以为空
     * 2.page 页码 not null、
     * 3.rows 每页显示的数据条数 not null
     * 4.startTime 开始时间  可以为空
     * 5.endTime   结束时间  可以为空
     * 6.branchId  机构  可以为空
     * @author szq
     * @return data（map("total", total;"rows", list）
     */
    @RequestMapping("/findVipStoreHistory")
    public String findVipStoreHistory() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            Args.isInteger(params.get("tenantId"),"商户id必须为整数");
            Args.notNull(params.get("page"),"页码不能为空");
            Args.notNull(params.get("rows"),"rows不能为空");
            if(params.get("phoneOrCode") != null) {
                params.put("queryStr",params.get("phoneOrCode"));
            } else {
                params.put("queryStr","");
            }
            if (params.get("startTime") != null) {
                params.put("startDate",params.get("startTime"));
            }
            if (params.get("endTime") != null) {
                params.put("endDate",params.get("endTime"));
            }
            rest.setData(vipService.listVipStore(params));
            rest.setMessage("会员储值历史查询成功");
            rest.setIsSuccess(true);
        } catch (Exception e) {
            LogUtil.logError(e,params);
            rest.setMessage(e.getMessage());
            rest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(rest);
    }
    /**
     * 开启或关闭储值规则明细
     */
    @RequestMapping("/startOrCloseVipStoreRule")
    public String startOrCloseVipStoreRule() {
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            String isOff = StringUtils.trimToNull(params.get("isOff"));
            Args.isTrue(isOff != null &&
                    (isOff.equals("true")  || isOff.equals("false")), "isOff 必须是true or false");
            String tId = StringUtils.trimToNull(params.get("tenantId"));
            String bId = StringUtils.trimToNull(params.get("branchId"));
            Args.notNull(bId, "branchId not null");
            Args.notNull(tId, "tenantId not null");
            Args.isInteger(bId, "branchId应该是整数");
            Args.isInteger(tId,"tenantId应该是整数");
            boolean offOrNot = Boolean.parseBoolean(isOff);
            vipStoreService.startOrCloseVipStoreRule(new BigInteger(tId), new BigInteger(bId), offOrNot);
            rest.setIsSuccess(true);
            rest.setMessage("操作成功");
        } catch (Exception e) {
            LogUtil.logError(e, params);
            rest.setMessage("操作失败");
            rest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 新增pos储值订单
     */
    @RequestMapping("/addPosStoreOrder")
    public String addPosStoreOrder(){
        ApiRest rest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            rest = vipService.addPosStoreOrder(params);
        } catch (Exception e) {
            LogUtil.logError("新增pos储值订单：" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage("新增pos储值订单异常");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取美团机构映射url
     * @return
     */
    @RequestMapping("/getMeituanBindUrl")
    public String getMeituanBindUrl(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            if(params.get("meituanBusiness") != null && params.get("tenantId") != null && params.get("branchId") != null){
                String mapUrl = "https://open-erp.meituan.com/storemap?developerId=" + PropertyUtils.getDefault("meituanDeveloperId");
                mapUrl += "&businessId=" + params.get("meituanBusiness");
                mapUrl += "&ePoiId=" + params.get("tenantId") + "Z" + params.get("branchId");
                mapUrl += "&signKey=" + PropertyUtils.getDefault("meituanSignKey") + "&eName=";
                if(params.get("branchName") != null){
                    mapUrl += params.get("branchName");
                }
                rest.setIsSuccess(true);
                rest.setData(mapUrl);
                rest.setMessage("查询url成功!");
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("business、branchId、tenantId不能为空。");
            }

        } catch (Exception e) {
            LogUtil.logError("获取二维码异常：" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage("获取二维码异常");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取美团机构解绑url
     * @return
     */
    @RequestMapping("/getMeituanUnBindUrl")
    public String getMeituanUnBindUrl(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            if(params.get("meituanBusiness") != null && params.get("appAuthToken") != null){
                String mapUrl = "https://open-erp.meituan.com/releasebinding?signKey=" + PropertyUtils.getDefault("meituanSignKey");
                mapUrl += "&businessId=" + params.get("meituanBusiness");
                mapUrl += "&appAuthToken=" + params.get("appAuthToken");
                rest.setIsSuccess(true);
                rest.setData(mapUrl);
                rest.setMessage("查询url成功!");
            } else {
                rest.setIsSuccess(false);
                rest.setMessage("business、appAuthToken不能为空。");
            }

        } catch (Exception e) {
            LogUtil.logError("获取二维码异常：" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage("获取二维码异常");
        }
        return BeanUtils.toJsonStr(rest);
    }

    /**
     * 获取美团机构解绑url
     * @return
     */
    @RequestMapping("/addWechatVip")
    public String addWechatVip(){
        ApiRest rest = new ApiRest();
        Map<String,String> params = AppHandler.params();
        try {
            rest=vipService.addWechatVip(params);
        } catch (Exception e) {
            LogUtil.logError("获取二维码异常：" + e.getMessage());
            rest.setIsSuccess(false);
            rest.setMessage("获取二维码异常");
        }
        return BeanUtils.toJsonStr(rest);
    }

    @RequestMapping(value = "/registerVip")
    public String registerVip() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = vipService.registerVip(requestParameters);
        } catch (Exception e) {
            LogUtil.logError(String.format(erp.chain.common.Constants.LOGGER_ERROR_FORMAT, "保存会员信息失败", "VipController", "registerVip", e.getClass().getSimpleName(), e.getMessage(), requestParameters));
            apiRest = new saas.api.common.ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setIsSuccess(false);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * POS查询标签组和标签
     * */
    @RequestMapping("/qryLabelAndGroup")
    public String qryLabelAndGroup(){
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
            apiRest = vipService.qryLabelAndGroup(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError("查询标签失败");
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * POS新增标签
     * */
    @RequestMapping("/addLabelFromPos")
    public String addLabelFromPos(){
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
            apiRest = vipService.addLabel(params);
        }catch (Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError("新增标签失败");
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取标签编码
     * */
    @RequestMapping("/qryLabelCode")
    public String qryLabelCode(){
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
     * POS修改会员标签
     * */
    @RequestMapping("/updateVipLabel")
    public String updateVipLabel(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null && "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null && "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("id") == null && "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
            }
            apiRest = vipService.updateVipLabel(params);
        }catch(Exception e){
            LogUtil.logError(e, params);
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }


    /*数据大屏用接口--start*/
    /**
     * 查询会员列表
     */
    @RequestMapping("/qryVipListForBS")
    public String qryVipListForBS(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(parseRest(result,List.class));
            }
            result = vipService.qryVipListForBS(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(parseRest(result,List.class));
    }

    @RequestMapping("qryVipStoreForBS")
    public String qryVipStoreForBS(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.qryVipStoreForBS(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    @RequestMapping("qryVipTradeForBS")
    public String qryVipTradeForBS(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = vipService.qryVipTradeForBS(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e,params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /*数据大屏用接口--end*/
}

