package erp.chain.model.supply;

import erp.chain.model.SearchModel;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 分页查询model
 *
 * @author hefuzi 2016-12-15
 */
public class QueryBranchRequireGoodsSettingModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger distributionCenterId;
    public BigInteger branchId;
    /**
     * 要货机构 格式：1,2,3
     */
    @Pattern(regexp = "\\d+(,\\d+)?", message = "格式错误")
    public String branchIds;
    public String branchCodeOrNameLike;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getBranchCodeOrNameLike() {
        return like(branchCodeOrNameLike);
    }

    public void setBranchCodeOrNameLike(String branchCodeOrNameLike) {
        this.branchCodeOrNameLike = branchCodeOrNameLike;
    }

    public String getBranchIds() {
        return branchIds;
    }

    public String [] getBranchIdsArray() {
        if (branchIds != null){
            return branchIds.split(",");
        }
        return null;
    }

    public void setBranchIds(String branchIds) {
        this.branchIds = branchIds;
    }

    public int onlySelf;

    public int getOnlySelf() {
        return onlySelf;
    }

    public void setOnlySelf(int onlySelf) {
        this.onlySelf = onlySelf;
    }
}