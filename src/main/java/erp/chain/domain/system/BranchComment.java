package erp.chain.domain.system;

import java.math.BigInteger;
import java.util.Date;

/**
 * 店铺评价
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2019/1/17
 */
public class BranchComment{
    ;
    private BigInteger id;
    private BigInteger tenantId;
    private BigInteger branchId;
    private String customerName;
    private String customerPhone;
    private Integer customerSex;
    private String saleCode;
    private Integer customerScore;
    private String comment;
    private String extraComment;
    private Date createdAt;
    private BigInteger createdBy;
    private Date updatedAt;
    private BigInteger updatedBy;
    private boolean isDeleted;
    private Date commentAt;
    private Date extraCommentAt;
    private Integer state;


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

    public String getCustomerName(){
        return customerName;
    }

    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }

    public String getCustomerPhone(){
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone){
        this.customerPhone = customerPhone;
    }

    public Integer getCustomerSex(){
        return customerSex;
    }

    public void setCustomerSex(Integer customerSex){
        this.customerSex = customerSex;
    }

    public String getSaleCode(){
        return saleCode;
    }

    public void setSaleCode(String saleCode){
        this.saleCode = saleCode;
    }

    public Integer getCustomerScore(){
        return customerScore;
    }

    public void setCustomerScore(Integer customerScore){
        this.customerScore = customerScore;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getExtraComment(){
        return extraComment;
    }

    public void setExtraComment(String extraComment){
        this.extraComment = extraComment;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public BigInteger getCreatedBy(){
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy){
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt){
        this.updatedAt = updatedAt;
    }

    public BigInteger getUpdatedBy(){
        return updatedBy;
    }

    public void setUpdatedBy(BigInteger updatedBy){
        this.updatedBy = updatedBy;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public Date getCommentAt(){
        return commentAt;
    }

    public void setCommentAt(Date commentAt){
        this.commentAt = commentAt;
    }

    public Date getExtraCommentAt(){
        return extraCommentAt;
    }

    public void setExtraCommentAt(Date extraCommentAt){
        this.extraCommentAt = extraCommentAt;
    }

    public Integer getState(){
        return state;
    }

    public void setState(Integer state){
        this.state = state;
    }
}
