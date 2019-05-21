package erp.chain.mapper.o2o;

import erp.chain.domain.o2o.PrepaidCardAccount;
import erp.chain.domain.o2o.PrepaidCardInfo;
import erp.chain.mapper.BaseMapper;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/5
 */
@Mapper
public interface PrepaidCardMapper extends BaseMapper{

    List<PrepaidCardInfo> listPreCardInfo(SearchModel searchModel);
    List<PrepaidCardInfo> listCardInfoByCodeList(@Param("cardCodes")List<String> cardCodes,@Param("tenantId")BigInteger tenantId);
    long countPreCard(SearchModel searchModel);
    int batchCreateCard(@Param("tenantId")BigInteger tenantId,
                        @Param("branchId")BigInteger branchId,
                        @Param("typeId")BigInteger typeId,
                        @Param("createBy")String createBy,
                        @Param("cardCodes")List<String> cardCodes,
                        @Param("storeAmount")BigDecimal storeAmount);
    List<String> getUsedCode(@Param("tenantId")BigInteger tenantId,
                             @Param("branchId")BigInteger branchId,
                             @Param("code")String code);
    PrepaidCardInfo getInfoById(@Param("tenantId") BigInteger tenantId,@Param("cardId") BigInteger cardId);
    PrepaidCardInfo getInfoByCode(@Param("tenantId") BigInteger tenantId,@Param("cardCode") String cardCode);
    int saveAccount(PrepaidCardAccount prepaidCardAccount);
    int updateCardAmount(PrepaidCardInfo prepaidCardInfo);
    long countAccount(Map<String,String> params);
    List<Map> prepaidCardAccountList(Map<String,String> params);
}
