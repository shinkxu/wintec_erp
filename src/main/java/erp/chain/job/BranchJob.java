package erp.chain.job;

import erp.chain.service.setting.OrganizationService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.LogUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 活跃商户标记
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/9/19
 */
@DisallowConcurrentExecution
public class BranchJob implements Job{
    private String className = this.getClass().getName();
    private static final String LOCAL_HOST_ADDRESS = ApplicationHandler.obtainLocalHostAddress();
    @Autowired
    private OrganizationService organizationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        LogUtils.info(LOCAL_HOST_ADDRESS + "有效机构标记定时任务开始执行。");
        try {
            organizationService.branchJob();
        } catch (Exception e) {
            LogUtils.error("有效机构标记定时任务执行失败", className, "execute", e);
        }
    }
}
