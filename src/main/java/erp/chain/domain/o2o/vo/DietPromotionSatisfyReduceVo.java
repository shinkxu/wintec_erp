package erp.chain.domain.o2o.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by songzhiqiang on 2017/2/5.
 */
public class DietPromotionSatisfyReduceVo {
    /**
     *
     */
    BigInteger dietPromotionId;
    /**
     *
     */
    BigInteger buyGoodsId;
    /**
     *
     */
    String buyGoodsName;
    /**
     * 满足的金额数
     */
    BigDecimal satisfy;
    /**
     * 优惠的金额数
     */
    BigDecimal reduction;
}
