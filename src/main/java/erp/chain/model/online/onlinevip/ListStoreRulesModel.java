package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-05-11.
 */
public class ListStoreRulesModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

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

    public BigInteger getVipTypeId() {
        return vipTypeId;
    }

    public void setVipTypeId(BigInteger vipTypeId) {
        this.vipTypeId = vipTypeId;
    }
}
