package erp.chain.service.system;

import erp.chain.domain.Branch;
import erp.chain.domain.SysRole;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.RoleMapper;
import erp.chain.mapper.system.InitTenantMapper;
import erp.chain.mapper.system.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户初始化
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/3
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InitTenantService{
    @Autowired
    private InitTenantMapper initTenantMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 初始化商户信息
     *
     * @return
     */
    public ApiRest initTenant(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantid = params.get("tenantid").toString();
        String loginname = params.get("loginname").toString();
        String userid = params.get("userid").toString();
        String business = params.get("business").toString();
        if(tenantid != null && loginname != null && userid != null){
            String[] parmas = loginname.split(",");
            if(parmas.length < 4){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数错误!");
            }
            else{
                loginname = parmas[0];
                String branchName = parmas[1];
                String phone = parmas[2];
                String contacts = parmas[3];
                String businessStr = business;
                if(business.equals("6")){
                    businessStr = "5";
                    business = "5";
                }
                String initType = "0";
                if(params.get("initType") != null && !params.get("initType").toString().equals("")){
                    initType = params.get("initType").toString();
                }
                BigInteger branchId = initTenantMapper.initTenant(tenantid, loginname, userid, branchName, phone, contacts, businessStr, initType);
                if(branchId.equals(BigInteger.valueOf(-1))){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("初始化商户信息失败！");
                    return apiRest;
                }
                Branch branch = branchMapper.findBranchByTenantIdAndBranchId(BigInteger.valueOf(Long.valueOf(tenantid)),branchId);
                if(branch == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询门店失败！");
                    return apiRest;
                }
                if("1".equals(business)){
                    initTenantMapper.insertRestRole(tenantid, branchId);
                        /*sqlString = "select * from template_role_privilege_r where role_id in(1,2,3,4)";//去除 5（区域经理）
                        roleCodeMap = ["1":"01,管理员", "2":"02,收银员", "3":"03,服务员", "4":"04,店长"];
                        defaultPackageName = "saas.web";*/
                }
                else if("2".equals(business)){
                    initTenantMapper.insertRetailRole(tenantid, branchId);
                        /*sqlString = "select * from template_role_privilege_r where role_id in(6,7,8)";
                        roleCodeMap = ["6":"01,管理员", "8":"02,收银员", "7":"04,店长"];
                        defaultPackageName = "saas.retail";*/
                }
                else if("5".equals(business)){
                    initTenantMapper.insertRetailRole(tenantid, branchId);
                        /*sqlString = "select * from template_role_privilege_r where role_id in(6,7,8)";
                        roleCodeMap = ["6":"01,管理员", "8":"02,收银员", "7":"04,店长"];
                        defaultPackageName = "saas.retail";*/
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("角色数据初始化失败,未知业态");
                    return apiRest;
                }
                SysRole sysRole = roleMapper.getManager(tenantid);
                roleMapper.insertRoleUser(sysRole.getId(), userid, sysRole.getTenantId());

                Map map = new HashMap();
                map.put("tenantId", tenantid);
                List<Map> roleList = roleMapper.queryRoleList(map);

                ApiRest rest = ProxyApi.proxyGet("bs", "auth", "listTmpRolePrivileges", null);
                if(!rest.getIsSuccess()){
                    return rest;
                }
                Map privilegeMap = (Map)rest.getData();
                for(Map role : roleList){
                    if(branch.getCommercialType() != null && branch.getCommercialType() == 3){//新农贸
                        if(role.get("roleCode").equals("02")){
                            List privileges = (List)privilegeMap.get("12");
                            initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                        }
                    }else{
                        if(business.equals("1")){
                            if(role.get("roleCode").equals("01")){
                                List privileges = (List)privilegeMap.get("11");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("02")){
                                List privileges = (List)privilegeMap.get("12");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("04")){
                                List privileges = (List)privilegeMap.get("13");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                        }
                        else if(business.equals("2")){
                            if(role.get("roleCode").equals("01")){
                                List privileges = (List)privilegeMap.get("14");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("02")){
                                List privileges = (List)privilegeMap.get("15");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("04")){
                                List privileges = (List)privilegeMap.get("16");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                        }
                        else if(business.equals("5")){
                            if(role.get("roleCode").equals("01")){
                                List privileges = (List)privilegeMap.get("14");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("02")){
                                List privileges = (List)privilegeMap.get("15");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                            if(role.get("roleCode").equals("04")){
                                List privileges = (List)privilegeMap.get("16");
                                initTenantMapper.insertRolePrivilege(role.get("id").toString(), privileges, role.get("tenantId").toString());
                            }
                        }
                        else{
                            apiRest.setIsSuccess(false);
                            apiRest.setError("角色数据初始化失败,未知业态");
                            return apiRest;
                        }
                    }
                }
                apiRest.setData(branchId);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("角色数据初始化成功");
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("请录入数据进行初始化！");
        }
        return apiRest;
    }

    /**
     * 加载菜单
     *
     * @param params
     * @return
     */
    public ApiRest getPrivileges(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        if(params.get("branchId") != null && !params.get("branchId").equals("")){
            BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
            Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
            if(branch != null){
                map.put("branchType", branch.getBranchType());
            }
            map.put("branchId", branchId);
        }
        else{
            map.put("branchId", null);
        }
        String packageName = "erp.chain";
        if(params.get("packageName") != null && !params.get("packageName").toString().equals("")){
            packageName = params.get("packageName").toString();
        }
        map.put("packageName", packageName);
        List<BigInteger> privileges = roleMapper.getRolePrivileges(userId);
        if(privileges == null || privileges.isEmpty()){
            apiRest.setError("权限为空！");
            apiRest.setIsSuccess(false);
            return apiRest;
        }
        map.put("privileges", privileges);
        ApiRest rest = ProxyApi.proxyPost("bs", "auth", "loadPrivilegeAll", map);
        if(!rest.getIsSuccess()){
            return rest;
        }
            /*if(((List)rest.getData()).size()==0){
                apiRest.setIsSuccess(false);
                apiRest.setError("权限为空！");
                return apiRest;
            }*/
        apiRest.setData(rest.getData());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("加载权限成功！");
        return apiRest;
    }

    /**
     * 读取初始化模版
     *
     * @param params
     * @return
     */
    public ApiRest getInitInfo(Map params){
        ApiRest apiRest = new ApiRest();
        String businessId = params.get("businessId").toString();
        String type = params.get("type").toString();
        List<Map> dataTemplateList = initTenantMapper.getInitInfo(businessId, type);
        apiRest.setIsSuccess(true);
        apiRest.setData(dataTemplateList);
        apiRest.setMessage("读取初始化模版成功！");
        return apiRest;
    }

    /**
     * 初始化商户登录数据
     *
     * @param params
     * @return
     */
    public ApiRest tenantLoginInit(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId").toString();
        String businessId = params.get("businessId").toString();
        List resultList = initTenantMapper.tenantLoginInit(businessId, tenantId);
        if(resultList.size() > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("初始化登录数据成功！");
            apiRest.setData(resultList.get(0));
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化登录数据失败！");
        }
        return apiRest;
    }

    /**
     * 初始化商户登录数据
     *
     * @param params
     * @return
     */
    public ApiRest tenantLoginInit2(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId").toString();
        Branch branch = branchMapper.getMainBranch(tenantId);
        if(branch != null){
            if(params.get("commercialType")!=null){
                branch.setCommercialType(Integer.valueOf(params.get("commercialType").toString()));
                branch.setLastUpdateAt(new Date());
                branchMapper.update(branch);
            }
            if(initTenantMapper.countUnit(branch.getTenantId(),branch.getId())==0){
                int i = initTenantMapper.tenantLoginInit2(tenantId, branch.getId().toString());
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("初始化登录数据成功！");
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("初始化登录数据失败！");
                }
            }else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("初始化登录数据成功！");
            }
        }
        return apiRest;
    }

    public ApiRest clearBranchData(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        Integer type = Integer.valueOf(params.get("type"));
        String userName = params.get("userName");
        String opFrom = params.get("opFrom");
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId")));
        Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
        if(branch == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到门店信息！");
            return apiRest;
        }
        int i=initTenantMapper.clearBranchData(tenantId,branchId,userName,type,null,null);
        if(i!=1){
            apiRest.setIsSuccess(false);
            apiRest.setError("清除数据失败！");
            return apiRest;
        }
        String opName = null;
        if(type == 1){//清除销售单据
            opName="清除销售单据";
        }
        else if(type == 2){//清除会员数据
            opName="清除会员数据";
        }
        else if(type == 3){//清除库存单据
            opName="清除库存单据";
        }
        else if(type == 4){//清除基础资料
            opName="清除基础资料";
        }
        else if(type == 5){//清除所有数据
            opName="清除所有数据";
        }
        operateLogMapper.createOpLog("清除数据", null, opName, "0", "1", userName, opFrom, "类型"+type.toString(), tenantId, branchId);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("清除数据成功！");
        return apiRest;
    }
}
