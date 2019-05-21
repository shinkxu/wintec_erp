package erp.chain.domain.supply;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2017/3/23.
 */
public class RequireGoodsPaymentOrder {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 要货单ID
     */
    private BigInteger requireGoodsOrderId;
    /**
     * 要货单单据号
     */
    private String requireGoodsOrderNo;
    /**
     * 订单总额
     */
    private BigDecimal orderTotal;
    /**
     * 付款金额
     */
    private BigDecimal paidTotal;
    /**
     * 支付类型：1-支付宝支付，2-微信支付，3-银联支付，4-其他支付，6-银行转账
     */
    private Integer payType;
    /**
     * 提交支付用户ID
     */
    private BigInteger submitBy;
    /**
     * 微信订单号
     */
    private String transactionId;
    /**
     * 完成支付时间
     */
    private Date timeEnd;
    /**
     * 微信支付appId
     */
    private String wechatPayAppid;
    /**
     * 微信支付商户号
     */
    private String wechatPayMchid;
    /**
     * 微信支付子商户号
     */
    private String wechatPaySubMchid;

    /**
     * 支付宝订单号
     */
    private String tradeNo;

    /**
     * 支付宝支付AppId
     */
    private String alipayAppid;

    /**
     * 支付宝买家ID
     */
    private String buyerId;

    /**
     * 支付宝卖家ID
     */
    private String sellerId;
    private BigInteger version;
    private BigInteger localId;
    private BigInteger tenantId;

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getRequireGoodsOrderId() {
        return requireGoodsOrderId;
    }

    public void setRequireGoodsOrderId(BigInteger requireGoodsOrderId) {
        this.requireGoodsOrderId = requireGoodsOrderId;
    }

    public String getRequireGoodsOrderNo() {
        return requireGoodsOrderNo;
    }

    public void setRequireGoodsOrderNo(String requireGoodsOrderNo) {
        this.requireGoodsOrderNo = requireGoodsOrderNo;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getPaidTotal() {
        return paidTotal;
    }

    public void setPaidTotal(BigDecimal paidTotal) {
        this.paidTotal = paidTotal;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigInteger getSubmitBy() {
        return submitBy;
    }

    public void setSubmitBy(BigInteger submitBy) {
        this.submitBy = submitBy;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getWechatPayAppid() {
        return wechatPayAppid;
    }

    public void setWechatPayAppid(String wechatPayAppid) {
        this.wechatPayAppid = wechatPayAppid;
    }

    public String getWechatPayMchid() {
        return wechatPayMchid;
    }

    public void setWechatPayMchid(String wechatPayMchid) {
        this.wechatPayMchid = wechatPayMchid;
    }

    public String getWechatPaySubMchid() {
        return wechatPaySubMchid;
    }

    public void setWechatPaySubMchid(String wechatPaySubMchid) {
        this.wechatPaySubMchid = wechatPaySubMchid;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getAlipayAppid() {
        return alipayAppid;
    }

    public void setAlipayAppid(String alipayAppid) {
        this.alipayAppid = alipayAppid;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getLocalId() {
        return localId;
    }

    public void setLocalId(BigInteger localId) {
        this.localId = localId;
    }
}
