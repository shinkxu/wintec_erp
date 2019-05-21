package erp.chain.service.o2o;

import com.saas.common.Constants;
import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.Payment;
import erp.chain.domain.Tenant;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.ConsumeHistoryVO;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.PaymentMapper;
import erp.chain.mapper.o2o.*;
import erp.chain.service.system.SmsService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.WeiXinUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;
import saas.api.util.ApiBaseServiceUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangms on 2017/1/16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipStoreHistoryService{
    @Autowired
    private VipStoreHistoryMapper vipStoreHistoryMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipTypeMapper vipTypeMapper;
    @Autowired
    private VipStoreRuleMapper vipStoreRuleMapper;
    @Autowired
    private VipStoreRuleDetailMapper vipStoreRuleDetailMapper;
    @Autowired
    private VipTradeHistoryMapper vipTradeHistoryMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private VipService vipService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private CardCouponsService cardCouponsService;

    /**
     * 分页查询
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON vipStoreList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        if(params.get("page") != null && params.get("rows") != null){
            queryParams.put("rows", params.get("rows"));
            queryParams.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        if(params.get("vipId") != null){
            queryParams.put("vipId", params.get("vipId"));
        }
        if(params.get("branchId") != null){
            queryParams.put("storeBranchId", params.get("branchId"));
        }
        queryParams.put("storeBranchId", params.get("branchId"));
        List<VipStoreHistory> list = vipStoreHistoryMapper.vipStoreList(queryParams);
        int count = list.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 冲正
     *
     * @param params
     * @throws ServiceException
     */
    public Map<String, Object> correctStoreHistory(Map params) throws ServiceException{
        Map<String, Object> result = new HashMap<>();
        VipBook vipBook = new VipBook();
        if(params.get("vipId") != null){
            String storeFrom = "";
            if(params.get("storeFrom") != null && !"".equals(params.get("storeFrom"))){
                storeFrom = params.get("storeFrom").toString();
            }
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", params.get("vipId"));
            queryParams.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(queryParams);
            if(null==vip||vip.getStatus()==1){
                result.put("isSuccess", false);
                result.put("success", "1");
                result.put("msg", "该会员不存在或者已退卡,不能冲正。");
                return result;
            }
            double corrAmount = 0;
            BigDecimal clearScore = BigDecimal.ZERO;
                /*if(params.get("currAmount") != null && !"".equals(params.get("currAmount"))){
                    corrAmount = Double.parseDouble(params.get("currAmount").toString());
                }else{*/
            Map map = new HashMap();
            map.put("id", params.get("storeHistoryId"));
            map.put("tenantId", params.get("tenantId"));
            VipStoreHistory storeHistory1 = vipStoreHistoryMapper.findByCondition(map);
            if(storeHistory1 == null){
                result.put("isSuccess", false);
                result.put("success", "1");
                result.put("msg", "未查到充值记录！");
                return result;
            }
            //储值金额+赠送金额
            corrAmount = storeHistory1.getPayAmount().doubleValue() + storeHistory1.getGiftAmount().doubleValue();
            if(corrAmount > vip.getVipStore().doubleValue()){
                corrAmount = vip.getVipStore().doubleValue();
            }
            params.put("paymentCode", storeHistory1.getStoreCode());
            vipBook = vipBookMapper.findByCondition(params);
                    /*if(vipBook==null){
                        result.setSuccess("1");
                        result.setMsg("未查到积分纪录！");
                        return result;
                    }*/
            if(vipBook != null){
                clearScore = BigDecimal.valueOf(vipBook.getVipScore().doubleValue());
            }
            //}
            //先检查账户余额是否大于冲正额
            if(vip.getVipStore().doubleValue() >= corrAmount && params.get("storeHistoryId") != null){
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String currCode = "P" + format.format(new Date());
                //修改账户余额
                queryParams.put("vipStore", vip.getVipStore().doubleValue() - corrAmount);
                Map<String, Object> param = new HashMap<>();
                param.put("id", params.get("storeHistoryId"));
                param.put("tenantId", params.get("tenantId"));
                VipStoreHistory storeHistory = vipStoreHistoryMapper.findByCondition(param);
                if(storeHistory == null){
                    result.put("isSuccess", false);
                    result.put("success", "1");
                    result.put("msg", "未查到充值记录！");
                    return result;
                }
                //累计金额减去除赠送外的金额
                if(storeHistory.getGiftAmount() != null && corrAmount > storeHistory.getGiftAmount().doubleValue()){
                    queryParams.put("vipStoreTotal", vip.getVipStoreTotal().doubleValue() - (corrAmount - storeHistory.getGiftAmount().doubleValue()));
                }
                //减去储值时赠送积分
                queryParams.put("remainingScore", (vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore()).subtract(clearScore));
                queryParams.put("sumScore", (vip.getSumScore() == null ? BigDecimal.ZERO : vip.getSumScore()).subtract(clearScore));
                queryParams.put("largessscore", (vip.getLargessscore() == null ? BigDecimal.ZERO : vip.getLargessscore()).subtract(clearScore));
                vipMapper.update(queryParams);

                WeiXinUtils.updateMemberBonusSafe(vip, clearScore.multiply(BigDecimal.valueOf(-1)).intValue(), WeiXinUtils.obtainRecordBonus(6));
                //修改充值记录
                param.put("correctFlag", true);
                param.put("remark", "冲正单号：" + currCode);
                vipStoreHistoryMapper.update(param);

                //新增冲正记录
                VipStoreHistory corrHistory = new VipStoreHistory();
                //添加冲正记录数据
                if(corrAmount >= storeHistory.getGiftAmount().doubleValue()){
                    corrHistory.setGiftAmount(storeHistory.getGiftAmount());
                    corrHistory.setPayAmount(new BigDecimal(corrAmount - storeHistory.getGiftAmount().doubleValue()));
                }
                else{
                    corrHistory.setGiftAmount(new BigDecimal(corrAmount));
                    corrHistory.setPayAmount(new BigDecimal(0));
                }
                corrHistory.setVipId(vip.getId());
                corrHistory.setStoreCode(currCode);
                corrHistory.setStoreType("2");
                corrHistory.setPayType(params.get("payType").toString());
                if(params.get("branchId") != null){
                    corrHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    corrHistory.setStoreBranchId(vip.getBranchId());
                }
                if(params.get("branchName") != null){
                    corrHistory.setStoreBranchName(params.get("branchName").toString());
                }
                else{
                    corrHistory.setStoreBranchName(storeHistory.getStoreBranchName());
                }
                if(params.get("createBy") != null){
                    corrHistory.setCreateBy(params.get("createBy").toString());
                }
                if(params.get("remark") != null){
                    corrHistory.setRemark(params.get("remark").toString());
                }
                if(params.get("storeFrom") != null){
                    corrHistory.setStoreFrom(params.get("storeFrom").toString());
                }
                corrHistory.setCorrectCode(storeHistory.getStoreCode());
                corrHistory.setCreateAt(new Date());
                corrHistory.setLastUpdateAt(new Date());
                corrHistory.setTenantId(vip.getTenantId());
                corrHistory.setVipOperStore(vip.getVipStore().subtract(BigDecimal.valueOf(corrAmount)));
                if(null==params.get("paymentId")){
                    corrHistory.setPaymentId(storeHistory1.getPaymentId());
                }else{
                    corrHistory.setPaymentId(new BigInteger(params.get("paymentId").toString()));
                }
                /*扣减导购员提成*/
                if(storeHistory.getGuideId() != null){
                    corrHistory.setGuideId(storeHistory.getGuideId());
                    corrHistory.setStoreRate(storeHistory.getStoreRate());
                    corrHistory.setCommissionAmount(BigDecimal.ZERO.subtract(storeHistory.getCommissionAmount()));
                }
                int flag = vipStoreHistoryMapper.insert(corrHistory);
                if(flag == 1){
                    result.put("isSuccess", true);
                    result.put("success", "0");
                    result.put("msg", "冲正成功！");
                }

                if(vipBook != null){
                    //生成积分变动记录
                    VipBook vipBook1 = new VipBook();
                    vipBook1.setVipId(vip.getId());
                    vipBook1.setTenantId(vip.getTenantId());
                    vipBook1.setBranchId(vip.getBranchId());
                    vipBook1.setBookType(6);
                    vipBook1.setOperateBy("冲正扣减");
                    vipBook1.setOperateAt(new Date());
                    vipBook1.setVipCode(vip.getVipCode());
                    vipBook1.setTotalScore(vip.getRemainingScore());
                    vipBook1.setVipScore(BigDecimal.ZERO.subtract(clearScore));
                    vipBook1.setCreateAt(new Date());
                    vipBook1.setLastUpdateAt(new Date());
                    vipBook1.setStoreFrom(storeFrom);
                    vipBook1.setPaymentCode(currCode);
                    vipBookMapper.insert(vipBook1);
                }
            }
            else{
                result.put("isSuccess", false);
                result.put("success", "1");
                result.put("msg", "冲正金额大于账户余额！");
            }
        }
        else{
            result.put("isSuccess", false);
            result.put("success", "1");
            result.put("msg", "冲正失败。会员不存在。");
        }

        return result;
    }

    public ApiRest correctStoreHistoryOnlinePay(Map params){
        ApiRest apiRest = new ApiRest();
        VipBook vipBook = new VipBook();
        if(params.get("vipId") != null){
            String storeFrom = "0";
            if(params.get("storeFrom") != null && !"".equals(params.get("storeFrom"))){
                storeFrom = params.get("storeFrom").toString();
            }
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", params.get("vipId"));
            queryParams.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(queryParams);
            double corrAmount;
            BigDecimal clearScore = BigDecimal.ZERO;
                /*if(params.get("currAmount") != null && !"".equals(params.get("currAmount"))){
                    corrAmount = Double.parseDouble(params.get("currAmount").toString());
                }else{*/
            params.put("id", params.get("storeHistoryId"));
            VipStoreHistory storeHistory1 = vipStoreHistoryMapper.findByCondition(params);
            if(storeHistory1 == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查到充值记录！");
                return apiRest;
            }
            //储值金额+赠送金额
            Double refundAmount = storeHistory1.getPayAmount().doubleValue();
            corrAmount = storeHistory1.getPayAmount().doubleValue() + storeHistory1.getGiftAmount().doubleValue();
            if(corrAmount > vip.getVipStore().doubleValue()){
                corrAmount = vip.getVipStore().doubleValue();
            }
            params.put("paymentCode", storeHistory1.getStoreCode());
            vipBook = vipBookMapper.findByCondition(params);
                    /*if(vipBook==null){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("未查到积分纪录！");
                        return apiRest;
                    }*/
            if(vipBook != null){
                clearScore = BigDecimal.valueOf(vipBook.getVipScore().doubleValue());
            }
            /*clearScore = BigDecimal.valueOf(vipBook.getVipScore().doubleValue());*/
            //}
            //先检查账户余额是否大于冲正额
            if(vip.getVipStore().doubleValue() >= corrAmount && params.get("storeHistoryId") != null){
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String currCode = "P" + format.format(new Date());
                //修改账户余额
                queryParams.put("vipStore", vip.getVipStore().doubleValue() - corrAmount);
                Map<String, Object> param = new HashMap<>();
                param.put("id", params.get("storeHistoryId"));
                param.put("tenantId", params.get("tenantId"));
                VipStoreHistory storeHistory = vipStoreHistoryMapper.findByCondition(param);
                if(storeHistory == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未查到充值记录！");
                    apiRest.setMessage("未查到充值记录！");
                    return apiRest;
                }
                String type = params.get("type").toString();
                String opUser = "System";
                if(params.get("createBy") != null){
                    opUser = params.get("createBy").toString();
                }
                Map<String, String> payParam = new HashMap<>();
                payParam.put("tenantId", params.get("tenantId").toString());
                payParam.put("branchId", storeHistory.getStoreBranchId().toString());
                if(type.equals("WX")){//微信退款
                    payParam.put("out_trade_no", storeHistory.getOrderNumber());
                    payParam.put("out_refund_no", currCode);
                    payParam.put("total_fee", String.valueOf((int)(refundAmount * 100)));
                    payParam.put("refund_fee", String.valueOf((int)(refundAmount * 100)));
                    payParam.put("op_user_id", opUser);
                    apiRest = ProxyApi.proxyGet("out", "pay", "wxpayRefund", payParam);
                    if(!apiRest.getIsSuccess()){
                        return apiRest;
                    }
                }
                else if(type.equals("ZFB")){//支付宝退款
                    payParam.put("out_trade_no", storeHistory.getOrderNumber());
                    payParam.put("out_request_no", currCode);
                    payParam.put("refund_amount", refundAmount.toString());
                    apiRest = ProxyApi.proxyGet("out", "pay", "alipayRefund", payParam);
                    if(!apiRest.getIsSuccess()){
                        return apiRest;
                    }
                }
                LogUtil.logInfo("线上支付退款成功！单号：" + storeHistory.getStoreCode() + "，tenantId=" + storeHistory.getTenantId() + "。");
                //累计金额减去除赠送外的金额
                if(storeHistory.getGiftAmount() != null && corrAmount > storeHistory.getGiftAmount().doubleValue()){
                    queryParams.put("vipStoreTotal", vip.getVipStoreTotal().doubleValue() - (corrAmount - storeHistory.getGiftAmount().doubleValue()));
                }
                //减去储值时赠送积分
                queryParams.put("remainingScore", (vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore()).subtract(clearScore));
                queryParams.put("sumScore", (vip.getSumScore() == null ? BigDecimal.ZERO : vip.getSumScore()).subtract(clearScore));
                queryParams.put("largessscore", (vip.getLargessscore() == null ? BigDecimal.ZERO : vip.getLargessscore()).subtract(clearScore).subtract(clearScore));
                vipMapper.update(queryParams);

                WeiXinUtils.updateMemberBonusSafe(vip, clearScore.intValue(), WeiXinUtils.obtainRecordBonus(6));
                //修改充值记录
                param.put("correctFlag", true);
                param.put("remark", "冲正单号：" + currCode);
                vipStoreHistoryMapper.update(param);

                //新增冲正记录
                VipStoreHistory corrHistory = new VipStoreHistory();
                //添加冲正记录数据
                if(corrAmount >= storeHistory.getGiftAmount().doubleValue()){
                    corrHistory.setGiftAmount(storeHistory.getGiftAmount());
                    corrHistory.setPayAmount(new BigDecimal(corrAmount - storeHistory.getGiftAmount().doubleValue()));
                }
                else{
                    corrHistory.setGiftAmount(new BigDecimal(corrAmount));
                    corrHistory.setPayAmount(new BigDecimal(0));
                }
                corrHistory.setVipId(vip.getId());
                corrHistory.setStoreCode(currCode);
                corrHistory.setStoreType("2");
                corrHistory.setPayType(params.get("payType").toString());
                if(params.get("branchId") != null){
                    corrHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    corrHistory.setStoreBranchId(vip.getBranchId());
                }
                if(params.get("branchName") != null){
                    corrHistory.setStoreBranchName(params.get("branchName").toString());
                }
                else{
                    corrHistory.setStoreBranchName(storeHistory.getStoreBranchName());
                }
                if(params.get("createBy") != null){
                    corrHistory.setCreateBy(params.get("createBy").toString());
                }
                if(params.get("remark") != null){
                    corrHistory.setRemark(params.get("remark").toString());
                }
                corrHistory.setCorrectCode(storeHistory.getStoreCode());
                corrHistory.setCreateAt(new Date());
                corrHistory.setLastUpdateAt(new Date());
                corrHistory.setTenantId(vip.getTenantId());
                corrHistory.setStoreFrom(storeFrom);
                /*扣减导购员提成*/
                if(storeHistory.getGuideId() != null){
                    corrHistory.setGuideId(storeHistory.getGuideId());
                    corrHistory.setStoreRate(storeHistory.getStoreRate());
                    corrHistory.setCommissionAmount(BigDecimal.ZERO.subtract(storeHistory.getCommissionAmount()));
                }
                int flag = vipStoreHistoryMapper.insert(corrHistory);
                if(flag == 1){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("冲正成功！");
                }

                if(vipBook != null){
                    //生成积分变动记录
                    VipBook vipBook1 = new VipBook();
                    vipBook1.setVipId(vip.getId());
                    vipBook1.setTenantId(vip.getTenantId());
                    vipBook1.setBranchId(vip.getBranchId());
                    vipBook1.setBookType(6);
                    vipBook1.setOperateBy("冲正扣减");
                    vipBook1.setOperateAt(new Date());
                    vipBook1.setVipCode(vip.getVipCode());
                    vipBook1.setTotalScore(vip.getRemainingScore());
                    vipBook1.setVipScore(BigDecimal.ZERO.subtract(clearScore));
                    vipBook1.setCreateAt(new Date());
                    vipBook1.setLastUpdateAt(new Date());
                    vipBook1.setStoreFrom(storeFrom);
                    vipBook1.setPaymentCode(currCode);
                    vipBookMapper.insert(vipBook1);
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("冲正金额大于账户余额！");
                apiRest.setMessage("冲正金额大于账户余额！");
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("冲正失败。会员不存在。");
            apiRest.setMessage("冲正失败。会员不存在。");
        }
        return apiRest;
    }

//    /**
//     * 冲正
//     */
//    def rectifyStoreHistory(params){
//        ApiRest rest = new ApiRest();
//        try {
//            if(params.vipId){
//                Vip vip = Vip.findById(Integer.parseInt(params.vipId));
//                double corrAmount = 0;
//                if(params.currAmount){
//                    corrAmount = Double.parseDouble(params.currAmount);
//                }
//                //先检查账户余额是否大于冲正额
//                if(vip.vipStore >= corrAmount && params.storeHistoryId){
//
//                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String currCode = "P" + format.format(new Date());
//                    //修改账户余额
//                    vip.setVipStore(vip.vipStore - corrAmount);
//
//                    VipStoreHistory storeHistory = VipStoreHistory.findById(Integer.parseInt(params.storeHistoryId));
//                    //累计金额减去除赠送外的金额
//                    if(storeHistory.giftAmount && corrAmount > storeHistory.giftAmount){
//                        vip.setVipStoreTotal(vip.vipStoreTotal - (corrAmount - storeHistory.giftAmount));
//                    }
//                    vip.save flush:true;
//                    //修改充值记录
//
//                    storeHistory.setCorrectFlag(true);
//                    storeHistory.setRemark("冲正单号：" + currCode);
//                    storeHistory.save flush: true;
//
//                    //新增冲正记录
//                    VipStoreHistory corrHistory = new VipStoreHistory();
//                    //添加冲正记录数据
//                    if(corrAmount >= storeHistory.giftAmount){
//                        corrHistory.setGiftAmount(storeHistory.giftAmount);
//                        corrHistory.setPayAmount(corrAmount - storeHistory.giftAmount);
//                    } else {
//                        corrHistory.setGiftAmount(corrAmount);
//                        corrHistory.setPayAmount(0);
//                    }
//                    corrHistory.setVipId(vip.id);
//                    corrHistory.setStoreCode(currCode);
//                    corrHistory.setStoreType("2");
//                    corrHistory.setPayType(params.payType);
//                    corrHistory.setStoreBranchId(storeHistory.storeBranchId);
//                    corrHistory.setStoreBranchName(storeHistory.storeBranchName);
//                    corrHistory.setRemark(params.remark);
//                    corrHistory.setCorrectCode(storeHistory.storeCode);
//
//                    corrHistory.save flush: true;
//
//                    rest.data = corrHistory;
//                    rest.isSuccess = true;
//                    rest.message = "冲正成功！"
//
//                } else {
//                    rest.isSuccess = false;
//                    rest.message = "冲正金额大于账户余额！"
//                }
//            } else {
//                rest.isSuccess = false;
//                rest.message = "冲正失败。会员不存在。"
//            }
//        } catch (Exception e) {
//            LogUtil.logError("冲正异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }

    /**
     * 会员充值
     *
     * @param params
     * @throws ServiceException
     */
    public ResultJSON addStoreHistory(Map params) throws ServiceException, ParseException{
        ResultJSON result = new ResultJSON();
        if(params.get("vipId") != null && !"".equals(params.get("vipId"))){
            ApiRest rest = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
            Tenant tenant = null;
            VipStoreRuleDetails storeRuleDetails = null;
            if(Constants.REST_RESULT_SUCCESS.equals(rest.getResult())){
                if(rest.getData() != null){
                    tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
                }
            }
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", params.get("vipId"));
            queryParams.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(queryParams);
            //修改账户余额
            if(null != vip.getVipStore()){
                //queryParams.put("vipStore",vip.getVipStore().doubleValue() + Double.parseDouble(params.get("payAmount").toString()) + Double.parseDouble(params.get("giftAmount").toString()));
                vip.setVipStore(BigDecimal.valueOf(vip.getVipStore().doubleValue() + Double.parseDouble(params.get("payAmount").toString()) + Double.parseDouble(params.get("giftAmount").toString())));
            }
            else{
                vip.setVipStore(BigDecimal.valueOf(Double.parseDouble(params.get("payAmount").toString()) + Double.parseDouble(params.get("giftAmount").toString())));
                //queryParams.put("vipStore",Double.parseDouble(params.get("payAmount").toString()) + Double.parseDouble(params.get("giftAmount").toString()));
            }
            if(null != vip.getVipStoreTotal()){
                //累计金额不包含赠送
                //queryParams.put("vipStoreTotal",vip.getVipStoreTotal().doubleValue() + Double.parseDouble(params.get("payAmount").toString()));
                vip.setVipStoreTotal(BigDecimal.valueOf(vip.getVipStoreTotal().doubleValue() + Double.parseDouble(params.get("payAmount").toString())));
            }
            else{
                //queryParams.put("vipStoreTotal", params.get("payAmount").toString());
                vip.setVipStoreTotal(BigDecimal.valueOf(Double.parseDouble(params.get("payAmount").toString())));
            }
            //vipMapper.update(queryParams);

            //添加储值记录
            //新增冲正记录
            VipStoreHistory storeHistory = new VipStoreHistory();
            //添加冲正记录数据
            //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            String currCode = tenantConfigService.getToday("CZ", 8);//"CZ" + format.format(new Date()) + vipService.getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, vip.getTenantId());
            VipStoreRule vipStoreRule = null;
            Map<String, Object> qParam = new HashMap<>();
            if(tenant != null && tenant.getIsBranchManagementStore()){
                qParam.put("tenantId", vip.getTenantId());
                qParam.put("off", false);
                qParam.put("branchId", params.get("branchId"));
                List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(qParam);
                if(list != null && list.size() > 0){
                    vipStoreRule = list.get(0);
                }
            }
            else{
                //查询总店
                Map<String, Object> param0 = new HashMap<>();
                param0.put("tenantId", vip.getTenantId());
                param0.put("branchType", 0);
                Branch branch0 = branchMapper.find(param0);
                qParam.put("tenantId", vip.getTenantId());
                qParam.put("off", false);
                qParam.put("branchId", branch0.getId());
                vipStoreRule = vipStoreRuleMapper.findByCondition(qParam);
                if(vipStoreRule == null){
                    qParam.remove("branchId");
                    qParam.put("noBranch", 1);
                    List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(qParam);
                    if(list != null && list.size() > 0){
                        vipStoreRule = list.get(0);
                    }
                }

            }
            if(null != vipStoreRule){
                storeHistory.setDeadType(vipStoreRule.getDeadType());
                storeHistory.setDeadNum(vipStoreRule.getDeadNum());
                storeHistory.setDeadUnit(vipStoreRule.getDeadUnit());

                //查询储值规则detail
                if(params.get("detailId") != null){
                    Map<String, Object> param3 = new HashMap<>();
                    param3.put("ruleId", vipStoreRule.getId());
                    param3.put("id", params.get("detailId"));
                    param3.put("tenantId", vipStoreRule.getTenantId());
                    storeRuleDetails = vipStoreRuleDetailMapper.findByCondition(param3);
                    if(storeRuleDetails != null){
                        storeHistory.setRuleDetailId(storeRuleDetails.getId());
                    }
                }
            }
            else{
                storeHistory.setDeadType("0");
            }

            storeHistory.setVipId(vip.getId());
            storeHistory.setStoreCode(currCode);
            storeHistory.setStoreType("1");
            storeHistory.setPayAmount(new BigDecimal(params.get("payAmount").toString()));
            storeHistory.setGiftAmount(new BigDecimal(params.get("giftAmount").toString()));
            storeHistory.setPayType("6");
            storeHistory.setPaymentId(BigInteger.valueOf(Long.parseLong(params.get("payType").toString())));
            if(params.get("branchId") != null){
                storeHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
            }
            else{
                storeHistory.setStoreBranchId(vip.getBranchId());
            }
            if(params.get("branchName") != null){
                storeHistory.setStoreBranchName(params.get("branchName").toString());
            }
            if(params.get("createBy") != null){
                storeHistory.setCreateBy(params.get("createBy").toString());
            }
            if(params.get("storeFrom") != null){
                storeHistory.setStoreFrom(params.get("storeFrom").toString());
            }
            else{
                storeHistory.setStoreFrom("2");
            }
            storeHistory.setCreateAt(new Date());
            storeHistory.setLastUpdateAt(new Date());
            /*储值后余额*/
            BigDecimal vipS = vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore();
            storeHistory.setVipOperStore(vipS);
            storeHistory.setTenantId(vip.getTenantId());
            if(params.get("storeOperId") != null){
                storeHistory.setStoreOpId(new BigInteger(params.get("storeOperId").toString()));
            }
            /*导购员*/
            if(params.get("guideId") != null && !"".equals(params.get("guideId"))){
                Employee employee = employeeMapper.findEmployeeById(vip.getTenantId(), BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())), BigInteger.valueOf(Long.parseLong(params.get("guideId").toString())));
                if(employee == null){
                    result.setIsSuccess(false);
                    result.setSuccess("1");
                    result.setMsg("查询导购员失败");
                    return result;
                }
                storeHistory.setGuideId(employee.getId());
                storeHistory.setStoreRate(employee.getStoreRate());
                storeHistory.setCommissionAmount(new BigDecimal(params.get("payAmount").toString()).multiply(employee.getStoreRate().divide(new BigDecimal(100))));
            }
            int flag = vipStoreHistoryMapper.insert(storeHistory);
            BigInteger branchId = null;
            if(params.get("branchId") != null){
                branchId = new BigInteger(params.get("branchId").toString());
            }
            else{
                branchId = vip.getBranchId();
            }
            if(flag == 1){
                result.setSuccess("0");
                result.setMsg("充值成功！");
                if(vip.getPhone() != null && !vip.getPhone().equals("")){
                    Map smsMap = new HashMap();
                    smsMap.put("type", 2);
                    smsMap.put("tenantId", vip.getTenantId().toString());
                    smsMap.put("number", vip.getPhone());
                    Branch branch = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                    if(branch != null){
                        DecimalFormat df = new DecimalFormat("0.00");
                        smsMap.put("branchId", branchId.toString());
                        smsMap.put("content", (df.format(storeHistory.getPayAmount().add(storeHistory.getGiftAmount())) + "###" + df.format((vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore()))));
                        ApiRest ssr = smsService.sendSMS(smsMap);
                        if(!ssr.getIsSuccess()){
                            LogUtil.logInfo("发送储值短信失败！vipId=" + vip.getId());
                        }
                    }
                }
                //储值后赠送积分或优惠券
                if(storeRuleDetails != null){
                    if(storeRuleDetails.getAfterStoreType() == 1){
                        //赠送积分
                        if(storeRuleDetails.getAfterStoreData() != null){
                            BigDecimal addScore = new BigDecimal(storeRuleDetails.getAfterStoreData());
                            dealScore(branchId,vip, addScore, storeHistory.getStoreCode());
                            if(vip.getRemainingScore() != null){
                                vip.setRemainingScore(vip.getRemainingScore().add(addScore));
                                vip.setSumScore(vip.getSumScore() == null ? addScore : vip.getSumScore().add(addScore));
                            }
                            else{
                                vip.setRemainingScore(addScore);
                                vip.setSumScore(addScore);
                            }
                            if(vip.getLargessscore() != null){
                                vip.setLargessscore(vip.getLargessscore().add(addScore));
                            }
                            else{
                                vip.setLargessscore(addScore);
                            }
                            if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                DecimalFormat sd=new DecimalFormat("#");
                                Map smsMap = new HashMap();
                                smsMap.put("type", 3);
                                smsMap.put("tenantId", vip.getTenantId().toString());
                                smsMap.put("number", vip.getPhone());
                                smsMap.put("branchId",branchId.toString() );
                                smsMap.put("content", ("储值赠送###" +sd.format(addScore)+"###"+sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()))));
                                ApiRest ssr = smsService.sendSMS(smsMap);
                                if(!ssr.getIsSuccess()){
                                    LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                }
                            }
                        }
                    }
                    else if(storeRuleDetails.getAfterStoreType() == 2){
                        //赠送优惠券
                        if(storeRuleDetails.getAfterStoreData() != null){
                            Map<String, String> map3 = new HashMap<>();
                            map3.put("tenantId", vip.getTenantId().toString());
                            map3.put("cardId", storeRuleDetails.getAfterStoreData());
                            map3.put("vipId", vip.getId().toString());
                            map3.put("branchId",branchId.toString());
                            map3.put("channel","store");
                            map3.put("orderId",storeHistory.getId()==null?"":storeHistory.getId().toString());
                            map3.put("orderCode",storeHistory.getStoreCode());
                            map3.put("empId",storeHistory.getStoreOpId()==null?"":storeHistory.getStoreOpId().toString());
                            cardCouponsService.sendCardToVip(map3);
                        }
                    }
                }
                vipMapper.update(vip);
            }

        }
        else{
            result.setSuccess("1");
            result.setMsg("充值失败。会员不存在。");
        }

        return result;
    }


    private void dealScore(BigInteger branchId,Vip vip, BigDecimal addScore, String code){
        VipBook vipBook = new VipBook();

        //记录积分台帐
        vipBook.setVipId(vip.getId());
        vipBook.setTenantId(vip.getTenantId());
        vipBook.setBranchId(branchId);
        vipBook.setBookType(3);
        vipBook.setOperateBy("储值赠送");
        vipBook.setOperateAt(new Date());
        vipBook.setVipCode(vip.getVipCode());
        vipBook.setTotalScore(vip.getRemainingScore());
        vipBook.setVipScore(addScore);
        vipBook.setCreateAt(new Date());
        vipBook.setLastUpdateAt(new Date());
        vipBook.setStoreFrom("2");
        vipBook.setPaymentCode(code);
        vipBookMapper.insert(vipBook);
    }


    /**
     * pos用会员充值
     *
     * @param params
     * @throws ServiceException
     */
    public ApiRest vipStoreForPos(Map params) throws ServiceException{
        ApiRest result = new ApiRest();
        if(params.get("vipId") != null){
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(queryParams);
            if(vip == null){
                result.setIsSuccess(false);
                result.setError("会员信息查询失败！");
                result.setMessage("会员信息查询失败！");
                return result;
            }
            double amount = 0;
            if(params.get("amount") != null){
                amount = Double.parseDouble(params.get("amount").toString());
            }

            double payLimit = amount;
            double presentLimit = 0;

            Tenant tenant = null;
            ApiRest rest = SaaSApi.findTenantById(vip.getTenantId());
            if(rest.getIsSuccess()){
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
            }
            VipStoreRule vipStoreRule = null;
            Map<String, Object> param = new HashMap<>();

            if(tenant != null && tenant.getIsBranchManagementStore()){
                param.put("tenantId", vip.getTenantId());
                param.put("off", false);
                if(params.get("branchId") != null){
                    param.put("branchId", new BigInteger(params.get("branchId").toString()));
                }
                List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                if(list != null && list.size() > 0){
                    vipStoreRule = list.get(0);
                }
            }
            else{
                //查询总店
                Map<String, Object> param0 = new HashMap<>();
                param0.put("tenantId", vip.getTenantId());
                param0.put("branchType", 0);
                Branch branch0 = branchMapper.find(param0);
                param.put("tenantId", vip.getTenantId());
                param.put("off", false);
                param.put("branchId", branch0.getId());
                vipStoreRule = vipStoreRuleMapper.findByCondition(param);
                if(vipStoreRule == null){
                    param.remove("branchId");
                    param.put("noBranch", 1);
                    List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                    if(list != null && list.size() > 0){
                        vipStoreRule = list.get(0);
                    }
                }
            }

            if(null != vipStoreRule && !vipStoreRule.isOff()){
                Map<String, Object> param1 = new HashMap<>();
                param1.put("ruleId", vipStoreRule.getId());
                List<VipStoreRuleDetails> details = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param1);
                int maxIndex = -1;
                double currPayLimit = 0;
                if(null != details && details.size() > 0){
                    for(int i = 0; i < details.size(); i++){
                        if(amount >= details.get(i).getPayLimit().doubleValue() && currPayLimit < details.get(i).getPayLimit().doubleValue()){
                            maxIndex = i;
                            currPayLimit = details.get(i).getPayLimit().doubleValue();
                        }
                    }
                }
                if(maxIndex >= 0 && details != null){
                    if(null != details.get(maxIndex).getPresentLimit()){
                        presentLimit = details.get(maxIndex).getPresentLimit().doubleValue();
                    }
                }
            }

            //修改账户余额
            if(null != vip.getVipStore()){
                queryParams.put("vipStore", vip.getVipStore().doubleValue() + payLimit + presentLimit);
            }
            else{
                queryParams.put("vipStore", payLimit + presentLimit);
            }
            if(null != vip.getVipStoreTotal()){
                //累计金额不包含赠送
                queryParams.put("vipStoreTotal", vip.getVipStoreTotal().doubleValue() + payLimit);
            }
            else{
                queryParams.put("vipStoreTotal", payLimit);
            }
            vipMapper.update(queryParams);

            //添加储值记录
            VipStoreHistory storeHistory = new VipStoreHistory();
            //添加冲正记录数据
            //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            String currCode = tenantConfigService.getToday("CZ", 8);//"CZ" + format.format(new Date()) + vipService.getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, vip.getTenantId());

            if(null != vipStoreRule){
                storeHistory.setDeadType(vipStoreRule.getDeadType());
                storeHistory.setDeadNum(vipStoreRule.getDeadNum());
                storeHistory.setDeadUnit(vipStoreRule.getDeadUnit());
            }
            else{
                storeHistory.setDeadType("0");
            }
            storeHistory.setVipId(vip.getId());
            storeHistory.setStoreCode(currCode);
            storeHistory.setStoreType("1");
            storeHistory.setPayAmount(new BigDecimal(payLimit));
            storeHistory.setGiftAmount(new BigDecimal(presentLimit));
            if(params.get("payType") != null){
                //1:现金 2:银行卡 3:支付宝 4:微信
                storeHistory.setPayType(params.get("payType").toString());
            }
            else{
                storeHistory.setPayType("1");
            }
            if(params.get("branchName") != null){
                storeHistory.setStoreBranchName(params.get("branchName").toString());
            }
            //记录操作人
            if(params.get("storeOpName") != null){
                storeHistory.setStoreOpName(params.get("storeOpName").toString());
            }
            if(params.get("createBy") != null){
                storeHistory.setCreateBy(params.get("createBy").toString());
                storeHistory.setLastUpdateBy(params.get("createBy").toString());
            }
            if(params.get("branchId") != null){
                storeHistory.setStoreBranchId(BigInteger.valueOf(Long.valueOf(params.get("branchId").toString())));
            }
            storeHistory.setCreateAt(new Date());
            storeHistory.setLastUpdateAt(new Date());

            if(params.get("storeFrom") != null){
                storeHistory.setStoreFrom(params.get("storeFrom").toString());
            }
            else{
                storeHistory.setStoreFrom("0");
            }
            /*储值后余额*/
            if(null != vip.getVipStore()){
                storeHistory.setVipOperStore(BigDecimal.valueOf(vip.getVipStore().doubleValue()));
            }
            else{
                storeHistory.setVipOperStore(BigDecimal.valueOf(payLimit + presentLimit));
            }
            storeHistory.setTenantId(vip.getTenantId());
            /*导购员*/
            if(params.get("guideId") != null && !"".equals(params.get("guideId"))){
                Employee employee = employeeMapper.findEmployeeById(vip.getTenantId(), BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())), BigInteger.valueOf(Long.parseLong(params.get("guideId").toString())));
                if(employee == null){
                    result.setIsSuccess(false);
                    result.setError("查询导购员失败");
                    return result;
                }
                storeHistory.setGuideId(employee.getId());
                storeHistory.setStoreRate(employee.getStoreRate());
                storeHistory.setCommissionAmount(new BigDecimal(params.get("payAmount").toString()).multiply(employee.getStoreRate().divide(new BigDecimal(100))));
            }
            vipStoreHistoryMapper.insert(storeHistory);
            Map vipMs = new HashMap();
            vipMs.put("id", vip.getId());
            Vip resultVip = vipMapper.findById(vipMs);
            if(resultVip == null){
                result.setIsSuccess(false);
                result.setMessage("充值失败。会员不存在。");
                result.setError("充值失败。会员不存在。");
                return result;
            }
            result.setData(resultVip);
            result.setIsSuccess(true);
            result.setMessage("充值成功！");
            if(vip.getPhone() != null && !vip.getPhone().equals("")){
                Map smsMap = new HashMap();
                smsMap.put("type", 2);
                smsMap.put("tenantId", vip.getTenantId().toString());
                BigInteger branchId = null;
                if(params.get("branchId") != null){
                    branchId = new BigInteger(params.get("branchId").toString());
                }
                else{
                    branchId = vip.getBranchId();
                }
                smsMap.put("number", vip.getPhone());
                Branch branch = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                if(branch != null){
                    DecimalFormat df = new DecimalFormat("0.00");
                    smsMap.put("branchId", branchId.toString());
                    smsMap.put("content", (df.format(storeHistory.getPayAmount().add(storeHistory.getGiftAmount())) + "###" + df.format((vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore()).add(storeHistory.getPayAmount()).add(storeHistory.getGiftAmount()))));
                    ApiRest ssr = smsService.sendSMS(smsMap);
                    if(!ssr.getIsSuccess()){
                        LogUtil.logInfo("发送储值短信失败！vipId=" + vip.getId());
                    }
                }
            }
        }
        else{
            result.setIsSuccess(false);
            result.setMessage("充值失败。会员不存在。");
            result.setError("充值失败。会员不存在。");
        }

        return result;
    }

    /**
     * pos用会员充值V2(token防重)
     *
     * @param params
     * @throws ServiceException
     */
    public ResultJSON vipStoreForPosV2(Map params) throws ServiceException, ParseException, IOException{
        ResultJSON result = new ResultJSON();
        if(params.get("vipId") != null){
            if(params.get("token") == null){
                result.setSuccess("1");
                result.setMsg("token不存在!");
                return result;
            }
            String redisToken = "CZ_"+params.get("tenantId")+"_"+params.get("branchId")+"_"+params.get("vipId")+"_"+params.get("token");
            if(PartitionCacheUtils.get(redisToken) != null){
                result.setSuccess("1");
                result.setMsg("正在处理，请稍候!");
                return result;
            }
            String branchName="";
            BigDecimal rechargeAmount=BigDecimal.ZERO;
            BigDecimal giveAmount= BigDecimal.ZERO;
            BigDecimal availableBalance=BigDecimal.ZERO;
            Integer givePoint=0;
            VipStoreRuleDetails storeRuleDetails = null;
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", params.get("vipId"));
            queryParams.put("tenantId",params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(queryParams);
            if(vip==null){
                result.setSuccess("1");
                result.setMsg("查询会员失败!");
                return result;
            }
            Map<String, Object> tokenParam = new HashMap<>();
            tokenParam.put("token", params.get("token"));
            tokenParam.put("branchId", params.get("branchId"));
            tokenParam.put("tenantId", params.get("tenantId"));
            VipStoreHistory checkStore = vipStoreHistoryMapper.findByCondition(tokenParam);
            if(checkStore != null){
                PartitionCacheUtils.del(redisToken);
                result.setObject(vip);
                result.setSuccess("0");
                result.setMsg("充值成功！");
                return result;
            }
            PartitionCacheUtils.set(redisToken, params.get("token").toString());

            double amount = 0;
            if(params.get("amount") != null){
                amount = Double.parseDouble(params.get("amount").toString());
            }

            double payLimit = amount;
            double presentLimit = 0;
            rechargeAmount=BigDecimal.valueOf(amount);

            Tenant tenant = null;
            ApiRest rest = SaaSApi.findTenantById(vip.getTenantId());
            if(rest.getIsSuccess()){
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
            }
            VipStoreRule vipStoreRule = null;
            Map<String, Object> param = new HashMap<>();
            if(tenant != null && tenant.getIsBranchManagementStore()){
                param.put("tenantId", vip.getTenantId());
                param.put("off", false);
                param.put("branchId", params.get("branchId")==null?vip.getBranchId():new BigInteger(params.get("branchId").toString()));
                List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                if(list != null && list.size() > 0){
                    vipStoreRule = list.get(0);
                }
            }
            else{
                //查询总店
                Map<String, Object> param0 = new HashMap<>();
                param0.put("tenantId", vip.getTenantId());
                param0.put("branchType", 0);
                Branch branch0 = branchMapper.find(param0);
                param.put("tenantId", vip.getTenantId());
                param.put("off", false);
                param.put("branchId", branch0.getId());
                vipStoreRule = vipStoreRuleMapper.findByCondition(param);
                if(vipStoreRule == null){
                    param.remove("branchId");
                    param.put("noBranch", 1);
                    List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                    if(list != null && list.size() > 0){
                        vipStoreRule = list.get(0);
                    }
                }
            }
            BigInteger detailId = null;
            if(null != vipStoreRule && !vipStoreRule.isOff()){
                Map<String, Object> param1 = new HashMap<>();
                param1.put("ruleId", vipStoreRule.getId());
                List<VipStoreRuleDetails> details = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param1);
                int maxIndex = -1;
                double currPayLimit = 0;
                if(null != details && details.size() > 0){
                    for(int i = 0; i < details.size(); i++){
                        if((details.get(i).getStartTime() == null || details.get(i).getStartTime().compareTo(new Date()) <= 1)
                                && (details.get(i).getEndTime() == null || details.get(i).getEndTime().compareTo(new Date()) >= 1)){
                            //判断是否是生效一次
                            if(details.get(i).getEffectTimes() != null && details.get(i).getEffectTimes() == 1){
                                Map<String, Object> map = new HashMap<>();
                                map.put("tenantId", vipStoreRule.getTenantId());
                                map.put("vipId", vip.getId());
                                map.put("ruleDetailId", details.get(i).getId());
                                List<VipStoreHistory> histories = vipStoreHistoryMapper.vipStoreList(map);
                                if(histories != null && histories.size() > 0){
                                    continue;
                                }
                            }
                            //判断是否指定会员类型
                            if(details.get(i).getPointedVipType() != null && !details.get(i).getPointedVipType().equals(BigInteger.ZERO)){
                                if(!details.get(i).getPointedVipType().equals(vip.getTypeId())){
                                    continue;
                                }
                            }
                            if(amount >= details.get(i).getPayLimit().doubleValue() && currPayLimit < details.get(i).getPayLimit().doubleValue()){
                                maxIndex = i;
                                currPayLimit = details.get(i).getPayLimit().doubleValue();
                            }
                        }
                    }
                }
                if(maxIndex >= 0){
                    if(details != null && null != details.get(maxIndex).getPresentLimit()){
                        presentLimit = details.get(maxIndex).getPresentLimit().doubleValue();
                        detailId = details.get(maxIndex).getId();
                        storeRuleDetails=details.get(maxIndex);
                    }
                }
            }
            //如果是自定义输入金额
            if(params.get("inputPresent") != null){
                if(presentLimit!=Double.parseDouble(params.get("inputPresent").toString())){
                    presentLimit = Double.parseDouble(params.get("inputPresent").toString());
                    detailId = null;
                }
            }
            giveAmount=BigDecimal.valueOf(presentLimit);

            //修改账户余额
            if(null != vip.getVipStore()){
                queryParams.put("vipStore", vip.getVipStore().doubleValue() + payLimit + presentLimit);
            }
            else{
                queryParams.put("vipStore", payLimit + presentLimit);
            }
            if(null != vip.getVipStoreTotal()){
                //累计金额不包含赠送
                queryParams.put("vipStoreTotal", vip.getVipStoreTotal().doubleValue() + payLimit);
            }
            else{
                queryParams.put("vipStoreTotal", payLimit);
            }
            vipMapper.update(queryParams);

            //添加储值记录
            VipStoreHistory storeHistory = new VipStoreHistory();
            //添加冲正记录数据
            //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            String currCode;
            if(params.get("orderNumber") != null && !params.get("orderNumber").toString().equals("")){
                currCode = params.get("orderNumber").toString();
            }
            else{
                currCode = tenantConfigService.getToday("CZ", 8);//"CZ" + format.format(new Date()) + vipService.getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, vip.getTenantId());
            }
            if(null != vipStoreRule){
                storeHistory.setDeadType(vipStoreRule.getDeadType());
                storeHistory.setDeadNum(vipStoreRule.getDeadNum());
                storeHistory.setDeadUnit(vipStoreRule.getDeadUnit());

                //查询储值规则detail
                if(detailId != null){
                    Map<String, Object> param3 = new HashMap<>();
                    param3.put("ruleId", vipStoreRule.getId());
                    param3.put("id", detailId);
                    param3.put("tenantId", vipStoreRule.getTenantId());
                    storeRuleDetails = vipStoreRuleDetailMapper.findByCondition(param3);
                    if(storeRuleDetails != null){
                        storeHistory.setRuleDetailId(storeRuleDetails.getId());
                    }
                }
            }
            else{
                storeHistory.setDeadType("0");
            }

            storeHistory.setVipId(vip.getId());
            storeHistory.setStoreCode(currCode);
            storeHistory.setStoreType("1");
            storeHistory.setPayAmount(new BigDecimal(payLimit));
            storeHistory.setGiftAmount(new BigDecimal(presentLimit));
            if(params.get("payType") != null){
                //1:现金 2:银行卡 3:支付宝 4:微信
                storeHistory.setPayType(params.get("payType").toString());
            }
            else{
                storeHistory.setPayType("1");
            }
            if(params.get("orderNumber") != null){
                storeHistory.setOrderNumber(params.get("orderNumber").toString());
            }
            //记录操作人
            if(params.get("storeOpName") != null){
                storeHistory.setStoreOpName(params.get("storeOpName").toString());
            }
            if(params.get("createBy") != null){
                storeHistory.setCreateBy(params.get("createBy").toString());
            }
            if(params.get("branchId") != null){
                storeHistory.setStoreBranchId(BigInteger.valueOf(Long.valueOf(params.get("branchId").toString())));
            }
            else{
                storeHistory.setStoreBranchId(vip.getBranchId());
            }
            storeHistory.setCreateAt(new Date());
            storeHistory.setLastUpdateAt(new Date());
            if(params.get("token") != null){
                storeHistory.setToken(params.get("token").toString());
            }

            if(params.get("storeFrom") != null){
                storeHistory.setStoreFrom(params.get("storeFrom").toString());
            }
            else{
                storeHistory.setStoreFrom("0");
            }
            /*储值后余额*/
            if(null != vip.getVipStore()){
                storeHistory.setVipOperStore(BigDecimal.valueOf(vip.getVipStore().doubleValue() + payLimit + presentLimit));
            }
            else{
                storeHistory.setVipOperStore(BigDecimal.valueOf(payLimit + presentLimit));
            }

            if(params.get("paymentId") != null){
                storeHistory.setPaymentId(new BigInteger(params.get("paymentId").toString()));
            }
            if(params.get("paymentCode") != null&&!"".equals(params.get("paymentCode").toString())){
                String paymentCode=params.get("paymentCode").toString();
                Payment payment=paymentMapper.findByTenantIdAndPaymentCode(vip.getTenantId(),paymentCode);
                if(payment!=null){
                    storeHistory.setPaymentId(payment.getId());
                }
            }
            if(params.get("storeOperId") != null){
                storeHistory.setStoreOpId(new BigInteger(params.get("storeOperId").toString()));
            }
            storeHistory.setTenantId(vip.getTenantId());
            /*导购员*/
            if(params.get("guideId") != null && !"".equals(params.get("guideId"))){
                Employee employee = employeeMapper.findEmployeeById(vip.getTenantId(), BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())), BigInteger.valueOf(Long.parseLong(params.get("guideId").toString())));
                if(employee == null){
                    result.setIsSuccess(false);
                    result.setSuccess("1");
                    result.setMsg("查询导购员失败");
                    return result;
                }
                storeHistory.setGuideId(employee.getId());
                storeHistory.setStoreRate(employee.getStoreRate());
                storeHistory.setCommissionAmount(new BigDecimal(params.get("payAmount").toString()).multiply(employee.getStoreRate().divide(new BigDecimal(100))));
            }
            BigInteger branchId = null;
            if(params.get("branchId") != null){
                branchId = new BigInteger(params.get("branchId").toString());
            }
            else{
                branchId = vip.getBranchId();
            }
            Branch branch = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
            if(branch != null){
                branchName=branch.getName();
            }
            vipStoreHistoryMapper.insert(storeHistory);
            vip = vipMapper.findByCondition(queryParams);
            availableBalance=vip.getVipStore();
            result.setObject(vip);
            result.setSuccess("0");
            result.setMsg("充值成功！");
            //储值后赠送积分或优惠券
            if(storeRuleDetails != null){
                if(storeRuleDetails.getAfterStoreType() == 1){
                    //赠送积分
                    if(storeRuleDetails.getAfterStoreData() != null){
                        BigDecimal addScore = new BigDecimal(storeRuleDetails.getAfterStoreData());
                        dealScore(branchId,vip, addScore, storeHistory.getStoreCode());
                        if(vip.getRemainingScore() != null){
                            vip.setRemainingScore(vip.getRemainingScore().add(addScore));
                            vip.setSumScore(vip.getSumScore().add(addScore));
                        }
                        else{
                            vip.setRemainingScore(addScore);
                            vip.setSumScore(addScore);
                        }
                        if(vip.getLargessscore() != null){
                            vip.setLargessscore(vip.getLargessscore().add(addScore));
                        }
                        else{
                            vip.setLargessscore(addScore);
                        }
                        givePoint=(int)addScore.doubleValue();
                        if(vip.getPhone() != null && !vip.getPhone().equals("")){
                            DecimalFormat sd=new DecimalFormat("#");
                            Map smsMap = new HashMap();
                            smsMap.put("type", 3);
                            smsMap.put("tenantId", vip.getTenantId().toString());
                            smsMap.put("number", vip.getPhone());
                            smsMap.put("branchId",branchId.toString() );
                            smsMap.put("content", ("储值赠送###" +sd.format(addScore)+"###"+sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()))));
                            ApiRest ssr = smsService.sendSMS(smsMap);
                            if(!ssr.getIsSuccess()){
                                LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                            }
                        }
                        WeiXinUtils.updateMemberBonusSafe(vip, WeiXinUtils.obtainRecordBonus(3));
                    }
                }
                else if(storeRuleDetails.getAfterStoreType() == 2){
                    //赠送优惠券
                    if(storeRuleDetails.getAfterStoreData() != null){
                        Map<String, String> map3 = new HashMap<>();
                        map3.put("tenantId", vip.getTenantId().toString());
                        map3.put("cardId", storeRuleDetails.getAfterStoreData());
                        map3.put("vipId", vip.getId().toString());
                        map3.put("channel","store");
                        map3.put("branchId",branchId.toString());
                        map3.put("orderId",storeHistory.getId()==null?"":storeHistory.getId().toString());
                        map3.put("orderCode",storeHistory.getStoreCode());
                        map3.put("empId",storeHistory.getStoreOpId()==null?"":storeHistory.getStoreOpId().toString());
                        cardCouponsService.sendCardToVip(map3);
                    }
                }
            }
            vipMapper.update(vip);
            if(vip.getPhone() != null && !vip.getPhone().equals("")){
                Map smsMap = new HashMap();
                smsMap.put("type", 2);
                smsMap.put("tenantId", vip.getTenantId().toString());
                smsMap.put("number", vip.getPhone());
                if(branch != null){
                    DecimalFormat df = new DecimalFormat("0.00");
                    smsMap.put("branchId", branchId.toString());
                    smsMap.put("content", (df.format(storeHistory.getPayAmount().add(storeHistory.getGiftAmount())) + "###" + df.format((vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore()))));
                    ApiRest ssr = smsService.sendSMS(smsMap);
                    if(!ssr.getIsSuccess()){
                        LogUtil.logInfo("发送储值短信失败！vipId=" + vip.getId());
                    }
                }
            }
            WeiXinUtils.sendRechargeTemplateSafe(vip.getId(), branchName, new Date(), rechargeAmount, giveAmount, availableBalance, givePoint);
            PartitionCacheUtils.del(redisToken);
        }
        else{
            result.setSuccess("1");
            result.setMsg("充值失败。会员不存在。");
        }

        return result;
    }

    /**
     * pos储值消费退款
     *
     * @param params
     * @throws ServiceException
     */
    public ApiRest vipStoreCorrectForPos(Map params) throws ServiceException, ParseException{
        ApiRest rest = new ApiRest();
        if(params.get("vipId") != null && !params.get("vipId").toString().equals("0") && params.get("requestTime") != null){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("id", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(param1);
            if(vip == null){
                rest.setMessage("未查询到会员信息！");
                rest.setIsSuccess(false);
                return rest;
            }
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date requestTime = format1.parse(params.get("requestTime").toString());
            if(System.currentTimeMillis() - requestTime.getTime() <= 30 * 1000){
                double amount = 0;
                if(params.get("amount") != null){
                    amount = Double.parseDouble(params.get("amount").toString());
                }
                //修改账户余额
                if(null != vip.getVipStore()){
                    param1.put("vipStore", vip.getVipStore().doubleValue() + amount);
                }
                else{
                    param1.put("vipStore", amount);
                }
                //修改账户积分
                BigDecimal clearScore = BigDecimal.ZERO;
                if(params.get("orderCode") != null && !"".equals(params.get("orderCode"))){
                    String paymentCode = params.get("orderCode").toString();
                    params.put("vipId", vip.getId());
                    params.put("paymentCode", paymentCode);
                    params.put("amount", amount);
                    VipBook vipBook1 = vipBookMapper.findVipBookHistory(params);
                    if(vipBook1 != null){
                        clearScore = vipBook1.getVipScore();
                    }
                }
                    /*if(params.get("orderCode") != null && !"".equals(params.get("orderCode"))){
                        String paymentCode = params.get("orderCode").toString();
                        params.put("vipId", vip.getId());
                        params.put("paymentCode", paymentCode);
                        VipBook vipBook1 = vipBookMapper.findByCondition(params);
                        if(vipBook1!=null){
                            BigDecimal clearScore = vipBook1.getVipScore();
                            param1.put("remainingScore", vip.getRemainingScore().subtract(clearScore));
                            param1.put("sumScore", vip.getSumScore().subtract(clearScore));
                            param1.put("largessscore", vip.getLargessscore().subtract(clearScore));
                            //生成积分变动记录
                            VipBook vipBook = new VipBook();
                            vipBook.setVipId(vip.getId());
                            vipBook.setTenantId(vip.getTenantId());
                            vipBook.setBranchId(vip.getBranchId());
                            vipBook.setBookType(1);
                            vipBook.setOperateBy("退款扣减");
                            vipBook.setOperateAt(new Date());
                            vipBook.setVipCode(vip.getVipCode());
                            vipBook.setTotalScore(vip.getRemainingScore().subtract(clearScore));
                            vipBook.setVipScore(BigDecimal.ZERO.subtract(clearScore));
                            vipBook.setCreateAt(new Date());
                            vipBook.setLastUpdateAt(new Date());
                            vipBook.setStoreFrom("0");
                            vipBook.setPaymentCode(params.get("refundOrderCode").toString());
                            vipBookMapper.insert(vipBook);
                        }
                    }*/
                //退款后减去累计消费金额
                if(vip.getBuyTimes() != null){
                    param1.put("buyTimes", vip.getBuyTimes() - 1);
                }
                if(vip.getSumConsume() != null){
                    param1.put("sumConsume", vip.getSumConsume().subtract(BigDecimal.valueOf(amount)));
                }
                if(vip.getSumFeed() != null){
                    param1.put("sumFeed", vip.getSumFeed().subtract(BigDecimal.valueOf(amount)));
                }
                    /*if(vip.getSumScore() != null){
                        param1.put("sumScore", vip.getSumScore().subtract(clearScore));
                    }*/
                vipMapper.update(param1);

                //添加储值记录
                VipStoreHistory storeHistory = new VipStoreHistory();
                //添加冲正记录数据
                //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
                String currCode = tenantConfigService.getToday("CZ", 8); //"CZ" + format.format(new Date()) + vipService.getNextSelValue(SysConfig.SYS_VIP_STORE_NUM,3,vip.getTenantId());
                /*if(params.get("saleCode") != null && !"".equals(params.get("saleCode"))){
                    currCode = params.get("saleCode").toString();
                }
                else if(params.get("refundOrderCode") != null && !"".equals(params.get("refundOrderCode"))){
                    currCode = params.get("refundOrderCode").toString();
                }*/
                if(params.get("refundOrderCode") != null && !"".equals(params.get("refundOrderCode"))){
                    currCode = params.get("refundOrderCode").toString();
                }
                else if(params.get("saleCode") != null && !"".equals(params.get("saleCode"))){
                    currCode = params.get("saleCode").toString();
                }
                storeHistory.setStoreCode(currCode);
                storeHistory.setVipId(vip.getId());
                storeHistory.setStoreType("4");
                storeHistory.setPayAmount(new BigDecimal(amount));
                storeHistory.setGiftAmount(new BigDecimal(0));
                storeHistory.setPayType("7");
                if(params.get("branchId") != null){
                    storeHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    storeHistory.setStoreBranchId(vip.getBranchId());
                }
                if(params.get("branchName") != null){
                    storeHistory.setStoreBranchName(params.get("branchName").toString());
                }
                if(params.get("createBy") != null){
                    storeHistory.setCreateBy(params.get("createBy").toString());
                }
                storeHistory.setCreateAt(new Date());
                storeHistory.setLastUpdateAt(new Date());
                storeHistory.setStoreFrom("0");
                /*消费退款后余额*/
                if(null != vip.getVipStore()){
                    storeHistory.setVipOperStore(BigDecimal.valueOf(vip.getVipStore().doubleValue() + amount));
                }
                else{
                    storeHistory.setVipOperStore(BigDecimal.valueOf(amount));
                }
                storeHistory.setTenantId(vip.getTenantId());
                vipStoreHistoryMapper.insert(storeHistory);
                //添加消费记录
                VipTradeHistory vipTradeHistory = new VipTradeHistory();
                vipTradeHistory.setVipId(vip.getId());
                vipTradeHistory.setTradeNo(currCode);
                vipTradeHistory.setTradeType("4");
                vipTradeHistory.setTradeAmount(new BigDecimal(amount));
                if(params.get("branchId") != null){
                    vipTradeHistory.setTradeBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    vipTradeHistory.setTradeBranchId(vip.getBranchId());
                }
                if(params.get("branchName") != null){
                    vipTradeHistory.setTradeBranchName(params.get("branchName").toString());
                }
                if(params.get("createBy") != null){
                    vipTradeHistory.setCreateBy(params.get("createBy").toString());
                }
                vipTradeHistory.setTradeDate(new Date());
                vipTradeHistory.setCreateAt(new Date());
                vipTradeHistory.setLastUpdateAt(new Date());
                vipTradeHistory.setTenantId(vip.getTenantId());
                vipTradeHistory.setAddScore(BigDecimal.ZERO.subtract(clearScore));
                vipTradeHistoryMapper.insert(vipTradeHistory);

                rest.setData(vip);
                rest.setMessage("退款成功！");
                rest.setIsSuccess(true);
            }
            else{
                rest.setMessage("请求超时！");
                rest.setIsSuccess(false);
            }

        }
        else{
            rest.setMessage("储值消费退款失败。会员或请求时间不存在。");
            rest.setIsSuccess(false);
        }
        return rest;
    }

    /**
     * 查询pos上传结果
     *
     * @param params
     */
    public ResultJSON queryUploadResult(Map params){
        ResultJSON result = new ResultJSON();
        if(params.get("saleCode") != null && params.get("vipId") != null){
            VipStoreHistory vipStoreHistory = vipStoreHistoryMapper.findByCondition(params);
            if(vipStoreHistory != null){
                result.setSuccess("0");
                result.setMsg("查询储值记录成功!");
                result.setObject(vipStoreHistory);
            }
            else{
                result.setSuccess("1");
                result.setMsg("储值记录不存在!");
            }
        }
        else{
            result.setSuccess("1");
            result.setMsg("saleCode 或 vipId 为null,查询失败!");
        }
        return result;
    }

//    /**
//     * 会员储值
//     * @author szq
//     */
//    def increaseStoreHistory(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            Vip vip = Vip.findById(new BigInteger(params.vipId));
//            if (null != vip.vipStore) {
//                vip.setVipStore(vip.vipStore + new BigDecimal(params.payAmount) + new BigDecimal(params.giftAmount));
//            } else {
//                vip.setVipStore(new BigDecimal(params.payAmount) + new BigDecimal(params.giftAmount));
//            }
//            if (null != vip.vipStoreTotal) {
//                vip.setVipStoreTotal(vip.vipStoreTotal + Double.parseDouble(params.payAmount));
//            } else {
//                vip.setVipStoreTotal(Double.parseDouble(params.payAmount));
//            }
//            vip.save flush: true;
//            VipStoreHistory storeHistory = new VipStoreHistory();
//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//            String currCode = "CZ" + format.format(new Date());
//            VipStoreRule vipStoreRule = null;
//            if (params.branchId != null) {
//                vipStoreRule = VipStoreRule.findByTenantIdAndBranchId(new BigInteger(params.tenantId), new BigInteger(params.branchId));
//            } else {
//                vipStoreRule = VipStoreRule.findByTenantId(new BigInteger(params.tenantId));
//            }
//            if (null != vipStoreRule) {
//                storeHistory.setDeadType(vipStoreRule.deadType);
//                storeHistory.setDeadNum(vipStoreRule.deadNum);
//                storeHistory.setDeadUnit(vipStoreRule.deadUnit);
//            } else {
//                storeHistory.setDeadType("0");
//            }
//            storeHistory.setVipId(vip.id);
//            storeHistory.setStoreCode(currCode);
//            storeHistory.setStoreType("1");
//            storeHistory.setPayAmount(new BigDecimal(params.payAmount));
//            storeHistory.setGiftAmount(new BigDecimal(params.giftAmount));
//            storeHistory.setPayType(params.payType);
//            storeHistory.setStoreBranchId(vip.branchId);
//            storeHistory.save flush: true;
//            rest.data = storeHistory;
//            rest.message = "会员储值成功";
//            rest.isSuccess = true;
//        } catch (Exception e) {
//            LogUtil.logError("会员储值异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 根据会员id查询会员储值历史
//     * @author szq
//     */
//    public Map<String,Object> listVipStoreHistory(Map params) {
//        Map<String,Object> map = new HashMap<String,Object>();
//        try {
//            StringBuffer query = new StringBuffer("from VipStoreHistory vsh where 1=1 ");
//            StringBuffer queryCount = new StringBuffer("select count(*) from VipStoreHistory vsh where 1=1 ");
//            def queryParams = new HashMap();
//            queryParams.max = params.rows;
//            queryParams.offset = (Integer.parseInt(params.page)-1) * Integer.parseInt(params.rows);
//            def nameParams = new HashMap();
//
//            params.each { k,v ->
//                if('vipId'.equals(k) && v){
//                    query.append(" AND vsh.vipId = :vipId ")
//                    queryCount.append(" AND vsh.vipId = :vipId ")
//                    nameParams.vipId = v.asType(BigInteger)
//                }
//            }
//
//            def list = VipStoreHistory.executeQuery(query.toString(),nameParams,queryParams);
//            def count = VipStoreHistory.executeQuery(queryCount.toString(),nameParams);
//            map.put("total" , count.size() > 0 ? count[0] : 0);
//            map.put("rows" , list);
//        } catch (Exception e) {
//            LogUtil.logError("查询会员储值历史异常" + e.message);
//            throw new ServiceException();
//        }
//        return map;
//    }
//

    /**
     * 查询消费历史记录
     *
     * @param params
     */
    public ApiRest qryConsumeHistory(Map params){
        ApiRest rest = new ApiRest();
        if(params.get("vipId") != null && params.get("months") != null){
            List<ConsumeHistoryVO> historyVOList = new ArrayList<>();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) - Integer.parseInt(params.get("months").toString()));
            Map<String, Object> param = new HashMap<>();
            param.put("vipId", params.get("vipId"));
            List<VipTradeHistory> tradeList = vipTradeHistoryMapper.vipTradeList(param);
            for(VipTradeHistory t : tradeList){
                if(!"2".equals(t.getTradeType())){
                    ConsumeHistoryVO ch = new ConsumeHistoryVO();
                    ch.setVipId(t.getVipId().toString());
                    ch.setTradeNo(t.getTradeNo());
                    ch.setTradeType(t.getTradeType());
                    if(t.getAddScore() != null){
                        ch.setAddScore(t.getAddScore());
                    }
                    ch.setBranchId(t.getTradeBranchId());
                    if(t.getTradeBranchName() != null){
                        ch.setBranchName(t.getTradeBranchName());
                    }
                    else{
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("id", t.getTradeBranchId());
                        List<Branch> list = branchMapper.findBranchByTenantId(param1);
                        if(list != null && list.size() > 0){
                            ch.setBranchName(list.get(0).getName());
                        }
                    }
                    ch.setCreateAt(t.getCreateAt());
                    ch.setPayAmount(t.getPayAmount());
                    ch.setTradeAmount(t.getTradeAmount());
                    ch.setTradeDate(t.getCreateAt());
                    if(ch.getCreateAt().after(c.getTime())){
                        historyVOList.add(ch);
                    }
                }
            }
            List<VipStoreHistory> storeList = vipStoreHistoryMapper.vipStoreList(param);
            for(VipStoreHistory v : storeList){
                if("1".equals(v.getStoreType())){
                    ConsumeHistoryVO ch = new ConsumeHistoryVO();
                    ch.setVipId(v.getVipId().toString());
                    ch.setTradeNo(v.getStoreCode());
                    ch.setTradeType("4");
                    ch.setAddScore(new BigDecimal(0));
                    ch.setBranchId(v.getStoreBranchId());
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("id", v.getStoreBranchId());
                    List<Branch> list = branchMapper.findBranchByTenantId(param1);
                    if(list != null && list.size() > 0){
                        ch.setBranchName(list.get(0).getName());
                    }
                    ch.setCreateAt(v.getCreateAt());
                    ch.setPayAmount(v.getPayAmount());
                    ch.setTradeAmount(v.getPayAmount());
                    ch.setTradeDate(v.getCreateAt());
                    if(ch.getCreateAt().after(c.getTime())){
                        historyVOList.add(ch);
                    }
                }
            }
            Collections.sort(historyVOList);
            rest.setData(historyVOList);
            rest.setIsSuccess(true);
            rest.setMessage("查询消费历史成功!");
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("查询消费历史失败!");
        }
        return rest;
    }

    /**
     * 上传交易记录(微餐厅)
     *
     * @param params
     */
    public ApiRest uploadTradeHistory(Map params){
        ApiRest rest = new ApiRest();
        LogUtil.logInfo("微餐厅上传交易记录参数："+ BeanUtils.toJsonStr(params));
        if(params.get("totalValue") != null && params.get("vipId") != null && params.get("orderCode") != null){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("id", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(param1);
            if(vip != null){
                param1.put("id", vip.getTypeId());
                VipType vipType = vipTypeMapper.findByCondition(param1);
                double score = 0;
                VipTradeHistory vipTradeHistory = new VipTradeHistory();

                //记录消费历史
                vipTradeHistory.setVipId(vip.getId());
                vipTradeHistory.setTradeNo(params.get("orderCode").toString());
                vipTradeHistory.setOrderCode(params.get("orderCode").toString());
                vipTradeHistory.setTradeType("3");
                vipTradeHistory.setTradeAmount(new BigDecimal(params.get("totalValue").toString()));
                vipTradeHistory.setPayAmount(new BigDecimal(params.get("totalValue").toString()));
                if(params.get("scorePay") != null){
                    vipTradeHistory.setIntegralAmount(new BigDecimal(params.get("scorePay").toString()));
                }
                if(params.get("branchId") != null){
                    vipTradeHistory.setTradeBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    vipTradeHistory.setTradeBranchId(vip.getBranchId());
                }
                vipTradeHistory.setTradeDate(new Date());
                if(vipType != null){
                    //积分开启，则进行积分
                    if(vipType.getToSavePoints().intValue() == 1){
                        Map p = new HashMap();
                        p.put("tenantId", vip.getTenantId());
                        BigInteger branchId;
                        if(params.get("branchId") != null){
                            branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
                        }
                        else{
                            branchId = vip.getBranchId();
                        }
                        p.put("branchId", branchId);
                        p.put("promotionType", 2);
                        VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(p);
                        boolean isDouble = false;
                        boolean isVipBirthday = false;
                        if(null != vipSpecialPromotion){
                            if(vipSpecialPromotion.getIsDoubleScoreBirthday()!=null&&vipSpecialPromotion.getIsDoubleScoreBirthday() == 1){
                                isDouble = true;
                            }
                        }
                        SimpleDateFormat dateF = new SimpleDateFormat("MM-dd");
                        if(vip.getBirthday() != null && ((dateF.format(vip.getBirthday())).equals(dateF.format(new Date())))){
                            isVipBirthday = true;
                        }
                        if(vipType.getPointsFactor() != null){
                            score = vipTradeHistory.getTradeAmount().doubleValue() / vipType.getPointsFactor().doubleValue();
                        }
                        else{
                            score = vipTradeHistory.getTradeAmount().doubleValue() / 1;
                        }
                        score = (int)Math.floor(score);
                        if(isDouble && isVipBirthday){
                            score = score * 2;
                        }
                        score = score * vipService.getVipDayScoreTimes(vip.getTenantId(),branchId,new Date());
                        if(null != vip.getRemainingScore()){
                            vip.setRemainingScore(vip.getRemainingScore().add(new BigDecimal(score)));
                        }
                        else{
                            vip.setRemainingScore(new BigDecimal(score));
                        }
                        if(null != vip.getSumScore()){
                            vip.setSumScore(vip.getSumScore().add(new BigDecimal(score)));
                        }
                        else{
                            vip.setSumScore(new BigDecimal(score));
                        }
                        if(vip.getBuyTimes() != null){
                            vip.setBuyTimes(vip.getBuyTimes() + 1);
                        }
                        else{
                            vip.setBuyTimes(1);
                        }
                        if(vip.getSumConsume() != null){
                            vip.setSumConsume(vip.getSumConsume().add(new BigDecimal(params.get("totalValue").toString())));
                        }
                        else{
                            vip.setSumConsume(new BigDecimal(params.get("totalValue").toString()));
                        }
                    }
                }
                vipTradeHistory.setCreateAt(new Date());
                vipTradeHistory.setLastUpdateAt(new Date());
                vipTradeHistory.setAddScore(new BigDecimal(score));
                vipTradeHistory.setTenantId(vip.getTenantId());
                vipTradeHistoryMapper.insert(vipTradeHistory);
                vipMapper.update(vip);
                //记录积分台帐，当积分不为0的时候记录台账
                if(score!=0){
                    VipBook vipBook = new VipBook();
                    vipBook.setVipId(vip.getId());
                    vipBook.setTenantId(vip.getTenantId());
                    if(params.get("branchId") != null){
                        vipBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                    }
                    else{
                        vipBook.setBranchId(vip.getBranchId());
                    }
                    vipBook.setBookType(1);
                /*if(params.get("scorePay") != null){
                    vipBook.setBookType(2);
                }else{
                    vipBook.setBookType(1);
                }*/
                    vipBook.setOperateBy("微餐厅");
                    vipBook.setOperateAt(new Date());
                    vipBook.setVipCode(vip.getVipCode());
                    vipBook.setPaymentCode(params.get("orderCode").toString());
                /*if(params.get("scorePay") != null){
                    vipBook.setVipScore(BigDecimal.ZERO.subtract(new BigDecimal(params.get("scorePay").toString())));
                }else{
                    vipBook.setVipScore(new BigDecimal(score));
                }*/
                    vipBook.setVipScore(new BigDecimal(score));
                    vipBook.setSumConsume(new BigDecimal(params.get("totalValue").toString()));
                    vipBook.setTotal(new BigDecimal(params.get("totalValue").toString()));
                    vipBook.setRealTotal(new BigDecimal(params.get("totalValue").toString()));
                    vipBook.setCreateAt(new Date());
                    vipBook.setLastUpdateAt(new Date());
                    vipBook.setStoreFrom("1");
                /*if(params.get("scorePay") != null){
                    vipBook.setVipScore(vip.getRemainingScore().subtract(new BigDecimal(params.get("scorePay").toString())));
                }else{
                    vipBook.setTotalScore(vip.getRemainingScore().add(new BigDecimal(score)));
                }*/
                    vipBook.setTotalScore(vip.getRemainingScore());
                    vipBookMapper.insert(vipBook);

                    int intScore = Double.valueOf(score).intValue();
                    WeiXinUtils.updateMemberBonusSafe(vip, intScore, "消费积分变动");
                    WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), intScore, 1);
                    if(StringUtils.isNotEmpty(vip.getPhone())){
                        Map smsMap = new HashMap();
                        smsMap.put("type", 3);
                        smsMap.put("tenantId", vip.getTenantId().toString());
                        BigInteger branchId = null;
                        if(params.get("branchId") != null){
                            branchId = new BigInteger(params.get("branchId").toString());
                        }
                        else{
                            branchId = vip.getBranchId();
                        }

                        smsMap.put("number", vip.getPhone());
                        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                        if(branch != null){
                            DecimalFormat sd=new DecimalFormat("#");
                            smsMap.put("branchId", branchId.toString());
                            smsMap.put("content", "消费###"+(int)score+"###"+sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore())));
                            ApiRest ssr = smsService.sendSMS(smsMap);
                            if(!ssr.getIsSuccess()){
                                LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                            }
                        }
                    }
                }

            }

            rest.setIsSuccess(true);
            rest.setMessage("消费记录上传成功!");
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("总金额totalValue或openId不存在!");
        }
        return rest;
    }

//    public Map<String, Object> findVipStoreHistory(Map params) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        List<VipTradeHistoryVo> vipTradeHistoryVoArrayList = new ArrayList<VipTradeHistoryVo>();
//        String tenantId = params.tenantId;
//        try {
//            StringBuffer query = new StringBuffer("select vsh.store_code tradeNo,v.vip_name name,v.phone phone,v.vip_code vipCode,vsh.store_type tradeType,vsh.pay_amount tradeAmount," +
//                    "vsh.gift_amount payAmount,vsh.store_branch_name tradeBranchName,vsh.create_at tradeDate,vsh.create_by tradeUserName,vsh.store_branch_id branchId" +
//                    " from vip_store_history vsh left join vip v on vsh.vip_id=v.id where v.tenant_id=${tenantId}");
//            StringBuffer queryCount = new StringBuffer("select count(*) from vip_store_history vsh left join vip v on vsh.vip_id=v.id where v.tenant_id=${tenantId}");
//            if (params.startTime) {
//                String startTime = params.startTime;
//                query.append(" and vsh.create_at >= '${startTime}'");
//                queryCount.append(" and vsh.create_at >= '${startTime}'");
//            }
//            if (params.endTime) {
//                String endTime = params.endTime;
//                query.append(" and vsh.create_at <= '${endTime}'");
//                queryCount.append(" and vsh.create_at <= '${endTime}'");
//            }
//            if (params.branchId) {
//                BigInteger branchId = new BigInteger(params.branchId);
//                query.append(" and vsh.store_branch_id = ${branchId}");
//                queryCount.append(" and vsh.store_branch_id = ${branchId}");
//            }
//            if (params.phoneOrCode) {
//                String phoneOrCode =params.phoneOrCode;
//                query.append(" and (v.phone = ${phoneOrCode} or v.vip_code = ${phoneOrCode})");
//                queryCount.append(" and (v.phone = ${phoneOrCode} or v.vip_code = ${phoneOrCode})");
//            }
//            Integer max = params['rows']?.asType(int);
//            Integer page = params['page']?.asType(int);
//            def sq = getSession().createSQLQuery(query.toString());
//            def cq = getSession().createSQLQuery(queryCount.toString());
//            if (max != null && page != null) {//分页查询
//                sq.setMaxResults(max)
//                sq.setFirstResult((page - 1) * max)
//            }
//            def countNum = cq.list()[0]
//            def list = [];
//            if (countNum > 0) {
//                list = sq.list();
//            }
//            if (null != list && list.size() > 0) {
//                VipTradeHistoryVo vipTradeHistoryVo = null;
//                for (int i = 0; i < list.size(); i++) {
//                    vipTradeHistoryVo = new VipTradeHistoryVo();
//                    vipTradeHistoryVo.code = list[i][3];
//                    vipTradeHistoryVo.tradeNo = list[i][0];
//                    vipTradeHistoryVo.name = list[i][1];
//                    vipTradeHistoryVo.phone = list[i][2];
//                    vipTradeHistoryVo.tradeType = list[i][4];
//                    vipTradeHistoryVo.tradeAmount = list[i][5];
//                    vipTradeHistoryVo.payAmount = list[i][6];
//                    vipTradeHistoryVo.tradeBranchName = list[i][7];
//                    vipTradeHistoryVo.tradeDate = list[i][8];
//                    vipTradeHistoryVo.tradeUserName = list[i][9];
//                    vipTradeHistoryVo.branchId = list[i][10];
//                    if (!vipTradeHistoryVo.tradeBranchName) {
//                        if (list[i][10]) {
//                            Branch branch = Branch.findByIdAndIsDeleted(list[i][10], false);
//                            if (branch) {
//                                vipTradeHistoryVo.tradeBranchName = branch.name;
//                            }
//                        }
//                    }
//                    vipTradeHistoryVoArrayList.add(vipTradeHistoryVo);
//                }
//            }
//            map = ["total": countNum, "rows": vipTradeHistoryVoArrayList]
//        } catch (Exception e) {
//            LogUtil.logError("会员消费历史查询异常" + e.message);
//            throw new ServiceException();
//        }
//        return map
//    }

}

