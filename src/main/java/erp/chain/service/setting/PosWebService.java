package erp.chain.service.setting;

import com.saas.common.util.PartitionCacheUtils;
import erp.chain.domain.Branch;
import erp.chain.domain.Pos;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.PosMapper;
import erp.chain.service.system.OperateLogService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pos设置
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PosWebService{
    @Autowired
    private PosMapper posMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 查询pos列表
     *
     * @param params
     * @return
     */
    public ApiRest queryPosList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        if(params.get("branchId") != null && !params.get("branchId").equals("")){
            BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
            params.put("branchId", branchId);
        }
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        List<Map> list = posMapper.queryPosList(params);
        Long count = posMapper.queryPosListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询Pos列表成功！");
        return apiRest;
    }

    /**
     * 根据ID查询Pos
     *
     * @param params
     * @return
     */
    public ApiRest queryPosById(Map params){
        ApiRest apiRest = new ApiRest();
        Pos pos = posMapper.queryPosById(params);
        if(pos == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询pos失败！");
        }
        else{
            apiRest.setIsSuccess(true);
            apiRest.setData(pos);
            apiRest.setMessage("查询Pos成功！");
        }
        return apiRest;
    }

    /**
     * 新增或修改Pos
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdatePos(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        if(params.get("id") != null && !params.get("id").equals("")){
            Pos pos = posMapper.queryPosById(params);
            String userName = params.get("userName") == null ? "" : params.get("userName").toString();
            String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
            Map oldPos = CommonUtils.ObjectToMap(pos);
            if(pos == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询pos失败！");
                return apiRest;
            }
            pos.setPassword(params.get("password").toString());
            pos.setLastUpdateBy("System");
            pos.setLastUpdateAt(new Date());
            pos.setVersion(pos.getVersion().add(BigInteger.valueOf(1)));
            pos.setStatus(Integer.valueOf(params.get("status").toString()));
            int i = posMapper.update(pos);
            if(i > 0){
                operateLogService.saveLog(pos, oldPos, 2, opFrom, userName, pos.getTenantId(), pos.getBranchId());
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改pos信息成功！");
                apiRest.setData(pos);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改pos信息失败！");
            }
        }
        else{
            Branch branch = branchMapper.findByTenantIdAndBranchId(params);
            if(branch == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到机构信息！");
                return apiRest;
            }
            if(posMapper.getMaxPosCode(tenantId) != null && posMapper.getMaxPosCode(tenantId).equals("999")){
                apiRest.setIsSuccess(false);
                apiRest.setError("您增加的pos已经超限，请注销其它pos使用");
                return apiRest;
            }
            Pos pos = new Pos();
            String code = SerialNumberGenerate.nextSerialNumber(3, posMapper.getMaxPosCode(tenantId));
            pos.setPosCode(code);
            pos.setTenantId(tenantId);
            pos.setBranchId(branchId);
            pos.setTenantCode(params.get("tenantCode").toString());
            pos.setBranchCode(branch.getCode());
            pos.setBranchName(branch.getName());
            pos.setPassword(params.get("password").toString());
            pos.setLastUpdateBy("System");
            pos.setLastUpdateAt(new Date());
            pos.setCreateAt(new Date());
            pos.setCreateBy("System");
            pos.setStatus(Integer.valueOf(params.get("status").toString()));
            pos.setVersion(BigInteger.valueOf(0));
            int i = posMapper.insert(pos);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增pos信息成功！");
                apiRest.setData(pos);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("新增pos信息失败！");
            }
        }
        return apiRest;
    }

    /**
     * 生成POS编码
     *
     * @param tenantId
     * @return
     */
    public ApiRest getPosCode(BigInteger tenantId){
        ApiRest apiRest = new ApiRest();
        String code = SerialNumberGenerate.nextSerialNumber(3, posMapper.getMaxPosCode(tenantId));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成POS编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 注销pos
     *
     * @param params
     * @return
     */
    public ApiRest cancelPos(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        Pos pos = posMapper.queryPosById(params);
        if(pos == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询pos失败！");
            return apiRest;
        }
        String userName = params.get("userName") == null ? "" : params.get("userName").toString();
        String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
        Map oldPos = CommonUtils.ObjectToMap(pos);
        pos.setStatus(0);
        pos.setDeviceCode("");
        pos.setAccessToken("");
        pos.setAppName("");
        pos.setAppVersion("");
        pos.setLastUpdateAt(new Date());
        pos.setLastUpdateBy(userName);
        pos.setVersion(pos.getVersion() == null ? BigInteger.ZERO : pos.getVersion().add(BigInteger.valueOf(1)));
        int i = posMapper.update(pos);
        if(i > 0){
            operateLogService.saveLog(pos, oldPos, 2, opFrom, userName, pos.getTenantId(), pos.getBranchId());
            apiRest.setIsSuccess(true);
            apiRest.setMessage("注销POS成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("注销POS失败！");
        }
        return apiRest;
    }

    /**
     * 自动拉取重置
     *
     * @param params
     * @return
     */
    public ApiRest disableAutoPull(Map params) throws IOException{
        ApiRest apiRest = new ApiRest();
        params.put("id", params.get("posId").toString());
        Pos pos = posMapper.queryPosById(params);
        if(pos == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询pos失败！");
            return apiRest;
        }
        pos.setIsPullLog(0);
        pos.setLastUpdateAt(new Date());
        pos.setLastUpdateBy("System");
        pos.setVersion(pos.getVersion() == null ? BigInteger.ZERO : pos.getVersion().add(BigInteger.valueOf(1)));
        int i = posMapper.update(pos);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("重置拉取状态成功！");
            PartitionCacheUtils.del("PullLog_" + pos.getTenantId() + "_" + pos.getBranchId() + "_" + pos.getId());
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("重置拉取状态失败！");
        }
        return apiRest;
    }

    /**
     * 删除pos
     *
     * @param params
     * @return
     */
    public ApiRest delPos(Map params) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        Pos pos = posMapper.queryPosById(params);
        if(pos == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询pos失败！");
            return apiRest;
        }
        String userName = params.get("userName") == null ? "" : params.get("userName").toString();
        String opFrom = params.get("opFrom") == null ? "" : params.get("opFrom").toString();
        Map oldPos = CommonUtils.ObjectToMap(pos);
        pos.setStatus(0);
        pos.setDeviceCode("");
        pos.setAccessToken("");
        pos.setAppName("");
        pos.setAppVersion("");
        pos.setLastUpdateAt(new Date());
        pos.setLastUpdateBy(userName);
        pos.setVersion(pos.getVersion().add(BigInteger.valueOf(1)));
        pos.setDeleted(true);
        int i = posMapper.update(pos);
        if(i > 0){
            operateLogService.saveLog(pos, oldPos, 3, opFrom, userName, pos.getTenantId(), pos.getBranchId());
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除POS成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("删除POS失败！");
        }
        return apiRest;
    }

    public ApiRest disableOrEnablePos(String userName, String opFrom, BigInteger tenantId, BigInteger branchId, BigInteger posId, Integer status) throws IllegalAccessException{
        ApiRest apiRest = new ApiRest();
        Pos pos = posMapper.findByTenantIdAndBranchIdAndId(tenantId, branchId, posId);
        Validate.notNull(pos, "ID为：" + posId + "POS不存在！");
        Map oldPos = CommonUtils.ObjectToMap(pos);
        String statusText = null;
        String originalStatusText = null;
        switch(pos.getStatus()){
            case 0:
                originalStatusText = "停用";
                break;
            case 1:
                originalStatusText = "启用";
                break;
            default:
                originalStatusText = "";
                break;
        }

        switch(status){
            case 0:
                statusText = "停用";
                break;
            case 1:
                statusText = "启用";
                break;
            default:
                statusText = "";
                break;
        }
        Validate.isTrue(!pos.getStatus().equals(status), "POS已经是" + originalStatusText + "状态，不能重复" + statusText);
        pos.setStatus(status);
        posMapper.update(pos);
        operateLogService.saveLog(pos, oldPos, 3, opFrom, userName, pos.getTenantId(), pos.getBranchId());
        apiRest.setData(pos);
        apiRest.setMessage(statusText + "成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 获取设备号
     *
     * @param params
     * @return
     */
    public ApiRest getDeviceCodes(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        List<Pos> posList = posMapper.getDeviceCodes(tenantId);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取设备号成功！");
        apiRest.setData(posList);
        return apiRest;
    }
}
