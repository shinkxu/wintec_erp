package erp.chain.domain.online;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-10.
 */
public class OnlineGoodsPromotion {
    private BigInteger id;
    private BigInteger dietPromotionId;
    /**
     * 是否可用（1：可用；0：不可用）
     */
    private boolean isUse;
    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
    /**
     * week
     */
    private String week;
    private BigInteger tenantId;
    /**
     * 促销编码
     */
    private String promotionCode;

    /**
     * 促销名称
     */
    private String promotionName;
    /**
     * 促销模式\n1-全单折扣\n2-单品折扣\n3-单品特价\n4-买赠\n5-加价换购\n6-再买优惠\n7-满减
     */
    private Integer promotionType;
    /**
     * 促销范围（0：全部；1：线下促销；2：线上促销）
     */
    private Integer scope;
    /**
     * 0-所有顾客\n1-所有会员\n2-非会员\n
     */
    private Integer forCustomerType;
    /**
     * 会员等级\n\n如果适用于会员，再选择对应的具体会员等级\n
     */
    private BigInteger memGradeId;
    /**
     * 备注
     */
    private String memo;
    /**
     * 0-启用\n1-禁用（用户）
     */
    private Integer promotionStatus;
    private BigInteger goodsId;
    private BigInteger buyGoodsId;
    private BigInteger packageId;
    private BigInteger giveGoodsId;
    private BigDecimal buyNum;
    private BigInteger givePackageId;
    private BigDecimal giveNum;
    /**
     * 满足的金额数
     */
    private BigDecimal satisfy;
    /**
     * 减少的金额
     */
    private BigDecimal reduction;
    /**
     * 第几份开始打折
     */
    private BigDecimal quantity;
    /**
     * discount
     */
    private BigDecimal discount;
    private BigDecimal promotionPrice;
    private BigDecimal promotionPercent;
    private BigInteger branchId;
    private String promotionDescription;

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

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
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

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigInteger getBuyGoodsId() {
        return buyGoodsId;
    }

    public void setBuyGoodsId(BigInteger buyGoodsId) {
        this.buyGoodsId = buyGoodsId;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigInteger getGiveGoodsId() {
        return giveGoodsId;
    }

    public void setGiveGoodsId(BigInteger giveGoodsId) {
        this.giveGoodsId = giveGoodsId;
    }

    public BigDecimal getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(BigDecimal buyNum) {
        this.buyNum = buyNum;
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

    public BigDecimal getSatisfy() {
        return satisfy;
    }

    public void setSatisfy(BigDecimal satisfy) {
        this.satisfy = satisfy;
    }

    public BigDecimal getReduction() {
        return reduction;
    }

    public void setReduction(BigDecimal reduction) {
        this.reduction = reduction;
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

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public BigDecimal getPromotionPercent() {
        return promotionPercent;
    }

    public void setPromotionPercent(BigDecimal promotionPercent) {
        this.promotionPercent = promotionPercent;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }
}
