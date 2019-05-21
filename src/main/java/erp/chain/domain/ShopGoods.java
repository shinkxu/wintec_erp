package erp.chain.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by lip on 2018/10/26.
 */
public class ShopGoods extends BaseDomain {

    BigInteger id;

    /**
     * 创建菜品的商户id
     */
    BigInteger tenantId;

    /**
     * 创建菜品的机构id
     */
    BigInteger branchId;

    /**
     * 商品名称
     */
    String goodsName;

    /**
     * 兑换开始时间
     * */
    Date startDate;

    /**
     * 兑换结束时间
     * */
    Date endDate;

    /**
     * 零售价
     */
    BigDecimal salePrice;

    /**
     * 兑换规则
     * 1-使用统一规则
     * 2-自定义积分规则
     * */
    Integer changeType;

    /**
     * 可兑换数量
     * */
    BigDecimal changeQuantity;

    Integer isLimit;

    /**
     * 限购数量
     * */
    BigDecimal limitQuantity;

    /**
     * 是否为券
     * */
    Integer isTicket;

    BigInteger ticketId;

    /**
     * 商品状态
       0-正常
       1-停售
     */
    Integer goodsStatus;

    /**
     * 生效前多长时间显示
     * */
    Integer effectDate;

    /**
     * 显示时间类型
     * 1-天
     * 2-小时
     * 3-分钟
     * */
    Integer dateType;

    /**
     * 图片路径
     */
    String photo;

    /**
     * 兑换说明
     */
    String memo;

    /**
     * 关联商品表ID
     * */
    BigInteger goodsId;


    String createBy;
    Date createAt;
    String lastUpdateBy;
    Date lastUpdateAt;
    boolean isDeleted;

    BigDecimal useScore;
    BigDecimal useMoney;
    BigDecimal alreadyChangeQuantity;
    BigDecimal surplusQuantity;

    String ticketName;

    Integer cardType;

    public void setUseScore(BigDecimal useScore) {
        this.useScore = useScore;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public void setAlreadyChangeQuantity(BigDecimal alreadyChangeQuantity) {
        this.alreadyChangeQuantity = alreadyChangeQuantity;
    }

    public void setSurplusQuantity(BigDecimal surplusQuantity) {
        this.surplusQuantity = surplusQuantity;
    }

    public BigDecimal getUseScore() {

        return useScore;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public BigDecimal getAlreadyChangeQuantity() {
        return alreadyChangeQuantity;
    }

    public BigDecimal getSurplusQuantity() {
        return surplusQuantity;
    }

    public BigInteger getId() {
        return id;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public BigDecimal getChangeQuantity() {
        return changeQuantity;
    }

    public BigDecimal getLimitQuantity() {
        return limitQuantity;
    }

    public Integer getIsTicket() {
        return isTicket;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public Integer getEffectDate() {
        return effectDate;
    }

    public Integer getDateType() {
        return dateType;
    }

    public String getPhoto() {
        return photo;
    }

    public String getMemo() {
        return memo;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public Date getLastUpdateAt() {
        return lastUpdateAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public void setChangeQuantity(BigDecimal changeQuantity) {
        this.changeQuantity = changeQuantity;
    }

    public void setLimitQuantity(BigDecimal limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    public void setIsTicket(Integer isTicket) {
        this.isTicket = isTicket;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public void setEffectDate(Integer effectDate) {
        this.effectDate = effectDate;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setLastUpdateAt(Date lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigInteger getTicketId() {
        return ticketId;
    }

    public void setTicketId(BigInteger ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Integer isLimit) {
        this.isLimit = isLimit;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }
}
