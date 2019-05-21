package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 盘点单
 */
public class PdOrder extends BaseDomain {
    /**
     * 机构Id
     */
    public BigInteger branchId;
    /**
     *
     */
    public String code;
    /**
     * 制单人
     */
    public BigInteger makeBy;
    /**
     * 制单人姓名
     */
    public String makeName;
    /**
     * 制单时间
     */
    public Date makeAt;
    /**
     * 损溢数量
     */
    public BigDecimal checkQuantity;
    /**
     * 损溢金额
     */
    public BigDecimal checkAmount;
    /**
     * 实际数量
     */
    public BigDecimal reallyQuantity;

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigInteger getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(BigInteger makeBy) {
        this.makeBy = makeBy;
    }

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public Date getMakeAt() {
        return makeAt;
    }

    public void setMakeAt(Date makeAt) {
        this.makeAt = makeAt;
    }

    public BigDecimal getCheckQuantity() {
        return checkQuantity;
    }

    public void setCheckQuantity(BigDecimal checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    public BigDecimal getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(BigDecimal checkAmount) {
        this.checkAmount = checkAmount;
    }

    public BigDecimal getReallyQuantity() {
        return reallyQuantity;
    }

    public void setReallyQuantity(BigDecimal reallyQuantity) {
        this.reallyQuantity = reallyQuantity;
    }
}
