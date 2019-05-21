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
public class PrecisionMarketingOrderCouponsDetail extends BaseDomain{
    private BigInteger id;
    private BigInteger orderId;
    private BigInteger couponsId;
    private Integer couponsQuantity;
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

    public BigInteger getCouponsId(){
        return couponsId;
    }

    public void setCouponsId(BigInteger couponsId){
        this.couponsId = couponsId;
    }

    public Integer getCouponsQuantity(){
        return couponsQuantity;
    }

    public void setCouponsQuantity(Integer couponsQuantity){
        this.couponsQuantity = couponsQuantity;
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
