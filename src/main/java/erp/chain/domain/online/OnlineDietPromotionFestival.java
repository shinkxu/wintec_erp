package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-08-02.
 */
@Table(name = "diet_promotion_festival")
public class OnlineDietPromotionFestival {
    private BigInteger id;
    private BigInteger dietPromotionId;
    private BigInteger tenantId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 卡券id
     */
    private BigInteger cardId;
    private BigInteger limitPerOne;

    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    private BigDecimal sendLimitValue;
    private BigInteger totalInventory;
    private BigInteger remainInventory;
    private BigInteger version;
    private boolean precisionUse;

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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
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

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public boolean isPrecisionUse() {
        return precisionUse;
    }

    public void setPrecisionUse(boolean precisionUse) {
        this.precisionUse = precisionUse;
    }
}
