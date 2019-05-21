package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/1/18.
 */
public class VipTradeHistory {
    /**
     *会员id
     */
    BigInteger vipId;
    /**
     *交易单号
     */
    String tradeNo;
    /**
     *1:消费,2:撤销
     */
    String tradeType;
    /**
     *消费金额
     */
    BigDecimal tradeAmount;
    /**
     *使用积分
     */
    BigInteger useIntegral;
    /**
     *积分抵现金额
     */
    BigDecimal integralAmount;
    /**
     *优惠卷编码
     */
    String useCouponCode;
    /**
     *优惠券抵现金额
     */
    BigDecimal useCouponAmount;
    /**
     *实际支付金额
     */
    BigDecimal payAmount;
    /**
     *交易机构id
     */
    BigInteger tradeBranchId;
    /**
     *交易机构名称
     */
    String tradeBranchName;
    /**
     *交易时间
     */
    Date tradeDate;
    /**
     *冲正操作人id
     */
    BigInteger tradeUserId;
    /**
     *交易操作人名称
     */
    String tradeUserName;
    /**
     * 是否已用于领取红包
     * 0:否
     * 1:是
     */
    String usedForRedpaper;
    /**
     * 产生积分
     */
    BigDecimal addScore;
    /**
     * token验证 ，用于防止重复提交
     */
    String token;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger tenantId;
    String orderCode;
    BigInteger paymentId;

    public BigInteger getPaymentId(){
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId){
        this.paymentId = paymentId;
    }

    public String getOrderCode(){
        return orderCode;
    }

    public void setOrderCode(String orderCode){
        this.orderCode = orderCode;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public BigInteger getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(BigInteger useIntegral) {
        this.useIntegral = useIntegral;
    }

    public BigDecimal getIntegralAmount() {
        return integralAmount;
    }

    public void setIntegralAmount(BigDecimal integralAmount) {
        this.integralAmount = integralAmount;
    }

    public String getUseCouponCode() {
        return useCouponCode;
    }

    public void setUseCouponCode(String useCouponCode) {
        this.useCouponCode = useCouponCode;
    }

    public BigDecimal getUseCouponAmount() {
        return useCouponAmount;
    }

    public void setUseCouponAmount(BigDecimal useCouponAmount) {
        this.useCouponAmount = useCouponAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigInteger getTradeBranchId() {
        return tradeBranchId;
    }

    public void setTradeBranchId(BigInteger tradeBranchId) {
        this.tradeBranchId = tradeBranchId;
    }

    public String getTradeBranchName() {
        return tradeBranchName;
    }

    public void setTradeBranchName(String tradeBranchName) {
        this.tradeBranchName = tradeBranchName;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigInteger getTradeUserId() {
        return tradeUserId;
    }

    public void setTradeUserId(BigInteger tradeUserId) {
        this.tradeUserId = tradeUserId;
    }

    public String getTradeUserName() {
        return tradeUserName;
    }

    public void setTradeUserName(String tradeUserName) {
        this.tradeUserName = tradeUserName;
    }

    public BigDecimal getAddScore() {
        return addScore;
    }

    public void setAddScore(BigDecimal addScore) {
        this.addScore = addScore;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getUsedForRedpaper(){
        return usedForRedpaper;
    }

    public void setUsedForRedpaper(String usedForRedpaper){
        this.usedForRedpaper = usedForRedpaper;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }
}
