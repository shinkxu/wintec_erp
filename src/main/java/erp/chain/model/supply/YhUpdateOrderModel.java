package erp.chain.model.supply;

import erp.chain.model.Model;
import org.hibernate.validator.constraints.Length;

import javax.validation.Validator;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * 要货单model
 */
public class YhUpdateOrderModel extends Model {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    @Length(min = 8,max = 8)
    public String tenantCode;
    @NotNull
    public BigInteger yhBranchId;

    /**
     * 单据号
     */
    @NotNull
    @Pattern(regexp = "YH\\d{12}", message = "单据格式错误")
    public String code;


    @NotNull
    @Size(min = 1, max = 200, message = "details最多200个")
    public List<Detail> details;

    /**
     * 修改人（员工id）
     */
    @NotNull
    public BigInteger empId;

    public Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public class Detail extends Model {
        /**
         * 配货价
         */
        @DecimalMin("0")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal price;

        /**
         * 要货数量
         */
        @DecimalMin("0.001")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal quantity;

        /**
         * 商品id
         */
        @NotNull
        public BigInteger goodsId;
        /**
         * 要货备注
         */
        @Size(max = 20)
        public String memo;

        /**
         * 单位类型 1-销售单位，2-包装单位
         * */
        public BigInteger unitType;

        /**
         * 单位换算 1销售单位=？*包装单位
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

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            if (price != null){
                this.price = price.setScale(3, BigDecimal.ROUND_HALF_UP);
            }
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

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
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
}
