package erp.chain.service.setting;

import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import com.saas.common.util.MD5Utils;
import com.saas.common.util.PartitionCacheUtils;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.*;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.*;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.GsonUntil;
import erp.chain.utils.SerialNumberGenerate;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.AuthApi;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 后台员工
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/1
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmployeeWebService{
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private SaleDetailMapper saleDetailMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CommonMapper commonMapper;


    /**
     * 员工列表
     *
     * @param params
     * @return
     */
    public ApiRest queryEmployee(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = employeeMapper.queryEmployee(params);
        for(Map i : list){
            List<Map> roles = employeeMapper.queryRoles(BigInteger.valueOf(Long.valueOf(i.get("userId").toString())));
            String roleName = "";
            for(Map j : roles){
                roleName += j.get("roleName") + " ";
            }
            i.put("roleName", roleName);
            i.put("roles", roles);
        }
        Long count = employeeMapper.queryEmployeeSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询员工列表成功！");
        return apiRest;
    }

    /**
     * 根据ID查询员工
     *
     * @param params
     * @return
     */
    public ApiRest queryEmployeeById(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = employeeMapper.queryEmployeeById(params);
        if(map.isEmpty()){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询员工失败！");
            return apiRest;
        }
        List<Map> roles = employeeMapper.queryRoles(BigInteger.valueOf(Long.valueOf(map.get("userId").toString())));
        String roleName = "";
        for(Map j : roles){
            roleName += j.get("roleName") + " ";
        }
        map.put("roleName", roleName);
        map.put("roles", roles);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询员工成功！");
        return apiRest;
    }

    /**
     * 新增或修改员工档案
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateEmployee(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String name = params.get("name").toString();
        Integer discountRate = Integer.valueOf(params.get("discountRate").toString());
        Integer discountAmount = Integer.valueOf(params.get("discountAmount").toString());
        Integer state = Integer.valueOf(params.get("state").toString());
        String roleIds = "";
        if(params.get("roles") != null && !params.get("roles").equals("")){
            roleIds = params.get("roles").toString();
        }
        if(params.get("roleIds") != null && !params.get("roleIds").equals("")){
            roleIds = params.get("roleIds").toString();
        }
        if(state != 1 && state != 2){
            apiRest.setIsSuccess(false);
            apiRest.setError("state只能为1或2！");
            return apiRest;
        }
        Integer pricePrivilege = Integer.parseInt(params.get("pricePrivilege") == null ? "1" : params.get("pricePrivilege").toString());
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            Employee employee = employeeMapper.getEmployeeById(id, tenantId);
            if(employee == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询员工失败！");
                return apiRest;
            }
            employee.setLastUpdateAt(new Date());
            employee.setLastUpdateBy("System");
            employee.setName(name);
            employee.setDiscountRate(discountRate);
            employee.setDiscountAmount(discountAmount);
            employee.setVersion(employee.getVersion() == null ? BigInteger.ONE : employee.getVersion().add(BigInteger.ONE));
            employee.setPricePrivilege(pricePrivilege);
            if(employee.getState() != state){
                if(state == 2 || state == 0){
                    if(employee.getCode().equals("0000")){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("编号为0000的员工不能禁用！");
                        return apiRest;
                    }
                    else{
                        ApiRest ret = AuthApi.enableSysUser(employee.getUserId(), false);
                        if(!ret.getIsSuccess()){
                            return ret;
                        }
                    }
                }
                if(state == 1){
                    ApiRest ret = AuthApi.enableSysUser(employee.getUserId(), true);
                    if(!ret.getIsSuccess()){
                        return ret;
                    }
                }
            }
            employee.setState(state);
            String message = "修改员工信息成功！";
            if(employee.getCode().equals("0000")){
                message = "修改员工信息成功，管理员无法更换角色！";
            }
            else{
                List<Map> roles = employeeMapper.queryRoles(employee.getUserId());
                int i = employeeMapper.deleteOldRole(employee.getUserId());
                if(i < roles.size()){
                    apiRest.setError("删除原角色失败！");
                    apiRest.setIsSuccess(false);
                    return apiRest;
                }
                for(String roleId : roleIds.split(",")){
                    BigInteger roleIId = BigInteger.valueOf(Long.valueOf(roleId));
                    Long role = employeeMapper.findRole(roleIId, tenantId);
                    if(role <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("查询角色不存在！");
                        return apiRest;
                    }
                    int j = employeeMapper.saveRole(employee.getUserId(), roleIId, employee.getTenantId());
                    if(j <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("保存角色与用户关系失败！");
                        return apiRest;
                    }
                }
            }
            ApiRest rest = saveUserName(employee.getUserId(), name);
            if(!rest.getIsSuccess()){
                return rest;
            }
            int k = employeeMapper.updateEmployee(employee);
            if(k <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改员工信息失败！");
                return apiRest;
            }
            Map map = employeeMapper.queryEmployeeById(params);
            List<Map> empRoles = employeeMapper.queryRoles(employee.getUserId());
            String roleName = "";
            for(Map j : empRoles){
                roleName += j.get("roleName") + " ";
            }
            map.put("roleName", roleName);
            map.put("roles", empRoles);
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage(message);
        }
        else{
            if(params.get("tenantCode") == null || params.get("tenantCode").equals("")){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增员工时，tenantCode不能为空！");
                return apiRest;
            }
            if(!tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM).getIsSuccess()){
                return tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM);
            }
            String code = employeeMapper.getEmployeeCode(tenantId);
            Map paramsMap = new HashMap();
            paramsMap.put("userType", Constants.USER_TYPE_TENANT_EMPLOYEE.toString());
            paramsMap.put("loginName", SerialNumberGenerate.nextSerialNumber(4, code));
            paramsMap.put("tenantCode", params.get("tenantCode"));
            paramsMap.put("loginPass", "888888");
            paramsMap.put("name", name);
            paramsMap.put("tenantId", tenantId);
            paramsMap.put("state", state);
            LogUtil.logInfo("#########注册员工账号。时间：" + new Date());
            ApiRest rest = ProxyApi.proxyGet("bs", "user", "register", paramsMap);//员工档案增加，此处导致添加很慢
            LogUtil.logInfo("#########注册员工账号，insert时间：" + new Date());
            if(rest.getIsSuccess()){
                Map acMap = new HashMap();
                acMap.put("userId", rest.getData().toString());
                ApiRest rest2 = ProxyApi.proxyGet("bs", "user", "activate", acMap);
                if(!rest2.getIsSuccess()){
                    AuthApi.deleteSysUser(BigInteger.valueOf(Long.valueOf(rest.getData().toString())));
                    return rest2;
                }
                Employee employee = new Employee();
                employee.setCode(SerialNumberGenerate.nextSerialNumber(4, code));
                employee.setPasswordForLocal(MD5Utils.stringMD5("888888").toLowerCase());
                employee.setLastUpdateAt(new Date());
                employee.setLastUpdateBy("System");
                employee.setName(name);
                employee.setUserId(BigInteger.valueOf(Long.valueOf(rest.getData().toString())));
                employee.setLoginName(SerialNumberGenerate.nextSerialNumber(4, code) + ":" + params.get("tenantCode"));
                employee.setDiscountRate(discountRate);
                employee.setDiscountAmount(discountAmount);
                if(params.get("saleRate")!=null && !params.get("saleRate").equals("")){
                    employee.setSaleRate(new BigDecimal(params.get("saleRate").toString()));
                }
                if(params.get("storeRate")!=null && !params.get("storeRate").equals("") ){
                    employee.setStoreRate(new BigDecimal(params.get("storeRate").toString()));
                }
                employee.setVersion(BigInteger.ZERO);
                employee.setState(state);
                employee.setCreateAt(new Date());
                employee.setCreateBy("System");
                employee.setIsDeleted(false);
                employee.setTenantId(tenantId);
                employee.setBranchId(branchId);
                Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
                if(branch != null && branch.getBranchType() == 0){
                    String userAreas = "";
                    List<Area> areas = areaMapper.findAllByTenantId(tenantId);
                    for(Area area : areas){
                        userAreas += (area.getId() + ",");
                    }
                    if(userAreas.length() > 0){
                        userAreas = userAreas.substring(0, userAreas.length() - 1);
                    }
                    else{
                        userAreas = null;
                    }
                    employee.setUserAreas(userAreas);
                }
                else{
                    employee.setUserAreas(null);
                }
                ApiRest apiRest1 = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_EMP_NUM);
                if(!apiRest1.getIsSuccess()){
                    AuthApi.deleteSysUser(employee.getUserId());
                    return apiRest1;
                }
                if(!roleIds.equals("")){
                    for(String roleId : roleIds.split(",")){
                        BigInteger roleIId = BigInteger.valueOf(Long.valueOf(roleId));
                        Long role = employeeMapper.findRole(roleIId, tenantId);
                        if(role <= 0){
                            AuthApi.deleteSysUser(employee.getUserId());
                            apiRest.setIsSuccess(false);
                            apiRest.setError("查询角色不存在！");
                            return apiRest;
                        }
                        int j = employeeMapper.saveRole(employee.getUserId(), roleIId, employee.getTenantId());
                        if(j <= 0){
                            AuthApi.deleteSysUser(employee.getUserId());
                            apiRest.setIsSuccess(false);
                            apiRest.setError("保存角色与用户关系失败！");
                            return apiRest;
                        }
                    }
                }
                employee.setPricePrivilege(pricePrivilege);
                int k = employeeMapper.insert(employee);
                if(k <= 0){
                    AuthApi.deleteSysUser(employee.getUserId());
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增员工信息失败！");
                    return apiRest;
                }
                params.put("id", employee.getId());
                Map map = employeeMapper.queryEmployeeById(params);
                List<Map> empRoles = employeeMapper.queryRoles(employee.getUserId());
                String roleName = "";
                for(Map j : empRoles){
                    roleName += j.get("roleName") + " ";
                }
                map.put("roleName", roleName);
                map.put("roles", empRoles);
                apiRest.setIsSuccess(true);
                apiRest.setData(map);
                apiRest.setMessage("添加员工帐号成功！已创建员工(帐号" + employee.getLoginName() + ",密码:888888)");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("添加员工帐号失败！" + rest.getMessage());
            }
        }
        return apiRest;
    }

    /**
     * 新增或修改员工档案
     *
     * @param params
     * @return
     */
    public ApiRest saveEmp(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String name = params.get("name").toString();
        Integer discountRate = Integer.valueOf(params.get("discountRate").toString());
        Integer discountAmount = Integer.valueOf(params.get("discountAmount").toString());
        Integer state = Integer.valueOf(params.get("state").toString());
        String roleIds = "";
        if(params.get("roles") != null && !params.get("roles").equals("")){
            roleIds = params.get("roles").toString();
        }
        if(params.get("roleIds") != null && !params.get("roleIds").equals("")){
            roleIds = params.get("roleIds").toString();
        }
        if(state != 1 && state != 2){
            apiRest.setIsSuccess(false);
            apiRest.setError("state只能为1或2！");
            return apiRest;
        }

        if(params.get("tenantCode") == null || params.get("tenantCode").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setError("新增员工时，tenantCode不能为空！");
            return apiRest;
        }
        if(!tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM).getIsSuccess()){
            return tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM);
        }
        String code = employeeMapper.getEmployeeCode(tenantId);
        Map paramsMap = new HashMap();
        paramsMap.put("userType", Constants.USER_TYPE_TENANT_EMPLOYEE.toString());
        paramsMap.put("loginName", SerialNumberGenerate.nextSerialNumber(4, code));
        paramsMap.put("tenantCode", params.get("tenantCode"));
        paramsMap.put("loginPass", "888888");
        paramsMap.put("name", name);
        paramsMap.put("tenantId", tenantId);
        paramsMap.put("state", state);
        LogUtil.logInfo("#########注册员工账号。时间：" + new Date());
        ApiRest rest = ProxyApi.proxyGet("bs", "user", "register", paramsMap);//员工档案增加，此处导致添加很慢
        LogUtil.logInfo("#########注册员工账号，调用接口结束。时间：" + new Date());
        if(rest.getIsSuccess()){
            Map acMap = new HashMap();
            acMap.put("userId", rest.getData().toString());
            ApiRest rest2 = ProxyApi.proxyGet("bs", "user", "activate", acMap);
            if(!rest2.getIsSuccess()){
                return rest2;
            }
            Employee employee = new Employee();
            employee.setCode(SerialNumberGenerate.nextSerialNumber(4, code));
            employee.setPasswordForLocal(MD5Utils.stringMD5("888888").toLowerCase());
            employee.setLastUpdateAt(new Date());
            employee.setLastUpdateBy("System");
            employee.setName(name);
            employee.setUserId(BigInteger.valueOf(Long.valueOf(rest.getData().toString())));
            employee.setLoginName(SerialNumberGenerate.nextSerialNumber(4, code) + ":" + params.get("tenantCode"));
            employee.setDiscountRate(discountRate);
            employee.setDiscountAmount(discountAmount);
            employee.setVersion(BigInteger.ZERO);
            employee.setState(state);
            employee.setCreateAt(new Date());
            employee.setCreateBy("System");
            employee.setIsDeleted(false);
            employee.setTenantId(tenantId);
            employee.setBranchId(branchId);
            Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
            if(branch != null && branch.getBranchType() == 0){
                String userAreas = "";
                List<Area> areas = areaMapper.findAllByTenantId(tenantId);
                for(Area area : areas){
                    userAreas += (area.getId() + ",");
                }
                if(userAreas.length() > 0){
                    userAreas = userAreas.substring(0, userAreas.length() - 1);
                }
                else{
                    userAreas = null;
                }
                employee.setUserAreas(userAreas);
            }
            else{
                employee.setUserAreas(null);
            }
            ApiRest apiRest1 = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_EMP_NUM);
            if(!apiRest1.getIsSuccess()){
                return apiRest1;
            }
            if(!roleIds.equals("")){
                for(String roleId : roleIds.split(",")){
                    BigInteger roleIId = BigInteger.valueOf(Long.valueOf(roleId));
                    Long role = employeeMapper.findRole(roleIId, tenantId);
                    if(role <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("查询角色不存在！");
                        return apiRest;
                    }
                    int j = employeeMapper.saveRole(employee.getUserId(), roleIId, employee.getTenantId());
                    if(j <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("保存角色与用户关系失败！");
                        return apiRest;
                    }
                }
            }
            int k = employeeMapper.insert(employee);
            if(k <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增员工信息失败！");
                return apiRest;
            }
            params.put("id", employee.getId());
            List<Map> listEmp = new ArrayList<>();
            Map map = employeeMapper.queryEmployeeById(params);
            List<Map> empRoles = employeeMapper.queryRoles(employee.getUserId());
            String roleName = "";
            for(Map j : empRoles){
                roleName += j.get("roleName") + " ";
            }
            map.put("roleName", roleName);
            map.put("roles", empRoles);
            listEmp.add(map);
            Map resultMap = new HashMap();
            resultMap.put("rows", listEmp);
            resultMap.put("total", listEmp.size());
            apiRest.setIsSuccess(true);
            apiRest.setData(resultMap);
            apiRest.setMessage("添加员工帐号成功！已创建员工(帐号" + employee.getLoginName() + ",密码:888888)");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("添加员工帐号失败！" + rest.getMessage());
        }
        return apiRest;
    }

    /**
     * 更改用户名
     */
    public ApiRest saveUserName(BigInteger userId, String name){
        ApiRest ApiRest = AuthApi.saveUserName(userId, name);
        return ApiRest;
    }

    /**
     * 删除员工
     *
     * @param params
     * @return
     */
    public ApiRest delEmployee(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Employee employee = employeeMapper.getEmployeeById(id, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        if(employee.getCode().equals("0000")){
            apiRest.setIsSuccess(false);
            apiRest.setError("默认管理员禁止删除！");
            return apiRest;
        }
        Map empMap = new HashMap();
        empMap.put("tenantId", tenantId);
        empMap.put("branchId", employee.getBranchId());
        Branch branch = branchMapper.findByTenantIdAndBranchId(empMap);
        if(branch != null && employeeMapper.queryEmployeeSum(empMap) == 1){
            apiRest.setIsSuccess(false);
            apiRest.setError("此机构下没有其他员工，删除失败！");
            return apiRest;
        }
        List<Map> roles = employeeMapper.queryRoles(employee.getUserId());
        int a = employeeMapper.deleteOldRole(employee.getUserId());
        if(a < roles.size()){
            apiRest.setError("删除原角色失败！");
            apiRest.setIsSuccess(false);
            return apiRest;
        }
        ApiRest ApiRest = AuthApi.deleteSysUser(employee.getUserId());
        if(!ApiRest.getIsSuccess()){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除用户失败！");
            return apiRest;
        }
        ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_EMP_NUM);
        if(!r.getIsSuccess()){
            return r;
        }
        employee.setIsDeleted(true);
        employee.setLastUpdateAt(new Date());
        employee.setLastUpdateBy("System");
        int i = employeeMapper.updateEmployee(employee);
        if(i <= 0) {
            apiRest.setIsSuccess(false);
            apiRest.setError("删除员工信息失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除员工成功！");
        return apiRest;
    }

    /**
     * 删除员工
     *
     * @param params
     * @return
     */
    public ApiRest delEmp(Map params){
        ApiRest apiRest = new ApiRest();
        String ids = params.get("ids").toString();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        int count = 0;
        List<Map> errorList = new ArrayList<>();
        Map resultMap = new HashMap();
        for(String empId : ids.split(",")){
            Map errorMap = new HashMap();
            BigInteger id = BigInteger.valueOf(Long.parseLong(empId));
            Employee employee = employeeMapper.getEmployeeById(id, tenantId);
            if(employee == null){
                errorMap.put("id", id);
                errorMap.put("mes", "未查到员工信息！");
                errorList.add(errorMap);
                apiRest.setIsSuccess(false);
                apiRest.setError("未查到员工信息！");
            }
            else{
                if(employee.getCode().equals("0000")){
                    errorMap.put("id", id);
                    errorMap.put("mes", "员工号为0000的员工不能删除！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError("员工号为0000的员工不能删除！");
                }
                else{
                    Map empMap = new HashMap();
                    empMap.put("tenantId", tenantId);
                    empMap.put("branchId", employee.getBranchId());
                    Branch branch = branchMapper.findByTenantIdAndBranchId(empMap);
                    if(branch != null && employeeMapper.queryEmployeeSum(empMap) == 1){
                        errorMap.put("id", id);
                        errorMap.put("mes", "此机构下没有其他员工，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("此机构下没有其他员工，删除失败！");
                    }
                    else{
                        List<Map> roles = employeeMapper.queryRoles(employee.getUserId());
                        int a = employeeMapper.deleteOldRole(employee.getUserId());
                        if(a < roles.size()){
                            errorMap.put("id", id);
                            errorMap.put("mes", "删除原角色失败！");
                            errorList.add(errorMap);
                            apiRest.setError("删除原角色失败！");
                            apiRest.setIsSuccess(false);
                        }
                        else{
                            ApiRest ApiRest = AuthApi.deleteSysUser(employee.getUserId());
                            if(!ApiRest.getIsSuccess()){
                                errorMap.put("id", id);
                                errorMap.put("mes", "删除用户失败！");
                                errorList.add(errorMap);
                                apiRest.setIsSuccess(false);
                                apiRest.setError("删除用户失败！");
                            }
                            else{
                                ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_EMP_NUM);
                                if(r.getIsSuccess()){
                                    employee.setIsDeleted(true);
                                    employee.setLastUpdateAt(new Date());
                                    employee.setLastUpdateBy("System");
                                    int i = employeeMapper.updateEmployee(employee);
                                    if(i <= 0){
                                        errorMap.put("id", id);
                                        errorMap.put("mes", "删除员工信息失败！");
                                        errorList.add(errorMap);
                                        apiRest.setIsSuccess(false);
                                        apiRest.setError("删除员工信息失败！");
                                    }
                                    else{
                                        errorMap.put("id", id);
                                        errorMap.put("mes", "删除成功！");
                                        errorList.add(errorMap);
                                        apiRest.setIsSuccess(true);
                                        count++;
                                    }

                                }

                            }

                        }

                    }

                }
            }
        }
        if(count > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除成功");
        }
        if(errorList.size() > 0){
            apiRest.setError("");
            for(int i = 0; i < errorList.size(); i++){
                apiRest.setError("id为" + errorList.get(i).get("id") + "的员工：" + errorList.get(i).get("mes") + "。");
            }
        }
        resultMap.put("num", count);
        resultMap.put("errors", errorList);
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 获取编码
     *
     * @param params
     * @return
     */

    public ApiRest getEmployeeCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String code = employeeMapper.getEmployeeCode(tenantId);
        code = SerialNumberGenerate.nextSerialNumber(4, code);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取员工编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 重置密码
     *
     * @param params
     * @return
     */
    public ApiRest resetPassword(Map params) throws IOException, ClassNotFoundException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Employee employee = employeeMapper.getEmployeeById(id, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        if(employee.getCode().equals("0000")){
            employee.setPasswordForLocal(DigestUtils.md5Hex("888888"));
            employee.setLastUpdateAt(new Date());
            employee.setLastUpdateBy(params.get("userName") == null ? "" : params.get("userName").toString());
            employeeMapper.updateEmployee(employee);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("POS密码重置成功，重置后的密码为888888,员工号为0000的不允许重置登录密码");
            return apiRest;
        }
        ApiRest ret = AuthApi.resetPassword(employee.getUserId(), "888888");
        if(ret.getIsSuccess()){
            employee.setPasswordForLocal(DigestUtils.md5Hex("888888"));
            employee.setLastUpdateAt(new Date());
            employee.setLastUpdateBy(params.get("userName") == null ? "" : params.get("userName").toString());
            employeeMapper.updateEmployee(employee);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("密码重置成功，重置后的密码为888888");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("重置密码失败！");
        }
        return apiRest;
    }

    /**
     * 获取员工信息
     *
     * @param params
     * @return
     */
    public ApiRest getEmployee(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        Employee employee = employeeMapper.getEmployee(userId, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        Map bMap=new HashMap();
        bMap.put("tenantId",employee.getTenantId());
        bMap.put("branchId",employee.getBranchId());
        Branch branch=branchMapper.findByTenantIdAndBranchId(bMap);
        if(branch==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("门店信息不存在！");
            return apiRest;
        }
        ApiRest rest = AuthApi.listSysUser(userId.toString());
        if(!rest.getIsSuccess()){
            return rest;
        }
        if(((List)rest.getData()).size() == 0 || rest.getData() == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到用户信息！");
            return apiRest;
        }
        Map resultMap = new HashMap();
        resultMap.put("branch",branch);
        resultMap.put("employee", employee);
        resultMap.put("user", ((Map)((List)rest.getData()).get(0)));
        employee.setName(((Map)((List)rest.getData()).get(0)).get("name").toString());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询员工信息成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 修改pos密码
     *
     * @param params
     * @return
     */
    public ApiRest doChangePOSPwd(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        Employee employee = employeeMapper.getEmployee(userId, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        String tempPwd = employee.getPasswordForLocal();
        String oldPass = params.get("oldPass").toString();
        String newPass = params.get("newPass").toString();
        if(tempPwd.equals(MD5Utils.stringMD5(oldPass)) || tempPwd.equals(MD5Utils.stringMD5(oldPass).toLowerCase())){
            employee.setPasswordForLocal(MD5Utils.stringMD5(newPass).toLowerCase());
            employee.setLastUpdateAt(new Date());
            employee.setLastUpdateBy("System");
            int k = employeeMapper.updateEmployee(employee);
            if(k <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改pos密码失败！");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改pos密码成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("旧密码输入错误！");
        }
        return apiRest;
    }

    /**
     * 重置密码
     *
     * @param params
     * @return
     */
    public ApiRest resetPasswordForLocal(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger empId = BigInteger.valueOf(Long.parseLong((String)params.get("empId")));
        Employee employee = employeeMapper.getEmployeeById(empId, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        String newPass = "888888";
        employee.setPasswordForLocal(MD5Utils.stringMD5(newPass).toLowerCase());
        employee.setLastUpdateAt(new Date());
        employee.setLastUpdateBy("System");
        int k = employeeMapper.updateEmployee(employee);
        if(k <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("重置密码失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("重置密码成功！");
        return apiRest;
    }

    /**
     * 解绑手机
     *
     * @param params
     * @return
     */
    public ApiRest removeMobileBind(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        ApiRest rest = AuthApi.removeMobileBind(userId);
        if(rest.getIsSuccess()){
            Employee e = employeeMapper.getEmployee(userId, tenantId);
            if(e != null){
                e.setPhone("");
                e.setLastUpdateAt(new Date());
                int k = employeeMapper.updateEmployee(e);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("解绑手机号码失败！");
                    return apiRest;
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("解绑手机号码成功！");
            }
        }
        else{
            return rest;
        }
        return apiRest;
    }

    /**
     * 绑定手机
     *
     * @param params
     * @return
     */
    public ApiRest bindMobile(Map params) throws IOException, ClassNotFoundException{
        ApiRest apiRest = new ApiRest();
        String userId = params.get("userId").toString();
        String tenantId = params.get("tenantId").toString();
        String number = params.get("number").toString();
        String authCode = params.get("authCode").toString();
        String sessionId = params.get("sessionId").toString();
        boolean rest = AuthApi.bindMobileUnique(number);
        if(rest){
                /*Map map=new HashMap();
                map.put("userId",userId);
                map.put("tenantId",tenantId);
                map.put("number",number);*/
            ApiRest res = AuthApi.bindMobile(userId, number, authCode, sessionId);
            if(res.getIsSuccess()){
                Employee e = employeeMapper.getEmployee(BigInteger.valueOf(Long.parseLong(userId)), BigInteger.valueOf(Long.parseLong(tenantId)));
                if(e == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询员工失败！");
                    return apiRest;
                }
                e.setPhone(number);
                int i = employeeMapper.updateEmployee(e);
                if(i < 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询员工失败！");
                    return apiRest;
                }
                else{
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("绑定手机成功！");
                }
            }
            else{
                return res;
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("手机已经被绑定！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 兼容零售pos同步数据接口
     *
     * @param params
     * @return
     */
    public ApiRest queryEmpPager(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = employeeMapper.queryEmpPager(params);
        for(Map i : list){
            List<Map> roles = employeeMapper.queryRoles(BigInteger.valueOf(Long.valueOf(i.get("userId").toString())));
            if(roles.size() > 0){
                i.put("sysRoles", roles);
            }
            else{
                i.put("sysRoles", new ArrayList<>());
            }
        }
        Long count = employeeMapper.queryEmpPagerSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询员工列表成功！");
        return apiRest;
    }

    /**
     * 获取员工信息
     *
     * @param params
     * @return
     */
    public ApiRest findEmployeeByUser(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        Employee employee = employeeMapper.findEmployeeByUser(userId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到员工信息！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询员工信息成功！");
        apiRest.setData(employee);
        return apiRest;
    }

    public ApiRest initEmployee(Map<String, String> params){
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
//        ApiRest checkConfigRest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_EMP_NUM);
//        Validate.isTrue(checkConfigRest.getIsSuccess(), checkConfigRest.getError());
        Employee employee = new Employee(params);

        String code = SerialNumberGenerate.nextSerialNumber(4, employeeMapper.getEmployeeCode(tenantId));
        employee.setCode(code);
        employee.setState(1);
        employee.setLoginName(code + ":" + params.get("tenantCode"));
        employee.setCreateAt(new Date());
        employee.setLastUpdateAt(new Date());
        employeeMapper.insert(employee);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(code);
        apiRest.setMessage("员工初始化完成！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest queryUserRolesByUserId(BigInteger userId){
        List<Map> roles = employeeMapper.queryRoles(userId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(roles);
        apiRest.setMessage("查询用户权限成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest removeMobileBind(BigInteger userId){
        ApiRest removeMobileBindRest = AuthApi.removeMobileBind(userId);
        Validate.isTrue(removeMobileBindRest.getIsSuccess(), removeMobileBindRest.getMessage());
        employeeMapper.removeMobileBind(userId);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("手机解绑成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest doAccountSetting(Map params){
        ApiRest result = new ApiRest();
        String name = params.get("name").toString();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger userId = BigInteger.valueOf(Long.valueOf(params.get("userId").toString()));
        Employee employee = employeeMapper.getEmployee(userId, tenantId);
        ApiRest rest = saveUserName(employee.getUserId(), name);
        if(rest.getIsSuccess()){
            employee.setName(name);
            employeeMapper.updateEmployee(employee);
            result.setIsSuccess(true);
            result.setMessage("修改用户信息成功!");
        }
        else{
            result.setIsSuccess(false);
            result.setError("修改用户信息失败!");
        }

        return result;
    }

    /**
     * 设置区域权限
     */
    public ApiRest areaAuthority(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        Employee employee = employeeMapper.getEmployeeById(id, tenantId);
        if(employee == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到该员工");
            return apiRest;
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        params.put("id", id);
        int count = employeeMapper.areaAuthority(params);
        if(count <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("用户区域权限设置失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("用户区域权限设置成功");
        return apiRest;
    }

    /**
     * 导购员销售提成明细
     */
    public ApiRest guidePercentageSale(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        if(params.get("rows") != null && params.get("page") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<Map> list = employeeMapper.guidePercentageSale(params);
        List<Map> sumList = employeeMapper.guidePercentageSaleSum(params);
        map.put("rows", list);
        map.put("total", sumList.get(0).get("total"));
        map.put("footer", sumList);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询导购员销售提成明细成功");
        return apiRest;
    }

    /**
     * 导购员充值提成明细
     */
    public ApiRest guidePercentageStore(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        if(params.get("rows") != null && params.get("page") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }
        List<Map> list = employeeMapper.guidePercentageStore(params);
        List<Map> sumList = employeeMapper.guidePercentageStoreSum(params);
        map.put("rows", list);
        map.put("total", sumList.get(0).get("total"));
        map.put("footer", sumList);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询导购员充值提成明细成功");
        return apiRest;
    }

    /**
     * 导购员提成汇总
     */
    public ApiRest guideSummary(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        List<Map> list = employeeMapper.guideSummary(params);
        List<Map> sumList = employeeMapper.guideSummarySum(params);
        map.put("rows", list);
        map.put("total", sumList.get(0).get("total"));
        map.put("footer", sumList);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询导购员提成汇总成功");
        return apiRest;
    }

    /**
     * 查询导购员
     */
    public ApiRest queryGuiDe(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        if(null != params.get("guideName") && !params.get("guideName").equals("")){
            params.put("guideName", params.get("guideName").toString().trim());
        }
        Integer rows = employeeMapper.queryGuiDeTotal(params); //先查询该门店下导购员的个数
        Integer offset = 0;

        if(params.get("isExactQuery") == null || "".equals(params.get("isExactQuery"))){
            params.put("isExactQuery",0);
        }
        if(params.get("rows") != null && params.get("page") != null){
            rows = Integer.parseInt(params.get("rows").toString());
            offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        }
        Integer tenantId = Integer.parseInt(params.get("tenantId").toString());
       /* Integer branchId = Integer.parseInt(params.get("branchId").toString());*/
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        /*params.put("branchId", branchId);*/
        List<Map> list = employeeMapper.queryGuiDe(params);
        int total = employeeMapper.queryGuiDeTotal(params);
        map.put("rows", list);
        map.put("total", total);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询导购员成功！");
        return apiRest;
    }

    public ApiRest addGuide(Map params){
        ApiRest apiRest = new ApiRest();
        SysRole sysRoles = employeeMapper.queryGuideRole(params);//查询该门店下是否有导购员角色,如果没有为该门店添加导购员角色
        if(sysRoles == null){
            SysRole sysRole = new SysRole();
            ApiRest rest = roleService.getRoleCode(params);
            if(!rest.getIsSuccess()){
                return rest;
            }
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
            BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
            sysRole.setTenantId(tenantId);
            sysRole.setBranchId(branchId);
            sysRole.setRoleName("导购员");
            sysRole.setRoleCode(rest.getData().toString());
            sysRole.setRoleType(5);
            sysRole.setLastUpdateAt(new Date());
            sysRole.setLastUpdateBy("System");
            sysRole.setCreateAt(new Date());
            sysRole.setCreateBy("System");
            sysRole.setPackageName("erp.chain");
            int j = roleMapper.insert(sysRole);
            if(j <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("添加角色失败！");
                return apiRest;
            }

        }
        if(sysRoles==null){
        sysRoles = employeeMapper.queryGuideRole(params);
        }
        params.put("discountRate",0);
        params.put("discountAmount",0);
        params.put("roles",sysRoles.getId());
        apiRest= addOrUpdateEmployee(params);
        return apiRest;
    }

    /**
     * 导购员销售提成明细
     */
    public ApiRest guideSaleJob() throws IOException{
        ApiRest rest = new ApiRest();
        LogUtil.logInfo("***********处理导购员销售明细任务开始************");
        String key=PropertyUtils.getDefault("topic.guide.sale");
        String errorKey=PropertyUtils.getDefault("topic.guide.sale")+"_error";
        /*while(true){
            String data = PartitionCacheUtils.rpop(errorKey);
            try{
                if(data != null && !"".equals(data)){
                    SaleDetail saleDetail = GsonUntil.jsonAsModel(data, SaleDetail.class);
                    if(saleDetail != null && saleDetail.getGuideId() != null && !saleDetail.getGuideId().equals(BigInteger.ZERO)){
                        Employee employee = employeeMapper.getEmployeeById(saleDetail.getGuideId(), saleDetail.getTenantId());
                        if(employee != null && employee.getSaleRate() != null && !employee.getSaleRate().equals(BigDecimal.ZERO)){
                            BigDecimal commissionAmount = saleDetail.getReceivedAmount().multiply(employee.getSaleRate()).multiply(BigDecimal.valueOf(0.01));
                            int i = saleDetailMapper.insertGuideSale(saleDetail, employee.getSaleRate(), commissionAmount);
                        }
                    }
                }
                else{
                    rest.setIsSuccess(true);
                    rest.setMessage("导购员销售明细任务:积队列为空");
                    LogUtil.logInfo("***********导购员销售明细任务任务:导购员销售明细任务队列为空************");
                    //队列为空则结束
                    break;
                }
            }
            catch(Exception e){
                LogUtil.logError("导购员销售明细任务处理异常:" + e.getMessage());
                //处理异常数据
                PartitionCacheUtils.rpush(key, data);
            }
        }*/
        while(true){
            String data = PartitionCacheUtils.rpop(key);
            try{
                if(data != null && !"".equals(data)){
                    SaleDetail saleDetail = GsonUntil.jsonAsModel(data, SaleDetail.class);
                    if(saleDetail != null && saleDetail.getGuideId() != null && !saleDetail.getGuideId().equals(BigInteger.ZERO)){
                        Employee employee = employeeMapper.getEmployeeById(saleDetail.getGuideId(), saleDetail.getTenantId());
                        if(employee != null && employee.getSaleRate() != null && !employee.getSaleRate().equals(BigDecimal.ZERO)){
                            BigDecimal commissionAmount = saleDetail.getReceivedAmount().multiply(employee.getSaleRate()).multiply(BigDecimal.valueOf(0.01));
                            int i = saleDetailMapper.insertGuideSale(saleDetail, employee.getSaleRate(), commissionAmount);
                        }
                    }
                }
                else{
                    rest.setIsSuccess(true);
                    rest.setMessage("导购员销售明细任务:积队列为空");
                    LogUtil.logInfo("***********导购员销售明细任务任务:导购员销售明细任务队列为空************");
                    //队列为空则结束
                    break;
                }
            }
            catch(Exception e){
                LogUtil.logError("导购员销售明细任务处理异常:" + e.getMessage());
                //处理异常数据
                PartitionCacheUtils.rpush(errorKey, data);
            }
        }
        return rest;
    }

    public ApiRest updateGuide(Map params){
        ApiRest apiRest = new ApiRest();
        Employee employee = new Employee();

        if(params.get("name") != null){
            employee.setName(params.get("name").toString());
        }
        if(params.get("state") != null){
            employee.setState(Integer.valueOf(params.get("state").toString()));
        }
        if(params.get("saleRate") != null){
            employee.setSaleRate(new BigDecimal(params.get("saleRate").toString()));
        }
        if(params.get("storeRate") != null){
            employee.setStoreRate(new BigDecimal(params.get("storeRate").toString()));
        }
        employee.setId(new BigInteger(params.get("id").toString()));
        int i = employeeMapper.updateEmployee(employee);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("修改导购员失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改导购员成功");
        return apiRest;

    }
    public ApiRest changeEmpInfo(Map<String,String> params){
        ApiRest apiRest = new ApiRest();
        Employee employee = new Employee();

        if(params.get("newName") != null){
            employee.setName(params.get("newName"));
        }
        employee.setId(new BigInteger(params.get("id")));
        int i = employeeMapper.updateEmployee(employee);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("修改信息失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改信息成功");
        return apiRest;
    }
}
