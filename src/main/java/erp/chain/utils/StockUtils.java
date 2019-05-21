package erp.chain.utils;

import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.online.OnlineDietOrderDetail;
import erp.chain.domain.online.OnlineDietOrderInfo;
import erp.chain.domain.online.OnlineGoods;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by liuyandong on 2018-05-31.
 */
public class StockUtils {
    public static void handleOnlineStock(BigInteger dietOrderInfoId) throws IOException {
        OnlineDietOrderInfo onlineDietOrderInfo = DatabaseHelper.find(OnlineDietOrderInfo.class, dietOrderInfoId);
        Validate.notNull(onlineDietOrderInfo, "订单不存在！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("diet_order_info_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderInfoId);
        List<OnlineDietOrderDetail> onlineDietOrderDetails = DatabaseHelper.findAll(OnlineDietOrderDetail.class, searchModel);
        Validate.isTrue(CollectionUtils.isNotEmpty(onlineDietOrderDetails), "订单明细为空！");

        BigInteger tenantId = onlineDietOrderInfo.getTenantId();
        BigInteger branchId = onlineDietOrderInfo.getBranchId();
        String orderCode = onlineDietOrderInfo.getOrderCode();
        boolean isRefund = onlineDietOrderInfo.getIsRefund();
        Date payAt = onlineDietOrderInfo.getPayAt() != null ? onlineDietOrderInfo.getPayAt() : new Date();

        int orderMode = onlineDietOrderInfo.getOrderMode();
        if (orderMode == 6 || orderMode == 7) {
            SearchModel onlineGoodsSearchModel = new SearchModel(true);
            String goodsName = orderMode == 6 ? "美团外卖" : "饿了么外卖";
            onlineGoodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getTenantId());
            onlineGoodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, onlineDietOrderInfo.getBranchId());
            onlineGoodsSearchModel.addSearchCondition("goods_name", Constants.SQL_OPERATION_SYMBOL_EQUALS, goodsName);
            OnlineGoods onlineGoods = DatabaseHelper.find(OnlineGoods.class, onlineGoodsSearchModel);

            BigInteger goodsId = onlineGoods.getId();

            List<OnlineDietOrderDetail> onlineDietOrderDetailList = new ArrayList<OnlineDietOrderDetail>();
            onlineDietOrderDetailList.clear();
            for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
                if (onlineDietOrderDetail.getGoodsId().compareTo(goodsId) != 0) {
                    onlineDietOrderDetailList.add(onlineDietOrderDetail);
                }
            }
            onlineDietOrderDetails = onlineDietOrderDetailList;
        }

        String topic = PropertyUtils.getDefault("redis_topics");
        for (OnlineDietOrderDetail onlineDietOrderDetail : onlineDietOrderDetails) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tenantId", tenantId);
            map.put("branchId", branchId);
            map.put("goodsId", onlineDietOrderDetail.getGoodsId());
            map.put("price", onlineDietOrderDetail.getSalePrice());
            map.put("quantity", onlineDietOrderDetail.getQuantity());
            map.put("createBy", onlineDietOrderDetail.getCreateBy());
            map.put("code", orderCode);
            if (isRefund) {
                map.put("occurType", 7);
            } else {
                map.put("occurType", 6);
            }
            map.put("billCreateTime", payAt);
            Long returnValue = PartitionCacheUtils.lpush(topic, GsonUtils.toJson(map));
            Validate.notNull(returnValue, "库存计算发送失败！");
        }
    }
}
