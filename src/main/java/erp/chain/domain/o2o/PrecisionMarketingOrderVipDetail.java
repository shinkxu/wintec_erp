package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/12/4
 */
public class PrecisionMarketingOrderVipDetail extends BaseDomain{
    private BigInteger id;
    private BigInteger orderId;
    private BigInteger vipId;
    private boolean isDeleted;
    private BigInteger createdBy;
    private BigInteger updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger tenantId;
    private BigInteger branchId;

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public BigInteger getOrderId(){
        return orderId;
    }

    public void setOrderId(BigInteger orderId){
        this.orderId = orderId;
    }

    public BigInteger getVipId(){
        return vipId;
    }

    public void setVipId(BigInteger vipId){
        this.vipId = vipId;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public BigInteger getCreatedBy(){
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy){
        this.createdBy = createdBy;
    }

    public BigInteger getUpdatedBy(){
        return updatedBy;
    }

    public void setUpdatedBy(BigInteger updatedBy){
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt){
        this.updatedAt = updatedAt;
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
}
