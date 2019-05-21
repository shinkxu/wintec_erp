package erp.chain.service.o2o;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.Employee;
import erp.chain.mapper.EmployeeMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.*;

/**
 * 微信
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WechatInfoService{
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 查询微信设置信息
     *
     * @param params
     * @return
     */
    public ApiRest wechatInfo(Map params){
        ApiRest apiRest = new ApiRest();
        Map map = new HashMap();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger userId = BigInteger.valueOf(Long.parseLong((String)params.get("userId")));
        Employee employee = employeeMapper.getTenantEmployee(tenantId);
        if(employee != null){
            //如果不是店长账号，进行标记
            if(!Objects.equals(userId, employee.getUserId())){
                map.put("mainFlag", false);
            }
            userId = employee.getUserId();
        }
        params.put("sysUserId", userId);
        params.put("methodKey", "listWechatInfo");
        String jsonResult = SaaSApi.listWechatInfo(params);
        JSONObject j = JSONObject.fromObject(jsonResult);
        String success = (String)j.get("success");
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        Integer total = Integer.valueOf(jj.get("total").toString());
        if(total == 0){
            apiRest.setIsSuccess(false);
            apiRest.setCode("201");
            apiRest.setError("未查询到账号配置信息");
            return apiRest;
        }
        JSONArray bb = jj.getJSONArray("rows");
        List<Map> list = new ArrayList<>();
        for(int i = 0; i < bb.size(); i++){
            Map wechatInfo = new HashMap();
            Map result = (Map)bb.get(i);
            wechatInfo.put("id", result.get("id"));
            wechatInfo.put("appId", result.get("appId").toString());
            wechatInfo.put("appSecret", result.get("appSecret").toString());
            wechatInfo.put("name", result.get("name").toString());
            wechatInfo.put("originalId", result.get("originalId").toString());
            wechatInfo.put("token", result.get("token").toString());
            wechatInfo.put("mchId", result.get("mchId").toString());
            list.add(wechatInfo);
        }
        map.put("rows", list);
        map.put("total", total);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询成功！");
        return apiRest;
    }

    /**
     * 保存微信配置信息
     * @param params
     * @return
     *//*
    public ApiRest saveOrUpdateWechatInfo(Map params){
        ApiRest apiRest = new ApiRest();
        try{
            String jsonResult="";
            params.put("methodKey", "saveOrUpdate");
            //ApiRest rest= ProxyApi.proxyGet("out","wechat","info",params);
            jsonResult = ApiWebUtils.doPost("http://localhost:9005/wechat/info",params,2000,2000);//SaaSApi.saveWechatInfo(params);
            JSONObject j = JSONObject.fromObject(jsonResult);
            apiRest.setIsSuccess((boolean)j.get("isSuccess"));
            apiRest.setMessage(j.get("msg").toString());
            apiRest.setError(j.get("msg").toString());
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("保存微信配置信息失败！");
            ServiceException se = new ServiceException("1002", "保存微信配置信息失败！", e.getMessage());
            LogUtil.logError(e.getMessage());
            throw se;
        }
        return apiRest;
    }*/
}
