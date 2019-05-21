package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 采购单
 */
public class CgOrder extends BaseDomain {
    /**
     * 机构Id
     */
    public BigInteger branchId;
    /**
     * 单号规则见用例
     */
    public String code;
    /**
     * 制单人（员工id）
     */
    public BigInteger makeBy;

    /**
     * 制单时间
     */
    public Date makeAt;
    /**
     * 采购数量，明细和
     */
    public BigDecimal quantity;
    /**
     * 采购金额：明细金额之和
     */
    public BigDecimal amount;
    /**
     * 1进货2退货
     */
    public Integer orderType;

    /**
     * 单位类型 1-销售单位，2-包装单位
     * */
    public BigInteger unitType;

    /**
     * 单位换算 1销售单位=？*包装单位
     * */
    public BigDecimal unitRelation;

    /**
     * 供应商ID
     * */
    public BigInteger supplierId;

    /**
     * 供应商名称
     * */
    public String supplierName;

    /**
     * 是否修改商品档案
     * */
    public Integer isUpdatePrice;

    public Date settlementDate;

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

    public BigInteger getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(BigInteger makeBy) {
        this.makeBy = makeBy;
    }

    public Date getMakeAt() {
        return this.makeAt;
    }

    public void setMakeAt(Date makeAt) {
        this.makeAt = makeAt;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigInteger getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierId(BigInteger supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(Integer isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }
}
