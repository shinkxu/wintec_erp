package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-05-11.
 */
@Table(name = "vip_store_rule_details")
public class OnlineVipStoreRuleDetail {
    private BigInteger id;
    /**
     * 规则id
     */
    private BigInteger ruleId;
    /**
     * 充值金额
     */
    private BigDecimal payLimit;
    /**
     * 赠送金额
     */
    private BigDecimal presentLimit;

    private Date createAt;
    private String createBy;
    private Date lastUpdateAt;
    private String lastUpdateBy;
    private boolean isDeleted;
    private BigInteger version;
    private BigInteger localId;
    private BigInteger tenantId;
    private BigInteger branchId;

    /**
     * 有效次数，0：不限制 1:生效一次
     */
    private Integer effectTimes = 0;
    /**
     * 有效期开始时间，不填为不限
     */
    private Date startTime;
    /**
     * 有效期结束时间，不填为不限
     */
    private Date endTime;
    /**
     * 储值后赠送类型 0:无 1:赠送积分 2:赠送优惠券
     */
    private Integer afterStoreType = 0;
    /**
     * 储值后赠送数据，类型为1表示积分，类型为2则记录优惠券id
     */
    private BigInteger afterStoreData;
    /**
     * 是否用于微餐厅储值
     */
    private boolean isForWechat = false;
    /**
     * 指定会员类型
     */
    private BigInteger pointedVipType;
    /**
     * 赠送优惠券名称
     */
    private String afterStoreDataName;
    /**
     * 指定类型名称
     */
    private String pointedVipTypeName;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public BigInteger getAfterStoreData() {
        return afterStoreData;
    }

    public void setAfterStoreData(BigInteger afterStoreData) {
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
}
