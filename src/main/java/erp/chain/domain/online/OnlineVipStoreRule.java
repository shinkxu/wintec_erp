package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-05-11.
 */
@Table(name = "vip_store_rule")
public class OnlineVipStoreRule {
    private BigInteger id;
    private BigInteger tenantId;
    /**
     机构id(冗余,暂不用)
     */
    private BigInteger branchId;
    /**
     * 是否开启,0开启,1关闭
     */
    private boolean isOff;
    /**
     * 储值有效方式,0:永久,1:一定期限内有效
     */
    private String deadType;
    /**
     * 有效期基数
     */
    private BigInteger deadNum;
    /**
     * 有效期单位,1:日,2:月,3:年
     */
    private String deadUnit;

    private Date createAt;
    private String createBy;
    private Date lastUpdateAt;
    private String lastUpdateBy;
    private boolean isDeleted;
    private BigInteger version;
    private BigInteger localId;
    private boolean isFixedStore;

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

    public boolean getIsOff() {
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

    public boolean getIsFixedStore() {
        return isFixedStore;
    }

    public void setIsFixedStore(boolean isFixedStore) {
        this.isFixedStore = isFixedStore;
    }
}
