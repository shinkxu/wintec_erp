package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 采购单明细
 */
public class CgOrderDetail  extends BaseDomain {
    /**
     * 机构Id
     */
    public BigInteger branchId;
    /**
     * 采购单价
     */
    public BigDecimal purchaseAmount;
    /**
     * 采购数量
     */
    public BigDecimal quantity;
    /**
     * 采购单Id
     */
    public BigDecimal amount;
    /**
     * 采购单Id
     */
    public BigInteger storeOrderId;
    /**
     * 商品Id
     */
    public BigInteger goodsId;

    /**
     * 单位类型 1-销售单位，2-包装单位
     * */
    public BigInteger unitType;

    /**
     * 单位换算 1包装单位=？*销售单位
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

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
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

    public BigInteger getStoreOrderId() {
        return storeOrderId;
    }

    public void setStoreOrderId(BigInteger storeOrderId) {
        this.storeOrderId = storeOrderId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }
}
