package erp.chain.mapper.system;

import erp.chain.domain.system.BranchComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 店铺评价
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2019/1/17
 */
@Mapper
public interface BranchCommentMapper{
    int insertComment(BranchComment branchComment);
    int updateComment(BranchComment branchComment);
    List<Map> getBranchComment(Map<String,String> params);
    long countBranchComment(Map<String,String> params);
    BranchComment getCommentById(@Param("commentId")String commentId,@Param("tenantId")String tenantId);
    Map getBranchScore(@Param("branchId")BigInteger branchId, @Param("tenantId")BigInteger tenantId);
}
