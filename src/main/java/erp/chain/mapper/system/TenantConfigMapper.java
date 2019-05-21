package erp.chain.mapper.system;

import erp.chain.domain.system.PosSysConfig;
import erp.chain.domain.system.TenantConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * 商户配置参数
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/17
 */
@Mapper
public interface TenantConfigMapper{
    int update(TenantConfig tenantConfig);
    TenantConfig findByNameAndTenantId(@Param("key") String key,@Param("tenantId") BigInteger tenantId);
    int insert(TenantConfig tenantConfig);
    int insertPosSysConfig(PosSysConfig posSysConfig);
    int updatePosSysConfig(PosSysConfig posSysConfig);
    PosSysConfig getConfig(@Param("tenantId")BigInteger tenantId,
                                 @Param("branchId")BigInteger branchId,
                                 @Param("employeeId")BigInteger employeeId,
                                 @Param("type")String type);
    PosSysConfig recoverPosSysConfig(@Param("recoverCode")String recoverCode,@Param("type")String type);
}
