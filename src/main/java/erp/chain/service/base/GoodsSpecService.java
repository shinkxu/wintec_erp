package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.GoodsSpec;
import erp.chain.domain.SpecGroup;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.GoodsSpecMapper;
import erp.chain.mapper.SpecGroupMapper;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.SerialNumberGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜品口味
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsSpecService{
    @Autowired
    private GoodsSpecMapper goodsSpecMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private TenantConfigService tenantConfigService;

    /**
     * 查询口味列表
     *
     * @param params
     * @return
     */
    public ApiRest goodsSpecList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
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
        Branch branch = branchMapper.findByTenantIdAndBranchId(params);
        if(branch == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到机构信息！");
            return apiRest;
        }
        if(params.get("groupId") != null && !params.get("groupId").equals("")){
            BigInteger groupId = BigInteger.valueOf(Long.parseLong((String)params.get("groupId")));
            SpecGroup specGroup = specGroupMapper.findByIdAndTenantId(groupId, tenantId);
            if(specGroup == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("口味组查询失败！");
                return apiRest;
            }
        }
        List<Map> list = goodsSpecMapper.goodsSpecList(params);
        Long count = goodsSpecMapper.goodsSpecListCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询口味成功！");
        return apiRest;
    }

    /**
     * 获取口味编码
     *
     * @param params
     * @return
     */
    public ApiRest getSpecCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger groupId = BigInteger.valueOf(Long.parseLong((String)params.get("groupId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String code = goodsSpecMapper.getSpecCode(params);
        if(code == null || code.equals("")){
            SpecGroup specGroup = specGroupMapper.findByIdAndTenantId(groupId, tenantId);
            if(specGroup == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到口味分组信息！");
                return apiRest;
            }
            code = specGroup.getCode() + "01";
        }
        else{
            code = SerialNumberGenerate.nextSerialNumber(code.length(), code);
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(code);
        apiRest.setMessage("获取口味编码成功！");
        return apiRest;
    }

    /**
     * 新增或修改口味
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateSpec(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger groupId = BigInteger.valueOf(Long.parseLong((String)params.get("groupId")));
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigDecimal price = BigDecimal.valueOf(Double.valueOf(params.get("price").toString()));
        String property = params.get("property").toString();
        SpecGroup specGroup = specGroupMapper.findByIdAndTenantId(groupId, tenantId);
        if(specGroup == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未找到此分组！");
            return apiRest;
        }
        Long propertyCount = goodsSpecMapper.checkProperty(params);
        if(propertyCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("同一分组下不允许口味名称相同！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            GoodsSpec goodsSpec = goodsSpecMapper.findByIdAndTenantId(id, tenantId);
            if(goodsSpec == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("没有找到需要修改的口味！");
                return apiRest;
            }
            goodsSpec.setLastUpdateBy("System");
            goodsSpec.setLastUpdateAt(new Date());
            goodsSpec.setPrice(price);
            goodsSpec.setProperty(property);
            int i = goodsSpecMapper.update(goodsSpec);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改口味成功！");
                apiRest.setData(goodsSpec);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改口味失败！");
            }
        }
        else{
            ApiRest r = tenantConfigService.getMaxValue(tenantId, SysConfig.SYS_SPEC_NUM);
            if(!r.getIsSuccess()){
                apiRest = r;
                return apiRest;
            }
            if(goodsSpecMapper.countByBranch(params) >= Long.valueOf(r.getData().toString())){
                apiRest.setIsSuccess(false);
                apiRest.setError("每个机构最多可以添加" + r.getData() + "个口味！");
                return apiRest;
            }
            ApiRest rest = getSpecCode(params);
            if(!rest.getIsSuccess()){
                apiRest = rest;
                return apiRest;
            }
            String code = rest.getData().toString();
            GoodsSpec goodsSpec = new GoodsSpec();
            goodsSpec.setCode(code);
            goodsSpec.setPrice(price);
            goodsSpec.setProperty(property);
            goodsSpec.setCreateBy("System");
            goodsSpec.setCreateAt(new Date());
            goodsSpec.setLastUpdateAt(new Date());
            goodsSpec.setLastUpdateBy("System");
            goodsSpec.setDescribing("商品口味");
            goodsSpec.setParentId(BigInteger.valueOf(1));
            goodsSpec.setGroupId(groupId);
            goodsSpec.setVersion(BigInteger.valueOf(0));
            goodsSpec.setTenantId(tenantId);
            goodsSpec.setBranchId(branchId);
            goodsSpec.setOrderKey(0);
            int i = goodsSpecMapper.insert(goodsSpec);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增口味成功！");
                apiRest.setData(goodsSpec);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("新增口味失败！");
            }
        }
        return apiRest;
    }

    /**
     * 删除口味
     *
     * @param params
     * @return
     */
    public ApiRest delGoodsSpec(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        GoodsSpec goodsSpec = goodsSpecMapper.findByIdAndTenantId(id, tenantId);
        if(goodsSpec == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("没有找到需要删除的口味！");
            return apiRest;
        }
        goodsSpec.setDeleted(true);
        goodsSpec.setLastUpdateAt(new Date());
        int i = goodsSpecMapper.update(goodsSpec);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除口味成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("删除口味失败！");
        }
        return apiRest;
    }
}
