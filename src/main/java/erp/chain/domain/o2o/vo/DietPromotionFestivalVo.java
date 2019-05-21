package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/4/12.
 */
public class DietPromotionFestivalVo {
    /**
     * ��ȯid
     */
    BigInteger dietPromotionFestivalId;
    /**
     * ����id
     */
    BigInteger dietPromotionId;
    /**
     * �̻�id
     */
    BigInteger tenanatId;
    /**
     * �����
     */
    String activityName;
    /**
     * ��ȯid
     */
    BigInteger cardId;
    /**
     * ͬһ���ͻ����ԲμӵĴ���
     */
    BigInteger limitPerOne;
    /**
     * ��ȯ����
     */
    BigDecimal sendLimitValue;
    /**
     * ��ȯ����
     */
    BigInteger totalInventory;
    /**
     * ʣ����
     */
    BigInteger remainInventory;


    /**
     *�����ŵ�id
     */
    BigInteger createBranchId;
    /**
     *��������
     */
    String promotionCode;
    /**
     *��������
     */
    String promotionName;
    /**
     *��������
     */
    Integer promotionType;

    Date startDate;

    Date endDate;

    Date startTime;

    Date endTime;

    Integer applyToMon;

    Integer applyToTue;

    Integer applyToWed;

    Integer applyToThu;

    Integer applyToFri;

    Integer applyToSat;

    Integer applyToSun;
    /**
     * ������Χ��
     * 0��ȫ����
     * 1�����´�����
     * 2�����ϴ���
     */
    Integer scope;
    /**
     * ��������
     * 0-���й˿�
     1-���л�Ա
     2-�ǻ�Ա
     */
    Integer forCustomerType;
    /**
     * ��Ա�ȼ�
     */
    BigInteger memGradeId;

    String memo;

    Integer promotionStatus;
    /**
     * �Ƿ��ڴ���ʱ��
     */
    boolean isUse;

    /**
     * �Ƿ���Ӵ���
     */
    boolean isSuperposition;

    /**
     * ��������
     */
    String cardName;
    /**
     * ��������     1:����ȯ 2:��Ʒȯ
     */
    BigInteger cardType;
    /**
     * ʹ���޶�
     */
    BigDecimal limitValue;
    /**
     * ���
     */
    BigDecimal faceValue;
    /**
     * ��Ч�ڡ�(��λ����)
     */
    BigInteger periodOfValidity;
    /**
     * ��ɫֵ
     */
    String colorValue;
    /**
     * ��ע
     */
    String remark;
    /**
     * ��Ա���п��õĿ�ȯ������
     */
    Integer remainCardCount;

    /**
     * ʹ���ŵ꣨��������ʾ��
     */
    String branchNames;

    boolean openSendRule;
    boolean openUseRule;

    BigDecimal discount;

    public BigDecimal getDiscount(){
        return discount;
    }

    public void setDiscount(BigDecimal discount){
        this.discount = discount;
    }

    public boolean isOpenSendRule(){
        return openSendRule;
    }

    public void setOpenSendRule(boolean openSendRule){
        this.openSendRule = openSendRule;
    }

    public boolean isOpenUseRule(){
        return openUseRule;
    }

    public void setOpenUseRule(boolean openUseRule){
        this.openUseRule = openUseRule;
    }

    public String getBranchNames() {
        return branchNames;
    }

    public void setBranchNames(String branchNames) {
        this.branchNames = branchNames;
    }

    public Integer getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(Integer promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public BigInteger getDietPromotionFestivalId() {
        return dietPromotionFestivalId;
    }

    public void setDietPromotionFestivalId(BigInteger dietPromotionFestivalId) {
        this.dietPromotionFestivalId = dietPromotionFestivalId;
    }

    public BigInteger getDietPromotionId() {
        return dietPromotionId;
    }

    public void setDietPromotionId(BigInteger dietPromotionId) {
        this.dietPromotionId = dietPromotionId;
    }

    public BigInteger getTenanatId() {
        return tenanatId;
    }

    public void setTenanatId(BigInteger tenanatId) {
        this.tenanatId = tenanatId;
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


    public boolean getIsUse() {
        return isUse;
    }

    public void setIsUse(boolean isUse) {
        this.isUse = isUse;
    }

    public void setIsSuperposition(boolean isSuperposition) {
        this.isSuperposition = isSuperposition;
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

    public Integer getRemainCardCount() {
        return remainCardCount;
    }

    public void setRemainCardCount(Integer remainCardCount) {
        this.remainCardCount = remainCardCount;
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
}
