package erp.chain.service.base;

import erp.chain.domain.Goods;
import erp.chain.domain.GoodsProduceOrder;
import erp.chain.domain.GoodsProduceOrderDetail;
import erp.chain.domain.supply.store.StoreCompute;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsProduceOrderDetailMapper;
import erp.chain.mapper.GoodsProduceOrderMapper;
import erp.chain.model.Pager;
import erp.chain.model.base.ProduceOrderDetailModel;
import erp.chain.model.base.ProduceOrderModel;
import erp.chain.model.base.ProduceOrderUpdateModel;
import erp.chain.model.base.QueryGoodsProduceOrderModel;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.DateUtils;
import erp.chain.utils.NonUniqueResultException;
import erp.chain.utils.ResourceNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 商品加工单表
 *
 * @author hefuzi 2016-11-29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsProduceOrderService {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private GoodsProduceOrderMapper orderMapper;
    @Autowired
    private GoodsProduceOrderDetailMapper detailMapper;
    @Autowired
    private StoreComputeService storeComputeService;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 分页查询-商品加工单表
     */
    public Pager queryPager(QueryGoodsProduceOrderModel model) {
        long count = orderMapper.queryCount(model);
        model.getPager().setTotal(count);
        if (count > 0) {
            List<Map<String, Object>> rows = orderMapper.queryPager(model);
            model.getPager().setRows(rows);
        }
        return model.getPager();
    }

    /**
     * id查询-商品加工单表
     */
    public GoodsProduceOrder get(BigInteger id) {
        return orderMapper.get(id);
    }

    /**
     * id查询-商品加工单信息
     */
    public Map<String, Object> getInfo(BigInteger id) {
        QueryGoodsProduceOrderModel model = new QueryGoodsProduceOrderModel();
        model.orderId = id;
        List<Map<String, Object>> rows = orderMapper.queryPager(model);
        if (rows.size() == 0) {
            return null;
        }
        Map<String, Object> order = rows.get(0);
        List<Map<String, Object>> details = detailMapper.queryList((String) order.get("code"),order.get("branchId").toString());
        order.put("details", details);
        return order;
    }

    /**
     * id删除-商品加工单
     *
     * @throws OptimisticLockingFailureException [%s]数据版本过期或不存在
     * @throws ResourceNotExistException         数据[%s]不存在
     */
    public void delete(BigInteger id, BigInteger empId, BigInteger version) {
        GoodsProduceOrder oldGoodsProduceOrder = orderMapper.get(id);
        if (oldGoodsProduceOrder == null || oldGoodsProduceOrder.getStatus() != 1) {
            throw new ResourceNotExistException(String.format("加工单[%s]不存在或已审核", id));
        }
        int num = orderMapper.delete(id, empId, version);
        if (num != 1) {
            throw new OptimisticLockingFailureException(String.format("[%s]数据版本过期", version));
        }
        detailMapper.delete(oldGoodsProduceOrder.getCode(), empId);
    }

    /**
     * 审核单据
     *
     * @throws OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException         加工单[%s]不存在或已审核
     */
    public void audit(BigInteger orderId, BigInteger empId, BigInteger version) {
        GoodsProduceOrder order = orderMapper.get(orderId);
        if (order == null || order.getStatus() != 1) {
            throw new ResourceNotExistException(String.format("加工单[%s]不存在或已审核", orderId));
        }
        int num = orderMapper.audit(orderId, empId, version);
        if (num != 1) {
            throw new OptimisticLockingFailureException(String.format("[%s]数据版本过期", version));
        }

        List<Map<String, Object>> details = detailMapper.queryList(order.getCode(),order.getBranchId().toString());

        List<StoreCompute> computes = new ArrayList<>();
        StoreCompute orderCompute = new StoreCompute();
        orderCompute.tenantId = order.tenantId;
        orderCompute.branchId = order.getBranchId();
        orderCompute.goodsId = order.getGoodsId();
        orderCompute.code = order.getCode();
        orderCompute.occurType = order.getType() == 2 ? StoreCompute.CF_OUT : StoreCompute.ZH_IN;
        orderCompute.quantity = order.getQuantity();
        orderCompute.price = order.getPrice();
        orderCompute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
        orderCompute.createBy = empId;
        orderCompute.isUpdatePrice = order.isUpdatePrice == null ? 1 : order.isUpdatePrice;
        computes.add(orderCompute);

        for (Map<String, Object> detail : details) {
            StoreCompute compute = new StoreCompute();
            compute.tenantId = order.tenantId;
            compute.branchId = order.getBranchId();
            compute.goodsId = new BigInteger(detail.get("goodsId").toString());
            compute.code = order.getCode();
            compute.occurType = order.getType() == 1 ? StoreCompute.ZH_OUT : StoreCompute.CF_IN;
            compute.quantity = (BigDecimal) detail.get("quantity");
            compute.price = (BigDecimal) detail.get("price");
            compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
            compute.createBy = empId;
            Map params = new HashMap();
            params.put("tenantId", order.tenantId);
            params.put("id", order.getGoodsId());
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
            if(order.getBranchId().compareTo(goods.getBranchId()) == 0){
                compute.isUpdatePrice = order.isUpdatePrice == null ? 1 : order.isUpdatePrice;
            }else{
                compute.isUpdatePrice = 0;
            }
            computes.add(compute);
        }
        //计算库存
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
    }

    /**
     * id更新-商品加工单表
     *
     * @throws OptimisticLockingFailureException 数据版本过期
     * @throws ResourceNotExistException         加工单[%s]不存在,加工商品[%s]不存在
     */
    public void update(ProduceOrderUpdateModel model) {
        GoodsProduceOrder oldGoodsProduceOrder = orderMapper.get(model.orderId);
        if (oldGoodsProduceOrder == null || oldGoodsProduceOrder.getStatus() != 1) {
            throw new ResourceNotExistException(String.format("加工单[%s]不存在或已审核", model.orderId));
        }
        GoodsProduceOrder order = new GoodsProduceOrder();
        List<GoodsProduceOrderDetail> details = new ArrayList<>();
        order.setVersion(model.version.longValue());
        order.setId(model.orderId);
        order.setCode(oldGoodsProduceOrder.getCode());
        order.setTenantId(model.tenantId);
        order.setBranchId(model.branchId);
        order.setGoodsId(model.goodsId);
        order.setPrice(model.price);
        order.setQuantity(model.quantity);
        order.setLastUpdateBy(model.empId.toString());
        order.setLastUpdateAt(new Date());
        order.setMemo(model.memo);
        order.setIsUpdatePrice(model.isUpdatePrice == null ? 1 : model.isUpdatePrice);
        initOrderDetails(order, details, model.details);
        int num = orderMapper.update(order);
        if (num != 1) {
            throw new OptimisticLockingFailureException("数据版本过期");
        }

        detailMapper.delete(oldGoodsProduceOrder.getCode(), model.empId);
        detailMapper.saveList(details);
    }

    /**
     * 保存-商品加工单
     *
     * @throws ResourceNotExistException 加工商品[%s]不存在
     * @throws NonUniqueResultException  加工商品[%s]不存在商品id=%s的关系,单据号[%s]已存在
     */
    public BigInteger save(ProduceOrderModel model) {
        GoodsProduceOrder order = new GoodsProduceOrder();
        List<GoodsProduceOrderDetail> details = new ArrayList<>();

        order.setTenantId(model.tenantId);
        order.setBranchId(model.branchId);
        order.setGoodsId(model.goodsId);
        order.setCode(model.code);
        order.setType(model.type);
        order.setMakeEmpId(model.empId);
        order.setPrice(model.price);
        order.setQuantity(model.quantity);
        order.setMemo(model.memo);
        order.setMakeAt(new Date());
        order.setCreateAt(order.getMakeAt());
        order.setCreateBy(order.getMakeEmpId().toString());
        order.setLastUpdateBy(order.getCreateBy());
        order.setLastUpdateAt(order.getMakeAt());
        order.setStatus(1);
        order.setIsUpdatePrice(model.isUpdatePrice == null ? 1 : model.isUpdatePrice);
        initOrderDetails(order, details, model.details);
        try {
            orderMapper.save(order);
            detailMapper.saveList(details);
        } catch (DuplicateKeyException e) {
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.getCode()));
        }
        return order.getId();
    }

    private void initOrderDetails(GoodsProduceOrder order, List<GoodsProduceOrderDetail> details, List<ProduceOrderDetailModel> modelDetails) {
        List<Map<String, Long>> produceGoodsList = orderMapper.queryGoodsProduceRelation(order.getGoodsId(), order.tenantId);
        if (produceGoodsList == null || produceGoodsList.size() == 0) {
            throw new ResourceNotExistException(String.format("加工商品[%s]不存在", order.getGoodsId()));
        }
        for (ProduceOrderDetailModel detail : modelDetails) {
            boolean find = false;
            for (Map<String, Long> aProduceGoodsList : produceGoodsList) {
                if (aProduceGoodsList.get("goodsId").toString().equals(detail.goodsId.toString())
                        && !aProduceGoodsList.get("goodsId").toString().equals(order.getGoodsId().toString())) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                throw new NonUniqueResultException(String.format("加工商品[%s]不存在商品id=%s的关系", order.getGoodsId(), detail.goodsId));
            }

            GoodsProduceOrderDetail orderDetail = new GoodsProduceOrderDetail();
            orderDetail.setTenantId(order.tenantId);
            orderDetail.setBranchId(order.getBranchId());
            orderDetail.setGoodsId(detail.goodsId);
            orderDetail.setOrderCode(order.getCode());
            orderDetail.setPrice(detail.price);
            orderDetail.setQuantity(detail.quantity);
            orderDetail.setUnitType(detail.unitType);
            orderDetail.setUnitRelation(detail.unitRelation);
            orderDetail.setCreateAt(order.getMakeAt());
            orderDetail.setLastUpdateAt(order.getMakeAt());
            details.add(orderDetail);
        }
    }
}