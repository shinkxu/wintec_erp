package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-05-07.
 */
@Table(name = "sale")
public class OnlineSale {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * POS数据主键ID
     */
    private BigInteger clientId;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 所属机构，默认为0，代表总部
     */
    private BigInteger branchId;
    /**
     * 销售账单号
     */
    private String saleCode;
    /**
     * POS ID
     */
    private BigInteger posId;
    /**
     * 销售方式0-堂食 1-外卖 2-自提
     */
    private Integer saleMode;
    /**
     * POS号(冗余字段)
     */
    private String posCode;
    /**
     * 桌号 0-无桌号销售
     */
    private BigInteger tableId;
    /**
     * 销售合计
     （所有单品计价的合计）
     */
    private BigDecimal totalAmount;
    /**
     * 折扣额
     */
    private BigDecimal discountAmount;
    /**
     * 赠送额
     */
    private BigDecimal giveAmount;
    /**
     * 抹零额
     */
    private BigDecimal truncAmount;
    /**
     * 是否免单
     */
    private boolean isFreeOfCharge;
    /**
     * 所有服务费的合计，属于加收项目
     包括座位费、加工费等
     单项加工费另表保存
     */
    private BigDecimal serviceFee;
    /**
     * 实收金额
     扣除各种折扣，加上各种按单加价的费用，扣除找零、抹零后的实际收到的金额
     */
    private BigDecimal receivedAmount;
    /**
     * 订台人
     */
    private String orderAttendant;
    /**
     * 值台人
     */
    private String serviceAttendant;
    /**
     * 开台时间
     */
    private Date tableOpenAt;
    /**
     * 开台人
     */
    private String openAttendant;
    /**
     * 收银员
     */
    private BigInteger cashier;
    /**
     * 结账时间
     */
    private Date checkoutAt;
    /**
     * 促销活动
     */
    private BigInteger promotionId;
    /**
     * 是否冲销
     */
    private boolean isRefund;
    /**
     * 订单状态 0-录入 1-已提交 2-（卖方）已确认 3-（卖方）已拒绝 4-已支付 5-已取消
     */
    private Integer orderStatus;
    /**
     * 0-未发货 1-已发货 2-已收货
     */
    private Integer deliveryStatus;
    /**
     *
     */
    private String saleOrderCode;
    /**
     * 订单类型：0-pos订单，1-o2o订单
     */
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    /**
     * 流水类型0-pos  1-o2o
     */
    private Integer saleType;
    /**
     * 长款
     */
    private BigDecimal longAmount;
    /**
     * 收营员登录时间
     */
    private Date loginTime;
    /**
     *
     */
    private BigInteger localId;
    /**
     * pos本地用找零金额
     */
    private BigDecimal changeAmount;
    private String localSign;
    private BigInteger vipId;
    /**
     * 交易终端1-pos2-点菜宝3-H5扫码点餐4-微餐厅扫码点餐5-微餐厅到店自提6-微餐厅外卖点餐7-电话外卖8-第三方订单
     */
    private Integer transTerminal;
    /**
     * 1-pos支付2-自助支付
     */
    private Integer payType;
    /**
     * 导购员ID
     */
    private BigInteger guideId;
    /**
     * 餐盒费（包装费）
     */
    private BigDecimal boxPrice;
    /**
     * 店铺实际收入
     */
    private BigDecimal income;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getClientId() {
        return clientId;
    }

    public void setClientId(BigInteger clientId) {
        this.clientId = clientId;
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

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }

    public Integer getSaleMode() {
        return saleMode;
    }

    public void setSaleMode(Integer saleMode) {
        this.saleMode = saleMode;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGiveAmount() {
        return giveAmount;
    }

    public void setGiveAmount(BigDecimal giveAmount) {
        this.giveAmount = giveAmount;
    }

    public BigDecimal getTruncAmount() {
        return truncAmount;
    }

    public void setTruncAmount(BigDecimal truncAmount) {
        this.truncAmount = truncAmount;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getOrderAttendant() {
        return orderAttendant;
    }

    public void setOrderAttendant(String orderAttendant) {
        this.orderAttendant = orderAttendant;
    }

    public String getServiceAttendant() {
        return serviceAttendant;
    }

    public void setServiceAttendant(String serviceAttendant) {
        this.serviceAttendant = serviceAttendant;
    }

    public Date getTableOpenAt() {
        return tableOpenAt;
    }

    public void setTableOpenAt(Date tableOpenAt) {
        this.tableOpenAt = tableOpenAt;
    }

    public String getOpenAttendant() {
        return openAttendant;
    }

    public void setOpenAttendant(String openAttendant) {
        this.openAttendant = openAttendant;
    }

    public BigInteger getCashier() {
        return cashier;
    }

    public void setCashier(BigInteger cashier) {
        this.cashier = cashier;
    }

    public Date getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(Date checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public BigInteger getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(BigInteger promotionId) {
        this.promotionId = promotionId;
    }

    public boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getSaleOrderCode() {
        return saleOrderCode;
    }

    public void setSaleOrderCode(String saleOrderCode) {
        this.saleOrderCode = saleOrderCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public BigDecimal getLongAmount() {
        return longAmount;
    }

    public void setLongAmount(BigDecimal longAmount) {
        this.longAmount = longAmount;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getLocalSign() {
        return localSign;
    }

    public void setLocalSign(String localSign) {
        this.localSign = localSign;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public Integer getTransTerminal() {
        return transTerminal;
    }

    public void setTransTerminal(Integer transTerminal) {
        this.transTerminal = transTerminal;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigInteger getGuideId() {
        return guideId;
    }

    public void setGuideId(BigInteger guideId) {
        this.guideId = guideId;
    }

    public BigDecimal getBoxPrice() {
        return boxPrice;
    }

    public void setBoxPrice(BigDecimal boxPrice) {
        this.boxPrice = boxPrice;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
