package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 领用出入库单
 */
public class LyOrder extends BaseDomain {
    /**
     * 出入库机构(单据所属机构id)
     */
    public BigInteger branchId;
    /**
     * 单号
     */
    public String code;
    /**
     * 类型：1-入库 2-出库
     */
    public Integer type;
    /**
     * 状态：0-未审核 1-已审核
     */
    public Integer status;
    /**
     * 制单人
     */
    public BigInteger makeBy;
    /**
     * 制单时间
     */
    public Date makeAt;
    /**
     * 审核人
     */
    public BigInteger auditBy;
    /**
     * 审核时间
     */
    public Date auditAt;
    /**
     * 领用出入库数量明细合计
     */
    public BigDecimal quantity;
    /**
     * 成本价明细合计
     */
    public BigDecimal incurred;
    /**
     * 合计:quantity*incurred
     */
    public BigDecimal amount;

    /**
     * 单位类型 1-销售单位，2-包装单位
     * */
    public BigInteger unitType;

    /**
     * 单位换算 1销售单位=？*包装单位
     * */
    public BigDecimal unitRelation;

    public BigInteger getUnitType() {
        return unitType;
    }

    public BigDecimal getUnitRelation() {
        return unitRelation;
    }

    public void setUnitType(BigInteger unitType) {
        this.unitType = unitType;
    }

    public void setUnitRelation(BigDecimal unitRelation) {
        this.unitRelation = unitRelation;
    }

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

    public BigInteger getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(BigInteger makeBy) {
        this.makeBy = makeBy;
    }

    public Date getMakeAt() {
        return makeAt;
    }

    public void setMakeAt(Date makeAt) {
        this.makeAt = makeAt;
    }

    public BigInteger getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(BigInteger auditBy) {
        this.auditBy = auditBy;
    }

    public Date getAuditAt() {
        return auditAt;
    }

    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getIncurred() {
        return incurred;
    }

    public void setIncurred(BigDecimal incurred) {
        this.incurred = incurred;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
