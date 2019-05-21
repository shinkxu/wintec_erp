package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class SalePayment extends BaseDomain {

    /**
     * 付款单号
     */
    String salePaymentCode;
    /**
     * 销售流水号
     */
    String saleCode;
    /**
     * 支付方式
     */
    BigInteger paymentId;
    /**
     * 支付代码
     */
    String paymentCode;
    /**
     * 应付金额
     */
    BigDecimal payTotal;
    /**
     * 实付金额
     */
    BigDecimal amount;
    /**
     *
     */
    BigInteger posId;
    /**
     * 找零金额
     */
    BigDecimal changeAmount;
    /**
     *
     */
    String memo;
    /**
     * 收银员
     */
    BigInteger cashier;
    /**
     * 付款时间
     */
    Date paymentAt;
    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;

    Integer isRefund;
    String localSign;
    boolean isLongAmount;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger tenantId;
    String otherCode;

    public String getOtherCode(){
        return otherCode;
    }

    public void setOtherCode(String otherCode){
        this.otherCode = otherCode;
    }

    BigInteger vipId;
    BigInteger guideId;

    Integer transTerminal=1;
    Integer payType=1;
    Integer isManualMark=0;

    public Integer getIsManualMark(){
        return isManualMark;
    }

    public void setIsManualMark(Integer isManualMark){
        this.isManualMark = isManualMark;
    }

    public Integer getTransTerminal(){
        return transTerminal;
    }

    public void setTransTerminal(Integer transTerminal){
        this.transTerminal = transTerminal;
    }

    public Integer getPayType(){
        return payType;
    }

    public void setPayType(Integer payType){
        this.payType = payType;
    }

    public BigInteger getGuideId(){
        return guideId;
    }

    public void setGuideId(BigInteger guideId){
        this.guideId = guideId;
    }

    public BigInteger getVipId(){
        return vipId;
    }

    public void setVipId(BigInteger vipId){
        this.vipId = vipId;
    }

    public boolean getIsLongAmount(){
        return isLongAmount;
    }

    public void setIsLongAmount(boolean longAmount){
        isLongAmount = longAmount;
    }

    public SalePayment() {
        super();
    }

    public SalePayment(Map domainMap) {
        super(domainMap);
    }

    public String getLocalSign(){
        return localSign;
    }

    public void setLocalSign(String localSign){
        this.localSign = localSign;
    }

    public String getSalePaymentCode() {
        return salePaymentCode;
    }

    public void setSalePaymentCode(String salePaymentCode) {
        this.salePaymentCode = salePaymentCode;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(BigDecimal payTotal) {
        this.payTotal = payTotal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getCashier() {
        return cashier;
    }

    public void setCashier(BigInteger cashier) {
        this.cashier = cashier;
    }

    public Date getPaymentAt() {
        return paymentAt;
    }

    public void setPaymentAt(Date paymentAt) {
        this.paymentAt = paymentAt;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer isRefund() {
        return isRefund;
    }

    public void setRefund(Integer refund) {
        isRefund = refund;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
