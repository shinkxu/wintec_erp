package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Lip on 2018/12/13.
 */
public class VipStatement extends BaseDomain {

    BigInteger id;

    BigInteger tenantId;

    BigInteger branchId;

    BigInteger vipId;

    BigInteger typeId;

    BigDecimal beginAmount;

    BigDecimal storeAmount;

    BigDecimal payAmount;

    BigDecimal drawAmount;

    BigDecimal endAmount;

    Date reconciliationDate;

    Date createAt;

    String createBy;

    Date lastUpdateAt;

    String lastUpdateBy;

    boolean isDeleted;

    public BigInteger getId() {
        return id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public BigDecimal getBeginAmount() {
        return beginAmount;
    }

    public BigDecimal getStoreAmount() {
        return storeAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public BigDecimal getDrawAmount() {
        return drawAmount;
    }

    public BigDecimal getEndAmount() {
        return endAmount;
    }

    public Date getReconciliationDate() {
        return reconciliationDate;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public void setBeginAmount(BigDecimal beginAmount) {
        this.beginAmount = beginAmount;
    }

    public void setStoreAmount(BigDecimal storeAmount) {
        this.storeAmount = storeAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public void setDrawAmount(BigDecimal drawAmount) {
        this.drawAmount = drawAmount;
    }

    public void setEndAmount(BigDecimal endAmount) {
        this.endAmount = endAmount;
    }

    public void setReconciliationDate(Date reconciliationDate) {
        this.reconciliationDate = reconciliationDate;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
