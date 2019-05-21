package erp.chain.domain;

import java.math.BigInteger;

/**
 * Created by lipeng on 2017/5/12.
 *
 * 商品对应关系表
 */
public class GoodsMapping extends BaseDomain {

    /**
     * 商户ID
     * */
    BigInteger tenantId;

    /**
     * 配送中心ID
     * */
    BigInteger distributionCenterId;

    /**
     * 本地商品ID
     * */
    BigInteger ourGoodsId;

    /**
     * 第三方商品ID
     * */
    String otherGoodsId;

    String goodsCode;
    String goodsName;
    BigInteger typeId;
    String fullName;
    private String unitId;

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setTypeId(BigInteger typeId) {
        this.typeId = typeId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public String getFullName() {
        return fullName;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public BigInteger getOurGoodsId() {
        return ourGoodsId;
    }

    public String getOtherGoodsId() {
        return otherGoodsId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public void setOurGoodsId(BigInteger ourGoodsId) {
        this.ourGoodsId = ourGoodsId;
    }

    public void setOtherGoodsId(String otherGoodsId) {
        this.otherGoodsId = otherGoodsId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
