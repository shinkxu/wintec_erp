package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-07-26.
 */
@Table(name = "card_to_vip")
public class OnlineCardToVip {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 会员ID
     */
    private BigInteger vipId;
    /**
     * 卡券id
     */
    private BigInteger cardCouponsId;
    /**
     * 卡券活动id
     */
    private BigInteger festivalId;
    /**
     * 卡券名称
     */
    private String cardName;
    /**
     * 1:代金券 2:礼品券
     */
    private BigInteger cardType;
    /**
     * 使用状态 0 未用 1已用
     */
    private String useStatus;
    /**
     * 使用限额
     */
    private BigDecimal limitValue;
    /**
     * 面额
     */
    private BigDecimal faceValue;
    /**
     * 星期：0,0,0,0,0,0,0  0否1是
     */
    private String week;
    /**
     * 赠品id
     */
    private BigInteger giftGoodsId;
    /**
     * 赠品数量
     */
    private BigInteger giftNum;
    /**
     * 赠品名称
     */
    private String giftGoodsName;
    /**
     * 赠品图片路径
     */
    private String giftGoodsPhoto;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 有效期。(单位：天)
     */
    private BigInteger periodOfValidity;
    /**
     * 颜色值
     */
    private String colorValue;
    /**
     * 卡券码
     */
    private String cardCode;

    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;

    /**
     * 折扣
     */
    private BigDecimal discount;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigInteger getCardCouponsId() {
        return cardCouponsId;
    }

    public void setCardCouponsId(BigInteger cardCouponsId) {
        this.cardCouponsId = cardCouponsId;
    }

    public BigInteger getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(BigInteger festivalId) {
        this.festivalId = festivalId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public BigInteger getCardType() {
        return cardType;
    }

    public void setCardType(BigInteger cardType) {
        this.cardType = cardType;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public BigDecimal getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(BigDecimal limitValue) {
        this.limitValue = limitValue;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public BigInteger getGiftGoodsId() {
        return giftGoodsId;
    }

    public void setGiftGoodsId(BigInteger giftGoodsId) {
        this.giftGoodsId = giftGoodsId;
    }

    public BigInteger getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(BigInteger giftNum) {
        this.giftNum = giftNum;
    }

    public String getGiftGoodsName() {
        return giftGoodsName;
    }

    public void setGiftGoodsName(String giftGoodsName) {
        this.giftGoodsName = giftGoodsName;
    }

    public String getGiftGoodsPhoto() {
        return giftGoodsPhoto;
    }

    public void setGiftGoodsPhoto(String giftGoodsPhoto) {
        this.giftGoodsPhoto = giftGoodsPhoto;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigInteger getPeriodOfValidity() {
        return periodOfValidity;
    }

    public void setPeriodOfValidity(BigInteger periodOfValidity) {
        this.periodOfValidity = periodOfValidity;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
