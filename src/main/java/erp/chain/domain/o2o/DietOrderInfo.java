package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/1/21.
 */
public class DietOrderInfo extends BaseDomain {
    /**
     * 机构ID（外键）
     */
    BigInteger branchId;
    /**
     * 机构名称
     */
    String branchName;
    /**
     * 订单类型（1：堂食；2：店内自提；3:预约点餐；4：外卖）
     */
    Integer orderMode;
    /**
     * 订单编号
     */
    String orderCode;
    /**
     * 会员信息表ID（外键）
     */
    BigInteger vipId;
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
     */
    Integer orderStatus;
    /**
     * 就餐状态(0:未就餐;1:正在就餐;2:已就餐)外卖不填写
     */
    Integer eatStatus;
    /**
     * 支付状态(0:未支付;1:已支付)
     */
    Integer payStatus;
    /**
     * 应付金额
     */
    BigDecimal totalAmount;
    /**
     * 实际支付金额
     */
    BigDecimal receivedAmount;
    /**
     * 使用的积分数
     */
    Integer useScore;
    /**
     * 数量
     */
    Integer amount;
    /**
     * 是否使用优惠券
     */
    boolean isUsePrivilege;
    /**
     * 支付时间
     */
    Date payAt;
    /**
     * 产生积分数
     */
    Integer createScore;
    /**
     * 就餐人数
     */
    Integer eatPeople;
    /**
     * 预约时间，如果不是预约类型的订单，则不填写
     */
    Date appointmentDate;
    /**
     * 收货人姓名
     */
    String consignee;
    /**
     * 收货人电话
     */
    String mobilePhone;
    /**
     * 客户外卖时填写的配送时间
     */
    Date allocationDate;
    /**
     * 外卖送达时间
     */
    Date arriveDate;
    /**
     * 订单备注信息
     */
    String remark;
    /**
     * 是否免单
     */
    boolean isFreeOfCharge;
    /**
     * pos编码
     */
    String posCode;
    /**
     * 收银员
     */
    String cashier;
    /**
     * 开台时间
     */
    Date tableOpenAt;
    /**
     * 是否冲销
     */
    boolean isRefund;
    /**
     * 会员外卖地址表ID（外键）
     */
    BigInteger vipAddressId;
    /**
     * 会员外卖地址表name
     */
    String vipAddressName;
    /**
     * 支付方式 1:微信支付 2:支付宝支付 3:货到付款
     */
    Integer payWay;
    /**
     * 会员openId(pos用2016/2/19 by wangms)
     */
    String vipOpenid;
    /**
     * 积分抵付金额
     */
    BigDecimal scorePay;

    String sessionStr;

    /**
     * 优惠券抵付金额
     */
    BigDecimal cardValue;
    /**
     * 桌台码
     */
    String tableCode;

    /**
     * 使用的卡券id
     */
    BigInteger usedCardId;

    /**
     * 订单来源
     */
    Integer orderResource;
    /**
     * token验证，用于防止重复提交。
     */
    String token;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;
    /**
     * 美团当日的订单流水号（小票配送序号）
     */
    String meituanDaySeq;
    String meituanRefundReason;

    String dispatcherName;
    String dispatcherMobile;
    /**
     * 店铺实收
     */
    private BigDecimal income;
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

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

    public String getMeituanRefundReason() {
        return meituanRefundReason;
    }

    public void setMeituanRefundReason(String meituanRefundReason) {
        this.meituanRefundReason = meituanRefundReason;
    }

    /**
     * 配送费
     */
    private BigDecimal deliverFee;

    /**
     * 打包费
     */
    private BigDecimal packageFee;
    private Integer elemeOrderStatus;
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

    /**
     * 新大陆支付渠道订单号
     */
    private String officeId;

    public String getMeituanDaySeq() {
        return meituanDaySeq;
    }

    public void setMeituanDaySeq(String meituanDaySeq) {
        this.meituanDaySeq = meituanDaySeq;
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

    public Integer getOrderResource() {
        return orderResource;
    }

    public void setOrderResource(Integer orderResource) {
        this.orderResource = orderResource;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public Integer getElemeOrderStatus() {
        return elemeOrderStatus;
    }

    public void setElemeOrderStatus(Integer elemeOrderStatus) {
        this.elemeOrderStatus = elemeOrderStatus;
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

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
