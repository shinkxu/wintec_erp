package erp.chain.domain.o2o;

import erp.chain.annotations.Transient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/4/27.
 */
public class CardToVip implements Comparable<CardToVip> {
    /**
     * ����
     */
    BigInteger id;
    /**
     * �̻�ID
     */
    BigInteger tenantId;
    /**
     * ��ԱID
     */
    BigInteger vipId;
    /**
     * ��ȯID
     */
    BigInteger cardCouponsId;
    /**
     * ��ȯ�ID
     */
    BigInteger festivalId;
    /**
     * ��������
     */
    String cardName;
    /**
     * ��������     1:����ȯ 2:��Ʒȯ
     */
    BigInteger cardType;
    /**
     * ʹ��״̬ ʹ��״̬ 0 δ�� 1����
     */
    String useStatus;
    /**
     * ʹ���޶�
     */
    BigDecimal limitValue;
    /**
     * ���
     */
    BigDecimal faceValue;
    /**
     * ����
     */
    String week;
    /**
     *��ƷID
     */
    BigInteger giftGoodsId;
    /**
     * ��Ʒ����
     */
    BigInteger giftNum;
    /**
     * ��Ʒ����
     */
    String giftGoodsName;
    /**
     * ��ƷͼƬ
     */
    String giftGoodsPhoto;
    /**
     * �_ʼ�r�g
     */
    Date startTime;
    /**
     * �Y���r�g
     */
    Date endTime;
    /**
     * ��Ч�ڡ�(��λ����)
     */
    BigInteger periodOfValidity;
    /**
     * ��ɫֵ
     */
    String colorValue;
    /**
     * ����
     */
    String cardCode;

    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

    BigDecimal discount;

    Date releaseDate;
    /**
     * 使用时段
     */
    @Transient
    Date useStartTime;
    /**
     * 使用时段
     */
    @Transient
    Date useEndTime;
    @Transient
    String limitBranchNames;
    @Transient
    Integer isRelease;
    @Transient
    Integer isOverLimit;
    @Transient
    String remark;

    public Integer getIsOverLimit(){
        return isOverLimit;
    }

    public void setIsOverLimit(Integer isOverLimit){
        this.isOverLimit = isOverLimit;
    }

    public String getRemark(){
        return remark;
    }

    public void setRemark(String remark){
        this.remark = remark;
    }

    public Integer getIsRelease(){
        return isRelease;
    }

    public void setIsRelease(Integer isRelease){
        this.isRelease = isRelease;
    }

    public String getLimitBranchNames(){
        return limitBranchNames;
    }

    public void setLimitBranchNames(String limitBranchNames){
        this.limitBranchNames = limitBranchNames;
    }

    public Date getUseStartTime(){
        return useStartTime;
    }

    public void setUseStartTime(Date useStartTime){
        this.useStartTime = useStartTime;
    }

    public Date getUseEndTime(){
        return useEndTime;
    }

    public void setUseEndTime(Date useEndTime){
        this.useEndTime = useEndTime;
    }

    public Date getReleaseDate(){
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate){
        this.releaseDate = releaseDate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    @Override
    public int compareTo(CardToVip o) {
        if(this.getLimitValue().doubleValue() > o.getLimitValue().doubleValue()){
            return -1;
        } else {
            return 1;
        }
    }
}
