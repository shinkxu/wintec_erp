package erp.chain.service.system;

import erp.chain.mapper.system.OperateLogMapper;
import erp.chain.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/10/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OperateLogService{


    @Autowired
    private OperateLogMapper operateLogMapper;
    public void saveLog(Object newObj, Map oldObj, Integer type, String opFrom, String opBy, BigInteger tenantId, BigInteger branchId) throws IllegalAccessException {
        String opType;
        switch(type){
            case 1:
                opType="新增";
                break;
            case 2:
                opType="修改";
                break;
            case 3:
                opType="删除";
                break;
            default:
                opType="修改";
                break;
        }
        String resource="";
        switch(newObj.getClass().getSimpleName()){
            case "Goods":
                resource="商品";
                break;
            case "Payment":
                resource="支付方式";
                break;
            case "PaymentBranch":
                resource="支付方式(机构)";
                break;
            case "Pos":
                resource="Pos信息";
                break;
            default:
                opType=newObj.getClass().getSimpleName();
                break;
        }
        Map newMap= CommonUtils.ObjectToMap(newObj);
        newMap.remove("lastUpdateAt");
        newMap.remove("version");
        Map oldMap=oldObj;
        for(Object obj:newMap.keySet()){
            if(!newMap.get(obj).toString().equals(oldMap.get(obj)==null?"":oldMap.get(obj).toString())){
                BigInteger id=BigInteger.valueOf(Long.valueOf(newMap.get("id").toString()));
                operateLogMapper.createOpLog(resource,id,opType+(getName(newObj.getClass().getSimpleName(),obj.toString())),oldMap.get(obj)==null?"":oldMap.get(obj).toString(),newMap.get(obj).toString(),opBy,opFrom,"",tenantId,branchId);
            }
        }
    }

    private String getName(String resource,String key){
        String result=operateLogMapper.getName(resource,key);
        if(result==null){
            result="";
        }
        return result;
    }
}
