package erp.chain.model.supply.store;


import javax.validation.constraints.NotNull;

/**
 * 盘点model
 */
public class PdUpdateOrderModel extends PdOrderModel {

    @NotNull
    public Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
