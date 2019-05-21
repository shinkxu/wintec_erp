package erp.chain.mapper.system;

import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 初始化商户信息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/3
 */
@Mapper
public interface InitTenantMapper extends BaseMapper{
    BigInteger initTenant(@Param("tenantid")String tenantid,@Param("loginname")String loginname,@Param("userid")String userid,@Param("branchName")String branchName,@Param("phone")String phone,@Param("contacts")String contacts,@Param("business")String business,@Param("initType")String initType);
    void insertRestRole(@Param("tenantId")String tenantId,@Param("branchId")BigInteger branchId);
    void insertRetailRole(@Param("tenantId")String tenantId,@Param("branchId")BigInteger branchId);
    void insertRolePrivilege(@Param("roleId")String roleId,@Param("privileges")List privileges,@Param("tenantId")String tenantId);
    List<Map> getInitInfo(@Param("businessId")String businessId,@Param("type")String type);
    List tenantLoginInit(@Param("businessId")String businessId,@Param("tenantId")String tenantId);
    int tenantLoginInit2(@Param("tenantId")String tenantId,@Param("branchId")String branchId);
    long countUnit(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    int clearBranchData(@Param("tenantId")BigInteger tenantId,
                        @Param("branchId")BigInteger branchId,
                        @Param("userName")String userName,
                        @Param("type")Integer type,
                        @Param("startDate")String startDate,
                        @Param("endDate")String endDate);
}
