package erp.chain.mapper.online;

import erp.chain.domain.online.OnlineVip;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-10.
 */
@Mapper
public interface OnlineVipMapper {
    long insert(OnlineVip onlineVip);

    long update(OnlineVip onlineVip);

    OnlineVip find(SearchModel searchModel);

    List<OnlineVip> findAll(SearchModel searchModel);

    List<OnlineVip> findAllVipInfos(@Param("tenantId") BigInteger tenantId, @Param("openId") String openId, @Param("phone") String phone);

    long countVipCoupons(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId);

    List<Map<String, Object>> queryConsumeHistory(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<OnlineVip> findAllSameVips(@Param("tenantId") BigInteger tenantId,
                                    @Param("groupCode") String groupCode,
                                    @Param("phone") String phone,
                                    @Param("vipId") BigInteger vipId);
}
