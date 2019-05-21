package erp.chain.model.online.weixinsale;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ValidateUtils;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-06-22.
 */
public class ObtainWeiXinSaleOrderModel extends BasicModel {
    private BigInteger orderId;
    private String orderCode;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ValidateUtils.isTrue(orderId != null || orderCode != null, "参数orderId 和 orderCode 不能同时为空！");
    }
}
