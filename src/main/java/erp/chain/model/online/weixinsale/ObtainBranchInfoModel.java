package erp.chain.model.online.weixinsale;

import erp.chain.model.online.BasicModel;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-06-23.
 */
public class ObtainBranchInfoModel extends BasicModel {
    private BigInteger tenantId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
