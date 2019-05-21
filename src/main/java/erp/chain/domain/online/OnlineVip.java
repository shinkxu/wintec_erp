package erp.chain.domain.online;

import erp.chain.annotations.Table;
import erp.chain.annotations.Transient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-10.
 */
@Table(name = "vip")
public class OnlineVip {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 分店号
     */
    private BigInteger branchId;
    /**
     * 微信号
     */
    private String originalId;
    /**
     * 会员等级
     */
    private BigInteger typeId;
    /**
     * 会员编码
     */
    private String vipCode;
    /**
     * 会员名称
     */
    private String vipName;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 备注
     */
    private String memo;
    /**
     * 0-正常
     1-停用
     2-挂失
     */
    private Integer status;
    /**
     * 累计消费次数
     */
    private Integer buyTimes;
    /**
     * 累计消费金额
     */
    private BigDecimal sumConsume;
    /**
     * 累计支付金额
     */
    private BigDecimal sumFeed;
    /**
     * 累计积分:总积分
     */
    private BigDecimal sumScore;
    /**
     * 剩余积分
     */
    private BigDecimal remainingScore;
    /**
     * 累计赠送积分
     */
    private BigDecimal largessscore;
    /**
     * 已使用积分
     */
    private BigDecimal overscore;
    /**
     * 会员既往储值金额
     */
    private BigDecimal vipStoreTotal;
    /**
     * 会员储值金额
     */
    private BigDecimal vipStore;
    /**
     * 注册日期
     */
    private Date regDate;
    /**
     * 注册来源:1:电话注册 2:微信注册
     */
    private String regSource;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    /**
     * 地理位置：地理位置维度;地理位置经度;地理位置精度
     */
    private String position;
    /**
     * 主账号openid
     */
    private String mainOpenid;
    private BigInteger version;
    private BigInteger localId;
    /**
     * 储值卡消费密码
     */
    private String passwordForTrading;
    /**
     * 会员卡号
     */
    private String cardCode;
    /**
     * 会员期限
     */
    private Date expiryDate;
    /**
     * 微信会员卡id
     */
    private String cardId;
    /**
     * 微信会员卡号
     */
    private String userCardCode;
    /**
     * 会员标签
     */
    private String labelId;
    /**
     * 记录押金
     */
    private BigDecimal vipDeposit;

    private String miniProgramOpenId;

    @Transient
    private Long couponsCount;

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

    public BigInteger getTypeId() {
        return typeId;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
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

    public BigDecimal getRemainingScore() {
        return remainingScore;
    }

    public void setRemainingScore(BigDecimal remainingScore) {
        this.remainingScore = remainingScore;
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

    public BigDecimal getVipStoreTotal() {
        return vipStoreTotal;
    }

    public void setVipStoreTotal(BigDecimal vipStoreTotal) {
        this.vipStoreTotal = vipStoreTotal;
    }

    public BigDecimal getVipStore() {
        return vipStore;
    }

    public void setVipStore(BigDecimal vipStore) {
        this.vipStore = vipStore;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMainOpenid() {
        return mainOpenid;
    }

    public void setMainOpenid(String mainOpenid) {
        this.mainOpenid = mainOpenid;
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

    public String getPasswordForTrading() {
        return passwordForTrading;
    }

    public void setPasswordForTrading(String passwordForTrading) {
        this.passwordForTrading = passwordForTrading;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getUserCardCode() {
        return userCardCode;
    }

    public void setUserCardCode(String userCardCode) {
        this.userCardCode = userCardCode;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public BigDecimal getVipDeposit() {
        return vipDeposit;
    }

    public void setVipDeposit(BigDecimal vipDeposit) {
        this.vipDeposit = vipDeposit;
    }

    public Long getCouponsCount() {
        return couponsCount;
    }

    public void setCouponsCount(Long couponsCount) {
        this.couponsCount = couponsCount;
    }

    public String getMiniProgramOpenId() {
        return miniProgramOpenId;
    }

    public void setMiniProgramOpenId(String miniProgramOpenId) {
        this.miniProgramOpenId = miniProgramOpenId;
    }
}
