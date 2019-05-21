package erp.chain.model.online.onlinevip;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

/**
 * Created by liuyandong on 2018-08-01.
 */
public class ListFestivalsModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    private BigInteger branchId;

    @NotNull
    private BigInteger vipId;

    private BigDecimal receivedAmount;

    private Date payAt;

    public Date getPayAt(){
        return payAt;
    }

    public void setPayAt(Date payAt){
        this.payAt = payAt;
    }

    public BigDecimal getReceivedAmount(){
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount){
        this.receivedAmount = receivedAmount;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }
}
