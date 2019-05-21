package erp.chain.utils;

import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.Payment;
import erp.chain.domain.o2o.*;
import erp.chain.domain.online.OnlineDietOrderInfo;
import erp.chain.domain.online.OnlineGoodsPromotion;
import erp.chain.domain.online.OnlineVip;
import erp.chain.domain.online.OnlineVipType;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.service.o2o.VipStoreHistoryService;
import org.apache.commons.lang.ArrayUtils;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyandong on 2018-07-18.
 */
public class OrderUtils {
    private static VipStoreHistoryService vipStoreHistoryService;
    private static SerialNumberCreatorMapper serialNumberCreatorMapper;

    private static VipStoreHistoryService obtainVipStoreHistoryService() {
        if (vipStoreHistoryService == null) {
            vipStoreHistoryService = ApplicationHandler.getBean(VipStoreHistoryService.class);
        }
        return vipStoreHistoryService;
    }

    public static SerialNumberCreatorMapper obtainSerialNumberCreatorMapper() {
        if (serialNumberCreatorMapper == null) {
            serialNumberCreatorMapper = ApplicationHandler.getBean(SerialNumberCreatorMapper.class);
        }
        return serialNumberCreatorMapper;
    }

    public static void storePay(OnlineDietOrderInfo onlineDietOrderInfo, OnlineVip onlineVip, OnlineVipType onlineVipType) {
        BigInteger tenantId = onlineDietOrderInfo.getTenantId();
        BigInteger branchId = onlineDietOrderInfo.getBranchId();
        BigInteger vipId = onlineVip.getId();
        String orderCode = onlineDietOrderInfo.getOrderCode();
        BigDecimal receivedAmount = onlineDietOrderInfo.getReceivedAmount();
        Date payAt = onlineDietOrderInfo.getPayAt();

        Integer useScore = onlineDietOrderInfo.getUseScore();
        BigInteger useIntegral = useScore == null ? BigInteger.ZERO : BigInteger.valueOf(useScore);

        BigDecimal scorePay = onlineDietOrderInfo.getScorePay();
        BigDecimal integralAmount = scorePay == null ? BigDecimal.ZERO : scorePay;

        BigDecimal vipStore = onlineVip.getVipStore();
        ValidateUtils.isTrue(vipStore.compareTo(receivedAmount) >= 0, "储值余额不足！");

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("payment_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, "HYQB");

        Payment payment = null;
        int branchType = branch.getBranchType();
        if (branchType == 0) {
            payment = DatabaseHelper.find(Payment.class, searchModel);
        } else {
            payment = DatabaseHelper.find(Payment.class, "payment_branch", searchModel);
        }

        Date current = new Date();
        BigDecimal createScore = BigDecimal.ZERO;
        boolean isScore = payment.getPaymentStatus() == 0 && payment.getIsScore() && onlineVipType.getToSavePoints().compareTo(BigInteger.ONE) == 0;
        if (isScore) {
            BigDecimal pointsFactor = onlineVipType.getPointsFactor();
            createScore = receivedAmount.divide(pointsFactor, 0, BigDecimal.ROUND_DOWN);
            if (createScore.compareTo(BigDecimal.ZERO) > 0) {
                VipBook vipBook = new VipBook();
                vipBook.setTenantId(tenantId);
                vipBook.setBranchId(branchId);
                vipBook.setBookType(1);
                vipBook.setOperateAt(payAt);
                vipBook.setVipCode(vipBook.getVipCode());
                vipBook.setPaymentCode(orderCode);
                vipBook.setTotalScore(onlineVip.getRemainingScore());
                vipBook.setVipScore(createScore);
                vipBook.setSumConsume(receivedAmount);
                vipBook.setTotal(receivedAmount);
                vipBook.setRealTotal(receivedAmount);
                vipBook.setVipId(vipId);
                vipBook.setStoreFrom("1");
                vipBook.setCreateAt(current);
                vipBook.setLastUpdateAt(current);

                DatabaseHelper.insert(vipBook);

                onlineVip.setSumScore(onlineVip.getSumScore().add(createScore));
                onlineVip.setRemainingScore(onlineVip.getRemainingScore().add(createScore));
            }
        }
        VipTradeHistory vipTradeHistory = new VipTradeHistory();
        vipTradeHistory.setVipId(vipId);
        vipTradeHistory.setTradeNo(orderCode);
        vipTradeHistory.setTradeType("1");
        vipTradeHistory.setTradeAmount(receivedAmount);
        vipTradeHistory.setUseIntegral(useIntegral);
        vipTradeHistory.setIntegralAmount(integralAmount);
        vipTradeHistory.setPayAmount(receivedAmount);
        vipTradeHistory.setTradeBranchId(branchId);
        vipTradeHistory.setTradeBranchName(branch.getName());
        vipTradeHistory.setTradeDate(payAt);
        vipTradeHistory.setAddScore(createScore);
        vipTradeHistory.setTenantId(tenantId);
        vipTradeHistory.setOrderCode(orderCode);
        vipTradeHistory.setPaymentId(payment.getId());
        vipTradeHistory.setCreateAt(current);
        vipTradeHistory.setLastUpdateAt(current);
        DatabaseHelper.insert(vipTradeHistory);

        VipStoreHistory vipStoreHistory = new VipStoreHistory();
        vipStoreHistory.setVipId(vipId);
        vipStoreHistory.setStoreCode(orderCode);
        vipStoreHistory.setStoreType("3");
        vipStoreHistory.setPayAmount(receivedAmount);
        vipStoreHistory.setPayType("7");
        vipStoreHistory.setGiftAmount(BigDecimal.ZERO);
        vipStoreHistory.setStoreBranchId(branchId);
        vipStoreHistory.setStoreBranchName(branch.getName());
        vipStoreHistory.setStoreDate(current);
        vipStoreHistory.setCreateAt(current);
        vipStoreHistory.setLastUpdateAt(current);
        vipStoreHistory.setStoreFrom("0");
        vipStoreHistory.setVipOperStore(vipStore.subtract(receivedAmount));
        vipStoreHistory.setTenantId(tenantId);
        vipStoreHistory.setVersion(BigInteger.ZERO);

        DatabaseHelper.insert(vipStoreHistory);

        onlineVip.setVipStore(vipStore.subtract(receivedAmount));
        DatabaseHelper.update(onlineVip);
    }

    public static void scorePay(OnlineDietOrderInfo onlineDietOrderInfo, OnlineVip onlineVip) {
        BigInteger tenantId = onlineDietOrderInfo.getTenantId();
        BigInteger branchId = onlineDietOrderInfo.getBranchId();
        BigInteger vipId = onlineVip.getId();
        String orderCode = onlineDietOrderInfo.getOrderCode();
        BigDecimal receivedAmount = onlineDietOrderInfo.getReceivedAmount();
        Date payAt = onlineDietOrderInfo.getPayAt();
        BigDecimal useScore = BigDecimal.valueOf(onlineDietOrderInfo.getUseScore());

        VipBook vipBook = new VipBook();
        vipBook.setTenantId(tenantId);
        vipBook.setBranchId(branchId);
        vipBook.setBookType(2);
        vipBook.setOperateAt(payAt);
        vipBook.setVipCode(vipBook.getVipCode());
        vipBook.setPaymentCode(orderCode);
        vipBook.setTotalScore(onlineVip.getRemainingScore());
        vipBook.setVipScore(useScore);
        vipBook.setSumConsume(receivedAmount);
        vipBook.setTotal(receivedAmount);
        vipBook.setRealTotal(receivedAmount);
        vipBook.setVipId(vipId);
        vipBook.setStoreFrom("1");

        Date current = new Date();
        vipBook.setCreateAt(current);
        vipBook.setLastUpdateAt(current);

        DatabaseHelper.insert(vipBook);
        onlineVip.setRemainingScore(onlineVip.getRemainingScore().subtract(useScore));
        DatabaseHelper.update(onlineVip);
    }

    public static void weiXinPay(OnlineDietOrderInfo onlineDietOrderInfo, BigInteger vipId) {
        if (vipId == null) {
            return;
        }
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        if (onlineVip == null) {
            return;
        }
        OnlineVipType onlineVipType = DatabaseHelper.find(OnlineVipType.class, onlineVip.getTypeId());

        BigInteger tenantId = onlineDietOrderInfo.getTenantId();
        BigInteger branchId = onlineDietOrderInfo.getBranchId();
        String orderCode = onlineDietOrderInfo.getOrderCode();
        BigDecimal receivedAmount = onlineDietOrderInfo.getReceivedAmount();
        Date payAt = onlineDietOrderInfo.getPayAt();

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("payment_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, "WX");
        Payment payment = null;
        int branchType = branch.getBranchType();
        if (branchType == 0) {
            payment = DatabaseHelper.find(Payment.class, searchModel);
        } else {
            payment = DatabaseHelper.find(Payment.class, "payment_branch", searchModel);
        }

        Date current = new Date();
        boolean isScore = payment.getIsScore() && onlineVipType.getToSavePoints().compareTo(BigInteger.ONE) == 0;

        VipTradeHistory vipTradeHistory = new VipTradeHistory();
        if (isScore) {
            BigDecimal pointsFactor = onlineVipType.getPointsFactor();
            BigDecimal createScore = receivedAmount.divide(pointsFactor, 0, BigDecimal.ROUND_DOWN);
            if (createScore.compareTo(BigDecimal.ZERO) > 0) {
                VipBook vipBook = new VipBook();
                vipBook.setTenantId(tenantId);
                vipBook.setBranchId(branchId);
                vipBook.setBookType(1);
                vipBook.setOperateAt(payAt);
                vipBook.setVipCode(vipBook.getVipCode());
                vipBook.setPaymentCode(orderCode);
                vipBook.setTotalScore(onlineVip.getRemainingScore());
                vipBook.setVipScore(createScore);
                vipBook.setSumConsume(receivedAmount);
                vipBook.setTotal(receivedAmount);
                vipBook.setRealTotal(receivedAmount);
                vipBook.setVipId(vipId);
                vipBook.setStoreFrom("1");
                vipBook.setCreateAt(current);
                vipBook.setLastUpdateAt(current);

                DatabaseHelper.insert(vipBook);

                onlineVip.setSumScore(onlineVip.getSumScore().add(createScore));
                onlineVip.setRemainingScore(onlineVip.getRemainingScore().add(createScore));
                DatabaseHelper.update(onlineVip);
            }
            vipTradeHistory.setAddScore(createScore);
        } else {
            vipTradeHistory.setAddScore(BigDecimal.ZERO);
        }

        vipTradeHistory.setVipId(vipId);
        vipTradeHistory.setTradeNo(orderCode);
        vipTradeHistory.setTradeType("3");
        vipTradeHistory.setTradeAmount(receivedAmount);
        vipTradeHistory.setPayAmount(receivedAmount);
        vipTradeHistory.setIntegralAmount(onlineDietOrderInfo.getScorePay());
        vipTradeHistory.setTradeBranchId(branchId);
        vipTradeHistory.setTradeBranchName(branch.getName());
        vipTradeHistory.setTradeDate(current);
        vipTradeHistory.setCreateAt(current);
        vipTradeHistory.setLastUpdateAt(current);
        vipTradeHistory.setTenantId(tenantId);
        DatabaseHelper.insert(vipTradeHistory);
    }

    public static final DietOrderPromotionR buildDietOrderPromotionR(BigInteger orderId, String orderCode, BigDecimal actualSaveValue, OnlineGoodsPromotion onlineGoodsPromotion) {
        DietOrderPromotionR dietOrderPromotionR = new DietOrderPromotionR();
        dietOrderPromotionR.setOrderId(orderId);
        dietOrderPromotionR.setOrderCode(orderCode);
        dietOrderPromotionR.setPromotionCode(onlineGoodsPromotion.getPromotionCode());
        dietOrderPromotionR.setPromotionType(onlineGoodsPromotion.getPromotionType());
        dietOrderPromotionR.setScope(onlineGoodsPromotion.getScope());
        dietOrderPromotionR.setForCustomerType(onlineGoodsPromotion.getForCustomerType());
        dietOrderPromotionR.setMemGradeId(onlineGoodsPromotion.getMemGradeId());
        dietOrderPromotionR.setMemo(onlineGoodsPromotion.getMemo());
        dietOrderPromotionR.setPromotionStatus(onlineGoodsPromotion.getPromotionStatus());
        dietOrderPromotionR.setGoodsId(onlineGoodsPromotion.getGoodsId());
        dietOrderPromotionR.setBuyGoodsId(onlineGoodsPromotion.getBuyGoodsId());
        dietOrderPromotionR.setPackageId(onlineGoodsPromotion.getPackageId());
        dietOrderPromotionR.setGiveGoodsId(onlineGoodsPromotion.getGiveGoodsId());
        dietOrderPromotionR.setBuyNum(onlineGoodsPromotion.getBuyNum());
        dietOrderPromotionR.setGivePackageId(onlineGoodsPromotion.getGivePackageId());
        dietOrderPromotionR.setGiveNum(onlineGoodsPromotion.getGiveNum());
        dietOrderPromotionR.setSatisfy(onlineGoodsPromotion.getSatisfy());
        dietOrderPromotionR.setReduction(onlineGoodsPromotion.getReduction());
        dietOrderPromotionR.setQuantity(onlineGoodsPromotion.getQuantity());
        dietOrderPromotionR.setDiscount(onlineGoodsPromotion.getDiscount());
        dietOrderPromotionR.setActualSaveValue(actualSaveValue);
        return dietOrderPromotionR;
    }

    public static void refund(DietOrderInfo dietOrderInfo) throws ParseException {
        ValidateUtils.isTrue(dietOrderInfo.getPayStatus() == 1, "订单未付款！");

        int paidType = dietOrderInfo.getPaidType();
        ValidateUtils.isTrue(ArrayUtils.contains(new Integer[]{Constants.PAID_TYPE_ALIPAY, Constants.PAID_TYPE_WEI_XIN, Constants.PAID_TYPE_UM_PAY, Constants.PAID_TYPE_MIYA, Constants.PAID_TYPE_NEW_LAND, Constants.PAID_TYPE_VIP_STORE}, paidType), "该支付方式不支持退款！");

        BigInteger tenantId = dietOrderInfo.getTenantId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);
        Branch headquartersBranch = DatabaseHelper.find(Branch.class, searchModel);

        BigDecimal receivedAmount = dietOrderInfo.getReceivedAmount();
        String payOrderCode = dietOrderInfo.getPayOrderCode();
        String headquartersBranchId = headquartersBranch.getId().toString();

        BigInteger branchId = dietOrderInfo.getBranchId();

        if (paidType == Constants.PAID_TYPE_ALIPAY) {
            String refundNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + obtainSerialNumberCreatorMapper().getToday("TK", 4);

            Map<String, String> alipayRefundRequestParameters = new HashMap<String, String>();
            alipayRefundRequestParameters.put("tenantId", tenantId.toString());
            alipayRefundRequestParameters.put("branchId", branchId.toString());
            alipayRefundRequestParameters.put("pBranchId", headquartersBranchId);
            alipayRefundRequestParameters.put("out_trade_no", payOrderCode);
            alipayRefundRequestParameters.put("out_request_no", refundNo);
            alipayRefundRequestParameters.put("refund_amount", new DecimalFormat("#.##").format(receivedAmount));

            ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "pay", "alipayRefund", alipayRefundRequestParameters);
            ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

            saveRefundVipTradeHistory(dietOrderInfo, refundNo);
            dietOrderInfo.setRefundNo(refundNo);
        } else if (paidType == Constants.PAID_TYPE_WEI_XIN) {
            String refundNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + obtainSerialNumberCreatorMapper().getToday("TK", 4);
            String totalFee = String.valueOf(receivedAmount.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue());
            String refundFee = totalFee;

            Map<String, String> wxPayRefundRequestParameters = new HashMap<String, String>();
            wxPayRefundRequestParameters.put("tenantId", tenantId.toString());
            wxPayRefundRequestParameters.put("branchId", branchId.toString());
            wxPayRefundRequestParameters.put("pBranchId", headquartersBranchId);
            wxPayRefundRequestParameters.put("out_trade_no", payOrderCode);
            wxPayRefundRequestParameters.put("out_refund_no", refundNo);
            wxPayRefundRequestParameters.put("total_fee", totalFee);
            wxPayRefundRequestParameters.put("refund_fee", refundFee);
            wxPayRefundRequestParameters.put("op_user_id", "-1");

            ApiRest apiRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "pay", "wxpayRefund", wxPayRefundRequestParameters);
            ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

            saveRefundVipTradeHistory(dietOrderInfo, refundNo);
            dietOrderInfo.setRefundNo(refundNo);
        } else if (paidType == Constants.PAID_TYPE_VIP_STORE) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
            Map<String, Object> params = new HashMap<>();
            params.put("vipId", dietOrderInfo.getVipId());
            params.put("requestTime", simpleDateFormat.format(new Date()));
            params.put("amount", receivedAmount);
            params.put("branchId", branchId);

            ApiRest apiRest = obtainVipStoreHistoryService().vipStoreCorrectForPos(params);
            ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());
        }
    }

    private static void saveRefundVipTradeHistory(DietOrderInfo dietOrderInfo, String refundNo) {
        Date now = new Date();
        VipTradeHistory vipTradeHistory = new VipTradeHistory();
        vipTradeHistory.setVipId(dietOrderInfo.getVipId());
        vipTradeHistory.setTradeNo(refundNo);
        vipTradeHistory.setTradeType("4");
        vipTradeHistory.setTradeAmount(dietOrderInfo.getReceivedAmount());
        vipTradeHistory.setTradeBranchId(dietOrderInfo.getBranchId());
        vipTradeHistory.setTradeDate(now);
        vipTradeHistory.setCreateAt(now);
        vipTradeHistory.setLastUpdateAt(now);
        vipTradeHistory.setTenantId(dietOrderInfo.getTenantId());
        DatabaseHelper.insert(vipTradeHistory);
    }

    public static void refundSafe(DietOrderInfo dietOrderInfo) {
        try {
            refund(dietOrderInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
