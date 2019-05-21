package erp.chain.domain.o2o;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by wangms on 2017/4/1.
 */
public class MeituanOrder {
    BigInteger avgSendTime;

    /**
     * 订单创建时间
     */
    BigInteger cTime;
    /**
     * 订单备注
     */
    String caution;
    /**
     * 用户预计送达时间，“立即送达”时为0
     */
    BigInteger deliveryTime;
    /**
     * 订单菜品详情 参照 detail
     */
    String detail;
    /**
     * 	城市Id
     */
    BigInteger cityId;
    /**
     * 机构当天的订单流水号
     */
    String daySeq;
    /**
     * erp方机构id
     */
    String ePoiId;
    /**
     * 订单扩展信息 参考 extras
     */
    String extras;
    /**
     * 是否需要发票
     */
    BigInteger hasInvoiced;
    /**
     * 发票抬头
     */
    String invoiceTitle;
    /**
     * 用户是否收藏此机构
     */
    boolean isFavorites;
    /**
     * 用户是否第一次在此机构点餐
     */
    boolean isPoiFirstOrder;
    /**
     * 是否是预定单
     */
    BigInteger isPre;
    /**
     * 是否第三方配送 0：否；1：是
     */
    BigInteger isThirdShipping;
    /**
     * 实际送餐地址纬度
     */
    BigDecimal latitude;
    /**
     * 实际送餐地址经度
     */
    BigDecimal longitude;
    /**
     * 商家对账信息
     */
    String poiReceiveDetail;
    /**
     * 配送类型码
     */
    String logisticsCode;
    /**
     * 配送完成时间
     */
    BigInteger logisticsCompletedTime;
    /**
     * 配送单确认时间，骑手接单时间
     */
    BigInteger logisticsConfirmTime;
    /**
     * 骑手电话
     */
    BigInteger logisticsDispatcherMobile;
    /**
     * 骑手姓名
     */
    String logisticsDispatcherName;
    /**
     * 骑手取单时间
     */
    BigInteger logisticsFetchTime;
    /**
     * 配送方ID
     */
    Integer logisticsId;
    /**
     * 配送方名称
     */
    String logisticsName;
    /**
     * 配送单下单时间
     */
    BigInteger logisticsSendTime;
    /**
     * 配送订单状态code
     */
    Integer logisticsStatus;
    /**
     * 订单完成时间
     */
    BigInteger orderCompletedTime;
    /**
     * 商户确认时间
     */
    BigInteger orderConfirmTime;
    /**
     * 订单取消时间
     */
    BigInteger orderCancelTime;
    /**
     * 订单Id
     */
    BigInteger orderId;
    /**
     * 订单展示Id
     */
    BigInteger orderIdView;
    /**
     * 用户下单时间
     */
    BigInteger orderSendTime;
    /**
     * 订单原价
     */
    BigDecimal originalPrice;
    /**
     * 订单支付类型
     */
    Integer payType;
    /**
     * 机构地址
     */
    String poiAddress;
    /**
     * 机构Id
     */
    BigInteger poiId;
    /**
     * 机构名称
     */
    String poiName;
    /**
     * 机构服务电话
     */
    String poiPhone;
    /**
     * 收货人地址
     */
    String recipientAddress;
    /**
     * 收货人名称
     */
    String recipientName;
    /**
     * 收货人电话
     */
    String recipientPhone;
    /**
     * 骑手电话
     */
    String shipperPhone;
    /**
     * 配送费用
     */
    BigDecimal shippingFee;
    /**
     * 订单状态
     */
    Integer status;
    /**
     * 总价
     */
    BigDecimal total;
    /**
     * 订单更新时间
     */
    BigInteger uTime;

    public BigInteger getAvgSendTime() {
        return avgSendTime;
    }

    public void setAvgSendTime(BigInteger avgSendTime) {
        this.avgSendTime = avgSendTime;
    }

    public BigInteger getcTime() {
        return cTime;
    }

    public void setcTime(BigInteger cTime) {
        this.cTime = cTime;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public BigInteger getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(BigInteger deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigInteger getCityId() {
        return cityId;
    }

    public void setCityId(BigInteger cityId) {
        this.cityId = cityId;
    }

    public String getDaySeq() {
        return daySeq;
    }

    public void setDaySeq(String daySeq) {
        this.daySeq = daySeq;
    }

    public String getePoiId() {
        return ePoiId;
    }

    public void setePoiId(String ePoiId) {
        this.ePoiId = ePoiId;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public BigInteger getHasInvoiced() {
        return hasInvoiced;
    }

    public void setHasInvoiced(BigInteger hasInvoiced) {
        this.hasInvoiced = hasInvoiced;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setIsFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public boolean isPoiFirstOrder() {
        return isPoiFirstOrder;
    }

    public void setIsPoiFirstOrder(boolean isPoiFirstOrder) {
        this.isPoiFirstOrder = isPoiFirstOrder;
    }

    public BigInteger getIsPre() {
        return isPre;
    }

    public void setIsPre(BigInteger isPre) {
        this.isPre = isPre;
    }

    public BigInteger getIsThirdShipping() {
        return isThirdShipping;
    }

    public void setIsThirdShipping(BigInteger isThirdShipping) {
        this.isThirdShipping = isThirdShipping;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getPoiReceiveDetail() {
        return poiReceiveDetail;
    }

    public void setPoiReceiveDetail(String poiReceiveDetail) {
        this.poiReceiveDetail = poiReceiveDetail;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public BigInteger getLogisticsCompletedTime() {
        return logisticsCompletedTime;
    }

    public void setLogisticsCompletedTime(BigInteger logisticsCompletedTime) {
        this.logisticsCompletedTime = logisticsCompletedTime;
    }

    public BigInteger getLogisticsConfirmTime() {
        return logisticsConfirmTime;
    }

    public void setLogisticsConfirmTime(BigInteger logisticsConfirmTime) {
        this.logisticsConfirmTime = logisticsConfirmTime;
    }

    public BigInteger getLogisticsDispatcherMobile() {
        return logisticsDispatcherMobile;
    }

    public void setLogisticsDispatcherMobile(BigInteger logisticsDispatcherMobile) {
        this.logisticsDispatcherMobile = logisticsDispatcherMobile;
    }

    public String getLogisticsDispatcherName() {
        return logisticsDispatcherName;
    }

    public void setLogisticsDispatcherName(String logisticsDispatcherName) {
        this.logisticsDispatcherName = logisticsDispatcherName;
    }

    public BigInteger getLogisticsFetchTime() {
        return logisticsFetchTime;
    }

    public void setLogisticsFetchTime(BigInteger logisticsFetchTime) {
        this.logisticsFetchTime = logisticsFetchTime;
    }

    public Integer getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Integer logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public BigInteger getLogisticsSendTime() {
        return logisticsSendTime;
    }

    public void setLogisticsSendTime(BigInteger logisticsSendTime) {
        this.logisticsSendTime = logisticsSendTime;
    }

    public Integer getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(Integer logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public BigInteger getOrderCompletedTime() {
        return orderCompletedTime;
    }

    public void setOrderCompletedTime(BigInteger orderCompletedTime) {
        this.orderCompletedTime = orderCompletedTime;
    }

    public BigInteger getOrderConfirmTime() {
        return orderConfirmTime;
    }

    public void setOrderConfirmTime(BigInteger orderConfirmTime) {
        this.orderConfirmTime = orderConfirmTime;
    }

    public BigInteger getOrderCancelTime() {
        return orderCancelTime;
    }

    public void setOrderCancelTime(BigInteger orderCancelTime) {
        this.orderCancelTime = orderCancelTime;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public BigInteger getOrderIdView() {
        return orderIdView;
    }

    public void setOrderIdView(BigInteger orderIdView) {
        this.orderIdView = orderIdView;
    }

    public BigInteger getOrderSendTime() {
        return orderSendTime;
    }

    public void setOrderSendTime(BigInteger orderSendTime) {
        this.orderSendTime = orderSendTime;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

    public BigInteger getPoiId() {
        return poiId;
    }

    public void setPoiId(BigInteger poiId) {
        this.poiId = poiId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiPhone() {
        return poiPhone;
    }

    public void setPoiPhone(String poiPhone) {
        this.poiPhone = poiPhone;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigInteger getuTime() {
        return uTime;
    }

    public void setuTime(BigInteger uTime) {
        this.uTime = uTime;
    }
}
