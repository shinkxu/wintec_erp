package erp.chain.model.supply.store;

import erp.chain.model.Model;
import erp.chain.utils.In;
import org.hibernate.validator.constraints.Length;

import javax.validation.Validator;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * 损溢model
 */
public class SyOrderModel extends Model {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;

    /**
     * 单据号
     */
    @NotNull
    @Pattern(regexp = "[SY]{2}\\d{12}", message = "单据格式错误")
    public String code;


    @NotNull
    @Size(min = 1, max = 1000, message = "details最多1000个")
    public List<Detail> details;

    /**
     * 制单人（员工id）
     */
    @NotNull
    public BigInteger empId;

    @Length(max = 200)
    public String memo;

    public class Detail extends Model {

        /**
         * 损溢数量
         */
        @DecimalMin("-9999999.999")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal quantity;

        /**
         * 商品id
         */
        @NotNull
        public BigInteger goodsId;

        /**
         * 单位类型 1-销售单位，2-包装单位
         * */
        public BigInteger unitType;

        /**
         * 单位换算 1包装单位=？*销售单位
         * */
        public BigDecimal unitRelation;

        public BigInteger getUnitType() {
            return unitType;
        }

        public BigDecimal getUnitRelation() {
            return unitRelation;
        }

        public void setUnitType(BigInteger unitType) {
            this.unitType = unitType;
        }

        public void setUnitRelation(BigDecimal unitRelation) {
            this.unitRelation = unitRelation;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            if (quantity != null) {
                this.quantity = quantity.setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        }

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }
    }

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @Override
    public boolean validate(Validator validator) {
        if (!super.validate(validator)) {
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
