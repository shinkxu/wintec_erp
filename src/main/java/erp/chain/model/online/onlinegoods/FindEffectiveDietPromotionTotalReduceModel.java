package erp.chain.model.online.onlinegoods;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-13.
 */
public class FindEffectiveDietPromotionTotalReduceModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Integer scope;

    @NotNull
    private Integer forCustomerType;

    @NotNull
    private BigDecimal totalAmount;

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

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getForCustomerType() {
        return forCustomerType;
    }

    public void setForCustomerType(Integer forCustomerType) {
        this.forCustomerType = forCustomerType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
