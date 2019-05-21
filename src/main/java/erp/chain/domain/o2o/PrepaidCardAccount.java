package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/11
 */
public class PrepaidCardAccount{
    private BigInteger id;
    private BigInteger cardId;
    private BigInteger tenantId;
    private BigInteger branchId;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    private BigDecimal amount;
    private BigDecimal giveAmount;
    private BigDecimal totalAmount;
    private BigInteger paymentId;
    private String orderCode;
    private BigInteger opId;
    private Integer opType;


    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public BigInteger getCardId(){
        return cardId;
    }

    public void setCardId(BigInteger cardId){
        this.cardId = cardId;
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

    public boolean getIsDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public BigDecimal getGiveAmount(){
        return giveAmount;
    }

    public void setGiveAmount(BigDecimal giveAmount){
        this.giveAmount = giveAmount;
    }

    public BigDecimal getTotalAmount(){
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount){
        this.totalAmount = totalAmount;
    }

    public BigInteger getPaymentId(){
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId){
        this.paymentId = paymentId;
    }

    public String getOrderCode(){
        return orderCode;
    }

    public void setOrderCode(String orderCode){
        this.orderCode = orderCode;
    }

    public BigInteger getOpId(){
        return opId;
    }

    public void setOpId(BigInteger opId){
        this.opId = opId;
    }

    public Integer getOpType(){
        return opType;
    }

    public void setOpType(Integer opType){
        this.opType = opType;
    }
}
