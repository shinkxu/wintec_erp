package erp.chain.domain.online;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2018-06-15.
 */
public class Plan {
    /**
     * ID
     */
    private BigInteger id;
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 门店ID
     */
    private BigInteger branchId;
    /**
     * 分组索引
     */
    private BigInteger planGroupId;
    /**
     * 分组名称
     */
    private String name;
    /**
     * 方案类型，1-储值规则，2-次卡方案
     */
    private Integer type;
    /**
     * 储值规则ID
     */
    private BigInteger ruleId;
    /**
     * 次卡方案ID
     */
    private BigInteger cardTypeId;
    /**
     * 方案图片url
     */
    private String planImageUrl;
    /**
     * 介绍图片url
     */
    private String introduceImageUrl;
    /**
     * 使用说明
     */
    private String instructions;
    private String createBy;
    private Date createAt;
    private String lastUpdateBy;
    private Date lastUpdateAt;
    private boolean isDeleted;

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

    public BigInteger getPlanGroupId() {
        return planGroupId;
    }

    public void setPlanGroupId(BigInteger planGroupId) {
        this.planGroupId = planGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigInteger getRuleId() {
        return ruleId;
    }

    public void setRuleId(BigInteger ruleId) {
        this.ruleId = ruleId;
    }

    public BigInteger getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(BigInteger cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getPlanImageUrl() {
        return planImageUrl;
    }

    public void setPlanImageUrl(String planImageUrl) {
        this.planImageUrl = planImageUrl;
    }

    public String getIntroduceImageUrl() {
        return introduceImageUrl;
    }

    public void setIntroduceImageUrl(String introduceImageUrl) {
        this.introduceImageUrl = introduceImageUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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
}
