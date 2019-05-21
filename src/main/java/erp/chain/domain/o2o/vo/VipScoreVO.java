package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by wangms on 2017/3/3.
 */
public class VipScoreVO {

    String scoreType;
    BigInteger tenantId;
    String createBy;
    BigInteger goodsId;
    BigDecimal quantity;
    Integer isFreeOfCharge;

    /**
     * 会员id
     */
    BigInteger vipId;
    /**
     * 需要积分的金额
     */
    BigDecimal amount;
    /**
     * 交易单号
     */
    String tradeCode;
    /**
     * 支付方式
     */
    String paymentCode;
    /**
     * 是否退款 0消费 1退款
     */
    String isRefund;
    BigInteger branchId;
    BigInteger empId;
    BigInteger paymentId;
    Date tradeDate;
    Integer isLongAmount;

    public Integer getIsLongAmount(){
        return isLongAmount;
    }

    public void setIsLongAmount(Integer isLongAmount){
        this.isLongAmount = isLongAmount;
    }

    public Date getTradeDate(){
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate){
        this.tradeDate = tradeDate;
    }

    public String getScoreType(){
        return scoreType;
    }

    public void setScoreType(String scoreType){
        this.scoreType = scoreType;
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public String getCreateBy(){
        return createBy;
    }

    public void setCreateBy(String createBy){
        this.createBy = createBy;
    }

    public BigInteger getGoodsId(){
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId){
        this.goodsId = goodsId;
    }

    public BigDecimal getQuantity(){
        return quantity;
    }

    public void setQuantity(BigDecimal quantity){
        this.quantity = quantity;
    }

    public Integer getIsFreeOfCharge(){
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(Integer isFreeOfCharge){
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public BigInteger getPaymentId(){
        return paymentId;
    }

    public void setPaymentId(BigInteger paymentId){
        this.paymentId = paymentId;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public BigInteger getEmpId(){
        return empId;
    }

    public void setEmpId(BigInteger empId){
        this.empId = empId;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }
}
