package erp.chain.model.base;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 分页查询商品加工单model
 *
 * @author hefuzi 2016-11-29
 */
public class QueryGoodsProduceOrderModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    /**
     *
     */
    @NotNull
    public BigInteger branchId;
    public BigInteger orderId;
    /**
     * 编号模糊查询
     */
    public String codeLike;
    /**
     * 状态：1-未审核 2-已审核
     */
//    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    @In(value = {"1", "2"})
    public Integer status;
    /**
     * 类型：1-组合 2-拆分
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    public Integer type;
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
    public BigInteger makeEmpId;
    /**
     * 审核人ID（员工ID）
     */
    public BigInteger auditEmpId;

    public String code;

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

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
    }

    public Integer getStatus() {
        return status;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public BigInteger getMakeEmpId() {
        return makeEmpId;
    }

    public void setMakeEmpId(BigInteger makeEmpId) {
        this.makeEmpId = makeEmpId;
    }

    public BigInteger getAuditEmpId() {
        return auditEmpId;
    }

    public void setAuditEmpId(BigInteger auditEmpId) {
        this.auditEmpId = auditEmpId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}