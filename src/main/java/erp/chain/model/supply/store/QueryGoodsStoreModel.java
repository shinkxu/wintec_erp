package erp.chain.model.supply.store;


import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询库存model
 */
public class QueryGoodsStoreModel extends erp.chain.model.SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;
    /**
     * 总部机构
     */
    @NotNull
    public BigInteger rootBranchId;

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
     * 是否管理库存商品
     */
    @In(value = {"1","0"},message = "必须是[1,0]中一个")
    public Integer storeGoods;

    /**
     * 商品id
     */
    public BigInteger goodsId;

    /**
     * 商品编号或名称模糊查询
     */
    public String goodsNameOrCodeLike;

    /**
     * 条码
     */
    public String barCode;
    /**
     * 只读取总部商品
     */
    public Boolean onlyReadRootBranchGoods;

    /**
     * ids
     * */
    public String ids;

    public Boolean getOnlyReadRootBranchGoods() {
        return onlyReadRootBranchGoods;
    }

    public void setOnlyReadRootBranchGoods(Boolean onlyReadRootBranchGoods) {
        this.onlyReadRootBranchGoods = onlyReadRootBranchGoods;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public BigInteger getRootBranchId() {
        return rootBranchId;
    }

    public void setRootBranchId(BigInteger rootBranchId) {
        this.rootBranchId = rootBranchId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStoreGoods() {
        return storeGoods;
    }

    public void setStoreGoods(Integer storeGoods) {
        this.storeGoods = storeGoods;
    }

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
    public String [] getCatIdArray(){
        if (this.catIds != null){
            return catIds.split(",");
        }
        return null;
    }

    Integer onlySelf;

    public Integer getOnlySelf() {
        return onlySelf;
    }

    public void setOnlySelf(Integer onlySelf) {
        this.onlySelf = onlySelf;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
