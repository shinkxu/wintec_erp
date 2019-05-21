package erp.chain.service.online;

import com.saas.common.util.Common;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.CardType;
import erp.chain.domain.o2o.VipStoreHistory;
import erp.chain.domain.o2o.VipStoreRuleDetails;
import erp.chain.domain.online.Plan;
import erp.chain.domain.online.PlanGroup;
import erp.chain.domain.online.WeiXinSaleOrder;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.online.PlanMapper;
import erp.chain.model.online.weixinsale.*;
import erp.chain.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by liuyandong on 2018-06-15.
 */
@Service
public class WeiXinSaleService {
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private PlanMapper planMapper;

    /**
     * 获取微信售卡详情
     *
     * @param obtainWeiXinSaleInfoModel
     * @return
     */
    public ApiRest obtainWeiXinSaleInfo(ObtainWeiXinSaleInfoModel obtainWeiXinSaleInfoModel) {
        BigInteger tenantId = obtainWeiXinSaleInfoModel.getTenantId();
        BigInteger branchId = obtainWeiXinSaleInfoModel.getBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<PlanGroup> planGroups = DatabaseHelper.findAll(PlanGroup.class, searchModel);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(planGroups)) {
            List<BigInteger> planGroupIds = new ArrayList<BigInteger>();
            for (PlanGroup planGroup : planGroups) {
                planGroupIds.add(planGroup.getId());
            }

            List<Plan> plans = planMapper.findAllPlanInfos(tenantId, branchId, planGroupIds);
            List<BigInteger> ruleIds = new ArrayList<BigInteger>();
            List<BigInteger> cardTypeIds = new ArrayList<BigInteger>();
            for (Plan plan : plans) {
                int type = plan.getType();
                if (type == 1) {
                    ruleIds.add(plan.getRuleId());
                } else if (type == 2) {
                    cardTypeIds.add(plan.getCardTypeId());
                }
            }

            Map<BigInteger, VipStoreRuleDetails> vipStoreRuleDetailsMap = new HashMap<BigInteger, VipStoreRuleDetails>();
            if (CollectionUtils.isNotEmpty(ruleIds)) {
                SearchModel vipStoreRuleDetailsSearchModel = new SearchModel(true);
                vipStoreRuleDetailsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, ruleIds);
                List<VipStoreRuleDetails> vipStoreRuleDetails = DatabaseHelper.findAll(VipStoreRuleDetails.class, vipStoreRuleDetailsSearchModel);
                for (VipStoreRuleDetails vipStoreRuleDetail : vipStoreRuleDetails) {
                    vipStoreRuleDetailsMap.put(vipStoreRuleDetail.getId(), vipStoreRuleDetail);
                }
            }

            Map<BigInteger, CardType> cardTypeMap = new HashMap<BigInteger, CardType>();
            if (CollectionUtils.isNotEmpty(cardTypeIds)) {
                SearchModel cardTypeSearchModel = new SearchModel(true);
                cardTypeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, cardTypeIds);
                List<CardType> cardTypes = DatabaseHelper.findAll(CardType.class, cardTypeSearchModel);
                for (CardType cardType : cardTypes) {
                    cardTypeMap.put(cardType.getId(), cardType);
                }
            }

            Map<BigInteger, List<Map<String, Object>>> planMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            for (Plan plan : plans) {
                List<Map<String, Object>> planList = planMap.get(plan.getPlanGroupId());
                if (CollectionUtils.isEmpty(planList)) {
                    planList = new ArrayList<Map<String, Object>>();
                    planMap.put(plan.getPlanGroupId(), planList);
                }
                Map<String, Object> planInfo = ApplicationHandler.toMap(plan);
                int type = plan.getType();
                if (type == 1) {
                    planInfo.put("vipStoreRuleDetail", vipStoreRuleDetailsMap.get(plan.getRuleId()));
                } else if (type == 2) {
                    planInfo.put("cardType", cardTypeMap.get(plan.getCardTypeId()));
                }
                planList.add(planInfo);
            }

            for (PlanGroup planGroup : planGroups) {
                Map<String, Object> planGroupMap = ApplicationHandler.toMap(planGroup);
                planGroupMap.put("planInfos", planMap.get(planGroup.getId()));
                data.add(planGroupMap);
            }
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取微信售卡信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存微信售卡详情
     *
     * @param saveWeiXinSaleInfoModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveWeiXinSaleInfo(SaveWeiXinSaleInfoModel saveWeiXinSaleInfoModel) {
        BigInteger tenantId = saveWeiXinSaleInfoModel.getTenantId();
        BigInteger branchId = saveWeiXinSaleInfoModel.getBranchId();
        List<SaveWeiXinSaleInfoModel.PlanGroupInfo> planGroupInfos = saveWeiXinSaleInfoModel.getPlanGroupInfos();
        List<BigInteger> deleteGroupIds = saveWeiXinSaleInfoModel.getDeleteGroupIds();
        List<BigInteger> deletePlanIds = saveWeiXinSaleInfoModel.getDeletePlanIds();

        if (CollectionUtils.isNotEmpty(deleteGroupIds)) {
            UpdateModel updateModel = new UpdateModel(true);
            updateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            updateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupIds);
            updateModel.setTableName("plan_group");
            updateModel.addContentValue("is_deleted", 1, "#");
            DatabaseHelper.universalUpdate(updateModel);
        }

        if (CollectionUtils.isNotEmpty(deletePlanIds)) {
            UpdateModel updateModel = new UpdateModel(true);
            updateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            updateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deletePlanIds);
            updateModel.setTableName("plan");
            updateModel.addContentValue("is_deleted", 1, "#");
            DatabaseHelper.universalUpdate(updateModel);
        }

        SearchModel planGroupSearchModel = new SearchModel(true);
        planGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        planGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<PlanGroup> planGroups = DatabaseHelper.findAll(PlanGroup.class, planGroupSearchModel);
        Map<BigInteger, PlanGroup> planGroupMap = new HashMap<BigInteger, PlanGroup>();
        for (PlanGroup planGroup : planGroups) {
            planGroupMap.put(planGroup.getId(), planGroup);
        }

        SearchModel planSearchModel = new SearchModel(true);
        planSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        planSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<Plan> plans = DatabaseHelper.findAll(Plan.class, planSearchModel);
        Map<BigInteger, Plan> planMap = new HashMap<BigInteger, Plan>();
        for (Plan plan : plans) {
            planMap.put(plan.getId(), plan);
        }

        Date date = new Date();
        for (SaveWeiXinSaleInfoModel.PlanGroupInfo planGroupInfo : planGroupInfos) {
            BigInteger planGroupId = planGroupInfo.getId();
            PlanGroup planGroup = null;
            if (planGroupId != null) {
                planGroup = planGroupMap.get(planGroupId);
                ValidateUtils.notNull(planGroup, "分组不存在！");
                planGroup.setGroupIndex(planGroupInfo.getGroupIndex());
                planGroup.setName(planGroupInfo.getName());
                planGroup.setLayout(planGroupInfo.getLayout());
                planGroup.setLastUpdateAt(date);

                DatabaseHelper.update(planGroup);
            } else {
                planGroup = new PlanGroup();
                planGroup.setTenantId(tenantId);
                planGroup.setBranchId(branchId);
                planGroup.setGroupIndex(planGroupInfo.getGroupIndex());
                planGroup.setName(planGroupInfo.getName());
                planGroup.setLayout(planGroupInfo.getLayout());
                planGroup.setCreateAt(date);
                planGroup.setLastUpdateAt(date);

                DatabaseHelper.insert(planGroup);
            }

            List<SaveWeiXinSaleInfoModel.PlanInfo> planInfos = planGroupInfo.getPlanInfos();
            for (SaveWeiXinSaleInfoModel.PlanInfo planInfo : planInfos) {
                BigInteger planId = planInfo.getId();

                Plan plan = null;

                if (planId != null) {
                    plan = planMap.get(planId);
                    ValidateUtils.notNull(plan, "方案不存在！");

                    plan.setName(planInfo.getName());
                    plan.setType(planInfo.getType());
                    plan.setRuleId(planInfo.getRuleId());
                    plan.setCardTypeId(planInfo.getCardTypeId());
                    plan.setPlanImageUrl(planInfo.getPlanImageUrl());
                    plan.setIntroduceImageUrl(planInfo.getIntroduceImageUrl());
                    plan.setInstructions(planInfo.getInstructions());
                    plan.setLastUpdateAt(date);

                    DatabaseHelper.update(plan);
                } else {
                    plan = new Plan();
                    plan.setTenantId(tenantId);
                    plan.setBranchId(branchId);
                    plan.setPlanGroupId(planGroup.getId());
                    plan.setName(planInfo.getName());
                    plan.setType(planInfo.getType());
                    plan.setRuleId(planInfo.getRuleId());
                    plan.setCardTypeId(planInfo.getCardTypeId());
                    plan.setPlanImageUrl(planInfo.getPlanImageUrl());
                    plan.setIntroduceImageUrl(planInfo.getIntroduceImageUrl());
                    plan.setInstructions(planInfo.getInstructions());
                    plan.setCreateAt(date);
                    plan.setLastUpdateAt(date);

                    DatabaseHelper.insert(plan);
                }
            }
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存微信售卡信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取方案详情
     *
     * @param obtainPlanDetailModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainPlanDetail(ObtainPlanDetailModel obtainPlanDetailModel) {
        BigInteger tenantId = obtainPlanDetailModel.getTenantId();
        BigInteger branchId = obtainPlanDetailModel.getBranchId();
        BigInteger planId = obtainPlanDetailModel.getPlanId();
        BigInteger vipId = obtainPlanDetailModel.getVipId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, planId);
        Plan plan = DatabaseHelper.find(Plan.class, searchModel);
        Validate.notNull(plan, "方案不存在！");

        int type = plan.getType();

        Map<String, Object> data = new HashMap<String, Object>();
        VipStoreRuleDetails vipStoreRuleDetails = null;
        CardType cardType = null;
        if (type == 1) {
            vipStoreRuleDetails = DatabaseHelper.find(VipStoreRuleDetails.class, plan.getRuleId());
            if (vipStoreRuleDetails.getEffectTimes() == 1) {
                SearchModel vipStoreHistorySearchModel = new SearchModel(true);
                vipStoreHistorySearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
                vipStoreHistorySearchModel.addSearchCondition("rule_detail_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipStoreRuleDetails.getId());
                List<VipStoreHistory> vipStoreHistories = DatabaseHelper.findAll(VipStoreHistory.class, vipStoreHistorySearchModel);
                if (CollectionUtils.isNotEmpty(vipStoreHistories)) {
                    data.put("isBought", true);
                }
            }
        } else if (type == 2) {
            cardType = DatabaseHelper.find(CardType.class, plan.getCardTypeId());
        }

        data.put("plan", plan);
        data.put("vipStoreRuleDetail", vipStoreRuleDetails);
        data.put("cardType", cardType);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取方案详情成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存订单
     *
     * @param saveOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveOrder(SaveOrderModel saveOrderModel) {
        BigInteger tenantId = saveOrderModel.getTenantId();
        BigInteger branchId = saveOrderModel.getBranchId();
        BigInteger planId = saveOrderModel.getPlanId();
        BigInteger vipId = saveOrderModel.getVipId();

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        SearchModel planSearchModel = new SearchModel(true);
        planSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        planSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        planSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, planId);
        Plan plan = DatabaseHelper.find(Plan.class, planSearchModel);
        Validate.notNull(plan, "方案不存在！");

        int type = plan.getType();

        VipStoreRuleDetails vipStoreRuleDetails = null;
        CardType cardType = null;
        if (type == 1) {
            vipStoreRuleDetails = DatabaseHelper.find(VipStoreRuleDetails.class, plan.getRuleId());
            Validate.notNull(vipStoreRuleDetails, "储值规则不存在！");
        } else if (type == 2) {
            cardType = DatabaseHelper.find(CardType.class, plan.getCardTypeId());
            Validate.notNull(cardType, "次卡方案不存在！");
        }

        WeiXinSaleOrder weiXinSaleOrder = new WeiXinSaleOrder();
        weiXinSaleOrder.setTenantId(tenantId);
        weiXinSaleOrder.setBranchId(branchId);
        weiXinSaleOrder.setVipId(vipId);

        if (type == 1) {
            weiXinSaleOrder.setTotalAmount(vipStoreRuleDetails.getPayLimit());
            weiXinSaleOrder.setOrderType(1);
            weiXinSaleOrder.setRuleId(vipStoreRuleDetails.getId());
        } else if (type == 2) {
            weiXinSaleOrder.setTotalAmount(cardType.getTmPrice());
            weiXinSaleOrder.setOrderType(2);
            weiXinSaleOrder.setCardTypeId(cardType.getId());
        }
        String orderCode = "WSO" + branch.getId() + Common.getToday().substring(2) + serialNumberCreatorMapper.getToday("WSO", 4);
        weiXinSaleOrder.setOrderCode(orderCode);
        weiXinSaleOrder.setPayStatus(0);
        weiXinSaleOrder.setCreateAt(new Date());
        weiXinSaleOrder.setLastUpdateAt(new Date());
        weiXinSaleOrder.setIsDeleted(false);
        DatabaseHelper.insert(weiXinSaleOrder);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(weiXinSaleOrder);
        apiRest.setMessage("保存订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取微信售卡订单
     *
     * @param obtainWeiXinSaleOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainWeiXinSaleOrder(ObtainWeiXinSaleOrderModel obtainWeiXinSaleOrderModel) {
        BigInteger orderId = obtainWeiXinSaleOrderModel.getOrderId();
        String orderCode = obtainWeiXinSaleOrderModel.getOrderCode();

        SearchModel searchModel = new SearchModel(true);
        if (orderId != null) {
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        } else if (StringUtils.isNotBlank(orderCode)) {
            searchModel.addSearchCondition("order_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderCode);
        }

        WeiXinSaleOrder weiXinSaleOrder = DatabaseHelper.find(WeiXinSaleOrder.class, searchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(weiXinSaleOrder);
        apiRest.setMessage("获取订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainBranchInfo(ObtainBranchInfoModel obtainBranchInfoModel) {
        BigInteger tenantId = obtainBranchInfoModel.getTenantId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("is_tinyhall", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
        long count = DatabaseHelper.count(Branch.class, searchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count", count);
        if (count == 1) {
            Branch branch = DatabaseHelper.find(Branch.class, searchModel);
            data.put("branch", branch);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询微餐厅门店数量成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
