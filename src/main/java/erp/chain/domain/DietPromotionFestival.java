package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by songzhiqiang on 2017/2/5.
 */
public class DietPromotionFestival extends BaseDomain {
    BigInteger dietPromotionId;
    /**
     *活动名称
     */
    String activityName;
    /**
     * 卡券id
     */
    BigInteger cardId;
    BigInteger limitPerOne;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    BigDecimal sendLimitValue;
    BigInteger totalInventory;
    BigInteger remainInventory;
    boolean precisionUse;

    public boolean isPrecisionUse() {
        return precisionUse;
    }

    public void setPrecisionUse(boolean precisionUse) {
        this.precisionUse = precisionUse;
    }

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
    }

    public BigInteger getLimitPerOne() {
        return limitPerOne;
    }

    public void setLimitPerOne(BigInteger limitPerOne) {
        this.limitPerOne = limitPerOne;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
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

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigDecimal getSendLimitValue() {
        return sendLimitValue;
    }

    public void setSendLimitValue(BigDecimal sendLimitValue) {
        this.sendLimitValue = sendLimitValue;
    }

    public BigInteger getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(BigInteger totalInventory) {
        this.totalInventory = totalInventory;
    }

    public BigInteger getRemainInventory() {
        return remainInventory;
    }

    public void setRemainInventory(BigInteger remainInventory) {
        this.remainInventory = remainInventory;
    }
}
