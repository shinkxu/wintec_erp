package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/17.
 */
public class VipSpecialPromotion {

    BigDecimal addScore;
    Integer promotionType;
    String prizeDietPromotionIds;
    String prizeDietPromotionNames;

    BigInteger branchId;
    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    Integer isDoubleScoreBirthday;
    Integer vipDayTimesScore;
    String weeksSetting;
    String daysSetting;

    public Integer getIsDoubleScoreBirthday() {
        return isDoubleScoreBirthday;
    }

    public void setIsDoubleScoreBirthday(Integer isDoubleScoreBirthday) {
        this.isDoubleScoreBirthday = isDoubleScoreBirthday;
    }

    public BigDecimal getAddScore() {
        return addScore;
    }

    public void setAddScore(BigDecimal addScore) {
        this.addScore = addScore;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public String getPrizeDietPromotionIds() {
        return prizeDietPromotionIds;
    }

    public void setPrizeDietPromotionIds(String prizeDietPromotionIds) {
        this.prizeDietPromotionIds = prizeDietPromotionIds;
    }

    public String getPrizeDietPromotionNames() {
        return prizeDietPromotionNames;
    }

    public void setPrizeDietPromotionNames(String prizeDietPromotionNames) {
        this.prizeDietPromotionNames = prizeDietPromotionNames;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
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

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getVipDayTimesScore() {
        return vipDayTimesScore;
    }

    public void setVipDayTimesScore(Integer vipDayTimesScore) {
        this.vipDayTimesScore = vipDayTimesScore;
    }

    public String getDaysSetting() {
        return daysSetting;
    }

    public String getWeeksSetting() {
        return weeksSetting;
    }

    public void setWeeksSetting(String weeksSetting) {
        this.weeksSetting = weeksSetting;
    }

    public void setDaysSetting(String daysSetting) {
        this.daysSetting = daysSetting;
    }
}
