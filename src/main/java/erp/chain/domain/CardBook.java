package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 卡台帐
 * Created by wangms on 2017/9/9.
 */
public class CardBook {
    /**
     * 持卡人id
     */
    BigInteger holderId;
    BigInteger branchId;
    /**
     * 卡类型：4会员卡 5预付卡
     */
    String cardType;
    /**
     * 操作类型：1制卡2充值3退款4退款扣除5回收5消费6消费退款
     */
    String operateType;
    /**
     * 1:现金 2:银行卡 3:支付宝 4:微信 5惠支付6其他
     */
    String payType;
    /**
     * 执行操作前卡余额
     */
    BigDecimal vipStore;
    /**
     * 充值金额（预付卡）
     */
    BigDecimal payAmount;
    /**
     * 赠送金额（预付卡）
     */
    BigDecimal giftAmount;
    /**
     * 押金（预付卡）
     */
    BigDecimal deposit;
    /**
     * 操作后余额
     */
    BigDecimal vipStoreAfter;

    String remark;

    BigDecimal inputAmount;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    BigInteger tmRemainTimes;
    //次卡变更次数
    BigInteger changeTimes;

    String cardCode;

    BigInteger typeId;

    public BigInteger getChangeTimes() {
        return changeTimes;
    }

    public void setChangeTimes(BigInteger changeTimes) {
        this.changeTimes = changeTimes;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public BigInteger getTmRemainTimes() {
        return tmRemainTimes;
    }
    public void setTmRemainTimes(BigInteger tmRemainTimes) {
        this.tmRemainTimes = tmRemainTimes;
    }
    public BigDecimal getInputAmount() {
        return inputAmount;
    }

    public void setInputAmount(BigDecimal inputAmount) {
        this.inputAmount = inputAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getVipStore() {
        return vipStore;
    }

    public void setVipStore(BigDecimal vipStore) {
        this.vipStore = vipStore;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getVipStoreAfter() {
        return vipStoreAfter;
    }

    public void setVipStoreAfter(BigDecimal vipStoreAfter) {
        this.vipStoreAfter = vipStoreAfter;
    }

    public BigInteger getHolderId() {
        return holderId;
    }

    public void setHolderId(BigInteger holderId) {
        this.holderId = holderId;
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
