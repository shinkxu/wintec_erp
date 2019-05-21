package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.DistrictService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 行政区域
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/27
 */
@Controller
@ResponseBody
@RequestMapping(value = {"/district","publicWebService"})
public class DistrictController{
    @Autowired
    private DistrictService districtService;
    @Autowired
    private HttpServletRequest request;
    /**
     * 根据父级行政区域查询行政区划
     * @return
     */
    @RequestMapping("/qryDistrictByPid")
    public String qryDistrictByPid(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = districtService.qryDistrictByPid(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 根据父级行政区域查询行政区划
     * @return
     */
    @RequestMapping("/qryDistrictById")
    public String qryDistrictById(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = districtService.qryDistrictById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询所有行政区划
     * @return
     */
    @RequestMapping("/qryAllDistrict")
    public String qryAllDistrict(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = districtService.qryAllDistrict();
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/findDistrictByIds")
    public String findDistrictByIds(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = districtService.findDistrictByIds(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/getAllIndustry")
    public String getAllIndustry(){
        ApiRest apiRest = new ApiRest();
        try {
            String realPath = request.getServletContext().getRealPath("/easyui/business.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(realPath), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            String jsonString = sb.toString();
            JSONArray jsonArray = JSONArray.fromObject(jsonString);
            apiRest.setData(jsonArray);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    @RequestMapping("/getSystemColor")
    public String getSystemColor(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = districtService.getSystemColor();
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
}
