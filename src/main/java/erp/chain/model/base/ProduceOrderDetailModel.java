package erp.chain.model.base;

import erp.chain.model.Model;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ProduceOrderDetailModel extends Model {
    public ProduceOrderDetailModel() {
    }

    /**
     * 单价
     */
    @DecimalMin("0")
    @DecimalMax("9999999.999")
    @NotNull
    public BigDecimal price;

    /**
     * 数量
     */
    @DecimalMin("0.001")
    @DecimalMax("9999999.999")
    @NotNull
    public BigDecimal quantity;

    /**
     * 被组合/拆分商品id
     */
    @NotNull
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


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price != null) {
            this.price = price.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity != null) {
            this.quantity = quantity.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }
}