package erp.chain.mapper.supply;

import erp.chain.domain.supply.GoodsInvoicing;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * 进销存
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/2/26
 */
@Mapper
public interface GoodsInvoicingMapper extends BaseMapper{
    int insert(GoodsInvoicing goodsInvoicing);
    int update(GoodsInvoicing goodsInvoicing);
    GoodsInvoicing findGoodsInvocing(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,
                                     @Param("goodsId")BigInteger goodsId,@Param("invoicingDate")String invoicingDate);
    GoodsInvoicing findLastGoodsInvocing(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,
                                         @Param("goodsId")BigInteger goodsId);
}
