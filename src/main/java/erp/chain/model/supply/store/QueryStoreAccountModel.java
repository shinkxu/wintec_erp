package erp.chain.model.supply.store;

import erp.chain.utils.In;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分页查询库存流水model
 */
public class QueryStoreAccountModel extends erp.chain.model.SearchModel {
    @NotNull
    public BigInteger tenantId;
    @NotNull
    public BigInteger branchId;

    @In(value = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20"},
            message = "必须是[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]中一个")
    public String occurType;


    /**
     * 最小发生成本
     */
    public BigDecimal occurIncurredMin;
    /**
     * 最大发生成本
     */
    public BigDecimal occurIncurredMax;
    /**
     * 最小发生数量
     */
    public BigDecimal occurQuantityMin;
    /**
     * 最大发生数量
     */
    public BigDecimal occurQuantityMax;
    /**
     * 最小发生金额
     */
    public BigDecimal occurAmountMin;
    /**
     * 最大发生金额
     */
    public BigDecimal occurAmountMax;

    /**
     * 商品或单据号模糊查询
     */
    public String goodsOrOrderCodeLike;
    /**
     * 商品、员工或单据号
     */
    public String goodsOrOrderEmpCode;
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
     * 商品ID
     * */
    public BigInteger goodsId;

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

    public String getOccurType() {
        return occurType;
    }

    public void setOccurType(String occurType) {
        this.occurType = occurType;
    }

    public BigDecimal getOccurIncurredMin() {
        return occurIncurredMin;
    }

    public void setOccurIncurredMin(BigDecimal occurIncurredMin) {
        this.occurIncurredMin = occurIncurredMin;
    }

    public BigDecimal getOccurIncurredMax() {
        return occurIncurredMax;
    }

    public void setOccurIncurredMax(BigDecimal occurIncurredMax) {
        this.occurIncurredMax = occurIncurredMax;
    }

    public BigDecimal getOccurQuantityMin() {
        return occurQuantityMin;
    }

    public void setOccurQuantityMin(BigDecimal occurQuantityMin) {
        this.occurQuantityMin = occurQuantityMin;
    }

    public BigDecimal getOccurQuantityMax() {
        return occurQuantityMax;
    }

    public void setOccurQuantityMax(BigDecimal occurQuantityMax) {
        this.occurQuantityMax = occurQuantityMax;
    }

    public BigDecimal getOccurAmountMin() {
        return occurAmountMin;
    }

    public void setOccurAmountMin(BigDecimal occurAmountMin) {
        this.occurAmountMin = occurAmountMin;
    }

    public BigDecimal getOccurAmountMax() {
        return occurAmountMax;
    }

    public void setOccurAmountMax(BigDecimal occurAmountMax) {
        this.occurAmountMax = occurAmountMax;
    }

    public String getGoodsOrOrderCodeLike() {
        return like(goodsOrOrderCodeLike);
    }

    public void setGoodsOrOrderCodeLike(String goodsOrOrderCodeLike) {
        this.goodsOrOrderCodeLike = goodsOrOrderCodeLike;
    }

    public String getGoodsOrOrderEmpCode() {
        return goodsOrOrderEmpCode;
    }

    public void setGoodsOrOrderEmpCode(String goodsOrOrderEmpCode) {
        this.goodsOrOrderEmpCode = goodsOrOrderEmpCode;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }
}
