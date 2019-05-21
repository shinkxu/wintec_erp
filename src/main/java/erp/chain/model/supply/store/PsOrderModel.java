package erp.chain.model.supply.store;

import erp.chain.model.Model;
import erp.chain.utils.In;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.Validator;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * 配送model
 */
public class PsOrderModel extends Model {
    @NotNull
    public BigInteger tenantId;
    /**
     * 出入库机构"
     */
    @NotNull
    public BigInteger branchId;
    /**
     * 收发货机构
     */
    @NotNull
    public BigInteger targetBranchId;

    /**
     * 单据号
     */
    @NotNull
    @Pattern(regexp = "P[CR]\\d{12}", message = "单据格式错误")
    public String code;
    /**
     * 要货单号/配送入库单号
     */
    public String requireGoodsCode;

    /**
     * 单据类型(1-入库，2-出库)
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    @NotNull
    public Integer type;

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

    /**
     * 是否修改商品档案
     * */
    public Integer isUpdatePrice;

    public class Detail extends Model {

        /**
         * 出入库数量
         */
        @DecimalMin("0")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal quantity;
        /**
         * 配送价
         */
        @DecimalMin("0")
        @DecimalMax("9999999.999")
        @NotNull
        public BigDecimal distributionPrice;

        /**
         * 商品id
         */
        @NotNull
        public BigInteger goodsId;

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            if (quantity != null) {
                this.quantity = quantity.setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        }

        public BigDecimal getDistributionPrice() {
            return distributionPrice;
        }

        public void setDistributionPrice(BigDecimal distributionPrice) {
            if (distributionPrice != null) {
                this.distributionPrice = distributionPrice.setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        }

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public Integer getIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(Integer isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
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
        if (type == 2 && StringUtils.trimToEmpty(requireGoodsCode).isEmpty()){
            this.errors.put("requireGoodsCode", "requireGoodsCode 不能为null");
        }
        return true;
    }

    public BigInteger getTargetBranchId() {
        return targetBranchId;
    }

    public void setTargetBranchId(BigInteger targetBranchId) {
        this.targetBranchId = targetBranchId;
    }

    public String getRequireGoodsCode() {
        return requireGoodsCode;
    }

    public void setRequireGoodsCode(String requireGoodsCode) {
        this.requireGoodsCode = requireGoodsCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
