package erp.chain.controller.o2o;

import com.saas.common.ResultJSON;
import com.saas.common.util.LogUtil;
import erp.chain.service.o2o.VipBookService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Map;

/**
 *
 * Created by wangms on 2017/1/9.
 */
@Controller
@ResponseBody
@RequestMapping("/vipBook")
public class VipBookController {
    @Autowired
    private VipBookService vipBookService;
    /**
     * 查询会员台帐列表
     */
    @RequestMapping("/vipBookList")
    public String vipBookList(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipBookService.vipBookList(params);
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 新增会员台帐
     */
    @RequestMapping("/addVipBook")
    public String addVipBook(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipBookService.addVipBook(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }
    /**
     * 保存会员台帐
     */
    @RequestMapping("/saveVipBook")
    public String saveVipBook(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            String tenantId = params.get("tenantId");
            if(StringUtils.isBlank(tenantId)){
                result.setSuccess("1");
                result.setMsg("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(result);
            }
            result = vipBookService.addVipBook(params);
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 修改会员台帐
     */
    @RequestMapping("/editVipBook")
    public String editVipBook(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            if(params.get("id") != null && !"".equals(params.get("id"))){
                result = vipBookService.editVipBookById(new BigInteger(params.get("id")));
            } else {
                result.setSuccess("1");
                result.setMsg("id不能为空!");
            }
        } catch (Exception e) {
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 删除会员台帐
     */
    @RequestMapping("/deleteVipBook")
    public String deleteVipBook(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipBookService.deleteVipBook(params.get("ids"));
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("操作失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

    /**
     * 查找所有vipCode
     */
    @RequestMapping("/findAllVipCode")
    public String findAllVipCode(){
        ResultJSON result = new ResultJSON();
        Map<String,String> params= AppHandler.params();
        try {
            result = vipBookService.findAllVipCode();
        } catch (Exception e){
            LogUtil.logError(e, params);
            result.setSuccess("1");
            result.setMsg("查询失败!");
        }
        return BeanUtils.toJsonStr(result);
    }

}
