package erp.chain.model.base;

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
 * 商品加工单model
 */
public class ProduceOrderModel extends Model {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;

    /**
     * 数量
     */
    @DecimalMax(value = "9999999.999")
    @DecimalMin(value = "0.001")
    @NotNull
    public BigDecimal quantity;

    /**
     * 单价
     */
    @DecimalMax(value = "9999999.999")
    @DecimalMin(value = "0")
    @NotNull
    public BigDecimal price;
    /**
     * 组合/拆分商品id
     */
    @NotNull
    public BigInteger goodsId;
    /**
     * 类型：1-组合 2-拆分
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]其中一个")
    @NotNull
    public Integer type;

    /**
     * 单据号
     */
    @NotNull
    @Pattern(regexp = "(ZH|CF)\\d{12}", message = "单据格式错误")
    public String code;


    @NotNull
    @Size(min = 1, max = 200, message = "details最多200个")
    public List<ProduceOrderDetailModel> details;

    @Length(max = 200)
    public String memo;

    /**
     * 制单人（员工id）
     */
    @NotNull
    public BigInteger empId;

    /**
     * 是否修改商品档案
     * */
    public Integer isUpdatePrice;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ProduceOrderDetailModel> getDetails() {
        return details;
    }

    public void setDetails(List<ProduceOrderDetailModel> details) {
        this.details = details;
    }

    public BigInteger getEmpId() {
        return empId;
    }

    public void setEmpId(BigInteger empId) {
        this.empId = empId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity != null) {
            this.quantity = quantity.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price != null) {
            this.price = price.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
        for (ProduceOrderDetailModel detail : details) {
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
