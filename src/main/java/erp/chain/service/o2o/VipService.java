package erp.chain.service.o2o;

import com.saas.common.Constants;
import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.*;
import erp.chain.domain.o2o.*;
import erp.chain.domain.o2o.vo.VipScoreVO;
import erp.chain.domain.o2o.vo.VipVo;
import erp.chain.domain.system.SysConfig;
import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.*;
import erp.chain.mapper.o2o.*;
import erp.chain.mapper.report.ReportMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.service.BranchService;
import erp.chain.service.IcService;
import erp.chain.service.system.SmsService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;
import saas.api.util.ApiBaseServiceUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangms on 2017/1/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipService{
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipTypeMapper vipTypeMapper;
    @Autowired
    private VipStoreRuleMapper vipStoreRuleMapper;
    @Autowired
    private VipStoreRuleDetailMapper vipStoreRuleDetailMapper;
    @Autowired
    private VipAddressMapper vipAddressMapper;
    @Autowired
    private VipBookMapper vipBookMapper;
    @Autowired
    private VipStoreHistoryMapper vipStoreHistoryMapper;
    @Autowired
    private VipTradeHistoryMapper vipTradeHistoryMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private DietOrderInfoMapper dietOrderInfoMapper;
    @Autowired
    private PosStoreOrderMapper posStoreOrderMapper;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private CardToVipMapper cardToVipMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private BranchService branchService;
    @Autowired
    private DietPromotionFestivalMapper dietPromotionFestivalMapper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private IcService icService;
    @Autowired
    private CardCouponsService cardCouponsService;
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 查询会员列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipList(Map params) throws ServiceException, ParseException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        //查询会员分组
        String loginBId = null;
        if(params.get("branchId") != null && params.get("tenantId") != null && !"0".equals(params.get("branchType"))){
            loginBId=params.get("branchId").toString();
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("groups", bIds);
                }
            }
        }
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<VipVo> vipList = vipMapper.findVoByCondition(params);
        for(VipVo vipVo:vipList){
            Map<String, Object> map0 = new HashMap<>();
            map0.put("vipId", vipVo.getId());
            map0.put("tenantId", vipVo.getTenantId());
            if(loginBId==null){
                map0.put("branchId",vipVo.getBranchId());
            }else{
                map0.put("branchId",loginBId);
            }
            List<CardToVip> cardToVips = cardToVipMapper.listCardToVip(map0);
            if(cardToVips != null){
                vipVo.setCardCouponsCount(cardToVips.size());
            }
            map0.put("cardKind", 3);
            List<CardAccount> accounts = cardMapper.listCardAccounts(map0);
            if(accounts != null){
                vipVo.setTimesCardCount(accounts.size());
            }
        }
        List<Map> footer = vipMapper.findVoByConditionSum(params);
        map.put("total", footer.get(0).get("total"));
        map.put("rows", vipList);
        map.put("footer", footer);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 查询会员列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipListForMarketing(Map params) throws ServiceException, ParseException{
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        //查询会员分组
        String loginBId = null;
        if(params.get("branchId") != null && params.get("tenantId") != null && !"0".equals(params.get("branchType"))){
            loginBId=params.get("branchId").toString();
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("groups", bIds);
                }
            }
        }
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        if(params.get("sumConsume1") != null && Double.parseDouble(params.get("sumConsume1").toString()) <= 0){
            params.put("sumConsume1Z", "1");
        }
        if(params.get("vipStore1") != null && Double.parseDouble(params.get("vipStore1").toString()) <= 0){
            params.put("vipStore1Z", "1");
        }
        if(params.get("remainingScore1") != null && Double.parseDouble(params.get("remainingScore1").toString()) <= 0){
            params.put("remainingScore1Z", "1");
        }
        List<VipVo> countList = new ArrayList<>();
        if(params.get("labelId") != null && !"".equals(params.get("params"))){
            String[] ids = params.get("labelId").toString().split(",");
            List<VipVo> newList;
            params.put("labelId", null);
            for(String id : ids){
                params.put("labelId", id);
                newList = vipMapper.findVoByCondition(params);
                countList.addAll(newList);
            }
            for(int i = 0; i < countList.size(); i++){
                for(int j = countList.size() - 1; j > i; j--){
                    VipVo vo1 = countList.get(i);
                    VipVo vo2 = countList.get(j);
                    if(vo1.getId().compareTo(vo2.getId()) == 0){
                        countList.remove(j);
                    }
                }
            }
        }
        else{
            countList = vipMapper.findVoByCondition(params);
        }
        List<Map> footer = vipMapper.findVoByConditionSum(params);
        int total = 0;
        if(countList != null){
            total = countList.size();
        }
        int indexStart = 0;
        int indexEnd = 100;
        if(params.get("page") != null && params.get("rows") != null){
            indexStart = (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString());
            indexEnd = indexStart + Integer.parseInt(params.get("rows").toString());
        }
        String birth1 = "";
        String birth2 = "";
        if(params.get("month1") != null && params.get("day1") != null){
            if(Integer.parseInt(params.get("month1").toString()) < 10){
                birth1 += "0" + params.get("month1");
            }
            else{
                birth1 += params.get("month1");
            }
            if(Integer.parseInt(params.get("day1").toString()) < 10){
                birth1 += "-0" + params.get("day1");
            }
            else{
                birth1 += "-" + params.get("day1");
            }
        }
        if(params.get("month2") != null && params.get("day2") != null){
            if(Integer.parseInt(params.get("month2").toString()) < 10){
                birth2 += "0" + params.get("month2");
            }
            else{
                birth2 += params.get("month2");
            }
            if(Integer.parseInt(params.get("day2").toString()) < 10){
                birth2 += "-0" + params.get("day2");
            }
            else{
                birth2 += "-" + params.get("day2");
            }
        }
        List<VipVo> vipList = new ArrayList<>();
        int addCount = 0;
        if(countList != null){
            for(VipVo vipVo : countList){
                int days = 0;
                if(vipVo.getSumConsume() != null && vipVo.getSumConsume().doubleValue() > 0){
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("vipId", vipVo.getId());
                    map1.put("tenantId", vipVo.getTenantId());
                    List<VipTradeHistory> histories = vipTradeHistoryMapper.vipTradeList(map1);
                    if(histories != null && histories.size() > 0 && histories.get(histories.size() - 1).getCreateAt() != null && !"".equals(histories.get(histories.size() - 1).getCreateAt())){
                        days = getDays(histories.get(histories.size() - 1).getCreateAt(), new Date());
                    }
                }
                else{
                    days = getDays(vipVo.getCreateAt(), new Date());
                }
                vipVo.setUnConsumeDays(days);

                if(params.get("unConsumeDays1") != null){
                    if(Integer.parseInt(params.get("unConsumeDays1").toString()) > days){
                        total--;
                        continue;
                    }
                }
                if(params.get("unConsumeDays2") != null){
                    if(Integer.parseInt(params.get("unConsumeDays2").toString()) < days){
                        total--;
                        continue;
                    }
                }
                if(!"".equals(birth1)){
                    if(vipVo.getBirthday() != null){
                        Date birthd1 = format.parse(birth1);
                        Date birth = format.parse(format.format(vipVo.getBirthday()));
                        if(birthd1.compareTo(birth) > 0){
                            total--;
                            continue;
                        }
                    }
                    else{
                        total--;
                        continue;
                    }
                }
                if(!"".equals(birth2)){
                    if(vipVo.getBirthday() != null){
                        Date birthd2 = format.parse(birth2);
                        Date birth = format.parse(format.format(vipVo.getBirthday()));
                        if(birthd2.compareTo(birth) < 0){
                            total--;
                            continue;
                        }
                    }
                    else{
                        total--;
                        continue;
                    }
                }
                /*会员标签*/
                if(vipVo.getLabelId() != null && !"".equals(vipVo.getLabelId())){
                    String labelIds = vipVo.getLabelId();
                    params.put("branchId", vipVo.getBranchId());
                    params.put("ids", labelIds);
                    String labelNames = vipMapper.queryNamesByIds(params);
                    vipVo.setLabelName(labelNames);
                }

                if(addCount >= indexStart){
                    if(vipList.size() < indexEnd - indexStart){
                        Map<String, Object> map0 = new HashMap<>();
                        map0.put("vipId", vipVo.getId());
                        map0.put("tenantId", vipVo.getTenantId());
                        if(loginBId==null){
                            map0.put("branchId",vipVo.getBranchId());
                        }else{
                            map0.put("branchId",loginBId);
                        }
                        List<CardToVip> cardToVips = cardToVipMapper.listCardToVip(map0);
                        if(cardToVips != null){
                            vipVo.setCardCouponsCount(cardToVips.size());
                        }
                        map0.put("cardKind", 3);
                        List<CardAccount> accounts = cardMapper.listCardAccounts(map0);
                        if(accounts != null){
                            vipVo.setTimesCardCount(accounts.size());
                        }
                        vipList.add(vipVo);
                    }
                    else{
//                           break;
                    }
                }
                else{
                    addCount++;
                }
            }
        }
        map.put("total", total);
        map.put("rows", vipList);
        map.put("footer", footer);
        result.setJsonMap(map);
        return result;
    }

    private int getDays(Date date1, Date date2){
        return (int)((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    /**
     * 查询会员列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipImportList(Map params) throws ServiceException{
        //查询会员分组
        /*if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("groups", bIds);
                }
            }
        }*/
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        List<VipVo> countList = vipMapper.findImportVoByCondition(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        List<VipVo> vipList = vipMapper.findImportVoByCondition(params);
        List<Map> footer = vipMapper.findImportVoByConditionSum(params);
        int total = 0;
        if(countList != null){
            total = countList.size();
        }
        map.put("total", total);
        map.put("rows", vipList);
        map.put("footer", footer);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 查询该商户下已注册手机号
     * */
    public ApiRest queryRegisteredPhone(Map params){
        ApiRest apiRest = new ApiRest();
        //查询会员分组
        Map<String, Object> param0 = new HashMap<>();
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            param0.put("branchId", params.get("branchId"));
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                Map<String, Object> paramZ = new HashMap<>();
                paramZ.put("tenantId", params.get("tenantId"));
                paramZ.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    param0.remove("branchId");
                    param0.put("groups", bIds);
                }
            }
        }
        param0.put("tenantId", params.get("tenantId"));
        List<String> phoneList = vipMapper.queryRegisteredPhone(param0);
        List<String> importPhone = vipMapper.queryImportedPhone(param0);
        List<String> cardCodeList = vipMapper.queryRegisteredCardCode(param0);
        List<String> importCardCode = vipMapper.queryImportedCardCode(param0);
        Map map = new HashMap();
        map.put("registeredPhone", phoneList);
        map.put("importedPhone", importPhone);
        map.put("cardCodeList", cardCodeList);
        map.put("importCardCode", importCardCode);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询已注册手机号成功");
        return apiRest;
    }

    /**
     * 保存会员导入数据
     * */
    public ApiRest saveVipsImport(Map params){
        ApiRest apiRest = new ApiRest();
        ApiRest apiRest1 = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        Map tenant = (Map)((Map) apiRest1.getData()).get("tenant");
        int total;
        DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        if(params.get("vipList") != null){
            //查询总部机构

            Map<String, Object> param = new HashMap<>();
            param.put("branchType", 0);
            param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
            Branch branch = branchMapper.find(param);
            String vipListJson=ZipUtils.unzipText(params.get("vipList").toString());
            List<Map> vipList = GsonUntil.jsonAsList(vipListJson, Map.class);
            if(vipList != null && vipList.size() > 0){
                total = vipList.size();
                List<Vip> vipsList = new ArrayList<>();
                for(int i = 0; i < vipList.size(); i++){
                    Map vipMap = vipList.get(i);
                    Vip vip = new Vip();
                    if(vipMap.get("vipName") != null){
                        vip.setVipName(vipMap.get("vipName").toString());
                    }
                    vip.setSex("男".equals(vipMap.get("sex").toString()) == true ? 1 : 2);
                    if(vipMap.get("birthday") != null && !"".equals(vipMap.get("birthday"))){
                        try{
                            vip.setBirthday(format1.parse(vipMap.get("birthday").toString()));
                        }catch (Exception e){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("生日格式错误");
                        }
                    }else{
                        vip.setBirthday(new Date());
                    }
                    if(vipMap.get("phone") != null){
                        vip.setPhone(vipMap.get("phone").toString());
                    }
                    if(vipMap.get("email") != null){
                        vip.setEmail(vipMap.get("email").toString());
                    }
                    if(vipMap.get("vipStoreTotal") != null){
                        vip.setVipStoreTotal(BigDecimal.valueOf(Double.parseDouble(vipMap.get("vipStoreTotal").toString())));
                    }else{
                        vip.setVipStoreTotal(BigDecimal.ZERO);
                    }
                    if(vipMap.get("vipStore") != null){
                        vip.setVipStore(BigDecimal.valueOf(Double.parseDouble(vipMap.get("vipStore").toString())));
                    }else{
                        vip.setVipStore(BigDecimal.ZERO);
                    }
                    if(vipMap.get("remainingScore") != null){
                        vip.setRemainingScore(BigDecimal.valueOf(Double.parseDouble(vipMap.get("remainingScore").toString())));
                    }else{
                        vip.setRemainingScore(BigDecimal.ZERO);
                    }
                    if(vipMap.get("cardCode") != null){
                        vip.setCardCode(vipMap.get("cardCode").toString());
                    }
                    vip.setBranchId(BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())));
                    vip.setTenantId(BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString())));
                    vip.setIsDeleted(false);
                    vip.setCreateAt(new Date());
                    vip.setLastUpdateAt(new Date());
                    //查询会员类型
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                    if(tenant.get("isBranchManagementVip").toString().equals("false") || tenant.get("isBranchManagementVip").toString().equals("0")){
                        param1.put("branchId", branch.getId());
                    } else{
                        param1.put("branchId", params.get("branchId"));
                    }
                    if(vipMap.get("typeName") != null && !"".equals(vipMap.get("typeName"))){
                        param1.put("typeName", vipMap.get("typeName"));
                    } else{
                        param1.put("isOnlineDefault", 1);
                    }
                    VipType vipType = vipTypeMapper.findByCondition(param1);
                    if(vipType != null){
                        vip.setTypeId(vipType.getId());
                    } else{
                        vip.setTypeId(BigInteger.valueOf(Long.parseLong("-1")));
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                    String code = format.format(new Date()) + getNextSelValueNew(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()),i+1);
                    vip.setVipCode(code);
                    vip.setRegSource("1");
                    vip.setStatus(0);
                    if(params.get("userName") != null){
                        vip.setCreateBy(params.get("userName").toString());
                        vip.setLastUpdateBy(params.get("userName").toString());
                    }
                    vipsList.add(vip);
                }
                int count = vipMapper.importVipList(vipsList);
                if(count > 0){
                    addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()), count);
                    apiRest.setIsSuccess(true);
                    String messageS = "导入完毕！共导入" + count + "条数据，成功" + count + "条,失败" + (total - count) + "条";
                    apiRest.setMessage(messageS);
                }else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("导入失败，请检查数据是否正确");
                }
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("可导入数据为空");
            }
        }else{
            apiRest.setIsSuccess(false);
            apiRest.setError("可导入数据为空");
        }
        return apiRest;
    }

    /**
     * 保存会员导入数据
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest saveVipImport(Map params){
        ApiRest rest = new ApiRest();
        ApiRest rest1 = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        Map tenant = (Map)((Map)rest1.getData()).get("tenant");
        int count = 0;
        int errorCount = 0;
        if(params.get("vipList") != null && params.get("branchId") != null){
            //查询总部机构
            Map<String, Object> param = new HashMap<>();
            param.put("branchType", 0);
            param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
            Branch branch = branchMapper.find(param);
            List<Map> vipList = GsonUntil.jsonAsList(params.get("vipList").toString(), Map.class);
            if(vipList != null && vipList.size() > 0){
                for(int i = 0; i < vipList.size(); i++){
                    try{
                        Map vip = vipList.get(i);
                        if(vip.get("remainingScore") != null){
                            vip.put("sumScore", vip.get("remainingScore"));
                        }
                        vip.put("branchId", params.get("branchId"));
                        vip.put("tenantId", params.get("tenantId"));
                        vip.put("isDeleted", 0);
                        vip.put("createAt", new Date());
                        vip.put("lastUpdateAt", new Date());
                        //查询会员类型
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                        if(tenant.get("isBranchManagementVip").toString().equals("false") || tenant.get("isBranchManagementVip").toString().equals("0")){
                            param1.put("branchId", branch.getId());
                        }
                        else{
                            param1.put("branchId", params.get("branchId"));
                        }
                        if(vip.get("typeName") != null && !"".equals(vip.get("typeName"))){
                            param1.put("typeName", vip.get("typeName"));
                        }
                        else{
                            param1.put("isOnlineDefault", 1);
                        }
                        VipType vipType = vipTypeMapper.findByCondition(param1);
                        if(vipType != null){
                            vip.put("typeId", vipType.getId());
                        }
                        else{
                            vip.put("typeId", -1);
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                        String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                        vip.put("vipCode", code);
                        vip.put("regSource", 1);
                        vip.put("status", 0);
                        if(params.get("userName") != null){
                            vip.put("createBy", params.get("userName"));
                        }
                        if(vip.get("phone") != null && !"".equals(vip.get("phone"))){
                            //查询会员分组
                            Map<String, Object> param0 = new HashMap<>();
                            if(params.get("branchId") != null && params.get("tenantId") != null){
                                param0.put("branchId", params.get("branchId"));
                                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                                    Map<String, Object> paramZ = new HashMap<>();
                                    paramZ.put("tenantId", params.get("tenantId"));
                                    paramZ.put("groupCode", branch.getGroupCode());
                                    List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                                    if(branches != null && branches.size() > 0){
                                        String bIds = "";
                                        for(Branch b : branches){
                                            bIds += b.getId() + ",";
                                        }
                                        bIds = bIds.substring(0, bIds.length() - 1);
                                        param0.remove("branchId");
                                        param0.put("groups", bIds);
                                    }
                                }
                            }
                            param0.put("tenantId", params.get("tenantId"));
                            param0.put("phone", vip.get("phone"));
                            List<Vip> list = vipMapper.select(param0);
                            if(list != null && list.size() > 0){
                                rest.setIsSuccess(false);
                                rest.setError("存在已注册手机号，导入失败");
                                return rest;
                            }
                            List<Vip> list1 = vipMapper.selectVipImport(param0);
                            if(list1 != null && list1.size() > 0){
                                rest.setIsSuccess(false);
                                rest.setError("请勿重复导入");
                                return rest;
                            }
                        }
                        //卡号重复跳过
                        if(vip.get("cardCode") != null && !"".equals(vip.get("cardCode"))){
                            Map<String, Object> param0 = new HashMap<>();
                            param0.put("tenantId", params.get("tenantId"));
                            param0.put("code", vip.get("cardCode"));
                            param0.put("branchId", params.get("branchId"));
                            Card checkCard = icService.findCardByGroup(param0);
                            if(checkCard != null){
                                rest.setIsSuccess(false);
                                rest.setError("存在已注册卡号，导入失败");
                                return rest;
                            }
                        }
                        int flag = vipMapper.insertVipImport(vip);
                        if(flag == 1){
                            addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()));
                            count++;
                        }
                    }
                    catch(Exception e){
                        //某一行导入失败，则记录失败个数!
                        errorCount++;
                        LogUtil.logError(e, params);
                    }
                }
            }
        }
        rest.setIsSuccess(true);
        if("0".equals(params.get("allNum"))){
            rest.setMessage("文件数据为空");
            rest.setError("文件数据为空");
        }
        else{
            String messageS = "导入完毕！共导入" + params.get("allNum") + "条数据，成功" + count + "条,失败" + errorCount + "条";
            if(errorCount > 0){
                messageS += "，请检查会员姓名、性别、联系电话是否为空";
            }
            rest.setMessage(messageS);
            rest.setError(messageS);
        }
        return rest;
    }

    /**
     * 删除会员导入数据
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON delVipImportById(Map params){
        ResultJSON result = new ResultJSON();
        if(params.get("ids") != null && !"".equals(params.get("ids"))){
            String[] ids = params.get("ids").toString().split(",");
            Map<String, Object> map2 = new HashMap<>();
            map2.put("tenantId", params.get("tenantId"));
            map2.put("isDeleted", 1);
            for(int i = 0; i < ids.length; i++){
                map2.put("id", ids[i]);
                vipMapper.updateImport(map2);
            }
        }
        result.setSuccess("0");
        result.setMsg("会员删除成功");
        return result;
    }

    /**
     * 保存会员导入数据
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest confirmImport(Map params){
        ApiRest rest = new ApiRest();
        List<Vip> vipImports = vipMapper.selectVipImport(params);
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        map.put("type", 2);
        List<Card> cards = cardMapper.select(map);
        int count = 0;
        /* if(cards != null && cards.size() > 0){*/
        if(vipImports != null){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tenantId", params.get("tenantId"));
            map1.put("branchId", params.get("branchId"));
            Branch branch = branchMapper.findByTenantIdAndBranchId(map1);
            if(branch == null){
                rest.setIsSuccess(false);
                rest.setError("查询机构失败！");
                return rest;
            }
            for(Vip vip : vipImports){
                //手机号重复则跳过
                if(vip.getPhone() != null && !"".equals(vip.getPhone())){
                    //查询会员分组
                    Map<String, Object> param0 = new HashMap<>();
                    if(params.get("branchId") != null && params.get("tenantId") != null){
                        param0.put("branchId", params.get("branchId"));
                        if(branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                            Map<String, Object> paramZ = new HashMap<>();
                            paramZ.put("tenantId", params.get("tenantId"));
                            paramZ.put("groupCode", branch.getGroupCode());
                            List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                            if(branches != null && branches.size() > 0){
                                String bIds = "";
                                for(Branch b : branches){
                                    bIds += b.getId() + ",";
                                }
                                bIds = bIds.substring(0, bIds.length() - 1);
                                param0.remove("branchId");
                                param0.put("groups", bIds);
                            }
                        }
                    }
                    param0.put("tenantId", params.get("tenantId"));
                    param0.put("phone", vip.getPhone());
                    List<Vip> list = vipMapper.select(param0);
                    if(list != null && list.size() > 0){
                        count++;
                        continue;
                    }
                }
                //卡号重复跳过
                if(vip.getCardCode() != null && !"".equals(vip.getCardCode())){
                    Map<String, Object> param0 = new HashMap<>();
                    param0.put("tenantId", params.get("tenantId"));
                    param0.put("code", vip.getCardCode());
                    param0.put("branchId", branch.getId());
                    Card checkCard = icService.findCardByGroup(param0);
                    if(checkCard != null){
                        count++;
                        continue;
                    }
                }
                BigInteger id = vip.getId();
                vip.setId(null);
                vip.setVipStoreTotal(vip.getVipStoreTotal() == null ? vip.getVipStore() : vip.getVipStoreTotal());
                vipMapper.insert2(vip);
                if(vip.getVipStore() != null && vip.getVipStore().doubleValue() != 0){
                    //添加储值记录
                    VipStoreHistory storeHistory = new VipStoreHistory();
                    //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
                    String currCode = tenantConfigService.getToday("CZ", 8);//"CZ" + format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, vip.getTenantId());
                    storeHistory.setVipId(vip.getId());
                    storeHistory.setStoreCode(currCode);
                    storeHistory.setStoreType("1");
                    storeHistory.setPayAmount(vip.getVipStore());
                    storeHistory.setVipOperStore(vip.getVipStore());
                    storeHistory.setGiftAmount(BigDecimal.ZERO);
                    storeHistory.setPayType("1");
                    storeHistory.setStoreBranchName(branch.getName());
                    storeHistory.setStoreBranchId(branch.getId());
                    //记录操作人
                    if(params.get("branchId") != null){
                        storeHistory.setStoreBranchId(BigInteger.valueOf(Long.valueOf(params.get("branchId").toString())));
                    }
                    else{
                        storeHistory.setStoreBranchId(vip.getBranchId());
                    }
                    storeHistory.setCreateAt(new Date());
                    storeHistory.setLastUpdateAt(new Date());
                    storeHistory.setStoreFrom("2");
                    storeHistory.setRemark("会员导入");
                    if(params.get("userName") != null){
                        storeHistory.setCreateBy(params.get("userName").toString());
                    }
                    storeHistory.setTenantId(vip.getTenantId());
                    vipStoreHistoryMapper.insert(storeHistory);
                }
                //创建卡
                if(StringUtils.isNotEmpty(vip.getCardCode())){
                    Card card = new Card();
                    card.setCode(vip.getCardCode());
                    card.setType(4);
                    card.setTenantId(new BigInteger(params.get("tenantId").toString()));
                    if(params.get("tenantCode") != null){
                        card.setTenantCode(params.get("tenantCode").toString());
                    }
                    card.setBranchCode(branch.getCode());
                    card.setAuthCardId(cards.size() == 0 ? null : cards.get(0).getId());
                    card.setHolderId(vip.getId());
                    card.setCreateAt(new Date());
                    card.setLastUpdateAt(new Date());
                    card.setBranchId(branch.getId());
                    card.setState(1);
                    if(params.get("userName") != null){
                        card.setCreateBy(params.get("userName").toString());
                    }
                    cardMapper.insertCard(card);
                }
                Map<String, Object> map2 = new HashMap<>();
                map2.put("tenantId", params.get("tenantId"));
                map2.put("id", id);
                map2.put("isDeleted", 1);
                vipMapper.updateImport(map2);
            }
        }
        rest.setIsSuccess(true);
        if(count > 0){
            rest.setMessage("导入成功！其中有" + count + "条数据手机号或卡号重复，请删除。");
        }
        else{
            rest.setMessage("导入成功！");
        }
            /*} else {
                rest.setIsSuccess(false);
                rest.setMessage("请先通过POS创建授权卡！");
            }*/
        return rest;
    }

    /**
     * 无分页查询会员列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipListNoPage(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        /*BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        ApiRest rest = SaaSApi.findTenantById(tenantId);
        if(!rest.getIsSuccess()){
            result.setIsSuccess(false);
            result.setMsg(rest.getError() == null ? rest.getError() : rest.getMessage());
            return result;
        }
        Map tenant = (Map)((Map)rest.getData()).get("tenant");
        if(tenant.get("isBranchManagementVip").toString().equals("false") || tenant.get("isBranchManagementVip").toString().equals("0")){
            params.remove("branchId");
        }*/
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.put("branchId", bIds);
                }
            }
        }
        List<Map> list = vipMapper.queryVipNoPage(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", list.size());
        result.setSuccess("0");
        result.setObject(resultMap);
        return result;
    }

    /**
     * pos新增会员
     * @param params
     * @return
     * @throws ParseException
     */
    public ApiRest saveVipForPos(Map<String, String> params) throws ParseException{
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String tenantCode = params.get("tenantCode");
        Vip vipByPhone = null;
        params.put("phone", params.get("phone") == null ? "" : params.get("phone"));
        params.put("email", params.get("email") == null ? "" : params.get("email"));
        params.put("memo", params.get("memo") == null ? "" : params.get("memo"));
        params.put("labelId", params.get("labelId") == null ? "" : params.get("labelId"));
        if(params.get("birthdayStr") != null && !params.get("birthdayStr").equals("") && !params.get("birthdayStr").equals("null")){
            params.put("birthday", params.get("birthdayStr"));
        }
        if(params.get("phone") != null && !"".equals(params.get("phone"))){
            Map<String, Object> param0 = new HashMap<>();

            //查询会员分组
            if(params.get("branchId") != null && params.get("tenantId") != null){
                param0.put("branchId", params.get("branchId"));
                Map<String, Object> paramZ = new HashMap<>();
                paramZ.put("id", params.get("branchId"));
                paramZ.put("tenantId", params.get("tenantId"));
                Branch branch = branchMapper.find(paramZ);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    paramZ.remove("id");
                    paramZ.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                    if(branches != null && branches.size() > 0){
                        String bIds = "";
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                        param0.remove("branchId");
                        param0.put("groups", bIds);
                    }
                }
            }
            param0.put("tenantId", params.get("tenantId"));
            param0.put("phone", params.get("phone"));
            List<Vip> list = vipMapper.select(param0);
            if(list != null && list.size() > 0){
                vipByPhone = list.get(0);
            }
        }
        if(params.get("id") != null && !"".equals(params.get("id"))){//修改会员信息
            if(vipByPhone != null){
                if(!vipByPhone.getId().equals(new BigInteger(params.get("id").toString()))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("该电话已经被注册");
                    return apiRest;
                }
            }
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("id"));
            Vip vips = vipMapper.findByCondition(param);
            params.remove("branchId");
            params.remove("tenantId");
            if(params.get("labelId") != null && !"".equals(params.get("labelId"))){
                int groupCount = vipMapper.labelCount(params);
                String labels[] = params.get("labelId").toString().split(",");
                if(groupCount != labels.length){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("每个标签组只允许选择一个标签");
                    return apiRest;
                }
            }
            int flag = vipMapper.update(params);
            if(flag == 1){
                apiRest.setData(vips);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("会员更新成功");
            }
        }
        else{//新增会员
            if(vipByPhone != null){
                apiRest.setIsSuccess(false);
                apiRest.setError("该电话已经被注册");
                return apiRest;
            }
            if(isInLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId")))){//检验会员上限
                Map<String, Object> param = new HashMap<>();
                param.put("branchType", 0);
                param.put("tenantId", new BigInteger(params.get("tenantId")));
                Branch branch = branchMapper.find(param);
                // 自动生成会员编号
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                if(branch != null){
                    String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                    params.put("vipCode", code);
                    if(params.get("phone") == null || "".equals(params.get("phone"))){
                        params.put("phone", "");
                    }

                    if(params.get("status") == null || "".equals(params.get("status"))){
                        params.put("status", "0");//默认是正常的
                    }
                    if(null == params.get("typeId")){
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("isOnlineDefault", 1);
                        param1.put("tenantId", params.get("tenantId"));
                        VipType vipType = vipTypeMapper.findByCondition(param1);
                        if(vipType != null){
                            params.put("typeId", vipType.getId().toString());
                        }
                        else{
                            params.put("typeId", "0");
                        }
                    }

                    if(null == params.get("branchId")){
                        params.put("branchId", branch.getId().toString());
                    }
                    if(null == params.get("regSource")){
                        params.put("regSource", "1");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    params.put("createAt", format1.format(new Date()));
                    params.put("lastUpdateAt", format1.format(new Date()));
                    params.put("isDeleted", "0");
                    if(params.get("labelId") != null && !"".equals(params.get("labelId"))){
                        int groupCount = vipMapper.labelCount(params);
                        String labels[] = params.get("labelId").toString().split(",");
                        if(groupCount != labels.length){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("每个标签组只允许选择一个标签");
                            return apiRest;
                        }
                    }
                    Vip vip = null;
                    if(StringUtils.isNotEmpty(params.get("cardCode")) && StringUtils.isNotEmpty(params.get("authCardId"))){
                        Map<String, Object> paramsMap = new HashMap<>();
                        String cardCode = params.get("cardCode");
                        String authCardId = params.get("authCardId");

                        paramsMap.put("code", authCardId);
                        paramsMap.put("tenantId",tenantId);
                        Card authCard = icService.findCard(paramsMap);
                        //判断授权卡是否有效
                        if(authCard == null || !authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH)){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("授权卡信息不匹配！");
                            return apiRest;
                        }
                        paramsMap.clear();//清空参数，重复利用

                        paramsMap.put("code", cardCode);
                        paramsMap.put("tenantId", tenantId);
                        paramsMap.put("branchId", branchId);
                        Card card = icService.findCardByGroup(paramsMap);
                        if(card != null ){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("卡号已经注册！");
                            return apiRest;

                        }
                        String cardFaceNum = params.get("cardFaceNum");
                        if(StringUtils.isNotEmpty(params.get("cardFaceNum"))){
                            paramsMap.clear();//清空参数，重复利用
                            paramsMap.put("code", cardFaceNum);
                            paramsMap.put("tenantId", tenantId);
                            paramsMap.put("branchId", branchId);
                            Card checkCard = icService.findCardByGroup(paramsMap);
                            if(checkCard!=null){
                                apiRest.setIsSuccess(false);
                                apiRest.setError("卡面号已经存在！");
                                return apiRest;
                            }
                        }
                        int flag = vipMapper.insert(params);
                        if(flag == 1){
                            addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId")));
                            Map<String, Object> param1 = new HashMap<>();
                            param1.put("tenantId", tenantId);
                            param1.put("branchId", branchId);
                            param1.put("vipCode", code);
                            vip = vipMapper.findByCondition(param1);

                            card = new Card();
                            card.setAuthCardId(authCard.getId());
                            card.setCode(cardCode);
                            card.setType(4);
                            card.setState(1);
                            card.setTenantId(vip.getTenantId());
                            card.setTenantCode(tenantCode);
                            card.setBranchCode(branch.getCode());
                            card.setHolderId(vip.getId());
                            card.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
                            card.setCreateAt(new Date());
                            card.setCreateBy("IC");
                            card.setLastUpdateAt(new Date());
                            card.setLastUpdateBy("IC");
                            card.setCardFaceNum(cardFaceNum);

                            int id = cardMapper.insertCard(card);
                        }
                    }
                    else{
                        int flag = vipMapper.insert(params);
                        if(flag == 1){
                            addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId")));
                            Map<String, Object> param1 = new HashMap<>();
                            param1.put("tenantId", tenantId);
                            param1.put("branchId", branchId);
                            param1.put("vipCode", code);
                            vip = vipMapper.findByCondition(param1);
                        }

                    }
                    if(vip!=null){
                        apiRest.setData(vip);
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("会员保存成功");
                        //查询是否有新注册会员活动
                        Map<String, Object> param2 = new HashMap<>();
                        param2.put("tenantId", tenantId);
                        param2.put("branchId", branchId);
                        param2.put("vipCode", code);
                        param2.put("promotionType", 1);
                        VipSpecialPromotion promotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(param2);
                        if(promotion != null){
                            Map<String, Object> map5 = new HashMap<>();
                            map5.put("id", vip.getTypeId());
                            map5.put("tenantId", vip.getTenantId());
                            VipType vipType = vipTypeMapper.findByCondition(map5);
                            //赠送积分
                            if(vipType != null && BigInteger.ONE.equals(vipType.getToSavePoints()) && promotion.getAddScore() != null&&promotion.getAddScore().compareTo(BigDecimal.ZERO)!=0){
                                dealScore(vip, promotion.getAddScore());
                                if(vip.getRemainingScore() != null){
                                    vip.setRemainingScore(vip.getRemainingScore().add(promotion.getAddScore()));
                                    vip.setSumScore(vip.getSumScore().add(promotion.getAddScore()));
                                }
                                else{
                                    vip.setRemainingScore(promotion.getAddScore());
                                    vip.setSumScore(promotion.getAddScore());
                                }
                                if(vip.getLargessscore() != null){
                                    vip.setLargessscore(vip.getLargessscore().add(promotion.getAddScore()));
                                }
                                else{
                                    vip.setLargessscore(promotion.getAddScore());
                                }
                                vipMapper.update(vip);
                                if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                    DecimalFormat sd=new DecimalFormat("#");
                                    Map smsMap = new HashMap();
                                    smsMap.put("type", 3);
                                    smsMap.put("tenantId", vip.getTenantId().toString());
                                    smsMap.put("number", vip.getPhone());
                                    smsMap.put("branchId", branch.getId().toString());
                                    smsMap.put("content", ("注册赠送###" +sd.format(promotion.getAddScore())+"###"+sd.format(vip.getRemainingScore())));
                                    ApiRest ssr = smsService.sendSMS(smsMap);
                                    if(!ssr.getIsSuccess()){
                                        LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                    }
                                }
                            }
                            //赠送优惠券
                            if(StringUtils.isNotEmpty(promotion.getPrizeDietPromotionIds())){
                                Map<String, String> map3 = new HashMap<>();
                                map3.put("tenantId", promotion.getTenantId().toString());
                                map3.put("cardId", promotion.getPrizeDietPromotionIds());
                                map3.put("vipId", vip.getId().toString());
                                map3.put("channel", "register");
                                map3.put("branchId", promotion.getBranchId().toString());
                                cardCouponsService.sendCardToVip(map3);
                            }
                        }
                    }else{
                        apiRest.setIsSuccess(false);
                        apiRest.setError("创建会员失败!");
                    }
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("没有查询到该商户下的总部!");
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("超过会员上限。");
            }
        }
        return apiRest;
    }


    /**
     * 保存或者新增会员
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveVip(Map params) throws ServiceException, ParseException{
        ResultJSON result = new ResultJSON();
        Vip vipByPhone = null;
        params.put("phone", params.get("phone") == null ? "" : params.get("phone"));
        params.put("email", params.get("email") == null ? "" : params.get("email"));
        params.put("memo", params.get("memo") == null ? "" : params.get("memo"));
        params.put("labelId", params.get("labelId") == null ? "" : params.get("labelId"));
        if(params.get("birthdayStr") != null && !params.get("birthdayStr").equals("") && !params.get("birthdayStr").equals("null")){
            params.put("birthday", params.get("birthdayStr"));
        }
        if(params.get("phone") != null && !"".equals(params.get("phone"))){
            Map<String, Object> param0 = new HashMap<>();

            //查询会员分组
            if(params.get("branchId") != null && params.get("tenantId") != null){
                param0.put("branchId", params.get("branchId"));
                Map<String, Object> paramZ = new HashMap<>();
                paramZ.put("id", params.get("branchId"));
                paramZ.put("tenantId", params.get("tenantId"));
                Branch branch = branchMapper.find(paramZ);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    paramZ.remove("id");
                    paramZ.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                    if(branches != null && branches.size() > 0){
                        String bIds = "";
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                        param0.remove("branchId");
                        param0.put("groups", bIds);
                    }
                }
            }
            param0.put("tenantId", params.get("tenantId"));
            param0.put("phone", params.get("phone"));
            List<Vip> list = vipMapper.select(param0);
            if(list != null && list.size() > 0){
                vipByPhone = list.get(0);
            }
        }
        if(params.get("id") != null && !"".equals(params.get("id"))){
            if(vipByPhone != null){
                if(!vipByPhone.getId().equals(new BigInteger(params.get("id").toString()))){
                    result.setSuccess("1");
                    result.setMsg("该电话已经被注册");
                    return result;
                }
            }
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("id"));
            Vip vips = vipMapper.findByCondition(param);
            params.remove("branchId");
            params.remove("tenantId");
            if(params.get("labelId") != null && !"".equals(params.get("labelId"))){
                int groupCount = vipMapper.labelCount(params);
                String labels[] = params.get("labelId").toString().split(",");
                if(groupCount != labels.length){
                    result.setSuccess("1");
                    result.setMsg("每个标签组只允许选择一个标签");
                    return result;
                }
            }
            int flag = vipMapper.update(params);
            if(flag == 1){
                result.setObject(vips);
                result.setSuccess("0");
                result.setMsg("会员更新成功");
            }
        }
        else{
            if(vipByPhone != null){
                result.setSuccess("1");
                result.setMsg("该电话已经被注册");
                return result;
            }
            if(isInLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()))){
                Map<String, Object> param = new HashMap<>();
                param.put("branchType", 0);
                param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                Branch branch = branchMapper.find(param);
                // 自动生成会员编号
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                if(branch != null){
                    String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                    params.put("vipCode", code);
                    if(params.get("phone") == null || "".equals(params.get("phone"))){
                        params.put("phone", "");
                    }

                    if(params.get("status") == null || "".equals(params.get("status"))){
                        params.put("status", "0");//默认是正常的
                    }
                    if(null == params.get("typeId")){
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("isOnlineDefault", 1);
                        param1.put("tenantId", params.get("tenantId"));
                        VipType vipType = vipTypeMapper.findByCondition(param1);
                        if(vipType != null){
                            params.put("typeId", vipType.getId());
                        }
                        else{
                            params.put("typeId", 0);
                        }
                    }

                    if(null == params.get("branchId")){
                        params.put("branchId", branch.getId());
                    }
                    if(null == params.get("regSource")){
                        params.put("regSource", "1");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    params.put("createAt", format1.format(new Date()));
                    params.put("lastUpdateAt", format1.format(new Date()));
                    params.put("isDeleted", 0);
                    if(params.get("labelId") != null && !"".equals(params.get("labelId"))){
                        int groupCount = vipMapper.labelCount(params);
                        String labels[] = params.get("labelId").toString().split(",");
                        if(groupCount != labels.length){
                            result.setSuccess("1");
                            result.setMsg("每个标签组只允许选择一个标签");
                            return result;
                        }
                    }
                    int flag = vipMapper.insert(params);
                    if(flag == 1){
                        addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()));
                        Map<String, Object> param1 = new HashMap<>();
                        param1.put("tenantId", params.get("tenantId"));
                        param1.put("branchId", params.get("branchId"));
                        param1.put("vipCode", code);
                        Vip vip = vipMapper.findByCondition(param1);
                        result.setObject(vip);
                        result.setSuccess("0");
                        result.setMsg("会员保存成功");

                        //查询是否有新注册会员活动
                        param1.put("promotionType", 1);
                        VipSpecialPromotion promotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(param1);
                        if(promotion != null){
                            Map<String, Object> map5 = new HashMap<>();
                            map5.put("id", vip.getTypeId());
                            map5.put("tenantId", vip.getTenantId());
                            VipType vipType = vipTypeMapper.findByCondition(map5);
                            //赠送积分
                            if(vipType != null && BigInteger.ONE.equals(vipType.getToSavePoints()) && promotion.getAddScore() != null&&promotion.getAddScore().compareTo(BigDecimal.ZERO)!=0){
                                dealScore(vip, promotion.getAddScore());
                                if(vip.getRemainingScore() != null){
                                    vip.setRemainingScore(vip.getRemainingScore().add(promotion.getAddScore()));
                                    vip.setSumScore(vip.getSumScore().add(promotion.getAddScore()));
                                }
                                else{
                                    vip.setRemainingScore(promotion.getAddScore());
                                    vip.setSumScore(promotion.getAddScore());
                                }
                                if(vip.getLargessscore() != null){
                                    vip.setLargessscore(vip.getLargessscore().add(promotion.getAddScore()));
                                }
                                else{
                                    vip.setLargessscore(promotion.getAddScore());
                                }
                                vipMapper.update(vip);
                                if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                    DecimalFormat sd=new DecimalFormat("#");
                                    Map smsMap = new HashMap();
                                    smsMap.put("type", 3);
                                    smsMap.put("tenantId", vip.getTenantId().toString());
                                    smsMap.put("number", vip.getPhone());
                                    smsMap.put("branchId", branch.getId().toString());
                                    smsMap.put("content", ("注册赠送###" +sd.format(promotion.getAddScore())+"###"+sd.format(vip.getRemainingScore())));
                                    ApiRest ssr = smsService.sendSMS(smsMap);
                                    if(!ssr.getIsSuccess()){
                                        LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                    }
                                }
                            }
                            //赠送优惠券
                            if(StringUtils.isNotEmpty(promotion.getPrizeDietPromotionIds())){
                                Map<String, String> map3 = new HashMap<>();
                                map3.put("tenantId", promotion.getTenantId().toString());
                                map3.put("cardId", promotion.getPrizeDietPromotionIds());
                                map3.put("vipId", vip.getId().toString());
                                map3.put("channel", "register");
                                map3.put("branchId", promotion.getBranchId().toString());
                                cardCouponsService.sendCardToVip(map3);
                            }
                        }
                    }
                }
                else{
                    result.setSuccess("1");
                    result.setMsg("没有查询到该商户下的总店!");
                }
            }
            else{
                result.setSuccess("1");
                result.setMsg("超过会员上限。");
            }
        }
        return result;
    }

    private void dealScore(Vip vip, BigDecimal addScore){
        VipBook vipBook = new VipBook();

        //记录积分台帐
        vipBook.setVipId(vip.getId());
        vipBook.setTenantId(vip.getTenantId());
        vipBook.setBranchId(vip.getBranchId());
        vipBook.setBookType(3);
        vipBook.setOperateBy("新注册会员活动");
        vipBook.setOperateAt(new Date());
        vipBook.setVipCode(vip.getVipCode());
        vipBook.setTotalScore(vip.getRemainingScore());
        vipBook.setVipScore(addScore);
        vipBook.setCreateAt(new Date());
        vipBook.setLastUpdateAt(new Date());
        vipBook.setStoreFrom("2");
        vipBook.setPaymentCode("ZCHY");
        vipBookMapper.insert(vipBook);
    }

    /**
     * 查询下一个队列序号
     *
     * @return
     */
    public String getNextSelValue(String type, int length, BigInteger tenantId){
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        String value = "";
        if(tenantConfig != null){
            int valueInt = Integer.parseInt(tenantConfig.getValue()) + 1;
            //超过最大值则归0
            if(valueInt > Integer.parseInt(tenantConfig.getMaxValue())){
                valueInt = 0;
            }
            value = String.valueOf(valueInt);
            while(value.length() < length){
                value = "0" + value;
            }
            /*tenantConfig.setValue(String.valueOf(valueInt));
            tenantConfigMapper.update(tenantConfig);*/
        }
        return value;
    }

    /**
     * 查询下一个队列序号
     *
     * @return
     */
    public String getNextSelValueNew(String type, int length, BigInteger tenantId, int index){
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        String value = "";
        if(tenantConfig != null){
            int valueInt = Integer.parseInt(tenantConfig.getValue()) + index;
            //超过最大值则归0
            if(valueInt > Integer.parseInt(tenantConfig.getMaxValue())){
                valueInt = 0;
            }
            value = String.valueOf(valueInt);
            while(value.length() < length){
                value = "0" + value;
            }
            /*tenantConfig.setValue(String.valueOf(valueInt));
            tenantConfigMapper.update(tenantConfig);*/
        }
        return value;
    }

    /**
     * 微餐厅注册默认会员接口
     */
    public ApiRest registerDefaultVipForCt(Map params){
        ApiRest rest = new ApiRest();
        if(isInLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()))){
            if(params.get("tenantId") != null && params.get("openId") != null){
                Map<String, Object> param = new HashMap<>();
                param.put("branchType", 0);
                param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                Branch branch = branchMapper.find(param);
                // 自动生成会员编号
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                if(branch != null){
                    String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                    params.put("status", "0");//默认是正常的
                    Vip vip = new Vip();
                    vip.setTenantId(new BigInteger(params.get("tenantId").toString()));
                    vip.setVipName("游客" + code);
                    vip.setVipCode(code);
                    vip.setOriginalId(params.get("openId").toString());
                    vip.setPhone("");
                    vip.setStatus(0);//默认是正常的
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("isOnlineDefault", 1);
                    param1.put("tenantId", new BigInteger(params.get("tenantId").toString()));
                    BigInteger branchId=branch.getId();
                    if(params.get("branchId")!=null&&StringUtils.isNotEmpty(params.get("branchId").toString())){
                        branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
                    }
                    param1.put("branchId",branchId);
                    VipType vipType = vipTypeMapper.findByCondition(param1);
                    if(vipType != null){
                        vip.setTypeId(vipType.getId());
                    }
                    if(null == params.get("branchId")){
                        vip.setBranchId(branch.getId());
                    }
                    else{
                        vip.setBranchId(new BigInteger(params.get("branchId").toString()));
                    }
                    vip.setRegSource("2");
                    vip.setCreateAt(new Date());
                    vip.setLastUpdateAt(new Date());
                    int flag = vipMapper.insert2(vip);
                    if(flag == 1){
                        addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()));
                        rest.setMessage("会员添加成功");
                        rest.setIsSuccess(true);
                        rest.setData(vip);
                    }
                    else{
                        rest.setMessage("会员添加失败");
                        rest.setIsSuccess(false);
                    }
                }
                else{
                    rest.setMessage("没有查询到该商户下的总店!");
                    rest.setIsSuccess(false);
                }

            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("商户id或openId不存在");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("最多添加" + getLimit(SysConfig.SYS_VIP_NUM) + "条会员");
        }
        return rest;
    }

    /**
     * 修改会员邮箱，生日
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON updateVip(Map params) throws ServiceException, ParseException{
        ResultJSON result = new ResultJSON();
        if(params.get("id") != null && !"".equals(params.get("id"))){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("id"));
            Vip vip = vipMapper.findByCondition(param);
            if(params.get("birthday") != null){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                vip.setBirthday(format.parse(params.get("birthday").toString()));
                param.put("birthday", format.parse(params.get("birthday").toString()));
            }
            if(params.get("email") != null && !"".equals(params.get("email"))){
                vip.setEmail(params.get("email").toString());
            }
            vipMapper.update(vip);
            result.setSuccess("0");
            result.setMsg("会员信息修改成功");

        }
        return result;
    }
    /**
     * 根据会员ID获取会员
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipById(BigInteger id, String tenantId, String branchId, String status) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(id != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
                /*if(status != null){
                    map.put("status",status);
                }*/
            map.put("tenantId", tenantId);
            VipVo vip = vipMapper.findVoById(map);
            if(vip != null){
                Map<String,String> typeMap = new HashMap<>();
                typeMap.put("tenantId",tenantId);
                typeMap.put("branchId",branchId);
                typeMap.put("typeId",vip.getTypeId().toString());
                Map disMap = vipMapper.branchDiscount(typeMap);
                if(disMap != null && disMap.get("discountRate") != null){
                    vip.setDiscountRate(BigDecimal.valueOf(Double.valueOf(disMap.get("discountRate").toString())));
                }
                Map<String, Object> map1 = new HashMap<>();
                map1.put("tenantId", vip.getTenantId());
                map1.put("holderId", vip.getId());
                List<Card> cards = cardMapper.select(map1);
                if(cards != null && cards.size() > 0){
                    vip.setCardCode(cards.get(0).getCode());
                    vip.setCardFaceNum(cards.get(0).getCardFaceNum());
                }

                map1.put("vipId", vip.getId());
                List<VipStoreHistory> storeHistories = vipStoreHistoryMapper.vipStoreList(map1);
                if(storeHistories != null && storeHistories.size() > 0){
                    BigDecimal giftTotal = BigDecimal.ZERO;
                    String lastPayType = "1";
                    Date date = storeHistories.get(0).getCreateAt();
                    vip.setLastStorePayType(storeHistories.get(0).getPayType());
                    for(VipStoreHistory history : storeHistories){
                        if("1".equals(history.getStoreType())){
                            if(date==null){
                                date=history.getCreateAt();
                            }
                            giftTotal = giftTotal.add(history.getGiftAmount() == null ? BigDecimal.ZERO : history.getGiftAmount());
                            if(history.getCreateAt().after(date)){
                                date = history.getCreateAt();
                                vip.setLastStorePayType(history.getPayType());
                            }
                        }
                        else if("2".equals(history.getStoreType())){
                            giftTotal = giftTotal.add((history.getGiftAmount() == null ? BigDecimal.ZERO : history.getGiftAmount()).multiply(new BigDecimal("-1")));
                        }
                    }
                    vip.setGiftAmountTotal(giftTotal);
                }

                /*新增会员标签信息*/
                String labelsId = vip.getLabelId();
                if(labelsId != null && !"".equals(labelsId)){
                    vip.setLabelId(labelsId);
                    Map queryMap = new HashMap();
                    queryMap.put("ids", labelsId);
                    queryMap.put("tenantId", tenantId);
                    queryMap.put("branchId", vip.getBranchId());
                    String labelsName = vipMapper.queryNamesByIds(queryMap);
                    vip.setLabelName(labelsName);
                }
            }
            if(vip != null){
                result.setSuccess("0");
                result.setObject(vip);
            }
            else{
                result.setSuccess("1");
                result.setMsg("会员不存在");
            }
        }
        else{
            result.setSuccess("1");
            result.setMsg("查询失败,无效的id");
        }
        return result;
    }

    /**
     * 验证会员编码是否唯一
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON uniqueCode(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(params.get("vipCode") != null && !"".equals(params.get("vipCode"))){
            //根据会员编码获取会员
            Map<String, Object> param = new HashMap<>();
            param.put("vipCode", params.get("vipCode"));
            param.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(param);
            if(vip != null){//如果能找到会员信息
                if(params.get("id") != null && !"".equals(params.get("id"))){
                    //判断是是否是正在修改的会员本身的编码
                    if(!vip.getId().equals(new BigInteger(params.get("id").toString()))){
                        //不是会员本身的编码则说明编码重复
                        result.setSuccess("1");
                    }
                    else{
                        //已存在编码是会员本身的说明不重复
                        result.setSuccess("0");
                    }
                }
                else{
                    //不是正在编辑的,则编码重复
                    result.setSuccess("1");
                }
            }
            else{
                //找不到会员信息则会员编码不重复
                result.setSuccess("0");
            }
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效的编码");
        }
        return result;
    }

    /**
     * 验证会员电话是否唯一
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON uniquePhone(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        try{
            if(params.get("phone") != null && !"".equals(params.get("phone"))){
                //根据会员电话获取会员
                Map<String, Object> param = new HashMap<>();
                param.put("phone", params.get("phone"));
                param.put("tenantId", params.get("tenantId"));
                Vip vip = vipMapper.findByCondition(param);
                if(vip != null){//如果能找到会员信息
                    if(params.get("id") != null && !"".equals(params.get("id"))){
                        //判断是是否是正在修改的会员本身的编电话
                        if(!vip.getId().equals(new BigInteger(params.get("id").toString()))){
                            //不是会员本身的电话则说明编码重复
                            result.setSuccess("1");
                        }
                        else{
                            //已存在电话是会员本身的说明不重复
                            result.setSuccess("0");
                        }
                    }
                    else{
                        //不是正在编辑的,则电话重复
                        result.setSuccess("1");
                    }
                }
                else{//找不到会员信息则会员电话不重复 再判断手机号是否与会员编码重复
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("vipCode", params.get("phone"));
                    param1.put("tenantId", params.get("tenantId"));
                    Vip vipInfo = vipMapper.findByCondition(param1);
                    if(vipInfo != null){//如果能找到会员信息
                        //手机号 已经是 会员号
                        result.setSuccess("1");
                    }
                    else{
                        //找不到会员信息则会员编码不重复
                        result.setSuccess("0");
                    }
                }
            }
            else{
                throw new Exception("无效的电话");
            }
        }
        catch(Exception e){
            result.setIsSuccess(false);
            result.setMsg(e.getMessage());
            LogUtil.logError("验证会员电话是否唯一异常:" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除会员信息
     *
     * @param ids
     * @return
     * @throws ServiceException
     */
    public ResultJSON delVips(String ids) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(ids != null){
            for(String id : ids.split(",")){
                Map<String, Object> param = new HashMap<>();
                param.put("id", id);
                Vip vip = vipMapper.findByCondition(param);
                /*//2018-08-24ancong删除
                if(vip != null && vip.getOriginalId() != null && !"".equals(vip.getOriginalId())){
                    result.setSuccess("1");
                    result.setMsg("该会员已绑定微信，请解绑后删除！");
                    return result;
                }*/
                if(vip != null){
                    if(vip.getVipStore() != null && vip.getVipStore().doubleValue() != 0.0){
                        result.setSuccess("1");
                        result.setMsg("该会员储值余额不为0，不能删除！");
                        return result;
                    }
                }
                param.put("isDeleted", 1);
                param.put("lastUpdateAt", new Date());
                vipMapper.update(param);
//                    minusLimit(SysConfig.SYS_VIP_NUM,vip.getTenantId());

                //删除会员卡号
                Map cardParams = new HashMap();
                cardParams.put("holderId", vip.getId());
                cardParams.put("tenantId", vip.getTenantId());
                List<Card> cardList = cardMapper.select(cardParams);
                if(cardList != null && cardList.size() > 0){
                    Card card = cardList.get(0);
                    if(card != null){
                        Map paramsMap = new HashMap();
                        paramsMap.put("id", card.getId());
                        paramsMap.put("state", 0);
                        paramsMap.put("isDeleted", 1);
                        paramsMap.put("lastUpdateAt", new Date());

                        cardMapper.update(paramsMap);
                    }
                }
            }
            result.setSuccess("0");
            result.setMsg("会员删除成功");
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效的数据");
        }
        return result;
    }

    /**
     * 会员绑定微信
     *
     * @param params
     * @throws ServiceException
     */
    public ResultJSON bindChat(Map params) throws ServiceException{
        ResultJSON resultJSON = new ResultJSON();
        if(params.get("vipId") != null && !"".equals(params.get("vipId"))){
            Map<String, Object> param = new HashMap<>();
            param.put("vipId", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(param);
            if(vip != null){
                param.put("id", vip.getId());
                param.put("originalId", params.get("openId"));
                vipMapper.update(param);
                resultJSON.setSuccess("0");
                resultJSON.setMsg("绑定成功");
            }
        }
        else{
            resultJSON.setSuccess("1");
            resultJSON.setMsg("无效会员");
        }
        return resultJSON;
    }

    /**
     * 手机号获取会员
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON findVipByPhone(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        String tenantId = params.get("tenantId").toString();
        if(params.get("phone") != null && !"".equals(params.get("phone"))){
            Map<String, Object> param = new HashMap<>();
            param.put("phone", params.get("phone"));
            param.put("tenantId", tenantId);
            //param.put("status",0);
            Vip vip = vipMapper.findByCondition(param);
            result.setSuccess("0");
            result.setMsg("查询成功");
            result.setObject(vip);
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效手机号");
        }
        return result;
    }

    /**
     * 微信号获取会员
     */
    public ResultJSON findVipByOpenId(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        String tenantId = params.get("tenantId").toString();
        if(params.get("openid") != null && !"".equals(params.get("openid"))){
            Map<String, Object> param = new HashMap<>();
            param.put("originalId", params.get("openid"));
            param.put("tenantId", tenantId);
            //param.put("status",0);
            Vip vip = vipMapper.findByCondition(param);
            result.setSuccess("0");
            result.setMsg("查询成功");
            result.setObject(vip);
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效微信识别号");
        }
        return result;
    }

    //
//    /**
//     * 根据手机号或会员编码获取会员
//     * @return
//     * @throws ServiceException
//     */
//    def findVipByPhoneOrCode(Map params) throws ServiceException {
//        ResultJSON result = new ResultJSON();
//        try {
//            String searchText = params.searchText;
//            String tenantId = params.tenantId;
//            if (searchText) {
//                def vip = Vip.findByPhoneAndTenantId(searchText, tenantId);
//                if (null == vip) {
//                    vip = Vip.findByVipCodeAndTenantId(searchText, tenantId);
//                }
//                result.success = "0";
//                result.msg = "查询成功";
//                result.object = vip;
//            } else {
//                throw new Exception("无效搜索内容")
//            }
//        } catch (Exception e) {
//            ServiceException se = new ServiceException("1001", "查询失败", e.message)
//            throw se
//        }
//        return result;
//    }
//
//    /**
//     * 根据手机号或会员编码获取会员
//     * @author szq
//     */
//    def findVipByPhoneOrVipCode(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            String searchText = params.searchText;
//            String tenantId = params.tenantId;
//            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            if (searchText) {
//                StringBuffer queryPhone = new StringBuffer("SELECT v.id,v.tenant_id,v.original_id,v.branch_id,v.vip_code,v.vip_name,v.sex,v.birthday,v.phone,v.email,v.status,p.type_name,v.reg_source,v.vip_store,v.vip_store_total,v.remaining_score,v.largessscore,v.sum_score,v.reg_date,v.create_by FROM vip v" +
//                        " left join vip_type p on v.type_id=p.id where v.tenant_id=:tenantId and v.phone=:searchText")
//                StringBuffer queryVipCode = new StringBuffer("SELECT v.id,v.tenant_id,v.original_id,v.branch_id,v.vip_code,v.vip_name,v.sex,v.birthday,v.phone,v.email,v.status,p.type_name,v.reg_source,v.vip_store,v.vip_store_total,v.remaining_score,v.largessscore,v.sum_score,v.reg_date,v.create_by FROM vip v" +
//                        " left join vip_type p on v.type_id=p.id where v.tenant_id=:tenantId and v.vip_code=:searchText")
//                def nameParams = new HashMap();
//                nameParams.tenantId = new BigInteger(params.tenantId);
//                nameParams.searchText = searchText
//                Query listSqOne = getSession().createSQLQuery(queryPhone.toString());
//                Query listSqTwo = getSession().createSQLQuery(queryVipCode.toString());
//                for (Map.Entry<String, String> nameParam : nameParams) {
//                    listSqOne.setParameter(nameParam.key, nameParam.value)
//                    listSqTwo.setParameter(nameParam.key, nameParam.value)
//                }
//                VipVo vipVo = new VipVo();
//                def listPhone = listSqOne.list();
//                def listVipCode = listSqTwo.list();
//                if (listPhone.size() > 0) {
//                    vipVo.setId(listPhone.get(0)[0])
//                    vipVo.setTenantId(listPhone.get(0)[1])
//                    vipVo.setOriginalId(listPhone.get(0)[2])
//                    vipVo.setBranchId(listPhone.get(0)[3])
//                    vipVo.setVipCode(listPhone.get(0)[4])
//                    vipVo.setVipName(listPhone.get(0)[5])
//                    vipVo.setSex(listPhone.get(0)[6])
//                    vipVo.setBirthday(listPhone.get(0)[7])
//                    vipVo.setPhone(listPhone.get(0)[8])
//                    vipVo.setEmail(listPhone.get(0)[9])
//                    vipVo.setStatus(listPhone.get(0)[10])
//                    vipVo.setTypeName(listPhone.get(0)[11])
//                    vipVo.setRegSource(listPhone.get(0)[12])
//                    vipVo.setVipStore(listPhone.get(0)[13])
//                    vipVo.setVipStoreTotal(listPhone.get(0)[14])
//                    vipVo.setRemainingScore(listPhone.get(0)[15])
//                    vipVo.setLargessscore(listPhone.get(0)[16])
//                    vipVo.setSumScore(listPhone.get(0)[17])
//                    vipVo.setRegDate(sim.format(listPhone.get(0)[18]))
//                    vipVo.setCreateBy(listPhone.get(0)[19])
//                } else if (listVipCode.size() > 0) {
//                    vipVo.setId(listVipCode.get(0)[0])
//                    vipVo.setTenantId(listVipCode.get(0)[1])
//                    vipVo.setOriginalId(listVipCode.get(0)[2])
//                    vipVo.setBranchId(listVipCode.get(0)[3])
//                    vipVo.setVipCode(listVipCode.get(0)[4])
//                    vipVo.setVipName(listVipCode.get(0)[5])
//                    vipVo.setSex(listVipCode.get(0)[6])
//                    vipVo.setBirthday(listVipCode.get(0)[7])
//                    vipVo.setPhone(listVipCode.get(0)[8])
//                    vipVo.setEmail(listVipCode.get(0)[9])
//                    vipVo.setStatus(listVipCode.get(0)[10])
//                    vipVo.setTypeName(listVipCode.get(0)[11])
//                    vipVo.setRegSource(listVipCode.get(0)[12])
//                    vipVo.setVipStore(listVipCode.get(0)[13])
//                    vipVo.setVipStoreTotal(listVipCode.get(0)[14])
//                    vipVo.setRemainingScore(listVipCode.get(0)[15])
//                    vipVo.setLargessscore(listVipCode.get(0)[16])
//                    vipVo.setSumScore(listVipCode.get(0)[17])
//                    vipVo.setRegDate(sim.format(listVipCode.get(0)[18]))
//                    vipVo.setCreateBy(listVipCode.get(0)[19])
//                } else {
//                    vipVo = null;
//                }
//                rest.isSuccess = true;
//                rest.message = "查询成功";
//                rest.data = vipVo;
//            } else {
//                rest.isSuccess = false;
//                rest.message = "无效搜索内容";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("查询会员异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
    public ResultJSON linkage(Map params){
        ResultJSON result = new ResultJSON();
        ApiRest rest = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        Tenant tenant = null;
        if(Constants.REST_RESULT_SUCCESS.equals(rest.getResult())){
            if(rest.getData() != null){
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
            }
        }
        if(params.get("vipId") != null && !"".equals(params.get("vipId"))){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(param);
            double amount = 0;
            if(params.get("newValue") != null && !"".equals(params.get("newValue"))){
                amount = Double.parseDouble(params.get("newValue").toString());
            }
            double presentLimit = 0;

            VipStoreRule vipStoreRule = null;
            Map<String, Object> qParam = new HashMap<>();
            if(tenant != null && tenant.getIsBranchManagementStore()){
                qParam.put("tenantId", vip.getTenantId());
                qParam.put("off", false);
                qParam.put("branchId", params.get("branchId"));
                List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(qParam);
                if(list != null && list.size() > 0){
                    vipStoreRule = list.get(0);
                }
            }
            else{
                //查询总店
                Map<String, Object> param0 = new HashMap<>();
                param0.put("tenantId", vip.getTenantId());
                param0.put("branchType", 0);
                Branch branch0 = branchMapper.find(param0);
                qParam.put("tenantId", vip.getTenantId());
                qParam.put("off", false);
                qParam.put("branchId", branch0.getId());
                vipStoreRule = vipStoreRuleMapper.findByCondition(qParam);
                if(vipStoreRule == null){
                    qParam.remove("branchId");
                    qParam.put("noBranch", 1);
                    List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(qParam);
                    if(list != null && list.size() > 0){
                        vipStoreRule = list.get(0);
                    }
                }

            }
            BigInteger detailId = null;
            if(null != vipStoreRule){
                qParam.put("ruleId", vipStoreRule.getId());
                qParam.remove("branchId");
                List<VipStoreRuleDetails> details = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(qParam);
                int maxIndex = -1;
                double currPayLimit = 0;
                if(null != details && details.size() > 0){
                    for(int i = 0; i < details.size(); i++){
                        if((details.get(i).getStartTime() == null || details.get(i).getStartTime().compareTo(new Date()) <= 1)
                                && (details.get(i).getEndTime() == null || details.get(i).getEndTime().compareTo(new Date()) >= 1)){
                            //判断是否是生效一次
                            if(details.get(i).getEffectTimes() != null && details.get(i).getEffectTimes() == 1){
                                Map<String, Object> map = new HashMap<>();
                                map.put("tenantId", vipStoreRule.getTenantId());
                                map.put("vipId", vip.getId());
                                map.put("ruleDetailId", details.get(i).getId());
                                List<VipStoreHistory> histories = vipStoreHistoryMapper.vipStoreList(map);
                                if(histories != null && histories.size() > 0){
                                    continue;
                                }
                            }
                            //判断是否指定会员类型
                            if(details.get(i).getPointedVipType() != null && !details.get(i).getPointedVipType().equals(BigInteger.ZERO)){
                                if(!details.get(i).getPointedVipType().equals(vip.getTypeId())){
                                    continue;
                                }
                            }
                            if(amount >= details.get(i).getPayLimit().doubleValue() && currPayLimit < details.get(i).getPayLimit().doubleValue()){
                                maxIndex = i;
                                currPayLimit = details.get(i).getPayLimit().doubleValue();
                            }
                        }
                    }
                }
                if(maxIndex >= 0 && null != details){
                    if(null != details.get(maxIndex).getPresentLimit()){
                        presentLimit = details.get(maxIndex).getPresentLimit().doubleValue();
                        detailId = details.get(maxIndex).getId();
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("presentLimit", presentLimit);
            map.put("detailId", detailId);
            result.setJsonMap(map);
            result.setSuccess("true");
            result.setMsg("充值成功！");

        }
        else{
            result.setSuccess("false");
            result.setMsg("充值失败。会员不存在。");
        }
        return result;
    }


    public ApiRest cancelVipTrade(Map<String, String> params){
        ApiRest apiRest = new ApiRest();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("tenantId", params.get("tenantId"));
        paramMap.put("branchId", params.get("branchId"));
        paramMap.put("vipId", params.get("vipId"));
        paramMap.put("saleCode", params.get("saleCode"));
        paramMap.put("storeType", "3");
        List<VipStoreHistory> vipStoreHistoryList = vipStoreHistoryMapper.findListByCondition(paramMap);
        if(vipStoreHistoryList.size() > 0){
            paramMap.clear();
            paramMap.put("tenantId", params.get("tenantId"));
            paramMap.put("id", params.get("vipId"));
            Vip vip = vipMapper.findById(paramMap);
            BigDecimal amount = BigDecimal.ZERO;
            for(VipStoreHistory vipStoreHistory : vipStoreHistoryList){
                amount = vipStoreHistory.getPayAmount() == null ? BigDecimal.ZERO : vipStoreHistory.getPayAmount().add(amount);
                vipStoreHistory.setRemark("客户取消订单，金额返还，此储值消费记录无效！");
                vipStoreHistory.setLastUpdateAt(new Date());
                vipStoreHistory.setIsDeleted(true);
                vipStoreHistoryMapper.update1(vipStoreHistory);
            }
            if(vip != null){
                vip.setVipStore(vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore().add(amount));
                vip.setLastUpdateAt(new Date());
                vipMapper.update(vip);
            }
        }
        paramMap.clear();
        paramMap.put("tenantId", params.get("tenantId"));
        paramMap.put("branchId", params.get("branchId"));
        paramMap.put("vipId", params.get("vipId"));
        paramMap.put("tradeNo", params.get("saleCode"));
        paramMap.put("tradeType", "1");
        List<VipTradeHistory> vipTradeHistoryList = vipTradeHistoryMapper.findListByCondition(paramMap);
        if(vipTradeHistoryList.size() > 0){
            for(VipTradeHistory vipTradeHistory : vipTradeHistoryList){
                vipTradeHistory.setLastUpdateBy("客户取消订单，金额返还，此消费记录无效！");
                vipTradeHistory.setLastUpdateAt(new Date());
                vipTradeHistory.setIsDeleted(true);
                vipTradeHistoryMapper.update1(vipTradeHistory);
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("储值支付退款成功！");
        return apiRest;
    }

    /**
     * 查询会员类型
     *
     * @param params
     */
    public ResultJSON queryVipType(Map params){
        ResultJSON result = new ResultJSON();
        if(params.get("vipId") != null){
            Map<String, Object> qParam = new HashMap<>();
            qParam.put("id", params.get("vipId"));
            Vip vip = vipMapper.findByCondition(qParam);
            Map<String, Object> param = new HashMap<>();
            param.put("id", vip.getTypeId());
            VipType vipType = vipTypeMapper.findByCondition(param);
            if(vipType != null){
                result.setSuccess("0");
                result.setMsg("查询成功!");
                result.setObject(vipType);
            }
            else{
                result.setSuccess("1");
                result.setMsg("数据异常，该会员没有会员等级!");
            }

        }
        else{
            result.setSuccess("1");
            result.setMsg("vipId不存在，查询失败!");
        }
        return result;
    }

    /**
     * 用于微餐厅注册会员
     * 查询当前商户 线上注册默认会员类型
     *
     * @param params
     */
    public ResultJSON qryDefaultVipType(Map params){
        ResultJSON result = new ResultJSON();
        Map<String, Object> qParam = new HashMap<>();
        qParam.put("tenantId", params.get("tenantId"));
        qParam.put("isOnlineDefault", params.get("isOnlineDefault"));
        qParam.put("isDeleted", params.get("isDeleted"));
        VipType vipType = vipTypeMapper.findByCondition(qParam);
        if(vipType != null){
            result.setSuccess("0");
            result.setMsg("查询成功!");
            result.setObject(vipType);
        }
        else{
            result.setSuccess("1");
            result.setMsg("数据异常，该会员没有会员等级!");
        }
        return result;
    }
//
//    /**
//     * 查询 会员类型
//     */
//    def vipTypeList(Map params) throws ServiceException {
//        try {
//            ResultJSON result = new ResultJSON()
//            def queryString = new StringBuffer("from VipType as v where v.isDeleted = false");
//            queryString.append(" and v.tenantId  =" + params.tenantId);
//            Query query = getSession().createQuery(queryString.toString());
//
//            result.object = query.list();
//            result.msg = "查询成功";
//            result.success = true;
//            return result
//        } catch (Exception e) {
//            ServiceException se = new ServiceException("1001", "查询失败", e.message)
//            throw se
//        }
//    }
//

    /**
     * 判断该账号是否为会员
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON isVip(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        VipAddress vipAddr = null;
        Map<String, Object> qParam = new HashMap<>();
        if(params.get("phone") != null && !"".equals(params.get("phone"))){
            String phone = params.get("phone").toString();
            if(phone.indexOf("-") > 0){
                qParam.put("telPhone", params.get("phone"));
                vipAddr = vipAddressMapper.findByCondition(qParam);
            }
            else{
                qParam.put("mobilePhone", params.get("phone"));
                vipAddr = vipAddressMapper.findByCondition(qParam);
            }
            if(vipAddr != null && vipAddr.getVipId().doubleValue() > 0){
                result.setSuccess("true");
            }
            else{
                result.setSuccess("false");
            }
        }
        else{
            result.setSuccess("false");
        }
        return result;
    }

    /**
     * 验证同一个商户下会员电话是否重复
     */
    public ResultJSON qryUniquePhone(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(params.get("tenantId") != null){
            Map<String, Object> qParam = new HashMap<>();
            qParam.put("tenantId", params.get("tenantId"));
            qParam.put("phone", params.get("phone"));
            //查询会员分组
            if(params.get("branchId") != null && params.get("tenantId") != null){
                qParam.put("branchId", params.get("branchId"));
                Map<String, Object> param = new HashMap<>();
                param.put("id", params.get("branchId"));
                param.put("tenantId", params.get("tenantId"));
                Branch branch = branchMapper.find(param);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    param.remove("id");
                    param.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(param);
                    if(branches != null && branches.size() > 0){
                        String bIds = "";
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                        qParam.remove("branchId");
                        qParam.put("groups", bIds);
                    }
                }
            }
            Vip uniquePhone = vipMapper.findByCondition(qParam);
            if(uniquePhone != null){
                result.setIsSuccess(false);
                result.setMsg("该手机号已被占用，请重新输入手机号！");
                result.setObject(uniquePhone);
            }
            else{
                result.setIsSuccess(true);
                result.setMsg("该手机号可用！");
            }
        }
        else{
            result.setIsSuccess(false);
            result.setMsg("商户号不存在。");
        }
        return result;
    }

    /**
     * 获取会员类型
     */
    public ResultJSON findVipType(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(params.get("id") != null && params.get("tenantId") != null){
            Map<String, Object> qParam = new HashMap<>();
            qParam.put("tenantId", params.get("tenantId"));
            qParam.put("id", params.get("id"));
            qParam.put("isDeleted", false);
            Vip vip = vipMapper.findByCondition(qParam);
            if(vip != null){
                Map<String, Object> param = new HashMap<>();
                param.put("id", vip.getTypeId());
                param.put("tenantId", params.get("tenantId"));
                param.put("isDeleted", false);
                VipType vipType = vipTypeMapper.findByCondition(param);
                if(vipType != null){
                    result.setIsSuccess(true);
                    result.setMsg("查询成功");
                    result.setObject(vipType);
                }
            }
        }
        return result;
    }
//    /**
//     * 会员信息'{"newVip":"今日新会员","vipCount":"会员总数","todayTrade":"今日消费","todayStore":"今日储值"}'
//     * @param tId not null
//     * @author hxh
//     */
//    public Map<String, Object> vipInfo(BigInteger tId) {
//        Validate.notNull(tId, "tId not null")
//
//        String today = DateUtils.formatData(new Date())
//
//        String sql = """ SELECT COUNT(v.id) total  FROM vip v WHERE 1 = 1
//        AND v.tenant_id = ${tId} AND v.is_deleted = 0
//        AND v.create_at >= '${today} 00:00:00'
//        AND v.create_at <= '${today} 23:59:59' """
//        String sqlCount = " select count(v.id) from vip v where 1=1 AND v.tenant_id = ${tId} AND v.is_deleted = 0"
//
//        String sqlTrade = """ SELECT  FORMAT(SUM(v.trade_amount),2) total FROM vip vp INNER JOIN vip_trade_history v ON vp.id = v.vip_id WHERE 1 = 1
//        AND vp.tenant_id = ${tId} AND v.is_deleted = 0  AND v.trade_type = 1
//        AND v.create_at >= '${today} 00:00:00'
//        AND v.create_at <= '${today} 23:59:59' """
//
//        String sqlStore = """ SELECT  FORMAT(SUM(v.pay_amount),2) total FROM vip vp INNER JOIN vip_store_history v ON vp.id = v.vip_id WHERE 1 = 1
//        AND vp.tenant_id = ${tId} AND v.is_deleted = 0  AND v.store_type = 1
//        AND v.create_at >= '${today} 00:00:00'
//        AND v.create_at <= '${today} 23:59:59' """
//
//
//        BigInteger newVip = getSession().createSQLQuery(sql).uniqueResult()
//        BigInteger vipCount = getSession().createSQLQuery(sqlCount).uniqueResult()
//        String todayTrade = getSession().createSQLQuery(sqlTrade).uniqueResult()
//        String todayStore = getSession().createSQLQuery(sqlStore).uniqueResult()
//        todayTrade = todayTrade == null ? "0.00" : todayTrade
//        todayStore = todayStore == null ? "0.00" : todayStore
//
//        return [newVip: newVip, vipCount: vipCount, todayTrade: todayTrade, todayStore: todayStore]
//    }
//    /**
//     * 最新执行促销信息'{"promotionType":"类型","startDate":"开始时间","endDate":"结束时间","memo":"备注"}'
//     * @param tId not null
//     * @author hxh
//     */
//    public Map<String, Object> promotionInfo(BigInteger tId) {
//        Validate.notNull(tId, "tId not null")
//        String today = DateUtils.formatData("yyyy-MM-dd HH:mm:ss", new Date())
//        String sql = """
//        SELECT  * FROM
//                (SELECT d.id, d.tenant_id,d.promotion_type,d.promotion_code,d.promotion_name, d.start_date,d.end_date,d.create_at,d.memo,
//                        CONCAT(d.start_date, ' ', d.start_time) start_t,
//                        CONCAT(d.end_date, ' ', d.end_time) end_t
//                        FROM diet_promotion d
//                        WHERE 1 = 1 AND d.promotion_status = 0  AND d.is_deleted = 0 and d.tenant_id = ${tId}) f
//        WHERE f.start_t <= '${today}' AND f.end_t >= '${today}'
//        ORDER BY f.create_at DESC LIMIT 0,1"""
//
//        def list = getSession().createSQLQuery(sql).uniqueResult()
//        def info = [:]
//        if (list) {
//            if (list[2] == 4) {
//                info.typeName = "买A赠B";
//            }
//            if (list[2] == 7) {
//                info.typeName = "单品满减";
//            }
//            if (list[2] == 6) {
//                info.typeName = "第二份优惠";
//            }
//            if (list[2] == 9) {
//                info.typeName = "整单优惠";
//            }
//            if (list[2] == 11) {
//                info.typeName = "单品促销";
//            }
//            if (list[2] == 8) {
//                info.typeName = "卡券活动";
//            }
//            if (list[2] == 12) {
//                info.typeName = "刮刮乐";
//            }
//            info.promotionType = list[2]
//            info.promotionCode = list[3]
//            info.startDate = list[5]
//            info.endDate = list[6]
//            info.memo = list[8]
//        }
//        return info
//    }
//    /**
//     * 分页查询会员列表
//     * @parampage rows 同时为null时是不分页查询
//     * 否则
//     * page 不为空且是不小于1的整数
//     * rows 不为空且是不小于1的整数
//     * @return map["total":total,"rows":vipList]
//     * total为符合条件的记录的条数，vipList是第page页的会员信息列表
//     * @"queryStr"是按会员编号，电话查询的输入条件
//     * @author genghui
//     */
//    def qryVipListUseForPage(Map params) {
//        def page = params.page
//        def rows = params.rows
//        def tenantId = StringUtils.trimToNull(params.tenantId)
//        def branchId = StringUtils.trimToNull(params.branchId)
//        Args.isInteger(tenantId, "tenantId是整数")
//        Args.isInteger(branchId, "branchId是整数")
//        def map = new HashMap<String, Object>()
//        StringBuffer query = new StringBuffer("(SELECT v.id,v.tenant_id,v.original_id,v.branch_id,v.vip_code,v.vip_name,v.sex,v.birthday,v.phone,v.email,v.status,p.type_name,v.reg_source,v.vip_store,v.vip_store_total FROM vip v" +
//                " left join vip_type p on v.type_id=p.id where 1=1 AND v.is_deleted = 0 ")
//        StringBuffer queryCount = new StringBuffer("(select count(v.id) from  vip v LEFT JOIN vip_type p ON v.type_id = p.id WHERE 1 = 1 AND v.is_deleted = 0 ");
//        def namedParams = new HashMap();
//        if (params.tenantId != null) {
//            Args.isInteger(params.tenantId, "tenantId是整数")
//        }
//        if (params.branchId != null) {
//            Args.isInteger(params.branchId, "branchId是整数")
//        }
//        params.each { k, v ->
//            if ('branchId'.equals(k) && v) {
//                query.append(' AND v.branch_id= :branchId ')
//                queryCount.append(' AND v.branch_id= :branchId ')
//                namedParams.branchId = v.asType(BigInteger)
//            }
//            if ('tenantId'.equals(k) && v) {
//                query.append(' AND v.tenant_id= :tenantId ')
//                queryCount.append(' AND v.tenant_id= :tenantId ')
//                namedParams.tenantId = v.asType(BigInteger)
//            }
//            //按会员编号 电话 查询 add by genghui
//            if ('queryStr'.equals(k) && v) {
//                query.append(" AND (v.vip_code like '%" + v.toString() + "%'or v.phone like '%" + v.toString() + "%')")
//                queryCount.append(" AND (v.vip_code like '%" + v.toString() + "%' or v.phone like '%" + v.toString() + "%')")
//            }
//        }
//        query.append(")");
//        queryCount.append(")");
//        def listSq = getSession().createSQLQuery(query.toString())
//        def countSq = getSession().createSQLQuery(queryCount.toString())
//        Args.isInteger(page, "page是整数")
//        Args.isTrue(Integer.parseInt(page) >= 1, "page 的值不小于1")
//        Args.isInteger(rows, "rows是整数")
//        Args.isTrue(Integer.parseInt(rows) >= 1, "rows 的值不小于1")
//        int max = Integer.parseInt(rows)
//        int offset = (Integer.parseInt(page) - 1) * max
//        listSq.setMaxResults(max)
//        listSq.setFirstResult(offset)
//        namedParams.eachWithIndex { Map.Entry<Object, Object> entry, int i ->
//            listSq.setParameter(entry.key, entry.value.toString())
//            countSq.setParameter(entry.key, entry.value.toString())
//        }
//        List<VipVo> vipList = new ArrayList<VipVo>();
//        if (listSq.list().size() > 0) {
//            for (int i = 0; i < listSq.list().size(); i++) {
//                VipVo vipVo = new VipVo();
//                vipVo.setId(listSq.list().get(i)[0])
//                vipVo.setTenantId(listSq.list().get(i)[1])
//                vipVo.setOriginalId(listSq.list().get(i)[2])
//                vipVo.setBranchId(listSq.list().get(i)[3])
//                vipVo.setVipCode(listSq.list().get(i)[4])
//                vipVo.setVipName(listSq.list().get(i)[5])
//                vipVo.setSex(listSq.list().get(i)[6])
//                vipVo.setBirthday(listSq.list().get(i)[7])
//                vipVo.setPhone(listSq.list().get(i)[8])
//                vipVo.setEmail(listSq.list().get(i)[9])
//                vipVo.setStatus(listSq.list().get(i)[10])
//                vipVo.setTypeName(listSq.list().get(i)[11])
//                vipVo.setRegSource(listSq.list().get(i)[12])
//                vipVo.setVipStore(listSq.list().get(i)[13])
//                vipVo.setVipStoreTotal(listSq.list().get(i)[14])
//                vipList.add(vipVo)
//            }
//        }
//        def total = countSq.list()[0]
//        map.put("total", total);
//        map.put("rows", vipList);
//        return map
//    }

    /**
     * 根据消费金额计算赠送的积分
     *
     * @author szq
     * @params not null 参数：
     * 1.vipId
     * 2.moneytary 消费金额
     */
    public Vip calculateScores(BigInteger vipId, BigDecimal moneytary, String paymentCode){
        Map<String, Object> qParam = new HashMap<>();
        qParam.put("id", vipId);
        Vip vip = vipMapper.findByCondition(qParam);
        if(vip != null){
            if(vip.getTypeId() != null){
                Map<String, Object> param = new HashMap<>();
                param.put("id", vip.getTypeId());
                VipType vipType = vipTypeMapper.findByCondition(param);
                if(vipType != null && vipType.getToSavePoints().doubleValue() == 1){
                    if(vipType.getPointsFactor() == null){
                        vipType.setPointsFactor(new BigDecimal(1.00));
                    }
                    double score = moneytary.doubleValue() / (vipType.getPointsFactor().doubleValue());
                    if(vip.getSumScore() == null){
                        qParam.put("sumScore", score);
                    }
                    else{
                        qParam.put("sumScore", vip.getSumScore().doubleValue() + score);
                    }
                    if(vip.getRemainingScore() == null){
                        qParam.put("remainingScore", score);
                    }
                    else{
                        qParam.put("remainingScore", vip.getRemainingScore().doubleValue() + score);
                    }
                    vipMapper.update(qParam);
                    vip = vipMapper.findByCondition(qParam);
                    VipBook vipBook = new VipBook();
                    vipBook.setTotalScore(vip.getRemainingScore());
                    vipBook.setVipId(vip.getId());
                    vipBook.setTenantId(vip.getTenantId());
                    vipBook.setBranchId(vip.getBranchId());
                    vipBook.setBookType(1);
                    vipBook.setOperateBy("收银员");
                    vipBook.setOperateAt(new Date());
                    vipBook.setVipCode(vip.getVipCode());
                    vipBook.setPaymentCode(paymentCode);
                    vipBook.setVipScore(new BigDecimal(score));
                    vipBook.setSumConsume(moneytary);
                    vipBook.setIsDeleted(false);
                    vipBook.setCreateAt(new Date());
                    vipBook.setLastUpdateAt(new Date());

                    vipBookMapper.insert(vipBook);
                }
            }
            return vip;
        }
        return null;
    }

    /**
     * 微餐厅认证会员信息
     *
     * @return
     * @params 1.id
     * 2.vipName  姓名
     * 3.sex   性别
     * 4.birthday 生日
     * @author szq
     */
    public ApiRest confirmVipInfo(Map params) throws ServiceException, ParseException{
        ApiRest rest = new ApiRest();
        String[] sexx = new String[2];
        sexx[0] = "1";
        sexx[1] = "2";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(params.get("phone") != null && params.get("id") != null && params.get("vipName") != null && params.get("sex") != null && params.get("sex") != null){
            Map<String, Object> qParam = new HashMap<>();
            qParam.put("id", params.get("id"));
            qParam.put("isDeleted", false);
            Vip vip = vipMapper.findByCondition(qParam);
            if(vip != null){
                vip.setVipName(params.get("vipName").toString());
                vip.setBirthday(sdf.parse(params.get("birthday").toString()));
                vip.setSex(Integer.valueOf(params.get("sex").toString()));
                vip.setPhone(params.get("phone").toString());
                Map param1=new HashMap();
                param1.put("tenantId",vip.getTenantId());
                param1.put("branchId",vip.getBranchId());
                param1.put("promotionType", 1);
                VipSpecialPromotion promotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(param1);
                if(promotion != null){
                    Map<String, Object> map5 = new HashMap<>();
                    map5.put("id", vip.getTypeId());
                    map5.put("tenantId", vip.getTenantId());
                    VipType vipType = vipTypeMapper.findByCondition(map5);
                    //赠送积分
                    if(vipType != null && BigInteger.ONE.equals(vipType.getToSavePoints()) && promotion.getAddScore() != null&&promotion.getAddScore().compareTo(BigDecimal.ZERO)!=0){
                        dealScore(vip, promotion.getAddScore());
                        if(vip.getRemainingScore() != null){
                            vip.setRemainingScore(vip.getRemainingScore().add(promotion.getAddScore()));
                            vip.setSumScore(vip.getSumScore().add(promotion.getAddScore()));
                        }
                        else{
                            vip.setRemainingScore(promotion.getAddScore());
                            vip.setSumScore(promotion.getAddScore());
                        }
                        if(vip.getLargessscore() != null){
                            vip.setLargessscore(vip.getLargessscore().add(promotion.getAddScore()));
                        }
                        else{
                            vip.setLargessscore(promotion.getAddScore());
                        }
                        if(vip.getPhone() != null && !vip.getPhone().equals("")){
                            DecimalFormat sd=new DecimalFormat("#");
                            Map smsMap = new HashMap();
                            smsMap.put("type", 3);
                            smsMap.put("tenantId", vip.getTenantId().toString());
                            smsMap.put("number", vip.getPhone());
                            smsMap.put("branchId", vip.getBranchId().toString());
                            smsMap.put("content", ("注册赠送###" +sd.format(promotion.getAddScore())+"###"+sd.format(vip.getRemainingScore())));
                            ApiRest ssr = smsService.sendSMS(smsMap);
                            if(!ssr.getIsSuccess()){
                                LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                            }
                        }
                    }
                    //赠送优惠券
                    if(StringUtils.isNotEmpty(promotion.getPrizeDietPromotionIds())){
                        Map<String, String> map3 = new HashMap<>();
                        map3.put("tenantId", vip.getTenantId().toString());
                        map3.put("cardId", promotion.getPrizeDietPromotionIds());
                        map3.put("vipId", vip.getId().toString());
                        map3.put("channel", "register_wechat");
                        map3.put("branchId", vip.getBranchId().toString());
                        cardCouponsService.sendCardToVip(map3);
                    }
                    vipMapper.update(vip);

                    BigDecimal addScore = promotion.getAddScore();
                    if (addScore != null) {
                        WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), addScore.intValue(), 1);
                    }
                }else{
                    vipMapper.update(vip);
                }
                rest.setIsSuccess(true);
                rest.setMessage("会员信息认证成功");
            }

        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("phone、id、vipName、sex、birthday不能为空！");
        }
        return rest;
    }

    /**
     * 根据会员编码或电话查询会员信息
     *
     * @author szq
     */
    public List<VipVo> findVipByCondition(Map params){
        List<VipVo> vips = null;
        Map<String, Object> qParam = new HashMap<>();
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            qParam.put("qryBranchId",params.get("branchId").toString());
            qParam.put("branchId", params.get("branchId"));
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    qParam.remove("branchId");
                    qParam.put("groups", bIds);
                }
            }
        }
        qParam.put("vipCode", params.get("code"));
        qParam.put("phone", params.get("code"));
        qParam.put("tenantId", params.get("tenantId"));
        //qParam.put("status","0");
        vips = vipMapper.findByFhoneOrCode(qParam);
        Map<String, Object> map1 = new HashMap<>();
        if(vips != null){
            for(VipVo vip : vips){
                map1.put("tenantId", vip.getTenantId());
                map1.put("holderId", vip.getId());
                List<Card> cards = cardMapper.select(map1);
                if(cards != null && cards.size() > 0){
                    vip.setCardCode(cards.get(0).getCode());
                    vip.setCardFaceNum(cards.get(0).getCardFaceNum());
                }

                map1.put("vipId", vip.getId());
                List<VipStoreHistory> storeHistories = vipStoreHistoryMapper.vipStoreList(map1);
                if(storeHistories != null && storeHistories.size() > 0){
                    BigDecimal giftTotal = BigDecimal.ZERO;
                    String lastPayType = "1";
                    Date date = storeHistories.get(0).getCreateAt();
                    vip.setLastStorePayType(storeHistories.get(0).getPayType());
                    for(VipStoreHistory history : storeHistories){
                        if("1".equals(history.getStoreType())){
                            if(date==null){
                                date=history.getCreateAt();
                            }
                            giftTotal = giftTotal.add(history.getGiftAmount() == null ? BigDecimal.ZERO : history.getGiftAmount());
                            if(history.getCreateAt().before(date)){
                                date = history.getCreateAt();
                                vip.setLastStorePayType(history.getPayType());
                            }
                        }
                        else if("2".equals(history.getStoreType())){
                            giftTotal = giftTotal.add((history.getGiftAmount() == null ? BigDecimal.ZERO : history.getGiftAmount()).multiply(new BigDecimal("-1")));
                        }
                    }
                    vip.setGiftAmountTotal(giftTotal);
                }
                /*新增会员标签信息*/
                String labelsId = vip.getLabelId();
                if(labelsId != null && !"".equals(labelsId)){
                    vip.setLabelId(labelsId);
                    Map queryMap = new HashMap();
                    queryMap.put("ids", labelsId);
                    queryMap.put("tenantId", vip.getTenantId());
                    queryMap.put("branchId", vip.getBranchId());
                    String labelsName = vipMapper.queryNamesByIds(queryMap);
                    vip.setLabelName(labelsName);
                }
            }
        }
        LogUtil.logInfo("返回会员信息："+BeanUtils.toJsonStr(vips));
        return vips;
    }


    /**
     * 根据会员编码或电话或卡号查询会员信息（pos查询会员流程简化接口）
     * 手机号或编码优先查询，卡号优先级靠后
     *
     * @author ac
     */
    public VipVo findVipForPos(Map params){
        VipVo resultVip = null;
        Map<String, Object> qParam = new HashMap<>();
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            qParam.put("qryBranchId",params.get("branchId").toString());
            qParam.put("branchId", params.get("branchId"));
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    qParam.remove("branchId");
                    qParam.put("groups", bIds);
                }
            }
        }
        qParam.put("vipCode", params.get("code"));
        qParam.put("phone", params.get("code"));
        qParam.put("tenantId", params.get("tenantId"));
        //qParam.put("status","0");
        List<VipVo> vips = vipMapper.findByFhoneOrCode(qParam);
        if(vips.size()>0){
            resultVip = vips.get(0);
            resultVip.setCardCode(resultVip.getVipCardCode());
        }else{
            qParam.put("code", params.get("code"));
            List<Card> list = cardMapper.select1(params);
            if(list.size()>0){
                Card userCard = list.get(0);
                if(userCard != null && userCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP) &&  userCard.getTenantId().equals(new BigInteger(params.get("tenantId").toString()))){
                    Map vipMap = new HashMap();
                    vipMap.put("id", userCard.getHolderId());
                    vipMap.put("tenantId", params.get("tenantId"));
                    resultVip = vipMapper.findVoById(vipMap);
                    resultVip.setCardCode(resultVip.getVipCardCode());
                }
            }
        }
        return resultVip;
    }

//    /**
//     * 根据tenantId查询会员
//     * @author szq
//     */
//    public Map<String, Object> listVips(Map params) {
//        def map = new HashMap<String, Object>();
//        try {
//            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            StringBuffer query = new StringBuffer("(SELECT v.id,v.tenant_id,v.original_id,v.branch_id,v.vip_code,v.vip_name,v.sex,v.birthday,v.phone,v.email,v.status,p.type_name,v.reg_source,v.vip_store,v.vip_store_total,v.remaining_score,v.largessscore,v.sum_score,v.reg_date,v.create_by FROM vip v" +
//                    " left join vip_type p on v.type_id=p.id where 1=1 AND v.is_deleted = false ")
//            StringBuffer queryCount = new StringBuffer("(select count(*) from  vip v LEFT JOIN vip_type p ON v.type_id = p.id WHERE 1 = 1 AND v.is_deleted = false ");
//            def max = params['rows'] ? params['rows'] : 20
//            def offset = params['page'] ? (Integer.parseInt(params['page']) - 1) * Integer.parseInt(max) : 0
//            def namedParams = new HashMap();
//            params.each { k, v ->
//                if ('branchId'.equals(k) && v) {
//                    query.append(' AND v.branch_id= :branchId ')
//                    queryCount.append(' AND v.branch_id= :branchId ')
//                    namedParams.branchId = v.asType(BigInteger)
//                }
//                if ('tenantId'.equals(k) && v) {
//                    query.append(' AND v.tenant_id= :tenantId ')
//                    queryCount.append(' AND v.tenant_id= :tenantId ')
//                    namedParams.tenantId = v.asType(BigInteger)
//                }
//                //按会员编号 名称 查询
//                if ('queryStr'.equals(k) && v) {
//                    query.append(" AND (v.vip_code like '%" + v.toString() + "%' or v.vip_name  like '%" + v.toString() + "%' )")
//                    queryCount.append(" AND (v.vip_code like '%" + v.toString() + "' or v.vip_name  like '%" + v.toString() + "%' )")
//                }
//            }
//            query.append(" )");
//            queryCount.append(" )");
//            def listSq = getSession().createSQLQuery(query.toString())
//            def countSq = getSession().createSQLQuery(queryCount.toString())
//            if (params['rows'] && params['page']) {
//                listSq.setMaxResults(max?.asType(int))
//                listSq.setFirstResult(offset?.asType(int))
//            }
//            namedParams.eachWithIndex { Map.Entry<Object, Object> entry, int i ->
//                listSq.setParameter(entry.key, entry.value.toString())
//                countSq.setParameter(entry.key, entry.value.toString())
//            }
//            List<VipVo> vipList = new ArrayList<VipVo>();
//            def list = listSq.list();
//            if (list.size() > 0) {
//                for (int i = 0; i < list.size(); i++) {
//                    VipVo vipVo = new VipVo();
//                    vipVo.setId(list.get(i)[0])
//                    vipVo.setTenantId(list.get(i)[1])
//                    vipVo.setOriginalId(list.get(i)[2])
//                    vipVo.setBranchId(list.get(i)[3])
//                    vipVo.setVipCode(list.get(i)[4])
//                    vipVo.setVipName(list.get(i)[5])
//                    vipVo.setSex(list.get(i)[6])
//                    vipVo.setBirthday(list.get(i)[7])
//                    vipVo.setPhone(list.get(i)[8])
//                    vipVo.setEmail(list.get(i)[9])
//                    vipVo.setStatus(list.get(i)[10])
//                    vipVo.setTypeName(list.get(i)[11])
//                    vipVo.setRegSource(list.get(i)[12])
//                    vipVo.setVipStore(list.get(i)[13])
//                    vipVo.setVipStoreTotal(list.get(i)[14])
//                    vipVo.setRemainingScore(list.get(i)[15])
//                    vipVo.setLargessscore(list.get(i)[16])
//                    vipVo.setSumScore(list.get(i)[17])
//                    if (list.get(i)[18] != null) {
//                        vipVo.setRegDate(sim.format(list.get(i)[18]))
//                    }
//                    vipVo.setCreateBy(list.get(i)[19])
//                    vipList.add(vipVo)
//                }
//            }
//            def total = countSq.list()[0]
//            map.put("total", total);
//            map.put("rows", vipList);
//        } catch (Exception e) {
//            LogUtil.logError("查询会员信息异常" + e.message);
//            throw new ServiceException();
//        }
//        return map;
//    }
//
//    /**
//     * 新增会员
//     * @author szq
//     */
//    def createVip(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            if (isInLimit(SysConfig.SYS_VIP_NUM)) {
//                if (params.phone) {
//                    Vip vipByPhone = Vip.findByTenantIdAndPhone(new BigInteger(params.tenantId), params.phone);
//                    if (vipByPhone != null) {
//                        rest.isSuccess = false;
//                        rest.message = "该电话已经被注册";
//                        return rest;
//                    }
//                }
//                V_BranchCard branch = V_BranchCard.findByBranchTypeAndTenantId(0, new BigInteger(params.tenantId));
//                // 自动生成会员编号
//                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
//                if (branch) {
//                    String code = format.format(new Date()) + SeqUtils.getSeqVal(getSession(), "VIP", 5);
//                    params.vipCode = code;
//
//                    if (StringUtils.isBlank(params.status)) {
//                        params.status = "0";//默认是正常的
//                    }
//                    Vip vip = BeanUtils.copyProperties(Vip.class, params)
//
//                    if (vip.hasErrors()) {
//                        throw Exception("数据未通过校验")
//                    }
//                    if (null == vip.typeId) {
////                        VipType vipType = VipType.findByTypeCodeAndTenantId("0000",vip.tenantId);
//                        VipType vipType = VipType.findByIsOnlineDefaultAndTenantIdAndIsDeleted(true, vip.tenantId, false);
//                        vip.typeId = vipType.id;
//                    }
//                    if (null == vip.branchId) {
//                        vip.branchId = branch.id;
//                    }
//                    if (null == vip.regSource) {
//                        vip.regSource = "1";
//                    }
//                    Date date = new Date();
//                    SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd");
//                    vip.regDate = formatD.format(date);
//                    getSession().save(vip)
//                    addLimit(SysConfig.SYS_VIP_NUM)
//                    rest.data = vip;
//                    rest.message = "会员添加成功";
//                    rest.isSuccess = true;
//                } else {
//                    rest.message = "没有查询到该商户下的总店!";
//                    rest.isSuccess = false;
//                }
//
//            } else {
//                rest.isSuccess = false;
//                rest.message = "最多添加" + getLimit(SysConfig.SYS_VIP_NUM) + "条会员";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("新增会员异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 新增会员时验证电话是否重复
//     * @author szq
//     */
//    def verifyPhone(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            Vip vip = null;
//            if (params.branchId != null) {
//                vip = Vip.findByTenantIdAndPhoneAndBranchId(new BigInteger(params.tenantId), params.phone, new BigInteger(params.branchId));
//            } else {
//                vip = Vip.findByTenantIdAndPhone(new BigInteger(params.tenantId), params.phone);
//            }
//
//            if (vip != null) {
//                if (StringUtils.isNotBlank(params.id)) {
//                    //判断是是否是正在修改的会员本身的编电话
//                    if (vip.id != Integer.parseInt(params.id)) {
//                        //不是会员本身的电话则说明编码重复
//                        rest.isSuccess = true;
//                        rest.message = "该电话已经被注册";
//                    } else {
//                        //已存在电话是会员本身的说明不重复
//                        rest.isSuccess = false;
//                        rest.message = "该电话没有被注册";
//                    }
//                } else {
//                    rest.isSuccess = true;
//                    rest.message = "该电话已经被注册";
//                }
//            } else {
//                Vip vipInfo = null;
//                if (params.branchId != null) {
//                    vipInfo = Vip.findByVipCodeAndTenantIdAndBranchId(params.phone, new BigInteger(params.tenantId), new BigInteger(params.branchId));
//                } else {
//                    vipInfo = Vip.findByVipCodeAndTenantId(params.phone, new BigInteger(params.tenantId));
//                }
//                if (vipInfo != null) {//如果能找到会员信息
//                    //手机号 已经是 会员号
//                    rest.isSuccess = true;
//                    rest.message = "该电话已经被注册";
//                } else {
//                    //找不到会员信息则会员编码不重复
//                    rest.isSuccess = false;
//                    rest.message = "该电话没有被注册";
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.logError("验证电话是否被注册异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 根据id查询会员
//     * @author szq
//     */
//    public Vip findVipById(BigInteger id) {
//        Vip vip = Vip.findById(id);
//        return vip;
//    }
//
//    /**
//     * 根据id删除会员
//     * @author szq
//     */
//    def deleteVip(BigInteger id) {
//        ApiRest rest = new ApiRest();
//        try {
//            Vip vip = Vip.findById(id);
//            if (vip) {
//                if (vip.originalId != null && vip.originalId) {
//                    rest.isSuccess = false;
//                    rest.message = "该会员已绑定微信，请解绑后删除";
//                    return rest;
//                }
//                if (vip.vipStore != 0 && vip.vipStore != null) {
//                    rest.isSuccess = false;
//                    rest.message = "该会员储值余额不为0，不能删除";
//                    return rest;
//                }
//                vip.isDeleted = true;
//                vip.lastUpdateAt = new Date();
//                vip.save flush: true;
//                minusLimit(SysConfig.SYS_VIP_NUM);
//                rest.isSuccess = true;
//                rest.message = "该会员删除成功";
//            } else {
//                rest.isSuccess = false;
//                rest.message = "该会员不存在或已被删除";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("删除会员异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 根据id修改会员
//     * @author szq
//     */
//    def editVip(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            Vip vip = Vip.findById(new BigInteger(params.id));
//            if (!vip) {
//                rest.message = "该会员不存在或已被删除";
//                rest.isSuccess = true;
//                return rest;
//            }
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            if (params.birthday != null) {
//                vip.setBirthday(format.parse(params.birthday));
//            }
//            vip.setEmail(params.email);
//            if (StringUtils.isEmpty((String)params["expiryDate"])) {
//                vip.setExpiryDate(null);
//            } else {
//                vip.setExpiryDate(format.parse((String)params["expiryDate"]));
//            }
//            vip.save flush: true;
//            rest.data = vip;
//            rest.message = "会员修改成功";
//            rest.isSuccess = true;
//        } catch (Exception e) {
//            LogUtil.logError("修改会员异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 会员甄选
//     * @author szq
//     */
//    public List<VipVo> listVipSelectAll(Map params) {
//        List<VipVo> vipList = new ArrayList<VipVo>();
//        try {
//            StringBuffer query = new StringBuffer("SELECT v.id,v.tenant_id,v.original_id,v.branch_id,v.vip_code,v.vip_name,v.sex,v.birthday,v.phone,v.email,v.status,p.type_name,p.preferential_policy,v.reg_source,v.type_id," +
//                    "v.vip_store,v.vip_store_total FROM vip v left join vip_type p on v.type_id = p.id where 1=1")
//            def max = params['rows'] ? params['rows'] : 20
//            def offset = params['page'] ? (Integer.parseInt(params['page']) - 1) * Integer.parseInt(max) : 0
//            def namedParams = new HashMap();
//            SimpleDateFormat simL = new SimpleDateFormat("yyyy-MM-dd");
//            Date dL = new Date();
//            params.each { k, v ->
//                if ("tenantId".equals(k) && v) {
//                    if (params.init == "1") {
//                        query.append(' AND v.tenant_id=  -1');
//                    } else {
//                        query.append(' AND v.tenant_id=:tenantId ');
//                        namedParams.tenantId = v.asType(BigInteger);
//                    }
//                }
//                if ("sex".equals(k) && v) {
//                    def sexs = params.sex.split(",");
//                    if (sexs.size() != 3) {
//                        if (sexs.size() == 2) {
//                            if (Integer.parseInt(sexs[0]) == 1 && Integer.parseInt(sexs[1]) == 2) {
//                                query.append(' AND (v.sex=1 or v.sex=2) ');
//                            }
//                            if (Integer.parseInt(sexs[0]) == 1 && Integer.parseInt(sexs[1]) == 3) {
//                                query.append(' AND (v.sex!=2 or v.sex is null) ');
//                            }
//                            if (Integer.parseInt(sexs[0]) == 2 && Integer.parseInt(sexs[1]) == 3) {
//                                query.append(' AND (v.sex!=1 or v.sex is null) ');
//                            }
//                        }
//                        if (sexs.size() == 1) {
//                            if (Integer.parseInt(sexs[0]) != 3) {
//                                v = sexs[0];
//                                query.append(' AND v.sex =:sex1 ')
//                                namedParams.sex1 = v.asType(Integer)
//                            } else {
//                                query.append(' AND (v.sex<1 or v.sex>2 or v.sex is null)  ')
//                            }
//                        }
//                    }
//                }
//                if ("startAge".equals(k) && v) {
//                    v = (Integer.parseInt(simL.format(dL).substring(0, 4)) - Integer.parseInt(params.endAge)) + "-01-01";
//                    query.append(' AND v.birthday >=:start ')
//                    namedParams.start = v.asType(String)
//                }
//                if ("endAge".equals(k) && v) {
//                    v = (Integer.parseInt(simL.format(dL).substring(0, 4)) - Integer.parseInt(params.startAge)) + "-12-31";
//                    query.append(' AND v.birthday <=:end ')
//                    namedParams.end = v.asType(String)
//                }
//                if ("birthday".equals(k) && v) {// 生日 格式为月日 01-01
//                    String b = params.birthday;
//                    String aa = b.substring(0, b.indexOf("-"));
//                    String bb = b.substring(b.indexOf("-") + 1, b.length());
//                    def bir = "";
//                    if (aa.length() == 1) {
//                        bir += "0" + aa
//                    } else {
//                        bir += aa;
//                    }
//                    if (bb.length() == 1) {
//                        bir += "-0" + bb;
//                    } else {
//                        bir += "-" + bb;
//                    }
//                    query.append(" AND (v.birthday  like '%" + bir + "%') ");
//                }
//                if ("branch".equals(k) && v) {//卡开机构
//                    query.append(' AND v.branch_id =:branch ')
//                    namedParams.branch = v.asType(BigInteger)
//                }
//                if ("startOpenCard".equals(k) && v) {//开卡时间
//                    query.append(' AND v.create_at >= :startOpenCard ');
//                    namedParams.startOpenCard = v.asType(String);
//                }
//                if ("endOpenCard".equals(k) && v) {//开卡时间
//                    query.append(' AND v.create_at <= :endOpenCard ');
//                    namedParams.endOpenCard = v.asType(String);
//                }
//                if ("regSource".equals(k) && v) {
//                    def regSource = params.regSource.split(",");
//
//                    if (regSource[2] == "1") {
//                        if (regSource[0] == "0") {
//                            query.append(' AND (v.reg_source != 1 or v.reg_source is null) ')
//                        }
//                        if (regSource[1] == "0") {
//                            query.append(' AND (v.reg_source != 2 or v.reg_source is null) ')
//                        }
//                    } else {
//                        if (regSource[0] == "1" && regSource[1] == "1") {
//                            query.append(' AND (v.reg_source = 1 or v.reg_source = 2) ')
//                        } else {
//                            if (regSource[0] == "1") {
//                                query.append(' AND v.reg_source = 1 ')
//                            }
//                            if (regSource[1] == "1") {
//                                query.append(' AND v.reg_source = 2 ')
//                            }
//                        }
//                    }
//                }
//                if ("vipType".equals(k) && v) {
//                    query.append(" AND ( ")
//                    def vipType = params.vipType.split(",");
//                    if (vipType.size() >= 2) {
//                        for (def i = 0; i < vipType.length; i++) {
//                            if (i < vipType.length - 1) {
//                                //v = vipType[i];
//                                query.append(" p.type_name ='" + vipType[i] + "' or ")
//                            } else {
//                                //v = vipType[i];
//                                query.append(" p.type_name ='" + vipType[i] + "' ) ")
//                            }
//                        }
//                    }
//                    if (vipType.size() == 1) {
//                        v = vipType[0];
//                        query.append("  p.type_name =:vipType  )")
//                        namedParams.vipType = v.asType(String);
//                    }
//                }
//                if ("startVipStore".equals(k) && v) {//储值余额
//                    if (Integer.parseInt(params.startVipStore) == 0 && Integer.parseInt(params.endVipStore) == 0) {
//                        query.append(' AND (v.vip_store = 0 or v.vip_store is null )  ')
//                    } else if (Integer.parseInt(params.startVipStore) == 0 && Integer.parseInt(params.endVipStore) > 0) {
//                        query.append(' AND ((v.vip_store >= 0   ')
//                        query.append(' AND v.vip_store <=:endVipStore ) ')
//                        v = Integer.parseInt(params.endVipStore);
//                        namedParams.endVipStore = v.asType(Integer);
//                        query.append('  or v.vip_store is null ) ')
//                    } else if (Integer.parseInt(params.startVipStore) > 0 && Integer.parseInt(params.endVipStore) > 0) {
//                        query.append(' AND v.vip_store >=:startVipStore   ')
//                        namedParams.startVipStore = v.asType(Integer);
//
//                        v = Integer.parseInt(params.endVipStore);
//                        query.append(' AND v.vip_store <=:endVipStore  ')
//                        namedParams.endVipStore = v.asType(Integer);
//                    }
//                }
//                if ("startRemainingScore".equals(k) && v) {//积分余额
//                    if (Integer.parseInt(params.startRemainingScore) == 0 && Integer.parseInt(params.endRemainingScore) == 0) {
//                        query.append(' AND (v.remaining_score =0 or v.remaining_score is null )')
//                    } else if (Integer.parseInt(params.startRemainingScore) == 0 && Integer.parseInt(params.endRemainingScore) > 0) {
//                        query.append(' AND ((v.remaining_score >=:startRemainingScore ')
//                        namedParams.startRemainingScore = v.asType(Integer);
//
//                        query.append('  AND v.remaining_score <=:endRemainingScore) ')
//                        v = Integer.parseInt(params.endRemainingScore);
//                        namedParams.endRemainingScore = v.asType(Integer);
//                        query.append('  or v.remaining_score is null ) ')
//                    } else if (Integer.parseInt(params.startRemainingScore) > 0 && Integer.parseInt(params.endRemainingScore) > 0) {
//                        query.append(' AND (v.remaining_score >=:startRemainingScore ')
//                        namedParams.startRemainingScore = v.asType(Integer);
//
//                        query.append('  AND v.remaining_score <=:endRemainingScore) ')
//                        v = Integer.parseInt(params.endRemainingScore);
//                        namedParams.endRemainingScore = v.asType(Integer);
//                    }
//                }
//                if ("startVipStoreTotal".equals(k) && v) {//累计储值总额
//                    if (Integer.parseInt(params.startVipStoreTotal) == 0 && Integer.parseInt(params.endVipStoreTotal) == 0) {
//                        query.append(' AND (v.vip_store_total =0 or v.vip_store_total is null )')
//                    } else if (Integer.parseInt(params.startVipStoreTotal) == 0 && Integer.parseInt(params.endVipStoreTotal) > 0) {
//                        query.append(' AND ((v.vip_store_total >=:startVipStoreTotal ')
//                        namedParams.startVipStoreTotal = v.asType(Integer)
//
//                        query.append(' AND v.vip_store_total <=:endVipStoreTotal ) ')
//                        v = Integer.parseInt(params.endVipStoreTotal);
//                        namedParams.endVipStoreTotal = v.asType(Integer)
//                        query.append(' or v.vip_store_total is null )')
//                    } else {
//                        query.append(' AND (v.vip_store_total >=:startVipStoreTotal ')
//                        namedParams.startVipStoreTotal = v.asType(Integer)
//
//                        query.append(' AND v.vip_store_total <=:endVipStoreTotal ) ')
//                        v = Integer.parseInt(params.endVipStoreTotal);
//                        namedParams.endVipStoreTotal = v.asType(Integer)
//                    }
//                }
//                if ("startSumConsume".equals(k) && v) {//累计消费总额
//                    if (Integer.parseInt(params.startSumConsume) == 0 && Integer.parseInt(params.endSumConsume) == 0) {
//                        query.append(' AND (v.sum_consume =0 or v.sum_consume is null )')
//                    } else if (Integer.parseInt(params.startSumConsume) == 0 && Integer.parseInt(params.endSumConsume) > 0) {
//                        query.append(' AND ((v.sum_consume >=:startSumConsume ')
//                        namedParams.startSumConsume = v.asType(Integer)
//
//                        query.append(' AND v.sum_consume <=:endSumConsume ) ')
//                        v = Integer.parseInt(params.endSumConsume)
//                        namedParams.endSumConsume = v.asType(Integer)
//
//                        query.append('  or v.sum_consume is null ) ')
//                    } else {
//                        query.append(' AND (v.sum_consume >=:startSumConsume ')
//                        namedParams.startSumConsume = v.asType(Integer)
//                        query.append(' AND v.sum_consume <=:endSumConsume ) ')
//                        v = Integer.parseInt(params.endSumConsume)
//                        namedParams.endSumConsume = v.asType(Integer)
//                    }
//                }
//                if ("startBuyTimes".equals(k) && v) {//消费次数
//                    if (Integer.parseInt(params.startBuyTimes) == 0 && Integer.parseInt(params.endBuyTimes) == 0) {
//                        query.append(' AND (v.buy_times =0 or v.buy_times is null )')
//                    } else if (Integer.parseInt(params.startBuyTimes) == 0 && Integer.parseInt(params.endBuyTimes) > 0) {
//                        query.append(' AND ((v.buy_times >=0 ')
//
//                        query.append(' AND v.buy_times <=:endBuyTimes ) ')
//                        v = Integer.parseInt(params.endBuyTimes);
//                        namedParams.endBuyTimes = v.asType(Integer)
//
//                        query.append('  or v.buy_times is null )')
//                    } else {
//                        query.append(' AND (v.buy_times >=:startBuyTimes ')
//                        namedParams.startBuyTimes = v.asType(Integer)
//
//                        query.append(' AND v.buy_times <=:endBuyTimes ) ')
//                        v = Integer.parseInt(params.endBuyTimes);
//                        namedParams.endBuyTimes = v.asType(Integer)
//                    }
//                }
//            }
//            Query queryy = getSession().createSQLQuery(query.toString());
//            queryy.setMaxResults(max?.asType(int))
//            queryy.setFirstResult(offset?.asType(int))
//            namedParams.eachWithIndex { Map.Entry<Object, Object> entry, int i ->
//                queryy.setParameter(entry.key, entry.value.toString())
//            }
//            def list = queryy.list();
//            if (list && list.size() > 0) {
//                for (int i = 0; i < list.size(); i++) {
//                    VipVo vipVo = new VipVo();
//                    vipVo.setId(list.get(i)[0])
//                    vipVo.setTenantId(list.get(i)[1])
//                    vipVo.setOriginalId(list.get(i)[2])
//                    vipVo.setBranchId(list.get(i)[3])
//                    vipVo.setVipCode(list.get(i)[4])
//                    vipVo.setVipName(list.get(i)[5])
//                    vipVo.setSex(list.get(i)[6])
//                    vipVo.setBirthday(list.get(i)[7])
//                    vipVo.setPhone(list.get(i)[8])
//                    vipVo.setEmail(list.get(i)[9])
//                    vipVo.setStatus(list.get(i)[10])
//                    vipVo.setTypeName(list.get(i)[11])
//                    vipVo.setRegSource(list.get(i)[13])
//                    vipVo.setTypeId(list.get(i)[14])
//                    vipVo.setVipStore(list.get(i)[15])
//                    vipVo.setVipStoreTotal(list.get(i)[16])
//                    vipList.add(vipVo)
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.logError("查询会员异常" + e.message);
//            throw new ServiceException();
//        }
//        return vipList;
//    }

    /**
     * 处理积分计算队列
     *
     * @param
     * @return
     */
    public ApiRest dealVipScore() throws IOException{
        ApiRest rest = new ApiRest();
        //LogUtil.logInfo("***********处理积分任务开始************");
        String data = "";
        String key = "";
        String errorKey = "";
        while(true){
            key = PropertyUtils.getDefault("topic.vip.score");
            errorKey = PropertyUtils.getDefault("topic.vip.score") + "_error";
            data = PartitionCacheUtils.rpop(key);
            try{
                if(data != null && !"".equals(data)){
                    //LogUtil.logInfo("处理积分的数据"+data);
                    rest = vipScoreDeal(data);
                }
                else{
                    rest.setIsSuccess(true);
                    rest.setMessage("积分任务:积分队列为空");
                    //LogUtil.logInfo("***********积分任务:积分队列为空************");
                    //队列为空则结束
                    break;
                }
            }
            catch(Exception e){
                LogUtil.logError("积分计算数据处理异常:" + e.getMessage());
                //处理异常数据
                PartitionCacheUtils.lpush(errorKey, data);
            }
        }
        return rest;
    }

    /**
     * 处理积分计算队列
     *
     * @param
     * @return
     */
    public ApiRest dealVipScoreError() throws IOException{
        ApiRest rest = new ApiRest();
        //LogUtil.logInfo("***********处理积分任务开始************");
        String data = "";
        String key = "";
        String errorKey = "";
        while(true){
            key = PropertyUtils.getDefault("topic.vip.score");
            errorKey = PropertyUtils.getDefault("topic.vip.score") + "_error";
            data = PartitionCacheUtils.rpop(errorKey);
            try{
                if(data != null && !"".equals(data)){
                    //LogUtil.logInfo("处理积分的数据Error"+data);
                    rest = vipScoreDeal(data);
                }
                else{
                    rest.setIsSuccess(true);
                    rest.setMessage("积分任务:积分队列为空");
                    //LogUtil.logInfo("***********积分任务:积分队列为空************");
                    //队列为空则结束
                    break;
                }
            }
            catch(Exception e){
                LogUtil.logError("积分计算数据处理异常:" + e.getMessage());
                //处理异常数据
                PartitionCacheUtils.lpush(key, data);
            }
        }
        return rest;
    }

    private ApiRest vipScoreDeal(String data){
        ApiRest rest = new ApiRest();
        VipScoreVO vipScoreVO = GsonUntil.jsonAsModel(data, VipScoreVO.class);
        if(vipScoreVO != null && vipScoreVO.getVipId() != null && !vipScoreVO.getVipId().equals(BigInteger.ZERO)){
            Map<String, Object> param = new HashMap<>();
            param.put("id", vipScoreVO.getVipId());
            if(vipScoreVO.getTenantId() != null){
                param.put("tenantId", vipScoreVO.getTenantId());
            }
            Vip vip = vipMapper.findById(param);
            if(vip != null){
                VipType vipType = vipTypeMapper.findTypeById(vip.getTenantId(), vip.getTypeId());
                if(vipType != null){
                    if(vipScoreVO.getScoreType() == null){
                        Date tradeDate = new Date();
                        if(null != vipScoreVO.getTradeDate()){
                            tradeDate = vipScoreVO.getTradeDate();
                        }
                        VipTradeHistory vipTradeHistory = new VipTradeHistory();
                        if(!vipScoreVO.getPaymentCode().equals("HYQB")){
                            vipTradeHistory.setVipId(vip.getId());
                            vipTradeHistory.setPaymentId(vipScoreVO.getPaymentId());
                            vipTradeHistory.setTradeNo(vipScoreVO.getTradeCode());
                            if(vipScoreVO.getIsRefund().equals("0")){
                                vipTradeHistory.setTradeType("3");
                            }
                            else if(vipScoreVO.getIsRefund().equals("1")){
                                vipTradeHistory.setTradeType("5");
                            }
                            if(vipScoreVO.getPaymentCode().equals("JF")){
                                vipTradeHistory.setTradeAmount(vipScoreVO.getAmount().abs());
                                vipTradeHistory.setIntegralAmount(vipScoreVO.getAmount().abs());
                                vipTradeHistory.setPayAmount(BigDecimal.ZERO);
                            }
                            else{
                                if((vipScoreVO.getPaymentCode().equals("YHQ") || vipScoreVO.getPaymentCode().equals("DJQ") || vipScoreVO.getPaymentCode().contains("ZDY")) && vipScoreVO.getIsLongAmount() == 1){
                                    vipTradeHistory.setTradeAmount(vipScoreVO.getAmount().abs().multiply(BigDecimal.valueOf(-1)));
                                    vipTradeHistory.setPayAmount(vipScoreVO.getAmount().abs().multiply(BigDecimal.valueOf(-1)));
                                }
                                else{
                                    vipTradeHistory.setTradeAmount(vipScoreVO.getAmount().abs());
                                    vipTradeHistory.setPayAmount(vipScoreVO.getAmount().abs());
                                }
                            }
                            vipTradeHistory.setTradeBranchId(vipScoreVO.getBranchId());
                            vipTradeHistory.setTradeBranchName(branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), vipScoreVO.getBranchId()) == null ? "" : branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), vipScoreVO.getBranchId()).getName());
                            vipTradeHistory.setTradeDate(tradeDate);
                            Employee employee = employeeMapper.getEmployeeById(vipScoreVO.getEmpId(), vip.getTenantId());
                            vipTradeHistory.setCreateAt(new Date());
                            vipTradeHistory.setCreateBy(employee.getName());
                            vipTradeHistory.setLastUpdateAt(new Date());
                            vipTradeHistory.setLastUpdateBy(employee.getName());
                            vipTradeHistory.setToken("JOB" + vipScoreVO.getTradeCode());
                            vipTradeHistory.setTenantId(vip.getTenantId());
                            vipTradeHistory.setAddScore(BigDecimal.ZERO);
                            int i = vipTradeHistoryMapper.insert(vipTradeHistory);
                            if(i <= 0){
                                LogUtil.logInfo("保存消费记录失败！参数：" + BeanUtils.toJsonStr(vipScoreVO));
                            }
                            //修改累计消费次数及金额
                            if(vip.getBuyTimes() == null){
                                vip.setBuyTimes(0);
                            }
                            if(("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund()))){
                                if(vipScoreVO.getPaymentCode().equals("YHQ") || vipScoreVO.getPaymentCode().equals("DJQ") || vipScoreVO.getPaymentCode().contains("ZDY")){
                                    if(vipScoreVO.getIsLongAmount() == 0){
                                        vip.setBuyTimes(vip.getBuyTimes() - 1);
                                    }
                                }
                                else{
                                    vip.setBuyTimes(vip.getBuyTimes() - 1);
                                }
                            }
                            else if(("0".equals(vipScoreVO.getIsRefund()) || "false".equals(vipScoreVO.getIsRefund()))){
                                if(vipScoreVO.getPaymentCode().equals("YHQ") || vipScoreVO.getPaymentCode().equals("DJQ") || vipScoreVO.getPaymentCode().contains("ZDY")){
                                    if(vipScoreVO.getIsLongAmount() == 0){
                                        vip.setBuyTimes(vip.getBuyTimes() + 1);
                                    }
                                }
                                else{
                                    vip.setBuyTimes(vip.getBuyTimes() + 1);
                                }
                            }
                            if(vip.getSumConsume() == null){
                                vip.setSumConsume(BigDecimal.ZERO);
                            }
                            if(("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund()))){
                                if(vipScoreVO.getPaymentCode().equals("YHQ") || vipScoreVO.getPaymentCode().equals("DJQ") || vipScoreVO.getPaymentCode().contains("ZDY")){
                                    if(vipScoreVO.getIsLongAmount() == 0){
                                        vip.setSumConsume(vip.getSumConsume().subtract(vipScoreVO.getAmount().abs()));
                                    }
                                    else{
                                        vip.setSumConsume(vip.getSumConsume().add(vipScoreVO.getAmount().abs()));
                                    }
                                }
                                else{
                                    vip.setSumConsume(vip.getSumConsume().add(vipScoreVO.getAmount()));
                                }
                            }
                            else if(("0".equals(vipScoreVO.getIsRefund()) || "false".equals(vipScoreVO.getIsRefund()))){
                                if(vipScoreVO.getPaymentCode().equals("YHQ") || vipScoreVO.getPaymentCode().equals("DJQ") || vipScoreVO.getPaymentCode().contains("ZDY")){
                                    if(vipScoreVO.getIsLongAmount() == 0){
                                        vip.setSumConsume(vip.getSumConsume().add(vipScoreVO.getAmount()));
                                    }
                                    else{
                                        vip.setSumConsume(vip.getSumConsume().subtract(vipScoreVO.getAmount().abs()));
                                    }
                                }
                                else{
                                    vip.setSumConsume(vip.getSumConsume().add(vipScoreVO.getAmount()));
                                }
                            }
                            vipMapper.update(vip);
                        }
                    }
                    if(vipType.getScoreType() == 1){
                        if(vipScoreVO.getScoreType() == null){
                            String branchName="";
                            BigDecimal saleAmount=BigDecimal.ZERO;
                            Integer giveScore=0;
                            Payment payment = paymentMapper.getPaymentById(vipScoreVO.getPaymentId(), vipScoreVO.getTenantId());
                            if(payment != null && payment.getIsScore()){
                                Date tradeDate = new Date();
                                if(null != vipScoreVO.getTradeDate()){
                                    tradeDate = vipScoreVO.getTradeDate();
                                }
                                if(vipType.getToSavePoints().doubleValue() == 1 && vipScoreVO.getAmount() != null){
                                    int score = 0;
                                    Map p = new HashMap();
                                    p.put("tenantId", vipScoreVO.getTenantId());
                                    p.put("branchId", vipScoreVO.getBranchId());
                                    p.put("promotionType", 2);
                                    VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(p);
                                    boolean isDouble = false;
                                    boolean isVipBirthday = false;
                                    if(null != vipSpecialPromotion){
                                        if(vipSpecialPromotion.getIsDoubleScoreBirthday()!=null&&vipSpecialPromotion.getIsDoubleScoreBirthday() == 1){
                                            isDouble = true;
                                        }
                                    }
                                    SimpleDateFormat dateF = new SimpleDateFormat("MM-dd");
                                    if(vip.getBirthday() != null && ((dateF.format(vip.getBirthday())).equals(dateF.format(tradeDate)))){
                                        isVipBirthday = true;
                                    }
                                    VipBook vipBook = new VipBook();
                                    vipBook.setTotalScore(vip.getRemainingScore());
                                    if("JF".equals(vipScoreVO.getPaymentCode()) && ("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund())) && vipScoreVO.getAmount().doubleValue() != 0){
                                        int subScore = 0;
                                        if(vipType.getScoreUsage() != null && vipType.getPointsFactor().doubleValue() != 0){
                                            score = (int)vipScoreVO.getAmount().abs().doubleValue() * (int)vipType.getScoreUsage().doubleValue();
                                            subScore = (int)(vipScoreVO.getAmount().abs().doubleValue() / vipType.getPointsFactor().doubleValue());
                                        }
                                        else{
                                            score = vipScoreVO.getAmount().abs().intValue();
                                            subScore = vipScoreVO.getAmount().abs().intValue();
                                        }
                                        score=score-subScore;
                                    }
                                    else{
                                        if(vipType.getPointsFactor() != null && vipType.getPointsFactor().doubleValue() != 0){
                                            score = (int)(vipScoreVO.getAmount().doubleValue() / vipType.getPointsFactor().doubleValue());
                                            if(vipScoreVO.getIsLongAmount()==1){
                                                if("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund())){
                                                    score=Math.abs(score);
                                                }else{
                                                    score=(-1*score);
                                                }
                                            }
                                        }
                                        else{
                                            //默认1比1积分
                                            score = vipScoreVO.getAmount().intValue();
                                            if(vipScoreVO.getIsLongAmount()==1){
                                                if("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund())){
                                                    score=Math.abs(score);
                                                }else{
                                                    score=(-1*score);
                                                }
                                            }
                                        }
                                    }
                                    if(isDouble && isVipBirthday){
                                        score = score * 2;
                                    }
                                    score = score * getVipDayScoreTimes(vipScoreVO.getTenantId(),vipScoreVO.getBranchId(),vipScoreVO.getTradeDate());
                                    //累加积分
                                    if("JF".equals(vipScoreVO.getPaymentCode()) && ("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund())) && vipScoreVO.getAmount().doubleValue() > 0){
                                        if(vip.getSumScore() != null){
                                            param.put("sumScore", vip.getSumScore().intValue() + score);
                                        }
                                        else{
                                            param.put("sumScore", score);
                                        }
                                    }
                                    else{
                                        if(vip.getSumScore() != null){
                                            param.put("sumScore", vip.getSumScore().intValue() + score);
                                        }
                                        else{
                                            param.put("sumScore", score);
                                        }
                                    }
                                    if(vip.getRemainingScore() != null){
                                        param.put("remainingScore", vip.getRemainingScore().intValue() + score);
                                    }
                                    else{
                                        param.put("remainingScore", score);
                                    }
                                    vipMapper.update(param);
                                    Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), vipScoreVO.getBranchId());
                                    if(branch1 != null){
                                        branchName= branch1.getName();
                                    }
                                    WeiXinUtils.updateMemberBonusSafe(vip, score, WeiXinUtils.obtainRecordBonus(1));
                                    //记录积分台帐
                                    vipBook.setVipId(vip.getId());
                                    vipBook.setTenantId(vip.getTenantId());
                                    if(vipScoreVO.getBranchId() != null){
                                        vipBook.setBranchId(vipScoreVO.getBranchId());
                                    }
                                    else{
                                        vipBook.setBranchId(vip.getBranchId());
                                    }
                                    if(vipScoreVO.getEmpId() != null){
                                        vipBook.setEmpId(vipScoreVO.getEmpId());
                                    }
                                    vipBook.setBookType(1);
                                    vipBook.setOperateBy("积分计算任务");
                                    vipBook.setOperateAt(tradeDate);
                                    vipBook.setVipCode(vip.getVipCode());
                                    vipBook.setPaymentCode(vipScoreVO.getTradeCode());
                                    vipBook.setVipScore(new BigDecimal(score));
                                    vipBook.setSumConsume(vipScoreVO.getAmount());
                                    vipBook.setTotal(vipScoreVO.getAmount());
                                    vipBook.setRealTotal(vipScoreVO.getAmount());
                                    int j = vipBookMapper.insert(vipBook);
                                    if(j <= 0){
                                        rest.setIsSuccess(false);
                                        rest.setMessage("积分台账记录失败！");
                                        rest.setError("积分台账记录失败！");
                                    }
                                    if(score > 0){//避免出现退货减少积分发送短信的需求
                                        giveScore=score;
                                        saleAmount=vipScoreVO.getAmount();
                                        if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                            Map smsMap = new HashMap();
                                            DecimalFormat sd=new DecimalFormat("#");
                                            smsMap.put("type", 3);
                                            smsMap.put("tenantId", vip.getTenantId().toString());
                                            BigInteger branchId = vipScoreVO.getBranchId();
                                            smsMap.put("number", vip.getPhone());
                                            if(branch1 != null){
                                                smsMap.put("branchId", branchId.toString());
                                                smsMap.put("content", "消费###" + score + "###" + (sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()).add(BigDecimal.valueOf(score)))));
                                                ApiRest ssr2 = smsService.sendSMS(smsMap);
                                                if(!ssr2.getIsSuccess()){
                                                    LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if(payment!=null&&(payment.getPaymentCode().equals("JF")&&!payment.getIsScore())){
                                Date tradeDate = new Date();
                                if(null != vipScoreVO.getTradeDate()){
                                    tradeDate = vipScoreVO.getTradeDate();
                                }
                                if(vipType.getToSavePoints().doubleValue() == 1 && vipScoreVO.getAmount() != null){
                                    int score = 0;
                                    VipBook vipBook = new VipBook();
                                    vipBook.setTotalScore(vip.getRemainingScore());
                                    if(vipType.getScoreUsage() != null && vipType.getPointsFactor().doubleValue() != 0){
                                        score = (int)vipScoreVO.getAmount().abs().doubleValue() * (int)vipType.getScoreUsage().doubleValue();
                                    }
                                    else{
                                        score = vipScoreVO.getAmount().abs().intValue();
                                    }
                                    //累加积分
                                    if(("1".equals(vipScoreVO.getIsRefund()) || "true".equals(vipScoreVO.getIsRefund())) && vipScoreVO.getAmount().doubleValue() != 0){
                                        /*if(vip.getSumScore() != null){
                                            vip.setSumScore(vip.getSumScore().add(BigDecimal.valueOf(score)));
                                        }
                                        else{
                                            vip.setSumScore(BigDecimal.valueOf(score));
                                        }*/
                                        if(vip.getRemainingScore() != null){
                                            vip.setRemainingScore(vip.getRemainingScore().add(BigDecimal.valueOf(score)));
                                        }
                                        else{
                                            vip.setRemainingScore(BigDecimal.valueOf(score));
                                        }
                                        vipMapper.update(vip);
                                        WeiXinUtils.updateMemberBonusSafe(vip, score, WeiXinUtils.obtainRecordBonus(1));
                                        //记录积分台帐
                                        vipBook.setVipId(vip.getId());
                                        vipBook.setTenantId(vip.getTenantId());
                                        if(vipScoreVO.getBranchId() != null){
                                            vipBook.setBranchId(vipScoreVO.getBranchId());
                                        }
                                        else{
                                            vipBook.setBranchId(vip.getBranchId());
                                        }
                                        if(vipScoreVO.getEmpId() != null){
                                            vipBook.setEmpId(vipScoreVO.getEmpId());
                                        }
                                        vipBook.setBookType(7);
                                        vipBook.setOperateBy("收银员");
                                        vipBook.setOperateAt(tradeDate);
                                        vipBook.setVipCode(vip.getVipCode());
                                        vipBook.setPaymentCode(vipScoreVO.getTradeCode());
                                        vipBook.setVipScore(new BigDecimal(score));
                                        vipBook.setSumConsume(vipScoreVO.getAmount());
                                        vipBook.setTotal(vipScoreVO.getAmount());
                                        vipBook.setRealTotal(vipScoreVO.getAmount());
                                        int j = vipBookMapper.insert(vipBook);
                                        if(j <= 0){
                                            rest.setIsSuccess(false);
                                            rest.setMessage("积分台账记录失败！");
                                            rest.setError("积分台账记录失败！");
                                        }
                                    }
                                }
                            }
                            WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), giveScore, 1);
                        }
                    }
                    else if(vipType.getScoreType() == 2){
                        //如果是赠送商品，则不积分
                        if(vipScoreVO.getScoreType() != null && vipScoreVO.getScoreType().equals("goods") && vipScoreVO.getIsFreeOfCharge() == 0){
                            //按照商品积分，首先查商品是否有积分，若有则按照商品积分，若没有积分，查询分类积分
                            Map p = new HashMap();
                            p.put("tenantId", vip.getTenantId());
                            p.put("goodsId", vipScoreVO.getGoodsId());
                            //p.put("branchId",vipScoreVO.getBranchId());后期分店管理用
                            Map goods = goodsMapper.getGoodsScoreInfo(p);
                            if(goods == null || goods.get("scoreType") == null || Integer.valueOf(goods.get("scoreType").toString()) == 0){//商品不积分
                                //查询分类积分情况
                                if(goods != null){
                                    BigInteger catId = BigInteger.valueOf(Long.valueOf(goods.get("categoryId").toString()));
                                    Map category = categoryMapper.getCategoryScoreInfo(catId, vip.getTenantId());
                                    if((category != null) && category.get("scoreType") != null && (Integer.valueOf(category.get("scoreType").toString()) != 0)){//分类会积分的情况
                                        saveVipBook(vip, vipScoreVO, category);
                                    }
                                }
                            }
                            else{//商品积分
                                saveVipBook(vip, vipScoreVO, goods);
                            }
                        }
                    }
                }
                else{
                    LogUtil.logInfo("会员类型查询失败！参数：VipId" + vip.getId() + "，会员类型typeId=" + vip.getTypeId());
                }
            }
        }
        return rest;
    }

    private void saveVipBook(Vip vip, VipScoreVO vipScoreVO, Map scoreMap){
        String branchName="";
        BigDecimal saleAmount=vipScoreVO.getAmount();
        Integer giveScore=0;
        VipBook vipBook = new VipBook();
        Map p = new HashMap();
        p.put("tenantId", vipScoreVO.getTenantId());
        p.put("branchId", vipScoreVO.getBranchId());
        p.put("promotionType", 2);
        VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(p);
        boolean isDouble = false;
        boolean isVipBirthday = false;
        Date tradeDate = new Date();
        if(null != vipScoreVO.getTradeDate()){
            tradeDate = vipScoreVO.getTradeDate();
        }
        if(null != vipSpecialPromotion){
            if(vipSpecialPromotion.getIsDoubleScoreBirthday()!=null&&vipSpecialPromotion.getIsDoubleScoreBirthday() == 1){
                isDouble = true;
            }
        }
        SimpleDateFormat dateF = new SimpleDateFormat("MM-dd");
        if(vip.getBirthday() != null && ((dateF.format(vip.getBirthday())).equals(dateF.format(tradeDate)))){
            isVipBirthday = true;
        }
        Integer scoreType = Integer.valueOf(scoreMap.get("scoreType").toString());
        BigDecimal scoreValue = scoreMap.get("scoreValue") == null ? BigDecimal.ZERO : BigDecimal.valueOf(Double.valueOf(scoreMap.get("scoreValue").toString()));
        BigDecimal scorePercent = scoreMap.get("scorePercent") == null ? BigDecimal.ZERO : BigDecimal.valueOf(Double.valueOf(scoreMap.get("scorePercent").toString()));
        int score = 0;

        if(scoreType == 1){
            score = (scoreValue.multiply(vipScoreVO.getQuantity())).intValue();
        }
        else if(scoreType == 2){
            score = (scorePercent.multiply(vipScoreVO.getAmount()).multiply(BigDecimal.valueOf(0.01))).intValue();
        }
        if(isDouble && isVipBirthday){
            score = score * 2;
        }
        score = score * getVipDayScoreTimes(vipScoreVO.getTenantId(),vipScoreVO.getBranchId(),vipScoreVO.getTradeDate());
        if(vipScoreVO.getIsRefund().equals("1") || vipScoreVO.getIsRefund().equals("true")){
            score = score * (-1);
        }
        if(score != 0){
            Map param = new HashMap();
            param.put("id", vipScoreVO.getVipId());
            if(vipScoreVO.getTenantId() != null){
                param.put("tenantId", vipScoreVO.getTenantId());
            }
            vipBook.setTotalScore(vip.getRemainingScore());
            if(vip.getSumScore() != null){
                param.put("sumScore", vip.getSumScore().intValue() + score);
            }
            else{
                param.put("sumScore", score);
            }
            if(vip.getRemainingScore() != null){
                param.put("remainingScore", vip.getRemainingScore().intValue() + score);
            }
            else{
                param.put("remainingScore", score);
            }
            vipMapper.update(param);
            Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), vipScoreVO.getBranchId());
            if(branch1 != null){
                branchName=branch1.getName();
            }
            WeiXinUtils.updateMemberBonusSafe(vip, score, WeiXinUtils.obtainRecordBonus(1));
            //记录积分台帐
            vipBook.setVipId(vip.getId());
            vipBook.setTenantId(vip.getTenantId());
            if(vipScoreVO.getBranchId() != null){
                vipBook.setBranchId(vipScoreVO.getBranchId());
            }
            else{
                vipBook.setBranchId(vip.getBranchId());
            }
            if(vipScoreVO.getEmpId() != null){
                vipBook.setEmpId(vipScoreVO.getEmpId());
            }
            vipBook.setBookType(1);
            vipBook.setOperateBy("收银员");
            vipBook.setOperateAt(tradeDate);
            vipBook.setVipCode(vip.getVipCode());
            vipBook.setPaymentCode(vipScoreVO.getTradeCode());
            vipBook.setVipScore(new BigDecimal(score));
            vipBook.setSumConsume(vipScoreVO.getAmount());
            vipBook.setTotal(vipScoreVO.getAmount());
            vipBook.setRealTotal(vipScoreVO.getAmount());
            int j = vipBookMapper.insert(vipBook);
            if(j <= 0){
                LogUtil.logInfo("保存积分记录失败！");
            }
            if(score > 0){//避免出现退货减少积分发送短信的需求
                giveScore=score;
                if(vip.getPhone() != null && !vip.getPhone().equals("")){
                    Map smsMap = new HashMap();
                    DecimalFormat sd=new DecimalFormat("#");
                    smsMap.put("type", 3);
                    smsMap.put("tenantId", vip.getTenantId().toString());
                    BigInteger branchId = vipScoreVO.getBranchId();
                    smsMap.put("number", vip.getPhone());
                    if(branch1 != null){
                        branchName=branch1.getName();
                        smsMap.put("branchId", branchId.toString());
                        smsMap.put("content", "消费###" + score + "###" + (sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()).add(BigDecimal.valueOf(score)))));
                        ApiRest ssr2 = smsService.sendSMS(smsMap);
                        if(!ssr2.getIsSuccess()){
                            LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                        }
                    }
                }
            }
            WeiXinUtils.sendPointChangeTemplateSafe(vip.getId(), giveScore, 1);
        }
    }

//    def setToRedis() {
//        ApiRest rest = new ApiRest();
//        for (int i = 1; i < 5; i++) {
//            VipScoreVO vipScoreVO = new VipScoreVO();
//            vipScoreVO.vipId = 35;
//            vipScoreVO.tradeCode = i + "M";
//            vipScoreVO.amount = i * 100;
//            PartitionCacheUtils.rpush(PropertyUtils.getDefault("o2o_web_topics"), GsonUntil.objectToJson(vipScoreVO));
//        }
//        return rest;
//    }
//

    /**
     * 获取会员日积分倍数，无设置返回1倍
     * @return
     */
    public Integer getVipDayScoreTimes(BigInteger tenantId, BigInteger branchId, Date tradeDate){
        Integer result = 1;
        try{
            Map p = new HashMap();
            p.put("tenantId", tenantId);
            p.put("branchId", branchId);
            p.put("promotionType", 3);
            VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(p);
            if(vipSpecialPromotion != null && tradeDate!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(sdf.format(tradeDate)));
                int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                String week=weekDays[w];//获取周次
                Integer day=cal.get(cal.DATE);//获取日
                Integer type = vipSpecialPromotion.getPromotionType();
                boolean isVipDayScore=false;
                if(type == 3 && StringUtils.isNotEmpty(vipSpecialPromotion.getWeeksSetting())){//按周
                    for(String weekStr:vipSpecialPromotion.getWeeksSetting().split(",")){
                        if(weekStr.equals(week)){
                            isVipDayScore=true;
                            continue;
                        }
                    }
                }
                else if(type == 4 && StringUtils.isNotEmpty(vipSpecialPromotion.getDaysSetting())){//按日
                    for(String datStr:vipSpecialPromotion.getDaysSetting().split(",")){
                        if(datStr.equals(day.toString())){
                            isVipDayScore=true;
                            continue;
                        }
                    }
                }
                if(isVipDayScore){
                    result=vipSpecialPromotion.getVipDayTimesScore();
                }
            }
        }
        catch(Exception e){
            LogUtil.logInfo("vipService-getVipDayScoreTimes-" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据tenantId、会员类型查询积分规则
     *
     * @author szq
     */
    public ApiRest qryVipScoreRule(BigInteger tenantId, BigInteger vipTypeId){
        ApiRest rest = new ApiRest();
        Map par = new HashMap();
        par.put("id", vipTypeId);
        par.put("tenantId", tenantId);
        VipType vipType = vipTypeMapper.findByCondition(par);
        if(vipType != null){
            Map resultMap = new HashMap();
            resultMap.put("toSavePoints", vipType.getToSavePoints());
            resultMap.put("pointsFactor", vipType.getPointsFactor());
            resultMap.put("scoreUsage", vipType.getScoreUsage());
            rest.setData(resultMap);
            rest.setMessage("查询成功");
            rest.setIsSuccess(true);
        }
        else{
            rest.setData(null);
            rest.setError("该会员类型不存在");
            rest.setMessage("该会员类型不存在");
            rest.setIsSuccess(false);
        }
        return rest;
    }

    /**
     * 会员认证(与pos实体卡)合并会员信息
     *
     * @param params
     */
    public ApiRest vipCombine(Map params){
        ApiRest rest = new ApiRest();
        LogUtil.logInfo("调试：合并会员:vipId:" + params.get("wxVipId") + ",phone:" + params.get("phone") + "tenantId:" + params.get("tenantId"));
        if(params.get("wxVipId") != null && params.get("phone") != null && params.get("tenantId") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("phone", params.get("phone"));
            param.put("tenantId", params.get("tenantId"));
            List<Vip> cardVips = vipMapper.select(param);
            if(cardVips != null && cardVips.size() > 0){
                Vip cardVip = cardVips.get(0);
                if(cardVip.getOriginalId() != null && !"".equals(cardVip.getOriginalId())){
                    rest.setIsSuccess(false);
                    rest.setMessage("该手机已经绑定其他会员！");
                }
                else{
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("id", params.get("wxVipId"));
                    Vip wxVip = vipMapper.findByCondition(param1);
                    if(wxVip != null && wxVip.getTenantId().equals(new BigInteger(params.get("tenantId").toString()))){
                        //合并会员账号信息
                        cardVip.setOriginalId(wxVip.getOriginalId());
                        if(cardVip.getBuyTimes() == null){
                            cardVip.setBuyTimes(0);
                        }
                        if(wxVip.getBuyTimes() == null){
                            wxVip.setBuyTimes(0);
                        }
                        cardVip.setBuyTimes(cardVip.getBuyTimes() + wxVip.getBuyTimes());
                        if(cardVip.getSumConsume() == null){
                            cardVip.setSumConsume(new BigDecimal(0));
                        }
                        if(wxVip.getSumConsume() == null){
                            wxVip.setSumConsume(new BigDecimal(0));
                        }
                        else if(cardVip.getSumConsume() != null){
                            cardVip.setSumConsume(cardVip.getSumConsume().add(wxVip.getSumConsume()));
                        }
                        if(cardVip.getSumScore() == null){
                            cardVip.setSumScore(new BigDecimal(0));
                        }
                        if(wxVip.getSumScore() == null){
                            wxVip.setSumConsume(new BigDecimal(0));
                        }
                        else if(cardVip.getSumScore() != null){
                            cardVip.setSumScore(cardVip.getSumScore().add(wxVip.getSumScore()));
                        }
                        if(cardVip.getRemainingScore() == null){
                            cardVip.setRemainingScore(new BigDecimal(0));
                        }
                        if(wxVip.getRemainingScore() == null){
                            wxVip.setRemainingScore(new BigDecimal(0));
                        }
                        else if(cardVip.getRemainingScore() != null){
                            cardVip.setRemainingScore(cardVip.getRemainingScore().add(wxVip.getRemainingScore()));
                        }
                        if(cardVip.getOverscore() == null){
                            cardVip.setOverscore(new BigDecimal(0));
                        }
                        if(wxVip.getOverscore() == null){
                            wxVip.setOverscore(new BigDecimal(0));
                        }
                        else if(cardVip.getOverscore() != null){
                            cardVip.setOverscore(cardVip.getOverscore().add(wxVip.getOverscore()));
                        }
                        if(cardVip.getVipStore() == null){
                            cardVip.setVipStore(new BigDecimal(0));
                        }
                        if(wxVip.getVipStore() == null){
                            wxVip.setVipStore(new BigDecimal(0));
                        }
                        else if(cardVip.getVipStore() != null){
                            cardVip.setVipStore(cardVip.getVipStore().add(wxVip.getVipStore()));
                        }
                        if(cardVip.getVipStoreTotal() == null){
                            cardVip.setVipStoreTotal(new BigDecimal(0));
                        }
                        if(wxVip.getVipStoreTotal() == null){
                            wxVip.setVipStoreTotal(new BigDecimal(0));
                        }
                        else if(cardVip.getVipStoreTotal() != null){
                            cardVip.setVipStoreTotal(cardVip.getVipStoreTotal().add(wxVip.getVipStoreTotal()));
                        }

                        //转移消费历史记录
                        Map<String, Object> param2 = new HashMap<>();
                        param2.put("vipId", wxVip.getId());
                        param2.put("tenantId", wxVip.getTenantId());
                        List<VipTradeHistory> tradeHistories = vipTradeHistoryMapper.vipTradeList(param2);
                        for(VipTradeHistory trade : tradeHistories){
                            trade.setVipId(cardVip.getId());
                            vipTradeHistoryMapper.update1(trade);
                        }
                        //积分历史
                        List<VipBook> vipBooks = vipBookMapper.vipBookList(param2);
                        for(VipBook book : vipBooks){
                            book.setVipId(cardVip.getId());
                            book.setVipCode(cardVip.getVipCode());
                            vipBookMapper.update(book);
                        }
                        //订单
                        List<DietOrderInfo> orders = dietOrderInfoMapper.select(param2);
                        for(DietOrderInfo order : orders){
                            order.setVipId(cardVip.getId());
                            order.setLastUpdateAt(new Date());
                            dietOrderInfoMapper.update(order);
                        }

                        vipMapper.update(cardVip);
                        //合并成功删除微信会换
                        wxVip.setIsDeleted(true);
                        vipMapper.update(wxVip);

                        rest.setData(cardVip);
                        rest.setIsSuccess(true);
                        rest.setMessage("该手机已绑定实体卡,合并成功！");
                    }
                }
            }
            else{
                rest.setIsSuccess(true);
                rest.setMessage("该手机未注册会员！");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("tenantId、wxVipId或phone不存在！");
        }
        return rest;
    }

    public ResultJSON listVipTrade(Map params) throws ServiceException{
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<Map> list = vipMapper.listVipTrade(params);
        for(int i = 0; i < list.size(); i++){
            if("1".equals(list.get(i).get("tradeType")) || "4".equals(list.get(i).get("tradeType"))){
                list.get(i).put("storeAmount", list.get(i).get("tradeAmount"));
            }
            else{
                list.get(i).put("storeAmount", 0);
            }
        }
        Map<String, Object> paramsNotPage = new HashMap<>();
        paramsNotPage.put("tenantId", params.get("tenantId"));
        paramsNotPage.put("branchId", params.get("branchId"));
        paramsNotPage.put("vipId", params.get("vipId"));
        paramsNotPage.put("startDate", params.get("startDate"));
        paramsNotPage.put("endDate", params.get("endDate"));
        paramsNotPage.put("vipTypeName", params.get("vipTypeName"));
        if(params.get("resource") != null){
            paramsNotPage.put("resource", params.get("resource"));
        }
        if(params.get("paymentId") != null){
            paramsNotPage.put("paymentId", params.get("paymentId"));
        }
        if(params.get("orderResource") != null){
            paramsNotPage.put("orderResource", params.get("orderResource"));
        }
        List<Map> listAll = vipMapper.listVipTrade(paramsNotPage);
        BigDecimal sumStoreAmount = new BigDecimal("0");
        BigDecimal sumTradeAmount = new BigDecimal("0");
        BigDecimal sumAccumulateAmount = new BigDecimal("0");
        BigDecimal sumIntegralAmount = new BigDecimal("0");
        BigDecimal sumAddScore = new BigDecimal("0");
        for(int i = 0; i < listAll.size(); i++){
            if("4".equals(listAll.get(i).get("tradeType"))){
                if(listAll.get(i).get("tradeAmount") != null){
                    sumTradeAmount = sumTradeAmount.subtract((BigDecimal)listAll.get(i).get("tradeAmount"));
                }
                /*if(listAll.get(i).get("integralAmount") != null){
                    sumTradeAmount = sumTradeAmount.subtract((BigDecimal)listAll.get(i).get("integralAmount"));
                }*/
                sumAccumulateAmount = sumAccumulateAmount.subtract((BigDecimal)listAll.get(i).get("tradeAmount"));
            }
            else{
                if(listAll.get(i).get("tradeAmount") != null){
                    sumTradeAmount = sumTradeAmount.add((BigDecimal)listAll.get(i).get("tradeAmount"));
                }
                /*if(listAll.get(i).get("integralAmount") != null){
                    sumTradeAmount = sumTradeAmount.add((BigDecimal)listAll.get(i).get("integralAmount"));
                }*/
                sumAccumulateAmount = sumAccumulateAmount.add((BigDecimal)listAll.get(i).get("tradeAmount"));
            }
            BigDecimal amount = new BigDecimal("0");
            if("1".equals(listAll.get(i).get("tradeType"))){
                if(listAll.get(i).get("tradeAmount") != null){
                    amount = (BigDecimal)listAll.get(i).get("tradeAmount");
                    sumStoreAmount = sumStoreAmount.add(amount);
                }
            }
            else if("4".equals(listAll.get(i).get("tradeType"))){
                if(listAll.get(i).get("tradeAmount") != null){
                    amount = (BigDecimal)listAll.get(i).get("tradeAmount");
                    sumStoreAmount = sumStoreAmount.subtract(amount);
                }
            }
            else{
                sumStoreAmount = sumStoreAmount.add(amount);
            }

            if("4".equals(listAll.get(i).get("tradeType"))){
                if(listAll.get(i).get("integralAmount") != null){
                    sumIntegralAmount = sumIntegralAmount.subtract((BigDecimal)listAll.get(i).get("integralAmount"));
                }
            }
            else{
                if(listAll.get(i).get("integralAmount") != null){
                    sumIntegralAmount = sumIntegralAmount.add((BigDecimal)listAll.get(i).get("integralAmount"));
                }
            }
            if(listAll.get(i).get("addScore") != null){
                sumAddScore = sumAddScore.add((BigDecimal)listAll.get(i).get("addScore"));
            }
        }
        map.put("rows", list);
        map.put("total", listAll.size() > 0 ? listAll.size() : 0);
        Map<String, Object> saleTrendReport = new HashMap<>();
        List<Map> saleTrendReportMap = new ArrayList<>();
        saleTrendReport.put("storeAmount", sumStoreAmount);
        saleTrendReport.put("tradeAmount", sumTradeAmount);
//            saleTrendReport.put("accumulateAmount",sumAccumulateAmount);
        saleTrendReport.put("integralAmount", sumIntegralAmount);
        saleTrendReport.put("addScore", sumAddScore);
        saleTrendReportMap.add(saleTrendReport);
        map.put("footer", saleTrendReportMap);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }

    public ResultJSON listVipStore(Map params) throws ServiceException{
        ResultJSON resultJSON = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        if(params.get("newData") != null && !"".equals(params.get("newData"))){
            params.put("newData", "true");
        }
        List<Map> list = vipMapper.listVipStore(params);
        Map<String, Object> paramsNotPage = new HashMap<>();
        paramsNotPage.put("tenantId", params.get("tenantId"));
        paramsNotPage.put("branchId", params.get("branchId"));
        paramsNotPage.put("id", params.get("id"));
        paramsNotPage.put("vipId", params.get("vipId"));
        paramsNotPage.put("storeType", params.get("storeType"));
        paramsNotPage.put("startDate", params.get("startDate"));
        paramsNotPage.put("endDate", params.get("endDate"));
        paramsNotPage.put("vipTypeName", params.get("vipTypeName"));
        paramsNotPage.put("paymentType", params.get("paymentType"));
        paramsNotPage.put("storeFrom", params.get("storeFrom"));
        paramsNotPage.put("ruleDetailId", params.get("ruleDetailId"));
        List<Map> listAll = vipMapper.listVipStore(paramsNotPage);
        BigDecimal sumTradeAmount = new BigDecimal("0");
        BigDecimal sumPayAmount = new BigDecimal("0");
        for(int i = 0; i < listAll.size(); i++){
            if(listAll.get(i).get("payAmount") != null){
                if("2".equals(listAll.get(i).get("storeType"))){
                    sumTradeAmount = sumTradeAmount.subtract((BigDecimal)listAll.get(i).get("payAmount"));
                }
                else{
                    if("4".equals(listAll.get(i).get("storeType"))){
                        sumTradeAmount = sumTradeAmount.add(BigDecimal.ZERO);
                    }
                    else if("5".equals(listAll.get(i).get("storeType"))){
                        sumTradeAmount = sumTradeAmount.subtract((BigDecimal)listAll.get(i).get("payAmount"));
                    }
                    else{
                        sumTradeAmount = sumTradeAmount.add((BigDecimal)listAll.get(i).get("payAmount"));
                    }
                }
            }
            if(listAll.get(i).get("giftAmount") != null){
                if("2".equals(listAll.get(i).get("storeType"))){
                    sumPayAmount = sumPayAmount.subtract((BigDecimal)listAll.get(i).get("giftAmount"));
                }
                else{
                    if("4".equals(listAll.get(i).get("storeType"))){
                        sumPayAmount = sumPayAmount.add(BigDecimal.ZERO);
                    }
                    else{
                        sumPayAmount = sumPayAmount.add((BigDecimal)listAll.get(i).get("giftAmount"));
                    }
                }
            }
        }
        map.put("rows", list);
        map.put("total", listAll.size() > 0 ? listAll.size() : 0);
        Map<String, Object> saleTrendReport = new HashMap<>();
        List<Map> saleTrendReportMap = new ArrayList<>();
        saleTrendReport.put("payAmount", sumTradeAmount);
        saleTrendReport.put("giftAmount", sumPayAmount);
        saleTrendReportMap.add(saleTrendReport);
        map.put("footer", saleTrendReportMap);
        resultJSON.setJsonMap(map);
        return resultJSON;
    }


    /**
     * @param type
     * @return
     */
    protected ResultJSON minusLimit(String type, BigInteger tenantId){
        ResultJSON resultJSON = new ResultJSON();
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        if(tenantConfig != null){
            tenantConfig.setValue(String.valueOf(Integer.parseInt(tenantConfig.getValue()) - 1));
            tenantConfigMapper.update(tenantConfig);
            resultJSON.setSuccess("true");
            resultJSON.setIsSuccess(true);
            resultJSON.setMsg("条数更新成功");
        }
        else{
            resultJSON.setSuccess("false");
            resultJSON.setIsSuccess(false);
            resultJSON.setMsg("条数更新失败,不存在对应计数器");
        }
        return resultJSON;
    }

    protected boolean isInLimit(String type, BigInteger tenantId){
        //会员
        int yipSsConfig = 99999;
        boolean isInLimit = true;
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        if(null != tenantConfig){
            if(tenantConfig.getMaxValue() != null){
                yipSsConfig = Integer.parseInt(tenantConfig.getMaxValue());
            }
            // 限制计数器参数不为空,则判断其是否超出限制
            if(yipSsConfig <= Integer.parseInt(tenantConfig.getValue())){
                // 设置为超限
                isInLimit = false;
            }
        }
        else{
            // 如果限制计数器不存在,那么创建限制计数器
            // 存在条目限制配置,则创建计数器
            TenantConfig t = new TenantConfig();
            t.setName(type);
            t.setValue("0");
            t.setMaxValue("99999");
            t.setTenantId(tenantId);
            t.setCreateAt(new Date());
            tenantConfigMapper.insert(t);
        }
        return isInLimit;
    }

    /**
     * @param type
     * @return
     */
    protected ResultJSON addLimit(String type, BigInteger tenantId){
        ResultJSON resultJSON = new ResultJSON();
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        if(tenantConfig != null){
            tenantConfig.setValue(String.valueOf(Integer.parseInt(tenantConfig.getValue()) + 1));
            tenantConfigMapper.update(tenantConfig);
            resultJSON.setSuccess("true");
            resultJSON.setIsSuccess(true);
            resultJSON.setMsg("条数更新成功");
        }
        else{
            resultJSON.setSuccess("false");
            resultJSON.setIsSuccess(false);
            resultJSON.setMsg("条数更新失败,不存在对应计数器");
        }
        return resultJSON;
    }

    protected ResultJSON addLimit(String type, BigInteger tenantId, int count){
        ResultJSON resultJSON = new ResultJSON();
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId(type, tenantId);
        if(tenantConfig != null){
            tenantConfig.setValue(String.valueOf(Integer.parseInt(tenantConfig.getValue()) + count));
            tenantConfigMapper.update(tenantConfig);
            resultJSON.setSuccess("true");
            resultJSON.setIsSuccess(true);
            resultJSON.setMsg("条数更新成功");
        }
        else{
            resultJSON.setSuccess("false");
            resultJSON.setIsSuccess(false);
            resultJSON.setMsg("条数更新失败,不存在对应计数器");
        }
        return resultJSON;
    }

    /**
     * 查询限制数量已方便提示
     *
     * @param type SysConfig内的静态变量
     * @return 限制数量
     */
    protected String getLimit(String type){
        return tenantConfigMapper.findByNameAndTenantId(type, null).getValue();
    }

    /**
     * 获得新增会员的编码
     *
     * @return
     * @throws ServiceException
     */
    public ResultJSON getNextVipCode(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        VipAddress vipAddr = null;
        if(isInLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()))){
            Map<String, Object> param = new HashMap<>();
            param.put("branchType", 0);
            param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
            Branch branch = branchMapper.find(param);
            // 自动生成会员编号
            SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
            if(branch != null){
                String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                result.setObject(code);
                result.setSuccess("0");
                result.setMsg("会员编码查询成功");
            }
            else{
                result.setSuccess("1");
                result.setMsg("没有查询到该商户下的总店!");
            }
        }
        else{
            result.setSuccess("1");
            result.setMsg("会员编码查询失败");
        }
        return result;
    }

    /**
     * 增加、扣减积分
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest addVipScore(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        if(params.get("tenantId") != null && params.get("vipId") != null && params.get("amount") != null && params.get("bookType") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", params.get("vipId"));
            map.put("tenantId", params.get("tenantId"));
            Vip vip = vipMapper.findByCondition(map);
            if(vip != null){
                VipBook vipBook = new VipBook();
                vipBook.setVipId(new BigInteger(params.get("vipId").toString()));
                vipBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
                vipBook.setVipCode(vip.getVipCode());
                if(params.get("branchId") != null){
                    vipBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                }
                else{
                    vipBook.setBranchId(vip.getBranchId());
                }
                if(params.get("createBy") != null){
                    vipBook.setCreateBy(params.get("createBy").toString());
                    vipBook.setLastUpdateBy(params.get("createBy").toString());
                    vipBook.setOperateBy(params.get("createBy").toString());
                }
                if(params.get("paymentCode") != null){
                    vipBook.setPaymentCode(params.get("paymentCode").toString());
                }
                if(params.get("memo") != null){
                    vipBook.setMemo(params.get("memo").toString());
                }
                //bookType 2-使用积分3-赠送积分
                if("2".equals(params.get("bookType"))){
                    vipBook.setBookType(2);
                    if(vip.getRemainingScore() != null && vip.getRemainingScore().doubleValue() >= Double.parseDouble(params.get("amount").toString())){
                        vipBook.setTotalScore(vip.getRemainingScore());
                        vipBook.setVipScore(new BigDecimal(params.get("amount").toString()).abs());
                        vip.setRemainingScore(vip.getRemainingScore().add(new BigDecimal(params.get("amount").toString()).multiply(new BigDecimal("-1"))));
                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("积分余额不足。");
                        return rest;
                    }
                }
                else if("3".equals(params.get("bookType"))){
                    vipBook.setBookType(3);
                    if(vip.getRemainingScore() != null){
                        vipBook.setTotalScore(vip.getRemainingScore());
                        vip.setSumScore((vip.getSumScore()==null?BigDecimal.ZERO:vip.getSumScore()).add(new BigDecimal(params.get("amount").toString())));
                        vip.setRemainingScore(vip.getRemainingScore().add(new BigDecimal(params.get("amount").toString())));
                    }
                    else{
                        vipBook.setTotalScore(BigDecimal.ZERO);
                        vip.setRemainingScore(new BigDecimal(params.get("amount").toString()));
                        vip.setSumScore(new BigDecimal(params.get("amount").toString()));
                    }
                    vipBook.setVipScore(new BigDecimal(params.get("amount").toString()));
                }
                vip.setLastUpdateAt(new Date());

                vipBook.setIsDeleted(false);
                vipBook.setCreateAt(new Date());
                vipBook.setLastUpdateAt(new Date());
                vipBook.setOperateAt(new Date());
                int result1 = vipBookMapper.insert(vipBook);
                int result2 = vipMapper.update(vip);
                if(vip.getPhone() != null && !vip.getPhone().equals("")){
                    Map smsMap = new HashMap();
                    if("2".equals(params.get("bookType"))){
                        smsMap.put("type", 4);
                    }else if("3".equals(params.get("bookType"))){
                        smsMap.put("type", 3);
                    }
                    smsMap.put("tenantId", vip.getTenantId().toString());
                    BigInteger branchId = null;
                    if(params.get("branchId") != null){
                        branchId = new BigInteger(params.get("branchId").toString());
                    }
                    else{
                        branchId = vip.getBranchId();
                    }
                    DecimalFormat sd=new DecimalFormat("#");
                    smsMap.put("number", vip.getPhone());
                    Branch branch1 = branchMapper.findBranchByTenantIdAndBranchId(vip.getTenantId(), branchId);
                    if(branch1 != null){
                        smsMap.put("branchId", branchId.toString());
                        smsMap.put("content", "积分调整###"+sd.format(BigDecimal.valueOf(Double.valueOf(params.get("amount").toString())))+"###"+(sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()))));
                        ApiRest ssr = smsService.sendSMS(smsMap);
                        if(!ssr.getIsSuccess()){
                            LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                        }
                    }
                }

                WeiXinUtils.updateMemberBonusSafe(vip, WeiXinUtils.obtainRecordBonus(vipBook.getBookType()));

                if(result1 == 1 && result2 == 1){
                    rest.setIsSuccess(true);
                    rest.setMessage("操作成功！");
                }
            }

        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("参数不能为空！");
        }
        return rest;
    }

    /**
     * 会员卡退卡
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest refundCard(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        if(params.get("cardCode") != null && params.get("tenantId") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("code", params.get("cardCode"));
            map.put("branchId", params.get("branchId"));
            Card card = icService.findCardByGroup(map);
            if(card != null && card.getHolderId() != null && card.getType() == 4 && card.getState() == 1){
                CardBook cardBook = new CardBook();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("tenantId", params.get("tenantId"));
                map1.put("id", card.getHolderId());
                Vip vip = vipMapper.findById(map1);
                if(vip != null){
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("tenantId", params.get("tenantId"));
                    map2.put("id", vip.getTypeId());
                    VipType vipType = vipTypeMapper.findByCondition(map2);
                    if(vipType != null && vipType.isAllowRefund()){
                        card.setLastUpdateAt(new Date());
                        card.setState(0);
                        card.setDeleted(true);
                        cardMapper.updateCard(card);
                        cardBook.setTenantId(new BigInteger(params.get("tenantId").toString()));
                        if(params.get("branchId") != null){
                            cardBook.setBranchId(new BigInteger(params.get("branchId").toString()));
                        }
                        else{
                            cardBook.setBranchId(vip.getBranchId());
                        }
                        cardBook.setCardType(card.getType().toString());
                        cardBook.setOperateType("4");
                        cardBook.setVipStore(vip.getVipStore());
                        cardBook.setVipStoreAfter(BigDecimal.ZERO);
                        //会员退卡记录退押金
                            /*if(vipType.getDeposit() != null){
                                cardBook.setDeposit(new BigDecimal(String.valueOf(vipType.getDeposit().doubleValue() * -1)));
                            }*/
                        if(params.get("remark") != null){
                            cardBook.setRemark(params.get("remark").toString());
                        }
                        cardBook.setLastUpdateAt(new Date());
                        cardBook.setCreateAt(new Date());
                        cardBook.setHolderId(vip.getId());
                        cardBook.setIsDeleted(false);
                        if(params.get("inputAmount") != null){
                            cardBook.setInputAmount(new BigDecimal(params.get("inputAmount").toString()));
                        }

                        cardMapper.insertCardBook(cardBook);
                        rest.setIsSuccess(true);
                        rest.setMessage("会员卡退卡成功!");
                        rest.setData(vip);

                        /*退卡记录 1.积分变动，2.储值变动*/
                        VipBook vipBook = new VipBook();
                        vipBook.setTenantId(cardBook.getTenantId());
                        vipBook.setBranchId(cardBook.getBranchId());
                        vipBook.setBookType(5);
                        vipBook.setOperateBy(cardBook.getCreateBy());
                        vipBook.setOperateAt(cardBook.getCreateAt());
                        vipBook.setVipCode(vip.getVipCode());
                        //生成流水号
                        //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
                        String code = tenantConfigService.getToday("TK", 8);//"TK" + format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, vip.getTenantId());
                        vipBook.setPaymentCode(code);
                        vipBook.setTotalScore(BigDecimal.ZERO);
                        vipBook.setVipScore(vip.getRemainingScore());
                        vipBook.setSumConsume(BigDecimal.ZERO);
                        vipBook.setTotal(BigDecimal.ZERO);
                        vipBook.setRealTotal(BigDecimal.ZERO);
                        vipBook.setMemo("会员退卡，积分清零");
                        vipBook.setCreateBy(cardBook.getCreateBy());
                        vipBook.setCreateAt(cardBook.getCreateAt());
                        vipBook.setLastUpdateBy(cardBook.getLastUpdateBy());
                        vipBook.setLastUpdateAt(cardBook.getLastUpdateAt());
                        vipBook.setIsDeleted(false);
                        vipBook.setVipId(vip.getId());
                        vipBook.setStoreFrom("0");
                        vipBookMapper.insert(vipBook);

                        VipStoreHistory vipStoreHistory = new VipStoreHistory();
                        vipStoreHistory.setVipId(vip.getId());
                        vipStoreHistory.setStoreCode(code);
                        vipStoreHistory.setStoreType("5");
                        vipStoreHistory.setPayAmount(vip.getVipStore());
                        vipStoreHistory.setPayType(cardBook.getPayType());
                        vipStoreHistory.setGiftAmount(BigDecimal.ZERO);
                        vipStoreHistory.setStoreBranchId(cardBook.getBranchId());
                        vipStoreHistory.setStoreDate(cardBook.getCreateAt());
                        vipStoreHistory.setRemark("会员退卡，返还余额");
                        vipStoreHistory.setCreateBy(cardBook.getCreateBy());
                        vipStoreHistory.setCreateAt(cardBook.getCreateAt());
                        vipStoreHistory.setLastUpdateBy(cardBook.getLastUpdateBy());
                        vipStoreHistory.setLastUpdateAt(cardBook.getLastUpdateAt());
                        vipStoreHistory.setIsDeleted(false);
                        vipStoreHistory.setVersion(BigInteger.ZERO);
                        vipStoreHistory.setStoreFrom("0");
                        vipStoreHistory.setVipOperStore(BigDecimal.ZERO);
                        vipStoreHistory.setTenantId(vip.getTenantId());
                        //会员退卡记录退押金
                            /*if(vipType.getDeposit() != null){
                                vipStoreHistory.setDeposit(new BigDecimal(String.valueOf(vipType.getDeposit().doubleValue() * -1)));
                            }*/
                        vipStoreHistoryMapper.insert(vipStoreHistory);

                        vip.setStatus(1);
                        vip.setIsDeleted(true);
                        vip.setVipStore(BigDecimal.ZERO);
                        vip.setLastUpdateAt(new Date());
                        if(params.get("createBy") != null){
                            vip.setLastUpdateBy(params.get("createBy").toString());
                            card.setLastUpdateBy(params.get("createBy").toString());
                            cardBook.setCreateBy(params.get("createBy").toString());
                            cardBook.setLastUpdateBy(params.get("createBy").toString());
                        }
                        vipMapper.update(vip);

                    }
                    else{
                        rest.setIsSuccess(false);
                        rest.setMessage("该会员卡不允许退卡!");
                    }
                }
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("会员卡不存在或已停用!");
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("参数不能为null!");
        }
        return rest;
    }

    /**
     * 储值退款
     *
     * @return
     * @throws ServiceException
     */
    public ApiRest refundVipStore(Map params) throws ServiceException{
        ApiRest rest = new ApiRest();
        if(params.get("vipId") != null && params.get("tenantId") != null){
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tenantId", params.get("tenantId"));
            map1.put("id", params.get("vipId"));
            Vip vip = vipMapper.findById(map1);
            if(vip != null){
                Map<String, Object> map2 = new HashMap<>();
                map2.put("tenantId", params.get("tenantId"));
                map2.put("id", vip.getTypeId());
                VipType vipType = vipTypeMapper.findByCondition(map2);
                if(vipType != null && vipType.isAllowRefund()){
                    String createBy = "";
                    if(params.get("createBy") != null){
                        createBy = params.get("createBy").toString();
                    }
                    VipStoreHistory vipStoreHistory = new VipStoreHistory();
                    String code = tenantConfigService.getToday("TK", 8);
                    vipStoreHistory.setVipId(vip.getId());
                    vipStoreHistory.setStoreCode(code);
                    vipStoreHistory.setStoreType("5");
                    vipStoreHistory.setPayAmount(vip.getVipStore());
                    vipStoreHistory.setPayType(null);
                    vipStoreHistory.setGiftAmount(BigDecimal.ZERO);
                    if(params.get("branchId") != null){
                        vipStoreHistory.setStoreBranchId(new BigInteger(params.get("branchId").toString()));
                    }
                    else{
                        vipStoreHistory.setStoreBranchId(vip.getBranchId());
                    }
                    vipStoreHistory.setStoreDate(new Date());
                    vipStoreHistory.setRemark("会员退款，返还余额");
                    vipStoreHistory.setCreateBy(createBy);
                    vipStoreHistory.setCreateAt(new Date());
                    vipStoreHistory.setLastUpdateBy(createBy);
                    vipStoreHistory.setLastUpdateAt(new Date());
                    vipStoreHistory.setIsDeleted(false);
                    vipStoreHistory.setVersion(BigInteger.ZERO);
                    vipStoreHistory.setStoreFrom("0");
                    vipStoreHistory.setVipOperStore(BigDecimal.ZERO);
                    vipStoreHistory.setTenantId(vip.getTenantId());
                    //会员退卡记录退押金
                            /*if(vipType.getDeposit() != null){
                                vipStoreHistory.setDeposit(new BigDecimal(String.valueOf(vipType.getDeposit().doubleValue() * -1)));
                            }*/
                    vipStoreHistoryMapper.insert(vipStoreHistory);
                    vip.setLastUpdateAt(new Date());
                    vip.setLastUpdateBy(createBy);
                    vip.setVipStore(BigDecimal.ZERO);
                    vipMapper.update(vip);
                    rest.setIsSuccess(true);
                    rest.setMessage("退款成功!");
                    rest.setData(vip);

                }
                else{
                    rest.setIsSuccess(false);
                    rest.setMessage("该会员卡不允许退款!");
                }
            }
        }
        else{
            rest.setIsSuccess(false);
            rest.setMessage("参数不能为null!");
        }
        return rest;
    }

    /**
     * 查询会员统计数据
     *
     * @return
     */
    public ApiRest qryVipStatistics(Map params){
        ApiRest rest = new ApiRest();
        Map<String, Object> param1 = new HashMap<>();
        param1.put("tenantId", params.get("tenantId"));
        Map<String, Object> param0 = new HashMap<>();
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            param0.put("branchId", params.get("branchId"));
            param1.put("branchId", params.get("branchId"));
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            param.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                param.remove("id");
                param.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(param);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    param1.remove("branchId");
                    param1.put("groups", bIds);
                    param0.put("groups", bIds);
                    param0.remove("branchId");
                }
            }
        }
        Map<String, Object> data = new HashMap<>();
        List<Vip> vips = vipMapper.select(param1);
        if(vips != null && vips.size() > 0){
            //会员总数
            data.put("vipCount", vips.size());
            //总储值余额
            double totalVipStore = 0;
            //新增会员总数
            int newVipCount = 0;
            //今日储值总额
            double todayStore = 0;
            //今日储值消费总额
            double todayStorePay = 0;
            for(Vip vip : vips){
                if(vip.getVipStore() != null){
                    totalVipStore += vip.getVipStore().doubleValue();
                }
                Calendar today = Calendar.getInstance();
                if(vip.getCreateAt() != null){
                    Calendar createAt = Calendar.getInstance();
                    createAt.setTime(vip.getCreateAt());
                    if(today.get(Calendar.MONTH) == createAt.get(Calendar.MONTH)
                            && today.get(Calendar.DAY_OF_MONTH) == createAt.get(Calendar.DAY_OF_MONTH)
                            && today.get(Calendar.YEAR) == createAt.get(Calendar.YEAR)){
                        newVipCount++;
                    }
                }
            }
            if(params.get("tenantId") != null){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String day = format.format(new Date());
                param0.put("tenantId", params.get("tenantId"));
                param0.put("startDate", day + " 00:00:00");
                param0.put("endDate", day + " 23:59:59");
                List<VipStoreHistory> storeHistories = vipStoreHistoryMapper.vipStoreList(param0);
                if(storeHistories != null && storeHistories.size() > 0){
                    for(VipStoreHistory storeHistory : storeHistories){
                        if(storeHistory.getPayAmount() != null){
                            if("1".equals(storeHistory.getStoreType())){
                                todayStore += storeHistory.getPayAmount().doubleValue();
                            }
                            else if("2".equals(storeHistory.getStoreType())){
                                //冲正
                                todayStore -= storeHistory.getPayAmount().doubleValue();
                            }
                            else if("5".equals(storeHistory.getStoreType())){
                                todayStore -= storeHistory.getPayAmount().doubleValue();
                            }
                        }
                    }
                }
                List<VipTradeHistory> tradeHistories = vipTradeHistoryMapper.vipTradeList(param0);
                if(tradeHistories != null && tradeHistories.size() > 0){
                    for(VipTradeHistory tradeHistory : tradeHistories){
                        if(tradeHistory.getPayAmount() != null){
                            if("1".equals(tradeHistory.getTradeType())){
                                todayStorePay += tradeHistory.getPayAmount().doubleValue();
                            }
                            else if("4".equals(tradeHistory.getTradeType())){
                                //消费回退
                                todayStorePay -= tradeHistory.getTradeAmount().doubleValue();
                            }
                        }
                    }
                }
            }
            data.put("totalVipStore", totalVipStore);
            data.put("newVipCount", newVipCount);
            data.put("todayStore", todayStore);
            data.put("todayStorePay", todayStorePay);
            rest.setIsSuccess(true);
            rest.setMessage("会员统计数据查询成功！");
            rest.setData(data);
        }
        return rest;
    }

    /**
     * 查询商户下会员数量
     *
     * @param params
     */
    public ApiRest vipCount(Map params){
        ApiRest apiRest = new ApiRest();
        Integer count = vipMapper.select(params).size();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询商户下会员数量成功！");
        apiRest.setData(count);
        return apiRest;
    }

    /**
     * 新增pos储值订单
     */
    public ApiRest addPosStoreOrder(Map params){
        ApiRest rest = new ApiRest();
        PosStoreOrder posStoreOrder = new PosStoreOrder();
        if(params.get("branchId") != null){
            posStoreOrder.setBranchId(new BigInteger(params.get("branchId").toString()));
        }
        if(params.get("tenantId") != null){
            posStoreOrder.setTenantId(new BigInteger(params.get("tenantId").toString()));
        }
        if(params.get("vipId") != null){
            posStoreOrder.setVipId(new BigInteger(params.get("vipId").toString()));
        }
        if(params.get("store") == null || params.get("store").toString().equals("")){
            rest.setIsSuccess(false);
            rest.setError("储值金额不能为空！");
            return rest;
        }
        posStoreOrder.setStore(new BigDecimal(params.get("store").toString()));
        posStoreOrder.setPayStatus("0");
        //SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        //添加冲正记录数据
        String currCode = tenantConfigService.getToday("CZ", 8);//"CZ" + format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_STORE_NUM, 3, new BigInteger(params.get("tenantId").toString()));
        posStoreOrder.setStoreNo(currCode);
        posStoreOrderMapper.insert(posStoreOrder);
        rest.setMessage("新增pos储值订单成功！");
        rest.setIsSuccess(true);
        rest.setData(posStoreOrder);
        return rest;
    }

    /**
     * 新增会员特殊活动
     */
    public ApiRest addOrUpdateSpecialPromotion(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        Map<String, Object> map0 = new HashMap<>();
        map0.put("tenantId", params.get("tenantId"));
        map0.put("branchId", params.get("branchId"));
        map0.put("promotionType", params.get("promotionType"));
        VipSpecialPromotion vipSpecialPromotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(map0);
        if(vipSpecialPromotion != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", vipSpecialPromotion.getId());
            if(params.get("addScore") != null){
                map.put("addScore", params.get("addScore"));
            }
            if(params.get("couponId") != null){
                map.put("prizeDietPromotionIds", params.get("couponId"));
            }else{
                map.put("prizeDietPromotionIds", "");
            }
            if(params.get("couponName") != null){
                map.put("prizeDietPromotionNames", params.get("couponName"));
            }else{
                map.put("prizeDietPromotionNames", "");
            }
            if("1".equals(params.get("isDoubleScoreBirthday"))){
                map.put("isDoubleScoreBirthday", 1);
            }
            else if("0".equals(params.get("isDoubleScoreBirthday"))){
                map.put("isDoubleScoreBirthday", 0);
            }
            if(params.get("vipDayTimesScore") != null){
                map.put("vipDayTimesScore", params.get("vipDayTimesScore"));
            }
            if(params.get("promotionType") != null){
                map.put("promotionType", params.get("promotionType"));
            }
            if(params.get("weeksSetting") != null){
                map.put("weeksSetting", params.get("weeksSetting"));
            }
            if(params.get("daysSetting") != null){
                map.put("daysSetting", params.get("daysSetting"));
            }
            if(params.get("clear") != null){
                map.put("clear", params.get("clear"));
            }
            int flag = vipTypeMapper.updateVipSpecialPromotion(map);
            if(flag == 1){
                rest.setMessage("更新成功！");
                rest.setIsSuccess(true);
            }
        }
        else{
            vipSpecialPromotion = new VipSpecialPromotion();
            vipSpecialPromotion.setTenantId(new BigInteger(params.get("tenantId").toString()));
            vipSpecialPromotion.setBranchId(new BigInteger(params.get("branchId").toString()));
            vipSpecialPromotion.setPromotionType(new Integer(params.get("promotionType").toString()));
            if(params.get("addScore") != null){
                vipSpecialPromotion.setAddScore(new BigDecimal(params.get("addScore").toString()));
            }
            if(params.get("isDoubleScoreBirthday") != null){
                if("1".equals(params.get("isDoubleScoreBirthday"))){
                    vipSpecialPromotion.setIsDoubleScoreBirthday(1);
                }
                else if("0".equals(params.get("isDoubleScoreBirthday"))){
                    vipSpecialPromotion.setIsDoubleScoreBirthday(0);
                }
            }
            if(params.get("couponId") != null){
                vipSpecialPromotion.setPrizeDietPromotionIds(params.get("couponId").toString());
            }
            if(params.get("couponName") != null){
                vipSpecialPromotion.setPrizeDietPromotionNames(params.get("couponName").toString());
            }
            if(params.get("vipDayTimesScore") != null){
                vipSpecialPromotion.setVipDayTimesScore(Integer.parseInt(params.get("vipDayTimesScore").toString()));
            }
            vipSpecialPromotion.setCreateAt(new Date());
            vipSpecialPromotion.setLastUpdateAt(new Date());
            if(params.get("promotionType") != null){
                vipSpecialPromotion.setPromotionType(Integer.parseInt(params.get("promotionType").toString()));
            }
            if(params.get("weeksSetting") != null){
                vipSpecialPromotion.setWeeksSetting(params.get("weeksSetting").toString());
            }
            if(params.get("daysSetting") != null){
                vipSpecialPromotion.setDaysSetting(params.get("daysSetting").toString());
            }
            int flag = vipTypeMapper.insertVipSpecialPromotion(vipSpecialPromotion);
            if(flag == 1){
                rest.setMessage("新增成功！");
                rest.setIsSuccess(true);
            }
        }
        return rest;
    }

    /**
     * 查询会员特殊活动
     */
    public ApiRest qrySpecialPromotion(Map params){
        ApiRest rest = new ApiRest();
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        map.put("branchId", params.get("branchId"));
        map.put("promotionType", params.get("promotionType"));
        Map vipSpecialPromotion = vipTypeMapper.findSpecialPromotion(map);
        rest.setMessage("查询成功！");
        rest.setIsSuccess(true);
        rest.setData(vipSpecialPromotion);
        return rest;
    }

    /**
     * 会员自动升级（定时任务）
     */
    public ApiRest autoUpgrade(Map params){
        ApiRest rest = new ApiRest();
        try{
            Map<String, String> map1 = new HashMap<>();
            map1.put("partitionCode", PropertyUtils.getDefault("partition.code"));
            ApiRest rest1 = ProxyApi.proxyPost("bs", "tenant", "findTenantList", map1);
            LogUtil.logInfo("查询商户接口结果" + BeanUtils.toJsonStr(rest1));
            if(rest1.getIsSuccess()){
                List<Map> tenants = (List<Map>)((Map)rest1.getData()).get("tenantList");
                LogUtil.logInfo("商户数：" + tenants.size() + ",商户列表：" + BeanUtils.toJsonStr(tenants));
                if(tenants.size() > 0){
                    for(Map tenant : tenants){
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("tenantId", tenant.get("id"));
                        map2.put("autoUpgrade", 1);
                        List<VipType> types = vipTypeMapper.vipTypeList(map2);
                        LogUtil.logInfo("商户号：" + tenant.get("code") + "，会员类型数：" + types.size() + ",会员类型列表：" + BeanUtils.toJsonStr(types));
                        if(types.size() == 0){
                            continue;
                        }
                        map2.put("status", 0);
                        List<Vip> vips = vipMapper.select(map2);
                        LogUtil.logInfo("商户号：" + tenant.get("code") + "，会员数：" + vips.size() + ",会员列表：" + BeanUtils.toJsonStr(vips));
                        if(vips.size() == 0){
                            continue;
                        }
                        for(Vip vip : vips){
                            for(VipType vipType : types){
                                if(vipType.getCurrLevel() != null && vipType.getUpgradeType() != null && vipType.getUpgradeLimit() != null && vipType.getCurrLevel() > 0){
                                    Map<String, Object> map3 = new HashMap<>();
                                    map3.put("tenantId", vipType.getTenantId());
                                    map3.put("id", vip.getTypeId());
                                    List<VipType> types0 = vipTypeMapper.vipTypeList(map3);
                                    if(types0 != null && types0.size() > 0 && (types0.get(0).getCurrLevel() == null || types0.get(0).getCurrLevel() < vipType.getCurrLevel())){
                                        if(tenant.get("isBranchManagementVip").toString().equals("true") || tenant.get("isBranchManagementVip").toString().equals("1")){
                                            if(!vipType.getBranchId().equals(types0.get(0).getBranchId())){
                                                continue;
                                            }
                                        }
                                        //累计积分
                                        if(vipType.getUpgradeType() == 1){
                                            if(vip.getSumScore() != null && vip.getSumScore().doubleValue() >= vipType.getUpgradeLimit().doubleValue()){
                                                vip.setTypeId(vipType.getId());
                                            }
                                        }
                                        else if(vipType.getUpgradeType() == 2){
                                            //累计储值
                                            if(vip.getVipStoreTotal() != null && vip.getVipStoreTotal().doubleValue() >= vipType.getUpgradeLimit().doubleValue()){
                                                vip.setTypeId(vipType.getId());
                                            }
                                        }
                                        else if(vipType.getUpgradeType() == 3){
                                            //累计消费
                                            if(vip.getSumConsume() != null && vip.getSumConsume().doubleValue() >= vipType.getUpgradeLimit().doubleValue()){
                                                vip.setTypeId(vipType.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            LogUtil.logInfo("商户号：" + tenant.get("code") + "会员更新为：" + BeanUtils.toJsonStr(vip));
                            vipMapper.update(vip);
                        }
                    }
                }
            }
            rest.setIsSuccess(true);
            rest.setMessage("会员自动升级定时任务执行完毕！");
        }
        catch(Exception e){
            rest.setIsSuccess(false);
            rest.setMessage("会员自动升级定时任务异常:" + e.getMessage());
            LogUtil.logError(e, params);
        }
        return rest;
    }

    /**
     * 领券时创建新会员
     *
     * @return
     */
    public ApiRest addWechatVip(Map params) throws ParseException{
        ApiRest rest = new ApiRest();
        String vipName = params.get("vipName").toString();
        String sex = params.get("sex").toString();
        String birthday = params.get("birthday").toString();
        String phone = params.get("phone").toString();
        String openId = params.get("openId").toString();
        String tenantId = params.get("tenantId").toString();
        Branch branch = branchMapper.getMainBranch(tenantId);
        if(branch == null){
            rest.setIsSuccess(false);
            rest.setMessage("机构查询失败");
            rest.setError("机构查询失败:");
            return rest;
        }
        else{
            VipType type;
            VipType v1 = vipTypeMapper.getWechatVipType(tenantId, branch.getId().toString(), 1);
            if(v1 == null){
                VipType v2 = vipTypeMapper.getWechatVipType(tenantId, branch.getId().toString(), null);
                if(v2 == null){
                    rest.setIsSuccess(false);
                    rest.setMessage("会员类型查询失败");
                    rest.setError("会员类型查询失败");
                    return rest;
                }
                else{
                    type = v2;
                }
            }
            else{
                type = v1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMdd");
            String code = sdf2.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, BigInteger.valueOf(Long.valueOf(tenantId)));
            Vip vip = new Vip();
            vip.setCreateAt(new Date());
            vip.setLastUpdateAt(new Date());
            vip.setVipName(vipName);
            vip.setSex(Integer.valueOf(sex));
            vip.setPhone(phone);
            vip.setBirthday(sdf.parse(birthday));
            vip.setOriginalId(openId);
            vip.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
            vip.setStatus(0);
            vip.setTypeId(type.getId());
            vip.setBranchId(branch.getId());
            vip.setBranchName(branch.getName());
            vip.setVipCode(code);
            int i = vipMapper.insert2(vip);
            if(i > 0){
                addLimit(SysConfig.SYS_VIP_NUM, branch.getTenantId());
                rest.setIsSuccess(true);
                rest.setMessage("创建会员成功");
                rest.setData(vip);
            }
            else{
                rest.setIsSuccess(false);
                rest.setMessage("创建会员失败");
                rest.setError("创建会员失败");
            }
        }
        return rest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest registerVip(Map<String, String> parameters) throws ParseException{
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(parameters.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(parameters.get("branchId")));
        Validate.isTrue(isInLimit(SysConfig.SYS_VIP_NUM, tenantId), "最多添加" + getLimit(SysConfig.SYS_VIP_NUM, tenantId) + "条会员");

        Map<String, Object> params = new HashMap<>();
        params.put("branchType", 0);
        params.put("tenantId", tenantId);
        Branch branch = branchMapper.find(params);
        Validate.notNull(branch, "机构不存在！");

        Vip vip = null;
        String vipId = parameters.get("vipId");
        if(StringUtils.isNotBlank(vipId)){
            vip = vipMapper.findByIdAndTenantIdAndBranchId(BigInteger.valueOf(Long.valueOf(vipId)), tenantId, branchId);
        }
        else{
            vip = new Vip();
            // 自动生成会员编号
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
            String code = simpleDateFormat.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, tenantId);
            params.put("status", "0");//默认是正常的
            vip.setVipCode(code);
            vip.setTenantId(tenantId);
            vip.setStatus(0);//默认是正常的
            Map<String, Object> vipTypeParams = new HashMap<>();
            vipTypeParams.put("isOnlineDefault", 1);
            vipTypeParams.put("tenantId", tenantId);
            VipType vipType = vipTypeMapper.findByCondition(vipTypeParams);
            if(vipType != null){
                vip.setTypeId(vipType.getId());
            }
            vip.setBranchId(branchId);
            vip.setRegSource("2");
            vip.setCreateAt(new Date());
            vip.setTypeId(NumberUtils.createBigInteger("-1"));
        }

        vip.setSex(Integer.valueOf(parameters.get("sex")));
        vip.setVipName(parameters.get("vipName"));
        vip.setPhone(parameters.get("phone"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        vip.setBirthday(sdf.parse(parameters.get("birthday")));
        vip.setLastUpdateAt(new Date());
        vip.setCardId(parameters.get("cardId"));
        vip.setUserCardCode(parameters.get("userCardCode"));
        vip.setOriginalId(parameters.get("openid"));
        if(StringUtils.isNotBlank(vipId)){
            vipMapper.update(vip);
        }
        else{
            vipMapper.insert2(vip);
            addLimit(SysConfig.SYS_VIP_NUM, tenantId);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setData(vip);
        apiRest.setMessage("新增会员成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    private String getLimit(String type, BigInteger tenantId){
        return tenantConfigMapper.findByNameAndTenantId(type, tenantId).getValue();
    }

    /**
     * 查询标签组
     */
    public ApiRest queryLabelGroup(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<Map> list = vipMapper.queryLabelGroup(params);
        Long count = vipMapper.queryLabelGroupSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询会员标签组成功");
        return apiRest;
    }

    /**
     * 查询会员标签组表
     */
    public ApiRest queryLabelGroupTable(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = vipMapper.queryLabelGroupTable(params);
        Long count = vipMapper.queryLabelGroupSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询会员标签组表格成功");
        return apiRest;
    }

    /**
     * 新增或修改标签组
     */
    public ApiRest addOrUpdateLabelGroup(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        String groupName = params.get("groupName").toString();
        String userName = params.get("userName") == null ? "System" : params.get("userName").toString();
        long i = vipMapper.isGroupNameUsed(groupName, tenantId, branchId);
        if(i > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("分组名称重复");
            return apiRest;
        }
        LabelGroup labelGroup = new LabelGroup();
        labelGroup.setGroupName(groupName);
        labelGroup.setTenantId(tenantId);
        labelGroup.setBranchId(branchId);
        if(params.get("id") != null && !"".equals(params.get("id"))){
            labelGroup.setId(BigInteger.valueOf(Long.parseLong(params.get("id").toString())));
            labelGroup.setLastUpdateAt(new Date());
            labelGroup.setLastUpdateBy(userName);
            int i1 = vipMapper.updateLabelGroup(labelGroup);
            if(i1 <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改标签组失败");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改标签组成功");
        }
        else{
                /*Long count = vipMapper.queryLabelGroupSum(params);
                if(count >= Long.parseLong(getLimit(SysConfig.SYS_LABEL_NUM, tenantId))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("最多可以有" + getLimit(SysConfig.SYS_LABEL_NUM, tenantId) + "个标签组");
                    return apiRest;
                }*/
            ApiRest apiRest1 = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_LABEL_NUM);
            if(!apiRest1.getIsSuccess()){
                apiRest.setIsSuccess(false);
                apiRest.setError(apiRest1.getError().replace("单位", "标签组"));
                return apiRest;
            }
            String groupCode = vipMapper.getGroupCode(params);
            if(groupCode == null || groupCode.equals("")){
                groupCode = "01";
            }
            else{
                groupCode = SerialNumberGenerate.nextSerialNumber(2, groupCode);
            }
            labelGroup.setGroupCode(groupCode);
            labelGroup.setCreateAt(new Date());
            labelGroup.setCreateBy(userName);
            labelGroup.setLastUpdateAt(new Date());
            labelGroup.setLastUpdateBy(userName);
            int i1 = vipMapper.addLabelGroup(labelGroup);
            if(i1 <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增标签组失败");
                return apiRest;
            }
            tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_LABEL_NUM);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增标签组成功");
        }
        return apiRest;
    }

    /**
     * 删除标签组
     */
    public ApiRest delLabelGroup(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        LabelGroup labelGroup = vipMapper.findLabelGroupById(id, tenantId, branchId);
        if(labelGroup == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询标签组失败");
            return apiRest;
        }
        long i = vipMapper.queryLabelByGroupId(id, tenantId, branchId);
        if(i > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("该标签组下有标签，不能删除");
            return apiRest;
        }
        labelGroup.setIsDeleted(true);
        labelGroup.setLastUpdateAt(new Date());
        labelGroup.setLastUpdateBy("System");
        int i1 = vipMapper.updateLabelGroup(labelGroup);
        if(i1 <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除标签组失败");
            return apiRest;
        }
        tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_LABEL_NUM);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除标签组成功");
        return apiRest;
    }

    /**
     * 查询会员标签列表
     */
    public ApiRest queryLabelList(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Integer rows = Integer.parseInt(params.get("length").toString());
        Integer offset = Integer.parseInt(params.get("start").toString());
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        if(params.get("isPublic") != null && "true".equals(params.get("isPublic"))){
            if(params.get("groupId") == null || "".equals(params.get("groupId"))){
                String groupId = vipMapper.minGroupId(params);
                //params.remove("groupId");
                params.put("groupId", groupId);
            }
            params.remove("rows");
            params.remove("offset");
        }
        else{
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("onlySelf", onlySelf);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = vipMapper.queryLabelList(params);
        Long count = vipMapper.queryLabelSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询会员标签成功");
        return apiRest;
    }

    /**
     * 新增标签
     */
    public ApiRest addLabel(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        String labelName = params.get("labelName").toString();
        Long l = vipMapper.isLabelNameUsed(labelName, tenantId, branchId);
        if(l > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("标签名重复");
            return apiRest;
        }
        String labelCode = params.get("labelCode").toString();
        String userName = params.get("userName") == null ? "System" : params.get("userName").toString();
        BigInteger groupId = BigInteger.valueOf(Long.parseLong(params.get("groupId").toString()));
        VipLabel vipLabel = new VipLabel();
        vipLabel.setTenantId(tenantId);
        vipLabel.setBranchId(branchId);
        vipLabel.setGroupId(groupId);
        vipLabel.setLabelName(labelName);
        vipLabel.setLabelCode(labelCode);
        vipLabel.setCreateAt(new Date());
        vipLabel.setCreateBy(userName);
        vipLabel.setLastUpdateAt(new Date());
        vipLabel.setLastUpdateBy(userName);
        int i = vipMapper.addLabel(vipLabel);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("新增标签失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("新增标签成功");
        return apiRest;
    }

    /**
     * 获取标签编码
     */
    public ApiRest getLabelCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger groupId = BigInteger.valueOf(Long.parseLong(params.get("groupId").toString()));
        String code = vipMapper.getLabelCode(groupId, tenantId);
        if(code == null || "".equals(code)){
            LabelGroup labelGroup = vipMapper.findLabelGroupById(groupId, tenantId, branchId);
            if(labelGroup == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询标签组失败");
                return apiRest;
            }
            code = labelGroup.getGroupCode() + "01";
        }
        else{
            code = SerialNumberGenerate.nextSerialNumber(4, code);
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(code);
        apiRest.setMessage("获取标签编码成功！");
        return apiRest;
    }

    /**
     * 删除标签
     */
    public ApiRest delLabel(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        String userName = params.get("userName") == null ? "System" : params.get("userName").toString();
        VipLabel vipLabel = vipMapper.findLabelById(id, tenantId, branchId);
        if(vipLabel == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询标签失败");
            return apiRest;
        }
            /*String labelIds = vipMapper.labelIsUsed(tenantId, branchId);
            if(labelIds != null && !"".equals(labelIds)){
                if(labelIds.indexOf(id.toString()) >= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("标签被使用，不能删除");
                    return apiRest;
                }
            }*/
        List<Map> useLabelVip = vipMapper.useLabelVip(params);
        if(useLabelVip.size() > 0 && useLabelVip != null){
            for(int i = 0; i < useLabelVip.size(); i++){
                Vip vip = new Vip();
                Map map = useLabelVip.get(i);
                BigInteger vipId = BigInteger.valueOf(Long.parseLong(map.get("id").toString()));
                String labelId = map.get("labelId").toString();
                if(labelId.indexOf(id.toString()) == 0 && labelId.indexOf(",") == 1){
                    labelId = labelId.substring(2, labelId.length());
                }
                else if((labelId.indexOf(id.toString()) == labelId.length() - 1) && ((labelId.length() - 2) == labelId.indexOf(","))){
                    labelId = labelId.substring(0, labelId.length() - 2);
                }
                else{
                    if(labelId.indexOf(",") < 0){
                        labelId = "";
                    }
                    else{
                        labelId = labelId.replace("," + id + ",", ",");
                    }
                }
                vip.setId(vipId);
                vip.setLabelId(labelId);
                vipMapper.update(vip);
            }
        }
        vipLabel.setId(id);
        vipLabel.setTenantId(tenantId);
        vipLabel.setBranchId(branchId);
        vipLabel.setLastUpdateAt(new Date());
        vipLabel.setLastUpdateBy(userName);
        int i = vipMapper.delLabel(vipLabel);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除标签失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除标签成功");
        return apiRest;
    }

    /**
     * POS用查询标签组和标签信息
     */
    public ApiRest qryLabelAndGroup(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> labelAndGroup = vipMapper.queryLabelAndGroup(params);
        List list = new ArrayList();
        if(labelAndGroup != null && labelAndGroup.size() > 0){
            for(int i = 0; i < labelAndGroup.size(); i++){
                Map groupMap = new HashMap();
                Map map = labelAndGroup.get(i);
                List labelList = new ArrayList();
                String labelIds = map.get("labelId") == null ? "" : (String)map.get("labelId");
                String labelNames = map.get("labelName") == null ? "" : map.get("labelName").toString();
                BigInteger groupId = map.get("groupId") == null ? null : BigInteger.valueOf(Long.parseLong(map.get("groupId").toString()));
                String groupName = map.get("groupName") == null ? "" : map.get("groupName").toString();
                String ids[] = labelIds.split(",");
                String names[] = labelNames.split(",");
                if(ids != null && ids.length > 0 && !"".equals(ids[0])){
                    for(int j = 0; j < ids.length; j++){
                        Map labelMap = new HashMap();
                        labelMap.put("labelId", ids[j]);
                        labelMap.put("labelName", names[j]);
                        labelList.add(labelMap);
                    }
                }
                groupMap.put("groupId", groupId);
                groupMap.put("groupName", groupName);
                groupMap.put("label", labelList.size() == 0 ? null : labelList);
                list.add(groupMap);
            }
        }
        apiRest.setData(list);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询标签成功");
        return apiRest;
    }

    /**
     * 生成标签组树图
     */
    public ApiRest loadLabelGroupTree(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<LabelGroup> list = vipMapper.queryLabelGroupTree(params);
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                Map map = (Map)list.get(i);
                map.put("pId", BigInteger.valueOf(-1));
            }
        }
            /*LabelGroup labelGroup = new LabelGroup();
            labelGroup.setId(BigInteger.valueOf(-1));
            labelGroup.setGroupName("所有标签组");
            labelGroup.setGroupCode("00");
            list.add(labelGroup);*/
        apiRest.setData(list);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成标签组树图成功");
        return apiRest;
    }

    /**
     * POS修改会员标签
     */
    public ApiRest updateVipLabel(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("id", id);
        Vip vip = vipMapper.findById(params);
        if(vip == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询会员失败");
            return apiRest;
        }
        int i = vipMapper.updateVipLabel(params);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("修改会员标签失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改会员标签成功");
        return apiRest;
    }

    /**
     * 会员对账（定时任务）
     * */
    public ApiRest vipStatement(){
        ApiRest apiRest = new ApiRest();
        try{
            Map<String, String> map1 = new HashMap<>();
            map1.put("partitionCode", PropertyUtils.getDefault("partition.code"));
            ApiRest rest1 = ProxyApi.proxyPost("bs", "tenant", "findTenantList", map1);
            LogUtil.logInfo("查询商户接口结果" + BeanUtils.toJsonStr(rest1));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String today = sdf.format(new Date());
            String statementDate = reportMapper.getDate(today, -1);
            Date reconciliationDate = format.parse(statementDate + " 23:59:59");
            BigDecimal beginAmount;
            BigDecimal storeAmount;
            BigDecimal payAmount;
            BigDecimal drawAmount;
            BigDecimal endAmount;
            if(rest1.getIsSuccess()){
                List<Map> tenants = (List<Map>)((Map)rest1.getData()).get("tenantList");
                LogUtil.logInfo("商户数：" + tenants.size() + ",商户列表：" + BeanUtils.toJsonStr(tenants));
                if(tenants.size() > 0){
                    for(Map tenant : tenants){
                        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId("isUseVipStatement", BigInteger.valueOf(Long.parseLong(tenant.get("id").toString())));
                        if(tenantConfig != null && "1".equals(tenantConfig.getValue())){
                            Map map = new HashMap();
                            map.put("tenantId", tenant.get("id"));
                            map.put("statementDate", statementDate);
                            List<Vip> vips = vipMapper.selectVipIncludeDeleted(map);
                            LogUtil.logInfo("商户号：" + tenant.get("code") + "，会员数：" + vips.size() + ",会员列表：" + BeanUtils.toJsonStr(vips));
                            if(vips.size() == 0){
                                continue;
                            }
                            for(Vip vip : vips){
                                //判断是否已有对账记录
                                List<Map> statementList = vipMapper.isInVipStatement(vip.getTenantId(), vip.getBranchId(), vip.getId());
                                storeAmount = vipMapper.queryVipStoreAmount(vip.getTenantId(), vip.getId(), statementDate) == null ? BigDecimal.ZERO : vipMapper.queryVipStoreAmount(vip.getTenantId(), vip.getId(), statementDate);
                                payAmount = vipMapper.queryVipPayAmount(vip.getTenantId(), vip.getId(), statementDate) == null ? BigDecimal.ZERO : vipMapper.queryVipPayAmount(vip.getTenantId(), vip.getId(), statementDate);
                                drawAmount = vipMapper.queryVipDrawAmount(vip.getTenantId(), vip.getId(), statementDate) == null ? BigDecimal.ZERO : vipMapper.queryVipDrawAmount(vip.getTenantId(), vip.getId(), statementDate);
                                if(statementList != null && statementList.size() > 0){
                                    String reconciliationDate1 = statementList.get(0).get("reconciliationDate").toString().substring(0,10);
                                    if(reconciliationDate1.equals(statementDate)){
                                       continue;
                                    }
                                    beginAmount = BigDecimal.valueOf(Double.parseDouble(statementList.get(0).get("endAmount").toString()));
                                    endAmount = beginAmount.add(storeAmount).subtract(payAmount).subtract(drawAmount);
                                }else{
                                    endAmount = vip.getVipStore() == null ? BigDecimal.ZERO : vip.getVipStore();
                                    beginAmount = endAmount.subtract(storeAmount).add(payAmount).add(drawAmount);
                                }
                                VipStatement vipStatement = new VipStatement();
                                vipStatement.setTenantId(vip.getTenantId());
                                vipStatement.setBranchId(vip.getBranchId());
                                vipStatement.setVipId(vip.getId());
                                vipStatement.setTypeId(vip.getTypeId());
                                vipStatement.setBeginAmount(beginAmount);
                                vipStatement.setStoreAmount(storeAmount);
                                vipStatement.setPayAmount(payAmount);
                                vipStatement.setDrawAmount(drawAmount);
                                vipStatement.setEndAmount(endAmount);
                                vipStatement.setReconciliationDate(reconciliationDate);
                                vipStatement.setCreateAt(new Date());
                                vipStatement.setCreateBy("System");
                                vipStatement.setLastUpdateAt(new Date());
                                vipStatement.setLastUpdateBy("System");
                                vipStatement.setIsDeleted(false);
                                vipMapper.insertVipStatement(vipStatement);
                            }
                        }
                    }
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("会员对账定时任务执行完毕！");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("会员对账定时任务异常:" + e.getMessage());
            LogUtil.logError(e, null);
        }
        return apiRest;
    }

    /************************************米雅会员对接begin**********************************************/
    /**
     * 根据手机号查询会员信息
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest miyaVipFind(Map<String,String> params){
        ApiRest apiRest = new ApiRest();
        String mobile=params.get("mobile");
        String tenantId=params.get("tenantId");
        List<Map> vipList=vipMapper.miyaVipFind(tenantId,mobile);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("total",vipList.size());
        resultMap.put("rows",vipList);
        apiRest.setData(resultMap);
        apiRest.setMessage("查询会员信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
    public ApiRest miyaAddVip(Map<String,String> params) throws ParseException{
        ApiRest result = new ApiRest();
        Vip vipByPhone = null;
        params.put("phone", params.get("mobile") == null ? "" : params.get("mobile"));
        params.put("email", params.get("email") == null ? "" : params.get("email"));
        params.put("memo", params.get("memo") == null ? "" : params.get("memo"));
        params.put("labelId", params.get("labelId") == null ? "" : params.get("labelId"));
        if(params.get("birthday") != null && !params.get("birthday").equals("") && !params.get("birthday").equals("null")){
            params.put("birthday", params.get("birthday"));
        }
        if(params.get("mobile") != null && !"".equals(params.get("mobile"))){
            Map<String, Object> param0 = new HashMap<>();

            //查询会员分组
            if(params.get("branchId") != null && params.get("tenantId") != null){
                param0.put("branchId", params.get("branchId"));
                Map<String, Object> paramZ = new HashMap<>();
                paramZ.put("id", params.get("branchId"));
                paramZ.put("tenantId", params.get("tenantId"));
                Branch branch = branchMapper.find(paramZ);
                if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                    paramZ.remove("id");
                    paramZ.put("groupCode", branch.getGroupCode());
                    List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                    if(branches != null && branches.size() > 0){
                        String bIds = "";
                        for(Branch b : branches){
                            bIds += b.getId() + ",";
                        }
                        bIds = bIds.substring(0, bIds.length() - 1);
                        param0.remove("branchId");
                        param0.put("groups", bIds);
                    }
                }
            }
            param0.put("tenantId", params.get("tenantId"));
            param0.put("phone", params.get("mobile"));
            param0.put("remainingScore",0);
            param0.put("vipStore",0);
            List<Vip> list = vipMapper.select(param0);
            if(list != null && list.size() > 0){
                vipByPhone = list.get(0);
            }
        }
        if(vipByPhone != null){
            result.setIsSuccess(false);
            result.setError("该手机已经被注册");
            return result;
        }
        if(isInLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()))){
            Map<String, Object> param = new HashMap<>();
            param.put("branchType", 0);
            param.put("tenantId", new BigInteger(params.get("tenantId").toString()));
            Branch branch = branchMapper.find(param);
            // 自动生成会员编号
            SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
            if(branch != null){
                String code = format.format(new Date()) + getNextSelValue(SysConfig.SYS_VIP_NUM, 5, new BigInteger(params.get("tenantId").toString()));
                params.put("vipCode", code);
                if(params.get("mobile") == null || "".equals(params.get("mobile"))){
                    params.put("phone", "");
                }

                if(params.get("status") == null || "".equals(params.get("status"))){
                    params.put("status", "0");//默认是正常的
                }
                if(null == params.get("typeId")){
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("isOnlineDefault", 1);
                    param1.put("tenantId", params.get("tenantId"));
                    VipType vipType = vipTypeMapper.findByCondition(param1);
                    if(vipType != null){
                        params.put("typeId", vipType.getId().toString());
                    }
                    else{
                        params.put("typeId", "0");
                    }
                }

                if(null == params.get("branchId")){
                    params.put("branchId", branch.getId().toString());
                }
                if(null == params.get("regSource")){
                    params.put("regSource", params.get("regSource"));
                }
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.put("createAt", format1.format(new Date()));
                params.put("lastUpdateAt", format1.format(new Date()));
                params.put("isDeleted", "0");
                if(params.get("labelId") != null && !"".equals(params.get("labelId"))){
                    int groupCount = vipMapper.labelCount(params);
                    String labels[] = params.get("labelId").toString().split(",");
                    if(groupCount != labels.length){
                        result.setIsSuccess(false);
                        result.setError("每个标签组只允许选择一个标签");
                        return result;
                    }
                }
                int flag = vipMapper.insert(params);
                if(flag == 1){
                    addLimit(SysConfig.SYS_VIP_NUM, new BigInteger(params.get("tenantId").toString()));
                    Map<String, Object> param1 = new HashMap<>();
                    param1.put("tenantId", params.get("tenantId"));
                    param1.put("vipCode", code);
                    Vip vip = vipMapper.findByCondition(param1);
                    Map vipMap=vipMapper.miyaVipFindOne(vip.getTenantId().toString(),vip.getId());
                    result.setData(vipMap);
                    result.setIsSuccess(true);
                    result.setMessage("会员保存成功");
                    //查询是否有新注册会员活动
                    param1.put("promotionType", 1);
                    VipSpecialPromotion promotion = vipTypeMapper.findSpecialPromotionByTenantIdAndType(param1);
                    if(promotion != null){
                        Map<String, Object> map5 = new HashMap<>();
                        map5.put("id", vip.getTypeId());
                        map5.put("tenantId", vip.getTenantId());
                        VipType vipType = vipTypeMapper.findByCondition(map5);
                        //赠送积分
                        if(vipType != null && BigInteger.ONE.equals(vipType.getToSavePoints()) && promotion.getAddScore() != null && promotion.getAddScore().compareTo(BigDecimal.ZERO) != 0){
                            dealScore(vip, promotion.getAddScore());
                            if(vip.getRemainingScore() != null){
                                vip.setRemainingScore(vip.getRemainingScore().add(promotion.getAddScore()));
                                vip.setSumScore(vip.getSumScore().add(promotion.getAddScore()));
                            }
                            else{
                                vip.setRemainingScore(promotion.getAddScore());
                                vip.setSumScore(promotion.getAddScore());
                            }
                            if(vip.getLargessscore() != null){
                                vip.setLargessscore(vip.getLargessscore().add(promotion.getAddScore()));
                            }
                            else{
                                vip.setLargessscore(promotion.getAddScore());
                            }
                            vipMapper.update(vip);
                            if(vip.getPhone() != null && !vip.getPhone().equals("")){
                                DecimalFormat sd = new DecimalFormat("#");
                                Map smsMap = new HashMap();
                                smsMap.put("type", 3);
                                smsMap.put("tenantId", vip.getTenantId().toString());
                                smsMap.put("number", vip.getPhone());
                                smsMap.put("branchId", branch.getId().toString());
                                smsMap.put("content", ("注册赠送###" + sd.format(promotion.getAddScore()) + "###" + sd.format((vip.getRemainingScore()==null?BigDecimal.ZERO:vip.getRemainingScore()))));
                                ApiRest ssr = smsService.sendSMS(smsMap);
                                if(!ssr.getIsSuccess()){
                                    LogUtil.logInfo("发送消费短信失败！vipId=" + vip.getId());
                                }
                            }
                        }
                        //赠送优惠券
                        if(StringUtils.isNotEmpty(promotion.getPrizeDietPromotionIds())){
                            Map<String, String> map3 = new HashMap<>();
                            map3.put("tenantId", promotion.getTenantId().toString());
                            map3.put("cardId", promotion.getPrizeDietPromotionIds());
                            map3.put("vipId", vip.getId().toString());
                            map3.put("channel", "register");
                            map3.put("branchId", promotion.getBranchId().toString());
                            cardCouponsService.sendCardToVip(map3);
                        }
                    }
                }
            }
            else{
                result.setIsSuccess(false);
                result.setError("没有查询到该商户下的总店!");
            }
        }
        else{
            result.setIsSuccess(false);
            result.setError("超过会员上限。");
        }
        return result;
    }
    public ApiRest miyaUpdateVip(Map<String,String> params){
        ApiRest result = new ApiRest();
        Vip vipByPhone = null;
        params.put("email", params.get("email") == null ? "" : params.get("email"));
        params.put("memo", params.get("memo") == null ? "" : params.get("memo"));
        if(params.get("birthday") != null && !params.get("birthday").equals("") && !params.get("birthday").equals("null")){
            params.put("birthday", params.get("birthday")+" 00:00:00");
        }
        Map<String, Object> param0 = new HashMap<>();

        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            param0.put("branchId", params.get("branchId"));
            Map<String, Object> paramZ = new HashMap<>();
            paramZ.put("id", params.get("branchId"));
            paramZ.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(paramZ);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                paramZ.remove("id");
                paramZ.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    param0.remove("branchId");
                    param0.put("groups", bIds);
                }
            }
        }
        param0.put("tenantId", params.get("tenantId"));
        param0.put("phone", params.get("mobile"));
        List<Vip> list = vipMapper.select(param0);
        if(list != null && list.size() > 0){
            vipByPhone = list.get(0);
        }
        if(vipByPhone != null){
            Vip upVip=new Vip(params);
            upVip.setId(vipByPhone.getId());
            int flag = vipMapper.update(upVip);
            if(flag == 1){
                Map vipResult=vipMapper.miyaVipFindOne(vipByPhone.getTenantId().toString(),vipByPhone.getId());
                result.setIsSuccess(true);
                result.setData(vipResult);
                result.setMessage("会员更新成功");
                return result;
            }else{
                result.setIsSuccess(false);
                result.setError("会员更新失败");
                return result;
            }
        }else{
            result.setIsSuccess(false);
            result.setError("查询会员信息失败");
            return result;
        }
    }
    /************************************米雅会员对接end************************************************/

    /*数据大屏用接口--start*/
    /**
     * 无分页查询会员列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipListForBS(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        List<Map> list = vipMapper.queryVipForBS(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", list.size());
        result.setSuccess("0");
        result.setObject(resultMap);
        return result;
    }

    public ApiRest qryVipStoreForBS(Map params) throws ServiceException{
        ApiRest apiRest = new ApiRest();
        List<Map> list = vipMapper.queryVipStoreForBS(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", list.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest qryVipTradeForBS(Map params) throws ServiceException{
        ApiRest apiRest = new ApiRest();
        List<Map> list = vipMapper.queryVipTradeForBS(params);
        Map resultMap = new HashMap();
        resultMap.put("rows", list);
        resultMap.put("total", list.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(resultMap);
        return apiRest;
    }
    /*数据大屏用接口--end*/
}
