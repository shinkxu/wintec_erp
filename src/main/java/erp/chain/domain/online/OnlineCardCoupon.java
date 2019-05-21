package erp.chain.domain.online;

import erp.chain.annotations.Table;
import erp.chain.annotations.Transient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-07-27.
 */
@Table(name = "card_coupons")
public class OnlineCardCoupon {
    /**
     * 主键
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 卡劵名称
     */
    private String cardName;
    /**
     * 卡劵类型     1:代金券 2:礼品券
     */
    private BigInteger cardType;
    /**
     * 使用限额
     */
    private BigDecimal limitValue;
    /**
     * 面额
     */
    private BigDecimal faceValue;
    /**
     *赠品ID
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
     * 赠品图片
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
     * 备注
     */
    private String remark;
    /**
     * 创建人(申请人)
     */
    private String createBy;
    /**
     * 创建时间(申请时间)
     */
    private Date createAt;
    /**
     * 更新人
     */
    private String lastUpdateBy;
    /**
     * 更新时间
     */
    private Date lastUpdateAt;
    /**
     * 是否删除
     */
    private boolean isDeleted;
    /**
     * 折扣
     */
    private BigDecimal discount;
    /**
     * 启用发券规则
     */
    private boolean openSendRule;
    /**
     * 启用用券规则
     */
    private boolean openUseRule;
    /**
     * 用券商品id
     */
    private String limitGoodsIds;
    /**
     * 用券商品名称
     */
    private String limitGoodsNames;
    /**
     * 用券门店id
     */
    private String limitBranchIds;
    /**
     * 用券门店名称
     */
    private String limitBranchNames;
    /**
     * 状态 0正常1停用
     */
    private boolean status;
    /**
     * 发券活动id
     */
    private BigInteger festivalId;

    @Transient
    private String useStatus;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public boolean isOpenSendRule() {
        return openSendRule;
    }

    public void setOpenSendRule(boolean openSendRule) {
        this.openSendRule = openSendRule;
    }

    public boolean isOpenUseRule() {
        return openUseRule;
    }

    public void setOpenUseRule(boolean openUseRule) {
        this.openUseRule = openUseRule;
    }

    public String getLimitGoodsIds() {
        return limitGoodsIds;
    }

    public void setLimitGoodsIds(String limitGoodsIds) {
        this.limitGoodsIds = limitGoodsIds;
    }

    public String getLimitGoodsNames() {
        return limitGoodsNames;
    }

    public void setLimitGoodsNames(String limitGoodsNames) {
        this.limitGoodsNames = limitGoodsNames;
    }

    public String getLimitBranchIds() {
        return limitBranchIds;
    }

    public void setLimitBranchIds(String limitBranchIds) {
        this.limitBranchIds = limitBranchIds;
    }

    public String getLimitBranchNames() {
        return limitBranchNames;
    }

    public void setLimitBranchNames(String limitBranchNames) {
        this.limitBranchNames = limitBranchNames;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public BigInteger getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(BigInteger festivalId) {
        this.festivalId = festivalId;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }
}
