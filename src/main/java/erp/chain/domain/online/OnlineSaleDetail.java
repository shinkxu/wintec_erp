package erp.chain.domain.online;

import erp.chain.annotations.Column;
import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-05-07.
 */
@Table(name = "sale_detail")
public class OnlineSaleDetail {
    private BigInteger id;
    /**
     * 销售账单号
     */
    private String saleCode;
    /**
     * 套餐
     */
    private BigInteger packageId;
    /**
     * 商品
     */
    private BigInteger goodsId;
    /**
     * 是否套餐或套餐内商品
     */
    private boolean isPackage;
    /**
     * 促销活动
     */
    private BigInteger promotionId;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 实际售价
     */
    private BigDecimal salePriceActual;
    /**
     * 应收合计
     */
    private BigDecimal totalAmount;
    /**
     * 是否免单或赠送
     */
    private boolean isFreeOfCharge;
    /**
     * 实收合计
     */
    private BigDecimal receivedAmount;
    /**
     * 是否冲销
     */
    private boolean isRefund;
    /**
     *
     */
    private boolean isPrinted;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 所属机构，默认为0，代表总部
     */
    private BigInteger branchId;
    /**
     * 折扣额
     */
    private BigDecimal discountAmount;
    private BigDecimal discountAmount1;
    private BigInteger localId;
    /**
     * pos本地用结算时间
     */
    private Date payAt;
    /**
     * 备注
     */
    private String remark;
    /**
     * 找零
     */
    private BigDecimal changedPrice;
    /**
     * pos本地用桌台ID
     */
    @Column(name = "saleTable_id")
    private BigInteger saleTableId;
    /**
     * 单品销售备注
     */
    private String memo;
    /**
     * 口味id，可以为null
     */
    private BigInteger goodsSpecId;
    private String localSign;
    /**
     * 口味加价
     */
    private BigDecimal specAmount;
    /**
     *
     */
    private boolean isProduced;
    /**
     *
     */
    private boolean isServed;
    /**
     * 流水类型0或null为线下，1为线上
     */
    private Integer saleType;
    /**
     * 会员ID
     */
    private BigInteger vipId;
    /**
     * 操作员
     */
    private BigInteger cashier;
    /**
     * 导购员ID
     */
    private BigInteger guideId;
    /**
     * 交易终端1-pos2-点菜宝3-H5扫码点餐4-微餐厅扫码点餐5-微餐厅到店自提6-微餐厅外卖点餐7-电话外卖8-第三方订单
     */
    private Integer transTerminal;
    /**
     * 1-pos支付2-自助支付
     */
    private Integer payType;
    private BigInteger parentPackagId;
    /**
     * 店铺实际收入
     */
    private BigDecimal income;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public boolean getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean isPackage) {
        this.isPackage = isPackage;
    }

    public BigInteger getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(BigInteger promotionId) {
        this.promotionId = promotionId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePriceActual() {
        return salePriceActual;
    }

    public void setSalePriceActual(BigDecimal salePriceActual) {
        this.salePriceActual = salePriceActual;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public boolean getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount1() {
        return discountAmount1;
    }

    public void setDiscountAmount1(BigDecimal discountAmount1) {
        this.discountAmount1 = discountAmount1;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public Date getPayAt() {
        return payAt;
    }

    public void setPayAt(Date payAt) {
        this.payAt = payAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getChangedPrice() {
        return changedPrice;
    }

    public void setChangedPrice(BigDecimal changedPrice) {
        this.changedPrice = changedPrice;
    }

    public BigInteger getSaleTableId() {
        return saleTableId;
    }

    public void setSaleTableId(BigInteger saleTableId) {
        this.saleTableId = saleTableId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getGoodsSpecId() {
        return goodsSpecId;
    }

    public void setGoodsSpecId(BigInteger goodsSpecId) {
        this.goodsSpecId = goodsSpecId;
    }

    public String getLocalSign() {
        return localSign;
    }

    public void setLocalSign(String localSign) {
        this.localSign = localSign;
    }

    public BigDecimal getSpecAmount() {
        return specAmount;
    }

    public void setSpecAmount(BigDecimal specAmount) {
        this.specAmount = specAmount;
    }

    public boolean isProduced() {
        return isProduced;
    }

    public void setProduced(boolean produced) {
        isProduced = produced;
    }

    public boolean getIsServed() {
        return isServed;
    }

    public void setServed(boolean isServed) {
        this.isServed = isServed;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigInteger getCashier() {
        return cashier;
    }

    public void setCashier(BigInteger cashier) {
        this.cashier = cashier;
    }

    public BigInteger getGuideId() {
        return guideId;
    }

    public void setGuideId(BigInteger guideId) {
        this.guideId = guideId;
    }

    public Integer getTransTerminal() {
        return transTerminal;
    }

    public void setTransTerminal(Integer transTerminal) {
        this.transTerminal = transTerminal;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigInteger getParentPackagId() {
        return parentPackagId;
    }

    public void setParentPackagId(BigInteger parentPackagId) {
        this.parentPackagId = parentPackagId;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
