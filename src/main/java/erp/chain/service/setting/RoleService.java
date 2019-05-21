package erp.chain.service.setting;

import erp.chain.domain.SysRole;
import erp.chain.mapper.CommonMapper;
import erp.chain.mapper.RoleMapper;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * 角色
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService{
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private CommonMapper commonMapper;

    /**
     * 查询角色列表
     *
     * @param params
     * @return
     */
    public ApiRest queryRoleList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order"));
        }
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = roleMapper.queryRoleList(params);
        Long count = roleMapper.queryRoleListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询角色列表成功！");
        return apiRest;
    }

    /**
     * 获取角色编码
     *
     * @param params
     * @return
     */
    public ApiRest getRoleCode(Map params){
        ApiRest apiRest = new ApiRest();
        String code = roleMapper.getRoleCode(params);
        code = SerialNumberGenerate.nextSerialNumber(2, code);
        apiRest.setIsSuccess(true);
        apiRest.setData(code);
        apiRest.setMessage("查询角色编码成功！");
        return apiRest;
    }

    /**
     * 新增或修改角色
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateRole(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String name = params.get("name").toString();
        if(name.length() > 20){
            apiRest.setIsSuccess(false);
            apiRest.setError("角色名字不能超过20个字符！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            SysRole sysRole = roleMapper.findRoleById(id, tenantId);
            if(sysRole == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询角色失败！");
                return apiRest;
            }
            sysRole.setLastUpdateAt(new Date());
            sysRole.setLastUpdateBy("System");
            sysRole.setRoleName(name);
            int i = roleMapper.update(sysRole);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改角色失败！");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setData(sysRole);
            apiRest.setMessage("修改角色成功！");
        }
        else{
            ApiRest rest = getRoleCode(params);
            if(!rest.getIsSuccess()){
                return rest;
            }
            SysRole sysRole = new SysRole();
            sysRole.setTenantId(tenantId);
            sysRole.setBranchId(branchId);
            sysRole.setRoleName(name);
            sysRole.setRoleCode(rest.getData().toString());
            sysRole.setRoleType(4);
            sysRole.setLastUpdateAt(new Date());
            sysRole.setLastUpdateBy("System");
            sysRole.setCreateAt(new Date());
            sysRole.setCreateBy("System");
            if(params.get("packageName") != null && !params.get("packageName").toString().equals("")){
                sysRole.setPackageName(params.get("packageName").toString());
            }else{
                sysRole.setPackageName("erp.chain");
            }
            int j = roleMapper.insert(sysRole);
            if(j <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("添加角色失败！");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("添加角色成功！");
            apiRest.setData(sysRole);
        }
        return apiRest;
    }

    /**
     * 删除角色
     *
     * @param params
     * @return
     */
    public ApiRest delRole(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        SysRole sysRole = roleMapper.findRoleById(id, tenantId);
        if(sysRole == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询角色失败！");
            return apiRest;
        }
        if(sysRole.getRoleCode().equals("01")){
            apiRest.setIsSuccess(false);
            apiRest.setError("默认管理员禁止删除！");
            return apiRest;
        }
        if(sysRole.getRoleCode().equals("04")){
            apiRest.setIsSuccess(false);
            apiRest.setError("默认机构管理员禁止删除！");
            return apiRest;
        }
        if(sysRole.getRoleType() == 5){
            apiRest.setIsSuccess(false);
            apiRest.setError("默认机构导购员禁止删除！");
            return apiRest;
        }
        if(roleMapper.countUsedRole(id) > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("角色已经被使用，删除失败！");
            return apiRest;
        }
        sysRole.setLastUpdateAt(new Date());
        sysRole.setLastUpdateBy("System");
        sysRole.setDeleted(true);
        int i = roleMapper.update(sysRole);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除角色失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(sysRole);
        apiRest.setMessage("删除角色成功！");
        return apiRest;
    }

    /**
     * 通过获得用户id获得pos机权限
     *
     * @param uIds 用户id集合，格式：uId1,uId2,uId3
     */
    public Map<String, List<String>> getUserPosRes(String uIds, BigInteger tId, BigInteger bId){
        Map<String, List<String>> uRes = new HashMap<>();
        List<Map<String, Long>> uPiv = roleMapper.getUserPosRiv(uIds);

        StrBuilder pivBuilder = new StrBuilder();
        for(Map<String, Long> piv : uPiv){
            pivBuilder.append(",").append(piv.get("privilegeId"));
        }

        Map<String, String> bsParam = new HashMap<>();
        bsParam.put("privileges", pivBuilder.deleteFirst(",").toString());
        bsParam.put("packageName", "saas.pos");
        bsParam.put("tenantId", tId.toString());
        bsParam.put("branchId", bId.toString());
        bsParam.put("isTree", "isTree");
        ApiRest rest = ProxyApi.proxyPost("bs", "auth", "loadPrivilegeTree", bsParam);
        if(!rest.getIsSuccess()){
            throw new IllegalArgumentException(String.format(
                    "请求远端接口错误：[bs:auth/loadPrivilegeTree,privileges:%s,packageName:saas.pos],[%s]",
                    pivBuilder.deleteFirst(",").toString(), rest.getError()));
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rivResList = (List<Map<String, Object>>)rest.getData();

        for(Map<String, Long> piv : uPiv){
            Long uId = piv.get("userId");
            Long privilegeId = piv.get("privilegeId");

            for(Map<String, Object> rr : rivResList){
                if(rr.get("privilegeId").toString().equals(privilegeId.toString())){
                    List<String> plList = uRes.get(uId.toString());
                    if(plList == null){
                        plList = new ArrayList<>();
                        uRes.put(uId.toString(), plList);
                    }
                    plList.add(rr.get("controllerName").toString());
                }
            }
        }
        return uRes;
    }
}
