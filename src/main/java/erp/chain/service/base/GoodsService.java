package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PinYinHelper;
import erp.chain.domain.*;
import erp.chain.domain.supply.store.Store;
import erp.chain.domain.supply.store.StoreAccount;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.*;
import erp.chain.mapper.supply.store.StoreMapper;
import erp.chain.mapper.system.OperateLogMapper;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.service.BaseService;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品/菜品
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsService extends BaseService{
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private GoodsUnitMapper goodsUnitMapper;
    @Autowired
    private GoodsSpecRMapper goodsSpecRMapper;
    @Autowired
    private PublicWSService publicWSService;
    @Autowired
    private OperateLogMapper operateLogMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private PackageService packageService;
    @Autowired
    private GroupGoodsMapper groupGoodsMapper;
    @Autowired
    private GoodsUnitService goodsUnitService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StoreMapper storeMapper;

    /**
     * 查询goods列表
     *
     * @param params
     * @return
     */
    public ApiRest queryGoodsList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        if(params.get("branchIds") != null && !"".equals(params.get("branchIds"))){
            params.put("onlySelf", onlySelf);
            if(params.get("headOffice") != null && "true".equals(params.get("headOffice"))){
                Branch branch = branchMapper.findHeadquartersBranch(tenantId);
                String branchIds = params.get("branchIds").toString();
                branchIds = branchIds + "," + branch.getId();
                params.put("branchIds", branchIds);
            }
        }
        else{
            params.put("onlySelf", onlySelf);
        }
        Map map = new HashMap();
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field").toString()));
            params.put("order", params.get("order").toString());
        }
        if(params.get("rows") != null && !params.get("rows").equals("") && params.get("page") != null && !params.get("page").equals("")){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        params.put("tenantId", tenantId);
        params.put("branchId", branchId);
        if("".equals(params.get("categoryId")) || "-1".equals(params.get("categoryId"))){
            params.put("categoryId", null);
        }
        if(!StringUtils.isEmpty((String) params.get("barCode"))){
            params.remove("goodsCodeOrName");
        }
        List<Map> list = goodsMapper.queryGoodsList(params);
        Long count = goodsMapper.queryGoodsListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询goods列表成功！");
        return apiRest;
    }

    /**
     * 查询有库存的菜品
     *
     * @param params
     * @return
     */
    public ApiRest queryGoodsWithStoreInfoList(Map params){
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
        List<Map> list = goodsMapper.queryGoodsWithStoreInfoList(params);
        Long count = goodsMapper.queryGoodsWithStoreInfoListSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询goods列表成功！");
        return apiRest;
    }

    /**
     * 根据ID查询菜品档案
     *
     * @param params
     * @return
     */
    public ApiRest queryGoodsById(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> goodsList = goodsMapper.findGoodsWithSpec(params);
        Map goods = goodsList.get(0);
        String groupId = "";
        String groupName = "";
        if(goods.get("groupId") != null && goods.get("groupName") != null){
            for(int j = 0; j < goodsList.size(); j++){
                Map good = goodsList.get(j);
                groupId += good.get("groupId") + ",";
                groupName += good.get("groupName") + ",";
            }
        }
        if(groupId.indexOf(",") > -1 && groupName.indexOf(",") > -1){
            goods.put("groupId", groupId.substring(0, groupId.lastIndexOf(",")));
            goods.put("groupName", groupName.substring(0, groupName.lastIndexOf(",")));
        }
        else{
            goods.put("groupId", groupId);
            goods.put("groupName", groupName);
        }
        String parentId = goods.get("id").toString();
        params.put("parentId", parentId);
        List<Map> specGoods = goodsMapper.querySpecGoods(params);
        List<Map> specList = new ArrayList();
        for(int i = 0; i < specGoods.size(); i++){
            Map specMap = new HashMap();
            Map map = specGoods.get(i);
            specMap.put("specId", map.get("id"));
            specMap.put("specStandardName", map.get("standardName"));
            specMap.put("specBarCode", map.get("barCode"));
            specMap.put("specPurchasingPrice", map.get("purchasingPrice"));
            specMap.put("specSalePrice", map.get("salePrice"));
            specMap.put("specVipPrice", map.get("vipPrice"));
            specMap.put("specShippingPrice", map.get("shippingPrice1"));
            specList.add(specMap);
        }
        goods.put("specList", specList);
        Map queryMap = new HashMap();
        queryMap.put("tenantId", params.get("tenantId"));
        queryMap.put("goodsId", goods.get("id").toString());
        List<Map> packageInfo = goodsMapper.queryPackageByGoodsId(queryMap);
        goods.put("packageInfo", packageInfo);
        if(goods == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询商品失败！");
        }
        else{
            apiRest.setIsSuccess(true);
            apiRest.setData(goods);
            apiRest.setMessage("查询商品成功！");
        }
        return apiRest;
    }

    /**
     * 根据ID删除菜品档案
     *
     * @param params
     * @return
     */
    public ApiRest delGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        ApiRest re = SaaSApi.findTenantById(tenantId);
        if(!re.getIsSuccess() || re.getData() == null){
            return re;
        }
        String business = ((Map)((Map)re.getData()).get("tenant")).get("business1").toString();
        String businessName = "";
        if(business.equals("1")){
            businessName = "商品";
        }
        else if(business.equals("2")){
            businessName = "商品";
        }
        String ids = "";
        if(params.get("id") != null && !params.get("id").equals("")){
            ids = params.get("id").toString();
        }
        if(params.get("ids") != null && !params.get("ids").equals("")){
            ids = params.get("ids").toString();
        }
        Map resultMap = new HashMap();
        int count = 0;
        int errorCount = 0;
        List<Goods> goodsList = new ArrayList<>();
        List<Map> errorList = new ArrayList<>();
        //库存
        QueryGoodsStoreModel model = new QueryGoodsStoreModel();
        model.setTenantId(tenantId);
        model.setBranchId(branchId);
        model.setIds(ids);
        List<Map> quantity = goodsMapper.sumGoodsStore(model);
        Map errorMap = new HashMap();
        Map goodsMap = new HashMap();
        for(int q = 0; q < quantity.size(); q++){
            Map qMap = quantity.get(q);
            if(BigDecimal.valueOf(0L).compareTo(BigDecimal.valueOf(Double.parseDouble(qMap.get("quantity").toString()))) == -1){
                errorCount++;
                errorMap.put("id", qMap.get("goodsId"));
                /*errorMap.put("goodsName",goods.getGoodsName());*/
                errorMap.put("mes", businessName + "中有库存，删除失败！");
                errorList.add(errorMap);
                apiRest.setIsSuccess(false);
                apiRest.setError(businessName + "中有库存，删除失败！");
                ids = ids.replace(" ", "");
                ids = ids.replace(qMap.get("goodsId").toString(), "");
                ids = ids.replace(",,", ",");
                if(ids.startsWith(",")){
                    ids = ids.substring(1);
                }
                if(ids.endsWith(",")){
                    ids = ids.substring(0,ids.length() - 1);
                }
                continue;
            }
        }
        //套餐
        if(ids != null && !"".equals(ids) && !",".equals(ids)){
            if(",".equals(ids.substring(0, 1))){
                ids = ids.substring(1, ids.length());
            }
            if(",".equals(ids.substring(ids.length() - 1, ids.length()))){
                ids = ids.substring(0, ids.length() - 1);
            }
            ids = ids.replace(",,", ",");
            model.setIds(ids);
            List<Map> groupCount = goodsMapper.countGroupGoods(model);
            for(int w = 0; w < groupCount.size(); w++){
                Map gMap = groupCount.get(w);
                if(Integer.parseInt(gMap.get("count").toString()) > 0){
                    errorCount++;
                    errorMap.put("id", gMap.get("goodsId"));
                    /*errorMap.put("goodsName",goods.getGoodsName());*/
                    errorMap.put("mes", businessName + "是套餐" + businessName + "，删除失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError(businessName + "是套餐" + businessName + "，删除失败！");
                    ids = ids.replace(" ", "");
                    ids = ids.replace(gMap.get("goodsId").toString(), "");
                    ids = ids.replace(",,", ",");
                    if(ids.startsWith(",")){
                        ids = ids.substring(1);
                    }
                    if(ids.endsWith(",")){
                        ids = ids.substring(0,ids.length() - 1);
                    }
                    continue;
                }
            }
        }

        //菜牌
        if(ids != null && !"".equals(ids) && !",".equals(ids)){
            if(",".equals(ids.substring(0, 1))){
                ids = ids.substring(1, ids.length());
            }
            if(",".equals(ids.substring(ids.length() - 1, ids.length()))){
                ids = ids.substring(0, ids.length() - 1);
            }
            ids = ids.replace(",,", ",");
            model.setIds(ids);
            List<Map> menuGoodsCount = goodsMapper.countMenuGoods(model);
            for(int e = 0; e < menuGoodsCount.size(); e++){
                Map mMap = menuGoodsCount.get(e);
                if(Integer.parseInt(mMap.get("count").toString()) > 0){
                    errorCount++;
                    errorMap.put("id", mMap.get("goodsId"));
                    /*errorMap.put("goodsName",goods.getGoodsName());*/
                    errorMap.put("mes", businessName + "存在菜牌中，删除失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError(businessName + "存在菜牌中，删除失败！");
                    ids = ids.replace(" ", "");
                    ids = ids.replace(mMap.get("goodsId").toString(), "");
                    ids = ids.replace(",,", ",");
                    if(ids.startsWith(",")){
                        ids = ids.substring(1);
                    }
                    if(ids.endsWith(",")){
                        ids = ids.substring(0,ids.length() - 1);
                    }
                    continue;
                }
            }
        }

        //促销
        if(ids != null && !"".equals(ids) && !",".equals(ids)){
            if(",".equals(ids.substring(0, 1))){
                ids = ids.substring(1, ids.length());
            }
            if(",".equals(ids.substring(ids.length() - 1, ids.length()))){
                ids = ids.substring(0, ids.length() - 1);
            }
            ids = ids.replace(",,", ",");
            model.setIds(ids);
            List<Map> dietCount = goodsMapper.countDiet(model);
            for(int r = 0; r < dietCount.size(); r++){
                Map dMap = dietCount.get(r);
                if(Integer.parseInt(dMap.get("count").toString()) > 0){
                    errorCount++;
                    errorMap.put("id", dMap.get("goodsId"));
                    /*errorMap.put("goodsName",goods.getGoodsName());*/
                    errorMap.put("mes", businessName + "存在促销，删除失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError(businessName + "存在促销，删除失败！");
                    ids = ids.replace(" ", "");
                    ids = ids.replace(dMap.get("goodsId").toString(), "");
                    ids = ids.replace(",,", ",");
                    if(ids.startsWith(",")){
                        ids = ids.substring(1);
                    }
                    if(ids.endsWith(",")){
                        ids = ids.substring(0,ids.length() - 1);
                    }
                    continue;
                }
            }
        }

        //加工配方
        if(ids != null && !"".equals(ids) && !",".equals(ids)){
            if(",".equals(ids.substring(0, 1))){
                ids = ids.substring(1, ids.length());
            }
            if(",".equals(ids.substring(ids.length() - 1, ids.length()))){
                ids = ids.substring(0, ids.length() - 1);
            }
            ids = ids.replace(",,", ",");
            model.setIds(ids);
            List<Map> relationCount = goodsMapper.countRelation(model);
            for(int t = 0; t < relationCount.size(); t++){
                Map rMap = relationCount.get(t);
                if(Integer.parseInt(rMap.get("count").toString()) > 0){
                    errorCount++;
                    errorMap.put("id", rMap.get("goodsId"));
                    /*errorMap.put("goodsName",goods.getGoodsName());*/
                    errorMap.put("mes", businessName + "存在加工配方明细，删除失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError(businessName + "存在加工配方明细，删除失败！");
                    ids = ids.replace(" ", "");
                    ids = ids.replace(rMap.get("goodsId").toString(), "");
                    ids = ids.replace(",,", ",");
                    if(ids.startsWith(",")){
                        ids = ids.substring(1);
                    }
                    if(ids.endsWith(",")){
                        ids = ids.substring(0,ids.length() - 1);
                    }
                    continue;
                }
            }
        }

        /*验证并删除子规格商品*/
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        map.put("branchId", branchId);
        if(ids != null && !"".equals(ids) && !",".equals(ids)){
            List<Map> specGoods = goodsMapper.querySpecGoodsIds(tenantId, ids);
            for(int a = 0; a < specGoods.size(); a++){
                Map specMap = specGoods.get(a);
                String specIds = specMap.get("specIds").toString();
                QueryGoodsStoreModel specModel = new QueryGoodsStoreModel();
                specModel.setTenantId(tenantId);
                specModel.setIds(specIds);
                specModel.setBranchId(branchId);

                //库存
                List<Map> specQuantity = goodsMapper.sumGoodsStore(specModel);
                for(int z = 0; z < specQuantity.size(); z++){
                    Map sqMap = specQuantity.get(z);
                    if(BigDecimal.valueOf(0L).compareTo(BigDecimal.valueOf(Double.parseDouble(sqMap.get("quantity").toString()))) == -1){
                        errorMap.put("id", sqMap.get("goodsId"));
                        /*errorMap.put("goodsName",specGood.get("goodsName"));*/
                        errorMap.put("mes", "子规格中有库存，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("子规格中有库存，删除失败！");
                        ids = ids.replace(" ", "");
                        ids = ids.replace(specMap.get("goodsId").toString(), "");
                        ids = ids.replace(",,", ",");
                        if(ids.startsWith(",")){
                            ids = ids.substring(1);
                        }
                        if(ids.endsWith(",")){
                            ids = ids.substring(0,ids.length() - 1);
                        }
                        break;
                    }
                }

                //套餐
                List<Map> specGroupCount = goodsMapper.countGroupGoods(specModel);
                for(int x = 0; x < specGroupCount.size(); x++){
                    Map sgMap = specGroupCount.get(x);
                    if(Integer.parseInt(sgMap.get("count").toString()) > 0){
                        errorMap.put("id", sgMap.get("goodsId"));
                        /*errorMap.put("goodsName",specGood.get("goodsName"));*/
                        errorMap.put("mes", "子规格是套餐，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("子规格是套餐，删除失败！");
                        ids = ids.replace(" ", "");
                        ids = ids.replace(specMap.get("goodsId").toString(), "");
                        ids = ids.replace(",,", ",");
                        if(ids.startsWith(",")){
                            ids = ids.substring(1);
                        }
                        if(ids.endsWith(",")){
                            ids = ids.substring(0,ids.length() - 1);
                        }
                        break;
                    }
                }

                //菜牌
                List<Map> specMenuGoodsCount = goodsMapper.countMenuGoods(specModel);
                for(int c = 0; c < specMenuGoodsCount.size(); c++){
                    Map smMap = specMenuGoodsCount.get(c);
                    if(Integer.parseInt(smMap.get("count").toString()) > 0){
                        errorMap.put("id", smMap.get("goodsId"));
                        /*errorMap.put("goodsName",specGood.get("goodsName"));*/
                        errorMap.put("mes", "子规格存在菜牌中，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("子规格存在菜牌中，删除失败！");
                        ids = ids.replace(" ", "");
                        ids = ids.replace(specMap.get("goodsId").toString(), "");
                        ids = ids.replace(",,", ",");
                        if(ids.startsWith(",")){
                            ids = ids.substring(1);
                        }
                        if(ids.endsWith(",")){
                            ids = ids.substring(0,ids.length() - 1);
                        }
                        break;
                    }
                }

                //促销
                List<Map> specDietCount = goodsMapper.countDiet(specModel);
                for(int v = 0; v < specDietCount.size(); v++){
                    Map sdMap = specDietCount.get(v);
                    if(Integer.parseInt(sdMap.get("count").toString()) > 0){
                        errorMap.put("id", sdMap.get("goodsId"));
                        /*errorMap.put("goodsName",specGood.get("goodsName"));*/
                        errorMap.put("mes", "子规格存在促销，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("子规格存在促销，删除失败！");
                        ids = ids.replace(" ", "");
                        ids = ids.replace(specMap.get("goodsId").toString(), "");
                        ids = ids.replace(",,", ",");
                        if(ids.startsWith(",")){
                            ids = ids.substring(1);
                        }
                        if(ids.endsWith(",")){
                            ids = ids.substring(0,ids.length() - 1);
                        }
                        break;
                    }
                }

                //加工配方
                List<Map> specRelationCount = goodsMapper.countRelation(specModel);
                for(int b = 0; b < specRelationCount.size(); b++){
                    Map srMap = specRelationCount.get(b);
                    if(Integer.parseInt(srMap.get("count").toString()) > 0){
                        errorMap.put("id", srMap.get("goodsId"));
                        /*errorMap.put("goodsName",specGood.get("goodsName"));*/
                        errorMap.put("mes", "子规格存在加工配方明细，删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("子规格存在加工配方明细，删除失败！");
                        ids = ids.replace(" ", "");
                        ids = ids.replace(specMap.get("goodsId").toString(), "");
                        ids = ids.replace(",,", ",");
                        if(ids.startsWith(",")){
                            ids = ids.substring(1);
                        }
                        if(ids.endsWith(",")){
                            ids = ids.substring(0,ids.length() - 1);
                        }
                        break;
                    }
                }
            }
        }

        String newIds = "";
        for(String id : ids.split(",")){
            if(id != null && !"".equals(id)){
                goodsMap.put("id", id);
                goodsMap.put("tenantId", tenantId);
                Goods goods = goodsMapper.findGoodsByIdAndTenantId(goodsMap);
                if(goods == null){
                    errorCount++;
                    errorMap.put("id", id);
                    errorMap.put("mes", "查询" + businessName + "失败！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询" + businessName + "失败！");
                    continue;
                }
                else{
                    int x = goods.getGoodsName().indexOf("美团外卖");
                    int y = goods.getGoodsName().indexOf("饿了么");
                    if(x >= 0 || y >= 0){
                        errorCount++;
                        errorMap.put("id", goods.getId());
                        errorMap.put("goodsName", goods.getGoodsName());
                        errorMap.put("mes", "商品名中含有‘美团外卖’，‘饿了么’字样，不能删除！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setError("商品名中含有‘美团外卖’，‘饿了么’字样，不能删除！");
                        continue;//return apiRest;
                    }
                    else{
                        newIds = newIds + id + ",";
                        goodsList.add(goods);
                    }
                }
            }
        }

        if(ids != null && !"".equals(ids) && !",".equals(ids) && !"".equals(newIds)){
            Map delParams = new HashMap();
            delParams.put("ids", newIds.substring(0, newIds.lastIndexOf(",")));
            delParams.put("tenantId", tenantId);
                /*goods.setIsDeleted(true);
                goods.setLastUpdateAt(new Date());
                goods.setLastUpdateBy("System");*/
            Long i = goodsMapper.delGoods(delParams);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("删除成功！");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("删除失败！");
            }
            goodsMapper.delStoreOrderDetailOfGoods(delParams);
            goodsMapper.delLyStoreOrderDetailOfGoods(delParams);
            goodsMapper.delCheckOrderDetailOfGoods(delParams);
            goodsMapper.delPsStoreOrderDetailOfGoods(delParams);
            goodsMapper.delSyStoreOrderDetailOfGoods(delParams);
            ApiRest r = tenantConfigService.minusTenantConfigValue(tenantId, SysConfig.SYS_GOODS_NUM, i);
            if(!r.getIsSuccess()){
                apiRest = r;
            }
            if(i >= 0){
                apiRest.setIsSuccess(true);
                if("5".equals(params.get("business1"))){
                    apiRest.setMessage("成功删除" + i + "个商品，未删除商品存在库存/组合商品/促销/加工配方明细");
                }
                else{
                    apiRest.setMessage("成功删除" + i + "个商品，未删除商品存在库存/套餐/菜牌/促销/加工配方明细");
                }
            }
            if(errorList.size() > 0){
                apiRest.setError("");
                for(int f = 0; f < errorList.size(); f++){
                    apiRest.setError("id为" + errorList.get(f).get("id") + "的商品：" + errorList.get(f).get("mes") + "。");
                }
            }
            resultMap.put("num", i);
            resultMap.put("errors", errorList);
            resultMap.put("delList", goodsList);
        }
        else{
            resultMap.put("num", 0);
            resultMap.put("errors", new ArrayList());
            resultMap.put("delList", new ArrayList());
            apiRest.setIsSuccess(true);
            if("5".equals(params.get("business1"))){
                apiRest.setMessage("成功删除0个商品，未删除商品存在库存/组合商品/促销/加工配方明细");
            }
            else{
                apiRest.setMessage("成功删除0个商品，未删除商品存在库存/套餐/菜牌/促销/加工配方明细");
            }
        }

        apiRest.setData(resultMap);
        return apiRest;
    }

    /**
     * 获取编码
     *
     * @param params
     * @return
     */
    public ApiRest getGoodsCode(Map params){
        ApiRest apiRest = new ApiRest();
        params.put("id", params.get("categoryId"));
        Category category = categoryMapper.queryCatById(params);
        if(category != null){
            params.put("catCode", category.getCatCode());
            String gc = goodsMapper.getGoodsCode(params);
            if(gc == null || gc.equals("")){
                String code = category.getCatCode();
                if(code != null && !code.equals("") && code.length() == 2){
                    gc = code + "000000";
                }
                else{
                    gc = code + "0000";
                }
            }
            if(gc.substring(gc.length() - 4 ,gc.length()).equals("9999")){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("生成编码成功！");
                if(gc.length() > 8){
                    int index = Integer.parseInt(gc.substring(4, gc.length())) + 1;
                    apiRest.setData(gc.substring(0,4) + index);
                }else{
                    apiRest.setData(gc.substring(0,4)+"10000");
                }

                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("生成编码成功！");
            apiRest.setData(SerialNumberGenerate.nextGoodsCode(gc));
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询分类失败！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 获取编码
     *
     * @return
     */
    public String getinputGoodsCode(String nowCode, int i){
        String code = "";
        try{
            String gc = "";
            if(nowCode == null || nowCode.equals("")){
                gc = "98000000";
            }
            else{
                gc = nowCode;
            }
            code = (BigInteger.valueOf(Long.valueOf(gc)).add(BigInteger.valueOf(i))).toString();
        }
        catch(Exception e){
            ServiceException se = new ServiceException("1005", "获取编码失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return code;
    }

    /**
     * 修改商品积分
     *
     * @return
     */
    public ApiRest updateGoodsScore(Map params){
        ApiRest apiRest = new ApiRest();
        Map<String, Object> map = new HashMap<>();
        map.put("id", params.get("id"));
        if(params.get("scoreType") != null && !"".equals(params.get("scoreType"))){
            map.put("scoreType", params.get("scoreType"));
        }
        if(params.get("scoreValue") != null && !"".equals(params.get("scoreValue"))){
            map.put("scoreValue", params.get("scoreValue"));
        }
        if(params.get("scorePercent") != null && !"".equals(params.get("scorePercent"))){
            map.put("scorePercent", params.get("scorePercent"));
        }
        Goods g = goodsMapper.findGoodsByIdAndTenantId(params);
        if(g == null){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询商品失败！");
            apiRest.setError("查询商品失败！");
            return apiRest;
        }
        map.put("tenantId", g.getTenantId());
        map.put("branchId", g.getBranchId());
        map.put("goodsId", g.getId());
        goodsMapper.deleteGoodsScore(g.getTenantId(), g.getId());
        goodsMapper.saveGoodsScore(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("更新商品成功！");
        return apiRest;
    }


    /**
     * 新增或修改菜品档案
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        if(params.get("isAllowChangePrice")==null){
            params.put("isAllowChangePrice","0");
        }
        if(params.get("isAllowVipDiscount")==null){
            params.put("isAllowVipDiscount","0");
        }
        //App端使用
        if(null !=params.get("isForPoints")&&"0".equals(params.get("isForPoints"))){
            params.put("isAllowVipDiscount","0");
        }
        if(params.get("goods") != null && !params.get("goods").equals("")){
            JSONObject goodsJson = JSONObject.fromObject(params.get("goods"));
            BigInteger id = BigInteger.valueOf(Long.valueOf(goodsJson.get("id").toString()));
            BigInteger categoryId = BigInteger.valueOf(Long.valueOf(goodsJson.get("categoryId").toString()));
            String barCode = goodsJson.get("barCode").toString();
            String goodsName = goodsJson.get("goodsName").toString();
            BigInteger goodsUnitId = BigInteger.valueOf(Long.valueOf(goodsJson.get("goodsUnitId").toString()));
            BigDecimal salePrice = BigDecimal.valueOf(Double.valueOf(goodsJson.get("salePrice").toString()));
            BigDecimal vipPrice = BigDecimal.valueOf(Double.valueOf(goodsJson.get("vipPrice1").toString()));
            BigDecimal purchasingPrice = BigDecimal.valueOf(Double.valueOf(goodsJson.get("purchasingPrice").toString()));
            Integer goodsStatus = Integer.valueOf(goodsJson.get("goodsStatus").toString());
            boolean isStore = Boolean.valueOf(goodsJson.get("isStore").toString());
            boolean isDsc = Boolean.valueOf(goodsJson.get("isDsc").toString());
            boolean isWeigh = Boolean.valueOf(goodsJson.get("isWeigh").toString());
            boolean isCustomPrice = Boolean.valueOf(goodsJson.get("isCustomPrice") == null ? "0" : goodsJson.get("isCustomPrice").toString());
            boolean isAllowChangePrice =Boolean.valueOf(goodsJson.get("isAllowChangePrice")==null ?"0":goodsJson.get("isAllowChangePrice").toString());
            boolean isAllowVipDiscount =Boolean.valueOf(goodsJson.get("isAllowVipDiscount")==null ?"0":goodsJson.get("isAllowVipDiscount").toString());
            String standardName = "";
            if(params.get("standardName") != null && "".equals(params.get("standardName"))){
                standardName = params.get("standardName").toString();
            }

            /*if(vipPrice.compareTo(purchasingPrice) < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("会员价不能低于进价");
                return apiRest;
            }*/
            /*新增库存管理*/
            BigDecimal storeUpLimit = BigDecimal.ZERO;
            if(params.get("storeUpLimit") != null && !"".equals(params.get("storeUpLimit").toString())){
                storeUpLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeUpLimit").toString()));
            }
            BigDecimal storeLowLimit = BigDecimal.ZERO;
            if(params.get("storeLowLimit") != null && !"".equals(params.get("storeLowLimit").toString())){
                storeUpLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeLowLimit").toString()));
            }
            List<Map> specsList = new ArrayList<>();
            if(params.get("specsList") != null && "".equals(params.get("specsList"))){
                specsList = (List<Map>)params.get("specsList");
            }
            Map goodsMap = new HashMap();
            goodsMap.put("id", id);
            goodsMap.put("tenantId", tenantId);
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(goodsMap);
            if(goods == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到需要修改的商品");
                return apiRest;
            }
            Map unitMap = new HashMap();
            unitMap.put("id", goodsUnitId);
            unitMap.put("tenantId", tenantId);
            GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
            int x = goodsName.indexOf("美团外卖");
            int y = goodsName.indexOf("饿了么");
            if(x >= 0 || y >= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("商品名中含有‘美团外卖’，‘饿了么’字样，不能新增！");
                return apiRest;
            }
            if(goodsUnit == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询单位失败！");
                return apiRest;
            }
            Map catMap = new HashMap();
            catMap.put("id", categoryId);
            catMap.put("tenantId", tenantId);
            Category cat = categoryMapper.queryCatById(catMap);
            if(cat == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询分类失败！");
                return apiRest;
            }
            goods.setGoodsName(goodsName);
            goods.setBarCode(barCode);
            goods.setCategoryId(categoryId);
            goods.setMnemonic(createMnemonic(goodsName));
            goods.setGoodsUnitId(goodsUnitId);
            goods.setGoodsUnitName(goodsUnit.getUnitName());
            goods.setSalePrice(salePrice);
            goods.setVipPrice(vipPrice);
            goods.setVipPrice2(vipPrice);
            goods.setVipPrice1(vipPrice);
            goods.setPurchasingPrice(purchasingPrice);
            goods.setCategoryName(cat.getCatName());
            goods.setIsAllowChangePrice(isAllowChangePrice);
            goods.setIsAllowVipDiscount(isAllowVipDiscount);
            if(goods.getCombinationType() == 1 || goods.getCombinationType() == 2){
                goods.setIsStore(true);
            }
            else{
                goods.setIsStore(isStore);
            }
            goods.setGoodsType(1);
            goods.setIsForPoints(isDsc);
            goods.setIsDsc(isDsc);
            goods.setIsChangeprice(isCustomPrice);
            goods.setIsCustomPrice(isCustomPrice);
            goods.setIsWeigh(isWeigh);

            goods.setGoodsStatus(goodsStatus);
            goods.setLastUpdateAt(new Date());
            goods.setLastUpdateBy("System");
            goods.setVersion(goods.getVersion().add(BigInteger.ONE));
            goods.setStandardName(standardName);
            goods.setStoreUpLimit(storeUpLimit);
            goods.setStoreLowLimit(storeLowLimit);
            int i = goodsMapper.update(goods);
            /*修改子规格*/
            if(specsList != null && specsList.size() > 0){
                for(int j = 0; j < specsList.size(); j++){
                    Map<String, String> specMap = specsList.get(j);
                    String specId = specMap.get("specId");
                    String specStandardName = specMap.get("spceStandardName");
                    BigDecimal specSalePrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specSalePrice")));
                    BigDecimal specVipPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specVipPrice")));
                    BigDecimal specShippingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specShippingPrice")));
                    BigDecimal specPurchasingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specPurchasingPrice")));
                    Goods goods1 = new Goods();
                    if(specId == null || "".equals(specId)){
                        goods1.setStandardName(specStandardName);
                        goods1.setSalePrice(specSalePrice);
                        goods1.setVipPrice(specVipPrice);
                        goods1.setVipPrice1(specVipPrice);
                        goods1.setVipPrice2(specVipPrice);
                        goods1.setShippingPrice1(specShippingPrice);
                        goods1.setShippingPrice2(specShippingPrice);
                        goods1.setPurchasingPrice(specPurchasingPrice);
                        goods1.setParentId(id);
                        goods1.setGoodsCode(goods.getGoodsCode());
                        goods1.setGoodsName(goods.getGoodsName());
                        goods1.setCategoryId(goods.getCategoryId());
                        goods1.setCategoryName(goods.getCategoryName());
                        goods1.setTenantId(goods.getTenantId());
                        goods1.setBranchId(goods.getBranchId());
                        goods1.setMnemonic(goods.getMnemonic());
                        goods1.setPhoto(goods.getPhoto());
                        goods1.setSpecGroupId(goods.getSpecGroupId());
                        goods1.setGoodsUnitId(goods.getGoodsUnitId());
                        goods1.setGoodsUnitName(goods.getGoodsUnitName());
                        goods1.setIsPricetag(goods.getIsPricetag());
                        goods1.setIsStore(goods.getIsStore());
                        goods1.setCombinationType(goods.getCombinationType());
                        goods1.setVersion(goods.getVersion());
                        goods1.setGoodsType(goods.getGoodsType());
                        goods1.setIsForPoints(goods.getIsForPoints());
                        goods1.setIsDsc(goods.getIsDsc());
                        goods1.setIsRecommended(goods.getIsRecommended());
                        goods1.setIsNewgood(goods.getIsNewgood());
                        goods1.setIsChangeprice(goods.getIsChangeprice());
                        goods1.setIsCustomPrice(goods.getIsCustomPrice());
                        goods1.setIsTakeout(goods.getIsTakeout());
                        goods1.setGoodsStatus(goods.getGoodsStatus());
                        goods1.setPriceType(goods.getPriceType());
                        goods1.setCreateAt(new Date());
                        goods1.setLastUpdateAt(new Date());
                        goods1.setCreateBy(goods.getCreateBy());
                        goods1.setLastUpdateBy(goods.getLastUpdateBy());
                        goods1.setIsWeigh(goods.getIsWeigh());
                        goods1.setStoreLowLimit(goods.getStoreLowLimit());
                        goods1.setStoreUpLimit(goods.getStoreUpLimit());
                        int i1 = goodsMapper.insert(goods1);
                        if(i1 <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("增加规格时失败！");
                            return apiRest;
                        }
                    }
                    else{
                        goods1.setId(BigInteger.valueOf(Long.parseLong(specId)));
                        goods1.setStandardName(specStandardName);
                        goods1.setSalePrice(specSalePrice);
                        goods1.setVipPrice(specVipPrice);
                        goods1.setVipPrice1(specVipPrice);
                        goods1.setVipPrice2(specVipPrice);
                        goods1.setShippingPrice1(specShippingPrice);
                        goods1.setShippingPrice2(specShippingPrice);
                        goods1.setPurchasingPrice(specPurchasingPrice);
                        goods1.setLastUpdateAt(new Date());
                        int i1 = goodsMapper.update(goods1);
                        if(i1 <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("修改子规格时失败！");
                            return apiRest;
                        }
                    }
                }
            }
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改成功！");
                apiRest.setData(goods);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("修改失败！");
                return apiRest;
            }

        }
        else{
            if(params.get("tenantBusiness") == null || !"6".equals(params.get("tenantBusiness"))){
                Branch branch = branchMapper.findBranchByTenantIdAndBranchId(tenantId, branchId);
                if(branch.getBranchType() != 0 && branch.getIsManageGoods() == false){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("该机构不具备自营菜品权限");
                    return apiRest;
                }
            }
            BigInteger categoryId = BigInteger.valueOf(Long.parseLong((String)params.get("categoryId")));
            BigInteger goodsUnitId = BigInteger.valueOf(Long.parseLong((String)params.get("goodsUnitId")));
            String goodsName = params.get("goodsName").toString();
            String mnemonic = params.get("mnemonic") == null ? "" : params.get("mnemonic").toString();
            Integer goodsStatus = Integer.valueOf(params.get("goodsStatus").toString());
            BigDecimal salePrice = BigDecimal.valueOf(Double.valueOf((String)params.get("salePrice")));
            BigDecimal vipPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("vipPrice")));
            BigDecimal shippingPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("shippingPrice1")));
            BigDecimal purchasingPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("purchasingPrice")));
            Integer isForPoints = Integer.valueOf(params.get("isForPoints").toString());
            Integer isTakeout = Integer.valueOf(params.get("isTakeout").toString());
            Integer isStore = Integer.valueOf(params.get("isStore").toString());
            Integer isPricetag = Integer.valueOf(params.get("isPricetag").toString());
            Integer isRecommended = Integer.valueOf(params.get("isRecommended").toString());
            Integer isNewgood = Integer.valueOf(params.get("isNewgood").toString());
            Integer isChangeprice = Integer.valueOf(params.get("isChangeprice").toString());

            Integer isAllowChangePrice = Integer.valueOf(params.get("isAllowChangePrice") == null ? "0" : params.get("isAllowChangePrice").toString());
            Integer isAllowVipDiscount = Integer.valueOf(params.get("isAllowVipDiscount") == null ? "0" : params.get("isAllowVipDiscount").toString());
            Integer combinationType = Integer.valueOf(params.get("combinationType").toString());
            String photo = "";
            String standardName = "";
            if(params.get("standardName") != null && !"".equals(params.get("standardName"))){
                standardName = params.get("standardName").toString();
            }

       /*     if(vipPrice.compareTo(purchasingPrice) < 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("会员价不能低于进价");
                return apiRest;
            }*/

            /*新增库存管理*/
            BigDecimal storeUpLimit = BigDecimal.ZERO;
            if(params.get("storeUpLimit") != null && !"".equals(params.get("storeUpLimit"))){
                storeUpLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeUpLimit").toString()));
            }
            BigDecimal storeLowLimit = BigDecimal.ZERO;
            if(params.get("storeLowLimit") != null && !"".equals(params.get("storeLowLimit"))){
                storeLowLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeLowLimit").toString()));
            }

            /*新增供应商*/
            BigInteger supplierId = BigInteger.ZERO;
            if(params.get("supplierId") != null && !"".equals(params.get("supplierId"))){
                supplierId = BigInteger.valueOf(Long.parseLong(params.get("supplierId").toString()));
            }

            List<Map> specsList = new ArrayList<>();
            if(params.get("specsList") != null && !"".equals(params.get("specsList"))){
                specsList = JSONArray.fromObject(params.get("specsList"));
            }
            List<Map> delSpecsList = new ArrayList<>();
            if(params.get("delSpecsList") != null && !"".equals(params.get("delSpecsList"))){
                delSpecsList = JSONArray.fromObject(params.get("delSpecsList"));
            }

            /*新增套餐信息*/
            List<Map> packageInfoList = new ArrayList<>();
            if(params.get("packageInfoList") != null && !"".equals(params.get("packageInfoList"))){
                packageInfoList = JSONArray.fromObject(params.get("packageInfoList"));
            }
            //ApiRest rea=SaaSApi.findTenantById(tenantId);
            String barCode = "";
               /* if (!rea.getIsSuccess()) {
                    return rea;
                }
                Tenant tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rea.getData()).get("tenant"), Tenant.class);
                if(tenant!=null&&tenant.getBusiness1().equals("2")){
                    ApiRest res=publicWSService.barCreator(params);
                    if(!res.getIsSuccess()){
                        return res;
                    }else {
                        barCode=res.getData().toString();
                    }
                }*/
            if(params.get("barCode") != null && !"".equals(params.get("barCode"))){
                barCode = params.get("barCode").toString();
            }
            if(params.get("photo") != null && !params.get("photo").equals("")){
                photo = params.get("photo").toString();
            }

            String specGroupId = "";
            if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
                specGroupId = params.get("specGroupId").toString();
            }
            String[] specList = specGroupId.split(",");

            if(categoryMapper.countByParentIdAndTenantId(categoryId, tenantId) > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("此分类下有二级分类，不能添加商品");
                return apiRest;
            }
            Map unitMap = new HashMap();
            unitMap.put("id", goodsUnitId);
            unitMap.put("tenantId", tenantId);
            GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
            if(goodsUnit == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询单位失败！");
                return apiRest;
            }
            Map catMap = new HashMap();
            catMap.put("id", categoryId);
            catMap.put("tenantId", tenantId);
            Category cat = categoryMapper.queryCatById(catMap);
            if(cat == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询分类失败！");
                return apiRest;
            }

            /*新增包装单位*/
            BigInteger packingUnitId = null;
            if(params.get("packingUnitId") != null && !"".equals(params.get("packingUnitId"))){
                packingUnitId = BigInteger.valueOf(Long.parseLong(params.get("packingUnitId").toString()));
            }
            BigDecimal unitRelation = BigDecimal.ONE;
            if(params.get("unitRelation") != null && !"".equals(params.get("unitRelation"))){
                unitRelation = BigDecimal.valueOf(Double.valueOf(params.get("unitRelation").toString()));
            }
            Map packingMap = new HashMap();
            packingMap.put("id", packingUnitId);
            packingMap.put("tenantId", tenantId);
            GoodsUnit packingUnit = new GoodsUnit();
            if(packingUnitId != null){
                packingUnit = goodsUnitMapper.queryUnitById(packingMap);
                if(packingUnit == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("查询单位失败！");
                    return apiRest;
                }
            }

            //包装费
            BigDecimal boxPrice = BigDecimal.ZERO;
            if(params.get("boxPrice") != null && !"".equals(params.get("boxPrice"))){
                boxPrice = BigDecimal.valueOf(Double.parseDouble(params.get("boxPrice").toString()));
            }
            //推荐商品
            if(params.get("id") != null && !params.get("id").equals("")){
                String userName = params.get("userName").toString();
                String opFrom = params.get("opFrom").toString();
                Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
                if(!goods.getCategoryId().equals(categoryId)){
                    goodsMapper.deleteGoodsOrderByCatId(tenantId, goods.getId());
                }
                if(goods == null){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未查询到需要修改的商品");
                    return apiRest;
                }
                int x = goods.getGoodsName().indexOf("美团外卖");
                int y = goods.getGoodsName().indexOf("饿了么");
                if(x >= 0 || y >= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("商品名中含有‘美团外卖’，‘饿了么’字样，不能修改！");
                    return apiRest;
                }
                if(isRecommended == 1 && !goods.getIsRecommended()){
                    ApiRest rest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_RECOMMEND_NUM);
                    if(!rest.getIsSuccess()){
                        apiRest = rest;
                        return apiRest;
                    }
                    ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_RECOMMEND_NUM);
                    if(!r.getIsSuccess()){
                        apiRest = r;
                        return apiRest;
                    }
                }
                if(isRecommended == 0 && goods.getIsRecommended()){
                    ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_RECOMMEND_NUM);
                    if(!r.getIsSuccess()){
                        apiRest = r;
                        return apiRest;
                    }
                }

                if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
                    int count = goodsSpecRMapper.findByGoodsId(tenantId, goods.getId());
                    if(count == 0){
                        for(int s = 0; s < specList.length; s++){
                            String specId = specList[s];
                            GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                            newGoodsSpecR.setGoodsId(goods.getId());
                            newGoodsSpecR.setTenantId(tenantId);
                            newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                            int i = goodsSpecRMapper.insert(newGoodsSpecR);
                            if(i <= 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setError("修改商品与口味关联失败！");
                                return apiRest;
                            }
                        }
                    }
                    else{
                        goodsSpecRMapper.deleteByGoodsId(tenantId, goods.getId());
                        for(int s = 0; s < specList.length; s++){
                            String specId = specList[s];
                            GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                            newGoodsSpecR.setGoodsId(goods.getId());
                            newGoodsSpecR.setTenantId(tenantId);
                            newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                            int i = goodsSpecRMapper.insert(newGoodsSpecR);
                            if(i <= 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setError("修改商品与口味关联失败！");
                                return apiRest;
                            }
                        }
                                /*goodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                                int i=goodsSpecRMapper.update(goodsSpecR);
                                if(i<=0){
                                    apiRest.setIsSuccess(false);
                                    apiRest.setError("修改商品与口味关联失败！");
                                    return apiRest;
                                }*/
                    }
                }

                /*判断称重PLU是否已被占用*/
                Integer weightPlu = null;
                if(params.get("weighPlu") != null && !"".equals(params.get("weighPlu"))){
                    weightPlu = Integer.valueOf(params.get("weighPlu").toString());
                    if(!Objects.equals(goods.getWeighPlu(), weightPlu)){
                        int count = goodsMapper.isPluUsed(params);
                        if(count > 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("该称重PLU已被占用");
                            return apiRest;
                        }
                    }
                }

                /*判断该菜品是否存在于套餐中*/
                int isUseInPackage = goodsMapper.getGroupsByGoodsId(tenantId, BigInteger.valueOf(Long.parseLong(params.get("id").toString())));
                if("1".equals(params.get("goodsStatus")) && isUseInPackage > 0){
                    apiRest.setIsSuccess(false);
                    if("5".equals(params.get("business1"))){
                        apiRest.setError("该商品在组合商品中被使用，不能停售");
                    }
                    else{
                        apiRest.setError("该商品在套餐中被使用，不能停售");
                    }
                    return apiRest;
                }

                if(!Objects.equals(salePrice, goods.getSalePrice())){
                    operateLogMapper.createOpLog("商品", goods.getId(), "修改售价", goods.getSalePrice().toString(), salePrice.toString(), userName, opFrom, "菜品名称：" + goods.getGoodsName() + ",编码：" + goods.getGoodsCode(), tenantId, branchId);
                }
                if(!Objects.equals(purchasingPrice, goods.getPurchasingPrice())){
                    operateLogMapper.createOpLog("商品", goods.getId(), "修改进价", goods.getPurchasingPrice().toString(), purchasingPrice.toString(), userName, opFrom, "菜品名称：" + goods.getGoodsName() + ",编码：" + goods.getGoodsCode(), tenantId, branchId);
                }
                if(!Objects.equals(vipPrice, goods.getVipPrice())){
                    operateLogMapper.createOpLog("商品", goods.getId(), "修改会员价", goods.getVipPrice().toString(), vipPrice.toString(), userName, opFrom, "菜品名称：" + goods.getGoodsName() + ",编码：" + goods.getGoodsCode(), tenantId, branchId);
                }
                if(!Objects.equals(shippingPrice, goods.getShippingPrice1())){
                    operateLogMapper.createOpLog("商品", goods.getId(), "修改配货价", goods.getShippingPrice1().toString(), shippingPrice.toString(), userName, opFrom, "菜品名称：" + goods.getGoodsName() + ",编码：" + goods.getGoodsCode(), tenantId, branchId);
                }
                goods.setGoodsName(goodsName);
                goods.setCategoryId(categoryId);
                goods.setTenantId(tenantId);
                goods.setMnemonic(mnemonic);
                goods.setPhoto(photo);
                goods.setSpecGroupId(specGroupId == "" ? null : specGroupId);
                goods.setGoodsUnitId(goodsUnitId);
                goods.setGoodsUnitName(goodsUnit.getUnitName());
                goods.setSalePrice(salePrice);
                goods.setVipPrice(vipPrice);
                goods.setVipPrice2(vipPrice);
                goods.setVipPrice1(vipPrice);
                goods.setPurchasingPrice(purchasingPrice);
                goods.setIsPricetag(isPricetag == 1);
                goods.setCategoryName(cat.getCatName());
                if(goods.getCombinationType() == 1 || goods.getCombinationType() == 2){
                    goods.setIsStore(true);
                }
                else{
                    goods.setIsStore(isStore == 1);
                }
                goods.setCombinationType(combinationType);
                goods.setGoodsType(1);
                goods.setIsForPoints(isForPoints == 1);
                goods.setIsDsc(isForPoints == 1);
                goods.setIsRecommended(isRecommended == 1);
                goods.setIsNewgood(isNewgood == 1);
                goods.setIsChangeprice(isChangeprice == 1);
                goods.setIsCustomPrice(isChangeprice == 1);
                goods.setIsAllowChangePrice(isAllowChangePrice == 1);
                goods.setIsAllowVipDiscount(isAllowVipDiscount == 1);
                goods.setIsTakeout(isTakeout == 1);
                goods.setGoodsStatus(goodsStatus);
                goods.setShippingPrice1(shippingPrice);
                goods.setShippingPrice2(shippingPrice);
                goods.setBranchId(branchId);
                goods.setLastUpdateAt(new Date());
                goods.setLastUpdateBy("System");
                goods.setVersion(goods.getVersion().add(BigInteger.ONE));
                goods.setParentId(BigInteger.ZERO);
                if(params.get("isWeigh") != null && !params.get("isWeigh").equals("")){
                    goods.setIsWeigh(params.get("isWeigh").equals("1"));
                }
                goods.setStandardName(standardName);
                /*新增库存管理*/
                goods.setStoreUpLimit(storeUpLimit);
                goods.setStoreLowLimit(storeLowLimit);

                /*新增包装单位*/
                if(packingUnit != null){
                    goods.setPackingUnitId(packingUnit.getId());
                    goods.setPackingUnitName(packingUnit.getUnitName());
                }

                /*新增供应商*/
                goods.setSupplierId(supplierId);
                goods.setUnitRelation(unitRelation);

                /*条码*/
                goods.setBarCode(barCode);

                /*称重PLU*/
                goods.setWeighPlu(weightPlu);
                /*包装费*/
                goods.setBoxPrice(boxPrice);
                int i = goodsMapper.update(goods);
                BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
                /*有删除的子规格时删除*/
                if(delSpecsList != null && delSpecsList.size() > 0){
                    for(int m = 0; m < delSpecsList.size(); m++){
                        Map<String, String> specMap = delSpecsList.get(m);
                        String specId = specMap.get("specId");
                        Map map = new HashMap();
                        map.put("tenantId", tenantId);
                        map.put("branchId", branchId);
                        map.put("specId", specId);
                        map.put("id", specId);
                        Goods good1 = goodsMapper.findGoodsByIdAndTenantId(map);
                        Map errorMap = new HashMap();
                        if(good1 == null){
                            errorMap.put("id", id);
                            errorMap.put("mes", "查询子规格失败！");
                            apiRest.setIsSuccess(false);
                            apiRest.setError("查询子规格失败！");
                        }
                        QueryGoodsStoreModel model = new QueryGoodsStoreModel();
                        model.setTenantId(tenantId);
                        /*model.setGoodsId(good1.getId());*/
                        model.setBranchId(branchId);
                        model.setIds(good1.getId().toString());
                        List<Map> quantityList = goodsMapper.sumGoodsStore(model);
                        if(quantityList.size() != 0){
                            BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(quantityList.get(0).get("quantity").toString()));
                            if(quantity != null && BigDecimal.valueOf(0L).compareTo(quantity) == -1){
                                errorMap.put("id", good1.getId());
                                errorMap.put("goodsName", good1.getGoodsName());
                                errorMap.put("mes", "子规格中有库存，删除失败！");
                                apiRest.setIsSuccess(false);
                                apiRest.setError("子规格中有库存，删除失败！");
                                return apiRest;
                            }
                        }

                        List<Map> groupCountList = goodsMapper.countGroupGoods(model);
                        if(groupCountList.size() != 0){
                            Long groupCount = Long.parseLong(groupCountList.get(0).get("count").toString());
                            if(groupCount > 0){
                                errorMap.put("id", good1.getId());
                                errorMap.put("goodsName", good1.getGoodsName());
                                errorMap.put("mes", "子规格是套餐，删除失败！");
                                apiRest.setIsSuccess(false);
                                apiRest.setError("子规格是套餐，删除失败！");
                                return apiRest;
                            }
                        }

                        List<Map> menuGoodsCountList = goodsMapper.countMenuGoods(model);
                        if(menuGoodsCountList.size() != 0){
                            Long menuGoodsCount = Long.parseLong(menuGoodsCountList.get(0).get("count").toString());
                            if(menuGoodsCount > 0){
                                errorMap.put("id", good1.getId());
                                errorMap.put("goodsName", good1.getGoodsName());
                                errorMap.put("mes", "子规格存在菜牌中，删除失败！");
                                apiRest.setIsSuccess(false);
                                apiRest.setError("子规格存在菜牌中，删除失败！");
                                return apiRest;
                            }
                        }

                        List<Map> dietCountList = goodsMapper.countDiet(model);
                        if(dietCountList.size() != 0){
                            Long dietCount = Long.parseLong(dietCountList.get(0).get("count").toString());
                            if(dietCount > 0){
                                errorMap.put("id", good1.getId());
                                errorMap.put("goodsName", good1.getGoodsName());
                                errorMap.put("mes", "子规格存在促销，删除失败！");
                                apiRest.setIsSuccess(false);
                                apiRest.setError("子规格存在促销，删除失败！");
                                return apiRest;
                            }
                        }

                        List<Map> relationCountList = goodsMapper.countRelation(model);
                        if(relationCountList.size() != 0){
                            Long relationCount = Long.parseLong(relationCountList.get(0).get("count").toString());
                            if(relationCount > 0){
                                errorMap.put("id", good1.getId());
                                errorMap.put("goodsName", good1.getGoodsName());
                                errorMap.put("mes", "子规格存在加工配方明细，删除失败！");
                                apiRest.setIsSuccess(false);
                                apiRest.setError("子规格存在加工配方明细，删除失败！");
                                return apiRest;
                            }
                        }

                        int n = goodsMapper.deleteSpec(map);
                        if(n <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("删除子规格时失败！");
                            return apiRest;
                        }
                    }
                }
                /*有子规格的修改子规格数据*/
                if(specsList != null && specsList.size() > 0){
                    for(int j = 0; j < specsList.size(); j++){
                        Map<String, String> specMap = specsList.get(j);
                        String specId = specMap.get("specId");
                        String specStandardName = specMap.get("specStandardName");
                        BigDecimal specSalePrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specSalePrice")));
                        BigDecimal specVipPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specVipPrice")));
                        BigDecimal specShippingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specShippingPrice")));
                        BigDecimal specPurchasingPrice;
                        if(specMap.get("specPurchasingPrice") == null || "".equals(specMap.get("specPurchasingPrice"))){
                            specPurchasingPrice = goods.getPurchasingPrice();
                        }
                        else{
                            specPurchasingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specPurchasingPrice")));
                        }
                        Goods goods1 = new Goods();
                        if(specId == null || "".equals(specId)){
                            goods1.setTenantId(goods.getTenantId());
                            goods1.setBranchId(goods.getBranchId());
                            goods1.setCategoryId(goods.getCategoryId());
                            goods1.setCategoryName(goods.getCategoryName());
                            goods1.setGoodsCode(goods.getGoodsCode());
                            goods1.setGoodsName(goods.getGoodsName());
                            goods1.setSpecGroupId(goods.getSpecGroupId());
                            goods1.setMnemonic(goods.getMnemonic());
                            goods1.setGoodsUnitId(goods.getGoodsUnitId());
                            goods1.setGoodsUnitName(goods.getGoodsUnitName());
                            goods1.setGoodsStatus(goods.getGoodsStatus());
                            goods1.setCombinationType(goods.getCombinationType());
                            goods1.setPhoto(goods.getPhoto());

                            goods1.setParentId(id);
                            goods1.setStandardName(specStandardName);
                            goods1.setSalePrice(specSalePrice);
                            goods1.setVipPrice(specVipPrice);
                            goods1.setVipPrice1(specVipPrice);
                            goods1.setVipPrice2(specVipPrice);
                            goods1.setShippingPrice1(specShippingPrice);
                            goods1.setShippingPrice2(specShippingPrice);
                            goods1.setPurchasingPrice(specPurchasingPrice);

                            goods1.setIsForPoints(goods.getIsForPoints());
                            goods1.setIsDsc(goods.getIsDsc());
                            goods1.setIsStore(goods.getIsStore());
                            goods1.setIsPricetag(goods.getIsPricetag());
                            goods1.setIsChangeprice(goods.getIsChangeprice());
                            goods1.setIsTakeout(goods.getIsTakeout());
                            goods1.setIsWeigh(goods.getIsWeigh());
                            goods1.setIsNewgood(goods.getIsNewgood());
                            goods1.setIsRecommended(goods.getIsRecommended());

                            goods1.setVersion(goods.getVersion());
                            goods1.setGoodsType(goods.getGoodsType());
                            goods1.setIsCustomPrice(goods.getIsCustomPrice());
                            goods1.setPriceType(goods.getPriceType());
                            goods1.setCreateAt(new Date());
                            goods1.setLastUpdateAt(new Date());
                            goods1.setCreateBy(goods.getCreateBy());
                            goods1.setLastUpdateBy(goods.getLastUpdateBy());

                            goods1.setIsLowerfee(goods.getIsLowerfee());
                            goods1.setIsTemp(goods.getIsTemp());
                            goods1.setIsGiveticket(goods.getIsGiveticket());
                            goods1.setIsUseticket(goods.getIsUseticket());
                            goods1.setIsModifynum(goods.getIsModifynum());
                            goods1.setIsRevisedPrice(goods.getIsRevisedPrice());
                            goods1.setIsServicefee(goods.getIsServicefee());
                            goods1.setIsSertc(goods.getIsSertc());
                            goods1.setIsSaletc(goods.getIsSaletc());
                            goods1.setIsMuchspec(goods.getIsMuchspec());
                            goods1.setIsScore(goods.getIsScore());
                            goods1.setIsHotgood(goods.getIsHotgood());
                            goods1.setIsOrder(goods.getIsOrder());
                            goods1.setIsHere(goods.getIsHere());
                            goods1.setIsDeleted(false);

                            /*新增库存管理*/
                            goods1.setStoreUpLimit(goods.getStoreUpLimit());
                            goods1.setStoreLowLimit(goods.getStoreLowLimit());

                            //新增包装单位
                            goods1.setPackingUnitId(goods.getPackingUnitId());
                            goods1.setPackingUnitName(goods.getPackingUnitName());
                            goods1.setUnitRelation(goods.getUnitRelation());

                            //条码
                            if(specMap.get("specBarCode") != null){
                                String specBarCode = specMap.get("specBarCode");
                                goods1.setBarCode(specBarCode);
                            }

                            //新增供应商
                            goods1.setSupplierId(goods.getSupplierId());
                            /*包装费*/
                            goods1.setBoxPrice(boxPrice);
                            int i1 = goodsMapper.insert(goods1);
                            if(i1 <= 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setMessage("增加规格时失败！");
                                return apiRest;
                            }
                        }
                        else{
                            goods1.setTenantId(goods.getTenantId());
                            goods1.setBranchId(goods.getBranchId());
                            goods1.setCategoryId(goods.getCategoryId());
                            goods1.setCategoryName(goods.getCategoryName());
                            goods1.setGoodsCode(goods.getGoodsCode());
                            goods1.setGoodsName(goods.getGoodsName());
                            goods1.setSpecGroupId(goods.getSpecGroupId());
                            goods1.setMnemonic(goods.getMnemonic());
                            goods1.setGoodsUnitId(goods.getGoodsUnitId());
                            goods1.setGoodsUnitName(goods.getGoodsUnitName());
                            goods1.setGoodsStatus(goods.getGoodsStatus());
                            goods1.setCombinationType(goods.getCombinationType());
                            goods1.setPhoto(goods.getPhoto());

                            goods1.setId(BigInteger.valueOf(Long.parseLong(specId)));
                            goods1.setStandardName(specStandardName);
                            goods1.setSalePrice(specSalePrice);
                            goods1.setVipPrice(specVipPrice);
                            goods1.setVipPrice1(specVipPrice);
                            goods1.setVipPrice2(specVipPrice);
                            goods1.setShippingPrice1(specShippingPrice);
                            goods1.setShippingPrice2(specShippingPrice);
                            goods1.setPurchasingPrice(specPurchasingPrice);

                            goods1.setIsForPoints(goods.getIsForPoints());
                            goods1.setIsDsc(goods.getIsDsc());
                            goods1.setIsStore(goods.getIsStore());
                            goods1.setIsPricetag(goods.getIsPricetag());
                            goods1.setIsChangeprice(goods.getIsChangeprice());
                            goods1.setIsAllowChangePrice(goods.getIsAllowChangePrice());
                            goods1.setIsAllowVipDiscount(goods.getIsAllowVipDiscount());
                            goods1.setIsTakeout(goods.getIsTakeout());
                            goods1.setIsWeigh(goods.getIsWeigh());
                            goods1.setIsNewgood(goods.getIsNewgood());
                            goods1.setIsRecommended(goods.getIsRecommended());
                            goods1.setIsCustomPrice(goods.getIsCustomPrice());

                            goods1.setIsLowerfee(goods.getIsLowerfee());
                            goods1.setIsTemp(goods.getIsTemp());
                            goods1.setIsGiveticket(goods.getIsGiveticket());
                            goods1.setIsUseticket(goods.getIsUseticket());
                            goods1.setIsModifynum(goods.getIsModifynum());
                            goods1.setIsRevisedPrice(goods.getIsRevisedPrice());
                            goods1.setIsServicefee(goods.getIsServicefee());
                            goods1.setIsSertc(goods.getIsSertc());
                            goods1.setIsSaletc(goods.getIsSaletc());
                            goods1.setIsMuchspec(goods.getIsMuchspec());
                            goods1.setIsScore(goods.getIsScore());
                            goods1.setLastUpdateAt(new Date());
                            goods1.setIsDeleted(goods.getIsDeleted());
                            goods1.setIsHotgood(goods.getIsHotgood());
                            goods1.setIsOrder(goods.getIsOrder());
                            goods1.setIsHere(goods.getIsHere());

                            /*新增库存管理*/
                            goods1.setStoreUpLimit(goods.getStoreUpLimit());
                            goods1.setStoreLowLimit(goods.getStoreLowLimit());

                            /*新增包装单位*/
                            goods1.setPackingUnitId(goods.getPackingUnitId());
                            goods1.setPackingUnitName(goods.getPackingUnitName());
                            goods1.setUnitRelation(goods.getUnitRelation());

                            //条码
                            if(specMap.get("specBarCode") != null){
                                String specBarCode = specMap.get("specBarCode");
                                goods1.setBarCode(specBarCode);
                            }

                            //新增供应商
                            goods1.setSupplierId(goods.getSupplierId());
                            /*包装费*/
                            goods1.setBoxPrice(boxPrice);
                            int i1 = goodsMapper.update(goods1);
                            if(i1 <= 0){
                                apiRest.setIsSuccess(false);
                                apiRest.setMessage("修改子规格时失败！");
                                return apiRest;
                            }
                        }
                    }
                }
                /*修改关联套餐----1、删除；2、修改*/
                if(packageInfoList != null && packageInfoList.size() > 0){
                    Map packageMap = packageInfoList.get(0);
                    Goods packageGood = new Goods();
                    if(packageMap.get("id") != null && !"".equals(packageMap.get("id"))){
                        packageGood.setId(BigInteger.valueOf(Long.parseLong(packageMap.get("id").toString())));
                        packageGood.setGoodsName(packageMap.get("packageGoodName").toString());
                        packageGood.setMnemonic(packageMap.get("mnemonic").toString());
                        packageGood.setBarCode(packageMap.get("barCode") == null ? "" : packageMap.get("barCode").toString());
                        packageGood.setSalePrice(BigDecimal.valueOf(Double.parseDouble(packageMap.get("packageSalePrice").toString())));
                        packageGood.setVipPrice(BigDecimal.valueOf(Double.parseDouble(packageMap.get("packageVipPrice").toString())));
                        packageGood.setVipPrice1(BigDecimal.valueOf(Double.parseDouble(packageMap.get("packageVipPrice").toString())));
                        packageGood.setVipPrice2(BigDecimal.valueOf(Double.parseDouble(packageMap.get("packageVipPrice").toString())));
                        packageGood.setGoodsUnitId(goods.getPackingUnitId());
                        packageGood.setGoodsUnitName(goods.getPackingUnitName());
                        packageGood.setLastUpdateAt(new Date());
                        int i1 = goodsMapper.update(packageGood);
                        if(i1 <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setMessage("修改大包装商品时失败！");
                            return apiRest;
                        }
                        List<PackageGroup> packageGroupList = groupGoodsMapper.getGroupsByPackId(tenantId, BigInteger.valueOf(Long.parseLong(packageMap.get("id").toString())));
                        PackageGroup packageGroup = packageGroupList.get(0);
                        BigInteger groupId = packageGroup.getId();
                        /*商品修改单位大小转换关系后修改套餐/组合商品明细的数量*/
                        Map groupGoodsMap = new HashMap();
                        groupGoodsMap.put("quantity", goods.getUnitRelation());
                        groupGoodsMap.put("groupId", groupId);
                        groupGoodsMap.put("goodsId", goods.getId());
                        groupGoodsMap.put("branchId", branchId);
                        groupGoodsMap.put("tenantId", tenantId);
                        int i2 = groupGoodsMapper.updateQuantity(groupGoodsMap);
                        if(i2 <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("修改套餐明细失败");
                            return apiRest;
                        }
                    }
                    else{
                        packageMap.put("tenantId", tenantId.toString());
                        packageMap.put("branchId", branchId.toString());
                        packageMap.put("mnemonic", packageMap.get("mnemonic"));
                        packageMap.put("barCode", packageMap.get("barCode"));
                        packageMap.put("goodsName", packageMap.get("packageGoodName"));
                        packageMap.put("goodsStatus", goods.getGoodsStatus());
                        packageMap.put("isDsc", goods.getIsDsc() == true ? 1 : 0);
                        packageMap.put("isTakeout", goods.getIsTakeout() == true ? 1 : 0);
                        packageMap.put("salePrice", packageMap.get("packageSalePrice"));
                        packageMap.put("vipPrice", packageMap.get("packageVipPrice"));
                        packageMap.put("photo", goods.getPhoto());
                        List groupGoods = new ArrayList();
                        Map goodsMap = new HashMap();
                        goodsMap.put("id", goods.getId());
                        goodsMap.put("quantity", goods.getUnitRelation());
                        groupGoods.add(goodsMap);
                        Map map = new HashMap();
                        map.put("groupGoods", groupGoods);
                        packageMap.put("groupGoods", map);
                        /*关联小包装商品*/
                        packageMap.put("relatedGoodsId", goods.getId());
                        packageMap.put("goodsUnitId", goods.getPackingUnitId());
                        packageMap.put("goodsUnitName", goods.getPackingUnitName());
                        ApiRest apiRest1 = packageService.addOrUpdatePackage(packageMap);
                        if(!apiRest1.getIsSuccess()){
                            return apiRest1;
                        }
                    }
                }
                else{
                    Map map = new HashMap();
                    map.put("goodsId", id);
                    map.put("tenantId", tenantId);
                    List<Map> packageInfo = goodsMapper.queryPackageByGoodsId(map);
                    if(packageInfo != null && packageInfo.size() > 0){
                        Map delPackage = new HashMap();
                        delPackage.put("tenantId", tenantId.toString());
                        delPackage.put("id", packageInfo.get(0).get("id").toString());
                        ApiRest apiRest1 = packageService.delPackage(delPackage);
                        if(!apiRest1.getIsSuccess()){
                            return apiRest1;
                        }
                    }
                }
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("修改成功！");
                    apiRest.setData(goods);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改失败！");
                    return apiRest;
                }
            }
            else{
                /*判断称重PLU是否已被占用*/
                Integer weightPlu = null;
                if(params.get("weighPlu") != null && !"".equals(params.get("weighPlu"))){
                    weightPlu = Integer.valueOf(params.get("weighPlu").toString());
                    int count = goodsMapper.isPluUsed(params);
                    if(count > 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("该称重PLU已被占用");
                        return apiRest;
                    }
                }
                Map codeMap = new HashMap();
                codeMap.put("categoryId", categoryId);
                codeMap.put("tenantId", tenantId);
                ApiRest codeRest = getGoodsCode(codeMap);
                Goods goods = new Goods();
                ApiRest re = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_GOODS_NUM);
                if(!codeRest.getIsSuccess()){
                    apiRest = codeRest;
                    return apiRest;
                }
                goods.setGoodsCode(codeRest.getData().toString());
                goods.setBarCode(barCode);
                if(!re.getIsSuccess()){
                    apiRest = re;
                    return apiRest;
                }
                if(isRecommended == 1){
                    ApiRest rest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_RECOMMEND_NUM);
                    if(!rest.getIsSuccess()){
                        apiRest = rest;
                        return apiRest;
                    }
                    ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_RECOMMEND_NUM);
                    if(!r.getIsSuccess()){
                        apiRest = r;
                        return apiRest;
                    }
                }
                goods.setGoodsName(goodsName);
                goods.setCategoryId(categoryId);
                goods.setTenantId(tenantId);
                goods.setMnemonic(mnemonic);
                goods.setPhoto(photo);
                goods.setSpecGroupId(specGroupId == "" ? null : specGroupId);
                goods.setGoodsUnitId(goodsUnitId);
                goods.setGoodsUnitName(goodsUnit.getUnitName());
                goods.setSalePrice(salePrice);
                goods.setVipPrice(vipPrice);
                goods.setVipPrice2(vipPrice);
                goods.setVipPrice1(vipPrice);
                goods.setPurchasingPrice(purchasingPrice);
                goods.setIsPricetag(isPricetag == 1);
                goods.setCategoryName(cat.getCatName());
                if(combinationType == 1 || combinationType == 2){
                    goods.setIsStore(true);
                }
                else{
                    goods.setIsStore(isStore == 1);
                }
                goods.setCombinationType(combinationType);
                goods.setVersion(BigInteger.valueOf(0));
                goods.setGoodsType(1);
                goods.setIsForPoints(isForPoints == 1);
                goods.setIsDsc(isForPoints == 1);
                goods.setIsRecommended(isRecommended == 1);
                goods.setIsNewgood(isNewgood == 1);
                goods.setIsChangeprice(isChangeprice == 1);
                goods.setIsCustomPrice(isChangeprice == 1);
                goods.setIsAllowChangePrice(isAllowChangePrice==1);
                goods.setIsAllowVipDiscount(isAllowVipDiscount==1);

                goods.setIsTakeout(isTakeout == 1);
                goods.setGoodsStatus(goodsStatus);
                goods.setShippingPrice1(shippingPrice);
                goods.setShippingPrice2(shippingPrice);
                goods.setBranchId(branchId);
                goods.setPriceType(0);
                goods.setCreateAt(new Date());
                goods.setLastUpdateAt(new Date());
                goods.setCreateBy("System");
                goods.setLastUpdateBy("System");
                goods.setParentId(BigInteger.ZERO);
                if(params.get("isWeigh") != null && !params.get("isWeigh").equals("")){
                    goods.setIsWeigh(params.get("isWeigh").equals("1"));
                }
                /*规格名*/
                goods.setStandardName(standardName);
                /*新增库存管理*/
                goods.setStoreUpLimit(storeUpLimit);
                goods.setStoreLowLimit(storeLowLimit);

                /*新增包装单位*/
                if(packingUnit != null){
                    goods.setPackingUnitId(packingUnit.getId());
                    goods.setPackingUnitName(packingUnit.getUnitName());
                }
                goods.setUnitRelation(unitRelation);

                /*新增供应商*/
                goods.setSupplierId(supplierId);

                /*称重PLU*/
                goods.setWeighPlu(weightPlu);
                goods.setRelatedGoodsId(BigInteger.ZERO);
                /*包装费*/
                goods.setBoxPrice(boxPrice);
                int i = goodsMapper.insert(goods);
                /*库存---新增初始库存，若不为0则写入库存表*/
                if(params.get("store") != null && !"0".equals(params.get("store").toString())){
                    StoreAccount storeAccount = new StoreAccount();
                    storeAccount.setTenantId(goods.getTenantId());
                    storeAccount.setBranchId(goods.getBranchId());
                    storeAccount.setGoodsId(goods.getId());
                    storeAccount.setOccurIncurred(goods.getPurchasingPrice());
                    storeAccount.setOccurQuantity(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString())));
                    storeAccount.setOccurAmount(goods.getPurchasingPrice().multiply(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString()))));
                    storeAccount.setOccurAt(new Date());
                    storeAccount.setStoreIncurred(goods.getPurchasingPrice());
                    storeAccount.setStoreQuantity(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString())));
                    storeAccount.setOccurAmount(goods.getPurchasingPrice().multiply(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString()))));
                    storeAccount.setOccurType(21);
                    storeAccount.setOccurCode(null);
                    storeAccount.setStoreAccountAt(new Date());
                    storeAccount.setCreateAt(new Date());
                    storeAccount.setCreateBy(goods.getCreateBy());
                    storeAccount.setLastUpdateAt(new Date());
                    storeAccount.setLastUpdateBy(goods.getLastUpdateBy());
                    storeMapper.saveStoreAccount(storeAccount);
                    Store store = new Store();
                    store.setTenantId(goods.getTenantId());
                    store.setBranchId(goods.getBranchId());
                    store.setGoodsId(goods.getId());
                    store.setQuantity(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString())));
                    store.setAvgAmount(goods.getPurchasingPrice());
                    store.setStoreAmount(goods.getPurchasingPrice().multiply(BigDecimal.valueOf(Double.parseDouble(params.get("store").toString()))));
                    store.setCreateAt(new Date());
                    store.setCreateBy(goods.getCreateBy());
                    store.setLastUpdateAt(new Date());
                    store.setLastUpdateBy(goods.getLastUpdateBy());
                    store.setStoreAt(new Date());
                    storeMapper.saveStore(store);
                    operateLogMapper.createOpLog("新增商品初始化库存", goods.getId(), "写库存", "0", params.get("store").toString(), "System", "新增商品", null, tenantId, branchId);
                }
                /*获取刚刚新增商品的ID*/
                BigInteger id = goods.getId();
                /*保存子规格商品*/
                if(specsList != null && specsList.size() > 0){
                    Goods goods1 = new Goods();
                    goods1.setParentId(id);
                    goods1.setGoodsCode(goods.getGoodsCode());
                    goods1.setGoodsName(goods.getGoodsName());
                    goods1.setCategoryId(goods.getCategoryId());
                    goods1.setCategoryName(goods.getCategoryName());
                    goods1.setTenantId(goods.getTenantId());
                    goods1.setBranchId(goods.getBranchId());
                    goods1.setMnemonic(goods.getMnemonic());
                    goods1.setPhoto(goods.getPhoto());
                    goods1.setSpecGroupId(goods.getSpecGroupId());
                    goods1.setGoodsUnitId(goods.getGoodsUnitId());
                    goods1.setGoodsUnitName(goods.getGoodsUnitName());
                    goods1.setIsPricetag(goods.getIsPricetag());
                    goods1.setIsStore(goods.getIsStore());
                    goods1.setCombinationType(goods.getCombinationType());
                    goods1.setVersion(goods.getVersion());
                    goods1.setGoodsType(goods.getGoodsType());
                    goods1.setIsForPoints(goods.getIsForPoints());
                    goods1.setIsDsc(goods.getIsDsc());
                    goods1.setIsRecommended(goods.getIsRecommended());
                    goods1.setIsNewgood(goods.getIsNewgood());
                    goods1.setIsChangeprice(goods.getIsChangeprice());
                    goods1.setIsCustomPrice(goods.getIsCustomPrice());
                    goods1.setIsTakeout(goods.getIsTakeout());
                    goods1.setGoodsStatus(goods.getGoodsStatus());
                    goods1.setPriceType(goods.getPriceType());
                    goods1.setCreateAt(new Date());
                    goods1.setLastUpdateAt(new Date());
                    goods1.setCreateBy(goods.getCreateBy());
                    goods1.setLastUpdateBy(goods.getLastUpdateBy());
                    goods1.setIsWeigh(goods.getIsWeigh());
                    goods1.setBarCode(goods.getBarCode());
                    /*新增库存管理*/
                    goods1.setStoreUpLimit(goods.getStoreUpLimit());
                    goods1.setStoreLowLimit(goods.getStoreLowLimit());

                    /*新增包装单位*/
                    goods1.setPackingUnitId(goods.getPackingUnitId());
                    goods1.setPackingUnitName(goods.getPackingUnitName());
                    goods1.setUnitRelation(goods.getUnitRelation());

                    /*新增供应商*/
                    goods1.setSupplierId(goods.getSupplierId());

                    /*包装费*/
                    goods1.setBoxPrice(boxPrice);
                    for(int j = 0; j < specsList.size(); j++){
                        Map<String, String> specMap = specsList.get(j);
                        String specStandardName = specMap.get("specStandardName");
                        BigDecimal specPurchasingPrice;
                        if(specMap.get("specPurchasingPrice") == null || "".equals(specMap.get("specPurchasingPrice"))){
                            specPurchasingPrice = goods.getPurchasingPrice();
                        }
                        else{
                            specPurchasingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specPurchasingPrice")));
                        }
                        BigDecimal specSalePrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specSalePrice")));
                        BigDecimal specVipPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specVipPrice")));
                        BigDecimal specShippingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specShippingPrice")));
                        goods1.setId(null);
                        goods1.setStandardName(specStandardName);
                        if(specMap.get("specBarCode") != null){
                            String specBarCode = specMap.get("specBarCode");
                            goods1.setBarCode(specBarCode);
                        }
                        goods1.setPurchasingPrice(specPurchasingPrice);
                        goods1.setSalePrice(specSalePrice);
                        goods1.setVipPrice(specVipPrice);
                        goods1.setVipPrice1(specVipPrice);
                        goods1.setVipPrice2(specVipPrice);
                        goods1.setShippingPrice1(specShippingPrice);
                        goods1.setShippingPrice2(specShippingPrice);
                        int i1 = goodsMapper.insert(goods1);
                        if(i1 <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("新增子规格时失败！");
                        }
                    }
                }

                /*大包装商品保存为套餐/组合商品*/
                if(packageInfoList != null && packageInfoList.size() > 0){
                    Map packageInfo = packageInfoList.get(0);
                    Map packageMap = new HashMap();
                    packageMap.put("tenantId", tenantId.toString());
                    packageMap.put("branchId", branchId.toString());
                    packageMap.put("mnemonic", packageInfo.get("mnemonic"));
                    packageMap.put("barCode", packageInfo.get("barCode"));
                    packageMap.put("goodsName", packageInfo.get("packageGoodName"));
                    packageMap.put("goodsStatus", goods.getGoodsStatus());
                    packageMap.put("isDsc", goods.getIsDsc() == true ? 1 : 0);
                    packageMap.put("isTakeout", goods.getIsTakeout() == true ? 1 : 0);
                    packageMap.put("salePrice", packageInfo.get("packageSalePrice"));
                    packageMap.put("vipPrice", packageInfo.get("packageVipPrice"));
                    packageMap.put("shippingPrice1", packageInfo.get("shippingPrice1"));
                    packageMap.put("shippingPrice2", packageInfo.get("shippingPrice1"));
                    packageMap.put("photo", goods.getPhoto());
                    List groupGoods = new ArrayList();
                    Map goodsMap = new HashMap();
                    goodsMap.put("id", goods.getId());
                    goodsMap.put("quantity", goods.getUnitRelation());
                    groupGoods.add(goodsMap);
                    Map map = new HashMap();
                    map.put("groupGoods", groupGoods);
                    packageMap.put("groupGoods", map);
                    /*关联小包装商品*/
                    packageMap.put("relatedGoodsId", goods.getId());
                    packageMap.put("goodsUnitId", goods.getPackingUnitId());
                    packageMap.put("goodsUnitName", goods.getPackingUnitName());
                    ApiRest apiRest1 = packageService.addOrUpdatePackage(packageMap);
                    if(apiRest1.getIsSuccess() == false){
                        return apiRest1;
                    }
                }
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("新增成功！");
                    apiRest.setData(goods);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增失败！");
                    return apiRest;
                }
                if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
                    for(int s = 0; s < specList.length; s++){
                        String specId = specList[s];
                        GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                        newGoodsSpecR.setGoodsId(goods.getId());
                        newGoodsSpecR.setTenantId(tenantId);
                        newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                        int k = goodsSpecRMapper.insert(newGoodsSpecR);
                        if(k <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("保存商品与口味关联失败！");
                            return apiRest;
                        }
                    }
                }
                ApiRest res = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_GOODS_NUM);
                if(!res.getIsSuccess()){
                    apiRest = res;
                    return apiRest;
                }
            }
        }
        return apiRest;
    }

    /**
     * 批量导入数据
     *
     * @param params
     * @return
     */
    public ApiRest saveGoodsList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String business = params.get("business").toString();
        //导入数据总条数
        int goodsNum = Integer.valueOf(params.get("allNum").toString());
        if(goodsNum == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("数据为空，请确认参数！");
            return apiRest;
        }
        ApiRest re = tenantConfigService.isOverMax(tenantId, SysConfig.SYS_GOODS_NUM, goodsNum);
        if(!re.getIsSuccess()){
            return re;
        }

        List<Map<String, Object>> goodsJson = GsonUtils.fromJson(ZipUtils.unzipText(params.get("goodsList").toString()), List.class);
//            JSONArray goodsJson= JSONArray.fromObject(ZipUtils.unzipText(params.get("goodsList").toString()));

        String isUpdate = "";
        if(params.get("isUpdate") != null && !"".equals(params.get("isUpdate"))){
            isUpdate = params.get("isUpdate").toString();
        }
        List<Goods> updateGoodsList = new ArrayList<>();
        List<Goods> goodsList = new ArrayList<>();
        /*判断是否更新现有数据*/
        //int count=0;
        int i = 0;
        int u = 0;
        int z = 0;
        String nowCode = goodsMapper.getInputMaxCode(tenantId);
        //查找商户下的所有商品分类
        String tenantBusiness = params.get("tenantBusiness") == null ? null : params.get("tenantBusiness").toString();
        List<Map<String, Object>> categoryInfos = categoryMapper.findAll(tenantId, tenantBusiness, branchId);
        Map<String, BigInteger> categorys = new HashMap<String, BigInteger>();
        //将所有商品分类名称和id保存到categorys中
        for(Map<String, Object> categoryInfo : categoryInfos){
            categorys.put(MapUtils.getString(categoryInfo, "catName"), BigInteger.valueOf(MapUtils.getLongValue(categoryInfo, "id")));
        }

        Map<String, BigInteger> units = new HashMap<String, BigInteger>();
        //查找商户下的所有商品单位
        List<Map<String, Object>> unitInfos = goodsUnitMapper.findAll(tenantId);
        //将所有商品单位名称和id保存到units中
        for(Map<String, Object> unitInfo : unitInfos){
            units.put(MapUtils.getString(unitInfo, "unitName"), BigInteger.valueOf(MapUtils.getLongValue(unitInfo, "id")));
        }
        ApiRest r = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_UNIT_NUM);
        //判断是否单位达到限制条数，如果没达到限制本次导入不限制单位，程序向下执行
        if(!r.getIsSuccess() && "false".equals(isUpdate)){
            //如果达到限制条数，判断是否有新增单位，如果有则返回，没有继续执行。
            for(Map<String, Object> goodsMap : goodsJson){
                String goodsUnitName = MapUtils.getString(goodsMap, "goodsUnitName");
                BigInteger unitId;
                if(goodsUnitName.matches("^[a-zA-Z]*")){
                    unitId = units.get(goodsUnitName.toLowerCase()) == null ? units.get(goodsUnitName.toUpperCase()) : units.get(goodsUnitName.toLowerCase());
                }else{
                    unitId = units.get(goodsUnitName);
                }
                if(unitId==null){
                    return r;
                }
            }
        }
        //子规格
        List<Goods> standardList = new ArrayList<>();
        for(Map<String, Object> goodsMap : goodsJson){
            i++;
//          Map goodsMap=(Map)goods;
            Goods newGoods = new Goods();
            newGoods.setTenantId(tenantId);
            newGoods.setBranchId(branchId);
            newGoods.setLastUpdateBy("System");
            newGoods.setLastUpdateAt(new Date());
            newGoods.setCreateAt(new Date());
            newGoods.setCreateBy("System");
            String categoryName = MapUtils.getString(goodsMap, "categoryName");
            Validate.notEmpty(categoryName, "分类名称不能为空！");
            BigInteger categoryId = categorys.get(categoryName);
            if(categoryId == null){
                if("false".equals(isUpdate)) {
                    //如果分类id为空比较查找出来的所有商品分类名称中有没有这个id如果有则将id赋值如果没有添加新的分类
                    Map<String, String> addCategoryParams = new HashMap<String, String>();
                    addCategoryParams.put("parentId", "-1");
                    addCategoryParams.put("parentName", "所有分类");
                    addCategoryParams.put("catName", categoryName);
                    addCategoryParams.put("tenantId", "" + tenantId);
                    addCategoryParams.put("branchId", "" + branchId);
                    addCategoryParams.put("tenantBusiness", tenantBusiness);
                    ApiRest addCategoryResult = new ApiRest();
                    apiRest = categoryService.addCategory(addCategoryParams);
                    Category category = (Category) apiRest.getData();
                    if (category != null && category.getId() != null) {
                        categorys.put(categoryName, category.getId());
                        newGoods.setCategoryName(categoryName);
                        newGoods.setCategoryId(category.getId());
                    } else {
                        continue;
                    }
                  }else{
                    continue;
                }

            }
            else{
                newGoods.setCategoryName(categoryName);
                newGoods.setCategoryId(categoryId);
            }

            String barCode = MapUtils.getString(goodsMap, "barCode");
            /*ApiRest rest=SaaSApi.findTenantById(tenantId);
               if (!rest.getIsSuccess()) {
                   return rest;
               }
               Tenant tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
               if(tenant!=null&&tenant.getBusiness1().equals("2")){
                   ApiRest res=publicWSService.barCreator(params);
                   if(!res.getIsSuccess()){
                       barCode=res.getData().toString();
                   }
               }*/
            /*if(barCode.equals("")){
                   newGoods.setBarCode(newGoods.getGoodsCode());
               }else {
                   newGoods.setBarCode(barCode);
               }*/
            newGoods.setBarCode(barCode);
            java.text.NumberFormat nf = new DecimalFormat("##0.00");

            String goodsName = MapUtils.getString(goodsMap, "goodsName");
            Validate.notEmpty(goodsName, "名称不能为空！");
            newGoods.setGoodsName(goodsName);

            /*Validate.notEmpty(goodsMap.get("goodsUnitName").toString(),"单位不能为空！");
            GoodsUnit goodsUnit=goodsUnitMapper.getByName(tenantId,goodsMap.get("goodsUnitName").toString());
            if(goodsUnit==null){
                continue;
            }else{
                newGoods.setGoodsUnitName(goodsMap.get("goodsUnitName").toString());
                newGoods.setGoodsUnitId(goodsUnit.getId());
            }*/

            String goodsUnitName = MapUtils.getString(goodsMap, "goodsUnitName");
            Validate.notEmpty(goodsUnitName, "单位不能为空！");
            BigInteger unitId;
            if(goodsUnitName.matches("^[a-zA-Z]*")){
                unitId = units.get(goodsUnitName.toLowerCase()) == null ? units.get(goodsUnitName.toUpperCase()) : units.get(goodsUnitName.toLowerCase());
            }else{
                unitId = units.get(goodsUnitName);
            }
            if(unitId == null){
                if("false".equals(isUpdate)){
                Map<String,String> getNextUnitCodeParams=new HashMap();
                ApiRest getNextUnitCodeResult=new ApiRest();
                getNextUnitCodeParams.put("tenantId",""+tenantId);
                getNextUnitCodeResult= goodsUnitService.getNextUnitCode(getNextUnitCodeParams);
                if(getNextUnitCodeResult!=null){
                    ApiRest saveOrUpdateUnitResult=new ApiRest();
                    Map<String,String> saveOrUpdateUnitParams=new HashMap();
                    saveOrUpdateUnitParams.put("unitCode",getNextUnitCodeResult.getData().toString());
                    saveOrUpdateUnitParams.put("unitName",goodsUnitName);
                    saveOrUpdateUnitParams.put("tenantId",tenantId.toString());
                    saveOrUpdateUnitParams.put("branchId",branchId.toString());
                    saveOrUpdateUnitResult= goodsUnitService.saveUnit(saveOrUpdateUnitParams);
                    if(saveOrUpdateUnitResult!=null &&saveOrUpdateUnitResult.getIsSuccess()==true){
                        GoodsUnit getByNameResult= new GoodsUnit();
                        getByNameResult= goodsUnitMapper.getByName(tenantId,goodsUnitName);
                        if(getByNameResult!=null){
                            units.put(goodsUnitName,getByNameResult.getId());
                            newGoods.setGoodsUnitName(goodsUnitName);
                            newGoods.setGoodsUnitId(getByNameResult.getId());
                        }else{
                            continue;
                        }
                    }else{
                        throw new RuntimeException(saveOrUpdateUnitResult.getError());
                    }
                }
                }else{
                    continue;
                }
                //如果id为空则添加单位
            }
            else{
                newGoods.setGoodsUnitName(goodsUnitName);
                newGoods.setGoodsUnitId(unitId);
            }

            Double doublePurchasingPrice = MapUtils.getDouble(goodsMap, "purchasingPrice");
            Validate.notNull(doublePurchasingPrice, "进货价不能为空！");
            BigDecimal purchasingPrice = BigDecimal.valueOf(doublePurchasingPrice);

            newGoods.setPurchasingPrice(purchasingPrice);

            Double doubleSalePrice = MapUtils.getDouble(goodsMap, "salePrice");
            BigDecimal salePrice = BigDecimal.valueOf(doubleSalePrice);
            Validate.notNull(salePrice, "售价不能为空！");

            Double doubleVipPrice = MapUtils.getDouble(goodsMap, "vipPrice");
            Validate.notNull(doubleVipPrice, "会员价不能为空！");
            BigDecimal vipPrice = BigDecimal.valueOf(doubleVipPrice);

            newGoods.setSalePrice(salePrice);
            if(vipPrice.compareTo(BigDecimal.ZERO) == 0){
                newGoods.setVipPrice(salePrice);
                newGoods.setVipPrice1(salePrice);
                newGoods.setVipPrice2(salePrice);
            }
            else{
                newGoods.setVipPrice(vipPrice);
                newGoods.setVipPrice1(vipPrice);
                newGoods.setVipPrice2(vipPrice);
            }

            Integer goodsStatus = MapUtils.getInteger(goodsMap, "goodsStatus");
            Validate.notNull(goodsStatus, "状态不能为空！");

            newGoods.setGoodsStatus(goodsStatus);

            Double shippingPrice1 = MapUtils.getDouble(goodsMap, "shippingPrice1");
            Validate.notNull(shippingPrice1, "配送价不能为空！");

            BigDecimal shippingPrice = BigDecimal.valueOf(shippingPrice1);
            if(vipPrice.compareTo(BigDecimal.ZERO) == 0){
                newGoods.setShippingPrice1(salePrice);
                newGoods.setShippingPrice2(salePrice);
            }
            else{
                newGoods.setShippingPrice1(shippingPrice);
                newGoods.setShippingPrice2(shippingPrice);
            }
            /*java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[1-9][0-9]*");
               java.util.regex.Matcher match=pattern.matcher(weighPluStr);
               if(!((match.matches()&&Integer.valueOf(weighPluStr)<21&&Integer.valueOf(weighPluStr)>0))){
                   apiRest.setIsSuccess(false);
                   apiRest.setError("称重PLU格式错误，请录入1-3999的整数！");
                   return apiRest;
               }*/
           /* if(vipPrice.compareTo(salePrice) > 0*//* || vipPrice.compareTo(purchasingPrice) < 0*//*){
                apiRest.setIsSuccess(false);
//                    apiRest.setError("会员价不能低于进价并且不能高于售价");
                apiRest.setError("会员价不能高于售价");
                return apiRest;
            }*/

            String weighPluStr = MapUtils.getString(goodsMap, "weighPlu");
            String mnemonic = MapUtils.getString(goodsMap, "mnemonic");
            Validate.notEmpty(mnemonic, "助记码不能为空！");
            newGoods.setMnemonic(mnemonic);

            String goodsCode = MapUtils.getString(goodsMap, "goodsCode");
            if("true".equals(isUpdate)){
                if(goodsCode != null || !"".equals(goodsCode)){
                    params.put("goodsCode", goodsCode);
                    params.put("standardName", MapUtils.getString(goodsMap, "standardName"));
                    newGoods.setStandardName(MapUtils.getString(goodsMap, "standardName"));
                }else{
                    params.put("barCode", barCode);
                    params.put("goodsName", goodsName);
                    params.put("standardName", MapUtils.getString(goodsMap, "standardName"));
                    newGoods.setStandardName(MapUtils.getString(goodsMap, "standardName"));
                }
                params.put("isImport", 1);
                Goods goods = goodsMapper.findByCondition(params);
                if(goods != null){
                    if(StringUtils.isBlank(weighPluStr)){
                        newGoods.setWeighPlu(null);
                        newGoods.setIsWeigh(false);
                    }
                    else{
                        Integer weighPlu = Integer.valueOf(weighPluStr);
                        boolean checkPlu = checkPlu(branchId, tenantId, weighPlu, goods.getId());
                        if(checkPlu){
                            newGoods.setWeighPlu(weighPlu);
                            newGoods.setIsWeigh(true);
                        }
                        else{
                            apiRest.setIsSuccess(false);
                            apiRest.setError("称重PLU码“" + weighPlu + "”已经存在，请修改");
                            return apiRest;
                        }
                    }
                    BigInteger id = goods.getId();
                    newGoods.setPackingUnitId(goods.getPackingUnitId());
                    newGoods.setPackingUnitName(goods.getPackingUnitName());
                    goods = newGoods;
                    newGoods.setId(id);
                    goods.setGoodsCode(goodsCode);
                    goods.setCreateAt(null);
                    goods.setCreateBy(null);
                    int m = goodsMapper.update(goods);
                    if(m <= 0){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("更新已存在商品失败!");
                        return apiRest;
                    }
                    u++;
                    continue;
                }else{
                    if(StringUtils.isBlank(weighPluStr)){
                        newGoods.setWeighPlu(null);
                        newGoods.setIsWeigh(false);
                    }
                    else{
                        Integer weighPlu = Integer.valueOf(weighPluStr);
                        boolean checkPlu = checkPlu(branchId, tenantId, weighPlu, null);
                        if(checkPlu){
                            newGoods.setWeighPlu(weighPlu);
                            newGoods.setIsWeigh(true);
                        }
                        else{
                            apiRest.setIsSuccess(false);
                            apiRest.setError("称重PLU码“" + weighPlu + "”已经存在，请修改");
                            return apiRest;
                        }
                    }
                }
            }else{
                if(StringUtils.isBlank(weighPluStr)){
                    newGoods.setWeighPlu(null);
                    newGoods.setIsWeigh(false);
                }
                else{
                    Integer weighPlu = Integer.valueOf(weighPluStr);
                    boolean checkPlu = checkPlu(branchId, tenantId, weighPlu, null);
                    if(checkPlu){
                        newGoods.setWeighPlu(weighPlu);
                        newGoods.setIsWeigh(true);
                    }
                    else{
                        apiRest.setIsSuccess(false);
                        apiRest.setError("称重PLU码“" + weighPlu + "”已经存在，请修改");
                        return apiRest;
                    }
                }
            }
            if(goodsCode == null || "".equals(goodsCode)){
                String inputMaxCode = this.getinputGoodsCode(nowCode, i);
                newGoods.setGoodsCode(inputMaxCode);
            }else{
                newGoods.setGoodsCode(goodsCode);
                if(MapUtils.getString(goodsMap, "parentId") == null || "".equals(MapUtils.getString(goodsMap, "parentId"))){
                    List gList = goodsMapper.findParentIdByCode(tenantId, branchId, goodsCode);
                    if(gList.size() > 0){
                        z++;
                        continue;
                    }
                }
            }
            newGoods.setCombinationType(0);
            newGoods.setIsForPoints(true);
            newGoods.setIsDsc(true);
            if("5".equals(business)){
                newGoods.setIsStore(true);
            }else{
                newGoods.setIsStore(false);
            }
            newGoods.setIsPricetag(false);
            newGoods.setIsRecommended(false);
            newGoods.setIsChangeprice(false);
            newGoods.setIsCustomPrice(false);
            newGoods.setIsTakeout(false);
            newGoods.setIsNewgood(false);
            newGoods.setVersion(BigInteger.valueOf(0));
            newGoods.setGoodsType(1);
            newGoods.setPriceType(0);
            if(MapUtils.getString(goodsMap, "parentId") != null && !"".equals(MapUtils.getString(goodsMap, "parentId"))){
                newGoods.setParentId(BigInteger.valueOf(Long.parseLong(MapUtils.getString(goodsMap, "parentId"))));
                newGoods.setStandardName(MapUtils.getString(goodsMap, "standardName"));
                params.put("parentId", MapUtils.getString(goodsMap, "parentId"));
                //判断是否已存在该商品
                Map map = new HashMap();
                map.put("tenantId", tenantId);
                map.put("branchId", branchId);
                map.put("goodsName", newGoods.getGoodsName());
                map.put("barCode", newGoods.getBarCode());
                map.put("standardName", newGoods.getStandardName());
                List<Goods> goods = goodsMapper.findListByCondition(map);
                if(goods == null || goods.size() == 0){
                    standardList.add(newGoods);
                }else{
                    z++;
                }
                continue;
            }else{
                newGoods.setParentId(BigInteger.ZERO);
                newGoods.setStandardName(MapUtils.getString(goodsMap, "standardName"));
                params.put("parentId", MapUtils.getString(goodsMap, "parentId"));
            }
            //判断是否已存在该商品
            Map map = new HashMap();
            map.put("tenantId", tenantId);
            map.put("branchId", branchId);
            map.put("goodsName", newGoods.getGoodsName());
            map.put("barCode", newGoods.getBarCode());
            map.put("standardName", newGoods.getStandardName());
            List<Goods> goods = goodsMapper.findListByCondition(map);
            if("true".equals(isUpdate) && goods.size() > 0){
                Goods g = goods.get(0);
                g.setGoodsCode(goodsCode);
                g.setBarCode(barCode);
                g.setMnemonic(mnemonic);
                g.setGoodsName(goodsName);
                g.setCategoryId(categoryId);
                g.setCategoryName(categoryName);
                g.setGoodsUnitId(unitId);
                g.setGoodsUnitName(goodsUnitName);
                g.setPurchasingPrice(purchasingPrice);
                g.setSalePrice(salePrice);
                g.setShippingPrice1(shippingPrice);
                g.setShippingPrice2(shippingPrice);
                g.setVipPrice(vipPrice);
                g.setVipPrice1(vipPrice);
                g.setVipPrice2(vipPrice);
                g.setGoodsStatus(goodsStatus);
                g.setWeighPlu(StringUtils.isBlank(weighPluStr) == true ? null : Integer.valueOf(weighPluStr));
                int x = goodsMapper.update(g);
                if(x <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("更新已存在商品失败!");
                    return apiRest;
                }
                u++;
            }else{
                if(goods == null || goods.size() == 0){
                    goodsList.add(newGoods);
                }else{
                    z++;
                }
            }
        }

        if(goodsList.size() == 0 && u == 0 && standardList.size() == 0){
            if(z == 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("可导入数据为空，请确认模版数据是否正确！");
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError(z + "条商品已存在，不再重复导入！");
            }
        }
        else{
            int k = 0;
            if(goodsList.size() > 0 && goodsList != null){
                k = goodsMapper.saveList(goodsList);
            }
            if(standardList != null && standardList.size() > 0){
                for(int m = 0; m < standardList.size(); m++){
                    Goods goods = standardList.get(m);
                    List<Map> list = goodsMapper.findParentIdByCode(tenantId, branchId, goods.getGoodsCode());
                    BigInteger parentId = null;
                    if(list != null && list.size() > 0){
                        parentId = BigInteger.valueOf(Long.parseLong(list.get(0).get("id").toString()));
                    }
                    if(parentId != null){
                        goods.setParentId(parentId);
                    }
                    else{
                        goods.setParentId(BigInteger.ZERO);
                    }
                    goodsMapper.insert(goods);
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("成功导入" + k + "条数据，更新" + u + "条数据，" + (goodsNum - k - u) + "条导入失败！");
        }
        return apiRest;
    }

    /**
     * 批量导入数据
     *
     * @param params "goods" -> "{"barCode":"1132131","isCustomPrice":"true"}"
     * @return
     */
    public ApiRest saveGoods(Map params){
        ApiRest apiRest = new ApiRest();
        JSONObject goodsJson = JSONObject.fromObject(params.get("goods"));
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        Map codeMap = new HashMap();
        codeMap.put("categoryId", goodsJson.get("categoryId"));
        codeMap.put("tenantId", tenantId);
        ApiRest codeRest = getGoodsCode(codeMap);
        Map unitMap = new HashMap();
        unitMap.put("id", BigInteger.valueOf(Long.valueOf(goodsJson.get("goodsUnitId").toString())));
        unitMap.put("tenantId", tenantId);
        GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
        if(goodsUnit == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询单位失败！");
            return apiRest;
        }
        Map catMap = new HashMap();
        catMap.put("id", BigInteger.valueOf(Long.valueOf(goodsJson.get("categoryId").toString())));
        catMap.put("tenantId", tenantId);
        Category cat = categoryMapper.queryCatById(catMap);
        if(cat == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询分类失败！");
            return apiRest;
        }
        Goods goods = new Goods();
        ApiRest re = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_GOODS_NUM);
        if(!codeRest.getIsSuccess()){
            apiRest = codeRest;
            return apiRest;
        }
        goods.setGoodsCode(codeRest.getData().toString());
        if(!re.getIsSuccess()){
            apiRest = re;
            return apiRest;
        }
        goods.setIsRecommended(false);
        goods.setGoodsName(goodsJson.get("goodsName").toString());
        goods.setCategoryId(BigInteger.valueOf(Long.valueOf(goodsJson.get("categoryId").toString())));
        goods.setCategoryName(cat.getCatName());
        goods.setTenantId(tenantId);
        goods.setMnemonic(createMnemonic(goods.getGoodsName()));
        goods.setPhoto("");
        goods.setSpecGroupId(null);
        goods.setGoodsUnitId(BigInteger.valueOf(Long.valueOf(goodsJson.get("goodsUnitId").toString())));
        goods.setGoodsUnitName(goodsUnit.getUnitName());
        goods.setSalePrice(BigDecimal.valueOf(Double.valueOf(goodsJson.get("salePrice").toString())));
        goods.setVipPrice(BigDecimal.valueOf(Double.valueOf(goodsJson.get("vipPrice1").toString())));
        goods.setVipPrice2(BigDecimal.valueOf(Double.valueOf(goodsJson.get("vipPrice1").toString())));
        goods.setVipPrice1(BigDecimal.valueOf(Double.valueOf(goodsJson.get("vipPrice1").toString())));
        goods.setPurchasingPrice(BigDecimal.valueOf(Double.valueOf(goodsJson.get("purchasingPrice").toString())));
        goods.setIsPricetag(false);
        goods.setBarCode(goodsJson.get("barCode").toString());
        goods.setIsStore(goodsJson.get("isStore").equals("true"));
        goods.setCombinationType(0);
        goods.setVersion(BigInteger.valueOf(0));
        goods.setGoodsType(1);
        goods.setIsForPoints(goodsJson.get("isDsc").equals("true"));
        goods.setIsDsc(goodsJson.get("isDsc").equals("true"));
        goods.setIsRecommended(false);
        goods.setIsWeigh(goodsJson.get("isWeigh").equals("true"));
        goods.setIsNewgood(false);
        goods.setIsCustomPrice(goodsJson.get("isCustomPrice") != null && goodsJson.get("isCustomPrice").equals("true"));
        goods.setIsChangeprice(goodsJson.get("isCustomPrice") != null && goodsJson.get("isCustomPrice").equals("true"));

        goods.setIsTakeout(false);
        goods.setGoodsStatus(0);
        goods.setShippingPrice1(BigDecimal.ZERO);
        goods.setShippingPrice2(BigDecimal.ZERO);
        goods.setBranchId(branchId);
        goods.setPriceType(0);
        goods.setCreateAt(new Date());
        goods.setLastUpdateAt(new Date());
        goods.setCreateBy("System");
        goods.setLastUpdateBy("System");
        goods.setParentId(BigInteger.ZERO);
        goods.setStoreUpLimit(BigDecimal.ZERO);
        goods.setStoreLowLimit(BigDecimal.ZERO);

        int i = goodsMapper.insert(goods);
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增成功！");
            apiRest.setData(goods);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("新增失败！");
            return apiRest;
        }
        ApiRest res = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_GOODS_NUM);
        if(!res.getIsSuccess()){
            apiRest = res;
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 生成助记码（[a-z0-9]）
     *
     * @param value may be null
     * @return 转换小写并且去除^a-z^0-9
     */
    public static String createMnemonic(String value){
        value = StringUtils.trimToEmpty(value);
        String fl = PinYinHelper.stringFirstLetter(value);
        if(fl != null){
            fl = fl.toLowerCase().replaceAll("[^a-z^0-9]", "");
        }
        return fl;
    }

    /**
     * 批量设置菜品公共属性
     *
     * @param params
     * @return
     */
    public ApiRest batchSetup(Map params){
        ApiRest apiRest = new ApiRest();
        Map paramsMap = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        if(params.get("categoryId") != null && !"".equals(params.get("categoryId"))){
            BigInteger categoryId = BigInteger.valueOf(Long.valueOf(params.get("categoryId").toString()));
            Map catMap = new HashMap();
            catMap.put("id", categoryId);
            catMap.put("tenantId", tenantId);
            Category cat = categoryMapper.queryCatById(catMap);
            paramsMap.put("categoryId", categoryId);
            paramsMap.put("categoryName", cat.getCatName());
            if(cat == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询分类失败！");
                return apiRest;
            }
        }
        else{
            paramsMap.put("categoryId", "");
            paramsMap.put("categoryName", "");
        }
        if(params.get("goodsUnitId") != null && !"".equals(params.get("goodsUnitId"))){
            BigInteger goodsUnitId = BigInteger.valueOf(Long.valueOf(params.get("goodsUnitId").toString()));
            Map unitMap = new HashMap();
            unitMap.put("id", goodsUnitId);
            unitMap.put("tenantId", tenantId);
            GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
            if(goodsUnit == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询单位失败！");
                return apiRest;
            }
            paramsMap.put("goodsUnitId", goodsUnitId);
            paramsMap.put("goodsUnitName", goodsUnit.getUnitName());
        }
        else{
            paramsMap.put("goodsUnitId", "");
            paramsMap.put("goodsUnitName", "");
        }
        if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
            //BigInteger specGroupId = BigInteger.valueOf(Long.parseLong(params.get("specGroupId").toString()));
            String specGroupId = params.get("specGroupId").toString();
            String[] specList = specGroupId.split(",");
            String ids = params.get("ids").toString();
            String[] str = ids.split(",");
            for(int i = 0; i < str.length; i++){
                BigInteger goodsId = BigInteger.valueOf(Long.parseLong(str[i]));
                int count = goodsSpecRMapper.findByGoodsId(tenantId, goodsId);
                if(count == 0){
                    for(int s = 0; s < specList.length; s++){
                        String specId = specList[s];
                        GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                        newGoodsSpecR.setGoodsId(goodsId);
                        newGoodsSpecR.setTenantId(tenantId);
                        newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                        int j = goodsSpecRMapper.insert(newGoodsSpecR);
                        if(j <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("修改商品与口味关联失败！");
                            return apiRest;
                        }
                    }
                }else{
                    goodsSpecRMapper.deleteByGoodsId(tenantId, goodsId);
                    for(int s = 0; s < specList.length; s++){
                        String specId = specList[s];
                        GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                        newGoodsSpecR.setGoodsId(goodsId);
                        newGoodsSpecR.setTenantId(tenantId);
                        newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                        int j = goodsSpecRMapper.insert(newGoodsSpecR);
                        if(j <= 0){
                            apiRest.setIsSuccess(false);
                            apiRest.setError("修改商品与口味关联失败！");
                            return apiRest;
                        }
                    }
                }
            }
        }

        /*新增供应商*/
        BigInteger supplierId = null;
        if(params.get("supplierId") != null && !"".equals(params.get("supplierId"))){
            supplierId = BigInteger.valueOf(Long.parseLong(params.get("supplierId").toString()));
        }
        paramsMap.put("supplierId", supplierId);

        paramsMap.put("specGroupId", params.get("specGroupId"));
        paramsMap.put("tenantId", tenantId);
        paramsMap.put("branchId", branchId);
        paramsMap.put("goodsStatus", params.get("goodsStatus"));
        paramsMap.put("combinationType", params.get("combinationType"));
        paramsMap.put("purchasingPrice", params.get("purchasingPrice"));
        paramsMap.put("salePrice", params.get("salePrice"));
        paramsMap.put("isForPoints", params.get("isForPoints"));
        paramsMap.put("isDsc", params.get("isForPoints"));
        paramsMap.put("isStore", params.get("isStore"));
        paramsMap.put("isPricetag", params.get("isPricetag"));
        paramsMap.put("isCustomPrice", params.get("isChangePrice"));
        paramsMap.put("isTakeout", params.get("isTakeout"));
        paramsMap.put("isWeigh", params.get("isWeigh"));
        paramsMap.put("isNewgood", params.get("isNewgood"));
        paramsMap.put("isRecommended", params.get("isRecommended"));
        paramsMap.put("lastUpdateAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(null != params.get("isForPoints") && "0".equals(params.get("isForPoints")) ){
            paramsMap.put("isAllowVipDiscount","0");
        }
        if(null!=params.get("isAllowChangePrice")&& "1".equals(params.get("isAllowChangePrice")) && null==params.get("isChangePrice")){
            paramsMap.put("isAllowChangePrice",params.get("isAllowChangePrice"));
            paramsMap.put("isChangeprice", "0");
            //当是否改价为否时设置会员折上折为0
        }else if(null==params.get("isAllowChangePrice")||"0".equals(params.get("isAllowChangePrice"))){
            paramsMap.put("isAllowChangePrice",params.get("isAllowChangePrice"));
            paramsMap.put("isAllowVipDiscount","0");
        }
        //当时价商品为是时设置是否改价、会员折上折为否
        if(null!=params.get("isChangePrice")&&"1".equals(params.get("isChangePrice")) && null==params.get("isAllowChangePrice")){
            paramsMap.put("isChangeprice",params.get("isChangePrice"));
            paramsMap.put("isAllowChangePrice","0");
            paramsMap.put("isAllowVipDiscount","0");
        }else if(null!=params.get("isChangePrice")){
            paramsMap.put("isChangeprice",params.get("isChangePrice"));
        }
        if(params.get("vipPrice") != null && !"".equals(params.get("vipPrice"))){
            float vipPrice = Float.parseFloat(params.get("vipPrice").toString()) / (float)100;
            paramsMap.put("vipPrice", vipPrice);
        }
        if(params.get("shippingPrice1") != null && !"".equals(params.get("shippingPrice1"))){
            float shippingPrice1 = Float.parseFloat(params.get("shippingPrice1").toString()) / (float)100;
            paramsMap.put("shippingPrice1", shippingPrice1);
        }
        paramsMap.put("ids", params.get("ids"));
        if(!"".equals(params.get("ids")) && params.get("ids") != null){
            int count1 = goodsMapper.batchSetup(paramsMap);
            if(count1 <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("批量设置失败！");
                return apiRest;
            }
        }
        if(!"".equals(params.get("noStoreIds")) && params.get("noStoreIds") != null){
            paramsMap.put("isStore", null);
            paramsMap.put("ids", params.get("noStoreIds"));
            int count2 = goodsMapper.batchSetup(paramsMap);
            if(count2 <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("批量设置失败！");
                return apiRest;
            }
        }
        if(paramsMap.get("isChangeprice") != null && "1".equals(paramsMap.get("isChangeprice"))){
            int count3 = goodsMapper.updateRelated(paramsMap);
            if(count3 < 0){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("批量设置失败！");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("批量设置成功！");
        return apiRest;
    }

    /**
     * 删除子规格
     *
     * @param params
     */
    public ApiRest deleteSpec(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger specId = BigInteger.valueOf(Long.parseLong((String)params.get("specId")));
        params.put("specId", specId);
        int count = goodsMapper.deleteSpec(params);
        if(count < 0){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("删除子规格失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除子规格成功！");
        return apiRest;
    }

    /**
     * 添加商品档案-并添加到现有菜牌中（POS）
     */
    public ApiRest addGoodsAndMenu(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        BigInteger categoryId = BigInteger.valueOf(Long.parseLong((String)params.get("categoryId")));
        BigInteger goodsUnitId = BigInteger.valueOf(Long.parseLong((String)params.get("goodsUnitId")));
        String goodsName = params.get("goodsName").toString();
        String mnemonic = params.get("mnemonic") == null ? "" : params.get("mnemonic").toString();
        Integer goodsStatus = Integer.valueOf(params.get("goodsStatus").toString());
        BigDecimal salePrice = BigDecimal.valueOf(Double.valueOf((String)params.get("salePrice")));
        BigDecimal vipPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("vipPrice")));
        BigDecimal shippingPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("shippingPrice1")));
        BigDecimal purchasingPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("purchasingPrice")));
        Integer isForPoints = Integer.valueOf(params.get("isForPoints").toString());
        Integer isTakeout = Integer.valueOf(params.get("isTakeout").toString());
        Integer isStore = Integer.valueOf(params.get("isStore").toString());
        Integer isPricetag = Integer.valueOf(params.get("isPricetag").toString());
        Integer isRecommended = Integer.valueOf(params.get("isRecommended").toString());
        Integer isNewgood = Integer.valueOf(params.get("isNewgood").toString());
        Integer isChangeprice = Integer.valueOf(params.get("isChangeprice").toString());
        Integer combinationType = Integer.valueOf(params.get("combinationType").toString());
        String photo = "";
        String standardName = "";
        if(params.get("standardName") != null && !"".equals(params.get("standardName"))){
            standardName = params.get("standardName").toString();
        }
        /*库存管理*/
        BigDecimal storeUpLimit = BigDecimal.ZERO;
        if(params.get("storeUpLimit") != null && !"".equals(params.get("storeUpLimit"))){
            storeUpLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeUpLimit").toString()));
        }
        BigDecimal storeLowLimit = BigDecimal.ZERO;
        if(params.get("storeLowLimit") != null && !"".equals(params.get("storeLowLimit"))){
            storeLowLimit = BigDecimal.valueOf(Double.valueOf(params.get("storeLowLimit").toString()));
        }

        List<Map> specsList = new ArrayList<>();
        if(params.get("specsList") != null && !"".equals(params.get("specsList"))){
            specsList = JSONArray.fromObject(params.get("specsList"));
        }
        ApiRest rea = SaaSApi.findTenantById(tenantId);
        String barCode = "";
        if(!rea.getIsSuccess()){
            return rea;
        }
        if(params.get("barCode") != null && !params.get("barCode").equals("")){
            barCode = params.get("barCode").toString();
        }
        /*Tenant tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rea.getData()).get("tenant"), Tenant.class);
        if(tenant!=null&&tenant.getBusiness1().equals("2")){
            ApiRest res=publicWSService.barCreator(params);
            if(!res.getIsSuccess()){
                return res;
            }else {
                barCode=res.getData().toString();
            }
        }*/
        if(params.get("photo") != null && !params.get("photo").equals("")){
            photo = params.get("photo").toString();
        }
        String specGroupId = "";
        if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
            specGroupId = params.get("specGroupId").toString();
        }
        String[] specList = specGroupId.split(",");
        if(categoryMapper.countByParentIdAndTenantId(categoryId, tenantId) > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("此分类下有二级分类，不能添加商品");
            return apiRest;
        }
        Map unitMap = new HashMap();
        unitMap.put("id", goodsUnitId);
        unitMap.put("tenantId", tenantId);
        GoodsUnit goodsUnit = goodsUnitMapper.queryUnitById(unitMap);
        if(goodsUnit == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询单位失败！");
            return apiRest;
        }
        Map catMap = new HashMap();
        catMap.put("id", categoryId);
        catMap.put("tenantId", tenantId);
        Category cat = categoryMapper.queryCatById(catMap);
        if(cat == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询分类失败！");
            return apiRest;
        }
        /*包装单位*/
        BigInteger packingUnitId = null;
        if(params.get("packingUnitId") != null && !"".equals(params.get("packingUnitId"))){
            packingUnitId = BigInteger.valueOf(Long.parseLong(params.get("packingUnitId").toString()));
        }
        BigDecimal unitRelation = BigDecimal.ONE;
        if(params.get("unitRelation") != null && !"".equals(params.get("unitRelation"))){
            unitRelation = BigDecimal.valueOf(Double.valueOf(params.get("unitRelation").toString()));
        }
        Map packingMap = new HashMap();
        packingMap.put("id", packingUnitId);
        packingMap.put("tenantId", tenantId);
        GoodsUnit packingUnit = new GoodsUnit();
        if(packingUnitId != null){
            packingUnit = goodsUnitMapper.queryUnitById(packingMap);
            if(packingUnit == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询单位失败！");
                return apiRest;
            }
        }

        Map codeMap = new HashMap();
        codeMap.put("categoryId", categoryId);
        codeMap.put("tenantId", tenantId);
        ApiRest codeRest = getGoodsCode(codeMap);
        Goods goods = new Goods();
        ApiRest re = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_GOODS_NUM);
        if(!codeRest.getIsSuccess()){
            apiRest = codeRest;
            return apiRest;
        }
        goods.setGoodsCode(codeRest.getData().toString());
        if(!barCode.equals("")){
            goods.setBarCode(barCode);
        }
        else{
            goods.setBarCode(goods.getGoodsCode());
        }
        if(!re.getIsSuccess()){
            apiRest = re;
            return apiRest;
        }
        if(isRecommended == 1){
            ApiRest rest = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_RECOMMEND_NUM);
            if(!rest.getIsSuccess()){
                apiRest = rest;
                return apiRest;
            }
            ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_RECOMMEND_NUM);
            if(!r.getIsSuccess()){
                apiRest = r;
                return apiRest;
            }
        }
        if(params.get("isWeigh") != null && !params.get("isWeigh").equals("")){
            goods.setIsWeigh(params.get("isWeigh").equals("1"));
        }
        goods.setGoodsName(goodsName);
        goods.setCategoryId(categoryId);
        goods.setTenantId(tenantId);
        goods.setMnemonic(mnemonic);
        goods.setPhoto(photo);
        goods.setSpecGroupId(specGroupId == "" ? null : specGroupId);
        goods.setGoodsUnitId(goodsUnitId);
        goods.setGoodsUnitName(goodsUnit.getUnitName());
        goods.setSalePrice(salePrice);
        goods.setVipPrice(vipPrice);
        goods.setVipPrice2(vipPrice);
        goods.setVipPrice1(vipPrice);
        goods.setPurchasingPrice(purchasingPrice);
        goods.setIsPricetag(isPricetag == 1);
        goods.setCategoryName(cat.getCatName());
        if(combinationType == 1 || combinationType == 2){
            goods.setIsStore(true);
        }
        else{
            goods.setIsStore(isStore == 1);
        }
        goods.setCombinationType(combinationType);
        goods.setVersion(BigInteger.valueOf(0));
        goods.setGoodsType(1);
        goods.setIsForPoints(isForPoints == 1);
        goods.setIsDsc(isForPoints == 1);
        goods.setIsRecommended(isRecommended == 1);
        goods.setIsNewgood(isNewgood == 1);
        goods.setIsChangeprice(isChangeprice == 1);
        goods.setIsCustomPrice(isChangeprice == 1);
        goods.setIsTakeout(isTakeout == 1);
        goods.setGoodsStatus(goodsStatus);
        goods.setShippingPrice1(shippingPrice);
        goods.setShippingPrice2(shippingPrice);
        goods.setBranchId(branchId);
        goods.setPriceType(0);
        goods.setCreateAt(new Date());
        goods.setLastUpdateAt(new Date());
        goods.setCreateBy("System");
        goods.setLastUpdateBy("System");
        goods.setParentId(BigInteger.ZERO);
        if(params.get("isWeigh") != null && !params.get("isWeigh").equals("")){
            goods.setIsWeigh(params.get("isWeigh").equals("1"));
        }
        /*规格名*/
        goods.setStandardName(standardName);
        /*新增库存管理*/
        goods.setStoreUpLimit(storeUpLimit);
        goods.setStoreLowLimit(storeLowLimit);
        /*新增包装单位*/
        if(packingUnit != null){
            goods.setPackingUnitId(packingUnit.getId());
            goods.setPackingUnitName(packingUnit.getUnitName());
        }
        goods.setUnitRelation(unitRelation);
        int i = goodsMapper.insert(goods);
        /*获取刚刚新增商品的ID*/
        /*BigInteger id = goods.getId();*/
        /*保存子规格商品*/
        /*if(specsList != null && specsList.size()>0){
            Goods goods1 = new Goods();
            goods1.setParentId(id);
            goods1.setGoodsCode(goods.getGoodsCode());
            goods1.setGoodsName(goods.getGoodsName());
            goods1.setCategoryId(goods.getCategoryId());
            goods1.setCategoryName(goods.getCategoryName());
            goods1.setTenantId(goods.getTenantId());
            goods1.setBranchId(goods.getBranchId());
            goods1.setMnemonic(goods.getMnemonic());
            goods1.setPhoto(goods.getPhoto());
            goods1.setSpecGroupId(goods.getSpecGroupId());
            goods1.setGoodsUnitId(goods.getGoodsUnitId());
            goods1.setGoodsUnitName(goods.getGoodsUnitName());
            goods1.setIsPricetag(goods.getIsPricetag());
            goods1.setIsStore(goods.getIsStore());
            goods1.setCombinationType(goods.getCombinationType());
            goods1.setVersion(goods.getVersion());
            goods1.setGoodsType(goods.getGoodsType());
            goods1.setIsForPoints(goods.getIsForPoints());
            goods1.setIsDsc(goods.getIsDsc());
            goods1.setIsRecommended(goods.getIsRecommended());
            goods1.setIsNewgood(goods.getIsNewgood());
            goods1.setIsChangeprice(goods.getIsChangeprice());
            goods1.setIsCustomPrice(goods.getIsCustomPrice());
            goods1.setIsTakeout(goods.getIsTakeout());
            goods1.setGoodsStatus(goods.getGoodsStatus());
            goods1.setPriceType(goods.getPriceType());
            goods1.setCreateAt(new Date());
            goods1.setLastUpdateAt(new Date());
            goods1.setCreateBy(goods.getCreateBy());
            goods1.setLastUpdateBy(goods.getLastUpdateBy());
            goods1.setIsWeigh(goods.getIsWeigh());
            goods1.setBarCode(goods.getBarCode());
            *//*新增库存管理*//*
            goods1.setStoreUpLimit(goods.getStoreUpLimit());
            goods1.setStoreLowLimit(goods.getStoreLowLimit());
            *//*新增包装单位*//*
            goods1.setPackingUnitId(goods.getPackingUnitId());
            goods1.setPackingUnitName(goods.getPackingUnitName());
            goods1.setUnitRelation(goods.getUnitRelation());
            for(int j=0; j<specsList.size(); j++){
                Map<String, String> specMap = specsList.get(j);
                String specStandardName = specMap.get("specStandardName");
                BigDecimal specPurchasingPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specPurchasingPrice")));
                BigDecimal specSalePrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specSalePrice")));
                BigDecimal specVipPrice = BigDecimal.valueOf(Double.valueOf(specMap.get("specVipPrice")));
                BigDecimal specShippingPrice = BigDecimal.valueOf(Double.valueOf( specMap.get("specShippingPrice")));
                goods1.setId(null);
                goods1.setStandardName(specStandardName);
                goods1.setPurchasingPrice(specPurchasingPrice);
                goods1.setSalePrice(specSalePrice);
                goods1.setVipPrice(specVipPrice);
                goods1.setVipPrice1(specVipPrice);
                goods1.setVipPrice2(specVipPrice);
                goods1.setShippingPrice1(specShippingPrice);
                goods1.setShippingPrice2(specShippingPrice);
                int i1 = goodsMapper.insert(goods1);
                if(i1<=0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("新增子规格时失败！");
                }
            }
        }*/
        if(i > 0){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增成功！");
            apiRest.setData(goods);
            //查询该机构的菜牌
            Map menuParams = new HashMap();
            menuParams.put("tenantId", tenantId);
            menuParams.put("branchId", branchId);
            menuParams.put("status", 1);
            List<Map> list = menuMapper.queryMenu(menuParams);
            for(int m = 0; m < list.size(); m++){
                Map menuMap = list.get(m);
                BigInteger menuId = BigInteger.valueOf(Long.parseLong(menuMap.get("id").toString()));
                MenuGoods menuGoods = new MenuGoods();
                menuGoods.setMenuId(menuId);
                menuGoods.setGoodsCode(goods.getGoodsCode());
                menuGoods.setGoodsId(goods.getId());
                menuGoods.setGoodsName(goods.getGoodsName());
                menuGoods.setSalePrice(goods.getSalePrice());
                menuGoods.setVipPrice(goods.getVipPrice());
                menuGoods.setVipPrice2(goods.getVipPrice2());
                menuGoods.setTenantId(tenantId);
                menuGoods.setLastUpdateAt(new Date());
                menuGoods.setCreateAt(new Date());
                menuGoods.setLastUpdateBy("System");
                menuGoods.setCreateBy("System");
                menuGoods.setCreateBranchId(branchId);
                int k = menuMapper.insertMenuGoods(menuGoods);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("菜牌商品添加失败");
                    return apiRest;
                }
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("新增失败！");
            return apiRest;
        }
        if(params.get("specGroupId") != null && !params.get("specGroupId").equals("")){
            for(int s = 0; s < specList.length; s++){
                String specId = specList[s];
                GoodsSpecR newGoodsSpecR = new GoodsSpecR();
                newGoodsSpecR.setGoodsId(goods.getId());
                newGoodsSpecR.setTenantId(tenantId);
                newGoodsSpecR.setSpecGroupId(BigInteger.valueOf(Long.parseLong(specId)));
                int k = goodsSpecRMapper.insert(newGoodsSpecR);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("保存商品与口味关联失败！");
                    return apiRest;
                }
            }
        }
        ApiRest res = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_GOODS_NUM);
        if(!res.getIsSuccess()){
            apiRest = res;
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 商品排序
     *
     * @param params
     * @return
     */
    public ApiRest orderGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger catId = BigInteger.valueOf(Long.valueOf(params.get("catId").toString()));
        String tenantBusiness = null;
        if(params.get("tenantBusiness") != null){
            tenantBusiness = params.get("tenantBusiness").toString();
        }
        JSONArray goodsJson = JSONArray.fromObject(params.get("goodsJson"));
        Map catP=new HashMap();
        catP.put("id",catId);
        catP.put("tenantId",tenantId);
        Category category=categoryMapper.queryCatById(catP);
        BigInteger parentId=category.getParentId();
        JSONArray lastJsonArr = new JSONArray();
        JSONArray lastGoodsJsonArr = new JSONArray();
        List<Map> orderList = categoryMapper.orderedCategory(tenantId, branchId);
        Map<String, String> orderedMap = new HashMap<>();
        for(Map m : orderList){
            orderedMap.put(m.get("catId").toString(), m.get("orderId").toString());
        }
        if(orderedMap.get(catId.toString()) == null){//若分类未排序，提示分类排序
            apiRest.setIsSuccess(false);
            apiRest.setMessage("为了给您提供更好的体验，请先对分类进行排序！");
            return apiRest;
        }
        BigInteger finalOrderId=BigInteger.valueOf(100000000);
        if(parentId.compareTo(BigInteger.valueOf(-1))==0){//此分类是一级分类
            BigInteger parentCatOrderId=BigInteger.valueOf(Long.valueOf(orderedMap.get(catId.toString())));
            if(parentCatOrderId.compareTo(BigInteger.valueOf(100000000))<0){//一级分类是老规则排序
                List<Map> list = categoryMapper.listOrderCategory(tenantId, branchId, tenantBusiness);//查询已经排过的一级分类
                for(Map m : list){
                    BigInteger orderId = m.get("orderId") == null ? BigInteger.ZERO : BigInteger.valueOf(Long.valueOf(m.get("orderId").toString()));
                    BigInteger orderCatId = BigInteger.valueOf(Long.valueOf(m.get("id").toString()));
                    Map<String, Object> firstMap = new HashMap<>();
                    if(orderId.compareTo(BigInteger.ZERO) == 0){
                        orderId = BigInteger.valueOf(100000000);
                    }
                    else if(orderId.compareTo(BigInteger.valueOf(100000000)) < 0){
                        orderId = orderId.multiply(BigInteger.valueOf(100000000));
                    }
                    if(catId.compareTo(orderCatId) == 0){
                        finalOrderId = orderId;
                    }
                    firstMap.put("orderId", orderId);
                    firstMap.put("catId", orderCatId);
                    lastJsonArr.add(firstMap);//更新一级分类为新规则
                }
            }else{//一级分类是新排序
                finalOrderId=parentCatOrderId;
            }
        }else{//此分类是二级分类
            BigInteger secondCatOrderId=BigInteger.valueOf(Long.valueOf(orderedMap.get(catId.toString())));
            if(secondCatOrderId.compareTo(BigInteger.valueOf(100000000))<0){//二级分类是老规则排序
                if(orderedMap.get(parentId.toString()) == null){//若分类未排序，提示分类排序
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("为了给您提供更好的体验，请先对分类进行排序！");
                    return apiRest;
                }
                BigInteger firstOrderId=BigInteger.valueOf(100000000);
                BigInteger firstCatOrderId=BigInteger.valueOf(Long.valueOf(orderedMap.get(parentId.toString())));
                if(firstCatOrderId.compareTo(BigInteger.valueOf(100000000))<0){//一级分类是老规则排序
                    List<Map> list = categoryMapper.listOrderCategory(tenantId, branchId, tenantBusiness);//查询已经排过的一级分类
                    for(Map m : list){
                        BigInteger orderId = m.get("orderId") == null ? BigInteger.ZERO : BigInteger.valueOf(Long.valueOf(m.get("orderId").toString()));
                        BigInteger orderCatId = BigInteger.valueOf(Long.valueOf(m.get("id").toString()));
                        Map<String, Object> firstMap = new HashMap<>();
                        if(orderId.compareTo(BigInteger.ZERO) == 0){
                            orderId = BigInteger.valueOf(100000000);
                        }
                        else if(orderId.compareTo(BigInteger.valueOf(100000000)) < 0){
                            orderId = orderId.multiply(BigInteger.valueOf(100000000));
                        }
                        if(catId.compareTo(orderCatId) == 0){
                            firstOrderId = orderId;
                        }
                        firstMap.put("orderId", orderId);
                        firstMap.put("catId", orderCatId);
                        lastJsonArr.add(firstMap);
                    }
                }else{//一级分类是新排序
                    firstOrderId=firstCatOrderId;
                }
                finalOrderId=firstOrderId.add(secondCatOrderId.multiply(BigInteger.valueOf(100000)));
                Map<String, Object> secondMap = new HashMap<>();
                secondMap.put("orderId", finalOrderId);
                secondMap.put("catId", catId);
                lastJsonArr.add(secondMap);
            }else{//二级分类是新排序
                finalOrderId=secondCatOrderId;
            }
        }

        for(Object o:goodsJson){
            Map<String, Object> objMap = (Map<String, Object>)o;
            BigInteger goodsOrderId = BigInteger.valueOf(Long.valueOf(objMap.get("orderId").toString()));
            goodsOrderId=finalOrderId.add(goodsOrderId);
            objMap.put("orderId",goodsOrderId);
            objMap.put("catId",catId);
            lastGoodsJsonArr.add(objMap);
        }
        if(lastJsonArr.size() > 0){
            categoryMapper.deleteCategoryOrder(tenantId, branchId, lastJsonArr);
            categoryMapper.insertCatOrder(tenantId, branchId, lastJsonArr);
            categoryMapper.updateLastUpdateAt(lastJsonArr);
        }
        if(lastGoodsJsonArr.size() > 0){
            goodsMapper.deleteGoodsOrder(tenantId, branchId,lastGoodsJsonArr);
            goodsMapper.insertGoodsOrderV2(tenantId, branchId, lastGoodsJsonArr);
            goodsMapper.updateLastUpdateAt(lastGoodsJsonArr);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("成功对" + goodsJson.size() + "个商品进行排序！");
        return apiRest;
    }

    /**
     * 查询商品排序
     *
     * @param params
     * @return
     */
    public ApiRest listOrderGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger catId = BigInteger.valueOf(Long.valueOf(params.get("catId").toString()));
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        List<Map> list = goodsMapper.listOrderGoods(tenantId, branchId, catId, onlySelf);
        Map result = new HashMap();
        result.put("list", list);
        result.put("total", list.size());
        apiRest.setData(result);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询商品排序成功！");
        return apiRest;
    }

    /**
     * 根据商品条码查询商品档案信息（标准库）
     */
    public ApiRest queryByBarCode(Map params){
        ApiRest apiRest = new ApiRest();
        String barCode = params.get("barCode").toString();
        int goodsType = Integer.parseInt(params.get("goodsType").toString());
        apiRest = SaaSApi.findLibGoodsByBarcode(barCode, goodsType);
        return apiRest;
    }

    /**
     * 查询机构可用的称重并且助记码长度是5纯数字的商品档案
     *
     * @return
     */
    public ApiRest getWeighGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));

        List<Map> list = goodsMapper.getWeighGoods(tenantId, branchId);
        apiRest.setData(list);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询数据成功！");
        return apiRest;
    }

    public boolean checkPlu(BigInteger branchId, BigInteger tenantId, Integer weighPlu, BigInteger goodsId){
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        map.put("branchId", branchId);
        map.put("weighPlu", weighPlu);
        map.put("goodsId", goodsId);
        int i = goodsMapper.isPluUsed(map);
        if(i > 0){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * 条码查询商品
     */
    public ApiRest queryGoodsByBarCode(Map params){
        ApiRest apiRest = new ApiRest();
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            params.put("onlySelf", Integer.parseInt(params.get("onlySelf").toString()));
        }else{
            params.put("onlySelf", onlySelf);
        }
        List<Map> goods = goodsMapper.queryGoodsStoreInfoByBarCode(params);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询商品成功");
        apiRest.setData(goods);
        return apiRest;
    }

    /**
     * 检验条码是否已存在
     */
    public ApiRest checkBarCode(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> list = goodsMapper.checkBarCode(params);
        if(list.size() > 0 && "true".equals(params.get("isAdd"))){
            apiRest.setIsSuccess(true);
            apiRest.setResult("0");
            return apiRest;
        }else if(list.size() > 0 && !"true".equals(params.get("isAdd"))){
            if(list.size() == 1 && !params.get("goodsId").toString().equals(list.get(0).get("id").toString())){
                apiRest.setIsSuccess(true);
                apiRest.setResult("0");
                return apiRest;
            }else if(list.size() > 1){
                apiRest.setIsSuccess(true);
                apiRest.setResult("0");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setResult("1");
        return apiRest;
    }

    /**
     * 查询电子秤信息
     */
    public ApiRest scaleList(){
        ApiRest apiRest = new ApiRest();
        List<Map> list = goodsMapper.scaleList();
        apiRest.setIsSuccess(true);
        apiRest.setData(list);
        apiRest.setMessage("查询电子秤信息成功");
        return apiRest;
    }

    /**
     * 根据ID查询电子秤
     */
    public ApiRest scaleById(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = goodsMapper.scaleById(params);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询电子秤成功");
        apiRest.setData(map);
        return apiRest;
    }

    /**
     * 获取条码
     * */
    public ApiRest getBarCode(Map params){
        params.put("BAR_SIZE",7);
        params.put("isBarCode", 1);
        ApiRest apiRest = publicWSService.barCreator(params);
        String barCode = apiRest.getData().toString();
        params.put("barCode", barCode);
        List<Map> list = goodsMapper.checkBarCode(params);
        while (list == null && list.size() == 0){
            barCode = publicWSService.barCreator(params).getData().toString();
            params.put("barCode", barCode);
            list = goodsMapper.checkBarCode(params);
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(barCode);
        apiRest.setMessage("生成编码成功！");
        return apiRest;
    }


    //以下为积分商城-商品档案所用接口
    /**
     * 查询商品档案列表
     * */
    public ApiRest shopGoodsList(Map params){
        ApiRest apiRest = new ApiRest();
        List<ShopGoods> goodsList = goodsMapper.shopGoodsList(params);
        int startNum = 0;
        int endNum = 9999;
        if(params.get("page") != null && params.get("rows") != null && !"".equals(params.get("rows"))){
            startNum = (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString());
            endNum = Integer.parseInt(params.get("rows").toString()) + startNum;

        }
        List resultList = new ArrayList();
        for(int i=0; i<goodsList.size(); i++){
            ShopGoods shopGoods = goodsList.get(i);
            Date startDate = shopGoods.getStartDate();
            Date endDate = shopGoods.getEndDate();
            Date nowDate = new Date();
            if(startDate.before(nowDate) && endDate.after(nowDate)){
                shopGoods.setGoodsStatus(1);
                if(params.get("goodsStatus") != null && "1".equals(params.get("goodsStatus"))){
                    resultList.add(shopGoods);
                }
            }else if(startDate.after(nowDate)){
                shopGoods.setGoodsStatus(0);
                if(params.get("goodsStatus") != null && "0".equals(params.get("goodsStatus"))){
                    resultList.add(shopGoods);
                }
            }else{
                shopGoods.setGoodsStatus(2);
                if(params.get("goodsStatus") != null && "2".equals(params.get("goodsStatus").toString())){
                    resultList.add(shopGoods);
                }
            }
        }
        int count = goodsMapper.shopGoodsCount(params);
        Map map = new HashMap();
        if(endNum > (params.get("goodsStatus") == null ? count : resultList.size())){
            endNum = params.get("goodsStatus") == null ? count : resultList.size();
        }
        map.put("rows", params.get("goodsStatus") == null ? goodsList.subList(startNum, endNum) : resultList.subList(startNum, endNum));
        map.put("total", params.get("goodsStatus") == null ? count : resultList.size());
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询活动列表成功");
        apiRest.setData(map);
        return apiRest;
    }

    public ApiRest queryShopGoodsById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        ShopGoods goods = goodsMapper.findShopGoodsById(tenantId, branchId, id);
        if(goods == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查到该活动");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(goods);
        apiRest.setMessage("查询活动详情成功");
        return apiRest;
    }

    /**
     * 新增或修改商品档案
     * */
    public ApiRest addOrUpdateShopGoods(Map params) throws ServiceException, ParseException {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        String goodsName = params.get("goodsName").toString();
        Integer goodsStatus = Integer.parseInt(params.get("goodsStatus").toString());
        BigDecimal salePrice = BigDecimal.valueOf(Double.parseDouble(params.get("salePrice").toString()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = dateFormat.parse(params.get("startDate").toString()+ ":00");
        Date endDate = dateFormat.parse(params.get("endDate").toString()+ ":59");
        Integer changeType = Integer.parseInt(params.get("changeType").toString());
        BigDecimal changeQuantity = BigDecimal.valueOf(Double.parseDouble(params.get("changeQuantity").toString()));
        Integer isLimit = Integer.parseInt(params.get("isLimit").toString());
        Integer isTicket = Integer.parseInt(params.get("isTicket").toString());
        Integer effectDate = Integer.parseInt(params.get("effectDate").toString());
        Integer dateType = Integer.parseInt(params.get("dateType").toString());
        BigDecimal useScore = BigDecimal.valueOf(Double.parseDouble(params.get("useScore").toString()));
        BigDecimal useMoney = params.get("useMoney") == null ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(params.get("useMoney").toString()));
        String photo = params.get("photo").toString();

        ShopGoods shopGoods = new ShopGoods();
        shopGoods.setTenantId(tenantId);
        shopGoods.setBranchId(branchId);
        shopGoods.setGoodsName(goodsName);
        shopGoods.setStartDate(startDate);
        shopGoods.setEndDate(endDate);
        shopGoods.setSalePrice(salePrice);
        shopGoods.setUseScore(useScore);
        shopGoods.setUseMoney(useMoney);
        shopGoods.setChangeType(changeType);
        shopGoods.setChangeQuantity(changeQuantity);
        shopGoods.setIsLimit(isLimit);
        shopGoods.setLimitQuantity(params.get("limitQuantity") == null ? null : BigDecimal.valueOf(Double.parseDouble(params.get("limitQuantity").toString())));
        shopGoods.setIsTicket(isTicket);
        shopGoods.setTicketId(params.get("ticketId") == null ? null : BigInteger.valueOf(Long.parseLong(params.get("ticketId").toString())));
        shopGoods.setGoodsStatus(goodsStatus);
        shopGoods.setEffectDate(effectDate);
        shopGoods.setDateType(dateType);
        shopGoods.setMemo(params.get("memo") == null ? null : params.get("memo").toString());
        shopGoods.setLastUpdateAt(new Date());
        shopGoods.setLastUpdateBy(params.get("userName").toString());
        shopGoods.setIsDeleted(false);
        shopGoods.setAlreadyChangeQuantity(BigDecimal.ZERO);
        shopGoods.setSurplusQuantity(changeQuantity);
        shopGoods.setPhoto(photo);

        if(params.get("id") == null || "".equals(params.get("id"))){
            //判断商品名是否重复
            int m = goodsMapper.isUsedName(tenantId, goodsName);
            if(m > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("活动名称重复");
                return apiRest;
            }
            //在goods表中插入一条数据，为沿用之前流水、报表查询、库存
            Goods goods = new Goods();
            goods.setTenantId(tenantId);
            goods.setBranchId(branchId);
            goods.setGoodsName(goodsName);
            goods.setGoodsCode("00000000");
            goods.setCategoryId(BigInteger.ZERO);
            goods.setGoodsUnitId(BigInteger.ZERO);
            goods.setSalePrice(salePrice);
            goods.setVipPrice(salePrice);
            goods.setVipPrice1(salePrice);
            goods.setVipPrice2(salePrice);
            goods.setGoodsStatus(goodsStatus);
            goods.setPriceType(0);
            goods.setGoodsType(1);
            int i = goodsMapper.insert(goods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增活动失败");
                return apiRest;
            }
            shopGoods.setGoodsId(goods.getId());
            shopGoods.setCreateAt(new Date());
            shopGoods.setCreateBy(params.get("userName").toString());
            int j = goodsMapper.insertShopGoods(shopGoods);
            if(j <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增活动失败");
                return apiRest;
            }
        }else{
            BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
            ShopGoods shopGoods1 = goodsMapper.findShopGoodsById(tenantId, branchId, id);
            if(shopGoods1 == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未找到该活动");
                return apiRest;
            }
            shopGoods.setId(id);
            int i = goodsMapper.updateShopGoods(shopGoods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改活动失败");
                return apiRest;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("新增活动成功");
        return apiRest;
    }

    public ApiRest delShopGoods(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        /*ShopGoods goods = goodsMapper.findShopGoodsById(tenantId, branchId, id);
        if(goods == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到该商品");
            return apiRest;
        }*/
        int i = goodsMapper.delShopGoods(tenantId, branchId, params.get("ids").toString());
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除活动失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除活动成功");
        return apiRest;
    }


    /*根据条码查询商品信息*/
    public ApiRest doSelectGoodsByBarCode(Map params){
        ApiRest apiRest = new ApiRest();
        List<Map> list = goodsMapper.doCheckBarCode(params);
        for(Map map : list){
            map.put("goodsId",map.get("id"));
        }
        if(list.size() == 0){
            apiRest.setError("未找到对应商品!");
            apiRest.setIsSuccess(false);
            apiRest.setData(null);
        }else{
            apiRest.setMessage("根据条码查询商品成功!");
            apiRest.setIsSuccess(true);
            apiRest.setData(list);
        }

        return apiRest;
    }

    public ApiRest updatePrice(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        List infos = GsonUntil.jsonAsList(params.get("infos").toString(), Map.class);
        int errorCount = 0;
        for(int i=0; i<infos.size(); i++){
            int j = goodsMapper.updatePrice(tenantId, branchId, (Map) infos.get(i));
            if(j <= 0){
                errorCount++;
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("批量修改"+(infos.size() - errorCount)+"个商品价格成功！");
        return apiRest;
    }

}
