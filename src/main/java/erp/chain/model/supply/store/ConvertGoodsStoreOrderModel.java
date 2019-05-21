package erp.chain.model.supply.store;

import erp.chain.model.Model;
import erp.chain.utils.In;

import javax.validation.Validator;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 * 打包拆包
 */
public class ConvertGoodsStoreOrderModel extends Model {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;


    /**
     * 单据类型(1-打包,2-拆包)
     */
    @In(value = {"1", "2"}, message = "必须是[1,2]中一个")
    @NotNull
    public Integer type;

    /**
     * 数量
     */
    @DecimalMin("0")
    @DecimalMax("9999999.99")
    @NotNull
    public BigDecimal quantity;

    /**
     * 商品id
     */
    @NotNull
    public BigInteger goodsId;
    /**
     * 目标数量
     */
    @DecimalMin("0")
    @DecimalMax("9999999.99")
    @NotNull
    public BigDecimal targetQuantity;

    /**
     * 目标商品id
     */
    @NotNull
    public BigInteger targetGoodsId;
    @NotNull
    public BigInteger empId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity != null) {
            this.quantity = quantity.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(BigDecimal targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public BigInteger getTargetGoodsId() {
        return targetGoodsId;
    }

    public void setTargetGoodsId(BigInteger targetGoodsId) {
        this.targetGoodsId = targetGoodsId;
    }
}
