package erp.chain.mapper;

import erp.chain.domain.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2016/11/25.
 */
@Mapper
public interface AreaMapper extends BaseMapper {
    List<Area> queryAreaList(Map<String, Object> params);
    List<Area> findByCondition(Map<String, Object> params);
    List<Area> findAllByTenantId(@Param("tenantId") BigInteger tenantId);
    Area find(@Param("tenantId") BigInteger tenantId, @Param("id") BigInteger id);
    int update(Area area);
    String queryNextCode(@Param("tenantId") BigInteger tenantId, @Param("parentId") BigInteger parentId);
    Long insert(Area area);
    int findChildAreaCount(@Param("tenantId") BigInteger tenantId, @Param("areaId") BigInteger areaId);
    int findBranchCount(@Param("tenantId") BigInteger tenantId, @Param("areaId") BigInteger areaId);
    List<Map> queryAreasList(Map params);
    long queryAreasListSum(Map params);
}
