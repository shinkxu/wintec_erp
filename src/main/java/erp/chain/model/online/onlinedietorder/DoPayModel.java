package erp.chain.model.online.onlinedietorder;

import erp.chain.common.Constants;
import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-11.
 */
public class DoPayModel extends BasicModel {
    private static final String[] TRADE_TYPES = {Constants.MINIPROGRAM, Constants.NATIVE, Constants.APP, Constants.MWEB, Constants.JSAPI};

    @NotNull
    private BigInteger orderId;

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private String tradeType;

    private String subOpenId;

    private String openId;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(TRADE_TYPES, tradeType, "tradeType");
        if (Constants.MINIPROGRAM.equals(tradeType)) {
            ApplicationHandler.notBlank(subOpenId, "subOpenId");
        } else {
            ApplicationHandler.notBlank(openId, "openId");
        }
    }
}
