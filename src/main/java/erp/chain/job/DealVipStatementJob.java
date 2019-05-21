package erp.chain.job;

import erp.chain.service.o2o.VipService;
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
 * User: LiP
 * Date: 2018/12/13
 */
@DisallowConcurrentExecution
public class DealVipStatementJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private VipService vipService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "会员对账记录定时任务开始执行。");
        try {
            vipService.vipStatement();
        } catch (Exception e) {
            LogUtils.error("会员对账定时任务执行失败", className, "execute", e);
        }
    }
}
