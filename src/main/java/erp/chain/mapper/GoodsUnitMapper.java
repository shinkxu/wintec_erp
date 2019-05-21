package erp.chain.mapper;

import erp.chain.domain.GoodsUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsUnitMapper extends BaseMapper {
    List<Map> farmUnitList(Map params);
    long farmUnitListCount(Map params);
    List<HashMap<String, Object>> queryUnitList(Map params);
    Long queryUnitCount(Map params);
    GoodsUnit queryUnitById(Map params);
    GoodsUnit getByName(@Param("tenantId")BigInteger tenantId,@Param("unitName")String unitName);
    List<GoodsUnit> queryUnits(Map params);
    Long checkGoodsUnit(Map params);
    Long checkUnitName(Map params);
    int insert(GoodsUnit params);
    int update(GoodsUnit params);
    String getMaxGoodsUnitCodeWs(Map params);
    GoodsUnit findByTenantIdAndIsDeleted(@Param("tenantId")BigInteger tenantId);

    List<Map<String, Object>> findAll(@Param("tenantId") BigInteger tenantId);
}