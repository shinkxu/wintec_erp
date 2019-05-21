package erp.chain.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class SaleGoodsSpec extends BaseDomain {

    BigInteger saleDetailId;
    /**
     * 规格类型
     字典
     口味
     做法
     */
    BigInteger goodsSpecId;
    /**
     * 规格说明
     */
    String goodsSpecDescription;
    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger tenantId;

    public SaleGoodsSpec() {
        super();
    }

    public SaleGoodsSpec(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(BigInteger saleDetailId) {
        this.saleDetailId = saleDetailId;
    }

    public BigInteger getGoodsSpecId() {
        return goodsSpecId;
    }

    public void setGoodsSpecId(BigInteger goodsSpecId) {
        this.goodsSpecId = goodsSpecId;
    }

    public String getGoodsSpecDescription() {
        return goodsSpecDescription;
    }

    public void setGoodsSpecDescription(String goodsSpecDescription) {
        this.goodsSpecDescription = goodsSpecDescription;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
