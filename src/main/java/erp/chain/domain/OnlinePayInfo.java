package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/1/10
 */
public class OnlinePayInfo extends BaseDomain{

    BigInteger id;
    Integer payType;
    String outTradeNo;
    String tradeNo;
    String buyerUserId;
    BigInteger tenantId;
    BigInteger branchId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    String authCode;
    BigDecimal amount;
    Integer status;
    Integer opFrom;

    public Integer getOpFrom(){
        return opFrom;
    }

    public void setOpFrom(Integer opFrom){
        this.opFrom = opFrom;
    }

    public OnlinePayInfo(){
        super();
    }
    public OnlinePayInfo(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public Integer getPayType(){
        return payType;
    }

    public void setPayType(Integer payType){
        this.payType = payType;
    }

    public String getOutTradeNo(){
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo){
        this.outTradeNo = outTradeNo;
    }

    public String getTradeNo(){
        return tradeNo;
    }

    public void setTradeNo(String tradeNo){
        this.tradeNo = tradeNo;
    }

    public String getBuyerUserId(){
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId){
        this.buyerUserId = buyerUserId;
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

    public String getAuthCode(){
        return authCode;
    }

    public void setAuthCode(String authCode){
        this.authCode = authCode;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }
}
