package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Payment extends BaseDomain {

    /**
     * '支付代码'
     CSH 现金
     CRD 银行卡
     ZFB 支付宝
     WZF 微信支付
     JFD 积分抵现
     CZF 储值支付
     */
    String paymentCode;
    /**
     * '支付名称'
     */
    String paymentName;
    /**
     * 0-正常
     1-停用
     */
    Integer paymentStatus;
    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;
    /**
     * 0否1是
     */
    Boolean isScore;
    /**
     * 0否1是
     */
    Boolean isChange;
    /**
     * 0否1是
     */
    Boolean isMemo;
    /**
     * 0否1是
     */
    Boolean isSale;
    /**
     * 固定面值
     */
    BigDecimal fixValue;
    /**
     * 单笔限次
     */
    Integer fixNum;
    /**
     * 支付类型
     */
    Integer paymentType;
    /**
     * 是否代金券：0否1是
     */
    Integer isVoucher;
    BigInteger currencyId;
    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    Boolean isDeleted;
    BigInteger tenantId;
    BigInteger version;
    BigInteger localId;
    boolean isStore=true;
    boolean isOpenCashbox=false;

    Integer orderNumber;

    public boolean getIsStore(){
        return isStore;
    }

    public void setIsStore(boolean store){
        isStore = store;
    }

    public boolean getIsOpenCashbox(){
        return isOpenCashbox;
    }

    public void setIsOpenCashbox(boolean openCashbox){
        isOpenCashbox = openCashbox;
    }

    public BigInteger getCurrencyId(){
        return currencyId;
    }

    public void setCurrencyId(BigInteger currencyId){
        this.currencyId = currencyId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
    }

    public Payment() {
        super();
    }

    public Payment(Map domainMap) {
        super(domainMap);
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Boolean getIsScore() {
        return isScore;
    }

    public void setIsScore(Boolean score) {
        isScore = score;
    }

    public Boolean getChange() {
        return isChange;
    }

    public void setChange(Boolean change) {
        isChange = change;
    }

    public Boolean getMemo() {
        return isMemo;
    }

    public void setMemo(Boolean memo) {
        isMemo = memo;
    }

    public Boolean getSale() {
        return isSale;
    }

    public void setSale(Boolean sale) {
        isSale = sale;
    }

    public BigDecimal getFixValue() {
        return fixValue;
    }

    public void setFixValue(BigDecimal fixValue) {
        this.fixValue = fixValue;
    }

    public Integer getFixNum() {
        return fixNum;
    }

    public void setFixNum(Integer fixNum) {
        this.fixNum = fixNum;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getIsVoucher() {
        return isVoucher;
    }

    public void setIsVoucher(Integer isVoucher) {
        this.isVoucher = isVoucher;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}
