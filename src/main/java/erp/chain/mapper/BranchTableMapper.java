package erp.chain.mapper;


import erp.chain.domain.BranchTable;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BranchTableMapper extends BaseMapper {
    List<Map> queryTableList(Map params);
    Long queryTableListSum(Map params);
    Long checkName(Map params);
    Long checkCode(Map params);
    BranchTable getTableById(Map params);
    int insert(BranchTable branchTable);
    int update(BranchTable branchTable);
    String getTableCode(Map params);
}