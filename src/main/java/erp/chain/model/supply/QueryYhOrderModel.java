package erp.chain.model.supply;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询要货单
 */
public class QueryYhOrderModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    /**
     * 要货机构（即是要货机构又是配送机构）
     */
    @NotNull
    public BigInteger yhBranchId;
    /**
     * 配送机构
     */
    public BigInteger psBranchId;
    /**
     * 最小要货金额
     */
    public BigDecimal amountMin;
    /**
     * 最大要货金额
     */
    public BigDecimal amountMax;

    /**
     * 编号模糊查询
     */
    public String codeLike;
    /**
     * 状态0-未提交，1-已审核 2-已付款 3-已配送 4-已收货 5-已提交
     */
    @In(value = {"0","1","2","3","4","5"})
    public String status;

    public String statusList;

    /**
     * 支付类型：1-支付宝支付，2-微信支付，3-银联支付
     */
    @In(value = {"1","2","3"})
    public String payType;

    /**
     * 最小创建时间（格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String startDate;
    /**
     * 最大创建时间格式：yyyy-MM-dd hh:mm）
     */
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull()
    public String endDate;

    /**
     * 制单人ID（员工ID）
     */
    public BigInteger empId;

    public String code;
    /**
     * 出库次数，大于0则不再进行出库
     */
    public BigInteger psNum;

    /**
     * 要货单需要付款后才允许配送
     * */
    public String isPayDispatching;

    /**
     * 配送状态 0-未配送，1-已配送
     * */
    @In(value = {"0","1"})
    public String psStatus;

    public String getStatusList(){
        return statusList;
    }

    public void setStatusList(String statusList){
        this.statusList = statusList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getYhBranchId() {
        return yhBranchId;
    }

    public void setYhBranchId(BigInteger yhBranchId) {
        this.yhBranchId = yhBranchId;
    }

    public BigInteger getPsBranchId() {
        return psBranchId;
    }

    public void setPsBranchId(BigInteger psBranchId) {
        this.psBranchId = psBranchId;
    }

    public BigDecimal getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(BigDecimal amountMin) {
        this.amountMin = amountMin;
    }

    public BigDecimal getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(BigDecimal amountMax) {
        this.amountMax = amountMax;
    }

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
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

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

    public BigInteger getPsNum() {
        return psNum;
    }

    public void setPsNum(BigInteger psNum) {
        this.psNum = psNum;
    }

    public String getIsPayDispatching() {
        return isPayDispatching;
    }

    public void setIsPayDispatching(String ispayDispatching) {
        isPayDispatching = ispayDispatching;
    }

    public String getPsStatus() {
        return psStatus;
    }

    public void setPsStatus(String psStatus) {
        this.psStatus = psStatus;
    }
}
