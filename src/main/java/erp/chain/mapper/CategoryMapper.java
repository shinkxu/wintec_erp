package erp.chain.mapper;


import erp.chain.domain.Category;
import net.sf.json.JSONArray;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper extends BaseMapper {
    List<Map> queryCatList(Map params);
    Long queryUnitCount(Map params);
    Category queryCatById(Map params);
    int update(Category category);
    /*修改子分类的上级分类名*/
    int updateSubName(@Param("tenantId") BigInteger tenantId,@Param("parentId") BigInteger parentId, @Param("parentName") String parentName);
    int insert(Category category);
    Long checkName(Map params);
    String getFirstCatCode(Map params);
    String getSecondCatCode(Map params);
    int countByCategoryIdAndTenantId(@Param("catId")BigInteger catId,@Param("tenantId") BigInteger tenantId);
    int countByParentIdAndTenantId(@Param("catId")BigInteger catId,@Param("tenantId") BigInteger tenantId);
    List<Category> queryCatTreeData(Map params);
    List<Category> queryCats(Map params);
    List<Category> getCatsGoodsUsed(@Param("tenantId") BigInteger tenantId);
    Category findByTenantIdAndIsDeleted(@Param("tenantId") BigInteger tenantId);
    Category getByName(@Param("tenantId") BigInteger tenantId,@Param("catName") String catName);
    int deleteCategoryOrder(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("catJsonArr") JSONArray catJsonArr);
    int updateLastUpdateAt(@Param("catJsonArr") JSONArray catJsonArr);
    int insertCatOrder(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId,@Param("catJsonArr") JSONArray catJsonArr);
    List<Map> listOrderCategory(@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId, @Param("tenantBusiness") String tenantBusiness);
    List<Map> listOrderSecondCategory(@Param("firstCatId") BigInteger firstCatId,@Param("tenantId") BigInteger tenantId,@Param("branchId") BigInteger branchId);
    Map getCategoryScoreInfo(@Param("catId") BigInteger catId,@Param("tenantId") BigInteger tenantId);

    int deleteCategoryScore(@Param("tenantId") BigInteger tenantId, @Param("categoryId") BigInteger categoryId);
    int saveCategoryScore(Map params);

    List<Map<String, Object>> findAll(@Param("tenantId") BigInteger tenantId, @Param("tenantBusiness") String tenantBusiness, @Param("branchId")BigInteger branchId);
    //根据父类的POS销售中隐藏 修改子类的POS销售中隐藏的状态
    int doUpdatePosStatus(Category category);
    List<Category> getSecondByParentId(@Param("tenantId")BigInteger tenantId,@Param("parentId")BigInteger parentId);
    BigInteger getCatOrderId(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,@Param("catId")BigInteger catId);
    List<Map> orderedCategory(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
}