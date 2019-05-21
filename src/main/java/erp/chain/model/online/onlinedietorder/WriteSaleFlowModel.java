package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-05-24.
 */
public class WriteSaleFlowModel extends BasicModel {
    @NotNull
    private BigInteger dietOrderInfoId;

    public BigInteger getDietOrderInfoId() {
        return dietOrderInfoId;
    }

    public void setDietOrderInfoId(BigInteger dietOrderInfoId) {
        this.dietOrderInfoId = dietOrderInfoId;
    }
}
