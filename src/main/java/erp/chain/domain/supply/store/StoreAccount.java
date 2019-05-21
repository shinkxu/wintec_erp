package erp.chain.domain.supply.store;

import erp.chain.utils.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 库存流水domain
 */
public class StoreAccount extends BaseDomain{
    public BigInteger branchId;
    public BigInteger goodsId;
    public BigDecimal storeAmount;
    public BigDecimal storeIncurred;
    public BigDecimal storeQuantity;
    public BigDecimal occurIncurred;
    public BigDecimal occurQuantity;
    public BigDecimal occurAmount;
    public Date occurAt;
    public Date storeAccountAt;
    public int occurType;
    public String occurCode;

    public String getOccurCode() {
        return occurCode;
    }

    public void setOccurCode(String occurCode) {
        this.occurCode = occurCode;
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

    public BigDecimal getStoreAmount() {
        return storeAmount;
    }

    public void setStoreAmount(BigDecimal storeAmount) {
        this.storeAmount = storeAmount;
    }

    public BigDecimal getStoreIncurred() {
        return storeIncurred;
    }

    public void setStoreIncurred(BigDecimal storeIncurred) {
        this.storeIncurred = storeIncurred;
    }

    public BigDecimal getStoreQuantity() {
        return storeQuantity;
    }

    public void setStoreQuantity(BigDecimal store1uantity) {
        this.storeQuantity = store1uantity;
    }

    public BigDecimal getOccurIncurred() {
        return occurIncurred;
    }

    public void setOccurIncurred(BigDecimal occurIncurred) {
        this.occurIncurred = occurIncurred;
    }

    public BigDecimal getOccurQuantity() {
        return occurQuantity;
    }

    public void setOccurQuantity(BigDecimal occurQuantity) {
        this.occurQuantity = occurQuantity;
    }

    public BigDecimal getOccurAmount() {
        return occurAmount;
    }

    public void setOccurAmount(BigDecimal occurAmount) {
        this.occurAmount = occurAmount;
    }

    public Date getOccurAt() {
        return occurAt;
    }

    public void setOccurAt(Date occurAt) {
        this.occurAt = occurAt;
    }

    public Date getStoreAccountAt() {
        return storeAccountAt;
    }

    public void setStoreAccountAt(Date storeAccountAt) {
        this.storeAccountAt = storeAccountAt;
    }

    public int getOccurType() {
        return occurType;
    }

    public void setOccurType(int occurType) {
        this.occurType = occurType;
    }
}
