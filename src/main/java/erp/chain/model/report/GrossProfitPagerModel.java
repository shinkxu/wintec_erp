package erp.chain.model.report;

import erp.chain.model.SearchModel;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

/**
 * 查询商品毛利分析报表 model
 * @author hxh
 * @since 1.5.0
 */

public class GrossProfitPagerModel extends SearchModel {
    @NotNull(message = "商户不能为空！")
    public BigInteger tenantId;

    @NotNull(message = "branchId不能为空！")
    public BigInteger branchId;

    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2}", message = "格式错误")
    @NotNull(message = "开始时间不能为空")
    public String startDate;

    @Pattern(regexp = "\\d{4}-\\d{1,2}-\\d{1,2}", message = "格式错误")
    @NotNull(message = "结束时间不能为空")
    public String endDate;

    public String codeOrName;

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

    public String getCodeOrName() {
        return like(codeOrName);
    }

    public void setCodeOrName(String codeOrName) {
        this.codeOrName = codeOrName;
    }
}
