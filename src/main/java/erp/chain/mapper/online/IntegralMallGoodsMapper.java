package erp.chain.mapper.online;

import erp.chain.domain.ShopGoods;
import erp.chain.domain.online.IntegralMallGoods;
import erp.chain.utils.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-10-29.
 */
@Mapper
public interface IntegralMallGoodsMapper {
    List<IntegralMallGoods> findAll(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("vipId") BigInteger vipId);

    IntegralMallGoods find(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsId") BigInteger goodsId, @Param("vipId") BigInteger vipId);

    long updateSurplusQuantity(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsId") BigInteger goodsId, @Param("quantity") BigDecimal quantity);

    long updateAlreadyChangeQuantity(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsId") BigInteger goodsId, @Param("quantity") BigDecimal quantity);

    BigDecimal obtainExchangeQuantity(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId);

    List<Map<String, Object>> listOrders(@Param("tenantId") BigInteger tenantId, @Param("orderMode") Integer orderMode,
                                         @Param("offset") int offset, @Param("maxResults") int maxResults,
                                         @Param("orderStatus") Integer orderStatus,
                                         @Param("startDate") String startDate, @Param("endDate") String endDate,
                                         @Param("orderCode") String orderCode);
    long listOrdersCount(@Param("tenantId") BigInteger tenantId, @Param("orderMode") Integer orderMode,
                         @Param("orderStatus") Integer orderStatus,
                         @Param("startDate") String startDate, @Param("endDate") String endDate,
                         @Param("orderCode") String orderCode);

    int settingGoodsListCount(@Param("tenantId") BigInteger tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate,
                               @Param("goodsStatus")Integer goodsStatus, @Param("isTicket") Integer isTicket);

    List<ShopGoods> settingGoodsList(@Param("tenantId") BigInteger tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate,
                                     @Param("goodsStatus")Integer goodsStatus, @Param("isTicket") Integer isTicket);

    int updateGoodsSetting(@Param("tenantId") BigInteger tenantId, @Param("id") BigInteger id, @Param("changeQuantity") BigDecimal changeQuantity, @Param("surplusQuantity") BigDecimal surplusQuantity);
}
