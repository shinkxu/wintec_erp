package erp.chain.mapper.system;

import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 统计商户
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/13
 */
@Mapper
public interface WebServiceTenantMonitorMapper extends BaseMapper{
    Integer branchCount(@Param("tenantType")String tenantType);
    Integer branchCountCondition(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("tenantType")String tenantType);
    int markTestTenant(@Param("tenantId")BigInteger tenantId);
    List<Map> queryBranchCount(@Param("tenantIds")String tenantIds);
}
