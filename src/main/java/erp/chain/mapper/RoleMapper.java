package erp.chain.mapper;

import erp.chain.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 角色
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/13
 */
@Mapper
public interface RoleMapper extends BaseMapper{
    int insert(SysRole sysRole);
    int update(SysRole sysRole);
    List<Map> queryRoleList(Map params);
    Long queryRoleListSum(Map params);
    String getRoleCode(Map params);
    SysRole findRoleById(@Param("id")BigInteger id,@Param("tenantId") BigInteger tenantId);
    Long countUsedRole(@Param("roleId") BigInteger roleId);
    SysRole getShopManager(@Param("tenantId")BigInteger tenantId);
    SysRole getStallsManager(@Param("tenantId")BigInteger tenantId);
    int insertRoleUser(@Param("roleId")BigInteger roleId,@Param("userId")String userId,@Param("tenantId")BigInteger tenantId);
    SysRole getManager(@Param("tenantId")String tenantId);
    List<BigInteger> getRolePrivileges(@Param("userId")BigInteger userId);

    List<Map<String, Long>> getUserPosRiv(@Param("uIds") String uIds);

}
