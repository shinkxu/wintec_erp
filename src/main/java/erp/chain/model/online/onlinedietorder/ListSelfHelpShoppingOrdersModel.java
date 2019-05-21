package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-05-18.
 */
public class ListSelfHelpShoppingOrdersModel extends BasicModel {
    private static final Integer[] TYPES = {1, 2, 3};
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger vipId;

    @NotNull
    @Min(value = 1)
    private Integer page;

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private Integer rows;

    @NotNull
    private Integer type;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(TYPES, type, "type");
    }
}
