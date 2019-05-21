package erp.chain.domain.o2o;

import erp.chain.annotations.Transient;
import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 预付卡信息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/5
 */
public class PrepaidCardInfo extends BasicModel{
    private BigInteger id;
    @Transient
    private String cardTypeName;
    @Transient
    private BigInteger opId;
    @Transient
    private BigDecimal deposit=BigDecimal.ZERO;
    @Transient
    private Integer cardKind;
    @NotNull
    private BigInteger preCardId;
    private Integer status=2;
    @NotNull
    private String cardCode;
    private BigDecimal storeAmount=BigDecimal.ZERO;
    private BigDecimal allAmount=BigDecimal.ZERO;
    private Date createAt=new Date();
    private String createBy;
    private Date lastUpdateAt=new Date();
    private String lastUpdateBy;
    private boolean isDeleted=false;


    @NotNull
    private BigInteger tenantId;
    @NotNull
    private BigInteger branchId;


    public BigInteger getOpId(){
        return opId;
    }

    public void setOpId(BigInteger opId){
        this.opId = opId;
    }

    public BigDecimal getDeposit(){
        return deposit;
    }

    public void setDeposit(BigDecimal deposit){
        this.deposit = deposit;
    }


    public BigDecimal getAllAmount(){
        return allAmount;
    }

    public void setAllAmount(BigDecimal allAmount){
        this.allAmount = allAmount;
    }

    public Integer getCardKind(){
        return cardKind;
    }

    public void setCardKind(Integer cardKind){
        this.cardKind = cardKind;
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

    public PrepaidCardInfo(){
    }

    public String getCardTypeName(){
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName){
        this.cardTypeName = cardTypeName;
    }

    public BigInteger getId(){
        return id;
    }

    public void setId(BigInteger id){
        this.id = id;
    }

    public BigInteger getPreCardId(){
        return preCardId;
    }

    public void setPreCardId(BigInteger preCardId){
        this.preCardId = preCardId;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public String getCardCode(){
        return cardCode;
    }

    public void setCardCode(String cardCode){
        this.cardCode = cardCode;
    }

    public BigDecimal getStoreAmount(){
        return storeAmount;
    }

    public void setStoreAmount(BigDecimal storeAmount){
        this.storeAmount = storeAmount;
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
