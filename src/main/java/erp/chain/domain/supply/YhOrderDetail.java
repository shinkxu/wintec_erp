package erp.chain.domain.supply;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 要货明细
 */
public class YhOrderDetail extends BaseDomain {
    /**
     * 要货单ID
     */
    public BigInteger requireGoodsOrderId;
    /**
     * 产品ID
     */
    public BigInteger goodsId;
    /**
     * 产品数量
     */
    public BigDecimal amount;
    /**
     * 要货价
     */
    public BigDecimal price;

    /**
     * 配送数量
     */
    public BigDecimal distributionQuantity;

    /**
     * 收货数量
     */
    public BigDecimal receivingQuantity;

    /**
     * 小计金额 配货价 * 产品价格
     */
    public BigDecimal subtotal;

    /**
     * 单位类型 1-销售单位，2-包装单位
     * */
    public BigInteger unitType;

    /**
     * 单位换算 1销售单位=？*包装单位
     * */
    public BigDecimal unitRelation;

    public BigInteger tenantId;

    @Override
    public BigInteger getTenantId(){
        return tenantId;
    }

    @Override
    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

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

    public BigInteger getRequireGoodsOrderId() {
        return requireGoodsOrderId;
    }

    public void setRequireGoodsOrderId(BigInteger requireGoodsOrderId) {
        this.requireGoodsOrderId = requireGoodsOrderId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDistributionQuantity() {
        return distributionQuantity;
    }

    public void setDistributionQuantity(BigDecimal distributionQuantity) {
        this.distributionQuantity = distributionQuantity;
    }

    public BigDecimal getReceivingQuantity() {
        return receivingQuantity;
    }

    public void setReceivingQuantity(BigDecimal receivingQuantity) {
        this.receivingQuantity = receivingQuantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
