package erp.chain.service.system;

import erp.chain.domain.system.TenantConfig;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.mapper.system.TenantConfigMapper;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * 商户配置参数
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/17
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.RuntimeException.class}, timeout = 120)
public class TenantConfigService{
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private SerialNumberCreatorMapper serialNumberCreatorMapper;


    public ApiRest getTenantConfig(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        String key=params.get("name");
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        apiRest.setIsSuccess(true);
        apiRest.setData(tenantConfig);
        apiRest.setMessage("查询商户配置成功！");
        return apiRest;
    }
    public ApiRest updateTenantConfig(Map<String,String> params){
        ApiRest apiRest=new ApiRest();
        BigInteger tenantId=BigInteger.valueOf(Long.valueOf(params.get("tenantId")));
        String key=params.get("name");
        String value=params.get("value");
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            if("usebranchvipmessage".equals(key)){//插入
                tenantConfig=new TenantConfig();
                tenantConfig.setTenantId(tenantId);
                tenantConfig.setName(key);
                tenantConfig.setValue(value);
                tenantConfig.setMaxValue("1");
                tenantConfig.setLastUpdateAt(new Date());
                tenantConfig.setCreateAt(new Date());
                tenantConfig.setDeleted(false);
                tenantConfigMapper.insert(tenantConfig);
            }
        }else{
            tenantConfig.setValue(value);
            tenantConfig.setLastUpdateAt(new Date());
            tenantConfigMapper.update(tenantConfig);
        }
        apiRest.setIsSuccess(true);
        apiRest.setData(tenantConfig);
        apiRest.setMessage("修改商户配置成功！");
        return apiRest;
    }
    /**
     * 个数减一
     * @param tenantId
     * @param key
     * @return
     */
    public ApiRest minusTenantConfigOne(BigInteger tenantId, String key){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            String newValue=String.valueOf(Long.parseLong(tenantConfig.getValue())-1);
            tenantConfig.setValue(newValue);
            tenantConfig.setLastUpdateAt(new Date());
            int i=tenantConfigMapper.update(tenantConfig);
            if(i>0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("参数减一成功！");
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("参数减一失败！");
            }
            return apiRest;
        }
    }

    /**
     * 个数加一
     * @param tenantId
     * @param key
     * @return
     */
    public ApiRest addTenantConfigOne(BigInteger tenantId,String key){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            String newValue=String.valueOf(Long.parseLong(tenantConfig.getValue())+1);
            tenantConfig.setValue(newValue);
            tenantConfig.setLastUpdateAt(new Date());
            int i=tenantConfigMapper.update(tenantConfig);
            if(i>0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("参数加一成功！");
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("参数加一失败！");
            }
            return apiRest;
        }
    }

    /**
     * 检查是否可以添加
     * @param tenantId
     * @param key
     * @return
     */
    public ApiRest checkConfig(BigInteger tenantId,String key){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            if(Long.parseLong(tenantConfig.getValue())>=Long.parseLong(tenantConfig.getMaxValue())){
                apiRest.setIsSuccess(false);
                apiRest.setError("您最多可以添加"+tenantConfig.getMaxValue()+"个"+getItem(key));
                return apiRest;
            }else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("可以添加");
                return apiRest;
            }
        }
    }

    public String getItem(String key){
        String message = "";
        switch(key){
            case "vipnum":
                message = "会员";
                break;
            case "dietnum":
                message = "";
                break;
            case "firstcatnum":
                message = "一级分类";
                break;
            case "secondcatnum":
                message = "二级分类";
                break;
            case "goodsnum":
                message = "商品";
                break;
            case "packagenum":
                message = "套餐";
                break;
            case "hotgoodsnum":
                message = "热销商品";
                break;
            case "recommendnum":
                message = "推荐商品";
                break;
            case "specnum":
                message = "口味";
                break;
            case "branchnum":
                message = "机构";
                break;
            case "empnum":
                message = "员工";
                break;
            case "unitnum":
                message = "单位";
                break;
            case "paymentnum":
                message = "支付方式";
                break;
            case "dietpromotionnum":
                message = "促销活动";
                break;
            case "vipstorenum":
                message = "";
                break;
            case "labelnum":
                message = "会员标签";
                break;
            default:
                message = "";
                break;
        }
        return message;
    }
    /**
     * 查询最大数值
     * @param tenantId
     * @param key
     * @return
     */
    public ApiRest getMaxValue(BigInteger tenantId,String key){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            apiRest.setIsSuccess(true);
            apiRest.setData(tenantConfig.getMaxValue());
            return apiRest;
        }
    }

    public ApiRest isOverMax(BigInteger tenantId,String key,Integer value){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            Integer nowValue=Integer.valueOf(tenantConfig.getValue());
            Integer maxValue=Integer.valueOf(tenantConfig.getMaxValue());
            if(nowValue+value>maxValue){
                apiRest.setIsSuccess(false);
                apiRest.setError("超出数量限制，您只能导入最多"+(maxValue-nowValue)+"条数据");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("你还可以导入"+(maxValue-nowValue)+"条数据");
            return apiRest;
        }
    }


    /**
     * 获取下个单号，全局
     */
    public String getToday(String prefix,Integer length) {
        String no = serialNumberCreatorMapper.getToday(prefix,length);
        return String.format("%s%s%s", prefix, DateFormatUtils.format(new Date(), "yyMMdd"), no);
    }

    /**
     * 获取下个单号，机构级别
     */
    public String getBranchToday(String prefix, Integer length,BigInteger bId) {
        String no = serialNumberCreatorMapper.getBranchToday(prefix,length,bId);
        return String.format("%s%s%s", prefix, DateFormatUtils.format(new Date(), "yyMMdd"), no);
    }

    /**
     * 减自定义个数
     * @param tenantId
     * @param key
     * @return
     */
    public ApiRest minusTenantConfigValue(BigInteger tenantId, String key, Long value){
        ApiRest apiRest=new ApiRest();
        TenantConfig tenantConfig=tenantConfigMapper.findByNameAndTenantId(key,tenantId);
        if(tenantConfig==null){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到此配置项！");
            return apiRest;
        }else{
            String newValue=String.valueOf(Long.parseLong(tenantConfig.getValue())-value);
            tenantConfig.setValue(newValue);
            tenantConfig.setLastUpdateAt(new Date());
            int i=tenantConfigMapper.update(tenantConfig);
            if(i>0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("参数减"+value+"成功！");
            }else{
                apiRest.setIsSuccess(false);
                apiRest.setError("参数减"+value+"失败！");
            }
            return apiRest;
        }
    }
}
