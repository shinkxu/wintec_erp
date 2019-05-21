package erp.chain.mapper.system;

import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/6/16
 */
@Mapper
public interface OperateLogMapper extends BaseMapper{
    int createOpLog(@Param("resource")String resource,
                    @Param("resourceId")BigInteger resourceId,
                    @Param("operate")String operate,
                    @Param("oldValue")String oldValue,
                    @Param("newValue")String newValue,
                    @Param("opBy")String opBy,
                    @Param("opFrom")String opFrom,
                    @Param("memo")String memo,
                    @Param("tenantId")BigInteger tenantId,
                    @Param("branchId")BigInteger branchId
    );
    String getName(@Param("resource")String resource,@Param("key")String key);
}
