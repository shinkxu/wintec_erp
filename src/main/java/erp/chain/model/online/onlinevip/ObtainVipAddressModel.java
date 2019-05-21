package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-04-13.
 */
public class ObtainVipAddressModel extends BasicModel {
    @NotNull
    private BigInteger vipId;

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }
}
