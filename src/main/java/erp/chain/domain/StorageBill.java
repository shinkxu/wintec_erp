package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Lipeng on 2018/8/9.
 */
public class StorageBill extends BaseDomain  {

    BigInteger id;

    String code;

    BigInteger tenantId;

    BigInteger createBranchId;

    BigInteger chargeBranchId;

    Integer status;

    Date auditAt;

    BigInteger auditBy;

    Date confirmAt;

    BigInteger confirmBy;

    Date payAt;

    BigInteger payBy;

    BigDecimal chargeAmount;

    Date createAt;

    BigInteger createBy;

    Date lastUpdateAt;

    BigInteger lastUpdateBy;

    boolean isDeleted;

    Date chargeStartDate;

    Date chargeEndDate;

    public BigInteger getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getCreateBranchId() {
        return createBranchId;
    }

    public BigInteger getChargeBranchId() {
        return chargeBranchId;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getAuditAt() {
        return auditAt;
    }

    public BigInteger getAuditBy() {
        return auditBy;
    }

    public Date getConfirmAt() {
        return confirmAt;
    }

    public BigInteger getConfirmBy() {
        return confirmBy;
    }

    public Date getPayAt() {
        return payAt;
    }

    public BigInteger getPayBy() {
        return payBy;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public BigInteger getCreateBy() {
        return createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public BigInteger getLastUpdateBy() {
        return lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setCreateBranchId(BigInteger createBranchId) {
        this.createBranchId = createBranchId;
    }

    public void setChargeBranchId(BigInteger chargeBranchId) {
        this.chargeBranchId = chargeBranchId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    public void setAuditBy(BigInteger auditBy) {
        this.auditBy = auditBy;
    }

    public void setConfirmAt(Date confirmAt) {
        this.confirmAt = confirmAt;
    }

    public void setConfirmBy(BigInteger confirmBy) {
        this.confirmBy = confirmBy;
    }

    public void setPayAt(Date payAt) {
        this.payAt = payAt;
    }

    public void setPayBy(BigInteger payBy) {
        this.payBy = payBy;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setCreateBy(BigInteger createBy) {
        this.createBy = createBy;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setLastUpdateBy(BigInteger lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getChargeStartDate() {
        return chargeStartDate;
    }

    public Date getChargeEndDate() {
        return chargeEndDate;
    }

    public void setChargeStartDate(Date chargeStartDate) {
        this.chargeStartDate = chargeStartDate;
    }

    public void setChargeEndDate(Date chargeEndDate) {
        this.chargeEndDate = chargeEndDate;
    }
}
