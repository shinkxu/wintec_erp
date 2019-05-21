package erp.chain.service.system;

import com.saas.common.exception.ServiceException;
import erp.chain.mapper.system.DistrictMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行政区域
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DistrictService{
    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 根据父ID查询行政区划
     *
     * @param params
     * @return
     */
    public ApiRest qryDistrictByPid(Map<String,String> params){
        ApiRest apiRest = new ApiRest();
        BigInteger pid=BigInteger.ZERO;
        if(StringUtils.isNotEmpty(params.get("pid"))){
            pid=BigInteger.valueOf(Long.parseLong(params.get("pid")));
        }
        if(StringUtils.isNotEmpty(params.get("pId"))){
            pid=BigInteger.valueOf(Long.parseLong(params.get("pId")));
        }
        List<Map> districtList = districtMapper.qryDistrictByPid(pid);
        if(districtList.size() == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到行政区域信息！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询行政区域信息成功！");
        apiRest.setData(districtList);
        return apiRest;
    }

    /**
     * 根据ID查询行政区划
     *
     * @param params
     * @return
     */
    public ApiRest qryDistrictById(Map<String,String> params){
        ApiRest apiRest = new ApiRest();
        String id=BigInteger.ZERO.toString();
        if(StringUtils.isNotEmpty(params.get("id"))){
            id=params.get("id");
        }
        Map districtList = districtMapper.findDistrictById(id);
        if(districtList.size() == 0){
            apiRest.setIsSuccess(false);
            apiRest.setError("未查询到行政区域信息！");
            return apiRest;
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询行政区域信息成功！");
        apiRest.setData(districtList);
        return apiRest;
    }

    /**
     * 查询所有行政区划
     *
     * @return
     */
    public ApiRest qryAllDistrict(){
        ApiRest apiRest = new ApiRest();
        List<Map> districtList = districtMapper.qryAllDistrict();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询所有行政区域信息成功！");
        apiRest.setData(districtList);
        return apiRest;
    }

    public ApiRest findDistrictByIds(Map params){
        ApiRest apiRest = new ApiRest();
        String provinceId = params.get("provinceId").toString();
        String cityId = params.get("cityId").toString();
        String countyId = params.get("countyId").toString();
        Map<String, Object> districtMap = new HashMap<>();
        List<Map> districts = districtMapper.findDistrictByIds(provinceId, cityId, countyId);
        for(Map district : districts){
            if(district.get("id").toString().equals(provinceId)){
                districtMap.put("province", district);
            }
            else if(district.get("id").toString().equals(cityId)){
                districtMap.put("city", district);
            }
            else if(district.get("id").toString().equals(countyId)){
                districtMap.put("county", district);
            }
        }
        apiRest.setData(districtMap);
        apiRest.setIsSuccess(true);
        apiRest.setClazz(districtMap.getClass());
        return apiRest;
    }
    public ApiRest getSystemColor(){
        ApiRest apiRest = new ApiRest();
        List<Map> items = districtMapper.getSystemColor(8);
        apiRest.setData(items);
        apiRest.setIsSuccess(true);
        apiRest.setClazz(items.getClass());
        return apiRest;
    }
}
