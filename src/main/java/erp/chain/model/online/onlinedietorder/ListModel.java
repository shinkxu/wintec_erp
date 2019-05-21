package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import org.apache.commons.lang.ArrayUtils;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-05-04.
 */
public class ListModel extends BasicModel {
    private static final String[] TYPES = {"1", "2"};
    @NotNull
    private BigInteger vipId;
    @NotNull
    private String type;

    @NotNull
    private Integer page;

    @NotNull
    private Integer rows;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean validate() {
        return super.validate() && ArrayUtils.contains(TYPES, type);
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(TYPES, type, "type");
    }
}
