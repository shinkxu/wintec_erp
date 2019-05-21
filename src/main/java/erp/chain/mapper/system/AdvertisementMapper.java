package erp.chain.mapper.system;

import erp.chain.domain.system.AdBanner;
import erp.chain.domain.system.AdContent;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/11/2
 */
@Mapper
public interface AdvertisementMapper extends BaseMapper{
    List<AdBanner> listAdBanner(Map<String, String> params);

    List<Map> listAdContent(Map<String, String> params);

    long countAdContentListByBannerId(@Param("bannerId") String bannerId, @Param("tenantId") String tenantId);

    long countListAdBanner(Map<String, String> params);

    long countListAdContent(Map<String, String> params);

    AdBanner findBannerById(@Param("bannerId") String bannerId, @Param("tenantId") String tenantId, @Param("branchId") String branchId);

    Map findContentById(@Param("contentId") String contentId, @Param("tenantId") String tenantId, @Param("branchId") String branchId);

    AdContent findContent(@Param("contentId") String contentId, @Param("tenantId") String tenantId, @Param("branchId") String branchId);

    void deleteBanner(@Param("bannerId") String bannerId, @Param("tenantId") String tenantId, @Param("branchId") String branchId, @Param("empId") String empId);

    void deleteContent(@Param("contentId") String contentId, @Param("tenantId") String tenantId, @Param("branchId") String branchId, @Param("empId") String empId);

    void insertBanner(AdBanner adBanner);

    void updateBanner(AdBanner adBanner);

    void insertContent(AdContent adContent);

    void updateContent(AdContent adContent);

    void deleteContentBranchR(@Param("contentId") BigInteger contentId, @Param("tenantId") BigInteger tenantId);

    void insertContentBranchR(@Param("branchIds") List<String> branchIds, @Param("contentId") BigInteger contentId, @Param("tenantId") BigInteger tenantId);

    List<Map> contentBranchR(@Param("contentId") BigInteger contentId, @Param("tenantId") BigInteger tenantId);

    List<Map> loadBranchAdContent(@Param("branchId") String branchId, @Param("tenantId") String tenantId);
}
