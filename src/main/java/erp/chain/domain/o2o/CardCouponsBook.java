package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/12/1
 */
public class CardCouponsBook extends BaseDomain{
    private BigInteger id;
    private boolean isDeleted;
    private BigInteger createdBy;
    private BigInteger updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger tenantId;
    private BigInteger branchId;
    private BigInteger cardCouponsId;
    private BigInteger vipId;
    private String channel;
    private String cardCode;
    private String business;
    private BigInteger orderId;
    private String orderCode;
    private boolean state;
    private String memo;


    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
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

    public BigInteger getCardCouponsId(){
        return cardCouponsId;
    }

    public void setCardCouponsId(BigInteger cardCouponsId){
        this.cardCouponsId = cardCouponsId;
    }

    public BigInteger getVipId(){
        return vipId;
    }

    public void setVipId(BigInteger vipId){
        this.vipId = vipId;
    }

    public String getChannel(){
        return channel;
    }

    public void setChannel(String channel){
        this.channel = channel;
    }

    public String getCardCode(){
        return cardCode;
    }

    public void setCardCode(String cardCode){
        this.cardCode = cardCode;
    }

    public String getBusiness(){
        return business;
    }

    public void setBusiness(String business){
        this.business = business;
    }

    public BigInteger getOrderId(){
        return orderId;
    }

    public void setOrderId(BigInteger orderId){
        this.orderId = orderId;
    }

    public String getOrderCode(){
        return orderCode;
    }

    public void setOrderCode(String orderCode){
        this.orderCode = orderCode;
    }

    public boolean isState(){
        return state;
    }

    public void setState(boolean state){
        this.state = state;
    }

    public String getMemo(){
        return memo;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }
}
