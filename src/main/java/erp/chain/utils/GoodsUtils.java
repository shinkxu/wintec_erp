package erp.chain.utils;

import erp.chain.common.Constants;
import erp.chain.domain.online.OnlineGoodsPromotion;
import erp.chain.domain.online.OnlineVipType;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by liuyandong on 2018-08-28.
 */
public class GoodsUtils {
    public static BigDecimal obtainVipPrice(OnlineVipType onlineVipType, Map<String, Object> goodsInfo) {
        int preferentialPolicy = onlineVipType.getPreferentialPolicy().intValue();
        BigDecimal vipPrice = null;
        if (preferentialPolicy == 0) {
            vipPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "salePrice"));
        } else if (preferentialPolicy == 2) {
            vipPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "vipPrice"));
        } else if (preferentialPolicy == 3) {
            boolean isDsc = MapUtils.getBooleanValue(goodsInfo, "isDsc");
            if (isDsc) {
                BigDecimal discountRate = onlineVipType.getDiscountRate();
                vipPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "salePrice")).multiply(discountRate).divide(Constants.BIG_DECIMAL_ONE_HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
            } else {
                vipPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "salePrice"));
            }
        }
        return vipPrice;
    }

    public static BigDecimal obtainSalePrice(Map<String, Object> goodsInfo) {
        return BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "salePrice"));
    }

    public static boolean isGive(OnlineGoodsPromotion onlineGoodsPromotion, Map<String, Object> giveGoodsInfo, BigDecimal quantity) {
        return quantity.compareTo(onlineGoodsPromotion.getBuyNum()) >= 0 && MapUtils.isNotEmpty(giveGoodsInfo);
    }

    public static BigDecimal calculateGiveQuantity(BigDecimal quantity, OnlineGoodsPromotion onlineGoodsPromotion) {
        int count = quantity.divide(onlineGoodsPromotion.getBuyNum()).intValue();
        return onlineGoodsPromotion.getGiveNum().multiply(BigDecimal.valueOf(count));
    }

    public static BigDecimal calculatePromotionPrice(Map<String, Object> goodsInfo, OnlineVipType onlineVipType, OnlineGoodsPromotion onlineGoodsPromotion) {
        BigDecimal promotionPrice = onlineGoodsPromotion.getPromotionPrice();
        int preferentialPolicy = onlineVipType.getPreferentialPolicy().intValue();

        BigDecimal finalPromotionPrice = null;
        if (preferentialPolicy == 0) {
            finalPromotionPrice = promotionPrice;
        } else if (preferentialPolicy == 2) {
            finalPromotionPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "vipPrice"));
        } else if (preferentialPolicy == 3) {
            boolean isDsc = MapUtils.getBooleanValue(goodsInfo, "isDsc");
            if (isDsc) {
                BigDecimal discountRate = onlineVipType.getDiscountRate();
                finalPromotionPrice = promotionPrice.multiply(discountRate).divide(Constants.BIG_DECIMAL_ONE_HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
            } else {
                finalPromotionPrice = promotionPrice;
            }
        }
        return finalPromotionPrice;
    }
}
