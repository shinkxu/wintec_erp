package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.Goods;
import erp.chain.domain.GoodsSpec;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.DietOrderDetailShowVo;
import erp.chain.domain.o2o.vo.DietOrderInfoShowVo;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsSpecMapper;
import erp.chain.mapper.o2o.*;
import erp.chain.service.supply.store.impl.StoreOrderServiceImpl;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.GsonUntil;
import erp.chain.utils.OrderUtils;
import erp.chain.utils.SaleFlowUtils;
import erp.chain.utils.StockUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangms on 2017/1/21.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DietOrderInfoService {
    //    @Autowired
//    private VipGradeService vipGradeService;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private StoreOrderServiceImpl storeOrderService;
    /**
     * 订单类型 1：堂食
     */
    static final String ORDERMODE_TANGSHI = "1";

    /**
     * 订单类型 2：店内自提
     */
    static final String ORDERMODE_ZITI = "2";

    /**
     * 订单类型 3:预约点餐
     */
    static final String ORDERMODE_DIANCAN = "3";

    /**
     * 订单类型 4：外卖
     */
    static final String ORDERMODE_WAIMAI = "4";

    /**
     * 订单类型 6：美团外卖
     */
    static final String ORDERMODE_MEITUAN = "6";

    /**
     * 订单类型 7：饿了么外卖
     */
    static final String ORDERMODE_ELEME = "7";

    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private GoodsSpecMapper goodsSpecMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipAddressMapper vipAddressMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private VipService vipService;
    @Autowired
    private WxPosCodeMapper wxPosCodeMapper;
    @Autowired
    private VipStoreHistoryService vipStoreHistoryService;
    @Autowired
    private VipTradeHistoryMapper vipTradeHistoryMapper;
    @Autowired
    private TenantConfigService tenantConfigService;


    /**
     * 自助餐厅撤销取餐后将订单状态由已完成更新为未完成
     *
     * @author szq
     */
    public DietOrderInfo changeOrderStatus(BigInteger dietOrderInfoId, Integer orderStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dietOrderInfoId);
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(map);
        if (dietOrderInfo != null) {
            dietOrderInfo.setOrderStatus(orderStatus);
            dietOrderInfo.setLastUpdateAt(new Date());
            int flag = dietOrderInfoMapper.update(dietOrderInfo);
            if (flag == 1) {
                return dietOrderInfo;
            }
        }
        return null;
    }

    /**
     * 根据tenantId分页查询订单
     *
     * @author szq
     */
    public Map<String, Object> listDietOrderInfo(Map params) {
        Map<String, Object> map = new HashMap<>();
        //订单来源
        if (params.get("orderResource") != null) {
            if ("3".equals(params.get("orderResource"))) {//美团订餐
                params.put("orderMode", "6");
            } else if ("4".equals(params.get("orderResource"))) {//饿了么订餐
                params.put("orderMode", "7");
            } else if ("1".equals(params.get("orderResource"))) {//微餐厅订餐
                if (params.get("orderCode") == null || "".equals(params.get("orderCode"))) {
                    params.put("orderCode", "WX");
                }
            } else if ("5".equals(params.get("orderResource"))) {//自助购
                params.put("orderResource", "");
                params.put("orderMode", "8");
            } else {//电话订餐
                if (params.get("orderCode") == null || "".equals(params.get("orderCode"))) {
                    params.put("orderCode", "W");
                }
            }
        }
        List<DietOrderInfo> listCount = dietOrderInfoMapper.select(params);
        if (params.get("page") != null && params.get("rows") != null) {
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<DietOrderInfo> list = dietOrderInfoMapper.select(params);
        BigDecimal totalAmount = dietOrderInfoMapper.sumTotalAmount(params);
        //获取订单来源，第三方订单根据订单类型获取，微信和电话订单根据orderCode获取
        List<DietOrderInfo> lists = new ArrayList<>();
        for (DietOrderInfo order : list) {
            if (order.getOrderMode() != null) {
                if (order.getOrderMode() == 6) {//美团
                    order.setOrderResource(3);
                } else if (order.getOrderMode() == 7) {//饿了么
                    order.setOrderResource(4);
                } else if (order.getOrderCode().contains("WX")) {//有 WX 就是微餐厅订单，否则就是电话订单
                    order.setOrderResource(1);
                } else if (order.getOrderCode().contains("ZZG")) {//有 ZZG 就是自助购订单，否则就是电话订单
                    order.setOrderResource(5);
                } else if (order.getOrderMode() == 0) {  //APP点餐
                    order.setOrderResource(0);
                } else {
                    order.setOrderResource(2);
                }
            }
            lists.add(order);
        }
        //未接x单，已接x单,已出品x单，已完成x单，异常关闭x单。
        List<DietOrderInfo> list2 = dietOrderInfoMapper.select(params);
        int nm1 = 0;
        int nm2 = 0;
        int nm3 = 0;
        int nm4 = 0;
        int nm5 = 0;
        for (DietOrderInfo order : list2) {
            if (order.getOrderStatus() != null) {
                if (order.getOrderStatus() == 1) {
                    nm1++;
                }
                if (order.getOrderStatus() == 2) {
                    nm2++;
                }
                if (order.getOrderStatus() == 3) {
                    nm3++;
                }
                if (order.getOrderStatus() == 4) {
                    nm4++;
                }
                if (order.getOrderStatus() == 9) {
                    nm5++;
                }
            }
        }
        Map footMap = new HashMap();
        map.put("num1", nm1);
        map.put("num2", nm2);
        map.put("num3", nm3);
        map.put("num4", nm4);
        map.put("num5", nm5);
        footMap.put("receivedAmount", totalAmount);
        map.put("rows", lists);
        map.put("total", listCount.size());
        map.put("footer", footMap);
        return map;
    }

    /**
     * 根据id查询促销活动订单
     *
     * @author szq
     */
    public DietOrderInfo findDietOrderInfoById(BigInteger id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
        return dietOrderInfo;
    }

    /**
     * 删除订单  不支持批量删除
     *
     * @author szq
     */
    public ApiRest deleteDietOrderInfo(BigInteger id) {
        ApiRest rest = new ApiRest();
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
        if (dietOrderInfo == null || (dietOrderInfo.getOrderStatus() != null && dietOrderInfo.getOrderStatus() == 9)) {
            rest.setMessage("订单不存在或已被删除");
            rest.setIsSuccess(false);
            return rest;
        }
        dietOrderInfo.setOrderStatus(9);
        dietOrderInfo.setLastUpdateAt(new Date());
        int flag = dietOrderInfoMapper.update(dietOrderInfo);
        if (flag == 1) {
            rest.setMessage("订单删除成功");
            rest.setIsSuccess(true);
        }
        return rest;
    }

    /**
     * 新增订单
     *
     * @author szq
     */
    public ApiRest createOrder(Map params, List<Goods> goodsList) {
        ApiRest rest = new ApiRest();
        //根据手机号和姓名查询地址，若查到则修改，没有查到则新增
        try {
            List<VipAddress> listAdd = null;
            rest.setIsSuccess(true);
            rest.setMessage("订单添加成功");
        } catch (Exception e) {
            rest.setIsSuccess(false);
            rest.setMessage(e.getMessage());
            rest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return rest;
    }

    /**
     * 根据订单id查询订单详情
     *
     * @author szq
     */
    public DietOrderInfoShowVo showOrderDetail(BigInteger id) {
        DietOrderInfoShowVo dietOrderInfoShowVo = new DietOrderInfoShowVo();
        try {
            if (id != null) {
                Map<String, Object> param = new HashMap<>();
                param.put("id", id);
                BigDecimal totalAmount = BigDecimal.ZERO;
                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
                dietOrderInfoShowVo.setId(dietOrderInfo.getId());
                dietOrderInfoShowVo.setBranchId(dietOrderInfo.getBranchId());
                dietOrderInfoShowVo.setBranchName(dietOrderInfo.getBranchName());
                if (dietOrderInfo.getOrderMode() != null) {
                    if (dietOrderInfo.getOrderMode() == 6) {//美团
                        dietOrderInfoShowVo.setOrderResource(3);
                    } else if (dietOrderInfo.getOrderMode() == 7) {//饿了么
                        dietOrderInfoShowVo.setOrderResource(4);
                    } else if (dietOrderInfo.getOrderCode().contains("WX")) {//有 WX 就是微餐厅订单，否则就是电话订单
                        dietOrderInfoShowVo.setOrderResource(1);
                    } else if (dietOrderInfo.getOrderCode().contains("ZZG")) {//有 ZZG 就是自助购订单，否则就是电话订单
                        dietOrderInfoShowVo.setOrderResource(5);
                    } else if (dietOrderInfo.getOrderMode() == 0) {  //APP点餐
                        dietOrderInfoShowVo.setOrderResource(0);
                    } else {
                        dietOrderInfoShowVo.setOrderResource(2);
                    }
                } else {
                    dietOrderInfoShowVo.setOrderMode(dietOrderInfo.getOrderMode());
                    dietOrderInfoShowVo.setOrderResource(dietOrderInfo.getOrderResource());
                }
                dietOrderInfoShowVo.setOrderCode(dietOrderInfo.getOrderCode());
                dietOrderInfoShowVo.setOrderStatus(dietOrderInfo.getOrderStatus());
                if (dietOrderInfo.getVipId() != null) {
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("id", dietOrderInfo.getVipId());
                    param1.put("tenantId", dietOrderInfo.getTenantId());
                    Vip vip = vipMapper.findById(param1);
                    if (vip != null) {
                        dietOrderInfoShowVo.setVipId(vip.getId());
                        dietOrderInfoShowVo.setVipName(vip.getVipName());
                        dietOrderInfoShowVo.setVipCode(vip.getVipCode());
                        dietOrderInfoShowVo.setVipPhone(vip.getPhone());
                        dietOrderInfoShowVo.setVipStore(vip.getVipStore());
                    } else {
                        dietOrderInfoShowVo.setVipId(new BigInteger("-1"));
                        dietOrderInfoShowVo.setVipName("");
                        dietOrderInfoShowVo.setVipCode("");
                        dietOrderInfoShowVo.setVipPhone("");
                        dietOrderInfoShowVo.setVipStore(BigDecimal.ZERO);
                    }
                }
                dietOrderInfoShowVo.setEatStatus(dietOrderInfo.getEatStatus());
                dietOrderInfoShowVo.setPayStatus(dietOrderInfo.getPayStatus());
                dietOrderInfoShowVo.setPayWay(dietOrderInfo.getPayWay());
                dietOrderInfoShowVo.setReceivedAmount(dietOrderInfo.getReceivedAmount());
                dietOrderInfoShowVo.setUseScore(dietOrderInfo.getUseScore());
                dietOrderInfoShowVo.setAmount(dietOrderInfo.getAmount());
                dietOrderInfoShowVo.setIsUsePrivilege(dietOrderInfo.getIsUsePrivilege());
                dietOrderInfoShowVo.setPayAt(dietOrderInfo.getPayAt());
                dietOrderInfoShowVo.setCreateScore(dietOrderInfo.getCreateScore());
                dietOrderInfoShowVo.setEatPeople(dietOrderInfo.getEatPeople());
                dietOrderInfoShowVo.setAppointmentDate(dietOrderInfo.getAppointmentDate());
                dietOrderInfoShowVo.setConsignee(dietOrderInfo.getConsignee());
                dietOrderInfoShowVo.setMobilePhone(dietOrderInfo.getMobilePhone());
                dietOrderInfoShowVo.setAllocationDate(dietOrderInfo.getAllocationDate());
                dietOrderInfoShowVo.setArriveDate(dietOrderInfo.getArriveDate());
                dietOrderInfoShowVo.setRemark(dietOrderInfo.getRemark());
                dietOrderInfoShowVo.setIsFreeOfCharge(dietOrderInfo.getIsFreeOfCharge());
                dietOrderInfoShowVo.setPosCode(dietOrderInfo.getPosCode());
                dietOrderInfoShowVo.setCashier(dietOrderInfo.getCashier());
                dietOrderInfoShowVo.setTableOpenAt(dietOrderInfo.getTableOpenAt());
                dietOrderInfoShowVo.setIsRefund(dietOrderInfo.getIsRefund());
                dietOrderInfoShowVo.setVipAddressId(dietOrderInfo.getVipAddressId());
                dietOrderInfoShowVo.setVipAddressName(dietOrderInfo.getVipAddressName());
                dietOrderInfoShowVo.setMeituanDaySeq(dietOrderInfo.getMeituanDaySeq());
                dietOrderInfoShowVo.setDispatcherName(dietOrderInfo.getDispatcherName());
                dietOrderInfoShowVo.setDispatcherMobile(dietOrderInfo.getDispatcherMobile());
                dietOrderInfoShowVo.setIncome(dietOrderInfo.getIncome());
                dietOrderInfoShowVo.setDiscountAmount(dietOrderInfo.getDiscountAmount());
                dietOrderInfoShowVo.setConfirmOrderSource(dietOrderInfo.getConfirmOrderSource());

                Map<String, Object> param2 = new HashMap<>();
                param2.put("dietOrderInfoId", dietOrderInfo.getId());
                List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMapper.select(param2);
                List<DietOrderDetailShowVo> dietOrderDetailShowVoList = new ArrayList<>();
                for (int i = 0; i < dietOrderDetailList.size(); i++) {
                    DietOrderDetailShowVo dietOrderDetailShowVo = new DietOrderDetailShowVo();
                    dietOrderDetailShowVo.setStatus(dietOrderDetailList.get(i).getStatus());
                    if (dietOrderDetailList.get(i).isPackage()) {
                        dietOrderDetailShowVo.setIsPackage(true);
                        dietOrderDetailShowVo.setPackageId(dietOrderDetailList.get(i).getPackageId());
                        dietOrderDetailShowVo.setPackageName(dietOrderDetailList.get(i).getPackageName());
                    } else {
                        dietOrderDetailShowVo.setIsPackage(false);
                    }
                    dietOrderDetailShowVo.setGoodsId(dietOrderDetailList.get(i).getGoodsId());
                    dietOrderDetailShowVo.setGoodsName(dietOrderDetailList.get(i).getGoodsName());
                    if (dietOrderDetailList.get(i).getTaste() != null) {
                        Map<String, Object> param3 = new HashMap<>();
                        param3.put("id", dietOrderDetailList.get(i).getTaste());
                        GoodsSpec goodsSpec = goodsSpecMapper.findByCondition(param3);
                        if (goodsSpec != null) {
                            dietOrderDetailShowVo.setTaste(goodsSpec.getProperty());
                        }
                    }
                    if (dietOrderDetailList.get(i).getTasteName() != null) {
                        dietOrderDetailShowVo.setTasteName(dietOrderDetailList.get(i).getTasteName());
                    }
                    dietOrderDetailShowVo.setSize(dietOrderDetailList.get(i).getSize() == null ? null : dietOrderDetailList.get(i).getSize().toString());
                    dietOrderDetailShowVo.setPrice(dietOrderDetailList.get(i).getSalePrice());
                    dietOrderDetailShowVo.setQuantity(dietOrderDetailList.get(i).getQuantity());
                    //dietOrderDetailShowVo.setTotalPrice(dietOrderDetailList.get(i).getReceivedAmount());
                    dietOrderDetailShowVo.setTotalPrice(dietOrderDetailList.get(i).getTotalAmount());
                    dietOrderDetailShowVo.setIsFreeOfCharge(dietOrderDetailList.get(i).getIsFreeOfCharge());
                    dietOrderDetailShowVo.setPackageCode(dietOrderDetailList.get(i).getPackageCode());
                    dietOrderDetailShowVo.setGroupId(dietOrderDetailList.get(i).getGroupId());
                    dietOrderDetailShowVo.setGroupName(dietOrderDetailList.get(i).getGroupName());
                    dietOrderDetailShowVo.setGroupType(dietOrderDetailList.get(i).getGroupType());
                    if (dietOrderDetailList.get(i).getLocalId() != null) {
                        dietOrderDetailShowVo.setLocalId(dietOrderDetailList.get(i).getLocalId());
                    }
                    if (dietOrderDetailList.get(i).getRetailLocalId() != null) {
                        dietOrderDetailShowVo.setRetailLocalId(dietOrderDetailList.get(i).getRetailLocalId());
                    }
                    totalAmount = totalAmount.add(dietOrderDetailList.get(i).getSalePrice().multiply(dietOrderDetailList.get(i).getQuantity()));
                    dietOrderDetailShowVoList.add(dietOrderDetailShowVo);
                }
                dietOrderInfoShowVo.setTotalAmount(totalAmount);
                dietOrderInfoShowVo.setDietOrderDetailShowVoList(dietOrderDetailShowVoList);
                dietOrderInfoShowVo.setPackageFee(dietOrderInfo.getPackageFee());
                return dietOrderInfoShowVo;
            } else {
                throw new Exception("无效的id");
            }
        } catch (Exception e) {
            //pos调用接口太频繁了，log打印的太多，所以暂时屏蔽了
            ServiceException se = new ServiceException("1004", "编辑失败", e.getMessage());
            LogUtil.logError("显示订单详情:" + e.getMessage());
            throw se;
        }
    }

    /**
     * 修改订单
     *
     * @author szq
     */
    public ApiRest editOrder(Map params) throws ParseException {
        ApiRest rest = new ApiRest();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (params.get("id") != null) {
            String orderInfoId = params.get("id").toString();
            BigInteger id = BigInteger.valueOf(Long.valueOf(orderInfoId));
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
            if (params.get("name") != null) {
                dietOrderInfo.setConsignee(params.get("name").toString());
            }
            if (params.get("phone") != null) {
                dietOrderInfo.setMobilePhone(params.get("phone").toString());
            }
            if (params.get("address") != null) {
                dietOrderInfo.setVipAddressName(params.get("address").toString());
            }
            if (params.get("remark") != null) {
                dietOrderInfo.setRemark(params.get("remark").toString());
            }
            if (params.get("appointmentDate") != null) {
                dietOrderInfo.setAppointmentDate(sim.parse(params.get("appointmentDate").toString()));
            }
            if (params.get("allocationDate") != null) {
                dietOrderInfo.setAllocationDate(sim.parse(params.get("allocationDate").toString()));
            }
            dietOrderInfo.setLastUpdateAt(new Date());
            dietOrderInfoMapper.update(dietOrderInfo);
            rest.setData(dietOrderInfo);
            rest.setIsSuccess(true);
            rest.setMessage("订单修改成功");
            return rest;
        }
        return rest;
    }

    /**
     * POS接口————电话外卖下单：
     * 需要接口将POS传的tenantCode和branchCode转变成branchId和tenantId
     * 1.branchId String
     * 2.tenantId String
     * 3.
     * <p/>
     * 电话订单不涉及到微信消息通知
     * <p/>
     * 迭代2功能：
     * 1.完成基本的业务功能，实现下单
     * 迭代3功能：
     * 1.在迭代2的功能上，加入促销，会员折扣，积分折扣的功能
     */
    public ApiRest saveTakeoutOrderByTelPOS(Map params) throws ParseException {
        ApiRest result = new ApiRest();
        JSONObject jsonObject = JSONObject.fromObject(params.get("json"));
        // 取出订单的详情信息,并将其从主信息中移除
        JSONArray jj = (JSONArray) jsonObject.get("JsonMap");
        jsonObject.remove("JsonMap");
        // 取出时间属性,转换后存放进主信息中(时间格式在转换时会异常)
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date allocationDate = null;
        if (jsonObject.get("allocationDate") != null) {
            allocationDate = sim.parse(jsonObject.get("allocationDate").toString());
        }
        //获取订单类型，用于判断是否是第三方外卖
        String orderMode = jsonObject.get("orderMode").toString();

        jsonObject.remove("allocationDate");
        // 将对象中的id移除
        jsonObject.remove("id");
        // 将json对象转换成javaBean domain对象
        DietOrderInfo dietOrderInfo = GsonUntil.jsonAsModel(jsonObject.toString(), DietOrderInfo.class);
        List<Map> dietOrderDetailList = GsonUntil.jsonAsList(jj.toString(), Map.class);
//            List<DietOrderDetail> dietOrderDetailList = GsonUntil.jsonAsList(URLDecoder.decode(jj.toString(), "UTF-8"), DietOrderDetail.class);
        if (dietOrderInfo != null && allocationDate != null) {
            dietOrderInfo.setAllocationDate(allocationDate);
            if (dietOrderInfo.getDiscountAmount() == null) {
                dietOrderInfo.setDiscountAmount(BigDecimal.ZERO);
            }
        }
        // 根据商户ID 和机构code 查找此单的商户和机构信息
        if (params.get("tenantId") != null && params.get("code") != null && dietOrderInfo != null && dietOrderDetailList != null) {
            dietOrderInfo.setTenantId(BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString())));
            Map<String, Object> param = new HashMap<>();
            param.put("tenantId", dietOrderInfo.getTenantId());
            param.put("code", params.get("code"));
            Branch branch = branchMapper.find(param);
            if (branch != null) {
                dietOrderInfo.setBranchId(branch.getId());
                dietOrderInfo.setBranchName(branch.getName());
            } else {
                result.setError("订单保存失败,机构不存在!");
                result.setMessage("订单保存失败,机构不存在!");
                result.setIsSuccess(false);
                return result;
            }
        } else {
            // 如果商户信息和机构信息不存在,则此单是不能保存的订单
            result.setError("订单保存失败,商户或者机构不存在!");
            result.setMessage("订单保存失败,商户或者机构不存在!");
            result.setIsSuccess(false);
            return result;
        }
        // 保存订单主信息
        //7位随机码，订单日上限9999999
        String code = tenantConfigService.getToday("W", 8);// vipService.getNextSelValue(SysConfig.SYS_PROMOTION_NUM, 7, new BigInteger(params.get("tenantId").toString()));
        //String today = Common.getToday().substring(2);
        dietOrderInfo.setOrderCode(code);
        BigDecimal score = new BigDecimal(0);

        dietOrderInfo.setIsDeleted(false);
        dietOrderInfo.setCreateAt(new Date());
        dietOrderInfo.setLastUpdateAt(new Date());
        if (params.get("orderResource") != null) {
            dietOrderInfo.setOrderResource(new Integer(params.get("orderResource").toString()));
        } else {
            dietOrderInfo.setOrderResource(2);
        }
        dietOrderInfoMapper.insert(dietOrderInfo);

        // 循环保存订单详细信息
        for (int i = 0; i < dietOrderDetailList.size(); i++) {
            DietOrderDetail dietOrderDetail = new DietOrderDetail();
            if (dietOrderDetailList.get(i).get("isPackage") != null) {
                if ("1".equals(dietOrderDetailList.get(i).get("isPackage").toString()) || "true".equals(dietOrderDetailList.get(i).get("isPackage").toString())) {
                    dietOrderDetail.setIsPackage(true);
                } else {
                    dietOrderDetail.setIsPackage(false);
                }
            }
            if (dietOrderDetailList.get(i).get("retailLocalId") != null) {
                dietOrderDetail.setRetailLocalId(dietOrderDetailList.get(i).get("retailLocalId").toString());
            }
//                //设置订单详情的套餐属性
//                if(dietOrderDetail.isPackage()){
////                    Package aPackage = Package.find("from Package p where p.isDeleted = false and p.status = 1 and p.id = " + dietOrderDetailList.get(i).packageId);
//                    //goodsType 2 代表套餐......
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("id", dietOrderDetailList.get(i).get("packageId"));
//                    param.put("goodsType", 2);
//                    Goods aPackage = goodsMapper.findByCondition(param);
//                    if(aPackage != null){
//                        dietOrderDetail.setPackageId(aPackage.getId());
//                        dietOrderDetail.setPackageName(aPackage.getGoodsName());
//                        //TODO：迭代2：不涉及到促销，积分，会员折扣的业务功能
//                        dietOrderDetail.setSalePrice(new BigDecimal(dietOrderDetailList.get(i).get("salePrice").toString()));
//                        dietOrderDetail.setSalePriceActual(new BigDecimal(dietOrderDetailList.get(i).get("salePriceActual").toString()));
//                    } else {
//                        String message = "套餐：<套餐ID" + dietOrderDetailList.get(i).get("isPackage") + ",套餐名称:" + dietOrderDetailList.get(i).get("packageName") + ">无法找到，请核对后再下单";
//                        throw new ServiceException(message);
//                    }
//                } else { //设置订单详情的单品属性
            //如果是美团或饿了么订单，直接设置名称和价格
            if (ORDERMODE_MEITUAN.equals(orderMode) || ORDERMODE_ELEME.equals(orderMode)) {
                String goodsTypeName = "";
                if (ORDERMODE_MEITUAN.equals(orderMode)) {
                    goodsTypeName = "美团外卖";
                } else {
                    goodsTypeName = "饿了么外卖";
                }
                Map<String, Object> param = new HashMap<>();
                param.put("goodsName", goodsTypeName);
                param.put("tenantId", dietOrderInfo.getTenantId());
                //如果是第三方外卖，goodId设为一个统一的预设第三方商品
                Goods goods = goodsMapper.findByCondition(param);
                if (goods != null) {
                    dietOrderDetail.setGoodsId(goods.getId());
                }
                dietOrderDetail.setGoodsName(dietOrderDetailList.get(i).get("goodsName").toString());
                dietOrderDetail.setSalePrice(new BigDecimal(dietOrderDetailList.get(i).get("salePrice").toString()));
                dietOrderDetail.setSalePriceActual(new BigDecimal(dietOrderDetailList.get(i).get("salePriceActual").toString()));
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("id", dietOrderDetailList.get(i).get("goodsId"));
                Goods goods = goodsMapper.findByCondition(param);
                if (goods != null) {
                    dietOrderDetail.setGoodsId(goods.getId());
                    dietOrderDetail.setGoodsName(goods.getGoodsName());
                    //TODO：迭代2：不涉及到促销，积分，会员折扣的业务功能
                    if (score != null && goods.getScoreValue() != null) {
                        score = score.add(goods.getScoreValue());
                    }
                    dietOrderDetail.setSalePrice(new BigDecimal(dietOrderDetailList.get(i).get("salePrice").toString()));
                    dietOrderDetail.setSalePriceActual(new BigDecimal(dietOrderDetailList.get(i).get("salePriceActual").toString()));
                } else {
                    String message = "商品：<商品ID:" + dietOrderDetailList.get(i).get("goodsId") + ",名称:" + dietOrderDetailList.get(i).get("goodsName") + ">无法找到，请核对后再下单";
                    throw new ServiceException(message);
                }
            }

//                }
            dietOrderDetail.setStatus(0);
            if (dietOrderDetailList.get(i).get("isFreeOfCharge") != null && ("1".equals(dietOrderDetailList.get(i).get("isFreeOfCharge").toString()) || "true".equals(dietOrderDetailList.get(i).get("isFreeOfCharge").toString()))) {
                dietOrderDetail.setIsFreeOfCharge(true);
            } else {
                dietOrderDetail.setIsFreeOfCharge(false);
            }
            if (dietOrderDetailList.get(i).get("isRefund") != null && ("1".equals(dietOrderDetailList.get(i).get("isRefund").toString()) || "true".equals(dietOrderDetailList.get(i).get("isRefund").toString()))) {
                dietOrderDetail.setIsRefund(true);
            } else {
                dietOrderDetail.setIsRefund(false);
            }
            dietOrderDetail.setQuantity(new BigDecimal(dietOrderDetailList.get(i).get("quantity").toString()));
            dietOrderDetail.setTotalAmount(new BigDecimal(dietOrderDetailList.get(i).get("totalAmount").toString()));
            dietOrderDetail.setReceivedAmount(new BigDecimal(dietOrderDetailList.get(i).get("receivedAmount").toString()));
            if (dietOrderDetailList.get(i).get("taste") != null && !"".equals(dietOrderDetailList.get(i).get("taste"))) {
                dietOrderDetail.setTaste(Integer.parseInt(dietOrderDetailList.get(i).get("taste").toString()));
            } else {
                if (dietOrderDetailList.get(i).get("tasteName") != null) {
                    dietOrderDetail.setTasteName(dietOrderDetailList.get(i).get("tasteName").toString());
                }
            }
            if (dietOrderDetailList.get(i).get("size") != null && !"".equals(dietOrderDetailList.get(i).get("size"))) {
                dietOrderDetail.setSize(new BigDecimal(dietOrderDetailList.get(i).get("size").toString()).intValue());
            } else {
                if (dietOrderDetailList.get(i).get("sizeName") != null) {
                    dietOrderDetail.setSizeName(dietOrderDetailList.get(i).get("sizeName").toString());
                }
            }
            dietOrderDetail.setDietOrderInfoId(dietOrderInfo.getId());
            dietOrderDetail.setCreateAt(new Date());
            dietOrderDetail.setIsDeleted(false);
            if (dietOrderDetailList.get(i).get("localId") != null) {
                dietOrderDetail.setLocalId(new BigInteger(dietOrderDetailList.get(i).get("localId").toString()));
            }
            dietOrderDetailMapper.insert(dietOrderDetail);
        }
        //设置订单来源，默认为 2 POS
        if (params.get("orderResource") != null && !"".equals(params.get("orderResource"))) {
            dietOrderInfo.setOrderResource(Integer.parseInt(params.get("orderResource").toString()));
        } else {
            dietOrderInfo.setOrderResource(2);
        }
        if (score != null) {
            dietOrderInfo.setCreateScore((int) Float.parseFloat(score.toString()));
        }
        dietOrderInfo.setCreateAt(new Date());
        dietOrderInfo.setLastUpdateAt(new Date());
        dietOrderInfoMapper.update(dietOrderInfo);
        result.setIsSuccess(true);
        result.setMessage("下单成功");
        return result;
    }

    /**
     * POS 订单状态 修改 接口方法
     * <p/>
     * 暂未添加消息通知
     * <p/>
     * 修改状态1：（下单 —> 已接单，提交厨房 ）
     * 修改状态2：（下单 —> 已拒绝）
     * 修改状态3：（已接单，提交厨房 —> 正在派送）
     * 修改状态4：（正在派送 —> 已派送）
     * 修改状态5：（已派送 —> 已完成（结束））
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJSON changeOrderStatusByTelPOS(Map params) throws IOException {
        String status = params.get("order_status").toString();
        String orderId = params.get("dietOrderInfoId").toString();
        BigInteger dietOrderInfoId = BigInteger.valueOf(Long.valueOf(orderId));
        Map<String, Object> param = new HashMap<>();
        param.put("id", dietOrderInfoId);
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
        Validate.notNull(dietOrderInfo, "订单不存在！");
        //下单 —> 已接单，提交厨房
        if (status.equals("ORDERSTATUS_YIJIEDAN")) {
            int orderMode = dietOrderInfo.getOrderMode();
            if (orderMode != 6 && orderMode != 7) {
                dietOrderInfo.setConfirmOrderSource(1);
            }
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_YIJIEDAN);
            //TODO:需要添加消息通知
        }
        //已接单，提交厨房 —> 正在派送
        if (status.equals("ORDERSTATUS_PAISONGZHONG")) {
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_PAISONGZHONG);
        }
        //正在派送 —> 已派送（已完成）
        if (status.equals("ORDERSTATUS_YIPAISONG")) {
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_YIPAISONG);
            dietOrderInfo.setArriveDate(new Date());

            // 开始写入流水
            Integer payStatus = dietOrderInfo.getPayStatus();
            Integer payWay = dietOrderInfo.getPayWay();
            if (payStatus != null && payWay != null && payStatus == 1 && (payWay == 1 || payWay == 2 || payWay == 4)) {
                SaleFlowUtils.writeSaleFlow(dietOrderInfo);
//                Map<String, Object> parameters = new HashMap<String, Object>();
//                parameters.put("id", dietOrderInfo.getId());
//                storeOrderService.onlineSaleStore(parameters);
                StockUtils.handleOnlineStock(dietOrderInfo.getId());
            }
        }
        //下单 —> 已拒绝
        if (status.equals("ORDERSTATUS_REFUSED")) {
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_REFUSED);
            /*if ("1".equals(payRefund(dietOrderInfo))) {
                dietOrderInfo.setIsRefund(true);
            }*/
            OrderUtils.refundSafe(dietOrderInfo);
            dietOrderInfo.setIsRefund(true);
        }
        dietOrderInfo.setLastUpdateAt(new Date());
        dietOrderInfoMapper.update(dietOrderInfo);
        //修改订单详情状态
        if (params.get("detailStatus") != null) {
            Map<String, Object> param1 = new HashMap<>();
            param1.put("dietOrderInfoId", dietOrderInfoId);
            List<DietOrderDetail> list = dietOrderDetailMapper.select(param1);
            if (list != null) {
                for (DietOrderDetail detail : list) {
                    Map<String, Object> param2 = new HashMap<>();
                    param2.put("id", detail.getId());
                    param2.put("status", params.get("detailStatus"));
                    dietOrderDetailMapper.update(param2);
                }
            }
        }

        ResultJSON result = new ResultJSON();
        result.setMsg("状态更新成功");
        result.setSuccess("true");
        return result;
    }

    /**
     * POS 支付状态修改 接口方法
     * <p/>
     * 暂未添加消息通知
     * <p/>
     * 修改状态1：（未支付 —> 已支付 ）
     */
    public ResultJSON changePayStatusByTelPOS(Map<String, String> params) {
        ResultJSON result = new ResultJSON();
        String status = params.get("order_status");
        String dietOrderInfoId = params.get("dietOrderInfoId");
        if (StringUtils.isNotBlank(dietOrderInfoId)) {
            Map<String, Object> param = new HashMap<>();
            param.put("id", NumberUtils.createBigInteger(dietOrderInfoId));
            DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
            if (dietOrderInfo != null) {
                //未支付 —> 已支付
                if (status.equals("PAYSTATUS_YES")) {
                    dietOrderInfo.setPayStatus(DietOrderInfoStatus.PAYSTATUS_YES);
                    String discountAmount = params.get("discountAmount");
                    if (StringUtils.isNotBlank(discountAmount)) {
                        BigDecimal bigDecimalDiscountAmount = NumberUtils.createBigDecimal(discountAmount);
                        dietOrderInfo.setDiscountAmount(bigDecimalDiscountAmount);
                        dietOrderInfo.setReceivedAmount(dietOrderInfo.getTotalAmount().subtract(bigDecimalDiscountAmount));
                    }
                    dietOrderInfo.setPayAt(new Date());
                    dietOrderInfo.setLastUpdateAt(new Date());
                    dietOrderInfoMapper.update(dietOrderInfo);
                }
            } else {
                result.setMsg("找不到指定订单，请重新刷新核对后再提交");
                result.setSuccess("false");
                return result;
            }
            result.setMsg("状态更新成功");
            result.setSuccess("true");
        }
        return result;
    }

    /**
     * 根据时间范围选择所有订单 POS的接口方法
     */
    public ResultJSON listOrderInfoByTelPOS(Map params) {
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", params.get("tenantId"));
        param.put("code", params.get("code"));
        Branch branch = branchMapper.find(param);
        if (branch != null) {
            params.put("branchId", branch.getId());
        } else {
            params.put("branchId", 0);
        }
        params.put("special", 1);
        List<DietOrderInfo> countList = dietOrderInfoMapper.select(params);
        if (params.get("page") != null && params.get("rows") != null) {
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<DietOrderInfo> list = dietOrderInfoMapper.select(params);
        //未接x单，已接x单,已出品x单，已完成x单，异常关闭x单。
        int nm1 = 0;
        int nm2 = 0;
        int nm3 = 0;
        int nm4 = 0;
        int nm5 = 0;
        //如果是微餐厅的订单，加上openid
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getOrderMode() == 1 || list.get(i).getOrderMode() == 4 || list.get(i).getOrderMode() == 5) {
                if (list.get(i).getVipId() != null && list.get(i).getVipId().doubleValue() != 0) {
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("id", list.get(i).getVipId());
                    Vip vip = vipMapper.findById(param1);
                    if (vip != null && vip.getOriginalId() != null) {
                        list.get(i).setVipOpenid(vip.getOriginalId());
                    }
                }
            }
            if (list.get(i).getOrderStatus() != null) {
                if (list.get(i).getOrderStatus() == 1) {
                    nm1++;
                }
                if (list.get(i).getOrderStatus() == 2) {
                    nm2++;
                }
                if (list.get(i).getOrderStatus() == 3) {
                    nm3++;
                }
                if (list.get(i).getOrderStatus() == 4) {
                    nm4++;
                }
                if (list.get(i).getOrderStatus() == 9) {
                    nm5++;
                }
            }
        }
        BigDecimal totalAmount = dietOrderInfoMapper.sumTotalAmount(params);
        Map footMap = new HashMap();
        map.put("num1", nm1);
        map.put("num2", nm2);
        map.put("num3", nm3);
        map.put("num4", nm4);
        map.put("num5", nm5);
        footMap.put("receivedAmount", totalAmount);
        map.put("footer", footMap);
        map.put("rows", list);
        map.put("total", countList.size());
        resultJSON.setJsonMap(map);
        resultJSON.setMsg("成功");
        return resultJSON;
    }

    /**
     * 新增订单详情
     *
     * @param params
     */
    public ApiRest addOrderDetail(Map params) {
        ApiRest rest = new ApiRest();
        if (params.get("json") != null && params.get("orderId") != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("orderId"));
            DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(param);
            JSONObject jsonObject = JSONObject.fromObject(params.get("json"));
            // 取出订单的详情信息,并将其从主信息中移除
            JSONArray jj = JSONArray.fromObject(jsonObject.get("JsonMap"));
            List<Map> dietOrderDetailList = GsonUntil.jsonAsList(jj.toString(), Map.class);
            // 循环保存订单详细信息

            for (int i = 0; i < dietOrderDetailList.size(); i++) {
                DietOrderDetail dietOrderDetail = new DietOrderDetail();
                if ("1".equals(dietOrderDetailList.get(i).get("isPackage")) || "true".equals(dietOrderDetailList.get(i).get("isPackage"))) {
                    dietOrderDetail.setIsPackage(true);
                } else {
                    dietOrderDetail.setIsPackage(false);
                }
                if (dietOrderDetail.isPackage()) {
                    //goodsType 2 代表套餐......
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("id", dietOrderDetailList.get(i).get("packageId"));
                    param1.put("goodsType", 2);
                    Goods aPackage = goodsMapper.findByCondition(param1);
                    if (aPackage != null) {
                        dietOrderDetail.setPackageId(aPackage.getId());
                        dietOrderDetail.setPackageName(aPackage.getGoodsName());
                    }
                }
                dietOrderDetail.setGoodsName(dietOrderDetailList.get(i).get("goodsName").toString());
                dietOrderDetail.setSalePrice(new BigDecimal(dietOrderDetailList.get(i).get("salePrice").toString()));
                dietOrderDetail.setSalePriceActual(new BigDecimal(dietOrderDetailList.get(i).get("salePriceActual").toString()));
                dietOrderDetail.setGoodsId(new BigDecimal(dietOrderDetailList.get(i).get("goodsId").toString()).toBigInteger());
                Map<String, Object> param1 = new HashMap<>();
                param1.put("id", dietOrderDetailList.get(i).get("goodsId"));
                Goods goods = goodsMapper.findByCondition(param1);
                if (goods != null && goods.getScoreValue() != null && dietOrderDetailList.get(i).get("quantity") != null && dietOrderInfo.getCreateScore() != null) {
                    int createScore = goods.getScoreValue().intValue() * (new BigDecimal(dietOrderDetailList.get(i).get("quantity").toString()).intValue()) + dietOrderInfo.getCreateScore();
                    dietOrderInfo.setCreateScore(createScore);
                }
                if (dietOrderDetailList.get(i).get("isFreeOfCharge") != null) {
                    if ("1".equals(dietOrderDetailList.get(i).get("isFreeOfCharge")) || "true".equals(dietOrderDetailList.get(i).get("isFreeOfCharge"))) {
                        dietOrderDetail.setIsFreeOfCharge(true);
                    } else {
                        dietOrderDetail.setIsFreeOfCharge(false);
                    }
                }
                if (dietOrderDetailList.get(i).get("isRefund") != null) {
                    if ("1".equals(dietOrderDetailList.get(i).get("isRefund")) || "true".equals(dietOrderDetailList.get(i).get("isRefund"))) {
                        dietOrderDetail.setIsRefund(true);
                    } else {
                        dietOrderDetail.setIsRefund(false);
                    }
                }
                dietOrderDetail.setQuantity(new BigDecimal(dietOrderDetailList.get(i).get("quantity").toString()));
                dietOrderDetail.setTotalAmount(new BigDecimal(dietOrderDetailList.get(i).get("totalAmount").toString()));
                dietOrderDetail.setReceivedAmount(new BigDecimal(dietOrderDetailList.get(i).get("receivedAmount").toString()));
                if (dietOrderDetailList.get(i).get("taste") != null) {
                    dietOrderDetail.setTaste((int) Float.parseFloat(dietOrderDetailList.get(i).get("taste").toString()));
                }
                if (dietOrderDetailList.get(i).get("tasteName") != null) {
                    dietOrderDetail.setTasteName(dietOrderDetailList.get(i).get("tasteName").toString());
                }
                if (dietOrderDetailList.get(i).get("size") != null) {
                    dietOrderDetail.setSize((int) Float.parseFloat(dietOrderDetailList.get(i).get("size").toString()));
                } else {
                    dietOrderDetail.setSizeName(dietOrderDetailList.get(i).get("sizeName").toString());
                }
                dietOrderDetail.setDietOrderInfoId(dietOrderInfo.getId());
                dietOrderDetail.setCreateAt(new Date());
                if (dietOrderDetailList.get(i).get("status") != null) {
                    dietOrderDetail.setStatus((int) Float.parseFloat(dietOrderDetailList.get(i).get("status").toString()));
                } else {
                    dietOrderDetail.setStatus(0);
                }
                dietOrderDetailMapper.insert(dietOrderDetail);
                BigDecimal total = new BigDecimal(dietOrderDetailList.get(i).get("salePrice").toString()).multiply(new BigDecimal(dietOrderDetailList.get(i).get("quantity").toString()));
                //累计
                dietOrderInfo.setTotalAmount(dietOrderInfo.getTotalAmount().add(total));
                dietOrderInfo.setReceivedAmount(dietOrderInfo.getReceivedAmount().add(total));
                dietOrderInfo.setAmount(dietOrderInfo.getAmount() + (int) Float.parseFloat(dietOrderDetailList.get(i).get("quantity").toString()));

            }
            dietOrderInfo.setLastUpdateAt(new Date());
            dietOrderInfo.setOrderStatus(DietOrderInfoStatus.ORDERSTATUS_YIXIADAN);
            dietOrderInfoMapper.update(dietOrderInfo);
            rest.setIsSuccess(true);
            rest.setMessage("订单加菜成功!");
        } else {
            rest.setIsSuccess(false);
            rest.setMessage("orderId或json数据不存在!");
        }
        return rest;
    }

    /**
     * pos用验证接口，扫码与存储信息一致(有效期60s)
     *
     * @param
     * @return
     */
    public ResultJSON checkCodeWithPos(Map params) {
        ResultJSON resultJSON = new ResultJSON();
        Long nowMills = System.currentTimeMillis();
        if (params.get("vipCode") != null && params.get("code") != null && params.get("tenantId") != null) {
            resultJSON.setSuccess("0");
            resultJSON.setMsg("验证码不存在或已过期!");
            Map<String, Object> map = new HashMap<>();
            map.put("vipCode", params.get("vipCode"));
            Vip vip = vipMapper.findByCondition(map);
            if (vip != null && vip.getTenantId().equals(new BigInteger(params.get("tenantId").toString()))) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("vipId", vip.getId());
                map1.put("code", params.get("code"));
                List<WxPosCode> wxPosCodes = wxPosCodeMapper.getList(map1);
                if (wxPosCodes != null) {
                    for (WxPosCode wxPosCode : wxPosCodes) {
                        //60s内有效
                        Long createAt = wxPosCode.getCreateAt().getTime();
                        if (nowMills - createAt <= 60 * 1000) {
                            resultJSON.setSuccess("true");
                            resultJSON.setIsSuccess(true);
                            resultJSON.setMsg("验证成功!");
                        }
                    }
                }
            } else {
                resultJSON.setSuccess("false");
                resultJSON.setIsSuccess(false);
                resultJSON.setMsg("会员与商户不匹配!");
            }

        } else {
            resultJSON.setSuccess("false");
            resultJSON.setIsSuccess(false);
            resultJSON.setMsg("tenantId、vipCode或code不存在!");
        }
        return resultJSON;

    }

    @Transactional(readOnly = true)
    public ApiRest obtainDietOrderInfos(BigInteger tenantId, BigInteger branchId, List<BigInteger> dietOrderInfoIds) {
        List<DietOrderInfo> dietOrderInfos = dietOrderInfoMapper.findAllByTenantIdAndBranchIdAndIdInList(tenantId, branchId, dietOrderInfoIds);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(dietOrderInfos);
        apiRest.setMessage("获取订单信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
