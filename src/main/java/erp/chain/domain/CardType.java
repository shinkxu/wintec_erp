package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 卡规则
 * Created by wangms on 2017/9/26.
 */
public class CardType {

    Integer cardKind;
    BigDecimal deposit;
    BigDecimal giftLimit;
    BigDecimal giftAmount;
    BigInteger giftTimes;
    boolean allowRefund;
    String typeName;
    String typeCode;
    Date startTime;
    Date endTime;

    BigInteger branchId;
    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    BigInteger tmGoodsId;
    String tmGoodsName;

    BigInteger tmTotalTimes;

    BigDecimal tmPrice;
    /**
     * 有效期类型：1.无限制2.固定天数3.固定使用日期
     */
    Integer tmIntervalType;

    BigInteger tmInterval;

    /**
     * 面值卡面值
     * */
    BigDecimal denomination;

    /**
     * 次卡使用限制（useRuleDay内最多使用useRuleTime次）
     * */
    Integer useRuleDay;
    Integer useRuleTime;

    public String getTmGoodsName() {
        return tmGoodsName;
    }

    public void setTmGoodsName(String tmGoodsName) {
        this.tmGoodsName = tmGoodsName;
    }

    public BigInteger getTmGoodsId() {
        return tmGoodsId;
    }

    public void setTmGoodsId(BigInteger tmGoodsId) {
        this.tmGoodsId = tmGoodsId;
    }

    public BigInteger getTmTotalTimes() {
        return tmTotalTimes;
    }

    public void setTmTotalTimes(BigInteger tmTotalTimes) {
        this.tmTotalTimes = tmTotalTimes;
    }

    public BigDecimal getTmPrice() {
        return tmPrice;
    }

    public void setTmPrice(BigDecimal tmPrice) {
        this.tmPrice = tmPrice;
    }

    public Integer getTmIntervalType() {
        return tmIntervalType;
    }

    public void setTmIntervalType(Integer tmIntervalType) {
        this.tmIntervalType = tmIntervalType;
    }

    public BigInteger getTmInterval() {
        return tmInterval;
    }

    public void setTmInterval(BigInteger tmInterval) {
        this.tmInterval = tmInterval;
    }

    public Integer getCardKind() {
        return cardKind;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setCardKind(Integer cardKind) {
        this.cardKind = cardKind;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getGiftLimit() {
        return giftLimit;
    }

    public void setGiftLimit(BigDecimal giftLimit) {
        this.giftLimit = giftLimit;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public BigInteger getGiftTimes() {
        return giftTimes;
    }

    public void setGiftTimes(BigInteger giftTimes) {
        this.giftTimes = giftTimes;
    }

    public boolean isAllowRefund() {
        return allowRefund;
    }

    public void setAllowRefund(boolean allowRefund) {
        this.allowRefund = allowRefund;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal denomination) {
        this.denomination = denomination;
    }

    public Integer getUseRuleDay() {
        return useRuleDay;
    }

    public Integer getUseRuleTime() {
        return useRuleTime;
    }

    public void setUseRuleDay(Integer useRuleDay) {
        this.useRuleDay = useRuleDay;
    }

    public void setUseRuleTime(Integer useRuleTime) {
        this.useRuleTime = useRuleTime;
    }
}
