package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-13.
 */
@Table(name = "vip_type")
public class OnlineVipType {
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 会员类型编码
     */
    private String typeCode;
    /**
     * 会员类型名称
     */
    private String typeName;
    /**
     * 优惠政策 0-无优惠 1-消费积分 2-会员价 3-固定折扣
     */
    private BigInteger preferentialPolicy;
    /**
     * 设积分折算系数-多少元等于1积分
     */
    private BigDecimal pointsFactor;
    /**
     * 设置X积分等于1元，可用于前台消费
     */
    private BigInteger scoreUsage;
    /**
     * 使用哪个会员价 1或2
     */
    private BigInteger memPriceUsed;
    /**
     * 固定折扣率0.0 - 99.9
     */
    private BigDecimal discountRate;
    /**
     * 是否积分
     */
    private BigInteger toSavePoints;
    /**
     * 优惠是否包含套餐商品
     */
    private BigInteger isPackageDisc;
    /**
     * 是否线上默认 0不是,1是
     */
    private BigInteger isPromotionDisc;
    /**
     * 是否线上默认 0不是,1是
     */
    private boolean isOnlineDefault;

    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;

    private BigInteger version;
    /**
     * 机构ID
     */
    private BigInteger branchId;
    /**
     * 是否允许退款 0-否，1-是
     */
    private boolean allowRefund;
    /**
     * 押金
     */
    private BigDecimal deposit = BigDecimal.ZERO;
    /**
     * 是否开启自动升级
     */
    private boolean autoUpgrade;
    /**
     * 当前会员等级level
     */
    private Integer currLevel;
    /**
     * 会员升级条件
     */
    private BigDecimal upgradeLimit;
    /**
     * 会员升级条件类型1：累计积分2累计储值3累计消费
     */
    private Integer upgradeType;
    /**
     * 积分方式1：按金额积分2按商品积分
     */
    private Integer scoreType;

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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigInteger getPreferentialPolicy() {
        return preferentialPolicy;
    }

    public void setPreferentialPolicy(BigInteger preferentialPolicy) {
        this.preferentialPolicy = preferentialPolicy;
    }

    public BigDecimal getPointsFactor() {
        return pointsFactor;
    }

    public void setPointsFactor(BigDecimal pointsFactor) {
        this.pointsFactor = pointsFactor;
    }

    public BigInteger getScoreUsage() {
        return scoreUsage;
    }

    public void setScoreUsage(BigInteger scoreUsage) {
        this.scoreUsage = scoreUsage;
    }

    public BigInteger getMemPriceUsed() {
        return memPriceUsed;
    }

    public void setMemPriceUsed(BigInteger memPriceUsed) {
        this.memPriceUsed = memPriceUsed;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigInteger getToSavePoints() {
        return toSavePoints;
    }

    public void setToSavePoints(BigInteger toSavePoints) {
        this.toSavePoints = toSavePoints;
    }

    public BigInteger getIsPackageDisc() {
        return isPackageDisc;
    }

    public void setIsPackageDisc(BigInteger isPackageDisc) {
        this.isPackageDisc = isPackageDisc;
    }

    public BigInteger getIsPromotionDisc() {
        return isPromotionDisc;
    }

    public void setIsPromotionDisc(BigInteger isPromotionDisc) {
        this.isPromotionDisc = isPromotionDisc;
    }

    public boolean getIsOnlineDefault() {
        return isOnlineDefault;
    }

    public void setIsOnlineDefault(boolean isOnlineDefault) {
        this.isOnlineDefault = isOnlineDefault;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public boolean isAllowRefund() {
        return allowRefund;
    }

    public void setAllowRefund(boolean allowRefund) {
        this.allowRefund = allowRefund;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public boolean isAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(boolean autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public Integer getCurrLevel() {
        return currLevel;
    }

    public void setCurrLevel(Integer currLevel) {
        this.currLevel = currLevel;
    }

    public BigDecimal getUpgradeLimit() {
        return upgradeLimit;
    }

    public void setUpgradeLimit(BigDecimal upgradeLimit) {
        this.upgradeLimit = upgradeLimit;
    }

    public Integer getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(Integer upgradeType) {
        this.upgradeType = upgradeType;
    }

    public Integer getScoreType() {
        return scoreType;
    }

    public void setScoreType(Integer scoreType) {
        this.scoreType = scoreType;
    }
}
