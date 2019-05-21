package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2019-05-09.
 */
public class OfflineOrderDetailModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger vipId;

    @NotNull
    private String saleCode;

    @NotNull
    private Boolean isRefund;

    @NotNull
    private Date checkoutAt;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private BigDecimal discountAmount;

    @NotNull
    private BigDecimal realAmount;

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

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public Boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Boolean isRefund) {
        this.isRefund = isRefund;
    }

    public Date getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(Date checkoutAt) {
        this.checkoutAt = checkoutAt;
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

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }
}
