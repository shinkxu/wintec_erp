package erp.chain.service.o2o;

import com.saas.common.Constants;
import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Branch;
import erp.chain.domain.Tenant;
import erp.chain.domain.o2o.BranchDiscount;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.o2o.VipType;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.mapper.o2o.VipTypeMapper;
import erp.chain.utils.Args;
import erp.chain.utils.GsonUntil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;
import saas.api.util.ApiBaseServiceUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/1/12.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipGradeService{
    @Autowired
    private VipTypeMapper vipTypeMapper;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private BranchMapper branchMapper;

    /**
     * 获取商户所有会员类型
     *
     * @return List < VipType >,size>=1
     * @throws IllegalArgumentException :参数不符合要求
     */
    public ApiRest queryVipTypeList(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        Map map = new HashMap();
        map.put("tenantId", tenantId);
        ApiRest rest = SaaSApi.findTenantById(tenantId);
        if(!rest.getIsSuccess()){
            return rest;
        }
        Map tenant = (Map)((Map)rest.getData()).get("tenant");
        if(tenant.get("isBranchManagementVip").toString().equals("false") || tenant.get("isBranchManagementVip").toString().equals("0")){
            params.remove("branchId");
        }
        List<VipType> list = vipTypeMapper.vipTypeList(params);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询数据成功！");
        apiRest.setData(list);
        return apiRest;
    }

    /**
     * 获取商户所有会员类型
     *
     * @param tId not null
     * @return List < VipType >,size>=1
     * @throws IllegalArgumentException :参数不符合要求
     */
    public List<VipType> getVipTypeList(BigInteger tId){
        Validate.notNull(tId, "tId not null");
        Map map = new HashMap();
        map.put("tenantId", tId);
        return vipTypeMapper.vipTypeList(map);
    }

    /**
     * 查询会员自动升级类型
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ApiRest qryUpgradeType(Map params){
        ApiRest rest1 = new ApiRest();
        Map<String, Object> map = new HashMap<>();
        ApiRest rest = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        boolean branchFlag = false;
        if(Constants.REST_RESULT_SUCCESS.equals(rest.getResult())){
            Tenant tenant = null;
            if(rest.getData() != null){
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
                if(tenant != null){
                    branchFlag = tenant.isBranchManagementVip();
                }
            }
        }
        if(!branchFlag && params.get("branchId") != null){
            //查询总部机构
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tenantId", params.get("tenantId"));
            map1.put("branchType", 0);
            Branch branch0 = branchMapper.find(map1);
            params.put("branchId", branch0.getId());
        }
        List<VipType> list = vipTypeMapper.vipTypeList(params);
        if(list != null && list.size() > 0){
            rest1.setIsSuccess(true);
            rest1.setData(list.get(0).getUpgradeType());
        }
        return rest1;
    }

    /**
     * 查询会员等级列表
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipGradeList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        ApiRest rest = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        boolean branchFlag = false;
        if(Constants.REST_RESULT_SUCCESS.equals(rest.getResult())){
            Tenant tenant = null;
            if(rest.getData() != null){
                tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
                if(tenant != null){
                    branchFlag = tenant.isBranchManagementVip();
                }
            }
        }
        if(!branchFlag && params.get("branchId") != null){
            //查询总部机构
            Map<String, Object> map1 = new HashMap<>();
            map1.put("tenantId", params.get("tenantId"));
            map1.put("branchType", 0);
            Branch branch0 = branchMapper.find(map1);
            params.put("branchId", branch0.getId());
        }
        List<VipType> countList = vipTypeMapper.vipTypeList(params);
        if(params.get("page") != null && params.get("rows") != null){
            params.put("offset", ((Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString())));
        }

        List<VipType> list = vipTypeMapper.vipTypeList(params);
        int count = countList.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 保存或者新增会员等级
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveVipType(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("tenantId", params.get("tenantId"));
        map1.put("branchType", 0);
        Branch branch0 = branchMapper.find(map1);
        boolean branchFlag = false;
        ApiRest rest = SaaSApi.findTenantById(new BigInteger(params.get("tenantId").toString()));
        if(Constants.REST_RESULT_SUCCESS.equals(rest.getResult())){
            if(rest.getData() != null){
                Tenant tenant = ApiBaseServiceUtils.MapToObject((Map)((Map)rest.getData()).get("tenant"), Tenant.class);
                branchFlag = tenant.isBranchManagementVip();
            }
        }
        Map<String, Object> mapVipType = new HashMap<>();
        mapVipType.put("tenantId", params.get("tenantId"));
        //机构管理会员时，加入机构参数
        if(branchFlag && params.get("branchId") != null){
            mapVipType.put("branchId", params.get("branchId"));
        }
        else{
            mapVipType.put("branchId", branch0.getId());
        }
        List<VipType> vipTypes = vipTypeMapper.vipTypeList(mapVipType);
        //设置会员升级等级，则检测是否重复
        if("1".equals(params.get("autoUpgrade")) && params.get("currLevel") != null && !"".equals(params.get("currLevel")) && !"0".equals(params.get("currLevel"))){
            mapVipType.put("currLevel", params.get("currLevel"));
            mapVipType.put("autoUpgrade", 1);
            List<VipType> checks = vipTypeMapper.vipTypeList(mapVipType);
            if(checks != null && checks.size() > 0){
                if(params.get("id") != null && !"".equals(params.get("id"))){
                    if(!new BigInteger(params.get("id").toString()).equals(checks.get(0).getId())){
                        result.setSuccess("1");
                        result.setMsg("Level" + params.get("currLevel") + "已被占用，请重新选择！");
                        return result;
                    }
                }
                else{
                    result.setSuccess("1");
                    result.setMsg("Level" + params.get("currLevel") + "已被占用，请重新选择！");
                    return result;
                }
            }
            if(params.get("upgradeLimit") != null && !"".equals(params.get("upgradeLimit"))){
                //检测数值
                if(vipTypes != null){
                    for(VipType vt : vipTypes){
                        if(vt.getCurrLevel() != null && vt.getCurrLevel() > 0){
                            if(vt.getCurrLevel() < Integer.valueOf(params.get("currLevel").toString())){
                                if(vt.getUpgradeLimit() != null && vt.getUpgradeLimit().doubleValue() >= Double.parseDouble(params.get("upgradeLimit").toString())){
                                    result.setSuccess("1");
                                    result.setMsg("升级条件数值不能低于Level" + vt.getCurrLevel() + "的数值。");
                                    return result;
                                }
                            }
                            else if(vt.getCurrLevel() > Integer.valueOf(params.get("currLevel").toString())){
                                if(vt.getUpgradeLimit() != null && vt.getUpgradeLimit().doubleValue() <= Double.parseDouble(params.get("upgradeLimit").toString())){
                                    result.setSuccess("1");
                                    result.setMsg("升级条件数值不能高于Level" + vt.getCurrLevel() + "的数值。");
                                    return result;
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> map2 = new HashMap<>();
        map2.put("tenantId", params.get("tenantId"));
        map2.put("id", params.get("id"));
        VipType vipType;
        if("1".equals(params.get("isOnlineDefault"))){
            if(vipTypes != null){
                for(VipType vt : vipTypes){
                    if(vt.getIsOnlineDefault()){
                        vt.setIsOnlineDefault(false);
                        vipTypeMapper.update(vt);
                    }
                }
            }
        }
        else if("0".equals(params.get("isOnlineDefault"))){
            //如果是修改
            if(params.get("id") != null && !"".equals(params.get("id"))){
                vipType =  vipTypeMapper.findByCondition(map2);
                if(vipType != null && vipType.isOnlineDefault()){
                    result.setSuccess("1");
                    result.setMsg("默认会员类型不能置为空!");
                    return result;
                }
            }
        }

        if(params.get("id") != null && !"".equals(params.get("id"))){
            vipType =  vipTypeMapper.findByCondition(map2);
            params.put("lastUpdateAt", new Date());
            params.put("lastUpdateBy", "System");
            if(params.get("discountRate") == null){
                params.put("discountRate", null);
            }
            params.remove("branchId");
            int flag = vipTypeMapper.update(params);
            if(flag == 1){
                result.setSuccess("0");
                result.setMsg("会员类型修改成功!");
            }
            VipType newVipType = vipTypeMapper.findByCondition(map2);
            if(vipType.getPreferentialPolicy().compareTo(new BigInteger("3")) == 0 && newVipType.getPreferentialPolicy().compareTo(new BigInteger("3")) != 0){
                BranchDiscount branchDiscount = new BranchDiscount();
                branchDiscount.setTenantId(vipType.getTenantId());
                branchDiscount.setTypeId(vipType.getId());
                branchDiscount.setIsDeleted(true);
                branchDiscount.setLastUpdateAt(new Date());
                vipTypeMapper.deleteBranchDiscountByType(branchDiscount);
            }
        }
        else{//id不存在，则新增
            int pointsFactor = 1;
            int scoreUsage = 1;
            //若未传入typeCode则去现有数据最大值加一
            if(null == params.get("typeCode")){
                Map<String, Object> param = new HashMap<>();
                param.put("tenantId", params.get("tenantId"));
                //机构管理会员时，加入机构参数
                if(branchFlag && params.get("branchId") != null){
                    param.put("branchId", params.get("branchId"));
                }
                else{
                    param.put("branchId", branch0.getId());
                    params.put("branchId", branch0.getId());
                }
                List<VipType> list = vipTypeMapper.vipTypeList(param);
                int typeCodeInt = -1;
                if(null != list && list.size() > 0){
                    for(VipType vt : list){
                        if(Integer.parseInt(vt.getTypeCode()) > typeCodeInt){
                            typeCodeInt = Integer.parseInt(vt.getTypeCode());
                        }
                        if(vt.getPointsFactor() != null){
                            pointsFactor = vt.getPointsFactor().intValue();
                        }
                        if(vt.getScoreUsage() != null){
                            scoreUsage = vt.getScoreUsage().intValue();
                        }
                    }
                }
                params.put("typeCode", String.format("%04d", typeCodeInt + 1));
            }
            //若名字为空，则加入默认值
            if(null == params.get("typeName")){
                params.put("typeName", "新会员等级" + params.get("typeCode"));
            }
            if(null == params.get("preferentialPolicy")){
                params.put("preferentialPolicy", 0);
            }
            params.put("pointsFactor", pointsFactor);
            params.put("scoreUsage", scoreUsage);

            params.put("isDeleted", 0);
            params.put("createAt", new Date());
            params.put("lastUpdateAt", new Date());
            int flag = vipTypeMapper.insert(params);
            if(flag == 1){
                result.setSuccess("0");
                result.setMsg("新增会员类型成功!");
            }
        }
        return result;
    }

    /**
     * 根据会员ID获取会员
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipTypeById(Map<String,String> params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(StringUtils.isNotEmpty(params.get("id"))){
            Map<String, Object> param = new HashMap<>();
            param.put("id", params.get("id"));
            VipType vipType = vipTypeMapper.findByCondition(param);
            if(vipType != null){
                Map<String,String> typeMap = new HashMap<>();
                typeMap.put("tenantId",vipType.getTenantId().toString());
                typeMap.put("branchId",params.get("branchId"));
                typeMap.put("typeId",vipType.getId().toString());
                Map disMap = vipMapper.branchDiscount(typeMap);
                if(disMap != null && disMap.get("discountRate") != null){
                    vipType.setDiscountRate(BigDecimal.valueOf(Double.valueOf(disMap.get("discountRate").toString())));
                }
                result.setSuccess("0");
                result.setObject(vipType);
            }
        }
        else{
            result.setIsSuccess(false);
            result.setMsg("无效的ID");
        }
        return result;
    }
//
//    /**
//     * 根据会员类型id查询会员类型
//     * @author szq
//     */
//    def queryVipTypeById(BigInteger id){
//        ApiRest rest = new ApiRest();
//        if (id) {
//            def vipType = VipType.findById(id);
//            rest.isSuccess = true;
//            rest.message = "会员类型查询成功";
//            rest.data = vipType;
//        } else {
//            rest.isSuccess = false;
//            rest.message = "会员类型查询失败";
//        }
//        return rest;
//    }
//

    /**
     * 验证会员等级编码是否唯一
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON uniqueTypeCode(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(params.get("typeCode") != null){
            Map<String, Object> param = new HashMap<>();
            param.put("tenantId", params.get("tenant_id"));
            param.put("typeCode", params.get("typeCode"));
            //根据会员编码获取会员
            VipType vipType = vipTypeMapper.findByCondition(param);
            if(vipType != null){//如果能找到会员等级信息
                if(StringUtils.isNotBlank(params.get("id").toString())){
                    //判断是是否是正在修改的会员等级本身的编码
                    if(!vipType.getId().equals(new BigInteger(params.get("id").toString()))){
                        //不是会员本身的编码则说明编码重复
                        result.setSuccess("1");
                    }
                    else{
                        //已存在编码是会员本身的说明不重复
                        result.setSuccess("0");
                    }
                }
                else{
                    //不是正在编辑的,则编码重复
                    result.setSuccess("1");
                }
            }
            else{
                //找不到会员信息则会员编码不重复
                result.setSuccess("0");
            }
        }
        else{
            result.setIsSuccess(false);
            result.setMsg("会员编码无效");
        }
        return result;
    }

    /**
     * 删除会员信息
     *
     * @param ids
     * @return
     * @throws ServiceException
     */
    public ApiRest delVipTypes(String[] ids, String tId) throws ServiceException{
        String[] errors;
        //int num = 0;
        ApiRest rest = new ApiRest();
        for(String id : ids){
            if(id != null && !"".equals(id)){
                Map<String, Object> param = new HashMap<>();
                param.put("tenantId", new BigInteger(tId));
                param.put("id", new BigInteger(id));
                VipType vipType = vipTypeMapper.findByCondition(param);
                if(vipType != null){
                    try{
                        if(!vipType.getIsOnlineDefault()){
                            Map<String, Object> param1 = new HashMap<>();
                            param1.put("typeId", id);
                            List<Vip> list = vipMapper.select(param1);
                            if(list.size() <= 0){
                                param1.put("id", id);
                                param1.put("isDeleted", 1);
                                param1.put("lastUpdateAt", new Date());
                                param1.put("lastUpdateBy", "System");
                                int falg = vipTypeMapper.update(param1);
                                if(falg == 0){
                                    rest.setIsSuccess(false);
                                    rest.setMessage("会员类型删除失败");
                                    return rest;
                                }
                                else{
                                    rest.setIsSuccess(true);
                                    rest.setMessage("会员类型删除成功");
                                    return rest;
                                }
                            }
                            else{
                                rest.setIsSuccess(false);
                                rest.setMessage("该会员类型已经被使用，不允许删除");
                                return rest;
                            }
                        }
                    }
                    catch(Exception e){
                        rest.setIsSuccess(false);
                        rest.setError(e.getMessage());
                        rest.setMessage(e.getMessage());
                        LogUtil.logError(e.getMessage());
                    }
                }
            }
        }
        return rest;
    }

    /**
     * 无分页查询会员等级
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public List<VipType> queryVipTypeByVip(Map params) throws ServiceException{
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        if("true".equals(params.get("isBranchManagementVip"))){
            map.put("branchId", params.get("branchId"));
        }
        return vipTypeMapper.vipTypeList(params);
    }
//    /**
//     * 根据tenantId查询VipType
//     * @param params
//     * @return
//     * @throws ServiceException
//     */
//    def qryVipTypeByTenatId(Map params) throws ServiceException {
//        try {
//            String tenantId = params.tenantId;
//            if (params.tenantId) {
//                def list = VipType.findAllByTenantIdAndIsDeleted(Integer.parseInt(tenantId), false);
//                return list;
//            }
//
//
//        } catch (Exception e) {
//            ServiceException se = new ServiceException("1001", "查询失败", e.message)
//            throw se
//        }
//        return null;
//
//    }

    /**
     * 修改多个vipType
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveVipTypes(Map params) throws ServiceException, UnsupportedEncodingException{
        ResultJSON result = new ResultJSON();
        //查询总部机构
        Map<String, Object> map1 = new HashMap<>();
        map1.put("tenantId", params.get("tenantId"));
        map1.put("branchType", 0);
        Branch branch0 = branchMapper.find(map1);
        //将前台传来的json数据转为实体类
        List<Map> vipTypes = GsonUntil.jsonAsList(URLDecoder.decode(params.get("datas").toString(), "UTF-8"), Map.class);
        //循环保存所有
        if(null != vipTypes && vipTypes.size() > 0){
            int flag = 0;
            for(int i = 0; i < vipTypes.size(); i++){
                //判断会员类型名称是否重复
                List<VipType> vipTypeList = null;
                if("true".equals(params.get("isBranchManagementVip"))){
                    Map<String, Object> map = new HashMap<>();
                    map.put("tenantId", params.get("tenantId"));
                    map.put("typeName", vipTypes.get(i).get("id"));
                    map.put("branchId", params.get("branchId"));
                    vipTypeList = vipTypeMapper.vipTypeList(map);
                }
                else{
                    Map<String, Object> map = new HashMap<>();
                    map.put("tenantId", params.get("tenantId"));
                    map.put("typeName", vipTypes.get(i).get("typeName"));
                    map.put("branchId", branch0.getId());
                    vipTypeList = vipTypeMapper.vipTypeList(map);
                }
                if(vipTypeList != null){
                    for(int j = 0; j < vipTypeList.size(); j++){
                        if(!vipTypeList.get(j).getId().equals(vipTypes.get(i).get("id"))){
                            result.setSuccess("1");
                            result.setMsg(vipTypes.get(i).get("typeName") + " 会员类型名称重复");
                            return result;
                        }
                    }
                }
                //先查询，保存有变化的字段。
                Map<String, Object> param = new HashMap<>();
                String id = vipTypes.get(i).get("id").toString();
                param.put("id", Integer.parseInt(id.substring(0, id.length() - 2)));
                param.put("typeName", vipTypes.get(i).get("typeName"));
                String preferentialPolicy = vipTypes.get(i).get("preferentialPolicy").toString();
                param.put("preferentialPolicy", Integer.parseInt(preferentialPolicy.substring(0, preferentialPolicy.length() - 2)));
                param.put("isOnlineDefault", vipTypes.get(i).get("onlineDefault"));
                String pointsFactor = vipTypes.get(i).get("pointsFactor").toString();
                if("0.00".equals(pointsFactor)){
                    result.setSuccess("1");
                    result.setMsg("0元不能获得1积分");
                    return result;
                }
                if(Integer.parseInt(pointsFactor.substring(0, pointsFactor.length() - 3)) < 0){
                    result.setSuccess("1");
                    result.setMsg("请输入大于0的整数");
                    return result;
                }
                param.put("pointsFactor", vipTypes.get(i).get("pointsFactor"));
                String scoreUsagee = vipTypes.get(i).get("scoreUsage").toString();
                if("0.00".equals(scoreUsagee)){
                    result.setSuccess("1");
                    result.setMsg("0积分不能抵用1元现金");
                    return result;
                }
                if(Integer.parseInt(scoreUsagee.substring(0, scoreUsagee.length() - 3)) < 0){
                    result.setSuccess("1");
                    result.setMsg("请输入大于0的整数");
                    return result;
                }
                param.put("scoreUsage", Integer.parseInt(scoreUsagee.substring(0, scoreUsagee.length() - 3)));
                if(vipTypes.get(i).get("memPriceUsed") != null){
                    String memPriceUsed = vipTypes.get(i).get("memPriceUsed").toString();
                    param.put("memPriceUsed", Integer.parseInt(memPriceUsed.substring(0, memPriceUsed.length() - 2)));
                }
                param.put("discountRate", vipTypes.get(i).get("discountRate"));
                param.put("toSavePoints", vipTypes.get(i).get("toSavePoints"));
                param.put("branchId", vipTypes.get(i).get("branchId"));
                flag = vipTypeMapper.update(param);
                if(flag == 0){
                    result.setSuccess("1");
                    result.setMsg("保存失败");
                    return result;
                }
            }
        }
        result.setSuccess("0");
        result.setMsg("保存成功");
        return result;
    }

    /**
     * 修改会员积分规则
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON updateVipScoreRule(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        //查询总部机构
        Map<String, Object> map1 = new HashMap<>();
        map1.put("tenantId", params.get("tenantId"));
        map1.put("branchType", 0);
        Branch branch0 = branchMapper.find(map1);
        //判断会员类型名称是否重复
        List<VipType> vipTypeList = null;
        if(params.get("pointsFactor") != null){
            String pointsFactor = params.get("pointsFactor").toString();
            /*if("0.00".equals(pointsFactor)){
                result.setSuccess("1");
                result.setMsg("pointsFactor不能为0");
                return result;
            }*/
            if(Double.parseDouble(pointsFactor) < 0){
                result.setSuccess("1");
                result.setMsg("请输入大于等于0的整数");
                return result;
            }
            if(Double.parseDouble(pointsFactor) > 9999.99){
                result.setSuccess("1");
                result.setMsg("请输入小于等于9999.99的整数");
                return result;
            }
        }
        if(params.get("scoreUsage") != null){
            String scoreUsagee = params.get("scoreUsage").toString();
            if("0.00".equals(scoreUsagee)){
                result.setSuccess("1");
                result.setMsg("0积分不能抵用1元现金");
                return result;
            }
            if(Integer.parseInt(scoreUsagee) < 0){
                result.setSuccess("1");
                result.setMsg("请输入大于0的整数");
                return result;
            }
        }
        if("true".equals(params.get("isBranchManagementVip"))){
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("branchId", params.get("branchId"));
            vipTypeList = vipTypeMapper.vipTypeList(map);
        }
        else{
            Map<String, Object> map = new HashMap<>();
            map.put("tenantId", params.get("tenantId"));
            map.put("branchId", branch0.getId());
            vipTypeList = vipTypeMapper.vipTypeList(map);
        }
        int flag = 0;
        if(vipTypeList != null){
            for(VipType vipType : vipTypeList){
                //先查询，保存有变化的字段。
                Map<String, Object> param = new HashMap<>();
                param.put("id", vipType.getId());
                if(params.get("pointsFactor") != null){
                    param.put("pointsFactor", params.get("pointsFactor"));
                }
                if(params.get("scoreUsage") != null){
                    param.put("scoreUsage", params.get("scoreUsage"));
                }
                if(params.get("scoreType") != null){
                    param.put("scoreType", params.get("scoreType"));
                }
                if(params.get("upgradeType") != null){
                    param.put("upgradeType", params.get("upgradeType"));
                }
                param.put("lastUpdateAt", new Date());
                flag = vipTypeMapper.update(param);
            }
        }
        if(flag == 0){
            result.setSuccess("1");
            result.setMsg("保存失败");
            return result;
        }
        result.setSuccess("0");
        result.setMsg("保存成功");
        return result;

    }

    /**
     * 删除未保存的会员类型
     *
     * @param params
     * @return
     */
    public ResultJSON delNewType(Map params){
        ResultJSON result = new ResultJSON();
        if(params.get("newId") != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", params.get("newId"));
            map.put("isDeleted", 1);
            vipTypeMapper.update(map);
            result.setSuccess("0");
            result.setMsg("删除成功");
        }
        return result;
    }

    /**
     * 获取商户积分规则
     * 积分规则使用线上会员类型积分规则数据
     *
     * @param tId not null
     * @return '{"pointsFactor":"积分折算系数-多少元等于1积分","scoreUsage":"设置X积分等于1元，可用于前台消费"}'
     * @author hxh
     */
    public Map<String, Object> queryIntegralRule(BigInteger tId){
        Validate.notNull(tId, "tId not null");
        Map<String, Object> param = new HashMap<>();
        param.put("tenantId", tId);
        List<VipType> list = vipTypeMapper.vipTypeList(param);
        double pointsFactor = 1.0;
        int scoreUsage = 1;
        if(list != null){
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).getPointsFactor() != null && list.get(i).getScoreUsage() != null){
                    pointsFactor = list.get(i).getPointsFactor().doubleValue();
                    scoreUsage = list.get(i).getScoreUsage().intValue();
                }
            }
        }
        Map result = new HashMap();
        result.put("pointsFactor", pointsFactor);
        result.put("scoreUsage", scoreUsage);
        return result;
    }

    /**
     * 获取商户积分规则
     * 积分规则使用线上会员类型积分规则数据
     *
     * @return '{"pointsFactor":"积分折算系数-多少元等于1积分","scoreUsage":"设置X积分等于1元，可用于前台消费"}'
     * @author hxh
     */
    public Map<String, Object> queryIntegralRuleBranch(Map params){
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(params.get("tenantId").toString()));
        ApiRest rest = SaaSApi.findTenantById(tenantId);
        if(!rest.getIsSuccess()){
            return new HashMap<>();
        }
        Map tenant = (Map)((Map)rest.getData()).get("tenant");
        if(tenant.get("isBranchManagementVip").toString().equals("false") || tenant.get("isBranchManagementVip").toString().equals("0")){
            params.remove("branchId");
        }
        VipType type = vipTypeMapper.queryIntegralRuleByBranch(params);
        Map result = new HashMap();
        result.put("pointsFactor", type.getPointsFactor());
        result.put("scoreUsage", type.getScoreUsage());
        return result;
    }

    /**
     * 修改商户积分规则
     * 会更新所有会员类型的积分规则
     *
     * @param tId          not null
     * @param scoreUsage   not null    1 <= scoreUsage >= 9999
     * @param pointsFactor not null  1 <= pointsFactor >= 9999
     * @throws IllegalArgumentException :参数不符合要求
     * @author hxh
     */
    public void updateIntegralRule(BigInteger tId, BigInteger scoreUsage, BigDecimal pointsFactor){
        Validate.notNull(tId, "tId not null");
        Validate.notNull(scoreUsage, "scoreUsage not null");
        Validate.notNull(pointsFactor, "pointsFactor not null");
        Validate.isTrue(scoreUsage.compareTo(BigInteger.ONE) >= 0 && scoreUsage.compareTo(new BigInteger("9999")) <= 0, "1 <= scoreUsage <= 9999");
        Validate.isTrue(pointsFactor.compareTo(BigDecimal.ZERO) >= 0 && pointsFactor.compareTo(new BigDecimal("9999.99")) <= 0, "0 =< pointsFactor <= 9999.99");
        //BigDecimal pointsFactorr = new BigDecimal(pointsFactor)
        //更新所有会员类型的积分规则
        List<VipType> types = this.getVipTypeList(tId);
        for(VipType type : types){
            type.setScoreUsage(scoreUsage);
            type.setPointsFactor(pointsFactor);
            type.setLastUpdateAt(new Date());
            type.setLastUpdateBy("System");
            vipTypeMapper.update(type);
        }
    }
//
//    /**
//     * 获取商户所有会员类型
//     *
//     * @param tId not null
//     *
//     * @exception
//     * IllegalArgumentException :参数不符合要求
//     *
//     * @return List < VipType >,size>=1
//     */
//    public List<VipType> queryVipTypeList(BigInteger tId) {
//        Validate.notNull(tId, "tId not null")
//        return VipType.findAllByTenantIdAndIsDeleted(tId, false)
//    }
//

    /**
     * 保存线下会员类型 （设置isOnlineDefault=false、pointsFactor、scoreUsage根据积分规则设置）
     * 添加选项
     * typeName、preferentialPolicy、memPriceUsed、toSavePoints、discountRate
     * <p/>
     * preferentialPolicy 必须是[0,2,3]中一个
     * preferentialPolicy == 0 则 memPriceUsed 、discountRate可为null,保存时设置为null
     * preferentialPolicy == 2 则 memPriceUsed not null,必须是1或2;discountRate可为null,保存时设置为null
     * preferentialPolicy == 3 则 memPriceUsed 可为null,保存时设置为null;discountRate not null,必须 >=1 && <= 100
     * <p/>
     * typeName not null 10个字符以内包含10
     * <p/>
     * toSavePoints not null 必须是[1,0]中一个,其中1开启0关闭
     * <p/>
     * tenantId not null
     *
     * @param type not null
     * @return 保存成功之后会员类型
     * @throws IllegalArgumentException :参数不符合要求
     */
    public VipType saveOfflineVipType(VipType type){
        Validate.notNull(type, "type not null");
        Validate.notNull(type.getTenantId(), "tenantId not null");
        boolean boo = true;
        if(vipTypeMapper.findByTenantIdAndTypeName(type.getTenantId(), type.getTypeName()) != null){
            boo = false;
        }
        Args.isTrue(boo, "类型名称不能重复");
        if(type.getPreferentialPolicy() != null){
            String[] stringList = new String[]{"0", "2", "3"};
            Args.isIn(type.getPreferentialPolicy().toString(), stringList, "preferentialPolicy必须是[0,2,3]中一个");
        }
        Args.notNull(type.getTypeName(), "typeName not null");
        Args.isTrue(type.getTypeName().length() <= 10, "typeName 10个字符以内包含10");
        if(type.getToSavePoints() != null){
            String[] stringList2 = new String[]{"0", "1"};
            Args.isIn(type.getToSavePoints().toString(), stringList2, "toSavePoints必须是[0, 1]中一个");
        }
        if(type.getPreferentialPolicy().equals(BigInteger.ZERO)){
            type.setMemPriceUsed(null);
            type.setDiscountRate(null);
        }
        if(type.getPreferentialPolicy().equals(BigInteger.valueOf(2))){
            if(type.getMemPriceUsed() != null){
                String[] li = new String[]{"2", "1"};
                Args.isIn(type.getMemPriceUsed().toString(), li, "memPriceUsed必须是[2, 1]中一个");
                type.setDiscountRate(null);
            }
        }
        if(type.getPreferentialPolicy().equals(BigInteger.valueOf(3))){
            Args.isTrue(type.getDiscountRate().compareTo(BigDecimal.ONE) >= 0 && type.getDiscountRate().compareTo(BigDecimal.valueOf(100)) <= 0, "discountRate not null,必须 >=1 && <= 100");
            type.setMemPriceUsed(null);
        }
        //设置积分规则
        Map IntegralRule = this.queryIntegralRule(type.getTenantId());
        type.setTypeCode(nextVipTypeCode(type.getTenantId()));
        type.setPointsFactor(BigDecimal.valueOf(Double.valueOf(IntegralRule.get("pointsFactor").toString())));
        type.setScoreUsage(BigInteger.valueOf(Long.valueOf(IntegralRule.get("scoreUsage").toString())));
        type.setIsOnlineDefault(false);
        type.setCreateAt(new Date());
        type.setCreateBy("System");
        type.setLastUpdateAt(new Date());
        type.setLastUpdateBy("System");
        vipTypeMapper.insert(type);
        /*
            会员分类只能有一个是线上的
         */
        if(type.getIsOnlineDefault()){
            VipType online = vipTypeMapper.queryIntegralRule(type.getTenantId());
            online.setIsOnlineDefault(false);
        }
        return type;
    }

    private String nextVipTypeCode(BigInteger tId){

        /*String sql = "select max(v.type_code) from vip_type v where v.tenant_id = ${tId}"
        String maxCode = getSession().createSQLQuery(sql).uniqueResult()
        int code = 0
        if (maxCode != null) {
            code = Integer.parseInt(maxCode) + 1
        }
        return String.format("%04d", code)*/
        String sql = "select * from vip_type v where v.tenant_id = ${tId} order By v.type_code";
        List<VipType> list = vipTypeMapper.getVipTypeCode(tId);
        int code = 0;
        if(null != list && list.size() > 0){
            /*for (VipType vt : list) {
                if (Integer.parseInt(vt.getTypeCode()) > code) {
                    code = Integer.parseInt(vt.getTypeCode());
                }
            }*/
            for(VipType vipType : list){
                if(Integer.parseInt(vipType.getTypeCode()) > code){
                    code = Integer.parseInt(vipType.getTypeCode());
                }
            }
            code = code + 1;
        }
        return String.format("%04d", code);
    }


    /**
     * 修改会员类型，此方法不会改变会员类型的isOnlineDefault属性，可修改的选项
     * typeName、preferentialPolicy、memPriceUsed、toSavePoints、discountRate
     * <p/>
     * preferentialPolicy 必须是[0,2,3]中一个
     * preferentialPolicy == 0 则 memPriceUsed 、discountRate可为null,保存时设置为null
     * preferentialPolicy == 2 则 memPriceUsed not null,必须是1或2;discountRate可为null,保存时设置为null
     * preferentialPolicy == 3 则 memPriceUsed 可为null,保存时设置为null;discountRate not null,必须 >=1 && <= 100
     * <p/>
     * typeName not null 10个字符以内包含10
     * <p/>
     * toSavePoints not null 必须是[1,0]中一个,其中1开启0关闭
     * <p/>
     * tenantId not null
     * <p/>
     * id not null
     *
     * @param type not null
     * @return 保存成功之后会员类型
     * @throws IllegalArgumentException :参数不符合要求,修改的会员类型不存在
     */
    public VipType updateVipTypeIgnoreOnlineDefault(VipType type){
        Validate.notNull(type, "type not null");
        Validate.notNull(type.getTenantId(), "tenantId not null");
        Validate.notNull(type.getId(), "id not null");
        if(type.getPreferentialPolicy() != null){
            String[] stringList = new String[]{"0", "2", "3"};
            Args.isIn(type.getPreferentialPolicy().toString(), stringList, "preferentialPolicy必须是[0,2,3]中一个");
        }
        Args.notNull(type.getTypeName(), "typeName not null");
        Args.isTrue(type.getTypeName().length() <= 10, "typeName 10个字符以内包含10");
        if(type.getToSavePoints() != null){
            String[] stringList2 = new String[]{"0", "1"};
            Args.isIn(type.getToSavePoints().toString(), stringList2, "toSavePoints必须是[0, 1]中一个");
        }

        if(type.getPreferentialPolicy().equals(BigInteger.ZERO)){
            type.setMemPriceUsed(null);
            type.setDiscountRate(null);
        }
        if(type.getPreferentialPolicy().equals(BigInteger.valueOf(2))){
            if(type.getMemPriceUsed() != null){
                String[] li = new String[]{"2", "1"};
                Args.isIn(type.getMemPriceUsed().toString(), li, "memPriceUsed必须是[2, 1]中一个");
                type.setDiscountRate(null);
            }
        }
        if(type.getPreferentialPolicy().equals(BigInteger.valueOf(3))){
            Args.isTrue(type.getDiscountRate().compareTo(BigDecimal.ONE) >= 0 && type.getDiscountRate().compareTo(BigDecimal.valueOf(100)) <= 0, "discountRate not null,必须 >=1 && <= 100");
            type.setMemPriceUsed(null);
        }
        Map par = new HashMap();
        par.put("id", type.getId());
        par.put("tenantId", type.getTenantId());
        VipType oldType = vipTypeMapper.findByCondition(par);
        Args.notNull(oldType, "[${type.id}] 会员类型不存在或已删除");
        VipType isType = vipTypeMapper.findByTenantIdAndTypeName(type.getTenantId(), type.getTypeName());
        boolean boo = true;
        if(isType != null && !isType.getTypeName().equals(oldType.getTypeName())){
            boo = false;
        }
        Args.isTrue(boo, " 该会员类型已经存在");
        oldType.setTypeName(type.getTypeName());
        oldType.setPreferentialPolicy(type.getPreferentialPolicy());
        oldType.setMemPriceUsed(type.getMemPriceUsed());
        oldType.setDiscountRate(type.getDiscountRate());
        oldType.setToSavePoints(type.getToSavePoints());
        type.setIsOnlineDefault(false);
        vipTypeMapper.update(oldType);
        return oldType;
    }
//
//    /**
//     * 删除会员类型，线上注册默认会员、类型下有会员不能被删除
//     *
//     * @param id not null
//     * @param tId not null
//     *
//     * @return
//     *
//     * @exception
//     * IllegalArgumentException :参数不符合要求;删除 线上注册默认会员、类型下有会员;会员类型不存在
//     *
//     * @author hxh
//     */
//    public void delVipType(BigInteger id, BigInteger tId) {
//        Args.notNull(id, "id not null")
//        Args.notNull(tId, "tId not null")
//
//        VipType type = VipType.findByIdAndTenantIdAndIsDeleted(id, tId, false)
//        Args.notNull(type, "会员类型不存在")
//
//        if (type.isOnlineDefault){
//            Args.isTrue(false,"线上注册默认会员类型不能被删除")
//        }
//
//        //检测该类型下是否有会员
//        List<Vip> list = Vip.findAllByTypeId(type.id);
//        if (list.size() > 0) {
//            Args.isTrue(false,"[${type.typeName}]存在${list.size()}个会员")
//        }
//
//        type.isDeleted = true
//        type.save(flush: true)
//    }
//
//    /**
//     * 初始化vipType
//     */
//    public void initVipType(BigInteger tId){
//        String sql1 = """INSERT INTO vip_type (tenant_id,type_code,
//        type_name,preferential_policy,points_factor,score_usage,
//                mem_price_used,discount_rate,to_save_points,
//                is_package_disc,is_promotion_disc,is_online_default,
//                create_by,create_at,last_update_by,last_update_at,is_deleted
//        )
//        VALUES(${tId},'0002', '普通会员', '0', '1', '1', NULL,'1.000','0', '0','0', '1','admin', NOW(), 'admin',NOW(), '0') ;"""
//        String sql2 = """INSERT INTO vip_type ( tenant_id, type_code, type_name, preferential_policy, points_factor,
//        mem_price_used, discount_rate, to_save_points, is_package_disc, is_promotion_disc, create_by,
//                create_at, last_update_by, last_update_at, is_deleted,is_online_default,score_usage)
//        VALUES ( ${tId}, '0001', '银会员', '2', '1.00', '1', '1.000', '1', '1', '1', 'admin', NOW(), 'admin', NOW(), '0', '0','1')
//        """
//        String sql3 = """INSERT INTO vip_type ( tenant_id, type_code, type_name, preferential_policy, points_factor,
//        mem_price_used, discount_rate, to_save_points, is_package_disc, is_promotion_disc, create_by,
//                create_at, last_update_by, last_update_at, is_deleted,is_online_default,score_usage)
//        VALUES ( ${tId}, '0000', '金会员', '2', '1.00', '1', '1.000', '1', '1', '1', 'admin', NOW(), 'admin', NOW(), '0', '0','1');"""
//
//        getSession().createSQLQuery(sql1).executeUpdate()
//        getSession().createSQLQuery(sql2).executeUpdate()
//        getSession().createSQLQuery(sql3).executeUpdate()
//    }
//
//    /**
//     * 根据tenantId查询会员类型
//     * @author szq
//     */
//    public List<VipType> listVipType(BigInteger tenantId) {
//        List<VipType> vipTypes = VipType.findAllByTenantId(tenantId);
//        return vipTypes;
//    }
//
//    public List<VipType> listVipType(BigInteger tenantId, BigInteger branchId) {
//        List<VipType> vipTypes = VipType.findAllByTenantIdAndBranchId(tenantId, branchId);
//        return vipTypes;
//    }
//
//    /**
//     * 新增 会员类型
//     * @author szq
//     */
//    def addVipType(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            def list = VipType.findAllByTenantId(new BigInteger(params.tenantId));
//            if ("true".equals(params.isBranchManagementVip)) {
//                list = VipType.findAllByTenantIdAndBranchId(new BigInteger(params.tenantId), new BigInteger(params.branchId));
//            } else {
//                list = VipType.findAllByTenantId(new BigInteger(params.tenantId));
//            }
//            int typeCodeInt = -1;
//            if (null != list && list.size()>0) {
//                for (VipType vt : list) {
//                    if (Integer.parseInt(vt.getTypeCode()) > typeCodeInt) {
//                        typeCodeInt = Integer.parseInt(vt.getTypeCode());
//                    }
//                }
//            } else {
//                params.isOnlineDefault = true;
//            }
//            params.typeCode = String.format("%04d", typeCodeInt + 1);
//            if (null == params.typeName) {
//                params.typeName = "新会员等级" + params.typeCode;
//            }
//            if (null == params.preferentialPolicy) {
//                params.preferentialPolicy = 0;
//            }
//            VipType vipType = BeanUtils.copyProperties(VipType.class, params);
//            if (vipType.validate() == false) {
//                rest.isSuccess = false;
//                rest.message = "会员类型添加失败";
//                return rest;
//            }
//            getSession().save(vipType);
//            rest.data = vipType;
//            rest.isSuccess = true;
//            rest.message = "会员类型添加成功";
//        } catch (Exception e) {
//            LogUtil.logError("会员类型添加异常：" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 删除会员类型
//     * @author szq
//     */
//    def deleteVipType(BigInteger id) {
//        ApiRest rest = new ApiRest();
//        try {
//            VipType vipType = VipType.findById(id);
//            if (vipType != null) {
//                if (vipType.isOnlineDefault == true) {
//                    rest.isSuccess = false;
//                    rest.message = "该会员类型为线上默认，不能删除";
//                    return rest;
//                }
//                List<Vip> list = Vip.findAllByTypeId(id);
//                if (list.size() > 0) {
//                    rest.isSuccess = false;
//                    rest.message = "该会员类型已经被使用，不能删除";
//                    return rest;
//                }
//                vipType.isDeleted = true;
//                vipType.lastUpdateAt = new Date();
//                vipType.save flush: true;
//            } else {
//                rest.isSuccess = false;
//                rest.message = "该会员类型不存在或已被删除";
//                return rest;
//            }
//            rest.isSuccess = true;
//            rest.message = "会员类型删除成功";
//        } catch (Exception e) {
//            LogUtil.logError("会员类型删除异常：" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 修改会员类型
//     * @author szq
//     */
//    def editVipTypes(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            List<VipType> vipTypes = GsonUntil.jsonAsList(URLDecoder.decode(params.datas,"UTF-8"),VipType.class);
//            List<VipType> types = new ArrayList<VipType>();
//            if (null != vipTypes && vipTypes.size() > 0) {
//                for (VipType vt : vipTypes) {
//                    def vipTypeList = null;
//                    if ("true".equals(params.get("isBranchManagementVip"))) {
//                        vipTypeList = VipType.findAllByTenantIdAndTypeNameAndIsDeletedAndBranchId(new BigInteger((String)params.get("tenantId")), vt.getTypeName(), false, new BigInteger((String)params.get("branchId")));
//                    } else {
//                        vipTypeList = VipType.findAllByTenantIdAndTypeNameAndIsDeleted(new BigInteger((String)params.get("tenantId")), vt.getTypeName(), false);
//                    }
//                    if (vipTypeList) {
//                        for (def i = 0; i < vipTypeList.size(); i++) {
//                            if (vipTypeList.get(i).id != vt.getId()) {
//                                rest.isSuccess = false;
//                                rest.message = "会员类型名称已经存在";
//                                return rest;
//                            }
//                        }
//                    }
//                    VipType primaryVt = VipType.findById(vt.getId());
//                    if (primaryVt) {
//                        primaryVt.setTypeName(vt.getTypeName());
//                        primaryVt.setPreferentialPolicy(vt.getPreferentialPolicy());
//                        if (vt.getIsOnlineDefault() != null) {
//                            primaryVt.setIsOnlineDefault(vt.getIsOnlineDefault());
//                        }
//                        primaryVt.setPointsFactor(new BigDecimal(params.pointsFactor));
//                        primaryVt.setScoreUsage(Integer.parseInt(params.scoreUsage));
//                        primaryVt.setMemPriceUsed(vt.getMemPriceUsed());
//                        primaryVt.setDiscountRate(vt.getDiscountRate());
//                        if (vt.getToSavePoints() != null && vt.getToSavePoints() != "") {
//                            primaryVt.setToSavePoints(BigInteger.valueOf(vt.getToSavePoints()));
//                        }
//                        if (primaryVt.getBranchId() == null) {
//                            primaryVt.setBranchId(new BigInteger((String)params.get("branchId")));
//                        }
//                        primaryVt.save flush: true;
//                        types.add(primaryVt);
//                    }
//                }
//            }
//            rest.data = types;
//            rest.isSuccess = true;
//            rest.message = "会员类型修改成功";
//            String datasStr = params.datas;
//            if (datasStr.length() > 10 && vipTypes.size() == 0) {
//                rest.data = null;
//                rest.isSuccess = false;
//                rest.message = "Gson解析List对象失败";
//            }
//        } catch (Exception e) {
//            LogUtil.logError("会员类型修改异常：" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }

    /**
     * @param params
     * @return
     */
    public VipType findVipTypeByPhone(Map params){
        VipType vipType = null;
        String phone = params.get("phone").toString();
        String tenantId = params.get("tenantId").toString();
        Vip vip = null;
        if(phone != null){
            Map<String, Object> map = new HashMap<>();
            map.put("phone", phone);
            map.put("tenantId", tenantId);
            vip = vipMapper.findByCondition(map);
        }
        if(vip != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", vip.getTypeId());
            vipType = vipTypeMapper.findByCondition(map);
        }
        return vipType;
    }

    public ApiRest setOrUpdateBranchDiscount(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigDecimal discountRate = BigDecimal.valueOf(Double.parseDouble(params.get("discount").toString()));
        BigInteger empId = BigInteger.valueOf(Long.parseLong(params.get("empId").toString()));
        BigInteger typeId = BigInteger.valueOf(Long.parseLong(params.get("typeId").toString()));

        if(params.get("bdId") != null && !"".equals(params.get("bdId"))){
            BigInteger id = BigInteger.valueOf(Long.parseLong(params.get("bdId").toString()));
            BranchDiscount branchDiscount = vipTypeMapper.findBranchDiscount(tenantId,branchId,id);
            if(branchDiscount == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("为查询到该条折扣信息，修改失败");
                return apiRest;
            }
            branchDiscount.setDiscountRate(discountRate);
            branchDiscount.setEmpId(empId);
            branchDiscount.setLastUpdateAt(new Date());
            int i = vipTypeMapper.updateBranchDiscount(branchDiscount);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改折扣信息失败");
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改折扣信息成功");
        }else{
            BranchDiscount branchDiscount = new BranchDiscount();
            branchDiscount.setTenantId(tenantId);
            branchDiscount.setBranchId(branchId);
            branchDiscount.setTypeId(typeId);
            branchDiscount.setDiscountRate(discountRate);
            branchDiscount.setEmpId(empId);
            branchDiscount.setLastUpdateAt(new Date());
            branchDiscount.setLastUpdateBy("System");
            branchDiscount.setCreateAt(new Date());
            branchDiscount.setCreateBy("System");
            int i = vipTypeMapper.setBranchDiscount(branchDiscount);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("保存折扣信息失败");
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("保存折扣信息成功");
        }
        VipType vipType = vipTypeMapper.findTypeById(tenantId, typeId);
        vipType.setLastUpdateAt(new Date());
        int i = vipTypeMapper.update(vipType);
        if(i <= 0){
            apiRest.setIsSuccess(false);
        }
        return apiRest;
    }

    public ApiRest queryDiscountById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        BigInteger typeId = BigInteger.valueOf(Long.parseLong(params.get("typeId").toString()));
        BranchDiscount branchDiscount = vipTypeMapper.findBranchDiscountByTypeId(tenantId,branchId,typeId);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询折扣信息成功");
        apiRest.setData(branchDiscount);
        return apiRest;
    }
}
