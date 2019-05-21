package erp.chain.model.supply.store;


import javax.validation.constraints.NotNull;

/**
 * 配送model
 */
public class PsUpdateOrderModel extends PsOrderModel {
    /**
     *
     */
    @NotNull
    public Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
