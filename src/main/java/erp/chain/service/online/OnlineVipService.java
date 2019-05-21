package erp.chain.service.online;

import com.saas.common.ResultJSON;
import com.saas.common.util.Common;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.CardType;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.DietPromotionFestivalVo;
import erp.chain.domain.online.*;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.o2o.CardCouponsMapper;
import erp.chain.mapper.o2o.CardToVipMapper;
import erp.chain.mapper.o2o.VipBookMapper;
import erp.chain.mapper.online.*;
import erp.chain.model.online.onlinevip.*;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.service.o2o.VipStoreHistoryService;
import erp.chain.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.SmsApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2018-04-13.
 */
@Service
public class OnlineVipService {
    @Autowired
    private OnlineVipMapper onlineVipMapper;
    @Autowired
    private OnlineVipAddressMapper onlineVipAddressMapper;
    @Autowired
    private OnlineTenantConfigMapper onlineTenantConfigMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private OnlineVipTypeMapper onlineVipTypeMapper;
    @Autowired
    private OnlineTenantConfigService onlineTenantConfigService;
    @Autowired
    private OnlineVipStoreRuleMapper onlineVipStoreRuleMapper;
    @Autowired
    private OnlineVipStoreRuleDetailMapper onlineVipStoreRuleDetailMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private OnlineWxStoreOrderMapper onlineWxStoreOrderMapper;
    @Autowired
    private VipSpecialPromotionMapper vipSpecialPromotionMapper;
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private OnlineCardCouponMapper onlineCardCouponMapper;
    @Autowired
    private OnlineDietPromotionMapper onlineDietPromotionMapper;
    @Autowired
    private CardCouponsService cardCouponsService;
    @Autowired
    private CardCouponsMapper cardCouponsMapper;
    @Autowired
    private CardToVipMapper cardToVipMapper;

    /**
     * 获取会员信息
     *
     * @param obtainVipInfoModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainVipInfo(ObtainVipInfoModel obtainVipInfoModel) throws IOException {
        String openId = obtainVipInfoModel.getOpenId();
        BigInteger tenantId = obtainVipInfoModel.getTenantId();
        BigInteger branchId = obtainVipInfoModel.getBranchId();
        String scene = obtainVipInfoModel.getScene();

        SearchModel searchModel = new SearchModel(true);
        if (Constants.MINI_PROGRAM.equals(scene)) {
            searchModel.addSearchCondition("mini_program_open_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, openId);
        } else if (Constants.PUBLIC_ACCOUNT.equals(scene)) {
            searchModel.addSearchCondition("original_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, openId);
        }
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        OnlineVip onlineVip = onlineVipMapper.find(searchModel);
        OnlineVipType onlineVipType = null;

        Date date = new Date();
        if (onlineVip == null) {
            OnlineTenantConfig onlineTenantConfig = onlineTenantConfigService.checkTenantConfig(tenantId, SysConfig.SYS_VIP_NUM, "会员");
            Branch branch = DatabaseHelper.find(Branch.class, branchId);
            ValidateUtils.notNull(branch, "门店不存在！");

            SearchModel vipTypeSearchModel = new SearchModel(true);
            vipTypeSearchModel.addSearchCondition("is_online_default", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            vipTypeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            onlineVipType = onlineVipTypeMapper.find(vipTypeSearchModel);
            ValidateUtils.notNull(onlineVipType, "未检索到默认会员类型！");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

            int value = Integer.parseInt(onlineTenantConfig.getValue());
            String nextValue = String.valueOf(value + 1);
            while (nextValue.length() < 5) {
                nextValue = "0" + nextValue;
            }
            String vipCode = simpleDateFormat.format(date) + nextValue;
            onlineVip = new OnlineVip();
            onlineVip.setTenantId(tenantId);
            onlineVip.setBranchId(branch.getId());

            if (Constants.MINI_PROGRAM.equals(scene)) {
                onlineVip.setMiniProgramOpenId(openId);
            } else if (Constants.PUBLIC_ACCOUNT.equals(scene)) {
                onlineVip.setOriginalId(openId);
            }

            onlineVip.setTypeId(onlineVipType.getId());
            onlineVip.setVipCode(vipCode);
            onlineVip.setVipName("游客" + vipCode);
            onlineVip.setPhone("");
            onlineVip.setStatus(0);
            onlineVip.setRegSource("2");

            if (BigInteger.ONE.compareTo(onlineVipType.getToSavePoints()) == 0) {
                SearchModel vipSpecialPromotionSearchModel = new SearchModel(true);
                vipSpecialPromotionSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                vipSpecialPromotionSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                vipSpecialPromotionSearchModel.addSearchCondition("promotion_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
                VipSpecialPromotion vipSpecialPromotion = vipSpecialPromotionMapper.find(vipSpecialPromotionSearchModel);

                if (vipSpecialPromotion != null) {
                    BigDecimal addScore = vipSpecialPromotion.getAddScore();
                    if (addScore != null && addScore.compareTo(BigDecimal.ZERO) != 0) {
                        onlineVip.setRemainingScore(addScore);
                        onlineVip.setSumScore(addScore);

                        VipBook vipBook = new VipBook();

                        //记录积分台帐
                        vipBook.setVipId(onlineVip.getId());
                        vipBook.setTenantId(tenantId);
                        vipBook.setBranchId(branch.getId());
                        vipBook.setBookType(3);
                        vipBook.setOperateBy("新注册会员活动");
                        vipBook.setOperateAt(date);
                        vipBook.setVipCode(vipCode);
                        vipBook.setTotalScore(addScore);
                        vipBook.setVipScore(addScore);
                        vipBook.setCreateAt(date);
                        vipBook.setLastUpdateAt(date);
                        vipBook.setStoreFrom("2");
                        vipBook.setPaymentCode("ZCHY");
                        vipBookMapper.insert(vipBook);
                    }
                }
            }

            onlineVip.setCreateAt(date);
            onlineVip.setLastUpdateAt(date);
            onlineVip.setDeleted(false);
            onlineVipMapper.insert(onlineVip);

            onlineTenantConfig.setValue(String.valueOf(value + 1));
            onlineTenantConfig.setLastUpdateAt(date);
            onlineTenantConfigMapper.update(onlineTenantConfig);
        } else {
            SearchModel onlineVipTypeSearchModel = new SearchModel(true);
            onlineVipTypeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineVip.getTypeId());
            onlineVipType = DatabaseHelper.find(OnlineVipType.class, onlineVipTypeSearchModel);
        }

        SearchModel vipAddressSearchModel = new SearchModel(true);
        vipAddressSearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineVip.getId());
        List<OnlineVipAddress> onlineVipAddresses = onlineVipAddressMapper.findAll(vipAddressSearchModel);

        long couponsCount = onlineVipMapper.countVipCoupons(tenantId, onlineVip.getId());
        onlineVip.setCouponsCount(couponsCount);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vip", onlineVip);
        data.put("vipAddresses", onlineVipAddresses);
        data.put("vipType", onlineVipType);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取会员信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取会员地址
     *
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainVipAddress(ObtainVipAddressModel obtainVipAddressModel) {
        SearchModel vipAddressSearchModel = new SearchModel(true);
        vipAddressSearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipAddressModel.getVipId());
        List<OnlineVipAddress> onlineVipAddresses = onlineVipAddressMapper.findAll(vipAddressSearchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineVipAddresses);
        apiRest.setMessage("获取会员地址成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存会员地址
     *
     * @param saveVipAddressModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipAddress(SaveVipAddressModel saveVipAddressModel) {
        BigInteger vipId = saveVipAddressModel.getVipId();
        BigInteger vipAddressId = saveVipAddressModel.getVipAddressId();

        OnlineVipAddress onlineVipAddress = null;
        boolean isInsert = false;
        if (vipAddressId != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipAddressId);
            searchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
            onlineVipAddress = DatabaseHelper.find(OnlineVipAddress.class, searchModel);
            ValidateUtils.notNull(onlineVipAddress, "会员地址不存在！");
        } else {
            onlineVipAddress = new OnlineVipAddress();
            onlineVipAddress.setVipId(vipId);
            onlineVipAddress.setDefault(false);
            isInsert = true;
        }
        onlineVipAddress.setConsignee(saveVipAddressModel.getConsignee());
        onlineVipAddress.setArea(saveVipAddressModel.getArea());
        onlineVipAddress.setAddress(saveVipAddressModel.getAddress());
        onlineVipAddress.setMobilePhone(saveVipAddressModel.getMobilePhone());
        onlineVipAddress.setAreaName(saveVipAddressModel.getAreaName());

        if (isInsert) {
            DatabaseHelper.insert(onlineVipAddress);
        } else {
            DatabaseHelper.update(onlineVipAddress);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineVipAddress);
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询会员储值规则
     *
     * @param listStoreRulesModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest listStoreRules(ListStoreRulesModel listStoreRulesModel) {
        BigInteger tenantId = listStoreRulesModel.getTenantId();
        BigInteger branchId = listStoreRulesModel.getBranchId();
        BigInteger vipTypeId = listStoreRulesModel.getVipTypeId();

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);

        ApiRest findTenantByIdApiRest = SaaSApi.findTenantById(tenantId);
        ValidateUtils.isTrue(findTenantByIdApiRest.getIsSuccess(), findTenantByIdApiRest.getError());
        Map<String, Object> findTenantByIdApiRestData = (Map<String, Object>) findTenantByIdApiRest.getData();
        Map<String, Object> tenantInfo = (Map<String, Object>) findTenantByIdApiRestData.get("tenant");

        boolean isBranchManagementStore = MapUtils.getBooleanValue(tenantInfo, "isBranchManagementStore");

        SearchModel onlineVipStoreRuleSearchModel = new SearchModel(true);
        onlineVipStoreRuleSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        if (isBranchManagementStore) {
            onlineVipStoreRuleSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        } else {
            onlineVipStoreRuleSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, headquartersBranch.getId());
        }
        OnlineVipStoreRule onlineVipStoreRule = DatabaseHelper.find(OnlineVipStoreRule.class, onlineVipStoreRuleSearchModel);

        if (onlineVipStoreRule == null) {
            onlineVipStoreRule = new OnlineVipStoreRule();
            onlineVipStoreRule.setTenantId(tenantId);
            onlineVipStoreRule.setBranchId(headquartersBranch.getId());
            onlineVipStoreRule.setIsOff(false);
            onlineVipStoreRule.setDeadType("0");
            onlineVipStoreRule.setDeadNum(BigInteger.ZERO);
            onlineVipStoreRule.setDeadUnit("0");
            onlineVipStoreRule.setCreateAt(new Date());
            onlineVipStoreRule.setLastUpdateAt(new Date());
            onlineVipStoreRuleMapper.insert(onlineVipStoreRule);
        }

        SearchModel onlineVipStoreRuleDetailSearchModel = new SearchModel(true);
        onlineVipStoreRuleDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        onlineVipStoreRuleDetailSearchModel.addSearchCondition("rule_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineVipStoreRule.getId());
        onlineVipStoreRuleDetailSearchModel.addSearchCondition("is_for_wechat", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
        if (isBranchManagementStore) {
            onlineVipStoreRuleDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        } else {
            onlineVipStoreRuleDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, headquartersBranch.getId());
        }
        onlineVipStoreRuleDetailSearchModel.setWhereClause("IF(start_time is not NULL, start_time <= NOW(), 1 = 1) AND IF(end_time is not NULL, end_time >= NOW(), 1 = 1) AND (pointed_vip_type IS NULL OR pointed_vip_type = #{vipTypeId})");
        onlineVipStoreRuleDetailSearchModel.addNamedParameter("vipTypeId", vipTypeId);
        List<OnlineVipStoreRuleDetail> onlineVipStoreRuleDetails = DatabaseHelper.findAll(OnlineVipStoreRuleDetail.class, onlineVipStoreRuleDetailSearchModel);

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("vipStoreRule", onlineVipStoreRule);
        dataMap.put("vipStoreRuleDetails", onlineVipStoreRuleDetails);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(dataMap);
        apiRest.setMessage("查询储值规则成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 更新会员积分
     *
     * @param updateMemberBonusModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest updateMemberBonus(UpdateMemberBonusModel updateMemberBonusModel) {
        BigInteger tenantId = updateMemberBonusModel.getTenantId();
        BigInteger vipId = updateMemberBonusModel.getVipId();
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        OnlineVip onlineVip = onlineVipMapper.find(searchModel);
        ValidateUtils.notNull(onlineVip, "会员信息不存在！");

        WeiXinUtils.updateMemberBonusSafe(tenantId.toString(), onlineVip.getUserCardCode(), onlineVip.getCardId(), 500, null, "储值赠送500积分！");

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("更新会员积分成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存储值订单
     *
     * @param saveStoreOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveStoreOrder(SaveStoreOrderModel saveStoreOrderModel) {
        BigInteger tenantId = saveStoreOrderModel.getTenantId();
        BigInteger vipId = saveStoreOrderModel.getVipId();
        BigInteger store = saveStoreOrderModel.getStore();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = onlineVipMapper.find(searchModel);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        String serialNumber = serialNumberCreatorMapper.getToday("czp", 4);
        String today = Common.getToday();
        String storeNo = "czp" + vipId + today + serialNumber;
        OnlineWxStoreOrder onlineWxStoreOrder = new OnlineWxStoreOrder();
        onlineWxStoreOrder.setStoreNo(storeNo);
        onlineWxStoreOrder.setVipId(vipId);
        onlineWxStoreOrder.setTenantId(tenantId);
        onlineWxStoreOrder.setStore(store);
        onlineWxStoreOrder.setPayStatus(0);
        onlineWxStoreOrder.setCreateAt(new Date());
        onlineWxStoreOrder.setLastUpdateAt(new Date());

        onlineWxStoreOrderMapper.insert(onlineWxStoreOrder);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineWxStoreOrder);
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 发起储值支付
     *
     * @param doPayStoreOrderModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest doPayStoreOrder(DoPayStoreOrderModel doPayStoreOrderModel) throws IOException {
        BigInteger tenantId = doPayStoreOrderModel.getTenantId();
        BigInteger storeOrderId = doPayStoreOrderModel.getStoreOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, storeOrderId);
        OnlineWxStoreOrder onlineWxStoreOrder = onlineWxStoreOrderMapper.find(searchModel);
        ValidateUtils.notNull(onlineWxStoreOrder, "储值订单不存在！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        String headquartersBranchId = headquartersBranch.getId().toString();

        Map<String, String> unifiedOrderRequestParameters = new HashMap<String, String>();
        unifiedOrderRequestParameters.put("tenantId", tenantId.toString());
        unifiedOrderRequestParameters.put("branchId", headquartersBranchId);
        unifiedOrderRequestParameters.put("headquartersBranchId", headquartersBranchId);
        unifiedOrderRequestParameters.put("body", "储值支付");
        unifiedOrderRequestParameters.put("outTradeNo", onlineWxStoreOrder.getStoreNo());
        unifiedOrderRequestParameters.put("totalFee", String.valueOf(onlineWxStoreOrder.getStore().multiply(NumberUtils.createBigInteger("100")).intValue()));
        unifiedOrderRequestParameters.put("spbillCreateIp", ApplicationHandler.getRemoteAddress());

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String notifyUrl = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_O2O, partitionCode) + "/onlineVip/weiXinPayCallback";
        unifiedOrderRequestParameters.put("notifyUrl", notifyUrl);
        unifiedOrderRequestParameters.put("tradeType", doPayStoreOrderModel.getTradeType());
        unifiedOrderRequestParameters.put("subOpenId", doPayStoreOrderModel.getSubOpenId());
        ApiRest apiRest = ProxyApi.proxyPost(partitionCode, Constants.SERVICE_NAME_OUT, "weiXinPay", "unifiedOrder", unifiedOrderRequestParameters);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

        return apiRest;
    }

    @Autowired
    private VipStoreHistoryService vipStoreHistoryService;

    /**
     * 处理会员处置回调
     *
     * @param paidType
     * @param parameters
     * @return
     * @throws ParseException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handlePayCallback(int paidType, Map<String, String> parameters) throws ParseException, IOException {
        String orderCode = null;
        if (paidType == 1) {
            orderCode = parameters.get("out_trade_no");
        }
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("store_no", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderCode);
        OnlineWxStoreOrder onlineWxStoreOrder = DatabaseHelper.find(OnlineWxStoreOrder.class, searchModel);
        ValidateUtils.notNull(onlineWxStoreOrder, "订单不存在！");

        if (onlineWxStoreOrder.getPayStatus() == 0) {
            onlineWxStoreOrder.setPayStatus(1);
            onlineWxStoreOrderMapper.update(onlineWxStoreOrder);

            Map<String, String> params = new HashMap<String, String>();
            params.put("vipId", onlineWxStoreOrder.getVipId().toString());
            params.put("amount", onlineWxStoreOrder.getStore().toString());
            params.put("createBy", "微餐厅储值");
            params.put("token", "WXCZ" + onlineWxStoreOrder.getId());
            params.put("storeFrom", "1");
            params.put("tenantId", onlineWxStoreOrder.getTenantId().toString());
            ResultJSON resultJSON = vipStoreHistoryService.vipStoreForPosV2(params);
            ValidateUtils.isTrue("0".equals(resultJSON.getSuccess()), resultJSON.getMsg());
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("支付回调处理成功！");
        return apiRest;
    }

    /**
     * 分页查询储值规则
     *
     * @param listVipStoreRuleDetailsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listVipStoreRuleDetails(ListVipStoreRuleDetailsModel listVipStoreRuleDetailsModel) {
        BigInteger tenantId = listVipStoreRuleDetailsModel.getTenantId();
        BigInteger branchId = listVipStoreRuleDetailsModel.getBranchId();
        int page = listVipStoreRuleDetailsModel.getPage();
        int rows = listVipStoreRuleDetailsModel.getRows();

        ApiRest findTenantByIdApiRest = SaaSApi.findTenantById(tenantId);
        ValidateUtils.isTrue(findTenantByIdApiRest.getIsSuccess(), findTenantByIdApiRest.getError());
        Map<String, Object> findTenantByIdApiRestData = (Map<String, Object>) findTenantByIdApiRest.getData();
        Map<String, Object> tenantInfo = (Map<String, Object>) findTenantByIdApiRestData.get("tenant");

        boolean isBranchManagementStore = MapUtils.getBooleanValue(tenantInfo, "isBranchManagementStore");

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId));
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));
        searchConditions.add(new SearchCondition("is_for_wechat", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1));
        if (isBranchManagementStore) {
            searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId));
        } else {
            Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
            searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, headquartersBranch.getId()));
        }

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        searchModel.setWhereClause("IF(vip_store_rule_details.start_time is not NULL, vip_store_rule_details.start_time <= NOW(), 1 = 1) AND IF(vip_store_rule_details.end_time is not NULL, vip_store_rule_details.end_time >= NOW(), 1 = 1)");
        long count = DatabaseHelper.count(VipStoreRuleDetails.class, searchModel);

        List<VipStoreRuleDetails> vipStoreRuleDetails = new ArrayList<VipStoreRuleDetails>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setWhereClause("IF(vip_store_rule_details.start_time is not NULL, vip_store_rule_details.start_time <= NOW(), 1 = 1) AND IF(vip_store_rule_details.end_time is not NULL, vip_store_rule_details.end_time >= NOW(), 1 = 1)");
            vipStoreRuleDetails = DatabaseHelper.findAllPaged(VipStoreRuleDetails.class, pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", vipStoreRuleDetails);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询储值规则成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 分页查询次卡方案
     *
     * @param listCardTypesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listCardTypes(ListCardTypesModel listCardTypesModel) {
        BigInteger tenantId = listCardTypesModel.getTenantId();
        BigInteger branchId = listCardTypesModel.getBranchId();
        int page = listCardTypesModel.getPage();
        int rows = listCardTypesModel.getRows();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId));
        searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId));
        searchConditions.add(new SearchCondition("card_kind", Constants.SQL_OPERATION_SYMBOL_EQUALS, 3));
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        searchModel.setWhereClause("IF(card_type.tm_interval_type = 3 AND card_type.start_time IS NOT NULL, card_type.start_time <= NOW(), 1 = 1) AND IF(card_type.tm_interval_type = 3 AND card_type.end_time IS NOT NULL, card_type.end_time >= NOW(), 1 = 1)");
        long count = DatabaseHelper.count(CardType.class, searchModel);

        List<CardType> cardTypes = new ArrayList<CardType>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setWhereClause("IF(card_type.tm_interval_type = 3 AND card_type.start_time IS NOT NULL, card_type.start_time <= NOW(), 1 = 1) AND IF(card_type.tm_interval_type = 3 AND card_type.end_time IS NOT NULL, card_type.end_time >= NOW(), 1 = 1)");
            cardTypes = DatabaseHelper.findAllPaged(CardType.class, pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", cardTypes);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询次卡方案成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询会员卡券
     *
     * @param listCardsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listCards(ListCardsModel listCardsModel) {
        BigInteger tenantId = listCardsModel.getTenantId();
        BigInteger vipId = listCardsModel.getVipId();
        Integer cardType = listCardsModel.getCardType();
        BigDecimal limitValue = listCardsModel.getLimitValue();

        List<OnlineCardCoupon> onlineCardCoupons = onlineCardCouponMapper.listCards(tenantId, vipId, cardType, limitValue);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineCardCoupons);
        apiRest.setMessage("查询会员优惠券成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public String listFestivals(ListFestivalsModel listFestivalsModel) throws ParseException {
        BigInteger tenantId = listFestivalsModel.getTenantId();
        BigInteger branchId = listFestivalsModel.getBranchId();
        BigInteger vipId = listFestivalsModel.getVipId();
        BigDecimal receivedAmountM = listFestivalsModel.getReceivedAmount();
        Date payAt = listFestivalsModel.getPayAt();

        /*List<Map<String, Object>> festivals = onlineDietPromotionMapper.listFestivals(tenantId, branchId);

        List<BigInteger> dietPromotionIds = new ArrayList<BigInteger>();
        List<BigInteger> festivalIds = new ArrayList<BigInteger>();
        for (Map<String, Object> festival : festivals) {
            dietPromotionIds.add(BigInteger.valueOf(MapUtils.getLongValue(festival, "dietPromotionId")));
            festivalIds.add(BigInteger.valueOf(MapUtils.getLongValue(festival, "id")));
        }


        List<Map<String, Object>> effectiveBranches = onlineDietPromotionMapper.statisticsEffectiveBranches(dietPromotionIds);
        Map<BigInteger, String> effectiveBranchNames = new HashMap<BigInteger, String>();
        for (Map<String, Object> effectiveBranch : effectiveBranches) {
            effectiveBranchNames.put(BigInteger.valueOf(MapUtils.getLongValue(effectiveBranch, "dietPromotionId")), MapUtils.getString(effectiveBranch, "branchNames"));
        }

        List<Map<String, Object>> couponsCount = onlineCardCouponMapper.statisticsCouponsCount(vipId, festivalIds);
        Map<BigInteger, Integer> countMap = new HashMap<BigInteger, Integer>();
        for (Map<String, Object> map : couponsCount) {
            countMap.put(BigInteger.valueOf(MapUtils.getLongValue(map, "festivalId")), MapUtils.getIntValue(map, "count"));
        }

        for (Map<String, Object> festival : festivals) {
            BigInteger dietPromotionId = BigInteger.valueOf(MapUtils.getLongValue(festival, "dietPromotionId"));
            BigInteger festivalId = BigInteger.valueOf(MapUtils.getLongValue(festival, "id"));
            festival.put("branchNames", effectiveBranchNames.get(dietPromotionId));
            Integer remainCardCount = countMap.get(festivalId);
            festival.put("remainCardCount", remainCardCount == null ? 0 : remainCardCount);
        }*/

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DietPromotionFestivalVo> fitOnes = new ArrayList<>();


        Date nowDate = new Date();
        BigDecimal receivedAmount = BigDecimal.ZERO;
        if (payAt != null) {
            nowDate = payAt;
        }
        if (receivedAmountM != null) {
            receivedAmount = receivedAmountM;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", tenantId);
        map.put("status", "0");
        map.put("manualGet", "1");
        //map.put("openSendRule","0");
        map.put("getDate", format3.format(nowDate));
        map.put("receivedAmount", receivedAmount);
        List<CardCoupons> cardCouponsList = cardCouponsMapper.cardCouponsList(map);
        for (CardCoupons cardCoupons : cardCouponsList) {
            DietPromotionFestivalVo dietPromotionFestivalVo = new DietPromotionFestivalVo();
            Map<String, Object> map3 = new HashMap<>();
            map3.put("tenantId", tenantId);
            map3.put("vipId", vipId);
            map3.put("cardId", cardCoupons.getId());
            map3.put("useStatus", 0);
            List gotCards = cardToVipMapper.listCardToVip(map3);
            if (gotCards != null) {//已领取未使用，领用页面显示已领用
                dietPromotionFestivalVo.setRemainCardCount(gotCards.size());
            } else {//没有未用的，需要判断是否达到领取上限，达到了，页面不显示，未达到显示
                Map<String, Object> map1 = new HashMap<>();
                map1.put("cardId", cardCoupons.getId());
                map1.put("vipId", vipId);
                //单日领取限制
                if (cardCoupons.getDayGetLimit() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    map1.put("sendDate", sdf.format(new Date()));
                    //单日领取数
                    List<CardToVip> dayCheck = cardToVipMapper.listCardToVip(map1);
                    if (dayCheck != null && BigDecimal.valueOf(Double.valueOf(dayCheck.size())).compareTo(cardCoupons.getDayGetLimit()) >= 0) {
                        LogUtil.logInfo("listFestivals--发券时，会员已有券数量超过此券的单日领取数，vipId=" + vipId + "，cardId=" + cardCoupons.getId());
                        continue;
                    }
                }
                //累计领取数
                if (cardCoupons.getTotalGetLimit() != null) {
                    map1.remove("sendDate");
                    List<CardToVip> totalCheck = cardToVipMapper.listCardToVip(map1);
                    if (totalCheck != null && BigDecimal.valueOf(Double.valueOf(totalCheck.size())).compareTo(cardCoupons.getTotalGetLimit()) >= 0) {
                        LogUtil.logInfo("listFestivals--发券时，会员已有券数量超过此券的累计领取数，vipId=" + vipId + "，cardId=" + cardCoupons.getId());
                        continue;
                    }
                }
            }
            dietPromotionFestivalVo.setBranchNames(cardCoupons.getLimitBranchNames());
            //dietPromotionFestivalVo.setDietPromotionFestivalId(festival.getId());
            //dietPromotionFestivalVo.setDietPromotionId(festival.getDietPromotionId());
            dietPromotionFestivalVo.setTenanatId(cardCoupons.getTenantId());
            dietPromotionFestivalVo.setActivityName(cardCoupons.getCardName());
            dietPromotionFestivalVo.setCardId(cardCoupons.getId());
            //dietPromotionFestivalVo.setLimitPerOne(festival.getLimitPerOne());
            //dietPromotionFestivalVo.setSendLimitValue(festival.getSendLimitValue());
            if (cardCoupons.getSendAllAmount() == null) {
                dietPromotionFestivalVo.setTotalInventory(BigInteger.valueOf(-1));
                dietPromotionFestivalVo.setRemainInventory(BigInteger.valueOf(-1));
            } else {
                dietPromotionFestivalVo.setTotalInventory(cardCoupons.getSendAllAmount().toBigInteger());
                BigDecimal remain = cardCoupons.getSendAllAmount().subtract(cardCoupons.getSendedAmount() == null ? BigDecimal.ZERO : cardCoupons.getSendedAmount());
                dietPromotionFestivalVo.setRemainInventory(remain.toBigInteger());
            }

            dietPromotionFestivalVo.setCreateBranchId(cardCoupons.getBranchId());
            //dietPromotionFestivalVo.setPromotionCode(dietPromotion.getPromotionCode());
            //dietPromotionFestivalVo.setPromotionName(dietPromotion.getPromotionName());
            //dietPromotionFestivalVo.setPromotionType(dietPromotion.getPromotionType());
            if (cardCoupons.getStartTime() != null && cardCoupons.getEndTime() != null) {
                Date startDate = format1.parse(format1.format(cardCoupons.getStartTime()));
                Date endDate = format1.parse(format1.format(cardCoupons.getEndTime()));
                Date startTime = format2.parse(format2.format(cardCoupons.getStartTime()));
                Date endTime = format2.parse(format2.format(cardCoupons.getEndTime()));
                dietPromotionFestivalVo.setStartDate(startDate);
                dietPromotionFestivalVo.setEndDate(endDate);
                dietPromotionFestivalVo.setStartTime(startTime);
                dietPromotionFestivalVo.setEndTime(endTime);
            } else {
                dietPromotionFestivalVo.setStartDate(null);
                dietPromotionFestivalVo.setEndDate(null);
                dietPromotionFestivalVo.setStartTime(null);
                dietPromotionFestivalVo.setEndTime(null);
            }
            dietPromotionFestivalVo.setApplyToMon(cardCoupons.getWeekday1());
            dietPromotionFestivalVo.setApplyToTue(cardCoupons.getWeekday2());
            dietPromotionFestivalVo.setApplyToWed(cardCoupons.getWeekday3());
            dietPromotionFestivalVo.setApplyToThu(cardCoupons.getWeekday4());
            dietPromotionFestivalVo.setApplyToFri(cardCoupons.getWeekday5());
            dietPromotionFestivalVo.setApplyToSat(cardCoupons.getWeekday6());
            dietPromotionFestivalVo.setApplyToSun(cardCoupons.getWeekday7());
            dietPromotionFestivalVo.setScope(1);
            dietPromotionFestivalVo.setForCustomerType(1);
            //dietPromotionFestivalVo.setMemGradeId(dietPromotion.getMemGradeId());
            //dietPromotionFestivalVo.setMemo(dietPromotion.getMemo());
            //dietPromotionFestivalVo.setPromotionStatus(dietPromotion.getPromotionStatus());
            //dietPromotionFestivalVo.setIsUse(dietPromotion.getIsUse());
            //dietPromotionFestivalVo.setIsSuperposition(dietPromotion.getIsSuperposition());

            dietPromotionFestivalVo.setCardName(cardCoupons.getCardName());
            dietPromotionFestivalVo.setCardType(cardCoupons.getCardType());
            dietPromotionFestivalVo.setLimitValue(cardCoupons.getLimitValue());
            dietPromotionFestivalVo.setFaceValue(cardCoupons.getFaceValue());
            dietPromotionFestivalVo.setPeriodOfValidity(cardCoupons.getPeriodOfValidity());
            dietPromotionFestivalVo.setColorValue(cardCoupons.getColorValue());
            dietPromotionFestivalVo.setRemark(cardCoupons.getRemark());
            dietPromotionFestivalVo.setOpenSendRule(cardCoupons.isOpenSendRule());
            dietPromotionFestivalVo.setOpenUseRule(cardCoupons.isOpenUseRule());
            dietPromotionFestivalVo.setDiscount(cardCoupons.getDiscount());
            fitOnes.add(dietPromotionFestivalVo);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("clazz", null);
        result.put("code", "0");
        result.put("data", fitOnes);
        result.put("error", null);
        result.put("isSuccess", true);
        result.put("message", "查询卡券成功！");
        result.put("result", Constants.REST_RESULT_SUCCESS);
        result.put("url", null);
        result.put("version", null);
        return GsonUtils.toJson(result, "yyyy-MM-dd");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest receiveCoupon(ReceiveCouponModel receiveCouponModel) throws ParseException {
        BigInteger tenantId = receiveCouponModel.getTenantId();
        BigInteger vipId = receiveCouponModel.getVipId();
        BigInteger festivalId = receiveCouponModel.getFestivalId();
        BigInteger cardId = receiveCouponModel.getCardId();
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        ValidateUtils.notNull(onlineVip, "会员不存在！");
        Map<String, String> map3 = new HashMap<>();
        map3.put("tenantId", tenantId.toString());
        map3.put("cardId", cardId.toString());
        map3.put("vipId", onlineVip.getId().toString());
        map3.put("channel", "xcx");
        map3.put("branchId", onlineVip.getBranchId().toString());
        cardCouponsService.sendCardToVip(map3);
        /*OnlineDietPromotionFestival onlineDietPromotionFestival = DatabaseHelper.find(OnlineDietPromotionFestival.class, festivalId);
        ValidateUtils.notNull(onlineDietPromotionFestival, "卡券活动不存在！");

        ValidateUtils.isTrue(onlineDietPromotionFestival.getRemainInventory().compareTo(BigInteger.ZERO) > 0, "该卡券已被抢光！");

        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("festival_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, festivalId);
        searchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        searchModel.addSearchCondition("use_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);
        List<OnlineCardToVip> onlineCardToVips = DatabaseHelper.findAll(OnlineCardToVip.class, searchModel);
        ValidateUtils.isTrue(onlineCardToVips.isEmpty(), "已参加过该活动！");

        BigInteger cardId = onlineDietPromotionFestival.getCardId();
        OnlineCardCoupon onlineCardCoupon = DatabaseHelper.find(OnlineCardCoupon.class, cardId);
        ValidateUtils.notNull(onlineCardCoupon, "卡券不存在！");

        BigInteger dietPromotionId = onlineDietPromotionFestival.getDietPromotionId();
        OnlineDietPromotion onlineDietPromotion = DatabaseHelper.find(OnlineDietPromotion.class, dietPromotionId);
        ValidateUtils.notNull(onlineDietPromotion, "促销活动不存在！");

        String week = StringUtils.join(new Integer[]{onlineDietPromotion.getApplyToMon(), onlineDietPromotion.getApplyToTue(), onlineDietPromotion.getApplyToWed(), onlineDietPromotion.getApplyToThu(), onlineDietPromotion.getApplyToFri(), onlineDietPromotion.getApplyToSat(), onlineDietPromotion.getApplyToSun()}, ",");

        OnlineCardToVip onlineCardToVip = new OnlineCardToVip();
        onlineCardToVip.setTenantId(tenantId);
        onlineCardToVip.setFestivalId(festivalId);
        onlineCardToVip.setVipId(vipId);
        onlineCardToVip.setCardCouponsId(cardId);
        if (onlineCardCoupon.getPeriodOfValidity() != null) {
            onlineCardToVip.setPeriodOfValidity(onlineCardCoupon.getPeriodOfValidity());
        } else {
            onlineCardToVip.setPeriodOfValidity(BigInteger.ZERO);
        }
        onlineCardToVip.setCardName(onlineCardCoupon.getCardName());
        onlineCardToVip.setCardType(onlineCardCoupon.getCardType());
        onlineCardToVip.setUseStatus("0");
        if (onlineCardCoupon.getLimitValue() != null) {
            onlineCardToVip.setLimitValue(onlineCardCoupon.getLimitValue());
        } else {
            onlineCardToVip.setLimitValue(BigDecimal.ZERO);
        }
        if (onlineCardCoupon.getDiscount() != null) {
            onlineCardToVip.setDiscount(onlineCardCoupon.getDiscount());
        }
        onlineCardToVip.setFaceValue(onlineCardCoupon.getFaceValue());
        onlineCardToVip.setWeek(week);
        //换算时间
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startStr = format1.format(onlineDietPromotion.getStartDate()) + format2.format(onlineDietPromotion.getStartTime());
        String endStr = format1.format(onlineDietPromotion.getEndDate()) + format2.format(onlineDietPromotion.getEndTime());

        onlineCardToVip.setStartTime(format3.parse(startStr));
        onlineCardToVip.setEndTime(format3.parse(endStr));
        if (onlineCardCoupon.getColorValue() != null) {
            onlineCardToVip.setColorValue(onlineCardCoupon.getColorValue());
        } else {
            onlineCardToVip.setColorValue("0");
        }

        //生成卡券编码
        long mills = System.currentTimeMillis();
        String code = "" + tenantId + festivalId + mills;
        onlineCardToVip.setCardCode(code);
        onlineCardToVip.setCreateAt(new Date());
        onlineCardToVip.setLastUpdateAt(new Date());
        DatabaseHelper.insert(onlineCardToVip);
        //修改库存
        onlineDietPromotionFestival.setRemainInventory(onlineDietPromotionFestival.getRemainInventory().subtract(BigInteger.ONE));
        DatabaseHelper.update(onlineDietPromotionFestival);*/

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("领取卡券成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 发送验证码
     *
     * @param sendAuthCodeModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest sendAuthCode(SendAuthCodeModel sendAuthCodeModel) {
        BigInteger tenantId = sendAuthCodeModel.getTenantId();
        BigInteger branchId = sendAuthCodeModel.getBranchId();
        String phone = sendAuthCodeModel.getPhone();
        String sessionId = sendAuthCodeModel.getSessionId();

        OnlineTenantConfig ctUseTenantSmsTenantConfig = TenantConfigUtils.obtainTenantConfig(tenantId, "ct_use_tenant_sms");
        if (ctUseTenantSmsTenantConfig != null && "1".equals(ctUseTenantSmsTenantConfig.getValue())) {
            OnlineTenantConfig useBranchVipMessageTenantConfig = TenantConfigUtils.obtainTenantConfig(tenantId, "usebranchvipmessage");
            if ("1".equals(useBranchVipMessageTenantConfig.getValue())) {
                UpdateModel updateModel = new UpdateModel(true);
                updateModel.setTableName("branch");
                updateModel.addContentValue("sms_count", "sms_count - 1", "$");
                updateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                long influenceRowNumber = DatabaseHelper.universalUpdate(updateModel);
                Validate.isTrue(influenceRowNumber == 1, "门店不存在！");
            } else {
                Map<String, String> deductSmsCountRequestParameters = new HashMap<String, String>();
                deductSmsCountRequestParameters.put("tenantId", tenantId.toString());
                deductSmsCountRequestParameters.put("deductCount", "1");
                ApiRest deductSmsCountResult = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "tenant", "deductSmsCount", deductSmsCountRequestParameters);
                Validate.isTrue(deductSmsCountResult.getIsSuccess(), deductSmsCountResult.getError());
            }
        }
        ApiRest smsSendAuthCodeResult = SmsApi.SmsSendAuthCode(phone, sessionId);
        Validate.isTrue(smsSendAuthCodeResult.getIsSuccess(), smsSendAuthCodeResult.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("发送验证码成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private static final Map<String, String> TABLE_NAME_AND_COLUMN_NAME = new HashMap<String, String>();

    static {
        TABLE_NAME_AND_COLUMN_NAME.put("card_account", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("card_coupons_book", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("card_to_vip", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("diet_game_scratch_card_history", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("diet_game_scratch_vip", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("diet_order_info", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("guide_sale_detail", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("pos_store_order", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("precision_marketing_order_vip_detail", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("sale", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("sale_detail", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("sale_payment", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("vip_address", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("vip_book", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("vip_exchange_history", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("vip_flash_sale_history", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("vip_statement", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("vip_store_history", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("vip_trade_history", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("wei_xin_sale_order", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("wx_pos_code", "vip_id");

        TABLE_NAME_AND_COLUMN_NAME.put("wx_store_order", "vip_id");
        TABLE_NAME_AND_COLUMN_NAME.put("card", "holder_id");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest verifiedVip(VerifiedVipModel verifiedVipModel) {
        BigInteger tenantId = verifiedVipModel.getTenantId();
        BigInteger vipId = verifiedVipModel.getVipId();
        String phone = verifiedVipModel.getPhone();

        SearchModel onlineVipSearchModel = new SearchModel(true);
        onlineVipSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        onlineVipSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, onlineVipSearchModel);
        ValidateUtils.notNull(onlineVip, "会员信息不存在！");

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineVip.getBranchId());
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        List<OnlineVip> onlineVips = null;
        String groupCode = branch.getGroupCode();
        if (StringUtils.isBlank(groupCode)) {
            SearchModel listSearchModel = new SearchModel(true);
            listSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            listSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branch.getId());
            listSearchModel.setWhereClause("(phone = #{phone} OR id = #{vipId})");
            listSearchModel.addNamedParameter("phone", phone);
            listSearchModel.addNamedParameter("vipId", vipId);
            listSearchModel.setOrderBy("create_at");
            onlineVips = DatabaseHelper.findAll(OnlineVip.class, listSearchModel);
        } else {
            onlineVips = onlineVipMapper.findAllSameVips(tenantId, groupCode, phone, vipId);
        }

        OnlineVip reservedVip = onlineVips.get(0);

        int buyTimes = 0;
        BigDecimal sumConsume = BigDecimal.ZERO;
        BigDecimal sumFeed = BigDecimal.ZERO;
        BigDecimal sumScore = BigDecimal.ZERO;
        BigDecimal remainingScore = BigDecimal.ZERO;
        BigDecimal largessscore = BigDecimal.ZERO;
        BigDecimal overscore = BigDecimal.ZERO;
        BigDecimal vipStoreTotal = BigDecimal.ZERO;
        BigDecimal vipStore = BigDecimal.ZERO;
        BigDecimal vipDeposit = BigDecimal.ZERO;

        List<BigInteger> vipIds = new ArrayList<BigInteger>();
        Date now = new Date();
        int size = onlineVips.size();
        String publicAccountOpenId = null;
        String miniOpenId = null;
        for (int index = 0; index < size; index++) {
            OnlineVip vip = onlineVips.get(index);
            buyTimes = buyTimes + (vip.getBuyTimes() == null ? 0 : vip.getBuyTimes());
            sumConsume = sumConsume.add(vip.getSumConsume() == null ? BigDecimal.ZERO : vip.getSumConsume());
            sumFeed = sumFeed.add(vip.getSumFeed() == null ? BigDecimal.ZERO : vip.getSumFeed());
            sumScore = sumScore.add(vip.getSumScore() == null ? BigDecimal.ZERO : vip.getSumScore());
            remainingScore = remainingScore.add(vip.getRemainingScore() == null ? BigDecimal.ZERO : vip.getRemainingScore());
            largessscore = largessscore.add(vip.getLargessscore() == null ? BigDecimal.ZERO : vip.getLargessscore());
            overscore = overscore.add(vip.getOverscore() == null ? BigDecimal.ZERO : vip.getOverscore());
            vipStoreTotal = vipStoreTotal.add(vip.getVipStoreTotal() == null ? BigDecimal.ZERO : vip.getVipStoreTotal());
            vipStore = vipStore.add(vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore());
            vipDeposit = vipDeposit.add(vip.getVipDeposit() == null ? BigDecimal.ZERO : vip.getVipDeposit());

            String originalId = vip.getOriginalId();
            String miniProgramOpenId = vip.getMiniProgramOpenId();
            if (StringUtils.isNotBlank(originalId)) {
                publicAccountOpenId = originalId;
            }

            if (StringUtils.isNotBlank(miniProgramOpenId)) {
                miniOpenId = miniProgramOpenId;
            }

            if (index != 0) {
                vipIds.add(vip.getId());
                vip.setDeleted(true);
                vip.setLastUpdateAt(now);
                DatabaseHelper.update(vip);
            }
        }

        if (CollectionUtils.isNotEmpty(vipIds)) {
            for (Map.Entry<String, String> entry : TABLE_NAME_AND_COLUMN_NAME.entrySet()) {
                String tableName = entry.getKey();
                String columnName = entry.getValue();

                UpdateModel updateModel = new UpdateModel(true);
                updateModel.setTableName(tableName);
                updateModel.addContentValue(columnName, reservedVip.getId(), "#");
                updateModel.addSearchCondition(columnName, Constants.SQL_OPERATION_SYMBOL_IN, vipIds);
                DatabaseHelper.universalUpdate(updateModel);
            }
        }

        reservedVip.setBuyTimes(buyTimes);
        reservedVip.setSumConsume(sumConsume);
        reservedVip.setSumFeed(sumFeed);
        reservedVip.setSumScore(sumScore);
        reservedVip.setRemainingScore(remainingScore);
        reservedVip.setLargessscore(largessscore);
        reservedVip.setOverscore(overscore);
        reservedVip.setVipStoreTotal(vipStoreTotal);
        reservedVip.setVipStore(vipStore);
        reservedVip.setVipDeposit(vipDeposit);

        reservedVip.setPhone(phone);
        reservedVip.setOriginalId(publicAccountOpenId);
        reservedVip.setMiniProgramOpenId(miniOpenId);
        reservedVip.setLastUpdateAt(now);
        DatabaseHelper.update(reservedVip);

        SearchModel vipAddressSearchModel = new SearchModel(true);
        vipAddressSearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, reservedVip.getId());
        List<OnlineVipAddress> onlineVipAddresses = onlineVipAddressMapper.findAll(vipAddressSearchModel);

        SearchModel onlineVipTypeSearchModel = new SearchModel(true);
        onlineVipTypeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, reservedVip.getTypeId());
        OnlineVipType onlineVipType = DatabaseHelper.find(OnlineVipType.class, onlineVipTypeSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vip", reservedVip);
        data.put("vipAddresses", onlineVipAddresses);
        data.put("vipType", onlineVipType);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("认证会员信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取商户配置
     *
     * @param obtainTenantConfigModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainTenantConfig(ObtainTenantConfigModel obtainTenantConfigModel) {
        BigInteger tenantId = obtainTenantConfigModel.getTenantId();
        String name = obtainTenantConfigModel.getName();

        OnlineTenantConfig onlineTenantConfig = TenantConfigUtils.obtainTenantConfig(tenantId, name);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineTenantConfig);
        apiRest.setMessage("获取商户配置成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询会员消费历史
     *
     * @param queryConsumeHistoryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest queryConsumeHistory(QueryConsumeHistoryModel queryConsumeHistoryModel) {
        BigInteger tenantId = queryConsumeHistoryModel.getTenantId();
        BigInteger vipId = queryConsumeHistoryModel.getVipId();
        int months = queryConsumeHistoryModel.getMonths();

        List<Map<String, Object>> consumeHistories = onlineVipMapper.queryConsumeHistory(tenantId, vipId, DateUtils.addMonths(new Date(), months * -1), null);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(consumeHistories);
        apiRest.setMessage("查询会员消费历史成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
