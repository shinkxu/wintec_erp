package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-05-14.
 */
public class DoPayStoreOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger storeOrderId;

    @NotNull
    private String tradeType;

    @NotNull
    private String subOpenId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getStoreOrderId() {
        return storeOrderId;
    }

    public void setStoreOrderId(BigInteger storeOrderId) {
        this.storeOrderId = storeOrderId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getSubOpenId() {
        return subOpenId;
    }

    public void setSubOpenId(String subOpenId) {
        this.subOpenId = subOpenId;
    }
}
