package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by songzhiqiang on 2017/1/21.
 */
public class DietPromotionBuyGiveVo {
    /**
     *
     */
    BigInteger dietPromotionId;
    /**
     *
     */
    BigInteger buyGoodsId;
    String buyGoodsName;
    /**
     * 购买数量
     */
    BigDecimal buyAmount;
    BigInteger giveGoodsId;

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

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigInteger getGiveGoodsId() {
        return giveGoodsId;
    }

    public void setGiveGoodsId(BigInteger giveGoodsId) {
        this.giveGoodsId = giveGoodsId;
    }

    public String getGiveGoodsName() {
        return giveGoodsName;
    }

    public void setGiveGoodsName(String giveGoodsName) {
        this.giveGoodsName = giveGoodsName;
    }

    public BigInteger getGiveAmount() {
        return giveAmount;
    }

    public void setGiveAmount(BigInteger giveAmount) {
        this.giveAmount = giveAmount;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * 赠送商品
     */

    String giveGoodsName;
    /**
     * 赠送套餐
     */
    BigInteger giveAmount;

    BigInteger id;
}
