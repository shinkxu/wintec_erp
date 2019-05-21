package erp.chain.controller.system;

import com.saas.common.util.LogUtil;
import erp.chain.service.system.MessageNotificationService;
import erp.chain.utils.AppHandler;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/3/15
 */
@Controller
@ResponseBody
@RequestMapping("/messageNotification")
public class MessageNotificationController{

    @Autowired
    private MessageNotificationService messageNotificationService;

    /**
     * 查询消息通知列表
     * @return
     */
    @RequestMapping("/getMessageList")
    public String getMessageList(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.getMessageList(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据id查询通知
     * @return
     */
    @RequestMapping("/getMessageById")
    public String getMessageById(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.getMessageById(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增或修改消息通知
     * @return
     */
    @RequestMapping("/saveOrUpdateMessage")
    public String saveOrUpdateMessage(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("type")==null||params.get("type").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数type不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("targetType")==null||params.get("targetType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数targetType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isSendApos")==null||params.get("isSendApos").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isSendApos不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isSendWpos")==null||params.get("isSendWpos").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isSendWpos不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("title")==null||params.get("title").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数title不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("content")==null||params.get("content").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数content不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("createEmpId")==null||params.get("createEmpId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数createEmpId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("displayType")==null||params.get("displayType").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数displayType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isNewUser")==null||params.get("isNewUser").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isNewUser不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("isOldUser")==null||params.get("isOldUser").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数isOldUser不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.saveOrUpdateMessage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 删除消息通知
     * @return
     */
    @RequestMapping("/deleteMessage")
    public String deleteMessage(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.deleteMessage(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 发送消息
     * @return
     */
    @RequestMapping("/sendMessageNotification")
    public String sendMessageNotification(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.sendMessageNotification(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 重新投递
     * @return
     */
    @RequestMapping("/resendMessageNotification")
    public String resendMessageNotification(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.resendMessageNotification(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 撤回消息
     * @return
     */
    @RequestMapping("/withdrawMessageNotification")
    public String withdrawMessageNotification(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("id")==null||params.get("id").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.withdrawMessageNotification(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 查询回执消息
     * @return
     */
    @RequestMapping("/listMessageReceipt")
    public String listMessageReceipt(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            if (params.get("tenantId")==null||params.get("tenantId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("messageId")==null||params.get("messageId").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数messageId不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = messageNotificationService.listMessageReceipt(params);
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 回执处理任务
     * @return
     */
    @RequestMapping("/dealMessageReceipt")
    public String dealMessageReceipt(){
        ApiRest apiRest = new ApiRest();
        Map<String,String> params= AppHandler.params();
        try {
            apiRest = messageNotificationService.dealMessageReceipt();
        } catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

}
