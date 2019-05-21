package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by wangms on 2017/1/23.
 */
public class DietOrderInfoShowVo {
    /**
     * 机构ID（外键）
     */
    BigInteger branchId;
    /**
     * 机构名称
     */
    String branchName;
    /**
     * 订单类型（0：堂食；1：店内自提；2:预约点餐；3：外卖）
     */
    Integer orderMode;
    /**
     * 订单来源
     * */
    Integer orderResource;
    /**
     *订单编号
     */
    String orderCode;
    /**
     * 会员信息表ID（外键）
     */
    BigInteger vipId;

    /*************************会员信息***************************/

    String vipName;

    String vipCode;

    /*************************会员信息***************************/

    /**
     * 订单状态(
     0:已下单;
     1:已接单,提交厨房;2:正在派送;3已派送;<外卖状态>
     4:已上菜;5:已结束;6:已自提<堂食及自提状态>7:排队中
     )
     */
    Integer orderStatus;
    /**
     * 就餐状态(0:未就餐;1:正在就餐;2:已就餐)外卖不填写
     */
    Integer eatStatus;
    /**
     * 支付状态(0:未支付;1:已支付)
     */
    Integer payStatus;
    /**
     * 支付状态(1:微信支付;2:储存卡支付;3:现金支付)
     */
    Integer payWay;
    /**
     * 应付金额
     */
    BigDecimal totalAmount;
    /**
     * 实际支付金额
     */
    BigDecimal receivedAmount;
    /**
     * 使用的积分数
     */
    Integer useScore;
    /**
     * 数量
     */
    Integer amount;
    /**
     * 是否使用优惠券
     */
    boolean isUsePrivilege;
    /**
     * 支付时间
     */
    Date payAt;
    /**
     * 产生积分数
     */
    Integer createScore;
    /**
     *就餐人数
     */
    Integer eatPeople;
    /**
     * 预约时间，如果不是预约类型的订单，则不填写
     */
    Date appointmentDate;
    /**
     * 收货人姓名
     */
    String consignee;
    /**
     * 收货人电话
     */
    String mobilePhone;
    /**
     * 客户外卖时填写的配送时间
     */
    Date allocationDate;
    /**
     * 外卖送达时间
     */
    Date arriveDate;
    /**
     *订单备注信息
     */
    String remark;
    /**
     * 是否免单
     */
    boolean isFreeOfCharge;
    /**
     * pos编码
     */
    String posCode;
    /**
     * 收银员
     */
    String cashier;
    /**
     * 开台时间
     */
    Date tableOpenAt;
    /**
     *
     *是否冲销
     */
    boolean isRefund;
    /**
     * 会员外卖地址表ID（外键）
     */
    BigInteger vipAddressId;
    /**
     * 会员外卖地址表name
     */
    String vipAddressName;

    BigInteger id;

    Date createAt;
    /**
     * 美团当日的订单流水号（小票配送序号）
     */
    String meituanDaySeq;

    String dispatcherName;
    String dispatcherMobile;

    /**
     * 店铺实收
     */
    private BigDecimal income;
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;
    private BigDecimal packageFee;
    private String vipPhone;
    private BigDecimal vipStore;
    private Integer confirmOrderSource;

    public String getVipPhone(){
        return vipPhone;
    }

    public void setVipPhone(String vipPhone){
        this.vipPhone = vipPhone;
    }

    public BigDecimal getVipStore(){
        return vipStore;
    }

    public void setVipStore(BigDecimal vipStore){
        this.vipStore = vipStore;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }

    public String getDispatcherMobile() {
        return dispatcherMobile;
    }

    public void setDispatcherMobile(String dispatcherMobile) {
        this.dispatcherMobile = dispatcherMobile;
    }

    public String getMeituanDaySeq() {
        return meituanDaySeq;
    }

    public void setMeituanDaySeq(String meituanDaySeq) {
        this.meituanDaySeq = meituanDaySeq;
    }


    /**
     * 订单详情列表
     */
    List<DietOrderDetailShowVo> dietOrderDetailShowVoList;

    public List<DietOrderDetailShowVo> getDietOrderDetailShowVoList() {
        return dietOrderDetailShowVoList;
    }

    public void setDietOrderDetailShowVoList(List<DietOrderDetailShowVo> dietOrderDetailShowVoList) {
        this.dietOrderDetailShowVoList = dietOrderDetailShowVoList;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(Integer orderMode) {
        this.orderMode = orderMode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getEatStatus() {
        return eatStatus;
    }

    public void setEatStatus(Integer eatStatus) {
        this.eatStatus = eatStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Integer getUseScore() {
        return useScore;
    }

    public void setUseScore(Integer useScore) {
        this.useScore = useScore;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public boolean getIsUsePrivilege() {
        return isUsePrivilege;
    }

    public void setIsUsePrivilege(boolean isUsePrivilege) {
        this.isUsePrivilege = isUsePrivilege;
    }

    public Date getPayAt() {
        return payAt;
    }

    public void setPayAt(Date payAt) {
        this.payAt = payAt;
    }

    public Integer getCreateScore() {
        return createScore;
    }

    public void setCreateScore(Integer createScore) {
        this.createScore = createScore;
    }

    public Integer getEatPeople() {
        return eatPeople;
    }

    public void setEatPeople(Integer eatPeople) {
        this.eatPeople = eatPeople;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean getIsFreeOfCharge() {
        return isFreeOfCharge;
    }

    public void setIsFreeOfCharge(boolean isFreeOfCharge) {
        this.isFreeOfCharge = isFreeOfCharge;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public Date getTableOpenAt() {
        return tableOpenAt;
    }

    public void setTableOpenAt(Date tableOpenAt) {
        this.tableOpenAt = tableOpenAt;
    }

    public boolean getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public BigInteger getVipAddressId() {
        return vipAddressId;
    }

    public void setVipAddressId(BigInteger vipAddressId) {
        this.vipAddressId = vipAddressId;
    }

    public String getVipAddressName() {
        return vipAddressName;
    }

    public void setVipAddressName(String vipAddressName) {
        this.vipAddressName = vipAddressName;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPackageFee() {
        return packageFee;
    }

    public void setPackageFee(BigDecimal packageFee) {
        this.packageFee = packageFee;
    }

    public Integer getOrderResource() {
        return orderResource;
    }

    public void setOrderResource(Integer orderResource) {
        this.orderResource = orderResource;
    }

    public Integer getConfirmOrderSource() {
        return confirmOrderSource;
    }

    public void setConfirmOrderSource(Integer confirmOrderSource) {
        this.confirmOrderSource = confirmOrderSource;
    }
}
