package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 卡账户
 * Created by wangms on 2017/9/26.
 */
public class CardAccount {

    BigInteger typeId;
    BigInteger cardId;
    BigInteger vipId;
    String cardCode;
    String vipName;
    BigDecimal vipStore;
    BigDecimal vipStoreTotal;
    BigDecimal vipGiftTotal;
    String phone;
    String memo;
    Integer status;
    Integer buyTimes;
    BigDecimal sumConsume;

    BigInteger branchId;
    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    Integer cardKind;

    BigInteger tmGoodsId;
    String tmGoodsName;

    BigInteger tmRemainTimes;

    BigDecimal tmPrice;
    /**
     * 有效期类型：1.无限制2.固定天数3.固定使用日期
     */
    Integer tmIntervalType;

    BigInteger tmInterval;
    Date startTime;
    Date endTime;
    BigInteger tmTotalTimes;
    String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigInteger getTmTotalTimes() {
        return tmTotalTimes;
    }

    public void setTmTotalTimes(BigInteger tmTotalTimes) {
        this.tmTotalTimes = tmTotalTimes;
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

    public Integer getCardKind() {
        return cardKind;
    }

    public void setCardKind(Integer cardKind) {
        this.cardKind = cardKind;
    }

    public BigInteger getTmGoodsId() {
        return tmGoodsId;
    }

    public void setTmGoodsId(BigInteger tmGoodsId) {
        this.tmGoodsId = tmGoodsId;
    }

    public String getTmGoodsName() {
        return tmGoodsName;
    }

    public void setTmGoodsName(String tmGoodsName) {
        this.tmGoodsName = tmGoodsName;
    }

    public BigInteger getTmRemainTimes() {
        return tmRemainTimes;
    }

    public void setTmRemainTimes(BigInteger tmRemainTimes) {
        this.tmRemainTimes = tmRemainTimes;
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

    public BigInteger getTypeId() {
        return typeId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public BigDecimal getVipStore() {
        return vipStore;
    }

    public void setVipStore(BigDecimal vipStore) {
        this.vipStore = vipStore;
    }

    public BigDecimal getVipStoreTotal() {
        return vipStoreTotal;
    }

    public void setVipStoreTotal(BigDecimal vipStoreTotal) {
        this.vipStoreTotal = vipStoreTotal;
    }

    public BigDecimal getVipGiftTotal() {
        return vipGiftTotal;
    }

    public void setVipGiftTotal(BigDecimal vipGiftTotal) {
        this.vipGiftTotal = vipGiftTotal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBuyTimes() {
        return buyTimes;
    }

    public void setBuyTimes(Integer buyTimes) {
        this.buyTimes = buyTimes;
    }

    public BigDecimal getSumConsume() {
        return sumConsume;
    }

    public void setSumConsume(BigDecimal sumConsume) {
        this.sumConsume = sumConsume;
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
}
