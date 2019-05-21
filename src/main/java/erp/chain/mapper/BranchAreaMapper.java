package erp.chain.mapper;

import erp.chain.domain.BranchArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface BranchAreaMapper extends BaseMapper {
    List<Map> queryAreaList(Map params);
    Long queryAreaListSum(Map params);
    Long checkName(Map params);
    BranchArea getAreaById(Map params);
    int insert(BranchArea branchArea);
    int update(BranchArea branchArea);
    String getMaxAreaCode(Map params);
    Long useAreaCount(@Param("areaId")BigInteger areaId,@Param("tenantId")BigInteger tenantId);

}