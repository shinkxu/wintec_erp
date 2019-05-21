package erp.chain.service.base;

import erp.chain.domain.Supplier;
import erp.chain.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * 供应商
 * Created by lipeng on 2017/8/4.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class SupplierService{

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * 查询供应商列表
     */
    public ApiRest querySupplierList(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        if(params.get("rows") != null && !"".equals(params.get("rows")) && params.get("page") != null && !"".equals(params.get("page"))){
            Integer rows = Integer.parseInt(params.get("rows").toString());
            Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
            if("1".equals(params.get("page"))){
                if(params.get("supplierCodeOrName") == null || "".equals(params.get("supplierCodeOrName")) || "自".equals(params.get("supplierCodeOrName")) || "购".equals(params.get("supplierCodeOrName")) || "自购".equals(params.get("supplierCodeOrName")) || "000000".indexOf(params.get("supplierCodeOrName").toString()) != -1){
                    rows = rows - 1;
                }
            }
            else{
                if(params.get("supplierCodeOrName") == null || "".equals(params.get("supplierCodeOrName")) || "自".equals(params.get("supplierCodeOrName")) || "购".equals(params.get("supplierCodeOrName")) || "自购".equals(params.get("supplierCodeOrName")) || "000000".indexOf(params.get("supplierCodeOrName").toString()) != -1){
                    offset = offset - 1;
                }
            }
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
        List<Map> list = supplierMapper.querySupplierList(params);
        Long count = supplierMapper.querySupplierListSum(params);
        //加入自购选项
        List<Map> list1 = new ArrayList<>();
        Map map1 = new HashMap();
        if("1".equals(params.get("page"))){
            if(params.get("supplierStatus") == null || "".equals(params.get("supplierStatus"))){
                if(params.get("supplierCodeOrName") == null || "".equals(params.get("supplierCodeOrName"))){
                    map1.put("id", "0");
                    map1.put("supplierCode", "000000");
                    map1.put("supplierName", "自购");
                    map1.put("status", 1);
                    map1.put("memo", "必备选项，不可修改或删除");
                    list1.add(map1);
                    map.put("total", count + 1);
                }
                else{
                    if("自".equals(params.get("supplierCodeOrName")) || "购".equals(params.get("supplierCodeOrName")) || "自购".equals(params.get("supplierCodeOrName")) || "000000".indexOf(params.get("supplierCodeOrName").toString()) != -1){
                        map1.put("id", "0");
                        map1.put("supplierCode", "000000");
                        map1.put("supplierName", "自购");
                        map1.put("status", 1);
                        map1.put("memo", "必备选项，不可修改或删除");
                        list1.add(map1);
                        map.put("total", count + 1);
                    }
                    else{
                        map.put("total", count);
                    }
                }
            }
            else{
                if("1".equals(params.get("supplierStatus"))){
                    if(params.get("supplierCodeOrName") == null || "".equals(params.get("supplierCodeOrName")) || "自".equals(params.get("supplierCodeOrName")) || "购".equals(params.get("supplierCodeOrName")) || "自购".equals(params.get("supplierCodeOrName")) || "000000".indexOf(params.get("supplierCodeOrName").toString()) != -1){
                        map1.put("id", "0");
                        map1.put("supplierCode", "000000");
                        map1.put("supplierName", "自购");
                        map1.put("status", 1);
                        map1.put("memo", "必备选项，不可修改或删除");
                        list1.add(map1);
                        map.put("total", count + 1);
                    }
                    else{
                        map.put("total", count);
                    }
                }
                else{
                    map.put("total", count);
                }
            }
        }
        else{
            if(params.get("supplierCodeOrName") == null || "".equals(params.get("supplierCodeOrName")) || "自".equals(params.get("supplierCodeOrName")) || "购".equals(params.get("supplierCodeOrName")) || "自购".equals(params.get("supplierCodeOrName")) || "000000".indexOf(params.get("supplierCodeOrName").toString()) != -1){
                map.put("total", count + 1);
            }
            else{
                map.put("total", count);
            }
        }
        list1.addAll(list);
        map.put("rows", list1);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询供应商列表成功！");
        return apiRest;
    }

    /**
     * 新增或修改供应商信息
     */
    public ApiRest addOrUpdateSupplier(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong(params.get("tenantId").toString()));
        BigInteger branchId = BigInteger.valueOf(Long.parseLong(params.get("branchId").toString()));
        String supplierCode;
        if(params.get("supplierCode") == null || "".equals(params.get("supplierCode"))){
            ApiRest apiRest1 = getSupplierCode(params);
            supplierCode = apiRest1.getData().toString();
        }
        else{
            supplierCode = params.get("supplierCode").toString();
        }
        String supplierName = params.get("supplierName").toString();
        if("自购".equals(supplierName)){
            apiRest.setIsSuccess(false);
            apiRest.setError("供应商名称不能为自购");
            return apiRest;
        }
        if(params.get("supplierName").toString().length() > 50){
            apiRest.setIsSuccess(false);
            apiRest.setError("供应商名称不能超过50个字符");
            return apiRest;
        }
        String mnemonic = params.get("mnemonic") == null ? "" : params.get("mnemonic").toString();
        String contacts = params.get("contacts") == null ? "" : params.get("contacts").toString();
        String contactsNumber = params.get("contactsNumber") == null ? "" : params.get("contactsNumber").toString();
        String address = params.get("address") == null ? "" : params.get("address").toString();
        String memo = params.get("memo") == null ? "" : params.get("memo").toString();
        Integer status = 1;
        if(params.get("status") != null && !"".equals(params.get("status"))){
            status = Integer.parseInt(params.get("status").toString());
        }
        if(params.get("id") != null && !"".equals(params.get("id"))){
            Supplier supplier = supplierMapper.findSupplierByIdAndTenantId(params);
            if(supplier == null){
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到需要修改的供应商");
                return apiRest;
            }
            supplier.setSupplierCode(supplierCode);
            supplier.setSupplierName(supplierName);
            supplier.setMnemonic(mnemonic);
            supplier.setContacts(contacts);
            supplier.setContactsNumber(contactsNumber);
            supplier.setAddress(address);
            supplier.setMemo(memo);
            supplier.setStatus(status);
            supplier.setLastUpdateBy("System");
            supplier.setLastUpdateAt(new Date());
            int count = supplierMapper.update(supplier);
            if(count <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("修改供应商失败");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改供应商成功");
        }
        else{
            Supplier supplier = new Supplier();
            supplier.setTenantId(tenantId);
            supplier.setBranchId(branchId);
            supplier.setSupplierCode(supplierCode);
            supplier.setSupplierName(supplierName);
            supplier.setMnemonic(mnemonic);
            supplier.setContacts(contacts);
            supplier.setContactsNumber(contactsNumber);
            supplier.setAddress(address);
            supplier.setMemo(memo);
            supplier.setStatus(status);
            supplier.setCreateBy("System");
            supplier.setCreateAt(new Date());
            supplier.setLastUpdateBy("System");
            supplier.setLastUpdateAt(new Date());
            Map map = new HashMap();
            map.put("tenantId", tenantId);
            map.put("supplierCode", supplierCode);
            Long usedCode = supplierMapper.isUsedCode(map);
            if(usedCode > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("供应商编码已被使用，新增失败");
                return apiRest;
            }
            int count = supplierMapper.insert(supplier);
            if(count <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("新增供应商失败");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setData(supplier);
            apiRest.setMessage("新增供应商成功");
        }
        return apiRest;
    }

    /**
     * 根据ID查询供应商信息
     */
    public ApiRest querySupplierById(Map params){
        ApiRest apiRest = new ApiRest();
        Supplier supplier = supplierMapper.findSupplierByIdAndTenantId(params);
        if(supplier == null){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询供应商信息失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(supplier);
        apiRest.setMessage("查询供应商信息成功");
        return apiRest;
    }

    /**
     * 删除供应商
     */
    public ApiRest delSupplier(Map params){
        ApiRest apiRest = new ApiRest();
        String ids = params.get("ids").toString();
        params.put("ids", ids);
        //是否有商品使用该供应商
        Long gCount = supplierMapper.supplierToGoods(params);
        if(gCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("有商品为该供应商提供，不能删除");
            return apiRest;
        }
        //是否有未审核采购单使用该供货商
        Long oCount = supplierMapper.supplierToStoreOrder(params);
        if(oCount > 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("有采购单使用该供应商，不能删除");
            return apiRest;
        }
        int count = supplierMapper.delSupplier(params);
        if(count == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("删除供应商失败");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("删除供应商成功");
        return apiRest;
    }

    /**
     * 获取编码
     */
    public ApiRest getSupplierCode(Map params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId").toString();
        params.put("tenantId", tenantId);
        String code = supplierMapper.queryMaxCode(params);
        StringBuffer supplierCode = new StringBuffer();
        if(code == null || code == ""){
            supplierCode.append("000001");
        }
        else{
            supplierCode.append(String.valueOf(BigInteger.valueOf(Long.parseLong(code) + 1)));
        }
        int length = supplierCode.length();
        String zero = "";
        for(int i = 0; i < (6 - length); i++){
            zero += "0";
        }
        code = zero + supplierCode;
        apiRest.setIsSuccess(true);
        apiRest.setMessage("获取编码成功");
        apiRest.setData(code);
        return apiRest;
    }
}
