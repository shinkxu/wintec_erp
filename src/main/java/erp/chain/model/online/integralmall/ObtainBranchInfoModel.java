package erp.chain.model.online.integralmall;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-10-29.
 */
public class ObtainBranchInfoModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
