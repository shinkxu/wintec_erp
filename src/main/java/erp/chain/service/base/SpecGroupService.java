package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.SpecGroup;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.SpecGroupMapper;
import erp.chain.utils.SerialNumberGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 口味分组
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpecGroupService{
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private BranchMapper branchMapper;

    /**
     * 口味组列表
     *
     * @param params
     * @return
     */
    public ApiRest specGroupList(Map params){
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
        Branch branch = branchMapper.findByTenantIdAndBranchId(params);
        if(branch == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到机构信息！");
            return apiRest;
        }
        params.put("branchType", branch.getBranchType());
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<SpecGroup> list = specGroupMapper.specGroupList(params);
        Long count = specGroupMapper.specGroupListCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询口味分组成功！");
        return apiRest;
    }

    /**
     * 口味组列表只查本机构数据
     *
     * @param params
     * @return
     */
    public ApiRest branchSpecGroupList(Map params){
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
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<SpecGroup> list = specGroupMapper.branchSpecGroupList(params);
        Long count = specGroupMapper.branchSpecGroupListCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询口味分组成功！");
        return apiRest;
    }

    /**
     * 修改或新增口味组
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateSpecGroup(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String groupName = params.get("groupName").toString();
        long nameCount = specGroupMapper.checkGroupName(params);
        if(nameCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("分组名称重复！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            SpecGroup specGroup = specGroupMapper.findByIdAndTenantId(id, tenantId);
            specGroup.setName(groupName);
            specGroup.setLastUpdateBy("System");
            specGroup.setLastUpdateAt(new Date());
            specGroup.setVersion(specGroup.getVersion().add(BigInteger.valueOf(1)));
            int i = specGroupMapper.update(specGroup);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改分组信息成功！");
                apiRest.setData(specGroup);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改分组信息失败！");
            }
        }
        else{
            String sqlCode = specGroupMapper.getGroupCode(params);
            SpecGroup specGroup = new SpecGroup();
            if(sqlCode == null || sqlCode.equals("")){
                specGroup.setCode("01");
            }
            else if(String.valueOf(Integer.valueOf(sqlCode)+1).length()>sqlCode.length()){
                specGroup.setCode(String.valueOf(Integer.valueOf(sqlCode)+1));
            }else {
                specGroup.setCode(SerialNumberGenerate.nextSerialNumber(sqlCode.length()<2?2:sqlCode.length(), sqlCode));
            }
            specGroup.setCreateAt(new Date());
            specGroup.setCreateBy("System");
            specGroup.setLastUpdateAt(new Date());
            specGroup.setLastUpdateBy("System");
            specGroup.setBranchId(branchId);
            specGroup.setTenantId(tenantId);
            specGroup.setName(groupName);
            specGroup.setVersion(BigInteger.valueOf(0));
            int i = specGroupMapper.insert(specGroup);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增分组信息成功！");
                apiRest.setData(specGroup);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("新增分组信息失败！");
            }
        }
        return apiRest;
    }

    /**
     * 删除口味组
     *
     * @param params
     * @return
     */
    public ApiRest delSpecGroup(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        SpecGroup specGroup = specGroupMapper.findByIdAndTenantId(id, tenantId);
        if(specGroup == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询口味分组失败！");
            return apiRest;
        }
        params.put("groupId", specGroup.getId());
        Long count = specGroupMapper.countByGroupId(params);
        if(count > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("分组下有口味，不能删除！");
            return apiRest;
        }
        specGroup.setLastUpdateBy("System");
        specGroup.setLastUpdateAt(new Date());
        specGroup.setDeleted(true);
        int i = specGroupMapper.update(specGroup);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除分组信息成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("删除分组信息失败！");
        }
        return apiRest;
    }

    /**
     * 口味组编码
     *
     * @param params
     * @return
     */
    public ApiRest getGroupCode(Map params){
        ApiRest apiRest = new ApiRest();
        String sqlCode = specGroupMapper.getGroupCode(params);
        String code;
        if(sqlCode == null || sqlCode.equals("")){
            code="01";
        }
        else if(String.valueOf(Integer.valueOf(sqlCode)+1).length()>sqlCode.length()){
            code=String.valueOf(Integer.valueOf(sqlCode)+1);
        }else {
            code=SerialNumberGenerate.nextSerialNumber(sqlCode.length()<2?2:sqlCode.length(), sqlCode);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取分组编码成功！");
        apiRest.setData(code);
        return apiRest;
    }
}
