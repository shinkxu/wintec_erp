package erp.chain.domain.online;

import erp.chain.annotations.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-04-10.
 */
@Table(name = "goods")
public class OnlineGoods {
    private BigInteger id;
    private BigInteger tenantId;
    /**
     * 创建菜品的机构id(组织机构ID),默认为0 总部创建
     */
    private BigInteger branchId;
    /**
     * 商品分类 外键
     */
    private BigInteger categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 商品编码
     */
    private String goodsCode;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品简称
     */
    private String shortName;
    /**
     * 单位
     */
    private BigInteger goodsUnitId;
    /**
     * 单位(冗余字段显示使用)
     */
    private String goodsUnitName;
    /**
     * 商品条码
     */
    private String barCode;
    /**
     * 助记码
     */
    private String mnemonic;
    /**
     * 西文名称
     */
    private String westName;
    /**
     * 规格
     */
    private String spec;
    /**
     * 零售价
     */
    private BigDecimal salePrice;
    /**
     * 会员价
     */
    private BigDecimal vipPrice;
    /**
     * 会员价2
     */
    private BigDecimal vipPrice2;
    /**
     * 商品状态
     * 0-正常
     * 1-停售
     * 2-停购
     * 3-淘汰
     */
    private Integer goodsStatus;
    /**
     * 是否有折扣
     */
    private boolean isDsc;
    /**
     * 是否按照最低折扣：0否1是
     */
    private boolean isLowerfee;
    /**
     * 临时商品0否1是
     */
    private boolean isTemp;
    /**
     * 是否赠送券0否1是
     */
    private boolean isGiveticket;
    /**
     * 允许用券0否1是
     */
    private boolean isUseticket;
    /**
     * 参与积分0否1是
     */
    private boolean isScore;
    /**
     * 积分数量
     */
    private BigDecimal scoreValue;
    /**
     * 时价0否1是
     */
    private boolean isChangeprice;
    /**
     * 修改数量0否1是
     */
    private boolean isModifynum;
    /**
     * 是否可改价
     */
    private boolean isRevisedPrice;
    /**
     * 外卖价
     */
    private BigDecimal takeoutPrice;
    /**
     * 图片路径
     */
    private String photo;
    /**
     * 商品描述
     */
    private String goodsDesc;
    /**
     * 标签：逗号分割，最多3个
     */
    private String tag;
    /**
     * 服务费0否1是
     */
    private boolean isServicefee;
    /**
     * 服务提成0否1是
     */
    private boolean isSertc;
    /**
     * 点菜员提成
     */
    private boolean isSaletc;
    /**
     * 点菜员提成方式
     */
    private Integer saleTctype;
    /**
     * 点菜员提成比例
     */
    private BigDecimal saleTcrate;
    /**
     * 点菜员提成金额
     */
    private BigDecimal saleTctotal;
    /**
     * 多规格:0 否1是
     */
    private boolean isMuchspec;
    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 是否管理库存0否1是
     */
    private boolean isStore;
    /**
     * 计价方式:0普通1重量2数量
     */
    private Integer priceType;
    /**
     * 称重0否1是
     */
    private boolean isWeigh;
    /**
     * 可抵扣0否1是
     */
    private boolean isForPoints;
    /**
     * 抵扣价钱
     */
    private BigDecimal pointsValue;
    /**
     * 人气商品
     */
    private boolean isRanked;
    /**
     * 推荐商品
     */
    private boolean isRecommended;
    /**
     * 备注
     */
    private String memo;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    /**
     * 1普通商品2套餐3原料
     */
    private Integer goodsType;
    /**
     * 是否热卖
     */
    private boolean isHotgood;
    /**
     * 是否新品
     */
    private boolean isNewgood;
    /**
     * 是否外卖
     */
    private boolean isTakeout;
    /**
     * 是否自提
     */
    private boolean isTake;
    /**
     * 是否预定
     */
    private boolean isOrder;
    /**
     * 是否堂食
     */
    private boolean isHere;
    /**
     * 店内码
     */
    private String instoreCode;
    /**
     * 最低起订数量
     */
    private Integer miniOrderNum;
    /**
     * 口味分组ID
     */
    private String specGroupId;
    private BigDecimal purchasingPrice;
    private BigDecimal shippingPrice1;
    private BigDecimal shippingPrice2;
    /**
     * 是否打印标价签：0否1是
     */
    private Boolean isPricetag;
    private boolean isCustomPrice;
    private BigInteger version;
    /**
     * 品牌
     */
    private BigInteger brandId;
    private BigInteger localId;
    /**
     * pos本地用销售价格
     */
    private BigDecimal saleOpenPrice;
    /**
     * 组合拆分类型0-无1-组合2-拆分
     */
    private Integer combinationType;
    /**
     * 会员价1（兼容零售老版）
     */
    private BigDecimal vipPrice1;
    /**
     * 规格名称
     */
    private String standardName;
    /**
     * 主规格ID
     */
    private BigInteger parentId;
    /**
     * 库存上限
     */
    private BigDecimal storeUpLimit;
    /**
     * 库存下限
     */
    private BigDecimal storeLowLimit;
    /**
     * 包装单位ID
     */
    private BigInteger packingUnitId;

    /**
     * 包装单位名称
     */
    private String packingUnitName;
    /**
     * 销售单位与包装单位关系
     * 1包装单位 = x * 销售单位
     */
    private BigDecimal unitRelation;
    /**
     * 供应商
     */
    private BigInteger supplierId;
    /**
     * 称重PLU
     */
    private Integer weighPlu;
    /**
     * 积分类型 0不积分1固定积分2金额比例（0-100%）
     */
    private Integer scoreType;
    /**
     * 积分比例0~100
     */
    private BigDecimal scorePercent;
    /**
     * 套餐关联商品ID
     */
    private BigInteger relatedGoodsId;
    private BigDecimal boxPrice = BigDecimal.ZERO;

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

    public void setIsDsc(boolean isDsc) {
        this.isDsc = isDsc;
    }

    public boolean getIsLowerfee() {
        return isLowerfee;
    }

    public void setIsLowerfee(boolean isLowerfee) {
        this.isLowerfee = isLowerfee;
    }

    public boolean getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

    public boolean getIsGiveticket() {
        return isGiveticket;
    }

    public void setIsGiveticket(boolean isGiveticket) {
        this.isGiveticket = isGiveticket;
    }

    public boolean getIsUseticket() {
        return isUseticket;
    }

    public void setIsUseticket(boolean isUseticket) {
        this.isUseticket = isUseticket;
    }

    public boolean getIsScore() {
        return isScore;
    }

    public void setIsScore(boolean isScore) {
        this.isScore = isScore;
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

    public void setIsChangeprice(boolean isChangeprice) {
        this.isChangeprice = isChangeprice;
    }

    public boolean getIsModifynum() {
        return isModifynum;
    }

    public void setModifynum(boolean isModifynum) {
        this.isModifynum = isModifynum;
    }

    public boolean getIsRevisedPrice() {
        return isRevisedPrice;
    }

    public void setRevisedPrice(boolean isRevisedPrice) {
        this.isRevisedPrice = isRevisedPrice;
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

    public void setIsServicefee(boolean isServicefee) {
        this.isServicefee = isServicefee;
    }

    public boolean getIsSertc() {
        return isSertc;
    }

    public void setIsSertc(boolean isSertc) {
        this.isSertc = isSertc;
    }

    public boolean getIsSaletc() {
        return isSaletc;
    }

    public void setIsSaletc(boolean isSaletc) {
        this.isSaletc = isSaletc;
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

    public void setIsMuchspec(boolean isMuchspec) {
        this.isMuchspec = isMuchspec;
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

    public void setIsStore(boolean isStore) {
        this.isStore = isStore;
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

    public void seIstWeigh(boolean isWeigh) {
        this.isWeigh = isWeigh;
    }

    public boolean getIsForPoints() {
        return isForPoints;
    }

    public void setIsForPoints(boolean isForPoints) {
        this.isForPoints = isForPoints;
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

    public void setIsRanked(boolean isRanked) {
        this.isRanked = isRanked;
    }

    public boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public boolean getIsHotgood() {
        return isHotgood;
    }

    public void setIsHotgood(boolean isHotgood) {
        this.isHotgood = isHotgood;
    }

    public boolean getIsNewgood() {
        return isNewgood;
    }

    public void setIsNewgood(boolean isNewgood) {
        this.isNewgood = isNewgood;
    }

    public boolean getIsTakeout() {
        return isTakeout;
    }

    public void setIsTakeout(boolean isTakeout) {
        this.isTakeout = isTakeout;
    }

    public boolean getIsTake() {
        return isTake;
    }

    public void setIsTake(boolean isTake) {
        this.isTake = isTake;
    }

    public boolean getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(boolean isOrder) {
        this.isOrder = isOrder;
    }

    public boolean getIsHere() {
        return isHere;
    }

    public void setIsHere(boolean isHere) {
        this.isHere = isHere;
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

    public String getSpecGroupId() {
        return specGroupId;
    }

    public void setSpecGroupId(String specGroupId) {
        this.specGroupId = specGroupId;
    }

    public BigDecimal getPurchasingPrice() {
        return purchasingPrice;
    }

    public void setPurchasingPrice(BigDecimal purchasingPrice) {
        this.purchasingPrice = purchasingPrice;
    }

    public BigDecimal getShippingPrice1() {
        return shippingPrice1;
    }

    public void setShippingPrice1(BigDecimal shippingPrice1) {
        this.shippingPrice1 = shippingPrice1;
    }

    public BigDecimal getShippingPrice2() {
        return shippingPrice2;
    }

    public void setShippingPrice2(BigDecimal shippingPrice2) {
        this.shippingPrice2 = shippingPrice2;
    }

    public Boolean getIsPricetag() {
        return isPricetag;
    }

    public void setIsPricetag(Boolean isPricetag) {
        this.isPricetag = isPricetag;
    }

    public boolean getIsCustomPrice() {
        return isCustomPrice;
    }

    public void setIsCustomPrice(boolean isCustomPrice) {
        this.isCustomPrice = isCustomPrice;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getBrandId() {
        return brandId;
    }

    public void setBrandId(BigInteger brandId) {
        this.brandId = brandId;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigDecimal getSaleOpenPrice() {
        return saleOpenPrice;
    }

    public void setSaleOpenPrice(BigDecimal saleOpenPrice) {
        this.saleOpenPrice = saleOpenPrice;
    }

    public Integer getCombinationType() {
        return combinationType;
    }

    public void setCombinationType(Integer combinationType) {
        this.combinationType = combinationType;
    }

    public BigDecimal getVipPrice1() {
        return vipPrice1;
    }

    public void setVipPrice1(BigDecimal vipPrice1) {
        this.vipPrice1 = vipPrice1;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public BigDecimal getStoreUpLimit() {
        return storeUpLimit;
    }

    public void setStoreUpLimit(BigDecimal storeUpLimit) {
        this.storeUpLimit = storeUpLimit;
    }

    public BigDecimal getStoreLowLimit() {
        return storeLowLimit;
    }

    public void setStoreLowLimit(BigDecimal storeLowLimit) {
        this.storeLowLimit = storeLowLimit;
    }

    public BigInteger getPackingUnitId() {
        return packingUnitId;
    }

    public void setPackingUnitId(BigInteger packingUnitId) {
        this.packingUnitId = packingUnitId;
    }

    public String getPackingUnitName() {
        return packingUnitName;
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
