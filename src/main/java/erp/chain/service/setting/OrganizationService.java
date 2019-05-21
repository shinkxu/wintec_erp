package erp.chain.service.setting;

import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.*;
import erp.chain.domain.system.SysConfig;
import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.*;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.mapper.report.ReportMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.SerialNumberGenerate;
import erp.chain.utils.ZTree;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.AuthApi;
import saas.api.ProxyApi;
import saas.api.RegisterApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuyandong on 2016/11/25.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationService{

    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private PosMapper posMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ReportMapper reportMapper;

    public List<BigInteger> getChildAreaIds(BigInteger tenantId,BigInteger areaId,List<BigInteger> resultList){
        Map<String,Object> map=new HashMap<>();
        map.put("tenantId",tenantId);
        //map.put("parentId",areaId);
        //result.add(parentId);
        List<Area> list=areaMapper.queryAreaList(map);
        getChildAreaList(list,areaId,resultList);
        return resultList;
    }
    private static List<BigInteger> getChildAreaList(List<Area> list, BigInteger areaId, List<BigInteger> reList) {
        for (Area a:list) {
            if (a.getParentId().equals(areaId)) {//查询下级菜单
                BigInteger id=a.getId();
                reList.add(id);
                getChildAreaList(list, id, reList);
            }
        }
        return reList;
    }
    public String getChildAreaBranchIds(BigInteger tenantId,BigInteger areaId){
        List<BigInteger> resultList = new ArrayList<>();
        resultList.add(areaId);
        List<BigInteger> areaIds = getChildAreaIds(tenantId,areaId,resultList);
        String areaIdStr = "";
        for(BigInteger id:areaIds){
            areaIdStr = areaIdStr+id+",";
        }
        if(areaIdStr.length()>0){
            areaIdStr = areaIdStr.substring(0,areaIdStr.length()-1);
            Map params = new HashMap();
            params.put("tenantId",tenantId);
            params.put("areaIds",areaIdStr);
            return branchMapper.getChildBranchIds(params);
        }else{
            return "";
        }
    }


    @Transactional(readOnly = true)
    public ApiRest listBranches(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        params.put("tenantId", tenantId);
        String areaId = (String)params.get("areaId");
        String childAreaIds = null;
        if(StringUtils.isNotBlank(areaId) && !"-1".equals(areaId)){
            childAreaIds = branchMapper.getAreaChildLst(areaId);
            childAreaIds = childAreaIds.substring(2);
        }
        if(params.get("branchId") != null && params.get("isSingleBranch") != null && "true".equals(params.get("isSingleBranch"))){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("branchId"));
            Branch branch = branchMapper.find(param);
            if(branch != null && branch.getBranchType() != 0){
                params.put("singleBranchId", branch.getId());
                params.put("isUserAreas", false);
            }
        }
        //权限控制区域
        Map bMap = new HashMap();
        params.put("userAreas", "null");
        if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
            if(params.get("userId") != null && !"".equals(params.get("userId"))){
                BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
                Employee e = employeeMapper.getEmployee(userId, tenantId);
                String userAreas = e.getUserAreas();
                if(userAreas == null || "".equals(userAreas)){
                    userAreas = "null";
                }
                params.put("userAreas", userAreas);
                //员工拥有本店权限
                params.put("eBranchId", e.getBranchId());
                //有权限的子区域
                if(childAreaIds != null){
                    String childIds[] = childAreaIds.split(",");
                    String ids = "";
                    if(StringUtils.isNotBlank(areaId)){
                        ids = areaId;
                        params.put("childAreaIds", ids);
                    }
                    else{
                        for(int i = 0; i < childIds.length; i++){
                            if(userAreas.indexOf(childIds[i]) != -1){
                                ids += childIds[i] + ",";
                            }
                        }
                        params.put("childAreaIds", ids.substring(0, ids.lastIndexOf(",")));
                    }
                }
                if("0000".equals(e.getCode())){
                    params.put("authorityType", 1);
                }
            }
        }
        else{
            params.put("authorityType", 1);
            params.put("childAreaIds", childAreaIds);
        }
        Map<String, Object> data = new HashMap<>();
        Long branchCount = branchMapper.queryBranchCount(params);
        List<Map<String, Object>> branches = new ArrayList<>();
        if(branchCount > 0){
            if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
                Integer rows = Integer.parseInt(params.get("rows").toString());
                Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
                params.put("rows", rows);
                params.put("offset", offset);
            }
            branches = branchMapper.queryBranchList(params);
        }
        else{
            BigInteger branchId;
            if(params.get("eBranchId") != null && !"".equals(params.get("eBranchId"))){
                branchId = BigInteger.valueOf(Long.parseLong(params.get("eBranchId").toString()));
            }
            else{
                branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
            }

            bMap.put("tenantId", tenantId);
            bMap.put("singleBranchId", branchId);
            bMap.put("userAreas", params.get("userAreas") == "null" ? null : params.get("userAreas"));
            bMap.put("authorityType", params.get("authorityType"));
            bMap.put("codeName", params.get("codeName") == "null" ? null : params.get("codeName"));
            if((!"-1".equals(areaId) && areaId != null) || !"".equals(params.get("codeName")) || params.get("codeName") != null){
                bMap.put("childAreaIds", "-1");
            }
            branches = branchMapper.queryBranchList(bMap);
            branchCount = Long.parseLong("1");
        }
        data.put("rows", branches);
        data.put("total", branchCount);
        apiRest.setData(data);
        apiRest.setMessage("查询机构列表成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest getBranchAreaList(Map params){
        ApiRest apiRest = new ApiRest();
        List<Area> areas = areaMapper.queryAreaList(params);
        apiRest.setData(areas);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询区域列表成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest getBranchList(Map params){
        ApiRest apiRest = new ApiRest();
        List<Branch> areas = branchMapper.findBranchByTenantId(params);
        apiRest.setData(areas);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询机构列表成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listAreas(Map params){
        ApiRest apiRest = new ApiRest();
        //权限区域
        params.put("userAreas", "null");
        params.put("authorityType", -1);
        if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
            if(params.get("userId") != null && !"".equals(params.get("userId"))){
                BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
                BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
                Employee e = employeeMapper.getEmployee(userId, tenantId);
                String userAreas = e.getUserAreas();
                //本店所属区域
                    /*Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId,e.getBranchId());
                    userAreas = userAreas + "," + branch.getAreaId();*/
                if(userAreas == null || "".equals(userAreas)){
                    userAreas = "null";
                }
                params.put("userAreas", userAreas);
                if("0000".equals(e.getCode())){
                    params.put("authorityType", 1);
                }
            }
        }
        else{
            params.put("authorityType", 1);
        }
        List<Area> areas = areaMapper.queryAreaList(params);
        List<ZTree> zTrees = new ArrayList<ZTree>();
        zTrees.add(new ZTree("-1", "-999", "全部机构", 0, true, "-999"));
        for(Area area : areas){
            ZTree zTree = new ZTree(area.getId().toString(), area.getParentId().toString(), area.getName(), 0, true, area.getCode());
            zTree.setGroupCode(area.getGroupCode());
            zTrees.add(zTree);
        }
        apiRest.setData(zTrees);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询区域列表成功！");
        return apiRest;
    }

    /**
     * 修改区域分组
     *
     * @param params
     * @return
     */
    public ApiRest changeAreaGroup(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("id") != null && params.get("tenantId") != null){
            Area area = areaMapper.find(new BigInteger(params.get("tenantId").toString()), new BigInteger(params.get("id").toString()));
            if(area != null){
                if(params.get("isShared") != null){
                    Map<String, Object> param = new HashMap<>();
                    param.put("tenantId", params.get("tenantId"));
                    param.put("codeMap", area.getCode());
                    if(params.get("isShared").equals("true")){//设置区域共享，判断卡号和手机号是否重复
                        String branchIds = getChildAreaBranchIds(area.getTenantId(),area.getId());
                        if(branchIds != null && branchIds.length()>0){
                            Map<String, Object> param0 = new HashMap<>();
                            param0.put("tenantId", params.get("tenantId"));
                            param0.put("groups", branchIds);
                            int count = vipMapper.isHasRepeatPhoneOfVip(param0);
                            if(count > 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setError("存在重复手机号会员，不能设置为共享");
                                return apiRest;
                            }
                            int countCard = vipMapper.isHasRepeatCardCode(param0);
                            if(countCard > 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setError("存在重复卡号会员，不能设置为共享");
                                return apiRest;
                            }
                        }
                    }
                    List<Area> list = areaMapper.findByCondition(param);
                    if(list != null && list.size() > 0){
                        String areaIds = "";
                        for(Area a : list){
                            if("true".equals(params.get("isShared"))){
                                a.setGroupCode(area.getCode());
                            }
                            else if("false".equals(params.get("isShared"))){
                                a.setGroupCode("");
                            }
                            areaIds += a.getId() + ",";
                            area.setLastUpdateAt(new Date());
                            areaMapper.update(a);
                        }
                        if(areaIds.length() > 0){
                            areaIds = areaIds.substring(0, areaIds.length() - 1);
                        }
                        param.put("areaIds", areaIds);
                        List<Branch> branches = branchMapper.findBranchByTenantId(param);
                        if(branches != null && branches.size() > 0){
                            for(Branch branch : branches){
                                if("true".equals(params.get("isShared"))){
                                    branch.setGroupCode(area.getCode());
                                }
                                else if("false".equals(params.get("isShared"))){
                                    branch.setGroupCode("");
                                }
                                branch.setLastUpdateAt(new Date());
                                branchMapper.update(branch);
                            }
                        }
                    }
                }
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改区域分组成功！");
        return apiRest;
    }

    /**
     * 修改区域分组(全部共享或独立)
     *
     * @param params
     * @return
     */
    public ApiRest changeAreaGroupAll(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("tenantId") != null){
            List<Area> areas = areaMapper.findAllByTenantId(new BigInteger(params.get("tenantId").toString()));
            if(params.get("vipManageValue") != null && areas != null && areas.size() > 0){
                for(Area area : areas){

                    //会员共享规则由独立改为共享（全部共享或区域共享）时，判断会员是否存在重复手机号
                    if(("".equals(area.getGroupCode()) || area.getGroupCode() == null ) && !"2".equals(params.get("vipManageValue").toString())){
                        Map p = new HashMap();
                        p.put("tenantId", area.getTenantId());
                        int count = vipMapper.isHasRepeatPhoneOfVip(p);
                        if(count > 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("存在重复手机号会员，不能设置为共享");
                            return apiRest;
                        }
                        int countCard = vipMapper.isHasRepeatCardCode(p);
                        if(countCard > 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("存在重复卡号会员，不能设置为共享");
                            return apiRest;
                        }
                    }
                    if("1".equals(params.get("vipManageValue").toString())){
                        area.setGroupCode("00");
                    }
                    else if("2".equals(params.get("vipManageValue"))){
                        area.setGroupCode("");
                    }
                    else if("3".equals(params.get("vipManageValue"))){
                        area.setGroupCode("");
                    }
                    area.setLastUpdateAt(new Date());
                    areaMapper.update(area);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("tenantId", params.get("tenantId"));
                List<Branch> branches = branchMapper.findBranchByTenantId(map);
                for(Branch branch : branches){
                    if("1".equals(params.get("vipManageValue").toString())){
                        branch.setGroupCode("00");
                    }
                    else if("2".equals(params.get("vipManageValue"))){
                        branch.setGroupCode("");
                    }
                    else if("3".equals(params.get("vipManageValue"))){
                        branch.setGroupCode("");
                    }
                    branch.setLastUpdateAt(new Date());
                    branchMapper.update(branch);
                }
            }
        }

        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改成功！");
        return apiRest;
    }

    /**
     * 查询分组
     *
     * @param params
     * @return
     */
    public ApiRest getVipManageValue(Map params){
        ApiRest apiRest = new ApiRest();
        List<Area> areas = areaMapper.findAllByTenantId(new BigInteger(params.get("tenantId").toString()));
        Integer value = 1;
        if(areas != null && areas.size() > 0){
            String gCode = areas.get(0).getGroupCode();
            if("".equals(gCode)){
                value = 2;
            }
            for(Area area : areas){
                if(gCode != null && !gCode.equals(area.getGroupCode())){
                    value = 3;
                }
            }
        }
        apiRest.setData(value);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询分组成功！");
        return apiRest;
    }

    public ApiRest addOrUpdateArea(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        if(params.get("id") != null){
            BigInteger id = BigInteger.valueOf(Long.valueOf(params.get("id").toString()));
            Area area = areaMapper.find(tenantId, id);
            Validate.notNull(area, "修改失败，未查询到机构区域！");

            area.setName(params.get("name").toString());
            area.setLastUpdateAt(new Date());
            areaMapper.update(area);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改区域成功！");
        }
        else{
                /*if(params.get("userId") != null && !"".equals(params.get("userId"))){
                    BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
                    Employee employee = employeeMapper.getEmployee(userId, tenantId);
                    if(!"0000".equals(employee.getCode())){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("该机构不可新增区域");
                        return apiRest;
                    }
                }*/
            Area area = new Area();
            Date currentDate = new Date();
            BigInteger parentId = BigInteger.valueOf(Long.valueOf(params.get("parentId").toString()));
            String areaCode = null;
            if(params.get("code") != null && !"".equals(params.get("code"))){
                areaCode = params.get("code").toString();
            }
            if(parentId.compareTo(new BigInteger("-1")) != 0){
                Area parentArea = areaMapper.find(tenantId, parentId);
                Validate.notNull(parentArea, "新增区域失败，未查询到父级区域！");
                Validate.isTrue(parentArea.getCode().length() < 8, "新增机构区域失败，最多可以添加四级区域！");
                areaCode = queryNextCodeByParentId(tenantId, parentId, parentArea.getCode());
                area.setGroupCode(parentArea.getGroupCode());
            }else{
                area.setGroupCode("00");
            } /*else {
                    Validate.isTrue(false, "新增区域失败，顶级区域只可以有一个！");
                }*/
            area.setCode(areaCode);
            area.setTenantId(tenantId);
            area.setParentId(parentId);
            area.setName(params.get("name").toString());
            area.setCreateAt(currentDate);
            area.setLastUpdateAt(currentDate);
            areaMapper.insert(area);
            apiRest.setData(area);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增区域成功，为避免您在使用中出现问题，请先为本区域设置会员共享规则！");
        }
        return apiRest;
    }

    @Transactional
    public ApiRest deleteArea(BigInteger tenantId, BigInteger areaId){
        ApiRest apiRest = new ApiRest();
        Area area = areaMapper.find(tenantId, areaId);
        Validate.notNull(area, "未查询到机构区域信息！");

        int childAreaCount = areaMapper.findChildAreaCount(tenantId, areaId);
        Validate.isTrue(childAreaCount == 0, "此区域下有子区域不能被删除！");

        int branchCount = areaMapper.findBranchCount(tenantId, areaId);

        Validate.isTrue(branchCount == 0, "此区域下有机构不能被删除！");

        area.setIsDeleted(true);
        area.setLastUpdateAt(new Date());
        areaMapper.update(area);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除成功！");
        return apiRest;
    }
     @Transactional
      public void updateBranch(Map params){ //首页门店信息更新
         params.put("tenantId",Long.valueOf(params.get("tenantId").toString()));
         params.put("branchId",Long.valueOf(params.get("branchId").toString()));
         int result=branchMapper.updateBranch(params);
         if(result<=0){
        throw new RuntimeException("修改失败");
         }
     }
    @Transactional(readOnly = true)
    public ApiRest findBranchById(BigInteger tenantId, BigInteger branchId){
        ApiRest apiRest = new ApiRest();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        Branch branch = branchMapper.findByTenantIdAndBranchId(params);
        Validate.notNull(branch, "机构不存在！");

        Area area = areaMapper.find(branch.getTenantId(), branch.getAreaId());
        Validate.notNull(area, "机构区域不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("branch", branch);
        data.put("area", area);
        if(branch.getDistributionCenterId() != null){
            Branch distributionCenter = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branch.getDistributionCenterId());
            data.put("distributionCenter", distributionCenter);
        }

        apiRest.setData(data);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    @Transactional
    public ApiRest addOrUpdateBranch(Map params) throws IOException{
        ApiRest apiRest = new ApiRest();
//            Map<String, Object> data = new HashMap<String, Object>();
        String userName = params.get("userName").toString();
        Date currentDate = new Date();
        Branch branch = new Branch(params);
        if(branch.getBranchType() == 1){
            branch.setIsTinyhall(false);
        }

        Area area = areaMapper.find(branch.getTenantId(), branch.getAreaId());
        Validate.notNull(area, "查询区域信息失败！");

        if(branch.getId() != null){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tenantId", branch.getTenantId());
            map.put("branchId", branch.getId());
            Branch persistentBranch = branchMapper.findByTenantIdAndBranchId(map);
            Validate.notNull(branch, "未查询到机构信息！");
            boolean isManage = persistentBranch.getIsManageGoods();
            persistentBranch.setName(branch.getName());
            persistentBranch.setPhone(branch.getPhone());
            persistentBranch.setContacts(branch.getContacts());
            persistentBranch.setAddress(branch.getAddress());
            persistentBranch.setGeolocation(branch.getGeolocation());
            persistentBranch.setShippingPriceType(branch.getShippingPriceType());
            persistentBranch.setMemo(branch.getMemo());
            persistentBranch.setAreaId(branch.getAreaId());
            persistentBranch.setIsTinyhall(branch.getIsTinyhall());
            persistentBranch.setStartTakeoutTime(branch.getStartTakeoutTime());
            persistentBranch.setEndTakeoutTime(branch.getEndTakeoutTime());
            persistentBranch.setTakeoutTime(branch.getTakeoutTime());
            persistentBranch.setDistributionCenterId(branch.getDistributionCenterId());
            persistentBranch.setIsDockingExternalSystem(branch.getIsDockingExternalSystem());
            persistentBranch.setIsAutomaticExamination(branch.isAutomaticExamination());
            persistentBranch.setElemeAccountType(branch.getElemeAccountType());
            /*新增-是否管理自营商品*/
            persistentBranch.setIsManageGoods(branch.getIsManageGoods());
            /*新增-是否必须使用手机要货设置*/
            persistentBranch.setIsControlRange(branch.getIsControlRange());
            if(branch.getIsDockingExternalSystem()){
                persistentBranch.setExternalSystem(branch.getExternalSystem());
            }
            else{
                persistentBranch.setExternalSystem(null);
            }
            /*新增手机要货范围*/
            persistentBranch.setIsControlRange(branch.getIsControlRange());
            /*新增盘点时是否隐藏库存数量*/
            persistentBranch.setIsHiddenStore(branch.getIsHiddenStore());
            /*是否使用总部菜品*/
            persistentBranch.setIsUseHqGoods(branch.getIsUseHqGoods());
            if(branch.getIsTinyhall()){
                persistentBranch.setIsTakeout(branch.getIsTakeout());
                persistentBranch.setIsBuffet(params.get("isBuffet").equals("true"));
                persistentBranch.setIsInvite(params.get("isInvite").equals("true"));
                persistentBranch.setAmount(branch.getAmount());
                persistentBranch.setTakeoutAmount(branch.getTakeoutAmount());
                persistentBranch.setTakeoutStatus(branch.getTakeoutStatus());
//                    persistentBranch.setStartTakeoutTime(branch.getStartTakeoutTime());
//                    persistentBranch.setEndTakeoutTime(branch.getEndTakeoutTime());
//                    persistentBranch.setTakeoutTime(branch.getTakeoutTime());
                persistentBranch.setIsAllowPayLater(branch.getIsAllowPayLater());
                String payWays = "";
                if("true".equals(params.get("wxPay"))){
                    payWays = payWays + "1,";
                }
                if("true".equals(params.get("arPay"))){
                    payWays = payWays + "3,";
                }
                if("true".equals(params.get("storePay"))){
                    payWays = payWays + "4,";
                }
                if("true".equals(params.get("taPay"))){
                    payWays = payWays + "5,";
                }
                    /*if (!payWays.equals("") && payWays.substring(payWays.length() - 1).equals(",")) {
                        payWays = payWays.substring(0, payWays.length() - 1);
                    }*/
                if(StringUtils.isNotBlank(payWays)){
                    persistentBranch.setAllowPayWay(payWays.substring(0, payWays.length() - 1));
                }
                /*新增-外卖起送价，配送费*/
                persistentBranch.setAmount(branch.getAmount());
                persistentBranch.setTakeoutAmount(branch.getTakeoutAmount());
            }
            else{
                persistentBranch.setIsTakeout(false);
                persistentBranch.setIsBuffet(false);
                persistentBranch.setIsInvite(false);
                persistentBranch.setAmount(null);
                persistentBranch.setTakeoutRange(null);
                persistentBranch.setTakeoutAmount(null);
//                    persistentBranch.setStartTakeoutTime(null);
//                    persistentBranch.setEndTakeoutTime(null);
                persistentBranch.setIsAllowPayLater(false);
            }
            persistentBranch.setLastUpdateAt(currentDate);
            persistentBranch.setLastUpdateBy(userName);

            if(area.getGroupCode() != null){
                persistentBranch.setGroupCode(area.getGroupCode());
            }
            branchMapper.update(persistentBranch);
            posMapper.updatePosBranchNameByBranchId(branch.getId(), branch.getName());
//                data.put("branch", branch);
            /*
             * 自营关闭-自己创建菜牌停用
             * 自营开启-自己创建菜牌启用
             * */
            Integer status;
            if(!branch.getIsManageGoods() && isManage){
                status = 0;
                Menu menu = new Menu();
                menu.setCreateBranchId(persistentBranch.getId());
                menu.setStatus(status);
                menu.setLastUpdateAt(new Date());
                menuMapper.menuStatusSetting(menu);
            }
            apiRest.setData(persistentBranch);
            apiRest.setMessage("保存成功！");
            apiRest.setIsSuccess(true);
        }
        else{
            if(branch.getBranchType() == 4){
                int count = branchMapper.checkIsHasMall(params);
                if(count > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("只能有一个积分商城门店");
                    return apiRest;
                }
            }
            if(branch.getIsTakeout()){
                branch.setIsBuffet(params.get("isBuffet").equals("true"));
                branch.setIsInvite(params.get("isInvite").equals("true"));
                String payWays = "";
                if("true".equals(params.get("wxPay"))){
                    payWays = payWays + "1,";
                }
                if("true".equals(params.get("arPay"))){
                    payWays = payWays + "3,";
                }
                if("true".equals(params.get("storePay"))){
                    payWays = payWays + "4,";
                }
                if("true".equals(params.get("taPay"))){
                    payWays = payWays + "5,";
                }
                if(StringUtils.isNotBlank(payWays)){
                    branch.setAllowPayWay(payWays.substring(0, payWays.length() - 1));
                }
            }
            else{
                branch.setIsTinyhall(false);
                branch.setAmount(null);
                branch.setTakeoutRange(null);
                branch.setTakeoutAmount(null);
//                    branch.setStartTakeoutTime(null);
//                    branch.setEndTakeoutTime(null);
            }

            BigInteger tenantId = branch.getTenantId();
            String tenantCode = (String)params.get("tenantCode");

            ApiRest maxEmpNumRest = tenantConfigService.getMaxValue(tenantId, SysConfig.SYS_EMP_NUM);
            Validate.isTrue(maxEmpNumRest.getIsSuccess(), maxEmpNumRest.getError());

            String maxEmpNum = (String)maxEmpNumRest.getData();

            ApiRest checkSysEmpNumRest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM);
            Validate.isTrue(checkSysEmpNumRest.getIsSuccess(), "最多可添加" + maxEmpNum + "个员工，不能再创建机构");

            ApiRest maxBranchNumRest = tenantConfigService.getMaxValue(tenantId, SysConfig.SYS_BRANCH_NUM);
            Validate.isTrue(maxBranchNumRest.getIsSuccess(), maxBranchNumRest.getError());

            String maxBranchNum = (String)maxBranchNumRest.getData();

            ApiRest checkSysBranchNumRest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_BRANCH_NUM);

            Validate.isTrue(checkSysBranchNumRest.getIsSuccess(), "最多可添加" + maxBranchNum + "个机构！");
            branch.setStatus(1);
            branch.setCreateAt(currentDate);
            branch.setCreateBy(userName);
            branch.setLastUpdateAt(currentDate);
            branch.setLastUpdateBy(userName);
            if(area.getGroupCode() != null){
                branch.setGroupCode(area.getGroupCode());
            }
            Branch mainBranch = branchMapper.getMainBranch(tenantId.toString());
            /*
             * 自营关闭-自己创建菜牌停用
             * 自营开启-自己创建菜牌启用
             * */
                /*Integer status;
                if(!branch.getIsManageGoods()&&branch.getBranchType()!=0){
                    status = 0;
                    Menu menu = new Menu();
                    menu.setCreateBranchId(branch.getId());
                    menu.setStatus(status);
                    menu.setLastUpdateAt(new Date());
                    menuMapper.menuStatusSetting(menu);
                }*/

            if(mainBranch == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询总部机构失败！");
                return apiRest;
            }
            branch.setCommercialType(mainBranch.getCommercialType());
            branch.setEnvironmentType(mainBranch.getEnvironmentType());
            branchMapper.insert(branch);
            List<Payment> mainPayments = paymentMapper.getMainPayments(tenantId, mainBranch.getId());
            for(Payment payment : mainPayments){
                PaymentBranch payment1 = new PaymentBranch();
                payment1.setId(null);
                payment1.setCreateAt(new Date());
                payment1.setCreateBy("System");
                payment1.setLastUpdateAt(new Date());
                payment1.setLastUpdateBy("System");
                payment1.setCurrencyId(payment.getCurrencyId());
                payment1.setTenantPaymentId(payment.getId());
                payment1.setPaymentCode(payment.getPaymentCode());
                payment1.setPaymentName(payment.getPaymentName());
                payment1.setPaymentStatus(payment.getPaymentStatus());
                payment1.setBranchId(branch.getId());
                payment1.setIsScore(payment.getIsScore());
                payment1.setIsChange(payment.getChange());
                payment1.setIsMemo(payment.getMemo());
                payment1.setIsSale(payment.getSale());
                payment1.setFixValue(payment.getFixValue());
                payment1.setFixNum(payment.getFixNum());
                payment1.setPaymentType(payment.getPaymentType());
                payment1.setIsVoucher(payment.getIsVoucher());
                payment1.setVersion(BigInteger.ONE);
                payment1.setTenantId(tenantId);
                payment1.setDeleted(false);
                payment1.setIsStore(payment.getIsStore());
                payment1.setIsOpenCashbox(payment.getIsOpenCashbox());
                int k = paymentMapper.insertBranchPayment(payment1);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("保存支付方式数据失败！");
                    return apiRest;
                }

            }
                /*BigInteger branchId= branchMapper.initBranchPayment(branch.getTenantId(),branch.getId());
                if(branchId.equals(BigInteger.valueOf(-1))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("初始化机构支付信息失败！");
                    return apiRest;
                }*/
            Employee employee = initBranchInfo(branch.getBranchType(),tenantCode, tenantId, branch.getId(), userName);
            if(employee == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("初始化店长失败！");
                return apiRest;
            }
//                addBranchProduct(tenantId, branch.getId());
            String deploymentLocation = PropertyUtils.getDefault(Constants.DEPLOYMENT_LOCATION);
            if(StringUtils.isBlank(deploymentLocation) || Constants.SMARTPOS.equals(deploymentLocation)){
                addBranchProduct(tenantId, branch.getId());
            }
            else if(Constants.SANMI.equals(deploymentLocation)){
                addSanmiBranchProduct(tenantId, branch.getId());
            }
//                data.put("branch", branch);
//                data.put("employee", employee);
            apiRest.setData(branch);
            apiRest.setMessage("添加成功，已创建管理员(帐号:" + employee.getLoginName() + ",密码:888888)");
            apiRest.setIsSuccess(true);
        }
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest generateNextBranchCode(BigInteger tenantId){
        ApiRest apiRest = new ApiRest();
        String maxCode = branchMapper.findMaxBranchCode(tenantId);
        String branchCode = SerialNumberGenerate.getNextCode(3, maxCode);
        apiRest.setData(branchCode);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成机构编码成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest generateNextAreaCode(BigInteger tenantId, BigInteger parentId){
        ApiRest apiRest = new ApiRest();
            /*String maxCode = areaMapper.queryNextCode(tenantId, parentId);
            int length = maxCode.length();
            String areaCode = maxCode.substring(0, length - 2) + SerialNumberGenerate.nextSerialNumber(2, maxCode.substring(length - 2));
            apiRest.setData(areaCode);*/
        Area parentArea = areaMapper.find(tenantId, parentId);
        /*Validate.notNull(parentArea, "未检索到父级区域！");*/
        String areaCode;
        if(parentArea == null){
            areaCode = queryNextCodeByParentId(tenantId, parentId, "");
        }
        else{
            areaCode = queryNextCodeByParentId(tenantId, parentId, parentArea.getCode());
        }
        apiRest.setData(areaCode);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成区域编码成功！");
        return apiRest;
    }

    @Transactional
    public ApiRest deleteBranch(BigInteger tenantId, BigInteger branchId, String userName){
        ApiRest apiRest = new ApiRest();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tenantId", tenantId);
        map.put("branchId", branchId);
        Branch branch = branchMapper.findByTenantIdAndBranchId(map);
        if(branch.getCode().equals("000")){
            apiRest.setIsSuccess(false);
            apiRest.setError("总部禁止删除！");
            return apiRest;
        }
        if(branch.getBranchType() == 1){
            Map sumParams = new HashMap();
            sumParams.put("branchId", branch.getId());
            Long count = branchMapper.isDistributionCenterSum(sumParams);
            if(count > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("已被其他机构指定为配送中心，禁止删除！");
                return apiRest;
            }
        }
        branch.setIsDeleted(true);
        branch.setLastUpdateAt(new Date());
        branch.setLastUpdateBy(userName);
        branchMapper.update(branch);
        //删除机构后删除机构下的员工
        Map param = new HashMap();
        param.put("tenantId", branch.getTenantId());
        param.put("branchId", branch.getId());
        List<Map> employeeList = employeeMapper.queryEmployee(param);
        if(employeeList != null && employeeList.size() > 0){
            for(int i = 0; i < employeeList.size(); i++){
                Map employeeMap = employeeList.get(i);
                Employee employee = new Employee();
                BigInteger id = BigInteger.valueOf(Long.parseLong(employeeMap.get("id").toString()));
                BigInteger userId = BigInteger.valueOf(Long.parseLong(employeeMap.get("userId").toString()));
                List<Map> roles = employeeMapper.queryRoles(userId);
                int a = employeeMapper.deleteOldRole(userId);
                if(a < roles.size()){
                    apiRest.setError("删除原角色失败！");
                    apiRest.setIsSuccess(false);
                    return apiRest;
                }
                ApiRest ApiRest = AuthApi.deleteSysUser(userId);
                if(!ApiRest.getIsSuccess()){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("删除用户失败！");
                    return apiRest;
                }
                ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_EMP_NUM);
                if(!r.getIsSuccess()){
                    return r;
                }
                employee.setId(id);
                employee.setIsDeleted(true);
                employee.setLastUpdateAt(new Date());
                employee.setLastUpdateBy("System");
                int m = employeeMapper.deleteEmployee(employee);
                if(m <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("删除员工信息失败！");
                    return apiRest;
                }
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除机构成功！");
        return apiRest;
    }

    private String queryNextCodeByParentId(BigInteger tenantId, BigInteger parentId, String parentAreaCode){
        String maxCode = areaMapper.queryNextCode(tenantId, parentId);
        String nextCode = null;
        if(maxCode != null){
            int length = maxCode.length();
            nextCode = maxCode.substring(0, length - 2) + SerialNumberGenerate.nextSerialNumber(2, maxCode.substring(length - 2));
        }
        else{
            nextCode = parentAreaCode + "01";
        }
        return nextCode;
    }

    private Employee initBranchInfo(Integer branchType,String tenantCode, BigInteger tenantId, BigInteger branchId, String userName){
        SysRole sysRole = new SysRole();
        String initName = "";
        if(branchType == 1 || branchType == 2 || branchType == 3 || branchType == 4){
            sysRole = roleMapper.getShopManager(tenantId);
            initName = "店长";
        }else if(branchType == 5){
            sysRole = roleMapper.getShopManager(tenantId);
            initName = "市场管理员";
        }else if(branchType == 6){
            sysRole = roleMapper.getStallsManager(tenantId);
            initName = "档口管理员";
        }
        if(sysRole == null){
            return null;
        }
        String maxCode = employeeMapper.getEmployeeCode(tenantId);
        Validate.notEmpty(maxCode, "机构初始化失败！");
        String employeeCode = SerialNumberGenerate.nextSerialNumber(4, maxCode);
        Map<String, String> map = new HashMap<String, String>();
        map.put("tenantId", tenantId.toString());
        map.put("tenantCode", tenantCode);
        map.put("employeeCode", employeeCode);
        map.put("flag", "new");
        ApiRest registerShopManagerRest = ProxyApi.proxyGet("bs", "user", "registerShopManager", map);
        Validate.isTrue(registerShopManagerRest.getIsSuccess(), registerShopManagerRest.getError());
        Map<String, Object> data = (Map<String, Object>)registerShopManagerRest.getData();
        BigInteger userId = new BigInteger(data.get("id").toString());
        String loginPass = (String)data.get("loginPass");

        Employee employee = new Employee();
        employee.setTenantId(tenantId);
        employee.setDiscountAmount(0);
        employee.setDiscountRate(100);
        employee.setCreateBy(userName);
        employee.setUserId(userId);
        employee.setLoginName(RegisterApi.tenantEmployeeLoginName(employeeCode, tenantCode));
        employee.setPasswordForLocal(loginPass);
        employee.setCode(employeeCode);
        employee.setName(initName);
        employee.setState(1);
        employee.setBranchId(branchId);
        employee.setCreateAt(new Date());
        employee.setCreateBy("System");
        employee.setLastUpdateAt(new Date());
        employee.setLastUpdateBy("System");
        employeeMapper.insert(employee);


        employeeMapper.saveRole(userId, sysRole.getId(), sysRole.getTenantId());

        return employee;
    }

    private void addBranchProduct(BigInteger tenantId, BigInteger branchId){
        ApiRest addBranchProductRest = SaaSApi.addBranchProduct(tenantId, branchId);
        Validate.isTrue(addBranchProductRest.getIsSuccess(), addBranchProductRest.getMessage());
    }

    private void addSanmiBranchProduct(BigInteger tenantId, BigInteger branchId){
        Map<String, String> params = new HashMap<String, String>();
        params.put("tenantId", tenantId.toString());
        params.put("branchId", branchId.toString());
        ApiRest addSanmiBranchProductRest = ProxyApi.proxyGet(Constants.SERVICE_NAME_BS, "tenant", "addSanmiBranchProduct", params);
        Validate.isTrue(addSanmiBranchProductRest.getIsSuccess(), addSanmiBranchProductRest.getError());
    }

    @Transactional(readOnly = true)
    public ApiRest listBranchAreaTree(BigInteger tenantId, BigInteger distributionCenterId, Map params){
        ApiRest apiRest = new ApiRest();
        List<Area> areas = areaMapper.findAllByTenantId(tenantId);
        List<Branch> branches = branchMapper.findAllByBranchTypeInListAndTenantIdAndDistributionCenterId(new Integer[]{0, 2, 3}, tenantId, distributionCenterId);

        List<ZTree> zTrees = new ArrayList<ZTree>();

        //权限控制区域
        String userAreas = null;
        if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas"))){
            if(params.get("userId") != null && !"".equals(params.get("userId"))){
                BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
                Employee e = employeeMapper.getEmployee(userId, tenantId);
                userAreas = e.getUserAreas();
                if("0000".equals(e.getCode())){
                    for(Area area : areas){
                        zTrees.add(new ZTree(area.getId() + "_are", area.getParentId() + "_are", area.getName(), "0", true, true));
                    }
                    for(Branch branch : branches){
                        zTrees.add(new ZTree(branch.getId().toString(), branch.getAreaId() + "_are", branch.getCode() + "-" + branch.getName(), false, "1"));
                    }
                    apiRest.setData(zTrees);
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("查询区域树成功！");
                    return apiRest;
                }
            }
        }
        for(Area area : areas){
            if(userAreas != null){
                if(userAreas.indexOf(area.getId().toString()) != -1){
                    zTrees.add(new ZTree(area.getId() + "_are", area.getParentId() + "_are", area.getName(), "0", true, true));
                }
            }
        }

        for(Branch branch : branches){
            zTrees.add(new ZTree(branch.getId().toString(), branch.getAreaId() + "_are", branch.getCode() + "-" + branch.getName(), false, "1"));
        }

        apiRest.setData(zTrees);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询区域树成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listBranchAreaTreeAll(BigInteger tenantId, Map params){
        ApiRest apiRest = new ApiRest();
        List<Area> areas = areaMapper.findAllByTenantId(tenantId);
        Map pa = new HashMap();
        pa.put("tenantId", tenantId);
        List<ZTree> zTrees = new ArrayList<ZTree>();
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee e = employeeMapper.getEmployee(userId, tenantId);
        //权限控制区域
        if(params.get("isUserAreas") != null && "true".equals(params.get("isUserAreas")) && !"0000".equals(e.getCode())){
            String userAreas = e.getUserAreas();
            Branch b = branchMapper.findBranchByTenantIdAndBranchId(tenantId, e.getBranchId());
            if(userAreas != null && !"".equals(userAreas)){
                userAreas = userAreas + "," + b.getAreaId();
            }
            pa.put("areaIds", userAreas);

            for(Area area : areas){
                if(userAreas != null && !"".equals(userAreas)){
                    if(userAreas.indexOf(area.getId().toString()) != -1){
                        zTrees.add(new ZTree(area.getId() + "_are", area.getParentId() + "_are", area.getName(), "0", true, true));
                    }
                }
            }

            List<Branch> branches;
            if(pa.get("areaIds") == null){
                Map bMap = new HashMap();
                bMap.put("tenantId", tenantId);
                bMap.put("id", e.getBranchId());
                branches = branchMapper.findBranchByTenantId(bMap);
            }
            else{
                branches = branchMapper.findBranchByTenantId(pa);
            }

            for(Branch branch : branches){
                if(b.getId() != null && e.getId() != null && userAreas != null && !"".equals(userAreas)){
                    if(e.getUserAreas().indexOf(branch.getAreaId().toString()) == -1){
                        if(branch.getId().compareTo(e.getBranchId()) == 0){
                            zTrees.add(new ZTree(b.getId().toString(), b.getAreaId() + "_are", b.getCode() + "-" + b.getName(), false, "1"));
                        }
                    }
                    else{
                        zTrees.add(new ZTree(branch.getId().toString(), branch.getAreaId() + "_are", branch.getCode() + "-" + branch.getName(), false, "1"));
                    }
                }
                else{
                    if(b.getAreaId().compareTo(branch.getAreaId()) == 0){
                        if(branch.getId().compareTo(e.getBranchId()) == 0){
                            zTrees.add(new ZTree(b.getId().toString(), b.getAreaId() + "_are", b.getCode() + "-" + b.getName(), false, "1"));
                        }
                    }
                }
            }
        }
        else{
            List<Branch> branches = branchMapper.findBranchByTenantId(pa);
            for(Area area : areas){
                zTrees.add(new ZTree(area.getId() + "_are", area.getParentId() + "_are", area.getName(), "0", true, true));
            }

            for(Branch branch : branches){
                zTrees.add(new ZTree(branch.getId().toString(), branch.getAreaId() + "_are", branch.getCode() + "-" + branch.getName(), false, "1"));
            }
        }
        apiRest.setData(zTrees);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询区域树成功！");
        return apiRest;
    }

    public ApiRest findBranchByUser(Map params){
        ApiRest r = new ApiRest();
        String userId = params.get("userId").toString();
        String tenantId = params.get("tenantId").toString();
        if(StringUtils.isEmpty(userId)){
            r = ApiRest.INVALID_PARAMS_ERROR;
            return r;
        }
        Map map = new HashMap();
        Employee e = employeeMapper.getEmployee(BigInteger.valueOf(Long.valueOf(userId)), BigInteger.valueOf(Long.valueOf(tenantId)));
        if(e != null){
            Branch b = branchMapper.findBranchByTenantIdAndBranchId(BigInteger.valueOf(Long.valueOf(tenantId)), e.getBranchId());
            if(b != null){
                map.put("employee", e);
                map.put("branch", b);
                r.setData(map);
            }
        }
        r.setIsSuccess(true);
        return r;
    }


    public ApiRest getMainBranch(Map params){
        ApiRest r = new ApiRest();
        String tenantId = params.get("tenantId").toString();
        Branch branch = branchMapper.getMainBranch(tenantId);
        if(branch == null){
            r.setIsSuccess(false);
            r.setError("查询机构失败！");
            return r;
        }
        r.setIsSuccess(true);
        r.setData(branch);
        r.setMessage("查询机构成功！");
        return r;
    }

    public ApiRest queryAreasList(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
            /*Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);*/
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger userId = BigInteger.valueOf(Long.parseLong(params.get("userId").toString()));
        Employee employee = employeeMapper.getEmployee(userId, tenantId);
        String userAreas = employee.getUserAreas();
        params.put("userAreas", userAreas);
        if("0000".equals(employee.getCode())){
            params.put("authorityType", 1);
        }
        List<Map> list = areaMapper.queryAreasList(params);
        Long count = areaMapper.queryAreasListSum(params);
        map.put("rows", list);
        map.put("total", count);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询区域列表成功");
        return apiRest;
    }

    public ApiRest savePosSlaves(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        Integer posSlaves = Integer.valueOf(params.get("posSlaves").toString());
        Integer slaves = branch.getPosSlaves() == null ? 0 : branch.getPosSlaves();
        branch.setLastUpdateAt(new Date());
        branch.setPosSlaves(posSlaves + slaves);
        int i = branchMapper.update(branch);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("更新机构数据失败！");
            apiRest.setMessage("更新机构数据失败！");
        }
        else{
            apiRest.setData(branch);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("更新机构数据成功！");
        }
        return apiRest;
    }

    public void branchJob(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String yesterday = reportMapper.getDate(today, -1);
        String startDate = yesterday + " 00:00:00";
        String endDate = yesterday + " 23:59:59";
        Integer count = 0;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogUtil.logInfo("有效机构标记任务开始执行，时间" + sdf2.format(new Date()));
        try{
            List<Branch> branchList = branchMapper.getAllBranches();
            LogUtil.logInfo("统计未被标记为有效机构的有" + branchList.size() + "个");
            for(Branch branch : branchList){
                Map params = new HashMap();
                params.put("startDate", startDate);
                params.put("endDate", endDate);
                params.put("tenantId", branch.getTenantId());
                params.put("branchId", branch.getId());
                long result = reportMapper.branchData(params);
                if(result > 10){
                    Integer days = branch.getEffectiveDays() + 1;
                    branch.setEffectiveDays(days);
                    if(days >= 30){
                        count++;
                        branch.setEffectiveDate(new Date());
                        branch.setIsEffective(true);
                        branch.setLastUpdateAt(new Date());
                        Map<String, String> map = new HashMap<>();
                        map.put("tenantId", branch.getTenantId().toString());
                        map.put("branchId", branch.getId().toString());
                        map.put("effectiveDate", sdf2.format(branch.getEffectiveDate()));
                        ApiRest rest = ProxyApi.proxyGet("bs", "tenant", "saveEffectiveBranchInfo", map);
                        if(!rest.getIsSuccess()){
                            LogUtil.logInfo("bs-saveEffectiveBranchInfo失败，branchId=" + branch.getId() + ",tenantId=" + branch.getTenantId());
                        }
                    }
                    int i = branchMapper.update(branch);
                    if(i <= 0){
                        LogUtil.logError("更新机构为有效机构失败！");
                    }
                }
                else{
                    branch.setEffectiveDays(0);
                    branch.setLastUpdateAt(new Date());
                    int i = branchMapper.update(branch);
                    if(i <= 0){
                        LogUtil.logError("更新机构为有效机构失败！");
                    }
                    LogUtil.logInfo("商户ID" + branch.getTenantId() + ",机构【" + branch.getCode() + "】有效天数重置为0，时间" + sdf2.format(new Date()));
                }
            }
        }
        catch(Exception e){
            LogUtil.logError("有效机构定时任务执行出错，时间" + sdf2.format(new Date()) + "，错误信息" + e.getMessage());
        }
        LogUtil.logInfo("更新为有效机构的个数为" + count + "个");
        LogUtil.logInfo("有效机构标记任务结束执行，时间" + sdf2.format(new Date()));
    }

    public ApiRest checkIsHasMall(Map params){
        ApiRest apiRest = new ApiRest();
        int i =branchMapper.checkIsHasMall(params);
        if(i>0){
            apiRest.setIsSuccess(true);
            apiRest.setData(1);
            apiRest.setMessage("已有积分商城门店");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(0);
        apiRest.setError("未创建积分商城门店");
        return apiRest;
    }
    public ApiRest addSmsCount(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        Integer addSmsCount=Integer.valueOf(params.get("addSmsCount").toString());
        Branch branch=branchMapper.findBranchByTenantIdAndBranchId(tenantId,branchId);
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询门店信息失败！");
        }else{
            branch.setSmsCount((branch.getSmsCount()==null?0:branch.getSmsCount())+addSmsCount);
            branch.setLastUpdateAt(new Date());
            branchMapper.update(branch);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("短信充值成功！");
        }
        return apiRest;
    }
    public ApiRest updateBranchSmsUseWay(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        String smsBranchUseWay=params.get("smsBranchUseWay")==null?"":params.get("smsBranchUseWay").toString();
        Branch branch=branchMapper.findBranchByTenantIdAndBranchId(tenantId,branchId);
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询门店信息失败！");
        }else{
            branch.setSmsUseWay(smsBranchUseWay);
            branch.setLastUpdateAt(new Date());
            branchMapper.update(branch);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("更新信息成功！");
        }
        return apiRest;
    }

    public ApiRest isUseVipStatement(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        TenantConfig tenantConfig;
        if("1".equals(params.get("isUseVipStatement"))){
            tenantConfig = new TenantConfig();
            tenantConfig.setName("isUseVipStatement");
            tenantConfig.setValue("1");
            tenantConfig.setMaxValue("999");
            tenantConfig.setDeleted(false);
            tenantConfig.setCreateBy("admin");
            tenantConfig.setCreateAt(new Date());
            tenantConfig.setLastUpdateBy("admin");
            tenantConfig.setLastUpdateAt(new Date());
            tenantConfig.setTenantId(tenantId);
            tenantConfig.setVersion(BigInteger.ZERO);
            tenantConfigMapper.insert(tenantConfig);
        }else{
            tenantConfig = tenantConfigMapper.findByNameAndTenantId("isUseVipStatement", tenantId);
            if(tenantConfig == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到该配置信息");
                return apiRest;
            }
            tenantConfig.setDeleted(true);
            int i = tenantConfigMapper.update(tenantConfig);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改配置信息失败");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改配置信息成功");
        return apiRest;
    }

    public ApiRest queryIsUseVipStatement(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        TenantConfig tenantConfig = tenantConfigMapper.findByNameAndTenantId("isUseVipStatement", tenantId);
        if(tenantConfig == null){
            apiRest.setData(0);
        }else{
            apiRest.setData(1);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询配置信息成功");
        return apiRest;
    }



    /**
     * 查询所有下级机构（不含自身）
     * @param tenantId
     * @param branchId
     * @param resultList
     * @return
     */
    public List<BigInteger> getChildBranchIds(BigInteger tenantId,BigInteger branchId,List<BigInteger> resultList){
        Map<String,Object> map=new HashMap<>();
        map.put("tenantId",tenantId);
        //map.put("parentId",branchId);
        //result.add(parentId);
        List<Branch> list=branchMapper.findBranchByTenantId(map);
        getChildList(list,branchId,resultList);
        return resultList;
    }
    private static List<BigInteger> getChildList(List<Branch> list, BigInteger branchId, List<BigInteger> reList) {
        for (Branch b:list) {
            if (b.getParentId().equals(branchId)) {//查询下级菜单
                BigInteger id=b.getId();
                reList.add(id);
                getChildList(list, id, reList);
            }
        }
        return reList;
    }

    /**
     * 查询直接下级机构（不含自身）
     * @param tenantId
     * @param branchId
     * @param resultList
     * @return
     */
    public List<BigInteger> getFirstChildBranchIds(BigInteger tenantId,BigInteger branchId,List<BigInteger> resultList){
        Map<String,Object> map=new HashMap<>();
        map.put("tenantId",tenantId);
        map.put("parentId",branchId);
        //result.add(parentId);
        List<Branch> list=branchMapper.findBranchByTenantId(map);
        for(Branch b:list){
            resultList.add(b.getId());
        }
        return resultList;
    }
}
