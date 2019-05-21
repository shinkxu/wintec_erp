package erp.chain.service.base;

import erp.chain.domain.GiveBackReason;
import erp.chain.mapper.GiveBackReasonMapper;
import erp.chain.utils.SerialNumberGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赠退原因Service
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GiveBackReasonService{
    @Autowired
    private GiveBackReasonMapper giveBackReasonMapper;

    /**
     * 分页查询赠退原因
     *
     * @param params
     * @return
     */
    public ApiRest queryGBReason(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        Map map = new HashMap();
        Integer rows = Integer.parseInt(params.get("rows").toString());
        Integer offset = (Integer.parseInt(params.get("page").toString()) - 1) * rows;
        params.put("rows", rows);
        params.put("offset", offset);
        params.put("tenantId", tenantId);
        List<HashMap<String, Object>> list = giveBackReasonMapper.queryGBReason(params);
        Long count = giveBackReasonMapper.queryGBReasonCount(params);
        map.put("total", count);
        map.put("rows", list);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询赠退原因成功！");
        return apiRest;
    }

    /**
     * 根据ID查询赠退原因
     *
     * @param params
     * @return
     */
    public ApiRest queryGBReasonById(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        params.put("id", id);
        params.put("tenantId", tenantId);
        GiveBackReason map = giveBackReasonMapper.queryGBReasonById(params);
        apiRest.setIsSuccess(true);
        apiRest.setData(map);
        apiRest.setMessage("查询赠退原因成功！");
        return apiRest;
    }

    /**
     * 新增或修改赠退原因
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateGBR(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        String reason = params.get("reason").toString();
        if(params.get("id") != null && !params.get("id").equals("")){
            BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
            Map map = new HashMap();
            map.put("id", id);
            map.put("tenantId", tenantId);
            GiveBackReason giveBackReason = giveBackReasonMapper.queryGBReasonById(map);
            if(giveBackReason != null){
                giveBackReason.setReason(reason);
                if(params.get("type") != null && !params.get("type").equals("")){
                    giveBackReason.setType(Integer.parseInt(params.get("type").toString()));
                }
                giveBackReason.setLastUpdateAt(new Date());
                int t = giveBackReasonMapper.update(giveBackReason);
                if(t > 0){
                    apiRest.setIsSuccess(true);
                    apiRest.setMessage("修改赠退原因成功！");
                    apiRest.setData(giveBackReason);
                }
                else{
                    apiRest.setIsSuccess(false);
                    apiRest.setError("修改赠退原因失败！");
                }
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("未查询到要修改的赠退原因！");
            }
        }
        else{
            GiveBackReason giveBackReason = new GiveBackReason();
            String code = SerialNumberGenerate.nextSerialNumber(2, giveBackReasonMapper.getMaxGBRCodeWs(params));
            giveBackReason.setCode(code);
            giveBackReason.setType(Integer.parseInt(params.get("type").toString()));
            giveBackReason.setReason(reason);
            giveBackReason.setTenantId(tenantId);
            giveBackReason.setLastUpdateAt(new Date());
            giveBackReason.setCreateAt(new Date());
            giveBackReason.setLastUpdateBy("System");
            giveBackReason.setCreateBy("System");
            int t = giveBackReasonMapper.insert(giveBackReason);
            if(t > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("添加赠退原因成功！");
                apiRest.setData(giveBackReason);
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("添加赠退原因失败！");
            }
        }
        return apiRest;
    }

    /**
     * 删除赠退原因
     *
     * @param params
     * @return
     */
    public ApiRest delGBReason(Map params){
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String)params.get("tenantId")));
        BigInteger id = BigInteger.valueOf(Long.parseLong((String)params.get("id")));
        Map map = new HashMap();
        map.put("id", id);
        map.put("tenantId", tenantId);
        GiveBackReason giveBackReason = giveBackReasonMapper.queryGBReasonById(map);
        if(giveBackReason != null){
            giveBackReason.setDeleted(true);
            giveBackReason.setLastUpdateBy("System");
            giveBackReason.setLastUpdateAt(new Date());
            int t = giveBackReasonMapper.update(giveBackReason);
            if(t > 0){
                apiRest.setIsSuccess(true);
                apiRest.setMessage("删除赠退原因成功！");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setError("删除赠退原因失败！");
            }
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到要删除的赠退原因！");
        }
        return apiRest;
    }

    /**
     * 生成赠退原因编码
     *
     * @param params
     * @return
     */
    public ApiRest getNextGBRCode(Map params){
        ApiRest apiRest = new ApiRest();
        String code = SerialNumberGenerate.nextSerialNumber(2, giveBackReasonMapper.getMaxGBRCodeWs(params));
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成赠退原因编码成功！");
        apiRest.setData(code);
        return apiRest;
    }
}
