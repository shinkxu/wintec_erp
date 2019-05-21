package erp.chain.mapper;

import erp.chain.domain.GoodsSpecR;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface GoodsSpecRMapper extends BaseMapper {
    int findByGoodsId(@Param("tenantId")BigInteger tenantId,@Param("goodsId") BigInteger goodsId);
    int insert(GoodsSpecR goodsSpecR);
    int update(GoodsSpecR goodsSpecR);
    int deleteByGoodsId(@Param("tenantId")BigInteger tenantId,@Param("goodsId") BigInteger goodsId);
}