package erp.chain.service.system;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.domain.OnlinePayInfo;
import erp.chain.mapper.system.OnlinePayInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.util.Date;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/1/10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OnlinePayInfoService{
    @Autowired
    private OnlinePayInfoMapper onlinePayInfoMapper;

    public ApiRest saveInfo(Map params){
        ApiRest apiRest = new ApiRest();
        OnlinePayInfo onlinePayInfo = new OnlinePayInfo(params);
        OnlinePayInfo info = onlinePayInfoMapper.findInfo(onlinePayInfo);
        if(info != null){
            onlinePayInfo.setId(info.getId());
            onlinePayInfo.setLastUpdateAt(new Date());
            int i = onlinePayInfoMapper.update(onlinePayInfo);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("更新支付信息失败");
                apiRest.setError("更新支付信息失败");
            }
            else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("更新支付信息成功");
            }
        }
        else{
            onlinePayInfo.setLastUpdateAt(new Date());
            onlinePayInfo.setCreateAt(new Date());
            int i = onlinePayInfoMapper.insert(onlinePayInfo);
            if(i <= 0){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("新增支付信息失败");
                apiRest.setError("新增支付信息失败");
            }
            else{
                apiRest.setIsSuccess(true);
                apiRest.setMessage("新增支付信息成功");
            }
        }
        return apiRest;
    }
}
