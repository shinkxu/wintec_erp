package erp.chain.mapper.report;

import erp.chain.mapper.BaseMapper;
import erp.chain.model.report.GrossProfitPagerModel;
import erp.chain.model.report.SaleEmpWorkPagerModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
@Mapper
public interface ReportMapper extends BaseMapper {
    /**
     * 交易流水报表
     */
    List<Map> saleFlowReport(Map params);

    List<Map> saleFlowReportSum(Map params);

    /**
     * 明细流水报表
     */
    List<Map> saleGoodsReport(Map params);

    List<Map> saleGoodsReportSum(Map params);

    /**
     * 支付流水报表
     */
    List<Map> salePaymentReport(Map params);

    List<Map> salePaymentReportSum(Map params);

    /**
     * 单品汇总报表
     */
    List<Map> singleSaleReport(Map params);

    List<Map> singleSaleReportSum(Map params);

    /**
     * 分类汇总报表
     */
    List<Map> cateSaleReport(Map params);

    List<Map> cateSaleReportSum(Map params);

    /**
     * 销售趋势报表
     */
    List<Map> saleTrendReport(Map params);

    List<Map> saleTrendReportSum(Map params);

    /**
     * 营业概况报表
     */
    List<Map> businessReport(Map params);

    /**
     * 机构报表
     */
    List<Map> areaList(@Param("areaId") String areaId);
    List<Map> firstAreaList(@Param("areaId") String areaId,@Param("tenantId") BigInteger tenantId);

    List<Map> branchReport(Map params);
    List<Map> branchReportSum(Map params);

    /**
     * 机构支付方式汇总报表
     */
    List<Map> paymentMethodReport(Map params);
    List<Map> paymentMethodReportSum(Map params);
    List<Map> paymentMethodWays(Map params);
    /**
     * 查询线上数据
     * */
    List<Map> dietOrderReport(Map params);
    List<Map> dietOrderSumReport(Map params);

    /**
     * 收款对账
     */
    List<Map> payReport(Map params);

    List<Map> payReportSum(Map params);

    List<Map> payWays(Map params);

    List<Map> dayPayReport(Map params);

    List<Map> dayPayReportSum(Map params);

    List<Map> storedValueReport(Map params);

    List<Map> storedValueReportTotalSum(Map params);


    /**
     * 支付汇总（微信）
     */
    List<Map> paymentReport(Map params);

    Map<String, Object> querySaleEmpWorkCount(SaleEmpWorkPagerModel model, @Param("payInfo") List<String> payInfo);

    List<Map<String, Object>> querySaleEmpWorkPager(SaleEmpWorkPagerModel model, @Param("payInfo") List<String> payInfo);

    List<Map<String, Object>> usedPaymentInfo(SaleEmpWorkPagerModel model);

    String getDate(@Param("date")String date,@Param("value")Integer value);

    Map<String,Object> queryGrossProfitCount(GrossProfitPagerModel model);

    List<Map<String, Object>> queryGrossProfitPager(GrossProfitPagerModel model);

    /**
     * 首页30天销售趋势
     * @param params
     * @return
     */
    List<Map> saleTrend30DaysReport(Map params);
    List<Map> saleTrend30DaysReportSum(Map params);

    /**
     * 支付方式汇总
     *
     * @param  params
     * @return
     * */
    List<Map> paymentTypeReport(Map params);
    List<Map> paymentTypeSum(Map params);
    List<Map> allPayment(Map params);

    /**
     * 区域销售汇总
     *
     * @param params
     * @return
     * */
    List<Map> areaReport(Map params);
    List<Map> areaSum(Map params);
    Integer isSubregion(@Param("areaId")String areaId,  @Param("tenantId") BigInteger tenantId);
    List<Map> findSubregions(@Param("pId")String pId, @Param("tenantId")BigInteger tenantId);
    List<Map> subregions(Map params);
    List<Map> subregionsSum(Map params);
    BigInteger findAreaId(BigInteger tenantId);
    List<Map> findFirstArea(@Param("tenantId") BigInteger tenantId, @Param("userAreas") String userAreas, @Param("authorityType")int authorityType);

    /**
     * 营业概况
     *
     * @param params
     * @return
     * */
    List<Map> businessOverviewReport(Map params);

    /**
     * 赠退明细查询
     *
     * @param params
     * @return
     * */
    List<Map> retirementReport(Map params);
    List<Map> retirementReportSum(Map params);


    /**
     * 库存预警查询
     * */
    List<Map> storeWarningReport(Map params);
    Long storeWarningReportSum(Map params);

    /**
     * 套餐销售汇总
     * */
    List<Map> packageReport(Map params);
    List<Map> packageReportSum(Map params);

    List<Map> pushData(Map params);
    List<Map> wxPushBusiness(@Param("tenantId") BigInteger tenantId,@Param("branchId")BigInteger branchId,
                             @Param("beginDate")String begin,@Param("endDate")String end);
    Long countVipIncrease(@Param("tenantId") BigInteger tenantId,@Param("branchId")BigInteger branchId,
                          @Param("beginDate")String begin,@Param("endDate")String end);

    /*************************************** 营业概况（新begin）*************************************/
    Map businessVip(Map params);
    Map businessPos(Map params);
    Map businessOrder(Map params);
    Map businessPayment(Map params);
    Map businessVipStore(Map params);
    Map businessVipTrade(Map params);
    Map businessStoreSale(Map params);
    Map businessStoreOccur(Map params);
    Map businessStore(Map params);
    Map businessRetire(Map params);
    /*************************************** 营业概况（新end）*************************************/


    /**
     * 会员台帐-积分记录
     * */
    List<Map> vipScoreReport(Map params);
    Long vipScoreReportSum(Map params);
    List<Map> vipScoreReportFoot(Map params);

    /**
     * 会员台账-储值记录
     * */
    List<Map> vipStoreReport(Map params);
    Long vipStoreReportSum(Map params);
    List<Map> vipStoreReportFoot(Map params);

    /**
     * 会员台账-消费记录
     * */
    List<Map> vipTradeReport(Map params);
    Long vipTradeReportSum(Map params);
    List<Map> infoInTradeHistory(Map prams);
    List<Map> infoInSale(Map params);
    List<Map> infoInSaleDetail(Map params);
    List<Map> infoInPayment(Map params);
    List<Map> vipTradeReportFoot(Map params);

    long branchData(Map params);
    List<Map> branchActiveData(@Param("tenantIds")String tenantIds,
                         @Param("branchIds")String branchIds,
                         @Param("startDate")String startDate,
                         @Param("endDate")String endDate);

    List<Map<BigInteger, BigInteger>> statisticsActiveBranches(@Param("tenantIds") List<BigInteger> tenantIds,
                                                               @Param("branchIds") List<BigInteger> branchIds,
                                                               @Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);

    /**
     * 规格汇总报表
     * */
    List<Map> standardSaleGoodsIds(Map params);
    List<Map> standardIds(Map params);
    List<Map> standardReport(Map params);
    Long standardReportSum(Map params);
    List<Map> standardReportFoot(Map params);

    /**
     * 进销存报表
     * */
    List<Map> goodsInvoicingReport(Map params);
    Long goodsInvoicingReportSum(Map params);
    List<Map> beginAmount(Map params);
    List<Map> endAmount(Map params);
    List<Map> goodsInvoicingReportFoot(Map params);

    /**
     * 销售趋势分析
     * */
    List<Map> saleTrendAnalysis(Map params);

    /**
     * 机构收款汇总
     * */
    List<Map> storePayWays(Map params);
    List<Map> getAllPayWays(Map params);
    List<Map> gatheringOfBranchReport(Map params);

    /**
     * 会员对账报表
     * */
    List<Map> vipStatementReport(Map params);
    List<Map> vipStatementReportSum(Map params);
    List<Map> vipBeginAmount(Map params);
    List<Map> vipEndAmount(Map params);

    /**
     * 机构销售日报
     * */
    List<Map> branchSaleDaily(Map params);
    List<Map> branchSaleDailySum(Map params);

    /**
     * 机构区间日报
     * */
    List<Map> branchIntervalDaily(Map params);
    List<Map> branchIntervalDailySum(Map params);


    /**
     * 查询小票信息
     * @param params
     * @return
     */
    Map getSaleInfo(Map<String,String> params);
    List<Map> getSaleDetailInfo(Map<String,String> params);
    List<Map> getSalePaymentInfo(Map<String,String> params);


}
