package erp.chain.mapper;


import erp.chain.domain.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface PackageMapper extends BaseMapper {
    List<Map> queryPackage(Map params);
    Long queryPackageSum(Map params);
    Goods getPackageById(@Param("id")BigInteger id, @Param("tenantId")BigInteger tenantId);
    int insert(Goods goods);
    int update(Goods goods);
    String getPackageCode(@Param("tenantId") BigInteger tenantId);
}