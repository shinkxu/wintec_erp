package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Vip extends BaseDomain {

    /**
     * 分店号
     */
    BigInteger branchId;
    /**
     * 微信号
     */
    String originalId;
    /**
     * 会员编码
     */
    String vipCode;
    /**
     * 会员名称
     */
    String vipName;

    /**
     * 性别
     */
    Integer sex;

    /**
     * 出生日期
     */
    Date birthday;
    /**
     * 手机号
     */
    String phone;
    /**
     * 电子邮箱
     */
    String email;
    /**
     * 备注
     */
    String memo;
    /**
     * 0-正常
     1-停用
     2-挂失
     */
    Integer status;
    /**
     * 累计消费次数
     */
    Integer buyTimes;
    /**
     * 累计消费金额
     */
    BigDecimal sumConsume;
    /**
     * 累计支付金额
     */
    BigDecimal sumFeed;
    /**
     * 累计积分:总积分
     */
    BigDecimal sumScore;
    /**
     * 剩余积分
     */
    BigDecimal remainingScore;
    /**
     * 累计赠送积分
     */
    BigDecimal largessscore;
    /**
     * 已使用积分
     */
    BigDecimal overscore;
    /**
     * 会员等级
     */
    BigInteger typeId;
    /**
     * 注册日期
     */
    String regDate;
    /**
     * 注册来源:1:电话注册 2:微信注册
     */
    String regSource;
    /**
     * 会员储值金额
     */
    BigDecimal vipStore;
    /**
     * 会员既往储值金额
     */
    BigDecimal vipStoreTotal;
    String branchName;

    public String getBranchName(){
        return branchName;
    }

    public void setBranchName(String branchName){
        this.branchName = branchName;
    }

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    /**
     * 储值卡消费密码
     */
    String passwordForTrading;

    /**
     * 会员类型名称
     */
    String vipTypeName;
    String cardCode;
    String position;
    String mainOpenid;
    BigInteger version;
    BigInteger localId;
    boolean isOnlineDefault;
    String cardId;
    String userCardCode;

    BigDecimal deposit;
    String aliUserId;

    public String getAliUserId(){
        return aliUserId;
    }

    public void setAliUserId(String aliUserId){
        this.aliUserId = aliUserId;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getCardId(){
        return cardId;
    }

    public void setCardId(String cardId){
        this.cardId = cardId;
    }

    public String getUserCardCode(){
        return userCardCode;
    }

    public void setUserCardCode(String userCardCode){
        this.userCardCode = userCardCode;
    }

    /**
     * 累计赠送金额
     */
    BigDecimal giftAmountTotal;

    String lastStorePayType;

    String labelId;

    public BigDecimal getGiftAmountTotal() {
        return giftAmountTotal;
    }

    public void setGiftAmountTotal(BigDecimal giftAmountTotal) {
        this.giftAmountTotal = giftAmountTotal;
    }

    public String getLastStorePayType() {
        return lastStorePayType;
    }

    public void setLastStorePayType(String lastStorePayType) {
        this.lastStorePayType = lastStorePayType;
    }

    public boolean isOnlineDefault() {
        return isOnlineDefault;
    }

    public void setIsOnlineDefault(boolean isOnlineDefault) {
        this.isOnlineDefault = isOnlineDefault;
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

    public String getMainOpenid() {
        return mainOpenid;
    }

    public void setMainOpenid(String mainOpenid) {
        this.mainOpenid = mainOpenid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPasswordForTrading() {
        return passwordForTrading;
    }

    public void setPasswordForTrading(String passwordForTrading) {
        this.passwordForTrading = passwordForTrading;
    }

    public Vip() {
        super();
    }

    public Vip(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getSumFeed() {
        return sumFeed;
    }

    public void setSumFeed(BigDecimal sumFeed) {
        this.sumFeed = sumFeed;
    }

    public BigDecimal getSumScore() {
        return sumScore;
    }

    public void setSumScore(BigDecimal sumScore) {
        this.sumScore = sumScore;
    }

    public void setRemainingScore(BigDecimal remainingScore) {
        this.remainingScore = remainingScore;
    }

    public BigDecimal getRemainingScore() {
        return remainingScore;
    }

    public BigDecimal getLargessscore() {
        return largessscore;
    }

    public void setLargessscore(BigDecimal largessscore) {
        this.largessscore = largessscore;
    }

    public BigDecimal getOverscore() {
        return overscore;
    }

    public void setOverscore(BigDecimal overscore) {
        this.overscore = overscore;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
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

    public String getVipTypeName() {
        return vipTypeName;
    }

    public void setVipTypeName(String vipTypeName) {
        this.vipTypeName = vipTypeName;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }
}
