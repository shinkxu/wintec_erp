package erp.chain.mapper;

import erp.chain.domain.Branch;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
@Mapper
public interface BranchMapper extends BaseMapper {
    Branch find(Map<String, Object> params);
    List<Map> findList(Map<String, String> params);
    long countList(Map<String, String> params);
    boolean updateByMap(Map<String, Object> params);
    Branch findByTenantIdAndBranchId(Map params);
    List<Map<String, Object>> queryBranchList(Map<String, Object> params);
    Long queryBranchCount(Map<String, Object> params);
    String getAreaChildLst(String areaId);
    int insert(Branch branch);
    int update(Branch branch);
    String findMaxBranchCode(@Param("tenantId")BigInteger tenantId);
    Branch findBranchByTenantIdAndBranchId(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    List<Branch> findAllByBranchTypeInListAndTenantIdAndDistributionCenterId(@Param("branchTypes") Integer[] branchTypes, @Param("tenantId") BigInteger tenantId, @Param("distributionCenterId") BigInteger distributionCenterId);
    List<Branch> findBranchByTenantId(Map<String, Object> params);
    BigInteger initBranchPayment(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    Branch getMainBranch(@Param("tenantId")String tenantId);
    List<Branch> getChildBranches(@Param("tenantId")BigInteger tenantId);
    Branch findByTenantIdAndIdAndShopId(@Param("tenantId") BigInteger tenantId,
                                        @Param("branchId") BigInteger branchId,
                                        @Param("shopId") BigInteger shopId);
    int clearElemeBindingRestaurant(@Param("shopId") BigInteger shopId, @Param("branchId") BigInteger branchId);
    Branch findByShopId(@Param("shopId") BigInteger shopId);
    List<Map<String, Object>> listBranches(@Param("queryStr") String queryStr,
                                           @Param("isEffective")String isEffective,
                                           @Param("startDate") String startDate,
                                           @Param("endDate") String endDate,
                                           @Param("sqlOperationSymbol") String sqlOperationSymbol,
                                           @Param("branchIds") List<BigInteger> branchIds,
                                           @Param("tenantIds") List<BigInteger> tenantIds,
                                           @Param("offset") Integer offset,
                                           @Param("maxResult") Integer maxResult);
    int count(@Param("queryStr") String queryStr,
              @Param("isEffective")String isEffective,
              @Param("startDate") String startDate,
              @Param("endDate") String endDate,
              @Param("sqlOperationSymbol") String sqlOperationSymbol,
              @Param("branchIds") List<BigInteger> branchIds,
              @Param("tenantIds") List<BigInteger> tenantIds);
    Map<String, Object> findBranchInfoById(@Param("branchId") BigInteger branchId);
    List<Map<String, Object>> statisticsBranchCount(@Param("tenantIds") List<BigInteger> tenantIds);
    Branch findHeadquartersBranch(@Param("tenantId") BigInteger tenantId);
    List<Map<String, Object>> statisticsAgentBranchCount(@Param("tenantIds") List<BigInteger> tenantIds,
                                                         @Param("yesterdayStartTime") Date yesterdayStartTime,
                                                         @Param("yesterdayEndTime") Date yesterdayEndTime,
                                                         @Param("thisMonthStartTime") Date thisMonthStartTime,
                                                         @Param("thisMonthEndTime") Date thisMonthEndTime);
    List<Branch> getAllBranches();
    List<Map<String, Object>> findAllByTenantId(@Param("tenantId") BigInteger tenantId);

    Long isDistributionCenterSum(Map params);
    List<Map> statisticsBranches(@Param("tenantIds") List<BigInteger> tenantIds,
                                 @Param("branchIds") List<BigInteger> branchIds);
    List<Branch> findAll(SearchModel searchModel);
    int updateBranch (Map params);

    int checkIsHasMall(Map params);

    String getChildBranchIds(Map params);
    int removeBranch(Map<String,String> params);
    List<Branch> findChildBranchList(Map<String,Object> params);
}
