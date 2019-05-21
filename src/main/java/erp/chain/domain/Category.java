package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Category extends BaseDomain {

    /**
     * 商品分类码
     */
    String catCode;
    /**
     * 商品分类名称
     */
    String catName;
    /**
     * 助记码
     */
    String mnemonics;
    /**
     * 上级分类ID
     外键
     */
    BigInteger parentId;
    /**
     * 是否折扣
     */
    boolean isDiscount;
    /**
     * 是否管理库存
     */
    boolean isStore;
    /**
     * 冗余字段显示使用
     */
    String parentName;

    Integer categoryType;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger localId;
    BigInteger branchId;

    Integer scoreType;
    BigDecimal scoreValue;
    BigDecimal scorePercent;

    /*父类的pos销售显示状态*/
    Integer parentCategoryType;

    public BigDecimal getScorePercent() {
        return scorePercent;
    }

    public void setScorePercent(BigDecimal scorePercent) {
        this.scorePercent = scorePercent;
    }

    public Integer getScoreType() {
        return scoreType;
    }

    public void setScoreType(Integer scoreType) {
        this.scoreType = scoreType;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
    }

    public Category() {
        super();
    }

    public Category(Map domainMap) {
        super(domainMap);
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getMnemonics() {
        return mnemonics;
    }

    public void setMnemonics(String mnemonics) {
        this.mnemonics = mnemonics;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean store) {
        isStore = store;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
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

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getParentCategoryType() {
        return parentCategoryType;
    }

    public void setParentCategoryType(Integer parentCategoryType) {
        this.parentCategoryType = parentCategoryType;
    }
}
