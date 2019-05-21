package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by lipeng on 2018/1/16.
 */
public class VipLabel extends BaseDomain {

    /**
     * ID
     * */
    BigInteger id;

    /**
     * 编码
     * */
    String labelCode;

    /**
     * 名称
     * */
    String labelName;

    /**
     * 商户ID
     * */
    BigInteger tenantId;

    /**
     * 机构ID
     * */
    BigInteger branchId;

    /**
     * 标签组ID
     * */
    BigInteger groupId;

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

    public String getLabelCode() {
        return labelCode;
    }

    public String getLabelName() {
        return labelName;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public BigInteger getId() {
        return id;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
