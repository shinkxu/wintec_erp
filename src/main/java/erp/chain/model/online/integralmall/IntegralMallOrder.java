package erp.chain.model.online.integralmall;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by Lip on 2018/11/8.
 */
public class IntegralMallOrder extends BasicModel {

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger id;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
