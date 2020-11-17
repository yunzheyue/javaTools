package com.example.demo.mybatisplus;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class DruidConfig {
    @Value("${spring.datasource.bn-lbs.url}")
    private String dbUrl;

    @Value("${spring.datasource.bn-lbs.username}")
    private String username;

    @Value("${spring.datasource.bn-lbs.password}")
    private String password;

    @Value("${spring.datasource.bn-lbs.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.bn-lbs.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.bn-lbs.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.bn-lbs.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.bn-lbs.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.bn-lbs.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.bn-lbs.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.bn-lbs.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.bn-lbs.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.bn-lbs.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.bn-lbs.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.bn-lbs.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.bn-lbs.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.bn-lbs.filters}")
    private String filters;

    @Value("${spring.datasource.bn-lbs.connectionProperties}")
    private String connectionProperties;
    @Autowired
    WallFilter wallFilter;

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> reg = new ServletRegistrationBean<StatViewServlet>();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", "admin");
        reg.addInitParameter("loginPassword", "admin");
        reg.addInitParameter("logSlowSql", "true");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<WebStatFilter>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Primary
    @Bean(name = "datasource")
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        List<Filter> filterList = new ArrayList<>();
        filterList.add(wallFilter);
        datasource.setProxyFilters(filterList);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
        }
        datasource.setConnectionProperties(connectionProperties);

        return datasource;
    }

    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate createJdbcTemplate(@Qualifier("datasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "wallFilter")
    @DependsOn("wallConfig")
    public WallFilter wallFilter(WallConfig wallConfig) {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        return wallFilter;
    }

    @Bean(name = "wallConfig")
    public WallConfig wallConfig() {
        //允许一次执行多条语句
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        //允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);
        return wallConfig;
    }

}