package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2019-02-15.
 */
public class ListWxReplyTemplatesModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
