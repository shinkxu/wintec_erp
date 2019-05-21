package erp.chain.model.supply.store;


import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询采购单model
 */
public class QueryCgOrderModel extends erp.chain.model.SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;
    /**
     * 单据类型(1-进货,2-退货)
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    public Integer type;
    /**
     * 最小数量
     */
    public BigDecimal quantityMin;
    /**
     * 最大数量
     */
    public BigDecimal quantityMax;
    /**
     * 最小金额
     */
    public BigDecimal amountMin;
    /**
     * 最大金额
     */
    public BigDecimal amountMax;


    /**
     * 单据号模糊查询
     */
    public String codeLike;
    /**
     * 单据编号查询
     */
    public String code;

    /**
     * 最小创建时间（格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String startDate;
    /**
     * 最大创建时间格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String endDate;

    /**
     * 制单人ID（员工ID）
     */
    public BigInteger empId;

    /**
     * 供应商ID
     * */
    public BigInteger supplierIds;

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public BigDecimal getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(BigDecimal quantityMin) {
        this.quantityMin = quantityMin;
    }

    public BigDecimal getQuantityMax() {
        return quantityMax;
    }

    public void setQuantityMax(BigDecimal quantityMax) {
        this.quantityMax = quantityMax;
    }

    public BigDecimal getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(BigDecimal amountMin) {
        this.amountMin = amountMin;
    }

    public BigDecimal getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(BigDecimal amountMax) {
        this.amountMax = amountMax;
    }

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
    }

    public BigInteger getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(BigInteger supplierIds) {
        this.supplierIds = supplierIds;
    }
}
