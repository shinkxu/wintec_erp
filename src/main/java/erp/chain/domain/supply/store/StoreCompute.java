package erp.chain.domain.supply.store;

import erp.chain.service.supply.store.StoreComputeService;
import erp.chain.utils.DateUtils;
import org.apache.commons.logging.Log;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库存单包装类
 */
public class StoreCompute {
    /*发生类型：零售销售*/
    public static final int RETAIL_SALE = 1;
    /*发生类型：零售退货*/
    public static final int RETAIL_RETURN = 5;
    /*发生类型：盘点损溢*/
    public static final int INVENTORY = 2;
    /*发生类型：采购退货*/
    public static final int PURCHASE_RETURN = 4;
    /*发生类型：采购进货*/
    public static final int PURCHASE_ADD = 3;
    /*发生类型：线上销售*/
    public static final int ONLINE_SALE = 6;
    /*发生类型：线上退货*/
    public static final int ONLINE_RETURN = 7;
    /*发生类型：打包入库*/
    public static final int PACK_IN = 8;
    /*发生类型：打包出库*/
    public static final int PACK_OUT = 9;
    /*发生类型：拆包入库*/
    public static final int UNPACK_IN = 10;
    /*发生类型：拆包出库*/
    public static final int UNPACK_OUT = 11;
    /*发生类型：配送出库*/
    public static final int PS_OUT = 12;
    /*发生类型：配送入库*/
    public static final int PS_IN = 13;
    /*发生类型：领用出库*/
    public static final int LY_OUT = 14;
    /*发生类型：领用入库*/
    public static final int LY_IN = 15;
    /*发生类型：损溢出入库*/
    public static final int SY_IN_OUT = 16;
    /*发生类型：组合入库*/
    public static final int ZH_IN = 17;
    /*发生类型：组合出库*/
    public static final int ZH_OUT = 18;
    /*发生类型：拆分入库*/
    public static final int CF_IN = 19;
    /*发生类型：拆分出库*/
    public static final int CF_OUT = 20;

    /**
     * 商户id
     */
    public BigInteger tenantId;
    /**
     * 机构Id
     */
    public BigInteger branchId;
    /**
     * 商品Id
     */
    public BigInteger goodsId;
    /**
     * occurType == PURCHASE_RETURN ：本次退货价
     * occurType == PURCHASE_ADD ：本次进价
     * occurType == 2 ：本次进价
     */
    public BigDecimal price;
    /**
     * 数量
     * occurType == 2 ：实盘数量
     */
    public BigDecimal quantity;
    /**
     * 损溢数量（盘点时不为null）
     */
    public BigDecimal checkQuantity;
    /**
     * 编码
     */
    public String code;
    /**
     * 发生类型
     */
    public int occurType;
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public String billCreateTime;
    /**
     * 操作人empId
     */
    public BigInteger createBy;

    /**
     * 单位类型 1-销售单位，2-包装单位
     * */
    public BigInteger unitType;

    /**
     * 单位换算 1销售单位=？*包装单位
     * */
    public BigDecimal unitRelation;

    /**
     * 是否更新商品档案
     * */
    public Integer isUpdatePrice;

    public BigInteger getUnitType() {
        return unitType;
    }

    public BigDecimal getUnitRelation() {
        return unitRelation;
    }

    public void setUnitType(BigInteger unitType) {
        this.unitType = unitType;
    }

    public void setUnitRelation(BigDecimal unitRelation) {
        this.unitRelation = unitRelation;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCheckQuantity() {
        return checkQuantity;
    }

    public void setCheckQuantity(BigDecimal checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getOccurType() {
        return occurType;
    }

    public void setOccurType(int occurType) {
        this.occurType = occurType;
    }

    public String getBillCreateTime() {
        return billCreateTime;
    }

    public Date getBillCreateTimeToDate() {
        return DateUtils.strToDate(billCreateTime);
    }

    public void setBillCreateTime(String billCreateTime) {
        this.billCreateTime = billCreateTime;
    }

    public BigInteger getCreateBy() {
        return createBy;
    }

    public void setCreateBy(BigInteger createBy) {
        this.createBy = createBy;
    }

    public Integer getIsUpdatePrice() {
        return isUpdatePrice;
    }

    public void setIsUpdatePrice(Integer isUpdatePrice) {
        this.isUpdatePrice = isUpdatePrice;
    }

    /**
     * 计算库存 发生并发是一直计算完
     */
    public static void executeStoreCompute(StoreCompute compute, StoreComputeService storeComputeService, Log logger) {
        List<StoreCompute> computes = new ArrayList<>();
        computes.add(compute);
        executeStoreCompute(computes,storeComputeService,logger);
    }

    /**
     * 批量计算库存 发生并发是一直计算完
     */
    public static void executeStoreCompute(List<StoreCompute> computes, StoreComputeService storeComputeService, Log logger) {
        boolean isVersion = true;
        while (isVersion) {
            try {
                storeComputeService.executeStoreCompute(computes);
                isVersion = false;
            } catch (OptimisticLockingFailureException | DuplicateKeyException e) {
                logger.warn("计算库存，数据版本并发");
            }
        }
    }
}
