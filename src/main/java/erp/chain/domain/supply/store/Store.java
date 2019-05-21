package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 商品库存实体类
 */
public class Store extends BaseDomain {
    public BigInteger branchId;
    public BigInteger goodsId;
    public BigDecimal quantity;
    public BigDecimal avgAmount;
    public BigDecimal storeAmount;
    public Date storeAt;

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

    public Date getStoreAt() {
        return storeAt;
    }

    public void setStoreAt(Date storeAt) {
        this.storeAt = storeAt;
    }
}
