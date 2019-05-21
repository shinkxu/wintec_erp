package erp.chain.model.report;

import erp.chain.model.SearchModel;
import erp.chain.utils.In;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

/**
 * 赠送退菜
 */
public class GiveAndReturnPagerModel extends SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;
    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull
    public String startDate;

    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}", message = "格式错误")
    @NotNull
    public String endDate;

    @Min(value = 1L, message = "必须大于等于1")
    public Integer page;

    public String codeLike;

    /**
     * 1:退菜,2:赠送
     */
    @In({"1","2"})
    public String type;

    public String empId;

    @Min(value = 1L, message = "必须大于等于1")
    public Integer rows;

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getCodeLike() {
        return like(codeLike);
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
