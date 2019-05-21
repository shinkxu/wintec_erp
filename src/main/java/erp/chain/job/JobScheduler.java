package erp.chain.job;

import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by xumx on 2016/11/18.
 */
@Component
public class JobScheduler {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    SchedulerFactoryBean schedulerFactoryBean;

    public void scheduler() {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            // 开始积分计算定时任务
            startDealVipScoreJob();

            // 开始库存处理定时任务
            startStoreAccountJob();
            // 开始导购员销售定时任务
            startGuideSaleJob();
            // 开始消息回执处理定时任务
            startMessageReceiptJob();
            // 开始会员自动升级定时任务
            startVipAutoUpgradeJob();
            // 开始会员生日定时任务
            startVipBirthdayJob();
            // 开始清空积分定时任务
            startVipClearScoreJob();
            // 开始有效机构标记定时任务
            startBranchJob();
            // 开始管家婆原料同步定时任务
            startGraspDataJob();
            // 开始会员对账定时任务
            startVipStatementJob();
        } catch (Exception e) {
            log.error("JobScheduler.scheduler - {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 开始积分计算定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startDealVipScoreJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealVipScore", "vipGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealVipScore", "vipGroup");
        String dealVipScoreJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_VIP_SCORE_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealVipScoreJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealVipScoreJobDetail = JobBuilder.newJob(DealVipScoreJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealVipScoreJobCronExpression);
            Trigger dealVipScoreJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealVipScoreJobDetail, dealVipScoreJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始库存计算定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startStoreAccountJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealStoreAccount", "storeGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealStoreAccount", "storeGroup");
        String dealStoreAccountJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_STORE_ACCOUNT_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealStoreAccountJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealStoreAccountDetail = JobBuilder.newJob(DealStoreAccountJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealStoreAccountJobCronExpression);
            Trigger dealStoreAccountJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealStoreAccountDetail, dealStoreAccountJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始导购员销售定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startGuideSaleJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealGuideSale", "guideGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealGuideSale", "guideGroup");
        String dealGuideSaleJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_GUIDE_SALE_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealGuideSaleJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealGuideSaleDetail = JobBuilder.newJob(DealGuideSaleJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealGuideSaleJobCronExpression);
            Trigger dealGuideSaleDetailJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealGuideSaleDetail, dealGuideSaleDetailJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始处理消息回执定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startMessageReceiptJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealMessageReceipt", "messageGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealMessageReceipt", "messageGroup");
        String dealMessageReceiptJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_MESSAGE_RECEIPT_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealMessageReceiptJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealMessageReceipt = JobBuilder.newJob(DealMessageReceiptJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealMessageReceiptJobCronExpression);
            Trigger dealMessageReceiptJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealMessageReceipt, dealMessageReceiptJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始会员自动升级定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startVipAutoUpgradeJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealVipAutoUpgrade", "vipGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealVipAutoUpgrade", "vipGroup");
        String dealVipAutoUpgradeJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_VIP_AUTO_UPGRADE_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealVipAutoUpgradeJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealVipAutoUpgrade = JobBuilder.newJob(DealVipAutoUpgradeJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealVipAutoUpgradeJobCronExpression);
            Trigger dealVipAutoUpgradeJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealVipAutoUpgrade, dealVipAutoUpgradeJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始会员生日定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startVipBirthdayJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealVipBirthday", "vipGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealVipBirthday", "vipGroup");
        String dealVipBirthdayJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_VIP_BIRTHDAY_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealVipBirthdayJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealVipBirthday = JobBuilder.newJob(DealVipBirthdayJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealVipBirthdayJobCronExpression);
            Trigger dealVipBirthdayJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealVipBirthday, dealVipBirthdayJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }


    /**
     * 开始会员积分清空定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startVipClearScoreJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealVipClearScore", "vipGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealVipClearScore", "vipGroup");
        String dealVipClearScoreJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_VIP_CLEAR_SCORE_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealVipClearScoreJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealVipClearScore = JobBuilder.newJob(DealVipClearScoreJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealVipClearScoreJobCronExpression);
            Trigger dealVipClearScoreJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealVipClearScore, dealVipClearScoreJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始有效机构标记定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startBranchJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealBranch", "branchGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealBranch", "branchGroup");
        String dealBranchJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_BRANCH_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealBranchJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealBranch = JobBuilder.newJob(BranchJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealBranchJobCronExpression);
            Trigger dealBranchJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealBranch, dealBranchJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始有效机构标记定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startGraspDataJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if (Constants.SERVICE_NAME_POS.equals(serviceName)) {
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealGraspData", "graspGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealGraspData", "graspGroup");
        String dealGraspDataJobCronExpression = PropertyUtils.getDefault(Constants.DEAL_GRASP_DATA_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dealGraspDataJobCronExpression)) {
            stopJob(jobKey, triggerKey);
            JobDetail dealGraspData = JobBuilder.newJob(GraspDataSynchronizationJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealGraspDataJobCronExpression);
            Trigger dealGraspDataJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealGraspData, dealGraspDataJobCronTrigger);
        } else {
            stopJob(jobKey, triggerKey);
        }
    }

    /**
     * 开始会员对账定时任务
     *
     * @throws IOException
     * @throws SchedulerException
     */
    private void startVipStatementJob() throws IOException, SchedulerException {
        String serviceName = PropertyUtils.getDefault(Constants.SERVICE_NAME);
        if(Constants.SERVICE_NAME_POS.equals(serviceName)){
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey("dealVipStatementJob", "graspGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey("dealVipStatementJob", "graspGroup");
        String dealVipStatementJobException = PropertyUtils.getDefault(Constants.DEAL_VIP_STATEMENT_JOB_CRON_EXPRESSION);
        if(StringUtils.isNotBlank(dealVipStatementJobException)){
            stopJob(jobKey, triggerKey);
            JobDetail dealVipStatement = JobBuilder.newJob(DealVipStatementJob.class).withIdentity(jobKey).build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dealVipStatementJobException);
            Trigger dealVipStatementJobCronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dealVipStatement, dealVipStatementJobCronTrigger);
        }else{
            stopJob(jobKey, triggerKey);
        }
    }

    private void stopJob(JobKey jobKey, TriggerKey triggerKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);

            scheduler.deleteJob(jobKey);
        }
    }
}
