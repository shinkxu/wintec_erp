package erp.chain.configurations;

import erp.chain.aspects.DataSourceRoutingAspect;
import erp.chain.common.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liuyandong on 2018-07-17.
 */
@Configuration
@ConditionalOnBean(name = {Constants.PRIMARY_DATA_SOURCE, Constants.SECONDARY_DATA_SOURCE, Constants.ROUTING_DATA_SOURCE})
public class ReadWriteSplittingConfiguration {
    @Bean
    public DataSourceRoutingAspect dataSourceRoutingAspect() {
        return new DataSourceRoutingAspect();
    }
}
