package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2019/5/5
 */
public class SaleDetailAbnormal extends BaseDomain{
    /**
     * 销售账单号
     */
    String saleCode;
    /**
     * 套餐
     */
    BigInteger packageId;
    /**
     * 商品
     */
    BigInteger goodsId;
    /**
     * 是否套餐或套餐内商品
     */
    boolean isPackage;
    /**
     * 促销活动
     */
    BigInteger promotionId;
    /**
     * 数量
     */
    BigDecimal quantity;
    /**
     * 售价
     */
    BigDecimal salePrice;
    /**
     * 实际售价
     */
    BigDecimal salePriceActual;
    /**
     * 应收合计
     */
    BigDecimal totalAmount;
    /**
     * 折扣额
     */
    BigDecimal discountAmount;
    /**
     * 是否免单或赠送
     */
    Integer isFreeOfCharge;
    /**
     * 实收合计
     */
    BigDecimal receivedAmount;
    /**
     * 是否冲销
     */
    Integer isRefund;
    /**
     *
     */
    boolean isPrinted;
    /**
     *
     */
    boolean isProduced;
    /**
     *
     */
    boolean isServed;
    /**
     *
     */
    BigInteger parentPackagId;
    /**
     * 所属机构，默认为0，代表总部
     */
    BigInteger branchId;

    BigInteger id;
    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;
    BigInteger tenantId;

    String localSign;
    BigDecimal specAmount;
    String memo;

    BigInteger vipId;
    BigInteger cashier;
    BigInteger guideId;
    Integer transTerminal=1;
    Integer payType=1;
    String batchId;
    String batchGoodsCode;

    public String getBatchId(){
        return batchId;
    }

    public void setBatchId(String batchId){
        this.batchId = batchId;
    }

    public String getBatchGoodsCode(){
        return batchGoodsCode;
    }

    public void setBatchGoodsCode(String batchGoodsCode){
        this.batchGoodsCode = batchGoodsCode;
    }

    public Integer getTransTerminal(){
        return transTerminal;
    }

    public void setTransTerminal(Integer transTerminal){
        this.transTerminal = transTerminal;
    }

    public Integer getPayType(){
        return payType;
    }

    public void setPayType(Integer payType){
        this.payType = payType;
    }

    public BigInteger getGuideId(){
        return guideId;
    }

    public void setGuideId(BigInteger guideId){
        this.guideId = guideId;
    }

    public BigInteger getCashier(){
        return cashier;
    }

    public void setCashier(BigInteger cashier){
        this.cashier = cashier;
    }

    public BigInteger getVipId(){
        return vipId;
    }

    public void setVipId(BigInteger vipId){
        this.vipId = vipId;
    }

    public String getMemo(){
        return memo;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }

    public BigDecimal getSpecAmount(){
        return specAmount;
    }

    public void setSpecAmount(BigDecimal specAmount){
        this.specAmount = specAmount;
    }

    public SaleDetailAbnormal() {
        super();
    }

    public SaleDetailAbnormal(Map domainMap) {
        super(domainMap);
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public boolean getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean aPackage) {
        isPackage = aPackage;
    }

    public BigInteger getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(BigInteger promotionId) {
        this.promotionId = promotionId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePriceActual() {
        return salePriceActual;
    }

    public void setSalePriceActual(BigDecimal salePriceActual) {
        this.salePriceActual = salePriceActual;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(Integer freeOfCharge) {
        isFreeOfCharge = freeOfCharge;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Integer isRefund() {
        return isRefund;
    }

    public void setRefund(Integer refund) {
        isRefund = refund;
    }

    public boolean getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(boolean printed) {
        isPrinted = printed;
    }

    public boolean isProduced() {
        return isProduced;
    }

    public void setProduced(boolean produced) {
        isProduced = produced;
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean served) {
        isServed = served;
    }

    public BigInteger getParentPackagId() {
        return parentPackagId;
    }

    public void setParentPackagId(BigInteger parentPackagId) {
        this.parentPackagId = parentPackagId;
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

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getLocalSign() {
        return localSign;
    }

    public void setLocalSign(String localSign) {
        this.localSign = localSign;
    }
}
