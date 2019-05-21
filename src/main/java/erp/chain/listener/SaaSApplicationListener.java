package erp.chain.listener;

import erp.chain.common.Constants;
import erp.chain.job.JobScheduler;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.ConfigurationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Xumx on 2015/8/31.
 */
@Component
public class SaaSApplicationListener implements ServletContextListener {

    @Autowired
    JobScheduler jobScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String initQuartz = ConfigurationUtils.getConfigurationSafe(Constants.INIT_QUARTZ);
        if (Constants.LOWER_CASE_TRUE.equals(initQuartz)) {
            jobScheduler.scheduler();
        }
        ApplicationHandler.servletContext = sce.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
