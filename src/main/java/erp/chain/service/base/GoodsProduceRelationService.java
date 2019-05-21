package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Goods;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GoodsProduceRelationMapper;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

import static net.sf.json.JSONObject.fromObject;

/**
 * 加工配方
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsProduceRelationService{
    @Autowired
    private GoodsProduceRelationMapper goodsProduceRelationMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 查询加工商品
     *
     * @param params
     * @return
     */
    public ApiRest queryProduceGoods(Map params){
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
        if("-1".equals(params.get("categoryId"))){
            params.put("categoryId", null);
        }
        List<Map> list = goodsProduceRelationMapper.queryProduceGoods(params);
        Long count = goodsProduceRelationMapper.queryProduceGoodsSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询加工商品成功！");
        return apiRest;
    }

    /**
     * 查询加工商品
     *
     * @param params
     * @return
     */
    public ApiRest getRelationById(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        Map map = new HashMap();
        List<Map> list = goodsProduceRelationMapper.getRelationById(params);
        Long count = goodsProduceRelationMapper.getRelationByIdSum(params);
        params.put("id", params.get("produceGoodsId"));
        Goods good = goodsMapper.findGoodsByIdN(params);
        map.put("total", count);
        map.put("rows", list);
        map.put("goodData", good);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询加工商品组合明细成功！");
        return apiRest;
    }

    /**
     * 保存对应关系
     *
     * @param params
     * @return
     */
    public ApiRest saveGoodsRelation(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger produceGoodsId = BigInteger.valueOf(Long.parseLong((String)params.get("produceGoodsId")));
        JSONObject relation = fromObject(params.get("relation"));
        Map produceMap = new HashMap();
        produceMap.put("id", produceGoodsId);
        produceMap.put("tenantId", tenantId);
        Goods produceGoods = goodsMapper.findGoodsByIdAndTenantId(produceMap);
        if(produceGoods == null || (produceGoods.getCombinationType() != 1 && produceGoods.getCombinationType() != 2 && produceGoods.getCombinationType() != 3)){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询加工商品信息失败！");
            return apiRest;
        }
        Map goodsMap = new HashMap();
        int goodsCount = 0;
        if(relation.get("relation") == null || relation.get("relation").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setError("json参数错误，请确认参数准确！");
            return apiRest;
        }
        for(Map json : (List<Map>)relation.getJSONArray("relation")){
            if(!(json.get("goodsId") != null && json.get("goodsId") != "" && json.get("quantity") != null && json.get("quantity") != "")){
                apiRest.setIsSuccess(false);
                apiRest.setError("json参数错误，请确认参数准确！");
                return apiRest;
            }
        }
        for(Map json : (List<Map>)relation.getJSONArray("relation")){
            goodsMap.put(json.get("goodsId"), json.get("goodsId"));
            Map goMap = new HashMap();
            goMap.put("id", json.get("goodsId"));
            goMap.put("tenantId", tenantId);
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(goMap);
            if(goods == null){
                goodsCount++;
            }
            else if(goods.getId().equals(produceGoodsId)){
                apiRest.setIsSuccess(false);
                apiRest.setError("明细不能包括自身商品！");
                return apiRest;
            }
        }
        if(goodsMap.size() < relation.getJSONArray("relation").size()){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在重复商品，保存失败！");
            return apiRest;
        }
        if(goodsCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在未查询到的商品，保存失败！");
            return apiRest;
        }
        if(goodsProduceRelationMapper.getRelationByIdSum(params) > 0){
            int i = goodsProduceRelationMapper.deleteByProduceGoodsId(produceGoodsId);
            if(i < goodsProduceRelationMapper.getRelationByIdSum(params)){
                apiRest.setIsSuccess(false);
                apiRest.setError("删除原明细失败！");
                return apiRest;
            }
        }
        List<Map> relationList = new ArrayList<>();
        for(Map json : (List<Map>)relation.getJSONArray("relation")){
            Map insertMap = new HashMap();
            insertMap.put("id", null);
            insertMap.put("produceGoodsId", produceGoodsId);
            insertMap.put("goodsId", json.get("goodsId"));
            insertMap.put("branchId", branchId);
            insertMap.put("tenantId", tenantId);
            insertMap.put("quantity", json.get("quantity"));
            insertMap.put("isDeleted", false);
            insertMap.put("createAt", new Date());
            insertMap.put("createBy", "System");
            insertMap.put("lastUpdateAt", new Date());
            insertMap.put("lastUpdateBy", "System");
            insertMap.put("memo", "");
            insertMap.put("version", 0);
            relationList.add(insertMap);
        }
        if(relationList.size() > 0){
            int k = goodsProduceRelationMapper.insertRelation(relationList);
            if(k < relationList.size()){
                apiRest.setIsSuccess(false);
                apiRest.setError("添加失败！");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存加工配方成功！");
        Map map = new HashMap();
        List<Map> list = goodsProduceRelationMapper.getRelationById(params);
        Long count = goodsProduceRelationMapper.getRelationByIdSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setData(map);
        return apiRest;
    }




    /**
     * 删除加工配方商品
     *
     * @param params
     * @return
     */
    public ApiRest doDeleteRelationById(Map params){

        ApiRest apiRest = new ApiRest();
        BigInteger produceGoodsId = BigInteger.valueOf(Long.parseLong((String)params.get("produceGoodsId")));

       int flag1 = goodsProduceRelationMapper.doDeleteRelationGoodsById(produceGoodsId);
       int flag2 = goodsProduceRelationMapper.deleteByProduceGoodsId(produceGoodsId);
       if(flag1 > 0 && flag2 >=0){
           apiRest.setIsSuccess(true);
           apiRest.setMessage("删除成功!");
       }else{
           apiRest.setIsSuccess(false);
           apiRest.setMessage("删除失败!");
       }
        return apiRest;
    }

}
