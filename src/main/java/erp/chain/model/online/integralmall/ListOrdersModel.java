package erp.chain.model.online.integralmall;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ValidateUtils;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-11-07.
 */
public class ListOrdersModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    private Integer orderMode;

    private Integer orderStatus;

    private String startDate;

    private String endDate;

    private String orderCode;

    @NotNull
    private Integer page;

    @NotNull
    private Integer rows;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(Integer orderMode) {
        this.orderMode = orderMode;
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

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (orderMode != null) {
            ValidateUtils.isTrue(orderMode == 9 || orderMode == 10, "订单类型错误！");
        }
    }
}
