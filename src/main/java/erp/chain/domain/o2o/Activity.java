package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.Map;

/**
 * Created by songzhiqiang on 2017/1/19.
 */
public class Activity {
    /**
     * 促销编码
     */
    String promotionCode;
    /**
     * 促销名称
     */
    String promotionName;
    /**
     * 营销时间
     */
    String activityTime;
    /**
     * 促销模式
     1-全单折扣
     2-单品折扣
     3-单品特价
     4-买赠
     5-加价换购
     6-再买优惠
     7-满减
     */
    Integer promotionType;
    /**
     *
     */
    java.sql.Date startDate;
    /**
     *
     */
    java.sql.Date endDate;
    /**
     * 开始日期
     开始时间
     */
    Time startTime;
    /**
     * 结束日期
     结束时间
     */
    Time endTime;
    /**
     *
     */
    Integer applyToMon;
    /**
     *
     */
    Integer applyToTue;
    /**
     *
     */
    Integer applyToWed;
    /**
     *
     */
    Integer applyToThu;
    /**
     *
     */
    Integer applyToFri;
    /**
     *
     */
    Integer applyToSat;
    /**
     *
     */
    Integer applyToSun;
    /**
     * 促销范围（0：全部；1：线下促销；2：线上促销）
     */
    Integer scope;
    /**
     * 0-所有顾客
     1-所有会员
     2-非会员
     */
    Integer forCustomerType;
    /**
     * 会员等级

     如果适用于会员，再选择对应的具体会员等级
     */
    BigInteger memGradeId;
    /**
     * 备注
     */
    String memo;
    /**
     * 0-启用
     1-禁用（用户）
     */
    Integer promotionStatus;
    /**
     * 是否可用（1：可用；0：不可用）
     */
    boolean isUse;
    /**
     * 是否叠加
     */
    boolean isSuperposition;
    /**
     * 审核人
     */
    String auditeBy;
    /**
     * 审核时间
     */
    Date auditeAt;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger status;
    String effectiveInterval;

    public String getEffectiveInterval() {
        return effectiveInterval;
    }

    public void setEffectiveInterval(String effectiveInterval) {
        this.effectiveInterval = effectiveInterval;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public java.sql.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.sql.Date startDate) {
        this.startDate = startDate;
    }

    public java.sql.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.sql.Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Integer getApplyToMon() {
        return applyToMon;
    }

    public void setApplyToMon(Integer applyToMon) {
        this.applyToMon = applyToMon;
    }

    public Integer getApplyToTue() {
        return applyToTue;
    }

    public void setApplyToTue(Integer applyToTue) {
        this.applyToTue = applyToTue;
    }

    public Integer getApplyToWed() {
        return applyToWed;
    }

    public void setApplyToWed(Integer applyToWed) {
        this.applyToWed = applyToWed;
    }

    public Integer getApplyToThu() {
        return applyToThu;
    }

    public void setApplyToThu(Integer applyToThu) {
        this.applyToThu = applyToThu;
    }

    public Integer getApplyToFri() {
        return applyToFri;
    }

    public void setApplyToFri(Integer applyToFri) {
        this.applyToFri = applyToFri;
    }

    public Integer getApplyToSat() {
        return applyToSat;
    }

    public void setApplyToSat(Integer applyToSat) {
        this.applyToSat = applyToSat;
    }

    public Integer getApplyToSun() {
        return applyToSun;
    }

    public void setApplyToSun(Integer applyToSun) {
        this.applyToSun = applyToSun;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getForCustomerType() {
        return forCustomerType;
    }

    public void setForCustomerType(Integer forCustomerType) {
        this.forCustomerType = forCustomerType;
    }

    public BigInteger getMemGradeId() {
        return memGradeId;
    }

    public void setMemGradeId(BigInteger memGradeId) {
        this.memGradeId = memGradeId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(Integer promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public boolean isSuperposition() {
        return isSuperposition;
    }

    public void setSuperposition(boolean superposition) {
        isSuperposition = superposition;
    }

    public String getAuditeBy() {
        return auditeBy;
    }

    public void setAuditeBy(String auditeBy) {
        this.auditeBy = auditeBy;
    }

    public Date getAuditeAt() {
        return auditeAt;
    }

    public void setAuditeAt(Date auditeAt) {
        this.auditeAt = auditeAt;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public BigInteger getStatus() {
        return status;
    }

    public void setStatus(BigInteger status) {
        this.status = status;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }
}
