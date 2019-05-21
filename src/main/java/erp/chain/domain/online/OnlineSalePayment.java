package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-05-07.
 */
@Table(name = "sale_payment")
public class OnlineSalePayment {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 付款单号
     */
    private String salePaymentCode;
    /**
     * 销售流水号
     */
    private String saleCode;
    /**
     * 支付方式
     */
    private BigInteger paymentId;
    /**
     * 支付代码
     */
    private String paymentCode;
    /**
     * 应付金额
     */
    private BigDecimal payTotal;
    /**
     * 实付金额
     */
    private BigDecimal amount;
    /**
     *
     */
    private BigInteger posId;
    /**
     * 找零金额
     */
    private BigDecimal changeAmount;
    /**
     *
     */
    private String memo;
    /**
     * 收银员
     */
    private BigInteger cashier;
    /**
     * 付款时间
     */
    private Date paymentAt;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    private BigInteger tenantId;
    /**
     * 所属机构，默认为0，代表总部
     */
    private BigInteger branchId;
    /**
     * 是否退货
     */
    private boolean isRefund;
    private BigInteger localId;
    private String localSign;
    /**
     * 是否长款0-否1-是
     */
    private boolean isLongAmount;

    /**
     * 团购券号、银行卡号等
     */
    private String otherCode;
    /**
     * 会员ID
     */
    private BigInteger vipId;
    /**
     * 流水类型0或null为线下，1为线上
     */
    private Integer saleType;
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
     * 店铺实际收入
     */
    private BigDecimal income;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getSalePaymentCode() {
        return salePaymentCode;
    }

    public void setSalePaymentCode(String salePaymentCode) {
        this.salePaymentCode = salePaymentCode;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(BigDecimal payTotal) {
        this.payTotal = payTotal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getCashier() {
        return cashier;
    }

    public void setCashier(BigInteger cashier) {
        this.cashier = cashier;
    }

    public Date getPaymentAt() {
        return paymentAt;
    }

    public void setPaymentAt(Date paymentAt) {
        this.paymentAt = paymentAt;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean refund) {
        isRefund = refund;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public String getLocalSign() {
        return localSign;
    }

    public void setLocalSign(String localSign) {
        this.localSign = localSign;
    }

    public boolean isLongAmount() {
        return isLongAmount;
    }

    public void setLongAmount(boolean longAmount) {
        isLongAmount = longAmount;
    }

    public String getOtherCode() {
        return otherCode;
    }

    public void setOtherCode(String otherCode) {
        this.otherCode = otherCode;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
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

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
