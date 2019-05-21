package erp.chain.model.supply.store;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询配送
 */
public class QueryPsOrderModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    /**
     * 出入库机构"
     */
    public BigInteger branchId;
    /**
     * 收发货机构
     */
    public BigInteger targetBranchId;
    /**
     * 最小要货金额
     */
    public BigDecimal amountMin;
    /**
     * 最大要货金额
     */
    public BigDecimal amountMax;

    /**
     * 编号模糊查询
     */
    public String codeLike;
    /**
     * 状态0-未提交，1-已审核 2-已付款 3-已配送 4-已收货 5-已提交
     */
    @In(value = {"0","1","2","3","4","5"})
    public String status;

    /**
     * 单据类型(1-入库，2-出库)
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    public Integer type;
    /**
     * 配送次数
     */
    public Integer pscNum;

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

    public String code;

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

    public BigInteger getTargetBranchId() {
        return targetBranchId;
    }

    public void setTargetBranchId(BigInteger targetBranchId) {
        this.targetBranchId = targetBranchId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPscNum() {
        return pscNum;
    }

    public void setPscNum(Integer pscNum) {
        this.pscNum = pscNum;
    }
}
