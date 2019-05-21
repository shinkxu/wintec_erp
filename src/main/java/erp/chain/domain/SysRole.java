package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 角色
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/13
 */
public class SysRole extends BaseDomain{
    BigInteger id;
    String packageName;
    BigInteger tenantId;
    BigInteger branchId;
    String roleCode;
    String roleName;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    Integer roleType;

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public String getPackageName(){
        return packageName;
    }

    public void setPackageName(String packageName){
        this.packageName = packageName;
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

    public String getRoleCode(){
        return roleCode;
    }

    public void setRoleCode(String roleCode){
        this.roleCode = roleCode;
    }

    public String getRoleName(){
        return roleName;
    }

    public void setRoleName(String roleName){
        this.roleName = roleName;
    }

    public String getCreateBy(){
        return createBy;
    }

    public void setCreateBy(String createBy){
        this.createBy = createBy;
    }

    public Date getCreateAt(){
        return createAt;
    }

    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    public String getLastUpdateBy(){
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt(){
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt){
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public Integer getRoleType(){
        return roleType;
    }

    public void setRoleType(Integer roleType){
        this.roleType = roleType;
    }
}
