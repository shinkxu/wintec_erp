package erp.chain.mapper;

import erp.chain.domain.Supplier;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by lipeng on 2016/11/25.
 */
@Mapper
public interface SupplierMapper extends BaseMapper {

    /**
     * 供应商列表
     * */
    List<Map> querySupplierList(Map params);
    Long querySupplierListSum(Map params);

    /**
     * 通过ID和tenantId查询供应商信息
     * */
    Supplier findSupplierByIdAndTenantId(Map params);

    /**
     * 新增供应商
     * */
    int insert(Supplier supplier);

    /**
     * 修改供应商
     * */
    int update(Supplier supplier);

    /**
     * 查询是否有商品使用该供应商
     * */
    Long supplierToGoods(Map params);

    /**
     * 是否有采购单使用该供应商
     * */
    Long supplierToStoreOrder(Map params);

    /**
     * 删除供应商
     * */
    int delSupplier(Map params);

    /**
     * 查询当前供应商最大编码
     * */
    String queryMaxCode(Map params);

    /**
     * 查询是否有重复code
     * */
    Long isUsedCode(Map params);
}
