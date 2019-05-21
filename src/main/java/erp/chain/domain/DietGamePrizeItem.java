package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by songzhiqiang on 2017/2/7.
 */
public class DietGamePrizeItem {
    BigInteger id;
    BigInteger dietPromotionId;
    BigInteger tenantId;
    String itemName;
    /**
     * 中奖概率
     */
    BigDecimal winningProbability;
    /**
     * 奖品数量
     */
    BigInteger amount;
    BigInteger prizeDietPromotionId;
    BigInteger prizeDietPromotionType;
    String lastUpdateBy;
    String createBy;
    /**
     * 更新时间
     */
    Date lastUpdateAt;
    /**
     * 创建时间(申请时间)
     */
    Date createAt;
    boolean isDeleted;
    String promotionName;
    String prizeDietPromotionName;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPrizeDietPromotionName() {
        return prizeDietPromotionName;
    }

    public void setPrizeDietPromotionName(String prizeDietPromotionName) {
        this.prizeDietPromotionName = prizeDietPromotionName;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getWinningProbability() {
        return winningProbability;
    }

    public void setWinningProbability(BigDecimal winningProbability) {
        this.winningProbability = winningProbability;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public BigInteger getPrizeDietPromotionId() {
        return prizeDietPromotionId;
    }

    public void setPrizeDietPromotionId(BigInteger prizeDietPromotionId) {
        this.prizeDietPromotionId = prizeDietPromotionId;
    }

    public BigInteger getPrizeDietPromotionType() {
        return prizeDietPromotionType;
    }

    public void setPrizeDietPromotionType(BigInteger prizeDietPromotionType) {
        this.prizeDietPromotionType = prizeDietPromotionType;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
