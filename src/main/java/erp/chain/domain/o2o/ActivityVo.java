package erp.chain.domain.o2o;

import erp.chain.domain.DietGamePrizeItem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * Created by songzhiqiang on 2017/1/19.
 */
public class ActivityVo {
    BigInteger promotionId;
    String promotionName;
    String branchIds;
    String branchNames;
    Integer promotionType;
    Integer forCustomerType;
    Integer scope;
    java.sql.Date startDate;
    java.sql.Date endDate;
    Integer applyToMon;
    Integer applyToTue;
    Integer applyToWed;
    Integer applyToThu;
    Integer applyToFri;
    Integer applyToSat;
    Integer applyToSun;
    BigDecimal satisfy;
    Integer reduceType;
    BigDecimal reduction;
    BigInteger payType;
    BigDecimal discount;
    BigInteger cardId;
    String cardName;
    BigDecimal sendLimitValue;
    BigInteger totalInventory;
    String startAndEndDate;
    //��ȯ�����
    String gameName;
    //�������
    BigInteger attendTimes;
    String remark;
    String effectiveInterval;

    public String getEffectiveInterval() {
        return effectiveInterval;
    }

    public void setEffectiveInterval(String effectiveInterval) {
        this.effectiveInterval = effectiveInterval;
    }

    //��Ʒ
    List<DietGamePrizeItem> items;
    List<HashMap<String, Object>> details;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public BigInteger getAttendTimes() {
        return attendTimes;
    }

    public void setAttendTimes(BigInteger attendTimes) {
        this.attendTimes = attendTimes;
    }

    public List<DietGamePrizeItem> getItems() {
        return items;
    }

    public void setItems(List<DietGamePrizeItem> items) {
        this.items = items;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(String branchIds) {
        this.branchIds = branchIds;
    }

    public String getBranchNames() {
        return branchNames;
    }

    public void setBranchNames(String branchNames) {
        this.branchNames = branchNames;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Integer getForCustomerType() {
        return forCustomerType;
    }

    public void setForCustomerType(Integer forCustomerType) {
        this.forCustomerType = forCustomerType;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
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

    public Integer getApplyToMon() {
        return applyToMon;
    }

    public void setApplyToMon(Integer applyToMon) {
        this.applyToMon = applyToMon;
    }

    public Integer getApplyToWed() {
        return applyToWed;
    }

    public void setApplyToWed(Integer applyToWed) {
        this.applyToWed = applyToWed;
    }

    public Integer getApplyToTue() {
        return applyToTue;
    }

    public void setApplyToTue(Integer applyToTue) {
        this.applyToTue = applyToTue;
    }

    public Integer getApplyToThu() {
        return applyToThu;
    }

    public void setApplyToThu(Integer applyToThu) {
        this.applyToThu = applyToThu;
    }

    public Integer getApplyToSat() {
        return applyToSat;
    }

    public void setApplyToSat(Integer applyToSat) {
        this.applyToSat = applyToSat;
    }

    public Integer getApplyToFri() {
        return applyToFri;
    }

    public void setApplyToFri(Integer applyToFri) {
        this.applyToFri = applyToFri;
    }

    public Integer getApplyToSun() {
        return applyToSun;
    }

    public void setApplyToSun(Integer applyToSun) {
        this.applyToSun = applyToSun;
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

    public Integer getReduceType() {
        return reduceType;
    }

    public void setReduceType(Integer reduceType) {
        this.reduceType = reduceType;
    }

    public BigInteger getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(BigInteger promotionId) {
        this.promotionId = promotionId;
    }

    public BigInteger getPayType() {
        return payType;
    }

    public void setPayType(BigInteger payType) {
        this.payType = payType;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
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

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getStartAndEndDate() {
        return startAndEndDate;
    }

    public void setStartAndEndDate(String startAndEndDate) {
        this.startAndEndDate = startAndEndDate;
    }

    public List<HashMap<String, Object>> getDetails() {
        return details;
    }

    public void setDetails(List<HashMap<String, Object>> details) {
        this.details = details;
    }
}
