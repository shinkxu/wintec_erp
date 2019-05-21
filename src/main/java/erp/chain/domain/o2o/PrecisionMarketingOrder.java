package erp.chain.domain.o2o;

import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/12/4
 */
public class PrecisionMarketingOrder extends BaseDomain{
    private BigInteger id;
    private boolean isDeleted;
    private BigInteger createdBy;
    private BigInteger updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger tenantId;
    private BigInteger branchId;
    private String code;
    private String name;
    private BigInteger sendGroupId;
    private Integer vipQuantity;
    private String content;
    private BigDecimal score;
    private BigDecimal exp;

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

    public BigInteger getSendGroupId(){
        return sendGroupId;
    }

    public void setSendGroupId(BigInteger sendGroupId){
        this.sendGroupId = sendGroupId;
    }

    public Integer getVipQuantity(){
        return vipQuantity;
    }

    public void setVipQuantity(Integer vipQuantity){
        this.vipQuantity = vipQuantity;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public BigDecimal getScore(){
        return score;
    }

    public void setScore(BigDecimal score){
        this.score = score;
    }

    public BigDecimal getExp(){
        return exp;
    }

    public void setExp(BigDecimal exp){
        this.exp = exp;
    }
}
