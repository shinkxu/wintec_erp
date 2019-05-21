package erp.chain.domain.system;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 短信发送记录
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/6/23
 */
public class SmsUseHistory extends BaseDomain{
    BigInteger id;
    BigInteger tenantId;
    BigInteger useBranchId;
    /**
     * 1-会员消费
     */
    Integer type;
    String sendNumber;
    Integer restCount;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

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

    public BigInteger getUseBranchId(){
        return useBranchId;
    }

    public void setUseBranchId(BigInteger useBranchId){
        this.useBranchId = useBranchId;
    }

    public Integer getType(){
        return type;
    }

    public void setType(Integer type){
        this.type = type;
    }

    public String getSendNumber(){
        return sendNumber;
    }

    public void setSendNumber(String sendNumber){
        this.sendNumber = sendNumber;
    }

    public Integer getRestCount(){
        return restCount;
    }

    public void setRestCount(Integer restCount){
        this.restCount = restCount;
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
