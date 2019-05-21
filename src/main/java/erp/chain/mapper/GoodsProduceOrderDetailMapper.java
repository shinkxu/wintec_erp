package erp.chain.mapper;

import erp.chain.domain.GoodsProduceOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 商品加工单明细表
 *
 * @author hefuzi 2016-11-29
 */
@Mapper
public interface GoodsProduceOrderDetailMapper {


    /**
     * List查询-商品加工单明细表
     */
    List<Map<String,Object>> queryList(@Param("orderCode") String orderCode,@Param("branchId") String branchId);


    /**
     * orderCode删除-商品加工单明细表
     *
     * @return 0-失败(版本过期或不存在) 1-成功
     */
    int delete(@Param("orderCode") String orderCode, @Param("empId") BigInteger empId);


    /**
     * 批量保存-商品加工单明细表
     */
    int saveList(@Param("list") List<GoodsProduceOrderDetail> list);
}