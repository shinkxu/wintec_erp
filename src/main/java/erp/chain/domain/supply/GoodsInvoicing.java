package erp.chain.domain.supply;


import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 进销存
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/2/26
 */
public class GoodsInvoicing extends BaseDomain{
    BigInteger id;
    BigInteger tenantId;
    BigInteger branchId;
    BigInteger goodsId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted=false;
    Date invoicingDate;
    BigDecimal beginQuantity=BigDecimal.ZERO;
    BigDecimal beginAmount=BigDecimal.ZERO;
    BigDecimal endQuantity=BigDecimal.ZERO;
    BigDecimal endAmount=BigDecimal.ZERO;
    BigDecimal cgInQuantity=BigDecimal.ZERO;
    BigDecimal cgInAmount=BigDecimal.ZERO;
    BigDecimal cgOutQuantity=BigDecimal.ZERO;
    BigDecimal cgOutAmount=BigDecimal.ZERO;
    BigDecimal lyInQuantity=BigDecimal.ZERO;
    BigDecimal lyInAmount=BigDecimal.ZERO;
    BigDecimal lyOutQuantity=BigDecimal.ZERO;
    BigDecimal lyOutAmount=BigDecimal.ZERO;
    BigDecimal psInQuantity=BigDecimal.ZERO;
    BigDecimal psInAmount=BigDecimal.ZERO;
    BigDecimal psOutQuantity=BigDecimal.ZERO;
    BigDecimal psOutAmount=BigDecimal.ZERO;
    BigDecimal jgInQuantity=BigDecimal.ZERO;
    BigDecimal jgInAmount=BigDecimal.ZERO;
    BigDecimal jgOutQuantity=BigDecimal.ZERO;
    BigDecimal jgOutAmount=BigDecimal.ZERO;
    BigDecimal syInQuantity=BigDecimal.ZERO;
    BigDecimal syInAmount=BigDecimal.ZERO;
    BigDecimal syOutQuantity=BigDecimal.ZERO;
    BigDecimal syOutAmount=BigDecimal.ZERO;
    BigDecimal pdInQuantity=BigDecimal.ZERO;
    BigDecimal pdInAmount=BigDecimal.ZERO;
    BigDecimal pdOutQuantity=BigDecimal.ZERO;
    BigDecimal pdOutAmount=BigDecimal.ZERO;
    BigDecimal saleOutQuantity=BigDecimal.ZERO;
    BigDecimal saleOutAmount=BigDecimal.ZERO;
    BigDecimal saleOutTotal=BigDecimal.ZERO;
    BigDecimal saleInQuantity=BigDecimal.ZERO;
    BigDecimal saleInAmount=BigDecimal.ZERO;
    BigDecimal saleInTotal=BigDecimal.ZERO;
    BigDecimal onlineSaleOutQuantity=BigDecimal.ZERO;
    BigDecimal onlineSaleOutAmount=BigDecimal.ZERO;
    BigDecimal onlineSaleOutTotal=BigDecimal.ZERO;
    BigDecimal onlineSaleInQuantity=BigDecimal.ZERO;
    BigDecimal onlineSaleInAmount=BigDecimal.ZERO;
    BigDecimal onlineSaleInTotal=BigDecimal.ZERO;

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

    public BigInteger getGoodsId(){
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId){
        this.goodsId = goodsId;
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

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }

    public Date getInvoicingDate(){
        return invoicingDate;
    }

    public void setInvoicingDate(Date invoicingDate){
        this.invoicingDate = invoicingDate;
    }

    public BigDecimal getBeginQuantity(){
        return beginQuantity;
    }

    public void setBeginQuantity(BigDecimal beginQuantity){
        this.beginQuantity = beginQuantity;
    }

    public BigDecimal getBeginAmount(){
        return beginAmount;
    }

    public void setBeginAmount(BigDecimal beginAmount){
        this.beginAmount = beginAmount;
    }

    public BigDecimal getEndQuantity(){
        return endQuantity;
    }

    public void setEndQuantity(BigDecimal endQuantity){
        this.endQuantity = endQuantity;
    }

    public BigDecimal getEndAmount(){
        return endAmount;
    }

    public void setEndAmount(BigDecimal endAmount){
        this.endAmount = endAmount;
    }

    public BigDecimal getCgInQuantity(){
        return cgInQuantity;
    }

    public void setCgInQuantity(BigDecimal cgInQuantity){
        this.cgInQuantity = cgInQuantity;
    }

    public BigDecimal getCgInAmount(){
        return cgInAmount;
    }

    public void setCgInAmount(BigDecimal cgInAmount){
        this.cgInAmount = cgInAmount;
    }

    public BigDecimal getCgOutQuantity(){
        return cgOutQuantity;
    }

    public void setCgOutQuantity(BigDecimal cgOutQuantity){
        this.cgOutQuantity = cgOutQuantity;
    }

    public BigDecimal getCgOutAmount(){
        return cgOutAmount;
    }

    public void setCgOutAmount(BigDecimal cgOutAmount){
        this.cgOutAmount = cgOutAmount;
    }

    public BigDecimal getLyInQuantity(){
        return lyInQuantity;
    }

    public void setLyInQuantity(BigDecimal lyInQuantity){
        this.lyInQuantity = lyInQuantity;
    }

    public BigDecimal getLyInAmount(){
        return lyInAmount;
    }

    public void setLyInAmount(BigDecimal lyInAmount){
        this.lyInAmount = lyInAmount;
    }

    public BigDecimal getLyOutQuantity(){
        return lyOutQuantity;
    }

    public void setLyOutQuantity(BigDecimal lyOutQuantity){
        this.lyOutQuantity = lyOutQuantity;
    }

    public BigDecimal getLyOutAmount(){
        return lyOutAmount;
    }

    public void setLyOutAmount(BigDecimal lyOutAmount){
        this.lyOutAmount = lyOutAmount;
    }

    public BigDecimal getPsInQuantity(){
        return psInQuantity;
    }

    public void setPsInQuantity(BigDecimal psInQuantity){
        this.psInQuantity = psInQuantity;
    }

    public BigDecimal getPsInAmount(){
        return psInAmount;
    }

    public void setPsInAmount(BigDecimal psInAmount){
        this.psInAmount = psInAmount;
    }

    public BigDecimal getPsOutQuantity(){
        return psOutQuantity;
    }

    public void setPsOutQuantity(BigDecimal psOutQuantity){
        this.psOutQuantity = psOutQuantity;
    }

    public BigDecimal getPsOutAmount(){
        return psOutAmount;
    }

    public void setPsOutAmount(BigDecimal psOutAmount){
        this.psOutAmount = psOutAmount;
    }

    public BigDecimal getJgInQuantity(){
        return jgInQuantity;
    }

    public void setJgInQuantity(BigDecimal jgInQuantity){
        this.jgInQuantity = jgInQuantity;
    }

    public BigDecimal getJgInAmount(){
        return jgInAmount;
    }

    public void setJgInAmount(BigDecimal jgInAmount){
        this.jgInAmount = jgInAmount;
    }

    public BigDecimal getJgOutQuantity(){
        return jgOutQuantity;
    }

    public void setJgOutQuantity(BigDecimal jgOutQuantity){
        this.jgOutQuantity = jgOutQuantity;
    }

    public BigDecimal getJgOutAmount(){
        return jgOutAmount;
    }

    public void setJgOutAmount(BigDecimal jgOutAmount){
        this.jgOutAmount = jgOutAmount;
    }

    public BigDecimal getSyInQuantity(){
        return syInQuantity;
    }

    public void setSyInQuantity(BigDecimal syInQuantity){
        this.syInQuantity = syInQuantity;
    }

    public BigDecimal getSyInAmount(){
        return syInAmount;
    }

    public void setSyInAmount(BigDecimal syInAmount){
        this.syInAmount = syInAmount;
    }

    public BigDecimal getSyOutQuantity(){
        return syOutQuantity;
    }

    public void setSyOutQuantity(BigDecimal syOutQuantity){
        this.syOutQuantity = syOutQuantity;
    }

    public BigDecimal getSyOutAmount(){
        return syOutAmount;
    }

    public void setSyOutAmount(BigDecimal syOutAmount){
        this.syOutAmount = syOutAmount;
    }

    public BigDecimal getPdInQuantity(){
        return pdInQuantity;
    }

    public void setPdInQuantity(BigDecimal pdInQuantity){
        this.pdInQuantity = pdInQuantity;
    }

    public BigDecimal getPdInAmount(){
        return pdInAmount;
    }

    public void setPdInAmount(BigDecimal pdInAmount){
        this.pdInAmount = pdInAmount;
    }

    public BigDecimal getPdOutQuantity(){
        return pdOutQuantity;
    }

    public void setPdOutQuantity(BigDecimal pdOutQuantity){
        this.pdOutQuantity = pdOutQuantity;
    }

    public BigDecimal getPdOutAmount(){
        return pdOutAmount;
    }

    public void setPdOutAmount(BigDecimal pdOutAmount){
        this.pdOutAmount = pdOutAmount;
    }

    public BigDecimal getSaleOutQuantity(){
        return saleOutQuantity;
    }

    public void setSaleOutQuantity(BigDecimal saleOutQuantity){
        this.saleOutQuantity = saleOutQuantity;
    }

    public BigDecimal getSaleOutAmount(){
        return saleOutAmount;
    }

    public void setSaleOutAmount(BigDecimal saleOutAmount){
        this.saleOutAmount = saleOutAmount;
    }

    public BigDecimal getSaleOutTotal(){
        return saleOutTotal;
    }

    public void setSaleOutTotal(BigDecimal saleOutTotal){
        this.saleOutTotal = saleOutTotal;
    }

    public BigDecimal getSaleInQuantity(){
        return saleInQuantity;
    }

    public void setSaleInQuantity(BigDecimal saleInQuantity){
        this.saleInQuantity = saleInQuantity;
    }

    public BigDecimal getSaleInAmount(){
        return saleInAmount;
    }

    public void setSaleInAmount(BigDecimal saleInAmount){
        this.saleInAmount = saleInAmount;
    }

    public BigDecimal getSaleInTotal(){
        return saleInTotal;
    }

    public void setSaleInTotal(BigDecimal saleInTotal){
        this.saleInTotal = saleInTotal;
    }

    public BigDecimal getOnlineSaleOutQuantity(){
        return onlineSaleOutQuantity;
    }

    public void setOnlineSaleOutQuantity(BigDecimal onlineSaleOutQuantity){
        this.onlineSaleOutQuantity = onlineSaleOutQuantity;
    }

    public BigDecimal getOnlineSaleOutAmount(){
        return onlineSaleOutAmount;
    }

    public void setOnlineSaleOutAmount(BigDecimal onlineSaleOutAmount){
        this.onlineSaleOutAmount = onlineSaleOutAmount;
    }

    public BigDecimal getOnlineSaleOutTotal(){
        return onlineSaleOutTotal;
    }

    public void setOnlineSaleOutTotal(BigDecimal onlineSaleOutTotal){
        this.onlineSaleOutTotal = onlineSaleOutTotal;
    }

    public BigDecimal getOnlineSaleInQuantity(){
        return onlineSaleInQuantity;
    }

    public void setOnlineSaleInQuantity(BigDecimal onlineSaleInQuantity){
        this.onlineSaleInQuantity = onlineSaleInQuantity;
    }

    public BigDecimal getOnlineSaleInAmount(){
        return onlineSaleInAmount;
    }

    public void setOnlineSaleInAmount(BigDecimal onlineSaleInAmount){
        this.onlineSaleInAmount = onlineSaleInAmount;
    }

    public BigDecimal getOnlineSaleInTotal(){
        return onlineSaleInTotal;
    }

    public void setOnlineSaleInTotal(BigDecimal onlineSaleInTotal){
        this.onlineSaleInTotal = onlineSaleInTotal;
    }
}
