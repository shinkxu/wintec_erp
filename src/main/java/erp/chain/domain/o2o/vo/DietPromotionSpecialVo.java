package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by songzhiqiang on 2017/2/5.
 */
public class DietPromotionSpecialVo {
    /**
     * 促销单id
     */
    BigInteger dietPromotionId;
    /**
     * 促销商品id
     */
    BigInteger buyGoodsId;
    /**
     * 促销商品名称
     */
    String buyGoodsName;
    /**
     * 促销商品原价
     */
    BigDecimal salePrice;
    /**
     * 促销商品促销价
     */
    BigDecimal promotionPrice;
    /**
     * 促销折扣
     */
    BigDecimal promotionPercent;

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public BigInteger getBuyGoodsId() {
        return buyGoodsId;
    }

    public void setBuyGoodsId(BigInteger buyGoodsId) {
        this.buyGoodsId = buyGoodsId;
    }

    public String getBuyGoodsName() {
        return buyGoodsName;
    }

    public void setBuyGoodsName(String buyGoodsName) {
        this.buyGoodsName = buyGoodsName;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public BigDecimal getPromotionPercent() {
        return promotionPercent;
    }

    public void setPromotionPercent(BigDecimal promotionPercent) {
        this.promotionPercent = promotionPercent;
    }
}
