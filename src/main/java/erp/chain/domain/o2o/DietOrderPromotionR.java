package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * Created by liuyandong on 2018/9/7.
 */
public class DietOrderPromotionR {
    private BigInteger id;
    /**
     * 订单id
     */
    private BigInteger orderId;
    /**
     * 订单编号
     */
    private String orderCode;
    /**
     * 促销编号
     */
    private String promotionCode;
    /**
     * 促销类型
     */
    private Integer promotionType;
    /**
     * 促销范围（0：全部；1：线下促销；2：线上促销）
     */
    private Integer scope;
    /**
     * 0-所有顾客 1-所有会员 2-非会员
     */
    private Integer forCustomerType;
    /**
     * 会员等级id(暂不用)
     */
    private BigInteger memGradeId;
    /**
     * 促销备注
     */
    private String memo;
    /**
     * 促销状态
     */
    private Integer promotionStatus;
    /**
     * 促销商品
     */
    private BigInteger goodsId;
    /**
     * 购买商品
     */
    private BigInteger buyGoodsId;
    /**
     * 套餐
     */
    private BigInteger packageId;
    /**
     * 赠送商品
     */
    private BigInteger giveGoodsId;
    /**
     * 购买数量
     */
    private BigDecimal buyNum;
    /**
     * 赠送套餐
     */
    private BigInteger givePackageId;
    /**
     * 赠送数量
     */
    private BigDecimal giveNum;
    /**
     * 金额
     */
    private BigDecimal satisfy;
    /**
     * 减免金额
     */
    private BigDecimal reduction;
    /**
     * 购买数量
     */
    private BigDecimal quantity;
    /**
     * 购买折扣
     */
    private BigDecimal discount;
    /**
     * 实际优惠金额
     */
    private BigDecimal actualSaveValue;

    private BigInteger version;
    private BigInteger localId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
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

    public BigInteger getMemGradeId() {
        return memGradeId;
    }

    public void setMemGradeId(BigInteger memGradeId) {
        this.memGradeId = memGradeId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(Integer promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigInteger getBuyGoodsId() {
        return buyGoodsId;
    }

    public void setBuyGoodsId(BigInteger buyGoodsId) {
        this.buyGoodsId = buyGoodsId;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigInteger getGiveGoodsId() {
        return giveGoodsId;
    }

    public void setGiveGoodsId(BigInteger giveGoodsId) {
        this.giveGoodsId = giveGoodsId;
    }

    public BigDecimal getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(BigDecimal buyNum) {
        this.buyNum = buyNum;
    }

    public BigInteger getGivePackageId() {
        return givePackageId;
    }

    public void setGivePackageId(BigInteger givePackageId) {
        this.givePackageId = givePackageId;
    }

    public BigDecimal getGiveNum() {
        return giveNum;
    }

    public void setGiveNum(BigDecimal giveNum) {
        this.giveNum = giveNum;
    }

    public BigDecimal getSatisfy() {
        return satisfy;
    }

    public void setSatisfy(BigDecimal satisfy) {
        this.satisfy = satisfy;
    }

    public BigDecimal getReduction() {
        return reduction;
    }

    public void setReduction(BigDecimal reduction) {
        this.reduction = reduction;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getActualSaveValue() {
        return actualSaveValue;
    }

    public void setActualSaveValue(BigDecimal actualSaveValue) {
        this.actualSaveValue = actualSaveValue;
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
}
