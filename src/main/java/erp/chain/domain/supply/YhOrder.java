package erp.chain.domain.supply;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 要货单
 */
public class YhOrder extends BaseDomain {
    /**
     * 要货单单号
     */
    public String requireGoodsOrderNo;
    /**
     * 商户号
     */
    public String tenantCode;
    /**
     * 要货机构ID
     */
    public BigInteger branchId;
    /**
     * 配送中心ID
     */
    public BigInteger distributionCenterId;
    /**
     * 制单时间
     */
    public Date makeAt;
    /**
     * 制单人
     */
    public String makeBy;
    /**
     * 创建人ID
     */
    public BigInteger createUserId;
    /**
     * 状态0-未提交 1-已审核 2-已付款 3-已配送 4-已收货 5-已提交
     */
    public Integer status;
    /**
     * 审核时间
     */
    public Date auditAt;
    /**
     * 审核人
     */
    public String auditBy;
    /**
     * 审核人ID
     */
    public BigInteger auditUserId;
    /**
     * 总金额
     */
    public BigDecimal totalAmount;
    /**
     * 付款金额
     */
    public BigDecimal amountPaid;
    /**
     * 支付类型：1-支付宝支付，2-微信支付，3-银联支付
     */
    public Integer paidType;
    /**
     * 完成付款时间
     */
    public Date lastPaid;

    /**
     * 操作人
     * */
    public String empName;

    public String getRequireGoodsOrderNo() {
        return requireGoodsOrderNo;
    }

    public void setRequireGoodsOrderNo(String requireGoodsOrderNo) {
        this.requireGoodsOrderNo = requireGoodsOrderNo;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public Date getMakeAt() {
        return makeAt;
    }

    public void setMakeAt(Date makeAt) {
        this.makeAt = makeAt;
    }

    public String getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(String makeBy) {
        this.makeBy = makeBy;
    }

    public BigInteger getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(BigInteger createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAuditAt() {
        return auditAt;
    }

    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public BigInteger getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(BigInteger auditUserId) {
        this.auditUserId = auditUserId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getPaidType() {
        return paidType;
    }

    public void setPaidType(Integer paidType) {
        this.paidType = paidType;
    }

    public Date getLastPaid() {
        return lastPaid;
    }

    public void setLastPaid(Date lastPaid) {
        this.lastPaid = lastPaid;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
