package erp.chain.model.online.integralmall;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ValidateUtils;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by Lip on 2018-11-07.
 */
public class SettingGoodsModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    private String startDate;

    private String endDate;

    private Integer goodsStatus;

    @NotNull
    private Integer page;

    @NotNull
    private Integer rows;

    @NotNull
    private Integer isTicket;

    private List<Map> goodsList;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(Integer isTicket) {
        this.isTicket = isTicket;
    }

    public List<Map> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Map> goodsList) {
        this.goodsList = goodsList;
    }
}
