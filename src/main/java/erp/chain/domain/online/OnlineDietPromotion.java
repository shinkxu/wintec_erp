package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

/**
 * Created by liuyandong on 2018-08-02.
 */
@Table(name = "diet_promotion")
public class OnlineDietPromotion {
    private BigInteger id;
    private BigInteger tenantId;
    private BigInteger createBranchId;
    /**
     * 促销编码
     */
    private String promotionCode;
    /**
     * 促销名称
     */
    private String promotionName;
    /**
     * 促销模式
     * 1-全单折扣
     * 2-单品折扣
     * 3-单品特价
     * 4-买赠
     * 5-加价换购
     * 6-再买优惠
     * 7-满减
     */
    private Integer promotionType;
    /**
     *
     */
    private Date startDate;
    /**
     *
     */
    private Date endDate;
    /**
     * 开始日期
     * 开始时间
     */
    private Time startTime;
    /**
     * 结束日期
     * 结束时间
     */
    private Time endTime;
    /**
     *
     */
    private Integer applyToMon;
    /**
     *
     */
    private Integer applyToTue;
    /**
     *
     */
    private Integer applyToWed;
    /**
     *
     */
    private Integer applyToThu;
    /**
     *
     */
    private Integer applyToFri;
    /**
     *
     */
    private Integer applyToSat;
    /**
     *
     */
    private Integer applyToSun;
    /**
     * 促销范围（0：全部；1：线下促销；2：线上促销）
     */
    private Integer scope;
    /**
     * 0-所有顾客
     * 1-所有会员
     * 2-非会员
     */
    private Integer forCustomerType;
    /**
     * 会员等级
     * <p>
     * 如果适用于会员，再选择对应的具体会员等级
     */
    private BigInteger memGradeId;
    /**
     * 备注
     */
    private String memo;
    /**
     * 0-启用
     * 1-禁用（用户）
     */
    private Integer promotionStatus;
    /**
     * 是否可用（1：可用；0：不可用）
     */
    private boolean isUse;
    /**
     * 是否叠加
     */
    private boolean isSuperposition;

    private String createBy;
    private Date createAt;
    /**
     * 审核人
     */
    private String auditeBy;
    /**
     * 审核时间
     */
    private Date auditeAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    boolean isDeleted;
    private BigInteger version;
    private BigInteger forVipType;
    private String effectiveInterval;

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

    public BigInteger getCreateBranchId() {
        return createBranchId;
    }

    public void setCreateBranchId(BigInteger createBranchId) {
        this.createBranchId = createBranchId;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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

    public boolean getIsUse() {
        return isUse;
    }

    public void setIsUse(boolean isUse) {
        this.isUse = isUse;
    }

    public boolean isSuperposition() {
        return isSuperposition;
    }

    public boolean getIsSuperposition() {
        return isSuperposition;
    }

    public void setIsSuperposition(boolean isSuperposition) {
        this.isSuperposition = isSuperposition;
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

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getForVipType() {
        return forVipType;
    }

    public void setForVipType(BigInteger forVipType) {
        this.forVipType = forVipType;
    }

    public String getEffectiveInterval() {
        return effectiveInterval;
    }

    public void setEffectiveInterval(String effectiveInterval) {
        this.effectiveInterval = effectiveInterval;
    }
}
