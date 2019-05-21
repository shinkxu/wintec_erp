package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/1/19.
 */
public class VipVo {
    /**
     * �ֵ��
     */
    BigInteger branchId;

    /**
     * 注册机构名称
     * */
    String branchName;

    /**
     * ΢�ź�
     */
    String originalId;
    /**
     * ��Ա����
     */
    String vipCode;
    /**
     * ��Ա����
     */
    String vipName;

    /**
     * �Ա�
     */
    Integer sex;

    /**
     * ��������
     */
    Date birthday;
    /**
     * �ֻ���
     */
    String phone;
    /**
     * ��������
     */
    String email;
    /**
     * ��ע
     */
    String memo;
    /**
     * 0-����
     1-ͣ��
     2-��ʧ
     */
    Integer status;
    /**
     * �ۼ����Ѵ���
     */
    Integer buyTimes;
    /**
     * �ۼ����ѽ��
     */
    BigDecimal sumConsume;
    /**
     * ��ֵ���
     */
    BigDecimal vipStore;
    /**
     * ��Ա������ֵ���
     */
    BigDecimal vipStoreTotal;
    /**
     * �ۼ�֧�����
     */
    BigDecimal sumFeed;
    /**
     * �ۼƻ���:�ܻ���
     */
    BigDecimal sumScore;
    /**
     * ʣ�����
     */
    BigDecimal remainingScore;
    /**
     * �ۼ����ͻ���
     */
    BigDecimal largessscore;
    /**
     * ��ʹ�û���
     */
    BigDecimal overscore;

    /**
     * ע������
     */
    String regDate;
    /**
     * ��Ա����id
     */
    Integer typeId;
    /**
     * ��Ա��������
     */
    String typeName;
    /**
     *ע����Դ:1:�绰ע�� 2:΢��ע��
     */
    String regSource;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

    String cardCode;
    String vipCardCode;
    String cardFaceNum;

    /**
     * 累计赠送金额
     */
    BigDecimal giftAmountTotal;

    String lastStorePayType;

    /**
     * 储值卡消费密码
     */
    String passwordForTrading;
    /**
     * 会员类型编码
     */
    String typeCode;
    BigInteger preferentialPolicy;

    BigDecimal pointsFactor;

    BigInteger memPriceUsed;

    BigDecimal discountRate;

    BigInteger toSavePoints;

    BigInteger isPackageDisc;

    BigInteger isPromotionDisc;
    /**
     * 是否线上默认 0不是,1是
     */
    boolean isOnlineDefault;
    /**
     * 多少积分换算1元钱
     */
    BigInteger scoreUsage;
    /**
     * 是否允许退款 0-否，1-是
     * */
    boolean allowRefund;

    /**
     * 押金
     * */
    BigDecimal deposit;

    Integer unConsumeDays;
    /**
     * 次卡数量
     */
    Integer timesCardCount;

    Integer cardCouponsCount;

    /**
     * 会员标签ID
     * */
    String labelId;

    /**
     * 会员标签名称
     * */
    String labelName;


    public String getCardFaceNum(){
        return cardFaceNum;
    }

    public void setCardFaceNum(String cardFaceNum){
        this.cardFaceNum = cardFaceNum;
    }

    public Integer getTimesCardCount() {
        return timesCardCount;
    }

    public void setTimesCardCount(Integer timesCardCount) {
        this.timesCardCount = timesCardCount;
    }

    public Integer getCardCouponsCount() {
        return cardCouponsCount;
    }

    public void setCardCouponsCount(Integer cardCouponsCount) {
        this.cardCouponsCount = cardCouponsCount;
    }

    public Integer getUnConsumeDays() {
        return unConsumeDays;
    }

    public void setUnConsumeDays(Integer unConsumeDays) {
        this.unConsumeDays = unConsumeDays;
    }

    public String getPasswordForTrading() {
        return passwordForTrading;
    }
    public void setPasswordForTrading(String passwordForTrading) {
        this.passwordForTrading = passwordForTrading;
    }
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public BigInteger getPreferentialPolicy() {
        return preferentialPolicy;
    }

    public void setPreferentialPolicy(BigInteger preferentialPolicy) {
        this.preferentialPolicy = preferentialPolicy;
    }

    public BigDecimal getPointsFactor() {
        return pointsFactor;
    }

    public void setPointsFactor(BigDecimal pointsFactor) {
        this.pointsFactor = pointsFactor;
    }

    public BigInteger getMemPriceUsed() {
        return memPriceUsed;
    }

    public void setMemPriceUsed(BigInteger memPriceUsed) {
        this.memPriceUsed = memPriceUsed;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigInteger getToSavePoints() {
        return toSavePoints;
    }

    public void setToSavePoints(BigInteger toSavePoints) {
        this.toSavePoints = toSavePoints;
    }

    public BigInteger getIsPackageDisc() {
        return isPackageDisc;
    }

    public void setIsPackageDisc(BigInteger isPackageDisc) {
        this.isPackageDisc = isPackageDisc;
    }

    public BigInteger getIsPromotionDisc() {
        return isPromotionDisc;
    }

    public void setIsPromotionDisc(BigInteger isPromotionDisc) {
        this.isPromotionDisc = isPromotionDisc;
    }

    public boolean isOnlineDefault() {
        return isOnlineDefault;
    }

    public void setIsOnlineDefault(boolean isOnlineDefault) {
        this.isOnlineDefault = isOnlineDefault;
    }

    public BigInteger getScoreUsage() {
        return scoreUsage;
    }

    public void setScoreUsage(BigInteger scoreUsage) {
        this.scoreUsage = scoreUsage;
    }

    public boolean isAllowRefund() {
        return allowRefund;
    }

    public void setAllowRefund(boolean allowRefund) {
        this.allowRefund = allowRefund;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

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

    public String getVipCardCode(){
        return vipCardCode;
    }

    public void setVipCardCode(String vipCardCode){
        this.vipCardCode = vipCardCode;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
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

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
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

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLabelId() {
        return labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
