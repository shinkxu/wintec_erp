package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 配送出入库单明细
 */
public class PsOrderDetail extends BaseDomain {
    /**
     * 出入库机构(单据所属机构id)
     */
    public BigInteger branchId;
    /**
     * 单据id
     */
    public BigInteger orderId;
    /**
     * 商品id
     */
    public BigInteger goodsId;
    /**
     * 出入库数量
     */
    public BigDecimal quantity;
    /**
     * 配送价
     */
    public BigDecimal distributionPrice;
    /**
     * 合计:quantity*distributionPrice
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDistributionPrice() {
        return distributionPrice;
    }

    public void setDistributionPrice(BigDecimal distributionPrice) {
        this.distributionPrice = distributionPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
