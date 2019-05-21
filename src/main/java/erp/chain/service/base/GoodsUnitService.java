package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import erp.chain.domain.GoodsUnit;
import erp.chain.domain.system.SysConfig;
import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsUnitMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * 单位Service
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsUnitService{
    @Autowired
    private GoodsUnitMapper goodsUnitMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;

    /**
     * 查询单位列表(接口用)
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest queryGoodsUnitListWs(Map params) throws ServiceException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = null;
        if(params.get("branchId") != null && !"".equals(params.get("branchId"))){
            branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        }
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<HashMap<String, Object>> list = goodsUnitMapper.queryUnitList(params);
        Long count = goodsUnitMapper.queryUnitCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询商品单位成功！");
        return apiRest;
    }

    /**
     * 根据单位id查询（接口用）
     *
     * @param params
     * @return
     */
    public ApiRest queryUnitById(Map params){
        ApiRest apiRest = new ApiRest();
        GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(params);
        if(goodsUnit != null){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询单位成功！");
            apiRest.setData(goodsUnit);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到相应单位，请确认参数是否正确！");
        }
        return apiRest;
    }

    /**
     * 删除GoodsUnit
     *
     * @param params
     * @return
     */
    public ApiRest delUnitById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String ids = "";
        if(params.get("id") != null && !params.get("id").equals("")){
            ids = params.get("id").toString();
        }
        if(params.get("ids") != null && !params.get("ids").equals("")){
            ids = params.get("ids").toString();
        }
        Map resultMap = new HashMap();
        int count = 0;
        List<GoodsUnit> unitList = new ArrayList<>();
        List<Map> errorList = new ArrayList<>();

        for(String id : ids.split(",")){
            Map errorMap = new HashMap();
            Map unitMap = new HashMap();
            unitMap.put("tenantId", tenantId);
            unitMap.put("id", id);
            GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
            // 删除时判断是否有菜品使用
            if(goodsUnit != null){
                Long usedCount = goodsUnitMapper.checkGoodsUnit(params);
                if(usedCount > 0){
                    errorMap.put("id", goodsUnit.getId());
                    errorMap.put("unitName", goodsUnit.getUnitName());
                    errorMap.put("mes", "单位已经被使用，删除失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError("单位已经被使用，删除失败！");
                }
                else{
                    goodsUnit.setIsDeleted(true);
                    goodsUnit.setLastUpdateBy("System");
                    goodsUnit.setLastUpdateAt(new Date());
                    ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_UNIT_NUM);
                    if(!r.getIsSuccess()){
                        apiRest = r;
                        return apiRest;
                    }
                    int i = goodsUnitMapper.update(goodsUnit);
                    if(i > 0){
                        count++;
                        unitList.add(goodsUnit);
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("单位删除成功！");
                    }
                }
            }
            else{
                errorMap.put("id", id);
                errorMap.put("mes", "未找到ID为" + params.get("id") + "，商户ID为" + params.get("tenantId") + "的单位，请确认信息是否准确！");
                errorList.add(errorMap);
                apiRest.setIsSuccess(false);
                apiRest.setError("未找到ID为" + params.get("id") + "，商户ID为" + params.get("tenantId") + "的单位，请确认信息是否准确！");
                return apiRest;
            }
        }
        if(count > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("单位删除成功");
        }
        if(errorList.size() > 0){
            apiRest.setError("");
            for(int i = 0; i < errorList.size(); i++){
                apiRest.setError("" + errorList.get(i).get("mes") + "");
            }
        }
        resultMap.put("num", count);
        resultMap.put("errors", errorList);
        resultMap.put("delList", unitList);
        apiRest.setData(resultMap);
        return apiRest;
    }
    /**
     * 新增单位不检查单位限制，为商品档案新增导入调用
     */
    public ApiRest saveUnit(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = null;
        if(params.get("branchId") != null){
            branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        }
        String unitName = params.get("unitName").toString();
        if(params.get("unitName").toString().length() > 10){
            apiRest.setIsSuccess(false);
            apiRest.setError("商品单位名称不能超过10个字符");
            return apiRest;
        }
            GoodsUnit goodsUnit = new GoodsUnit();
            params.put("id", null);
            params.put("unitName", unitName);
         /*   ApiRest r = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_UNIT_NUM);
            if(!r.getIsSuccess()){
                apiRest = r;
                return apiRest;
            }*/
            Long nameNum = goodsUnitMapper.checkUnitName(params);
            if(nameNum > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("单位名字重复！");
            }
            else{
                goodsUnit.setUnitCode(SerialNumberGenerate.nextSerialNumber(2, goodsUnitMapper.getMaxGoodsUnitCodeWs(params)));
                goodsUnit.setUnitName(unitName);
                goodsUnit.setCreateAt(new Date());
                goodsUnit.setLastUpdateAt(new Date());
                goodsUnit.setCreateBy("System");
                goodsUnit.setLastUpdateBy("System");
                goodsUnit.setTenantId(tenantId);
                goodsUnit.setVersion(BigInteger.valueOf(0));
                goodsUnit.setBranchId(branchId);
                int i = goodsUnitMapper.insert(goodsUnit);
                ApiRest rest = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_UNIT_NUM);
                if(!rest.getIsSuccess()){
                    apiRest = rest;
                    return apiRest;
                }
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("新增单位成功！");
                    apiRest.setData(goodsUnit);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("新增单位失败！");
                }
            }
        return apiRest;
    }

    /**
     * 新增或修改单位
     *
     * @param params
     * @return
     */
    public ApiRest saveOrUpdateUnit(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = null;
        if(params.get("branchId") != null){
            branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        }
        String unitName = params.get("unitName").toString();
        if(params.get("unitName").toString().length() > 10){
            apiRest.setIsSuccess(false);
            apiRest.setError("商品单位名称不能超过10个字符");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(params);
            if(goodsUnit == null){
                apiRest.setError("未查询到单位！");
                apiRest.setIsSuccess(false);
            }
            else{
                params.put("unitName", unitName);
                Long nameNum = goodsUnitMapper.checkUnitName(params);
                if(nameNum > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("单位名字重复！");
                }
                else{
                    /*如果修改单位名称则修改关联的商品单位名称*/
                    if(!unitName.equals(goodsUnit.getUnitName())){
                        Map map = new HashMap();
                        map.put("tenantId", goodsUnit.getTenantId());
                        map.put("branchId", goodsUnit.getBranchId() == null ? params.get("branchId") : goodsUnit.getBranchId());
                        map.put("unitId", goodsUnit.getId());
                        map.put("unitName", unitName);
                        goodsMapper.updateUnitOrCategory(map);
                        Map map1 = new HashMap();
                        map1.put("tenantId", goodsUnit.getTenantId());
                        map1.put("branchId", goodsUnit.getBranchId() == null ? params.get("branchId") : goodsUnit.getBranchId());
                        map1.put("pUnitId", goodsUnit.getId());
                        map1.put("pUnitName", unitName);
                        goodsMapper.updateUnitOrCategory(map1);
                    }
                    goodsUnit.setUnitName(unitName);
                    goodsUnit.setLastUpdateAt(new Date());
                    goodsUnit.setLastUpdateBy("System");
                    int i = goodsUnitMapper.update(goodsUnit);
                    if(i > 0){
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("修改单位成功！");
                        apiRest.setData(goodsUnit);
                    }
                    else{
                        apiRest.setIsSuccess(false);
                        apiRest.setMessage("修改单位失败！");
                    }
                }
            }
        }
        else{
            GoodsUnit goodsUnit = new GoodsUnit();
            params.put("id", null);
            params.put("unitName", unitName);
            if(params.get("tenantBusiness") != null && "6".equals(params.get("tenantBusiness").toString())){
                TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(SysConfig.SYS_UNIT_NUM,tenantId);
                long count = goodsUnitMapper.farmUnitListCount(params);
                if(count >= Integer.valueOf(tenantConfig.getMaxValue())){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("您最多可以添加"+tenantConfig.getMaxValue()+"个单位");
                    return apiRest;
                }
            }else{
                ApiRest r = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_UNIT_NUM);
                if(!r.getIsSuccess()){
                    apiRest = r;
                    return apiRest;
                }
            }
            Long nameNum = goodsUnitMapper.checkUnitName(params);
            if(nameNum > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("单位名字重复！");
            }
            else{
                goodsUnit.setUnitCode(SerialNumberGenerate.nextSerialNumber(2, goodsUnitMapper.getMaxGoodsUnitCodeWs(params)));
                goodsUnit.setUnitName(unitName);
                goodsUnit.setCreateAt(new Date());
                goodsUnit.setLastUpdateAt(new Date());
                goodsUnit.setCreateBy("System");
                goodsUnit.setLastUpdateBy("System");
                goodsUnit.setTenantId(tenantId);
                goodsUnit.setVersion(BigInteger.valueOf(0));
                goodsUnit.setBranchId(branchId);
                int i = goodsUnitMapper.insert(goodsUnit);
                ApiRest rest = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_UNIT_NUM);
                if(!rest.getIsSuccess()){
                    apiRest = rest;
                    return apiRest;
                }
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("新增单位成功！");
                    apiRest.setData(goodsUnit);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("新增单位失败！");
                }
            }
        }
        return apiRest;
    }

    /**
     * 生成单位编码
     *
     * @param params
     * @return
     */
    public ApiRest getNextUnitCode(Map params){
        ApiRest apiRest = new ApiRest();
        String code = SerialNumberGenerate.nextSerialNumber(2, goodsUnitMapper.getMaxGoodsUnitCodeWs(params));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成单位编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    public ApiRest getGoodsUnitList(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("tenantBusiness") != null && !"".equals(params.get("tenantBusiness")) && "6".equals(params.get("tenantBusiness"))){
            params.put("onlySelf", 1);
        }
        else{
            params.put("onlySelf", 0);
        }
        List<GoodsUnit> goodsUnits = goodsUnitMapper.queryUnits(params);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取单位成功！");
        apiRest.setData(goodsUnits);
        return apiRest;
    }
}
