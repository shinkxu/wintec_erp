package erp.chain.domain.online;

import erp.chain.annotations.Table;
import erp.chain.annotations.Transient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Table(name = "diet_order_detail")
public class OnlineDietOrderDetail {
    private BigInteger id;
    /**
     * diet_order_info.id
     */
    private BigInteger dietOrderInfoId;
    /**
     * 是否是套餐菜品（0：不是；1：是）
     */
    private boolean isPackage;
    /**
     * 菜品ID
     */
    private BigInteger goodsId;
    /**
     * 菜品名称
     */
    private String goodsName;
    /**
     * 套餐表ID（外键）
     */
    private BigInteger packageId;
    /**
     * 套餐表对应记录的名称
     */
    private String packageName;
    /**
     * 零售单价
     */
    private BigDecimal salePrice;
    /**
     * 实际单价
     */
    private BigDecimal salePriceActual;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 应收总价
     */
    private BigDecimal totalAmount;
    /**
     * 实收总价
     */
    private BigDecimal receivedAmount;
    /**
     * 是否免单或者赠送
     */
    private boolean isFreeOfCharge;
    /**
     * 是否冲销
     */
    private boolean isRefund;
    /**
     * 味道
     */
    private Integer taste;
    /**
     * 口味名
     */
    private String tasteName;
    /**
     * 大小
     */
    private Integer size;
    /**
     * 尺寸名
     */
    private String sizeName;
    private Date createAt;
    private String createBy;
    private Date lastUpdateAt;
    private String lastUpdateBy;
    private boolean isDeleted;
    /**
     * 状态
     */
    private Integer status;
    private BigInteger version;
    private BigInteger localId;
    private BigInteger tenantId;
    private BigInteger branchId;
    /**
     * pos套餐明细关联
     */
    private String retailLocalId;
    /**
     * 套餐编码
     */
    private String packageCode;
    /**
     * 组id，用来标记多人点餐
     */
    private String groupId;
    /**
     * 组名称
     */
    private String groupName;
    /**
     * 组类型，normal-正常的菜品，extra-配送费等，discount-赠品
     */
    private String groupType;

    @Transient
    private BigDecimal discountAmount;

    @Transient
    private BigDecimal otherExpenses;

    @Transient
    private BigDecimal income;

    @Transient
    private boolean allowDiscount;

    private BigDecimal scoreSalePrice;

    private BigDecimal scoreSalePriceActual;

    private BigDecimal scoreTotalAmount;

    private BigDecimal scoreDiscountAmount;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDietOrderInfoId() {
        return dietOrderInfoId;
    }

    public void setDietOrderInfoId(BigInteger dietOrderInfoId) {
        this.dietOrderInfoId = dietOrderInfoId;
    }

    public boolean getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean isPackage) {
        this.isPackage = isPackage;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public Integer getTaste() {
        return taste;
    }

    public void setTaste(Integer taste) {
        this.taste = taste;
    }

    public String getTasteName() {
        return tasteName;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
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

    public String getRetailLocalId() {
        return retailLocalId;
    }

    public void setRetailLocalId(String retailLocalId) {
        this.retailLocalId = retailLocalId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getOtherExpenses() {
        return otherExpenses;
    }

    public void setOtherExpenses(BigDecimal otherExpenses) {
        this.otherExpenses = otherExpenses;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public boolean isAllowDiscount() {
        return allowDiscount;
    }

    public void setAllowDiscount(boolean allowDiscount) {
        this.allowDiscount = allowDiscount;
    }

    public BigDecimal getScoreSalePrice() {
        return scoreSalePrice;
    }

    public void setScoreSalePrice(BigDecimal scoreSalePrice) {
        this.scoreSalePrice = scoreSalePrice;
    }

    public BigDecimal getScoreSalePriceActual() {
        return scoreSalePriceActual;
    }

    public void setScoreSalePriceActual(BigDecimal scoreSalePriceActual) {
        this.scoreSalePriceActual = scoreSalePriceActual;
    }

    public BigDecimal getScoreTotalAmount() {
        return scoreTotalAmount;
    }

    public void setScoreTotalAmount(BigDecimal scoreTotalAmount) {
        this.scoreTotalAmount = scoreTotalAmount;
    }

    public BigDecimal getScoreDiscountAmount() {
        return scoreDiscountAmount;
    }

    public void setScoreDiscountAmount(BigDecimal scoreDiscountAmount) {
        this.scoreDiscountAmount = scoreDiscountAmount;
    }

    public static class Builder {
        private final OnlineDietOrderDetail instance = new OnlineDietOrderDetail();

        public Builder id(BigInteger id) {
            instance.setId(id);
            return this;
        }

        public Builder dietOrderInfoId(BigInteger dietOrderInfoId) {
            instance.setDietOrderInfoId(dietOrderInfoId);
            return this;
        }

        public Builder isPackage(boolean isPackage) {
            instance.setIsPackage(isPackage);
            return this;
        }

        public Builder goodsId(BigInteger goodsId) {
            instance.setGoodsId(goodsId);
            return this;
        }

        public Builder goodsName(String goodsName) {
            instance.setGoodsName(goodsName);
            return this;
        }

        public Builder packageId(BigInteger packageId) {
            instance.setPackageId(packageId);
            return this;
        }

        public Builder packageName(String packageName) {
            instance.setPackageName(packageName);
            return this;
        }

        public Builder salePrice(BigDecimal salePrice) {
            instance.setSalePrice(salePrice);
            return this;
        }

        public Builder salePriceActual(BigDecimal salePriceActual) {
            instance.setSalePriceActual(salePriceActual);
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            instance.setQuantity(quantity);
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            instance.setTotalAmount(totalAmount);
            return this;
        }

        public Builder receivedAmount(BigDecimal receivedAmount) {
            instance.setReceivedAmount(receivedAmount);
            return this;
        }

        public Builder isFreeOfCharge(boolean isFreeOfCharge) {
            instance.setIsFreeOfCharge(isFreeOfCharge);
            return this;
        }

        public Builder isRefund(boolean isRefund) {
            instance.setIsRefund(isRefund);
            return this;
        }

        public Builder taste(Integer taste) {
            instance.setTaste(taste);
            return this;
        }

        public Builder tasteName(String tasteName) {
            instance.setTasteName(tasteName);
            return this;
        }

        public Builder size(Integer size) {
            instance.setSize(size);
            return this;
        }

        public Builder sizeName(String sizeName) {
            instance.setSizeName(sizeName);
            return this;
        }

        public Builder createAt(Date createAt) {
            instance.setCreateAt(createAt);
            return this;
        }

        public Builder createBy(String createBy) {
            instance.setCreateBy(createBy);
            return this;
        }

        public Builder lastUpdateAt(Date lastUpdateAt) {
            instance.setLastUpdateAt(lastUpdateAt);
            return this;
        }

        public Builder lastUpdateBy(String lastUpdateBy) {
            instance.setLastUpdateBy(lastUpdateBy);
            return this;
        }

        public Builder isDeleted(boolean isDeleted) {
            instance.setIsDeleted(isDeleted);
            return this;
        }

        public Builder status(Integer status) {
            instance.setStatus(status);
            return this;
        }

        public Builder version(BigInteger version) {
            instance.setVersion(version);
            return this;
        }

        public Builder localId(BigInteger localId) {
            instance.setLocalId(localId);
            return this;
        }

        public Builder tenantId(BigInteger tenantId) {
            instance.setTenantId(tenantId);
            return this;
        }

        public Builder branchId(BigInteger branchId) {
            instance.setBranchId(branchId);
            return this;
        }

        public Builder retailLocalId(String retailLocalId) {
            instance.setRetailLocalId(retailLocalId);
            return this;
        }

        public Builder packageCode(String packageCode) {
            instance.setPackageCode(packageCode);
            return this;
        }

        public Builder groupId(String groupId) {
            instance.setGroupId(groupId);
            return this;
        }

        public Builder groupName(String groupName) {
            instance.setGroupName(groupName);
            return this;
        }

        public Builder groupType(String groupType) {
            instance.setGroupType(groupType);
            return this;
        }

        public Builder discountAmount(BigDecimal discountAmount) {
            instance.setDiscountAmount(discountAmount);
            return this;
        }

        public Builder otherExpenses(BigDecimal otherExpenses) {
            instance.setOtherExpenses(otherExpenses);
            return this;
        }

        public Builder income(BigDecimal income) {
            instance.setIncome(income);
            return this;
        }

        public Builder allowDiscount(boolean allowDiscount) {
            instance.setAllowDiscount(allowDiscount);
            return this;
        }

        public Builder scoreSalePrice(BigDecimal scoreSalePrice) {
            instance.setScoreSalePrice(scoreSalePrice);
            return this;
        }

        public Builder scoreSalePriceActual(BigDecimal scoreSalePriceActual) {
            instance.setScoreSalePriceActual(scoreSalePriceActual);
            return this;
        }

        public Builder scoreTotalAmount(BigDecimal scoreTotalAmount) {
            instance.setScoreTotalAmount(scoreTotalAmount);
            return this;
        }

        public Builder scoreDiscountAmount(BigDecimal scoreDiscountAmount) {
            instance.setScoreDiscountAmount(scoreDiscountAmount);
            return this;
        }

        public OnlineDietOrderDetail build() {
            OnlineDietOrderDetail onlineDietOrderDetail = new OnlineDietOrderDetail();
            onlineDietOrderDetail.setId(instance.getId());
            onlineDietOrderDetail.setDietOrderInfoId(instance.getDietOrderInfoId());
            onlineDietOrderDetail.setIsPackage(instance.getIsPackage());
            onlineDietOrderDetail.setGoodsId(instance.getGoodsId());
            onlineDietOrderDetail.setGoodsName(instance.getGoodsName());
            onlineDietOrderDetail.setPackageId(instance.getPackageId());
            onlineDietOrderDetail.setPackageName(instance.getPackageName());
            onlineDietOrderDetail.setSalePrice(instance.getSalePrice());
            onlineDietOrderDetail.setSalePriceActual(instance.getSalePriceActual());
            onlineDietOrderDetail.setQuantity(instance.getQuantity());
            onlineDietOrderDetail.setTotalAmount(instance.getTotalAmount());
            onlineDietOrderDetail.setReceivedAmount(instance.getReceivedAmount());
            onlineDietOrderDetail.setIsFreeOfCharge(instance.getIsFreeOfCharge());
            onlineDietOrderDetail.setIsRefund(instance.getIsRefund());
            onlineDietOrderDetail.setTaste(instance.getTaste());
            onlineDietOrderDetail.setTasteName(instance.getTasteName());
            onlineDietOrderDetail.setSize(instance.getSize());
            onlineDietOrderDetail.setSizeName(instance.getSizeName());
            onlineDietOrderDetail.setCreateAt(instance.getCreateAt());
            onlineDietOrderDetail.setCreateBy(instance.getCreateBy());
            onlineDietOrderDetail.setLastUpdateAt(instance.getLastUpdateAt());
            onlineDietOrderDetail.setLastUpdateBy(instance.getLastUpdateBy());
            onlineDietOrderDetail.setIsDeleted(instance.getIsDeleted());
            onlineDietOrderDetail.setStatus(instance.getStatus());
            onlineDietOrderDetail.setVersion(instance.getVersion());
            onlineDietOrderDetail.setLocalId(instance.getLocalId());
            onlineDietOrderDetail.setTenantId(instance.getTenantId());
            onlineDietOrderDetail.setBranchId(instance.getBranchId());
            onlineDietOrderDetail.setRetailLocalId(instance.getRetailLocalId());
            onlineDietOrderDetail.setPackageCode(instance.getPackageCode());
            onlineDietOrderDetail.setGroupId(instance.getGroupId());
            onlineDietOrderDetail.setGroupName(instance.getGroupName());
            onlineDietOrderDetail.setGroupType(instance.getGroupType());
            onlineDietOrderDetail.setDiscountAmount(instance.getDiscountAmount());
            onlineDietOrderDetail.setOtherExpenses(instance.getOtherExpenses());
            onlineDietOrderDetail.setIncome(instance.getIncome());
            onlineDietOrderDetail.setAllowDiscount(instance.isAllowDiscount());
            onlineDietOrderDetail.setScoreSalePrice(instance.getScoreSalePrice());
            onlineDietOrderDetail.setScoreSalePriceActual(instance.getScoreSalePriceActual());
            onlineDietOrderDetail.setScoreTotalAmount(instance.getScoreTotalAmount());
            onlineDietOrderDetail.setScoreDiscountAmount(instance.getScoreDiscountAmount());
            return onlineDietOrderDetail;
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
