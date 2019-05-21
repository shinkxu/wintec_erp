package erp.chain.domain.o2o;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/1/21.
 */
public class VipStoreRule {
    /**
     机构id(冗余,暂不用)
     */
    BigInteger branchId;
    /**
     * 是否开启,0开启,1关闭
     */
    boolean isOff;
    /**
     * 储值有效方式,0:永久,1:一定期限内有效
     */
    String deadType;
    /**
     * 有效期基数
     */
    BigInteger deadNum;
    /**
     * 有效期单位,1:日,2:月,3:年
     */
    String deadUnit;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;

    boolean isFixedStore;

    public boolean isFixedStore() {
        return isFixedStore;
    }

    public void setIsFixedStore(boolean isFixedStore) {
        this.isFixedStore = isFixedStore;
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

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public boolean isOff() {
        return isOff;
    }

    public void setIsOff(boolean isOff) {
        this.isOff = isOff;
    }

    public String getDeadType() {
        return deadType;
    }

    public void setDeadType(String deadType) {
        this.deadType = deadType;
    }

    public BigInteger getDeadNum() {
        return deadNum;
    }

    public void setDeadNum(BigInteger deadNum) {
        this.deadNum = deadNum;
    }

    public String getDeadUnit() {
        return deadUnit;
    }

    public void setDeadUnit(String deadUnit) {
        this.deadUnit = deadUnit;
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
