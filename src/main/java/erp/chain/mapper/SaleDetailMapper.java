package erp.chain.mapper;


import erp.chain.domain.SaleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface SaleDetailMapper extends BaseMapper {
    int insertGuideSale(@Param("saleDetail")SaleDetail saleDetail,
                        @Param("saleRate")BigDecimal saleRate,
                        @Param("commissionAmount")BigDecimal commissionAmount);
}