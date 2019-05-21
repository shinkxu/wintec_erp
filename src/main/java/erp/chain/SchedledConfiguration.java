package erp.chain;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.concurrent.Executors;

/**
 * Created by xumx on 2016/11/18.
 */

//@Configuration
public class SchedledConfiguration implements SchedulingConfigurer{

    /*@Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        return schedulerFactoryBean;
    }*/
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }

    @Bean
    public JobFactory jobFactory() {
        return new JobFactory();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));
    }

    public class JobFactory extends AdaptableJobFactory {
        @Autowired
        private AutowireCapableBeanFactory autowireCapableBeanFactory;

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            autowireCapableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }
    }
}
