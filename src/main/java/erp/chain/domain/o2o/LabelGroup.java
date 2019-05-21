package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by lipeng on 2018/1/16.
 */
public class LabelGroup extends BaseDomain {

    /**
     * id
     * */
    BigInteger id;

    /**
     * 编码
     * */
    String groupCode;

    /**
     * 名称
     * */
    String groupName;

    /**
     * 商户ID
     * */
    BigInteger tenantId;

    /**
     * 机构ID
     * */
    BigInteger branchId;

    /**
     * 创建时间
     * */
    Date createAt;

    /**
     * 创建人
     * */
    String createBy;

    /**
     * 最后修改时间
     * */
    Date lastUpdateAt;

    /**
     * 最后修改人
     * */
    String lastUpdateBy;

    /**
     * 删除标识
     * */
    boolean isDeleted;

    BigInteger pId;

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public BigInteger getId() {
        return id;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getpId() {
        return pId;
    }

    public void setpId(BigInteger pId) {
        this.pId = pId;
    }
}
