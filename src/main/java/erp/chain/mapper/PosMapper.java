package erp.chain.mapper;

import erp.chain.domain.Pos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/10/31.
 */
@Mapper
public interface PosMapper extends BaseMapper {
    public Pos find(Map<String, Object> params);
    int updatePosBranchNameByBranchId(@Param("branchId") BigInteger branchId, @Param("branchName") String branchName);

    /**后台使用接口*/
    List<Map> queryPosList(Map params);
    Long queryPosListSum(Map params);
    Pos queryPosById(Map params);
    int insert(Pos pos);
    int update(Pos pos);
    int clearInfo(Pos pos);
    String getMaxPosCode(@Param("tenantId") BigInteger tenantId);
    Pos findByTenantIdAndBranchIdAndId(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId,@Param("posId")BigInteger posId);
    List<Pos> findByDeviceCode(@Param("deviceCode")String deviceCode);
    Pos findPosByTenantIdAndDeviceCode(@Param("deviceCode")String deviceCode,@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    Pos findNoUsedPos(@Param("tenantId")BigInteger tenantId,@Param("branchId")BigInteger branchId);
    void clearSaleData(Map params);
    void clearSaleDetailData(Map params);
    void clearSalePaymentData(Map params);
    List<Pos> getDeviceCodes(@Param("tenantId")BigInteger tenantId);


    List<Map> findList(Map<String,String> params);
    long findListSum(Map<String,String> params);
}
