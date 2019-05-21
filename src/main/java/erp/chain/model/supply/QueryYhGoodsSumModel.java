package erp.chain.model.supply;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

/**
 * 分页查询要货单汇总
 */
public class QueryYhGoodsSumModel extends SearchModel {

    @NotNull
    public BigInteger tenantId;
    /**
     * 配送机构
     */
    @NotNull
    public String psBranchId;
    /**
     * 要货机构 格式：yhBranchId1,yhBranchId2,yhBranchId3
     *
     */
    @Pattern(regexp = "\\d+(,\\d+)*?", message = "格式错误")
    public String yhBranchIds;

    /**
     * 要货单状态:1-已审核 2-已付款
     */
    //@In(value = {"1","2"})
    public String yhOrderStatus;

    /**
     * 最小创建时间（格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String startDate;
    /**
     * 最大创建时间（格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String endDate;

    /**
     * 配送状态 0-未配送，1-已配送
     * */
    @In(value = {"0","1"})
    public String psStatus;

    public int onlySelf;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getYhBranchIds() {
        return yhBranchIds;
    }

    public void setYhBranchIds(String yhBranchIds) {
        this.yhBranchIds = yhBranchIds;
    }

    public String getYhOrderStatus() {
        return yhOrderStatus;
    }

    public void setYhOrderStatus(String yhOrderStatus) {
        this.yhOrderStatus = yhOrderStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPsBranchId() {
        return psBranchId;
    }

    public void setPsBranchId(String psBranchId) {
        this.psBranchId = psBranchId;
    }

    public String [] getYhBranchIdsArray(){
        if (this.yhBranchIds != null){
            return yhBranchIds.split(",");
        }
        return null;
    }

    public String getPsStatus() {
        return psStatus;
    }

    public void setPsStatus(String psStatus) {
        this.psStatus = psStatus;
    }

    public int getOnlySelf() {
        return onlySelf;
    }

    public void setOnlySelf(int onlySelf) {
        this.onlySelf = onlySelf;
    }
}
