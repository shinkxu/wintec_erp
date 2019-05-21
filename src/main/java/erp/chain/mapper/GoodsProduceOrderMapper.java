package erp.chain.mapper;

import erp.chain.domain.GoodsProduceOrder;
import erp.chain.model.base.QueryGoodsProduceOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 商品加工单表
 *
 * @author hefuzi 2016-11-29
 */
@Mapper
public interface GoodsProduceOrderMapper {
    /**
     * 分页查询-商品加工单表
     */
    List<Map<String, Object>> queryPager(QueryGoodsProduceOrderModel model);

    /**
     * 统计查询-商品加工单表
     */
    long queryCount(QueryGoodsProduceOrderModel model);

    /**
     * id查询-商品加工单表
     */
    GoodsProduceOrder get(@Param("id") BigInteger id);

    /**
     * id删除-商品加工单表
     *
     * @return 0-失败(版本过期或不存在) 1-成功
     */
    int delete(@Param("id") BigInteger id, @Param("empId") BigInteger empId, @Param("version") BigInteger version);

    /**
     * id审核-商品加工单表
     *
     * @return 0-失败(版本过期或不存在) 1-成功
     */
    int audit(@Param("id") BigInteger id, @Param("empId") BigInteger empId, @Param("version") BigInteger version);

    /**
     * id更新-商品加工单表
     *
     * @return 0-失败(版本过期或不存在) 1-成功
     */
    int update(GoodsProduceOrder goodsProduceOrder);

    /**
     * 保存-商品加工单表
     */
    int save(GoodsProduceOrder goodsProduceOrder);

    /**
     * 查询加工商品加工关系
     */
    List<Map<String, Long>> queryGoodsProduceRelation(@Param("produceGoodsId") BigInteger produceGoodsId,
                                                      @Param("tenantId") BigInteger tenantId);


}