package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/1/16.
 */
public class VipStoreHistory {
    /**
     *会员id
     */
    BigInteger vipId;
    /**
     *操作单号
     */
    String storeCode;
    /**
     *支付方式
     */
    String payType;
    /**
     *1:充值,2:冲正
     */
    String storeType;
    /**
     *实收金额
     */
    BigDecimal payAmount;
    /**
     *赠送金额
     */
    BigDecimal giftAmount;
    /**
     *储值机构id
     */
    BigInteger storeBranchId;
    /**
     *储值机构名称
     */
    String storeBranchName;
    /**
     *储值时间
     */
    Date storeDate;
    /**
     *储值操作员
     */
    BigInteger storeOpId;
    /**
     *储值操作员名称
     */
    String storeOpName;
    /**
     *冲正标志,0:未冲正,1:已冲正
     */
    boolean correctFlag;
    /**
     *冲正单号
     */
    String correctCode;
    /**
     *冲正时间
     */
    Date correctDate;
    /**
     *冲正操作人id
     */
    BigInteger correctOpId;
    /**
     *冲正操作人名称
     */
    String correctOpName;
    /**
     *备注
     */
    String remark;
    /**
     * 储值有效方式,0:永久,1:一定期限内有效
     */
    String deadType;
    /**
     * 有效期基数
     */
    BigInteger deadNum;
    /**
     * 有效期单位,1:日,2:月,3:年
     */
    String deadUnit;
    /**\
     *过期时间
     */
    Date deadLine;
    /**
     * token验证，用于防止重复提交。
     */
    String token;

    String storeFrom;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;

    /**
     * 储值后余额
     * */
    BigDecimal vipOperStore;
    BigInteger ruleDetailId;
    BigInteger paymentId;
    BigInteger tenantId;
    BigDecimal deposit;
    String orderNumber;

    /**
     * 导购员
     * */
    BigInteger guideId;
    BigDecimal storeRate;
    BigDecimal commissionAmount;

    public String getOrderNumber(){
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber){
        this.orderNumber = orderNumber;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId) {
        this.paymentId = paymentId;
    }

    public BigInteger getRuleDetailId() {
        return ruleDetailId;
    }

    public void setRuleDetailId(BigInteger ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
    }

    public String getStoreFrom() {
        return storeFrom;
    }

    public void setStoreFrom(String storeFrom) {
        this.storeFrom = storeFrom;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
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

    public BigInteger getStoreBranchId() {
        return storeBranchId;
    }

    public void setStoreBranchId(BigInteger storeBranchId) {
        this.storeBranchId = storeBranchId;
    }

    public String getStoreBranchName() {
        return storeBranchName;
    }

    public void setStoreBranchName(String storeBranchName) {
        this.storeBranchName = storeBranchName;
    }

    public Date getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(Date storeDate) {
        this.storeDate = storeDate;
    }

    public BigInteger getStoreOpId() {
        return storeOpId;
    }

    public void setStoreOpId(BigInteger storeOpId) {
        this.storeOpId = storeOpId;
    }

    public String getStoreOpName() {
        return storeOpName;
    }

    public void setStoreOpName(String storeOpName) {
        this.storeOpName = storeOpName;
    }

    public boolean isCorrectFlag() {
        return correctFlag;
    }

    public void setCorrectFlag(boolean correctFlag) {
        this.correctFlag = correctFlag;
    }

    public String getCorrectCode() {
        return correctCode;
    }

    public void setCorrectCode(String correctCode) {
        this.correctCode = correctCode;
    }

    public Date getCorrectDate() {
        return correctDate;
    }

    public void setCorrectDate(Date correctDate) {
        this.correctDate = correctDate;
    }

    public BigInteger getCorrectOpId() {
        return correctOpId;
    }

    public void setCorrectOpId(BigInteger correctOpId) {
        this.correctOpId = correctOpId;
    }

    public String getCorrectOpName() {
        return correctOpName;
    }

    public void setCorrectOpName(String correctOpName) {
        this.correctOpName = correctOpName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeadType() {
        return deadType;
    }

    public void setDeadType(String deadType) {
        this.deadType = deadType;
    }

    public BigInteger getDeadNum() {
        return deadNum;
    }

    public void setDeadNum(BigInteger deadNum) {
        this.deadNum = deadNum;
    }

    public String getDeadUnit() {
        return deadUnit;
    }

    public void setDeadUnit(String deadUnit) {
        this.deadUnit = deadUnit;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigDecimal getVipOperStore() {
        return vipOperStore;
    }

    public void setVipOperStore(BigDecimal vipOperStore) {
        this.vipOperStore = vipOperStore;
    }

    public BigInteger getGuideId() {
        return guideId;
    }

    public BigDecimal getStoreRate() {
        return storeRate;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setGuideId(BigInteger guideId) {
        this.guideId = guideId;
    }

    public void setStoreRate(BigDecimal storeRate) {
        this.storeRate = storeRate;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }
}
