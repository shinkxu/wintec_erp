package erp.chain.model.online.onlinedietorder;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.GsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-04-09.
 */
public class SaveDietOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Integer orderMode;

    @NotNull
    private BigInteger vipId;

    private BigInteger vipAddressId;

    private String remark;

    private String tableCode;

    private List<GoodsInfo> goodsInfos;

    @NotNull
    private Integer scope;

    @NotNull
    private Integer forCustomerType;

    @NotNull
    private Integer menuType;

    @NotNull
    private Integer payWay;

    private BigDecimal useScore;

    private String eatTime;
    private String deliveryTime;
    private String pickedUpTime;

    private BigInteger cardId;

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

    public Integer getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(Integer orderMode) {
        this.orderMode = orderMode;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigInteger getVipAddressId() {
        return vipAddressId;
    }

    public void setVipAddressId(BigInteger vipAddressId) {
        this.vipAddressId = vipAddressId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public void setGoodsInfos(String goodsInfos) {
        this.goodsInfos = GsonUtils.jsonToList(goodsInfos, GoodsInfo.class);
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Integer getForCustomerType() {
        return forCustomerType;
    }

    public void setForCustomerType(Integer forCustomerType) {
        this.forCustomerType = forCustomerType;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public BigDecimal getUseScore() {
        return useScore;
    }

    public void setUseScore(BigDecimal useScore) {
        this.useScore = useScore;
    }

    public String getEatTime() {
        return eatTime;
    }

    public void setEatTime(String eatTime) {
        this.eatTime = eatTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPickedUpTime() {
        return pickedUpTime;
    }

    public void setPickedUpTime(String pickedUpTime) {
        this.pickedUpTime = pickedUpTime;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
    }

    public static class GoodsInfo extends BasicModel {
        private static final Integer[] GOODS_TYPES = {1, 2};
        @NotNull
        private BigInteger goodsId;
        private Integer goodsType;
        @NotNull
        private BigDecimal quantity;
        private List<PackageGroupInfo> packageGroupInfos;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

        public Integer getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(Integer goodsType) {
            this.goodsType = goodsType;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public List<PackageGroupInfo> getPackageGroupInfos() {
            return packageGroupInfos;
        }

        public void setPackageGroupInfos(List<PackageGroupInfo> packageGroupInfos) {
            this.packageGroupInfos = packageGroupInfos;
        }

        @Override
        public boolean validate() {
            boolean isValidate = super.validate() && ArrayUtils.contains(GOODS_TYPES, goodsType);
            if (!isValidate) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(packageGroupInfos)) {
                for (PackageGroupInfo packageGroupInfo : packageGroupInfos) {
                    isValidate = packageGroupInfo.validate();
                    if (!isValidate) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class PackageGroupInfo extends BasicModel {
        @NotNull
        private BigInteger groupId;

        @NotEmpty
        private List<GroupGoodsInfo> groupGoodsInfos;

        public BigInteger getGroupId() {
            return groupId;
        }

        public void setGroupId(BigInteger groupId) {
            this.groupId = groupId;
        }

        public List<GroupGoodsInfo> getGroupGoodsInfos() {
            return groupGoodsInfos;
        }

        public void setGroupGoodsInfos(List<GroupGoodsInfo> groupGoodsInfos) {
            this.groupGoodsInfos = groupGoodsInfos;
        }

        @Override
        public boolean validate() {
            boolean isValidate = super.validate();
            if (!isValidate) {
                return false;
            }
            for (GroupGoodsInfo groupGoodsInfo : groupGoodsInfos) {
                isValidate = groupGoodsInfo.validate();
                if (!isValidate) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class GroupGoodsInfo extends BasicModel {
        @NotNull
        BigInteger goodsId;

        @NotNull
        BigDecimal quantity;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }
    }

    @Override
    public boolean validate() {
        return super.validate();
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.isTrue(CollectionUtils.isNotEmpty(goodsInfos), "goodsInfos");
        for (GoodsInfo goodsInfo : goodsInfos) {
            ApplicationHandler.isTrue(goodsInfo.validate(), "goodsInfos");
        }
    }
}
