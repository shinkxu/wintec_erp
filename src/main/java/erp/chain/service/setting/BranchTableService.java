package erp.chain.service.setting;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.BranchArea;
import erp.chain.domain.BranchTable;
import erp.chain.mapper.BranchAreaMapper;
import erp.chain.mapper.BranchTableMapper;
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
 * 桌台
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BranchTableService{
    @Autowired
    private BranchAreaMapper branchAreaMapper;
    @Autowired
    private BranchTableMapper branchTableMapper;

    /**
     * 查询桌台区域列表
     *
     * @param params
     * @return
     */
    public ApiRest queryAreaList(Map params){
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
        List<Map> list = branchAreaMapper.queryAreaList(params);
        Long count = branchAreaMapper.queryAreaListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询桌台区域列表成功！");
        return apiRest;
    }

    /**
     * 新增或修改桌台区域
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateArea(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String name = params.get("name").toString();
        Long nameCount = branchAreaMapper.checkName(params);
        if(nameCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("区域名称已存在，添加失败！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BranchArea branchArea = branchAreaMapper.getAreaById(params);
            if(branchArea == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("桌台区域查询失败！");
                return apiRest;
            }
            branchArea.setName(name);
            branchArea.setLastUpdateAt(new Date());
            branchArea.setLastUpdateBy("System");
            branchArea.setVersion(branchArea.getVersion().add(BigInteger.ONE));
            int i = branchAreaMapper.update(branchArea);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改桌台区域成功！");
                apiRest.setData(branchArea);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改桌台区域失败！");
            }
        }
        else{
            BranchArea branchArea = new BranchArea();
            String code = SerialNumberGenerate.nextSerialNumber(2, branchAreaMapper.getMaxAreaCode(params));
            branchArea.setCode(code);
            branchArea.setName(name);
            branchArea.setTenantId(tenantId);
            branchArea.setBranchId(branchId);
            branchArea.setCreateBy("System");
            branchArea.setCreateAt(new Date());
            branchArea.setLastUpdateAt(new Date());
            branchArea.setLastUpdateBy("System");
            branchArea.setVersion(BigInteger.ZERO);
            int i = branchAreaMapper.insert(branchArea);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增桌台区域成功！");
                apiRest.setData(branchArea);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("新增桌台区域失败！");
            }
        }
        return apiRest;
    }

    /**
     * 获取区域编码
     *
     * @param params
     * @return
     */
    public ApiRest getAreaCode(Map params){
        ApiRest apiRest = new ApiRest();
        String code = SerialNumberGenerate.nextSerialNumber(2, branchAreaMapper.getMaxAreaCode(params));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取机构区域编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 删除桌台区域
     *
     * @param params
     * @return
     */
    public ApiRest delArea(Map params){
        ApiRest apiRest = new ApiRest();
        BranchArea branchArea = branchAreaMapper.getAreaById(params);
        if(branchArea == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("桌台区域查询失败！");
            return apiRest;
        }
        Long count = branchAreaMapper.useAreaCount(branchArea.getId(), branchArea.getTenantId());
        if(count > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("已经被桌台使用的区域不能删除！");
            return apiRest;
        }
        branchArea.setLastUpdateAt(new Date());
        branchArea.setLastUpdateBy("System");
        branchArea.setVersion(branchArea.getVersion().add(BigInteger.ONE));
        branchArea.setDeleted(true);
        int i = branchAreaMapper.update(branchArea);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除桌台区域成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("删除桌台区域失败！");
        }
        return apiRest;
    }

    /**
     * 查询桌台列表
     *
     * @param params
     * @return
     */
    public ApiRest queryTableList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        Map map = new HashMap();
        if(params.get("page") != null && !params.get("page").equals("") && params.get("rows") != null && !params.get("rows").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        List<Map> list = branchTableMapper.queryTableList(params);
        Long count = branchTableMapper.queryTableListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询桌台列表成功！");
        return apiRest;
    }

    /**
     * 新增或修改桌台
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateTable(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String code = params.get("code").toString();
        if(code.length() != 3){
            apiRest.setIsSuccess(false);
            apiRest.setError("桌台编码只能为3位数字！");
            return apiRest;
        }
        String name = params.get("name").toString();
        Integer num = Integer.valueOf(params.get("num").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        Long nameCount = branchTableMapper.checkName(params);
        if(nameCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("桌台名称已存在！");
            return apiRest;
        }
        Long codeCount = branchTableMapper.checkCode(params);
        if(codeCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("桌台编码已存在！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BranchTable branchTable = branchTableMapper.getTableById(params);
            if(branchTable == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("桌台查询失败！");
                return apiRest;
            }
            branchTable.setCode(code);
            branchTable.setName(name);
            branchTable.setNum(num);
            branchTable.setStatus(status);
            branchTable.setLastUpdateAt(new Date());
            branchTable.setLastUpdateBy("System");
            branchTable.setVersion(branchTable.getVersion().add(BigInteger.ONE));
            int i = branchTableMapper.update(branchTable);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改桌台成功！");
                apiRest.setData(branchTable);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改桌台失败！");
            }
        }
        else{
            BranchTable branchTable = new BranchTable();
            BigInteger areaId = BigInteger.valueOf(Long.parseLong((String)params.get("areaId")));
            Map map = new HashMap();
            map.put("tenantId", tenantId);
            map.put("id", areaId);
            map.put("branchId", branchId);
            BranchArea branchArea = branchAreaMapper.getAreaById(map);
            if(branchArea == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到桌台区域信息！");
                return apiRest;
            }
            branchTable.setBranchAreaId(branchArea.getId());
            branchTable.setCode(code);
            branchTable.setName(name);
            branchTable.setTenantId(tenantId);
            branchTable.setBranchId(branchId);
            branchTable.setNum(num);
            branchTable.setType(1);
            branchTable.setStatus(status);
            branchTable.setCreateBy("System");
            branchTable.setCreateAt(new Date());
            branchTable.setLastUpdateAt(new Date());
            branchTable.setLastUpdateBy("System");
            branchTable.setVersion(BigInteger.ZERO);
            int i = branchTableMapper.insert(branchTable);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增桌台成功！");
                apiRest.setData(branchTable);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("新增桌台失败！");
            }
        }
        return apiRest;
    }

    /**
     * 获取桌台编码
     *
     * @param params
     * @return
     */
    public ApiRest getTableCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger areaId = BigInteger.valueOf(Long.parseLong((String)params.get("areaId")));
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        map.put("id", areaId);
        map.put("branchId", branchId);
        BranchArea branchArea = branchAreaMapper.getAreaById(map);
        if(branchArea == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到桌台区域信息！");
            return apiRest;
        }
        String code;
        Map areaMap = new HashMap();
        areaMap.put("tenantId", tenantId);
        areaMap.put("branchId", branchId);
        areaMap.put("areaCode", branchArea.getCode() == null ? "00" : branchArea.getCode());
        areaMap.put("areaId", areaId);
        code = branchTableMapper.getTableCode(areaMap);
        if(code == null || code.equals("")){
            code = SerialNumberGenerate.getNextCatTwoCode(branchArea.getCode() == null ? "0100" : branchArea.getCode() + "00");
        }
        else{
            code = SerialNumberGenerate.getNextCatTwoCode(code);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 删除桌台
     *
     * @param params
     * @return
     */
    public ApiRest delTable(Map params){
        ApiRest apiRest = new ApiRest();
        BranchTable branchTable = branchTableMapper.getTableById(params);
        if(branchTable == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("桌台查询失败！");
            return apiRest;
        }
        branchTable.setLastUpdateAt(new Date());
        branchTable.setLastUpdateBy("System");
        branchTable.setVersion(branchTable.getVersion().add(BigInteger.ONE));
        branchTable.setDeleted(true);
        int i = branchTableMapper.update(branchTable);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除桌台成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("删除桌台失败！");
        }
        return apiRest;
    }
}
