package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Package extends BaseDomain {

    /**
     * 套餐编码
     */
    String packageCode;
    /**
     * 套餐名称
     */
    String packageName;
    /**
     * 状态：0停用1正常
     */
    Integer status;
    /**
     * 助记码
     */
    String mnemonic;
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
     * 单位
     */
    BigInteger goodsUnitId;
    /**
     * 是否有折扣
     */
    boolean isDsc;
    /**
     * 参与积分0否1是
     */
    boolean isScore;
    /**
     * 是否外卖
     */
    boolean isTakeout;
    /**
     * 是否自提
     */
    boolean isTake;
    /**
     * 是否预定
     */
    boolean isOrder;
    /**
     * 是否堂食
     */
    boolean isHere;
    /**
     * 机构
     */
    BigInteger branchId;

    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;

    public Package() {
        super();
    }

    public Package(Map domainMap) {
        super(domainMap);
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
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

    public BigInteger getGoodsUnitId() {
        return goodsUnitId;
    }

    public void setGoodsUnitId(BigInteger goodsUnitId) {
        this.goodsUnitId = goodsUnitId;
    }

    public boolean isDsc() {
        return isDsc;
    }

    public void setDsc(boolean dsc) {
        isDsc = dsc;
    }

    public boolean isScore() {
        return isScore;
    }

    public void setScore(boolean score) {
        isScore = score;
    }

    public boolean isTakeout() {
        return isTakeout;
    }

    public void setTakeout(boolean takeout) {
        isTakeout = takeout;
    }

    public boolean isTake() {
        return isTake;
    }

    public void setTake(boolean take) {
        isTake = take;
    }

    public boolean isOrder() {
        return isOrder;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }

    public boolean isHere() {
        return isHere;
    }

    public void setHere(boolean here) {
        isHere = here;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
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
}
