package erp.chain.controller.system;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;
import erp.chain.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/5/4
 */
@Controller
@ResponseBody
@RequestMapping("/pushMessage")
public class PushMessageController{

    @Autowired
    private ReportService reportService;

    public PushPayload buildPushObject_all_alias_alert(Map map) {
        NumberFormat nFormat= NumberFormat.getNumberInstance();
        nFormat.setMaximumFractionDigits(5);
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(map.get("tenantId").toString()+"@"+map.get("branchId").toString()))
                .setNotification(Notification.newBuilder()
                        .setAlert("昨日营业额："+nFormat.format(map.get("totalAmount"))+"，客单数："+map.get("totalCount")+"，客单价："+nFormat.format(map.get("averagePrice")))
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("惠管家昨日营业状况").build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra("extra_key", "extra_value").build())
                        .build())
                .build();
    }

    @RequestMapping("/pushMessage")
    public void pushMessage() throws IOException{
        String MASTER_SECRET= PropertyUtils.getDefault("jpush_master_secret");
        String APP_KEY= PropertyUtils.getDefault("jpush_app_key");
        JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

        try {
            List<Map> resultList=reportService.pushData();
            for(Map data:resultList){
                PushPayload payload = buildPushObject_all_alias_alert(data);
                PushResult result = jpushClient.sendPush(payload);
                LogUtil.logInfo("Got result - " + result);
            }

        } catch (APIConnectionException e) {
            // Connection error, should retry later
            LogUtil.logError("Connection error, should retry later", e.getMessage());

        } catch (APIRequestException e) {
            LogUtil.logError("Should review the error, and fix the request", e.getMessage());
            LogUtil.logInfo("HTTP Status: " + e.getStatus());
            LogUtil.logInfo("Error Code: " + e.getErrorCode());
            LogUtil.logInfo("Error Message: " + e.getErrorMessage());
        }
    }
}
