package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.PinYinHelper;
import erp.chain.domain.Branch;
import erp.chain.domain.Category;
import erp.chain.domain.Goods;
import erp.chain.domain.system.SysConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CategoryMapper;
import erp.chain.mapper.GoodsMapper;
import erp.chain.service.system.TenantConfigService;
import erp.chain.utils.CommonUtils;
import erp.chain.utils.SerialNumberGenerate;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 分类
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CategoryService{
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private BranchMapper branchMapper;
    /**
     * 分类列表
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest queryCatList(Map params) throws ServiceException {
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        if(params.get("rows") != null && params.get("page") != null){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            params.put("rows", rows);
            params.put("offset", offset);
        }
        else{
            params.put("rows", 9999);
            params.put("offset", 1);
        }
        params.put("tenantId", tenantId);
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        List<Map> list = categoryMapper.queryCatList(params);
        Long count = categoryMapper.queryUnitCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询分类成功！");
        return apiRest;
    }
    /**
     * 分类列表
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest queryCatById(Map params) throws ServiceException {
        ApiRest apiRest=new ApiRest();
        Category category = categoryMapper.queryCatById(params);

        if("所有分类".equals(category.getParentName())){
            category.setParentCategoryType(0);
        }else{
            params.put("id",category.getParentId());
            Category categoryFather = categoryMapper.queryCatById(params);
            category.setParentCategoryType(categoryFather.getCategoryType());
        }

        if(category == null){
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询分类失败！");
        }
        else{
            apiRest.setIsSuccess(true);
            apiRest.setData(category);
            apiRest.setMessage("查询分类成功！");
        }
        return apiRest;
    }
    /**
     * 根据分类id修改分类名称(接口)
     * @param params
     * @return
     */
    public ApiRest updateCatName(Map params) {
        ApiRest apiRest = new ApiRest();
        if(params.get("catName").toString().equals("套餐")){
            apiRest.setIsSuccess(false);
            apiRest.setError("分类名称不允许为“套餐”，请修改");
            return apiRest;
        }
        Long count = categoryMapper.checkName(params);
        if(count > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("分类名称重复，修改失败！");
            return apiRest;
        }
        Category category = categoryMapper.queryCatById(params);
        if(category != null){
            if(!(params.get("catName").toString()).equals(category.getCatName())){
                Map map = new HashMap();
                map.put("tenantId", category.getTenantId());
                if(category.getBranchId() != null){
                    map.put("branchId", category.getBranchId());
                }
                else{
                    map.put("branchId", BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())));
                    category.setBranchId(BigInteger.valueOf(Long.parseLong(params.get("branchId").toString())));
                }
                map.put("catName", params.get("catName").toString());
                map.put("cateId", category.getId());
                goodsMapper.updateUnitOrCategory(map);
            }
            category.setCatName(params.get("catName").toString());
            category.setMnemonics(PinYinHelper.stringFirstLetter(params.get("catName").toString()));
            category.setLastUpdateAt(new Date());
            category.setLastUpdateBy("System");
            /*POS销售中是否隐藏*/
            String isHidden;
            if(params.get("isHidden") != null && !"".equals(params.get("isHidden"))){
                isHidden = params.get("isHidden").toString();
                if("1".equals(isHidden)){
                    category.setCategoryType(1);
                }
                else{
                    category.setCategoryType(0);
                }
            }
            if(params.get("scoreType") != null && !"".equals(params.get("scoreType"))){
                category.setScoreType(new Integer(params.get("scoreType").toString()));
            }
            if(params.get("scoreValue") != null && !"".equals(params.get("scoreValue"))){
                category.setScoreValue(new BigDecimal(params.get("scoreValue").toString()));
            }
            if(params.get("scorePercent") != null && !"".equals(params.get("scorePercent"))){
                category.setScorePercent(new BigDecimal(params.get("scorePercent").toString()));
            }
            int flag1 = categoryMapper.update(category);
            List<Category> sonCatList =categoryMapper.getSecondByParentId(category.getTenantId(),category.getId());
            int flag2 = 1;
            if(sonCatList.size() != 0){

                Category categoryImpl =  new Category();
                categoryImpl.setCategoryType(category.getCategoryType());
                categoryImpl.setLastUpdateAt(new Date());
                categoryImpl.setLastUpdateBy("System");
                categoryImpl.setParentId(category.getId());
                categoryImpl.setTenantId(category.getTenantId());
                //父类的POS销售中隐藏 状态发生改变，子类也跟着变
               flag2 = categoryMapper.doUpdatePosStatus(categoryImpl);
            }

            if(flag1 > 0 && flag2 >0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("分类修改成功！");
                apiRest.setData(category);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setMessage("分类修改失败！");
            }
            /*修改子分类的上级分类名*/
            categoryMapper.updateSubName(category.getTenantId(), category.getId(), category.getCatName());
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setMessage("查询分类失败！");
        }
        return apiRest;
    }

    /**
     * 修改分类积分
     * */
    public ApiRest updateCatScore(Map params){
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
        Category category = categoryMapper.queryCatById(params);
        if(category == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询分类失败");
            return apiRest;
        }
        map.put("tenantId", category.getTenantId());
        map.put("branchId", category.getBranchId());
        map.put("catId", category.getId());
        categoryMapper.deleteCategoryScore(category.getTenantId(), category.getId());
        categoryMapper.saveCategoryScore(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("更新分类积分成功");
        return apiRest;
    }

    /**
     * 增加分类
     * @param params
     * @return
     */
    public ApiRest addCategory(Map params) {
        ApiRest apiRest = new ApiRest();
        Category category = new Category();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger branchId = null;
        if(params.get("branchId") != null && !"".equals(params.get("branchId"))){
            branchId = BigInteger.valueOf(Long.parseLong((String)params.get("branchId")));
        }
        BigInteger parentId = BigInteger.valueOf(Long.parseLong((String)params.get("parentId")));
        String catName = params.get("catName").toString();
        if(params.get("catName").toString().equals("套餐")){
            apiRest.setIsSuccess(false);
            apiRest.setError("分类名称不允许为“套餐”，请修改");
            return apiRest;
        }
        String mnemonics = PinYinHelper.stringFirstLetter(StringUtils.trimToEmpty(catName));
        String code;
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
        /*POS销售中是否隐藏*/
        String isHidden;
        if(params.get("isHidden") != null && !"".equals(params.get("isHidden"))){
            isHidden = params.get("isHidden").toString();
            if("1".equals(isHidden)){
                params.put("categoryType", "1");
            }
        }

        category.setTenantId(tenantId);
        category.setBranchId(branchId);
        category.setParentId(parentId);
        category.setCatName(catName);
        category.setMnemonics(mnemonics);
        category.setStore(false);
        category.setDiscount(false);
        category.setCreateBy("System");
        category.setCreateAt(new Date());
        category.setLastUpdateAt(new Date());
        category.setLastUpdateBy("System");
        category.setVersion(BigInteger.valueOf(0));
        Long count = categoryMapper.checkName(params);
        if(count > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("分类名称重复，添加失败！");
            return apiRest;
        }
        if(Objects.equals(parentId, BigInteger.valueOf(-1))){
            if(params.get("categoryType") != null && !params.get("categoryType").equals("")){
                category.setCategoryType(Integer.valueOf(params.get("categoryType").toString()));
            }
            else{
                category.setCategoryType(0);
            }
            ApiRest firstCheck = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_FIRST_CAT_NUM);
            if(!firstCheck.getIsSuccess()){
                apiRest = firstCheck;
                return apiRest;
            }
            if(categoryMapper.getFirstCatCode(params) == null){
                code = "01";
            }
            else{
                Integer length = String.valueOf(Integer.parseInt(categoryMapper.getFirstCatCode(params)) + 1).length();
                if(length > 2){
                    code = SerialNumberGenerate.getNextCode(length, categoryMapper.getFirstCatCode(params));
                }
                else{
                    code = SerialNumberGenerate.getNextCode(2, categoryMapper.getFirstCatCode(params));
                }
            }
            category.setCatCode(code);
            category.setParentName("所有分类");
            int i = categoryMapper.insert(category);
            if(i > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("分类添加成功！");
                apiRest.setData(category);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setMessage("分类添加失败！");
            }
            ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_FIRST_CAT_NUM);
            if(!r.getIsSuccess()){
                apiRest = r;
                return apiRest;
            }
        }
        else{
            ApiRest secondCheck = tenantConfigService.checkConfig(tenantId, SysConfig.SYS_SECOND_CAT_NUM);
            if(!secondCheck.getIsSuccess()){
                apiRest = secondCheck;
                return apiRest;
            }
            Map map = new HashMap();
            map.put("id", parentId);
            map.put("tenantId", tenantId);
            Category pCategory = categoryMapper.queryCatById(map);
            if(pCategory != null){
                int goodsCount = categoryMapper.countByCategoryIdAndTenantId(pCategory.getId(), tenantId);
                if(goodsCount > 0){
                    apiRest.setError("分类下已建立" + businessName + "档案,不能增加下级分类!");
                    apiRest.setIsSuccess(false);
                    return apiRest;
                }
                if(business.equals("1")){
                    if(pCategory.getCatCode().length() == 4){
                        apiRest.setError("二级分类下不允许再添加子分类！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }
                else if(business.equals("2")){
                    if(pCategory.getCatCode().length() == 6){
                        apiRest.setError("三级分类下不允许再添加子分类！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }
                String oneCode = pCategory.getCatCode();
                category.setParentName(pCategory.getCatName());
                category.setCategoryType(pCategory.getCategoryType());
                Map codeMap = new HashMap();
                codeMap.put("oneCode", oneCode);
                codeMap.put("tenantId", tenantId);
                code = categoryMapper.getSecondCatCode(codeMap);
                if(code != null && !code.equals("")){
                    category.setCatCode(SerialNumberGenerate.getNextCatTwoCode(code));
                }
                else{
                    category.setCatCode(SerialNumberGenerate.getNextCatTwoCode(oneCode + "00"));
                }
                int i = categoryMapper.insert(category);
                if(i > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("二级分类添加成功！");
                    apiRest.setData(category);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("二级分类添加失败！");
                }
                ApiRest r = tenantConfigService.addTenantConfigOne(tenantId, SysConfig.SYS_SECOND_CAT_NUM);
                if(!r.getIsSuccess()){
                    apiRest = r;
                    return apiRest;
                }
            }
            else{
                apiRest.setError("未查询到相关父级分类，请核对参数是否正确");
                apiRest.setIsSuccess(false);
                return apiRest;
            }
        }
        apiRest.setMessage("添加成功");
        apiRest.setIsSuccess(true);
        apiRest.setData(category);
        return apiRest;
    }
    /**
     * 删除商品分类（接口用）
     * @param params
     * @return
     */
    public ApiRest delCat(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            Category category = categoryMapper.queryCatById(params);
            // 删除时判断是否有菜品使用
            if(category != null){
                int goodsCount = categoryMapper.countByCategoryIdAndTenantId(id, tenantId);
                int cateCount = categoryMapper.countByParentIdAndTenantId(id, tenantId);
                if(goodsCount > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("分类已经被使用，删除失败！");
                }
                if(cateCount > 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("分类含有下级分类，删除失败！");
                }
                if(goodsCount == 0 && cateCount == 0){
                    category.setIsDeleted(true);
                    category.setLastUpdateAt(new Date());
                    category.setLastUpdateBy("System");
                    int i = categoryMapper.update(category);
                    if(i > 0){
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("分类删除成功！");
                    }
                    else{
                        apiRest.setIsSuccess(false);
                        apiRest.setMessage("分类删除失败！");
                    }
                    ApiRest r;
                    if(Objects.equals(category.getParentId(), BigInteger.valueOf(-1))){
                        r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_FIRST_CAT_NUM);
                        if(!r.getIsSuccess()){
                            apiRest = r;
                            return r;
                        }
                    }
                    else{
                        r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_SECOND_CAT_NUM);
                        if(!r.getIsSuccess()){
                            apiRest = r;
                            return r;
                        }
                    }
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("删除分类成功！");
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("未找到ID为" + id + "，商户ID为" + tenantId + "的分类，请确认信息是否准确！");
                return apiRest;
            }
        }
        else if(params.get("ids") != null && !params.get("ids").equals("")){
            Map resultMap = new HashMap();
            int count = 0;
            List<Category> catList = new ArrayList<>();
            List<Map> errorList = new ArrayList<>();
            String ids = params.get("ids").toString();
            for(String catId : ids.split(",")){
                BigInteger id = BigInteger.valueOf(Long.parseLong(catId));
                Map errorMap = new HashMap();
                Map catMap = new HashMap();
                catMap.put("tenantId", tenantId);
                catMap.put("id", id);
                Category category = categoryMapper.queryCatById(catMap);
                // 删除时判断是否有菜品使用
                if(category != null){
                    category.setIsDeleted(true);
                    category.setLastUpdateAt(new Date());
                    category.setLastUpdateBy("System");
                    ApiRest r;
                    if(Objects.equals(category.getParentId(), BigInteger.valueOf(-1))){
                        r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_FIRST_CAT_NUM);
                        if(!r.getIsSuccess()){
                            apiRest = r;
                            return r;
                        }
                    }
                    else{
                        r = tenantConfigService.minusTenantConfigOne(tenantId, SysConfig.SYS_SECOND_CAT_NUM);
                        if(!r.getIsSuccess()){
                            apiRest = r;
                            return r;
                        }
                    }
                    int i = categoryMapper.update(category);
                    if(i > 0){
                        count++;
                        catList.add(category);
                        apiRest.setIsSuccess(true);
                        apiRest.setMessage("分类删除成功！");
                    }
                    else{
                        errorMap.put("id", category.getId());
                        errorMap.put("mes", "分类删除失败！");
                        errorList.add(errorMap);
                        apiRest.setIsSuccess(false);
                        apiRest.setMessage("分类删除失败！");
                    }
                }
                else{
                    errorMap.put("id", id);
                    errorMap.put("mes", "未找到分类ID为" + id + "，商户ID为" + tenantId + "的分类，请确认信息是否准确！");
                    errorList.add(errorMap);
                    apiRest.setIsSuccess(false);
                    apiRest.setError("未找到ID为" + id + "，商户ID为" + tenantId + "的分类，请确认信息是否准确！");
                    return apiRest;
                }
            }
            if(count > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("分类删除成功");
            }
            if(errorList.size() > 0){
                apiRest.setError("");
                for(int i = 0; i < errorList.size(); i++){
                    apiRest.setError("id为" + errorList.get(i).get("id") + "的分类：" + errorList.get(i).get("mes") + "。");
                }
            }
            resultMap.put("num", count);
            resultMap.put("errors", errorList);
            resultMap.put("delItem", catList);
            apiRest.setData(resultMap);
        }
        return apiRest;
    }

    /**
     * 获取分类编码
     * @param params
     * @return
     */
    public ApiRest getCatCode(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String parentId = params.get("parentId").toString();
        String code;
        Map map = new HashMap();
        if(parentId.equals("-1")){
            map.put("tenantId", tenantId);
            if(categoryMapper.getFirstCatCode(params) == null){
                code = "01";
            }
            else{
                Integer length = String.valueOf(Integer.parseInt(categoryMapper.getFirstCatCode(params)) + 1).length();
                if(length > 2){
                    code = SerialNumberGenerate.getNextCode(length, categoryMapper.getFirstCatCode(params));
                }
                else{
                    code = SerialNumberGenerate.getNextCode(2, categoryMapper.getFirstCatCode(params));
                }
            }
            //code=SerialNumberGenerate.getNextCode(2, categoryMapper.getFirstCatCode(params));
        }
        else{
            map.put("tenantId", tenantId);
            map.put("oneCode", params.get("oneCode"));
            code = categoryMapper.getSecondCatCode(map);
            if(code != null && !code.equals("")){
                code = SerialNumberGenerate.getNextCatTwoCode(code);
            }
            else{
                code = SerialNumberGenerate.getNextCatTwoCode(params.get("oneCode") + "00");
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取编码成功！");
        apiRest.setData(code);
        return apiRest;
    }

    /**
     * 获取分类树
     * @param params
     * @return
     */
    public ApiRest getCatTree(Map params){
        ApiRest apiRest = new ApiRest();
        int onlySelf = 0;
        if(params.get("onlySelf") != null && !"".equals(params.get("onlySelf"))){
            onlySelf = Integer.parseInt(params.get("onlySelf").toString());
        }
        params.put("onlySelf", onlySelf);
        if(params.get("headOffice") != null && "true".equals(params.get("headOffice"))){
            String branchIds = params.get("branchIds") == null ? params.get("branchId").toString() : params.get("branchIds").toString();
            Branch branch = branchMapper.findHeadquartersBranch(BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString())));
            branchIds = branchIds + "," + branch.getId();
            params.put("branchIds", branchIds);
        }
        List<Category> list = categoryMapper.queryCatTreeData(params);
        Category category = new Category();
        category.setId(BigInteger.valueOf(-1));
        category.setCatName("所有分类");
        category.setCatCode("00");
        list.add(category);
        if(params.get("havePackage") != null && params.get("havePackage").equals("1")){
            Category category1 = new Category();
            category1.setId(BigInteger.valueOf(-9900));
            category1.setParentId(BigInteger.valueOf(-1));
            category1.setCatName("套餐");
            category1.setCatCode("9900");
            list.add(category1);
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询分类树成功！");
        apiRest.setData(list);
        return apiRest;
    }

    public ApiRest getNoChildCategoryList(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("tenantBusiness") != null && !"".equals(params.get("tenantBusiness")) && "6".equals(params.get("tenantBusiness"))){
            params.put("onlySelf", 1);
        }
        else{
            params.put("onlySelf", 0);
        }
        List<Category> list = categoryMapper.queryCats(params);
        List<Category> result = new ArrayList<>();
        for(Category c : list){
            params.put("parentId", c.getId());
            List<Category> l = categoryMapper.queryCats(params);
            if(l.size() == 0){
                result.add(c);
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询分类树成功！");
        apiRest.setData(result);
        return apiRest;
    }

    /**
     * 分类排序，规则0000000000共11位，前3位一级分类，次3位二级分类，后5位预留商品顺序
     * 给一级分类排序，初始化其下级分类的默认顺序，若有商品，初始化其下商品的默认顺序
     * 给二级分类排序，若父级分类未排序，初始化一级分类的顺序，若有商品，初始化其下商品的默认顺序
     * @param params
     * @return
     */
    public ApiRest orderCategory(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        String tenantBusiness = null;
        if(params.get("tenantBusiness") != null){
            tenantBusiness = params.get("tenantBusiness").toString();
        }
        String catLevel = params.get("catLevel").toString();//分类级别
        JSONArray catJsonArr = JSONArray.fromObject(params.get("catJson"));
        JSONArray lastJsonArr = new JSONArray();
        JSONArray lastGoodsJsonArr = new JSONArray();
        List<Map> orderList = categoryMapper.orderedCategory(tenantId, branchId);
        Map<String, String> orderedMap = new HashMap<>();
        for(Map m : orderList){
            orderedMap.put(m.get("catId").toString(), m.get("orderId").toString());
        }
        if(catJsonArr.size() > 0){
            if(catLevel.equals("1")){//一级分类
                for(Object o : catJsonArr){
                    Map<String, Object> objMap = (Map<String, Object>)o;
                    BigInteger orderId = BigInteger.valueOf(Long.valueOf(objMap.get("orderId").toString()));
                    BigInteger catId = BigInteger.valueOf(Long.valueOf(objMap.get("catId").toString()));
                    List<Map> orderFirstGoodsList = goodsMapper.orderedGoods(tenantId, branchId, catId);
                    Map<String, String> orderedFirstGoodsMap = new HashMap<>();
                    for(Map m : orderFirstGoodsList){
                        orderedFirstGoodsMap.put(m.get("goodsId").toString(), m.get("orderId").toString());
                    }
                    orderId = orderId.multiply(BigInteger.valueOf(100000000));
                    objMap.put("orderId", orderId);
                    lastJsonArr.add(objMap);
                    List<Category> categoryList = categoryMapper.getSecondByParentId(tenantId, catId);
                    for(Category c : categoryList){//二级存在情况，已经排过（新规则OR老规则），未排过
                        Map<String, Object> catMap = new HashMap<>();
                        BigInteger secondOrderId = null;
                        if(StringUtils.isNotEmpty(orderedMap.get(c.getId().toString()))){//已经排过序
                            secondOrderId = BigInteger.valueOf(Long.valueOf(orderedMap.get(c.getId().toString())));
                            if(secondOrderId.compareTo(BigInteger.valueOf(100000000)) < 0){//用的排序方式是老规则（1，2，3...）
                                secondOrderId = orderId.add(secondOrderId.multiply(BigInteger.valueOf(100000)));
                            }
                            else{//新规则
                                secondOrderId = orderId.add(secondOrderId.mod(BigInteger.valueOf(100000000)));
                            }
                        }
                        else{//未排过的默认在前
                            secondOrderId = orderId;
                        }
                        catMap.put("catId", c.getId());
                        catMap.put("orderId", secondOrderId);
                        lastJsonArr.add(catMap);
                        List<Map> orderSecondGoodsList = goodsMapper.orderedGoods(tenantId, branchId, c.getId());
                        Map<String, String> orderedSecondGoodsMap = new HashMap<>();
                        for(Map m : orderSecondGoodsList){
                            orderedSecondGoodsMap.put(m.get("goodsId").toString(), m.get("orderId").toString());
                        }
                        List<Goods> goodsList = goodsMapper.getGoodsByCatId(tenantId, c.getId());
                        for(Goods g : goodsList){//二级分类下的商品排序
                            Map<String, Object> goodsMap = new HashMap<>();
                            BigInteger goodsOrderId = null;
                            if(StringUtils.isNotEmpty(orderedSecondGoodsMap.get(g.getId().toString()))){//已经排过序
                                goodsOrderId = BigInteger.valueOf(Long.valueOf(orderedSecondGoodsMap.get(g.getId().toString())));
                                if(goodsOrderId.compareTo(BigInteger.valueOf(100000000)) < 0){//用的排序方式是老规则（1，2，3...）
                                    goodsOrderId = secondOrderId.add(goodsOrderId);
                                }
                                else{//新规则
                                    goodsOrderId = secondOrderId.add(goodsOrderId.mod(BigInteger.valueOf(100000)));
                                }
                            }
                            else{//未排过的默认在前
                                goodsOrderId = secondOrderId;
                            }
                            goodsMap.put("catId", g.getCategoryId());
                            goodsMap.put("goodsId", g.getId());
                            goodsMap.put("orderId", goodsOrderId);
                            lastGoodsJsonArr.add(goodsMap);
                        }
                    }
                    List<Goods> goodsList = goodsMapper.getGoodsByCatId(tenantId, catId);
                    for(Goods g : goodsList){//一级分类下的商品排序
                        Map<String, Object> goodsMap = new HashMap<>();
                        BigInteger goodsOrderId = null;
                        if(StringUtils.isNotEmpty(orderedFirstGoodsMap.get(g.getId().toString()))){//已经排过序
                            goodsOrderId = BigInteger.valueOf(Long.valueOf(orderedFirstGoodsMap.get(g.getId().toString())));
                            if(goodsOrderId.compareTo(BigInteger.valueOf(100000000)) < 0){//用的排序方式是老规则（1，2，3...）
                                goodsOrderId = orderId.add(goodsOrderId);
                            }
                            else{//新规则
                                goodsOrderId = orderId.add(goodsOrderId.mod(BigInteger.valueOf(100000)));
                            }
                        }
                        else{//未排过的默认在前
                            goodsOrderId = orderId;
                        }
                        goodsMap.put("catId", g.getCategoryId());
                        goodsMap.put("goodsId", g.getId());
                        goodsMap.put("orderId", goodsOrderId);
                        lastGoodsJsonArr.add(goodsMap);
                    }
                }
            }
            else if(catLevel.equals("2")){//二级分类排序
                Map catP = new HashMap();
                catP.put("id", ((Map<String, Object>)catJsonArr.get(0)).get("catId").toString());
                catP.put("tenantId", tenantId);
                Category cat = categoryMapper.queryCatById(catP);//去父类
                catP.put("id", cat.getParentId());
                Category parentCat = categoryMapper.queryCatById(catP);
                if(orderedMap.get(parentCat.getId().toString()) == null){//若父类未排序，则提示排父类
                    apiRest.setIsSuccess(false);
                    apiRest.setMessage("为了给您提供更好的体验，请先对上级分类进行排序！");
                    return apiRest;
                }
                BigInteger firstOrderId = BigInteger.valueOf(100000000);
                List<Map> list = categoryMapper.listOrderCategory(tenantId, branchId, tenantBusiness);//查询已经排过的一级分类
                for(Map m : list){
                    BigInteger orderId = m.get("orderId") == null ? BigInteger.ZERO : BigInteger.valueOf(Long.valueOf(m.get("orderId").toString()));
                    BigInteger parentCatId = BigInteger.valueOf(Long.valueOf(m.get("id").toString()));
                    Map<String, Object> firstMap = new HashMap<>();
                    if(orderId.compareTo(BigInteger.ZERO) == 0){
                        orderId = BigInteger.valueOf(100000000);
                    }
                    else if(orderId.compareTo(BigInteger.valueOf(100000000)) < 0){
                        orderId = orderId.multiply(BigInteger.valueOf(100000000));
                    }
                    if(parentCat.getId().compareTo(parentCatId) == 0){
                        firstOrderId = orderId;
                    }
                    firstMap.put("orderId", orderId);
                    firstMap.put("catId", parentCatId);
                    lastJsonArr.add(firstMap);
                }

                for(Object o : catJsonArr){
                    Map<String, Object> objMap = (Map<String, Object>)o;
                    BigInteger orderId = BigInteger.valueOf(Long.valueOf(objMap.get("orderId").toString()));
                    BigInteger catId = BigInteger.valueOf(Long.valueOf(objMap.get("catId").toString()));
                    BigInteger secondOrderId = firstOrderId.add(orderId.multiply(BigInteger.valueOf(100000)));
                    objMap.put("orderId", secondOrderId);
                    lastJsonArr.add(objMap);

                    List<Map> orderSecondGoodsList = goodsMapper.orderedGoods(tenantId, branchId, catId);
                    Map<String, String> orderedSecondGoodsMap = new HashMap<>();
                    for(Map m : orderSecondGoodsList){
                        orderedSecondGoodsMap.put(m.get("goodsId").toString(), m.get("orderId").toString());
                    }
                    List<Goods> goodsList = goodsMapper.getGoodsByCatId(tenantId, catId);
                    for(Goods g : goodsList){//二级分类下的商品排序
                        Map<String, Object> goodsMap = new HashMap<>();
                        BigInteger goodsOrderId = null;
                        if(StringUtils.isNotEmpty(orderedSecondGoodsMap.get(g.getId().toString()))){//已经排过序
                            goodsOrderId = BigInteger.valueOf(Long.valueOf(orderedSecondGoodsMap.get(g.getId().toString())));
                            if(goodsOrderId.compareTo(BigInteger.valueOf(100000000)) < 0){//用的排序方式是老规则（1，2，3...）
                                goodsOrderId = secondOrderId.add(goodsOrderId);
                            }
                            else{//新规则
                                goodsOrderId = secondOrderId.add(goodsOrderId.mod(BigInteger.valueOf(100000)));
                            }
                        }
                        else{//未排过的默认在前
                            goodsOrderId = secondOrderId;
                        }
                        goodsMap.put("catId", g.getCategoryId());
                        goodsMap.put("goodsId", g.getId());
                        goodsMap.put("orderId", goodsOrderId);
                        lastGoodsJsonArr.add(goodsMap);
                    }
                }
            }
            if(lastJsonArr.size()>0){
                categoryMapper.deleteCategoryOrder(tenantId, branchId, lastJsonArr);
                categoryMapper.insertCatOrder(tenantId, branchId, lastJsonArr);
                categoryMapper.updateLastUpdateAt(catJsonArr);
            }
            if(lastGoodsJsonArr.size() > 0){
                goodsMapper.deleteGoodsOrderV2(tenantId, branchId);
                goodsMapper.insertGoodsOrderV2(tenantId, branchId, lastGoodsJsonArr);
                goodsMapper.updateLastUpdateAt(lastGoodsJsonArr);
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("成功对" + catJsonArr.size() + "个分类进行排序！");
        return apiRest;
    }

    /**
     * 查询一级分类排序
     * @param params
     * @return
     */
    public ApiRest listOrderCategory(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        String tenantBusiness = null;
        if(params.get("tenantBusiness") != null){
            tenantBusiness = params.get("tenantBusiness").toString();
        }
        List<Map> list = categoryMapper.listOrderCategory(tenantId, branchId, tenantBusiness);
        Map result = new HashMap();
        result.put("list", list);
        result.put("total", list.size());
        apiRest.setData(result);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询一级分类排序成功！");
        return apiRest;
    }
    /**
     * 查询二级分类排序
     * @param params
     * @return
     */
    public ApiRest listOrderSecondCategory(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(params.get("branchId").toString()));
        BigInteger firstCatId = BigInteger.valueOf(Long.valueOf(params.get("firstCatId").toString()));
        List<Map> list = categoryMapper.listOrderSecondCategory(firstCatId, tenantId, branchId);
        Map result = new HashMap();
        result.put("list", list);
        result.put("total", list.size());
        apiRest.setData(result);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询二级分类排序成功！");
        return apiRest;
    }
}
