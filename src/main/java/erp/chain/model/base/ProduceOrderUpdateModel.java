package erp.chain.model.base;


import erp.chain.model.Model;
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
public class ProduceOrderUpdateModel extends Model {

    @NotNull
    public BigInteger version;
    @NotNull
    public BigInteger orderId;
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


    @NotNull
    @Size(min = 1, max = 200, message = "details最多200个")
    public List<ProduceOrderDetailModel> details;

    /**
     * 制单人（员工id）
     */
    @NotNull
    public BigInteger empId;

    @Length(max = 200)
    public String memo;

    /**
     * 是否更新商品档案
     * */
    public Integer isUpdatePrice;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
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

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(Integer isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
    }
}
