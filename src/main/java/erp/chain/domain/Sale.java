package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Sale extends BaseDomain {

    /**
     * 销售账单号
     */
    String saleCode;
    /**
     * 销售方式
     0-堂食
     1-外卖
     2-自提
     */
    Integer saleMode;
    /**
     * 桌号
     0-无桌号销售
     */
    BigInteger tableId;
    /**
     *
     */
    BigInteger posId;
    /**
     * POS号(冗余字段)
     */
    String posCode;
    /**
     * 销售合计
     （所有单品计价的合计）
     */
    BigDecimal totalAmount;
    /**
     * 折扣额
     */
    BigDecimal discountAmount;
    /**
     * 赠送额
     */
    BigDecimal giveAmount;
    /**
     * 抹零额
     */
    BigDecimal truncAmount;
    /**
     * 是否免单
     */
    boolean isFreeOfCharge;
    /**
     * 所有服务费的合计，属于加收项目
     包括座位费、加工费等
     单项加工费另表保存
     */
    BigDecimal serviceFee;
    /**
     * 实收金额
     扣除各种折扣，加上各种按单加价的费用，扣除找零、抹零后的实际收到的金额
     */
    BigDecimal receivedAmount;
    /**
     * 订台人
     */
    String orderAttendant;
    /**
     * 值台人
     */
    String serviceAttendant;
    /**
     * 开台时间
     */
    Date tableOpenAt;
    /**
     * 开台人
     */
    String openAttendant;
    /**
     * 收银员
     */
    BigInteger cashier;
    /**
     * 结账时间
     */
    Date checkoutAt;
    /**
     * 促销活动
     */
    BigInteger promotionId;
    /**
     * 是否冲销
     */
    Integer isRefund;
    /**
     * 订单状态
     0-录入
     1-已提交
     2-（卖方）已确认
     3-（卖方）已拒绝
     4-已支付
     5-已取消
     */
    Integer orderStatus;
    /**
     * 0-未发货
     1-已发货
     2-已收货
     */
    Integer deliveryStatus;
    /**
     *
     */
    String saleOrderCode;
    /**
     * 订单类型：0-pos订单，1-o2o订单
     */
    Integer saleType;
    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;

    /**
     * 长款
     */
    BigDecimal longAmount;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger tenantId;

    BigInteger clientId;
    BigInteger localId;
    BigDecimal changeAmount;
    String localSign;
    BigInteger vipId;
    BigInteger guideId;
    Integer transTerminal=1;
    Integer payType=1;

    public Integer getTransTerminal(){
        return transTerminal;
    }

    public void setTransTerminal(Integer transTerminal){
        this.transTerminal = transTerminal;
    }

    public Integer getPayType(){
        return payType;
    }

    public void setPayType(Integer payType){
        this.payType = payType;
    }

    public BigInteger getGuideId(){
        return guideId;
    }

    public void setGuideId(BigInteger guideId){
        this.guideId = guideId;
    }

    public BigInteger getVipId(){
        return vipId;
    }

    public void setVipId(BigInteger vipId){
        this.vipId = vipId;
    }

    public String getLocalSign(){
        return localSign;
    }

    public void setLocalSign(String localSign){
        this.localSign = localSign;
    }

    public Sale() {
        super();
    }

    public Sale(Map domainMap) {
        super(domainMap);
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public Integer getSaleMode() {
        return saleMode;
    }

    public void setSaleMode(Integer saleMode) {
        this.saleMode = saleMode;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
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

    public boolean isFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setFreeOfCharge(boolean freeOfCharge) {
        isFreeOfCharge = freeOfCharge;
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

    public Integer isRefund() {
        return isRefund;
    }

    public void setRefund(Integer refund) {
        isRefund = refund;
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

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getLongAmount() {
        return longAmount;
    }

    public void setLongAmount(BigDecimal longAmount) {
        this.longAmount = longAmount;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getClientId() {
        return clientId;
    }

    public void setClientId(BigInteger clientId) {
        this.clientId = clientId;
    }
}
