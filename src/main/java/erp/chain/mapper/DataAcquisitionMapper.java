package erp.chain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by liuyandong on 2016/11/25.
 */
@Mapper
public interface DataAcquisitionMapper {
    int deleteWithCover(@Param("tableName") String tableName,
                        @Param("tenantCode") String tenantCode,
                        @Param("timestamp") String timestamp);

    int deleteByTypeIds(@Param("tableName") String tableName,
                        @Param("tenantCode") String tenantCode,
                        @Param("typeIds") List<String> typeIds);


    int insert(@Param("tableName") String tableName,
               @Param("tenantCode") String tenantCode,
               @Param("dataList") List dataList,
               @Param("timestamp") String timestamp);

    int update(@Param("tableName") String tableName,
               @Param("tenantCode") String tenantCode,
               @Param("timestamp") String timestamp,
               @Param("fullName") String fullName,
               @Param("typeId") String typeId);
}
