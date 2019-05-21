package erp.chain.model.supply.store;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

/**
 * 分页查询库存单据
 */
public class QueryOrderModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    public BigInteger branchId;
    /**
     * 单据类型(1-采购进货单,2-采购退货单,3-领用入库单,4-领用出库单,
     * 5-加工单,6-损溢单,7-盘点单,8-配送出库单,9-配送入库单)
     */
    @In(value = {"1", "2","3", "4", "5", "6", "7", "8", "9" })
    public Integer type;
    /**
     * 状态：0-未审核 1-已审核
     */
    @In(value = {"0","1"})
    public Integer status;
    /**
     * 制单人或审核人ID（员工ID）
     */
    public BigInteger opEmpId;
    /**
     * 制单人ID（员工ID）
     */
    public BigInteger makeEmpId;
    /**
     * 审核人ID（员工ID）
     */
    public BigInteger auditEmpId;

    /**
     * 单据号模糊查询
     */
    public String codeLike;
    /**
     * 最小创建时间（格式：yyyy-MM-dd）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2}", message = "格式错误")
    @NotNull()
    public String startDate;
    /**
     * 最大创建时间格式：yyyy-MM-dd）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2}", message = "格式错误")
    @NotNull()
    public String endDate;

    /**
     * 供应商ID
     * */
    public String supplierIds;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigInteger getOpEmpId() {
        return opEmpId;
    }

    public void setOpEmpId(BigInteger opEmpId) {
        this.opEmpId = opEmpId;
    }

    public BigInteger getMakeEmpId() {
        return makeEmpId;
    }

    public void setMakeEmpId(BigInteger makeEmpId) {
        this.makeEmpId = makeEmpId;
    }

    public BigInteger getAuditEmpId() {
        return auditEmpId;
    }

    public void setAuditEmpId(BigInteger auditEmpId) {
        this.auditEmpId = auditEmpId;
    }

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }
}
