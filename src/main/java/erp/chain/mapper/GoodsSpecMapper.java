package erp.chain.mapper;

import erp.chain.domain.GoodsSpec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsSpecMapper extends BaseMapper {
    List<Map> goodsSpecList(Map params);
    GoodsSpec findByCondition(Map params);
    Long goodsSpecListCount(Map params);
    String getSpecCode(Map params);
    Long checkProperty(Map params);
    GoodsSpec findByIdAndTenantId(@Param("id")BigInteger id,@Param("tenantId") BigInteger tenantId);
    int insert(GoodsSpec goodsSpec);
    int update(GoodsSpec goodsSpec);
    Long countByBranch(Map params);
}