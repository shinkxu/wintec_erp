package erp.chain.model.online.onlinegoods;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-18.
 */
public class ListMenuInfosModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Integer menuType;

    @NotNull
    private Integer scope;

    @NotNull
    private Integer forCustomerType;

    @NotNull
    private BigInteger vipTypeId;

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

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
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

    public BigInteger getVipTypeId() {
        return vipTypeId;
    }

    public void setVipTypeId(BigInteger vipTypeId) {
        this.vipTypeId = vipTypeId;
    }
}
