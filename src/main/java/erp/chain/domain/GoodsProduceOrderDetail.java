package erp.chain.domain;

import java.math.BigInteger;
import java.math.BigDecimal;
/**
 * 商品加工单明细表
 *
 * @author hefuzi 2016-11-29
 */
public class GoodsProduceOrderDetail extends erp.chain.utils.BaseDomain{

    /**
     * 
     */
    private String orderCode;
    /**
     * 被组合/拆分商品id
     */
    private BigInteger goodsId;
    /**
     *
     */
    private BigInteger branchId;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private BigDecimal quantity;

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

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }
}