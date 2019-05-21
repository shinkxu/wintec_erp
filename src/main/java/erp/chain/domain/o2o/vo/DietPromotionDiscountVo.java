package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by songzhiqiang on 2017/2/5.
 */
public class DietPromotionDiscountVo {
    /**
     *
     */
    BigInteger dietPromotionId;
    /**
     *
     */
    BigInteger buyGoodsId;
    /**
     *
     */
    String  buyGoodsName;
    /**
     * 第几份开始打折
     */
    BigDecimal quantity;
    /**
     * 折扣数
     */
    BigDecimal discount;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
