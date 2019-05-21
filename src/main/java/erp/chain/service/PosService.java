package erp.chain.service;


import com.saas.common.Constants;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.Employee;
import erp.chain.domain.Pos;
import erp.chain.domain.system.PosSysConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.PosMapper;
import erp.chain.mapper.SaleMapper;
import erp.chain.mapper.system.OperateLogMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xumx on 2016/10/31.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class PosService extends BaseService {

    @Autowired
    private PosMapper posMapper;
    @Autowired
    private SaleMapper saleMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private EmployeeMapper employeeMapper;

    public ApiRest getMaxSaleCodeByPos(BigInteger posId, String business) {
        ApiRest apiRest = new ApiRest();
        try {
            String maxSaleCode = "";
            Map params = new HashMap();
            params.put("id", posId);
            Pos pos = posMapper.find(params);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sf.format(new Date());
            if (pos != null) {
                maxSaleCode = saleMapper.getMaxSaleCode(posId, pos.getTenantId(), pos.getBranchId(), today);
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("获取流水号成功！");
            apiRest.setData(maxSaleCode);
            log.debug("POS-{}-{} synchronized getMaxSaleCOde ", posId, business);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setMessage("获取流水号失败！");
            apiRest.setError(e.getClass().getSimpleName() + " - " + e.getMessage());
            log.error("DataService.getMaxSaleCodeByPos({},{}) - {} - {}", posId, business, e.getClass().getSimpleName(), e.getMessage());
        }
        return apiRest;
    }

    public Pos findById(BigInteger id) throws Exception {
        return findPos(id);
    }

    public Map<String, Object> find(Map<String, String[]> params) {
        try {
            boolean b = false;
            StringBuilder hql = new StringBuilder("select * from pos where is_deleted = false ");
            if (params.get("token") != null) {
                hql.append(" and access_token = '").append(params.get("token")[0]).append("' ");
                b = true;
            }
            if (params.get("deviceCode") != null) {
                hql.append(" and device_code = '").append(params.get("deviceCode")[0]).append("' ");
                b = true;
            }
            if (params.get("status") != null) {
                hql.append(" and status = ").append(params.get("status")[0]);
                b = true;
            }
            if (params.get("posCode") != null) {
                hql.append(" and pos_code = '").append(params.get("posCode")[0]).append("' ");
                b = true;
            }
            if (params.get("password") != null) {
                hql.append(" and password = '").append(params.get("password")[0]).append("' ");
                b = true;
            }
            if (params.get("branchCode") != null) {
                hql.append(" and branch_code = '").append(params.get("branchCode")[0]).append("' ");
                b = true;
            }
            if (params.get("tenantCode") != null) {
                hql.append(" and tenant_code = '").append(params.get("tenantCode")[0]).append("' ");
                b = true;
            }
            if (params.get("id") != null) {
                hql.append(" and id = ").append(params.get("id")[0]).append(" ");
                b = true;
            }
            if (b) {
                List<Map<String, Object>> list = select(hql.toString());
                if (list != null && list.size() > 0) {
                    return CommonUtils.DomainMap(list.get(0));
                }
            }
        } catch (Exception e) {
            log.error("PosService.find({}) - {} - {}", params, e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public ApiRest register(BigInteger posId, String token, String deviceCode) {
        ApiRest apiRest = new ApiRest();
        try {
            Map params = new HashMap();
            params.put("id", posId);
            Pos pos = posMapper.find(params);
            if (pos != null) {
                ApiRest r = SaaSApi.findTenantById(pos.getTenantId());
                if (r.getIsSuccess()) {
                    String tenantType = ((Map) ((Map) r.getData()).get("tenant")).get("tenantType").toString();
                    String tenantId = ((Map) ((Map) r.getData()).get("tenant")).get("id").toString();
                    int i = update(String.format("update pos set access_token = '%s', device_code = '%s' where id = %s", token, deviceCode, posId));
                    if (i > 0) {
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("激活成功！");
                        apiRest.setData(1);
                    } else {
                        apiRest.setError("pos激活失败！");
                        apiRest.setIsSuccess(false);
                    }
                } else {
                    apiRest.setError("查询商户信息失败！");
                    apiRest.setIsSuccess(false);
                }
            } else {
                apiRest.setIsSuccess(false);
                apiRest.setError("查询pos信息失败！");
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("pos激活失败！");
            log.error("PosService.register({},{},{}) - {} - {}", posId, token, deviceCode, e.getClass().getSimpleName(), e.getMessage());
        }
        return apiRest;
    }

    public Map<String, Object> findVip(BigInteger vipId) {
        List<Map<String, Object>> list = select(String.format("select * from vip where id = %s", vipId));
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 初始化POS
     */
    public ApiRest initPos(BigInteger tenantId, BigInteger branchId, BigInteger posId, String deviceCode) {
        ApiRest apiRest = new ApiRest();
        try {
            Pos pos = posMapper.findByTenantIdAndBranchIdAndId(tenantId, branchId, posId);
            Validate.notNull(pos, "POS不存在");
            List<Pos> posList = posMapper.findByDeviceCode(deviceCode);
            String message = "";
            for (Pos p : posList) {
                message += (p.getBranchName() + " ");
            }
            if (posList.size() > 0) {
                apiRest.setIsSuccess(false);
                apiRest.setError("POS已初始化，对应的机构:" + message + "，请解除与机构的关系");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("获取pos信息成功！");
            apiRest.setData(pos);
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("初始化POS失败！");
            ServiceException se = new ServiceException("1002", "初始化POS失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 初始化POS
     */
    public ApiRest autoInitPos(String tenantCode, BigInteger tenantId, BigInteger branchId, String deviceCode) {
        ApiRest apiRest = new ApiRest();
        try {
            List<Pos> posList = posMapper.findByDeviceCode(deviceCode);
            Pos usePos = null;
            int type = 0;
            String message = "";
            for (Pos p : posList) {
                if (Objects.equals(p.getTenantId(), tenantId)) {
                    if (Objects.equals(p.getBranchId(), branchId)) {
                        p.setStatus(1);
                        p.setDeviceCode("");
                        p.setAccessToken("");
                        p.setAppName("");
                        p.setAppVersion("");
                        p.setLastUpdateAt(new Date());
                        p.setLastUpdateBy("System");
                        p.setVersion(p.getVersion().add(BigInteger.valueOf(1)));
                        int i = posMapper.update(p);
                        if (i > 0) {
                            usePos = p;
                        } else {
                            apiRest.setIsSuccess(false);
                            apiRest.setError("注销POS失败！");
                            return apiRest;
                        }
                    } else {
                        message += (p.getBranchName() + " ");
                    }
                } else {
                    p.setStatus(1);
                    p.setDeviceCode("");
                    p.setAccessToken("");
                    p.setAppName("");
                    p.setAppVersion("");
                    p.setLastUpdateAt(new Date());
                    p.setLastUpdateBy("System");
                    p.setVersion(p.getVersion().add(BigInteger.valueOf(1)));
                    int i = posMapper.update(p);
                    if (i <= 0) {
                        apiRest.setIsSuccess(false);
                        apiRest.setError("注销POS失败！");
                        return apiRest;
                    }
                    type = 1;
                }
            }
            if (usePos != null) {
                Map posAutoMap = new HashMap();
                posAutoMap.put("branchCode", usePos.getBranchCode());
                posAutoMap.put("posId", usePos.getId());
                posAutoMap.put("branchId", usePos.getBranchId());
                posAutoMap.put("tenantId", tenantId);
                posAutoMap.put("tenantCode", tenantCode);
                posAutoMap.put("deviceCode", deviceCode);
                posAutoMap.put("posCode", usePos.getPosCode());
                posAutoMap.put("password", usePos.getPassword());
                ApiRest res = ProxyApi.proxyPost(Constants.SERVICE_NAME_BS, "pos", "savePosAutoInfo", posAutoMap);
                if (!res.getIsSuccess()) {
                    return res;
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("获取pos信息成功！");
                apiRest.setData(res.getData());
            } else if (posList.size() > 0 && type == 0) {
                apiRest.setIsSuccess(false);
                apiRest.setMessage("POS已初始化，对应的机构:" + message + "，请解除与机构的关系");
                apiRest.setError("POS已初始化，对应的机构:" + message + "，请解除与机构的关系");
            } else if (posList.size() == 0 || (posList.size() > 0 && type == 1)) {
                Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
                if (branch == null) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未查询到机构信息！");
                    return apiRest;
                }
                Pos pos = new Pos();
                String code = SerialNumberGenerate.nextSerialNumber(3, posMapper.getMaxPosCode(tenantId));
                pos.setPosCode(code);
                pos.setTenantId(tenantId);
                pos.setBranchId(branchId);
                pos.setTenantCode(tenantCode);
                pos.setBranchCode(branch.getCode());
                pos.setBranchName(branch.getName());
                pos.setPassword("123456");
                pos.setLastUpdateBy("System");
                pos.setLastUpdateAt(new Date());
                pos.setCreateAt(new Date());
                pos.setCreateBy("System");
                pos.setStatus(1);
                pos.setVersion(BigInteger.valueOf(0));
                int i = posMapper.insert(pos);
                if (i <= 0) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增pos信息失败！");
                    return apiRest;
                }
                Map posAutoMap = new HashMap();
                posAutoMap.put("branchCode", branch.getCode());
                posAutoMap.put("posId", pos.getId());
                posAutoMap.put("branchId", branch.getId());
                posAutoMap.put("tenantId", tenantId);
                posAutoMap.put("tenantCode", tenantCode);
                posAutoMap.put("deviceCode", deviceCode);
                posAutoMap.put("posCode", pos.getPosCode());
                posAutoMap.put("password", pos.getPassword());
                ApiRest res = ProxyApi.proxyPost(Constants.SERVICE_NAME_BS, "pos", "savePosAutoInfo", posAutoMap);
                if (!res.getIsSuccess()) {
                    return res;
                }
                apiRest.setIsSuccess(true);
                apiRest.setMessage("获取pos信息成功！");
                apiRest.setData(res.getData());
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("获取pos信息失败！");
            ServiceException se = new ServiceException("1002", "获取pos信息失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 初始化POS
     */
    public ApiRest clearSaleData(Map params) {
        ApiRest apiRest = new ApiRest();
        try {
            BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
            Validate.notNull(tenantId, "tenantId is not null");
            BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
            Validate.notNull(branchId, "branchId is not null");
            BigInteger posId = BigInteger.valueOf(Long.valueOf(params.get("posId").toString()));
            Validate.notNull(posId, "posId is not null");
            operateLogMapper.createOpLog("pos清除数据", posId, "清除流水", "0", "1", "", "POSID" + posId, "前台清除", tenantId, branchId);
            posMapper.clearSaleDetailData(params);
            posMapper.clearSalePaymentData(params);
            posMapper.clearSaleData(params);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("清除数据成功！");
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("清除数据失败！");
            ServiceException se = new ServiceException("1002", "清除数据失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 拉取pos信息
     * @param params
     * @return
     */
    public ApiRest getUserPosInfo(Map params){
        ApiRest apiRest = new ApiRest();
        try {
            String deviceCode=params.get("deviceCode").toString();
            BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
            BigInteger branchId=BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
            String tenantCode=params.get("tenantCode").toString();
            String partitionCode=params.get("partitionCode")==null?"":params.get("partitionCode").toString();
            Pos pos=posMapper.findPosByTenantIdAndDeviceCode(deviceCode,tenantId,branchId);
            if(pos!=null){
                int i=posMapper.clearInfo(pos);
                if(i>0){
                    pos.setPartitionCode(partitionCode);
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("拉取pos信息成功！");
                    apiRest.setData(pos);
                }else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("拉取pos信息失败，注销pos失败！");
                }
            }else{
                Pos noUsedPos=posMapper.findNoUsedPos(tenantId,branchId);
                if(noUsedPos!=null){
                    if(noUsedPos.getStatus()==0){
                        noUsedPos.setStatus(1);
                        int i=posMapper.update(noUsedPos);
                        if(i>0){
                            noUsedPos.setPartitionCode(partitionCode);
                            apiRest.setIsSuccess(true);
                            apiRest.setMessage("拉取pos信息成功！");
                            apiRest.setData(noUsedPos);
                        }else{
                            apiRest.setIsSuccess(false);
                            apiRest.setError("拉取pos信息失败，更新未使用pos失败！");
                        }
                    }else{
                        noUsedPos.setPartitionCode(partitionCode);
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("拉取pos信息成功！");
                        apiRest.setData(noUsedPos);
                    }
                }else{
                    Branch branch=branchMapper.findByTenantIdAndBranchId(params);
                    if(branch==null){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("未查询到机构信息！");
                        return apiRest;
                    }
                    if(posMapper.getMaxPosCode(tenantId)!=null&&posMapper.getMaxPosCode(tenantId).equals("999")){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("您增加的pos已经超限，请注销其它pos使用");
                        return apiRest;
                    }
                    Pos newPos=new Pos();
                    String code= SerialNumberGenerate.nextSerialNumber(3, posMapper.getMaxPosCode(tenantId));
                    newPos.setPosCode(code);
                    newPos.setTenantId(tenantId);
                    newPos.setBranchId(branchId);
                    newPos.setTenantCode(tenantCode);
                    newPos.setBranchCode(branch.getCode());
                    newPos.setBranchName(branch.getName());
                    newPos.setPassword("888888");
                    newPos.setLastUpdateBy("System");
                    newPos.setLastUpdateAt(new Date());
                    newPos.setCreateAt(new Date());
                    newPos.setCreateBy("System");
                    newPos.setStatus(1);
                    newPos.setVersion(BigInteger.valueOf(0));
                    int i = posMapper.insert(newPos);
                    if(i > 0){
                        newPos.setPartitionCode(partitionCode);
                        apiRest.setIsSuccess(true);
                        apiRest.setError("拉取pos信息成功！");
                        apiRest.setData(newPos);
                    }else{
                        apiRest.setIsSuccess(false);
                        apiRest.setError("拉取pos信息失败，新增pos信息失败！");
                    }

                }
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("拉取pos信息失败！");
            ServiceException se = new ServiceException("1003", "拉取pos信息失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 上传pos系统配置，有则覆盖
     */
    public ApiRest uploadPosSysConfig(Map params) {
        ApiRest apiRest = new ApiRest();
        try {
            String type = params.get("type").toString();
            String tenantCode = params.get("tenantCode").toString();
            BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
            BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
            BigInteger employeeId = BigInteger.valueOf(Long.valueOf(params.get("employeeId").toString()));
            Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
            if (branch == null) {
                apiRest.setIsSuccess(false);
                apiRest.setError("查询机构失败！");
                return apiRest;
            }
            Employee employee = employeeMapper.getEmployeeById(employeeId, tenantId);
            if (employee == null) {
                apiRest.setIsSuccess(false);
                apiRest.setError("查询员工失败！");
                return apiRest;
            }
            String recoverCode = tenantCode + branch.getCode() + employee.getCode();
            String data = params.get("data").toString();
            PosSysConfig posSysConfig = tenantConfigMapper.getConfig(tenantId, branchId, employeeId, type);
            if (posSysConfig != null) {
                posSysConfig.setData(data);
                posSysConfig.setRecoverCode(recoverCode);
                posSysConfig.setLastUpdateAt(new Date());
                posSysConfig.setLastUpdateBy(employee.getName());
                int i = tenantConfigMapper.updatePosSysConfig(posSysConfig);
                if (i < 0) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改备份数据失败！");
                    return apiRest;
                }
            } else {
                posSysConfig = new PosSysConfig();
                posSysConfig.setTenantId(tenantId);
                posSysConfig.setBranchId(branchId);
                posSysConfig.setEmployeeId(employeeId);
                posSysConfig.setData(data);
                posSysConfig.setRecoverCode(recoverCode);
                posSysConfig.setCreateAt(new Date());
                posSysConfig.setCreateBy(employee.getName());
                posSysConfig.setLastUpdateAt(new Date());
                posSysConfig.setLastUpdateBy(employee.getName());
                posSysConfig.setType(type);
                int i = tenantConfigMapper.insertPosSysConfig(posSysConfig);
                if (i < 0) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("保存备份数据失败！");
                    return apiRest;
                }
            }
            apiRest.setData(posSysConfig);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("备份数据成功！");
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("备份数据失败！");
            ServiceException se = new ServiceException("4001", "备份数据失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 拉取pos系统参数
     */
    public ApiRest recoverPosSysConfig(Map params) {
        ApiRest apiRest = new ApiRest();
        try {
            String type = params.get("type").toString();
            String recoverCode = params.get("recoverCode").toString();
            PosSysConfig posSysConfig = tenantConfigMapper.recoverPosSysConfig(recoverCode, type);
            apiRest.setIsSuccess(true);
            apiRest.setData(posSysConfig);
            apiRest.setMessage("提取数据成功！");
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            apiRest.setIsSuccess(false);
            apiRest.setError("提取数据失败！");
            ServiceException se = new ServiceException("4002", "提取数据失败：" + e.getMessage());
            throw se;
        }
        return apiRest;
    }

}
