package erp.chain.service.online;

import com.saas.common.util.Common;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.ShopGoods;
import erp.chain.domain.o2o.CardCoupons;
import erp.chain.domain.o2o.CardCouponsBook;
import erp.chain.domain.o2o.CardToVip;
import erp.chain.domain.o2o.VipBook;
import erp.chain.domain.online.*;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.online.IntegralMallGoodsMapper;
import erp.chain.model.online.integralmall.*;
import erp.chain.service.o2o.CardCouponsService;
import erp.chain.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * Created by liuyandong on 2018-10-25.
 */
@Service
public class IntegralMallService {
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private IntegralMallGoodsMapper integralMallGoodsMapper;
    @Autowired
    private CardCouponsService cardCouponsService;

    @Transactional(readOnly = true)
    public ApiRest listGoodsInfos(ListGoodsInfosModel listGoodsInfosModel) {
        BigInteger tenantId = listGoodsInfosModel.getTenantId();
        BigInteger vipId = listGoodsInfosModel.getVipId();
        Branch branch = obtainBranchInfo(tenantId);
        ValidateUtils.notNull(branch, "商户未开通积分商城！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, searchModel);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        List<IntegralMallGoods> integralMallGoodses = integralMallGoodsMapper.findAll(tenantId, branch.getId(), vipId);
        BigDecimal exchangeQuantity = integralMallGoodsMapper.obtainExchangeQuantity(tenantId, vipId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vipInfo", onlineVip);
        data.put("goodsInfos", integralMallGoodses);
        data.put("exchangeQuantity", exchangeQuantity);
        data.put("branchInfo", branch);
        data.put("currentTime", new Date());

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取商品信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveOrder(SaveOrderModel saveOrderModel) throws ParseException {
        BigInteger tenantId = saveOrderModel.getTenantId();
        BigInteger vipId = saveOrderModel.getVipId();
        BigInteger goodsId = saveOrderModel.getGoodsId();
        BigDecimal quantity = saveOrderModel.getQuantity();

        Branch branch = obtainBranchInfo(tenantId);
        ValidateUtils.notNull(branch, "商户未开通积分商城！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, searchModel);
        ValidateUtils.notNull(onlineVip, "会员不存在！");

        BigInteger branchId = branch.getId();
        Date date = new Date();
        long time = date.getTime();

        IntegralMallGoods integralMallGoods = integralMallGoodsMapper.find(tenantId, branchId, goodsId, vipId);
        ValidateUtils.notNull(integralMallGoods, "商品不存在！");
        ValidateUtils.isTrue(time >= integralMallGoods.getStartDate().getTime() && time <= integralMallGoods.getEndDate().getTime(), "商品不在生效日期内！");

        if (integralMallGoods.getIsLimit() == 1) {
            ValidateUtils.isTrue(integralMallGoods.getExchangeQuantity().add(quantity).compareTo(integralMallGoods.getLimitQuantity()) <= 0, "超过限购数量！");
        }

        ValidateUtils.isTrue(integralMallGoods.getSurplusQuantity().compareTo(quantity) >= 0, "换购数量超过剩余可兑数量！");

        BigDecimal useScore = integralMallGoods.getUseScore();
        BigDecimal totalScore = useScore.multiply(quantity);
        BigDecimal scorePay = BigDecimal.ZERO;

        BigDecimal remainingScore = onlineVip.getRemainingScore();
        if (remainingScore == null) {
            remainingScore = BigDecimal.ZERO;
        }
        ValidateUtils.isTrue(remainingScore.compareTo(totalScore) >= 0, "积分余额不足！");

        onlineVip.setRemainingScore(remainingScore.subtract(totalScore));

        BigDecimal overscore = onlineVip.getOverscore();
        if (overscore == null) {
            overscore = BigDecimal.ZERO;
        }
        onlineVip.setOverscore(overscore.add(totalScore));
        DatabaseHelper.update(onlineVip);

        String orderCode = "JFSC" + branchId + Common.getToday().substring(2) + serialNumberCreatorMapper.getToday("JFSC", 4);

        BigDecimal scoreTotalAmount = useScore.multiply(quantity);
        BigDecimal scoreReceivedAmount = useScore.multiply(quantity);
        BigDecimal scoreDiscountAmount = BigDecimal.ZERO;

        OnlineDietOrderInfo onlineDietOrderInfo = new OnlineDietOrderInfo();
        onlineDietOrderInfo.setTenantId(tenantId);
        onlineDietOrderInfo.setBranchId(branchId);
        onlineDietOrderInfo.setBranchName(branch.getName());
        onlineDietOrderInfo.setOrderMode(integralMallGoods.getIsTicket() == 1 ? 9 : 10);
        onlineDietOrderInfo.setOrderCode(orderCode);
        onlineDietOrderInfo.setVipId(vipId);

        int orderStatus = 0;
        boolean isCoupon = integralMallGoods.getIsTicket() == 1;
        if (isCoupon) {
            orderStatus = 23;
        } else {
            orderStatus = 22;
        }

        onlineDietOrderInfo.setOrderStatus(orderStatus);
        onlineDietOrderInfo.setEatStatus(0);
        onlineDietOrderInfo.setPayStatus(1);
        onlineDietOrderInfo.setTotalAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setReceivedAmount(BigDecimal.ZERO);
        onlineDietOrderInfo.setDiscountAmount(BigDecimal.ZERO);

        onlineDietOrderInfo.setScoreTotalAmount(scoreTotalAmount);
        onlineDietOrderInfo.setScoreReceivedAmount(scoreReceivedAmount);
        onlineDietOrderInfo.setScoreDiscountAmount(scoreDiscountAmount);

        onlineDietOrderInfo.setUseScore(totalScore.intValue());
        onlineDietOrderInfo.setAmount(quantity.intValue());
        onlineDietOrderInfo.setIsUsePrivilege(false);
        onlineDietOrderInfo.setPayAt(date);
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
        onlineDietOrderInfo.setScorePay(scorePay);
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
        DatabaseHelper.insert(onlineDietOrderInfo);

        BigInteger dietOrderInfoId = onlineDietOrderInfo.getId();

        OnlineDietOrderDetail onlineDietOrderDetail = new OnlineDietOrderDetail();
        onlineDietOrderDetail.setDietOrderInfoId(dietOrderInfoId);
        onlineDietOrderDetail.setIsPackage(false);
        onlineDietOrderDetail.setGoodsId(goodsId);
        onlineDietOrderDetail.setGoodsName(integralMallGoods.getGoodsName());
        onlineDietOrderDetail.setPackageId(null);
        onlineDietOrderDetail.setPackageName(null);
        onlineDietOrderDetail.setQuantity(quantity);

        onlineDietOrderDetail.setSalePrice(BigDecimal.ZERO);
        onlineDietOrderDetail.setSalePriceActual(BigDecimal.ZERO);
        onlineDietOrderDetail.setTotalAmount(BigDecimal.ZERO);
        onlineDietOrderDetail.setReceivedAmount(BigDecimal.ZERO);

        onlineDietOrderDetail.setScoreSalePrice(useScore);
        onlineDietOrderDetail.setScoreSalePriceActual(useScore);
        onlineDietOrderDetail.setScoreTotalAmount(useScore.multiply(quantity));
        onlineDietOrderDetail.setScoreDiscountAmount(BigDecimal.ZERO);

        onlineDietOrderDetail.setIsFreeOfCharge(false);
        onlineDietOrderDetail.setIsRefund(false);
        onlineDietOrderDetail.setTaste(null);
        onlineDietOrderDetail.setTasteName(null);
        onlineDietOrderDetail.setSize(null);
        onlineDietOrderDetail.setSizeName(null);
        onlineDietOrderDetail.setCreateAt(date);
        onlineDietOrderDetail.setLastUpdateAt(date);
        onlineDietOrderDetail.setTenantId(tenantId);
        onlineDietOrderDetail.setBranchId(branchId);

        if (isCoupon) {
            CardCoupons cardCoupons = DatabaseHelper.find(CardCoupons.class, integralMallGoods.getTicketId());
            Map<String, String> map3 = new HashMap<>();
            map3.put("tenantId", tenantId.toString());
            map3.put("cardId", cardCoupons.getId().toString());
            map3.put("vipId", onlineVip.getId().toString());
            map3.put("channel", "score");
            map3.put("orderId", onlineDietOrderInfo.getId().toString());
            map3.put("orderCode", onlineDietOrderInfo.getOrderCode());
            if(branch!=null){
                map3.put("branchId", branch.getId().toString());
            }
            cardCouponsService.sendCardToVip(map3);
            /*Map params = new HashMap();
            params.put("vipId", onlineVip.getId().toString());
            params.put("cardId", cardCoupons.getId().toString());
            params.put("cardName", cardCoupons.getCardName());
            params.put("tenantId", tenantId.toString());
            params.put("branchIds", cardCoupons.getLimitBranchIds());
            params.put("createBranchId", branchId.toString());
            ApiRest apiRest = cardCouponsService.sendVipCardForScoreMall(params);
            ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getMessage());*/
        }

        List<OnlineDietOrderDetail> onlineDietOrderDetails = new ArrayList<OnlineDietOrderDetail>();
        onlineDietOrderDetails.add(onlineDietOrderDetail);
        DatabaseHelper.insertAll(onlineDietOrderDetails);

        integralMallGoodsMapper.updateAlreadyChangeQuantity(tenantId, branchId, goodsId, quantity);
        integralMallGoodsMapper.updateSurplusQuantity(tenantId, branchId, goodsId, quantity);

        SearchModel vipExchangeHistorySearchModel = new SearchModel(true);
        vipExchangeHistorySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        vipExchangeHistorySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        vipExchangeHistorySearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, goodsId);
        vipExchangeHistorySearchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        VipExchangeHistory vipExchangeHistory = DatabaseHelper.find(VipExchangeHistory.class, vipExchangeHistorySearchModel);
        if (vipExchangeHistory == null) {
            vipExchangeHistory = new VipExchangeHistory();
            vipExchangeHistory.setTenantId(tenantId);
            vipExchangeHistory.setBranchId(branchId);
            vipExchangeHistory.setGoodsId(goodsId);
            vipExchangeHistory.setVipId(vipId);
            vipExchangeHistory.setExchangeQuantity(quantity);
            vipExchangeHistory.setCreateAt(date);
            vipExchangeHistory.setLastUpdateAt(date);
            DatabaseHelper.insert(vipExchangeHistory);
        } else {
            vipExchangeHistory.setExchangeQuantity(vipExchangeHistory.getExchangeQuantity().add(quantity));
            vipExchangeHistory.setLastUpdateAt(date);
            DatabaseHelper.update(vipExchangeHistory);
        }

        VipBook vipBook = new VipBook();
        vipBook.setTenantId(tenantId);
        vipBook.setBranchId(branchId);
        vipBook.setBookType(2);
        vipBook.setOperateAt(date);
        vipBook.setVipCode(vipBook.getVipCode());
        vipBook.setPaymentCode(orderCode);
        vipBook.setTotalScore(onlineVip.getRemainingScore().add(totalScore));
        vipBook.setVipScore(totalScore);
        vipBook.setSumConsume(scoreReceivedAmount);
        vipBook.setTotal(scoreReceivedAmount);
        vipBook.setRealTotal(scoreReceivedAmount);
        vipBook.setVipId(vipId);
        vipBook.setStoreFrom("1");
        vipBook.setCreateAt(date);
        vipBook.setLastUpdateAt(date);

        DatabaseHelper.insert(vipBook);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("orderInfo", onlineDietOrderInfo);
        data.put("orderDetails", onlineDietOrderDetails);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("保存订单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainBranchInfo(ObtainBranchInfoModel obtainBranchInfoModel) {
        BigInteger tenantId = obtainBranchInfoModel.getTenantId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 4);

        Branch branch = DatabaseHelper.find(Branch.class, searchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setMessage("获取门店信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private Branch obtainBranchInfo(BigInteger tenantId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 4);

        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        return branch;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainGoodsInfo(ObtainGoodsInfoModel obtainGoodsInfoModel) {
        BigInteger tenantId = obtainGoodsInfoModel.getTenantId();
        BigInteger goodsId = obtainGoodsInfoModel.getGoodsId();
        BigInteger vipId = obtainGoodsInfoModel.getVipId();

        Branch branch = obtainBranchInfo(tenantId);
        ValidateUtils.notNull(branch, "商户未开通积分商城！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        OnlineVip onlineVip = DatabaseHelper.find(OnlineVip.class, searchModel);
        Validate.notNull(onlineVip, "会员信息不存在！");

        IntegralMallGoods integralMallGoods = integralMallGoodsMapper.find(tenantId, branch.getId(), goodsId, vipId);
        Validate.notNull(integralMallGoods, "商品不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vipInfo", onlineVip);
        data.put("goodsInfo", integralMallGoods);
        data.put("currentTime", new Date());

        if (integralMallGoods.getIsTicket() == 1) {
            CardCoupons cardCoupons = DatabaseHelper.find(CardCoupons.class, integralMallGoods.getTicketId());
            data.put("cardCoupons", cardCoupons);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取商品信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listOrderInfos(ListOrderInfosModel listOrderInfosModel) {
        BigInteger tenantId = listOrderInfosModel.getTenantId();
        BigInteger vipId = listOrderInfosModel.getVipId();
        int page = listOrderInfosModel.getPage();
        int rows = listOrderInfosModel.getRows();
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("is_deleted", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0));
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId));
        searchConditions.add(new SearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipId));
        searchConditions.add(new SearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{9, 10}));

        Map<String, Object> data = listOrderInfos(searchConditions, page, rows);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data.get("rows"));
        apiRest.setMessage("查询订单列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private Map<String, Object> listOrderInfos(List<SearchCondition> searchConditions, int page, int rows) {
        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        long count = DatabaseHelper.count(OnlineDietOrderInfo.class, searchModel);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setOrderBy("pay_at DESC");
            List<OnlineDietOrderInfo> onlineDietOrderInfos = DatabaseHelper.findAllPaged(OnlineDietOrderInfo.class, pagedSearchModel);

            if (CollectionUtils.isNotEmpty(onlineDietOrderInfos)) {
                List<BigInteger> orderIds = new ArrayList<>();
                for (OnlineDietOrderInfo onlineDietOrderInfo : onlineDietOrderInfos) {
                    orderIds.add(onlineDietOrderInfo.getId());
                }

                SearchModel onlineDietOrderDetailSearchModel = new SearchModel(true);
                onlineDietOrderDetailSearchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_IN, orderIds);

                List<OnlineDietOrderDetail> onlineDietOrderDetails = DatabaseHelper.findAll(OnlineDietOrderDetail.class, onlineDietOrderDetailSearchModel);
                Map<BigInteger, List<OnlineDietOrderDetail>> onlineDietOrderDetailsMap = new HashMap<BigInteger, List<OnlineDietOrderDetail>>();
                for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
                    BigInteger orderId = onlineDietOrderDetail.getDietOrderInfoId();
                    List<OnlineDietOrderDetail> onlineDietOrderDetailList = onlineDietOrderDetailsMap.get(orderId);
                    if (CollectionUtils.isEmpty(onlineDietOrderDetailList)) {
                        onlineDietOrderDetailList = new ArrayList<OnlineDietOrderDetail>();
                        onlineDietOrderDetailsMap.put(orderId, onlineDietOrderDetailList);
                    }
                    onlineDietOrderDetailList.add(onlineDietOrderDetail);
                }

                for (OnlineDietOrderInfo onlineDietOrderInfo : onlineDietOrderInfos) {
                    Map<String, Object> orderInfo = new HashMap<String, Object>();
                    orderInfo.put("orderInfo", onlineDietOrderInfo);
                    orderInfo.put("orderDetails", onlineDietOrderDetailsMap.get(onlineDietOrderInfo.getId()));
                    data.add(orderInfo);
                }
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", count);
        map.put("rows", data);

        return map;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainOrderInfo(ObtainOrderInfoModel obtainOrderInfoModel) {
        BigInteger tenantId = obtainOrderInfoModel.getTenantId();
        BigInteger orderId = obtainOrderInfoModel.getOrderId();

        Branch branch = obtainBranchInfo(tenantId);
        ValidateUtils.notNull(branch, "未开通积分商城！");
        BigInteger branchId = branch.getId();

        SearchModel onlineDietOrderInfoSearchModel = new SearchModel(true);
        onlineDietOrderInfoSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        onlineDietOrderInfoSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        onlineDietOrderInfoSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        onlineDietOrderInfoSearchModel.addSearchCondition("order_mode", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{9, 10});

        OnlineDietOrderInfo onlineDietOrderInfo = DatabaseHelper.find(OnlineDietOrderInfo.class, onlineDietOrderInfoSearchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        SearchModel onlineDietOrderDetailSearchModel = new SearchModel(true);
        onlineDietOrderDetailSearchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getId());
        OnlineDietOrderDetail onlineDietOrderDetail = DatabaseHelper.find(OnlineDietOrderDetail.class, onlineDietOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        IntegralMallGoods integralMallGoods = integralMallGoodsMapper.find(tenantId, branchId, onlineDietOrderDetail.getGoodsId(), onlineDietOrderInfo.getVipId());
        if (integralMallGoods.getIsTicket() == 1) {
            CardCoupons cardCoupons = DatabaseHelper.find(CardCoupons.class, integralMallGoods.getTicketId());
            data.put("cardCoupons", cardCoupons);
            SearchModel bookSearch=new SearchModel(true);
            bookSearch.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getVipId());
            bookSearch.addSearchCondition("card_coupons_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardCoupons.getId());
            bookSearch.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getId());
            bookSearch.addSearchCondition("channel", Constants.SQL_OPERATION_SYMBOL_EQUALS, "score");
            bookSearch.addSearchCondition("state", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
            bookSearch.addSearchCondition("business", Constants.SQL_OPERATION_SYMBOL_EQUALS, "coupons_get");
            CardCouponsBook cardCouponsBook=DatabaseHelper.find(CardCouponsBook.class,bookSearch);
            ValidateUtils.notNull(cardCouponsBook, "未查询到领券记录！");

            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("vip_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getVipId());
            searchModel.addSearchCondition("card_coupons_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardCoupons.getId());
            searchModel.addSearchCondition("card_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, cardCouponsBook.getCardCode());
            CardToVip cardToVip = DatabaseHelper.find(CardToVip.class, searchModel);
            data.put("cardToVip", cardToVip);
        }

        data.put("orderInfo", onlineDietOrderInfo);
        data.put("orderDetail", onlineDietOrderDetail);
        data.put("goodsInfo", integralMallGoods);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取订单信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listOrders(ListOrdersModel listOrdersModel) {
        BigInteger tenantId = listOrdersModel.getTenantId();
        Integer orderMode = listOrdersModel.getOrderMode();
        Integer orderStatus = listOrdersModel.getOrderStatus();
        String startDate = listOrdersModel.getStartDate();
        String endDate = listOrdersModel.getEndDate();
        String orderCode = listOrdersModel.getOrderCode();
        int page = listOrdersModel.getPage();
        int rows = listOrdersModel.getRows();

        long total = integralMallGoodsMapper.listOrdersCount(tenantId, orderMode, orderStatus, startDate, endDate, orderCode);

        List<Map<String, Object>> details;
        if (total > 0) {
            details = integralMallGoodsMapper.listOrders(tenantId, orderMode, (page - 1) * rows, rows, orderStatus, startDate, endDate, orderCode);
            total = integralMallGoodsMapper.listOrdersCount(tenantId, orderMode, orderStatus, startDate, endDate, orderCode);
        } else {
            details = new ArrayList<Map<String, Object>>();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("rows", details);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询订单列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest verifyOrder(VerifyOrderModel verifyOrderModel) {
        BigInteger tenantId = verifyOrderModel.getTenantId();
        BigInteger orderId = verifyOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        OnlineDietOrderInfo onlineDietOrderInfo = DatabaseHelper.find(OnlineDietOrderInfo.class, searchModel);
        ValidateUtils.notNull(onlineDietOrderInfo, "订单不存在！");

        onlineDietOrderInfo.setOrderStatus(23);
        onlineDietOrderInfo.setLastUpdateAt(new Date());
        DatabaseHelper.update(onlineDietOrderInfo);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("核销成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest settingGoodsList(SettingGoodsModel settingGoodsModel) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = settingGoodsModel.getTenantId();
        String startDate = settingGoodsModel.getStartDate();
        String endDate = settingGoodsModel.getEndDate();
        Integer goodsStatus = settingGoodsModel.getGoodsStatus();
        Integer isTicket = settingGoodsModel.getIsTicket();

        int startNum = 0;
        int endNum = 9999;
        if (settingGoodsModel.getPage() != null && settingGoodsModel.getRows() != null && !"".equals(settingGoodsModel.getRows())) {
            startNum = (Integer.parseInt(settingGoodsModel.getPage().toString()) - 1) * Integer.parseInt(settingGoodsModel.getRows().toString());
            endNum = Integer.parseInt(settingGoodsModel.getRows().toString()) + startNum;

        }

        List<ShopGoods> list;
        List resultList = new ArrayList();
        int total = integralMallGoodsMapper.settingGoodsListCount(tenantId, startDate, endDate, goodsStatus, isTicket);

        if (total > 0) {
            list = integralMallGoodsMapper.settingGoodsList(tenantId, startDate, endDate, goodsStatus, isTicket);
            for (int i = 0; i < list.size(); i++) {
                ShopGoods shopGoods = list.get(i);
                Date start = shopGoods.getStartDate();
                Date end = shopGoods.getEndDate();
                Date nowDate = new Date();
                if (start.before(nowDate) && end.after(nowDate)) {
                    shopGoods.setGoodsStatus(1);
                    if (settingGoodsModel.getGoodsStatus() != null && settingGoodsModel.getGoodsStatus() == 1) {
                        resultList.add(shopGoods);
                    }
                } else if (start.after(nowDate)) {
                    shopGoods.setGoodsStatus(0);
                    if (settingGoodsModel.getGoodsStatus() != null && settingGoodsModel.getGoodsStatus() == 0) {
                        resultList.add(shopGoods);
                    }
                } else {
                    shopGoods.setGoodsStatus(2);
                    if (settingGoodsModel.getGoodsStatus() != null && settingGoodsModel.getGoodsStatus() == 2) {
                        resultList.add(shopGoods);
                    }
                }
            }
        } else {
            list = new ArrayList();
        }
        if (endNum > (settingGoodsModel.getGoodsStatus() == null ? total : resultList.size())) {
            endNum = settingGoodsModel.getGoodsStatus() == null ? total : resultList.size();
        }
        Map map = new HashMap();
        map.put("rows", settingGoodsModel.getGoodsStatus() == null ? list.subList(startNum, endNum) : resultList.subList(startNum, endNum));
        map.put("total", settingGoodsModel.getGoodsStatus() == null ? total : resultList.size());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询上架商品列表成功");
        apiRest.setData(map);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest updateGoodsSetting(SettingGoodsModel settingGoodsModel) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = settingGoodsModel.getTenantId();
        List<Map> goodsList = settingGoodsModel.getGoodsList();
        for (int i = 0; i < goodsList.size(); i++) {
            Map goods = goodsList.get(i);
            BigInteger id = BigInteger.valueOf(Long.valueOf(goods.get("id").toString().substring(0, goods.get("id").toString().indexOf("."))));
            BigDecimal changeQuantity = BigDecimal.valueOf(Double.valueOf(goods.get("changeQuantity").toString()));
            int j = integralMallGoodsMapper.updateGoodsSetting(tenantId, id, changeQuantity, changeQuantity);
            if (j < 0) {
                apiRest.setIsSuccess(false);
                apiRest.setError("修改商品上架设置失败");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改商品上架设置成功");
        return apiRest;
    }
}
