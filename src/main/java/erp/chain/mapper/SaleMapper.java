package erp.chain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface SaleMapper extends BaseMapper {
    String getMaxSaleCode(@Param("posId")BigInteger posId,@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,@Param("today")String today);
}