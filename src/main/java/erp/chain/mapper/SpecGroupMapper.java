package erp.chain.mapper;

import erp.chain.domain.SpecGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 口味分组
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
@Mapper
public interface SpecGroupMapper{
    List<SpecGroup> specGroupList(Map params);
    List<SpecGroup> branchSpecGroupList(Map params);
    Long specGroupListCount(Map params);
    Long branchSpecGroupListCount(Map params);
    SpecGroup findByIdAndTenantId(@Param("id")BigInteger id,@Param("tenantId")BigInteger tenantId);
    Long checkGroupName(Map params);
    int insert(SpecGroup specGroup);
    int update(SpecGroup specGroup);
    String getGroupCode(Map params);
    Long countByGroupId(Map params);
}
