package erp.chain.model.online.integralmall;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-10-25.
 */
public class SaveOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;
    @NotNull
    private BigInteger vipId;
    @NotNull
    private BigInteger goodsId;
    @NotNull
    @DecimalMin(value = "1")
    private BigDecimal quantity;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
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
}
