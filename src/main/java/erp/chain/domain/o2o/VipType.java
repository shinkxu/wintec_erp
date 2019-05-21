package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class VipType extends BaseDomain {

    /**
     * 会员类型编码
     */
    String typeCode;
    /**
     * 会员类型名称
     */
    String typeName;

    BigInteger preferentialPolicy;

    BigDecimal pointsFactor;

    BigInteger memPriceUsed;

    BigDecimal discountRate;

    BigInteger toSavePoints;

    BigInteger isPackageDisc;

    BigInteger isPromotionDisc;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    /**
     * 是否线上默认 0不是,1是
     */
    boolean isOnlineDefault;
    /**
     * 多少积分换算1元钱
     */
    BigInteger scoreUsage;
    BigInteger branchId;
    BigInteger version;
    BigInteger localId;

    /**
     * 是否允许退款 0-否，1-是
     * */
    boolean allowRefund;

    /**
     * 押金
     * */
    BigDecimal deposit=BigDecimal.ZERO;

    boolean autoUpgrade;
    Integer currLevel;
    Integer upgradeType;
    BigDecimal upgradeLimit;
    Integer scoreType;

    public Integer getScoreType() {
        return scoreType;
    }

    public void setScoreType(Integer scoreType) {
        this.scoreType = scoreType;
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

    public Integer getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(Integer upgradeType) {
        this.upgradeType = upgradeType;
    }

    public BigDecimal getUpgradeLimit() {
        return upgradeLimit;
    }

    public void setUpgradeLimit(BigDecimal upgradeLimit) {
        this.upgradeLimit = upgradeLimit;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isOnlineDefault() {
        return isOnlineDefault;
    }

    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getScoreUsage() {
        return scoreUsage;
    }

    public void setScoreUsage(BigInteger scoreUsage) {
        this.scoreUsage = scoreUsage;
    }

    public boolean getIsOnlineDefault() {
        return isOnlineDefault;
    }

    public void setIsOnlineDefault(boolean isOnlineDefault) {
        this.isOnlineDefault = isOnlineDefault;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public VipType() {
        super();
    }

    public VipType(Map domainMap) {
        super(domainMap);
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
}
