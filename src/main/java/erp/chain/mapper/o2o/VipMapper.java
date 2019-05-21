package erp.chain.mapper.o2o;


import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.VipVo;
import erp.chain.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface VipMapper{
    int insert(Map params);
    List<Vip> select(Map<String,Object> params);
    Vip findByCondition(Map params);
    Vip findById(Map params);
    VipVo findVoById(Map params);
    List<VipVo> findVoByCondition(Map params);
    List<VipVo> findImportVoByCondition(Map params);
    List<Map> findImportVoByConditionSum(Map params);
    int update(Map params);
    int update(Vip vip);
    int updateImport(Map params);
    int insert2(Vip vip);
    List<Map> queryVipNoPage(Map params);
    List<Map> listVipTrade(Map params);
    List<Map> listVipStore(Map params);
    List<VipVo> findByFhoneOrCode(Map params);
    List<WxPosCode> findAllByVipIdAndCode(@Param("vipId")BigInteger vipId,@Param("code")String code);
    int insertVipImport(Map params);
    List<Vip> selectVipImport(Map params);
    Vip findByIdAndTenantIdAndBranchId(@Param("id") BigInteger id, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    /*会员标签*/
    List<Map> queryLabelList(Map params);
    Long queryLabelSum(Map params);
    int addLabel(VipLabel vipLabel);
    int delLabel(VipLabel vipLabel);
    String getLabelCode(@Param("groupId") BigInteger groupId, @Param("tenantId") BigInteger tenantId);
    Long isLabelNameUsed(@Param("labelName") String labelName, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    VipLabel findLabelById(@Param("id") BigInteger id, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    String labelIsUsed(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    /*会员信息用查询*/
    String queryNamesByIds(Map params);
    List<Map> queryLabelAndGroup(Map params);

    /*会员标签组*/
    List<Map> queryLabelGroup(Map params);
    List<Map> queryLabelGroupTable(Map params);
    Long queryLabelGroupSum(Map params);
    Long isGroupNameUsed(@Param("groupName") String groupName, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    int addLabelGroup(LabelGroup labelGroup);
    int updateLabelGroup(LabelGroup labelGroup);
    LabelGroup findLabelGroupById(@Param("id") BigInteger id, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    Long queryLabelByGroupId(@Param("groupId") BigInteger groupId, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
    String getGroupCode(Map params);
    List<LabelGroup> queryLabelGroupTree(Map params);
    int updateVipLabel(Map params);
    String minGroupId(Map params);
    int labelCount(Map params);
    List useLabelVip(Map params);

    Vip find(SearchModel searchModel);
    List<Vip> findAll(SearchModel searchModel);

    List<Map> findVoByConditionSum(Map params);


    List<Map> miyaVipFind(@Param("tenantId")String tenantId,@Param("mobile")String mobile);
    Map miyaVipFindOne(@Param("tenantId")String tenantId,@Param("vipId")BigInteger vipId);

    List<String> queryRegisteredPhone(Map params);
    List<String> queryImportedPhone(Map params);
    List<String> queryRegisteredCardCode(Map params);
    List<String> queryImportedCardCode(Map params);


    int importVipList(@Param("vipsList")List<Vip> vipsList);

    List<Map> isInVipStatement(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("vipId") BigInteger vipId);
    BigDecimal queryVipStoreAmount(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId, @Param("statementDate") String statementDate);
    BigDecimal queryVipPayAmount(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId, @Param("statementDate") String statementDate);
    BigDecimal queryVipDrawAmount(@Param("tenantId") BigInteger tenantId, @Param("vipId") BigInteger vipId, @Param("statementDate") String statementDate);
    int insertVipStatement(VipStatement vipStatement);
    List<Vip> selectVipIncludeDeleted(Map params);

    List<Map> queryVipForBS(Map params);
    List<Map> queryVipStoreForBS(Map params);
    List<Map> queryVipTradeForBS(Map params);

    /*判断商户下是否有手机号重复会员*/
    int isHasRepeatPhoneOfVip(Map params);
    /*判断商户下是否有卡号（卡面号）重复*/
    int isHasRepeatCardCode(Map params);

    Map branchDiscount(Map params);

}