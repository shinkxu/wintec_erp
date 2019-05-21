package erp.chain.job;

import erp.chain.service.o2o.CardService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.LogUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/9/6
 */
@DisallowConcurrentExecution
public class DealVipClearScoreJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private CardService cardService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "会员清空积分定时任务开始执行。");
        try {
            cardService.clearScoreThread(new HashMap());
        } catch (Exception e) {
            LogUtils.error("会员清空积分定时任务执行失败", className, "execute", e);
        }
    }
}
