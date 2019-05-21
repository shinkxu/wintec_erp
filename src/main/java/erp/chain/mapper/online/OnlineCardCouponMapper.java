package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineCardCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-07-27.
 */
@Mapper
public interface OnlineCardCouponMapper {
    List<OnlineCardCoupon> listCards(@Param("tenantId") BigInteger tenantId,
                                     @Param("vipId") BigInteger vipId,
                                     @Param("cardType") Integer cardType,
                                     @Param("limitValue") BigDecimal limitValue);

    List<Map<String, Object>> statisticsCouponsCount(@Param("vipId") BigInteger vipId, @Param("festivalIds") List<BigInteger> festivalIds);
}
