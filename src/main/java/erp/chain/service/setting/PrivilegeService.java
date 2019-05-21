package erp.chain.service.setting;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.SysRole;
import erp.chain.mapper.RoleMapper;
import erp.chain.mapper.system.PrivilegeMapper;
import erp.chain.utils.ZTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 权限
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PrivilegeService{
    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 加载系统权限
     *
     * @param params
     * @return
     */
    public ApiRest getPrivilege(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        if(params.get("branchId") != null && !params.get("branchId").equals("")){
            BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
            map.put("branchId", branchId);
        }
        else{
            map.put("branchId", null);
        }
        //map.put("packageName","erp.chain");
        ApiRest rest = ProxyApi.proxyPost("bs", "auth", "loadPrivilegeAll", map);
        if(!rest.getIsSuccess()){
            return rest;
        }
        //List<Map> result=privilegeMapper.getSystemPrivilege(branchClass,branchType);
        List<ZTree> zTreeList = parseZTree((List<Map>)rest.getData());
        apiRest.setData(zTreeList);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("加载权限成功！");
        return apiRest;
    }

    /**
     * 转换为List<ZTree>
     *
     * @param array
     * @return
     */
    private List<ZTree> parseZTree(List<Map> array){
        List<ZTree> zTrees = new ArrayList<>();
        ZTree zTree1 = new ZTree();
        zTree1.setId("-99999");
        zTree1.setName("全部权限");
        zTree1.setpId("-99999");
        zTrees.add(zTree1);
        for(Map map : array){
            ZTree resPTree = new ZTree();
            if(map.size() == 5){
                for(Map resMap : (List<Map>)map.get("resList")){
                    ZTree resTree = new ZTree();
                    resTree.setId(resMap.get("resId").toString() + "_res");
                    resTree.setpId(resMap.get("parentId").toString() + "_res");
                    resTree.setName(resMap.get("resName").toString());
                    resTree.setTitle(resMap.get("resName").toString());
                    resTree.setChildNumber(((List)resMap.get("opList")).size());
                    zTrees.add(resTree);
                    for(Map opMap : (List<Map>)resMap.get("opList")){
                        ZTree opTree = new ZTree();
                        opTree.setId(opMap.get("privilegeId").toString());
                        opTree.setpId(resTree.getId());
                        /*if(!opMap.get("opName").equals("pos操作")){
                            opTree.setIsHidden(true);
                        }*/
                        opTree.setName(opMap.get("opName").toString());
                        opTree.setTitle(opMap.get("opName").toString());
                        opTree.setChildNumber(1);
                        zTrees.add(opTree);
                    }
                }
            }
            else{
                resPTree.setpId("-99999");
                resPTree.setName(map.get("resName").toString());
                resPTree.setId(map.get("resId").toString() + "_res");
                resPTree.setTitle(map.get("resName").toString());
                resPTree.setChildNumber(((List)map.get("resList")).size());
                zTrees.add(resPTree);
                for(Map resMap : (List<Map>)map.get("resList")){
                    ZTree resTree = new ZTree();
                    resTree.setId(resMap.get("resId").toString() + "_res");
                    resTree.setpId(resPTree.getId());
                    resTree.setName(resMap.get("resName").toString());
                    resTree.setTitle(resMap.get("resName").toString());
                    resTree.setChildNumber(((List)resMap.get("opList")).size());
                    zTrees.add(resTree);
                    for(Map opMap : (List<Map>)resMap.get("opList")){
                        //if(!opMap.get("opName").equals("pos操作")){
                        ZTree opTree = new ZTree();
                        opTree.setId(opMap.get("privilegeId").toString());
                        opTree.setpId(resTree.getId());
                        opTree.setName(opMap.get("opName").toString());
                        opTree.setTitle(opMap.get("opName").toString());
                        opTree.setChildNumber(1);
                        zTrees.add(opTree);
                        //}
                    }
                }
            }

        }
        return zTrees;
    }

    /**
     * 获取角色权限
     *
     * @param params
     * @return
     */
    public ApiRest getRolePrivilege(Map params){
        ApiRest apiRest = new ApiRest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-hh");
        BigInteger roleId = BigInteger.valueOf(Long.parseLong((String)params.get("roleId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        SysRole sysRole = roleMapper.findRoleById(roleId, tenantId);
        if(sysRole == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到角色信息！");
            return apiRest;
        }
        List<Map> result = privilegeMapper.rolePrivilege(roleId, sdf.format(new Date()));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询权限成功！");
        apiRest.setData(result);
        return apiRest;
    }

    /**
     * 保存角色权限
     *
     * @param params
     * @return
     */
    public ApiRest saveRolePrivilege(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger roleId = BigInteger.valueOf(Long.parseLong((String)params.get("roleId")));
        privilegeMapper.deleteOldPrivilege(roleId);
        String[] privileges = params.get("privileges").toString().split(",");
        SysRole sysRole = roleMapper.findRoleById(roleId, null);
        if(sysRole == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询角色失败！");
            return apiRest;
        }
        int i = privilegeMapper.saveRolePrivileges(roleId, privileges, sysRole.getTenantId());
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("添加角色权限失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存角色权限成功！");
        return apiRest;
    }
}
