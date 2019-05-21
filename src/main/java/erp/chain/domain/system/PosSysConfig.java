package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/1/15
 */
public class PosSysConfig extends BaseDomain{
    BigInteger id;
    BigInteger tenantId;
    BigInteger branchId;
    BigInteger employeeId;
    String recoverCode;
    String data;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    String type;

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
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

    public BigInteger getEmployeeId(){
        return employeeId;
    }

    public void setEmployeeId(BigInteger employeeId){
        this.employeeId = employeeId;
    }

    public String getRecoverCode(){
        return recoverCode;
    }

    public void setRecoverCode(String recoverCode){
        this.recoverCode = recoverCode;
    }

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public Date getCreateAt(){
        return createAt;
    }

    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    public String getCreateBy(){
        return createBy;
    }

    public void setCreateBy(String createBy){
        this.createBy = createBy;
    }

    public Date getLastUpdateAt(){
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt){
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy(){
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }
}
