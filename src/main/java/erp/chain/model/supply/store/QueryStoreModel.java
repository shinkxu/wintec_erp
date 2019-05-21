package erp.chain.model.supply.store;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询库存model
 */
public class QueryStoreModel extends erp.chain.model.SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;

    /**
     * 商品分类id集合，不同分类按','隔开，格式：catId1,catId2
     */
    @Pattern(regexp = "\\d+(,\\d+)?", message = "格式错误")
    public String catIds;

    /**
     * 最小成本
     */
    public BigDecimal incurredMin;
    /**
     * 最大成本
     */
    public BigDecimal incurredMax;
    /**
     * 最小数量
     */
    public BigDecimal quantityMin;
    /**
     * 最大数量
     */
    public BigDecimal quantityMax;
    /**
     * 最小金额
     */
    public BigDecimal amountMin;
    /**
     * 最大金额
     */
    public BigDecimal amountMax;

    /**
     * 商品编号或名称模糊查询
     */
    public String goodsNameOrCodeLike;

    /**
     * 是否选择供应商
     * *//*
    public Integer isSupplier;*/

    /**
     * 供应商ID
     * */
    public String supplierId;

    /**
     * 是否使用总部菜品
     * */
    public  int onlySelf;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getCatIds() {
        return catIds;
    }

    public void setCatIds(String catIds) {
        this.catIds = catIds;
    }

    public BigDecimal getIncurredMin() {
        return incurredMin;
    }

    public void setIncurredMin(BigDecimal incurredMin) {
        this.incurredMin = incurredMin;
    }

    public BigDecimal getIncurredMax() {
        return incurredMax;
    }

    public void setIncurredMax(BigDecimal incurredMax) {
        this.incurredMax = incurredMax;
    }

    public BigDecimal getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(BigDecimal quantityMin) {
        this.quantityMin = quantityMin;
    }

    public BigDecimal getQuantityMax() {
        return quantityMax;
    }

    public void setQuantityMax(BigDecimal quantityMax) {
        this.quantityMax = quantityMax;
    }

    public BigDecimal getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(BigDecimal amountMin) {
        this.amountMin = amountMin;
    }

    public BigDecimal getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(BigDecimal amountMax) {
        this.amountMax = amountMax;
    }

    public String getGoodsNameOrCodeLike() {
        return like(goodsNameOrCodeLike);
    }

    public void setGoodsNameOrCodeLike(String goodsNameOrCodeLike) {
        this.goodsNameOrCodeLike = goodsNameOrCodeLike;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String [] getCatIdArray(){
        if (this.catIds != null){
            return catIds.split(",");
        }
        return null;
    }

    public int getOnlySelf() {
        return onlySelf;
    }

    public void setOnlySelf(int onlySelf) {
        this.onlySelf = onlySelf;
    }
}
