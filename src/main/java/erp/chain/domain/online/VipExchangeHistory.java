package erp.chain.domain.online;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-10-31.
 */
public class VipExchangeHistory {
    private BigInteger id;
    private BigInteger tenantId;
    private BigInteger branchId;
    private BigInteger goodsId;
    private BigInteger vipId;
    private BigDecimal exchangeQuantity;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigDecimal getExchangeQuantity() {
        return exchangeQuantity;
    }

    public void setExchangeQuantity(BigDecimal exchangeQuantity) {
        this.exchangeQuantity = exchangeQuantity;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
