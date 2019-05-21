package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.GsonUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-05-16.
 */
public class SaveSelfHelpShoppingOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger vipId;

    @NotEmpty
    private List<GoodsInfo> goodsInfos;

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public void setGoodsInfos(String goodsInfos) {
        this.goodsInfos = GsonUtils.jsonToList(goodsInfos, GoodsInfo.class);
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (GoodsInfo goodsInfo : goodsInfos) {
            ApplicationHandler.isTrue(goodsInfo.validate(), "goodsInfos");
        }
    }

    public static class GoodsInfo extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private BigDecimal quantity;

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
    }
}
