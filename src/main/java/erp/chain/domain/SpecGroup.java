package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 口味分组
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
public class SpecGroup extends BaseDomain{
    BigInteger id;
    String code;
    String name;
    BigInteger tenantId;
    BigInteger branchId;
    boolean isDeleted;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    BigInteger version;
    BigInteger localId;
    String goodsSpecName;

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
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

    public String getGoodsSpecName() {
        return goodsSpecName;
    }

    public void setGoodsSpecName(String goodsSpecName) {
        this.goodsSpecName = goodsSpecName;
    }
}
