package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by wangms on 2017/1/23.
 */
public class DietOrderDetailShowVo {
    /**
     * 是否是套餐菜品（0：不是；1：是）
     */
    boolean isPackage;

    /**
     * 菜品表ID（外键）
     */
    BigInteger goodsId;

    /**
     * 套餐表ID（外键）字段名称暂定
     */
    BigInteger packageId;

    /**
     * 菜品名称
     * */
    String goodsName;
    String packageName;
    /**
     * 单价
     */
    BigDecimal price;
    /**
     * 数量
     */
    BigDecimal quantity;
    /**
     * 总价
     */
    BigDecimal totalPrice;
    /**
     * 味道
     */
    String taste;
    /**
     * 口味名
     */
    String tasteName;
    /**
     * 大小
     */
    String size;
    /**
     * 状态
     */
    Integer status;

    /**
     * 是否免单
     */
    boolean isFreeOfCharge;

    BigInteger localId;

    String RetailLocalId;
    private String packageCode;
    private String groupId;
    private String groupName;
    private String groupType;

    public String getRetailLocalId() {
        return RetailLocalId;
    }

    public void setRetailLocalId(String retailLocalId) {
        RetailLocalId = retailLocalId;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public String getPackageName(){
        return packageName;
    }

    public void setPackageName(String packageName){
        this.packageName = packageName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public String getTasteName() {
        return tasteName;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
