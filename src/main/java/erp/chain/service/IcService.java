package erp.chain.service;

import com.saas.common.Constants;
import com.saas.common.util.CacheUtils;
import com.saas.common.util.LogUtil;
import com.saas.common.util.MD5Utils;
import erp.chain.domain.*;
import erp.chain.domain.o2o.Vip;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.CardMapper;
import erp.chain.mapper.CardRecordMapper;
import erp.chain.mapper.EmployeeMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.utils.AESUtils;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.RSAUtils;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.SmsApi;
import saas.api.common.ApiRest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * Created by xumx on 2016/11/15.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class IcService extends BaseService{

    @Autowired
    private BranchMapper branchMapper;

    private static Map<String, Map<String, Object>> keyMap = new HashMap<>();

    protected static void putKeys(String posId, Map<String, Object> map) throws IOException, ClassNotFoundException{
        CacheUtils.setObject(MD5Utils.stringMD5("pos_" + posId), map, -1);
        //keyMap.put("pos_" + posId, map);
    }

    protected static Map<String, Object> getKeys(String posId) throws IOException, ClassNotFoundException{
        Object result = CacheUtils.getObject(MD5Utils.stringMD5("pos_" + posId));
        return (Map<String, Object>)result;
    }

    @Autowired
    CardMapper cardMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    VipMapper vipMapper;
    @Autowired
    CardRecordMapper cardRecordMapper;

    public Card findCard(Map<String, Object> params){
        List<Card> list = cardMapper.select(params);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public Card getCardByCode(Map<String, Object> params){
        List<Card> list = cardMapper.findCardByCode(params);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public Card findCardByGroup(Map<String, Object> params){
        //查询会员分组
        if(params.get("branchId") != null && params.get("tenantId") != null){
            Map<String, Object> paramZ = new HashMap<>();
            paramZ.put("id", params.get("branchId"));
            paramZ.put("tenantId", params.get("tenantId"));
            Branch branch = branchMapper.find(paramZ);
            if(branch != null && branch.getGroupCode() != null && !"".equals(branch.getGroupCode())){
                paramZ.remove("id");
                paramZ.put("groupCode", branch.getGroupCode());
                List<Branch> branches = branchMapper.findBranchByTenantId(paramZ);
                if(branches != null && branches.size() > 0){
                    String bIds = "";
                    for(Branch b : branches){
                        bIds += b.getId() + ",";
                    }
                    bIds = bIds.substring(0, bIds.length() - 1);
                    params.remove("branchId");
                    params.put("groups", bIds);
                }
            }
        }
        List<Card> list = cardMapper.select1(params);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public Employee findEmployee(Map<String, Object> params){
        List<Employee> list = employeeMapper.select(params);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public Vip findVip(Map<String, Object> params){
        List<Vip> list = vipMapper.select(params);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * RSA加密
     *
     * @param posId
     * @return
     */
    public Map<String, String> rsa(String posId, String tenantCode){
        Map<String, String> r = new HashMap<>();
        try{
            Map<String, Object> keyMap = getKeys(tenantCode + "_" + posId);
            //LogUtil.logInfo("RSA加密"+BeanUtils.toJsonStr(keyMap));
            if(keyMap == null){
                keyMap = RSAUtils.generate();
                putKeys(tenantCode + "_" + posId, keyMap);
            }
            RSAPublicKey publicKey = (RSAPublicKey)keyMap.get("public");
            r.put("modulus", publicKey.getModulus().toString());
            r.put("exponent", publicKey.getPublicExponent().toString());
            r.put("encode", Base64.encodeBase64String(publicKey.getEncoded()));
            r.put("Result", Constants.REST_RESULT_SUCCESS);
        }
        catch(Exception e){
            r.put("Result", Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IcService.rsa({}) - {} - {}", posId, e.getClass().getSimpleName(), e.getMessage());
        }
        LogUtil.logInfo("RSA加密" + BeanUtils.toJsonStr(r));
        return r;
    }

    /**
     * AES加密
     *
     * @param posId
     * @param data
     * @return
     */
    public Map<String, String> aes(String posId, String posCode, String tenantCode, String data){
        Map<String, String> r = new HashMap<>();
        try{
            Map<String, Object> keyMap = getKeys(tenantCode + "_" + posId);
            LogUtil.logInfo("传入参数posId="+posId+",posCode="+posCode+",tenantCode="+tenantCode+",data="+data);
            if(keyMap != null){
                RSAPrivateKey privateKey = (RSAPrivateKey)keyMap.get("private");
                if(privateKey == null){
                    r.put("Result", Constants.REST_RESULT_FAILURE);
                    r.put("Message", "安全信道未建立");
                    r.put("Code", "420201");
                    log.info("IcService.aes({}, {}) - 安全信道未建立", posId, data);
                    return r;
                }
                try{
                    String d = RSAUtils.decrypt(data, privateKey);
                    JSONObject json = JSONObject.fromObject(d);
                    LogUtil.logInfo("解密后的JSON="+json);
                    if(tenantCode.equals(json.get("tenantCode")) && posCode.equals(json.get("posCode"))){
                        SecretKey aes = (SecretKey)keyMap.get("aes");
                        if(aes == null){
                            aes = AESUtils.generate();
                            keyMap.put("aes", aes);
                            putKeys(tenantCode + "_" + posId, keyMap);
                        }
                        String aesStr = new Base64().encodeToString(aes.getEncoded());
                        String rsaStr = RSAUtils.encrypt(aesStr, privateKey);
                        r.put("Result", Constants.REST_RESULT_SUCCESS);
                        r.put("Data", rsaStr);
                    }
                    else{
                        r.put("Result", Constants.REST_RESULT_FAILURE);
                        r.put("Message", "验证失败");
                        r.put("Code", "420202");
                        log.info("IcService.aes({} ,{}) - 验证失败", posId, data);
                    }
                }
                catch(Exception e1){
                    r.put("Result", Constants.REST_RESULT_FAILURE);
                    r.put("Message", "解密失败");
                    r.put("Code", "420203");

                }
            }
            else{
                r.put("Result", Constants.REST_RESULT_FAILURE);
                r.put("Message", "密钥未初始化");
                r.put("Code", "420204");
                log.info("IcService.aes({} ,{}) - 密钥未初始化", posId, data);
            }
        }
        catch(Exception e){
            r.put("Result", Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IcService.aes0({} ,{}) - {} - {}", posId, data, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }

    protected JSONObject schannel(String tenantCode, String posId, String data, Map<String, String> r) throws NoSuchPaddingException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException{
        LogUtil.logInfo(tenantCode + "----" + posId + "-----" + data + "----" + BeanUtils.toJsonStr(r));
        if(StringUtils.isEmpty(tenantCode) || StringUtils.isEmpty(posId) || StringUtils.isEmpty(data)){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Message", "参数无效");
            r.put("Code", "420423");
            return null;
        }
        Map<String, Object> keyMap = getKeys(tenantCode + "_" + posId);
        //LogUtil.logInfo("获取值：getKeys(tenantCode_posId)"+BeanUtils.toJsonStr(keyMap));
        if(keyMap == null || keyMap.get("aes") == null){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Message", "安全信道未建立");
            r.put("Code", "420424");
            return null;
        }
        SecretKey aes = (SecretKey)keyMap.get("aes");
        //解密
        String jsonStr = AESUtils.decrypt(data, aes);
        return JSONObject.fromObject(jsonStr);
    }

    /**
     * ID卡有效性验证
     *
     * @param posId
     * @param data
     * @param type
     * @return
     */
    public Map<String, String> verifyCard(String posId, String data, Integer type, String tenantId, String tenantCode, String branchId, String branchCode, String posCode) throws IOException{
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            Object cardId = json.get("cardId");
            String cardCode = cardId.toString().replace(" ", "");
            if(type.equals(erp.chain.common.Constants.CARD_TYPE_AUTH)){/** 验证授权卡 */
                //判断卡号是否为空
                if(!(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                    //获取授权卡信息
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("code", cardCode);
                    paramsMap.put("tenantId", tenantId);
                    Card card = findCard(paramsMap);
                    //判断授权卡是否有效
                    if(card != null && card.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH) && card.getTenantCode().equals(tenantCode) && card.getTenantId().equals(new BigInteger(tenantId)) && card.getBranchCode().equals(branchCode)){
                        r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                        r.put("Message", "授权卡有效");
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "授权卡信息不匹配");
                        r.put("Code", "420413");
                        log.info("IcService.verifyCard({}, {}, {}) - {} - 授权卡信息不匹配", posId, data, type, cardCode);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420402");
                    log.info("IcService.verifyCard({}, {}, {}) - ${} - 卡号无效", posId, data, type, cardCode);
                }
            }
            else if(type.equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)){/** 验证会员卡 */
                if(!(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("code", cardCode);
                    paramsMap.put("tenantId", tenantId);
                    paramsMap.put("branchId", branchId);
                    Card card = findCardByGroup(paramsMap);
                    if(card != null && card.getType().equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP) && card.getTenantCode().equals(tenantCode) && card.getTenantId().equals(new BigInteger(tenantId))){
                        r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                        r.put("Vip", card.getHolderId().toString());
                        log.info("IcService.verifyCard({}, {}, {}) - {} - 验证通过 - {}", posId, data, type, cardCode, card.getHolderId());
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "用户信息不匹配");
                        if(card == null){
                            r.put("Error", "无效卡");
                            r.put("Code", "420420");
                            log.info("IcService.verifyCard({}, {}, {}) - {} - 无效卡", posId, data, type, cardCode);
                        }
                        else if(!card.getType().equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)){
                            r.put("Error", "卡类型不符 - ${card.getType()}");
                            r.put("Code", "420421");
                            log.info("IcService.verifyCard({}, {}, {}) - {} - 卡类型不符 - {}", posId, data, type, cardCode, card.getType());
                        }
                        else{
                            r.put("Error", "非当前商户的用户卡");
                            r.put("Code", "420422");
                            log.info("IcService.verifyCard({}, {}, {}) - {} - 非当前商户的用户卡", posId, data, type, cardCode);
                        }
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420402");
                    log.info("IcService.verifyCard({}, {}, {}) - {} - 卡号无效", posId, data, type, cardCode);
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "卡信息无效");
                r.put("Code", "420418");
                log.info("IcService.verifyCard({}, {}, {}) - {} - 卡信息无效", posId, data, type, cardCode);
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IcService.verifyCard({}, {}, {}) - {} - {}", posId, data, type, e.getClass().getSimpleName(), e.getMessage());
        }
        log.info("IcService.verifyCard({}, {}, {}) - {}", posId, data, type, new ObjectMapper().writeValueAsString(r));
        return r;
    }

    public Map<String, String> initCard(Pos pos, String data, String acn) throws NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ClassNotFoundException{
        Map<String, String> resultMap = new HashMap<>();

        //初始化母卡
        if("initMasterCard".equals(acn)){

            JSONObject json = schannel(pos.getTenantCode(), pos.getId().toString(), data, resultMap);
            LogUtil.logInfo("解析结果" + json);
            if(json == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "解析失败");
                return resultMap;
            }
            String authCode = (String)json.get("authCode");
            if(StringUtils.isEmpty(authCode)){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无验证码");
                return resultMap;
            }
            //获取商户信息
            ApiRest rest = SaaSApi.findTenantById(pos.getTenantId());
            if(rest == null || !rest.getIsSuccess()){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", rest != null ? rest.getMessage() : null);
                resultMap.put("Error", rest != null ? rest.getError() : null);
                log.warn("IcService.initCard({}, {}, {}) - 查询商户信息异常 - {} - {}", pos.getId(), acn, data, resultMap.get("Message"), resultMap.get("Error"));
                return resultMap;
            }
            Map tenant = (Map)((Map)rest.getData()).get("tenant");
            //判断商户是否绑定手机号
            if(StringUtils.isEmpty((String)tenant.get("phoneNumber"))){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "商户未绑定手机号");
                log.info("IcService.initCard({},{},{}) - 商户未绑定手机号", pos.getId(), acn, data);
                return resultMap;
            }
            //校验手机验证码
            rest = SmsApi.SmsVerifyAuthCode((String)tenant.get("phoneNumber"), authCode, MD5Utils.stringMD5((String)tenant.get("phoneNumber")));
            if(rest == null || !rest.getIsSuccess()){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", rest != null ? rest.getMessage() : null);
                resultMap.put("Error", rest != null ? rest.getError() : null);
                log.warn("IcService.initCard({},{},{}) - 验证验证码失败 - {} - {}", pos.getId(), acn, data, resultMap.get("Message"), resultMap.get("Error"));
                return resultMap;
            }
            return initCard(pos.getId().toString(), data, erp.chain.common.Constants.CARD_TYPE_MASTER, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode(), null);

        }
        else if("initMasterCardNoAuthCode".equals(acn)){

            JSONObject json = schannel(pos.getTenantCode(), pos.getId().toString(), data, resultMap);
            LogUtil.logInfo("解析结果" + json);
            if(json == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "解析失败");
                return resultMap;
            }
            //获取商户信息
            /*ApiRest rest = SaaSApi.findTenantById(pos.getTenantId());
            if (rest == null || !rest.getIsSuccess()) {
                resultMap.put("Result",Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", rest != null ? rest.getMessage() : null);
                resultMap.put("Error", rest != null ? rest.getError() : null);
                log.warn("IcService.initCard({}, {}, {}) - 查询商户信息异常 - {} - {}", pos.getId(), acn, data, resultMap.get("Message"), resultMap.get("Error"));
                return resultMap;
            }*/
            /*Map tenant = (Map)((Map)rest.getData()).get("tenant");*/
            //判断商户是否绑定手机号
            /*if (StringUtils.isEmpty((String)tenant.get("phoneNumber"))) {
                resultMap.put("Result",Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "商户未绑定手机号");
                log.info("IcService.initCard({},{},{}) - 商户未绑定手机号", pos.getId(), acn, data);
                return resultMap;
            }*/
            //校验手机验证码
            return initCard(pos.getId().toString(), data, erp.chain.common.Constants.CARD_TYPE_MASTER, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode(), null);

        }
        else if("initAuthCard".equals(acn)){
            JSONObject json = schannel(pos.getTenantCode(), pos.getId().toString(), data, resultMap);
            if(json == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "解析失败");
                return resultMap;
            }
            String employeeCode = (String)json.get("employeeCode");
            if(StringUtils.isEmpty(employeeCode)){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无员工号");
                return resultMap;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("tenantId", pos.getTenantId());
            params.put("branchId", pos.getBranchId());
            params.put("code", employeeCode);
            Employee employee = findEmployee(params);
            if(employee == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无效的员工");
                return resultMap;
            }
            else if(employee.getCardId() != null || StringUtils.isNotEmpty(employee.getCardCode())){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "员工已绑定其他卡");
                return resultMap;
            }
            else{
                resultMap = initCard(pos.getId().toString(), data, erp.chain.common.Constants.CARD_TYPE_AUTH, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode(), employee.getId().toString());
                if(Constants.REST_RESULT_SUCCESS.equals(resultMap.get("Result"))){
                    employee.setCardId(new BigInteger(resultMap.get("cardId")));
                    employee.setCardCode(resultMap.get("cardCode"));
                    employeeMapper.updateEmployee(employee);
                }
                return resultMap;
            }

        }
        else if("initUserCard".equals(acn)){
            //获取加密数据中的会员id

            JSONObject json = schannel(pos.getTenantCode(), pos.getId().toString(), data, resultMap);
            if(json == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "解析失败");
                return resultMap;
            }
            String vipId = null;
            if(json.get("vipId").toString().contains("id")){
                vipId = (JSONObject.fromObject(json.get("vipId"))).get("id").toString();
            }
            else{
                vipId = json.get("vipId").toString();
            }
            if(StringUtils.isEmpty(vipId)){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无会员id");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("tenantId", pos.getTenantId());
            //params.put("branchId", pos.getBranchId());
            params.put("id", BigInteger.valueOf(Long.valueOf(vipId)));
            Vip vip = findVip(params);
            if(vip == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无效的会员");
                return resultMap;
            }
            else{
                return initCard(pos.getId().toString(), data, erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode(), null);
            }
        }
        else if("initEmployeeCard".equals(acn)){

            JSONObject json = schannel(pos.getTenantCode(), pos.getId().toString(), data, resultMap);
            if(json == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "解析失败");
                return resultMap;
            }
            String employeeCode = (String)json.get("employeeCode");
            if(StringUtils.isEmpty(employeeCode)){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无员工号");
                return resultMap;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("tenantId", pos.getTenantId());
            params.put("branchId", pos.getBranchId());
            params.put("code", employeeCode);
            Employee employee = findEmployee(params);
            if(employee == null){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "无效的员工");
                return resultMap;
            }
            else if(employee.getCardId() != null || StringUtils.isNotEmpty(employee.getCardCode())){
                resultMap.put("Result", Constants.REST_RESULT_FAILURE);
                resultMap.put("Message", "员工已绑定其他卡");
                return resultMap;
            }
            else{
                resultMap = initCard(pos.getId().toString(), data, erp.chain.common.Constants.CARD_TYPE_EMPLOYEE, pos.getTenantId().toString(), pos.getTenantCode(), pos.getPosCode(), pos.getBranchId().toString(), pos.getBranchCode(), employee.getId().toString());
                if(Constants.REST_RESULT_SUCCESS.equals(resultMap.get("Result"))){
                    employee.setCardId(new BigInteger(resultMap.get("cardId")));
                    employee.setCardCode(resultMap.get("cardCode"));
                    employeeMapper.updateEmployee(employee);
                }
                return resultMap;
            }

        }
        else{
            resultMap.put("Result", Constants.REST_RESULT_FAILURE);
            resultMap.put("Message", "参数错误");
        }
        return resultMap;
    }

    /**
     * ID卡初始化
     *
     * @param posId
     * @param data
     * @param type
     * @return
     */
    public Map<String, String> initCard(String posId, String data, Integer type, String tenantId, String tenantCode, String posCode, String branchId, String branchCode, String employeeId){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            if(type.equals(erp.chain.common.Constants.CARD_TYPE_MASTER)){/** 母卡初始化 */
                //判断json中数据与当前pos是否匹配
                if(tenantCode.equals(String.valueOf(json.get("tenantCode"))) && posCode.equals(String.valueOf(json.get("posCode")))){
                    Map<String, Object> params = new HashMap<>();
                    params.put("type", erp.chain.common.Constants.CARD_TYPE_MASTER);
                    params.put("tenantCode", tenantCode);
                    params.put("tenantId", new BigInteger(tenantId));
                    Card card = findCard(params);
                    if(card == null){
                        //母卡卡号
                        Object cardId = json.get("cardId");
                        String cardCode = cardId.toString().replace(" ", "");
                        //判断卡号是否有效
                        if(!(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                            saveCard(r, null, cardCode, null, erp.chain.common.Constants.CARD_TYPE_MASTER, erp.chain.common.Constants.CARD_STATE_ENABLED, new BigInteger(tenantId), tenantCode, null, null, branchId);
                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                            r.put("Message", "卡号无效");
                            r.put("Code", "420402");
                            log.info("IcService.initCard({}, {}, {}) - 卡号无效", posId, data, type);
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "商户母卡已创建，不能重复创建");
                        r.put("Code", "420403");
                        log.info("IcService.initCard({}, {}, {}) - 商户母卡只能有一张", posId, data, type);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "验证失败");
                    r.put("Code", "420404");
                    log.info("IcService.initCard({}, {}, {}) - 验证失败", posId, data, type);
                }
            }
            else if(type.equals(erp.chain.common.Constants.CARD_TYPE_AUTH)){/** 授权卡初始化 */
                //判断json中数据与当前pos是否匹配
                if(tenantCode.equals(String.valueOf(json.get("tenantCode"))) && posCode.equals(String.valueOf(json.get("posCode")))){
                    //母卡卡号
                    Object masterCardId = json.get("masterCardId");
                    String masterCardCode = masterCardId.toString().replace(" ", "");
                    //授权卡卡号
                    Object cardId = json.get("cardId");
                    String cardCode = cardId.toString().replace(" ", "");
                    //判断卡号是否有效
                    if(!(masterCardId instanceof JSONNull) && StringUtils.isNotEmpty(masterCardCode)
                            && !(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                        //获取母卡信息
                        Map<String, Object> params = new HashMap<>();
                        params.put("code", masterCardCode);
                        params.put("tenantId", new BigInteger(tenantId));
                        Card masterCard = findCard(params);
                        //判断母卡信息是否匹配
                        if(masterCard != null && masterCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_MASTER)
                                && masterCard.getTenantId().equals(new BigInteger(tenantId)) && masterCard.getTenantCode().equals(tenantCode)){
                            //员工号
                            Object employeeCode = json.get("employeeCode");
                            //判断员工号是否有效
                            if(!(employeeCode instanceof JSONNull) && StringUtils.isNotEmpty((String)employeeCode)){
                                if(StringUtils.isEmpty(employeeId)){
                                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                    r.put("Message", "员工无效");
                                    return r;
                                }
                                saveCard(r, masterCard.getId(), cardCode, null, erp.chain.common.Constants.CARD_TYPE_AUTH, erp.chain.common.Constants.CARD_STATE_ENABLED, new BigInteger(tenantId), tenantCode, branchCode, new BigInteger(employeeId), branchId);
                                if(com.saas.common.Constants.REST_RESULT_SUCCESS.equals(r.get("Result"))){
                                    r.put("cardCode", cardCode);
                                }
                            }
                            else{
                                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                r.put("Message", "员工号无效");
                                r.put("Code", "420407");
                                log.info("IcService.initCard({}, {}, {}) - 员工号无效", posId, data, type);
                            }
                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                            r.put("Message", "母卡信息不匹配");
                            r.put("Code", "420408");
                            log.info("IcService.initCard({}, {}, {}) - 母卡信息不匹配", posId, data, type);
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "卡号无效");
                        r.put("Code", "420409");
                        log.info("IcService.initCard({}, {}, {}) - 卡号无效", posId, data, type);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "商户信息不匹配");
                    r.put("Code", "420410");
                    log.info("IcService.initCard({}, {}, {}) - 商户信息不匹配", posId, data, type);
                }
            }
            else if(type.equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)){/** 用户卡初始化 */
                //授权卡卡号
                Object authCardId = json.get("authCardId");
                String authCardCode = authCardId.toString().replace(" ", "");
                //会员卡卡号
                Object cardId = json.get("cardId");
                String cardCode = cardId.toString().replace(" ", "");
                Object cardFaceId = json.get("cardFaceNum");
                String cardFaceNum = cardFaceId==null?"":cardFaceId.toString().replace(" ", "");
                //判断卡号是否为空
                if(!(authCardId instanceof JSONNull) && StringUtils.isNotEmpty(authCardCode)
                        && !(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                    //获取授权卡信息
                    Map<String, Object> params = new HashMap<>();
                    params.put("code", authCardCode);
                    params.put("tenantId", new BigInteger(tenantId));
                    Card authCard = findCard(params);
                    //判断授权卡是否有效
                    if(authCard != null && authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH) && authCard.getTenantCode().equals(tenantCode) && authCard.getTenantId().equals(new BigInteger(tenantId)) && authCard.getBranchCode().equals(branchCode)){
                        //会员id
                        Object vipId = null;
                        if(json.get("vipId").toString().contains("id")){
                            vipId = JSONObject.fromObject(json.get("vipId")).get("id");
                        }
                        else{
                            vipId = json.get("vipId");
                        }

                        //判断会员id是否为空
                        if(!(vipId instanceof JSONNull) && StringUtils.isNotEmpty(vipId.toString())){
                            saveCard(r, authCard.getId(), cardCode, cardFaceNum, erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP, erp.chain.common.Constants.CARD_STATE_ENABLED, new BigInteger(tenantId), tenantCode, branchCode, BigInteger.valueOf(Long.valueOf(vipId.toString())), branchId);
                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                            r.put("Message", "会员信息无效");
                            r.put("Code", "420412");
                            log.info("IcService.initCard({}, {}, {}) - 会员信息无效", posId, data, type);
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "授权卡信息不匹配");
                        r.put("Code", "420413");
                        log.info("IcService.initCard({}, {}, {}) - 授权卡信息不匹配", posId, data, type);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420414");
                    log.info("IcService.initCard({}, {}, {}) - 卡号无效", posId, data, type);
                }
            }
            else if(type.equals(erp.chain.common.Constants.CARD_TYPE_EMPLOYEE)){/** 员工卡初始化 */
                //授权卡卡号
                Object authCardId = json.get("authCardId");
                String authCardCode = authCardId.toString().replace(" ", "");
                //员工卡卡号
                Object cardId = json.get("cardId");
                String cardCode = cardId.toString().replace(" ", "");
                //判断卡号是否为空
                if(!(authCardId instanceof JSONNull) && StringUtils.isNotEmpty(authCardCode)
                        && !(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                    //获取授权卡信息
                    Map<String, Object> params = new HashMap<>();
                    params.put("code", authCardCode);
                    params.put("tenantId", new BigInteger(tenantId));
                    Card authCard = findCard(params);
                    //判断授权卡是否有效
                    if(authCard != null && authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH) && authCard.getTenantCode().equals(tenantCode) && authCard.getTenantId().equals(new BigInteger(tenantId)) && authCard.getBranchCode().equals(branchCode)){
                        //员工号
                        Object employeeCode = json.get("employeeCode");
                        //判断员工号是否为空
                        if(!(employeeCode instanceof JSONNull) && StringUtils.isNotEmpty((String)employeeCode)){
                            if(StringUtils.isEmpty(employeeId)){
                                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                r.put("Message", "员工无效");
                                return r;
                            }
                            saveCard(r, authCard.getId(), cardCode, null, erp.chain.common.Constants.CARD_TYPE_EMPLOYEE, erp.chain.common.Constants.CARD_STATE_ENABLED, new BigInteger(tenantId), tenantCode, branchCode, new BigInteger(employeeId), branchId);
                            if(com.saas.common.Constants.REST_RESULT_SUCCESS.equals(r.get("Result"))){
                                r.put("cardCode", cardCode);
                            }
                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                            r.put("Message", "员工号无效");
                            r.put("Code", "420416");
                            log.info("IcService.initCard({}, {}, {}) - 会员信息无效", posId, data, type);
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "授权卡信息不匹配");
                        r.put("Code", "420413");
                        log.info("IcService.initCard({}, {}, {}) - 授权卡信息不匹配", posId, data, type);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420417");
                    log.info("IcService.initCard({}, {}, {}) - 卡号无效", posId, data, type);
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "卡信息无效");
                r.put("Code", "420418");
                log.info("IcService.initCard({}, {}, {}) - 卡信息无效", posId, data, type);
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IcService.initCard({}, {}, {}) - {} - {}", posId, data, type, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }

    /**
     * 保存卡信息
     *
     * @param r
     * @param authCardId
     * @param code
     * @param type
     * @param state
     * @param tenantId
     * @param tenantCode
     * @param branchCode
     * @param holderId
     * @return
     */
    private Map<String, String> saveCard(Map<String, String> r, BigInteger authCardId, String code, String cardFaceNum, Integer type, Integer state, BigInteger tenantId, String tenantCode, String branchCode, BigInteger holderId, String branchId){
        if(r == null){
            r = new HashMap<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("tenantId", tenantId);
        Card card = null;
        if(type == 4){
            params.put("branchId", branchId);
            card = findCardByGroup(params);
        }else{
            card = findCard(params);
        }
        if(card == null){
            if(StringUtils.isNotEmpty(cardFaceNum)){
                Map<String, Object> params2 = new HashMap<>();
                params2.put("code", cardFaceNum);
                params2.put("tenantId", tenantId);
                Card checkCard = null;
                if(type == 4){
                    params2.put("branchId", branchId);
                    checkCard = findCardByGroup(params2);
                }else{
                    checkCard = findCard(params2);
                }
                if(checkCard!=null){
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡面号已经存在！");
                    r.put("Code", "420419");
                    return r;
                }
            }
            card = new Card();
            card.setAuthCardId(authCardId);
            card.setCode(code);
            card.setType(type);
            card.setState(state);
            card.setTenantId(tenantId);
            card.setTenantCode(tenantCode);
            card.setBranchCode(branchCode);
            card.setHolderId(holderId);
            card.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
            Date now = new Date();
            card.setCreateAt(now);
            card.setCreateBy("IC");
            card.setLastUpdateAt(now);
            card.setLastUpdateBy("IC");
            card.setCardFaceNum(cardFaceNum);

            int id = cardMapper.insertCard(card);
            if(id > 0){
                r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                r.put("Message", "创建成功");
                r.put("cardId", String.valueOf(id));
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "创建失败");
                r.put("Code", "199");
            }
        }
        else{
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Message", "卡号已注册");
            r.put("Code", "420419");
            log.info("IcService.saveCard({}) - 卡号已注册", code);
        }
        return r;
    }

    /**
     * 保存ID卡记录
     *
     * @param posId
     * @param data
     * @return
     */
    public Map<String, String> cardRecord(String posId, String tenantCode, String data){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }

            Object cardId = json.get("cardId");
            String cardCode = cardId.toString().replace(" ", "");
            if(!(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                Map<String, Object> params = new HashMap<>();
                params.put("code", cardCode);
                params.put("tenantCode", tenantCode);
                Card card = findCard(params);
                if(card != null){
                    CardRecord cardRecord = new CardRecord();
                    cardRecord.setCardId(card.getId());
                    cardRecord.setJson(json.getJSONObject("json").toString());
                    Date now = new Date();
                    cardRecord.setCreateAt(now);
                    cardRecord.setCreateBy("IC");
                    cardRecord.setLastUpdateAt(now);
                    cardRecord.setLastUpdateBy("IC");
                    cardRecordMapper.insertCardRecord(cardRecord);
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420420");
                    log.info("IdService.verifyCard({}, {}) - 卡号无效", posId, data);
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "卡号无效");
                r.put("Code", "420402");
                log.info("IdService.verifyCard({}, {}) - 卡号无效", posId, data);
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.info("IdService.verifyCard({}, {}) - {} - {}", posId, data, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }

    /*public Map<String, String> deleCard(String posId, String data, String tenantId, String tenantCode, String posCode, String branchId, String branchCode){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            Object cardId = json.get("cardId");
            String cardCode = cardId.toString().replace(" ", "");
            Object authCardId = json.get("authCardId");
            String authCardCode = authCardId.toString().replace(" ", "");
            if(!(authCardId instanceof JSONNull) && StringUtils.isNotEmpty(authCardCode)
                    && !(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){

                Map<String, Object> params = new HashMap<>();
                params.put("code", authCardCode);
                params.put("tenantId", new BigInteger(tenantId));
                Card authCard = findCard(params);
                params.put("code", cardCode);
                Card card = findCard(params);
                if(authCard != null && card != null){
                    if((card.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH) && authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_MASTER))
                            || ((card.getType().equals(erp.chain.common.Constants.CARD_TYPE_EMPLOYEE) || card.getType().equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)) && authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH))){
                        if((authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_MASTER) && authCard.getTenantId().equals(new BigInteger(tenantId)) && authCard.getTenantCode().equals(tenantCode) && card.getTenantId().equals(new BigInteger(tenantId)) && card.getTenantCode().equals(tenantCode))
                                || (authCard.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH) && authCard.getTenantId().equals(new BigInteger(tenantId)) && authCard.getTenantCode().equals(tenantCode) && card.getTenantId().equals(new BigInteger(tenantId)) && card.getTenantCode().equals(tenantCode) && authCard.getBranchCode().equals(branchCode) && card.getBranchCode().equals(branchCode))){
                            card.setDeleted(true);
                            cardMapper.updateCard(card);
                            if(Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)){
                                Map par = new HashMap();
                                par.put("id", card.getHolderId());
                                Vip vip = vipMapper.findByCondition(par);
                                if(vip == null){
                                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                    r.put("Message", "未查询到会员信息");
                                    r.put("Code", "420420");
                                    log.info("IdService.deleCard({}, {}) - 未查询到会员信息", posId, data);
                                }
                                else{
                                    vip.setCardCode("");
                                    vip.setLastUpdateAt(new Date());
                                    int i = vipMapper.update(vip);
                                    r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                                    r.put("Message", "删除成功");
                                    r.put("type", card.getType() == null ? null : card.getType().toString());
                                    r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                                }
                            }
                            else if(Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_EMPLOYEE) || Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_AUTH)){
                                Employee employee = employeeMapper.getEmployeeById(card.getHolderId(), card.getTenantId());
                                if(employee == null){
                                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                    r.put("Message", "未查询到员工信息");
                                    r.put("Code", "420421");
                                    log.info("IdService.deleCard({}, {}) - 未查询到会员信息", posId, data);
                                }
                                else{
                                    int i = employeeMapper.deleteCardInfo(employee.getId());
                                    r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                                    r.put("Message", "删除成功");
                                    r.put("type", card.getType() == null ? null : card.getType().toString());
                                    r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                                }
                            }
                            else{
                                r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                                r.put("Message", "删除成功");
                                r.put("type", card.getType() == null ? null : card.getType().toString());
                                r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                            }

                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                            r.put("Message", "商户信息不匹配");
                            r.put("Code", "420413");
                            log.info("IdService.deleCard({}, {}) - 商户信息不匹配", posId, data);
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "卡信息不匹配");
                        r.put("Code", "420410");
                        log.info("IdService.deleCard({}, {}) - 卡信息不匹配", posId, data);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡号无效");
                    r.put("Code", "420420");
                    log.info("IdService.deleCard({}, {}) - 卡号无效", posId, data);
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "卡号无效");
                r.put("Code", "420402");
                log.info("IdService.deleCard({}, {}) - 卡号无效 - {} - {}", posId, data, authCardId, cardId);
            }

        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IdService.deleCard({}, {}) - {} - {}", posId, data, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }*/

    public Map<String, String> deleCard(String posId, String data, String tenantId, String tenantCode, String posCode, String branchId, String branchCode){
        return deleCardNew(posId,data,tenantId,tenantCode,posCode,branchId,branchCode);
    }

    public Map<String, String> deleCardNew(String posId, String data, String tenantId, String tenantCode, String posCode, String branchId, String branchCode){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            Object cardId = json.get("cardId");
            String cardCode = cardId.toString().replace(" ", "");
            if(!(cardId instanceof JSONNull) && StringUtils.isNotEmpty(cardCode)){
                Map<String, Object> params = new HashMap<>();
                params.put("code", cardCode);
                params.put("tenantId", tenantId);
                Card card = getCardByCode(params);
                if(card != null){
                    if((card.getType().equals(erp.chain.common.Constants.CARD_TYPE_AUTH)
                            || card.getType().equals(erp.chain.common.Constants.CARD_TYPE_EMPLOYEE) || card.getType().equals(erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP))
                            && card.getTenantId().equals(new BigInteger(tenantId)) && card.getTenantCode().equals(tenantCode)){
                        card.setDeleted(true);
                        cardMapper.updateCard(card);
                        if(Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_USER_MEMBERSHIP)){
                            Map par = new HashMap();
                            par.put("id", card.getHolderId());
                            par.put("tenantId", card.getTenantId());
                            Vip vip = vipMapper.findByCondition(par);
                            if(vip == null){
                                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                r.put("Message", "未查询到会员信息");
                                r.put("Code", "420420");
                                log.info("IdService.deleCard({}, {}) - 未查询到会员信息", posId, data);
                            }
                            else{
                                vip.setCardCode("");
                                vip.setLastUpdateAt(new Date());
                                int i = vipMapper.update(vip);
                                r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                                r.put("Message", "删除成功");
                                r.put("type", card.getType() == null ? null : card.getType().toString());
                                r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                            }
                        }
                        else if(Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_EMPLOYEE) || Objects.equals(card.getType(), erp.chain.common.Constants.CARD_TYPE_AUTH)){
                            Employee employee = employeeMapper.getEmployeeById(card.getHolderId(), card.getTenantId());
                            if(employee == null){
                                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                                r.put("Message", "未查询到员工信息");
                                r.put("Code", "420421");
                                log.info("IdService.deleCard({}, {}) - 未查询到会员信息", posId, data);
                            }
                            else{
                                int i = employeeMapper.deleteCardInfo(employee.getId());
                                r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                                r.put("Message", "删除成功");
                                r.put("type", card.getType() == null ? null : card.getType().toString());
                                r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                            }
                        }
                        else{
                            r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                            r.put("Message", "删除成功");
                            r.put("type", card.getType() == null ? null : card.getType().toString());
                            r.put("holder", card.getHolderId() == null ? null : card.getHolderId().toString());
                        }
                    }
                    else{
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "商户信息不匹配");
                        r.put("Code", "420413");
                        log.info("IdService.deleCard({}, {}) - 商户信息不匹配", posId, data);
                    }
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "卡信息不匹配");
                    r.put("Code", "420410");
                    log.info("IdService.deleCard({}, {}) - 卡信息不匹配", posId, data);
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "卡号无效");
                r.put("Code", "420420");
                log.info("IdService.deleCard({}, {}) - 卡号无效", posId, data);
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.error("IdService.deleCard({}, {}) - {} - {}", posId, data, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }

    /**
     * 根据会员id查询卡信息
     *
     * @param posId
     * @param data
     * @return
     */
    public Map<String, String> findCardByHolderId(Integer type, String posId, String data, String tenantId, String tenantCode, String branchId, String branchCode, String posCode){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            Object holderId = json.get("holderId");
            if(!(holderId instanceof JSONNull) && StringUtils.isNotEmpty((String)holderId)){
                Map paramsMap = new HashMap();
                paramsMap.put("holderId", holderId);
                paramsMap.put("tenantCode", tenantCode);
                paramsMap.put("type", type);
                Card card = findCard(paramsMap);
                if(card != null){
                    r.put("Card", BeanUtils.toJsonStr(card));
                    r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                    r.put("Message", "查询卡信息成功");
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "未查询到卡信息");
                    r.put("Code", "420420");
                    LogUtil.logInfo("IdService.findCardByHolderId(${posId}, ${data}) - 未查询到卡信息");
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "未查询到卡信息");
                r.put("Code", "420402");
                LogUtil.logInfo("IdService.findCardByHolderId(${posId}, ${data}) - 未查询到卡信息");
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            LogUtil.logError("IdService.findCardByHolderId(${posId}, ${data}) - ${e.getMessage()}");
        }
        return r;
    }

    /**
     * 查询母卡
     *
     * @return
     */
    public Map<String, String> findMasterCard(Pos pos){
        Map<String, String> r = new HashMap<>();
        try{
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("type", 1);
            paramsMap.put("tenantId", pos.getTenantId());
            paramsMap.put("tenantCode", pos.getTenantCode());
            Card card = findCard(paramsMap);
            if(card != null){
                r.put("Card", BeanUtils.toJsonStr(card));
                r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                r.put("Message", "查询母卡信息成功");
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "未查询到母卡信息");
                r.put("Code", "420420");
                LogUtil.logInfo("IdService.findMasterCard(${posId}, ${data}) - 未查询到母卡信息");
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            LogUtil.logError("IdService.findMasterCard(${pos.getId()}, ${pos}) - ${e.getMessage()}");
        }
        return r;
    }

    /**
     * 查询母卡
     *
     * @return
     */
    public ApiRest findCardByCode(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            String tenantCode = params.get("tenantCode").toString();
            String tenantId = params.get("tenantId").toString();
            String branchId = params.get("branchId").toString();
            String code = params.get("code").toString();
            Map<String, Object> par = new HashMap<>();
            par.put("tenantCode", tenantCode);
            par.put("tenantId", tenantId);
            par.put("branchId", branchId);
            par.put("code", code);
            Card card = findCardByGroup(par);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询卡信息成功！");
            apiRest.setData(card);
        }
        catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询卡信息异常");
            LogUtil.logError("IdService.findCardByCode(${params}) - ${e.getMessage()}");
        }
        return apiRest;
    }
    /**
     * 更新卡面号
     *
     * @return
     */
    public Map<String, String> updateCardFaceNum(String posId, String tenantCode, String data){
        Map<String, String> r = new HashMap<>();
        try{
            JSONObject json = schannel(tenantCode, posId, data, r);
            if(json == null){
                return r;
            }
            Object vipIdObj = json.get("vipId");
            BigInteger vipId = BigInteger.valueOf(Long.valueOf(vipIdObj.toString()));
            Object cardFaceNumObj = json.get("cardFaceNum");
            String cardFaceNum = cardFaceNumObj.toString().replace(" ", "");
            if(!(vipIdObj instanceof JSONNull) && vipId != null && StringUtils.isNotEmpty(cardFaceNum)){
                Map<String,Object> paramsMap = new HashMap<>();
                paramsMap.put("holderId", vipId);
                paramsMap.put("tenantCode", tenantCode);
                paramsMap.put("type", 4);
                Card card = findCard(paramsMap);
                if(card != null){
                    Map<String,Object> checkCodeMap=new HashMap<>();
                    checkCodeMap.put("tenantCode",tenantCode);
                    checkCodeMap.put("type",4);
                    checkCodeMap.put("code",cardFaceNum);
                    Card checkCard=findCard(checkCodeMap);
                    if(checkCard!=null && !checkCard.getId().equals(card.getId())){
                        r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                        r.put("Message", "卡面号已经存在！");
                        r.put("Code", "420420");
                        return r;
                    }
                    card.setCardFaceNum(cardFaceNum);
                    card.setLastUpdateAt(new Date());
                    cardMapper.updateCard(card);
                    r.put("Card", BeanUtils.toJsonStr(card));
                    r.put("Result", com.saas.common.Constants.REST_RESULT_SUCCESS);
                    r.put("Message", "更新卡面号成功！");
                }
                else{
                    r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                    r.put("Message", "未查询到卡信息");
                    r.put("Code", "420420");
                    LogUtil.logInfo("IdService.findCardByHolderId(${posId}, ${data}) - 未查询到卡信息");
                }
            }
            else{
                r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
                r.put("Message", "参数无效");
                r.put("Code", "420402");
                log.info("IdService.verifyCard({}, {}) - 卡号无效", posId, data);
            }
        }
        catch(Exception e){
            r.put("Result", com.saas.common.Constants.REST_RESULT_FAILURE);
            r.put("Error", e.getMessage());
            r.put("Code", "199");
            log.info("IdService.verifyCard({}, {}) - {} - {}", posId, data, e.getClass().getSimpleName(), e.getMessage());
        }
        return r;
    }
}
