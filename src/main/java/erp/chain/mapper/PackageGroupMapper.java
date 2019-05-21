package erp.chain.mapper;


import erp.chain.domain.PackageGroup;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PackageGroupMapper extends BaseMapper {
    PackageGroup getGroupByPackageId(@Param("tenantId")BigInteger tenantId,@Param("packageId") BigInteger packageId);
    int update(PackageGroup packageGroup);
    int insert(PackageGroup packageGroup);

    List<PackageGroup> findAll(SearchModel searchModel);
}