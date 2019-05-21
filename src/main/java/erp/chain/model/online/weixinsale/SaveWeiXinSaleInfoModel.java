package erp.chain.model.online.weixinsale;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-06-15.
 */
public class SaveWeiXinSaleInfoModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotEmpty
    private List<PlanGroupInfo> planGroupInfos;
    private List<BigInteger> deleteGroupIds;
    private List<BigInteger> deletePlanIds;

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

    public List<PlanGroupInfo> getPlanGroupInfos() {
        return planGroupInfos;
    }

    public void setPlanGroupInfos(List<PlanGroupInfo> planGroupInfos) {
        this.planGroupInfos = planGroupInfos;
    }

    public List<BigInteger> getDeleteGroupIds() {
        return deleteGroupIds;
    }

    public void setDeleteGroupIds(List<BigInteger> deleteGroupIds) {
        this.deleteGroupIds = deleteGroupIds;
    }

    public List<BigInteger> getDeletePlanIds() {
        return deletePlanIds;
    }

    public void setDeletePlanIds(List<BigInteger> deletePlanIds) {
        this.deletePlanIds = deletePlanIds;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (PlanGroupInfo planGroupInfo : planGroupInfos) {
            ApplicationHandler.isTrue(planGroupInfo.validate(), "planGroupInfo");
        }
    }

    public static class PlanGroupInfo extends BasicModel {
        private static final Integer[] LAYOUTS = {1, 2};
        private BigInteger id;
        @NotNull
        private Integer groupIndex;

        @Length(max = 10)
        private String name;

        @NotNull
        private Integer layout;

        private List<PlanInfo> planInfos;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public Integer getGroupIndex() {
            return groupIndex;
        }

        public void setGroupIndex(Integer groupIndex) {
            this.groupIndex = groupIndex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getLayout() {
            return layout;
        }

        public void setLayout(Integer layout) {
            this.layout = layout;
        }

        public List<PlanInfo> getPlanInfos() {
            return planInfos;
        }

        public void setPlanInfos(List<PlanInfo> planInfos) {
            this.planInfos = planInfos;
        }

        @Override
        public boolean validate() {
            boolean isOk = super.validate();
            if (!isOk) {
                return false;
            }

            isOk = ArrayUtils.contains(LAYOUTS, layout);
            if (!isOk) {
                return false;
            }

            if (CollectionUtils.isNotEmpty(planInfos)) {
                for (PlanInfo planInfo : planInfos) {
                    isOk = planInfo.validate();
                    if (!isOk) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class PlanInfo extends BasicModel {
        private static final Integer[] TYPES = {1, 2};

        private BigInteger id;

        @NotNull
        @Length(max = 20)
        private String name;

        @NotNull
        private Integer type;

        private BigInteger ruleId;

        private BigInteger cardTypeId;

        @NotNull
        @Length(max = 255)
        private String planImageUrl;

        @NotNull
        @Length(max = 255)
        private String introduceImageUrl;

        @Length(max = 200)
        private String instructions;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
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

        @Override
        public boolean validate() {
            boolean isOk = super.validate();
            if (!isOk) {
                return false;
            }

            isOk = ArrayUtils.contains(TYPES, type);
            if (!isOk) {
                return false;
            }

            if (type == 1 && ruleId == null) {
                return false;
            }

            if (type == 2 && cardTypeId == null) {
                return false;
            }

            return true;
        }
    }
}
