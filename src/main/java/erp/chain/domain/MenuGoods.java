package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class MenuGoods extends BaseDomain {
    /**
     * 商品编码
     */
    String goodsCode;
    /**
     *
     */
    BigInteger goodsId;
    /**
     * 商品名称（冗余字段显示用）
     */
    String goodsName;
    /**
     * 零售价
     */
    BigDecimal salePrice;
    /**
     * 会员价
     */
    BigDecimal vipPrice;
    /**
     * 会员价2
     */
    BigDecimal vipPrice2;
    /**
     *
     */
    BigInteger menuId;

    BigInteger id;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    BigInteger tenantId;
    Integer isPackage;
    boolean isForPoints;
    BigInteger createBranchId;

    public BigInteger getCreateBranchId(){
        return createBranchId;
    }

    public void setCreateBranchId(BigInteger createBranchId){
        this.createBranchId = createBranchId;
    }

    public Integer getIsPackage(){
        return isPackage;
    }

    public void setIsPackage(Integer isPackage){
        this.isPackage = isPackage;
    }

    public boolean isForPoints(){
        return isForPoints;
    }

    public void setForPoints(boolean forPoints){
        isForPoints = forPoints;
    }

    public MenuGoods() {
        super();
    }

    public MenuGoods(Map domainMap) {
        super(domainMap);
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(BigDecimal vipPrice) {
        this.vipPrice = vipPrice;
    }

    public BigDecimal getVipPrice2() {
        return vipPrice2;
    }

    public void setVipPrice2(BigDecimal vipPrice2) {
        this.vipPrice2 = vipPrice2;
    }

    public BigInteger getMenuId() {
        return menuId;
    }

    public void setMenuId(BigInteger menuId) {
        this.menuId = menuId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }
}
