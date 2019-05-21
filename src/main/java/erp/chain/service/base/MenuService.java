package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.*;
import erp.chain.mapper.*;
import erp.chain.utils.DateUtils;
import erp.chain.utils.SerialNumberGenerate;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.sf.json.JSONObject.fromObject;

/**
 * 菜牌
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuService{
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private GroupGoodsMapper groupGoodsMapper;

    /**
     * 查询菜牌列表
     *
     * @param params
     * @return
     */
    public ApiRest queryMenu(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        Integer branchType = -1;
        if(params.get("branchType") != null && !"".equals(params.get("branchType"))){
            branchType = Integer.parseInt(params.get("branchType").toString());
        }
        Integer isManageGoods = -1;
        if(params.get("isManageGoods") != null && !"".equals(params.get("isManageGoods"))){
            isManageGoods = Integer.parseInt(params.get("isManageGoods").toString());
        }
        params.put("branchType", branchType);
        params.put("isManageGoods", isManageGoods);
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        List<Map> list = menuMapper.queryMenu(params);
        Long count = menuMapper.queryMenuSum(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询菜牌成功！");
        return apiRest;
    }

    /**
     * 查询菜牌详情，按照菜牌ID
     *
     * @return
     */
    public ApiRest findMenuDetail(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Menu menu = menuMapper.getMenuById(id, tenantId);
        Map map = new HashMap();
        if(menu == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌信息失败！");
            return apiRest;
        }
        map.put("menu", menu);
        List<Map> menuGoodsList = menuMapper.getMenuGoodsWithOrder(id, tenantId,menu.getCreateBranchId());
        map.put("detailTotal", menuGoodsList.size());
        map.put("menuGoods", menuGoodsList);
        List<Map> branches = menuMapper.getMenuBranch(id, tenantId);
        map.put("branchTotal", branches.size());
        map.put("branches", branches);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询菜牌明细成功！");
        apiRest.setData(map);
        return apiRest;
    }

    /**
     * 删除菜牌以及详情
     *
     * @return
     */
    public ApiRest delMenu(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Menu menu = menuMapper.getMenuById(id, tenantId);
        if(menu == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌信息失败！");
            return apiRest;
        }
        //List<Map> menuGoodsList = menuMapper.getMenuGoods(id,tenantId);
        int i = menuMapper.delMenuGoods(id, tenantId);
        if(i <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除菜牌商品失败！");
            return apiRest;
        }
        //List<Map> branches = menuMapper.getMenuBranch(id,tenantId);
        int j = menuMapper.delMenuBranch(id);
        if(j <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除菜牌机构关系失败！");
            return apiRest;
        }
        menu.setLastUpdateAt(new Date());
        menu.setLastUpdateBy("System");
        menu.setDeleted(true);
        int k = menuMapper.update(menu);
        if(k <= 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除菜牌失败！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除菜牌及明细成功！");
        return apiRest;
    }

    /**
     * 获取菜牌编码
     *
     * @return
     */
    public ApiRest getMenuCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String currCode = "CP" + DateUtils.getSpaceDate("yyMMdd");
        String code = menuMapper.getMenuCode(currCode, tenantId);
        code = SerialNumberGenerate.nextMenuCode(code);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取菜牌编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 新增或修改菜牌以及明细
     *
     * @return
     */
    public ApiRest addOrUpdateMenu(Map params) throws ParseException{
        ApiRest apiRest = new ApiRest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        String cronStartAt = params.get("cronStartAt").toString();
        String cronEndAt = params.get("cronEndAt").toString();
        String name = params.get("name").toString();
        String branchIds = params.get("branchIds").toString();
        Integer status = Integer.valueOf(params.get("status").toString());
        Integer menuType = Integer.valueOf(params.get("menuType").toString());
        BigInteger createBranchId = null;
        if(params.get("createBranchId") != null && !"".equals(params.get("createBranchId"))){
            createBranchId = BigInteger.valueOf(Long.parseLong(params.get("createBranchId").toString()));
        }
        JSONObject goodsJson = fromObject(params.get("goods"));
        Map goodsMap = new HashMap();
        Map branchMap = new HashMap();
        int goodsCount = 0;
        int branchCount = 0;
        if(goodsJson.get("goods") == null || goodsJson.get("goods").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setError("json参数错误，请确认参数准确！");
            return apiRest;
        }
        for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
            if(!(json.get("goodsId") != null && json.get("goodsId") != "" && json.get("salePrice") != null && json.get("salePrice") != "" && json.get("vipPrice") != null && json.get("vipPrice") != "")){
                apiRest.setIsSuccess(false);
                apiRest.setError("json参数错误，请确认参数准确！");
                return apiRest;
            }
        }
        for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
            goodsMap.put(json.get("goodsId"), json.get("goodsId"));
            Map goMap = new HashMap();
            goMap.put("id", json.get("goodsId"));
            goMap.put("tenantId", tenantId);
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(goMap);
            if(goods == null){
                goodsCount++;
            }
        }
        if(goodsMap.size() < goodsJson.getJSONArray("goods").size()){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在重复商品，保存失败！");
            return apiRest;
        }
        for(String branchId : params.get("branchIds").toString().split(",")){
            branchMap.put(branchId, branchId);
            Map seMap = new HashMap();
            seMap.put("branchId", branchId);
            seMap.put("tenantId", tenantId);
            Branch branch = branchMapper.findByTenantIdAndBranchId(seMap);
            if(branch == null){
                branchCount++;
            }
        }
        if(branchMap.size() < params.get("branchIds").toString().split(",").length){
            apiRest.setIsSuccess(false);
            apiRest.setError("机构重复，保存失败！");
            return apiRest;
        }
        if(goodsCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在未查询到的商品，保存失败！");
            return apiRest;
        }
        if(branchCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在未查询到的机构，保存失败！");
            return apiRest;
        }
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            Menu menu = menuMapper.getMenuById(id, tenantId);
            if(menu == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("查询菜牌信息失败！");
                return apiRest;
            }
            List<Map> menuGoodsListOld = menuMapper.getMenuGoods(id, tenantId);
            int i = menuMapper.delMenuGoods(id, tenantId);
            if(menuGoodsListOld.size() > i){
                apiRest.setIsSuccess(false);
                apiRest.setError("删除菜牌商品失败！");
                return apiRest;
            }
            List<Map> branchesOld = menuMapper.getMenuBranch(id, tenantId);
            int j = menuMapper.delMenuBranch(id);
            if(branchesOld.size() > j){
                apiRest.setIsSuccess(false);
                apiRest.setError("删除菜牌机构关系失败！");
                return apiRest;
            }
            for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
                Map seGoMap = new HashMap();
                seGoMap.put("id", json.get("goodsId"));
                seGoMap.put("tenantId", tenantId);
                Goods goods = goodsMapper.findGoodsByIdAndTenantId(seGoMap);
                MenuGoods menuGoods = new MenuGoods();
                menuGoods.setMenuId(menu.getId());
                menuGoods.setGoodsCode(goods.getGoodsCode());
                menuGoods.setGoodsId(goods.getId());
                menuGoods.setGoodsName(goods.getGoodsName());
                menuGoods.setSalePrice(BigDecimal.valueOf(Double.valueOf(json.get("salePrice").toString())));
                menuGoods.setVipPrice(BigDecimal.valueOf(Double.valueOf(json.get("vipPrice").toString())));
                menuGoods.setVipPrice2(BigDecimal.valueOf(Double.valueOf(json.get("vipPrice").toString())));
                menuGoods.setTenantId(tenantId);
                menuGoods.setLastUpdateAt(new Date());
                menuGoods.setCreateAt(new Date());
                menuGoods.setLastUpdateBy("System");
                menuGoods.setCreateBy("System");
                menuGoods.setCreateBranchId(createBranchId);
                int k = menuMapper.insertMenuGoods(menuGoods);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("菜牌商品添加失败");
                    return apiRest;
                }
            }
            List<Map> menuGoodsList = menuMapper.getMenuGoods(menu.getId(), tenantId);
            for(String branchId : branchIds.split(",")){
                Map branchMenuR = new HashMap();
                branchMenuR.put("menuId", menu.getId());
                branchMenuR.put("branchId", branchId);
                branchMenuR.put("tenantId", tenantId);
                int a = menuMapper.insertBranchMenuR(branchMenuR);
                if(a <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("菜牌商品添加失败");
                    return apiRest;
                }
            }
            List<Map> branches = menuMapper.getMenuBranch(id, tenantId);
            menu.setMenuName(name);
            menu.setMenuType(menuType);
            menu.setStatus(status);
            menu.setCronStartAt(sdf.parse(cronStartAt + " 00:00:00"));
            menu.setCronEndAt(sdf.parse(cronEndAt + " 23:59:59"));
            menu.setLastUpdateAt(new Date());
            menu.setLastUpdateBy("System");
            menu.setVersion(menu.getVersion().add(BigInteger.ONE));
            menu.setCreateBranchId(createBranchId);
            int b = menuMapper.update(menu);
            if(b <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改菜牌信息失败！");
                return apiRest;
            }
            Map map = new HashMap();
            map.put("menu", menu);
            map.put("detailTotal", menuGoodsList.size());
            map.put("menuGoods", menuGoodsList);
            map.put("branchTotal", branches.size());
            map.put("branches", branches);

            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改菜牌及明细成功！");
            apiRest.setData(map);
        }
        else{
            Menu menu = new Menu();
            ApiRest res = getMenuCode(params);
            String code = "";
            if(!res.getIsSuccess()){
                return res;
            }
            code = res.getData().toString();
            menu.setMenuCode(code);
            menu.setMenuName(name);
            menu.setMenuType(menuType);
            menu.setStatus(status);
            menu.setCronStartAt(sdf.parse(cronStartAt + " 00:00:00"));
            menu.setCronEndAt(sdf.parse(cronEndAt + " 23:59:59"));
            menu.setLastUpdateAt(new Date());
            menu.setLastUpdateBy("System");
            menu.setTenantId(tenantId);
            menu.setCreateAt(new Date());
            menu.setCreateBy("System");
            menu.setVersion(BigInteger.ZERO);
            menu.setCreateBranchId(createBranchId);
            int b = menuMapper.insert(menu);
            if(b <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增菜牌信息失败！");
                return apiRest;
            }
            for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
                Map seGoMap = new HashMap();
                seGoMap.put("id", json.get("goodsId"));
                seGoMap.put("tenantId", tenantId);
                Goods goods = goodsMapper.findGoodsByIdAndTenantId(seGoMap);
                MenuGoods menuGoods = new MenuGoods();
                menuGoods.setMenuId(menu.getId());
                menuGoods.setGoodsCode(goods.getGoodsCode());
                menuGoods.setGoodsId(goods.getId());
                menuGoods.setGoodsName(goods.getGoodsName());
                menuGoods.setSalePrice(BigDecimal.valueOf(Double.valueOf(json.get("salePrice").toString())));
                menuGoods.setVipPrice(BigDecimal.valueOf(Double.valueOf(json.get("vipPrice").toString())));
                menuGoods.setVipPrice2(BigDecimal.valueOf(Double.valueOf(json.get("vipPrice").toString())));
                menuGoods.setTenantId(tenantId);
                menuGoods.setLastUpdateAt(new Date());
                menuGoods.setCreateAt(new Date());
                menuGoods.setLastUpdateBy("System");
                menuGoods.setCreateBy("System");
                menuGoods.setCreateBranchId(createBranchId);
                int k = menuMapper.insertMenuGoods(menuGoods);
                if(k <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("菜牌商品添加失败");
                    return apiRest;
                }
            }
            List<Map> menuGoodsList = menuMapper.getMenuGoods(menu.getId(), tenantId);
            for(String branchId : branchIds.split(",")){
                Map branchMenuR = new HashMap();
                branchMenuR.put("menuId", menu.getId());
                branchMenuR.put("branchId", branchId);
                branchMenuR.put("tenantId", tenantId);
                int a = menuMapper.insertBranchMenuR(branchMenuR);
                if(a <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("菜牌商品添加失败");
                    return apiRest;
                }
            }
            List<Map> branches = menuMapper.getMenuBranch(menu.getId(), tenantId);
            Map map = new HashMap();
            map.put("menu", menu);
            map.put("detailTotal", menuGoodsList.size());
            map.put("menuGoods", menuGoodsList);
            map.put("branchTotal", branches.size());
            map.put("branches", branches);

            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增菜牌及明细成功！");
            apiRest.setData(map);
        }
        return apiRest;
    }

    /**
     * 查询菜牌及明细
     */
    public ApiRest queryMenuInfo(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = StringUtils.trimToNull(params.get("tenantId").toString());
        String branchId = StringUtils.trimToNull(params.get("branchId").toString());
        String menuType;
        Validate.notNull(tenantId, "查询菜牌及明细错误：tenantId is null");
        Validate.notNull(branchId, "查询菜牌及明细错误：branchId is null");
        if(params.get("menuType") == null){
            menuType = "1,2,3";
        }
        else{
            menuType = params.get("menuType").toString();
        }
        List<Map> mInfo = menuMapper.getMenuInfo(tenantId, branchId, menuType);
        List<Map> menuList = new ArrayList();
        for(Map info : mInfo){
            boolean isNot = true;
            String packDetail = "";
            if(info.get("categoryId").toString().equals("-9900")){
                List<Map> list = menuMapper.findMenuGoods(tenantId, info.get("goodsId").toString());
                for(Map i : list){
                    String name = i.get("goodsName").toString().length() > 5 ? i.get("goodsName").toString().substring(0, 5) + ".." : i.get("goodsName").toString();
                    packDetail += (name + "x" + i.get("quantity").toString() + " ");
                }
            }
            info.put("packDetail", packDetail);
            for(Map me : menuList){
                if(me.get("menuId").equals(info.get("menuId"))){
                    List<Map> details = (List<Map>)me.get("details");
                    details.add(copyDetail(info));
                    isNot = false;
                    /*apiRest.setIsSuccess(false);
                    apiRest.setError("查询失败！");
                    return apiRest;*/
                    break;
                }
            }
            if(isNot){
                Map menu = new HashMap();
                menu.put("menuId", info.get("menuId"));
                menu.put("tenantId", info.get("tenantId"));
                menu.put("branchId", info.get("branchId"));
                menu.put("menuCode", info.get("menuCode"));
                menu.put("menuName", info.get("menuName"));
                menu.put("menuType", info.get("menuType"));
                menu.put("status", info.get("status"));
                menu.put("cronStartAt", info.get("cronStartAt"));
                menu.put("cronEndAt", info.get("cronEndAt"));
                menu.put("isActive", info.get("isActive"));
                menu.put("memo", info.get("memo"));
                List<Map> details = new ArrayList<>();
                details.add(copyDetail(info));
                menu.put("details", details);
                menuList.add(menu);
            }
        }
        apiRest.setData(menuList);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    /**
     * 查询菜牌及明细（套餐组和明细）
     */
    public ApiRest queryMenuInfoPackage(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = StringUtils.trimToNull(params.get("tenantId").toString());
        String branchId = StringUtils.trimToNull(params.get("branchId").toString());
        String menuType;
        Validate.notNull(tenantId, "查询菜牌及明细错误：tenantId is null");
        Validate.notNull(branchId, "查询菜牌及明细错误：branchId is null");
        if(params.get("menuType") == null){
            menuType = "1,2,3";
        }
        else{
            menuType = params.get("menuType").toString();
        }
        List<Map> mInfo = menuMapper.getMenuInfo(tenantId, branchId, menuType);
        List<Map> menuList = new ArrayList();
        for(Map info : mInfo){
            boolean isNot = true;
            String packDetail = "";
            if(info.get("categoryId").toString().equals("-9900")){
                List<Map> list = menuMapper.findMenuGoods(tenantId, info.get("goodsId").toString());
                for(Map i : list){
                    String name = i.get("goodsName").toString().length() > 5 ? i.get("goodsName").toString().substring(0, 5) + ".." : i.get("goodsName").toString();
                    packDetail += (name + "x" + i.get("quantity").toString() + " ");
                }
                List<PackageGroup> packageGroupList = groupGoodsMapper.getGroupsByPackId(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(info.get("goodsId").toString())));
                List<Map> result = new ArrayList<>();
                for(PackageGroup packageGroup : packageGroupList){
                    Map group = new HashMap();
                    group.put("group", packageGroup);
                    List<Map> groupGoodses = groupGoodsMapper.getGroupGoodsByGroupId(BigInteger.valueOf(Long.valueOf(tenantId)), packageGroup.getId());
                    group.put("groupGoods", groupGoodses);
                    group.put("goodsTotal", groupGoodses.size());
                    result.add(group);

                }
                info.put("groups", result);
                info.put("groupCount", packageGroupList.size());
            }
            info.put("packDetail", packDetail);
            for(Map me : menuList){
                if(me.get("menuId").equals(info.get("menuId"))){
                    List<Map> details = (List<Map>)me.get("details");
                    details.add(copyDetail2(info));
                    isNot = false;
                    /*apiRest.setIsSuccess(false);
                    apiRest.setError("查询失败！");
                    return apiRest;*/
                    break;
                }
            }
            if(isNot){
                Map menu = new HashMap();
                menu.put("menuId", info.get("menuId"));
                menu.put("tenantId", info.get("tenantId"));
                menu.put("branchId", info.get("branchId"));
                menu.put("menuCode", info.get("menuCode"));
                menu.put("menuName", info.get("menuName"));
                menu.put("menuType", info.get("menuType"));
                menu.put("status", info.get("status"));
                menu.put("cronStartAt", info.get("cronStartAt"));
                menu.put("cronEndAt", info.get("cronEndAt"));
                menu.put("isActive", info.get("isActive"));
                menu.put("memo", info.get("memo"));
                List<Map> details = new ArrayList<>();
                details.add(copyDetail2(info));
                menu.put("details", details);
                menuList.add(menu);
            }
        }
        apiRest.setData(menuList);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    /**
     * 导出电子秤商品
     */
    public ApiRest exportWeighGoods(Map params){
        ApiRest apiRest = new ApiRest();
        int onlySelf = 0;
        if(params.get("isUseHqGoods") != null && "1".equals(params.get("isUseHqGoods"))){
            onlySelf = 1;
        }
        params.put("onlySelf", onlySelf);
        List<Map> list = menuMapper.exportWeighGoods(params);
        apiRest.setIsSuccess(true);
        apiRest.setData(list);
        apiRest.setMessage("查询电子秤商品成功");
        return apiRest;
    }

    /**
     * 更新菜牌明细价格
     * */
    public ApiRest updateMenuDetailPrice(Map params) throws ParseException{
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("id").toString()));
        JSONObject goodsJson = fromObject(params.get("goods"));
        ApiRest rest = SaaSApi.findTenantById(tenantId);
        if(!rest.getIsSuccess()){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询商户失败！");
            return apiRest;
        }
        String business = ((Map)((Map)rest.getData()).get("tenant")).get("business1").toString();
        if(!"6".equals(business)){
            apiRest.setIsSuccess(false);
            apiRest.setError("只有商圈业态可以使用此功能！");
            return apiRest;
        }

        Map goodsMap = new HashMap();
        if(goodsJson.get("goods") == null || goodsJson.get("goods").equals("")){
            apiRest.setIsSuccess(false);
            apiRest.setError("json参数错误，请确认参数准确！");
            return apiRest;
        }

        //原菜牌明细
        Map mgMap = (Map) findMenuDetail(params).getData();
        List<Map> menuGoods = (List) mgMap.get("menuGoods");

        for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
            if(!(json.get("goodsId") != null && json.get("goodsId") != "" && json.get("salePrice") != null && json.get("salePrice") != "" && json.get("vipPrice") != null && json.get("vipPrice") != "")){
                apiRest.setIsSuccess(false);
                apiRest.setError("json参数错误，请确认参数准确！");
                return apiRest;
            }
        }
        String goodsIds = "";
        for(Map json : (List<Map>)goodsJson.getJSONArray("goods")){
            goodsMap.put(json.get("goodsId"), json.get("goodsId"));
            Map goMap = new HashMap();
            goMap.put("id", json.get("goodsId"));
            goMap.put("tenantId", tenantId);
            Goods goods = goodsMapper.findGoodsByIdAndTenantId(goMap);
            if(goods == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未找到该商品");
                return apiRest;
            }else{
                goodsIds += json.get("goodsId") + ",";
            }
        }
        if(goodsMap.size() < goodsJson.getJSONArray("goods").size()){
            apiRest.setIsSuccess(false);
            apiRest.setError("存在重复商品，保存失败！");
            return apiRest;
        }

        //添加原菜牌商品
        List goodsList = goodsJson.getJSONArray("goods");
        for(Map map : menuGoods){
            String goodsId = map.get("goodsId").toString();
            if(goodsIds.indexOf(goodsId) < 0){
                goodsList.add(map);
            }
        }

        Menu menu = menuMapper.getMenuById(id, tenantId);
        if(menu == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜牌信息失败！");
            return apiRest;
        }

        List<Map> branches = menuMapper.getMenuBranch(id, tenantId);
        String branchIds = "";
        for(int i=0; i<branches.size(); i++){
            branchIds += branches.get(i).get("id").toString() + ",";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> newParams = new HashMap<>();
        newParams.put("tenantId", tenantId);
        newParams.put("branchIds", branchIds.substring(0, branchIds.lastIndexOf(",")));
        newParams.put("cronStartAt", sdf.format(menu.getCronStartAt()));
        newParams.put("cronEndAt", sdf.format(menu.getCronEndAt()));
        newParams.put("name", menu.getMenuName());
        newParams.put("status", menu.getStatus());
        newParams.put("createBranchId", menu.getCreateBranchId());
        newParams.put("menuType", menu.getMenuType());
        newParams.put("goods", goodsJson);

        ApiRest addRest = addOrUpdateMenu(newParams);
        if(!addRest.getIsSuccess()){
            apiRest.setIsSuccess(false);
            apiRest.setError("新增菜牌失败");
            return apiRest;
        }

        //删除原菜牌
        ApiRest delRest = delMenu(params);
        if(!delRest.getIsSuccess()){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除原菜牌失败");
        }

        apiRest.setIsSuccess(true);
        apiRest.setMessage("更新菜牌成功");
        return apiRest;
    }

    private Map copyDetail(Map info){
        Map detail = new HashMap();
        detail.put("goodsId", info.get("goodsId"));
        detail.put("goodsCode", info.get("goodsCode"));
        detail.put("goodsName", info.get("goodsName"));
        detail.put("goodsCategoryId", info.get("categoryId"));
        detail.put("goodsCategoryName", info.get("categoryName"));
        detail.put("goodsUnitId", info.get("goodsUnitId"));
        detail.put("goodsUnitName", info.get("goodsUnitName"));
        detail.put("parentId", info.get("parentId"));
        detail.put("standardName", info.get("standardName"));
        detail.put("boxPrice", info.get("boxPrice"));

        detail.put("salePrice", info.get("salePrice"));
        detail.put("vipPrice", info.get("vipPrice"));
        detail.put("vipPrice2", info.get("vipPrice2"));
        detail.put("goodsType", info.get("goodsType"));
        detail.put("photo", info.get("photo"));
        detail.put("goodsStatus", info.get("goodsStatus"));
        detail.put("isNewgood", info.get("isNewgood"));
        detail.put("isRecommended", info.get("isRecommended"));
        detail.put("isTakeout", info.get("isTakeout"));
        detail.put("isForPoints", info.get("isForPoints"));
        detail.put("packDetail", info.get("packDetail"));
        return detail;
    }

    private Map copyDetail2(Map info){
        Map detail = new HashMap();
        detail.put("goodsId", info.get("goodsId"));
        detail.put("goodsCode", info.get("goodsCode"));
        detail.put("goodsName", info.get("goodsName"));
        detail.put("goodsCategoryId", info.get("categoryId"));
        detail.put("goodsCategoryName", info.get("categoryName"));
        detail.put("goodsUnitId", info.get("goodsUnitId"));
        detail.put("goodsUnitName", info.get("goodsUnitName"));
        detail.put("parentId", info.get("parentId"));
        detail.put("standardName", info.get("standardName"));
        detail.put("boxPrice", info.get("boxPrice"));

        detail.put("salePrice", info.get("salePrice"));
        detail.put("vipPrice", info.get("vipPrice"));
        detail.put("vipPrice2", info.get("vipPrice2"));
        detail.put("goodsType", info.get("goodsType"));
        detail.put("photo", info.get("photo"));
        detail.put("goodsStatus", info.get("goodsStatus"));
        detail.put("isNewgood", info.get("isNewgood"));
        detail.put("isRecommended", info.get("isRecommended"));
        detail.put("isTakeout", info.get("isTakeout"));
        detail.put("isForPoints", info.get("isForPoints"));
        detail.put("packDetail", info.get("packDetail"));
        detail.put("groups", info.get("groups"));
        detail.put("groupCount", info.get("groupCount"));
        return detail;
    }
}
