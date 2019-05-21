package erp.chain.mapper;

import erp.chain.domain.DietPromotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietPromotionMapper extends BaseMapper {
    List<DietPromotion> dietPromotionList(Map params);
    List<DietPromotion> getDietPromotionList(Map params);
    int update(DietPromotion dietPromotion);
    int insert(DietPromotion dietPromotion);
    DietPromotion findDietPromotionById(Map params);
    List<DietPromotion> dietPromotionFestivalList(Map params);
    List<DietPromotion> findAllByTenantIdAndPromotionType(@Param("tenantId") BigInteger tenantId, @Param("promotionType")  Integer promotionType);
}