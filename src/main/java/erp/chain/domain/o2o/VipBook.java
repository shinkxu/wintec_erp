package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/1/11.
 */
public class VipBook {
    BigInteger vipId;
    BigInteger branchId;
    Integer bookType;
    BigInteger operateUserId;
    String operateBy;
    Date operateAt;
    String vipCode;
    String paymentCode;
    BigDecimal totalScore;
    BigDecimal vipScore;
    BigDecimal sumConsume;
    BigDecimal total;
    BigDecimal realTotal;
    String memo;
    String token;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;
    String storeFrom;
    BigInteger empId;

    public BigInteger getEmpId(){
        return empId;
    }

    public void setEmpId(BigInteger empId){
        this.empId = empId;
    }

    public String getStoreFrom() {
        return storeFrom;
    }
    public void setStoreFrom(String storeFrom) {
        this.storeFrom = storeFrom;
    }
    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer getBookType() {
        return bookType;
    }

    public void setBookType(Integer bookType) {
        this.bookType = bookType;
    }

    public BigInteger getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(BigInteger operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateBy() {
        return operateBy;
    }

    public void setOperateBy(String operateBy) {
        this.operateBy = operateBy;
    }

    public Date getOperateAt() {
        return operateAt;
    }

    public void setOperateAt(Date operateAt) {
        this.operateAt = operateAt;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getVipScore() {
        return vipScore;
    }

    public void setVipScore(BigDecimal vipScore) {
        this.vipScore = vipScore;
    }

    public BigDecimal getSumConsume() {
        return sumConsume;
    }

    public void setSumConsume(BigDecimal sumConsume) {
        this.sumConsume = sumConsume;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getRealTotal() {
        return realTotal;
    }

    public void setRealTotal(BigDecimal realTotal) {
        this.realTotal = realTotal;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
