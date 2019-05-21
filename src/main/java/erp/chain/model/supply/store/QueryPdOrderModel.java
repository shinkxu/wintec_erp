package erp.chain.model.supply.store;



import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询采购单model
 */
public class QueryPdOrderModel extends erp.chain.model.SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;

    /**
     * 最小损溢数量
     */
    public BigDecimal checkQuantityMin;
    /**
     * 最大损溢数量
     */
    public BigDecimal checkQuantityMax;
    /**
     * 最小损溢金额
     */
    public BigDecimal checkAmountMin;
    /**
     * 最大损溢金额
     */
    public BigDecimal checkAmountMax;


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

    public BigDecimal getCheckQuantityMin() {
        return checkQuantityMin;
    }

    public void setCheckQuantityMin(BigDecimal checkQuantityMin) {
        this.checkQuantityMin = checkQuantityMin;
    }

    public BigDecimal getCheckQuantityMax() {
        return checkQuantityMax;
    }

    public void setCheckQuantityMax(BigDecimal checkQuantityMax) {
        this.checkQuantityMax = checkQuantityMax;
    }

    public BigDecimal getCheckAmountMin() {
        return checkAmountMin;
    }

    public void setCheckAmountMin(BigDecimal checkAmountMin) {
        this.checkAmountMin = checkAmountMin;
    }

    public BigDecimal getCheckAmountMax() {
        return checkAmountMax;
    }

    public void setCheckAmountMax(BigDecimal checkAmountMax) {
        this.checkAmountMax = checkAmountMax;
    }

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
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

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }
}
