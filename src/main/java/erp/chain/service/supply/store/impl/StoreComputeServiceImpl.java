package erp.chain.service.supply.store.impl;


import com.saas.common.util.LogUtil;
import erp.chain.domain.Goods;
import erp.chain.domain.supply.GoodsInvoicing;
import erp.chain.domain.supply.store.GoodsStoreInfo;
import erp.chain.domain.supply.store.Store;
import erp.chain.domain.supply.store.StoreAccount;
import erp.chain.domain.supply.store.StoreCompute;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsProduceRelationMapper;
import erp.chain.mapper.supply.GoodsInvoicingMapper;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 计算库存 ServiceImpl
 */
@Service("storeComputeService")
@Transactional(rollbackFor = Exception.class)
public class StoreComputeServiceImpl implements StoreComputeService {
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsProduceRelationMapper goodsProduceRelationMapper;
    @Autowired
    private GoodsInvoicingMapper goodsInvoicingMapper;

    @Override
    public void executeStoreCompute(StoreCompute compute) {
        List<StoreCompute> computes = new ArrayList<>();
        computes.add(compute);
        this.executeStoreCompute(computes);
    }

    @Override
    public void executeStoreCompute(List<StoreCompute> computes) {
        String[] goodsIds = new String[computes.size()];
        for (int x = 0; x < goodsIds.length; x++) {
            goodsIds[x] = computes.get(x).getGoodsId().toString();
        }
        List<GoodsStoreInfo> infoList = storeMapper.queryGoodsStoreInfoList(computes.get(0).getTenantId(),false, computes.get(0).getBranchId(), goodsIds);

        for (StoreCompute compute : computes) {
            GoodsStoreInfo info = findStoreInfo(infoList, compute.getGoodsId());
            List<GoodsStoreInfo> storeInfoList = new ArrayList<>();

            //商品未找到或不需要管理库存(配方商品除外)，则不处理库存计算
            if(info==null||info.getCombinationType()==null||(info.getCombinationType() != 3 && !info.getStore())){
                continue;
            }
            //配方商品明细
            if (info.getCombinationType() == 3 && info.getRelationGoodsIds() != null) {
                String[] detailIds = info.getRelationGoodsIds().split(",");
                List<GoodsStoreInfo> detailInfoList = storeMapper.queryGoodsStoreInfoList(compute.getTenantId(),false,
                        compute.getBranchId(), detailIds);
                for (GoodsStoreInfo detailInfo : detailInfoList) {
                    if (detailInfo.getStore()) {//明细管理库存
                        storeInfoList.add(detailInfo);
                    }
                }
            }
            if (info.getStore()) {
                storeInfoList.add(info);
            }
            for (GoodsStoreInfo storeInfo : storeInfoList) {
                switch (compute.getOccurType()) {
                    case StoreCompute.RETAIL_SALE:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.RETAIL_RETURN:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.ONLINE_RETURN:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.ONLINE_SALE:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.PACK_IN:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.PACK_OUT:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.UNPACK_IN:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.UNPACK_OUT:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.PS_OUT:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.LY_OUT:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.LY_IN:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.SY_IN_OUT:
                        executeInOrOut(compute, storeInfo);
                        break;
                    case StoreCompute.INVENTORY:
                        executeInventory(compute, storeInfo);
                        break;
                    case StoreCompute.PURCHASE_RETURN:
                        executePurchaseReturn(compute, storeInfo);
                        break;
                    case StoreCompute.PURCHASE_ADD:
                        executePurchaseAdd(compute, storeInfo);
                        break;
                    case StoreCompute.PS_IN:
                        executePurchaseAdd(compute, storeInfo);
                        break;
                    case StoreCompute.ZH_IN:
                        executePurchaseAdd(compute, storeInfo);
                        break;
                    case StoreCompute.ZH_OUT:
                        executePurchaseReturn(compute, storeInfo);
                        break;
                    case StoreCompute.CF_IN:
                        executePurchaseAdd(compute, storeInfo);
                        break;
                    case StoreCompute.CF_OUT:
                        executePurchaseReturn(compute, storeInfo);
                        break;
                    default:
                        throw new IllegalArgumentException(compute.getOccurType() + " --不支持此类型库存计算");
                }
            }
        }
    }

    /**
     * 计算采购进货的库存
     */
    private void executePurchaseAdd(StoreCompute compute, GoodsStoreInfo storeInfo) {
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal thisAvgAmount;
        BigDecimal thisStoreAmount = BigDecimal.ZERO;
        if (storeInfo.getStoreId() != null) {
            quantity = storeInfo.getQuantity();
            thisStoreAmount = storeInfo.getStoreAmount();
        }
        StoreAccount account = new StoreAccount();
        account.occurIncurred = compute.getPrice();
        account.occurQuantity = computeOccurQuantity(compute);
        account.occurAmount = account.occurIncurred.multiply(account.occurQuantity);
        account.setTenantId(storeInfo.getTenantId());
        account.setBranchId(storeInfo.getBranchId());
        account.setGoodsId(storeInfo.getGoodsId());

        //库存数量操作之后值
        BigDecimal resQuantity = computeQuantity(quantity, compute);
        /*
         操作后库存数量为零
         成本=本次进价  库存金额为零
         */
        if (resQuantity.compareTo(BigDecimal.ZERO) == 0) {
            quantity = resQuantity;
            thisStoreAmount = BigDecimal.ZERO;
            thisAvgAmount = compute.getPrice();
            this.saveGoodsInvoicing(account,compute);
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);

            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1){
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
        /*
        初始为正，操作后为正（判断库存数量）
        （如果没有库存或者库存数量为0则使用此算法）
        进货金额 = 本次进价 * 进货数量
        库存金额 = 库存金额+进货金额
        成本 =（库存金额+进货金额）/（库存数量+进货数量）
        */
        if (quantity.compareTo(BigDecimal.ZERO) >= 0 && resQuantity.compareTo(BigDecimal.ZERO) > 0) {
            quantity = resQuantity;

            thisStoreAmount = thisStoreAmount.add(compute.getPrice().multiply(compute.getQuantity()));
            thisAvgAmount = thisStoreAmount.divide(quantity,3,BigDecimal.ROUND_HALF_DOWN);
            this.saveGoodsInvoicing(account,compute);
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);

            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1) {
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
        /*
        初始为负，操作后为正或负（判断库存数量）
        成本=本次进价
        库存数量=原库存数量+进货数量
        库存金额=成本*库存数量
        */
        if (quantity.compareTo(BigDecimal.ZERO) < 0 &&
                (resQuantity.compareTo(BigDecimal.ZERO) > 0 || resQuantity.compareTo(BigDecimal.ZERO) < 0)) {
            quantity = resQuantity;
            thisAvgAmount = compute.getPrice();
            thisStoreAmount = quantity.multiply(thisAvgAmount);
            this.saveGoodsInvoicing(account,compute);
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);

            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1) {
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
    }

    /**
     * 计算采购退货的库存
     * 如果resQuantity==0&&resAmount==0 则按resQuantity==0计算
     */
    private void executePurchaseReturn(StoreCompute compute, GoodsStoreInfo storeInfo) {
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal thisStoreAmount = BigDecimal.ZERO;
        BigDecimal thisAvgAmount;
        if (storeInfo.getStoreId() != null) {
            quantity = storeInfo.getQuantity();
            thisStoreAmount = storeInfo.getStoreAmount();
        }
        StoreAccount account = new StoreAccount();
        account.occurIncurred = compute.getPrice();
        account.occurQuantity = computeOccurQuantity(compute);
        account.occurAmount = account.occurIncurred.multiply(account.occurQuantity);
        account.setTenantId(storeInfo.getTenantId());
        account.setBranchId(storeInfo.getBranchId());
        account.setGoodsId(storeInfo.getGoodsId());

        //库存数量操作之后值
        BigDecimal resQuantity = computeQuantity(quantity, compute);
        BigDecimal resAmount = computeAmount(thisStoreAmount, compute);
        /*
         操作后库存数量为零
         成本=本次退货价  库存金额为零
         */
        if (resQuantity.compareTo(BigDecimal.ZERO) == 0) {
            quantity = resQuantity;
            thisStoreAmount = BigDecimal.ZERO;
            thisAvgAmount = compute.getPrice();
            /*account.occurIncurred = thisAvgAmount;
            account.occurQuantity = quantity;
            account.occurAmount = thisStoreAmount;*/
            this.saveGoodsInvoicing(account,compute);
            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1){
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
        /*
        初始为正，操作后为负（判断库存数量或库存金额）
        (如果没有库存或者库存数量为0则使用此算法）
        成本=本次退货价
        库存数量=原库存数量-退货数量
        库存金额=成本*库存数量
        */
        if (quantity.compareTo(BigDecimal.ZERO) >= 0
                && (resQuantity.compareTo(BigDecimal.ZERO) < 0
                || resAmount.compareTo(BigDecimal.ZERO) < 0)) {
            quantity = resQuantity;
            thisAvgAmount = compute.getPrice();
            thisStoreAmount = quantity.multiply(thisAvgAmount);
            /*account.occurIncurred = thisAvgAmount;
            account.occurQuantity = quantity;
            account.occurAmount = thisStoreAmount;*/
            this.saveGoodsInvoicing(account,compute);
            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1) {
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
        /*
        初始为正，操作后为正（判断库存数量）
        退货金额 = 本次退货价 * 退货数量
        库存金额=库存金额-退货金额
        成本=（库存金额-退货金额）/（库存数量-退货数量）
        */
        if (quantity.compareTo(BigDecimal.ZERO) >= 0
                && resQuantity.compareTo(BigDecimal.ZERO) > 0) {
            quantity = resQuantity;
            thisStoreAmount = thisStoreAmount.subtract(compute.getPrice().multiply(compute.getQuantity()));
            thisAvgAmount = thisStoreAmount.divide(quantity,3,BigDecimal.ROUND_HALF_DOWN);
            /*account.occurIncurred = thisAvgAmount;
            account.occurQuantity = quantity;
            account.occurAmount = thisStoreAmount;*/
            this.saveGoodsInvoicing(account,compute);
            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1) {
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
        /*
        初始为负，操作后为负（判断库存数量）
        成本=本次退货价
        库存数量=原库存数量-退货数量
        库存金额=成本*库存数量
        */
        if (quantity.compareTo(BigDecimal.ZERO) < 0
                && resQuantity.compareTo(BigDecimal.ZERO) < 0) {
            quantity = resQuantity;
            thisAvgAmount = compute.getPrice();
            thisStoreAmount = quantity.multiply(thisAvgAmount);
            /*account.occurIncurred = thisAvgAmount;
            account.occurQuantity = quantity;
            account.occurAmount = thisStoreAmount;*/
            this.saveGoodsInvoicing(account,compute);
            account.storeQuantity = quantity;
            account.storeAmount = thisStoreAmount;
            account.storeIncurred = thisAvgAmount;
            this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);
            this.saveStoreAccount(account, compute);
            if (storeInfo.getPurchasingPrice().compareTo(compute.getPrice()) != 0) {
                if(compute.getIsUpdatePrice() == null || compute.getIsUpdatePrice() == 1) {
                    storeMapper.updateGoodsPurchasingPrice(compute.getGoodsId(), compute.getPrice());
                }
            }
            return;
        }
    }

    /**
     * 计算盘点
     * <p>
     * 如果第一入库 库存成本=实盘进价
     * 库存数量 = 实盘数量
     * 库存金额 =成本*库存数量
     */
    private void executeInventory(StoreCompute compute, GoodsStoreInfo storeInfo) {
        BigDecimal quantity = compute.getQuantity();
        BigDecimal thisAvgAmount;
        BigDecimal thisStoreAmount;
        if (storeInfo.getStoreId() != null) {
            thisAvgAmount = storeInfo.getAvgAmount();
        } else {
            thisAvgAmount = storeInfo.getPurchasingPrice();
        }
        thisStoreAmount = quantity.multiply(thisAvgAmount);
        StoreAccount account = new StoreAccount();

        account.occurIncurred = thisAvgAmount;
        account.occurQuantity = compute.getCheckQuantity();
        account.occurAmount = account.occurIncurred.multiply(account.occurQuantity);

        account.storeQuantity = quantity;
        account.storeAmount = thisStoreAmount;
        account.storeIncurred = thisAvgAmount;
        account.setTenantId(storeInfo.getTenantId());
        account.setBranchId(storeInfo.getBranchId());
        account.setGoodsId(storeInfo.getGoodsId());
        this.saveGoodsInvoicing(account,compute);
        this.saveOrUpdateStore(compute, quantity, storeInfo, thisAvgAmount, thisStoreAmount);
        this.saveStoreAccount(account, compute);
    }

    /**
     * 计算出入库
     * 如果商品没有库存则使用商品进价当库存成本计算
     */
    private void executeInOrOut(StoreCompute compute, GoodsStoreInfo storeInfo) {
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal thisAvgAmount = storeInfo.getPurchasingPrice();
        BigDecimal thisStoreAmount;
        if (storeInfo.getStoreId() != null) {
            quantity = storeInfo.getQuantity();
            thisAvgAmount = storeInfo.getAvgAmount();
        }
        Map goodsMap=new HashMap();
        goodsMap.put("tenantId",compute.getTenantId());
        goodsMap.put("id",compute.getGoodsId());
        Goods goods=goodsMapper.findGoodsByIdAndTenantId(goodsMap);
        BigDecimal goodsNum=BigDecimal.ONE;
        if(goods!=null){
            if(goods.getCombinationType()==3){
                LogUtil.logInfo("计算配方商品库存======");
                goodsNum=goodsProduceRelationMapper.getRelationQuantity(compute.getGoodsId(),storeInfo.getGoodsId());
                LogUtil.logInfo("配方商品计算的商品数量num="+goodsNum+",goodsId="+storeInfo.getGoodsId()+",pGoodsId="+compute.getGoodsId());
                if(goodsNum==null||goodsNum.compareTo(BigDecimal.ZERO)==0){
                    return;
                }
            }
        }
        //库存数量操作之后值
        BigDecimal resQuantity = computeQuantityPF(quantity, compute, goodsNum);
        thisStoreAmount = resQuantity.multiply(thisAvgAmount);
        StoreAccount account = new StoreAccount();
        account.occurIncurred = thisAvgAmount;
        account.occurQuantity = computeOccurQuantityPF(compute,goodsNum);
        account.occurAmount = account.occurIncurred.multiply(account.occurQuantity);

        account.storeQuantity = resQuantity;
        account.storeAmount = thisStoreAmount;
        account.storeIncurred = thisAvgAmount;
        account.setTenantId(storeInfo.getTenantId());
        account.setBranchId(storeInfo.getBranchId());
        account.setGoodsId(storeInfo.getGoodsId());
        this.saveGoodsInvoicing(account,compute);
        this.saveOrUpdateStore(compute, resQuantity, storeInfo, thisAvgAmount, thisStoreAmount);

        this.saveStoreAccountPF(account, compute,storeInfo);
    }

    /**
     * 保存库存流水
     * 进退货就是本次进价  销售盘点就是当前库存成本
     */
    private void saveStoreAccount(StoreAccount account, StoreCompute compute) {
        account.occurAmount = new BigDecimal(account.occurAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.occurQuantity = new BigDecimal(account.occurQuantity.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.occurIncurred = new BigDecimal(account.occurIncurred.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeQuantity = new BigDecimal(account.storeQuantity.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeAmount = new BigDecimal(account.storeAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeIncurred = new BigDecimal(account.storeIncurred.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.createAt = new Date();
        account.lastUpdateAt = account.createAt;
        account.isDeleted = false;
        account.tenantId = compute.getTenantId();
        account.branchId = compute.getBranchId();
        account.occurAt = account.createAt;
        account.goodsId = compute.getGoodsId();
        account.occurType = compute.getOccurType();
        account.occurCode = compute.getCode();
        account.storeAccountAt = compute.getBillCreateTimeToDate();
        account.createBy = compute.getCreateBy() != null ? compute.getCreateBy().toString() : null;
        account.lastUpdateBy = account.createBy;
        storeMapper.saveStoreAccount(account);
    }
    /**
     * 保存库存流水
     * 进退货就是本次进价  销售盘点就是当前库存成本
     */
    private void saveStoreAccountPF(StoreAccount account, StoreCompute compute,GoodsStoreInfo storeInfo) {
        account.occurAmount = new BigDecimal(account.occurAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.occurQuantity = new BigDecimal(account.occurQuantity.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.occurIncurred = new BigDecimal(account.occurIncurred.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeQuantity = new BigDecimal(account.storeQuantity.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeAmount = new BigDecimal(account.storeAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.storeIncurred = new BigDecimal(account.storeIncurred.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        account.createAt = new Date();
        account.lastUpdateAt = account.createAt;
        account.isDeleted = false;
        account.tenantId = compute.getTenantId();
        account.branchId = compute.getBranchId();
        account.occurAt = account.createAt;
        account.goodsId = storeInfo.getGoodsId();
        account.occurType = compute.getOccurType();
        account.occurCode = compute.getCode();
        account.storeAccountAt = compute.getBillCreateTimeToDate();
        account.createBy = compute.getCreateBy() != null ? compute.getCreateBy().toString() : null;
        account.lastUpdateBy = account.createBy;
        storeMapper.saveStoreAccount(account);
    }

    /**
     * 修改或保存库存
     *
     * @param compute         单据
     * @param resQuantity     当前库存数量
     * @param storeInfo       商品库存信息
     * @param thisAvgAmount   当前库存成本
     * @param thisStoreAmount 当前库存金额
     * @throws OptimisticLockingFailureException 数据版本过期
     */
    private void saveOrUpdateStore(StoreCompute compute, BigDecimal resQuantity,
                                   GoodsStoreInfo storeInfo, BigDecimal thisAvgAmount, BigDecimal thisStoreAmount) {
        resQuantity = new BigDecimal(resQuantity.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        thisAvgAmount = new BigDecimal(thisAvgAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        thisStoreAmount = new BigDecimal(thisStoreAmount.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

        Store store = new Store();
        store.id = storeInfo.getStoreId();
        store.tenantId = compute.getTenantId();
        store.branchId = compute.getBranchId();
        store.goodsId = storeInfo.getGoodsId();
        store.quantity = resQuantity;
        store.avgAmount = thisAvgAmount;
        store.storeAmount = thisStoreAmount;
        store.storeAt = compute.getBillCreateTimeToDate();
        store.createAt = new Date();
        store.lastUpdateAt = store.createAt;
        store.version = storeInfo.getStoreVersion();
        int res;
        if (store.id == null) {
            storeMapper.saveStore(store);
            storeInfo.setStoreVersion(0L);
        } else {
            res = storeMapper.updateStore(store);
            if (res == 0) {
                throw new OptimisticLockingFailureException("库存id=" + store.id.toString() + "version=" + store.version + "版本过期");
            }
            storeInfo.setStoreVersion(store.version + 1);
        }
        storeInfo.setStoreId(store.id);
        storeInfo.setStoreAmount(store.storeAmount);
        storeInfo.setQuantity(store.quantity);
        storeInfo.setAvgAmount(store.avgAmount);
    }

    /**
     * 计算库存数量
     *
     * @param quantity 原库存数量
     * @param compute  单据扣减数量
     * @return 当前库存数量
     */
    private BigDecimal computeQuantity(BigDecimal quantity, StoreCompute compute) {
        BigDecimal inQ = compute.getQuantity();
        if (compute.getOccurType() == StoreCompute.RETAIL_SALE ||
                compute.getOccurType() == StoreCompute.PURCHASE_RETURN ||
                compute.getOccurType() == StoreCompute.ONLINE_SALE ||
                compute.getOccurType() == StoreCompute.PACK_OUT ||
                compute.getOccurType() == StoreCompute.UNPACK_OUT ||
                compute.getOccurType() == StoreCompute.PS_OUT ||
                compute.getOccurType() == StoreCompute.LY_OUT ||
                compute.getOccurType() == StoreCompute.ZH_OUT ||
                compute.getOccurType() == StoreCompute.CF_OUT
                ) {//出库操作
            inQ = compute.getQuantity().multiply(new BigDecimal(-1));
        }
        return quantity.add(inQ);
    }
    /**
     * 计算库存数量
     *
     * @param quantity 原库存数量
     * @param compute  单据扣减数量
     * @return 当前库存数量
     */
    private BigDecimal computeQuantityPF(BigDecimal quantity, StoreCompute compute ,BigDecimal goodsNum) {
        BigDecimal inQ = compute.getQuantity().multiply(goodsNum);
        if (compute.getOccurType() == StoreCompute.RETAIL_SALE ||
                compute.getOccurType() == StoreCompute.PURCHASE_RETURN ||
                compute.getOccurType() == StoreCompute.ONLINE_SALE ||
                compute.getOccurType() == StoreCompute.PACK_OUT ||
                compute.getOccurType() == StoreCompute.UNPACK_OUT ||
                compute.getOccurType() == StoreCompute.PS_OUT ||
                compute.getOccurType() == StoreCompute.LY_OUT ||
                compute.getOccurType() == StoreCompute.ZH_OUT ||
                compute.getOccurType() == StoreCompute.CF_OUT
                ) {//出库操作
            inQ = compute.getQuantity().multiply(new BigDecimal(-1)).multiply(goodsNum);
        }
        return quantity.add(inQ);
    }

    /**
     * 计算库存金额
     */
    private BigDecimal computeAmount(BigDecimal amount, StoreCompute compute) {
        BigDecimal inQ = compute.getQuantity();
        if (compute.getOccurType() == StoreCompute.RETAIL_SALE ||
                compute.getOccurType() == StoreCompute.PURCHASE_RETURN ||
                compute.getOccurType() == StoreCompute.ONLINE_SALE ||
                compute.getOccurType() == StoreCompute.PACK_OUT ||
                compute.getOccurType() == StoreCompute.UNPACK_OUT ||
                compute.getOccurType() == StoreCompute.PS_OUT ||
                compute.getOccurType() == StoreCompute.LY_OUT ||
                compute.getOccurType() == StoreCompute.ZH_OUT ||
                compute.getOccurType() == StoreCompute.CF_OUT
                ) {//出库操作
            inQ = compute.getQuantity().multiply(new BigDecimal(-1));
        }
        return amount.add(inQ.multiply(compute.getPrice()));

    }

    /**
     * 计算发生成本
     */
    private BigDecimal computeOccurQuantity(StoreCompute compute) {
        BigDecimal inQ = compute.getQuantity();
        if (compute.getOccurType() == StoreCompute.RETAIL_SALE ||
                compute.getOccurType() == StoreCompute.PURCHASE_RETURN ||
                compute.getOccurType() == StoreCompute.ONLINE_SALE ||
                compute.getOccurType() == StoreCompute.PACK_OUT ||
                compute.getOccurType() == StoreCompute.UNPACK_OUT ||
                compute.getOccurType() == StoreCompute.PS_OUT ||
                compute.getOccurType() == StoreCompute.LY_OUT ||
                compute.getOccurType() == StoreCompute.ZH_OUT ||
                compute.getOccurType() == StoreCompute.CF_OUT) {//出库操作
            inQ = compute.getQuantity().multiply(new BigDecimal(-1));
        }
        return inQ;
    }
    /**
     * 计算发生成本
     */
    private BigDecimal computeOccurQuantityPF(StoreCompute compute,BigDecimal goodsNum) {
        BigDecimal inQ = compute.getQuantity().multiply(goodsNum);
        if (compute.getOccurType() == StoreCompute.RETAIL_SALE ||
                compute.getOccurType() == StoreCompute.PURCHASE_RETURN ||
                compute.getOccurType() == StoreCompute.ONLINE_SALE ||
                compute.getOccurType() == StoreCompute.PACK_OUT ||
                compute.getOccurType() == StoreCompute.UNPACK_OUT ||
                compute.getOccurType() == StoreCompute.PS_OUT ||
                compute.getOccurType() == StoreCompute.LY_OUT ||
                compute.getOccurType() == StoreCompute.ZH_OUT ||
                compute.getOccurType() == StoreCompute.CF_OUT) {//出库操作
            inQ = compute.getQuantity().multiply(new BigDecimal(-1)).multiply(goodsNum);
        }
        return inQ;
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
    private void saveGoodsInvoicing(StoreAccount account, StoreCompute compute){
        try{
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            String date=sf.format(compute.getBillCreateTimeToDate());
            GoodsInvoicing useGoodsInvoicing=null;
            GoodsInvoicing goodsInvoicing1=goodsInvoicingMapper.findGoodsInvocing(account.getTenantId(),account.getBranchId(),account.getGoodsId(),date);
            if(goodsInvoicing1==null){
                GoodsInvoicing goodsInvoicing2=goodsInvoicingMapper.findLastGoodsInvocing(account.getTenantId(),account.getBranchId(),account.getGoodsId());
                GoodsInvoicing newGoodsInvoicing=new GoodsInvoicing();
                newGoodsInvoicing.setTenantId(account.getTenantId());
                newGoodsInvoicing.setBranchId(account.getBranchId());
                newGoodsInvoicing.setGoodsId(account.getGoodsId());
                newGoodsInvoicing.setCreateAt(new Date());
                newGoodsInvoicing.setCreateBy("System");
                newGoodsInvoicing.setLastUpdateAt(new Date());
                newGoodsInvoicing.setLastUpdateBy("System");
                newGoodsInvoicing.setInvoicingDate(compute.getBillCreateTimeToDate());
                if(goodsInvoicing2==null){
                    Map map=new HashMap();
                    map.put("tenantId",account.getTenantId());
                    map.put("branchId",account.getBranchId());
                    map.put("goodsId",account.getGoodsId());
                    Store store=storeMapper.selectByGoodsId(map);
                    if(store==null){
                        newGoodsInvoicing.setBeginAmount(BigDecimal.ZERO);
                        newGoodsInvoicing.setBeginQuantity(BigDecimal.ZERO);
                        newGoodsInvoicing.setEndAmount(BigDecimal.ZERO);
                        newGoodsInvoicing.setEndQuantity(BigDecimal.ZERO);
                    }else{
                        newGoodsInvoicing.setBeginAmount(store.getStoreAmount());
                        newGoodsInvoicing.setBeginQuantity(store.getQuantity());
                        newGoodsInvoicing.setEndAmount(store.getStoreAmount());
                        newGoodsInvoicing.setEndQuantity(store.getQuantity());
                    }
                }else{
                    newGoodsInvoicing.setBeginAmount(goodsInvoicing2.getEndAmount());
                    newGoodsInvoicing.setBeginQuantity(goodsInvoicing2.getEndQuantity());
                    newGoodsInvoicing.setEndAmount(goodsInvoicing2.getEndAmount());
                    newGoodsInvoicing.setEndQuantity(goodsInvoicing2.getEndQuantity());
                }
                int i = goodsInvoicingMapper.insert(newGoodsInvoicing);
                useGoodsInvoicing = newGoodsInvoicing;
            }else{
                useGoodsInvoicing=goodsInvoicing1;
            }
            switch (compute.getOccurType()) {
                case StoreCompute.RETAIL_SALE:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setSaleOutAmount(useGoodsInvoicing.getSaleOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setSaleOutQuantity(useGoodsInvoicing.getSaleOutQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setSaleOutTotal(useGoodsInvoicing.getSaleOutTotal().add(compute.getPrice()));
                    break;
                case StoreCompute.RETAIL_RETURN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setSaleInAmount(useGoodsInvoicing.getSaleInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setSaleInQuantity(useGoodsInvoicing.getSaleInQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setSaleInTotal(useGoodsInvoicing.getSaleInTotal().add(compute.getPrice()));
                    break;
                case StoreCompute.ONLINE_RETURN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setOnlineSaleInAmount(useGoodsInvoicing.getOnlineSaleInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setOnlineSaleInQuantity(useGoodsInvoicing.getOnlineSaleInQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setOnlineSaleInTotal(useGoodsInvoicing.getOnlineSaleInTotal().add(compute.getPrice()));
                    break;
                case StoreCompute.ONLINE_SALE:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setOnlineSaleOutAmount(useGoodsInvoicing.getOnlineSaleOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setOnlineSaleOutQuantity(useGoodsInvoicing.getOnlineSaleOutQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setOnlineSaleOutTotal(useGoodsInvoicing.getOnlineSaleOutTotal().add(compute.getPrice()));
                    break;
                case StoreCompute.PACK_IN:
                    break;
                case StoreCompute.PACK_OUT:
                    break;
                case StoreCompute.UNPACK_IN:
                    break;
                case StoreCompute.UNPACK_OUT:
                    break;
                case StoreCompute.PS_OUT:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setPsOutAmount(useGoodsInvoicing.getPsOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setPsOutQuantity(useGoodsInvoicing.getPsOutQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.LY_OUT:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setLyOutAmount(useGoodsInvoicing.getLyOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setLyOutQuantity(useGoodsInvoicing.getLyOutQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.LY_IN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setLyInAmount(useGoodsInvoicing.getLyInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setLyInQuantity(useGoodsInvoicing.getLyInQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.SY_IN_OUT:
                    if(account.occurQuantity.compareTo(BigDecimal.ZERO)>=0){
                        useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                        useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                        useGoodsInvoicing.setSyInAmount(useGoodsInvoicing.getSyInAmount().add(account.occurAmount.abs()));
                        useGoodsInvoicing.setSyInQuantity(useGoodsInvoicing.getSyInQuantity().add(account.occurQuantity.abs()));
                    }else{
                        useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                        useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                        useGoodsInvoicing.setSyOutAmount(useGoodsInvoicing.getSyOutAmount().add(account.occurAmount.abs()));
                        useGoodsInvoicing.setSyOutQuantity(useGoodsInvoicing.getSyOutQuantity().add(account.occurQuantity.abs()));
                    }
                    break;
                case StoreCompute.INVENTORY:
                    if(account.occurQuantity.compareTo(BigDecimal.ZERO)>=0){
                        useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                        useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                        useGoodsInvoicing.setPdInAmount(useGoodsInvoicing.getPdInAmount().add(account.occurAmount.abs()));
                        useGoodsInvoicing.setPdInQuantity(useGoodsInvoicing.getPdInQuantity().add(account.occurQuantity.abs()));
                    }else{
                        useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                        useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                        useGoodsInvoicing.setPdOutAmount(useGoodsInvoicing.getPdOutAmount().add(account.occurAmount.abs()));
                        useGoodsInvoicing.setPdOutQuantity(useGoodsInvoicing.getPdOutQuantity().add(account.occurQuantity.abs()));
                    }
                    break;
                case StoreCompute.PURCHASE_RETURN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setCgOutAmount(useGoodsInvoicing.getCgOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setCgOutQuantity(useGoodsInvoicing.getCgOutQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.PURCHASE_ADD:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setCgInAmount(useGoodsInvoicing.getCgInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setCgInQuantity(useGoodsInvoicing.getCgInQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.PS_IN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setPsInAmount(useGoodsInvoicing.getPsInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setPsInQuantity(useGoodsInvoicing.getPsInQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.ZH_IN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setJgInAmount(useGoodsInvoicing.getJgInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setJgInQuantity(useGoodsInvoicing.getJgInQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.ZH_OUT:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setJgOutAmount(useGoodsInvoicing.getJgOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setJgOutQuantity(useGoodsInvoicing.getJgOutQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.CF_IN:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().add((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().add(account.occurQuantity.abs()));
                    useGoodsInvoicing.setJgInAmount(useGoodsInvoicing.getJgInAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setJgInQuantity(useGoodsInvoicing.getJgInQuantity().add(account.occurQuantity.abs()));
                    break;
                case StoreCompute.CF_OUT:
                    useGoodsInvoicing.setEndAmount(useGoodsInvoicing.getEndAmount().subtract((account.occurAmount.abs())));
                    useGoodsInvoicing.setEndQuantity(useGoodsInvoicing.getEndQuantity().subtract(account.occurQuantity.abs()));
                    useGoodsInvoicing.setJgOutAmount(useGoodsInvoicing.getJgOutAmount().add(account.occurAmount.abs()));
                    useGoodsInvoicing.setJgOutQuantity(useGoodsInvoicing.getJgOutQuantity().add(account.occurQuantity.abs()));
                    break;
                default:
                    throw new IllegalArgumentException(compute.getOccurType() + " --不支持此类型库存计算");
            }
            useGoodsInvoicing.setLastUpdateAt(new Date());
            int j=goodsInvoicingMapper.update(useGoodsInvoicing);
        }catch(Exception e){
            LogUtil.logError("storeComputeServiceImpl.saveGoodsInvoicing:"+e.getMessage());
        }

    }
}
