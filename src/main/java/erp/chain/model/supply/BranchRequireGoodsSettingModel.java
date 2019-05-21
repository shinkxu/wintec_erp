package erp.chain.model.supply;

import erp.chain.model.Model;

import javax.validation.Validator;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * 机构要货关系model
 */
public class BranchRequireGoodsSettingModel extends Model {
    @NotNull
    public BigInteger tenantId;
    /**
     * 要货机构
     */
    @NotNull
    public BigInteger branchId;
    /**
     * 配送机构
     */
    @NotNull
    public BigInteger distributionCenterId;

    /**
     * 制单人（员工id）
     */
    @NotNull
    public BigInteger empId;

    @NotNull
    public List<Detail> details;

    public class Detail extends Model {
        /**
         * 配货价
         */
        @DecimalMin("0")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal price;

        /**
         * 商品id
         */
        @NotNull
        public BigInteger goodsId;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price.setScale(3, BigDecimal.ROUND_HALF_UP);
        }

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }
    }
    @Override
    public boolean validate(Validator validator) {
        if (!super.validate(validator)) {
            return false;
        }
        if (this.distributionCenterId.compareTo(this.branchId) == 0) {
            this.errors.put("distributionCenterId", "distributionCenterId和branchId不能重复");
            return false;
        }
        HashSet<BigInteger> set = new HashSet<>();
        for (Detail detail : details) {
            if (detail.validate(validator)) {
                if (!set.add(detail.goodsId)) {
                    this.errors.put("goodsId", String.format("商品id=%s重复", detail.goodsId.toString()));
                    return false;
                }
            } else {
                this.errors.putAll(detail.getErrors());
                return false;
            }
        }
        return true;
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

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }
}
