package erp.chain.domain.online;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-10-29.
 */
public class IntegralMallGoods {
    private BigInteger id;

    /**
     * 创建菜品的商户id
     */
    private BigInteger tenantId;

    /**
     * 创建菜品的机构id
     */
    private BigInteger branchId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 兑换开始时间
     * */
    private Date startDate;

    /**
     * 兑换结束时间
     * */
    private Date endDate;

    /**
     * 零售价
     */
    private BigDecimal salePrice;

    /**
     * 兑换规则
     * 1-使用统一规则
     * 2-自定义积分规则
     * */
    private Integer changeType;

    /**
     * 可兑换数量
     * */
    private BigDecimal changeQuantity;

    private Integer isLimit;

    /**
     * 限购数量
     * */
    private BigDecimal limitQuantity;

    /**
     * 是否为券
     * */
    private Integer isTicket;

    private BigInteger ticketId;

    /**
     * 商品状态
     0-正常
     1-停售
     */
    private Integer goodsStatus;

    /**
     * 生效前多长时间显示
     * */
    private Integer effectDate;

    /**
     * 显示时间类型
     * 1-天
     * 2-小时
     * 3-分钟
     * */
    private Integer dateType;

    /**
     * 图片路径
     */
    private String photo;

    /**
     * 兑换说明
     */
    private String memo;

    /**
     * 关联商品表ID
     * */
    private BigInteger goodsId;
    private BigDecimal useScore;
    private BigDecimal useMoney;


    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;
    private BigDecimal exchangeQuantity;
    private BigDecimal alreadyChangeQuantity;
    private BigDecimal surplusQuantity;

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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public BigDecimal getChangeQuantity() {
        return changeQuantity;
    }

    public void setChangeQuantity(BigDecimal changeQuantity) {
        this.changeQuantity = changeQuantity;
    }

    public Integer getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Integer isLimit) {
        this.isLimit = isLimit;
    }

    public BigDecimal getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(BigDecimal limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    public Integer getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(Integer isTicket) {
        this.isTicket = isTicket;
    }

    public BigInteger getTicketId() {
        return ticketId;
    }

    public void setTicketId(BigInteger ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public Integer getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(Integer effectDate) {
        this.effectDate = effectDate;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
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

    public BigDecimal getUseScore() {
        return useScore;
    }

    public void setUseScore(BigDecimal useScore) {
        this.useScore = useScore;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public BigDecimal getExchangeQuantity() {
        return exchangeQuantity;
    }

    public void setExchangeQuantity(BigDecimal exchangeQuantity) {
        this.exchangeQuantity = exchangeQuantity;
    }

    public BigDecimal getAlreadyChangeQuantity() {
        return alreadyChangeQuantity;
    }

    public void setAlreadyChangeQuantity(BigDecimal alreadyChangeQuantity) {
        this.alreadyChangeQuantity = alreadyChangeQuantity;
    }

    public BigDecimal getSurplusQuantity() {
        return surplusQuantity;
    }

    public void setSurplusQuantity(BigDecimal surplusQuantity) {
        this.surplusQuantity = surplusQuantity;
    }
}
