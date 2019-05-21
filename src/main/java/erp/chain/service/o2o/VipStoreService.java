package erp.chain.service.o2o;

import com.saas.common.Constants;
import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.StorageBill;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.o2o.VipStoreHistory;
import erp.chain.domain.o2o.VipStoreRule;
import erp.chain.domain.o2o.VipStoreRuleDetails;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.DietPromotionFestivalMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.mapper.o2o.VipStoreHistoryMapper;
import erp.chain.mapper.o2o.VipStoreRuleDetailMapper;
import erp.chain.mapper.o2o.VipStoreRuleMapper;
import erp.chain.utils.Args;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.GsonUntil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangms on 2017/2/8.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.RuntimeException.class}, timeout = 120)
public class VipStoreService {
    @Autowired
    private VipStoreRuleMapper vipStoreRuleMapper;
    @Autowired
    private VipStoreRuleDetailMapper vipStoreRuleDetailMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipStoreHistoryMapper vipStoreHistoryMapper;
    @Autowired
    private DietPromotionFestivalMapper dietPromotionFestivalMapper;
    @Autowired
    private DietPromotionService dietPromotionService;
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 修改会员储值规则
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveVipStoreRule(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("id",params.get("id"));
            param.put("tenantId",params.get("tenantId"));
            LogUtil.logInfo("修改储值规则，传入参数："+ BeanUtils.toJsonStr(param));
            VipStoreRule vipStoreRule = vipStoreRuleMapper.findByCondition(param);
            LogUtil.logInfo("修改储值规则，查询储值规则："+ BeanUtils.toJsonStr(vipStoreRule));
            if(vipStoreRule==null){
                result.setSuccess("1");
                result.setMsg("储值规则查询失败！");
                return result;
            }
            //校验并修改
            if (null != params.get("isOff")) {
                if ("true".equals(params.get("isOff"))) {
                    param.put("isOff",true);
                } else {
                    param.put("isOff",false);
                }
            }
            //校验并修改
            if (null != params.get("isFixedStore")) {
                if ("true".equals(params.get("isFixedStore"))) {
                    param.put("isFixedStore",true);
                } else {
                    param.put("isFixedStore",false);
                }
            }
            if (null != params.get("deadType")) {
                param.put("deadType", params.get("deadType"));
            } else {
                param.put("deadType", "0");
            }
            if (null != params.get("deadNum")) {
                param.put("deadNum", params.get("deadNum"));
            }
            if (null != params.get("deadUnit")) {
                param.put("deadUnit", params.get("deadUnit"));
            }
            param.put("lastUpdateAt",new Date());
            param.put("lastUpdateBy","System");
            param.put("id",vipStoreRule.getId());
            vipStoreRuleMapper.update(param);
            //如果传入了details信息，则保存details
            if (null != params.get("details")) {
                List<VipStoreRuleDetails> details = GsonUntil.jsonAsList(URLDecoder.decode(params.get("details").toString(), "UTF-8"), VipStoreRuleDetails.class);
                //遍历并保存
                if (null != details && details.size() > 0) {
                    for (VipStoreRuleDetails vrd : details) {
                        if (vrd.getId().doubleValue() > 0) {
                            Map<String,Object> param1 = new HashMap<>();
                            param1.put("id",vrd.getId());
                            VipStoreRuleDetails preDetail = vipStoreRuleDetailMapper.findByCondition(param1);
                            if (null != vrd.getPayLimit()) {
                                param1.put("payLimit",vrd.getPayLimit());
                                //根据充值金额查询，若存在重复，则提示修改失败
                                Map<String,Object> param2 = new HashMap<>();
                                param2.put("ruleId",vipStoreRule.getId());
                                param2.put("payLimit",vrd.getPayLimit());
                                param2.put("tenantId",params.get("tenantId"));
                                VipStoreRuleDetails check = vipStoreRuleDetailMapper.findByCondition(param2);
                                if (check != null && check.getId().doubleValue() != preDetail.getId().doubleValue()) {
                                    result.setSuccess("1");
                                    result.setMsg("充值金额不允许重复！");
                                    return result;
                                }
                            }
                            if (null != vrd.getPresentLimit()) {
                                param1.put("payLimit", vrd.getPresentLimit());
                            }
                            param1.put("lastUpdateAt",new Date());
                            param1.put("lastUpdateBy","System");
                            vipStoreRuleDetailMapper.update(param1);
                        }
                    }
                }

            }
            //返回结果
            result.setSuccess("0");
            result.setMsg("储值规则修改成功！");
        } catch (Exception e) {
            LogUtil.logError("vipStoreService.saveVipStoreRule-"+e.getMessage());
            result.setSuccess("1");
            result.setMsg("系统错误，修改失败！");
        }

        return result;

    }

    /**
     * 新增或修改储值规则明细
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON addOrModifyVipStoreRuleDetails(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        try {
            //先判断id,存在说明是修改
            if (params.get("id") != null && !"".equals(params.get("id"))) {
                Map<String,Object> param = new HashMap<>();
                param.put("id",params.get("id"));
                VipStoreRuleDetails vipStoreRuleDetails = vipStoreRuleDetailMapper.findByCondition(param);
                if("1".equals(params.get("timeType"))){
                    params.remove("startTime");
                    params.remove("endTime");
                }
                if (vipStoreRuleDetails != null) {
                    //如果查到数据，则进行修改
                    if (null != params.get("payLimit")) {
                        param.put("payLimit", params.get("payLimit"));
                        Map<String,Object> param1 = new HashMap<>();
                        param1.put("payLimit", params.get("payLimit"));
                        param1.put("ruleId", vipStoreRuleDetails.getRuleId());
                        param1.put("tenantId", vipStoreRuleDetails.getTenantId());
                        param1.put("pointedVipType", params.get("pointedVipType"));
                        param1.put("id", vipStoreRuleDetails.getId());
                        long check = vipStoreRuleDetailMapper.findByCondition2(param1);
                        if (check >0) {
                            result.setSuccess("1");
                            result.setMsg("充值金额不允许重复！");
                            return result;
                        }
                    }
                    if (null != params.get("presentLimit")) {
                        param.put("presentLimit", params.get("presentLimit"));
                    }
                    param.put("lastUpdateAt", new Date());
                    param.put("lastUpdateBy", "System");
                    int flag = vipStoreRuleDetailMapper.update(params);
                    if(flag == 1){
                        result.setSuccess("0");
                        result.setMsg("修改成功！");
                    }
                } else {
                    result.setSuccess("1");
                    result.setMsg("未找到数据！");
                }

            } else {
                //否则，新增一条
                Map<String,Object> param = new HashMap<>();
                if (params.get("ruleId") != null) {
                    param.put("ruleId", params.get("ruleId"));
                }
                if (params.get("branchId") != null) {
                    param.put("branchId", params.get("branchId"));
                }else{
                    param.put("branchId", branchMapper.getMainBranch(params.get("tenantId").toString()).getId());
                }
                if (params.get("effectTimes") != null) {
                    param.put("effectTimes", params.get("effectTimes"));
                }else{
                    param.put("effectTimes", 0);
                }
                if (params.get("startTime") != null) {
                    param.put("startTime", params.get("startTime"));
                }
                if (params.get("endTime") != null) {
                    param.put("endTime", params.get("endTime"));
                }
                if (params.get("afterStoreType") != null) {
                    param.put("afterStoreType", params.get("afterStoreType"));
                }else{
                    param.put("afterStoreType", 0);
                }
                if (params.get("afterStoreData") != null&&params.get("afterStoreDataName") != null) {
                    param.put("afterStoreDataName", params.get("afterStoreDataName"));
                    param.put("afterStoreData", params.get("afterStoreData"));
                }else{
                    param.put("afterStoreData", params.get("afterStoreData"));
                }
                if (params.get("isForWechat") != null) {
                    param.put("isForWechat", params.get("isForWechat"));
                }else{
                    param.put("isForWechat", 0);
                }
                if (params.get("pointedVipType") != null) {
                    param.put("pointedVipType", params.get("pointedVipType"));
                }
                if (params.get("pointedVipTypeName") != null) {
                    param.put("pointedVipTypeName", params.get("pointedVipTypeName"));
                }
                if (params.get("payLimit") != null) {
                    param.put("payLimit", params.get("payLimit"));
                    Map<String,Object> param1 = new HashMap<>();
                    param1.put("payLimit", params.get("payLimit"));
                    param1.put("ruleId", params.get("ruleId"));
                    param1.put("tenantId", params.get("tenantId"));
                    param1.put("pointedVipType",params.get("pointedVipType"));
                    long check = vipStoreRuleDetailMapper.findByCondition2(param1);
                    if (check >0) {
                        result.setSuccess("1");
                        result.setMsg("充值金额不允许重复！");
                        return result;
                    }
                } else {
                    param.put("payLimit", 0);
                }
                if (params.get("presentLimit") != null) {
                    param.put("presentLimit", params.get("presentLimit"));
                } else {
                    param.put("presentLimit", 0);
                }
                param.put("isDeleted", 0);
                param.put("createAt", new Date());
                param.put("lastUpdateAt", new Date());
                param.put("tenantId", params.get("tenantId"));
                int flag = vipStoreRuleDetailMapper.insert(param);
                if(flag == 1){
                    result.setSuccess("0");
                    result.setMsg("新增成功！");
                }
            }

        } catch (Exception e) {
            result.setSuccess( "1");
            result.setMsg("操作失败！");
            LogUtil.logError(e, params);
        }

        return result;
    }
    /**
     * 根据tenentCode查询储值细则
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryStoreRuleDetailsByTenantCode(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        try {
            if(params.get("tenantCode") != null){
                ApiRest tenantRest = SaaSApi.findTenantByCode(params.get("tenantCode").toString());
                Map<String,Object> map = new HashMap<>();
                if (Constants.REST_RESULT_SUCCESS.equals(tenantRest.getResult())) {
                    Map tenant = (Map)((Map)tenantRest.getData()).get("tenant");
                    BigInteger tenantId = new BigInteger(tenant.get("id").toString());
                    VipStoreRule vipStoreRule = null;
                    //查询总店
                    Map<String,Object> param0 = new HashMap<>();
                    param0.put("tenantId",tenantId);
                    param0.put("branchType",0);
                    Branch branch0 = branchMapper.find(param0);
                    if (Boolean.valueOf(tenant.get("isBranchManagementStore").toString())) {
                        Map<String,Object> param = new HashMap<>();
                        param.put("tenantId",tenantId);
                        if(params.get("branchId") != null){
                            param.put("branchId",new BigInteger(params.get("branchId").toString()));
                        }
                        List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                        if(list != null && list.size() > 0){
                            vipStoreRule = list.get(0);
                        }
                    } else {
                        Map<String,Object> param = new HashMap<>();
                        param.put("tenantId",tenantId);
                        param.put("branchId",branch0.getId());
                        params.put("branchId",branch0.getId());
                        vipStoreRule = vipStoreRuleMapper.findByCondition(param);
                        if(vipStoreRule == null){
                            param.remove("branchId");
                            param.put("noBranch", 1);
                            List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                            if(list != null && list.size() > 0){
                                vipStoreRule = list.get(0);
                            }
                        }
                    }
                    //如果vipStoreRule不存在，则新增一条
                    if(vipStoreRule == null){
                        Map<String,Object> param = new HashMap<>();
                        param.put("tenantId",tenantId);
                        param.put("branchId",params.get("branchId"));
                        param.put("isOff",0);
                        param.put("deadType","0");
                        param.put("deadNum",0);
                        param.put("deadUnit",0);
                        param.put("createAt",new Date());
                        param.put("lastUpdateAt",new Date());
                        param.put("isDeleted",0);
                        int flag = vipStoreRuleMapper.insert(param);
                        if(flag == 1){
                            Map<String,Object> param2 = new HashMap<>();
                            param2.put("tenantId",tenantId);
                            param2.put("branchId",params.get("branchId"));
                            vipStoreRule = vipStoreRuleMapper.findByCondition(param2);
                        }
                    }
                    map.put("vipStoreRule",vipStoreRule);
                    result.setSuccess("0");
                    result.setMsg("查询储值细则成功！");
                    if (null != vipStoreRule) {
                        Map<String,Object> param1 = new HashMap<>();
                        param1.put("ruleId",vipStoreRule.getId());
                        List<VipStoreRuleDetails> countList = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param1);
                        //分页
                        if(params.get("page") != null && params.get("rows") != null){
                            param1.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
                            param1.put("rows",params.get("rows"));
                        }
                        List<Map> list = vipStoreRuleDetailMapper.storeRuleDetailsList(param1);
                        if(countList != null && list != null){
                            map.put("total", countList.size());
                            map.put("rows", list);
                        }

                    }
                    result.setJsonMap(map);
                } else {
                    result.setSuccess("1");
                    result.setMsg("查询失败,未检索到合法商户!");
                }
            } else {
                result.setSuccess("1");
                result.setMsg("查询失败,tenantCode不能为空!");
            }
        } catch (Exception e) {
            result.setSuccess("1");
            result.setMsg("系统异常，查询失败！");
            LogUtil.logError("qryStoreRule异常：" + e.getMessage());
        }
        return result;
    }

    public ResultJSON qryVipStoreRule(Map<String,String> params){
        ResultJSON result = new ResultJSON();
        if(StringUtils.isEmpty(params.get("tenantId"))){
            result.setIsSuccess(false);
            result.setMsg("参数错误，缺少必要查询条件tenantId");
            return result;
        }
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        String opFrom = "WX";
        String vipTypeId = null;
        String vipId = null;
        if(StringUtils.isNotEmpty(params.get("opFrom"))){
            opFrom = params.get("opFrom");
        }
        if(StringUtils.isNotEmpty(params.get("vipId"))){
            vipId = params.get("vipId");
        }
        if(StringUtils.isNotEmpty(params.get("vipTypeId"))){
            vipTypeId = params.get("vipTypeId");
        }
        ApiRest tenantRest = SaaSApi.findTenantById(tenantId);
        Map<String, Object> map = new HashMap<>();
        if(!Constants.REST_RESULT_SUCCESS.equals(tenantRest.getResult())){
            result.setIsSuccess(false);
            result.setMsg(tenantRest.getError()==null ? tenantRest.getMessage() : tenantRest.getError());
            return result;
        }
        Map tenant = (Map)((Map)tenantRest.getData()).get("tenant");
        VipStoreRule vipStoreRule = null;
        //查询总店
        Branch branch0 = branchMapper.getMainBranch(tenantId.toString());
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", tenantId);
        param.put("branchId", branch0.getId());
        if(Boolean.valueOf(tenant.get("isBranchManagementStore").toString())){
            if(params.get("branchId") != null){
                param.put("branchId", params.get("branchId"));
            }
        }
        else{
            params.put("branchId", branch0.getId().toString());
            vipStoreRule = vipStoreRuleMapper.findByCondition(param);
            if(vipStoreRule == null){
                param.remove("branchId");
                param.put("noBranch", 1);
            }
        }
        if(vipStoreRule == null){
            List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
            if(list != null && list.size() > 0){
                vipStoreRule = list.get(0);
            }
        }
        //如果vipStoreRule不存在，则新增一条
        if(vipStoreRule == null){
            vipStoreRule = addVipStoreRule(tenantId.toString(),params.get("branchId"));
        }
        if(vipStoreRule == null){//说明新增vipStoreRule失败
            result.setIsSuccess(false);
            result.setMsg("储值规则查询失败！");
            return result;
        }
        map.put("vipStoreRule", vipStoreRule);
        Map<String, Object> param1 = new HashMap<>();
        param1.put("ruleId", vipStoreRule.getId());
        if(opFrom.equals("WX")){//是否用于微商城
            param1.put("useForWechat","true");
        }
        if(StringUtils.isNotEmpty(vipTypeId)){//指定会员类型
            param1.put("pointedVipType",vipTypeId);
        }
        List<VipStoreRuleDetails> list = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param1);
        List<VipStoreRuleDetails> resultList = new ArrayList<>();
        if(StringUtils.isNotEmpty(vipId)){
            for(VipStoreRuleDetails vipStoreRuleDetails : list){
                boolean isCheckOnce = true;
                boolean isCheckDate = true;
                if(vipStoreRuleDetails.getEffectTimes() != null
                        && vipStoreRuleDetails.getEffectTimes() == 1){//校验是否只生效一次
                    Map<String, Object> historyMap = new HashMap<>();
                    historyMap.put("tenantId", vipStoreRule.getTenantId());
                    historyMap.put("vipId", vipId);
                    historyMap.put("ruleDetailId", vipStoreRuleDetails.getId());
                    List<VipStoreHistory> histories = vipStoreHistoryMapper.vipStoreList(historyMap);
                    if(histories.size() > 0){
                        isCheckOnce = false;
                    }
                }
                if(vipStoreRuleDetails.getStartTime() != null
                        && vipStoreRuleDetails.getEndTime() != null
                        && !(vipStoreRuleDetails.getStartTime().before(new Date())
                        && vipStoreRuleDetails.getEndTime().after(new Date()))){//校验有效期
                    isCheckDate = false;
                }
                if(isCheckDate && isCheckOnce){
                    resultList.add(vipStoreRuleDetails);
                }
            }
        }
        map.put("rows", resultList);
        result.setSuccess("0");
        result.setMsg("查询储值细则成功！");
        result.setJsonMap(map);
        return result;
    }

    private VipStoreRule addVipStoreRule(String tenantId,String branchId){
        Map<String, Object> paramNew = new HashMap<>();
        paramNew.put("tenantId", tenantId);
        paramNew.put("branchId", branchId);
        paramNew.put("isOff", 0);
        paramNew.put("deadType", "0");
        paramNew.put("deadNum", 0);
        paramNew.put("deadUnit", 0);
        paramNew.put("createAt", new Date());
        paramNew.put("lastUpdateAt", new Date());
        paramNew.put("isDeleted", 0);
        vipStoreRuleMapper.insert(paramNew);
        Map<String, Object> param2 = new HashMap<>();
        param2.put("tenantId", tenantId);
        param2.put("branchId", branchId);
        return vipStoreRuleMapper.findByCondition(param2);
    }
    /**
     * 根据tenentCode查询储值细则
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest qryStoreRuleDetailsByTenantCode1(Map params) throws ServiceException {
        ApiRest result = new ApiRest();
        try {
            ApiRest tenantRest = SaaSApi.findTenantByCode(params.get("tenantCode").toString());
            if (Constants.REST_RESULT_SUCCESS.equals(tenantRest.getResult())) {

                Map tenant = (Map)((Map)tenantRest.getData()).get("tenant");
                BigInteger tenantId = new BigInteger(tenant.get("id").toString());
                VipStoreRule vipStoreRule = null;
                //查询总店
                Map<String,Object> param0 = new HashMap<>();
                param0.put("tenantId",tenantId);
                param0.put("branchType",0);
                Branch branch0 = branchMapper.find(param0);
                if (Boolean.valueOf(tenant.get("isBranchManagementStore").toString())) {
                    Map<String,Object> param = new HashMap<>();
                    param.put("tenantId",tenantId);
                    param.put("branchId",new BigInteger(params.get("branchId").toString()));
                    List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                    if(list != null && list.size() > 0){
                        vipStoreRule = list.get(0);
                    }
                } else {
                    Map<String,Object> param = new HashMap<>();
                    param.put("tenantId",tenantId);
                    param.put("branchId",branch0.getId());
                    params.put("branchId",branch0.getId());
                    vipStoreRule = vipStoreRuleMapper.findByCondition(param);
                    if(vipStoreRule == null){
                        param.remove("branchId");
                        param.put("noBranch", 1);
                        List<VipStoreRule> list = vipStoreRuleMapper.vipStoreRuleList(param);
                        if(list != null && list.size() > 0){
                            vipStoreRule = list.get(0);
                        }
                    }
                }
                //如果vipStoreRule不存在，则新增一条
                if(vipStoreRule == null){
                    Map<String,Object> param = new HashMap<>();
                    param.put("tenantId",tenantId);
                    param.put("branchId",params.get("branchId"));
                    param.put("isOff",0);
                    param.put("deadType","0");
                    param.put("deadNum",0);
                    param.put("deadUnit",0);
                    param.put("createAt",new Date());
                    param.put("lastUpdateAt",new Date());
                    param.put("isDeleted",0);
                    int flag = vipStoreRuleMapper.insert(param);
                    if(flag == 1){
                        Map<String,Object> param2 = new HashMap<>();
                        param2.put("tenantId",tenantId);
                        param2.put("branchId",params.get("branchId"));
                        vipStoreRule = vipStoreRuleMapper.findByCondition(param2);
                    }
                }
                result.setIsSuccess(true);
                result.setMessage("查询储值细则成功！");
                if (null != vipStoreRule && !vipStoreRule.isOff()) {
                    Map<String,Object> param1 = new HashMap<>();
                    param1.put("ruleId",vipStoreRule.getId());
                    List<VipStoreRuleDetails> countList = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param1);
                    List<VipStoreRuleDetails> resultList = new ArrayList<>();
                    //条件筛选
                    if (null != countList && countList.size() > 0) {
                        for (int i = 0; i < countList.size(); i++) {
                            if((countList.get(i).getStartTime() == null || countList.get(i).getStartTime().compareTo(new Date()) <= 1)
                                    && (countList.get(i).getEndTime() == null || countList.get(i).getEndTime().compareTo(new Date()) >= 1)){
                                if(params.get("vipId") != null){
                                    Map<String,Object> param = new HashMap<>();
                                    param.put("id",params.get("vipId"));
                                    Vip vip = vipMapper.findByCondition(param);
                                    if(vip != null){
                                        //判断是否是生效一次
                                        if(countList.get(i).getEffectTimes() != null && countList.get(i).getEffectTimes() == 1){
                                            Map<String,Object> map = new HashMap<>();
                                            map.put("tenantId",vipStoreRule.getTenantId());
                                            map.put("vipId",vip.getId());
                                            map.put("ruleDetailId",countList.get(i).getId());
                                            List<VipStoreHistory> histories = vipStoreHistoryMapper.vipStoreList(map);
                                            if(histories != null && histories.size() > 0){
                                                continue;
                                            }
                                        }
                                        //判断是否指定会员类型
                                        if(countList.get(i).getPointedVipType() != null&& !countList.get(i).getPointedVipType().equals(BigInteger.ZERO)){
                                            if(!countList.get(i).getPointedVipType().equals(vip.getTypeId())){
                                                continue;
                                            }
                                        }
                                    }
                                }

                                resultList.add(countList.get(i));
                            }
                        }
                    }
                    result.setData(resultList);
                }
            } else {
                result.setIsSuccess(false);
                result.setError("查询失败,未检索到合法商户!");
                result.setMessage("查询失败,未检索到合法商户!");
            }
        } catch (Exception e) {
            result.setIsSuccess(false);
            result.setError("系统异常，查询失败！");
            result.setMessage("系统异常，查询失败！");
            LogUtil.logError("qryStoreRule异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据tenantId查询储值规则
     * @param params
     * @author szq
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryStoreRuleByTenantId(Map params) throws ServiceException {
        ResultJSON result = new ResultJSON();
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("tenantId", params.get("tenantId"));
            VipStoreRule vipStoreRule = vipStoreRuleMapper.findByCondition(param);
            List<VipStoreRuleDetails> vipStoreRuleDetailses = null;
            if(vipStoreRule != null && !vipStoreRule.isOff()){
                param.put("ruleId",vipStoreRule.getId());
                vipStoreRuleDetailses = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(param);
            }
            result.setObject(vipStoreRuleDetailses);
            result.setSuccess("0");
            result.setMsg("查询储值规则成功");
        } catch (Exception e) {
            result.setSuccess("1");
            result.setMsg(e.getMessage());
            LogUtil.logError(e,params);
        }
        return result;
    }

    /**
     * 保存储存规则明细，如果已存在储存则修改并且增加明细，否则增加并且增加明细
     *
     * @param tId not null
     * @param bId not null
     * @param rule not null 格式：
     * '{"isOff":"true or false not null","payLimit":">=0 && <= 9999 整数",
     * "presentLimit":" 整数 >=0 && <= 9999"}'
     * *@param VipStoreRuleDetails.findByRuleIdAndPayLimit(oldRule.id,payLimit) 应该为 null 充值金额不可重复
     * @exception
     * IllegalArgumentException :参数不符合要求
     *
     * @return Map < String , Object >  rule 保存之后的储存规则及明细
     * rule = '{"isOff":"true or false","payLimit":""，"presentLimit":"","detailId":"明细id"}'
     *
     * @author hxh
     */
    public Map<String, Object> saveVipStoreRuleDetail(BigInteger tId,
                                                      BigInteger bId, Map<String, String> rule) {

        Args.isTrue(rule.get("isOff") != null &&
                (rule.get("isOff").equals("true") || rule.get("isOff").equals("false")), "isOff 必须是true or false");
        boolean isOff = Boolean.parseBoolean(rule.get("isOff"));
        /*Args.isInteger(rule.payLimit, "payLimit必须是整数")
        Args.isInteger(rule.presentLimit, "presentLimit必须是整数")*/
        BigDecimal payLimit = BigDecimal.valueOf(Double.valueOf(rule.get("payLimit")));
        BigDecimal presentLimit = BigDecimal.valueOf(Double.valueOf(rule.get("presentLimit")));
        Args.isTrue(payLimit.compareTo(BigDecimal.ZERO)>=0&&payLimit.compareTo(BigDecimal.valueOf(9999))<=0,"payLimit 是>=0 && <= 9999 的数");
        Args.isTrue(presentLimit.compareTo(BigDecimal.ZERO)>=0&&presentLimit.compareTo(BigDecimal.valueOf(9999))<=0,"presentLimit 是>=0 && <= 9999 的数");
        //def oldRule = VipStoreRule.findByTenantIdAndBranchIdAndIsDeleted(tId, bId, false)
        Map params=new HashMap();
        params.put("tenantId",tId);
        VipStoreRule oldRule = vipStoreRuleMapper.findByCondition(params);
        //Args.notNull(oldRule, "商户ID=${tId}机构ID=${bId}不存在储值规则");
        VipStoreRuleDetails detail = new VipStoreRuleDetails();
        detail.setPayLimit(payLimit);
        detail.setPresentLimit(presentLimit);
        //不存在存储规则新建否则使用
        if (oldRule == null) {
            oldRule = new VipStoreRule();
            oldRule.setIsOff(false);
            oldRule.setDeadType("0");
            oldRule.setTenantId(tId);
            oldRule.setBranchId(bId);
            oldRule.setDeadUnit("1");
            oldRule.setDeadNum(BigInteger.ZERO);
            oldRule.setLastUpdateAt(new Date());
            oldRule.setLastUpdateBy("System");
            oldRule.setCreateAt(new Date());
            oldRule.setCreateBy("System");
            if(rule.get("branchId") != null){
                oldRule.setBranchId(new BigInteger(rule.get("branchId")));
            }
            detail.setLastUpdateAt(new Date());
            detail.setLastUpdateBy("System");
            detail.setCreateAt(new Date());
            detail.setCreateBy("System");
            detail.setTenantId(tId);
            vipStoreRuleMapper.insert(oldRule);
            detail.setRuleId(oldRule.getId());
            vipStoreRuleDetailMapper.insert(detail);
        } else {
            oldRule.setIsOff(oldRule.isOff());
            detail.setRuleId(oldRule.getId());
            Map ssMap=new HashMap();
            ssMap.put("ruleId", oldRule.getId());
            ssMap.put("payLimit", payLimit);
            Args.isTrue(vipStoreRuleDetailMapper.findByCondition(ssMap) == null, "充值金额不能重复");
            oldRule.setLastUpdateAt(new Date());
            oldRule.setLastUpdateBy("System");
            detail.setLastUpdateAt(new Date());
            detail.setLastUpdateBy("System");
            detail.setCreateAt(new Date());
            detail.setCreateBy("System");
            detail.setTenantId(tId);
            vipStoreRuleMapper.update(oldRule);
            vipStoreRuleDetailMapper.insert(detail);
        }
        Map res = new HashMap();
        res.put("isOff",isOff);
        res.put("detailId",detail.getId());
        res.put("payLimit",detail.getPayLimit());
        res.put("presentLimit",detail.getPresentLimit());

        return res;
    }

    /**
     * 修改储存规则明细，修改储存规则并且修改明细
     *
     * '{"isOff":"true or false not null","payLimit":">=0 && <= 9999 整数",
     * "presentLimit":" 整数 >=0 && <= 9999","detailId":"明细id"}'
     * @exception
     * IllegalArgumentException :参数不符合要求,储值规则不存在,明细不存在
     *
     * @author hxh
     */
    public Map updateVipStoreRuleDetail(BigInteger tId,
                                        BigInteger bId, Map<String, String> rule) {
        Args.isTrue(rule.get("isOff") != null &&
                (rule.get("isOff").equals("true") || rule.get("isOff").equals("false")), "isOff 必须是true or false");
        boolean isOff = Boolean.parseBoolean(rule.get("isOff"));
        //Args.isInteger(rule.get("payLimit"), "payLimit必须是整数");
        Args.isInteger(rule.get("detailId"), "detailId必须是整数");
        //Args.isInteger(rule.get("presentLimit"), "presentLimit必须是整数");
        BigDecimal payLimit = BigDecimal.valueOf(Double.valueOf(rule.get("payLimit")));
        BigDecimal presentLimit = BigDecimal.valueOf(Double.valueOf(rule.get("presentLimit")));
        Args.isTrue(payLimit.compareTo(BigDecimal.ZERO)>=0&&payLimit.compareTo(BigDecimal.valueOf(9999))<=0,"payLimit 是>=0 && <= 9999 的数");
        Args.isTrue(presentLimit.compareTo(BigDecimal.ZERO)>=0&&presentLimit.compareTo(BigDecimal.valueOf(9999))<=0,"presentLimit 是>=0 && <= 9999 的数");
        Map params=new HashMap();
        params.put("tenantId",tId);
        //params.put("branchId",bId);
        VipStoreRule oldRule = vipStoreRuleMapper.findByCondition(params);
        Args.notNull(oldRule, "储值规则不存在");
        Map reMap=new HashMap();
        reMap.put("id",rule.get("detailId"));
        VipStoreRuleDetails detail = vipStoreRuleDetailMapper.findByCondition(reMap);
        Args.notNull(detail, "明细不存在或已删除");
        /*def ruleId = detail.ruleId
        Args.isTrue(VipStoreRuleDetails.findByRuleIdAndPayLimit(ruleId, payLimit) == null, "充值金额不能重复")*/
        BigInteger ruleId = detail.getRuleId();
        Map map=new HashMap();
        map.put("ruleId",ruleId);
        map.put("payLimit",payLimit);
        VipStoreRuleDetails storeRuleDetails = vipStoreRuleDetailMapper.findByCondition(map);
        Boolean boo = true;
        if(storeRuleDetails != null && !storeRuleDetails.getPayLimit().equals(detail.getPayLimit())){
            boo = false;
        }
        Args.isTrue(boo,"充值金额不能重复");
        oldRule.setIsOff(oldRule.isOff());
        detail.setPayLimit(payLimit);
        detail.setPresentLimit(presentLimit);
        oldRule.setLastUpdateAt(new Date());
        oldRule.setLastUpdateBy("System");
        detail.setLastUpdateAt(new Date());
        detail.setLastUpdateBy("System");
        vipStoreRuleMapper.update(oldRule);
        vipStoreRuleDetailMapper.update(detail);
        Map result=new HashMap();
        map.put("rule",oldRule);
        map.put("detail",detail);
        return result;
    }

    /**
     * 删除储值规则明细
     *
     * @param
     * @param
     * @param
     *
     * @exception
     * IllegalArgumentException :参数不符合要求,储值规则不存在,明细不存在
     *
     */
    public int delVipStoreRuleDetailPc(Map params) {
        String ids = params.get("ids").toString();
        String[] idArray = ids.split(",");
        int num = 0;
        if(idArray.length > 0){
            for (String id : idArray) {
                try {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", id);
                    param.put("isDeleted", 1);
                    param.put("lastUpdateAt", new Date());
                    int flag = vipStoreRuleDetailMapper.update(param);
                    if (flag == 1) {
                        num++;
                    }
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage());
                }
            }
        }
        return num;
    }
    /**
     * 获取指定商户和机构的储值规则
     * @param tId not null
     * @param bId not null
     * @exception
     * IllegalArgumentException :参数不符合要求
     *
     * @return '{"isOff":"是否开启:true or false","deadType":"0:永久,1:一定期限内有效","id":"id"
     *      "details":"list<VipStoreRuleDetails>"}'
     * Map.isEmpty() == true 没有储值规则，Map.details.size() == 0 没有储值规则明细
     */
    public Map<String, Object> queryVipStoreRule(BigInteger tId, BigInteger bId) {
        Args.notNull(bId, "bId not null");
        Args.notNull(tId, "tId not null");
        Map res = new HashMap();
        Map param=new HashMap();
        param.put("tenantId",tId);
        //param.put("branchId",bId);
        VipStoreRule rule = vipStoreRuleMapper.findByCondition(param);
        List<VipStoreRuleDetails> vipStoreRuleDetailses = null;
        if(rule != null){
            Map params=new HashMap();
            params.put("ruleId",rule.getId());
            vipStoreRuleDetailses = vipStoreRuleDetailMapper.vipStoreRuleDetailsList(params);
            res.put("id",rule.getId());
            res.put("isOff",rule.isOff());
            res.put("deadType",rule.getDeadType());
            res.put("details",vipStoreRuleDetailses);
        }
        return res;
    }
    /**
     * 开启或关闭储值规则明细
     * @param tId not null
     * @param bId not null
     * @param bId not null
     */
    public void startOrCloseVipStoreRule(BigInteger tId, BigInteger bId, boolean isOff) {
        Args.notNull(tId,"tId not null");
        Args.notNull(bId,"bId not null");
        Args.notNull(isOff,"isOff not null");
        Map findMap=new HashMap();
        findMap.put("tenantId",tId);
        //findMap.put("branchId",bId);
        VipStoreRule rule =vipStoreRuleMapper.findByCondition(findMap);
        Args.notNull(rule,"未查到数据");
        rule.setIsOff(isOff);
        vipStoreRuleMapper.update(rule);
    }


    /**
     * 修改会员储值规则详情
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON updateVipStoreDetail(Map params) throws ServiceException, ParseException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> param = new HashMap<>();
        param.put("id", params.get("id"));
        param.put("tenantId", param.get("tenantId"));

        VipStoreRuleDetails details = vipStoreRuleDetailMapper.findByCondition(param);
        if(details != null){
            Map<String, Object> param1 = new HashMap<>();
            param1.put("payLimit", params.get("payLimit"));
            param1.put("ruleId", details.getRuleId());
            param1.put("pointedVipType", params.get("pointedVipType"));
            param1.put("id", details.getId());
            long check = vipStoreRuleDetailMapper.findByCondition2(param1);
            if(check > 0){
                result.setIsSuccess(false);
                result.setSuccess("1");
                result.setMsg("充值金额不允许重复！");
                return result;
            }
            if("1".equals(params.get("timeType"))){
                params.remove("startTime");
                params.remove("endTime");
            }
            int falg = vipStoreRuleDetailMapper.update(params);
            if(falg == 1){
                result.setIsSuccess(true);
                result.setSuccess("0");
                result.setMsg("会员储值规则修改成功");
                return result;
            }
            else{
                result.setIsSuccess(false);
                result.setSuccess("1");
                result.setMsg("会员储值规则修改失败");
                return result;
            }
        }
        return result;

    }

    /**
     * 修改会员储值规则详情
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest initVipStoreRuleBranch(Map params){
        ApiRest result = new ApiRest();
        Map<String, Object> param = new HashMap<>();
        if(params.get("tenantId") != null){
            param.put("tenantId", params.get("tenantId"));
            //查询branchId为null的数据
            VipStoreRule storeRule = vipStoreRuleMapper.findOldRule(param);
            //查询总店
            param.put("branchType", 0);
            Branch branch0 = branchMapper.find(param);
            if(branch0 != null){
                if(storeRule != null){
                    storeRule.setBranchId(branch0.getId());
                    storeRule.setLastUpdateAt(new Date());
                    vipStoreRuleMapper.update(storeRule);
                }
                else{
                    param.put("branchId", branch0.getId());
                    storeRule = vipStoreRuleMapper.findByCondition(param);
                    if(storeRule == null){
                        createStoreRule(new BigInteger(params.get("tenantId").toString()), branch0.getId());
                    }
                }
                //初始化分店rule
                Map<String, Object> param1 = new HashMap<>();
                param1.put("tenantId", params.get("tenantId"));
                List<Branch> branches = branchMapper.findBranchByTenantId(param1);
                if(branches != null && branches.size() > 0){
                    for(Branch branch : branches){
                        if(branch.getBranchType() != 0){
                            param1.put("branchId", branch.getId());
                            VipStoreRule storeRule1 = vipStoreRuleMapper.findByCondition(param1);
                            if(storeRule1 == null){
                                createStoreRule(new BigInteger(params.get("tenantId").toString()), branch.getId());
                            }
                        }
                    }
                }
            }
            result.setIsSuccess(true);
            result.setMessage("分店储值规则初始化成功!");
        }
        else{
            result.setIsSuccess(false);
            result.setError("tenantId不能为空!");
        }

        return result;

    }

    /**
     * 储值对账结算单列表
     * */
    public ApiRest queryStoreStatements(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("length").toString());
        Integer offset = Integer.parseInt(params.get("start").toString());
        params.put("rows", rows);
        params.put("offset", offset);
        List<Map> list = vipStoreRuleMapper.queryStoreStatements(params);
        Long count = vipStoreRuleMapper.queryStoreStatementsSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询储值对账结算单表成功！");
        return apiRest;
    }

    /**
     * 通过条件查询储值消费单据
     * */
    public ApiRest queryStoreOrder(Map params){
        ApiRest apiRest = new ApiRest();
        Integer rows = Integer.parseInt(params.get("length").toString());
        Integer offset = Integer.parseInt(params.get("start").toString());
        params.put("rows", rows);
        params.put("offset", offset);
        Map map = new HashMap();
        List<Map> list = vipStoreRuleMapper.queryStoreOrder(params);
        List<Map> footer = vipStoreRuleMapper.queryStoreOrderSum(params);
        map.put("rows", list);
        map.put("total", footer.get(0).get("total"));
        map.put("footer", footer);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询储值单据成功");
        return apiRest;
    }

    /**
     * 新增或修改处置对账结算单
     * */
    public ApiRest addOrUpdateStatement(Map params) throws Exception{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger chargeBranchId = BigInteger.valueOf(Long.parseLong(params.get("chargeBranchId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigDecimal chargeAmount = BigDecimal.valueOf(Double.parseDouble(params.get("chargeAmount").toString()));
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startDate = sf.parse(params.get("startDate").toString()+" 00:00:00");
        Date endDate = sf.parse(params.get("endDate").toString()+" 23:59:59");
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        String code = params.get("code").toString();
        if(params.get("id") != null && !"".equals(params.get("id"))){
            StorageBill storageBill = vipStoreRuleMapper.queryBillById(params);
            if(storageBill == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到该储值对账结算单");
                return apiRest;
            }
            params.put("lastUpdateAt", sf.format(new Date()));
            params.put("lastUpdateBy", userId);
            params.put("chargeStartDate", params.get("startDate").toString()+" 00:00:00");
            params.put("chargeEndDate", params.get("endDate").toString()+" 23:59:59");
            int i = vipStoreRuleMapper.updateStorageBill(params);
            if(i<0){
                apiRest.setIsSuccess(false);
                apiRest.setError("更新储值对账结算单失败");
                return apiRest;
            }
            params.put("billId", null);
            int j = vipStoreRuleMapper.clearVipStoreBillId(params);
            if(j<0){
                apiRest.setIsSuccess(false);
                apiRest.setError("更新储值对账结算单失败");
                return apiRest;
            }
            params.put("isSave", 1);
            params.put("branchId", chargeBranchId);
            params.put("billId", params.get("id"));
            int m = vipStoreRuleMapper.updateVipStoreBillId(params);
            if(m<0){
                apiRest.setIsSuccess(false);
                apiRest.setError("更新储值对账结算单失败");
            }
            apiRest.setData(storageBill.getId());
            apiRest.setIsSuccess(true);
            apiRest.setMessage("更新储值对账结算单成功");
        }else{
            StorageBill storageBill = new StorageBill();
            storageBill.setTenantId(tenantId);
            storageBill.setCreateBranchId(branchId);
            storageBill.setChargeBranchId(chargeBranchId);
            storageBill.setCode(code);
            storageBill.setStatus(0);
            storageBill.setChargeAmount(chargeAmount);
            storageBill.setCreateAt(new Date());
            storageBill.setCreateBy(e.getId());
            storageBill.setLastUpdateAt(new Date());
            storageBill.setLastUpdateBy(e.getId());
            storageBill.setIsDeleted(false);
            storageBill.setChargeStartDate(startDate);
            storageBill.setChargeEndDate(endDate);
            int i = vipStoreRuleMapper.saveStorageBill(storageBill);
            if(i<0){
                apiRest.setIsSuccess(false);
                apiRest.setError("保存储值对账结算单失败");
                return apiRest;
            }
            params.put("billId", storageBill.getId());
            params.put("branchId", chargeBranchId);
            int j = vipStoreRuleMapper.updateVipStoreBillId(params);
            if(j<0){
                apiRest.setIsSuccess(false);
                apiRest.setError("保存储值对账结算单失败");
                return apiRest;
            }
            apiRest.setData(storageBill.getId());
            apiRest.setIsSuccess(true);
            apiRest.setMessage("保存储值对账结算单成功");
        }
        return apiRest;
    }

    public ApiRest queryBillById(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> storageBill = vipStoreRuleMapper.queryBillInfoById(params);
        apiRest.setData(storageBill.get(0));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询结算单详情成功");
        return apiRest;
    }

    public ApiRest queryStoreByBillId(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        List<Map> list = vipStoreRuleMapper.queryStoreByBillId(params);
        List<Map> footer = vipStoreRuleMapper.queryStoreByBillIdSum(params);
        map.put("rows", list);
        map.put("total", footer.get(0).get("total"));
        map.put("footer", footer);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询结算单详情成功");
        return apiRest;
    }

    public ApiRest updateStatus(Map params){
        ApiRest apiRest = new ApiRest();
        StorageBill storageBill = vipStoreRuleMapper.queryBillById(params);
        if(storageBill == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到单据");
            return apiRest;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if("1".equals(params.get("status")) && params.get("branchId").equals(params.get("auditBranchId"))){
            params.put("status", "2");
            params.put("auditAt", format.format(new Date()));
            params.put("auditBy", params.get("userId"));
        }
        String msg = "";
        String error = "";
        if("1".equals(params.get("status").toString())){
            msg = "审核成功";
            error = "审核失败";
            params.put("auditAt", format.format(new Date()));
            params.put("auditBy", params.get("userId"));
        }else if("2".equals(params.get("status").toString())){
            msg = "确认成功";
            error = "确认失败";
            params.put("confirmAt", format.format(new Date()));
            params.put("confirmBy", params.get("userId"));
        }else if("3".equals(params.get("status").toString())){
            msg = "付款成功";
            error = "付款失败";
            params.put("payAt", format.format(new Date()));
            params.put("payBy", params.get("userId"));
        }
        int i = vipStoreRuleMapper.updateStorageBill(params);
        if(i<0){
            apiRest.setIsSuccess(false);
            apiRest.setError(error);
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage(msg);
        return apiRest;
    }

    public ApiRest deleteOrder(Map params){
        ApiRest apiRest = new ApiRest();
        StorageBill storageBill = vipStoreRuleMapper.queryBillById(params);
        if(storageBill == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到单据");
            return apiRest;
        }
        params.put("billId", storageBill.getId());
        int j = vipStoreRuleMapper.clearVipStoreBillId(params);
        if(j<0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除单据失败");
            return apiRest;
        }
        int i = vipStoreRuleMapper.updateStorageBill(params);
        if(i<0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除单据失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除单据成功");
        return apiRest;
    }


    private void createStoreRule(BigInteger tenantId,BigInteger branchId){
        VipStoreRule oldRule = new VipStoreRule();
        oldRule.setIsOff(false);
        oldRule.setDeadType("0");
        oldRule.setTenantId(tenantId);
        oldRule.setBranchId(branchId);
        oldRule.setDeadUnit("1");
        oldRule.setDeadNum(BigInteger.ZERO);
        oldRule.setLastUpdateAt(new Date());
        oldRule.setLastUpdateBy("System");
        oldRule.setCreateAt(new Date());
        oldRule.setCreateBy("System");
        vipStoreRuleMapper.insert(oldRule);
    }
}
