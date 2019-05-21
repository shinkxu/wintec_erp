package erp.chain.domain.supply;
import erp.chain.utils.BaseDomain;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 机构要货关系
 *
 * @author hefuzi 2016-12-15
 */
public class BranchRequireGoodsSetting extends BaseDomain {
    /**
     * 配送中心ID
     */
    private BigInteger distributionCenterId;
    /**
     * 机构ID
     */
    private BigInteger branchId;
    /**
     * 商品ID
     */
    private BigInteger goodsId;
    /**
     * 配货价
     */
    private BigDecimal shippingPrice;


    public BigInteger getDistributionCenterId() {
        return this.distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public BigInteger getBranchId() {
        return this.branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getGoodsId() {
        return this.goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getShippingPrice() {
        return this.shippingPrice;
    }

    public void setShippingPrice(BigDecimal shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

}