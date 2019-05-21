package erp.chain.mapper.supply.store;

import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.domain.supply.store.*;
import erp.chain.model.supply.store.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存单据
 */
@Mapper
public interface StoreOrderMapper {
    /**
     * 保存采购单
     *
     * @return 单据id
     * @throws org.springframework.dao.DuplicateKeyException 单据号已存在
     */
    public int saveCgOrder(CgOrder order);
    /**
     * 保存采购单
     *
     * @return 单据id
     * @throws org.springframework.dao.DuplicateKeyException 单据号已存在
     */
    public int saveCgOrderWS(CgOrder order);

    /**
     * 保存采购单明细
     */
    public int saveCgOrderDetail(@Param("details") List<CgOrderDetail> cgOrderDetails);

    /**
     * 统计机构采购单
     */
    long queryCgOrderCount(QueryCgOrderModel model);

    /**
     * 分页查询机构采购单
     */
    List<HashMap<String, Object>> queryCgOrderPager(QueryCgOrderModel model);

    /**
     * 统计机构采购单明细
     */
    long queryCgOrderDetailCount(QueryCgOrderDetailModel model);

    /**
     * 分页查询机构采购单明细
     */
    List<HashMap<String, Object>> queryCgOrderDetailPager(QueryCgOrderDetailModel model);

    /**
     * 查询机构采购单明细
     */
    List<HashMap<String, Object>> queryCgOrderDetailList(QueryCgOrderDetailModel model);

    /**
     * 查询机构采购单明细合计
     */
    List<HashMap<String, Object>> queryCgOrderDetailListTotal(QueryCgOrderDetailModel model);
    /**
     * 保存领用单
     *
     * @return 单据id
     * @throws org.springframework.dao.DuplicateKeyException 单据号已存在
     */
    public int saveLyOrder(LyOrder order);

    /**
     * 保存领用单明细
     */
    int saveLyOrderDetail(@Param("details") List<LyOrderDetail> details, @Param("orderId") BigInteger orderId);

    /**
     * 保存损溢单
     *
     * @return 单据id
     * @throws DuplicateKeyException 单据号已存在
     */
    public int saveSyOrder(SyOrder order);

    /**
     * 保存损溢单明细
     */
    int saveSyOrderDetail(@Param("details") List<SyOrderDetail> details, @Param("orderId") BigInteger id);

    /**
     * 审核单据
     */
    public int auditOrder(AuditOrderModel model);

    long queryLyOrderCount(QueryLyOrderModel model);

    List<HashMap<String, Object>> queryLyOrderPager(QueryLyOrderModel model);

    List<HashMap<String, Object>> queryLyOrderList(QueryLyOrderModel model);

    long queryLyOrderDetailCount(QueryLyOrderDetailModel model);

    List<HashMap<String, Object>> queryLyOrderDetailPager(QueryLyOrderDetailModel model);

    List<HashMap<String, Object>> queryLyOrderDetailList(QueryLyOrderDetailModel model);

    List<HashMap<String, Object>> queryLyOrderDetailListTotal(QueryLyOrderDetailModel model);

    int updateLyOrder(LyOrder order);

    /**
     * 单据类型(1-领用单,2-领用单)
     */
    int delOrderDetail(@Param("orderId") BigInteger orderId, @Param("type") Integer type);

    /**
     * 单据类型(1-领用单,2-领用单)
     */
    int delOrder(@Param("orderId") BigInteger orderId, @Param("version") Long version, @Param("type") Integer type, @Param("empId") BigInteger empId);

    long querySyOrderCount(QuerySyOrderModel model);

    List<HashMap<String, Object>> querySyOrderPager(QuerySyOrderModel model);

    List<HashMap<String, Object>> querySyOrderDetailList(QuerySyOrderDetailModel detailModel);
    List<HashMap<String, Object>> querySyOrderDetailListTotal(QuerySyOrderDetailModel detailModel);

    int updateSyOrder(SyOrder order);

    int savePdOrder(PdOrder order);
    int savePdOrderWS(PdOrder order);

    void savePdOrderDetail(@Param("details") List<PdOrderDetail> details);

    long queryPdOrderCount(QueryPdOrderModel model);

    List<HashMap<String, Object>> queryPdOrderPager(QueryPdOrderModel model);

    long queryPdOrderDetailCount(QueryPdOrderDetailModel model);

    List<HashMap<String, Object>> queryPdOrderDetailPager(QueryPdOrderDetailModel model);

    List<HashMap<String, Object>> queryPdOrderDetailList(QueryPdOrderDetailModel detailModel);

    long queryOrderCount(QueryOrderModel model);

    List<MapUnderscoreToCamelCase<String, Object>> queryOrderPager(QueryOrderModel model);
    List<Map> queryOrderPagerSum(QueryOrderModel model);

    int updateCgOrder(CgOrder order);

    int updatePdOrder(PdOrder order);

    void updateOrderWS(Map params);

    /**
     * 单品库存汇总
     * */
    List<Map> queryStoreSingleReport(Map params);
    Long queryStoreSingleReportCount(Map params);
    List<Map> queryStoreSingleSumReport(Map params);

    /**
     * 供应商结算单
     * */
    List<Map> querySupplierSettlements(Map params);
    Long querySupplierSettlementsSum(Map params);
    int addJsOrder(JsOrder jsOrder);
    int updateJsOrder(JsOrder jsOrder);
    JsOrder queryJsOrderById(@Param("id") BigInteger id);
    //根据结算单查询采购单
    List<Map> queryCgByJsOrder(Map params);
    int clearCgToJs(@Param("ids") String ids);
    int updateCgToJs(Map params);
    int auditJsOrder(Map params);
    List<Map> queryCgOrderToJs(Map params);
    List<Map> queryJsOrderByCode(Map params);
    List<Map> queryCgOrderByJsCode(Map params);

    /**
     * 根据code查询结算单合计
     * @param params
     * @return
     */
    List<Map> queryCgOrderDetailByJsCode(Map params);
    int deleteJsOrder(Map params);
}
