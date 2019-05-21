package erp.chain.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 权限
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/15
 */
@Mapper
public interface PrivilegeMapper{
    List<Map> rolePrivilege(@Param("roleId")BigInteger roleId, @Param("limitDate")String limitDate);
    void deleteOldPrivilege(@Param("roleId")BigInteger roleId);
    int saveRolePrivileges(@Param("roleId")BigInteger roleId,@Param("privileges") String[] privileges,@Param("tenantId")BigInteger tenantId);
}
