package erp.chain.job;

import erp.chain.service.setting.EmployeeWebService;
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
public class DealGuideSaleJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private EmployeeWebService employeeWebService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "导购员销售定时任务开始执行。");
        try {
            employeeWebService.guideSaleJob();
        } catch (Exception e) {
            LogUtils.error("导购员销售定时任务执行失败", className, "execute", e);
        }
    }
}
