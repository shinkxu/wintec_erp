package erp.chain.service.supply.store.impl;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.Goods;
import erp.chain.domain.o2o.DietOrderDetail;
import erp.chain.domain.o2o.DietOrderInfo;
import erp.chain.domain.supply.store.*;
import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.o2o.DietOrderDetailMapper;
import erp.chain.mapper.o2o.DietOrderInfoMapper;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.mapper.supply.store.StoreOrderMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.model.Pager;
import erp.chain.model.supply.store.*;
import erp.chain.service.o2o.VipService;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.DateUtils;
import erp.chain.utils.NonUniqueResultException;
import erp.chain.utils.ResourceNotExistException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 库存单据实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreOrderServiceImpl {
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private StoreOrderMapper storeOrderMapper;
    @Autowired
    private StoreComputeService storeComputeService;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private VipService vipService;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;

    private final Log logger = LogFactory.getLog(getClass());

    
    public BigInteger saveCgOrder(CgOrderModel model) {
        CgOrder order = new CgOrder();
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.makeBy = model.makeBy;
        order.orderType = model.type;
        order.makeAt = new Date();
        order.code = model.code;
        order.memo = model.memo;
        order.isUpdatePrice = model.isUpdatePrice == null ? 1 : model.isUpdatePrice;
        BigInteger supplierId = BigInteger.ZERO;
        if(model.supplierId == null){
            order.supplierId = supplierId;
        }else{
            order.supplierId = model.supplierId;
        }
        order.supplierName = model.supplierName;

        List<CgOrderDetail> details = new ArrayList<>(model.details.size());

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (CgOrderModel.Detail detail : model.details) {
            CgOrderDetail orderDetail = new CgOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.purchaseAmount = detail.purchaseAmount;
            orderDetail.quantity = detail.quantity;
            orderDetail.amount = orderDetail.quantity.multiply(orderDetail.purchaseAmount);
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.makeBy.toString();
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            totalAmount = totalAmount.add(orderDetail.amount);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;

        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        try {
            storeOrderMapper.saveCgOrder(order);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        for (CgOrderDetail detail : details) {
            detail.storeOrderId = order.id;
        }
        storeOrderMapper.saveCgOrderDetail(details);
        return order.id;
    }

    /**
     * 修改采购单
     */
    public void updateCgOrder(CgUpdateOrderModel model){
        QueryCgOrderModel queryCgOrderModel = new QueryCgOrderModel();
        queryCgOrderModel.tenantId = model.tenantId;
        queryCgOrderModel.branchId = model.branchId;
        queryCgOrderModel.code = model.code;
        List<HashMap<String, Object>> infoOrder = storeOrderMapper.queryCgOrderPager(queryCgOrderModel);
        if (infoOrder.size() != 1 || (Integer) infoOrder.get(0).get("status") == 1) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }

        CgOrder order = new CgOrder();
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.lastUpdateBy = model.makeBy.toString();
        order.lastUpdateAt = new Date();
        order.code = model.code;
        order.version = model.version;
        order.memo = model.memo;
        BigInteger supplierId = BigInteger.ZERO;
        if(model.supplierId == null){
            order.supplierId = supplierId;
        }else{
            order.supplierId = model.supplierId;
        }
        order.supplierName = model.supplierName;
        order.isUpdatePrice = model.isUpdatePrice == null ? 1 : model.isUpdatePrice;
        order.id = new BigInteger(infoOrder.get(0).get("orderId").toString());

        List<CgOrderDetail> details = new ArrayList<>(model.details.size());

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (CgUpdateOrderModel.Detail detail : model.details) {
            CgOrderDetail orderDetail = new CgOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.storeOrderId = order.id;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.purchaseAmount = detail.purchaseAmount;
            orderDetail.quantity = detail.quantity;
            orderDetail.amount = orderDetail.quantity.multiply(orderDetail.purchaseAmount);
            orderDetail.createAt = order.lastUpdateAt;
            orderDetail.createBy = order.lastUpdateBy.toString();
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            totalAmount = totalAmount.add(orderDetail.amount);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;

        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        int res = storeOrderMapper.updateCgOrder(order);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        storeOrderMapper.delOrderDetail(order.id, 3);
        storeOrderMapper.saveCgOrderDetail(details);
    }

    
    public BigInteger savePdOrder(PdOrderModel model) {
        PdOrder order = new PdOrder();
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.makeBy = model.empId;
        order.makeAt = new Date();
        order.code = model.code;
        order.memo = model.memo;

        List<PdOrderDetail> details = new ArrayList<>(model.details.size());
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (PdOrderModel.Detail detail : model.details) {
            PdOrderDetail orderDetail = new PdOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.makeBy.toString();
            orderDetail.reallyQuantity = detail.quantity;
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        BigDecimal totalCheckQuantity = BigDecimal.ZERO;
        BigDecimal totalCheckAmount = BigDecimal.ZERO;
        BigDecimal totalReallyQuantity = BigDecimal.ZERO;
        for (PdOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.purchasePrice = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
                detail.quantity = info.getStoreId() != null ? info.getQuantity().divide(detail.unitRelation,3,BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
            }else{
                //库存信息
                detail.purchasePrice = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
                detail.quantity = info.getStoreId() != null ? info.getQuantity() : BigDecimal.ZERO;
                detail.checkQuantity = detail.reallyQuantity.subtract(detail.quantity);
            }
            //损溢数量 = 实际数量 - 库存数量
            detail.checkQuantity = detail.reallyQuantity.subtract(detail.quantity);
            //损溢金额  = 损溢数量 * 库存成本
            detail.checkAmount = detail.checkQuantity.multiply(detail.purchasePrice);

            totalCheckQuantity = totalCheckQuantity.add(detail.checkQuantity);
            totalCheckAmount = totalCheckAmount.add(detail.checkAmount);
            totalReallyQuantity = totalReallyQuantity.add(detail.reallyQuantity);
        }
        order.checkQuantity = totalCheckQuantity;
        order.checkAmount = totalCheckAmount;
        order.reallyQuantity = totalReallyQuantity;

        try {
            storeOrderMapper.savePdOrder(order);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        for (PdOrderDetail detail : details) {
            detail.orderId = order.id;
        }
        storeOrderMapper.savePdOrderDetail(details);

        return order.id;
    }

    /**
     * 修改盘点单
     */
    public void updatePdOrder(PdUpdateOrderModel model){
        QueryPdOrderModel queryPdOrderModel = new QueryPdOrderModel();
        queryPdOrderModel.tenantId = model.tenantId;
        queryPdOrderModel.branchId = model.branchId;
        queryPdOrderModel.code = model.code;
        List<HashMap<String, Object>> infoOrder = storeOrderMapper.queryPdOrderPager(queryPdOrderModel);
        if (infoOrder.size() != 1 || (Integer) infoOrder.get(0).get("status") == 1) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }

        PdOrder order = new PdOrder();
        order.memo = model.memo;
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.lastUpdateBy = model.empId.toString();
        order.lastUpdateAt = new Date();
        order.code = model.code;
        order.version = model.version;
        order.id = new BigInteger(infoOrder.get(0).get("orderId").toString());
        List<PdOrderDetail> details = new ArrayList<>(model.details.size());
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (PdOrderModel.Detail detail : model.details) {
            PdOrderDetail orderDetail = new PdOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.orderId = order.id;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.lastUpdateAt;
            orderDetail.createBy = order.lastUpdateBy.toString();
            orderDetail.reallyQuantity = detail.quantity;
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        BigDecimal totalCheckQuantity = BigDecimal.ZERO;
        BigDecimal totalCheckAmount = BigDecimal.ZERO;
        BigDecimal totalReallyQuantity = BigDecimal.ZERO;
        for (PdOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.purchasePrice = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
                detail.quantity = info.getStoreId() != null ? info.getQuantity().divide(detail.unitRelation,3,BigDecimal.ROUND_CEILING) : BigDecimal.ZERO;
            }else{
                //库存信息
                detail.purchasePrice = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
                detail.quantity = info.getStoreId() != null ? info.getQuantity() : BigDecimal.ZERO;
            }
            //损溢数量 = 实际数量 - 库存数量
            detail.checkQuantity = detail.reallyQuantity.subtract(detail.quantity);
            //损溢金额  = 损溢数量 * 库存成本
            detail.checkAmount = detail.checkQuantity.multiply(detail.purchasePrice);

            totalCheckQuantity = totalCheckQuantity.add(detail.checkQuantity);
            totalCheckAmount = totalCheckAmount.add(detail.checkAmount);
            totalReallyQuantity = totalReallyQuantity.add(detail.reallyQuantity);
        }
        order.checkQuantity = totalCheckQuantity;
        order.checkAmount = totalCheckAmount;
        order.reallyQuantity = totalReallyQuantity;

        int res = storeOrderMapper.updatePdOrder(order);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        storeOrderMapper.delOrderDetail(order.id, 4);
        storeOrderMapper.savePdOrderDetail(details);
    }

    public BigInteger saveLyOrder(LyOrderModel model) {
        LyOrder order = new LyOrder();
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.makeBy = model.empId;
        order.type = model.type;
        order.makeAt = new Date();
        order.code = model.code;
        order.memo = model.memo;

        List<LyOrderDetail> details = new ArrayList<>(model.details.size());
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalIncurred = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (LyOrderModel.Detail detail : model.details) {
            LyOrderDetail orderDetail = new LyOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.makeBy.toString();
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        for (LyOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
                detail.amount = detail.incurred.multiply(detail.quantity);
            }else{
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
                detail.amount = detail.incurred.multiply(detail.quantity);
            }
            totalAmount = totalAmount.add(detail.amount);
            totalIncurred = totalIncurred.add(detail.incurred);
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;
        order.incurred = totalIncurred;
        try {
            storeOrderMapper.saveLyOrder(order);
            storeOrderMapper.saveLyOrderDetail(details, order.id);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        return order.id;
    }


    public void updateLyOrder(LyUpdateOrderModel model) {
        QueryLyOrderModel queryLyOrderModel = new QueryLyOrderModel();
        queryLyOrderModel.tenantId = model.tenantId;
        queryLyOrderModel.branchId = model.branchId;
        queryLyOrderModel.code = model.code;
        List<HashMap<String, Object>> infoOrder = storeOrderMapper.queryLyOrderPager(queryLyOrderModel);
        if (infoOrder.size() != 1 || (Integer) infoOrder.get(0).get("status") != 0) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }

        LyOrder order = new LyOrder();
        order.id = new BigInteger(infoOrder.get(0).get("orderId").toString());
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.lastUpdateBy = model.empId.toString();
        order.lastUpdateAt = new Date();
        order.code = model.code;
        order.version = model.version;
        order.memo = model.memo;

        List<LyOrderDetail> details = new ArrayList<>(model.details.size());
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalIncurred = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (LyUpdateOrderModel.Detail detail : model.details) {
            LyOrderDetail orderDetail = new LyOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.lastUpdateAt;
            orderDetail.createBy = order.lastUpdateBy;
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        for (LyOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
                detail.amount = detail.incurred.multiply(detail.quantity);
            }else{
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
                detail.amount = detail.incurred.multiply(detail.quantity);
            }
            totalAmount = totalAmount.add(detail.amount);
            totalIncurred = totalIncurred.add(detail.incurred);
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;
        order.incurred = totalIncurred;
        int res = storeOrderMapper.updateLyOrder(order);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        storeOrderMapper.delOrderDetail(order.id, 1);
        storeOrderMapper.saveLyOrderDetail(details, order.id);
    }


    public BigInteger saveSyOrder(SyOrderModel model) {
        SyOrder order = new SyOrder();
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.makeBy = model.empId;
        order.makeAt = new Date();
        order.code = model.code;
        order.memo = model.memo;
        List<SyOrderDetail> details = new ArrayList<>(model.details.size());
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalIncurred = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (SyOrderModel.Detail detail : model.details) {
            SyOrderDetail orderDetail = new SyOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = order.makeBy.toString();
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        for (SyOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
            }else{
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
            }
            detail.amount = detail.incurred.multiply(detail.quantity);
            totalAmount = totalAmount.add(detail.amount);
            totalIncurred = totalIncurred.add(detail.incurred);
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;
        order.incurred = totalIncurred;
        try {
            storeOrderMapper.saveSyOrder(order);
            storeOrderMapper.saveSyOrderDetail(details, order.id);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        return order.id;
    }


    public void updateSyOrder(SyUpdateOrderModel model) {
        QuerySyOrderModel orderModel = new QuerySyOrderModel();
        orderModel.tenantId = model.tenantId;
        orderModel.branchId = model.branchId;
        orderModel.code = model.code;
        List<HashMap<String, Object>> infoOrder = storeOrderMapper.querySyOrderPager(orderModel);
        if (infoOrder.size() != 1 || (Integer) infoOrder.get(0).get("status") != 0) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }

        SyOrder order = new SyOrder();
        order.id = new BigInteger(infoOrder.get(0).get("orderId").toString());
        order.tenantId = model.tenantId;
        order.branchId = model.branchId;
        order.lastUpdateBy = model.empId.toString();
        order.lastUpdateAt = new Date();
        order.code = model.code;
        order.version = model.version;
        order.memo = model.memo;
        List<SyOrderDetail> details = new ArrayList<>(model.details.size());
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalIncurred = BigDecimal.ZERO;
        String[] goodsIds = new String[model.details.size()];
        int x = 0;
        for (SyUpdateOrderModel.Detail detail : model.details) {
            SyOrderDetail orderDetail = new SyOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = detail.goodsId;
            orderDetail.quantity = detail.quantity;
            orderDetail.createAt = order.lastUpdateAt;
            orderDetail.createBy = order.lastUpdateBy;
            orderDetail.unitType = detail.unitType == null ? BigInteger.ONE : detail.unitType;
            orderDetail.unitRelation = detail.unitRelation == null ? BigDecimal.valueOf(1) : detail.unitRelation;
            totalQuantity = totalQuantity.add(orderDetail.quantity);
            details.add(orderDetail);
            goodsIds[x++] = detail.goodsId.toString();
        }

        //是否存在不管理库存商品
        List<GoodsStoreInfo> list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if (list.size() != details.size()) {
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        for (SyOrderDetail detail : details) {
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            int unitType = Integer.parseInt(detail.unitType.toString());
            if(unitType == 2){
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount().multiply(detail.unitRelation) : info.getPurchasingPrice().multiply(detail.unitRelation);
            }else{
                detail.incurred = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
            }
            detail.amount = detail.incurred.multiply(detail.quantity);
            totalAmount = totalAmount.add(detail.amount);
            totalIncurred = totalIncurred.add(detail.incurred);
        }
        order.quantity = totalQuantity;
        order.amount = totalAmount;
        order.incurred = totalIncurred;
        int res = storeOrderMapper.updateSyOrder(order);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        storeOrderMapper.delOrderDetail(order.id, 2);
        storeOrderMapper.saveSyOrderDetail(details, order.id);
    }


    public Pager querySyOrderPager(QuerySyOrderModel model) {
        long count = storeOrderMapper.querySyOrderCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.querySyOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }


    public HashMap<String, Object> querySyOrderInfo(BigInteger tId, BigInteger branchId, String code) {
        QuerySyOrderModel model = new QuerySyOrderModel();
        model.tenantId = tId;
        model.branchId = branchId;
        model.code = code;

        QuerySyOrderDetailModel detailModel = new QuerySyOrderDetailModel();
        detailModel.tenantId = tId;
        detailModel.branchId = branchId;
        detailModel.code = code;

        List<HashMap<String, Object>> order = storeOrderMapper.querySyOrderPager(model);
        if (order.size() == 1) {
            HashMap<String, Object> orderInfo = order.get(0);
            List<HashMap<String, Object>> rows = storeOrderMapper.querySyOrderDetailList(detailModel);
            List<HashMap<String, Object>> footer = storeOrderMapper.querySyOrderDetailListTotal(detailModel);
            orderInfo.put("details", rows);
            orderInfo.put("footer", footer);
            for (HashMap<String, Object> row : rows) {
                row.remove("code");
                row.remove("makeAt");
                row.remove("empName");
                row.remove("empCode");
                row.remove("empId");
                row.remove("status");
            }
            return orderInfo;
        }
        return null;
    }

    /**
     * 审核单据
     */

    public boolean auditOrder(AuditOrderModel model) {

        HashMap<String, Object> info = null;
        Integer occurType = 0;
        if (model.type == 1) {//领用单
            info = this.queryLyOrderInfo(model.tenantId, model.branchId, model.code);
            occurType = info != null && (Integer) info.get("type") == 1 ? StoreCompute.LY_IN : StoreCompute.LY_OUT;
        }
        if (model.type == 2) {//sy单
            info = this.querySyOrderInfo(model.tenantId, model.branchId, model.code);
            occurType = StoreCompute.SY_IN_OUT;
        }
        if (model.type == 3) {//cg单
            info = this.queryCgOrderInfo(model.tenantId, model.branchId, model.code);
            occurType = info != null && (Integer) info.get("orderType") == 1 ? StoreCompute.PURCHASE_ADD : StoreCompute.PURCHASE_RETURN;
        }
        if (model.type == 4) {//pd单
            info = this.queryPdOrderInfo(model.tenantId, model.branchId, model.code);
            occurType = StoreCompute.INVENTORY;
        }
        if (info == null || (Integer) info.get("status") == 1) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }

        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> details = (List<HashMap<String, Object>>) info.get("details");
        if(details.size() == 0){
            return false;
        }

        int val = storeOrderMapper.auditOrder(model);
        if (val != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        List<StoreCompute> computes = new ArrayList<>();
        for (HashMap<String, Object> detail : details) {
            StoreCompute compute = new StoreCompute();
            int unitType = 1;
            if(detail.get("unitType") != null && !"".equals(detail.get("unitType"))){
                unitType = Integer.parseInt(detail.get("unitType").toString());
            }
            BigDecimal unitRelation = BigDecimal.ONE;
            if(detail.get("unitRelation") != null && !"".equals(detail.get("unitRelation"))){
                unitRelation = BigDecimal.valueOf(Double.valueOf(detail.get("unitRelation").toString()));
            }
            compute.tenantId = model.tenantId;
            compute.branchId = model.branchId;
            compute.goodsId = new BigInteger(detail.get("goodsId").toString());
            compute.code = (String) info.get("code");
            compute.occurType = occurType;
            //compute.quantity = (BigDecimal) detail.get("quantity");
            if (model.type == 3){
                if(unitType == 2){
                    compute.price = ((BigDecimal) detail.get("purchaseAmount")).divide(unitRelation,10,BigDecimal.ROUND_HALF_DOWN);
                    compute.quantity = ((BigDecimal) detail.get("purchaseQuantity")).multiply(unitRelation);
                }else{
                    compute.price = (BigDecimal) detail.get("purchaseAmount");
                    compute.quantity = (BigDecimal) detail.get("purchaseQuantity");
                }
            }else  if (model.type == 4){
                if(unitType == 2){
                    compute.price = ((BigDecimal) detail.get("purchasePrice")).divide(unitRelation,10,BigDecimal.ROUND_HALF_DOWN);
                    compute.checkQuantity = ((BigDecimal) detail.get("checkQuantity")).multiply(unitRelation);
                    compute.quantity = ((BigDecimal) detail.get("reallyQuantity")).multiply(unitRelation);
                }else{
                    compute.price = (BigDecimal) detail.get("purchasePrice");
                    compute.checkQuantity = (BigDecimal) detail.get("checkQuantity");
                    compute.quantity = (BigDecimal) detail.get("reallyQuantity");
                }
            }else{
                if(unitType == 2){
                    compute.quantity = ((BigDecimal) detail.get("quantity")).multiply(unitRelation);
                }else{
                    compute.quantity = (BigDecimal) detail.get("quantity");
                }
            }
            compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
            compute.createBy = new BigInteger(info.get("empId").toString());
            if(model.type == 3){
                Map params = new HashMap();
                params.put("tenantId", model.tenantId);
                params.put("id", detail.get("goodsId"));
                Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
                if(model.branchId.compareTo(goods.getBranchId()) == 0){
                    compute.isUpdatePrice = (Integer) (info.get("isUpdatePrice") == null ? 1 : info.get("isUpdatePrice"));
                }else{
                    compute.isUpdatePrice = 0;
                }
            }
            computes.add(compute);
        }
        //计算库存
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
        return true;
    }


    public void delOrder(DelOrderModel model) {

        List<HashMap<String, Object>> infoOrder = null;
        if (model.type == 1) {//领用单
            QueryLyOrderModel queryLyOrderModel = new QueryLyOrderModel();
            queryLyOrderModel.tenantId = model.tenantId;
            queryLyOrderModel.branchId = model.branchId;
            queryLyOrderModel.code = model.code;
            infoOrder = storeOrderMapper.queryLyOrderPager(queryLyOrderModel);
        }
        if (model.type == 2) {//SY
            QuerySyOrderModel orderModel = new QuerySyOrderModel();
            orderModel.tenantId = model.tenantId;
            orderModel.branchId = model.branchId;
            orderModel.code = model.code;
            infoOrder = storeOrderMapper.querySyOrderPager(orderModel);
        }
        if (model.type == 3) {//cg
            QueryCgOrderModel orderModel = new QueryCgOrderModel();
            orderModel.tenantId = model.tenantId;
            orderModel.branchId = model.branchId;
            orderModel.code = model.code;
            infoOrder = storeOrderMapper.queryCgOrderPager(orderModel);
        }
        if (model.type == 4) {//pd
            QueryPdOrderModel orderModel = new QueryPdOrderModel();
            orderModel.tenantId = model.tenantId;
            orderModel.branchId = model.branchId;
            orderModel.code = model.code;
            infoOrder = storeOrderMapper.queryPdOrderPager(orderModel);
        }

        if (infoOrder == null || infoOrder.size() != 1 || (Integer) infoOrder.get(0).get("status") == 1) {
            throw new ResourceNotExistException(String.format("单据[%s]不存在或已审核", model.code));
        }
        BigInteger orderId = new BigInteger(infoOrder.get(0).get("orderId").toString());
        int res = storeOrderMapper.delOrder(orderId, model.version, model.type, model.empId);
        if (res != 1) {
            throw new OptimisticLockingFailureException(String.format("单据[%s]数据版本过期", model.code));
        }
        storeOrderMapper.delOrderDetail(orderId, model.type);
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


    public Pager queryCgOrderPager(QueryCgOrderModel model) {
        long count = storeOrderMapper.queryCgOrderCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryCgOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }


    public Pager queryPdOrderPager(QueryPdOrderModel model) {
        long count = storeOrderMapper.queryPdOrderCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryPdOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }


    public Pager queryLyOrderPager(QueryLyOrderModel model) {
        long count = storeOrderMapper.queryLyOrderCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryLyOrderPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    public Pager queryLyOrderDetailPager(QueryLyOrderDetailModel model) {
        long count = storeOrderMapper.queryLyOrderDetailCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryLyOrderDetailPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    public HashMap<String, Object> queryLyOrderInfo(BigInteger tId, BigInteger branchId, String code) {
        QueryLyOrderModel model = new QueryLyOrderModel();
        model.tenantId = tId;
        model.branchId = branchId;
        model.code = code;

        QueryLyOrderDetailModel detailModel = new QueryLyOrderDetailModel();
        detailModel.tenantId = tId;
        detailModel.branchId = branchId;
        detailModel.code = code;

        List<HashMap<String, Object>> order = storeOrderMapper.queryLyOrderPager(model);
        if (order.size() == 1) {
            HashMap<String, Object> orderInfo = order.get(0);
            List<HashMap<String, Object>> rows = storeOrderMapper.queryLyOrderDetailList(detailModel);
            List<HashMap<String, Object>> footer = storeOrderMapper.queryLyOrderDetailListTotal(detailModel);
            orderInfo.put("footer",footer);
            orderInfo.put("details", rows);
            for (HashMap<String, Object> row : rows) {
                row.remove("code");
                row.remove("type");
                row.remove("makeAt");
                row.remove("empName");
                row.remove("empCode");
                row.remove("empId");
                row.remove("status");
            }
            return orderInfo;
        }
        return null;
    }

    public Pager queryCgOrderDetailPager(QueryCgOrderDetailModel model) {
        long count = storeOrderMapper.queryCgOrderDetailCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryCgOrderDetailPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    public Pager queryPdOrderDetailPager(QueryPdOrderDetailModel model) {
        long count = storeOrderMapper.queryPdOrderDetailCount(model);
        model.getPager().setTotal(count);
        if (model.getPager().getTotal() > 0) {
            List<HashMap<String, Object>> rows = storeOrderMapper.queryPdOrderDetailPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    public HashMap<String, Object> queryCgOrderInfo(BigInteger tId, BigInteger branchId, String code) {
        QueryCgOrderModel model = new QueryCgOrderModel();
        model.tenantId = tId;
        model.branchId = branchId;
        model.code = code;

        QueryCgOrderDetailModel detailModel = new QueryCgOrderDetailModel();
        detailModel.tenantId = tId;
        detailModel.branchId = branchId;
        detailModel.code = code;

        List<HashMap<String, Object>> order = storeOrderMapper.queryCgOrderPager(model);
        if (order.size() == 1) {
            HashMap<String, Object> orderInfo = order.get(0);
            List<HashMap<String, Object>> rows = storeOrderMapper.queryCgOrderDetailList(detailModel);
            List<HashMap<String, Object>> footer=storeOrderMapper.queryCgOrderDetailListTotal(detailModel);
            orderInfo.put("details", rows);
            orderInfo.put("footer",footer.get(0));
            for (HashMap<String, Object> row : rows) {
                row.remove("code");
                row.remove("orderType");
                row.remove("makeAt");
                row.remove("empName");
                row.remove("empCode");
                row.remove("empId");
            }
            return orderInfo;
        }
        return null;
    }

    public HashMap<String, Object> queryPdOrderInfo(BigInteger tId, BigInteger branchId, String code) {
        QueryPdOrderModel model = new QueryPdOrderModel();
        model.tenantId = tId;
        model.branchId = branchId;
        model.code = code;

        QueryPdOrderDetailModel detailModel = new QueryPdOrderDetailModel();
        detailModel.tenantId = tId;
        detailModel.branchId = branchId;
        detailModel.code = code;

        List<HashMap<String, Object>> order = storeOrderMapper.queryPdOrderPager(model);
        if (order.size() == 1) {
            HashMap<String, Object> orderInfo = order.get(0);
            List<HashMap<String, Object>> rows = storeOrderMapper.queryPdOrderDetailList(detailModel);
            orderInfo.put("details", rows);
            for (HashMap<String, Object> row : rows) {
                row.remove("code");
                row.remove("makeAt");
                row.remove("empName");
                row.remove("empCode");
                row.remove("empId");
            }
            return orderInfo;
        }
        return null;
    }

    public void saveConvertGoodsStoreOrder(ConvertGoodsStoreOrderModel model) {
        String[] goodsIds = new String[]{model.goodsId.toString(), model.targetGoodsId.toString()};

        List list = storeMapper.queryGoodsStoreInfoList(model.tenantId, true, model.branchId, goodsIds);
        Validate.isTrue(list.size() == 2, "商品必须管理库存");

        StoreCompute compute = new StoreCompute();
        compute.tenantId = model.tenantId;
        compute.branchId = model.branchId;
        compute.goodsId = model.goodsId;
        compute.code = this.getNextCode(model.tenantId, model.branchId, model.type == 1 ? 4 : 5);
        compute.quantity = model.quantity;
        compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
        compute.createBy = model.empId;
        compute.occurType = model.type == 1 ? StoreCompute.PACK_OUT : StoreCompute.UNPACK_OUT;

        StoreCompute targetCompute = new StoreCompute();
        targetCompute.tenantId = model.tenantId;
        targetCompute.branchId = model.branchId;
        targetCompute.goodsId = model.targetGoodsId;
        targetCompute.code = compute.code;
        targetCompute.quantity = model.quantity;
        targetCompute.billCreateTime = compute.billCreateTime;
        targetCompute.createBy = model.empId;
        targetCompute.occurType = model.type == 1 ? StoreCompute.PACK_IN : StoreCompute.UNPACK_IN;

        List<StoreCompute> computes = new ArrayList<>();
        computes.add(compute);
        computes.add(targetCompute);
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
    }

    public String getNextCode(BigInteger tId, BigInteger bId, int orderType) {
        String type;
        switch (orderType) {
            case 1:
                type = "JH";
                break;
            case 2:
                type = "TH";
                break;
            case 3:
                type = "PD";
                break;
            case 4:
                type = "DB";
                break;
            case 5:
                type = "CB";
                break;
            case 6:
                type = "PC";
                break;
            case 7:
                type = "PR";
                break;
            case 8:
                type = "LC";
                break;
            case 9:
                type = "LR";
                break;
            case 10:
                type = "SY";
                break;
            case 20:
                type = "ZH";
                break;
            case 21:
                type = "CF";
                break;
            case 22:
                type = "YH";
                break;
            case 23:
                type = "JS";
                break;
            default:
                return "";
        }
        String no = serialNumberCreatorMapper.getBranchToday(type + tId.toString() + bId.toString(), 3, bId);
        return String.format("%s%s%s", type, DateFormatUtils.format(new Date(), "yyMMdd"), no);
    }

    /**
     * 分页查询库存单据
     */
    public Pager queryOrderPager(QueryOrderModel model) {
        long num = storeOrderMapper.queryOrderCount(model);
        model.getPager().setFooter(storeOrderMapper.queryOrderPagerSum(model));
        if (num > 0) {
            model.getPager().setRows(storeOrderMapper.queryOrderPager(model));
        }
        model.getPager().setTotal(num);
        return model.getPager();
    }

    /**
     * 分页查询单品汇总数据
     * @param params
     * */
    public ApiRest queryStoreSingleReport(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            params.put("tenantId",tenantId);
            Integer rows = Integer.parseInt((String) params.get("rows"));
            Integer page = Integer.parseInt((String) params.get("page"));
            Integer offset = (page - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
            List<Map> list = storeOrderMapper.queryStoreSingleReport(params);
            Long count = storeOrderMapper.queryStoreSingleReportCount(params);
            List<Map> footList = storeOrderMapper.queryStoreSingleSumReport(params);
            map.put("rows", list);
            map.put("total", count);
            map.put("footer", footList);
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询单品库存汇总成功！");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setMessage(e.getMessage());
            ServiceException se = new ServiceException("1001", "查询单品库存汇总失败！", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 导入采购单据
     * */
    public ApiRest importCgOrder(Map params){
        ApiRest apiRest = new ApiRest();
        Map queryMap = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            queryMap.put("tenantId",tenantId);
            queryMap.put("branchId",branchId);
            JSONArray goodsJson= JSONArray.fromObject(params.get("goodsList"));
            int goodsNum=Integer.valueOf(params.get("allNum").toString());
            if(goodsNum==0){
                apiRest.setIsSuccess(false);
                apiRest.setError("数据为空，请确认参数！");
                return apiRest;
            }
            List<Map> goodsList=new ArrayList<>();
            int i = 0;
            for(Object goods:goodsJson){
                i++;
                Map goodsMap=(Map)goods;
                String goodsCode = goodsMap.get("goodsCode").toString();
                String goodsName = goodsMap.get("goodsName").toString();
                if(goodsName.indexOf("'") >= 0){
                    goodsName.replaceAll("'", "\'");
                }
                if(goodsName.indexOf("(") >= 0){
                    String standardName = goodsName.substring(goodsName.indexOf("(")+1,goodsName.indexOf(")"));
                    String gName = goodsName.substring(0,goodsName.indexOf("("));
                    queryMap.put("goodsName", " g.goods_name = \""+gName+"\" AND g.standard_name = \""+standardName+"\"");
                }else{
                    queryMap.put("goodsName", " g.goods_name = \""+goodsName+"\" AND (g.standard_name IS NULL OR g.standard_name = '')");
                }
                String unitName = goodsMap.get("goodsUnitName").toString();
                queryMap.put("goodsCode", goodsCode);
                List<Map> good = goodsMapper.queryGoodsInfo(queryMap);
                if(good == null || good.size() == 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("第"+i+"行商品不存在");
                    return  apiRest;
                }
                Map gMap = good.get(0);
                goodsMap.put("id", gMap.get("id"));
                goodsMap.put("barCode", gMap.get("barCode"));
                goodsMap.put("categoryName", gMap.get("catName"));
                goodsMap.put("goodsUnitName", gMap.get("unitName"));
                goodsMap.put("packingUnitName", gMap.get("packUnitName"));
                goodsMap.put("unitRelation", gMap.get("unitRelation"));
                if(unitName.equals(gMap.get("unitName"))){
                    goodsMap.put("unitType", 1);
                }else if(unitName.equals(gMap.get("packUnitName"))){
                    goodsMap.put("unitType", 2);
                }else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("第"+i+"行商品单位错误");
                    return  apiRest;
                }
                goodsList.add(goodsMap);
            }
            Map resultMap = new HashMap();
            resultMap.put("rows", goodsList);
            apiRest.setIsSuccess(true);
            apiRest.setData(resultMap);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "导入采购进货单失败！", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 导入盘点单
     * */
    public ApiRest importPdOrder(Map params){
        ApiRest apiRest = new ApiRest();
        Map queryMap = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            queryMap.put("tenantId", tenantId);
            queryMap.put("branchId", branchId);
            JSONArray goodsJson= JSONArray.fromObject(params.get("goodsList"));
            int goodsNum=Integer.valueOf(params.get("allNum").toString());
            if(goodsNum==0){
                apiRest.setIsSuccess(false);
                apiRest.setError("数据为空，请确认参数！");
                return apiRest;
            }
            List<Map> goodsList=new ArrayList<>();
            int i = 0;
            for(Object goods:goodsJson){
                i++;
                Map goodsMap = (Map) goods;
                String goodsCode = goodsMap.get("goodsCode").toString();
                String goodsName = goodsMap.get("goodsName").toString();
                if(goodsName.indexOf("(") >= 0){
                    String standardName = goodsName.substring(goodsName.indexOf("(")+1,goodsName.indexOf(")"));
                    String gName = goodsName.substring(0,goodsName.indexOf("("));
                    queryMap.put("goodsName", " g.goods_name = \""+gName+"\" AND g.standard_name = \""+standardName+"\"");
                }else{
                    queryMap.put("goodsName", " g.goods_name = \""+goodsName+"\" AND (g.standard_name IS NULL OR g.standard_name = '')");
                }
                String unitName = goodsMap.get("goodsUnitName").toString();
                queryMap.put("goodsCode", goodsCode);
                queryMap.put("onlySelf", params.get("onlySelf"));
                List<Map> good = goodsMapper.queryGoodsStoreInfo(queryMap);
                if(good == null || good.size() == 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("第"+i+"行商品不存在");
                    return  apiRest;
                }
                Map gMap = good.get(0);
                goodsMap.put("goodsId", gMap.get("id"));
                goodsMap.put("categoryName", gMap.get("catName"));
                goodsMap.put("unitName", gMap.get("unitName"));
                goodsMap.put("packingUnitName", gMap.get("packUnitName"));
                goodsMap.put("unitRelation", gMap.get("unitRelation"));
                if(unitName.equals(gMap.get("unitName"))){
                    goodsMap.put("unitType", 1);
                }else if(unitName.equals(gMap.get("packUnitName"))){
                    goodsMap.put("unitType", 2);
                }else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("第"+i+"行商品单位错误");
                    return  apiRest;
                }
                goodsMap.put("quantity", gMap.get("quantity"));
                goodsMap.put("avgAmount", gMap.get("avgAmount"));
                goodsList.add(goodsMap);
            }
            Map resultMap = new HashMap();
            resultMap.put("rows", goodsList);
            apiRest.setIsSuccess(true);
            apiRest.setData(resultMap);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "导入盘点单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 查询供应商结算单（采购进货单、采购退货单）
     * */
    public ApiRest querySupplierSettlements(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            if(params.get("rows") != null && !"".equals(params.get("rows")) && params.get("page") != null && !"".equals(params.get("page"))){
                Integer offset = (Integer.parseInt((String) params.get("page")) - 1) * Integer.parseInt((String) params.get("rows"));
                params.put("offset", offset);
            }
            params.put("tenantId", tenantId);
            params.put("branchId", branchId);
            List<Map> list = storeOrderMapper.querySupplierSettlements(params);
            Long count = storeOrderMapper.querySupplierSettlementsSum(params);
            Map map = new HashMap();
            map.put("rows", list);
            map.put("total", count);
            apiRest.setData(map);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询供应商结算单成功");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "查询供应商结算单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 新增或修改结算单
     * */
    public ApiRest addOrUpdateJsOrder(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            BigInteger supplierId = BigInteger.valueOf(Long.parseLong(params.get("supplierId").toString()));
            BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
            String jsCode = params.get("jsCode").toString();
            BigDecimal settlementAmount = BigDecimal.ZERO;
            if(params.get("settlementAmount") != null && !"".equals(params.get("settlementAmount"))){
                settlementAmount = BigDecimal.valueOf(Double.parseDouble(params.get("settlementAmount").toString()));
            }
            String memo = "";
            if(params.get("memo") != null && !"".equals(params.get("memo"))){
                memo = params.get("memo").toString();
            }
            String cgIds = params.get("cgIds").toString();
            if(params.get("id") != null && !"".equals(params.get("id"))){
                BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
                JsOrder jsOrder = storeOrderMapper.queryJsOrderById(id);
                if(jsOrder == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未查询到该结算单据");
                    return apiRest;
                }
                jsOrder.setSupplierId(supplierId);
                jsOrder.setBranchId(branchId);
                jsOrder.setMemo(memo);
                jsOrder.setSettlementAmount(settlementAmount);
                jsOrder.setLastUpdateBy(userId);
                jsOrder.setLastUpdateAt(new Date());
                params.put("tenantId", tenantId);
                params.put("branchId", branchId);
                params.put("jsCode", jsCode);
                List cgList = storeOrderMapper.queryCgByJsOrder(params);
                if(cgList != null && cgList.size() > 0){
                    String ids = "";
                    for(int i=0; i<cgList.size(); i++){
                        Map map = (Map)cgList.get(i);
                        String cgId = map.get("id").toString();
                        ids += cgId +",";
                    }
                    ids = ids.substring(0, ids.lastIndexOf(","));
                    int m = storeOrderMapper.clearCgToJs(ids);
                    if(m <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("修改结算单对应采购单失败");
                        return apiRest;
                    }
                }
                params.put("ids", cgIds);
                int n = storeOrderMapper.updateCgToJs(params);
                if(n <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("更新结算单对应采购单失败");
                    return apiRest;
                }
                int i = storeOrderMapper.updateJsOrder(jsOrder);
                if(i <= 0) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改结算单失败");
                    return apiRest;
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改结算单成功");
            }else{
                JsOrder jsOrder = new JsOrder();
                jsOrder.setJsCode(jsCode);
                jsOrder.setTenantId(tenantId);
                jsOrder.setBranchId(branchId);
                jsOrder.setSupplierId(supplierId);
                jsOrder.setSettlementAmount(settlementAmount);
                jsOrder.setMemo(memo);
                jsOrder.setAuditStatus(0);
                jsOrder.setCreateAt(new Date());
                jsOrder.setCreateBy(userId);
                jsOrder.setIsDeleted(false);
                int i = storeOrderMapper.addJsOrder(jsOrder);
                Map map = new HashMap();
                map.put("ids", cgIds);
                map.put("jsCode", jsCode);
                int m = storeOrderMapper.updateCgToJs(map);
                if(i <= 0 || m <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增供应商结算单失败");
                    return apiRest;
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增供应商结算单成功");
            }
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "新增或修改结算单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 删除结算单
     * */
    public ApiRest deleteJsOrder(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
            BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
            params.put("id", id);
            params.put("userId", userId);
            JsOrder jsOrder = storeOrderMapper.queryJsOrderById(id);
            if(jsOrder == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到该单据");
                return apiRest;
            }
            int i = storeOrderMapper.deleteJsOrder(params);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("删除结算单失败");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除结算单成功");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "删除结算单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 审核结算单
     * */
    public ApiRest auditJsOrder(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
            BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
            params.put("id", id);
            params.put("auditBy", userId);
            JsOrder jsOrder = storeOrderMapper.queryJsOrderById(id);
            if(jsOrder == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到该单据");
                return apiRest;
            }
            int i = storeOrderMapper.auditJsOrder(params);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("审核单据失败");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("审核单据成功");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "审核结算单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return  apiRest;
    }

    /**
     * 查询采购单据
     * */
    public ApiRest queryCgOrderToJs(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            BigInteger supplierId = BigInteger.valueOf(Long.parseLong(params.get("supplierId").toString()));
            params.put("tenantId", tenantId);
            params.put("branchId", branchId);
            params.put("supplierId", supplierId);
            List<Map> list = storeOrderMapper.queryCgOrderToJs(params);
            map.put("rows", list);
            map.put("total",list.size());
            apiRest.setData(map);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询采购单据成功");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "查询采购单据失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     * 生成结算单单据号
     * */
    /*public ApiRest getJsNextCode(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            String code = vipService.getNextSelValue(SysConfig.SYS_PROMOTION_NUM, 7, new BigInteger(params.get("tenantId").toString()));
            String today = Common.getToday().substring(2);
            code = "JS" + today + code;
            addLimit(SysConfig.SYS_PROMOTION_NUM, new BigInteger(params.get("tenantId").toString()));
            apiRest.setData(code);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("生成单据号成功");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "生成结算单单据号失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }*/

    /**
     * 根据code查询结算单
     * */
    public ApiRest queryJsOrderByCode(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            params.put("tenantId", tenantId);
            params.put("branchId", branchId);
            List jsOrder = storeOrderMapper.queryJsOrderByCode(params);
            if(jsOrder.size() == 1){
                Map orderInfo = (Map) jsOrder.get(0);
                List cgOrder = storeOrderMapper.queryCgOrderByJsCode(params);
                List<Map> footer =storeOrderMapper.queryCgOrderDetailByJsCode(params);
                orderInfo.put("details", cgOrder);
                orderInfo.put("footer",footer);
                apiRest.setIsSuccess(true);
                apiRest.setData(orderInfo);
                apiRest.setMessage("查询成功");
                return apiRest;
            }
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询失败");
            apiRest.setData(null);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            ServiceException se = new ServiceException("1001", "查询结算单失败", e.getMessage());
            LogUtil.logError(e, params);
            throw se;
        }
        return apiRest;
    }

    /**
     *
     * @param type
     * @return
     */
    protected ResultJSON addLimit(String type, BigInteger tenantId){
        ResultJSON resultJSON = new ResultJSON();
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type,tenantId);
        if(tenantConfig != null){
            tenantConfig.setValue(String.valueOf(Integer.parseInt(tenantConfig.getValue()) + 1));
            tenantConfigMapper.update(tenantConfig);
            resultJSON.setSuccess("true");
            resultJSON.setIsSuccess(true);
            resultJSON.setMsg("条数更新成功");
        }else{
            resultJSON.setSuccess("false");
            resultJSON.setIsSuccess(false);
            resultJSON.setMsg("条数更新失败,不存在对应计数器");
        }
        return resultJSON;
    }

    /**
     * 线上订单库存处理
     * */
    public void onlineSaleStore(Map params){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<String> saleList = new ArrayList();
            DietOrderInfo dietOrderInfo = dietOrderInfoMapper.findByCondition(params);
            List<DietOrderDetail> details = dietOrderDetailMapper.findDetailByCondition(params);
            for(DietOrderDetail detail : details){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("tenantId", dietOrderInfo.getTenantId());
                map.put("branchId", dietOrderInfo.getBranchId());
                map.put("goodsId", detail.getGoodsId());
                map.put("price", detail.getSalePrice());
                map.put("quantity", detail.getQuantity());
                map.put("createBy", dietOrderInfo.getCreateBy());
                map.put("code", dietOrderInfo.getOrderCode());
                if(dietOrderInfo.getIsRefund()){
                    map.put("occurType", 7);
                }
                else{
                    map.put("occurType", 6);
                }
                if(dietOrderInfo.getPayAt()!= null){
                    map.put("billCreateTime", dateFormat.format(dietOrderInfo.getPayAt()));
                }
                else{
                    map.put("billCreateTime", dateFormat.format(new Date()));
                }
                String saleJson = JSONObject.fromObject(map).toString();
                saleList.add(saleJson);
            }
            String topic = PropertyUtils.getDefault("redis_topics");
            if(StringUtils.isNotEmpty(topic) && !saleList.isEmpty()){
                Long l = PartitionCacheUtils.lpush(topic, (String[])saleList.toArray(new String[saleList.size()]));
                if(l == null){
                    LogUtil.logInfo("库存计算发送失败");
                }else{
                    LogUtil.logInfo("库存计算发送成功");
                }
            }
        }catch (Exception e){
            LogUtil.logInfo("库存计算发送失败");
        }

    }
}
