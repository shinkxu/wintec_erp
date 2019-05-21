package erp.chain.service.o2o;

import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.Category;
import erp.chain.domain.Goods;
import erp.chain.domain.GoodsUnit;
import erp.chain.domain.o2o.DietOrderDetail;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.domain.o2o.MeituanGoodsToBranch;
import erp.chain.domain.o2o.vo.MeituanGoodsVo;
import erp.chain.domain.supply.store.Store;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CategoryMapper;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsUnitMapper;
import erp.chain.mapper.o2o.DietOrderDetailMapper;
import erp.chain.mapper.o2o.DietOrderInfoMapper;
import erp.chain.mapper.o2o.MeituanGoodsToBranchMapper;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 美团
 * Created by wangms on 2017/3/13.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MeituanService {
    private static final String MEI_TUAN_SERVICE_URL = ConfigurationUtils.getConfigurationSafe(Constants.MEI_TUAN_SERVICE_URL);
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MeituanGoodsToBranchMapper meituanGoodsToBranchMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsUnitMapper goodsUnitMapper;
    @Autowired
    private StoreMapper storeMapper;
//    /**
//     * 订单映射
//     * @param map
//     */
//    def mapDietOrder(Map map){
//        try {
//            DietOrderInfo orderInfo = new DietOrderInfo();
//            def orderData = map.get("data");
//            SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            orderInfo.setCreateAt(sdf.parse(sdf.format(new Date(orderData.get("cTime")))));
//            orderInfo.setRemark(orderData.get("caution"));
//            orderInfo.setAllocationDate(sdf.parse(sdf.format(new Date(orderData.get("deliveryTime")))));
//            orderInfo.setBranchId(new BigInteger(orderData.get("poiId").toString()));
//            orderInfo.setBranchName(orderData.get('poiName'));
//            if(orderData.get("logisticsCompletedTime")){
//                orderInfo.setArriveDate(sdf.parse(sdf.format(new Date(orderData.get("logisticsCompletedTime")))));
//            }
//            orderInfo.setTotalAmount(new BigDecimal(orderData.get("originalPrice")));
//            orderInfo.setVipAddressName(orderData.get("recipientAddress"));
//            orderInfo.setConsignee(orderData.get("recipientName"));
//            orderInfo.setMobilePhone(orderData.get("recipientPhone"));
//            orderInfo.setOrderStatus(orderData.get("status"));
//            orderInfo.setReceivedAmount(new BigDecimal(orderData.get('total')));
//            orderInfo.setOrderResource(Integer.valueOf("3"));
//            orderInfo.save flush:true;
//
//        } catch (Exception e){
//        }
//
//    }

    /**
     * 取消订单
     */
    public void cancelOrder(String code) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", code);
        DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
        if (orderInfo != null && orderInfo.getOrderStatus() != 9) {
            //将订单状态设置为 9：已拒绝
            orderInfo.setOrderStatus(9);
            orderInfo.setLastUpdateAt(new Date());
            dietOrderInfoMapper.update(orderInfo);
            //处理库存
            Map<String, Object> map = new HashMap<>();
            map.put("dietOrderInfoId", orderInfo.getId());
            map.put("tenantId", orderInfo.getTenantId());
            List<DietOrderDetail> dietOrderDetails = dietOrderDetailMapper.select(map);
            if (dietOrderDetails != null) {
                for (DietOrderDetail detail : dietOrderDetails) {
                    if (detail.getGoodsName() != null && !"美团外卖".equals(detail.getGoodsName())) {
                        Map<String, Object> pa4 = new HashMap<>();
                        pa4.put("branchId", orderInfo.getBranchId());
                        pa4.put("id", detail.getGoodsId());
                        Goods goods1 = goodsMapper.findByCondition(pa4);
                        if (goods1 != null) {
                            detail.setGoodsId(goods1.getId());
                            detail.setGoodsName(goods1.getGoodsName());
                            //dealGoods(goods1,orderInfo.getOrderCode(),5);
                        }
                    }
                }
            }

        }
    }

    /**
     * 更改订单配送状态
     */
    public void receiveShippingStatus(String code, Map map) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", code);
        DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
        if (orderInfo != null) {
            if ("20".equals(map.get("shippingStatus").toString())) {
                //记录骑手电话、骑手姓名
                if (map.get("dispatcherName") != null) {
                    orderInfo.setDispatcherName(map.get("dispatcherName").toString());
                }
                if (map.get("dispatcherMobile") != null) {
                    orderInfo.setDispatcherMobile(map.get("dispatcherMobile").toString());
                }
                orderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(orderInfo);
            }
        }
    }

    /**
     * 用户退款,修改状态
     */
    public void updatePayStatus(String code, int payStatus, int orderStatus, String reason) throws IOException {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", code);
        DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
        if (orderInfo != null) {
            if (payStatus != -1 && orderInfo.getPayStatus() != 3 && orderInfo.getPayStatus() != 4) {
                orderInfo.setPayStatus(payStatus);
            }
            orderInfo.setOrderStatus(orderStatus);
            if (reason != null && !"".equals(reason)) {
                orderInfo.setMeituanRefundReason(reason);
            }
            if (orderStatus == 10) {
                orderInfo.setIncome(BigDecimal.ZERO);
            }
            orderInfo.setLastUpdateAt(new Date());
            dietOrderInfoMapper.update(orderInfo);

            if (orderStatus == 6) {
                SaleFlowUtils.writeSaleFlow(orderInfo);
                StockUtils.handleOnlineStock(orderInfo.getId());
            }
        }
    }

    /**
     * 查询机构信息(获取token)
     *
     * @param branchId
     */
    public Branch getBranchInfo(String branchId) {
        if (branchId != null && !"".equals(branchId)) {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("id", branchId);
            return branchMapper.find(pa1);
        }
        return null;
    }

    /**
     * 组合菜品json
     */
    public JSONArray combineDishJson(String ids, Branch branch) {
        List<Goods> goods = new ArrayList<>();
        if (ids != null) {
            String[] idArr = ids.split(",");
            if (idArr.length > 0) {
                for (int i = 0; i < idArr.length; i++) {
                    Map<String, Object> pa1 = new HashMap<>();
                    pa1.put("id", idArr[i]);
                    Goods g = goodsMapper.findByCondition(pa1);
                    if (g != null) {
                        goods.add(g);
                    }
                }
            }
        } else {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("branchId", branch.getId());
            pa1.put("meituanBind", "true");
            goods = goodsMapper.findListByCondition(pa1);
        }
        if (goods != null && goods.size() > 0) {
            JSONArray dishes = new JSONArray();
            for (Goods good : goods) {
                if (!"美团外卖".equals(good.getGoodsName()) && !"饿了么外卖".equals(good.getGoodsName())) {
                    JSONObject dish = new JSONObject();
                    //餐盒数量
                    dish.put("boxNum", 1);
                    //餐盒价格 不能为负数
                    dish.put("boxPrice", 0.0);
                    //菜品分类
                    dish.put("categoryName", good.getCategoryName());
                    //菜品描述,非必填
                    if (good.getMemo() != null) {
                        dish.put("description", good.getMemo());
                    }
                    //菜名，同一分类下菜品名不能重复
                    dish.put("dishName", good.getGoodsName());
                    //erp方菜品Id，不同机构可以重复，同一机构内不能重复，最大长度128 : branchId + goodCode
                    dish.put("EDishCode", good.getBranchId() + good.getGoodsCode());
                    //erp方机构Id
                    dish.put("EpoiId", branch.getId());
                    //是否售完
                    dish.put("isSoldOut", 0);
                    //最小购买数量
                    if (good.getMiniOrderNum() != null) {
                        dish.put("minOrderCount", good.getMiniOrderNum());
                    } else {
                        dish.put("minOrderCount", 1);
                    }
                    // 图片id或地址 支持jpg,png格式，图片需要小于1600*1200(非必需)
                    if (good.getPhoto() != null && !"".equals(good.getPhoto())) {
                        String imgPath = "/nfs/uploads/photo" + good.getPhoto();
                        String imgName = good.getPhoto().substring(good.getPhoto().lastIndexOf("/") + 1);
                        if (!imgName.contains(".jpg") && !imgName.contains(".jpeg") && !imgName.contains(".png")) {
                            imgName += ".jpg";
                            imgPath += ".jpg";
                        }
                        String code = uploadImage(branch, imgPath, imgName);
                        if (code != null && !"".equals(code)) {
                            dish.put("picture", code);
                        }
                    }
                    dish.put("goodsId", good.getId());
                    //分类下菜品的顺序(非必需)
//                dish.put("sequence",);
                    //菜品价格，不能为负数，如果传递了skus价格，本参数可不传
                    dish.put("price", good.getSalePrice());
                    //单位/规格
                    dish.put("unit", good.getGoodsUnitName());
                    //sku信息
//                dish.put("skus",);
                    dishes.add(dish);
                }

            }
            LogUtil.logInfo("同步商品数据dishes：" + dishes);
            return dishes;
        }
        return null;
    }


    /**
     * 上传菜品图片
     */
    public String uploadImage(Branch branch, String imagePath, String imageName) {
        try {
            Map<String, Object> paramsInfo = new HashMap<>();
            String ePoiId = branch.getId().toString();
            if (imagePath != null && imageName != null && new File(imagePath).exists()) {
                //认领机构返回的token【一店一token】
                paramsInfo.put("appAuthToken", branch.getMeituanToken());
                //交互数据的编码
                paramsInfo.put("charset", "UTF-8");
                String timestamp = String.valueOf(new Date().getTime());
                //当前请求的时间戳【单位是秒】
                paramsInfo.put("timestamp", timestamp);
                String sign = SHA1Utils.SHA1(paramsInfo);
                paramsInfo.put("sign", sign);
                //请求的数字签名
                StringBuffer baseUrl = new StringBuffer(MEI_TUAN_SERVICE_URL + "/waimai/image/upload?");
                String result = "";
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("imageName", imageName);
                paramMap.put("ePoiId", branch.getTenantId() + "Z" + ePoiId);
                paramMap.put("file", new File(imagePath));
                paramMap.put("baseUrl", SHA1Utils.combineParamsAsUrl(baseUrl.toString(), paramsInfo));
                ApiRest rest1 = ProxyApi.proxyPostFile("out", "wechat", "connectOutUrlFile", paramMap);
                if (Constants.REST_RESULT_SUCCESS.equals(rest1.getResult())) {
                    result = rest1.getData().toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result, Map.class);
                    if (map.get("data") != null) {
                        return map.get("data").toString();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.logError("(美团)上传图片异常:" + e.getMessage());
        }
        return null;
    }

    /**
     * 保存菜品映射关系
     *
     * @return
     */
    public void saveMeituanGoodsBranch(JSONArray dishes, BigInteger branchId) {
        if (dishes != null) {
            if (dishes.size() > 0) {
                for (int i = 0; i < dishes.size(); i++) {
                    Map<String, Object> pa1 = new HashMap<>();
                    Map dish = (Map) dishes.get(i);
                    pa1.put("id", dish.get("goodsId").toString());
                    Goods g = goodsMapper.findByCondition(pa1);
                    if (g != null) {
                        MeituanGoodsToBranch meituanGoodsToBranch = new MeituanGoodsToBranch();
                        meituanGoodsToBranch.setBranchId(branchId);
                        meituanGoodsToBranch.setTenantId(g.getTenantId());
                        meituanGoodsToBranch.setGoodsId(g.getId());
                        meituanGoodsToBranch.setCreateAt(new Date());
                        meituanGoodsToBranch.setLastUpdateAt(new Date());
                        meituanGoodsToBranch.setIsDeleted(false);
                        meituanGoodsToBranch.setIsBinding(true);
                        meituanGoodsToBranchMapper.insert(meituanGoodsToBranch);
                    }
                }
            }
        }
    }

    /**
     * 新增美团菜品映射记录
     *
     * @return
     */
    public int addMeituanGoodsToBranch(String branchId, String goodsCode, BigInteger tenantId) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("goodsCode", goodsCode);
        pa1.put("tenantId", tenantId);
        pa1.put("branchId", branchId);
        List<Goods> goods = goodsMapper.findListByCondition(pa1);
        if (goods != null) {
            MeituanGoodsToBranch meituanGoodsToBranch = new MeituanGoodsToBranch();
            meituanGoodsToBranch.setBranchId(new BigInteger(branchId));
            meituanGoodsToBranch.setTenantId(tenantId);
            meituanGoodsToBranch.setGoodsId(goods.get(0).getId());
            meituanGoodsToBranch.setCreateAt(new Date());
            meituanGoodsToBranch.setLastUpdateAt(new Date());
            meituanGoodsToBranch.setIsDeleted(false);
            meituanGoodsToBranch.setIsBinding(true);
            return meituanGoodsToBranchMapper.insert(meituanGoodsToBranch);
        }

        return 0;
    }

    /**
     * 获取美团的订单id
     *
     * @param id diet_order_info表的Id
     * @return
     */
    public String getOrderId(String id) {
        if (id != null) {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("id", id);
            DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
            if (orderInfo != null) {
                if (orderInfo.getOrderCode() != null) {
                    return orderInfo.getOrderCode().substring(1, orderInfo.getOrderCode().length());
                }
            }
        }
        return null;
    }

    /**
     * 获取美团的订单信息
     *
     * @param id diet_order_info表的Id
     * @return
     */
    public DietOrderInfo getOrderInfo(String id) {
        if (id != null) {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("id", id);
            DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
            if (orderInfo != null) {
                return orderInfo;
            }
        }
        return null;
    }

    /**
     * 修改订单状态
     *
     * @param
     * @return
     */
    public void editOrderStaus(String orderCode, Integer status) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", orderCode);
        DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
        if (orderInfo != null) {
            orderInfo.setOrderStatus(status);
            if (status == 2) {
                orderInfo.setConfirmOrderSource(1);
            }
            orderInfo.setLastUpdateAt(new Date());
            dietOrderInfoMapper.update(orderInfo);
        }
    }

    /**
     * 修改订单状态
     *
     * @param
     * @return
     */
    public void editOrderStaus(String orderCode, Integer status, String courierName, String courierPhone) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", orderCode);
        DietOrderInfo orderInfo = dietOrderInfoMapper.findByCondition(pa1);
        if (orderInfo != null) {
            orderInfo.setOrderStatus(status);
            orderInfo.setDispatcherName(courierName);
            orderInfo.setDispatcherMobile(courierPhone);
            orderInfo.setLastUpdateAt(new Date());
            dietOrderInfoMapper.update(orderInfo);
        }
    }

    /**
     * 进行菜品映射
     */
    public JSONArray mappingGoods(List goods, Branch branch) {
        JSONArray dishMappings = new JSONArray();
        String branchId = String.valueOf(branch.getId());
        for (int i = 0; i < goods.size(); i++) {
            boolean mappingFlag = false;
            // eDishCode 不是 branchId + goodsCode 说明没有映射过
            if (StringUtils.isBlank(((Map) goods.get(i)).get("eDishCode").toString())) {
                //eDishCode为空
                mappingFlag = true;
            } else {
                String eDishCode = ((Map) goods.get(i)).get("eDishCode").toString();
                if (eDishCode.length() != (branchId.length() + 8)) { //goodsCode 为8位
                    //或长度不对
                    mappingFlag = true;
                } else {
                    if (!branchId.equals(eDishCode.substring(0, branchId.length()))) {
                        //或长度对，开头不是商户id
                        mappingFlag = true;
                    }
                }
            }
            if (mappingFlag) {
                //根据名称匹配
                Map<String, Object> pa1 = new HashMap<>();
                pa1.put("goodsName", ((Map) goods.get(i)).get("dishName"));
                pa1.put("branchId", branch.getId());
                Goods ourGoods = goodsMapper.findByCondition(pa1);
                if (ourGoods != null) {
                    JSONObject jo = new JSONObject();
                    jo.put("dishId", ((Map) goods.get(i)).get("dishId"));
                    jo.put("eDishCode", branch.getId() + ourGoods.getGoodsCode());
                    dishMappings.add(jo);
                }
            }
        }
        return dishMappings;
    }

    /**
     * 进行菜品映射
     */
    public JSONArray mappingGoodsSingle(String branchId, String dishId, String goodsCode) {
        JSONArray dishMappings = new JSONArray();
        //根据名称匹配
        JSONObject jo = new JSONObject();
        jo.put("dishId", dishId);
        jo.put("eDishCode", branchId + goodsCode);
        dishMappings.add(jo);
        return dishMappings;
    }

    /**
     * 处理订单已确认
     */
    public void orderConfirm(Map meituanOrder) {
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", "M" + meituanOrder.get("orderId"));
        DietOrderInfo order = dietOrderInfoMapper.findByCondition(pa1);
        if (order != null) {
            //status: 4商家已确认
            LogUtil.logInfo("接收美团确认订单回调（1）：" + GsonUtils.toJson(order));
            if (meituanOrder.get("status") != null) {
                if (Integer.parseInt(meituanOrder.get("status").toString()) == 4) {
                    order.setOrderStatus(2);
                    order.setLastUpdateAt(new Date());

                    LogUtil.logInfo("接收美团确认订单回调（2）：" + GsonUtils.toJson(order));
                    dietOrderInfoMapper.update(order);

                    LogUtil.logInfo("接收美团确认订单回调（3）：" + GsonUtils.toJson(order));
                }
            }
        }
    }

    /**
     * 接收美团订单映射
     *
     * @param meituanOrder
     */
    public void mapDietOrder(Map meituanOrder) {
        //check,防止重复保存
        LogUtil.logInfo("美团订单回调参数：" + BeanUtils.toJsonStr(meituanOrder));
        Map<String, Object> pa1 = new HashMap<>();
        pa1.put("orderCode", "M" + meituanOrder.get("orderId"));
        DietOrderInfo check = dietOrderInfoMapper.findByCondition(pa1);
        meituanOrder.put("ePoiId", meituanOrder.get("ePoiId").toString().substring(meituanOrder.get("ePoiId").toString().indexOf("Z") + 1));
        Map<String, Object> pa2 = new HashMap<>();
        pa2.put("id", meituanOrder.get("ePoiId"));
        Branch branch = branchMapper.find(pa2);
        if (check == null) {
            DietOrderInfo orderInfo = new DietOrderInfo();
            if (Integer.valueOf(meituanOrder.get("deliveryTime").toString()) != 0) {
                Date d = new Date(Long.parseLong(meituanOrder.get("deliveryTime").toString() + "000"));
                orderInfo.setAllocationDate(d);
            } else {
                orderInfo.setAllocationDate(new Date());
            }
            //商品数量
            orderInfo.setAmount(1);
            //备注
            String remark = "";
            if (meituanOrder.get("caution") != null) {
                remark = meituanOrder.get("caution").toString();
//                orderInfo.setRemark(meituanOrder.get("caution").toString());
            }

            String extras = meituanOrder.get("extras").toString();
            if (StringUtils.isNotBlank(extras)) {
                JSONArray extrasJsonArray = JSONArray.fromObject(extras);
                int size = extrasJsonArray.size();
                for (int index = 0; index < size; index++) {
                    JSONObject extraJsonObject = extrasJsonArray.getJSONObject(index);
                    if (extraJsonObject.isEmpty()) {
                        continue;
                    }
                    int type = extraJsonObject.getInt("type");
                    if (type == 4 || type == 5 || type == 23) {
                        remark = remark + "," + extraJsonObject.getString("remark");
                    }
                }
            }

            if (remark.length() > 200) {
                remark = remark.substring(0, 197) + "...";
            }
            orderInfo.setRemark(remark);

            if (meituanOrder.get("daySeq") != null) {
                orderInfo.setMeituanDaySeq(meituanOrder.get("daySeq").toString());
            }
            if (meituanOrder.get("ePoiId") != null) {
                orderInfo.setBranchId(new BigInteger(meituanOrder.get("ePoiId").toString()));
                orderInfo.setBranchName(meituanOrder.get("poiName").toString());
                if (branch != null) {
                    orderInfo.setTenantId(branch.getTenantId());
                }
            }
            orderInfo.setConsignee(meituanOrder.get("recipientName").toString());
            if (meituanOrder.get("cTime") != null && Integer.valueOf(meituanOrder.get("cTime").toString()) != 0) {
                orderInfo.setCreateAt(new Date(meituanOrder.get("cTime").toString()));
            } else {
                orderInfo.setCreateAt(new Date());
            }
            if (meituanOrder.get("uTime") != null && Integer.valueOf(meituanOrder.get("uTime").toString()) != 0) {
                orderInfo.setLastUpdateAt(new Date(meituanOrder.get("uTime").toString()));
            } else {
                orderInfo.setLastUpdateAt(new Date());
            }

            String recipientPhone = meituanOrder.get("recipientPhone").toString();
            Object backupRecipientPhone = meituanOrder.get("backupRecipientPhone");

            Set<String> phoneSet = null;
            if (backupRecipientPhone != null) {
                String backupRecipientPhoneJson = backupRecipientPhone.toString();
                if (StringUtils.isNotBlank(backupRecipientPhoneJson)) {
                    phoneSet = JacksonUtils.readValueAsSet(backupRecipientPhoneJson, String.class);
                    phoneSet.add(recipientPhone);
                } else {
                    phoneSet = new HashSet<String>();
                    phoneSet.add(recipientPhone);
                }
            } else {
                phoneSet = new HashSet<String>();
                phoneSet.add(recipientPhone);
            }

//            orderInfo.setMobilePhone(meituanOrder.get("recipientPhone").toString());
            orderInfo.setMobilePhone(StringUtils.join(phoneSet, ","));
            orderInfo.setOrderCode("M" + meituanOrder.get("orderId"));
            orderInfo.setOrderMode(6);
            orderInfo.setOrderResource(3);
            if (Integer.valueOf(meituanOrder.get("status").toString()) == 2) {
                orderInfo.setOrderStatus(1);
            }
            if (Integer.valueOf(meituanOrder.get("payType").toString()) == 1) {
                orderInfo.setPayStatus(0);
                orderInfo.setPayWay(3);
            } else if (Integer.valueOf(meituanOrder.get("payType").toString()) == 2) {
                orderInfo.setPayStatus(1);
                orderInfo.setPayWay(6);
                orderInfo.setPayAt(orderInfo.getCreateAt());
            }
            orderInfo.setVipAddressName(meituanOrder.get("recipientAddress").toString());
            orderInfo.setTotalAmount(new BigDecimal(meituanOrder.get("originalPrice").toString()));
            orderInfo.setReceivedAmount(new BigDecimal(meituanOrder.get("total").toString()));
            orderInfo.setDeliverFee(new BigDecimal(meituanOrder.get("shippingFee").toString()));
            orderInfo.setCreateScore(0);
            //记录店铺实收金额
            if (meituanOrder.get("poiReceiveDetail") != null) {
                String poiReceiveDetail = meituanOrder.get("poiReceiveDetail").toString();

                BigDecimal hundred = BigDecimal.valueOf(100);
                JSONObject poiReceiveDetailJsonObject = JSONObject.fromObject(poiReceiveDetail);
                if (poiReceiveDetailJsonObject.has("wmPoiReceiveCent")) {
                    orderInfo.setIncome(BigDecimal.valueOf(poiReceiveDetailJsonObject.getDouble("wmPoiReceiveCent")).divide(hundred));
                }

                if (poiReceiveDetailJsonObject.has("actOrderChargeByPoi")) {
                    BigDecimal discountAmount = BigDecimal.ZERO;
                    JSONArray actOrderChargeByPoiJsonArray = poiReceiveDetailJsonObject.getJSONArray("actOrderChargeByPoi");
                    int size = actOrderChargeByPoiJsonArray.size();
                    for (int index = 0; index < size; index++) {
                        JSONObject item = actOrderChargeByPoiJsonArray.getJSONObject(index);
                        discountAmount = discountAmount.add(BigDecimal.valueOf(item.getDouble("moneyCent")).divide(hundred));
                    }
                    orderInfo.setDiscountAmount(discountAmount);
                }
            }
            dietOrderInfoMapper.insert(orderInfo);
            //开始解析detail
            if (meituanOrder.get("detail") != null) {
                List<Map> details = GsonUntil.jsonAsList(meituanOrder.get("detail").toString(), Map.class);
                if (details != null && details.size() > 0) {
                    Map<String, Object> pa3 = new HashMap<>();
                    if (branch != null) {
                        pa3.put("tenantId", branch.getTenantId());
                    }
                    pa3.put("branchId", meituanOrder.get("ePoiId"));
                    pa3.put("goodsName", "美团外卖");
                    List<Goods> list = goodsMapper.findListByCondition(pa3);
                    Goods commonMeituan = null;
                    if (list != null && list.size() > 0) {
                        commonMeituan = list.get(0);
                    }
                    if (commonMeituan == null && branch != null) {
                        commonMeituan = addDefaultGoods(branch.getTenantId(), new BigInteger(meituanOrder.get("ePoiId").toString()));
                    }
                    double packageFee = 0;
                    for (Map mDetail : details) {
                        DietOrderDetail detail = new DietOrderDetail();
                        detail.setDietOrderInfoId(orderInfo.getId());
                        if (mDetail.get("app_food_code") != null) {
                            if (mDetail.get("app_food_code").toString().length() >= 8) {
                                String goodsCode = mDetail.get("app_food_code").toString().substring(mDetail.get("app_food_code").toString().length() - 8);
                                Map<String, Object> pa4 = new HashMap<>();
                                pa4.put("branchId", meituanOrder.get("ePoiId"));
                                pa4.put("goodsCode", goodsCode);
                                Goods goods1 = goodsMapper.findByCondition(pa4);
                                if (goods1 != null) {
                                    detail.setGoodsId(goods1.getId());
                                    detail.setGoodsName(goods1.getGoodsName());
                                    //dealGoods(goods1,orderInfo.getOrderCode(),1);
                                }
                            }
                        }
                        if (mDetail.get("food_name") != null) {
                            if (mDetail.get("spec") != null) {
                                detail.setGoodsName(mDetail.get("food_name").toString() + mDetail.get("spec").toString());
                            } else {
                                detail.setGoodsName(mDetail.get("food_name").toString());
                            }
                        }
                        if (mDetail.get("food_property") != null) {
                            detail.setTasteName(mDetail.get("food_property").toString());
                        }
                        if (commonMeituan != null && detail.getGoodsId() == null) {
                            detail.setGoodsId(commonMeituan.getId());
                        }
                        if (mDetail.get("box_num") != null && mDetail.get("box_price") != null) {
                            packageFee += Double.parseDouble(mDetail.get("box_num").toString()) * Double.parseDouble(mDetail.get("box_price").toString());
                        }
                        if (mDetail.get("cart_id") != null) {
                            detail.setGroupId(mDetail.get("cart_id").toString());
                        }

                        detail.setSalePrice(new BigDecimal(mDetail.get("price").toString()));
                        detail.setSalePriceActual(new BigDecimal(mDetail.get("price").toString()));
                        detail.setQuantity(new BigDecimal(mDetail.get("quantity").toString()));
                        detail.setTotalAmount(detail.getSalePrice().multiply(new BigDecimal(mDetail.get("quantity").toString())));
                        detail.setReceivedAmount(detail.getSalePrice().multiply(new BigDecimal(mDetail.get("quantity").toString())));
                        detail.setStatus(0);
                        detail.setCreateAt(new Date());
                        detail.setLastUpdateAt(new Date());
                        dietOrderDetailMapper.insert(detail);
                    }
                    orderInfo.setPackageFee(new BigDecimal(packageFee));
                    dietOrderInfoMapper.update(orderInfo);
                }
            }
        }

    }

    /**
     * @param goods1
     * @param orderCode
     * @param occurType 1 减少库存 5增加库存
     */
    private void dealGoods(Goods goods1, String orderCode, Integer occurType) {
        try {
            //处理库存
            if (goods1.getIsStore()) {
                Map<String, Object> pa5 = new HashMap<>();
                pa5.put("goodsId", goods1.getId());
                pa5.put("tenantId", goods1.getTenantId());
                Store store = storeMapper.selectByGoodsId(pa5);
                if (store != null && store.getQuantity() != null && store.getQuantity().doubleValue() > 0) {
                    List<String> saleList = new ArrayList<>();
                    /** 发送流水消息 开始 */
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("tenantId", goods1.getTenantId());
                    map.put("branchId", goods1.getBranchId());
                    map.put("goodsId", goods1.getId());
                    map.put("price", goods1.getSalePrice());
                    map.put("quantity", store.getQuantity());
                    map.put("createBy", "美团外卖");
                    map.put("occurType", occurType);
                    map.put("code", orderCode);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("billCreateTime", dateFormat.format(new Date()));
                    String saleJson = JSONObject.fromObject(map).toString();
                    saleList.add(saleJson);

                    String topic = null;
                    topic = PropertyUtils.getDefault("redis_topics");
                    if (StringUtils.isNotEmpty(topic) && !saleList.isEmpty()) {
                        Long l = PartitionCacheUtils.lpush(topic, (String[]) saleList.toArray(new String[saleList.size()]));
                        if (l == null) {
                            throw new Exception("库存计算发送失败");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.logError("美团外卖处理库存异常：" + e.getMessage());
        }
    }

    public Goods addDefaultGoods(BigInteger tenantId, BigInteger branchId) {
        Goods meituanGoods = new Goods();
        meituanGoods.setTenantId(tenantId);
        meituanGoods.setBranchId(branchId);
        List<Category> categorys = categoryMapper.getCatsGoodsUsed(tenantId);
        Category category = null;
        if (categorys != null && categorys.size() > 0) {
            category = categorys.get(0);
            meituanGoods.setCategoryId(category.getId());
            meituanGoods.setCategoryName(category.getCatName());
            Map<String, Object> param2 = new HashMap<>();
            param2.put("catCode", category.getCatCode());
            param2.put("tenantId", category.getTenantId());
            String gc = goodsMapper.getGoodsCode(param2);
            if (gc == null || gc.equals("")) {
                String code = category.getCatCode();
                if (code != null && !code.equals("") && code.length() == 2) {
                    gc = code + "000000";
                } else {
                    gc = code + "0000";
                }
            }
            meituanGoods.setGoodsCode(SerialNumberGenerate.nextGoodsCode(gc));
        }
        Map<String, Object> param3 = new HashMap<>();
        param3.put("tenantId", tenantId);
        List<GoodsUnit> goodsUnits = goodsUnitMapper.queryUnits(param3);
        if (goodsUnits != null && goodsUnits.size() > 0) {
            GoodsUnit goodsUnit = goodsUnits.get(0);
            meituanGoods.setGoodsUnitId(goodsUnit.getId());
            meituanGoods.setGoodsUnitName(goodsUnit.getUnitName());
        } else {
            meituanGoods.setGoodsUnitId(new BigInteger("0"));
        }
        meituanGoods.setGoodsName("美团外卖");
        meituanGoods.setMnemonic("mtwm");
        meituanGoods.setSalePrice(new BigDecimal(0));
        meituanGoods.setVipPrice(new BigDecimal(0));
        meituanGoods.setVipPrice1(new BigDecimal(0));
        meituanGoods.setVipPrice2(new BigDecimal(0));
        meituanGoods.setCombinationType(0);
        meituanGoods.setCreateBy("System");
        meituanGoods.setCreateAt(new Date());
        meituanGoods.setLastUpdateAt(new Date());
        meituanGoods.setLastUpdateBy("System");
        meituanGoods.setGoodsStatus(1);
        meituanGoods.setGoodsType(1);
        meituanGoods.setPriceType(0);
        meituanGoods.setIsPricetag(false);
        goodsMapper.insert(meituanGoods);
        return meituanGoods;
    }

    /**
     * 美团机构绑定回调，保存token
     */
    public void combineBranch(String ePoiId, String appAuthToken, String businessId, String meiTuanPoiId, String meiTuanPoiName) {
        if (ePoiId != null) {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("id", ePoiId);
            Branch branch = branchMapper.find(pa1);
            if (branch != null) {
                branch.setMeituanToken(appAuthToken);
                branch.setMeiTuanPoiId(meiTuanPoiId);
                branch.setMeiTuanPoiName(meiTuanPoiName);
                if (businessId != null) {
                    if (branch.getMeituanBusiness() != null && !"".equals(branch.getMeituanBusiness()) && !"0".equals(branch.getMeituanBusiness())) {
                        if (!businessId.equals(branch.getMeituanBusiness())) {
                            //美团业态：0:未开通业务 1: 接入团购&闪惠业务2: 接入外卖业务 3：团购&闪惠、外卖 都接入
                            branch.setMeituanBusiness("3");
                        }
                    } else {
                        branch.setMeituanBusiness(businessId);
                    }
                }
                branch.setLastUpdateAt(new Date());
                branchMapper.update(branch);
            }
        }

    }

    /**
     * 美团机构绑定回调，保存token
     */
    public void unbundingBranch(String ePoiId, String businessId) {
        if (ePoiId != null) {
            Map<String, Object> pa1 = new HashMap<>();
            pa1.put("id", ePoiId);
            Branch branch = branchMapper.find(pa1);
            if (branch != null) {
                if (businessId != null) {
                    if (branch.getMeituanBusiness() != null && !"".equals(branch.getMeituanBusiness())) {
                        //美团业态：0:未开通业务 1: 接入团购&闪惠业务2: 接入外卖业务 3：团购&闪惠、外卖 都接入
                        if ("1".equals(businessId)) {
                            if ("1".equals(branch.getMeituanBusiness())) {
                                branch.setMeituanBusiness("0");
                                branch.setMeituanToken("");
                            } else if ("3".equals(branch.getMeituanBusiness())) {
                                branch.setMeituanBusiness("2");
                            }
                        } else if ("2".equals(businessId)) {
                            if ("2".equals(branch.getMeituanBusiness())) {
                                branch.setMeituanBusiness("0");
                                branch.setMeituanToken("");
                            } else if ("3".equals(branch.getMeituanBusiness())) {
                                branch.setMeituanBusiness("1");
                            }
                        }
                        branch.setLastUpdateAt(new Date());
                    }
                }
                branch.setMeiTuanPoiId(null);
                branch.setMeiTuanPoiName(null);
                branchMapper.update(branch);
            }
        }

    }
//    /**
//     * 处理闪惠订单
//     */
//    def dealShanhuiOrder(Map map){
//        try {
//            def order = DietOrderInfo.findByOrderCode("M" + map.get("vendorOrderId"));
//            if(order){
//                //支付成功
//                if(map.get("orderStatus") == 10){
//                    order.setPayStatus(1);
//                } else if(map.get("orderStatus") == 20){ //退款成功
//                    order.setPayStatus(0);
//                }
//                order.save flush: true;
//            }
//        } catch (Exception e){
//            LogUtil.logError("处理闪惠订单异常：" + e.message);
//        }
//
//    }

    /**
     * 转换美团菜品列表
     *
     * @return
     */
    public Map<String, Object> getMeituanGoodsData(List list, Map params) {
        Map<String, Object> dMap = new HashMap<>();
        List<MeituanGoodsVo> mList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Map m = (Map) list.get(i);
                MeituanGoodsVo meituanGoodsVo = new MeituanGoodsVo();
                meituanGoodsVo.setCategoryName(m.get("categoryName").toString());
                meituanGoodsVo.setDishId(new BigInteger(m.get("dishId").toString()));
                meituanGoodsVo.setDishName(m.get("dishName").toString());
                if (m.get("eDishCode") != null && !"".equals(m.get("eDishCode"))) {
                    meituanGoodsVo.seteDishCode(m.get("eDishCode").toString());
                    String eCode = m.get("eDishCode").toString();
                    if (eCode.length() >= 8) {
                        String goodsCode = eCode.substring(eCode.length() - 8);
                        String branchId = eCode.substring(0, eCode.length() - 8);
                        Map<String, Object> pa1 = new HashMap<>();
                        pa1.put("goodsCode", goodsCode);
                        pa1.put("branchId", branchId);
                        List<Goods> goodsList = goodsMapper.findListByCondition(pa1);
                        if (goodsList != null && goodsList.size() > 0) {
                            meituanGoodsVo.setGoods(goodsList.get(0));
                        }
                    }
                }
                if (m.get("waiMaiDishSkuBases") != null) {
                    List<Map> waiMaiDishSkuBases = (List<Map>) m.get("waiMaiDishSkuBases");
                    meituanGoodsVo.setWaiMaiDishSkuBases(m.get("waiMaiDishSkuBases").toString());
                    meituanGoodsVo.setPrice(new BigDecimal(waiMaiDishSkuBases.get(0).get("price").toString()));
                    if (waiMaiDishSkuBases.get(0).get("spec") != null) {
                        meituanGoodsVo.setSpec(waiMaiDishSkuBases.get(0).get("spec").toString());
                    }
                    if (waiMaiDishSkuBases.get(0).get("eDishSkuCode") != null) {
                        meituanGoodsVo.seteDishSkuCode(waiMaiDishSkuBases.get(0).get("eDishSkuCode").toString());
                    }
                }

                boolean addFlag = true;
                if (params.get("goodsName") != null && !"".equals(params.get("goodsName"))) {
                    if (!m.get("dishName").toString().contains(params.get("goodsName").toString())) {
                        addFlag = false;
                    }
                }
                if (params.get("categoryName") != null && !"".equals(params.get("categoryName"))) {
                    if (!m.get("categoryName").toString().contains(params.get("categoryName").toString())) {
                        addFlag = false;
                    }
                }
                if (addFlag) {
                    mList.add(meituanGoodsVo);
                }
            }
        }
        dMap.put("rows", mList);
        dMap.put("total", mList.size());
        return dMap;
    }

    /**
     * 组合 dishSkuPrice
     *
     * @return
     */
    public JSONArray combineDishSkuPrices(String eDishCode, Map params) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("eDishCode", eDishCode);
        JSONArray skus = new JSONArray();
        JSONObject sku = new JSONObject();
        sku.put("price", params.get("price"));
        sku.put("skuId", eDishCode);
        skus.add(sku);
        jo.put("skus", skus);
        jsonArray.add(jo);
        return jsonArray;
    }

    public void handlePrivacyDowngradeCallback(Map<String, String> parameters) throws ParseException {
        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()) + " 00:00:00");
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("create_at", Constants.SQL_OPERATION_SYMBOL_GREATER_THAN_EQUALS, yesterday);
        long count = DatabaseHelper.count(OnlineDietOrderInfo.class, searchModel);
        long page = count % 1000 == 0 ? count / 100 : count / 1000 + 1;

        if (count > 0) {

        }*/
    }
}
