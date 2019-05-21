package erp.chain.configurations;

import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by liuyandong on 2018-08-16.
 */
@Configuration
@ConditionalOnProperty(name = Constants.INIT_QUARTZ, havingValue = Constants.LOWER_CASE_TRUE)
public class QuartzConfiguration {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory());

        String deployEnv = PropertyUtils.getDefault(Constants.DEPLOY_ENV);
        if (Constants.DEPLOY_ENV_WWW_ALI.equals(deployEnv) || Constants.DEPLOY_ENV_SANMI_WWW.equals(deployEnv)) {
            Resource configLocation = new ClassPathResource(Constants.QUARTZ_PROPERTIES);
            schedulerFactoryBean.setConfigLocation(configLocation);
            schedulerFactoryBean.setDataSource(dataSource);
            schedulerFactoryBean.setTransactionManager(platformTransactionManager);
            schedulerFactoryBean.setSchedulerName(Constants.SCHEDULER_NAME);
            schedulerFactoryBean.setApplicationContextSchedulerContextKey(Constants.APPLICATION_CONTEXT);
        }

        return schedulerFactoryBean;
    }

    @Bean
    public JobFactory jobFactory() {
        return new JobFactory();
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
