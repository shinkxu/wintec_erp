package erp.chain.domain;

import erp.chain.domain.o2o.ActivityVo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/12/29.
 */
public class CardCouponsReport extends BaseDomain {
    /**
     * 主键
     */
    BigInteger id;
    /**
     * 商户ID
     */
    BigInteger tenantId;

    BigInteger cardCouponsId;

    BigInteger sendNumber;

    BigInteger usedNumber;
    /**
     * 卡劵名称
     */
    String cardName;
    /**
     * 卡劵类型     1:代金券 2:礼品券
     */
    BigInteger cardType;


    /**
     * 使用限额
     */
    BigDecimal limitValue;
    /**
     * 面额
     */
    BigDecimal faceValue;
    /**
     * 创建人(申请人)
     */
    String createBy;
    /**
     * 创建时间(申请时间)
     */
    Date createAt;
    /**
     * 更新人
     */
    String lastUpdateBy;
    /**
     * 更新时间
     */
    Date lastUpdateAt;
    /**
     * 是否删除
     */
    boolean isDeleted;

    BigDecimal discount;

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

    public BigInteger getCardCouponsId() {
        return cardCouponsId;
    }

    public void setCardCouponsId(BigInteger cardCouponsId) {
        this.cardCouponsId = cardCouponsId;
    }

    public BigInteger getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(BigInteger sendNumber) {
        this.sendNumber = sendNumber;
    }

    public BigInteger getUsedNumber() {
        return usedNumber;
    }

    public void setUsedNumber(BigInteger usedNumber) {
        this.usedNumber = usedNumber;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
