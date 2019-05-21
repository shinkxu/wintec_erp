package erp.chain.configurations;

import com.alibaba.druid.pool.DruidDataSource;
import erp.chain.common.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfiguration{
    @Bean(name = Constants.PRIMARY_DATA_SOURCE)
    @ConfigurationProperties(prefix = "primary.datasource")
    @ConditionalOnProperty(value = {"primary.datasource.url", "secondary.datasource.url"})
    public DataSource primaryDataSource(){
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = Constants.SECONDARY_DATA_SOURCE)
    @ConfigurationProperties(prefix = "secondary.datasource")
    @ConditionalOnProperty(value = {"primary.datasource.url", "secondary.datasource.url"})
    public DataSource secondaryDataSource(){
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = Constants.ROUTING_DATA_SOURCE)
    @Primary
    @ConditionalOnProperty(value = {"primary.datasource.url", "secondary.datasource.url"})
    public RoutingDataSource routingDataSource(){
        RoutingDataSource routingDataSource = new RoutingDataSource();
        DataSource primaryDataSource = primaryDataSource();
        DataSource secondaryDataSource = secondaryDataSource();
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        targetDataSources.put("primaryDataSource", primaryDataSource);
        targetDataSources.put("secondaryDataSource", secondaryDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        return routingDataSource;
    }
}
