package erp.chain.service.o2o;

import com.saas.common.util.CacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.Category;
import erp.chain.domain.Goods;
import erp.chain.domain.GoodsUnit;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.ElemeGoodsAttribute;
import erp.chain.domain.o2o.vo.ElemeGoodsNewSpec;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CategoryMapper;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsUnitMapper;
import erp.chain.mapper.o2o.DietOrderDetailMapper;
import erp.chain.mapper.o2o.DietOrderInfoMapper;
import erp.chain.mapper.o2o.ElemeGoodsMappingMapper;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by liuyandong on 2017/5/16.
 */
@Service
public class ElemeService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsUnitMapper goodsUnitMapper;
    @Autowired
    private ElemeGoodsMappingMapper elemeGoodsMappingMapper;

    @Transactional
    public ApiRest doBindingRestaurant(String tenantId, String branchId, Long shopId) throws Exception {
        ApiRest apiRest = new ApiRest();
        BigInteger bigIntegerTenantId = BigInteger.valueOf(Long.valueOf(tenantId));
        BigInteger bigIntegerBranchId = BigInteger.valueOf(Long.valueOf(branchId));
        BigInteger bigIntegerShopId = BigInteger.valueOf(shopId);
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(bigIntegerTenantId, bigIntegerBranchId);
        Validate.notNull(branch, "机构不存在！");

        String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
        String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("shopId", shopId);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("openId", tenantId + "Z" + branchId);
        parameters.put("properties", properties);
        ApiRest callElemeSystemRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.shop.updateShop", tenantId, branchId, branch.getElemeAccountType().toString(), parameters);
        Validate.isTrue(callElemeSystemRest.getIsSuccess(), callElemeSystemRest.getError());

        Map<String, String> params = new HashMap<String, String>();
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("shopId", shopId.toString());
        ApiRest saveElemeBranchMappingRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "eleme", "saveElemeBranchMapping", params);
        Validate.isTrue(saveElemeBranchMappingRest.getIsSuccess(), saveElemeBranchMappingRest.getError());

        branchMapper.clearElemeBindingRestaurant(bigIntegerShopId, bigIntegerBranchId);

        branch.setShopId(bigIntegerShopId);
        branchMapper.update(branch);

        apiRest.setIsSuccess(true);
        apiRest.setMessage("机构绑定成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public DietOrderInfo findDietOrderInfoById(BigInteger tenantId, BigInteger branchId, BigInteger orderId) {
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndId(tenantId, branchId, orderId);
        Validate.notNull(dietOrderInfo, "ID为：" + orderId + "的订单不存在！");
        return dietOrderInfo;
    }

    @Transactional(readOnly = true)
    public List<DietOrderInfo> findDietOrderInfoByIds(BigInteger tenantId, BigInteger branchId, String[] dietOrderInfoIds) {
        List<BigInteger> ids = new ArrayList<BigInteger>();
        for (String dietOrderInfoId : dietOrderInfoIds) {
            ids.add(BigInteger.valueOf(Long.valueOf(dietOrderInfoId)));
        }
        List<DietOrderInfo> dietOrderInfos = dietOrderInfoMapper.findAllByTenantIdAndBranchIdAndIdInList(tenantId, branchId, ids);
        Validate.notEmpty(dietOrderInfos, "订单不存在！");
        return dietOrderInfos;
    }


    /**
     * 订单生效
     *
     * @param shopId
     * @param elemeOrderString
     * @return
     */
    @Transactional
    public ApiRest saveElemeOrder(BigInteger shopId, String elemeOrderString, String requestId, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject elemeOrderJson = JSONObject.fromObject(elemeOrderString);
        String id = elemeOrderJson.getString("id");
        String key = "_eleme.message.callback_" + id + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");
            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("饿了么订单保存成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                JSONArray groups = elemeOrderJson.getJSONArray("groups");
                JSONArray phoneListArray = elemeOrderJson.getJSONArray("phoneList");
                elemeOrderJson.remove("groups");
                elemeOrderJson.remove("phoneList");
                elemeOrderJson.remove("id");

                /*String description = elemeOrderJson.optString("description");
                if (StringUtils.isNotBlank(description)) {
                    Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(description);
                    if (matcher.find()) {
                        description = matcher.replaceAll("");
                        elemeOrderJson.put("description", description);
                    }
                }*/

                ElemeOrder elemeOrder = GsonUntil.jsonToObject(elemeOrderJson.toString(), ElemeOrder.class, "yyyy-MM-dd'T'HH:mm:ss");

                int phoneListArraySize = phoneListArray.size();
                if (phoneListArraySize > 2) {
                    phoneListArraySize = 2;
                }
                String phoneList = "";
                for (int phoneListArrayIndex = 0; phoneListArrayIndex < phoneListArraySize; phoneListArrayIndex++) {
                    phoneList = phoneList + "," + phoneListArray.getString(phoneListArrayIndex);
                }

                if (StringUtils.isNotBlank(phoneList)) {
                    phoneList = phoneList.substring(1);
                }
                elemeOrder.setOrderId(id);
                elemeOrder.setPhoneList(phoneList);
//            elemeOrder.save flush: true

                int groupsSize = groups.size();
                List<ElemeGoodsItem> allElemeGoodsItems = new ArrayList<ElemeGoodsItem>();
                for (int groupsIndex = 0; groupsIndex < groupsSize; groupsIndex++) {
                    JSONObject groupJsonObject = groups.getJSONObject(groupsIndex);
                    String groupName = groupJsonObject.getString("name");
                    String groupType = groupJsonObject.getString("type");
                    JSONArray items = groupJsonObject.getJSONArray("items");
                    int itemsSize = items.size();
                    String groupId = UUID.randomUUID().toString();
                    for (int itemsIndex = 0; itemsIndex < itemsSize; itemsIndex++) {
                        JSONObject item = items.getJSONObject(itemsIndex);
                        ElemeGoodsItem elemeGoodsItem = new ElemeGoodsItem();
                        elemeGoodsItem.setGroupId(groupId);
                        elemeGoodsItem.setItemId(BigInteger.valueOf(item.getLong("id")));
                        elemeGoodsItem.setSkuId(BigInteger.valueOf(item.getLong("skuId")));
                        elemeGoodsItem.setName(item.getString("name"));
                        elemeGoodsItem.setCategoryId(item.getLong("categoryId"));
                        elemeGoodsItem.setPrice(item.getDouble("price"));
                        elemeGoodsItem.setQuantity(item.getInt("quantity"));
                        elemeGoodsItem.setTotal(item.getDouble("total"));
                        JSONArray newSpecsJsonArray = item.optJSONArray("newSpecs");
                        if (newSpecsJsonArray != null) {
                            List<ElemeGoodsNewSpec> elemeGoodsNewSpecs = new ArrayList<ElemeGoodsNewSpec>();
                            int size = newSpecsJsonArray.size();
                            for (int index = 0; index < size; index++) {
                                ElemeGoodsNewSpec elemeGoodsNewSpec = new ElemeGoodsNewSpec();
                                JSONObject elemeGoodsNewSpecJsonObject = newSpecsJsonArray.getJSONObject(index);
                                elemeGoodsNewSpec.setName(elemeGoodsNewSpecJsonObject.optString("name"));
                                elemeGoodsNewSpec.setValue(elemeGoodsNewSpecJsonObject.optString("value"));
                                elemeGoodsNewSpecs.add(elemeGoodsNewSpec);
                            }
                            elemeGoodsItem.setNewSpecs(elemeGoodsNewSpecs);
                        }

                        JSONArray attributesJsonArray = item.optJSONArray("attributes");
                        if (attributesJsonArray != null) {
                            List<ElemeGoodsAttribute> elemeGoodsAttributes = new ArrayList<ElemeGoodsAttribute>();
                            int size = attributesJsonArray.size();
                            for (int index = 0; index < size; index++) {
                                ElemeGoodsAttribute elemeGoodsAttribute = new ElemeGoodsAttribute();
                                JSONObject elemeGoodsAttributeJsonObject = attributesJsonArray.getJSONObject(index);
                                elemeGoodsAttribute.setName(elemeGoodsAttributeJsonObject.optString("name"));
                                elemeGoodsAttribute.setValue(elemeGoodsAttributeJsonObject.optString("value"));
                                elemeGoodsAttributes.add(elemeGoodsAttribute);
                            }
                            elemeGoodsItem.setAttributes(elemeGoodsAttributes);
                        }
                        elemeGoodsItem.setGroupName(groupName);
                        elemeGoodsItem.setGroupType(groupType);
                        allElemeGoodsItems.add(elemeGoodsItem);
                    }
                }

                String openId = elemeOrderJson.getString("openId");
                String[] tenantIdAndBranchIdArray = openId.split("Z");
                BigInteger tenantId = BigInteger.valueOf(Long.valueOf(tenantIdAndBranchIdArray[0]));
                BigInteger branchId = BigInteger.valueOf(Long.valueOf(tenantIdAndBranchIdArray[1]));
                Branch branch = branchMapper.findByTenantIdAndIdAndShopId(tenantId, branchId, shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = new DietOrderInfo();
                dietOrderInfo.setOrderResource(4);
                dietOrderInfo.setBranchId(branch.getId());
                dietOrderInfo.setBranchName(branch.getName());
                dietOrderInfo.setTenantId(branch.getTenantId());
                dietOrderInfo.setOrderMode(7);
                dietOrderInfo.setOrderCode("E" + elemeOrder.getOrderId());
                dietOrderInfo.setAmount(1);
                dietOrderInfo.setMeituanDaySeq(String.valueOf(elemeOrder.getDaySn()));

                // 饿了么传过来的预计送达时间
                Date deliverTime = elemeOrder.getDeliverTime();
                // 饿了么传过来的预计送达时间为不为空，将客户外卖时填写的配送时间设置为饿了么传过来的预计送达时间，
                // 如果饿了么传过来的预计送达时间为空，将客户外卖时填写的配送时间设置为饿了么传过来的订单生效时间
                if (deliverTime != null) {
                    dietOrderInfo.setAllocationDate(elemeOrder.getDeliverTime());
                } else {
                    dietOrderInfo.setAllocationDate(elemeOrder.getActiveAt());
                }
                // 设置付款时间为饿了么传过来的订单生效时间
                dietOrderInfo.setPayAt(elemeOrder.getActiveAt());
                dietOrderInfo.setRemark(elemeOrder.getDescription());
                dietOrderInfo.setConsignee(elemeOrder.getConsignee());
                dietOrderInfo.setCreateAt(elemeOrder.getCreatedAt());
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfo.setMobilePhone(elemeOrder.getPhoneList());
                if ("unprocessed".equals(elemeOrder.getStatus())) {
                    dietOrderInfo.setOrderStatus(1);
                }
                if (elemeOrder.isOnlinePaid()) {
                    dietOrderInfo.setPayStatus(1);
                    dietOrderInfo.setPayWay(7);
                } else {
                    dietOrderInfo.setPayStatus(0);
                    dietOrderInfo.setPayWay(3);
                }
                dietOrderInfo.setVipAddressName(elemeOrder.getAddress());
                dietOrderInfo.setTotalAmount(BigDecimal.valueOf(elemeOrder.getOriginalPrice()));
                dietOrderInfo.setReceivedAmount(BigDecimal.valueOf(elemeOrder.getTotalPrice()));

//                BigDecimal activityTotal = BigDecimal.valueOf(elemeOrder.getActivityTotal() * -1);
//                dietOrderInfo.setDiscountAmount(activityTotal);

                BigDecimal shopPart = BigDecimal.valueOf(elemeOrder.getElemePart()).abs();
                dietOrderInfo.setDiscountAmount(shopPart);

                dietOrderInfo.setIncome(BigDecimal.valueOf(elemeOrder.getIncome()));
                dietOrderInfo.setCreateScore(0);
                dietOrderInfo.setDeliverFee(BigDecimal.valueOf(elemeOrder.getDeliverFee()));
                dietOrderInfo.setPackageFee(BigDecimal.valueOf(elemeOrder.getPackageFee()));
                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfoMapper.insert(dietOrderInfo);


                List<BigInteger> elemeGoodsIds = new ArrayList<BigInteger>();
                for (ElemeGoodsItem elemeGoodsItem : allElemeGoodsItems) {
                    elemeGoodsIds.add(elemeGoodsItem.getItemId());
                }
                List<Map<String, Object>> elemeGoodsInfos = goodsMapper.findAllElemeGoodsInfos(tenantId, branchId, elemeGoodsIds);

                Map<BigInteger, Map<String, Object>> elemeGoodsInfoMap = new HashMap<BigInteger, Map<String, Object>>();
                for (Map<String, Object> elemeGoodsInfo : elemeGoodsInfos) {
                    BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(elemeGoodsInfo, "elemeGoodsId"));
                    elemeGoodsInfoMap.put(goodsId, elemeGoodsInfo);
                }

                Goods elemeGoods = null;

                BigInteger elemePackageFeeGoodsIdId = BigInteger.valueOf(-70000);
                BigInteger ourPackageFeeGoodsId = BigInteger.valueOf(-9999);
                for (ElemeGoodsItem elemeGoodsItem : allElemeGoodsItems) {
                    DietOrderDetail dietOrderDetail = new DietOrderDetail();
                    dietOrderDetail.setDietOrderInfoId(dietOrderInfo.getId());

                    BigInteger itemId = elemeGoodsItem.getItemId();
                    BigInteger goodsId = null;
                    if (itemId.compareTo(elemePackageFeeGoodsIdId) == 0) {
                        goodsId = ourPackageFeeGoodsId;
                    } else {
                        Map<String, Object> elemeGoodsInfo = elemeGoodsInfoMap.get(itemId);
                        if (elemeGoodsInfo == null) {
                            if (elemeGoods == null) {
                                elemeGoods = goodsMapper.findByTenantIdAndBranchIdGoodsNameAndIsDeleted(tenantId, branchId, "饿了么外卖");
                            }
                            if (elemeGoods == null) {
                                elemeGoods = addDefaultGoods(tenantId, branchId);
                            }
                            goodsId = elemeGoods.getId();
                        } else {
                            goodsId = BigInteger.valueOf(MapUtils.getLongValue(elemeGoodsInfo, "id"));
                        }
                    }

                    dietOrderDetail.setGoodsId(goodsId);

                    String goodsName = elemeGoodsItem.getName();
                    goodsName = goodsName.split("\\[")[0];

                    dietOrderDetail.setGoodsName(goodsName);
                    dietOrderDetail.setSalePrice(BigDecimal.valueOf(elemeGoodsItem.getPrice()));
                    dietOrderDetail.setSalePriceActual(BigDecimal.valueOf(elemeGoodsItem.getPrice()));
                    dietOrderDetail.setQuantity(new BigDecimal(elemeGoodsItem.getQuantity()));
                    dietOrderDetail.setTotalAmount(BigDecimal.valueOf(elemeGoodsItem.getTotal()));
                    dietOrderDetail.setReceivedAmount(BigDecimal.valueOf(elemeGoodsItem.getTotal()));
                    dietOrderDetail.setStatus(0);
                    dietOrderDetail.setTasteName(elemeGoodsItem.getTasteName());
                    dietOrderDetail.setGroupId(elemeGoodsItem.getGroupId());
                    dietOrderDetail.setGroupName(elemeGoodsItem.getGroupName());
                    dietOrderDetail.setGroupType(elemeGoodsItem.getGroupType());
                    dietOrderDetailMapper.insert(dietOrderDetail);
                }
                apiRest = new ApiRest();
                apiRest.setMessage("饿了么订单保存成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理订单状态变更
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleOrderStatusChange(BigInteger shopId, String message, String requestId, int orderStatus, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");
            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);

                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                if (dietOrderInfo.getOrderStatus() == 6 && elemeOrderStatus == 56) {

                } else {
                    if (elemeOrderStatus == 53) {
                        dietOrderInfo.setDispatcherName(messageJson.optString("name"));
                        dietOrderInfo.setDispatcherMobile(messageJson.optString("phone"));
                    }

                    dietOrderInfo.setOrderStatus(orderStatus);
                    dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                    dietOrderInfo.setLastUpdateAt(new Date());
                    dietOrderInfoMapper.update(dietOrderInfo);
                }
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    @Transactional
    public ApiRest handleOrderStatusAndPayStatusChange(BigInteger shopId, String message, String requestId, int orderStatus, int originalPayStatus, int payStatus, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setOrderStatus(orderStatus);
                if (dietOrderInfo.getPayStatus() == originalPayStatus) {
                    dietOrderInfo.setPayStatus(payStatus);
                }
                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);

                if (orderStatus == 6) {
                    SaleFlowUtils.writeSaleFlow(dietOrderInfo);
                    StockUtils.handleOnlineStock(dietOrderInfo.getId());
                }

                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw new RuntimeException(e);
        }
        return apiRest;
    }

    @Transactional
    public ApiRest handleOrderStatusAndPayStatusChange(BigInteger shopId, String message, String requestId, int orderStatus, int payStatus, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setOrderStatus(orderStatus);
                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setPayStatus(payStatus);

                if (payStatus == 3) {
                    dietOrderInfo.setIncome(BigDecimal.ZERO);
                }

                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);

                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    @Transactional
    public ApiRest handlePayStatusChange(BigInteger shopId, String message, String requestId, int payStatus, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setPayStatus(payStatus);
                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);

                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理用户催单
     *
     * @return
     */
    @Transactional
    public ApiRest handleUserReminder(BigInteger shopId, String message, String requestId, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();

        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);

                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理商家回复用户催单
     *
     * @param shopId
     * @param message
     * @param requestId
     * @param elemeOrderStatus
     * @return
     */
    @Transactional
    public ApiRest handleSellerReplyUserReminder(BigInteger shopId, String message, String requestId, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();

        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);

                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest checkIsBind(String tenantId, String branchId) {
        ApiRest apiRest = new ApiRest();
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
        Validate.notNull(branch, "机构不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        if (branch.getShopId() != null) {
            Map<String, String> checkTenantIsAuthorizedParameters = new HashMap<String, String>();
            checkTenantIsAuthorizedParameters.put("tenantId", tenantId);
            checkTenantIsAuthorizedParameters.put("branchId", branchId);
            checkTenantIsAuthorizedParameters.put("flag", Constants.STANDARD_EDITION);
            checkTenantIsAuthorizedParameters.put("elemeAccountType", branch.getElemeAccountType().toString());
            ApiRest checkTenantIsAuthorizedRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_OUT, "eleme", "checkTenantIsAuthorized", checkTenantIsAuthorizedParameters);
            Validate.isTrue(checkTenantIsAuthorizedRest.getIsSuccess(), checkTenantIsAuthorizedRest.getError());

            String checkTenantIsAuthorizedRestData = (String) checkTenantIsAuthorizedRest.getData();
            if ("1".equals(checkTenantIsAuthorizedRestData)) {
                data.put("isBind", true);
            } else if ("0".equals(checkTenantIsAuthorizedRestData)) {
                data.put("isBind", false);
            }
        } else {
            data.put("isBind", false);
        }
        apiRest.setData(data);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    public Goods addDefaultGoods(BigInteger tenantId, BigInteger branchId) {
//        Goods meiTuanGoods = new Goods();
//        meiTuanGoods.setTenantId(tenantId);
//        meiTuanGoods.setBranchId(branchId);
        Category category = categoryMapper.getCatsGoodsUsed(tenantId).get(0);
//        if (category != null) {
//            meiTuanGoods.setCategoryId(category.getId());
//            meiTuanGoods.setCategoryName(category.getCatName());

//            String categoryCode = category.getCatCode();
//            String goodsCode = goodsMapper.findMaxGoodsCodeByTenantIdAndCategoryCode(tenantId, categoryCode);
//            if (StringUtils.isBlank(goodsCode)) {
//                if (categoryCode.length() == 2) {
//                    goodsCode = categoryCode + "000000";
//                } else {
//                    goodsCode = categoryCode + "0000";
//                }
//            }
//            meiTuanGoods.setGoodsCode(SerialNumberGenerate.nextGoodsCode(goodsCode));
//        }

        GoodsUnit goodsUnit = goodsUnitMapper.findByTenantIdAndIsDeleted(tenantId);
//        if (goodsUnit != null) {
//            meiTuanGoods.setGoodsUnitId(goodsUnit.getId());
//            meiTuanGoods.setGoodsUnitName(goodsUnit.getUnitName());
//        }
//
//        meiTuanGoods.setGoodsName("美团外卖");
//        meiTuanGoods.setMnemonic("mtwm");
//        meiTuanGoods.setSalePrice(BigDecimal.ZERO);
//        meiTuanGoods.setVipPrice(BigDecimal.ZERO);
//        meiTuanGoods.setVipPrice1(BigDecimal.ZERO);
//        meiTuanGoods.setVipPrice2(BigDecimal.ZERO);
//        meiTuanGoods.setCombinationType(0);
//        meiTuanGoods.setCreateBy("System");
//        meiTuanGoods.setCreateAt(new Date());
//        meiTuanGoods.setLastUpdateAt(new Date());
//        meiTuanGoods.setLastUpdateBy("System");
//        meiTuanGoods.setGoodsStatus(0);
//        meiTuanGoods.setGoodsType(1);
//        meiTuanGoods.setPriceType(0);
//        goodsMapper.insert(meiTuanGoods);


        Goods elemeGoods = new Goods();
        elemeGoods.setTenantId(tenantId);
        elemeGoods.setBranchId(branchId);
        if (category != null) {
            elemeGoods.setCategoryId(category.getId());
            elemeGoods.setCategoryName(category.getCatName());

            String categoryCode = category.getCatCode();
            String goodsCode = goodsMapper.findMaxGoodsCodeByTenantIdAndCategoryCode(tenantId, category.getCatCode());
            if (StringUtils.isBlank(goodsCode)) {
                if (categoryCode.length() == 2) {
                    goodsCode = categoryCode + "000000";
                } else {
                    goodsCode = categoryCode + "0000";
                }
            }
            elemeGoods.setGoodsCode(SerialNumberGenerate.nextGoodsCode(goodsCode));
        }

        if (goodsUnit != null) {
            elemeGoods.setGoodsUnitId(goodsUnit.getId());
            elemeGoods.setGoodsUnitName(goodsUnit.getUnitName());
        } else {
            elemeGoods.setGoodsUnitId(BigInteger.ZERO);
        }
        elemeGoods.setGoodsName("饿了么外卖");
        elemeGoods.setMnemonic("elmwm");
        elemeGoods.setSalePrice(new BigDecimal(0));
        elemeGoods.setVipPrice(new BigDecimal(0));
        elemeGoods.setVipPrice2(new BigDecimal(0));
        elemeGoods.setCombinationType(0);
        elemeGoods.setCreateBy("System");
        elemeGoods.setCreateAt(new Date());
        elemeGoods.setLastUpdateAt(new Date());
        elemeGoods.setLastUpdateBy("System");
        elemeGoods.setGoodsStatus(1);
        elemeGoods.setPriceType(0);
        elemeGoods.setGoodsType(1);
        elemeGoods.setIsPricetag(false);
        goodsMapper.insert(elemeGoods);
        return elemeGoods;
    }

    /**
     * 处理用户申请取消订单
     *
     * @param shopId：饿了么shop           id
     * @param message：消息体
     * @param requestId：请求Id
     * @param orderStatus：订单状态
     * @param payStatus：付款状态
     * @param elemeOrderStatus：饿了么回调标识
     * @return
     */
    @Transactional
    public ApiRest handleUserApplyCancelOrder(BigInteger shopId, String message, String requestId, int orderStatus, int payStatus, int elemeOrderStatus) {
        ApiRest apiRest = null;
        JSONObject messageJson = JSONObject.fromObject(message);
        String orderId = messageJson.getString("orderId");
        String key = "_eleme.message.callback_" + orderId + "_" + elemeOrderStatus;
        Jedis jedis = CacheUtils.getJedis();
        try {
            Long setnxReturnValue = jedis.setnx(key, key);
            Validate.notNull(setnxReturnValue, "执行jedis.setnx(" + key + "," + key + ")方法失败！");

            if (setnxReturnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            } else {
                jedis.expire(key, 60 * 30);
                Branch branch = branchMapper.findByShopId(shopId);
                Validate.notNull(branch, "shopId为：" + shopId + "的机构不存在！");

                DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndOrderCode(branch.getTenantId(), branch.getId(), "E" + orderId);
                Validate.notNull(dietOrderInfo, "订单不存在！");

                dietOrderInfo.setOrderStatus(orderStatus);
                dietOrderInfo.setElemeOrderStatus(elemeOrderStatus);
                dietOrderInfo.setPayStatus(payStatus);
                dietOrderInfo.setMeituanRefundReason(messageJson.getString("reason"));
                dietOrderInfo.setLastUpdateAt(new Date());
                dietOrderInfoMapper.update(dietOrderInfo);
                apiRest = new ApiRest();
                apiRest.setMessage("处理成功！");
                apiRest.setIsSuccess(true);
            }
            CacheUtils.returnResource(jedis);
        } catch (Exception e) {
            jedis.del(key);
            CacheUtils.returnResource(jedis);
            throw e;
        }
        return apiRest;
    }

    @Transactional(readOnly = true)
    public Branch findBranchInfo(BigInteger tenantId, BigInteger branchId) {
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Validate.notNull(branch, "机构不存在！");
        return branch;
    }

    public ApiRest queryItemByPage(BigInteger tenantId, BigInteger branchId, long limit, long offset) throws Exception {
        String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
        String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Validate.notNull(branch, "机构不存在！");

        Map<String, Object> queryPage = new HashMap<String, Object>();
        queryPage.put("shopId", branch.getShopId());
        queryPage.put("offset", offset);
        queryPage.put("limit", limit);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("queryPage", queryPage);
        ApiRest queryItemByPageApiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.product.item.queryItemByPage", tenantId.toString(), branchId.toString(), branch.getElemeAccountType().toString(), parameters);
        Validate.isTrue(queryItemByPageApiRest.getIsSuccess(), queryItemByPageApiRest.getError());

        Map<String, Object> data = (Map<String, Object>) queryItemByPageApiRest.getData();
        List<Map<String, Object>> elemeGoodsInfos = (List<Map<String, Object>>) data.get("result");

        List<Map<String, Object>> goodsInfos = new ArrayList<Map<String, Object>>();
        List<BigInteger> specIds = new ArrayList<BigInteger>();
        for (Map<String, Object> elemeGoodsInfo : elemeGoodsInfos) {
            List<Map<String, Object>> specs = (List<Map<String, Object>>) elemeGoodsInfo.get("specs");
            String name = MapUtils.getString(elemeGoodsInfo, "name");
            long categoryId = MapUtils.getLong(elemeGoodsInfo, "categoryId");
            long elemeGoodsId = MapUtils.getLong(elemeGoodsInfo, "id");
            String unit = MapUtils.getString(elemeGoodsInfo, "unit");
            for (Map<String, Object> spec : specs) {
                BigInteger specId = BigInteger.valueOf(MapUtils.getLongValue(spec, "specId"));
                specIds.add(specId);
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                String specName = MapUtils.getString(spec, "name");
                if (StringUtils.isNotBlank(specName)) {
                    goodsInfo.put("name", name + "-" + MapUtils.getString(spec, "name"));
                } else {
                    goodsInfo.put("name", name);
                }
                goodsInfo.put("price", MapUtils.getDoubleValue(spec, "price"));
                goodsInfo.put("categoryId", categoryId);
                goodsInfo.put("unit", unit);
                goodsInfo.put("specId", specId);
                goodsInfo.put("elemeGoodsId", elemeGoodsId);
                goodsInfos.add(goodsInfo);
            }
        }
        List<Map<String, Object>> elemeMappingGoodses = goodsMapper.findElemeMappingGoodses(tenantId, branchId, specIds);
        Map<BigInteger, Map<String, Object>> elemeMappingGoodsMap = new HashMap<BigInteger, Map<String, Object>>();
        for (Map<String, Object> elemeMappingGoods : elemeMappingGoodses) {
            elemeMappingGoodsMap.put(BigInteger.valueOf(MapUtils.getLongValue(elemeMappingGoods, "elemeGoodsId")), elemeMappingGoods);
        }

        for (Map<String, Object> goodsInfo : goodsInfos) {
            Map<String, Object> elemeMappingGoods = elemeMappingGoodsMap.get(BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "specId")));
            if (elemeMappingGoods == null) {
                continue;
            }
            goodsInfo.put("goodsId", elemeMappingGoods.get("goodsId"));
            goodsInfo.put("goodsName", elemeMappingGoods.get("goodsName"));
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(goodsInfos);
        apiRest.setMessage("查询饿了么产品列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest listGoodses(BigInteger tenantId, BigInteger branchId, BigInteger categoryId, BigInteger goodsId, String codeOrName) {
        List<Map<String, Object>> goodsInfos = goodsMapper.listGoodses(tenantId, branchId, categoryId, goodsId, codeOrName);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(goodsInfos);
        apiRest.setMessage("查询产品列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest saveGoodsMapping(BigInteger tenantId, BigInteger branchId, BigInteger elemeGoodsId, BigInteger ourGoodsId) {
        ElemeGoodsMapping elemeGoodsMapping = elemeGoodsMappingMapper.findByTenantIdAndBranchIdAndElemeGoodsId(tenantId, branchId, elemeGoodsId);
        if (elemeGoodsMapping == null) {
            elemeGoodsMapping = new ElemeGoodsMapping();
            elemeGoodsMapping.setTenantId(tenantId);
            elemeGoodsMapping.setBranchId(branchId);
            elemeGoodsMapping.setElemeGoodsId(elemeGoodsId);
            elemeGoodsMapping.setOurGoodsId(ourGoodsId);
            elemeGoodsMappingMapper.insert(elemeGoodsMapping);
        } else {
            elemeGoodsMapping.setOurGoodsId(ourGoodsId);
            elemeGoodsMappingMapper.update(elemeGoodsMapping);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("关联成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest confirmOrder(BigInteger tenantId, BigInteger branchId, BigInteger orderId) throws Exception {
        DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByTenantIdAndBranchIdAndId(tenantId, branchId, orderId);
        Validate.notNull(dietOrderInfo, "ID为：" + orderId + "的订单不存在！");

        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Validate.notNull(branch, "机构不存在！");

        String appKey = PropertyUtils.getDefault(Constants.ELEME_APP_KEY);
        String secret = PropertyUtils.getDefault(Constants.ELEME_APP_SECRET);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("orderId", dietOrderInfo.getOrderCode().substring(1));
        ApiRest apiRest = ElemeUtils.callElemeSystem(appKey, secret, "eleme.order.confirmOrderLite", tenantId.toString(), branchId.toString(), branch.getElemeAccountType().toString(), parameters);
        ValidateUtils.isTrue(apiRest.getIsSuccess(), apiRest.getError());

        dietOrderInfo.setConfirmOrderSource(1);
        dietOrderInfoMapper.update(dietOrderInfo);
        return apiRest;
    }
}
