package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * 商户参数
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/17
 */
public class TenantConfig extends BaseDomain{
    String name;
    String value;
    String maxValue;
    boolean isDeleted;
    String createBy;
    String lastUpdateBy;
    Date createAt;
    Date lastUpdateAt;
    BigInteger tenantId;
    BigInteger localId;
    BigInteger version;
    BigInteger id;


    public TenantConfig() {
        super();
    }

    public TenantConfig(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getMaxValue(){
        return maxValue;
    }

    public void setMaxValue(String maxValue){
        this.maxValue = maxValue;
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

    public String getLastUpdateBy(){
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getCreateAt(){
        return createAt;
    }

    public void setCreateAt(Date createAt){
        this.createAt = createAt;
    }

    public Date getLastUpdateAt(){
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt){
        this.lastUpdateAt = lastUpdateAt;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
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
}
