package erp.chain.mapper;


import erp.chain.domain.Employee;
import erp.chain.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper extends BaseMapper {
    List<Employee> select(Map<String, Object> map);

    int updateEmployee(Employee employee);

    String findMaxEmployeeCode(@Param("tenantId") BigInteger tenantId);

    int insert(Employee employee);

    /**
     * 后台接口
     */
    List<Map> queryEmployee(Map params);

    Long queryEmployeeSum(Map params);

    List<Map> queryRoles(@Param("userId") BigInteger userId);

    List<Map> queryRolesByCheckPhoneRoles(@Param("userId") BigInteger userId);

    Map queryEmployeeById(Map params);

    Map queryEmployeeByTenantIdAndBranchIdAndUserId(Map params);

    Employee getEmployeeById(@Param("id") BigInteger id, @Param("tenantId") BigInteger tenantId);

    Employee getEmployee(@Param("userId") BigInteger userId, @Param("tenantId") BigInteger tenantId);

    Employee findEmployeeByUser(@Param("userId") BigInteger userId);

    Long findRole(@Param("roleId") BigInteger roleId, @Param("tenantId") BigInteger tenantId);

    int deleteOldRole(@Param("userId") BigInteger userId);

    int saveRole(@Param("userId") BigInteger userId, @Param("roleId") BigInteger roleId, @Param("tenantId") BigInteger tenantId);

    String getEmployeeCode(@Param("tenantId") BigInteger tenantId);

    Employee getTenantEmployee(@Param("tenantId") BigInteger tenantId);

    List<Map> queryEmpPager(Map params);

    Long queryEmpPagerSum(Map params);

    int removeMobileBind(@Param("userId") BigInteger userId);

    int areaAuthority(Map params);

    int deleteCardInfo(@Param("empId") BigInteger empId);

    int deleteEmployee(Employee employee);


    /**
     * 导购员
     */
    List<Map> guidePercentageSale(Map params);

    List<Map> guidePercentageSaleSum(Map params);

    List<Map> guidePercentageStore(Map params);

    List<Map> guidePercentageStoreSum(Map params);

    List<Map> guideSummary(Map params);

    List<Map> guideSummarySum(Map params);

    Employee findEmployeeById(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("id") BigInteger id);

    List<Map> queryGuiDe(Map params);

    int queryGuiDeTotal(Map params);

    SysRole queryGuideRole(Map params);
    List<Map> findList(Map params);
    long countList(Map params);
}