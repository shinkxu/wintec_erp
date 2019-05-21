package erp.chain.domain.online;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-06-22.
 */
public class WeiXinSaleOrder {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 门店ID
     */
    private BigInteger branchId;
    /**
     * 会员ID
     */
    private BigInteger vipId;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单类型，1-储值，2-次卡
     */
    private Integer orderType;
    /**
     * 订单号
     */
    private String orderCode;
    /**
     * 储值规则ID
     */
    private BigInteger ruleId;
    /**
     * 次卡方案ID
     */
    private BigInteger cardTypeId;
    /**
     * 支付状态: 0-未支付，1-已支付
     */
    private Integer payStatus;
    /**
     * 支付方式，1-支付宝，2-微信，3-惠支付
     */
    private Integer payType;
    /**
     * 支付时间
     */
    private Date payTime;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigInteger getRuleId() {
        return ruleId;
    }

    public void setRuleId(BigInteger ruleId) {
        this.ruleId = ruleId;
    }

    public BigInteger getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(BigInteger cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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
}
