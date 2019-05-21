package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class DietPromotionBuyGive extends BaseDomain {

    BigInteger dietPromotionId;
    /**
     *
     */
    BigInteger goodsId;
    /**
     *
     */
    BigInteger packageId;
    /**
     * 购买数量
     */
    BigDecimal buyNum;
    /**
     * 赠送商品
     */
    BigInteger giveGoodsId;
    /**
     * 赠送套餐
     */
    BigInteger givePackageId;
    /**
     * 赠送数量
     */
    BigDecimal giveNum;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

    public DietPromotionBuyGive() {
        super();
    }

    public DietPromotionBuyGive(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigDecimal getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(BigDecimal buyNum) {
        this.buyNum = buyNum;
    }

    public BigInteger getGiveGoodsId() {
        return giveGoodsId;
    }

    public void setGiveGoodsId(BigInteger giveGoodsId) {
        this.giveGoodsId = giveGoodsId;
    }

    public BigInteger getGivePackageId() {
        return givePackageId;
    }

    public void setGivePackageId(BigInteger givePackageId) {
        this.givePackageId = givePackageId;
    }

    public BigDecimal getGiveNum() {
        return giveNum;
    }

    public void setGiveNum(BigDecimal giveNum) {
        this.giveNum = giveNum;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
