package erp.chain.domain.o2o;

import erp.chain.annotations.Transient;
import erp.chain.domain.BaseDomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by songzhiqiang on 2017/1/18.
 */
public class CardCoupons extends BaseDomain {
    /**
     * 主键
     */
    BigInteger id;
    /**
     * 商户ID
     */
    BigInteger tenantId;
    /**
     * 卡劵名称
     */
    String cardName;
    /**
     * 卡劵类型     1:代金券 2:礼品券
     */
    BigInteger cardType;
    /**
     * 使用限额
     */
    BigDecimal limitValue;
    /**
     * 面额
     */
    BigDecimal faceValue;
    /**
     *赠品ID
     */
    BigInteger giftGoodsId;
    /**
     * 赠品数量
     */
    BigInteger giftNum;
    /**
     * 赠品名称
     */
    String giftGoodsName;
    /**
     * 開始時間
     */
    Date startTime;
    /**
     * 結束時間
     */
    Date endTime;
    /**
     * 有效期。(单位：天)
     */
    BigInteger periodOfValidity;
    /**
     * 颜色值
     */
    String colorValue;
    /**
     * 赠品图片
     */
    String giftGoodsPhoto;
    /**
     * 备注
     */
    String remark;
    /**
     * 创建人(申请人)
     */
    String createBy;
    /**
     * 创建时间(申请时间)
     */
    Date createAt;
    /**
     * 更新人
     */
    String lastUpdateBy;
    /**
     * 更新时间
     */
    Date lastUpdateAt;
    /**
     * 是否删除
     */
    boolean isDeleted;

    BigDecimal discount;

    String limitGoodsIds;
    String limitBranchIds;
    String limitGoodsNames;
    String limitBranchNames;
    boolean openSendRule;
    boolean openUseRule;
    boolean status;

    BigInteger festivalId;
    /**
     * 手动领券：0不允许1-允许
     */
    Integer manualGet=1;
    /**
     * 发行总量
     */
    BigDecimal sendAllAmount;
    /**
     * 已发数量
     */
    BigDecimal sendedAmount;
    /**
     * N日后可用，默认0为立即可用，N天后可用
     */
    Integer useLimit=0;

    /**
     * 每日领取上限
     */
    BigDecimal dayGetLimit;
    /**
     * 累计领取上限
     */
    BigDecimal totalGetLimit;
    Integer weekday1=0;
    Integer weekday2=0;
    Integer weekday3=0;
    Integer weekday4=0;
    Integer weekday5=0;
    Integer weekday6=0;
    Integer weekday7=0;
    /**
     * 使用时间段（开始）
     */
    Date useStartTime;
    /**
     * 使用时间段（结束）
     */
    Date useEndTime;
    /**
     * 发券时间（开始）
     */
    Date sendStartTime;
    /**
     * 发券时间（结束）
     */
    Date sendEndTime;
    /**
     * 领取的最低消费金额
     */
    BigDecimal sendLimitValue;

    BigInteger branchId;

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public BigDecimal getSendLimitValue(){
        return sendLimitValue;
    }

    public void setSendLimitValue(BigDecimal sendLimitValue){
        this.sendLimitValue = sendLimitValue;
    }

    public Integer getManualGet(){
        return manualGet;
    }

    public void setManualGet(Integer manualGet){
        this.manualGet = manualGet;
    }

    public BigDecimal getSendAllAmount(){
        return sendAllAmount;
    }

    public void setSendAllAmount(BigDecimal sendAllAmount){
        this.sendAllAmount = sendAllAmount;
    }

    public BigDecimal getSendedAmount(){
        return sendedAmount;
    }

    public void setSendedAmount(BigDecimal sendedAmount){
        this.sendedAmount = sendedAmount;
    }

    public Integer getUseLimit(){
        return useLimit;
    }

    public void setUseLimit(Integer useLimit){
        this.useLimit = useLimit;
    }

    public BigDecimal getDayGetLimit(){
        return dayGetLimit;
    }

    public void setDayGetLimit(BigDecimal dayGetLimit){
        this.dayGetLimit = dayGetLimit;
    }

    public BigDecimal getTotalGetLimit(){
        return totalGetLimit;
    }

    public void setTotalGetLimit(BigDecimal totalGetLimit){
        this.totalGetLimit = totalGetLimit;
    }

    public Integer getWeekday1(){
        return weekday1;
    }

    public void setWeekday1(Integer weekday1){
        this.weekday1 = weekday1;
    }

    public Integer getWeekday2(){
        return weekday2;
    }

    public void setWeekday2(Integer weekday2){
        this.weekday2 = weekday2;
    }

    public Integer getWeekday3(){
        return weekday3;
    }

    public void setWeekday3(Integer weekday3){
        this.weekday3 = weekday3;
    }

    public Integer getWeekday4(){
        return weekday4;
    }

    public void setWeekday4(Integer weekday4){
        this.weekday4 = weekday4;
    }

    public Integer getWeekday5(){
        return weekday5;
    }

    public void setWeekday5(Integer weekday5){
        this.weekday5 = weekday5;
    }

    public Integer getWeekday6(){
        return weekday6;
    }

    public void setWeekday6(Integer weekday6){
        this.weekday6 = weekday6;
    }

    public Integer getWeekday7(){
        return weekday7;
    }

    public void setWeekday7(Integer weekday7){
        this.weekday7 = weekday7;
    }

    public Date getUseStartTime(){
        return useStartTime;
    }

    public void setUseStartTime(Date useStartTime){
        this.useStartTime = useStartTime;
    }

    public Date getUseEndTime(){
        return useEndTime;
    }

    public void setUseEndTime(Date useEndTime){
        this.useEndTime = useEndTime;
    }

    public Date getSendStartTime(){
        return sendStartTime;
    }

    public void setSendStartTime(Date sendStartTime){
        this.sendStartTime = sendStartTime;
    }

    public Date getSendEndTime(){
        return sendEndTime;
    }

    public void setSendEndTime(Date sendEndTime){
        this.sendEndTime = sendEndTime;
    }

    @Transient
    ActivityVo activityVo;

    public ActivityVo getActivityVo() {
        return activityVo;
    }

    public void setActivityVo(ActivityVo activityVo) {
        this.activityVo = activityVo;
    }

    public BigInteger getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(BigInteger festivalId) {
        this.festivalId = festivalId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLimitGoodsIds() {
        return limitGoodsIds;
    }

    public void setLimitGoodsIds(String limitGoodsIds) {
        this.limitGoodsIds = limitGoodsIds;
    }

    public String getLimitBranchIds() {
        return limitBranchIds;
    }

    public void setLimitBranchIds(String limitBranchIds) {
        this.limitBranchIds = limitBranchIds;
    }

    public String getLimitGoodsNames() {
        return limitGoodsNames;
    }

    public void setLimitGoodsNames(String limitGoodsNames) {
        this.limitGoodsNames = limitGoodsNames;
    }

    public String getLimitBranchNames() {
        return limitBranchNames;
    }

    public void setLimitBranchNames(String limitBranchNames) {
        this.limitBranchNames = limitBranchNames;
    }

    public boolean isOpenSendRule() {
        return openSendRule;
    }

    public void setOpenSendRule(boolean openSendRule) {
        this.openSendRule = openSendRule;
    }

    public boolean isOpenUseRule() {
        return openUseRule;
    }

    public void setOpenUseRule(boolean openUseRule) {
        this.openUseRule = openUseRule;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public BigInteger getCardType() {
        return cardType;
    }

    public void setCardType(BigInteger cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(BigDecimal limitValue) {
        this.limitValue = limitValue;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigInteger getGiftGoodsId() {
        return giftGoodsId;
    }

    public void setGiftGoodsId(BigInteger giftGoodsId) {
        this.giftGoodsId = giftGoodsId;
    }

    public String getGiftGoodsName() {
        return giftGoodsName;
    }

    public void setGiftGoodsName(String giftGoodsName) {
        this.giftGoodsName = giftGoodsName;
    }

    public BigInteger getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(BigInteger giftNum) {
        this.giftNum = giftNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigInteger getPeriodOfValidity() {
        return periodOfValidity;
    }

    public void setPeriodOfValidity(BigInteger periodOfValidity) {
        this.periodOfValidity = periodOfValidity;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getGiftGoodsPhoto() {
        return giftGoodsPhoto;
    }

    public void setGiftGoodsPhoto(String giftGoodsPhoto) {
        this.giftGoodsPhoto = giftGoodsPhoto;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
