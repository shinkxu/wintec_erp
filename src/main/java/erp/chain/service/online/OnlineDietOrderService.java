package erp.chain.service.online;

import com.saas.common.util.Common;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.BranchTable;
import erp.chain.domain.DietPromotion;
import erp.chain.domain.DietPromotionTotalReduce;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.domain.o2o.DietOrderInfoStatus;
import erp.chain.domain.o2o.DietOrderPromotionR;
import erp.chain.domain.online.*;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.DietPromotionTotalReduceMapper;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.online.*;
import erp.chain.model.online.onlinedietorder.*;
import erp.chain.model.online.onlinegoods.SaveSelfHelpShoppingOrderModel;
import erp.chain.service.report.ReportService;
import erp.chain.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2018-04-09.
 */
@Service
public class OnlineDietOrderService {
    @Autowired
    private OnlineDietOrderInfoMapper onlineDietOrderInfoMapper;
    @Autowired
    private OnlineDietOrderDetailMapper onlineDietOrderDetailMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private OnlineVipMapper onlineVipMapper;
    @Autowired
    private OnlineVipAddressMapper onlineVipAddressMapper;
    @Autowired
    private OnlineGoodsMapper onlineGoodsMapper;
    @Autowired
    private OnlineGoodsPromotionMapper onlineGoodsPromotionMapper;
    @Autowired
    private DietPromotionTotalReduceMapper dietPromotionTotalReduceMapper;

    /**
     * 保存订单
     *
     * @param saveDietOrderModel
     * @return
     * @throws ParseException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveDietOrder(SaveDietOrderModel saveDietOrderModel) throws ParseException {
        BigInteger tenantId = saveDietOrderModel.getTenantId();
        BigInteger branchId = saveDietOrderModel.getBranchId();
        BigInteger vipId = saveDietOrderModel.getVipId();
        BigInteger vipAddressId = saveDietOrderModel.getVipAddressId();
        Integer scope = saveDietOrderModel.getScope();
        Integer forCustomerType = saveDietOrderModel.getForCustomerType();
        Integer menuType = saveDietOrderModel.getMenuType();
        int orderMode = saveDietOrderModel.getOrderMode();
        int payWay = saveDietOrderModel.getPayWay();
        BigDecimal useScore = saveDietOrderModel.getUseScore();
        BigInteger cardId = saveDietOrderModel.getCardId();

        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(saveDietOrderModel.getTenantId(), saveDietOrderModel.getBranchId());
        ValidateUtils.notNull(branch, "机构不存在！");

        SearchModel vipSearchModel = new SearchModel(true);
        vipSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        vipSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = onlineVipMapper.find(vipSearchModel);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        SearchModel vipTypeSearchModel = new SearchModel(true);
        vipTypeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        vipTypeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineVip.getTypeId());
        OnlineVipType onlineVipType = DatabaseHelper.find(OnlineVipType.class, vipTypeSearchModel);
        ValidateUtils.notNull(onlineVipType, "会员类型不存在！");

        OnlineVipAddress onlineVipAddress = null;
        if (orderMode == 4) {
            SearchModel vipAddressSearchModel = new SearchModel(true);
            vipAddressSearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
            vipAddressSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipAddressId);
            onlineVipAddress = onlineVipAddressMapper.find(vipAddressSearchModel);
            ValidateUtils.notNull(onlineVipAddress, "会员地址不存在！");
        }


        Date date = new Date();
        OnlineDietOrderInfo onlineDietOrderInfo = new OnlineDietOrderInfo();
        onlineDietOrderInfo.setTenantId(saveDietOrderModel.getTenantId());
        onlineDietOrderInfo.setBranchId(branch.getId());
        onlineDietOrderInfo.setBranchName(branch.getName());
        onlineDietOrderInfo.setOrderMode(orderMode);
        String orderCode = "WX" + branch.getId() + Common.getToday().substring(2) + serialNumberCreatorMapper.getToday("WX", 4);
        onlineDietOrderInfo.setOrderCode(orderCode);
        onlineDietOrderInfo.setVipId(onlineVip.getId());
        onlineDietOrderInfo.setOrderStatus(1);
        onlineDietOrderInfo.setEatStatus(0);
        onlineDietOrderInfo.setPayStatus(0);
        onlineDietOrderInfo.setTotalAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setReceivedAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setUseScore(0);
        onlineDietOrderInfo.setAmount(0);
        onlineDietOrderInfo.setIsUsePrivilege(false);
        onlineDietOrderInfo.setPayAt(null);
        onlineDietOrderInfo.setCreateScore(0);
        onlineDietOrderInfo.setEatPeople(0);
        onlineDietOrderInfo.setAppointmentDate(null);
        if (onlineVipAddress != null) {
            onlineDietOrderInfo.setConsignee(onlineVipAddress.getConsignee());
            onlineDietOrderInfo.setMobilePhone(onlineVipAddress.getMobilePhone());
            onlineDietOrderInfo.setVipAddressId(onlineVipAddress.getId());
            onlineDietOrderInfo.setVipAddressName(onlineVipAddress.getAddress());
        } else {
            onlineDietOrderInfo.setConsignee(null);
            onlineDietOrderInfo.setMobilePhone(null);
            onlineDietOrderInfo.setVipAddressId(null);
            onlineDietOrderInfo.setVipAddressName(null);
        }

        Date allocationDate = null;
        if (orderMode == 1) {
            String eatTime = saveDietOrderModel.getEatTime();
            if ("立即就餐".equals(eatTime)) {
                allocationDate = new Date();
            } else {
                allocationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + eatTime);
            }
        } else if (orderMode == 2) {
            String pickedUpTime = saveDietOrderModel.getPickedUpTime();
            if ("立即自提".equals(pickedUpTime)) {
                allocationDate = new Date();
            } else {
                allocationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + pickedUpTime);
            }
        } else if (orderMode == 4) {
            String deliveryTime = saveDietOrderModel.getDeliveryTime();
            if ("立即配送".equals(deliveryTime)) {
                allocationDate = new Date();
            } else {
                allocationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + deliveryTime);
            }
        }

        onlineDietOrderInfo.setAllocationDate(allocationDate);
        onlineDietOrderInfo.setArriveDate(null);
        onlineDietOrderInfo.setRemark(saveDietOrderModel.getRemark());
        onlineDietOrderInfo.setIsFreeOfCharge(false);
        onlineDietOrderInfo.setPosCode(null);
        onlineDietOrderInfo.setCashier(null);
        onlineDietOrderInfo.setTableOpenAt(null);
        onlineDietOrderInfo.setIsRefund(false);
        onlineDietOrderInfo.setCreateAt(date);
        onlineDietOrderInfo.setLastUpdateAt(date);
        onlineDietOrderInfo.setPayWay(payWay);
        onlineDietOrderInfo.setOrderResource(1);
        onlineDietOrderInfo.setVipOpenid(onlineVip.getOriginalId());
        onlineDietOrderInfo.setScorePay(BigDecimal.ZERO);
        onlineDietOrderInfo.setSessionStr(null);
        onlineDietOrderInfo.setCardValue(BigDecimal.ZERO);
        onlineDietOrderInfo.setTableCode(saveDietOrderModel.getTableCode());
        onlineDietOrderInfo.setUsedCardId(null);
        onlineDietOrderInfo.setMeituanDaySeq(null);
        onlineDietOrderInfo.setElemeOrderStatus(null);
        onlineDietOrderInfo.setToken(null);
        onlineDietOrderInfo.setDeliverFee(BigDecimal.ZERO);
        onlineDietOrderInfo.setPackageFee(BigDecimal.ZERO);
        onlineDietOrderInfo.setMeituanRefundReason(null);
        onlineDietOrderInfo.setDispatcherName(null);
        onlineDietOrderInfo.setDispatcherMobile(null);
        onlineDietOrderInfo.setIncome(BigDecimal.ZERO);
        onlineDietOrderInfo.setDiscountAmount(null);
        onlineDietOrderInfoMapper.insert(onlineDietOrderInfo);

        BigInteger orderId = onlineDietOrderInfo.getId();

        List<OnlineGoodsPromotion> onlineGoodsPromotions = onlineGoodsPromotionMapper.findAllOnlineGoodsPromotions(tenantId, branchId, scope, forCustomerType);
        Map<BigInteger, OnlineGoodsPromotion> onlineGoodsPromotionMap = new HashMap<BigInteger, OnlineGoodsPromotion>();

        for (OnlineGoodsPromotion onlineGoodsPromotion : onlineGoodsPromotions) {
            onlineGoodsPromotionMap.put(onlineGoodsPromotion.getGoodsId(), onlineGoodsPromotion);
        }

        List<SaveDietOrderModel.GoodsInfo> goodsInfos = saveDietOrderModel.getGoodsInfos();
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> packageIds = new ArrayList<BigInteger>();
        List<BigInteger> giveGoodsIds = new ArrayList<BigInteger>();
        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            int goodsType = goodsInfo.getGoodsType();

            BigInteger goodsId = goodsInfo.getGoodsId();
            goodsIds.add(goodsId);
            if (goodsType == 1) {
//                goodsIds.add(goodsInfo.getGoodsId());
            } else if (goodsType == 2) {
                packageIds.add(goodsId);
                /*List<SaveDietOrderModel.PackageGroupInfo> packageGroupInfos = goodsInfo.getPackageGroupInfos();
                for (SaveDietOrderModel.PackageGroupInfo packageGroupInfo : packageGroupInfos) {
                    List<SaveDietOrderModel.GroupGoodsInfo> groupGoodsInfos = packageGroupInfo.getGroupGoodsInfos();
                    for (SaveDietOrderModel.GroupGoodsInfo groupGoodsInfo : groupGoodsInfos) {
                        goodsIds.add(groupGoodsInfo.getGoodsId());
                    }
                }*/
            }
            OnlineGoodsPromotion onlineGoodsPromotion = onlineGoodsPromotionMap.get(goodsId);
            if (onlineGoodsPromotion != null && onlineGoodsPromotion.getPromotionType() == 4) {
                giveGoodsIds.add(onlineGoodsPromotion.getGiveGoodsId());
            }
        }

        Map<String, Object> effectiveMenu = onlineGoodsMapper.findEffectiveMenu(tenantId, branchId, menuType);
        BigInteger menuId = BigInteger.valueOf(MapUtils.getLongValue(effectiveMenu, "menuId"));

//        SearchModel goodsSearchModel = new SearchModel(true);
//        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Map<String, Object>> menuGoodsInfos = onlineGoodsMapper.listGoodsInfos(tenantId, branchId, menuId, goodsIds);
        Map<BigInteger, Map<String, Object>> menuGoodsInfoMap = new HashMap<BigInteger, Map<String, Object>>();
        for (Map<String, Object> menuGoodsInfo : menuGoodsInfos) {
            menuGoodsInfoMap.put(BigInteger.valueOf(MapUtils.getLongValue(menuGoodsInfo, "id")), menuGoodsInfo);
        }

        Map<BigInteger, Map<String, Object>> giveGoodsInfoMap = new HashMap<BigInteger, Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(giveGoodsIds)) {
            List<Map<String, Object>> giveGoodsInfos = onlineGoodsMapper.listGoodsInfos(tenantId, branchId, menuId, giveGoodsIds);
            for (Map<String, Object> giveGoodsInfo : giveGoodsInfos) {
                giveGoodsInfoMap.put(BigInteger.valueOf(MapUtils.getLongValue(giveGoodsInfo, "id")), giveGoodsInfo);
            }
        }

        List<Map<String, Object>> packageGroupGoodsInfos = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(packageIds)) {
            packageGroupGoodsInfos = onlineGoodsMapper.findPackageGroupInfos(packageIds);
        }

        Map<BigInteger, List<Map<String, Object>>> packageRequiredGroupInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        Map<String, Map<String, Object>> packageOptionalGroupInfoMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> packageGroupGoodsInfo : packageGroupGoodsInfos) {
            BigInteger packageId = BigInteger.valueOf(MapUtils.getLongValue(packageGroupGoodsInfo, "packageId"));
            boolean isMain = MapUtils.getBooleanValue(packageGroupGoodsInfo, "isMain");
            if (isMain) {
                List<Map<String, Object>> packageRequiredGroupInfoList = packageRequiredGroupInfoMap.get(packageId);
                if (CollectionUtils.isEmpty(packageRequiredGroupInfoList)) {
                    packageRequiredGroupInfoList = new ArrayList<Map<String, Object>>();
                    packageRequiredGroupInfoMap.put(packageId, packageRequiredGroupInfoList);
                }
                packageRequiredGroupInfoList.add(packageGroupGoodsInfo);
            } else {
                packageOptionalGroupInfoMap.put(packageId + "_" + packageGroupGoodsInfo.get("packageGroupId") + "_" + packageGroupGoodsInfo.get("id"), packageGroupGoodsInfo);
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal receivedAmount = BigDecimal.ZERO;
        BigDecimal packageFee = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Integer amount = 0;
        List<OnlineDietOrderDetail> onlineDietOrderDetails = new ArrayList<OnlineDietOrderDetail>();
        List<DietOrderPromotionR> dietOrderPromotionRs = new ArrayList<DietOrderPromotionR>();
        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            int goodsType = goodsInfo.getGoodsType();
            Map<String, Object> menuGoodsInfo = menuGoodsInfoMap.get(goodsInfo.getGoodsId());
            ValidateUtils.notNull(menuGoodsInfo, "商品不存在！");

            BigInteger menuGoodsId = BigInteger.valueOf(MapUtils.getLongValue(menuGoodsInfo, "id"));
            String standardName = MapUtils.getString(menuGoodsInfo, "standardName");
            String goodsName = MapUtils.getString(menuGoodsInfo, "goodsName");
            BigDecimal vipPrice = GoodsUtils.obtainVipPrice(onlineVipType, menuGoodsInfo);

            boolean isPackage = false;
            BigInteger goodsId = null;
            BigInteger packageId = null;
            String packageName = null;
            String packageCode = null;
            if (goodsType == 1) {
                isPackage = false;
                goodsId = menuGoodsId;
                packageId = BigInteger.ZERO;
                packageName = null;
                packageCode = null;
            } else if (goodsType == 2) {
                isPackage = true;
                goodsId = BigInteger.ZERO;
                packageId = menuGoodsId;
                packageName = null;
                packageCode = UUID.randomUUID().toString();
            }
            BigDecimal quantity = goodsInfo.getQuantity();
            OnlineDietOrderDetail onlineDietOrderDetail = new OnlineDietOrderDetail();
            onlineDietOrderDetail.setDietOrderInfoId(orderId);
            onlineDietOrderDetail.setIsPackage(isPackage);
            onlineDietOrderDetail.setGoodsId(goodsId);
            onlineDietOrderDetail.setGoodsName(StringUtils.isBlank(standardName) ? goodsName : goodsName + "-" + standardName);
            onlineDietOrderDetail.setPackageId(packageId);
            onlineDietOrderDetail.setPackageName(packageName);
            onlineDietOrderDetail.setSalePrice(vipPrice);
            onlineDietOrderDetail.setSalePriceActual(vipPrice);
            onlineDietOrderDetail.setQuantity(quantity);
            onlineDietOrderDetail.setTotalAmount(vipPrice.multiply(quantity));
//            onlineDietOrderDetail.setReceivedAmount(onlineGoods.getVipPrice().multiply(goodsInfo.getQuantity()));
            onlineDietOrderDetail.setIsFreeOfCharge(false);
            onlineDietOrderDetail.setIsRefund(false);
            onlineDietOrderDetail.setTaste(null);
            onlineDietOrderDetail.setTasteName(null);
            onlineDietOrderDetail.setSize(null);
            onlineDietOrderDetail.setSizeName(null);
            onlineDietOrderDetail.setCreateAt(date);
            onlineDietOrderDetail.setCreateBy(null);
            onlineDietOrderDetail.setLastUpdateAt(date);
            onlineDietOrderDetail.setLastUpdateBy(null);
            onlineDietOrderDetail.setIsDeleted(false);
            onlineDietOrderDetail.setStatus(0);
            onlineDietOrderDetail.setVersion(null);
            onlineDietOrderDetail.setLocalId(null);
            onlineDietOrderDetail.setTenantId(tenantId);
            onlineDietOrderDetail.setBranchId(branchId);
            onlineDietOrderDetail.setRetailLocalId(null);
            onlineDietOrderDetail.setPackageCode(packageCode);
            onlineDietOrderDetail.setGroupId(null);
            onlineDietOrderDetail.setGroupName("正常菜品");
            onlineDietOrderDetail.setGroupType("normal");
            onlineDietOrderDetail.setAllowDiscount(MapUtils.getBooleanValue(menuGoodsInfo, "isDsc"));

            if (goodsType == 2) {
                StringBuilder packageDetail = new StringBuilder();
                List<Map<String, Object>> requiredGroupInfos = packageRequiredGroupInfoMap.get(menuGoodsId);
                for (Map<String, Object> requiredGroupInfo : requiredGroupInfos) {
                    OnlineDietOrderDetail packageDetailOnlineDietOrderDetail = new OnlineDietOrderDetail();
                    packageDetailOnlineDietOrderDetail.setDietOrderInfoId(orderId);
                    packageDetailOnlineDietOrderDetail.setIsPackage(true);
                    packageDetailOnlineDietOrderDetail.setGoodsId(BigInteger.valueOf(MapUtils.getLongValue(requiredGroupInfo, "id")));

                    String requiredGroupGoodsName = MapUtils.getString(requiredGroupInfo, "goodsName");
                    String requiredGroupStandardName = MapUtils.getString(requiredGroupInfo, "standardName");
                    String name = StringUtils.isBlank(requiredGroupStandardName) ? requiredGroupGoodsName : requiredGroupGoodsName + "-" + requiredGroupStandardName;
                    BigDecimal detailQuantity = BigDecimal.valueOf(MapUtils.getDoubleValue(requiredGroupInfo, "quantity"));
                    packageDetailOnlineDietOrderDetail.setGoodsName(name);
                    packageDetailOnlineDietOrderDetail.setPackageId(menuGoodsId);
                    packageDetailOnlineDietOrderDetail.setPackageName(null);
                    packageDetailOnlineDietOrderDetail.setSalePrice(BigDecimal.ZERO);
                    packageDetailOnlineDietOrderDetail.setSalePriceActual(BigDecimal.ZERO);
                    packageDetailOnlineDietOrderDetail.setQuantity(quantity.multiply(detailQuantity));
                    packageDetailOnlineDietOrderDetail.setTotalAmount(BigDecimal.ZERO);
                    packageDetailOnlineDietOrderDetail.setReceivedAmount(BigDecimal.ZERO);
                    packageDetailOnlineDietOrderDetail.setIsFreeOfCharge(false);
                    packageDetailOnlineDietOrderDetail.setIsRefund(false);
                    packageDetailOnlineDietOrderDetail.setTaste(null);
                    packageDetailOnlineDietOrderDetail.setTasteName(null);
                    packageDetailOnlineDietOrderDetail.setSize(null);
                    packageDetailOnlineDietOrderDetail.setSizeName(null);
                    packageDetailOnlineDietOrderDetail.setCreateAt(date);
                    packageDetailOnlineDietOrderDetail.setCreateBy(null);
                    packageDetailOnlineDietOrderDetail.setLastUpdateAt(date);
                    packageDetailOnlineDietOrderDetail.setLastUpdateBy(null);
                    packageDetailOnlineDietOrderDetail.setIsDeleted(false);
                    packageDetailOnlineDietOrderDetail.setStatus(0);
                    packageDetailOnlineDietOrderDetail.setVersion(null);
                    packageDetailOnlineDietOrderDetail.setLocalId(null);
                    packageDetailOnlineDietOrderDetail.setTenantId(tenantId);
                    packageDetailOnlineDietOrderDetail.setBranchId(branchId);
                    packageDetailOnlineDietOrderDetail.setRetailLocalId(null);
                    packageDetailOnlineDietOrderDetail.setPackageCode(packageCode);
                    packageDetailOnlineDietOrderDetail.setGroupId(null);
                    packageDetailOnlineDietOrderDetail.setGroupName("正常菜品");
                    packageDetailOnlineDietOrderDetail.setGroupType("normal");
                    onlineDietOrderDetails.add(packageDetailOnlineDietOrderDetail);
                    packageDetail.append(name).append("x").append(detailQuantity).append(" ");
                }
                List<SaveDietOrderModel.PackageGroupInfo> packageGroupInfos = goodsInfo.getPackageGroupInfos();
                for (SaveDietOrderModel.PackageGroupInfo packageGroupInfo : packageGroupInfos) {
                    List<SaveDietOrderModel.GroupGoodsInfo> groupGoodsInfos = packageGroupInfo.getGroupGoodsInfos();
                    for (SaveDietOrderModel.GroupGoodsInfo groupGoodsInfo : groupGoodsInfos) {
                        Map<String, Object> optionalGroupGoodsInfo = packageOptionalGroupInfoMap.get(menuGoodsId + "_" + packageGroupInfo.getGroupId() + "_" + groupGoodsInfo.getGoodsId());
                        ValidateUtils.notNull(optionalGroupGoodsInfo, "商品不存在！");

                        BigInteger optionalGroupGoodsId = BigInteger.valueOf(MapUtils.getLongValue(optionalGroupGoodsInfo, "id"));
                        String optionalGroupGoodsName = MapUtils.getString(optionalGroupGoodsInfo, "goodsName");
                        String optionalGroupStandardName = MapUtils.getString(optionalGroupGoodsInfo, "standardName");
                        String name = StringUtils.isBlank(optionalGroupStandardName) ? optionalGroupGoodsName : optionalGroupGoodsName + "-" + optionalGroupStandardName;

                        OnlineDietOrderDetail packageDetailOnlineDietOrderDetail = new OnlineDietOrderDetail();
                        packageDetailOnlineDietOrderDetail.setDietOrderInfoId(orderId);
                        packageDetailOnlineDietOrderDetail.setIsPackage(true);
                        packageDetailOnlineDietOrderDetail.setGoodsId(optionalGroupGoodsId);

                        packageDetailOnlineDietOrderDetail.setGoodsName(name);
                        packageDetailOnlineDietOrderDetail.setPackageId(menuGoodsId);
                        packageDetailOnlineDietOrderDetail.setPackageName(null);
                        packageDetailOnlineDietOrderDetail.setSalePrice(BigDecimal.ZERO);
                        packageDetailOnlineDietOrderDetail.setSalePriceActual(BigDecimal.ZERO);
                        packageDetailOnlineDietOrderDetail.setQuantity(quantity.multiply(groupGoodsInfo.getQuantity()));
                        packageDetailOnlineDietOrderDetail.setTotalAmount(BigDecimal.ZERO);
                        packageDetailOnlineDietOrderDetail.setReceivedAmount(BigDecimal.ZERO);
                        packageDetailOnlineDietOrderDetail.setIsFreeOfCharge(false);
                        packageDetailOnlineDietOrderDetail.setIsRefund(false);
                        packageDetailOnlineDietOrderDetail.setTaste(null);
                        packageDetailOnlineDietOrderDetail.setTasteName(null);
                        packageDetailOnlineDietOrderDetail.setSize(null);
                        packageDetailOnlineDietOrderDetail.setSizeName(null);
                        packageDetailOnlineDietOrderDetail.setCreateAt(date);
                        packageDetailOnlineDietOrderDetail.setCreateBy(null);
                        packageDetailOnlineDietOrderDetail.setLastUpdateAt(date);
                        packageDetailOnlineDietOrderDetail.setLastUpdateBy(null);
                        packageDetailOnlineDietOrderDetail.setIsDeleted(false);
                        packageDetailOnlineDietOrderDetail.setStatus(0);
                        packageDetailOnlineDietOrderDetail.setVersion(null);
                        packageDetailOnlineDietOrderDetail.setLocalId(null);
                        packageDetailOnlineDietOrderDetail.setTenantId(tenantId);
                        packageDetailOnlineDietOrderDetail.setBranchId(branchId);
                        packageDetailOnlineDietOrderDetail.setRetailLocalId(null);
                        packageDetailOnlineDietOrderDetail.setPackageCode(packageCode);
                        packageDetailOnlineDietOrderDetail.setGroupId(null);
                        packageDetailOnlineDietOrderDetail.setGroupName("正常菜品");
                        packageDetailOnlineDietOrderDetail.setGroupType("normal");
                        onlineDietOrderDetails.add(packageDetailOnlineDietOrderDetail);
                        packageDetail.append(name).append("x").append(groupGoodsInfo.getQuantity()).append(" ");
                    }
                }
                packageDetail = packageDetail.deleteCharAt(packageDetail.length() - 1);
                onlineDietOrderDetail.setPackageName(packageDetail.toString());
            }
            onlineDietOrderDetails.add(onlineDietOrderDetail);

            // 开始处理促销活动
            OnlineGoodsPromotion onlineGoodsPromotion = onlineGoodsPromotionMap.get(menuGoodsId);
            if (onlineGoodsPromotion != null) {
                int promotionType = onlineGoodsPromotion.getPromotionType();
                if (promotionType == 4) {
                    Map<String, Object> giveMenuGoodsInfo = giveGoodsInfoMap.get(onlineGoodsPromotion.getGiveGoodsId());
                    if (GoodsUtils.isGive(onlineGoodsPromotion, giveMenuGoodsInfo, quantity)) {
                        BigInteger giveMenuGoodsId = BigInteger.valueOf(MapUtils.getLongValue(giveMenuGoodsInfo, "id"));
                        String giveStandardName = MapUtils.getString(giveMenuGoodsInfo, "standardName");
                        String giveGoodsName = MapUtils.getString(giveMenuGoodsInfo, "goodsName");
                        BigDecimal salePrice = GoodsUtils.obtainSalePrice(giveMenuGoodsInfo);
                        BigDecimal giveQuantity = GoodsUtils.calculateGiveQuantity(quantity, onlineGoodsPromotion);
                        OnlineDietOrderDetail giveOnlineDietOrderDetail = new OnlineDietOrderDetail();
                        giveOnlineDietOrderDetail.setDietOrderInfoId(orderId);
                        giveOnlineDietOrderDetail.setIsPackage(false);
                        giveOnlineDietOrderDetail.setGoodsId(giveMenuGoodsId);
                        giveOnlineDietOrderDetail.setGoodsName(StringUtils.isBlank(giveStandardName) ? giveGoodsName : giveGoodsName + "-" + giveStandardName);
                        giveOnlineDietOrderDetail.setPackageId(BigInteger.ZERO);
                        giveOnlineDietOrderDetail.setPackageName(null);
                        giveOnlineDietOrderDetail.setSalePrice(salePrice);
                        giveOnlineDietOrderDetail.setSalePriceActual(salePrice);
                        giveOnlineDietOrderDetail.setQuantity(giveQuantity);
                        giveOnlineDietOrderDetail.setTotalAmount(salePrice.multiply(giveQuantity));
                        giveOnlineDietOrderDetail.setReceivedAmount(BigDecimal.ZERO);
                        giveOnlineDietOrderDetail.setIsFreeOfCharge(true);
                        giveOnlineDietOrderDetail.setIsRefund(false);
                        giveOnlineDietOrderDetail.setTaste(null);
                        giveOnlineDietOrderDetail.setTasteName(null);
                        giveOnlineDietOrderDetail.setSize(null);
                        giveOnlineDietOrderDetail.setSizeName(null);
                        giveOnlineDietOrderDetail.setCreateAt(date);
                        giveOnlineDietOrderDetail.setCreateBy(null);
                        giveOnlineDietOrderDetail.setLastUpdateAt(date);
                        giveOnlineDietOrderDetail.setLastUpdateBy(null);
                        giveOnlineDietOrderDetail.setIsDeleted(false);
                        giveOnlineDietOrderDetail.setStatus(0);
                        giveOnlineDietOrderDetail.setVersion(null);
                        giveOnlineDietOrderDetail.setLocalId(null);
                        giveOnlineDietOrderDetail.setTenantId(tenantId);
                        giveOnlineDietOrderDetail.setBranchId(branchId);
                        giveOnlineDietOrderDetail.setRetailLocalId(null);
                        giveOnlineDietOrderDetail.setPackageCode(null);
                        giveOnlineDietOrderDetail.setGroupId(null);
                        giveOnlineDietOrderDetail.setGroupName("赠品");
                        giveOnlineDietOrderDetail.setGroupType("discount");
                        onlineDietOrderDetails.add(giveOnlineDietOrderDetail);
                        amount = amount + 1;
                        totalAmount = totalAmount.add(giveOnlineDietOrderDetail.getTotalAmount());
                        BigDecimal thisDiscountAmount = giveOnlineDietOrderDetail.getTotalAmount().subtract(giveOnlineDietOrderDetail.getReceivedAmount());
                        discountAmount = discountAmount.add(thisDiscountAmount);
                        dietOrderPromotionRs.add(OrderUtils.buildDietOrderPromotionR(orderId, orderCode, thisDiscountAmount, onlineGoodsPromotion));
                        onlineDietOrderDetail.setReceivedAmount(vipPrice.multiply(quantity));
                    } else {
                        onlineDietOrderDetail.setReceivedAmount(vipPrice.multiply(quantity));
                    }
                } else if (promotionType == 6) {
                    int number = quantity.intValue() / 2;
                    BigDecimal discount = onlineGoodsPromotion.getDiscount();
                    BigDecimal thisDiscountAmount = vipPrice.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED.subtract(discount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED)).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(number));
                    onlineDietOrderDetail.setReceivedAmount(onlineDietOrderDetail.getTotalAmount().subtract(thisDiscountAmount));
                    dietOrderPromotionRs.add(OrderUtils.buildDietOrderPromotionR(orderId, orderCode, thisDiscountAmount, onlineGoodsPromotion));
                } else if (promotionType == 7) {
                    if (onlineDietOrderDetail.getTotalAmount().compareTo(onlineGoodsPromotion.getSatisfy()) >= 0) {
                        onlineDietOrderDetail.setReceivedAmount(onlineDietOrderDetail.getTotalAmount().subtract(onlineGoodsPromotion.getReduction()));
                        dietOrderPromotionRs.add(OrderUtils.buildDietOrderPromotionR(orderId, orderCode, onlineGoodsPromotion.getReduction(), onlineGoodsPromotion));
                    } else {
                        onlineDietOrderDetail.setReceivedAmount(vipPrice.multiply(quantity));
                    }
                } else if (promotionType == 11) {
                    BigDecimal promotionPrice = GoodsUtils.calculatePromotionPrice(menuGoodsInfo, onlineVipType, onlineGoodsPromotion);
                    if (onlineVipType.getPreferentialPolicy().intValue() == 2) {
                        onlineDietOrderDetail.setSalePrice(promotionPrice);
                        onlineDietOrderDetail.setSalePriceActual(promotionPrice);
                        BigDecimal thisTotalAmount = promotionPrice.multiply(quantity);
                        onlineDietOrderDetail.setTotalAmount(thisTotalAmount);
                        onlineDietOrderDetail.setReceivedAmount(thisTotalAmount);
                    } else {
                        BigDecimal salePrice = GoodsUtils.obtainSalePrice(menuGoodsInfo);
                        onlineDietOrderDetail.setSalePrice(salePrice);
                        onlineDietOrderDetail.setSalePriceActual(salePrice);
                        onlineDietOrderDetail.setTotalAmount(salePrice.multiply(quantity));
                        onlineDietOrderDetail.setReceivedAmount(promotionPrice.multiply(quantity));
                        dietOrderPromotionRs.add(OrderUtils.buildDietOrderPromotionR(orderId, orderCode, onlineDietOrderDetail.getTotalAmount().subtract(onlineDietOrderDetail.getReceivedAmount()), onlineGoodsPromotion));
                    }
                }
            } else {
                onlineDietOrderDetail.setReceivedAmount(vipPrice.multiply(quantity));
            }

            amount = amount + 1;
            totalAmount = totalAmount.add(onlineDietOrderDetail.getTotalAmount());
            receivedAmount = receivedAmount.add(onlineDietOrderDetail.getReceivedAmount());
            discountAmount = discountAmount.add(onlineDietOrderDetail.getTotalAmount().subtract(onlineDietOrderDetail.getReceivedAmount()));
            if (menuGoodsInfo.get("boxPrice") != null) {
                packageFee = packageFee.add(BigDecimal.valueOf(MapUtils.getDoubleValue(menuGoodsInfo, "boxPrice")).multiply(goodsInfo.getQuantity()));
            }
        }

        if (CollectionUtils.isNotEmpty(dietOrderPromotionRs)) {
            DatabaseHelper.insertAll(dietOrderPromotionRs);
        }

        DietPromotion dietPromotion = onlineGoodsPromotionMapper.findEffectiveDietPromotionTotalReduce(tenantId, branchId, scope, forCustomerType);
        DietPromotionTotalReduce dietPromotionTotalReduce = null;
        if (dietPromotion != null) {
            dietPromotionTotalReduce = dietPromotionTotalReduceMapper.findByDietPromotionId(dietPromotion.getId());
        }
        if (dietPromotionTotalReduce != null && receivedAmount.compareTo(dietPromotionTotalReduce.getSatisfy()) >= 0) {
            int reduceType = dietPromotionTotalReduce.getReduceType();
            if (reduceType == 1) {
                BigDecimal reduction = dietPromotionTotalReduce.getReduction();
                receivedAmount = receivedAmount.subtract(reduction);
                discountAmount = discountAmount.add(reduction);
            } else if (reduceType == 2) {
                BigDecimal totalReduce = BigDecimal.ZERO;
                BigDecimal discount = dietPromotionTotalReduce.getDiscount();
                for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
                    if (onlineDietOrderDetail.isAllowDiscount()) {
                        totalReduce = totalReduce.add(onlineDietOrderDetail.getReceivedAmount()).subtract(onlineDietOrderDetail.getReceivedAmount().multiply(discount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                receivedAmount = receivedAmount.subtract(totalReduce);
                discountAmount = discountAmount.add(totalReduce);
            }
        }


        if (payWay == 1 || payWay == 4) {
            if (useScore != null) {
                BigDecimal scoreUsage = onlineVipType.getScoreUsage() != null ? BigDecimal.valueOf(Double.valueOf(onlineVipType.getScoreUsage().toString())) : null;
                if (scoreUsage != null) {
                    BigDecimal scorePay = useScore.divide(scoreUsage, 2, BigDecimal.ROUND_DOWN);
                    onlineDietOrderInfo.setScorePay(scorePay);
                    discountAmount = discountAmount.add(scorePay);
                    receivedAmount = receivedAmount.subtract(scorePay);

                    onlineDietOrderInfo.setUseScore(useScore.intValue());
                    onlineDietOrderInfo.setScorePay(scorePay);
                }
            }
            if (payWay == 4) {
                onlineDietOrderInfo.setPayStatus(1);
                onlineDietOrderInfo.setPayAt(date);
            }
        } else if (payWay == 3) {
            ValidateUtils.isTrue(orderMode == 4, "只有外卖配送的订单才支持货到付款！");
        } else if (payWay == 5) {
            ValidateUtils.isTrue(orderMode == 1 || orderMode == 2, "只有自助点餐和店内自提的订单才支持吧台付款！");
        }

        if (cardId != null && cardId.compareTo(BigInteger.ZERO) != 0) {
            SearchModel searchModel = new SearchModel();
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardId);
            OnlineCardCoupon onlineCardCoupon = DatabaseHelper.find(OnlineCardCoupon.class, searchModel);
            ValidateUtils.notNull(onlineCardCoupon, "优惠券不存在！");

            BigDecimal cardValue = receivedAmount;
            receivedAmount = receivedAmount.subtract(onlineCardCoupon.getFaceValue());
            if (receivedAmount.compareTo(BigDecimal.ZERO) < 0) {
                receivedAmount = BigDecimal.ZERO;
            } else {
                cardValue = onlineCardCoupon.getFaceValue();
            }
            discountAmount = discountAmount.add(cardValue);
        }
        onlineDietOrderDetailMapper.insertAll(onlineDietOrderDetails);
        onlineDietOrderInfo.setAmount(amount);
        onlineDietOrderInfo.setTotalAmount(totalAmount);
        onlineDietOrderInfo.setReceivedAmount(receivedAmount);
//        onlineDietOrderInfo.setPackageFee(packageFee);
        onlineDietOrderInfo.setDiscountAmount(discountAmount);
        onlineDietOrderInfoMapper.update(onlineDietOrderInfo);

        if (payWay == 4) {
            OrderUtils.storePay(onlineDietOrderInfo, onlineVip, onlineVipType);
            if (useScore != null && useScore.compareTo(BigDecimal.ZERO) > 0) {
                OrderUtils.scorePay(onlineDietOrderInfo, onlineVip);
            }
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineDietOrderInfo);
        apiRest.setMessage("操作成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 发起支付
     *
     * @param doPayModel
     * @return
     * @throws IOException
     */
    public ApiRest doPay(DoPayModel doPayModel) throws IOException {
        BigInteger orderId = doPayModel.getOrderId();
        BigInteger tenantId = doPayModel.getTenantId();
        BigInteger branchId = doPayModel.getBranchId();
        String tradeType = doPayModel.getTradeType();
        String subOpenId = doPayModel.getSubOpenId();
        String openId = doPayModel.getOpenId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        OnlineDietOrderInfo onlineDietOrderInfo = onlineDietOrderInfoMapper.find(searchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);

        Map<String, String> unifiedOrderRequestParameters = new HashMap<String, String>();
        unifiedOrderRequestParameters.put("tenantId", tenantId.toString());
        unifiedOrderRequestParameters.put("branchId", branchId.toString());
        unifiedOrderRequestParameters.put("headquartersBranchId", headquartersBranch.getId().toString());
        unifiedOrderRequestParameters.put("body", "订单支付");
        unifiedOrderRequestParameters.put("outTradeNo", onlineDietOrderInfo.getOrderCode());
        unifiedOrderRequestParameters.put("totalFee", String.valueOf(onlineDietOrderInfo.getReceivedAmount().multiply(NumberUtils.createBigDecimal("100")).intValue()));
        unifiedOrderRequestParameters.put("spbillCreateIp", ApplicationHandler.getRemoteAddress());

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String notifyUrl = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_O2O, partitionCode) + "/onlineDietOrder/weiXinPayCallback";
        unifiedOrderRequestParameters.put("notifyUrl", notifyUrl);

        if (Constants.MINIPROGRAM.equals(tradeType)) {
            unifiedOrderRequestParameters.put("subOpenId", subOpenId);
        } else {
            unifiedOrderRequestParameters.put("openId", openId);
        }

        unifiedOrderRequestParameters.put("tradeType", tradeType);
        ApiRest apiRest = ProxyApi.proxyPost(partitionCode, Constants.SERVICE_NAME_OUT, "weiXinPay", "unifiedOrder", unifiedOrderRequestParameters);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

        return apiRest;
    }

    /**
     * 处理支付回调
     *
     * @param paidType
     * @param parameters
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePayCallback(int paidType, Map<String, String> parameters) {
        String orderCode = null;
        String payOrderCode = null;
        if (paidType == 1) {
            payOrderCode = parameters.get("out_trade_no");
            orderCode = payOrderCode;
        }
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("order_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderCode);
        OnlineDietOrderInfo onlineDietOrderInfo = DatabaseHelper.find(OnlineDietOrderInfo.class, searchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        if (onlineDietOrderInfo.getPayStatus() == 0) {
            Date now = new Date();
            onlineDietOrderInfo.setPayWay(paidType);
            onlineDietOrderInfo.setPayStatus(1);
            onlineDietOrderInfo.setPayOrderCode(payOrderCode);
            onlineDietOrderInfo.setPayAt(now);
            onlineDietOrderInfo.setLastUpdateAt(now);

            Integer useScore = onlineDietOrderInfo.getUseScore();
            BigInteger vipId = onlineDietOrderInfo.getVipId();
            if (useScore != null && useScore > 0 && vipId != null) {
                OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, vipId);
                OrderUtils.scorePay(onlineDietOrderInfo, onlineVip);
            }

            OrderUtils.weiXinPay(onlineDietOrderInfo, vipId);

            DatabaseHelper.update(onlineDietOrderInfo);
        }
    }

    /**
     * 获取订单列表
     *
     * @param listModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest list(ListModel listModel) {
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listModel.getVipId()));
        String type = listModel.getType();
        if ("1".equals(type)) {
            searchConditions.add(new SearchCondition("order_status", Constants.SQL_OPERATION_SYMBOL_NOT_IN, new int[]{4, 6, 9}));
        } else if ("2".equals(type)) {
            searchConditions.add(new SearchCondition("order_status", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{4, 6, 9}));
        }
        searchConditions.add(new SearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{1, 2, 4, 11}));
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        long count = onlineDietOrderInfoMapper.count(searchModel);

        List<OnlineDietOrderInfo> onlineDietOrderInfos = new ArrayList<OnlineDietOrderInfo>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setPage(listModel.getPage());
            pagedSearchModel.setRows(listModel.getRows());
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setOrderBy("create_at DESC");
            onlineDietOrderInfos = onlineDietOrderInfoMapper.findAllPaged(pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", onlineDietOrderInfos);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询订单列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取订单详细信息
     *
     * @param orderDetailModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderDetail(OrderDetailModel orderDetailModel) {
        ApplicationHandler.getHttpServletRequest().getCookies();
        BigInteger vipId = orderDetailModel.getVipId();
        BigInteger orderId = orderDetailModel.getOrderId();

        SearchModel onlineDietOrderInfoSearchModel = new SearchModel(true);
        onlineDietOrderInfoSearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        onlineDietOrderInfoSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        OnlineDietOrderInfo onlineDietOrderInfo = onlineDietOrderInfoMapper.find(onlineDietOrderInfoSearchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        SearchModel onlineDietOrderDetailSearchModel = new SearchModel(true);
        onlineDietOrderDetailSearchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        List<OnlineDietOrderDetail> onlineDietOrderDetails = onlineDietOrderDetailMapper.findAll(onlineDietOrderDetailSearchModel);

        SearchModel dietOrderPromotionRSearchModel = new SearchModel();
        dietOrderPromotionRSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        List<DietOrderPromotionR> dietOrderPromotionRs = DatabaseHelper.findAll(DietOrderPromotionR.class, dietOrderPromotionRSearchModel);
        Map<String, DietOrderPromotionR> dietOrderPromotionRMap = new HashMap<String, DietOrderPromotionR>();
        for (DietOrderPromotionR dietOrderPromotionR : dietOrderPromotionRs) {
            dietOrderPromotionRMap.put(dietOrderPromotionR.getGoodsId().toString(), dietOrderPromotionR);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("orderInfo", onlineDietOrderInfo);
        data.put("orderDetails", onlineDietOrderDetails);
        data.put("orderPromotions", dietOrderPromotionRMap);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询订单明细成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存自助购订单
     *
     * @param saveSelfHelpShoppingOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveSelfHelpShoppingOrder(SaveSelfHelpShoppingOrderModel saveSelfHelpShoppingOrderModel) {
        BigInteger tenantId = saveSelfHelpShoppingOrderModel.getTenantId();
        BigInteger branchId = saveSelfHelpShoppingOrderModel.getBranchId();
        BigInteger vipId = saveSelfHelpShoppingOrderModel.getVipId();
        List<SaveSelfHelpShoppingOrderModel.GoodsInfo> goodsInfos = saveSelfHelpShoppingOrderModel.getGoodsInfos();

        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        ValidateUtils.notNull(branch, "机构不存在！");

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (SaveSelfHelpShoppingOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            goodsIds.add(goodsInfo.getGoodsId());
        }

        SearchModel vipSearchModel = new SearchModel(true);
        vipSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
//        vipSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        vipSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = onlineVipMapper.find(vipSearchModel);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        Map<String, Object> effectiveMenu = onlineGoodsMapper.findEffectiveMenu(tenantId, branchId, 3);

        Map<BigInteger, Map<String, Object>> onlineGoodsMap = new HashMap<BigInteger, Map<String, Object>>();

        BigInteger menuId = BigInteger.valueOf(MapUtils.getLongValue(effectiveMenu, "menuId"));
        if (menuId.compareTo(BigInteger.ZERO) != 0) {
            List<Map<String, Object>> menuGoodsInfos = onlineGoodsMapper.listGoodsInfos(tenantId, branchId, menuId, goodsIds);
            for (Map<String, Object> menuGoodsInfo : menuGoodsInfos) {
                BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(menuGoodsInfo, "id"));
                onlineGoodsMap.put(goodsId, menuGoodsInfo);
            }
        } else {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
            searchModel.addSearchCondition("goods_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);

            List<OnlineGoods> onlineGoodses = onlineGoodsMapper.findAll(searchModel);
            for (OnlineGoods onlineGoods : onlineGoodses) {
                onlineGoodsMap.put(onlineGoods.getId(), ApplicationHandler.toMap(onlineGoods));
            }
        }

        Date date = new Date();

        OnlineDietOrderInfo onlineDietOrderInfo = new OnlineDietOrderInfo();
        onlineDietOrderInfo.setTenantId(tenantId);
        onlineDietOrderInfo.setBranchId(branchId);
        onlineDietOrderInfo.setBranchName(branch.getName());
        onlineDietOrderInfo.setOrderMode(8);
        String orderCode = "ZZG" + branch.getId() + Common.getToday().substring(2) + serialNumberCreatorMapper.getToday("ZZG", 4);
        onlineDietOrderInfo.setOrderCode(orderCode);
        onlineDietOrderInfo.setVipId(onlineVip.getId());
        onlineDietOrderInfo.setOrderStatus(1);
        onlineDietOrderInfo.setEatStatus(0);
        onlineDietOrderInfo.setPayStatus(0);
        onlineDietOrderInfo.setTotalAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setReceivedAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setUseScore(0);
        onlineDietOrderInfo.setAmount(0);
        onlineDietOrderInfo.setIsUsePrivilege(false);
        onlineDietOrderInfo.setPayAt(null);
        onlineDietOrderInfo.setCreateScore(0);
        onlineDietOrderInfo.setEatPeople(0);
        onlineDietOrderInfo.setAppointmentDate(null);
        onlineDietOrderInfo.setConsignee(null);
        onlineDietOrderInfo.setMobilePhone(null);
        onlineDietOrderInfo.setAllocationDate(null);
        onlineDietOrderInfo.setArriveDate(null);
        onlineDietOrderInfo.setRemark(null);
        onlineDietOrderInfo.setIsFreeOfCharge(false);
        onlineDietOrderInfo.setPosCode(null);
        onlineDietOrderInfo.setCashier(null);
        onlineDietOrderInfo.setTableOpenAt(null);
        onlineDietOrderInfo.setIsRefund(false);
        onlineDietOrderInfo.setCreateAt(date);
        onlineDietOrderInfo.setLastUpdateAt(date);
        onlineDietOrderInfo.setVipAddressId(null);
        onlineDietOrderInfo.setVipAddressName(null);
        onlineDietOrderInfo.setPayWay(1);
        onlineDietOrderInfo.setOrderResource(1);
        onlineDietOrderInfo.setVipOpenid(onlineVip.getOriginalId());
        onlineDietOrderInfo.setScorePay(BigDecimal.ZERO);
        onlineDietOrderInfo.setSessionStr(null);
        onlineDietOrderInfo.setCardValue(BigDecimal.ZERO);
        onlineDietOrderInfo.setTableCode(null);
        onlineDietOrderInfo.setUsedCardId(null);
        onlineDietOrderInfo.setMeituanDaySeq(null);
        onlineDietOrderInfo.setElemeOrderStatus(null);
        onlineDietOrderInfo.setToken(null);
        onlineDietOrderInfo.setDeliverFee(BigDecimal.ZERO);
        onlineDietOrderInfo.setPackageFee(BigDecimal.ZERO);
        onlineDietOrderInfo.setMeituanRefundReason(null);
        onlineDietOrderInfo.setDispatcherName(null);
        onlineDietOrderInfo.setDispatcherMobile(null);
        onlineDietOrderInfo.setIncome(BigDecimal.ZERO);
        onlineDietOrderInfo.setDiscountAmount(BigDecimal.ZERO);
        onlineDietOrderInfoMapper.insert(onlineDietOrderInfo);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal receivedAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        List<OnlineDietOrderDetail> onlineDietOrderDetails = new ArrayList<OnlineDietOrderDetail>();
        for (SaveSelfHelpShoppingOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            Map<String, Object> onlineGoods = onlineGoodsMap.get(goodsInfo.getGoodsId());
            ValidateUtils.notNull(onlineGoods, "商品不存在！");
            BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(onlineGoods, "id"));
            boolean isPackage = MapUtils.getIntValue(onlineGoods, "goodsType") == 2;
            String standardName = MapUtils.getString(onlineGoods, "standardName");
            String goodsName = MapUtils.getString(onlineGoods, "goodsName");
            BigDecimal vipPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(onlineGoods, "vipPrice"));

            OnlineDietOrderDetail onlineDietOrderDetail = new OnlineDietOrderDetail();
            onlineDietOrderDetail.setDietOrderInfoId(onlineDietOrderInfo.getId());
            onlineDietOrderDetail.setIsPackage(isPackage);
            onlineDietOrderDetail.setGoodsId(goodsId);
            onlineDietOrderDetail.setGoodsName(StringUtils.isBlank(standardName) ? goodsName : goodsName + "-" + standardName);
            onlineDietOrderDetail.setPackageId(isPackage ? goodsId : null);
            onlineDietOrderDetail.setPackageName(isPackage ? goodsName : null);
            onlineDietOrderDetail.setSalePrice(vipPrice);
            onlineDietOrderDetail.setSalePriceActual(vipPrice);
            onlineDietOrderDetail.setQuantity(goodsInfo.getQuantity());

            BigDecimal onlineDietOrderDetailTotalAmount = vipPrice.multiply(goodsInfo.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP);
            totalAmount = totalAmount.add(onlineDietOrderDetailTotalAmount);
            receivedAmount = receivedAmount.add(onlineDietOrderDetailTotalAmount);

            onlineDietOrderDetail.setTotalAmount(onlineDietOrderDetailTotalAmount);
            onlineDietOrderDetail.setReceivedAmount(onlineDietOrderDetailTotalAmount);
            onlineDietOrderDetail.setIsFreeOfCharge(false);
            onlineDietOrderDetail.setIsRefund(false);
            onlineDietOrderDetail.setTaste(null);
            onlineDietOrderDetail.setTasteName(null);
            onlineDietOrderDetail.setSize(null);
            onlineDietOrderDetail.setSizeName(null);
            onlineDietOrderDetail.setCreateAt(date);
            onlineDietOrderDetail.setCreateBy(null);
            onlineDietOrderDetail.setLastUpdateAt(date);
            onlineDietOrderDetail.setLastUpdateBy(null);
            onlineDietOrderDetail.setIsDeleted(false);
            onlineDietOrderDetail.setStatus(0);
            onlineDietOrderDetail.setVersion(null);
            onlineDietOrderDetail.setLocalId(null);
            onlineDietOrderDetail.setTenantId(tenantId);
            onlineDietOrderDetail.setBranchId(branchId);
            onlineDietOrderDetail.setRetailLocalId(null);
            onlineDietOrderDetail.setPackageCode(isPackage ? UUID.randomUUID().toString() : null);
            onlineDietOrderDetail.setGroupId(null);
            onlineDietOrderDetail.setGroupName("正常菜品");
            onlineDietOrderDetail.setGroupType("normal");
            onlineDietOrderDetails.add(onlineDietOrderDetail);
        }
        onlineDietOrderDetailMapper.insertAll(onlineDietOrderDetails);
        onlineDietOrderInfo.setTotalAmount(totalAmount);
        onlineDietOrderInfo.setReceivedAmount(receivedAmount);
        onlineDietOrderInfo.setDiscountAmount(discountAmount);

        onlineDietOrderInfoMapper.update(onlineDietOrderInfo);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("orderInfo", onlineDietOrderInfo);
        data.put("orderDetails", onlineDietOrderDetails);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("保存订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取桌台信息
     *
     * @param obtainTableInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainTableInfo(ObtainTableInfoModel obtainTableInfoModel) {
        BigInteger tenantId = obtainTableInfoModel.getTenantId();
        BigInteger branchId = obtainTableInfoModel.getBranchId();
        String tableCode = obtainTableInfoModel.getTableCode();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("code", Constants.SQL_OPERATION_SYMBOL_EQUALS, tableCode);

        BranchTable branchTable = DatabaseHelper.find(BranchTable.class, searchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(branchTable);
        apiRest.setMessage("查询桌台信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取自助购订单信息
     *
     * @param obtainSelfHelpShoppingOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainSelfHelpShoppingOrder(ObtainSelfHelpShoppingOrderModel obtainSelfHelpShoppingOrderModel) {
        BigInteger tenantId = obtainSelfHelpShoppingOrderModel.getTenantId();
        BigInteger branchId = obtainSelfHelpShoppingOrderModel.getBranchId();
        String orderCode = obtainSelfHelpShoppingOrderModel.getOrderCode();

        SearchModel onlineDietOrderInfoSearchModel = new SearchModel(true);
        onlineDietOrderInfoSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        onlineDietOrderInfoSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        onlineDietOrderInfoSearchModel.addSearchCondition("order_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderCode);
        onlineDietOrderInfoSearchModel.addSearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_EQUALS, 8);
        OnlineDietOrderInfo onlineDietOrderInfo = onlineDietOrderInfoMapper.find(onlineDietOrderInfoSearchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");
        ValidateUtils.isTrue(onlineDietOrderInfo.getPayStatus() == 1, "该订单未付款，不能进行核销操作！");

        SearchModel onlineDietOrderDetailSearchModel = new SearchModel(true);
        onlineDietOrderDetailSearchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getId());
        List<OnlineDietOrderDetail> onlineDietOrderDetails = onlineDietOrderDetailMapper.findAll(onlineDietOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("orderInfo", onlineDietOrderInfo);
        data.put("orderDetails", onlineDietOrderDetails);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 核验自助购订单
     *
     * @param verifySelfHelpShoppingOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest verifySelfHelpShoppingOrder(VerifySelfHelpShoppingOrderModel verifySelfHelpShoppingOrderModel) {
        BigInteger tenantId = verifySelfHelpShoppingOrderModel.getTenantId();
        BigInteger branchId = verifySelfHelpShoppingOrderModel.getBranchId();
        String orderCode = verifySelfHelpShoppingOrderModel.getOrderCode();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("order_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderCode);
        OnlineDietOrderInfo onlineDietOrderInfo = onlineDietOrderInfoMapper.find(searchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        boolean isSuccess = true;
        String error = null;
        if (onlineDietOrderInfo.getPayStatus() != 1) {
            isSuccess = false;
            error = "只有已付款的订单才能进行核验操作！";
        }

        if (onlineDietOrderInfo.getOrderStatus() != 1) {
            isSuccess = false;
            error = "订单状态错误！";
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineDietOrderInfo);
        if (isSuccess) {
            onlineDietOrderInfo.setOrderStatus(20);
            onlineDietOrderInfo.setLastUpdateAt(new Date());
            onlineDietOrderInfoMapper.update(onlineDietOrderInfo);

            apiRest.setMessage("核验订单成功！");
            apiRest.setIsSuccess(true);
        } else {
            apiRest.setError(error);
            apiRest.setIsSuccess(false);
        }

        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listSelfHelpShoppingOrders(ListSelfHelpShoppingOrdersModel listSelfHelpShoppingOrdersModel) {
        BigInteger tenantId = listSelfHelpShoppingOrdersModel.getTenantId();
        BigInteger vipId = listSelfHelpShoppingOrdersModel.getVipId();
        int page = listSelfHelpShoppingOrdersModel.getPage();
        int rows = listSelfHelpShoppingOrdersModel.getRows();
        int type = listSelfHelpShoppingOrdersModel.getType();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId));
        searchConditions.add(new SearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId));
        searchConditions.add(new SearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_EQUALS, 8));
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));
        if (type == 2) {
            searchConditions.add(new SearchCondition("order_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1));
            searchConditions.add(new SearchCondition("pay_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));
        } else if (type == 3) {
            searchConditions.add(new SearchCondition("order_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1));
            searchConditions.add(new SearchCondition("pay_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1));
        }

        SearchModel countSearchModel = new SearchModel();
        countSearchModel.setSearchConditions(searchConditions);
        long count = onlineDietOrderInfoMapper.count(countSearchModel);

        List<OnlineDietOrderInfo> onlineDietOrderInfos = null;
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setOrderBy("create_at DESC");
            onlineDietOrderInfos = onlineDietOrderInfoMapper.findAllPaged(pagedSearchModel);
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(onlineDietOrderInfos)) {
            List<BigInteger> onlineDietOrderInfoIds = new ArrayList<BigInteger>();
            for (OnlineDietOrderInfo onlineDietOrderInfo : onlineDietOrderInfos) {
                onlineDietOrderInfoIds.add(onlineDietOrderInfo.getId());
            }
            SearchModel onlineDietOrderDetailSearchModel = new SearchModel(true);
            onlineDietOrderDetailSearchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_IN, onlineDietOrderInfoIds);
            List<OnlineDietOrderDetail> onlineDietOrderDetails = onlineDietOrderDetailMapper.findAll(onlineDietOrderDetailSearchModel);

            Map<BigInteger, List<OnlineDietOrderDetail>> onlineDietOrderDetailMap = new HashMap<BigInteger, List<OnlineDietOrderDetail>>();
            for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
                List<OnlineDietOrderDetail> onlineDietOrderDetailList = onlineDietOrderDetailMap.get(onlineDietOrderDetail.getDietOrderInfoId());
                if (CollectionUtils.isEmpty(onlineDietOrderDetailList)) {
                    onlineDietOrderDetailList = new ArrayList<OnlineDietOrderDetail>();
                    onlineDietOrderDetailMap.put(onlineDietOrderDetail.getDietOrderInfoId(), onlineDietOrderDetailList);
                }
                onlineDietOrderDetailList.add(onlineDietOrderDetail);
            }

            for (OnlineDietOrderInfo onlineDietOrderInfo : onlineDietOrderInfos) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("orderInfo", onlineDietOrderInfo);
                map.put("orderDetails", onlineDietOrderDetailMap.get(onlineDietOrderInfo.getId()));
                result.add(map);
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", result);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest cancelSelfHelpShoppingOrder(CancelSelfHelpShoppingOrderMode cancelSelfHelpShoppingOrderMode) {
        BigInteger tenantId = cancelSelfHelpShoppingOrderMode.getTenantId();
        BigInteger branchId = cancelSelfHelpShoppingOrderMode.getBranchId();
        BigInteger orderId = cancelSelfHelpShoppingOrderMode.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        searchModel.addSearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_EQUALS, 8);
        OnlineDietOrderInfo onlineDietOrderInfo = DatabaseHelper.find(OnlineDietOrderInfo.class, searchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");
        ValidateUtils.notNull(onlineDietOrderInfo.getPayStatus() == 0, "只有未付款的订单才能取消！");
        ValidateUtils.notNull(onlineDietOrderInfo.getOrderStatus() == 1, "只有已下单状态的订单才能取消！");

        onlineDietOrderInfo.setOrderStatus(21);
        DatabaseHelper.update(onlineDietOrderInfo);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("取消订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest writeSaleFlow(WriteSaleFlowModel writeSaleFlowModel) throws IOException {
        BigInteger dietOrderInfoId = writeSaleFlowModel.getDietOrderInfoId();
        SaleFlowUtils.writeSaleFlow(dietOrderInfoId);
        StockUtils.handleOnlineStock(dietOrderInfoId);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("写入流水成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest autoFinishOrder() throws IOException {
        Date currentDate = new Date();
        Date startTime = DateUtils.addHours(currentDate, -4);
        Date endTime = DateUtils.addHours(currentDate, -2);
        SearchModel searchModel = new SearchModel(true);
        searchModel.setWhereClause("last_update_at >= #{startTime} AND last_update_at  <= #{endTime} AND (((order_mode = 1 OR order_mode = 2) AND order_status = 2) OR ((order_mode = 4 OR order_mode = 5) AND order_status = 3)) AND pay_status = 1 AND pay_way != 3 AND pay_way != 5 AND is_deleted = 0");
        searchModel.addNamedParameter("startTime", startTime);
        searchModel.addNamedParameter("endTime", endTime);

        List<DietOrderInfo> dietOrderInfos = DatabaseHelper.findAll(DietOrderInfo.class, searchModel);
        for (DietOrderInfo dietOrderInfo : dietOrderInfos) {
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_YIPAISONG);
            dietOrderInfo.setLastUpdateAt(currentDate);
            DatabaseHelper.update(dietOrderInfo);
            SaleFlowUtils.writeSaleFlow(dietOrderInfo);
            StockUtils.handleOnlineStock(dietOrderInfo.getId());
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("自动完成订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 分页查询线上订单
     *
     * @param listOnlineOrdersModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listOnlineOrders(ListOnlineOrdersModel listOnlineOrdersModel) {
        BigInteger vipId = listOnlineOrdersModel.getVipId();
        int page = listOnlineOrdersModel.getPage();
        int rows = listOnlineOrdersModel.getRows();
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId));
        searchConditions.add(new SearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{1, 2, 4, 11}));
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        long count = onlineDietOrderInfoMapper.count(searchModel);

        List<OnlineDietOrderInfo> onlineDietOrderInfos = new ArrayList<OnlineDietOrderInfo>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setOrderBy("create_at DESC");
            onlineDietOrderInfos = onlineDietOrderInfoMapper.findAllPaged(pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", onlineDietOrderInfos);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询订单列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Autowired
    private ReportService reportService;

    /**
     * 分页查询线下订单
     *
     * @param listOfflineOrdersModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listOfflineOrders(ListOfflineOrdersModel listOfflineOrdersModel) {
        BigInteger tenantId = listOfflineOrdersModel.getTenantId();
        BigInteger branchId = listOfflineOrdersModel.getBranchId();
        BigInteger vipId = listOfflineOrdersModel.getVipId();
        int page = listOfflineOrdersModel.getPage();
        int rows = listOfflineOrdersModel.getRows();

        Map<String, String> saleFlowReportParameters = new HashMap<String, String>();
        saleFlowReportParameters.put("tenantId", tenantId.toString());
        saleFlowReportParameters.put("branchId", branchId.toString());
        saleFlowReportParameters.put("vipId", vipId.toString());
        saleFlowReportParameters.put("page", String.valueOf(page));
        saleFlowReportParameters.put("rows", String.valueOf(rows));

        ApiRest apiRest = reportService.saleFlowReport(saleFlowReportParameters);
        return apiRest;
    }

    /**
     * 查询线下订单明细
     *
     * @param offlineOrderDetailModel
     * @return
     */
    public ApiRest offlineOrderDetail(OfflineOrderDetailModel offlineOrderDetailModel) {
        BigInteger tenantId = offlineOrderDetailModel.getTenantId();
        BigInteger branchId = offlineOrderDetailModel.getBranchId();
        BigInteger vipId = offlineOrderDetailModel.getVipId();
        String saleCode = offlineOrderDetailModel.getSaleCode();
        Boolean isRefund = offlineOrderDetailModel.getIsRefund();
        Date checkoutAt = offlineOrderDetailModel.getCheckoutAt();
        BigDecimal totalAmount = offlineOrderDetailModel.getTotalAmount();
        BigDecimal discountAmount = offlineOrderDetailModel.getDiscountAmount();
        BigDecimal realAmount = offlineOrderDetailModel.getRealAmount();

        Map<String, String> saleGoodsReportParameters = new HashMap<String, String>();
        saleGoodsReportParameters.put("tenantId", tenantId.toString());
        saleGoodsReportParameters.put("branchId", branchId.toString());
        saleGoodsReportParameters.put("vipId", vipId.toString());
        saleGoodsReportParameters.put("rows", "200000");
        saleGoodsReportParameters.put("page", "1");
        saleGoodsReportParameters.put("saleCode", saleCode);

        Map<String, String> salePaymentReportParameters = new HashMap<String, String>();
        salePaymentReportParameters.put("tenantId", tenantId.toString());
        salePaymentReportParameters.put("branchId", branchId.toString());
        salePaymentReportParameters.put("vipId", vipId.toString());
        salePaymentReportParameters.put("rows", "200000");
        salePaymentReportParameters.put("page", "1");
        salePaymentReportParameters.put("saleCode", saleCode);

        ApiRest saleGoodsReportApiRest = reportService.saleGoodsReport(saleGoodsReportParameters);
        ValidateUtils.isTrue(saleGoodsReportApiRest.getIsSuccess(), saleGoodsReportApiRest.getError());

        ApiRest salePaymentReportApiRest = reportService.salePaymentReport(salePaymentReportParameters);
        ValidateUtils.isTrue(salePaymentReportApiRest.getIsSuccess(), salePaymentReportApiRest.getError());

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("saleGoodsReportData", saleGoodsReportApiRest.getData());
        data.put("salePaymentReportData", salePaymentReportApiRest.getData());
        data.put("saleCode", saleCode);
        data.put("isRefund", isRefund);
        data.put("checkoutAt", checkoutAt);
        data.put("totalAmount", totalAmount);
        data.put("discountAmount", discountAmount);
        data.put("realAmount", realAmount);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询线下订单明细成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
