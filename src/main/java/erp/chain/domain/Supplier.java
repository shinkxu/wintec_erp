package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by lipeng on 2017/8/4.
 */
public class Supplier extends BaseDomain {

    /**
     * 供应商ID
     * */
    BigInteger id;

    /**
     * 所属商户ID
     * */
    BigInteger tenantId;

    /**
     * 所属机构ID
     * */
    BigInteger branchId;

    /**
     * 供应商编码
     * */
    String supplierCode;

    /**
     * 供应商名称
     * */
    String supplierName;

    /**
     * 助记码
     * */
    String mnemonic;

    /**
     * 状态 1-启用，2-禁用
     * */
    Integer status;

    /**
     * 联系人
     * */
    String contacts;

    /**
     * 联系电话
     * */
    String contactsNumber;

    /**
     * 地址
     * */
    String address;

    /**
     * 备注
     * */
    String memo;

    /**
     * 创建人
     * */
    String createBy;

    /**
     * 创建时间
     * */
    Date createAt;

    /**
     * 最后修改人
     * */
    String lastUpdateBy;

    /**
     * 最后修改时间
     * */
    Date lastUpdateAt;

    /**
     * 删除标识
     * */
    boolean isDeleted;

    public BigInteger getId() {
        return id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public Integer isStatus() {
        return status;
    }

    public String getContacts() {
        return contacts;
    }

    public String getContactsNumber() {
        return contactsNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getMemo() {
        return memo;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public void setContactsNumber(String contactsNumber) {
        this.contactsNumber = contactsNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
