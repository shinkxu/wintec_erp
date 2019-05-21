package erp.chain.model.supply.store;

import erp.chain.model.Model;
import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * 审核单据
 */
public class AuditOrderModel extends Model{
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;
    /**
     * 单据类型(1-领用单,2-损溢单,3-采购单,4-盘点单)
     * 审核操作：1-要货机构提交, 2-配送机构审核
     */
    @In(value = {"1", "2", "3", "4"})
    @NotNull
    public Integer type;
    /**
     * 版本号
     */
    @NotNull
    public Long version;

    /**
     * 单据号
     */
    @NotNull
    public String code;

    /**
     * 制单人（员工id）
     */
    @NotNull
    public BigInteger empId;

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
