package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.supply.store.*;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.mapper.supply.store.StoreOrderMapper;
import erp.chain.model.supply.store.ConvertGoodsStoreOrderModel;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.service.supply.store.impl.StoreOrderServiceImpl;
import erp.chain.utils.Args;
import erp.chain.utils.DateUtils;
import erp.chain.utils.NonUniqueResultException;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 库存单据零售pos接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreWSService{
    @Autowired
    private StoreOrderServiceImpl storeOrderService;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private StoreOrderMapper storeOrderMapper;
    @Autowired
    private StoreComputeService storeComputeService;
    @Autowired
    private GoodsMapper goodsMapper;

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * 储存单据信息
     *
     * @param params
     * @return
     */
    public ApiRest saveStoreOrder(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        String detailStr = StringUtils.trimToNull(params.get("detailStr").toString());
        JSONArray jsonArray = JSONArray.fromObject(detailStr);
        BigDecimal orderQu = BigDecimal.ZERO;
        BigDecimal orderAm = BigDecimal.ZERO;
            /*detailList.each {
                it.amount = it.purchaseAmount * it.quantity
                orderQu += it.quantity
                orderAm += it.amount
            }*/
        List<CgOrderDetail> detailList = new ArrayList<>();
        String[] goodsIds = new String[jsonArray.size()];
        int x = 0;
        for(Object object : jsonArray){
            CgOrderDetail cgOrderDetail = new CgOrderDetail();
            Map detailMap = (Map)object;
            cgOrderDetail.setPurchaseAmount(BigDecimal.valueOf(Double.valueOf(detailMap.get("purchaseAmount").toString())));
            cgOrderDetail.setQuantity(BigDecimal.valueOf(Double.valueOf(detailMap.get("quantity").toString())));
            cgOrderDetail.setGoodsId(BigInteger.valueOf(Long.valueOf(detailMap.get("goodsId").toString())));
            cgOrderDetail.setAmount(cgOrderDetail.getPurchaseAmount().multiply(cgOrderDetail.getQuantity()));
            cgOrderDetail.setTenantId(tenantId);
            cgOrderDetail.setBranchId(branchId);
            cgOrderDetail.setLastUpdateAt(new Date());
            cgOrderDetail.setLastUpdateBy(params.get("makeBy").toString());
            cgOrderDetail.setCreateAt(new Date());
            cgOrderDetail.setCreateBy(params.get("makeBy").toString());
            orderQu = orderQu.add(cgOrderDetail.getQuantity());
            orderAm = orderAm.add(cgOrderDetail.getAmount());
            goodsIds[x++] = cgOrderDetail.getGoodsId().toString();
            detailList.add(cgOrderDetail);
        }
        CgOrder order = new CgOrder();
        order.setTenantId(tenantId);
        order.setBranchId(branchId);
        order.setMakeAt(new Date());
        order.setMakeBy(BigInteger.valueOf(Long.valueOf(params.get("makeBy").toString())));
        order.setCreateAt(new Date());
        order.setCreateBy(params.get("makeBy").toString());
        order.setLastUpdateAt(new Date());
        order.setLastUpdateBy(params.get("makeBy").toString());
        order.setOrderType(Integer.valueOf(params.get("orderType").toString()));
        Args.isIn(order.getOrderType().toString(), new String[]{"1", "2"}, "orderType 必须是1和2其中一个");
        order.setCode(storeOrderService.getNextCode(tenantId, branchId, order.getOrderType()));
        order.setQuantity(orderQu);
        order.setAmount(orderAm);
        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if(list.size() != detailList.size()){
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        try{
            storeOrderMapper.saveCgOrderWS(order);
            audit(1, order.code, order.makeBy.toString(), params.get("makeName").toString(), order.tenantId, order.branchId);
        }
        catch(DuplicateKeyException e){
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        for(CgOrderDetail detail : detailList){
            detail.storeOrderId = order.id;
        }
        storeOrderMapper.saveCgOrderDetail(detailList);


        List<StoreCompute> computes = new ArrayList<>();

        for(CgOrderDetail cgOrderDetail : detailList){
            StoreCompute compute = new StoreCompute();
            compute.tenantId = cgOrderDetail.tenantId;
            compute.branchId = cgOrderDetail.branchId;
            compute.goodsId = cgOrderDetail.goodsId;
            compute.code = order.code;
            compute.occurType = order.orderType == 1 ? StoreCompute.PURCHASE_ADD : StoreCompute.PURCHASE_RETURN;
            compute.price = cgOrderDetail.purchaseAmount;
            compute.quantity = cgOrderDetail.quantity;
            compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
            compute.createBy = order.makeBy;
            computes.add(compute);

        }
        //计算库存
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存成功！");
        apiRest.setData(order);
        return apiRest;
    }

    /**
     * 查询单据
     *
     * @param params
     * @return
     */
    public ApiRest queryOrderPages(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> list = storeMapper.queryOrderPages(params);
        long count = storeMapper.queryOrderPagesCount(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 保存盘点单
     *
     * @param params
     * @return
     */
    public ApiRest saveCheckOrder(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        String detailStr = StringUtils.trimToNull(params.get("detailStr").toString());
        JSONArray jsonArray = JSONArray.fromObject(detailStr);
        String makeName = params.get("makeName").toString();
        BigInteger makeBy = BigInteger.valueOf(Long.valueOf(params.get("makeBy").toString()));
        //"detailStr" -> "[{"reallyQuantity":555.0,"goodsId":1014}]"

        PdOrder order = new PdOrder();
        order.tenantId = tenantId;
        order.branchId = branchId;
        order.makeBy = makeBy;
        order.makeAt = new Date();
        order.setMakeName(makeName);
        order.setCreateBy(makeBy.toString());
        order.setLastUpdateBy(makeBy.toString());
        order.setCreateAt(new Date());
        order.setLastUpdateAt(new Date());
        order.code = storeOrderService.getNextCode(tenantId, branchId, 3);

        List<PdOrderDetail> details = new ArrayList<>();
        String[] goodsIds = new String[jsonArray.size()];
        int x = 0;
        for(Object detail : jsonArray){
            Map detailMap = (Map)detail;
            PdOrderDetail orderDetail = new PdOrderDetail();
            orderDetail.tenantId = order.tenantId;
            orderDetail.branchId = order.branchId;
            orderDetail.goodsId = BigInteger.valueOf(Long.valueOf(detailMap.get("goodsId").toString()));
            orderDetail.quantity = BigDecimal.valueOf(Double.valueOf(detailMap.get("reallyQuantity").toString()));
            orderDetail.createAt = order.makeAt;
            orderDetail.createBy = makeBy.toString();
            orderDetail.lastUpdateAt = new Date();
            orderDetail.lastUpdateBy = makeBy.toString();
            orderDetail.reallyQuantity = BigDecimal.valueOf(Double.valueOf(detailMap.get("reallyQuantity").toString()));
            details.add(orderDetail);
            goodsIds[x++] = detailMap.get("goodsId").toString();
        }

        //是否存在不管理库存商品
        List list = storeMapper.queryGoodsStoreInfoList(order.tenantId, true, order.branchId, goodsIds);
        if(list.size() != details.size()){
            throw new NonUniqueResultException("存在商品不管理库存或不存在");
        }

        BigDecimal totalCheckQuantity = BigDecimal.ZERO;
        BigDecimal totalCheckAmount = BigDecimal.ZERO;
        BigDecimal totalReallyQuantity = BigDecimal.ZERO;
        for(PdOrderDetail detail : details){
            GoodsStoreInfo info = this.findStoreInfo(list, detail.goodsId);
            //库存信息
            detail.purchasePrice = info.getStoreId() != null ? info.getAvgAmount() : info.getPurchasingPrice();
            detail.quantity = info.getStoreId() != null ? info.getQuantity() : BigDecimal.ZERO;
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

        try{
            storeOrderMapper.savePdOrderWS(order);
            audit(2, order.code, order.makeBy.toString(), params.get("makeName").toString(), order.tenantId, order.branchId);
        }
        catch(DuplicateKeyException e){
            throw new NonUniqueResultException(String.format("单据号[%s]已存在", order.code));
        }

        for(PdOrderDetail detail : details){
            detail.orderId = order.id;
        }
        storeOrderMapper.savePdOrderDetail(details);
        List<StoreCompute> computes = new ArrayList<>();

        for(PdOrderDetail pdOrderDetail : details){
            StoreCompute compute = new StoreCompute();
            compute.tenantId = pdOrderDetail.tenantId;
            compute.branchId = pdOrderDetail.branchId;
            compute.goodsId = pdOrderDetail.goodsId;
            compute.code = order.code;
            compute.occurType = StoreCompute.INVENTORY;
            compute.price = pdOrderDetail.purchasePrice;
            compute.quantity = pdOrderDetail.reallyQuantity;
            compute.checkQuantity = pdOrderDetail.checkQuantity;
            compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
            compute.createBy = order.makeBy;
            computes.add(compute);
        }
        //计算库存
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存成功！");
        apiRest.setData(order);
        return apiRest;
    }

    /**
     * 查询单据
     *
     * @param params
     * @return
     */
    public ApiRest queryCheckOrderPages(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        List<Map> list = storeMapper.queryCheckOrderPages(params);
        long count = storeMapper.queryCheckOrderPagesCount(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 库存查询
     *
     * @param params
     * @return
     */
    public ApiRest queryStorePagerWS(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        List<Map> list = storeMapper.queryStorePagerWS(params);
        List<Map> countList = storeMapper.queryStorePagerWSCount(params);
        String count = "0";
        if(countList.size() > 0){
            count = countList.get(0).get("total").toString();
        }
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", count);
        resultMap.put("footer", countList.get(0));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 库存流水
     *
     * @param params
     * @return
     */
    public ApiRest queryStoreAccountPager(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        List<Map> list = storeMapper.queryStoreAccountPagerWS(params);
        List<Map> countList = storeMapper.queryStoreAccountPagerWSCount(params);
        String count = "0";
        if(countList.size() > 0){
            count = countList.get(0).get("total").toString();
        }
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", count);
        resultMap.put("footer", countList.get(0));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public void saveConvertGoodsStoreOrder(Map params, Integer type){
        ConvertGoodsStoreOrderModel model = new ConvertGoodsStoreOrderModel();
        model.type = type;
        model.tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        model.branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        model.empId = BigInteger.valueOf(Long.valueOf(params.get("empId").toString()));
        model.goodsId = BigInteger.valueOf(Long.valueOf(params.get("goodsId1").toString()));
        model.targetGoodsId = BigInteger.valueOf(Long.valueOf(params.get("goodsId2").toString()));
        model.quantity = BigDecimal.valueOf(Double.valueOf(params.get("num1").toString()));
        model.targetQuantity = BigDecimal.valueOf(Double.valueOf(params.get("num2").toString()));

        String[] goodsIds = new String[]{model.goodsId.toString(), model.targetGoodsId.toString()};

        List list = storeMapper.queryGoodsStoreInfoList(model.tenantId, true, model.branchId, goodsIds);
        Validate.isTrue(list.size() == 2, "商品必须管理库存");

        StoreCompute compute = new StoreCompute();
        compute.tenantId = model.tenantId;
        compute.branchId = model.branchId;
        compute.goodsId = model.goodsId;
        compute.code = storeOrderService.getNextCode(model.tenantId, model.branchId, model.type == 1 ? 4 : 5);
        compute.quantity = model.quantity;
        compute.billCreateTime = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date());
        compute.createBy = model.empId;
        compute.occurType = model.type == 1 ? StoreCompute.PACK_OUT : StoreCompute.UNPACK_OUT;

        StoreCompute targetCompute = new StoreCompute();
        targetCompute.tenantId = model.tenantId;
        targetCompute.branchId = model.branchId;
        targetCompute.goodsId = model.targetGoodsId;
        targetCompute.code = compute.code;
        targetCompute.quantity = model.targetQuantity;
        targetCompute.billCreateTime = compute.billCreateTime;
        targetCompute.createBy = model.empId;
        targetCompute.occurType = model.type == 1 ? StoreCompute.PACK_IN : StoreCompute.UNPACK_IN;

        List<StoreCompute> computes = new ArrayList<>();
        computes.add(compute);
        computes.add(targetCompute);
        StoreCompute.executeStoreCompute(computes, storeComputeService, logger);
    }

    /**
     * 查找商品库存
     *
     * @return null--商品不存在，否则返回
     */
    private GoodsStoreInfo findStoreInfo(List<GoodsStoreInfo> infoList, BigInteger goodsId){
        for(GoodsStoreInfo info : infoList){
            if(info.getGoodsId().compareTo(goodsId) == 0){
                return info;
            }
        }
        return null;
    }

    private void audit(Integer type, String code, String auditBy, String auditName, BigInteger tenantId, BigInteger branchId){
        Map param = new HashMap();
        param.put("type", type);
        param.put("code", code);
        param.put("auditBy", auditBy);
        param.put("auditName", auditName);
        param.put("tenantId", tenantId);
        param.put("branchId", branchId);
        storeOrderMapper.updateOrderWS(param);
    }
}
