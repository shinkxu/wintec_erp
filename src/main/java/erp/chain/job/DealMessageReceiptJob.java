package erp.chain.job;

import erp.chain.service.system.MessageNotificationService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.LogUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/9/6
 */
@DisallowConcurrentExecution
public class DealMessageReceiptJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private MessageNotificationService messageNotificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "处理消息回执定时任务开始执行。");
        try {
            messageNotificationService.dealMessageReceipt();
        } catch (Exception e) {
            LogUtils.error("处理消息回执定时任务执行失败", className, "execute", e);
        }
    }
}
