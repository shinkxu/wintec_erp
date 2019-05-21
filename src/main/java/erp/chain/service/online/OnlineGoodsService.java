package erp.chain.service.online;

import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.DietPromotion;
import erp.chain.domain.DietPromotionTotalReduce;
import erp.chain.domain.PackageGroup;
import erp.chain.domain.online.OnlineGoods;
import erp.chain.domain.online.OnlineGoodsPromotion;
import erp.chain.domain.online.OnlineVipType;
import erp.chain.mapper.*;
import erp.chain.mapper.online.OnlineGoodsMapper;
import erp.chain.mapper.online.OnlineGoodsPromotionMapper;
import erp.chain.mapper.online.OnlineVipMapper;
import erp.chain.model.online.onlinegoods.*;
import erp.chain.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-11.
 */
@Service
public class OnlineGoodsService {
    @Autowired
    private OnlineGoodsMapper onlineGoodsMapper;
    @Autowired
    private PackageGroupMapper packageGroupMapper;
    @Autowired
    private OnlineGoodsPromotionMapper onlineGoodsPromotionMapper;
    @Autowired
    private DietPromotionTotalReduceMapper dietPromotionTotalReduceMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private OnlineVipMapper onlineVipMapper;

    /**
     * 查询带有角标的商品
     *
     * @param listCornerGoodsInfosModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listCornerGoodsInfos(ListCornerGoodsInfosModel listCornerGoodsInfosModel) {
        BigInteger tenantId = listCornerGoodsInfosModel.getTenantId();
        BigInteger branchId = listCornerGoodsInfosModel.getBranchId();
        Integer menuType = listCornerGoodsInfosModel.getMenuType();
        Integer scope = listCornerGoodsInfosModel.getScope();
        Integer forCustomerType = listCornerGoodsInfosModel.getForCustomerType();

        List<Map<String, Object>> goodsInfos = listGoodsInfos(tenantId, branchId, menuType);

        List<OnlineGoodsPromotion> onlineGoodsPromotions = onlineGoodsPromotionMapper.findAllOnlineGoodsPromotions(tenantId, branchId, scope, forCustomerType);
        Map<String, OnlineGoodsPromotion> onlineGoodsPromotionMap = new HashMap<String, OnlineGoodsPromotion>();
        for (OnlineGoodsPromotion onlineGoodsPromotion : onlineGoodsPromotions) {
            onlineGoodsPromotionMap.put(onlineGoodsPromotion.getGoodsId().toString(), onlineGoodsPromotion);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("goodsInfos", goodsInfos);
        data.put("goodsPromotions", onlineGoodsPromotionMap);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询带有角标的商品信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询商品列表
     *
     * @param listGoodsInfosModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listGoodsInfos(ListGoodsInfosModel listGoodsInfosModel) {
        BigInteger tenantId = listGoodsInfosModel.getTenantId();
        BigInteger branchId = listGoodsInfosModel.getBranchId();
        int menuType = listGoodsInfosModel.getMenuType();

        List<Map<String, Object>> goodsInfos = listGoodsInfos(tenantId, branchId, menuType);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(goodsInfos);
        apiRest.setMessage("查询商品列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private List<Map<String, Object>> listGoodsInfos(BigInteger tenantId, BigInteger branchId, int menuType) {
        Map<String, Object> effectiveMenu = onlineGoodsMapper.findEffectiveMenu(tenantId, branchId, menuType);
        BigInteger menuId = BigInteger.valueOf(MapUtils.getLongValue(effectiveMenu, "menuId"));

        List<Map<String, Object>> goodsInfos = null;
        if (menuId.compareTo(BigInteger.ZERO) == 0) {
            goodsInfos = new ArrayList<Map<String, Object>>();
        } else {
            goodsInfos = onlineGoodsMapper.listGoodsInfos(tenantId, branchId, menuId, null);
        }

        List<BigInteger> packageIds = new ArrayList<BigInteger>();
        for (Map<String, Object> goodsInfo : goodsInfos) {
            int goodsType = MapUtils.getIntValue(goodsInfo, "goodsType");
            if (goodsType == 2) {
                packageIds.add(BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id")));
            }
        }

        Map<BigInteger, List<Map<String, Object>>> groupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        Map<BigInteger, String> packDetailMap = new HashMap<BigInteger, String>();
        if (CollectionUtils.isNotEmpty(packageIds)) {
            List<Map<String, Object>> packageInfos = onlineGoodsMapper.findPackageInfos(packageIds);
            Map<BigInteger, List<Map<String, Object>>> packageInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            for (Map<String, Object> packageInfo : packageInfos) {
                BigInteger packageGroupId = BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageGroupId"));
                packageInfo.remove("packageGroupId");
                List<Map<String, Object>> packageInfoList = packageInfoMap.get(packageGroupId);
                if (CollectionUtils.isEmpty(packageInfoList)) {
                    packageInfoList = new ArrayList<Map<String, Object>>();
                    packageInfoMap.put(packageGroupId, packageInfoList);
                }
                packageInfoList.add(packageInfo);
            }

            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("package_id", Constants.SQL_OPERATION_SYMBOL_IN, packageIds);
            List<PackageGroup> packageGroups = packageGroupMapper.findAll(searchModel);
            for (PackageGroup packageGroup : packageGroups) {
                BigInteger packageId = packageGroup.getPackageId();
                List<Map<String, Object>> groups = groupMap.get(packageId);
                if (CollectionUtils.isEmpty(groups)) {
                    groups = new ArrayList<Map<String, Object>>();
                    groupMap.put(packageGroup.getPackageId(), groups);
                }
                List<Map<String, Object>> groupGoods = packageInfoMap.get(packageGroup.getId());
                StringBuilder packDetail = new StringBuilder();
                if (packageGroup.isMain()) {
                    for (Map<String, Object> goodsInfo : groupGoods) {
                        String standardName = MapUtils.getString(goodsInfo, "standardName");
                        String goodsName = MapUtils.getString(goodsInfo, "goodsName");
                        packDetail.append(StringUtils.isNotBlank(standardName) ? goodsName + "-" + standardName : goodsName);
                        packDetail.append("x");
                        packDetail.append(goodsInfo.get("quantity"));
                        packDetail.append(" ");
                    }
                    packDetail.deleteCharAt(packDetail.length() - 1);
                    packDetailMap.put(packageId, packDetail.toString());
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("group", packageGroup);
                map.put("groupGoods", groupGoods);
                map.put("goodsTotal", groupGoods.size());
                groups.add(map);
            }
        }

        for (Map<String, Object> goodsInfo : goodsInfos) {
            int goodsType = MapUtils.getIntValue(goodsInfo, "goodsType");
            if (goodsType == 2) {
                BigInteger packageId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id"));
                goodsInfo.put("groups", groupMap.get(packageId));
                goodsInfo.put("packDetail", packDetailMap.get(packageId));
            }
        }
        return goodsInfos;
    }

    /**
     * 查询所有活动
     *
     * @param listGoodsPromotionModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listGoodsPromotions(ListGoodsPromotionModel listGoodsPromotionModel) throws ParseException {
        BigInteger tenantId = listGoodsPromotionModel.getTenantId();
        BigInteger branchId = listGoodsPromotionModel.getBranchId();
        Integer scope = listGoodsPromotionModel.getScope();
        Integer forCustomerType = listGoodsPromotionModel.getForCustomerType();

        List<OnlineGoodsPromotion> onlineGoodsPromotions = onlineGoodsPromotionMapper.findAllOnlineGoodsPromotions(tenantId, branchId, scope, forCustomerType);

        Map<BigInteger, OnlineGoodsPromotion> onlineGoodsPromotionMap = new HashMap<BigInteger, OnlineGoodsPromotion>();
        for (OnlineGoodsPromotion onlineGoodsPromotion : onlineGoodsPromotions) {
            onlineGoodsPromotionMap.put(onlineGoodsPromotion.getGoodsId(), onlineGoodsPromotion);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(onlineGoodsPromotionMap);
        apiRest.setMessage("查询促销活动成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询生效的整单优惠活动
     *
     * @param findEffectiveDietPromotionTotalReduceModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest findEffectiveDietPromotionTotalReduce(FindEffectiveDietPromotionTotalReduceModel findEffectiveDietPromotionTotalReduceModel) {
        BigInteger tenantId = findEffectiveDietPromotionTotalReduceModel.getTenantId();
        BigInteger branchId = findEffectiveDietPromotionTotalReduceModel.getBranchId();
        Integer scope = findEffectiveDietPromotionTotalReduceModel.getScope();
        Integer forCustomerType = findEffectiveDietPromotionTotalReduceModel.getForCustomerType();
        BigDecimal totalAmount = findEffectiveDietPromotionTotalReduceModel.getTotalAmount();
        DietPromotion dietPromotion = onlineGoodsPromotionMapper.findEffectiveDietPromotionTotalReduce(tenantId, branchId, scope, forCustomerType);

        DietPromotionTotalReduce effectiveDietPromotionTotalReduce = null;
        if (dietPromotion != null) {
            DietPromotionTotalReduce dietPromotionTotalReduce = dietPromotionTotalReduceMapper.findByDietPromotionId(dietPromotion.getId());
            if (dietPromotionTotalReduce.getSatisfy().compareTo(totalAmount) <= 0) {
                effectiveDietPromotionTotalReduce = dietPromotionTotalReduce;
            }
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(effectiveDietPromotionTotalReduce);
        apiRest.setMessage("查询生效的整单优惠活动成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询菜牌信息
     *
     * @param listMenuInfosModel
     * @return
     */
    @Transactional(readOnly = true)
    public String listMenuInfos(ListMenuInfosModel listMenuInfosModel) {
        BigInteger tenantId = listMenuInfosModel.getTenantId();
        BigInteger branchId = listMenuInfosModel.getBranchId();
        int menuType = listMenuInfosModel.getMenuType();
        int scope = listMenuInfosModel.getScope();
        int forCustomerType = listMenuInfosModel.getForCustomerType();
        BigInteger vipTypeId = listMenuInfosModel.getVipTypeId();

        OnlineVipType onlineVipType = DatabaseHelper.find(OnlineVipType.class, vipTypeId);
        ValidateUtils.notNull(onlineVipType, "会员类型不存在！");

        List<Map<String, Object>> goodsInfos = listGoodsInfos(tenantId, branchId, menuType);

        Map<BigInteger, Map<String, Object>> goodsInfoMap = new HashMap<BigInteger, Map<String, Object>>();
        Map<BigInteger, List<Map<String, Object>>> categoryIdAndGoodsInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        Map<BigInteger, List<Map<String, Object>>> specificationGoodsInfos = new HashMap<BigInteger, List<Map<String, Object>>>();
        Map<BigInteger, String> categoryInfo = new HashMap<BigInteger, String>();

        List<BigInteger> parentIds = new ArrayList<BigInteger>();
        for (Map<String, Object> goodsInfo : goodsInfos) {
            BigInteger parentId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "parentId"));
            if (parentId.compareTo(BigInteger.ZERO) == 0) {
                parentIds.add(BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id")));
            }
        }

        for (Map<String, Object> goodsInfo : goodsInfos) {
            BigInteger categoryId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "categoryId"));
            BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id"));
            BigInteger parentId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "parentId"));
            goodsInfoMap.put(goodsId, goodsInfo);
            if (parentId.compareTo(BigInteger.ZERO) != 0 && parentIds.contains(parentId)) {
                List<Map<String, Object>> specificationGoodsInfoList = specificationGoodsInfos.get(parentId);
                if (CollectionUtils.isEmpty(specificationGoodsInfoList)) {
                    specificationGoodsInfoList = new ArrayList<Map<String, Object>>();
                    specificationGoodsInfos.put(parentId, specificationGoodsInfoList);
                }
                specificationGoodsInfoList.add(goodsInfo);
                continue;
            }

            if (parentId.compareTo(BigInteger.ZERO) != 0) {
                goodsInfo.put("parentId", null);
            }

            List<Map<String, Object>> goodsInfoList = categoryIdAndGoodsInfoMap.get(categoryId);
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                goodsInfoList = new ArrayList<Map<String, Object>>();
                categoryIdAndGoodsInfoMap.put(categoryId, goodsInfoList);
            }
            goodsInfoList.add(goodsInfo);
            categoryInfo.put(categoryId, MapUtils.getString(goodsInfo, "categoryName"));
        }

        for (Map.Entry<BigInteger, List<Map<String, Object>>> entry : specificationGoodsInfos.entrySet()) {
            entry.getValue().add(goodsInfoMap.get(entry.getKey()));
        }

        List<Map<String, Object>> categoryInfos = new ArrayList<Map<String, Object>>();
        for (Map.Entry<BigInteger, String> entry : categoryInfo.entrySet()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", entry.getKey());
            map.put("name", entry.getValue());
            categoryInfos.add(map);
        }

        List<OnlineGoodsPromotion> onlineGoodsPromotions = onlineGoodsPromotionMapper.findAllOnlineGoodsPromotions(tenantId, branchId, scope, forCustomerType);
        Map<BigInteger, OnlineGoodsPromotion> onlineGoodsPromotionMap = new HashMap<BigInteger, OnlineGoodsPromotion>();

        DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
        DecimalFormat decimalFormat2 = new DecimalFormat("0.00");
        for (OnlineGoodsPromotion onlineGoodsPromotion : onlineGoodsPromotions) {
            int promotionType = onlineGoodsPromotion.getPromotionType();
            String promotionDescription = null;
            if (promotionType == 4) {
                Map<String, Object> buyGoodsInfo = goodsInfoMap.get(onlineGoodsPromotion.getBuyGoodsId());
                Map<String, Object> giveGoodsInfo = goodsInfoMap.get(onlineGoodsPromotion.getGiveGoodsId());
                if (buyGoodsInfo == null || giveGoodsInfo == null) {
                    continue;
                }
                promotionDescription = "买" + onlineGoodsPromotion.getBuyNum().intValue() + "份 " + buyGoodsInfo.get("goodsName") + " 送" + onlineGoodsPromotion.getGiveNum().intValue() + "份 " + giveGoodsInfo.get("goodsName") + "！";
            } else if (promotionType == 6) {
                promotionDescription = "第" + (onlineGoodsPromotion.getQuantity().add(BigDecimal.ONE)).intValue() + "件" + decimalFormat1.format(onlineGoodsPromotion.getDiscount().divide(BigDecimal.TEN)) + "折！";
            } else if (promotionType == 7) {
                promotionDescription = "满" + decimalFormat2.format(onlineGoodsPromotion.getSatisfy()) + "元减" + decimalFormat2.format(onlineGoodsPromotion.getReduction()) + "元！";
            } else if (promotionType == 11) {
                Map<String, Object> goodsInfo = goodsInfoMap.get(onlineGoodsPromotion.getGoodsId());
                promotionDescription = "原价" + decimalFormat1.format(GoodsUtils.obtainSalePrice(goodsInfo)) + " 现价" + decimalFormat2.format(GoodsUtils.calculatePromotionPrice(goodsInfo, onlineVipType, onlineGoodsPromotion));
            }

            onlineGoodsPromotion.setPromotionDescription(promotionDescription);

            onlineGoodsPromotionMap.put(onlineGoodsPromotion.getGoodsId(), onlineGoodsPromotion);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("goodsInfos", categoryIdAndGoodsInfoMap);
        data.put("categoryInfos", categoryInfos);
        data.put("goodsPromotions", onlineGoodsPromotionMap);
        data.put("specificationGoodsInfos", specificationGoodsInfos);


        Map<String, Object> returnValue = new HashMap<String, Object>();
        returnValue.put("clazz", null);
        returnValue.put("code", "0");
        returnValue.put("data", data);
        returnValue.put("error", null);
        returnValue.put("isSuccess", true);
        returnValue.put("message", "查询菜牌信息成功！");
        returnValue.put("result", Constants.REST_RESULT_SUCCESS);
        returnValue.put("url", null);
        returnValue.put("version", null);
        return GsonUtils.toJson(returnValue);
    }

    /**
     * 查询商品信息（仅自助购使用）
     *
     * @param obtainGoodsInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainGoodsInfo(ObtainGoodsInfoModel obtainGoodsInfoModel) {
        BigInteger tenantId = obtainGoodsInfoModel.getTenantId();
        BigInteger branchId = obtainGoodsInfoModel.getBranchId();
        String barCode = obtainGoodsInfoModel.getBarCode();

        Branch branch = DatabaseHelper.find(Branch.class, branchId);
        ValidateUtils.notNull(branch, "门店不存在！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        ValidateUtils.notNull(headquartersBranch, "总部门店不存在！");

        BigInteger headquartersBranchId = headquartersBranch.getId();

        int isUseHqGoods = branch.getIsUseHqGoods();

        Map<String, Object> effectiveMenu = onlineGoodsMapper.findEffectiveMenu(tenantId, branchId, 3);

        Object data = null;
        BigInteger menuId = BigInteger.valueOf(MapUtils.getLongValue(effectiveMenu, "menuId"));
        if (menuId.compareTo(BigInteger.ZERO) != 0) {
            Map<String, Object> goodsInfo = null;
            if (barCode.startsWith("27")) {
                int weighPlu = Integer.valueOf(barCode.substring(2, 7));
                goodsInfo = onlineGoodsMapper.findGoodsInfo(tenantId, branchId, headquartersBranchId, menuId, null, weighPlu, isUseHqGoods);
            } else {
                goodsInfo = onlineGoodsMapper.findGoodsInfo(tenantId, branchId, headquartersBranchId, menuId, barCode, null, isUseHqGoods);
            }
            ValidateUtils.notEmpty(goodsInfo, "商品不存在！");

            data = goodsInfo;
        } else {
            SearchModel branchSearchModel = new SearchModel(true);
            branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);

            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("goods_status", Constants.SQL_OPERATION_SYMBOL_EQUALS, 0);
            if (barCode.startsWith("27")) {
                int weighPlu = Integer.valueOf(barCode.substring(7, 12));
                searchModel.addSearchCondition("weigh_plu", Constants.SQL_OPERATION_SYMBOL_EQUALS, weighPlu);
            } else {
                searchModel.addSearchCondition("bar_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, barCode);
            }

            if (isUseHqGoods == 1) {
                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            } else if (isUseHqGoods == 0) {
                List<BigInteger> branchIds = new ArrayList<BigInteger>();
                branchIds.add(branchId);
                branchIds.add(headquartersBranchId);

                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_IN, branchIds);
            }

            OnlineGoods onlineGoods = DatabaseHelper.find(OnlineGoods.class, searchModel);
            ValidateUtils.notNull(onlineGoods, "商品不存在！");

            data = onlineGoods;
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询商品成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询购物袋（仅自助购使用）
     *
     * @param listShoppingBagsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listShoppingBags(ListShoppingBagsModel listShoppingBagsModel) {
        BigInteger tenantId = listShoppingBagsModel.getTenantId();
        BigInteger branchId = listShoppingBagsModel.getBranchId();

        Branch branch = DatabaseHelper.find(Branch.class, branchId);
        ValidateUtils.notNull(branch, "门店不存在！");

        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        ValidateUtils.notNull(headquartersBranch, "总部门店不存在！");

        Map<String, Object> effectiveMenu = onlineGoodsMapper.findEffectiveMenu(tenantId, branchId, 3);

        BigInteger menuId = BigInteger.valueOf(MapUtils.getLongValue(effectiveMenu, "menuId"));

        BigInteger headquartersBranchId = headquartersBranch.getId();
        int isUseHqGoods = branch.getIsUseHqGoods();

        Object data = null;
        if (menuId.compareTo(BigInteger.ZERO) != 0) {
            data = onlineGoodsMapper.findAllGoodsInfos(tenantId, branchId, headquartersBranchId, menuId, null, null, "购物袋", isUseHqGoods);
        } else {
            SearchModel branchSearchModel = new SearchModel(true);
            branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);

            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("goods_name", Constants.SQL_OPERATION_SYMBOL_EQUALS, "购物袋");

            if (isUseHqGoods == 1) {
                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            } else if (isUseHqGoods == 0) {
                List<BigInteger> branchIds = new ArrayList<BigInteger>();
                branchIds.add(branchId);
                branchIds.add(headquartersBranchId);

                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_IN, branchIds);
            }

            List<OnlineGoods> onlineGoodses = DatabaseHelper.findAll(OnlineGoods.class, searchModel);
            data = onlineGoodses;
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询购物袋成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
