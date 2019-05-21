package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/27.
 */
public class PromotionPaybackVo {
    BigInteger id;
    BigInteger tenantId;
    Date createAt;
    String createBy;
    String remark;

    String activityName;
    /**
     * ���ж�����Ʒʱ��¼��ϸ
     */
    String detail;

    Integer forCustomerType;
    Integer promotionType;
    /**
     * ��Χ��
     * 0��ȫ����
     * 1�����´�����
     * 2�����ϴ���
     */
    Integer scope;
    String branch;
    /**
     *
     */
    Integer promotionStatus;

    String startDate;

    String endDate;

    String week;

    BigInteger attendTimes;
    /**
     * ָ����Ա����
     */
    BigInteger forVipType;

    /**
     * ����Ľ����
     */
    BigDecimal satisfy;

    /**
     * �ۿ�
     */
    BigDecimal discount;
    /**
     * ֧������
     */
    BigInteger payType;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getForCustomerType() {
        return forCustomerType;
    }

    public void setForCustomerType(Integer forCustomerType) {
        this.forCustomerType = forCustomerType;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(Integer promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public BigInteger getAttendTimes() {
        return attendTimes;
    }

    public void setAttendTimes(BigInteger attendTimes) {
        this.attendTimes = attendTimes;
    }

    public BigInteger getForVipType() {
        return forVipType;
    }

    public void setForVipType(BigInteger forVipType) {
        this.forVipType = forVipType;
    }

    public BigDecimal getSatisfy() {
        return satisfy;
    }

    public void setSatisfy(BigDecimal satisfy) {
        this.satisfy = satisfy;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigInteger getPayType() {
        return payType;
    }

    public void setPayType(BigInteger payType) {
        this.payType = payType;
    }
}
