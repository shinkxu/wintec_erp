package erp.chain.mapper.o2o;

import erp.chain.domain.CardCouponsReport;
import erp.chain.domain.o2o.*;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by songzhiqiang on 2017/1/17.
 */
@Mapper
public interface CardCouponsMapper extends BaseMapper{
    List<CardCoupons> cardCouponsList(Map params);
    List<Map> getCardCouponsList(Map params);
    List<CardCoupons> cardCouponsListWithGoodsOrBranch(Map params);
    int updateCardCoupons(Map params);
    int updateCardCoupons(CardCoupons cardCoupons);

    int addCardCoupons(Map params);
    int addCardCoupons(CardCoupons cardCoupons);

    CardCoupons findCardCouponsById(Map params);
    CardCoupons findCardCouponsByIdIncludeDelete(Map params);

    List<CardCouponsReport> cardCouponsReportList(Map params);

    int updateCardCouponsReport(CardCouponsReport cardCouponsReport);

    int addCardCouponsReport(CardCouponsReport cardCouponsReport);

    int deleteCouponsGoods(@Param("tenantId")BigInteger tenantId,@Param("couponsId")BigInteger couponsId);
    int deleteCouponsBranch(@Param("tenantId")BigInteger tenantId,@Param("couponsId")BigInteger couponsId);
    int insertCouponsGoods(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,@Param("goodsIds")List<String> goodsIds);
    int insertCouponsBranch(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId,@Param("branchIds")List<String> branchIds);
    List<String> getCouponsBranch(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);

    long countCouponsGoods(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);
    long countCouponsBranch(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);

    long countCouponUsed(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);
    /**新调整**/
    CardCoupons getById(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);
    void updateSendedAmount(@Param("tenantId")BigInteger tenantId,@Param("couponsId")BigInteger couponsId,@Param("sendedAmount")BigDecimal sendedAmount);
    CardCouponsReport cardCouponsReport(@Param("couponsId")BigInteger couponsId,@Param("tenantId")BigInteger tenantId);
    void insertMarketingOrder(PrecisionMarketingOrder precisionMarketingOrder);
    void insertMarketingOrderCouponsDetail(PrecisionMarketingOrderCouponsDetail precisionMarketingOrderCouponsDetail);
    void insertMarketingOrderVipDetail(PrecisionMarketingOrderVipDetail precisionMarketingOrderVipDetail);
    void insertBookList(@Param("cardCouponsBookList") List<CardCouponsBook> cardCouponsBookList);
    List<Map> listMarketingOrder(Map<String,String> params);
    long countMarketingOrder(Map<String,String> params);
    List<Map> orderDetail(Map<String,String> params);
    List<Map> countOrderDetail(Map<String,String> params);
    List<Map> cardCouponsBook(Map<String,String> params);
    long countCardCouponsBook(Map<String,String> params);
    List<Map> couponsAccount(Map<String,String> params);
    long countCouponsAccount(Map<String,String> params);
}
