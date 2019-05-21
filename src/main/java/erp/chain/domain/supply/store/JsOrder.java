package erp.chain.domain.supply.store;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by LiPeng on 2018/2/26.
 */
public class JsOrder extends BaseDomain {

    BigInteger id;

    String jsCode;

    BigInteger tenantId;

    BigInteger branchId;

    BigInteger supplierId;

    Integer auditStatus;

    Integer settlementStatus;

    BigDecimal settlementAmount;

    Date createAt;

    BigInteger createBy;

    Date auditAt;

    BigInteger auditBy;

    boolean isDeleted;

    String memo;

    Date lastUpdateAt;

    BigInteger lastUpdateBy;

    public BigInteger getId() {
        return id;
    }

    public String getJsCode() {
        return jsCode;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public BigInteger getSupplierId() {
        return supplierId;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public BigInteger getCreateBy() {
        return createBy;
    }

    public Date getAuditAt() {
        return auditAt;
    }

    public BigInteger getAuditBy() {
        return auditBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public BigInteger getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setJsCode(String jsCode) {
        this.jsCode = jsCode;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setSupplierId(BigInteger supplierId) {
        this.supplierId = supplierId;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setCreateBy(BigInteger createBy) {
        this.createBy = createBy;
    }

    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    public void setAuditBy(BigInteger auditBy) {
        this.auditBy = auditBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setLastUpdateBy(BigInteger lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
}
