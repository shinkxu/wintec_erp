package erp.chain.service.supply;

import com.saas.common.Constants;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.Common;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import com.saas.common.util.QueueUtils;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.MapUnderscoreToCamelCase;
import erp.chain.domain.supply.BranchRequireGoodsSetting;
import erp.chain.domain.supply.RequireGoodsPaymentOrder;
import erp.chain.domain.supply.YhOrder;
import erp.chain.domain.supply.YhOrderDetail;
import erp.chain.domain.supply.store.GoodsStoreInfo;
import erp.chain.domain.supply.store.PsOrder;
import erp.chain.domain.supply.store.PsOrderDetail;
import erp.chain.domain.supply.store.StoreCompute;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.supply.BranchRequireGoodsSettingMapper;
import erp.chain.mapper.supply.RequireGoodsPaymentOrderMapper;
import erp.chain.mapper.supply.YhOrderMapper;
import erp.chain.mapper.supply.store.PsOrderMapper;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.model.Pager;
import erp.chain.model.supply.*;
import erp.chain.model.supply.store.*;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.DateUtils;
import erp.chain.utils.NonUniqueResultException;
import erp.chain.utils.ResourceNotExistException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import erp.chain.utils.*;
import saas.api.common.ApiRest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 要货相关
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class YhOrderService {
    @Autowired
    private YhOrderMapper yhOrderMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private PsOrderMapper psOrderMapper;
    @Autowired
    private StoreComputeService storeComputeService;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private BranchRequireGoodsSettingMapper branchRequireGoodsSettingMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private RequireGoodsPaymentOrderMapper requireGoodsPaymentOrderMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    private Log logger = LogFactory.getLog(getClass());

    /**
     * 分页查询要货商品信息
     */
    @SuppressWarnings("unchecked")
    public Pager queryYhGoodsPager(QueryYhGoodsModel model) {
//        long settingCount = yhOrderMapper.yhSettingCount(model.yhBranchId, model.psBranchId);
        if(!StringUtils.isEmpty(model.getBarCode())){
            model.setGoodsNameOrCodeLike("");
        }
        boolean yhSetting = model.onlySetting;
        System.out.print(model.getOnlySelf());
        long count = yhOrderMapper.queryYhGoodsCount(model, null, yhSetting);
        model.getPager().setTotal(count);
        if (count > 0) {
            List<HashMap<String, Object>> rows = yhOrderMapper.queryYhGoodsPager(model, null, yhSetting);
            String[] goodsIdAndQuantity = null;
            String[] goodsIds = null;
            String gIds = "";
            for (HashMap<String, Object> row : rows) {
                row.put("details", new ArrayList<HashMap<String, Object>>());
                if ((int)row.get("combinationType") == 3) {
                    String detailGoodsId = (String) row.get("detailGoodsId");
                    goodsIdAndQuantity = detailGoodsId != null ? detailGoodsId.split(",") : null;
                    if(goodsIdAndQuantity != null){
                        for(int i=0; i<goodsIdAndQuantity.length; i++){
                            String goodsId = goodsIdAndQuantity[i].substring(0,goodsIdAndQuantity[i].indexOf("="));
                            gIds+=goodsId+",";
                        }
                        goodsIds = gIds.split(",");
                    }
                }
            }
            if (goodsIds != null) {
                model.setGoodsNameOrCodeLike(null);
                yhSetting = false;
                List<HashMap<String, Object>> details = yhOrderMapper.queryYhGoodsPager(model, goodsIds, yhSetting);

                for (HashMap<String, Object> row : rows) {
                    if ((int)row.get("combinationType") == 3 && row.get("detailGoodsId") != null) {
                        for (HashMap<String, Object> detail : details) {
                            if (((String) row.get("detailGoodsId")).contains(detail.get("goodsId").toString())) {
                                HashMap<String, Object> detailClon = (HashMap<String, Object>) detail.clone();
                                String detailGoodsId = (String) row.get("detailGoodsId");
                                String[] ids = detailGoodsId != null ? detailGoodsId.split(",") : new String[]{};
                                for (String id : ids) {
                                    if (id.split("=")[0].equals(detailClon.get("goodsId").toString())) {
                                        detailClon.put("yhQuantity", id.split("=")[1]);
                                    }
                                }
                                detailClon.remove("detailGoodsId");
                                ((ArrayList<HashMap<String, Object>>) row.get("details")).add(detailClon);
                            }
                        }

                    }
                }
            }
            for (HashMap<String, Object> row : rows) {
                row.remove("detailGoodsId");
            }
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    /**
     * 保存要货单
     *
     * @throws erp.chain.utils.NonUniqueResultException 单据号已存在,存在不管理库存商品
     */
    public YhOrder doSaveYhOrder(YhOrderModel model) {
        YhOrder order = new YhOrder();
        order.tenantId = model.tenantId;
        order.tenantCode = model.tenantCode;
        order.branchId = model.yhBranchId;
        order.distributionCenterId = model.psBranchId;
        order.createUserId = model.empId;
        order.makeAt = new Date();
        order.requireGoodsOrderNo = model.code;

        List<YhOrderDetail> details = new ArrayList<>(model.details.size());
        BigDecimal totalAmount = BigDecimal.ZERO;

        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (YhOrderModel.Detail detail : model.details) {
            YhOrderDetail orderDetail = new YhOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.amount = detail.quantity;
            orderDetail.price = detail.price;
            orderDetail.subtotal = detail.price.multiply(detail.quantity);
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.createUserId.toString();
            orderDetail.memo = detail.memo;
            orderDetail.unitType = detail.unitType==null?BigInteger.ONE:detail.unitType;
            orderDetail.unitRelation = detail.unitRelation==null?BigDecimal.ONE:detail.unitRelation;
            orderDetail.tenantId=order.tenantId;
            totalAmount = totalAmount.add(orderDetail.subtotal);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }
        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);

        for (YhOrderDetail detail : details) {
            GoodsStoreInfo storeInfo = findStoreInfo(list, detail.goodsId);
            if (storeInfo == null || !storeInfo.getGoodsBranchCode().equals("000")) {
                throw new NonUniqueResultException("存在商品不管理库存或不存在");
            }
        }

        order.totalAmount = totalAmount;
        try {
            yhOrderMapper.saveYhOrder(order);
            yhOrderMapper.saveYhOrderDetail(details, order.id);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.requireGoodsOrderNo));
        }

        return order;
    }

    public BigInteger saveYhOrder(YhOrderModel yhOrderModel) {
        YhOrder yhOrder = doSaveYhOrder(yhOrderModel);
        return yhOrder.id;
    }

    public YhOrder saveYhOrderNew(YhOrderModel yhOrderModel) {
        return doSaveYhOrder(yhOrderModel);
    }

    /**
     * 分页查询要货单
     */
    public Pager queryYhOrderPager(QueryYhOrderModel model) {
        long count = yhOrderMapper.yhOrderCount(model);
        model.getPager().setTotal(count);
        List<HashMap<String, Object>> footer = yhOrderMapper.queryYhOrderSum(model);
        model.getPager().setFooter(footer);
        if (count > 0) {
            model.getPager().setOrderProperty("make_at");
            model.getPager().setOrder("DESC");
            List<HashMap<String, Object>> rows = yhOrderMapper.queryYhOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    /**
     * 查询要货单信息
     *
     * @param yhBranchId 要货机构id
     */
    public HashMap<String, Object> queryYhOrderInfo(BigInteger tId, BigInteger yhBranchId, String code) {
        QueryYhOrderModel model = new QueryYhOrderModel();
        model.tenantId = tId;
        model.yhBranchId = yhBranchId;
        model.code = code;
        List<HashMap<String, Object>> rows = yhOrderMapper.queryYhOrderPager(model);
        if (rows.size() == 1) {
            HashMap<String, Object> info = rows.get(0);
            List<HashMap<String, Object>> details = yhOrderMapper.queryYhOrderDetailList(new BigInteger(info.get("orderId").toString()));
            List<HashMap<String, Object>> footer = yhOrderMapper.queryYhOrderDetailListTotal(new BigInteger(info.get("orderId").toString()));
            info.put("details", details);
            info.put("footer",footer);
            return info;
        }
        return null;
    }

    /**
     * 审核要货单;
     * 审核操作：
     * 1-要货机构提交:只能审核未提交的单据 ，如果要货机构是自动提交，则单据致为已审核，否则是已提交
     * 2-配送机构审核：只能审核已提交的单据
     * branchId ： type==1是要货机构；type==2是配送机构
     *
     * @throws org.springframework.dao.OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException                                 单据不存在或已审核
     */
    public void auditOrder(AuditOrderModel auditOrderModel) {
        QueryYhOrderModel model = new QueryYhOrderModel();
        model.tenantId = auditOrderModel.tenantId;
        model.yhBranchId = auditOrderModel.branchId;
        model.code = auditOrderModel.code;
        //查询出要审核的该条数据
        List<HashMap<String, Object>> rows = yhOrderMapper.queryYhOrderPager(model);
        //判断如果大于一条则抛出异常
        if (rows.size() == 1) {
            HashMap<String, Object> info = rows.get(0);
            Integer status = (Integer) info.get("status");
            BigInteger psBranchId = new BigInteger(info.get("psBranchId").toString());
            BigInteger orderId = new BigInteger(info.get("orderId").toString());
            int resNum = -1;
            if (auditOrderModel.type == 1) {
                BigInteger yhBranchId = new BigInteger(info.get("yhBranchId").toString());
                if (yhBranchId.compareTo(auditOrderModel.branchId) == 0 && status == 0) {
                    //查询一下是否需要审核：0-不自动审核，1-自动审核
                    boolean isAutomaticExamination = yhOrderMapper.isAutomaticExaminationOfBranch(psBranchId);
                    Integer auditStatus = isAutomaticExamination ? 1 : 5;
                    resNum = yhOrderMapper.auditOrder(orderId, auditStatus, auditOrderModel.version, auditOrderModel.empId,null);
                }
            } else {
                if (psBranchId.compareTo(auditOrderModel.branchId) == 0 && status == 5) {
                    resNum = yhOrderMapper.auditOrder(orderId, 1, auditOrderModel.version, auditOrderModel.empId,"true");
                }
            }
            if (resNum == 0) {
                throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", auditOrderModel.code));
            }
            if (resNum == 1) {
                return;
            }
        }

        throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", auditOrderModel.code));

    }

    /**
     * 删除要货单
     *
     * @throws org.springframework.dao.OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException                                 单据不存在或已审核
     */
    public void delYhOrder(DelOrderModel delOrderModel) {
        QueryYhOrderModel model = new QueryYhOrderModel();
        model.tenantId = delOrderModel.tenantId;
        model.yhBranchId = delOrderModel.branchId;
        model.code = delOrderModel.code;
        List<HashMap<String, Object>> rows = yhOrderMapper.queryYhOrderPager(model);
        HashMap<String, Object> info = rows.size() == 1 ? rows.get(0) : null;
        if (info == null || (Integer) info.get("status") == 1
                || delOrderModel.branchId.compareTo(new BigInteger(info.get("yhBranchId").toString())) != 0) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", delOrderModel.code));
        }
        BigInteger orderId = new BigInteger(info.get("orderId").toString());
        int res = yhOrderMapper.delOrder(orderId, delOrderModel.version, delOrderModel.empId);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        yhOrderMapper.delOrderDetail(orderId);

    }

    /**
     * 修改要货单
     *
     * @throws org.springframework.dao.OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException                                 单据不存在或已审核
     */
    public void updateYhOrder(YhUpdateOrderModel updateOrderModel) {
        QueryYhOrderModel model = new QueryYhOrderModel();
        model.tenantId = updateOrderModel.tenantId;
        model.yhBranchId = updateOrderModel.yhBranchId;
        model.code = updateOrderModel.code;
        List<HashMap<String, Object>> rows = yhOrderMapper.queryYhOrderPager(model);
        HashMap<String, Object> info = rows.size() == 1 ? rows.get(0) : null;
        if (info == null || (Integer) info.get("status") == 1
                || updateOrderModel.yhBranchId.compareTo(new BigInteger(info.get("yhBranchId").toString())) != 0) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", updateOrderModel.code));
        }
        BigInteger orderId = new BigInteger(info.get("orderId").toString());

        List<YhOrderDetail> details = new ArrayList<>(updateOrderModel.details.size());
        BigDecimal totalAmount = BigDecimal.ZERO;

        String[] goodsIds = new String[updateOrderModel.details.size()];
        int x = 0;
        for (YhUpdateOrderModel.Detail detail : updateOrderModel.details) {
            YhOrderDetail orderDetail = new YhOrderDetail();
            orderDetail.tenantId = updateOrderModel.tenantId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.amount = detail.quantity;
            orderDetail.price = detail.price;
            orderDetail.subtotal = detail.price.multiply(detail.quantity);
            orderDetail.createAt = new Date();
            orderDetail.createBy = updateOrderModel.empId.toString();
            orderDetail.memo = detail.memo;
            orderDetail.unitType = detail.unitType==null?BigInteger.ONE:detail.unitType;
            orderDetail.unitRelation = detail.unitRelation==null?BigDecimal.ONE:detail.unitRelation;
            orderDetail.tenantId=updateOrderModel.tenantId;
            totalAmount = totalAmount.add(orderDetail.subtotal);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }
        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(updateOrderModel.tenantId, true, updateOrderModel.yhBranchId, goodsIds);

        for (YhOrderDetail detail : details) {
            GoodsStoreInfo storeInfo = findStoreInfo(list, detail.goodsId);
            if (storeInfo == null || !storeInfo.getGoodsBranchCode().equals("000")) {
                throw new NonUniqueResultException("存在商品不管理库存或不存在");
            }
        }
        int res = yhOrderMapper.updateYhOrder(orderId, totalAmount, updateOrderModel.version, updateOrderModel.empId);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        yhOrderMapper.delOrderDetail(orderId);
        yhOrderMapper.saveYhOrderDetail(details, orderId);
    }

    /**
     * 查找商品库存
     *
     * @return null--商品不存在，否则返回
     */
    private GoodsStoreInfo findStoreInfo(List<GoodsStoreInfo> infoList, BigInteger goodsId) {
        for (GoodsStoreInfo info : infoList) {
            if (info.getGoodsId().compareTo(goodsId) == 0) {
                return info;
            }
        }
        return null;
    }

    /**
     * 保存配送出入库单
     */
    public BigInteger savePsOrder(PsOrderModel model) {
        PsOrder order = new PsOrder();
        List<PsOrderDetail> details = new ArrayList<>(model.details.size());
        initOrder(model, order, details);
        try {
            psOrderMapper.savePsOrder(order);
            psOrderMapper.savePsOrderDetail(details, order.id);
        } catch (DuplicateKeyException e) {
            String message;
            if (e.getMessage().contains("index_require_goods_code_deleted")) {
                message = String.format("单据号[%s]已入库", order.requireGoodsCode);
            } else {
                message = String.format("单据号[%s]已存在", order.code);
            }
            throw new NonUniqueResultException(message);
        }

        return order.id;
    }

    public void updatePsOrder(PsUpdateOrderModel model) {
        QueryPsOrderModel model1 = new QueryPsOrderModel();
        model1.tenantId = model.tenantId;
        model1.branchId = model.branchId;
        model1.code = model.code;
        model1.status = "0";
        List<HashMap<String, Object>> rows = psOrderMapper.queryPsOrderPager(model1);
        HashMap<String, Object> info = rows.size() == 1 ? rows.get(0) : null;
        if (info == null) {
            throw new ResourceNotExistException(String.format("出库单据[%s]不存在", model.requireGoodsCode));
        }
        PsOrder order = new PsOrder();
        List<PsOrderDetail> details = new ArrayList<>(model.details.size());
        initOrder(model, order, details);
        order.id = new BigInteger(info.get("orderId").toString());
        order.version = model.version;

        try {
            int res = psOrderMapper.updatePsOrder(order);
            if (res != 1) {
                throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
            }
            psOrderMapper.delPsOrderDetails(order.id);
            psOrderMapper.savePsOrderDetail(details, order.id);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已入库", order.requireGoodsCode));
        }
    }

    private void initOrder(PsOrderModel model, PsOrder order, List<PsOrderDetail> details) {
        order.tenantId = model.tenantId;
        order.requireGoodsCode = model.requireGoodsCode;
        order.code = model.code;
        order.branchId = model.branchId;
        order.targetBranchId = model.targetBranchId;
        order.type = model.type;
        order.makeBy = model.empId;
        order.makeAt = new Date();
        order.memo = model.memo;
        order.isUpdatePrice = model.isUpdatePrice == null ? 1 : model.isUpdatePrice;

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDistributionPrice = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;


        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (PsOrderModel.Detail detail : model.details) {
            PsOrderDetail orderDetail = new PsOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.distributionPrice = detail.distributionPrice;
            orderDetail.amount = detail.distributionPrice.multiply(orderDetail.quantity);
            orderDetail.unitType = detail.unitType==null?BigInteger.ONE:detail.unitType;
            orderDetail.unitRelation = detail.unitRelation==null?BigDecimal.ONE:detail.unitRelation;
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.makeBy.toString();
            totalAmount = totalAmount.add(orderDetail.amount);
            totalDistributionPrice = totalDistributionPrice.add(orderDetail.distributionPrice);
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }
        if (order.type == 2) {
            //是否存在不管理库存商品
            List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);

            for (PsOrderDetail detail : details) {
                GoodsStoreInfo storeInfo = findStoreInfo(list, detail.goodsId);
                if (storeInfo == null || !storeInfo.getGoodsBranchCode().equals("000")) {
                    throw new NonUniqueResultException("存在商品不管理库存或不存在");
                }
            }
        } else {
            //入库单商品必须在出库单商品中，可以少于出库单
            QueryPsOrderModel model1 = new QueryPsOrderModel();
            model1.tenantId = model.tenantId;
            model1.branchId = model.targetBranchId;
            model1.targetBranchId = model.branchId;
            model1.code = model.requireGoodsCode;
            model1.status = "1";
            List<HashMap<String, Object>> rows = psOrderMapper.queryPsOrderPager(model1);
            HashMap<String, Object> info = rows.size() == 1 ? rows.get(0) : null;
            if (info == null) {
                throw new ResourceNotExistException(String.format("出库单据[%s]不存在", model.requireGoodsCode));
            }
            List<HashMap<String, Object>> infoList = psOrderMapper.queryPsOrderDetailList(new BigInteger(info.get("orderId").toString()));
            for (PsOrderDetail detail : details) {
                HashMap<String, Object> detailInfo = this.findPcInfo(infoList, detail.goodsId);
                if (detailInfo == null) {
                    throw new ResourceNotExistException(String.format("出库单[%s]中不存在商品[%s]", order.requireGoodsCode, detail.goodsId));
                }
            }
        }

        order.amount = totalAmount;
        order.quantity = totalQuantity;
        order.distributionPrice = totalDistributionPrice;
    }

    /**
     * 查找商品出库
     *
     * @return null--商品不存在，否则返回
     */
    private HashMap<String, Object> findPcInfo(List<HashMap<String, Object>> infoList, BigInteger goodsId) {
        for (HashMap<String, Object> info : infoList) {
            if (info.get("goodsId").toString().equals(goodsId.toString())) {
                return info;
            }
        }
        return null;
    }

    /**
     * 分页查询配送出入库单
     */
    public Pager queryPsGoodsPager(QueryPsOrderModel model) {
        long count = psOrderMapper.psOrderCount(model);
        model.getPager().setTotal(count);
        if (count > 0) {
            List<HashMap<String, Object>> rows = psOrderMapper.queryPsOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    public HashMap<String, Object> queryPsOrderInfo(BigInteger tId, BigInteger branchId, String code) {
        QueryPsOrderModel model1 = new QueryPsOrderModel();
        model1.tenantId = tId;
        model1.branchId = branchId;
        model1.code = code;
        List<HashMap<String, Object>> rows = psOrderMapper.queryPsOrderPager(model1);
        if (rows.size() == 1) {
            HashMap<String, Object> info = rows.get(0);
            List<HashMap<String, Object>> details = psOrderMapper.queryPsOrderDetailList(new BigInteger(info.get("orderId").toString()));
            info.put("details", details);
            return info;
        }
        return null;
    }

    public void delPsOrder(DelOrderModel delOrderModel) {
        QueryPsOrderModel model = new QueryPsOrderModel();
        model.tenantId = delOrderModel.tenantId;
        model.branchId = delOrderModel.branchId;
        model.code = delOrderModel.code;
        model.status = "0";
        List<HashMap<String, Object>> rows = psOrderMapper.queryPsOrderPager(model);
        HashMap<String, Object> info = rows.size() == 1 ? rows.get(0) : null;
        if (info == null) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", delOrderModel.code));
        }
        BigInteger orderId = new BigInteger(info.get("orderId").toString());
        int res = psOrderMapper.delOrder(orderId, delOrderModel.version, delOrderModel.empId);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        psOrderMapper.delPsOrderDetails(orderId);
    }

    public void auditPsOrder(AuditOrderModel model) {
        HashMap<String, Object> info = queryPsOrderInfo(model.tenantId, model.branchId, model.code);
        if (info == null || ((Integer) info.get("status")) != 0) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }
        BigInteger orderId = new BigInteger(info.get("orderId").toString());
        int res = psOrderMapper.auditOrder(orderId, model.version, model.empId);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> details = (List<HashMap<String, Object>>) info.get("details");
        List<StoreCompute> computes = new ArrayList<>();
        for (HashMap<String, Object> detail : details) {
            StoreCompute compute = new StoreCompute();
            compute.tenantId = new BigInteger(info.get("tenantId").toString());
            compute.branchId = new BigInteger(info.get("branchId").toString());
            compute.goodsId = new BigInteger(detail.get("goodsId").toString());
            compute.code = (String) info.get("code");
            Integer type = (Integer) info.get("type");
            compute.occurType = type == 2 ? StoreCompute.PS_OUT : StoreCompute.PS_IN;
            compute.price = type == 2 ? null : (BigDecimal) detail.get("distributionPrice");
            compute.quantity = new BigDecimal(detail.get("quantity").toString());
            int unitType = 1;
            if(detail.get("unitType") != null && !"".equals(detail.get("unitType"))){
                unitType = Integer.parseInt(detail.get("unitType").toString());
            }
            BigDecimal unitRelation = BigDecimal.ONE;
            if(detail.get("unitRelation") != null && !"".equals(detail.get("unitRelation"))){
                unitRelation = BigDecimal.valueOf(Double.valueOf(detail.get("unitRelation").toString()));
            }
            if(unitType == 2){
                BigDecimal price = compute.price;
                BigDecimal quantity = compute.quantity;
                if(price != null){
                    compute.price = price.divide(unitRelation,10,BigDecimal.ROUND_HALF_UP);
                }
                compute.quantity = quantity.multiply(unitRelation);
            }
            compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
            compute.createBy = model.empId;
            compute.isUpdatePrice = (Integer) (info.get("isUpdatePrice") == null ? 1 : info.get("isUpdatePrice"));
            computes.add(compute);
        }
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
    }

    /**
     * 分页查询要货商品汇总
     */
    public Pager queryYhGoodsSumPager(QueryYhGoodsSumModel model) {
        MapUnderscoreToCamelCase<String,Object> res = yhOrderMapper.queryYhGoodsSumCount(model);
        model.getPager().setTotal((Long) res.get("count"));
        res.remove("count");
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = yhOrderMapper.queryYhGoodsSumPager(model);
            model.getPager().setRows(rows);
        }
        model.getPager().setFooter(res);
        return model.getPager();
    }

    /**
     * 查询机构可以购买的产品及库存数量
     * @param params
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> listRequireGoods(final Map<String, String> params) throws IOException {
        List<Map<String, Object>> data = null;
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Validate.notNull(branch, "机构不存在！");

        Branch distributionCenter = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branch.getDistributionCenterId());
        Validate.notNull(distributionCenter, "机构没有指定配送中心！");

        //权限区域控制
        if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
            BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
            Employee e = employeeMapper.getEmployee(userId, tenantId);
            String userAreas = e.getUserAreas();
            String areaId = distributionCenter.getAreaId().toString();
            if(userAreas == null || userAreas.indexOf(areaId) == -1){
                return data;
            }
        }

        Map<String, Object> namedParams = new HashMap<String, Object>();
        namedParams.put("tenantId", tenantId);
        namedParams.put("branchId", branchId);
        namedParams.put("distributionCenterId", distributionCenter.getId());
        if (StringUtils.isNotBlank(params.get("categoryIds"))) {
            namedParams.put("categoryIds", params.get("categoryIds"));
        }

        if (StringUtils.isNotBlank(params.get("barCodeOrName"))) {
            namedParams.put("barCodeOrName", "%" + params.get("barCodeOrName") + "%");
        }

        if (distributionCenter.getIsDockingExternalSystem() && distributionCenter.getExternalSystem() == 1) {
            data = yhOrderMapper.listRequireGoodsByDockingExternalSystem(namedParams);
            Map<String, String> parameters = new HashMap<String, String>() {
                {
                    put("tenantCode", params.get("tenantCode"));
                }
            };
            ApiRest stockRest = ProxyApi.proxyGet("gateway", "grasp", "stock", parameters);
            Validate.isTrue(stockRest.getIsSuccess(), "查询商品库存失败：" + stockRest.getMessage());

            JSONArray jsonArray = JSONArray.fromObject(stockRest.getData());
            int size = jsonArray.size();
            for (Map<String, Object> map : data) {
                String otherGoodsId = (String) map.get("otherGoodsId");
                for (int index = 0; index < size; index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    String ptypeid = jsonObject.getString("ptypeid");
                    if (otherGoodsId.equals(ptypeid)) {
                        BigDecimal qty = BigDecimal.valueOf(jsonObject.getDouble("qty"));
                        map.put("stockAmount", qty);
                    }
                }
            }
        } else {
            data = yhOrderMapper.listRequireGoods(namedParams);
        }
        return data;
    }

    /**
     * 保存要货单
     * @param params
     */
    public YhOrder saveRequiredGoodsOrder(Map<String, String> params) {
        String paramsJson = params.get("paramsJson");
        JSONArray jsonArray = JSONArray.fromObject(paramsJson);
        int size = jsonArray.size();
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        String tenantCode = params.get("tenantCode");
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Validate.notNull(branch, "机构不存在！");

        Branch distributionCenter = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branch.getDistributionCenterId());
        Validate.notNull(distributionCenter, "机构没有指定配送中心！");

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (int index = 0; index < size; index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            goodsIds.add(BigInteger.valueOf(jsonObject.getLong("goodsId")));
        }
        //List<BranchRequireGoodsSetting> branchRequireGoodsSettings = branchRequireGoodsSettingMapper.findAllByGoodsIdInListAndBranchIdAndTenantIdAndDistributionCenterId(goodsIds, branchId, tenantId, distributionCenter.getId());
        YhOrder yhOrder = new YhOrder();
        String type = "YH";
        String serialNumber = serialNumberCreatorMapper.getBranchToday(type + tenantId.toString() + branchId.toString(), 3, branchId);
        String requireGoodsOrderNo = String.format("%s%s%s", type, DateFormatUtils.format(new Date(), "yyyMMdd"), serialNumber);
        yhOrder.setRequireGoodsOrderNo(requireGoodsOrderNo);
        yhOrder.setTenantId(tenantId);
        yhOrder.setTenantCode(tenantCode);
        yhOrder.setBranchId(branch.getId());
        yhOrder.setDistributionCenterId(distributionCenter.getId());
        yhOrder.makeAt = new Date();
        yhOrder.makeBy = params.get("userName");
        yhOrder.createUserId = BigInteger.valueOf(Long.valueOf(params.get("userId")));
        yhOrder.status = 0;
        yhOrder.setAmountPaid(BigDecimal.ZERO);
        /*if (distributionCenter.isAutomaticExamination()) {
            yhOrder.auditUserId = BigInteger.ZERO;
            yhOrder.auditAt = new Date();
            yhOrder.auditBy = "自动";
            yhOrder.status = 1;
        }*/

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<YhOrderDetail> yhOrderDetails = new ArrayList<YhOrderDetail>();
        for (int index = 0; index < size; index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            YhOrderDetail yhOrderDetail = new YhOrderDetail();
            BigInteger goodsId = BigInteger.valueOf(jsonObject.getLong("goodsId"));
            BigDecimal amount = BigDecimal.valueOf(jsonObject.getDouble("amount"));
            yhOrderDetail.setGoodsId(goodsId);
            yhOrderDetail.setAmount(amount);
            /*for (BranchRequireGoodsSetting branchRequireGoodsSetting : branchRequireGoodsSettings) {
                if (branchRequireGoodsSetting.getGoodsId().compareTo(goodsId) == 0) {
                    yhOrderDetail.setPrice(branchRequireGoodsSetting.getShippingPrice());
                    break;
                }
            }
            if(yhOrderDetail.getPrice() != null){
                    yhOrderDetail.setSubtotal(yhOrderDetail.getAmount().multiply(yhOrderDetail.getPrice()));
            }else{

            }*/
            BigDecimal price = BigDecimal.valueOf(jsonObject.getDouble("price"));
            yhOrderDetail.setPrice(price);
            yhOrderDetail.setSubtotal(yhOrderDetail.getAmount().multiply(yhOrderDetail.getPrice()));
            yhOrderDetail.setDistributionQuantity(BigDecimal.ZERO);
            yhOrderDetail.setReceivingQuantity(BigDecimal.ZERO);
            yhOrderDetail.setTenantId(tenantId);
            yhOrderDetail.setUnitType(jsonObject.get("unitType") == null ? BigInteger.ONE : BigInteger.valueOf(jsonObject.getLong("unitType")));
            yhOrderDetail.setUnitRelation(jsonObject.get("unitRelation") == null ? BigDecimal.ONE : BigDecimal.valueOf(jsonObject.getDouble("unitRelation")));
            yhOrderDetail.setCreateAt(new Date());
            yhOrderDetail.setLastUpdateAt(new Date());
            yhOrderDetails.add(yhOrderDetail);
            totalPrice = totalPrice.add(yhOrderDetail.subtotal);
        }
        yhOrder.setTotalAmount(totalPrice);
        yhOrder.setVersion(Long.valueOf(0));
        boolean isSendPaymentOrder = false;
        if (yhOrder.getTotalAmount().compareTo(BigDecimal.ZERO) == 0 && yhOrder.getStatus() == 1) {
            yhOrder.setStatus(2);
            isSendPaymentOrder = true;
        }
        yhOrderMapper.saveYhOrder(yhOrder);
        yhOrderMapper.saveYhOrderDetail(yhOrderDetails, yhOrder.getId());
        if (isSendPaymentOrder && distributionCenter.getIsDockingExternalSystem() && distributionCenter.getExternalSystem() == 1) {
            sendPaymentOrder(yhOrder);
        }
        return yhOrder;
    }

    /**
     * 发送管家婆库存系统发送要货单
     * @param yhOrder
     * @return
     */
    public ApiRest sendPaymentOrder(YhOrder yhOrder) {
        BigInteger tenantId = yhOrder.getTenantId();
        String tenantCode = yhOrder.getTenantCode();
        BigInteger ourBranchId = yhOrder.getBranchId();
        BigInteger distributionCenterId = yhOrder.getDistributionCenterId();
        BigInteger requireGoodsOrderId = yhOrder.getId();
        Map<String, Object> daqGraspBtype = yhOrderMapper.queryDaqGraspBtype(tenantId, ourBranchId, distributionCenterId, tenantCode);
        String bTypeID = (String) daqGraspBtype.get("typeId");
        String bTypeName = (String) daqGraspBtype.get("fullName");
        String number = yhOrder.getRequireGoodsOrderNo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TenantCode", tenantCode);
        jsonObject.put("BTypeID", bTypeID);
        jsonObject.put("BTypeName", bTypeName);
        jsonObject.put("Number", number);

        List<Map<String, Object>> requireGoodsOrderDetails = yhOrderMapper.queryRequireGoodsOrderDetails(tenantId, tenantCode, distributionCenterId, requireGoodsOrderId);
        JSONArray dlyData = new JSONArray();
        BigDecimal total = BigDecimal.ZERO;
        int size = requireGoodsOrderDetails.size();
        for (int index = 0; index < size; index++) {
            Map<String, Object> data = requireGoodsOrderDetails.get(index);
            JSONObject dlyDataItem = new JSONObject();
            if (index == 0) {
                String ptypeName = yhOrderMapper.queryDaqGraspPtypeNameByTypeId((String) data.get("otherGoodsId"), tenantCode);
                dlyDataItem.put("PTypeName", ptypeName);
            }
            BigDecimal amount = (BigDecimal) data.get("amount");
            BigDecimal unitRate = (BigDecimal) data.get("unitRate");
            BigDecimal price = (BigDecimal) data.get("price");
            total = total.add((BigDecimal) data.get("subtotal"));
            dlyDataItem.put("PTypeID", data.get("otherGoodsId"));
            dlyDataItem.put("Qty", amount.multiply(unitRate).toString());
            dlyDataItem.put("Price", price.divide(unitRate, 10, BigDecimal.ROUND_HALF_UP).toString());
            dlyDataItem.put("Unit", data.get("unitId").toString());
            dlyData.add(dlyDataItem);
        }
        jsonObject.put("Total", total.toString());
        jsonObject.put("DlyData", dlyData);
        LogUtil.logInfo(new SimpleDateFormat("yyyy - MM - dd HH:mm:ss").format(new Date()) + "向管家婆发送要货单：" + jsonObject.toString());
        Long lpushReturn = QueueUtils.lpush("topic_grasp_order", jsonObject.toString());
        Validate.notNull(lpushReturn, "向管家婆发送数据错误！");
        ApiRest apiRest = new ApiRest();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("要货单发送成功！");
        return apiRest;
    }

    /**
     * 根据要货单ID查询要货单详情
     * @param params
     * @return
     */
    public Map<String, Object> findRequireGoodsOrderById(Map<String, String> params) {
        BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id")));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        YhOrder yhOrder = yhOrderMapper.findByIdAndTenantIdAndBranchId(id, tenantId, branchId);
        Validate.notNull(yhOrder, "ID为：" + id.toString() + "的要货单不存在");
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        List<Map<String, Object>> list = yhOrderMapper.queryRequireGoodsOrderDetailsByRequireGoodsOrderId(yhOrder.getId());
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("branch", branch);
        result.put("requireGoodsOrder", yhOrder);
        result.put("requireGoodsOrderDetails", list);
        return result;
    }

    public ApiRest payRequireGoodsOrderById(BigInteger requireGoodsOrderId, Integer paidType, String submitBy, BigInteger tenantId, BigInteger branchId, String ipAddress, String openid) throws IOException {
        YhOrder yhOrder = yhOrderMapper.findByIdAndTenantIdAndBranchId(requireGoodsOrderId, tenantId, branchId);
        Validate.notNull(yhOrder, "要货单不存在！");
        Validate.isTrue(yhOrder.status == 1, "要货单状态异常！");
        Branch headquarters = branchMapper.findHeadquartersBranch(tenantId);
        ApiRest apiRest = null;
        switch (paidType) {
            case 2: apiRest = doWechatPay(yhOrder, ipAddress, submitBy, tenantId, openid, headquarters.getId());
                break;
        }
        return apiRest;
    }

    private ApiRest doWechatPay(YhOrder yhOrder, String ipAddress, String submitBy, BigInteger tenantId, String openid, BigInteger branchId) throws IOException {
        String body = "要货单支付";
        String outTradeNo = yhOrder.getRequireGoodsOrderNo();
        String productId = yhOrder.getRequireGoodsOrderNo();
        String notifyUrl = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_REST, PropertyUtils.getDefault("partition.code")) + "/chainPSSWebService/wxPayCallBack/" + yhOrder.getId();
        LogUtil.logInfo(notifyUrl);
//        String notifyUrl = "http://192.168.0.165:9001/chainPSSWebService/wxPayCallBack"
        int totalFee = yhOrder.totalAmount.multiply(new BigDecimal(100)).intValue();
        Map<String, String> params = new HashMap<String, String>();
        params.put("out_trade_no", outTradeNo);
        params.put("total_fee", String.valueOf(totalFee));
        params.put("body", body);
        params.put("ip", ipAddress);
        params.put("product_id", productId);
        params.put("notify_url", notifyUrl);
        params.put("tenantId", tenantId.toString());
        params.put("branchId", branchId.toString());
        params.put("supOpenid", openid);
        params.put("attach", submitBy);
        ApiRest payRest = ProxyApi.proxyPost(ProxyApi.app_out, "pay", "wxpayUnifiedorder", params);
        Validate.isTrue(payRest.getIsSuccess(), "微信下单失败：" + payRest.getMessage());
        return payRest;
    }

    public ApiRest changeRequireGoodsOrderStatus(Map<String, String> params, Integer paidType) throws ParseException {
        ApiRest apiRest = null;
        String requireGoodsOrderNo = null;
        BigDecimal amountPaid = null;
        if (paidType == 2) {
            requireGoodsOrderNo = params.get("out_trade_no");
            amountPaid = new BigDecimal(params.get("total_fee")).divide(new BigDecimal(100));
        } else if (paidType == 1) {
            requireGoodsOrderNo = params.get("out_trade_no");
            amountPaid = new BigDecimal(params.get("total_amount"));
        }
        YhOrder yhOrder = null;
        String requireGoodsOrderId = params.get("requireGoodsOrderId");
        if (StringUtils.isNotBlank(requireGoodsOrderId)) {
            yhOrder = yhOrderMapper.findByRequireGoodsOrderNoAndId(requireGoodsOrderNo, NumberUtils.createBigInteger(requireGoodsOrderId));
        } else {
            yhOrder = yhOrderMapper.findByRequireGoodsOrderNo(requireGoodsOrderNo);
        }
        Validate.notNull(yhOrder, "单据号为：" + requireGoodsOrderNo + "的要货单不存在！");
        if (yhOrder.status == 1) {
            yhOrder.status = 2;
            yhOrder.lastPaid = new Date();
            yhOrder.paidType = paidType;
            yhOrder.amountPaid = amountPaid;
            yhOrderMapper.updateRequireGoodsOrder(yhOrder);
            generatePaymentRecords(yhOrder, params, paidType);
            Branch distributionCenter = branchMapper.findBranchByTenantIdAndBranchId(yhOrder.getTenantId(), yhOrder.getDistributionCenterId());
            if (distributionCenter.getIsDockingExternalSystem() && distributionCenter.getExternalSystem() == 1) {
                apiRest = sendPaymentOrder(yhOrder);
            } else {
                apiRest = new ApiRest();
                apiRest.setIsSuccess(true);
                apiRest.setMessage("支付信息处理成功！");
            }
        } else {
            apiRest = new ApiRest();
            apiRest.setIsSuccess(true);
            apiRest.setMessage("支付信息处理成功");
        }
        return apiRest;
    }

    /**
     * 生成付款记录
     * @param yhOrder
     * @param params
     * @param payType
     * @throws ParseException
     */
    private void generatePaymentRecords(YhOrder yhOrder, Map<String, String> params, Integer payType) throws ParseException {
        RequireGoodsPaymentOrder requireGoodsPaymentOrder = new RequireGoodsPaymentOrder();
        requireGoodsPaymentOrder.setRequireGoodsOrderId(yhOrder.getId());
        requireGoodsPaymentOrder.setRequireGoodsOrderNo(yhOrder.getRequireGoodsOrderNo());
        requireGoodsPaymentOrder.setOrderTotal(yhOrder.totalAmount);
        requireGoodsPaymentOrder.setPayType(payType);
        requireGoodsPaymentOrder.setTenantId(yhOrder.getTenantId());
        if (payType == 2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            requireGoodsPaymentOrder.setPaidTotal(new BigDecimal(params.get("total_fee")).divide(new BigDecimal(100)));
            requireGoodsPaymentOrder.setSubmitBy(BigInteger.valueOf(Long.valueOf(params.get("attach"))));
            requireGoodsPaymentOrder.setTransactionId(params.get("transaction_id"));
            requireGoodsPaymentOrder.setTimeEnd(simpleDateFormat.parse(params.get("time_end")));
            requireGoodsPaymentOrder.setWechatPayAppid(params.get("appid"));
            requireGoodsPaymentOrder.setWechatPayMchid(params.get("mch_id"));
            requireGoodsPaymentOrder.setWechatPaySubMchid(params.get("sub_mch_id"));
        } else if (payType == 1) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            requireGoodsPaymentOrder.setPaidTotal(new BigDecimal(params.get("total_amount")));
            if (StringUtils.isNotBlank(params.get("attach"))) {
                requireGoodsPaymentOrder.setSubmitBy(BigInteger.valueOf(Long.valueOf(params.get("attach"))));
            }
            requireGoodsPaymentOrder.setTradeNo(params.get("trade_no"));
            requireGoodsPaymentOrder.setTimeEnd(simpleDateFormat.parse(params.get("notify_time")));
            requireGoodsPaymentOrder.setAlipayAppid(params.get("app_id"));
            requireGoodsPaymentOrder.setBuyerId(params.get("buyer_id"));
            requireGoodsPaymentOrder.setSellerId(params.get("seller_id"));
        }
        requireGoodsPaymentOrderMapper.insert(requireGoodsPaymentOrder);
    }

    /**
     * 查询最近一次要货单
     * */
    public ApiRest queryLastYhOrder(Map params){
        ApiRest apiRest = new ApiRest();
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong(params.get("distributionCenterId").toString()));
            params.put("tenantId", tenantId);
            params.put("branchId", branchId);
            params.put("distributionCenterId", distributionCenterId);
            List<Map> list = yhOrderMapper.queryLastYhOrder(params);
            if(list != null && list.size() > 0){
                Map info = list.get(0);
                List<HashMap<String, Object>> details = yhOrderMapper.queryYhOrderDetailList(new BigInteger(info.get("id").toString()));
                for(int i=0; i<details.size(); i++){
                    Map map = details.get(i);
                    map.put("yhQuantity", map.get("amount"));
                    map.put("gPrice", map.get("shippingPrice1"));
                }
                apiRest.setData(details);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("查询成功");
            }else{
                apiRest.setData(null);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("未查询到最近一次要货单据信息");
            }
        return apiRest;
    }

    /**
     * 查询机构要货模版中商品
     * */
    public ApiRest queryYhStencilGoods(Map params){
        ApiRest apiRest = new ApiRest();
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong(params.get("distributionCenterId").toString()));
            QueryBranchRequireGoodsSettingModel model = new QueryBranchRequireGoodsSettingModel();
            model.setTenantId(tenantId);
            model.setBranchId(branchId);
            model.setDistributionCenterId(distributionCenterId);
            long count = branchRequireGoodsSettingMapper.queryCount(model);
            if (count > 0) {
                List<MapUnderscoreToCamelCase> rows = branchRequireGoodsSettingMapper.queryPager(model);
                BigInteger [] ids = new BigInteger[rows.size()];
                for(int i=0; i<rows.size(); i++){
                    Map map = rows.get(i);
                    BigInteger id = BigInteger.valueOf(Long.parseLong(map.get("goodsId").toString()));
                    ids [i] = id;
                }
                List<Map<String, Object>> list = goodsMapper.queryRootBranchGoodsInfo(ids);
                if(list != null && list.size()>0){
                    for(int m=0; m<list.size(); m++){
                        Map map1 = list.get(m);
                        for (int n=0; n<rows.size(); n++){
                            Map map2 = rows.get(n);
                            if(map1.get("id").toString().equals(map2.get("goodsId").toString())){
                                map1.put("gPrice", map2.get("shippingPrice"));
                            }
                        }
                    }
                }
                apiRest.setData(list);
            }else{
                apiRest.setData(null);
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询要货模版商品成功");
        return apiRest;
    }
}
