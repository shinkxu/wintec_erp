package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Goods;
import erp.chain.domain.GroupGoods;
import erp.chain.domain.PackageGroup;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.GoodsMapper;
import erp.chain.mapper.GroupGoodsMapper;
import erp.chain.mapper.PackageGroupMapper;
import erp.chain.mapper.PackageMapper;
import erp.chain.model.supply.store.QueryGoodsStoreModel;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.SerialNumberGenerate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static net.sf.json.JSONObject.fromObject;

/**
 * 套餐
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/9
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PackageService{
    @Autowired
    private PackageMapper packageMapper;
    @Autowired
    private PackageGroupMapper packageGroupMapper;
    @Autowired
    private GroupGoodsMapper groupGoodsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TenantConfigService tenantConfigService;

    /**
     * 查询套餐列表
     *
     * @param params
     * @return
     */
    public ApiRest queryPackage(Map params){
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
        List<Map> list = packageMapper.queryPackage(params);
        Long count = packageMapper.queryPackageSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询套餐列表成功！");
        return apiRest;
    }

    /**
     * 查询套餐详情
     *
     * @param params
     * @return
     */
    public ApiRest packageDetail(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Goods goods = packageMapper.getPackageById(id, tenantId);
        if(goods == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询套餐失败！");
            return apiRest;
        }
        PackageGroup packageGroup = packageGroupMapper.getGroupByPackageId(tenantId, goods.getId());
        if(packageGroup == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询套餐分组失败！");
            return apiRest;
        }
        List<Map> groupGoods = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
        Map map = new HashMap();
        map.put("package", goods);
        map.put("group", packageGroup);
        map.put("groupGoods", groupGoods);
        map.put("goodsTotal", groupGoods.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询套餐详情成功！");
        return apiRest;
    }

    /**
     * 删除套餐以及详情
     *
     * @param params
     * @return
     */
    public ApiRest delPackage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Goods goods = packageMapper.getPackageById(id, tenantId);
        if(goods == null){
            apiRest.setIsSuccess(false);
            if("5".equals(params.get("business1"))){
                apiRest.setError("查询组合商品失败！");
            }
            else{
                apiRest.setError("查询套餐失败！");
            }
            return apiRest;
        }
        QueryGoodsStoreModel model = new QueryGoodsStoreModel();
        model.setGoodsId(goods.getId());
        model.setTenantId(tenantId);
        model.setIds(goods.getId().toString());
        List<Map> menuGoodsCountList = goodsMapper.countMenuGoods(model);
        if(menuGoodsCountList.size() > 0){
            Long menuGoodsCount = Long.parseLong(menuGoodsCountList.get(0).get("count").toString());
            if(menuGoodsCount > 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品存在上架商品中，删除失败！");
                }
                else{
                    apiRest.setError("套餐存在菜牌中，删除失败！");
                }
                return apiRest;
            }
        }
        List<PackageGroup> packageGroupList = groupGoodsMapper.getGroupsByPackId(tenantId, goods.getId());
        for(PackageGroup packageGroup : packageGroupList){
            packageGroup.setDeleted(true);
            packageGroup.setLastUpdateAt(new Date());
            packageGroup.setLastUpdateBy("System");
            packageGroup.setVersion(packageGroup.getVersion() == null ? BigInteger.ZERO : packageGroup.getVersion().add(BigInteger.ONE));
            int j = packageGroupMapper.update(packageGroup);
            if(j <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("删除组合商品分组失败，组合商品未被删除！");
                }
                else{
                    apiRest.setError("删除套餐分组失败，套餐未被删除！");
                }
                return apiRest;
            }
            List<Map> groupGoodses = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
            int k = groupGoodsMapper.deleteGoodsByGroupId(tenantId, packageGroup.getId());
            if(k < 0 && groupGoodses.size() != 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("删除组合商品失败！");
                }
                else{
                    apiRest.setError("删除套餐商品失败！");
                }
                return apiRest;
            }
        }
        goods.setIsDeleted(true);
        goods.setLastUpdateAt(new Date());
        goods.setLastUpdateBy("System");
        goods.setVersion(goods.getVersion().add(BigInteger.ONE));
        ApiRest r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_PACKAGE_NUM);
        if(!r.getIsSuccess()){
            apiRest = r;
            return apiRest;
        }
        int i = packageMapper.update(goods);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            if("5".equals(params.get("business1"))){
                apiRest.setError("删除组合商品失败！");
            }
            else{
                apiRest.setError("删除套餐失败！");
            }
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        if("5".equals(params.get("business1"))){
            apiRest.setMessage("删除组合商品及详情成功！");
        }
        else{
            apiRest.setMessage("删除套餐及详情成功！");
        }
        return apiRest;
    }

    /**
     * 获取套餐编码
     *
     * @param params
     * @return
     */
    public ApiRest getPackageCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String code = packageMapper.getPackageCode(tenantId);
        if(code == null || code.equals("")){
            code = "99000000";
        }
        code = SerialNumberGenerate.nextSerialNumber(8, code);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取套餐编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 新增或修改套餐
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdatePackage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String mnemonic = params.get("mnemonic").toString();
        String barCode = "";
        if(params.get("barCode") != null){
            barCode = params.get("barCode").toString();
        }
        String goodsName = params.get("goodsName").toString();
        Integer goodsStatus = Integer.valueOf(params.get("goodsStatus").toString());
        Integer isDsc = Integer.valueOf(params.get("isDsc").toString());
        Integer isTakeout = Integer.valueOf(params.get("isTakeout").toString());
        BigDecimal shippingPrice1 = null;
        if(params.get("shippingPrice1") != null && !"".equals(params.get("shippingPrice1"))){
            shippingPrice1 = BigDecimal.valueOf(Double.valueOf((String)params.get("shippingPrice1")));
        }

        BigDecimal salePrice = BigDecimal.valueOf(Double.valueOf((String)params.get("salePrice")));
        BigDecimal vipPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("vipPrice")));
        JSONObject groupGoodsJson = fromObject(params.get("groupGoods"));
        String photo = "";
        if(params.get("photo") != null && !params.get("photo").equals("")){
            photo = params.get("photo").toString();
        }
        /*商品档案直接添加的套餐/组合商品需要关联商品*/
        BigInteger relatedGoodsId = BigInteger.ONE;
        if(params.get("relatedGoodsId") != null && !"".equals(params.get("relatedGoodsId"))){
            relatedGoodsId = BigInteger.valueOf(Long.parseLong(params.get("relatedGoodsId").toString()));
        }
        BigInteger goodsUnitId = null;
        String goodsUnitName = null;
        if(params.get("goodsUnitId") != null && !"".equals(params.get("goodsUnitId"))){
            goodsUnitId = BigInteger.valueOf(Long.parseLong(params.get("goodsUnitId").toString()));
        }
        if(params.get("goodsUnitName") != null && !"".equals(params.get("goodsUnitName"))){
            goodsUnitName = params.get("goodsUnitName").toString();
        }

        Map goodsMap = new HashMap();
        int goodsCount = 0;
        if(groupGoodsJson.get("groupGoods") == null || groupGoodsJson.get("groupGoods").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setError("json参数错误，请确认参数准确！");
            return apiRest;
        }
        for(Map json : (List<Map>)groupGoodsJson.getJSONArray("groupGoods")){
            if(!(json.get("id") != null && json.get("id") != "" && json.get("quantity") != null && json.get("quantity") != "")){
                apiRest.setIsSuccess(false);
                apiRest.setError("json参数错误，请确认参数准确！");
                return apiRest;
            }
        }
        for(Map json : (List<Map>)groupGoodsJson.getJSONArray("groupGoods")){
            goodsMap.put(json.get("id"), json.get("id"));
            Map goMap = new HashMap();
            goMap.put("id", json.get("id"));
            goMap.put("tenantId", tenantId);
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(goMap);
            if(goods == null){
                goodsCount++;
            }
        }
        if(goodsMap.size() < groupGoodsJson.getJSONArray("groupGoods").size()){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在重复商品，保存失败！");
            return apiRest;
        }
        if(goodsCount > 0 && relatedGoodsId == BigInteger.ZERO){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在未查询到的商品，保存失败！");
            return apiRest;
        }
        Map map = new HashMap();
        if(params.get("id") != null && !params.get("id").equals("")){
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
            if(goods == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到需要修改的商品");
                return apiRest;
            }
            goods.setGoodsName(goodsName);
            goods.setMnemonic(mnemonic);
            goods.setGoodsStatus(goodsStatus);
            goods.setLastUpdateAt(new Date());
            goods.setLastUpdateBy("System");
            goods.setGoodsUnitId(BigInteger.ZERO);
            goods.setGoodsUnitName("份");
            goods.setCategoryId(BigInteger.valueOf(-9900));
            goods.setCategoryName("套餐");
            goods.setSalePrice(salePrice);
            goods.setVipPrice(vipPrice);
            goods.setVipPrice1(vipPrice);
            goods.setVipPrice2(vipPrice);
            goods.setIsDsc(isDsc == 1);
            goods.setIsForPoints(isDsc == 1);
            goods.setPhoto(photo);
            goods.setVersion(goods.getVersion() == null ? BigInteger.ZERO : goods.getVersion().add(BigInteger.ONE));
            goods.setIsTakeout(isTakeout == 1);

            /*新增条码*/
            goods.setBarCode(barCode);

            goods.setGoodsUnitId(goodsUnitId);
            goods.setGoodsUnitName(goodsUnitName);
            PackageGroup packageGroup = packageGroupMapper.getGroupByPackageId(tenantId, goods.getId());
            if(packageGroup == null){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("查询组合商品分组失败！");
                }
                else{
                    apiRest.setError("查询套餐分组失败！");
                }
                return apiRest;
            }
            List<Map> groupGoodses = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
            int k = groupGoodsMapper.deleteGoodsByGroupId(tenantId, packageGroup.getId());
                /*if(k!=groupGoodses.size()){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("删除套餐菜品失败！");
                    return apiRest;
                }*/
            for(Map json : (List<Map>)groupGoodsJson.getJSONArray("groupGoods")){
                Map seGoMap = new HashMap();
                seGoMap.put("id", json.get("id"));
                seGoMap.put("tenantId", tenantId);
                Goods packGoods = goodsMapper.findGoodsByIdAndTenantId(seGoMap);
                GroupGoods groupGoods1 = new GroupGoods();
                groupGoods1.setGroupId(packageGroup.getId());
                groupGoods1.setGoodsId(packGoods.getId());
                BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(json.get("quantity").toString()));
                groupGoods1.setQuantity(BigDecimal.valueOf(Long.parseLong(quantity.setScale(0, BigDecimal.ROUND_HALF_UP).toString())));
                groupGoods1.setTenantId(tenantId);
                groupGoods1.setBranchId(branchId);
                groupGoods1.setLastUpdateAt(new Date());
                groupGoods1.setCreateAt(new Date());
                groupGoods1.setLastUpdateBy("System");
                groupGoods1.setCreateBy("System");
                groupGoods1.setVersion(BigInteger.ZERO);
                int a = groupGoodsMapper.insert(groupGoods1);
                if(a <= 0){
                    apiRest.setIsSuccess(false);
                    if("5".equals(params.get("business1"))){
                        apiRest.setError("组合商品添加失败");
                    }
                    else{
                        apiRest.setError("套餐商品添加失败");
                    }
                    return apiRest;
                }
            }
            List<Map> groupGoods = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
            int i = goodsMapper.update(goods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品修改失败！");
                }
                else{
                    apiRest.setError("套餐修改失败！");
                }
                return apiRest;
            }
            map.put("package", goods);
            map.put("group", packageGroup);
            map.put("goodsTotal", groupGoods.size());
            map.put("groupGoods", groupGoods);
            apiRest.setIsSuccess(true);
            if("5".equals(params.get("business1"))){
                apiRest.setMessage("修改组合商品以及明细成功！");
            }
            else{
                apiRest.setMessage("修改套餐以及明细成功！");
            }
            apiRest.setData(map);
        }
        else{
            ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_PACKAGE_NUM);
            if(!r.getIsSuccess()){
                return r;
            }
            Goods goods = new Goods();
            ApiRest rest = getPackageCode(params);
            if(!rest.getIsSuccess()){
                return rest;
            }
            goods.setGoodsCode(rest.getData().toString());
            goods.setTenantId(tenantId);
            goods.setBranchId(branchId);
            goods.setCreateAt(new Date());
            goods.setCreateBy("System");
            goods.setVersion(BigInteger.ZERO);
            goods.setGoodsType(2);
            goods.setGoodsName(goodsName);
            goods.setMnemonic(mnemonic);
            goods.setGoodsStatus(goodsStatus);
            goods.setLastUpdateAt(new Date());
            goods.setLastUpdateBy("System");
            goods.setGoodsUnitId(BigInteger.ZERO);
            goods.setGoodsUnitName("份");
            goods.setCategoryId(BigInteger.valueOf(-9900));
            goods.setCategoryName("套餐");
            goods.setShippingPrice1(shippingPrice1);
            goods.setSalePrice(salePrice);
            goods.setVipPrice(vipPrice);
            goods.setVipPrice1(vipPrice);
            goods.setVipPrice2(vipPrice);
            goods.setIsDsc(isDsc == 1);
            goods.setIsForPoints(isDsc == 1);
            goods.setIsTakeout(isTakeout == 1);
            goods.setPriceType(0);
            goods.setPhoto(photo);
            /*新增条码*/
            goods.setBarCode(barCode);
            /*新增套餐关联商品ID*/
            goods.setRelatedGoodsId(relatedGoodsId);

            goods.setGoodsUnitId(goodsUnitId);
            goods.setGoodsUnitName(goodsUnitName);

            int i = goodsMapper.insert(goods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品添加失败！");
                }
                else{
                    apiRest.setError("套餐添加失败！");
                }
                return apiRest;
            }
            PackageGroup packageGroup = new PackageGroup();
            packageGroup.setLastUpdateAt(new Date());
            packageGroup.setLastUpdateBy("System");
            packageGroup.setCreateBy("System");
            packageGroup.setCreateAt(new Date());
            packageGroup.setVersion(BigInteger.ZERO);
            packageGroup.setPackageId(goods.getId());
            packageGroup.setCode("001");
            packageGroup.setGroupName("套餐必选组");
            packageGroup.setMain(true);
            packageGroup.setBranchId(branchId);
            packageGroup.setTenantId(tenantId);
            int b = packageGroupMapper.insert(packageGroup);
            if(b <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("新增组合商品分组失败！");
                }
                else{
                    apiRest.setError("新增套餐分组失败！");
                }
                return apiRest;
            }
            for(Map json : (List<Map>)groupGoodsJson.getJSONArray("groupGoods")){
                Map seGoMap = new HashMap();
                GroupGoods groupGoods1 = new GroupGoods();
                if(relatedGoodsId == BigInteger.ZERO){
                    seGoMap.put("id", json.get("id"));
                    seGoMap.put("tenantId", tenantId);
                    Goods packGoods = goodsMapper.findGoodsByIdAndTenantId(seGoMap);
                    groupGoods1.setGoodsId(packGoods.getId());
                }
                else{
                    groupGoods1.setGoodsId(relatedGoodsId);
                }
                groupGoods1.setGroupId(packageGroup.getId());
                BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(json.get("quantity").toString()));
                groupGoods1.setQuantity(BigDecimal.valueOf(Double.parseDouble((quantity.setScale(2, BigDecimal.ROUND_HALF_UP).toString()))));
                groupGoods1.setTenantId(tenantId);
                groupGoods1.setBranchId(branchId);
                groupGoods1.setLastUpdateAt(new Date());
                groupGoods1.setCreateAt(new Date());
                groupGoods1.setLastUpdateBy("System");
                groupGoods1.setCreateBy("System");
                groupGoods1.setVersion(BigInteger.ZERO);
                int a = groupGoodsMapper.insert(groupGoods1);
                if(a <= 0){
                    apiRest.setIsSuccess(false);
                    if("5".equals(params.get("business1"))){
                        apiRest.setError("组合商品添加失败");
                    }
                    else{
                        apiRest.setError("套餐商品添加失败");
                    }
                    return apiRest;
                }
            }
            List<Map> groupGoods = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
            map.put("package", goods);
            map.put("group", packageGroup);
            map.put("goodsTotal", groupGoods.size());
            map.put("groupGoods", groupGoods);
            apiRest.setIsSuccess(true);
            if("5".equals(params.get("business1"))){
                apiRest.setMessage("新增组合商品以及明细成功！");
            }
            else{
                apiRest.setMessage("新增套餐以及明细成功！");
            }
            apiRest.setData(map);
        }
        return apiRest;
    }

    /**
     * 新增或修改套餐，加入可选组版本
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpPackage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        String mnemonic = params.get("mnemonic").toString();
        String barCode = "";
        if(params.get("barCode") != null && !"".equals(params.get("barCode"))){
            barCode = params.get("barCode").toString();
        }
        String goodsName = params.get("goodsName").toString();
        Integer goodsStatus = Integer.valueOf(params.get("goodsStatus").toString());
        Integer isDsc = Integer.valueOf(params.get("isDsc").toString());
        Integer isTakeout = Integer.valueOf(params.get("isTakeout").toString());
        BigDecimal salePrice = BigDecimal.valueOf(Double.valueOf((String)params.get("salePrice")));
        BigDecimal vipPrice = BigDecimal.valueOf(Double.valueOf((String)params.get("vipPrice")));
        String photo = "";
        if(params.get("photo") != null && !params.get("photo").equals("")){
            photo = params.get("photo").toString();
        }
        JSONArray groupGoodsJson = JSONArray.fromObject(params.get("groupGoods").toString());
        if(groupGoodsJson == null || groupGoodsJson.size() == 0){
            apiRest.setIsSuccess(false);
            if("5".equals(params.get("business1"))){
                apiRest.setError("组合商品和明细不能为空！");
            }
            else{
                apiRest.setError("套餐组和明细不能为空！");
            }
            return apiRest;
        }
        int isMainCount = 0;
        int goodsSize = 0;
        int nullGoods = 0;
        int errorGoodsCount = 0;
        int typeError = 0;
        Map goodsMap = new HashMap();
        for(Object groupGoods : groupGoodsJson){
            //单个组对象(groupGoods)，包括一个group和多个goods
            Map groupGoodsMap = (Map)groupGoods;
            Map group = (Map)groupGoodsMap.get("group");
            String isMain = group.get("isMain").toString();
            if(isMain.equals("1") || isMain.equals("true")){
                isMainCount++;
            }
            List<Map> goodsList = (List<Map>)groupGoodsMap.get("goods");
            if(goodsList.size() == 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("分组中必须有商品！");
                return apiRest;
            }
            goodsSize += goodsList.size();
            for(Map goods : goodsList){
                Map pa = new HashMap();
                pa.put("id", goods.get("id"));
                pa.put("tenantId", tenantId);
                if(goods.get("id").equals("null")){
                    nullGoods++;
                }
                if(goodsMapper.findGoodsByIdAndTenantId(pa) == null){
                    errorGoodsCount++;
                }
                if(goodsMapper.findByIdAndTenantIdAndBranchIdAndGoodsTypeNotEqual(goods.get("id").toString(),
                        tenantId.toString(),
                        branchId.toString(),
                        "1") != null){
                    typeError++;
                }
                goodsMap.put(goods.get("id").toString(), goods.get("id").toString());
            }
        }
        if(typeError > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("商品类型错误，添加失败！");
            return apiRest;
        }
        if(errorGoodsCount > 0){
            apiRest.setIsSuccess(false);
            if(nullGoods > 0){
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品明细不能为空！");
                }
                else{
                    apiRest.setError("套餐组明细不能为空！");
                }
            }
            else{
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品中有未查询到的商品，添加失败！");
                }
                else{
                    apiRest.setError("套餐中有未查询到的商品，添加失败！");
                }
            }
            return apiRest;
        }
        if(isMainCount != 1){
            apiRest.setIsSuccess(false);
            if("5".equals(params.get("business1"))){
                apiRest.setError("组合商品中必须有且只有一个必选组！");
            }
            else{
                apiRest.setError("套餐中必须有且只有一个必选组！");
            }
            return apiRest;
        }
        if(goodsMap.size() < goodsSize){
            apiRest.setIsSuccess(false);
            if("5".equals(params.get("business1"))){
                apiRest.setError("组合商品中存在重复商品！");
            }
            else{
                apiRest.setError("套餐中存在重复商品！");
            }
            return apiRest;
        }
        //存放结果的map
        Map map = new HashMap();
        if(params.get("id") != null && !params.get("id").equals("")){
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(params);
            if(goods == null){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("未查询到需要修改的组合商品");
                }
                else{
                    apiRest.setError("未查询到需要修改的套餐");
                }
                return apiRest;
            }
            goods.setGoodsName(goodsName);
            goods.setMnemonic(mnemonic);
            goods.setBarCode(barCode);
            goods.setGoodsStatus(goodsStatus);
            goods.setLastUpdateAt(new Date());
            goods.setLastUpdateBy("System");
            goods.setGoodsUnitId(BigInteger.ZERO);
            goods.setGoodsUnitName("份");
            goods.setCategoryId(BigInteger.valueOf(-9900));
            goods.setCategoryName("套餐");
            goods.setSalePrice(salePrice);
            goods.setVipPrice(vipPrice);
            goods.setVipPrice2(vipPrice);
            goods.setVipPrice1(vipPrice);
            goods.setPurchasingPrice(salePrice);
            goods.setShippingPrice1(salePrice);
            goods.setShippingPrice2(salePrice);
            goods.setIsDsc(isDsc == 1);
            goods.setPhoto(photo);
            goods.setVersion(goods.getVersion() == null ? BigInteger.ZERO : goods.getVersion().add(BigInteger.ONE));
            goods.setIsTakeout(isTakeout == 1);

            ApiRest ap = deleteGroupAndGoods(goods.getId(), tenantId);
            if(!ap.getIsSuccess()){
                return ap;
            }
            ApiRest a = saveGroupAndGoods(groupGoodsJson, goods, tenantId, branchId);
            if(!ap.getIsSuccess()){
                return a;
            }
            int i = goodsMapper.update(goods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品修改失败！");
                }
                else{
                    apiRest.setError("套餐修改失败！");
                }
                return apiRest;
            }
            map = (Map)a.getData();
            map.put("package", goods);
            apiRest.setIsSuccess(true);
            if("5".equals(params.get("business1"))){
                apiRest.setMessage("修改组合商品以及明细成功！");
            }
            else{
                apiRest.setMessage("修改套餐以及明细成功！");
            }
            apiRest.setData(map);
        }
        else{
            ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_PACKAGE_NUM);
            if(!r.getIsSuccess()){
                return r;
            }
            Goods goods = new Goods();
            ApiRest rest = getPackageCode(params);
            if(!rest.getIsSuccess()){
                return rest;
            }
            goods.setGoodsCode(rest.getData().toString());
            goods.setTenantId(tenantId);
            goods.setBranchId(branchId);
            goods.setCreateAt(new Date());
            goods.setCreateBy("System");
            goods.setVersion(BigInteger.ZERO);
            goods.setGoodsType(2);
            goods.setGoodsName(goodsName);
            goods.setMnemonic(mnemonic);
            goods.setBarCode(barCode);
            goods.setGoodsStatus(goodsStatus);
            goods.setLastUpdateAt(new Date());
            goods.setLastUpdateBy("System");
            goods.setGoodsUnitId(BigInteger.ZERO);
            goods.setGoodsUnitName("份");
            goods.setCategoryId(BigInteger.valueOf(-9900));
            goods.setCategoryName("套餐");
            goods.setSalePrice(salePrice);
            goods.setVipPrice(vipPrice);
            goods.setVipPrice1(vipPrice);
            goods.setVipPrice2(vipPrice);
            goods.setPurchasingPrice(salePrice);
            goods.setShippingPrice1(salePrice);
            goods.setShippingPrice2(salePrice);
            goods.setIsDsc(isDsc == 1);
            goods.setIsTakeout(isTakeout == 1);
            goods.setPriceType(0);
            goods.setPhoto(photo);
            int i = goodsMapper.insert(goods);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                if("5".equals(params.get("business1"))){
                    apiRest.setError("组合商品添加失败！");
                }
                else{
                    apiRest.setError("套餐添加失败！");
                }
                return apiRest;
            }
            ApiRest a = saveGroupAndGoods(groupGoodsJson, goods, tenantId, branchId);
            if(!a.getIsSuccess()){
                return a;
            }
            map = (Map)a.getData();
            map.put("package", goods);
            apiRest.setIsSuccess(true);
            if("5".equals(params.get("business1"))){
                apiRest.setMessage("新增组合商品以及明细成功！");
            }
            else{
                apiRest.setMessage("新增套餐以及明细成功！");
            }
            apiRest.setData(map);
        }
        return apiRest;
    }

    public ApiRest deleteGroupAndGoods(BigInteger packageId, BigInteger tenantId){
        ApiRest apiRest = new ApiRest();
        List<PackageGroup> groups = groupGoodsMapper.getGroupsByPackId(tenantId, packageId);
        for(PackageGroup packageGroup : groups){
            int k = groupGoodsMapper.deleteGoodsByGroupId(tenantId, packageGroup.getId());
        }
        int t = groupGoodsMapper.deleteGroupByPackId(tenantId, packageId);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除套餐组和组内商品成功！");
        return apiRest;
    }

    public ApiRest saveGroupAndGoods(JSONArray jsonArray, Goods goods, BigInteger tenantId, BigInteger branchId){
        ApiRest apiRest = new ApiRest();
        String code = "000";
        int i = 1;
        int groupCount = 0;
        Map resultMap = new HashMap();
        List<Map> groupGoodsList = new ArrayList<>();
        for(Object groupGoods : jsonArray){
            groupCount++;
            Map result = new HashMap();
            //单个组对象(groupGoods)，包括一个group和多个goods
            Map groupGoodsMap = (Map)groupGoods;
            Map group = (Map)groupGoodsMap.get("group");
            String isMain = group.get("isMain").toString();
            PackageGroup packageGroup = new PackageGroup();
            packageGroup.setLastUpdateAt(new Date());
            packageGroup.setLastUpdateBy("System");
            packageGroup.setCreateBy("System");
            packageGroup.setCreateAt(new Date());
            packageGroup.setVersion(BigInteger.ZERO);
            packageGroup.setPackageId(goods.getId());
            packageGroup.setCode(SerialNumberGenerate.nextSerialNumber(3, code));
            packageGroup.setBranchId(branchId);
            packageGroup.setTenantId(tenantId);
            if(isMain.equals("1") || isMain.equals("true")){
                packageGroup.setGroupName("套餐必选组");
                packageGroup.setMain(true);
                packageGroup.setTotal(null);
            }
            else{
                packageGroup.setGroupName(group.get("groupName").toString());
                packageGroup.setMain(false);
                packageGroup.setTotal(Integer.valueOf(group.get("total").toString()));
                i++;
            }
            int b = packageGroupMapper.insert(packageGroup);
            if(b <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增套餐分组失败！");
                return apiRest;
            }
            result.put("group", packageGroup);
            int goodsCount = 0;
            List<Map> goodsList = (List<Map>)groupGoodsMap.get("goods");
            for(Map good : goodsList){
                goodsCount++;
                Map seGoMap = new HashMap();
                seGoMap.put("id", good.get("id").toString());
                seGoMap.put("tenantId", tenantId);
                Goods packGoods = goodsMapper.findGoodsByIdAndTenantId(seGoMap);
                GroupGoods groupGoods1 = new GroupGoods();
                groupGoods1.setGroupId(packageGroup.getId());
                groupGoods1.setGoodsId(packGoods.getId());
                groupGoods1.setTenantId(tenantId);
                groupGoods1.setBranchId(branchId);
                groupGoods1.setLastUpdateAt(new Date());
                groupGoods1.setCreateAt(new Date());
                groupGoods1.setLastUpdateBy("System");
                groupGoods1.setCreateBy("System");
                groupGoods1.setVersion(BigInteger.ZERO);
                if(isMain.equals("1") || isMain.equals("true")){
                    groupGoods1.setAddPrice(null);
                    groupGoods1.setQuantity(BigDecimal.valueOf(Double.parseDouble(good.get("quantity").toString())));
                } else{
                    groupGoods1.setAddPrice(BigDecimal.valueOf(Double.parseDouble(good.get("addPrice").toString())));
                    String quantity = good.get("quantity").toString();
                    groupGoods1.setQuantity(quantity == null || "".equals(quantity) ? BigDecimal.ONE : BigDecimal.valueOf(Double.parseDouble(quantity)));
                }
                int a = groupGoodsMapper.insert(groupGoods1);
                if(a <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("套餐商品添加失败");
                    return apiRest;
                }
            }
            result.put("goodsTotal", goodsCount);
            result.put("groupGoods", groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId()));
            groupGoodsList.add(result);
        }
        resultMap.put("groupCount", groupCount);
        resultMap.put("groups", groupGoodsList);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("保存套餐组和商品成功！");
        apiRest.setData(resultMap);
        return apiRest;

    }


    /**
     * 查询套餐详情，加入可选组的版本
     *
     * @param params
     * @return
     */
    public ApiRest getPackageDetail(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Goods goods = packageMapper.getPackageById(id, tenantId);
        if(goods == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询套餐失败！");
            return apiRest;
        }
        List<PackageGroup> packageGroupList = groupGoodsMapper.getGroupsByPackId(tenantId, id);
        List<Map> result = new ArrayList<>();
        for(PackageGroup packageGroup : packageGroupList){
            Map group = new HashMap();
            group.put("group", packageGroup);
            List<Map> groupGoodses = groupGoodsMapper.getGroupGoodsByGroupId(tenantId, packageGroup.getId());
            group.put("groupGoods", groupGoodses);
            group.put("goodsTotal", groupGoodses.size());
            result.add(group);

        }
            /*PackageGroup packageGroup=packageGroupMapper.getGroupByPackageId(tenantId,goods.getId());
            if(packageGroup==null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询套餐分组失败！");
                return apiRest;
            }
            List<Map> groupGoods=groupGoodsMapper.getGroupGoodsByGroupId(tenantId,packageGroup.getId());*/

        map.put("package", goods);
        map.put("groups", result);
        map.put("groupCount", packageGroupList.size());
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询套餐详情成功！");
        return apiRest;
    }

    /**
     * 商品排序
     *
     * @param params
     * @return
     */
    public ApiRest orderPackage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger catId = BigInteger.valueOf(-9900);
        JSONArray goodsJson = JSONArray.fromObject(params.get("goodsJson"));
        int i = 0;
        if(goodsJson.size() > 0){
            goodsMapper.deleteGoodsOrder(tenantId, branchId, goodsJson);
            i = goodsMapper.insertGoodsOrder(tenantId, branchId, catId, goodsJson);
        }
        int t = goodsMapper.updateLastUpdateAt(goodsJson);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("成功对" + i + "个套餐进行排序！");
        return apiRest;
    }

    /**
     * 查询商品排序
     *
     * @param params
     * @return
     */
    public ApiRest listOrderPackage(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger catId = BigInteger.valueOf(-9900);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        List<Map> list = goodsMapper.listOrderPackage(tenantId, branchId, catId, onlySelf);
        Map result = new HashMap();
        result.put("list", list);
        result.put("total", list.size());
        apiRest.setData(result);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询套餐排序成功！");
        return apiRest;
    }
}
