package erp.chain.domain.supply.store;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 商品及库存信息
 */
public class GoodsStoreInfo {
    private BigInteger goodsId;
    private Boolean isStore;
    /**
     * 组合拆分类型0-无1-组合2-拆分3-配方
     */
    private Integer combinationType;
    private BigInteger storeId;
    private BigInteger tenantId;
    private BigInteger branchId;
    private Long storeVersion;
    /**
     * 商品机构id
     */
    private String goodsBranchCode;
    /**
     * 库存数量
     */
    private BigDecimal quantity;
    /**
     * 库存数量
     */
    private BigDecimal avgAmount;
    /**
     * 库存金额
     */
    private BigDecimal storeAmount;

    /**
     * 商品进货价
     */
    private BigDecimal purchasingPrice;

    /**
     * 配方商品明细ID
     */
    private String relationGoodsIds;

    public String getGoodsBranchCode() {
        return goodsBranchCode;
    }

    public void setGoodsBranchCode(String goodsBranchCode) {
        this.goodsBranchCode = goodsBranchCode;
    }

    public String getRelationGoodsIds() {
        return relationGoodsIds;
    }

    public void setRelationGoodsIds(String relationGoodsIds) {
        this.relationGoodsIds = relationGoodsIds;
    }

    public Boolean getStore() {
        return isStore;
    }

    public void setStore(Boolean store) {
        isStore = store;
    }

    public Integer getCombinationType() {
        return combinationType;
    }

    public void setCombinationType(Integer combinationType) {
        this.combinationType = combinationType;
    }

    public BigDecimal getPurchasingPrice() {
        return purchasingPrice;
    }

    public void setPurchasingPrice(BigDecimal purchasingPrice) {
        this.purchasingPrice = purchasingPrice;
    }

    public BigInteger getStoreId() {
        return storeId;
    }

    public void setStoreId(BigInteger storeId) {
        this.storeId = storeId;
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

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public Long getStoreVersion() {
        return storeVersion;
    }

    public void setStoreVersion(Long storeVersion) {
        this.storeVersion = storeVersion;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }

    public BigDecimal getStoreAmount() {
        return storeAmount;
    }

    public void setStoreAmount(BigDecimal storeAmount) {
        this.storeAmount = storeAmount;
    }
}