package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * Created by wangms on 2017/1/21.
 */
public class VipStoreRuleDetails {
    /**
     * 规则id
     */
    BigInteger ruleId;
    /**
     * 充值金额
     */
    BigDecimal payLimit;
    /**
     * 赠送金额
     */
    BigDecimal presentLimit;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;
    BigInteger tenantId;
    BigInteger branchId;

    Integer effectTimes=0;
    Date startTime;
    Date endTime;
    Integer afterStoreType=0;
    String afterStoreData;
    String afterStoreDataName;
    boolean isForWechat=false;
    BigInteger pointedVipType;
    String pointedVipTypeName;

    public String getAfterStoreDataName() {
        return afterStoreDataName;
    }

    public void setAfterStoreDataName(String afterStoreDataName) {
        this.afterStoreDataName = afterStoreDataName;
    }

    public String getPointedVipTypeName() {
        return pointedVipTypeName;
    }

    public void setPointedVipTypeName(String pointedVipTypeName) {
        this.pointedVipTypeName = pointedVipTypeName;
    }

    public Integer getEffectTimes() {
        return effectTimes;
    }

    public void setEffectTimes(Integer effectTimes) {
        this.effectTimes = effectTimes;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getAfterStoreType() {
        return afterStoreType;
    }

    public void setAfterStoreType(Integer afterStoreType) {
        this.afterStoreType = afterStoreType;
    }

    public String getAfterStoreData() {
        return afterStoreData;
    }

    public void setAfterStoreData(String afterStoreData) {
        this.afterStoreData = afterStoreData;
    }

    public boolean getIsForWechat() {
        return isForWechat;
    }

    public void setIsForWechat(boolean isForWechat) {
        this.isForWechat = isForWechat;
    }

    public BigInteger getPointedVipType() {
        return pointedVipType;
    }

    public void setPointedVipType(BigInteger pointedVipType) {
        this.pointedVipType = pointedVipType;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
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

    public BigInteger getRuleId() {
        return ruleId;
    }

    public void setRuleId(BigInteger ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getPayLimit() {
        return payLimit;
    }

    public void setPayLimit(BigDecimal payLimit) {
        this.payLimit = payLimit;
    }

    public BigDecimal getPresentLimit() {
        return presentLimit;
    }

    public void setPresentLimit(BigDecimal presentLimit) {
        this.presentLimit = presentLimit;
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
}
