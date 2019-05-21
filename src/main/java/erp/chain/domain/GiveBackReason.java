package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class GiveBackReason extends BaseDomain {

    /**
     *
     */
    String memo;
    /**
     * 字典表0:赠送1退菜（字典表添加数据）
     */
    Integer type;
    /**
     * 原因
     */
    String reason;
    /**
     * 机构Id
     */
    BigInteger branchId;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger tenantId;
    BigInteger version;
    BigInteger localId;
    String code;

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
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

    public GiveBackReason() {
        super();
    }

    public GiveBackReason(Map domainMap) {
        super(domainMap);
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
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
