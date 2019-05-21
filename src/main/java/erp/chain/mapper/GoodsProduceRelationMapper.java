package erp.chain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 加工配方
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/14
 */
@Mapper
public interface GoodsProduceRelationMapper extends BaseMapper{
    List<Map> queryProduceGoods(Map params);
    Long queryProduceGoodsSum(Map params);
    List<Map> getRelationById(Map params);
    Long getRelationByIdSum(Map params);
    int deleteByProduceGoodsId(@Param("produceGoodsId")BigInteger produceGoodsId);
    int insertRelation(@Param("relationList")List<Map> relationList);
    BigDecimal getRelationQuantity(@Param("produceGoodsId")BigInteger produceGoodsId,@Param("goodsId")BigInteger goodsId);

    int doDeleteRelationGoodsById(@Param("produceGoodsId")BigInteger produceGoodsId);
}
