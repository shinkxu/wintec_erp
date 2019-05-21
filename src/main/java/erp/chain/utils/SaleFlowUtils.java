package erp.chain.utils;

import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.Payment;
import erp.chain.domain.o2o.CardToVip;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.domain.online.OnlineDietOrderDetail;
import erp.chain.domain.online.OnlineSale;
import erp.chain.domain.online.OnlineSaleDetail;
import erp.chain.domain.online.OnlineSalePayment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by liuyandong on 2018-05-22.
 */
public class SaleFlowUtils {
    public static void writeSaleFlow(DietOrderInfo dietOrderInfo) {
        Date date = new Date();
        BigInteger tenantId = dietOrderInfo.getTenantId();
        BigInteger branchId = dietOrderInfo.getBranchId();
        int transTerminal = -1;
        int saleMode = -1;
        int orderMode = dietOrderInfo.getOrderMode();
        if (orderMode == 0) {
            transTerminal = 4;
            saleMode = 0;
        } else if (orderMode == 1) {
            transTerminal = 4;
            saleMode = 0;
        } else if (orderMode == 2) {
            transTerminal = 5;
            saleMode = 2;
        } else if (orderMode == 3) {
            transTerminal = 4;
            saleMode = 0;
        } else if (orderMode == 4) {
            transTerminal = 6;
            saleMode = 1;
        } else if (orderMode == 5) {
            transTerminal = 5;
            saleMode = 2;
        } else if (orderMode == 6) {
            transTerminal = 9;
            saleMode = 1;
        } else if (orderMode == 7) {
            transTerminal = 8;
            saleMode = 1;
        }

        List<String> paymentCodeCodes = new ArrayList<String>();
        BigDecimal scorePay = dietOrderInfo.getScorePay() == null ? BigDecimal.ZERO : dietOrderInfo.getScorePay();
        if (scorePay.compareTo(BigDecimal.ZERO) > 0) {
            paymentCodeCodes.add("JF");
        }


        BigDecimal cardValue = BigDecimal.ZERO;
        BigInteger usedCardId = dietOrderInfo.getUsedCardId();
        if (usedCardId != null) {
            CardToVip cardToVip = DatabaseHelper.find(CardToVip.class, usedCardId);
            int cardType = cardToVip.getCardType().intValue();
            if (cardType == 1) {
                cardValue = cardToVip.getFaceValue();
            } else if (cardType == 3) {
                cardValue = dietOrderInfo.getTotalAmount().multiply(cardToVip.getDiscount()).divide(Constants.BIG_DECIMAL_ONE_HUNDRED);
            }
        }
        if (cardValue.compareTo(BigDecimal.ZERO) > 0) {
            paymentCodeCodes.add("YHQ");
        }

        BigDecimal totalAmount = dietOrderInfo.getTotalAmount();
        BigDecimal discountAmount = null;
        BigDecimal receivedAmount = null;
        BigDecimal income = dietOrderInfo.getIncome();
        if (orderMode == 6) {
            discountAmount = dietOrderInfo.getDiscountAmount() != null ? dietOrderInfo.getDiscountAmount() : BigDecimal.ZERO;
            receivedAmount = totalAmount.subtract(discountAmount);
        } else if (orderMode == 7) {
            discountAmount = dietOrderInfo.getDiscountAmount() != null ? dietOrderInfo.getDiscountAmount() : BigDecimal.ZERO;
            receivedAmount = totalAmount.subtract(discountAmount);
        } else {
            receivedAmount = dietOrderInfo.getReceivedAmount().add(scorePay).add(cardValue);
            discountAmount = dietOrderInfo.getDiscountAmount().subtract(scorePay).subtract(cardValue);
        }
        BigDecimal longAmount = receivedAmount.subtract(scorePay).subtract(cardValue);

        boolean hasLongAmount = false;
        if (longAmount.compareTo(BigDecimal.ZERO) < 0) {
            hasLongAmount = true;
        } else {
            String paymentCode = null;
            int payWay = dietOrderInfo.getPayWay();
            if (payWay == 1) {
                paymentCode = "WX";
            } else if (payWay == 2) {
                paymentCode = "ZFB";
            } else if (payWay == 3) {

            } else if (payWay == 4) {
                paymentCode = "HYQB";
            } else if (payWay == 5) {

            }
            if (StringUtils.isNotBlank(paymentCode)) {
                paymentCodeCodes.add(paymentCode);
            }
            if (orderMode == 6) {
                paymentCodeCodes.add("MT");
                addThirdPartyTakeoutPayment(tenantId, "MT", "美团线上支付");
            } else if (orderMode == 7) {
                paymentCodeCodes.add("ELM");
                addThirdPartyTakeoutPayment(tenantId, "ELM", "饿了么线上支付");
            }
        }

        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        paymentSearchModel.addSearchCondition("payment_code", Constants.SQL_OPERATION_SYMBOL_IN, paymentCodeCodes);
        List<Payment> payments = DatabaseHelper.findAll(Payment.class, paymentSearchModel);

        Employee employee = obtainDefaultOnlineCashier(tenantId, branchId);

        Map<String, Payment> paymentMap = new HashMap<String, Payment>();
        for (Payment payment : payments) {
            paymentMap.put(payment.getPaymentCode(), payment);
        }

        OnlineSale onlineSale = new OnlineSale();
        onlineSale.setTenantId(tenantId);
        onlineSale.setBranchId(branchId);

        String saleCode = dietOrderInfo.getOrderCode();
        onlineSale.setSaleCode(saleCode);
        onlineSale.setSaleMode(saleMode);
        onlineSale.setTotalAmount(totalAmount);
        onlineSale.setDiscountAmount(discountAmount);
        onlineSale.setGiveAmount(BigDecimal.ZERO);
        onlineSale.setTruncAmount(BigDecimal.ZERO);
        onlineSale.setIsFreeOfCharge(false);
        onlineSale.setServiceFee(BigDecimal.ZERO);
        onlineSale.setReceivedAmount(receivedAmount);
        onlineSale.setCashier(employee.getId());
        onlineSale.setCheckoutAt(dietOrderInfo.getPayAt());
        onlineSale.setIsRefund(false);
        onlineSale.setOrderStatus(2);
        onlineSale.setDeliveryStatus(0);
        onlineSale.setCreateAt(dietOrderInfo.getPayAt());
        onlineSale.setLastUpdateAt(date);
        onlineSale.setIsDeleted(false);
        onlineSale.setSaleType(1);
        onlineSale.setIncome(income);
        if (hasLongAmount) {
            onlineSale.setLongAmount(longAmount.abs());
        } else {
            onlineSale.setLongAmount(BigDecimal.ZERO);
        }
        onlineSale.setLocalSign(dietOrderInfo.getId() + "-" + dietOrderInfo.getOrderCode());
        onlineSale.setVipId(dietOrderInfo.getVipId());
        onlineSale.setTransTerminal(transTerminal);
        onlineSale.setPayType(2);
        onlineSale.setBoxPrice(dietOrderInfo.getPackageFee());
        DatabaseHelper.insert(onlineSale);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderInfo.getId());
        List<OnlineDietOrderDetail> onlineDietOrderDetails = DatabaseHelper.findAll(OnlineDietOrderDetail.class, searchModel);

        Map<String, List<OnlineDietOrderDetail>> packageDetailDietOrderDetailListMap = new HashMap<String, List<OnlineDietOrderDetail>>();
        List<OnlineDietOrderDetail> ordinaryCommodityOnlineDietOrderDetails = new ArrayList<OnlineDietOrderDetail>();

//        BigDecimal shouldShareDiscountAmount = new BigDecimal(discountAmount.toString());
        BigInteger packageFeeGoodsId = BigInteger.valueOf(-9999);
        BigDecimal otherExpenses = dietOrderInfo.getPackageFee() != null ? dietOrderInfo.getPackageFee() : BigDecimal.ZERO;
        otherExpenses = otherExpenses.add(dietOrderInfo.getDeliverFee() != null ? dietOrderInfo.getDeliverFee() : BigDecimal.ZERO);
        for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
            /*if (onlineDietOrderDetail.getIsFreeOfCharge()) {
                shouldShareDiscountAmount = shouldShareDiscountAmount.subtract(onlineDietOrderDetail.getTotalAmount());
            }*/
            BigInteger goodsId = onlineDietOrderDetail.getGoodsId();
            if (goodsId.compareTo(packageFeeGoodsId) == 0) {
                if (orderMode != 7) {
                    otherExpenses = otherExpenses.add(onlineDietOrderDetail.getTotalAmount());
                }
                continue;
            }

            OnlineDietOrderDetail cloneOnlineDietOrderDetail = ApplicationHandler.clone(OnlineDietOrderDetail.class, onlineDietOrderDetail);
            boolean isPackage = onlineDietOrderDetail.getIsPackage();
            if (isPackage) {
                if (goodsId.compareTo(BigInteger.ZERO) == 0) {
                    ordinaryCommodityOnlineDietOrderDetails.add(cloneOnlineDietOrderDetail);
                } else {
                    String packageCode = onlineDietOrderDetail.getPackageCode();
                    List<OnlineDietOrderDetail> onlineDietOrderDetailList = packageDetailDietOrderDetailListMap.get(packageCode);
                    if (CollectionUtils.isEmpty(onlineDietOrderDetailList)) {
                        onlineDietOrderDetailList = new ArrayList<OnlineDietOrderDetail>();
                        packageDetailDietOrderDetailListMap.put(packageCode, onlineDietOrderDetailList);
                    }
                    onlineDietOrderDetailList.add(cloneOnlineDietOrderDetail);
                }
            } else {
                ordinaryCommodityOnlineDietOrderDetails.add(cloneOnlineDietOrderDetail);
            }
        }

        // 开始计算分摊
        calculatedShare(otherExpenses, receivedAmount, discountAmount, income, ordinaryCommodityOnlineDietOrderDetails);
        for (OnlineDietOrderDetail onlineDietOrderDetail : ordinaryCommodityOnlineDietOrderDetails) {
            if (onlineDietOrderDetail.getIsPackage()) {
                calculatedShare(onlineDietOrderDetail.getOtherExpenses(), onlineDietOrderDetail.getTotalAmount().add(onlineDietOrderDetail.getOtherExpenses()).subtract(onlineDietOrderDetail.getDiscountAmount()), onlineDietOrderDetail.getDiscountAmount(), onlineDietOrderDetail.getIncome(), packageDetailDietOrderDetailListMap.get(onlineDietOrderDetail.getPackageCode()));
            }
        }

        List<OnlineSaleDetail> onlineSaleDetails = new ArrayList<OnlineSaleDetail>();
        for (OnlineDietOrderDetail onlineDietOrderDetail : ordinaryCommodityOnlineDietOrderDetails) {
            OnlineSaleDetail onlineSaleDetail = buildOnlineSaleDetail(employee, saleCode, tenantId, branchId, dietOrderInfo.getPayAt(), dietOrderInfo.getVipId(), transTerminal, onlineDietOrderDetail);
            onlineSaleDetails.add(onlineSaleDetail);
        }

        for (Map.Entry<String, List<OnlineDietOrderDetail>> entry : packageDetailDietOrderDetailListMap.entrySet()) {
            List<OnlineDietOrderDetail> onlineDietOrderDetailList = entry.getValue();
            for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetailList) {
                OnlineSaleDetail onlineSaleDetail = buildOnlineSaleDetail(employee, saleCode, tenantId, branchId, dietOrderInfo.getPayAt(), dietOrderInfo.getVipId(), transTerminal, onlineDietOrderDetail);
                onlineSaleDetails.add(onlineSaleDetail);
            }
        }

        DatabaseHelper.insertAll(onlineSaleDetails);


        List<OnlineSalePayment> onlineSalePayments = new ArrayList<OnlineSalePayment>();
        for (String paymentCode : paymentCodeCodes) {
            Payment payment = paymentMap.get(paymentCode);
            OnlineSalePayment onlineSalePayment = null;
            if ("JF".equals(paymentCode)) {
                onlineSalePayment = buildOnlineSalePayment(employee, saleCode, paymentCode, payment, scorePay, scorePay, dietOrderInfo.getPayAt(), tenantId, branchId, false, false, dietOrderInfo.getVipId(), 1, transTerminal, 2, income);
            } else if ("YHQ".equals(paymentCode)) {
                onlineSalePayment = buildOnlineSalePayment(employee, saleCode, paymentCode, payment, cardValue, cardValue, dietOrderInfo.getPayAt(), tenantId, branchId, false, false, dietOrderInfo.getVipId(), 1, transTerminal, 2, income);
            } else {
                onlineSalePayment = buildOnlineSalePayment(employee, saleCode, paymentCode, payment, longAmount, longAmount, dietOrderInfo.getPayAt(), tenantId, branchId, false, false, dietOrderInfo.getVipId(), 1, transTerminal, 2, income);
            }
            onlineSalePayments.add(onlineSalePayment);
        }

        if (hasLongAmount) {
            OnlineSalePayment onlineSalePayment = buildOnlineSalePayment(employee, saleCode, "YHQ", paymentMap.get("YHQ"), longAmount, longAmount, dietOrderInfo.getPayAt(), tenantId, branchId, false, true, dietOrderInfo.getVipId(), 1, transTerminal, 2, income);
            onlineSalePayments.add(onlineSalePayment);
        }

        DatabaseHelper.insertAll(onlineSalePayments);
    }

    private static OnlineSalePayment buildOnlineSalePayment(Employee employee, String saleCode, String paymentCode, Payment payment, BigDecimal payTotal, BigDecimal amount, Date payAt, BigInteger tenantId, BigInteger branchId, boolean refund, boolean longAmount, BigInteger vipId, int saleType, int transTerminal, int payType, BigDecimal income) {
        OnlineSalePayment onlineSalePayment = new OnlineSalePayment();
        onlineSalePayment.setSalePaymentCode("1");
        onlineSalePayment.setSaleCode(saleCode);
        if (payment != null) {
            onlineSalePayment.setPaymentId(payment.getId());
            onlineSalePayment.setPaymentCode(payment.getPaymentCode());
        } else {
            onlineSalePayment.setPaymentId(BigInteger.ZERO);
            onlineSalePayment.setPaymentCode(paymentCode);
        }
        onlineSalePayment.setPayTotal(payTotal);
        onlineSalePayment.setAmount(amount);
        onlineSalePayment.setCashier(employee.getId());
        onlineSalePayment.setPaymentAt(payAt);
        onlineSalePayment.setCreateAt(payAt);
        onlineSalePayment.setLastUpdateAt(new Date());
        onlineSalePayment.setTenantId(tenantId);
        onlineSalePayment.setBranchId(branchId);
        onlineSalePayment.setRefund(refund);
        onlineSalePayment.setLocalSign(UUID.randomUUID().toString());
        onlineSalePayment.setLongAmount(longAmount);
        onlineSalePayment.setVipId(vipId);
        onlineSalePayment.setSaleType(saleType);
        onlineSalePayment.setTransTerminal(transTerminal);
        onlineSalePayment.setPayType(payType);
        onlineSalePayment.setIncome(income);
        return onlineSalePayment;
    }

    public static void writeSaleFlow(BigInteger dietOrderInfoId) {
        DietOrderInfo dietOrderInfo = DatabaseHelper.find(DietOrderInfo.class, dietOrderInfoId);
        ValidateUtils.notNull(dietOrderInfo, "订单不存在！");
        writeSaleFlow(dietOrderInfo);
    }

    /**
     * 计算分摊
     *
     * @param receivedAmount
     * @param onlineDietOrderDetails
     */
    public static void calculatedShare(BigDecimal otherExpenses, BigDecimal receivedAmount, BigDecimal discountAmount, BigDecimal income, List<OnlineDietOrderDetail> onlineDietOrderDetails) {
        BigDecimal receivedAmountTotalShare = BigDecimal.ZERO;
        BigDecimal discountAmountTotalShare = BigDecimal.ZERO;
        BigDecimal otherExpensesTotalShare = BigDecimal.ZERO;
        BigDecimal incomeTotalShare = BigDecimal.ZERO;

        income = income != null ? income : BigDecimal.ZERO;

        Collections.sort(onlineDietOrderDetails, new Comparator<OnlineDietOrderDetail>() {
            @Override
            public int compare(OnlineDietOrderDetail o1, OnlineDietOrderDetail o2) {
                return o1.getTotalAmount().compareTo(o2.getTotalAmount());
            }
        });
        BigDecimal denominator = obtainDenominator(onlineDietOrderDetails);
        int size = onlineDietOrderDetails.size();
        for (int index = 0; index < size; index++) {
            OnlineDietOrderDetail onlineDietOrderDetail = onlineDietOrderDetails.get(index);
            /*if (onlineDietOrderDetail.getIsFreeOfCharge()) {
                onlineDietOrderDetail.setReceivedAmount(BigDecimal.ZERO);
                onlineDietOrderDetail.setDiscountAmount(BigDecimal.ZERO);
                onlineDietOrderDetail.setOtherExpenses(BigDecimal.ZERO);
                onlineDietOrderDetail.setIncome(BigDecimal.ZERO);
                continue;
            }*/

            if (index == size - 1) {
                onlineDietOrderDetail.setReceivedAmount(receivedAmount.subtract(receivedAmountTotalShare));
                onlineDietOrderDetail.setDiscountAmount(discountAmount.subtract(discountAmountTotalShare));
                onlineDietOrderDetail.setOtherExpenses(otherExpenses.subtract(otherExpensesTotalShare));
                onlineDietOrderDetail.setIncome(income.subtract(incomeTotalShare));
            } else {
                BigDecimal weight = onlineDietOrderDetail.getTotalAmount().divide(denominator, 10, BigDecimal.ROUND_DOWN);
                BigDecimal receivedAmountShare = receivedAmount.multiply(weight).setScale(2, BigDecimal.ROUND_DOWN);
                onlineDietOrderDetail.setReceivedAmount(receivedAmountShare);
                receivedAmountTotalShare = receivedAmountTotalShare.add(receivedAmountShare);

                BigDecimal discountAmountShare = discountAmount.multiply(weight).setScale(2, BigDecimal.ROUND_DOWN);
                onlineDietOrderDetail.setDiscountAmount(discountAmountShare);
                discountAmountTotalShare = discountAmountTotalShare.add(discountAmountShare);

                BigDecimal otherExpensesShare = otherExpenses.multiply(weight).setScale(2, BigDecimal.ROUND_DOWN);
                onlineDietOrderDetail.setOtherExpenses(otherExpensesShare);
                otherExpensesTotalShare = otherExpensesTotalShare.add(otherExpensesShare);

                BigDecimal incomeShare = income.multiply(weight).setScale(2, BigDecimal.ROUND_DOWN);
                onlineDietOrderDetail.setIncome(incomeShare);
                incomeTotalShare = incomeTotalShare.add(incomeShare);
            }
        }
    }

    public static BigDecimal obtainDenominator(List<OnlineDietOrderDetail> onlineDietOrderDetails) {
        BigDecimal denominator = BigDecimal.ZERO;
        for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
            /*if (!onlineDietOrderDetail.getIsFreeOfCharge()) {
                denominator = denominator.add(onlineDietOrderDetail.getTotalAmount());
            }*/
            denominator = denominator.add(onlineDietOrderDetail.getTotalAmount());
        }
        return denominator;
    }

    public static OnlineSaleDetail buildOnlineSaleDetail(Employee employee, String saleCode, BigInteger tenantId, BigInteger branchId, Date payAt, BigInteger vipId, int transTerminal, OnlineDietOrderDetail onlineDietOrderDetail) {
        OnlineSaleDetail onlineSaleDetail = new OnlineSaleDetail();
        onlineSaleDetail.setSaleCode(saleCode);
        onlineSaleDetail.setPackageId(onlineDietOrderDetail.getPackageId() != null ? onlineDietOrderDetail.getPackageId() : BigInteger.ZERO);
        onlineSaleDetail.setGoodsId(onlineDietOrderDetail.getGoodsId());
        onlineSaleDetail.setIsPackage(onlineDietOrderDetail.getIsPackage());
        onlineSaleDetail.setPromotionId(null);
        onlineSaleDetail.setQuantity(onlineDietOrderDetail.getQuantity());
        onlineSaleDetail.setSalePrice(onlineDietOrderDetail.getSalePrice());
        onlineSaleDetail.setSalePriceActual(onlineDietOrderDetail.getSalePriceActual());
        onlineSaleDetail.setTotalAmount(onlineDietOrderDetail.getTotalAmount().add(onlineDietOrderDetail.getOtherExpenses()));
        onlineSaleDetail.setIsFreeOfCharge(false);

        boolean isPackageDetail = onlineSaleDetail.getPackageId().compareTo(BigInteger.ZERO) != 0 && onlineDietOrderDetail.getGoodsId().compareTo(BigInteger.ZERO) != 0;
        if (isPackageDetail) {
            onlineSaleDetail.setReceivedAmount(onlineDietOrderDetail.getReceivedAmount());
        } else {
            onlineSaleDetail.setReceivedAmount(onlineSaleDetail.getTotalAmount().subtract(onlineDietOrderDetail.getDiscountAmount()));
        }
        onlineSaleDetail.setIsRefund(onlineDietOrderDetail.getIsRefund());
        onlineSaleDetail.setIsPrinted(false);
        onlineSaleDetail.setCreateAt(payAt);
        onlineSaleDetail.setLastUpdateAt(new Date());
        onlineSaleDetail.setIsDeleted(false);
        onlineSaleDetail.setTenantId(tenantId);
        onlineSaleDetail.setBranchId(branchId);
        onlineSaleDetail.setDiscountAmount(onlineDietOrderDetail.getDiscountAmount());
        onlineSaleDetail.setPayAt(payAt);
        onlineSaleDetail.setLocalSign(onlineDietOrderDetail.getId() + "-" + UUID.randomUUID().toString());
        onlineSaleDetail.setProduced(false);
        onlineSaleDetail.setServed(false);
        onlineSaleDetail.setSaleType(1);
        onlineSaleDetail.setVipId(vipId);
        onlineSaleDetail.setCashier(employee.getId());
        onlineSaleDetail.setTransTerminal(transTerminal);
        onlineSaleDetail.setPayType(2);
        onlineSaleDetail.setIncome(onlineDietOrderDetail.getIncome());

        return onlineSaleDetail;
    }

    public static void addThirdPartyTakeoutPayment(BigInteger tenantId, String paymentCode, String paymentName) {
        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("branch_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);

        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        paymentSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branch.getId());
        paymentSearchModel.addSearchCondition("payment_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, paymentCode);
        Payment payment = DatabaseHelper.find(Payment.class, paymentSearchModel);
        if (payment == null) {
            payment = new Payment();
            payment.setTenantId(tenantId);
            payment.setBranchId(branch.getId());
            payment.setPaymentCode(paymentCode);
            payment.setPaymentName(paymentName);
            payment.setPaymentStatus(1);
            payment.setCurrencyId(BigInteger.ZERO);
            payment.setIsDeleted(false);
            payment.setCreateAt(new Date());
            payment.setLastUpdateAt(new Date());
            DatabaseHelper.insert(payment);
        }
    }

    public static Employee obtainDefaultOnlineCashier(BigInteger tenantId, BigInteger branchId) {
        SearchModel employeeSearchModel = new SearchModel(true);
        employeeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        employeeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        employeeSearchModel.addSearchCondition("code", Constants.SQL_OPERATION_SYMBOL_EQUALS, "00000");
        Employee employee = DatabaseHelper.find(Employee.class, employeeSearchModel);
        if (employee == null) {
            employee = new Employee();
            employee.setTenantId(tenantId);
            employee.setBranchId(branchId);
            employee.setUserId(BigInteger.ZERO);
            employee.setCode("00000");
            employee.setName("线上收款员");
            employee.setSex(1);
            employee.setIsDeleted(false);
            employee.setSex(1);
            employee.setPricePrivilege(2);
            employee.setCreateAt(new Date());
            employee.setLastUpdateAt(new Date());
            employee.setCreateBy("线上订单写流水自动创建默认收款员");
            employee.setLastUpdateBy("线上订单写流水自动创建默认收款员");
            DatabaseHelper.insert(employee);
        }
        return employee;
    }
}
