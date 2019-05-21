package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.ElemeGoodsMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2018-01-18.
 */
@Mapper
public interface ElemeGoodsMappingMapper {
    long insert(ElemeGoodsMapping elemeGoodsMapping);
    long update(ElemeGoodsMapping elemeGoodsMapping);
    ElemeGoodsMapping findByTenantIdAndBranchIdAndElemeGoodsId(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("elemeGoodsId") BigInteger elemeGoodsId);
}
