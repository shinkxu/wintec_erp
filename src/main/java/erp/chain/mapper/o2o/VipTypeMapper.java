package erp.chain.mapper.o2o;


import erp.chain.domain.o2o.BranchDiscount;
import erp.chain.domain.o2o.VipSpecialPromotion;
import erp.chain.domain.o2o.VipType;
import erp.chain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface VipTypeMapper extends BaseMapper {
    List<VipType> vipTypeList(Map params);
    List<VipType> vipTypeListAuto(Map params);
    int insert(Map params);
    int insert(VipType vipType);
    int update(Map params);
    int update(VipType vipType);
    VipType findTypeById(@Param("tenantId")BigInteger tenantId,@Param("id")BigInteger typeId);
    VipType findByCondition(Map params);
    VipType queryIntegralRuleByBranch(Map params);
    VipType queryIntegralRule(@Param("tenantId")BigInteger tenantId);
    VipType findByTenantIdAndTypeName(@Param("tenantId")BigInteger tenantId, @Param("typeName")String typeName);
    List<VipType> getVipTypeCode(@Param("tId")BigInteger tenantId);
    int insertVipSpecialPromotion(VipSpecialPromotion vipSpecialPromotion);
    int updateVipSpecialPromotion(Map params);
    VipSpecialPromotion findSpecialPromotionByTenantIdAndType(Map params);
    Map findSpecialPromotion(Map params);
    List<VipSpecialPromotion> findSpecialPromotions(Map params);
    VipType getWechatVipType(@Param("tenantId")String tenantId,@Param("branchId")String branchId,@Param("isOnline")Integer isOnline);

    int setBranchDiscount(BranchDiscount branchDiscount);
    int updateBranchDiscount(BranchDiscount branchDiscount);
    BranchDiscount findBranchDiscount(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("id") BigInteger id);
    BranchDiscount findBranchDiscountByTypeId(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("typeId") BigInteger typeId);
    int deleteBranchDiscountByType(BranchDiscount branchDiscount);
}