package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-08.
 */
@Table(name = "diet_order_info")
public class OnlineDietOrderInfo {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 机构ID（外键）
     */
    private BigInteger branchId;
    /**
     * 机构名称
     */
    private String branchName;
    /**
     * 订单类型（1：堂食；2：店内自提；3:预约点餐；4：外卖，5：外卖自取 6美团外卖 7 饿了么外卖，8-自助购，9-积分商城兑换优惠券订单，10-积分商城兑换商品订单）
     */
    private Integer orderMode;
    /**
     * 订单编号
     */
    private String orderCode;
    /**
     * 会员信息表ID（外键）
     */
    private BigInteger vipId;
    /**
     * 订单状态
     * 1: 已下单;
     * 2: 已接单,提交厨房;
     * 3: 正在派送;
     * 4: 已派送;<外卖状态>;
     * 5: 已上菜;
     * 6: 已结束;
     * 7: 已自提<堂食及自提状态>;
     * 8: 排队中;
     * 9: 已拒绝;
     * 10: 用户撤单;
     * 11: 配送失败;
     * 12: 配送取消或物流拒单;
     * 20: 已核验
     * 21: 已取消
     * 22: 已支付
     * 23：已发货
     */
    private Integer orderStatus;
    /**
     * 就餐状态(0:未就餐;1:正在就餐;2:已就餐)外卖不填写
     */
    private Integer eatStatus;
    /**
     * 支付状态(0:未支付;1:已支付;2-已退款)
     */
    private Integer payStatus;
    /**
     * 应付金额
     */
    private BigDecimal totalAmount;
    /**
     * 实际支付金额
     */
    private BigDecimal receivedAmount;
    /**
     * 使用的积分数
     */
    private Integer useScore;
    /**
     * 数量
     */
    private Integer amount;
    /**
     * 是否使用优惠券
     */
    private boolean isUsePrivilege;
    /**
     * 支付时间
     */
    private Date payAt;
    /**
     * 产生积分数
     */
    private Integer createScore;
    /**
     * 就餐人数
     */
    private Integer eatPeople;
    /**
     * 预约时间，如果不是预约类型的订单，则不填写
     */
    private Date appointmentDate;
    /**
     * 收货人姓名
     */
    private String consignee;
    /**
     * 收货人电话
     */
    private String mobilePhone;
    /**
     * 客户外卖时填写的配送时间
     */
    private Date allocationDate;
    /**
     * 外卖送达时间
     */
    private Date arriveDate;
    /**
     * 订单备注信息
     */
    private String remark;
    /**
     * 是否免单
     */
    private boolean isFreeOfCharge;
    /**
     * pos编码
     */
    private String posCode;
    /**
     * 收银员
     */
    private String cashier;
    /**
     * 开台时间
     */
    private Date tableOpenAt;
    /**
     * 是否冲销
     */
    private boolean isRefund;
    /**
     * 创建时间
     */
    private Date createAt;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 最后更新时间
     */
    private Date lastUpdateAt;
    /**
     * 最后更新人
     */
    private String lastUpdateBy;
    /**
     * 是否删除
     */
    private boolean isDeleted;
    /**
     * 会员外卖地址表ID（外键）
     */
    private BigInteger vipAddressId;
    /**
     * 会员外卖地址表name
     */
    private String vipAddressName;
    /**
     * 支付方式 1:微信支付 2:支付宝支付 3:货到付款
     */
    private Integer payWay;
    /**
     * 订单来源
     */
    private Integer orderResource;
    /**
     * 会员openId(pos用2016/2/19 by wangms)
     */
    private String vipOpenid;
    /**
     * 积分抵付金额
     */
    private BigDecimal scorePay;
    /**
     * session id
     */
    private String sessionStr;

    /**
     * 优惠券抵付金额
     */
    private BigDecimal cardValue;
    /**
     * 桌台码
     */
    private String tableCode;

    /**
     * 使用的卡券id
     */
    private BigInteger usedCardId;
    private BigInteger version;
    private BigInteger localId;
    /**
     * 美团当日的订单流水号（小票配送序号）
     */
    private String meituanDaySeq;
    /**
     * 饿了么订单状态
     */
    private Integer elemeOrderStatus;
    /**
     * token验证，用于防止重复提交。
     */
    private String token;
    /**
     * 配送费
     */
    private BigDecimal deliverFee;

    /**
     * 打包费
     */
    private BigDecimal packageFee;
    /**
     * 美团退款原因
     */
    private String meituanRefundReason;
    /**
     * 配送员姓名
     */
    private String dispatcherName;
    /**
     * 配送员联系电话
     */
    private String dispatcherMobile;
    /**
     * 店铺实收
     */
    private BigDecimal income;
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    private BigDecimal scoreTotalAmount;

    private BigDecimal scoreReceivedAmount;

    private BigDecimal scoreDiscountAmount;

    private Integer confirmOrderSource = 0;

    /**
     * 支付方式，1-支付宝支付，2-微信支付，3联动微信支付，4-米雅，5-新大陆，6-储值
     */
    private Integer paidType;
    /**
     * 支付订单号
     */
    private String payOrderCode;
    /**
     * 退款单号
     */
    private String refundNo;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(Integer orderMode) {
        this.orderMode = orderMode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getEatStatus() {
        return eatStatus;
    }

    public void setEatStatus(Integer eatStatus) {
        this.eatStatus = eatStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Integer getUseScore() {
        return useScore;
    }

    public void setUseScore(Integer useScore) {
        this.useScore = useScore;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public boolean getIsUsePrivilege() {
        return isUsePrivilege;
    }

    public void setIsUsePrivilege(boolean isUsePrivilege) {
        this.isUsePrivilege = isUsePrivilege;
    }

    public Date getPayAt() {
        return payAt;
    }

    public void setPayAt(Date payAt) {
        this.payAt = payAt;
    }

    public Integer getCreateScore() {
        return createScore;
    }

    public void setCreateScore(Integer createScore) {
        this.createScore = createScore;
    }

    public Integer getEatPeople() {
        return eatPeople;
    }

    public void setEatPeople(Integer eatPeople) {
        this.eatPeople = eatPeople;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public Date getTableOpenAt() {
        return tableOpenAt;
    }

    public void setTableOpenAt(Date tableOpenAt) {
        this.tableOpenAt = tableOpenAt;
    }

    public boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigInteger getVipAddressId() {
        return vipAddressId;
    }

    public void setVipAddressId(BigInteger vipAddressId) {
        this.vipAddressId = vipAddressId;
    }

    public String getVipAddressName() {
        return vipAddressName;
    }

    public void setVipAddressName(String vipAddressName) {
        this.vipAddressName = vipAddressName;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public Integer getOrderResource() {
        return orderResource;
    }

    public void setOrderResource(Integer orderResource) {
        this.orderResource = orderResource;
    }

    public String getVipOpenid() {
        return vipOpenid;
    }

    public void setVipOpenid(String vipOpenid) {
        this.vipOpenid = vipOpenid;
    }

    public BigDecimal getScorePay() {
        return scorePay;
    }

    public void setScorePay(BigDecimal scorePay) {
        this.scorePay = scorePay;
    }

    public String getSessionStr() {
        return sessionStr;
    }

    public void setSessionStr(String sessionStr) {
        this.sessionStr = sessionStr;
    }

    public BigDecimal getCardValue() {
        return cardValue;
    }

    public void setCardValue(BigDecimal cardValue) {
        this.cardValue = cardValue;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public BigInteger getUsedCardId() {
        return usedCardId;
    }

    public void setUsedCardId(BigInteger usedCardId) {
        this.usedCardId = usedCardId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public String getMeituanDaySeq() {
        return meituanDaySeq;
    }

    public void setMeituanDaySeq(String meituanDaySeq) {
        this.meituanDaySeq = meituanDaySeq;
    }

    public Integer getElemeOrderStatus() {
        return elemeOrderStatus;
    }

    public void setElemeOrderStatus(Integer elemeOrderStatus) {
        this.elemeOrderStatus = elemeOrderStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigDecimal getDeliverFee() {
        return deliverFee;
    }

    public void setDeliverFee(BigDecimal deliverFee) {
        this.deliverFee = deliverFee;
    }

    public BigDecimal getPackageFee() {
        return packageFee;
    }

    public void setPackageFee(BigDecimal packageFee) {
        this.packageFee = packageFee;
    }

    public String getMeituanRefundReason() {
        return meituanRefundReason;
    }

    public void setMeituanRefundReason(String meituanRefundReason) {
        this.meituanRefundReason = meituanRefundReason;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }

    public String getDispatcherMobile() {
        return dispatcherMobile;
    }

    public void setDispatcherMobile(String dispatcherMobile) {
        this.dispatcherMobile = dispatcherMobile;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getScoreTotalAmount() {
        return scoreTotalAmount;
    }

    public void setScoreTotalAmount(BigDecimal scoreTotalAmount) {
        this.scoreTotalAmount = scoreTotalAmount;
    }

    public BigDecimal getScoreReceivedAmount() {
        return scoreReceivedAmount;
    }

    public void setScoreReceivedAmount(BigDecimal scoreReceivedAmount) {
        this.scoreReceivedAmount = scoreReceivedAmount;
    }

    public BigDecimal getScoreDiscountAmount() {
        return scoreDiscountAmount;
    }

    public void setScoreDiscountAmount(BigDecimal scoreDiscountAmount) {
        this.scoreDiscountAmount = scoreDiscountAmount;
    }

    public Integer getConfirmOrderSource() {
        return confirmOrderSource;
    }

    public void setConfirmOrderSource(Integer confirmOrderSource) {
        this.confirmOrderSource = confirmOrderSource;
    }

    public Integer getPaidType() {
        return paidType;
    }

    public void setPaidType(Integer paidType) {
        this.paidType = paidType;
    }

    public String getPayOrderCode() {
        return payOrderCode;
    }

    public void setPayOrderCode(String payOrderCode) {
        this.payOrderCode = payOrderCode;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public static class Builder {
        private final OnlineDietOrderInfo instance = new OnlineDietOrderInfo();

        public Builder id(BigInteger id) {
            instance.setId(id);
            return this;
        }

        public Builder tenantId(BigInteger tenantId) {
            instance.setTenantId(tenantId);
            return this;
        }

        public Builder branchId(BigInteger branchId) {
            instance.setBranchId(branchId);
            return this;
        }

        public Builder branchName(String branchName) {
            instance.setBranchName(branchName);
            return this;
        }

        public Builder orderMode(Integer orderMode) {
            instance.setOrderMode(orderMode);
            return this;
        }

        public Builder orderCode(String orderCode) {
            instance.setOrderCode(orderCode);
            return this;
        }

        public Builder vipId(BigInteger vipId) {
            instance.setVipId(vipId);
            return this;
        }

        public Builder orderStatus(Integer orderStatus) {
            instance.setOrderStatus(orderStatus);
            return this;
        }

        public Builder eatStatus(Integer eatStatus) {
            instance.setEatStatus(eatStatus);
            return this;
        }

        public Builder payStatus(Integer payStatus) {
            instance.setPayStatus(payStatus);
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            instance.setTotalAmount(totalAmount);
            return this;
        }

        public Builder receivedAmount(BigDecimal receivedAmount) {
            instance.setReceivedAmount(receivedAmount);
            return this;
        }

        public Builder useScore(Integer useScore) {
            instance.setUseScore(useScore);
            return this;
        }

        public Builder amount(Integer amount) {
            instance.setAmount(amount);
            return this;
        }

        public Builder isUsePrivilege(boolean isUsePrivilege) {
            instance.setIsUsePrivilege(isUsePrivilege);
            return this;
        }

        public Builder payAt(Date payAt) {
            instance.setPayAt(payAt);
            return this;
        }

        public Builder createScore(Integer createScore) {
            instance.setCreateScore(createScore);
            return this;
        }

        public Builder eatPeople(Integer eatPeople) {
            instance.setEatPeople(eatPeople);
            return this;
        }

        public Builder appointmentDate(Date appointmentDate) {
            instance.setAppointmentDate(appointmentDate);
            return this;
        }

        public Builder consignee(String consignee) {
            instance.setConsignee(consignee);
            return this;
        }

        public Builder mobilePhone(String mobilePhone) {
            instance.setMobilePhone(mobilePhone);
            return this;
        }

        public Builder allocationDate(Date allocationDate) {
            instance.setAllocationDate(allocationDate);
            return this;
        }

        public Builder arriveDate(Date arriveDate) {
            instance.setArriveDate(arriveDate);
            return this;
        }

        public Builder remark(String remark) {
            instance.setRemark(remark);
            return this;
        }

        public Builder isFreeOfCharge(boolean isFreeOfCharge) {
            instance.setIsFreeOfCharge(isFreeOfCharge);
            return this;
        }

        public Builder posCode(String posCode) {
            instance.setPosCode(posCode);
            return this;
        }

        public Builder cashier(String cashier) {
            instance.setCashier(cashier);
            return this;
        }

        public Builder tableOpenAt(Date tableOpenAt) {
            instance.setTableOpenAt(tableOpenAt);
            return this;
        }

        public Builder isRefund(boolean isRefund) {
            instance.setIsRefund(isRefund);
            return this;
        }

        public Builder createAt(Date createAt) {
            instance.setCreateAt(createAt);
            return this;
        }

        public Builder createBy(String createBy) {
            instance.setCreateBy(createBy);
            return this;
        }

        public Builder lastUpdateAt(Date lastUpdateAt) {
            instance.setLastUpdateAt(lastUpdateAt);
            return this;
        }

        public Builder lastUpdateBy(String lastUpdateBy) {
            instance.setLastUpdateBy(lastUpdateBy);
            return this;
        }

        public Builder isDeleted(boolean isDeleted) {
            instance.setIsDeleted(isDeleted);
            return this;
        }

        public Builder vipAddressId(BigInteger vipAddressId) {
            instance.setVipAddressId(vipAddressId);
            return this;
        }

        public Builder vipAddressName(String vipAddressName) {
            instance.setVipAddressName(vipAddressName);
            return this;
        }

        public Builder payWay(Integer payWay) {
            instance.setPayWay(payWay);
            return this;
        }

        public Builder orderResource(Integer orderResource) {
            instance.setOrderResource(orderResource);
            return this;
        }

        public Builder vipOpenid(String vipOpenid) {
            instance.setVipOpenid(vipOpenid);
            return this;
        }

        public Builder scorePay(BigDecimal scorePay) {
            instance.setScorePay(scorePay);
            return this;
        }

        public Builder sessionStr(String sessionStr) {
            instance.setSessionStr(sessionStr);
            return this;
        }

        public Builder cardValue(BigDecimal cardValue) {
            instance.setCardValue(cardValue);
            return this;
        }

        public Builder tableCode(String tableCode) {
            instance.setTableCode(tableCode);
            return this;
        }

        public Builder usedCardId(BigInteger usedCardId) {
            instance.setUsedCardId(usedCardId);
            return this;
        }

        public Builder version(BigInteger version) {
            instance.setVersion(version);
            return this;
        }

        public Builder localId(BigInteger localId) {
            instance.setLocalId(localId);
            return this;
        }

        public Builder meituanDaySeq(String meituanDaySeq) {
            instance.setMeituanDaySeq(meituanDaySeq);
            return this;
        }

        public Builder elemeOrderStatus(Integer elemeOrderStatus) {
            instance.setElemeOrderStatus(elemeOrderStatus);
            return this;
        }

        public Builder token(String token) {
            instance.setToken(token);
            return this;
        }

        public Builder deliverFee(BigDecimal deliverFee) {
            instance.setDeliverFee(deliverFee);
            return this;
        }

        public Builder packageFee(BigDecimal packageFee) {
            instance.setPackageFee(packageFee);
            return this;
        }

        public Builder meituanRefundReason(String meituanRefundReason) {
            instance.setMeituanRefundReason(meituanRefundReason);
            return this;
        }

        public Builder dispatcherName(String dispatcherName) {
            instance.setDispatcherName(dispatcherName);
            return this;
        }

        public Builder dispatcherMobile(String dispatcherMobile) {
            instance.setDispatcherMobile(dispatcherMobile);
            return this;
        }

        public Builder income(BigDecimal income) {
            instance.setIncome(income);
            return this;
        }

        public Builder discountAmount(BigDecimal discountAmount) {
            instance.setDiscountAmount(discountAmount);
            return this;
        }

        public Builder scoreTotalAmount(BigDecimal scoreTotalAmount) {
            instance.setScoreTotalAmount(scoreTotalAmount);
            return this;
        }

        public Builder scoreReceivedAmount(BigDecimal scoreReceivedAmount) {
            instance.setScoreReceivedAmount(scoreReceivedAmount);
            return this;
        }

        public Builder scoreDiscountAmount(BigDecimal scoreDiscountAmount) {
            instance.setScoreDiscountAmount(scoreDiscountAmount);
            return this;
        }

        public Builder confirmOrderSource(Integer confirmOrderSource) {
            instance.setConfirmOrderSource(confirmOrderSource);
            return this;
        }

        public Builder paidType(Integer paidType) {
            instance.setPaidType(paidType);
            return this;
        }

        public Builder payOrderCode(String payOrderCode) {
            instance.setPayOrderCode(payOrderCode);
            return this;
        }

        public Builder refundNo(String refundNo) {
            instance.setRefundNo(refundNo);
            return this;
        }

        public OnlineDietOrderInfo build() {
            OnlineDietOrderInfo onlineDietOrderInfo = new OnlineDietOrderInfo();
            onlineDietOrderInfo.setId(instance.getId());
            onlineDietOrderInfo.setTenantId(instance.getTenantId());
            onlineDietOrderInfo.setBranchId(instance.getBranchId());
            onlineDietOrderInfo.setBranchName(instance.getBranchName());
            onlineDietOrderInfo.setOrderMode(instance.getOrderMode());
            onlineDietOrderInfo.setOrderCode(instance.getOrderCode());
            onlineDietOrderInfo.setVipId(instance.getVipId());
            onlineDietOrderInfo.setOrderStatus(instance.getOrderStatus());
            onlineDietOrderInfo.setEatStatus(instance.getEatStatus());
            onlineDietOrderInfo.setPayStatus(instance.getPayStatus());
            onlineDietOrderInfo.setTotalAmount(instance.getTotalAmount());
            onlineDietOrderInfo.setReceivedAmount(instance.getReceivedAmount());
            onlineDietOrderInfo.setUseScore(instance.getUseScore());
            onlineDietOrderInfo.setAmount(instance.getAmount());
            onlineDietOrderInfo.setIsUsePrivilege(instance.getIsUsePrivilege());
            onlineDietOrderInfo.setPayAt(instance.getPayAt());
            onlineDietOrderInfo.setCreateScore(instance.getCreateScore());
            onlineDietOrderInfo.setEatPeople(instance.getEatPeople());
            onlineDietOrderInfo.setAppointmentDate(instance.getAppointmentDate());
            onlineDietOrderInfo.setConsignee(instance.getConsignee());
            onlineDietOrderInfo.setMobilePhone(instance.getMobilePhone());
            onlineDietOrderInfo.setAllocationDate(instance.getAllocationDate());
            onlineDietOrderInfo.setArriveDate(instance.getArriveDate());
            onlineDietOrderInfo.setRemark(instance.getRemark());
            onlineDietOrderInfo.setIsFreeOfCharge(instance.getIsFreeOfCharge());
            onlineDietOrderInfo.setPosCode(instance.getPosCode());
            onlineDietOrderInfo.setCashier(instance.getCashier());
            onlineDietOrderInfo.setTableOpenAt(instance.getTableOpenAt());
            onlineDietOrderInfo.setIsRefund(instance.getIsRefund());
            onlineDietOrderInfo.setCreateAt(instance.getCreateAt());
            onlineDietOrderInfo.setCreateBy(instance.getCreateBy());
            onlineDietOrderInfo.setLastUpdateAt(instance.getLastUpdateAt());
            onlineDietOrderInfo.setLastUpdateBy(instance.getLastUpdateBy());
            onlineDietOrderInfo.setIsDeleted(instance.getIsDeleted());
            onlineDietOrderInfo.setVipAddressId(instance.getVipAddressId());
            onlineDietOrderInfo.setVipAddressName(instance.getVipAddressName());
            onlineDietOrderInfo.setPayWay(instance.getPayWay());
            onlineDietOrderInfo.setOrderResource(instance.getOrderResource());
            onlineDietOrderInfo.setVipOpenid(instance.getVipOpenid());
            onlineDietOrderInfo.setScorePay(instance.getScorePay());
            onlineDietOrderInfo.setSessionStr(instance.getSessionStr());
            onlineDietOrderInfo.setCardValue(instance.getCardValue());
            onlineDietOrderInfo.setTableCode(instance.getTableCode());
            onlineDietOrderInfo.setUsedCardId(instance.getUsedCardId());
            onlineDietOrderInfo.setVersion(instance.getVersion());
            onlineDietOrderInfo.setLocalId(instance.getLocalId());
            onlineDietOrderInfo.setMeituanDaySeq(instance.getMeituanDaySeq());
            onlineDietOrderInfo.setElemeOrderStatus(instance.getElemeOrderStatus());
            onlineDietOrderInfo.setToken(instance.getToken());
            onlineDietOrderInfo.setDeliverFee(instance.getDeliverFee());
            onlineDietOrderInfo.setPackageFee(instance.getPackageFee());
            onlineDietOrderInfo.setMeituanRefundReason(instance.getMeituanRefundReason());
            onlineDietOrderInfo.setDispatcherName(instance.getDispatcherName());
            onlineDietOrderInfo.setDispatcherMobile(instance.getDispatcherMobile());
            onlineDietOrderInfo.setIncome(instance.getIncome());
            onlineDietOrderInfo.setDiscountAmount(instance.getDiscountAmount());
            onlineDietOrderInfo.setScoreTotalAmount(instance.getScoreTotalAmount());
            onlineDietOrderInfo.setScoreReceivedAmount(instance.getScoreReceivedAmount());
            onlineDietOrderInfo.setScoreDiscountAmount(instance.getScoreDiscountAmount());
            onlineDietOrderInfo.setConfirmOrderSource(instance.getConfirmOrderSource());
            onlineDietOrderInfo.setPaidType(instance.getPaidType());
            onlineDietOrderInfo.setPayOrderCode(instance.getPayOrderCode());
            onlineDietOrderInfo.setRefundNo(instance.getRefundNo());
            return onlineDietOrderInfo;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
