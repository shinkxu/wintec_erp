package erp.chain.model.supply.store;

import javax.validation.constraints.*;

/**
 * 采购单model
 */
public class CgUpdateOrderModel extends CgOrderModel{

    @NotNull
    public Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
