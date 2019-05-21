package erp.chain.domain;

import com.saas.common.util.LogUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by xumx on 2016/11/1.
 */
public class Branch extends BaseDomain  implements Comparable<Branch>{
    /**
     * 0总部1配送中心2直营店3加盟店4积分商城5农贸市场6农贸档口
     */
    Integer branchType;
    /**
     * 代码
     */
    String code;
    /**
     * 名称
     */
    String name;
    /**
     * 联系电话
     */
    String phone;
    /**
     * 联系人
     */
    String contacts;
    /**
     * 地址
     */
    String address;
    /**
     *
     */
    String geolocation;
    /**
     *
     */
    BigInteger areaId;
    /**
     *
     */
    BigInteger parentId;
    /**
     * 状态0 停用1启用
     */
    Integer status;
    /**
     * 备注
     */
    String memo;
    /**
     * 1实体店2网店3微店
     */
    Integer type;
    /**
     * 是否微餐厅
     */
    boolean isTinyhall;
    /**
     * 是否外卖0否1是
     */
    boolean isTakeout;
    /**
     * 外卖状态：1-正常，2-暂停
     */
    Integer takeoutStatus;
    /**
     * 起送金额
     */
    BigDecimal amount;
    /**
     * 起送范围
     */
    Integer takeoutRange;
    /**
     * 送餐费
     */
    BigDecimal takeoutAmount;
    /**
     * 配送时间段 08:00-11:00 (24小时制）
     */
    String takeoutTime;
    /**
     * 配送开始时间
     */
    String startTakeoutTime;
    /**
     * 配送结束时间
     */
    String endTakeoutTime;

    /**
     * 手机要货范围设置
     * */
    Integer isControlRange;

    public Integer getIsControlRange() {
        return isControlRange;
    }

    public void setIsControlRange(Integer isControlRange) {
        this.isControlRange = isControlRange;
    }

    BigInteger id;
    BigInteger tenantId;
    BigInteger version;
    BigInteger localId;
    String business;
    //机构距离
    BigDecimal distanceValue;
    String meituanToken;
    String meituanBusiness;

    String groupCode;
    String appVersion;
    boolean isEffective;

    /**
     * 盘点时是否隐藏库存数量
     * */
    boolean isHiddenStore;
    Integer effectiveDays=0;
    Date effectiveDate;
    Integer posSlaves=0;
    String mposVersion;
    String fposVersion;
    String aerpVersion;
    /**
     * 收银秤版本号
     */
    String aposScaleVersion;
    private String meiTuanPoiId;
    private String meiTuanPoiName;
    private Integer smsCount=0;
    private String smsUseWay;
    private Integer smsStatus=0;
    /**
     * 综合商业类型1-商圈（购物中心）2-农贸市场
     */
    Integer commercialType;
    /**
     * NULL，1-拉卡拉，2-建行，3-工行，4-聚合支付，5-招商银行，6-联迪支付
     */
    Integer environmentType;

    public Integer getEnvironmentType(){
        return environmentType;
    }

    public void setEnvironmentType(Integer environmentType){
        this.environmentType = environmentType;
    }

    public Integer getCommercialType(){
        return commercialType;
    }

    public void setCommercialType(Integer commercialType){
        this.commercialType = commercialType;
    }

    public Integer getSmsCount(){
        return smsCount;
    }

    public void setSmsCount(Integer smsCount){
        this.smsCount = smsCount;
    }

    public String getSmsUseWay(){
        return smsUseWay;
    }

    public void setSmsUseWay(String smsUseWay){
        this.smsUseWay = smsUseWay;
    }

    public Integer getSmsStatus(){
        return smsStatus;
    }

    public void setSmsStatus(Integer smsStatus){
        this.smsStatus = smsStatus;
    }

    public String getAerpVersion(){
        return aerpVersion;
    }

    public void setAerpVersion(String aerpVersion){
        this.aerpVersion = aerpVersion;
    }

    public String getMposVersion(){
        return mposVersion;
    }

    public String getAposScaleVersion(){
        return aposScaleVersion;
    }

    public void setAposScaleVersion(String aposScaleVersion){
        this.aposScaleVersion = aposScaleVersion;
    }

    public void setMposVersion(String mposVersion){
        this.mposVersion = mposVersion;
    }

    public String getFposVersion(){
        return fposVersion;
    }

    public void setFposVersion(String fposVersion){
        this.fposVersion = fposVersion;
    }

    public Integer getPosSlaves(){
        return posSlaves;
    }

    public void setPosSlaves(Integer posSlaves){
        this.posSlaves = posSlaves;
    }

    public Integer getEffectiveDays(){
        return effectiveDays;
    }

    public void setEffectiveDays(Integer effectiveDays){
        this.effectiveDays = effectiveDays;
    }

    public Date getEffectiveDate(){
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate){
        this.effectiveDate = effectiveDate;
    }

    public boolean getIsHiddenStore() {
        return isHiddenStore;
    }

    public void setIsHiddenStore(boolean isHiddenStore) {
        this.isHiddenStore = isHiddenStore;
    }

    public boolean getIsEffective(){
        return isEffective;
    }

    public void setIsEffective(boolean effective){
        isEffective = effective;
    }

    public String getAppVersion(){
        return appVersion;
    }

    public void setAppVersion(String appVersion){
        this.appVersion = appVersion;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public boolean getIsTakeout() {
        return isTakeout;
    }
    public String getMeituanBusiness() {
        return meituanBusiness;
    }

    public void setMeituanBusiness(String meituanBusiness) {
        this.meituanBusiness = meituanBusiness;
    }

    public String getMeituanToken(){
        return meituanToken;
    }

    public void setMeituanToken(String meituanToken){
        this.meituanToken = meituanToken;
    }

    public BigDecimal getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(BigDecimal distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public void setIsTinyhall(boolean isTinyhall) {
        this.isTinyhall = isTinyhall;
    }

    public void setIsTakeout(boolean isTakeout) {
        this.isTakeout = isTakeout;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getShippingPriceType() {
        return shippingPriceType;
    }

    public void setShippingPriceType(Integer shippingPriceType) {
        this.shippingPriceType = shippingPriceType;
    }

    public boolean getIsBuffet() {
        return isBuffet;
    }

    public void setIsBuffet(boolean isBuffet) {
        this.isBuffet = isBuffet;
    }

    public boolean getIsInvite() {
        return isInvite;
    }

    public void setIsInvite(boolean isInvite) {
        this.isInvite = isInvite;
    }

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public boolean getIsDockingExternalSystem() {
        return isDockingExternalSystem;
    }

    public void setIsDockingExternalSystem(boolean isDockingExternalSystem) {
        this.isDockingExternalSystem = isDockingExternalSystem;
    }

    public Integer getExternalSystem() {
        return externalSystem;
    }

    public void setExternalSystem(Integer externalSystem) {
        this.externalSystem = externalSystem;
    }

    public boolean isAutomaticExamination() {
        return isAutomaticExamination;
    }

    public void setIsAutomaticExamination(boolean isAutomaticExamination) {
        this.isAutomaticExamination = isAutomaticExamination;
    }

    public boolean getIsAllowPayLater() {
        return isAllowPayLater;
    }

    public void setIsAllowPayLater(boolean isAllowPayLater) {
        this.isAllowPayLater = isAllowPayLater;
    }

    public String getAllowPayWay() {
        return allowPayWay;
    }

    public void setAllowPayWay(String allowPayWay) {
        this.allowPayWay = allowPayWay;
    }

    Date createAt;
    String createBy;
    Date lastUpdateAt;
    String lastUpdateBy;
    boolean isDeleted;
    /**
     * 1=配送价1, 2=配送价2
     */
    Integer shippingPriceType;
    boolean isBuffet = false;
    boolean isInvite = false;
    /**
     * 配送中心ID
     */
    BigInteger distributionCenterId;
    /**
     * 是否对接外部系统
     */
    boolean isDockingExternalSystem;
    /**
     * 外部系统 1-管家婆【财贸双全II】
     */
    Integer externalSystem;
    /**
     * 要货单是否自动审核
     */
    boolean isAutomaticExamination;
    /**
     * 是否允许先下单后付款
     */
    boolean isAllowPayLater;
    /**
     * 微餐厅支持支付方式,1微信支付 3货到付款 4储值支付 5吧台付款 格式：1,2,5
     */
    String allowPayWay;

    private BigInteger shopId;

    private Integer elemeAccountType;

    public Integer getElemeAccountType() {
        return elemeAccountType;
    }

    public void setElemeAccountType(Integer elemeAccountType) {
        this.elemeAccountType = elemeAccountType;
    }

    public BigInteger getShopId() {
        return shopId;
    }

    public void setShopId(BigInteger shopId) {
        this.shopId = shopId;
    }

    public Branch() {
        super();
    }

    public Branch(Map domainMap) {
        super(domainMap);
    }

    public Integer getBranchType() {
        return branchType;
    }

    public void setBranchType(Integer branchType) {
        this.branchType = branchType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public BigInteger getAreaId() {
        return areaId;
    }

    public void setAreaId(BigInteger areaId) {
        this.areaId = areaId;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean getIsTinyhall() {
        return isTinyhall;
    }

    public void setTakeout(boolean takeout) {
        isTakeout = takeout;
    }

    public Integer getTakeoutStatus() {
        return takeoutStatus;
    }

    public void setTakeoutStatus(Integer takeoutStatus) {
        this.takeoutStatus = takeoutStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTakeoutRange() {
        return takeoutRange;
    }

    public void setTakeoutRange(Integer takeoutRange) {
        this.takeoutRange = takeoutRange;
    }

    public BigDecimal getTakeoutAmount() {
        return takeoutAmount;
    }

    public void setTakeoutAmount(BigDecimal takeoutAmount) {
        this.takeoutAmount = takeoutAmount;
    }

    public String getTakeoutTime() {
        return takeoutTime;
    }

    public void setTakeoutTime(String takeoutTime) {
        this.takeoutTime = takeoutTime;
    }

    public String getStartTakeoutTime() {
        return startTakeoutTime;
    }

    public void setStartTakeoutTime(String startTakeoutTime) {
        this.startTakeoutTime = startTakeoutTime;
    }

    public String getEndTakeoutTime() {
        return endTakeoutTime;
    }

    public void setEndTakeoutTime(String endTakeoutTime) {
        this.endTakeoutTime = endTakeoutTime;
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

    /**
     * 是否管理自营商品 0-否,1-是
     * */
    boolean isManageGoods;

    public boolean getIsManageGoods() {
        return isManageGoods;
    }

    public void setIsManageGoods(boolean isManageGoods) {
        this.isManageGoods = isManageGoods;
    }

    /**
     * 是否使用总部菜品 0-否，1-是
     * */
    Integer isUseHqGoods;

    public Integer getIsUseHqGoods() {
        return isUseHqGoods;
    }

    public void setIsUseHqGoods(Integer isUseHqGoods) {
        this.isUseHqGoods = isUseHqGoods;
    }

    public String getMeiTuanPoiId() {
        return meiTuanPoiId;
    }

    public void setMeiTuanPoiId(String meiTuanPoiId) {
        this.meiTuanPoiId = meiTuanPoiId;
    }

    public String getMeiTuanPoiName() {
        return meiTuanPoiName;
    }

    public void setMeiTuanPoiName(String meiTuanPoiName) {
        this.meiTuanPoiName = meiTuanPoiName;
    }

    @Override
    public int compareTo(Branch o) {
        try {
            //判断是否处于营业时间
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String start = "08:00";
            String end = "23:00";
            if(this.getTakeoutTime() != null){
                start = this.takeoutTime.substring(0,5);
                end = this.takeoutTime.substring(6,11);
            }
            //当前对象
            int curFlag = 0;
            if(format.parse(start).before(format.parse(format.format(now))) && format.parse(format.format(now)).before(format.parse(end))){
                curFlag = -1;
            } else {
                curFlag = 1;
            }
            //判断营业开关
            if(this.takeoutStatus == 2){
                curFlag = 1;
            }
            //比较对象
            int oFlag = 0;
            if(o.getTakeoutTime() != null){
                start = o.takeoutTime.substring(0,5);
                end = o.takeoutTime.substring(6,11);
            } else {
                start = "08:00";
                end = "23:00";
            }
            if(format.parse(start).before(format.parse(format.format(now))) && format.parse(format.format(now)).before(format.parse(end))){
                oFlag = -1;
            } else {
                oFlag = 1;
            }
            //判断营业状态
            if(o.takeoutStatus == 2){
                oFlag = 1;
            }

            if(curFlag > oFlag){
                return 1;
            } else if(curFlag < oFlag){
                return -1;
            } else {
                //营业状态相同则比较距离
                if(this.getDistanceValue() != null && o.getDistanceValue() != null){
                    return this.distanceValue.compareTo(o.distanceValue);
                } else {
                    return 0;
                }
            }
        } catch (Exception e){
            LogUtil.logError(e.getMessage());
        }
        return 0;
    }
}
