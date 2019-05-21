package erp.chain.service.setting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.BranchMapping;
import erp.chain.domain.GoodsMapping;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.MappingSettingMapper;
import erp.chain.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lipeng on 2017/5/8.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class MappingSettingService extends BaseService {

    @Autowired
    private MappingSettingMapper mappingSettingMapper;
    @Autowired
    private BranchMapper branchMapper;

    /**
     * 分页查询商品对应关系
     * @param params
     * @return
     * */
    /*public ApiRest listGoodsMapping(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            String tenantCode = (String) params.get("tenantCode");
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
            if (params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")) {
                Integer rows=Integer.parseInt(params.get("rows").toString());
                Integer offset=(Integer.parseInt(params.get("page").toString()) - 1) * rows;
                params.put("rows",rows);
                params.put("offset",offset);
            }
            params.put("tenantCode", tenantCode);
            params.put("tenantId", tenantId);
            Branch branch=branchMapper.getMainBranch(tenantId.toString());
            params.put("branchId", branch.getId());
            params.put("distributionCenterId", distributionCenterId);
            List<Map> list = mappingSettingMapper.listGoodsMapping(params);
            Long count = mappingSettingMapper.listGoodsMappingSum(params);
            map.put("total", count);
            map.put("rows", list);
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("分页查询商品对应关系成功！");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询商品对应关系失败！");
            ServiceException se = new ServiceException("1001", "查询商品对应关系失败", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }*/
    public ApiRest listGoodsMapping(BigInteger tenantId, String tenantCode, BigInteger distributionCenterId, int page, int rows, String goodsCodeOrName,int onlySelf) {
        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        Validate.notNull(headquartersBranch, "总部机构不存在！");
        long count = mappingSettingMapper.queryGoodsMappingCount(tenantId, tenantCode, headquartersBranch.getId(), distributionCenterId, goodsCodeOrName, onlySelf);
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (count > 0) {
            results = mappingSettingMapper.listGoodsMapping(tenantId, tenantCode, headquartersBranch.getId(), distributionCenterId, goodsCodeOrName, (page - 1) * rows, rows, onlySelf);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", results);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("分页查询商品对应关系成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 分页显示第三方商品（没有与goods表映射的产品）
     * @param params
     * @return
     * */
    /*public ApiRest listThirdGoods(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
            if(params.get("rows") != null && params.get("rows") != "" && params.get("page") != null && params.get("page") != ""){
                Integer rows = Integer.parseInt((String) params.get("rows"));
                Integer offset = (Integer.parseInt((String) params.get("page")) - 1) * rows;
                params.put("rows", rows);
                params.put("offset", offset);
            }
            params.put("tenantId", tenantId);
            params.put("distributionCenterId", distributionCenterId);
            List<Map> list = mappingSettingMapper.listThirdGoods(params);
            Long count = mappingSettingMapper.listThirdGoodsSum(params);
            map.put("total", count);
            map.put("rows", list);
            apiRest.setIsSuccess(true);
            apiRest.setData(map);
            apiRest.setMessage("查询第三方商品成功！");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询第三方商品失败！");
            ServiceException se = new ServiceException("1001", "查询第三方商品失败", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }*/
    public ApiRest listThirdGoods(BigInteger tenantId, String tenantCode, BigInteger distributionCenterId, String addGoodsIds, String deleteGoodIds, String goodsCodeOrName, int page, int rows) {
        long count = mappingSettingMapper.queryThirdGoodsCount(tenantId, tenantCode, distributionCenterId, addGoodsIds, deleteGoodIds, goodsCodeOrName);
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (count > 0) {
            results = mappingSettingMapper.listThirdGoods(tenantId, tenantCode, distributionCenterId, addGoodsIds, deleteGoodIds, goodsCodeOrName, (page - 1) * rows, rows);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", results);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询第三方商品成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 保存商品对应关系
     *
     * @param params
     * @return
     * */
    /*public ApiRest saveGoodsMapping(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
            params.put("tenantId", tenantId);
            params.put("distributionCenterId", distributionCenterId);
            String paramsJson = (String) params.get("paramsJson");
            JSONArray jsonArray = JSON.parseArray(paramsJson);
            List<BigInteger> goodsIds = new ArrayList();
            List<GoodsMapping> goodsMappings = new ArrayList();
            int length = jsonArray.size();
            for (int index=0; index<length; index++){
                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(index).toString());
                BigInteger ourGoodsId = BigInteger.valueOf(Long.parseLong(jsonObject.get("id").toString()));
                goodsIds.add(ourGoodsId);
                String typeId = null;
                if(jsonObject.get("typeId") != null){
                    typeId = jsonObject.get("typeId").toString();
                }
                if(typeId != null && !"".equals(typeId)){
                    GoodsMapping goodsMapping = new GoodsMapping();
                    goodsMapping.setTenantId(tenantId);
                    goodsMapping.setDistributionCenterId(distributionCenterId);
                    goodsMapping.setOurGoodsId(ourGoodsId);
                    goodsMapping.setOtherGoodsId(typeId);
                    goodsMappings.add(goodsMapping);
                }
            }

            if(goodsIds.size()>0){
                StringBuffer ids = new StringBuffer();
                for(int i=0; i<goodsIds.size(); i++){
                    ids.append(goodsIds.get(i)).append(",");
                }
                String gIds = ids.substring(0,ids.lastIndexOf(",")).toString();
                Map paramsMap = new HashMap();
                paramsMap.put("tenantId", tenantId);
                paramsMap.put("distributionCenterId", distributionCenterId);
                paramsMap.put("goodsIds", gIds);
                int count = mappingSettingMapper.deleteGoodsMapping(paramsMap);
            }

            if(goodsMappings.size()>0){
                int count = mappingSettingMapper.saveGoodsMapping(goodsMappings);
                if(count<=0){
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("保存商品对应关系失败！");
                    return apiRest;
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("保存商品对应关系成功！");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("保存商品对应关系失败！");
            ServiceException se = new ServiceException("1001", "保存商品对应关系失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }*/

    public ApiRest saveGoodsMapping(BigInteger tenantId, BigInteger distributionCenterId, String paramsJson) {
        JSONArray jsonArray = JSON.parseArray(paramsJson);
        int size = jsonArray.size();
        List<BigInteger> goodsIds = new ArrayList();
        List<GoodsMapping> goodsMappings = new ArrayList();
        for (int index = 0; index < size; index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            BigInteger ourGoodsId = jsonObject.getBigInteger("id");
            goodsIds.add(ourGoodsId);

            String typeId = jsonObject.getString("typeId");
            if (StringUtils.isNotBlank(typeId)) {
                GoodsMapping goodsMapping = new GoodsMapping();
                goodsMapping.setTenantId(tenantId);
                goodsMapping.setDistributionCenterId(distributionCenterId);
                goodsMapping.setOurGoodsId(ourGoodsId);
                String[] typeIdAndUnitId = typeId.split("_");
                goodsMapping.setOtherGoodsId(typeIdAndUnitId[0]);
                goodsMapping.setUnitId(typeIdAndUnitId[1]);
                goodsMappings.add(goodsMapping);
            }
        }
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            mappingSettingMapper.deleteGoodsMapping(tenantId, distributionCenterId, goodsIds);
        }
        if (CollectionUtils.isNotEmpty(goodsMappings)) {
            mappingSettingMapper.saveGoodsMapping(goodsMappings);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存商品对应关系成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 分页显示机构对应关系
     * @param params
     * @return
     * */
    public ApiRest listBranchMapping(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
            String tenantCode = params.get("tenantCode").toString();
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
            int rows = Integer.parseInt((String) params.get("rows"));
            int offset = (Integer.parseInt((String) params.get("page")) - 1) * rows;
            params.put("tenantId", tenantId);
            params.put("tenantCode", tenantCode);
            params.put("distributionCenterId", distributionCenterId);
            params.put("rows", rows);
            params.put("offset", offset);
            List<Map> list = mappingSettingMapper.listBranchMapping(params);
            Long count = mappingSettingMapper.listBranchMappingSum(params);
            map.put("rows", list);
            map.put("total", count);
            apiRest.setData(map);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("分页查询机构对应关系成功！");
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("分页查询机构对应关系失败！");
            ServiceException se = new ServiceException("1001", "分页查询机构对应关系失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 分页显示第三方机构
     * @param params
     * @return
     * */
    public ApiRest listThirdBranch(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
            String tenantCode = params.get("tenantCode").toString();
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String) params.get("branchId")));
            int rows = (params.get("rows")==null||params.get("rows").equals(""))?20:Integer.valueOf(params.get("rows").toString());
            int page=(params.get("page")==null||params.get("page").equals(""))?1:Integer.valueOf(params.get("page").toString());
            int offset = (page - 1) * rows;
            params.put("tenantId", tenantId);
            params.put("tenantCode", tenantCode);
            params.put("distributionCenterId", distributionCenterId);
            params.put("rows", rows);
            params.put("offset", offset);
            List<Map> list = mappingSettingMapper.listThirdBranch(params);
            Long count = mappingSettingMapper.listThirdBranchSum(params);
            map.put("rows", list);
            map.put("total", count);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询第三方机构成功！");
            apiRest.setData(map);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询第三方机构失败！");
            ServiceException se = new ServiceException("1001", "查询第三方机构失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }

    /**
     * 保存机构对应关系
     * @param params
     * @return
     * */
    public ApiRest saveBranchMapping(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
            BigInteger distributionCenterId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
            String paramsJson = params.get("paramsJson").toString();
            JSONArray  jsonArray = JSON.parseArray(paramsJson);
            List<BigInteger> branchIds = new ArrayList();
            List<BranchMapping> branchMappings = new ArrayList();
            int length = jsonArray.size();
            for(int index=0; index<length; index++){
                JSONObject jsonObject = JSON.parseObject(jsonArray.get(index).toString());
                BigInteger ourBranchId = BigInteger.valueOf(Long.parseLong(jsonObject.get("id").toString()));
                branchIds.add(ourBranchId);

                String typeId = null;
                if(jsonObject.get("typeId") != null){
                    typeId = jsonObject.get("typeId").toString();
                }
                if(typeId != null && !"".equals(typeId)){
                    BranchMapping branchMapping = new BranchMapping();
                    branchMapping.setTenantId(tenantId);
                    branchMapping.setDistributionCenterId(distributionCenterId);
                    branchMapping.setOurBranchId(ourBranchId);
                    branchMapping.setOtherBranchId(typeId);
                    branchMappings.add(branchMapping);
                }
            }

            if(branchIds.size()>0){
                StringBuffer ids = new StringBuffer();
                for(int i=0; i<branchIds.size(); i++){
                    ids.append(branchIds.get(i)).append(",");
                }
                String bIds = ids.substring(0,ids.lastIndexOf(","));
                Map paramsMap = new HashMap();
                paramsMap.put("tenantId", tenantId);
                paramsMap.put("distributionCenterId", distributionCenterId);
                paramsMap.put("branchIds", bIds);
                int count = mappingSettingMapper.deleteBranchMapping(paramsMap);
            }

            if(branchMappings.size()>0){
                int count = mappingSettingMapper.saveBranchMapping(branchMappings);
                if(count<=0){
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("保存机构对应关系时失败！");
                    return apiRest;
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("保存机构对应关系成功！");
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("保存机构对应关系失败！");
            ServiceException se = new ServiceException("1001", "保存机构对应关系失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }
}

