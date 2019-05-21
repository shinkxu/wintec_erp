package erp.chain.service.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.o2o.Vip;
import erp.chain.domain.o2o.VipAddress;
import erp.chain.mapper.o2o.VipAddressMapper;
import erp.chain.mapper.o2o.VipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangms on 2017/1/20.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipAddressService{

    @Autowired
    private VipAddressMapper vipAddressMapper;
    @Autowired
    private VipMapper vipMapper;

    /**
     * 查询会员收货地址列表
     *
     * @param params
     * @return
     * @throws com.saas.common.exception.ServiceException
     */
    public ResultJSON qryVipAddressList(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> queryParams = new HashMap<>();
        if(params.get("page") != null && params.get("rows") != null){
            queryParams.put("rows", params.get("rows"));
            queryParams.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("rows").toString()));
        }
        queryParams.put("vipId", params.get("vipId"));
        queryParams.put("queryStr", params.get("queryStr"));

        List list = vipAddressMapper.vipAddressList(queryParams);
        int count = list.size();
        map.put("total", count > 0 ? count : 0);
        map.put("rows", list);
        result.setJsonMap(map);
        return result;
    }

    /**
     * 无分页查询收货地址
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipAddressListNoPage(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("vipId", params.get("vipId"));
        List list = vipAddressMapper.vipAddressList(queryParams);
        result.setSuccess("0");
        result.setObject(list);
        return result;
    }

    /**
     * 保存或者新增收货地址
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveVipAddr(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
        Map<String, Object> map = new HashMap<>();
        if(params.get("vipId") != null){
            map.put("vipId", new BigInteger(params.get("vipId").toString()));
            Map par = new HashMap();
            par.put("id", params.get("vipId").toString());
            Vip vip = vipMapper.findById(par);
            if(vip != null){
                map.put("tenantId", vip.getTenantId());
            }
        }
        if(params.get("consignee") != null){
            map.put("consignee", params.get("consignee"));
        }
        if(params.get("area") != null){
            map.put("area", params.get("area"));
        }
        if(params.get("address") != null){
            map.put("address", params.get("address"));
        }
        if(params.get("mobilePhone") != null){
            map.put("mobilePhone", params.get("mobilePhone"));
        }
        if(params.get("telPhone") != null){
            map.put("telPhone", params.get("telPhone"));
        }
        if(params.get("isDefault") != null){
            if(params.get("isDefault").toString().equals("true") || params.get("isDefault").toString().equals("1")){
                map.put("isDefault", 1);
            }
            else if(params.get("isDefault").toString().equals("false") || params.get("isDefault").toString().equals("0")){
                map.put("isDefault", 0);
            }
        }
        if(params.get("createAt") != null){
            map.put("createAt", params.get("createAt"));
        }
        if(params.get("areaName") != null){
            map.put("areaName", params.get("areaName"));
        }
        map.put("lastUpdateAt", new Date());
        if(params.get("id") != null){
            map.put("id", new BigInteger(params.get("id").toString()));
            int flag = vipAddressMapper.update(map);
            if(flag == 1){
                result.setSuccess("0");
                result.setMsg("地址更新成功");
            }
        }
        else{
            map.put("createAt", new Date());
            map.put("isDeleted", false);
            int flag = vipAddressMapper.insert(map);
            if(flag == 1){
                result.setSuccess("0");
                result.setMsg("地址保存成功");
            }
        }
        return result;
    }

    /**
     * 根据ID获取收货地址
     *
     * @param
     * @return
     * @throws ServiceException
     */
    public ResultJSON qryVipAddrById(BigInteger id) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(id != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            VipAddress vipAdds = vipAddressMapper.findByCondition(map);
            if(vipAdds != null){
                result.setSuccess("0");
                result.setObject(vipAdds);
            }
            else{
                result.setSuccess("1");
                result.setMsg("查询收货地址失败!");
            }
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效的id");
        }
        return result;
    }

    /**
     * 删除会员收货信息
     *
     * @param ids
     * @return
     * @throws ServiceException
     */
    public ResultJSON delVipAdds(String ids) throws ServiceException{
        ResultJSON result = new ResultJSON();
        if(ids != null && !"".equals(ids)){
            for(String id : ids.split(",")){
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("isDeleted", 1);
                int flag = vipAddressMapper.update(map);
            }
            result.setSuccess("0");
            result.setMsg("收货地址删除成功");
        }
        else{
            result.setSuccess("1");
            result.setMsg("无效的数据");
        }
        return result;
    }

    /**
     * 设置会员默认收货地址
     *
     * @param params
     * @return
     * @throws ServiceException
     */
    public ResultJSON saveDefaultAddr(Map params) throws ServiceException{
        ResultJSON result = new ResultJSON();
//        try {
//            if(params.get("id") != null){
//                String update = "update VipAddress set isDefault = '0' where vipId="+params.vipId
//                VipAddress.executeUpdate(update);
//                String update_default = "update VipAddress set isDefault = '1' where id="+params.id
//                VipAddress.executeUpdate(update_default);
//                result.success = "0";
//                result.msg = "默认收货地址设置成功";
//            }else{
//                throw new Exception("无效的数据")
//            }
//        } catch (Exception e) {
//            ServiceException se = new ServiceException("1004", "编辑失败", e.message)
//            throw se
//        }
        return result;
    }
//
//    /**
//     * 根据会员id分页查询会员收货地址
//     * @author szq
//     */
//    public Map<String,Object> listVipAddress(Map params) {
//        Map<String,Object> map = new HashMap<String,Object>();
//        try {
//            StringBuffer query = new StringBuffer("from VipAddress t where 1=1 and t.vipId =:vipId");
//            StringBuffer queryCount = new StringBuffer("select count(t) from VipAddress t where 1=1 and vipId =:vipId");
//            def queryParams = new HashMap();
//            queryParams.max = params.rows;
//            queryParams.offset = (Integer.parseInt(params.page) - 1) * Integer.parseInt(params.rows);
//            def namedParams = new HashMap();
//            namedParams.vipId = new BigInteger(params.vipId);
//            params.each { k, v ->
//                if ('queryStr'.equals(k) && v) {
//                    query.append(" AND (t.consignee like :queryStr or t.mobilePhone like :queryStr)");
//                    queryCount.append(" AND (t.consignee like :queryStr or t.mobilePhone like :queryStr)");
//                    namedParams.queryStr = "%$v%";
//                }
//            }
//            List<VipAddress> vipAddresses = VipAddress.executeQuery(query.toString(),namedParams,queryParams);
//            def count = VipAddress.executeQuery(queryCount.toString(), namedParams);
//            map.put("total",count.size() > 0 ? count[0] : 0);
//            map.put("rows",vipAddresses);
//        } catch (Exception e) {
//            LogUtil.logError("查询会员收货地址异常" + e.message);
//            throw new ServiceException();
//        }
//        return map;
//    }
//
//    /**
//     * 新增收货地址
//     * @author szq
//     */
//    def addVipAddress(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            params.isDefault = params.isDefault=="on";
//            if (params.isDefault) {
//                String update = "update VipAddress set isDefault = '0' where vipId=" + params.vipId;
//                VipAddress.executeUpdate(update);
//            }
//            VipAddress vipAdds = BeanUtils.copyProperties(VipAddress.class,params);
//            if (vipAdds.hasErrors()) {
//                rest.message = "新增收货地址失败";
//                rest.isSuccess = false;
//                return rest;
//            }
//            VipAddress.saveAll(vipAdds);
//            rest.data = vipAdds;
//            rest.message = "新增收货地址成功";
//            rest.isSuccess = true;
//        } catch (Exception e) {
//            LogUtil.logError("新增收货地址异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 删除收货地址 不支持批量删除
//     * @author szq
//     */
//    def deleteVipAddress(BigInteger id){
//        ApiRest rest = new ApiRest();
//        try {
//            VipAddress vipAddress = VipAddress.findById(id);
//            if (vipAddress) {
//                vipAddress.isDeleted = "1";
//                vipAddress.save flush: true;
//            } else {
//                rest.message = "收货地址不存在或已被删除";
//                rest.isSuccess = false;
//                return rest;
//            }
//            rest.message = "收货地址删除成功";
//            rest.isSuccess = true;
//        } catch (Exception e) {
//            LogUtil.logError("收货地址删除异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
//
//    /**
//     * 根据收货地址id查询会员收货地址
//     * @author szq
//     */
//    public VipAddress findVipAddressById(BigInteger id) {
//        VipAddress vipAddress = VipAddress.findById(id);
//        return vipAddress;
//    }
//
//    /**
//     * 修改收货地址
//     * @author szq
//     */
//    def editVipAddress(Map params) {
//        ApiRest rest = new ApiRest();
//        try {
//            params.isDefault = params.isDefault=="on";
//            if (params.isDefault) {
//                String update = "update VipAddress set isDefault = '0' where vipId=" + params.vipId;
//                VipAddress.executeUpdate(update);
//            }
//            VipAddress vipAdds = VipAddress.findById(new BigInteger(params.id));
//            BeanUtils.copyProperties(vipAdds,params);
//            if (vipAdds.hasErrors()) {
//                rest.message = "修改收货地址失败";
//                rest.isSuccess = false;
//                return rest;
//            }
//            VipAddress.saveAll(vipAdds);
//            rest.data = vipAdds;
//            rest.message = "修改收货地址成功";
//            rest.isSuccess = true;
//        } catch (Exception e) {
//            LogUtil.logError("修改收货地址异常" + e.message);
//            throw new ServiceException();
//        }
//        return rest;
//    }
}
