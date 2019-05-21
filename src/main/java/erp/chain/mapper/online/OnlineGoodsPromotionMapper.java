package erp.chain.mapper.online;

import erp.chain.domain.DietPromotion;
import erp.chain.domain.online.OnlineGoodsPromotion;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-04-10.
 */
@Mapper
public interface OnlineGoodsPromotionMapper {
    OnlineGoodsPromotion find(SearchModel searchModel);

    List<OnlineGoodsPromotion> findAll(SearchModel searchModel);

    List<OnlineGoodsPromotion> findAllOnlineGoodsPromotions(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("scope") Integer scope, @Param("forCustomerType") Integer forCustomerType);

    DietPromotion findEffectiveDietPromotionTotalReduce(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("scope") Integer scopes, @Param("forCustomerType") Integer forCustomerType);
}
