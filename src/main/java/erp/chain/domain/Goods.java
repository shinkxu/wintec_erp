package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/3.
 */
public class Goods extends BaseDomain {
    /**
     * 创建菜品的机构id(组织机构ID),默认为0 总部创建
     */
    BigInteger branchId;
    /**
     * 商品分类 外键
     */
    BigInteger categoryId;
    /**
     *  分类名称
     */
    String categoryName;
    /**
     * 商品编码
     */
    String goodsCode;
    /**
     * 商品名称
     */
    String goodsName;
    /**
     * 商品简称
     */
    String shortName;
    /**
     * 单位
     */
    BigInteger goodsUnitId;
    /**
     * 单位(冗余字段显示使用)
     */
    String goodsUnitName;
    /**
     * 商品条码
     */
    String barCode;
    /**
     * 助记码
     */
    String mnemonic;
    /**
     * 西文名称
     */
    String westName;
    /**
     * 规格
     */
    String spec;
    /**
     * 零售价
     */
    BigDecimal salePrice;
    /**
     * 会员价
     */
    BigDecimal vipPrice;
    /**
     * 会员价
     */
    BigDecimal vipPrice1;

    /**
     * 会员价2
     */
    BigDecimal vipPrice2;
    /**
     * 商品状态
     0-正常
     1-停售
     2-停购
     3-淘汰
     */
    Integer goodsStatus;
    /**
     * 是否有折扣
     */
    boolean isDsc;
    /**
     * 是否按照最低折扣：0否1是
     */
    boolean isLowerfee;
    /**
     * 临时商品0否1是
     */
    boolean isTemp;
    /**
     * 是否赠送券0否1是
     */
    boolean isGiveticket;
    /**
     * 允许用券0否1是
     */
    boolean isUseticket;
    /**
     * 参与积分0否1是
     */
    boolean isScore;
    /**
     * 积分数量
     */
    BigDecimal scoreValue;
    /**
     * 时价0否1是
     */
    boolean isChangeprice;
    /**
     * 修改数量0否1是
     */
    boolean isModifynum;
    /**
     * 是否可改价
     */
    boolean isRevisedPrice;
    /**
     * 外卖价
     */
    BigDecimal takeoutPrice;
    /**
     * 图片路径
     */
    String photo;
    /**
     * 商品描述
     */
    String goodsDesc;
    /**
     * 标签：逗号分割，最多3个
     */
    String tag;
    /**
     * 服务费0否1是
     */
    boolean isServicefee;
    /**
     * 服务提成0否1是
     */
    boolean isSertc;
    /**
     * 点菜员提成
     */
    boolean isSaletc;
    /**
     * 点菜员提成方式
     */
    Integer saleTctype;
    /**
     * 点菜员提成比例
     */
    BigDecimal saleTcrate;
    /**
     * 点菜员提成金额
     */
    BigDecimal saleTctotal;
    /**
     * 多规格:0 否1是
     */
    boolean isMuchspec;
    /**
     * 重量
     */
    BigDecimal weight;
    /**
     * 是否管理库存0否1是
     */
    boolean isStore;
    /**
     * 计价方式:0普通1重量2数量
     */
    Integer priceType;
    /**
     * 称重0否1是
     */
    boolean isWeigh;
    /**
     * 可抵扣0否1是
     */
    boolean isForPoints;
    /**
     * 抵扣价钱
     */
    BigDecimal pointsValue;
    /**
     * 人气商品
     */
    boolean isRanked;
    /**
     * 推荐商品
     */
    boolean isRecommended;
    /**
     * 备注
     */
    String memo;
    /**
     * 1普通商品2套餐3原料
     */
    Integer goodsType;
    /**
     * 是否热卖
     */
    boolean isHotgood;
    /**
     * 是否新品
     */
    boolean isNewgood;
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
     * 店内码
     */
    String instoreCode;
    /**
     * 最低起订数量
     */
    Integer miniOrderNum;
    /**
     * 口味分组ID
     */
    String specGroupId;

    /**
     * 主规格ID
     * */
    BigInteger parentId;

    /**
     * 规格名称
     * */
    String standardName;

    /**
     * 库存上限
     * */
    BigDecimal storeUpLimit;

    /**
     * 库存下限
     * */
    BigDecimal storeLowLimit;

    /**
     * 包装单位ID
     * */
    BigInteger packingUnitId;

    /**
     * 包装单位名称
     * */
    String packingUnitName;

    /**
     * 销售单位与包装单位关系
     * 1包装单位 = x * 销售单位
     * */
    BigDecimal unitRelation;

    /**
     * 供应商
     * */
    BigInteger supplierId;

    Boolean isPricetag;

    Integer combinationType;
    BigDecimal purchasingPrice;
    BigDecimal shippingPrice1;
    BigDecimal shippingPrice2;
    boolean isCustomPrice;

    BigInteger id;
    BigInteger tenantId;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger version;
    BigInteger brandId;
    BigInteger localId;
    BigDecimal saleOpenPrice;

    Integer scoreType;
    BigDecimal scorePercent;
    BigDecimal boxPrice=BigDecimal.ZERO;

    public Integer getScoreType() {
        return scoreType;
    }

    public void setScoreType(Integer scoreType) {
        this.scoreType = scoreType;
    }

    public BigDecimal getScorePercent() {
        return scorePercent;
    }

    public void setScorePercent(BigDecimal scorePercent) {
        this.scorePercent = scorePercent;
    }

    /**
     * 称重PLU
     * */
    Integer weighPlu;

    /**
     * 套餐关联商品ID
     * */
    BigInteger relatedGoodsId;

    /**
     * 是否允许改价
     */
    boolean isAllowChangePrice;
    /**
     * 是否允许会员优惠
     */
    boolean isAllowVipDiscount;

    public boolean getIsAllowChangePrice() {
        return isAllowChangePrice;
    }

    public void setIsAllowChangePrice(boolean allowChangePrice) {
        isAllowChangePrice = allowChangePrice;
    }

    public boolean getIsAllowVipDiscount() {
        return isAllowVipDiscount;
    }

    public void setIsAllowVipDiscount(boolean allowVipDiscount) {
        isAllowVipDiscount = allowVipDiscount;
    }

    public BigDecimal getSaleOpenPrice(){
        return saleOpenPrice;
    }

    public void setSaleOpenPrice(BigDecimal saleOpenPrice){
        this.saleOpenPrice = saleOpenPrice;
    }

    public BigInteger getLocalId(){
        return localId;
    }

    public void setLocalId(BigInteger localId){
        this.localId = localId;
    }

    public BigInteger getBrandId(){
        return brandId;
    }

    public void setBrandId(BigInteger brandId){
        this.brandId = brandId;
    }

    public BigInteger getVersion(){
        return version;
    }

    public void setVersion(BigInteger version){
        this.version = version;
    }

    public boolean getIsCustomPrice(){
        return isCustomPrice;
    }

    public void setIsCustomPrice(boolean customPrice){
        isCustomPrice = customPrice;
    }

    public BigDecimal getShippingPrice2(){
        return shippingPrice2;
    }

    public void setShippingPrice2(BigDecimal shippingPrice2){
        this.shippingPrice2 = shippingPrice2;
    }

    public BigDecimal getShippingPrice1(){
        return shippingPrice1;
    }

    public void setShippingPrice1(BigDecimal shippingPrice1){
        this.shippingPrice1 = shippingPrice1;
    }

    public BigDecimal getPurchasingPrice(){
        return purchasingPrice;
    }

    public void setPurchasingPrice(BigDecimal purchasingPrice){
        this.purchasingPrice = purchasingPrice;
    }

    public Integer getCombinationType(){
        return combinationType;
    }

    public void setCombinationType(Integer combinationType){
        this.combinationType = combinationType;
    }

    public String getSpecGroupId(){
        return specGroupId;
    }

    public void setSpecGroupId(String specGroupId){
        this.specGroupId = specGroupId;
    }

    public Goods() {
        super();
    }

    public Goods(Map domainMap) {
        super(domainMap);
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public BigInteger getGoodsUnitId() {
        return goodsUnitId;
    }

    public void setGoodsUnitId(BigInteger goodsUnitId) {
        this.goodsUnitId = goodsUnitId;
    }

    public String getGoodsUnitName() {
        return goodsUnitName;
    }

    public void setGoodsUnitName(String goodsUnitName) {
        this.goodsUnitName = goodsUnitName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getWestName() {
        return westName;
    }

    public void setWestName(String westName) {
        this.westName = westName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public boolean getIsDsc() {
        return isDsc;
    }

    public void setIsDsc(boolean dsc) {
        isDsc = dsc;
    }

    public boolean getIsLowerfee() {
        return isLowerfee;
    }

    public void setIsLowerfee(boolean lowerfee) {
        isLowerfee = lowerfee;
    }

    public boolean getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean getIsGiveticket() {
        return isGiveticket;
    }

    public void setIsGiveticket(boolean giveticket) {
        isGiveticket = giveticket;
    }

    public boolean getIsUseticket() {
        return isUseticket;
    }

    public void setIsUseticket(boolean useticket) {
        isUseticket = useticket;
    }

    public boolean getIsScore() {
        return isScore;
    }

    public void setIsScore(boolean score) {
        isScore = score;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public boolean getIsChangeprice() {
        return isChangeprice;
    }

    public void setIsChangeprice(boolean changeprice) {
        isChangeprice = changeprice;
    }

    public boolean getIsModifynum() {
        return isModifynum;
    }

    public void setIsModifynum(boolean modifynum) {
        isModifynum = modifynum;
    }

    public boolean getIsRevisedPrice() {
        return isRevisedPrice;
    }

    public void setIsRevisedPrice(boolean revisedPrice) {
        isRevisedPrice = revisedPrice;
    }

    public BigDecimal getTakeoutPrice() {
        return takeoutPrice;
    }

    public void setTakeoutPrice(BigDecimal takeoutPrice) {
        this.takeoutPrice = takeoutPrice;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getIsServicefee() {
        return isServicefee;
    }

    public void setIsServicefee(boolean servicefee) {
        isServicefee = servicefee;
    }

    public boolean getIsSertc() {
        return isSertc;
    }

    public void setIsSertc(boolean sertc) {
        isSertc = sertc;
    }

    public boolean getIsSaletc() {
        return isSaletc;
    }

    public void setIsSaletc(boolean saletc) {
        isSaletc = saletc;
    }

    public Integer getSaleTctype() {
        return saleTctype;
    }

    public void setSaleTctype(Integer saleTctype) {
        this.saleTctype = saleTctype;
    }

    public BigDecimal getSaleTcrate() {
        return saleTcrate;
    }

    public void setSaleTcrate(BigDecimal saleTcrate) {
        this.saleTcrate = saleTcrate;
    }

    public BigDecimal getSaleTctotal() {
        return saleTctotal;
    }

    public void setSaleTctotal(BigDecimal saleTctotal) {
        this.saleTctotal = saleTctotal;
    }

    public boolean getIsMuchspec() {
        return isMuchspec;
    }

    public void setIsMuchspec(boolean muchspec) {
        isMuchspec = muchspec;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public boolean getIsStore() {
        return isStore;
    }

    public void setIsStore(boolean store) {
        isStore = store;
    }

    public Integer getPriceType() {
        return priceType;
    }

    public void setPriceType(Integer priceType) {
        this.priceType = priceType;
    }

    public boolean getIsWeigh() {
        return isWeigh;
    }

    public void setIsWeigh(boolean weigh) {
        isWeigh = weigh;
    }

    public boolean getIsForPoints() {
        return isForPoints;
    }

    public void setIsForPoints(boolean forPoints) {
        isForPoints = forPoints;
    }

    public BigDecimal getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(BigDecimal pointsValue) {
        this.pointsValue = pointsValue;
    }

    public boolean getIsRanked() {
        return isRanked;
    }

    public void setIsRanked(boolean ranked) {
        isRanked = ranked;
    }

    public boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public boolean getIsHotgood() {
        return isHotgood;
    }

    public void setIsHotgood(boolean hotgood) {
        isHotgood = hotgood;
    }

    public boolean getIsNewgood() {
        return isNewgood;
    }

    public void setIsNewgood(boolean newgood) {
        isNewgood = newgood;
    }

    public boolean getIsTakeout() {
        return isTakeout;
    }

    public void setIsTakeout(boolean takeout) {
        isTakeout = takeout;
    }

    public boolean isTake() {
        return isTake;
    }

    public void setTake(boolean take) {
        isTake = take;
    }

    public boolean getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(boolean order) {
        isOrder = order;
    }

    public boolean getIsHere() {
        return isHere;
    }

    public void setIsHere(boolean here) {
        isHere = here;
    }

    public String getInstoreCode() {
        return instoreCode;
    }

    public void setInstoreCode(String instoreCode) {
        this.instoreCode = instoreCode;
    }

    public Integer getMiniOrderNum() {
        return miniOrderNum;
    }

    public void setMiniOrderNum(Integer miniOrderNum) {
        this.miniOrderNum = miniOrderNum;
    }

    public Boolean getIsPricetag() {
        return isPricetag;
    }

    public void setIsPricetag(Boolean pricetag) {
        isPricetag = pricetag;
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
    public BigDecimal getVipPrice1(){
        return vipPrice1;
    }

    public void setVipPrice1(BigDecimal vipPrice1){
        this.vipPrice1 = vipPrice1;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public BigDecimal getStoreUpLimit() {
        return storeUpLimit;
    }

    public BigDecimal getStoreLowLimit() {
        return storeLowLimit;
    }

    public void setStoreUpLimit(BigDecimal storeUpLimit) {
        this.storeUpLimit = storeUpLimit;
    }

    public void setStoreLowLimit(BigDecimal storeLowLimit) {
        this.storeLowLimit = storeLowLimit;
    }

    public BigInteger getPackingUnitId() {
        return packingUnitId;
    }

    public String getPackingUnitName() {
        return packingUnitName;
    }

    public void setPackingUnitId(BigInteger packingUnitId) {
        this.packingUnitId = packingUnitId;
    }

    public void setPackingUnitName(String packingUnitName) {
        this.packingUnitName = packingUnitName;
    }

    public BigDecimal getUnitRelation() {
        return unitRelation;
    }

    public void setUnitRelation(BigDecimal unitRelation) {
        this.unitRelation = unitRelation;
    }

    public BigInteger getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(BigInteger supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getWeighPlu() {
        return weighPlu;
    }

    public void setWeighPlu(Integer weighPlu) {
        this.weighPlu = weighPlu;
    }

    public BigInteger getRelatedGoodsId() {
        return relatedGoodsId;
    }

    public void setRelatedGoodsId(BigInteger relatedGoodsId) {
        this.relatedGoodsId = relatedGoodsId;
    }

    public BigDecimal getBoxPrice() {
        return boxPrice;
    }

    public void setBoxPrice(BigDecimal boxPrice) {
        this.boxPrice = boxPrice;
    }
}
