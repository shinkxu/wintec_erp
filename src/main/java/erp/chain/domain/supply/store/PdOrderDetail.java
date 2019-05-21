package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 盘点单
 */
public class PdOrderDetail extends BaseDomain {

    /**
     *
     */
    public BigInteger branchId;
    /**
     * 成本
     */
    public BigDecimal purchasePrice;
    /**
     * 库存数量
     */
    public BigDecimal quantity;
    /**
     * ��ⵥId
     */
    public BigInteger orderId;
    /**
     * ��ƷId
     */
    public BigInteger goodsId;
    /**
     * 实际数量
     */
    public BigDecimal reallyQuantity;
    /**
     * 损溢数量
     */
    public BigDecimal checkQuantity;
    /**
     * 损溢金额
     */
    public BigDecimal checkAmount;

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

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getReallyQuantity() {
        return reallyQuantity;
    }

    public void setReallyQuantity(BigDecimal reallyQuantity) {
        this.reallyQuantity = reallyQuantity;
    }

    public BigDecimal getCheckQuantity() {
        return checkQuantity;
    }

    public void setCheckQuantity(BigDecimal checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    public BigDecimal getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(BigDecimal checkAmount) {
        this.checkAmount = checkAmount;
    }
}
