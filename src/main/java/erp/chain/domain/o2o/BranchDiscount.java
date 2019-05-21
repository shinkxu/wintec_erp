package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by LiP on 2018/12/18.
 */
public class BranchDiscount extends BaseDomain {

    BigInteger id;

    BigInteger tenantId;

    BigInteger branchId;

    BigInteger typeId;

    BigDecimal discountRate;

    BigInteger empId;

    String createBy;

    Date lastUpdateAt;

    String lastUpdateBy;

    boolean isDeleted;

    public BigInteger getId() {
        return id;
    }

    Date createAt;

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
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

    public BigInteger getEmpId() {
        return empId;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
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

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }
}
