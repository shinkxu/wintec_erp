package erp.chain.service.system;

import com.saas.common.Constants;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.domain.Branch;
import erp.chain.domain.system.SmsUseHistory;
import erp.chain.domain.system.SysConfig;
import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.system.SmsMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.SaaSApi;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/6/22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsService{

    @Autowired
    private SmsMapper smsMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;

    private final static String USE_BRANCH_VIP_MESSAGE="usebranchvipmessage";

    /**
     * 发送短信
     * @param params
     * @return
     */
    public ApiRest sendSMS(Map params){
        ApiRest apiRest=new ApiRest();
        try{
            BigInteger tenantId=BigInteger.valueOf(Long.valueOf((String)params.get("tenantId")));
            BigInteger branchId=BigInteger.valueOf(Long.valueOf((String)params.get("branchId")));
            TenantConfig tenantConfig1=tenantConfigMapper.findByNameAndTenantId(USE_BRANCH_VIP_MESSAGE,tenantId);
            String usebranchvipmessage="0";
            if(tenantConfig1!=null){
                usebranchvipmessage=tenantConfig1.getValue();
            }
            Branch branch=branchMapper.findBranchByTenantIdAndBranchId(tenantId,branchId);
            String name = branch.getName();
            ApiRest rest= SaaSApi.findTenantById(tenantId);
            Map tenant=(Map)((Map)rest.getData()).get("tenant");
            Integer type=Integer.valueOf(params.get("type").toString());
            if("0".equals(usebranchvipmessage)){//使用同一短信
                if(Integer.valueOf(tenant.get("smsStatus").toString())==1){
                    apiRest.setIsSuccess(false);
                    LogUtil.logInfo("运营未打开商户短信功能！商户ID"+tenantId);
                    apiRest.setError("用户短信功能未开放！");
                    return apiRest;
                }
                if(Integer.valueOf(tenant.get("smsCount").toString()) <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("短信余额不足，发送短信失败！");
                    LogUtil.logInfo("短信余额不足，发送短信失败！商户ID"+tenantId);
                    return apiRest;
                }
                if(tenant.get("smsUseWay")==null||tenant.get("smsUseWay").toString().equals("")){
                    apiRest.setIsSuccess(false);
                    LogUtil.logInfo("用户短信功能未开启！商户ID"+tenantId);
                    apiRest.setError("用户短信功能未开启！");
                    return apiRest;
                }else{
                    String smsUseWay=tenant.get("smsUseWay").toString();
                    boolean fl=false;
                    for(String i:smsUseWay.split(",")){
                        if(i.equals(type.toString())){
                            fl=true;
                        }
                    }
                    if(!fl){
                        apiRest.setIsSuccess(false);
                        LogUtil.logInfo("用户短信功能未开启！商户ID"+tenantId);
                        apiRest.setError("用户短信功能未开启！");
                        return apiRest;
                    }
                }
            }else if("1".equals(usebranchvipmessage)){//使用本机构短信
                if(branch.getSmsStatus()==1){
                    apiRest.setIsSuccess(false);
                    LogUtil.logInfo("商户未开启机构短信功能！商户ID"+tenantId);
                    apiRest.setError("商户未开启机构短信功能！");
                    return apiRest;
                }
                if(branch.getSmsCount() <= 0){
                    apiRest.setIsSuccess(false);
                    apiRest.setError("短信余额不足，发送短信失败！");
                    LogUtil.logInfo("短信余额不足，发送短信失败！商户ID"+tenantId);
                    return apiRest;
                }
                if(branch.getSmsUseWay()==null||branch.getSmsUseWay().equals("")){
                    apiRest.setIsSuccess(false);
                    LogUtil.logInfo("门店短信功能未开启！门店ID"+branch.getId());
                    apiRest.setError("门店短信功能未开启！");
                    return apiRest;
                }else{
                    String smsUseWay=branch.getSmsUseWay();
                    boolean fl=false;
                    for(String i:smsUseWay.split(",")){
                        if(i.equals(type.toString())){
                            fl=true;
                        }
                    }
                    if(!fl){
                        apiRest.setIsSuccess(false);
                        LogUtil.logInfo("门店短信功能未开启！门店ID"+branch.getId());
                        apiRest.setError("门店短信功能未开启！");
                        return apiRest;
                    }
                }
            }
            Map<String,String> map=new HashMap<>();
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=sf.format(new Date());
            String templateId;
            TenantConfig tenantConfig;
            switch(type){
                case 1:
                    templateId= PropertyUtils.getDefault("sms.template.vip_store_trade");
                    tenantConfig=tenantConfigMapper.findByNameAndTenantId(SysConfig.VIP_STORE_TRADE_MESSAGE,tenantId);
                    if(tenantConfig!=null && StringUtils.isNotEmpty(tenantConfig.getValue())){
                        templateId=tenantConfig.getValue();
                    }
                    map.put("tenantId",tenantId.toString());
                    map.put("number",params.get("number").toString());
                    map.put("template",templateId);
                    map.put("content",time+"###"+name+"###"+params.get("content").toString());
                    break;
                case 2:
                    templateId= PropertyUtils.getDefault("sms.template.vip_store");
                    tenantConfig=tenantConfigMapper.findByNameAndTenantId(SysConfig.VIP_STORE_MESSAGE,tenantId);
                    if(tenantConfig!=null && StringUtils.isNotEmpty(tenantConfig.getValue())){
                        templateId=tenantConfig.getValue();
                    }
                    map.put("tenantId",tenantId.toString());
                    map.put("number",params.get("number").toString());
                    map.put("template",templateId);
                    map.put("content",time+"###"+name+"###"+params.get("content").toString());
                    break;
                case 3:
                    templateId= PropertyUtils.getDefault("sms.template.vip_score");
                    tenantConfig=tenantConfigMapper.findByNameAndTenantId(SysConfig.VIP_SCORE_MESSAGE,tenantId);
                    if(tenantConfig!=null && StringUtils.isNotEmpty(tenantConfig.getValue())){
                        templateId=tenantConfig.getValue();
                    }
                    map.put("tenantId",tenantId.toString());
                    map.put("number",params.get("number").toString());
                    map.put("template",templateId);
                    map.put("content",time+"###"+name+"###"+params.get("content").toString());
                    break;
                case 4:
                    templateId= PropertyUtils.getDefault("sms.template.vip_score_use");
                    tenantConfig=tenantConfigMapper.findByNameAndTenantId(SysConfig.VIP_SCORE_USE_MESSAGE,tenantId);
                    if(tenantConfig!=null && StringUtils.isNotEmpty(tenantConfig.getValue())){
                        templateId=tenantConfig.getValue();
                    }
                    map.put("tenantId",tenantId.toString());
                    map.put("number",params.get("number").toString());
                    map.put("template",templateId);
                    map.put("content",time+"###"+name+"###"+params.get("content").toString());
                    break;
            }
            apiRest= ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT,"sms","sendByTemplate",map);
            if(apiRest.getIsSuccess()){
                if("0".equals(usebranchvipmessage)){//扣减商户短信账户
                    tenant.put("smsCount",Integer.valueOf(tenant.get("smsCount").toString())-1);
                    Map tenantMap=new HashMap();
                    tenantMap.put("tenant", BeanUtils.toJsonStr(tenant));
                    ApiRest ar=ProxyApi.proxyGet(Constants.SERVICE_NAME_BS,"tenant","save",tenantMap);
                    if(!ar.getIsSuccess()){
                        apiRest.setIsSuccess(false);
                        apiRest.setError("更新商户短信数失败！");
                        LogUtil.logInfo("更新商户短信数失败！tenantId="+tenantId);
                        return apiRest;
                    }
                }else if("1".equals(usebranchvipmessage)){//扣减门店短信账户
                    branch.setSmsCount(branch.getSmsCount()-1);
                    branch.setLastUpdateAt(new Date());
                    branchMapper.update(branch);

                }
                SmsUseHistory smsUseHistory=new SmsUseHistory();
                smsUseHistory.setType(type);
                smsUseHistory.setTenantId(tenantId);
                smsUseHistory.setUseBranchId(branchId);
                smsUseHistory.setLastUpdateAt(new Date());
                smsUseHistory.setLastUpdateBy("System");
                smsUseHistory.setCreateBy("System");
                smsUseHistory.setCreateAt(new Date());
                smsUseHistory.setSendNumber(params.get("number").toString());
                smsUseHistory.setRestCount(Integer.valueOf(tenant.get("smsCount").toString()));
                int i=smsMapper.saveSmsLog(smsUseHistory);
                if(i<=0){
                    LogUtil.logInfo("保存发送短信日志失败！tenantId="+tenantId);
                }
                Integer SMS_WARNING_COUNT=Integer.valueOf(PropertyUtils.getDefault("sms.warning.count"));
                if("0".equals(usebranchvipmessage)){//扣减商户短信账户
                    if(tenant.get("phoneNumber")!=""&&Integer.valueOf(tenant.get("smsCount").toString()) == (SMS_WARNING_COUNT-1)){
                        Map<String,String> warningMap=new HashMap<>();
                        String SMS_TENANT_COUNT_WARNING_TEMPLATE= PropertyUtils.getDefault("sms.template.tenant_count_warning");
                        warningMap.put("tenantId",tenantId.toString());
                        warningMap.put("number",tenant.get("phoneNumber").toString());
                        warningMap.put("template",SMS_TENANT_COUNT_WARNING_TEMPLATE);
                        warningMap.put("content",SMS_WARNING_COUNT.toString());
                        apiRest= ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT,"sms","sendByTemplate",warningMap);
                        if(!apiRest.getIsSuccess()){
                            LogUtil.logInfo("发送阈值提醒短息失败！tenantId="+tenantId);
                        }
                    }
                }else if("1".equals(usebranchvipmessage)){//扣减门店短信账户
                    if(StringUtils.isNotEmpty(branch.getPhone())&&branch.getSmsCount() == (SMS_WARNING_COUNT-1)){
                        Map<String,String> warningMap=new HashMap<>();
                        String SMS_TENANT_COUNT_WARNING_TEMPLATE= PropertyUtils.getDefault("sms.template.tenant_count_warning");
                        warningMap.put("tenantId",tenantId.toString());
                        warningMap.put("number",branch.getPhone());
                        warningMap.put("template",SMS_TENANT_COUNT_WARNING_TEMPLATE);
                        warningMap.put("content",SMS_WARNING_COUNT.toString());
                        apiRest= ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT,"sms","sendByTemplate",warningMap);
                        if(!apiRest.getIsSuccess()){
                            LogUtil.logInfo("发送阈值提醒短息失败！tenantId="+tenantId+",branchId="+branch.getId());
                        }
                    }

                }
            }else{
                LogUtil.logInfo("发送短息失败！TYPE="+type+"--TENANT_ID="+tenantId+"--BRANCH_ID="+branch.getId());
            }
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("发送短信失败！");
            LogUtil.logInfo("发送短息失败！");
        }
        return apiRest;
    }
}
