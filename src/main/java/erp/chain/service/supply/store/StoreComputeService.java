package erp.chain.service.supply.store;


import erp.chain.domain.supply.store.StoreCompute;

import java.util.List;

/**
 * 计算库存 Service
 */
public interface StoreComputeService {
    /**
     * 计算库存
     *
     * @param compute 库存单
     */
    public void executeStoreCompute(StoreCompute compute);

    /**
     * 批量计算库存
     *
     * @param computes 库存单集合
     */
    public void executeStoreCompute(List<StoreCompute> computes);
}
