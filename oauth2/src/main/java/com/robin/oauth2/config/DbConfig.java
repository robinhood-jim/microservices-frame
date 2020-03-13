package com.robin.oauth2.config;

import com.robin.core.base.dao.JdbcDao;
import com.robin.core.base.spring.SpringContextHolder;
import com.robin.core.query.util.QueryFactory;
import com.robin.core.sql.util.BaseSqlGen;
import com.robin.core.sql.util.MysqlSqlGen;
import com.robin.example.service.system.LoginService;
import com.robin.example.service.system.SysResourceService;
import com.robin.example.service.system.SysUserOrgService;
import com.robin.example.service.user.SysUserResponsiblityService;
import com.robin.example.service.user.SysUserService;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;


@Configuration
@EnableResourceServer
public class DbConfig {
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Value("${project.queryConfigPath}")
    private String queryConfigPath;

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "frame.datasource")
    public DataSource dataSource(){
        try {
            DataSource dataSource=DataSourceBuilder.create().build();
            return dataSource;
        }catch (Exception ex){
            logger.error("",ex);
        }
        return null;
    }
    @Bean(name = "oauthdataSource")
    @Qualifier("oauthdataSource")
    @Primary
    @DependsOn("springContextHolder")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSourceOauth(){
        DataSource dataSource=DataSourceBuilder.create().type(HikariDataSource.class).build();
        return dataSource;
    }

   /* @Bean
    protected AuthorizationCodeServices authorizationCodeServices(@Qualifier("oauthdataSource") DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);
    }*/
    @Bean(name="queryFactory")
    @Qualifier("queryFactory")
    public QueryFactory getQueryFactory(){
        QueryFactory factory=new QueryFactory();
        factory.setXmlConfigPath(queryConfigPath);
        return factory;
    }
    @Bean(name="lobHandler")
    @Qualifier("lobHandler")
    public LobHandler getLobHandler(){
        return new DefaultLobHandler();
    }

    @Bean(name = "sqlGen")
    @Qualifier("sqlGen")
    public BaseSqlGen getSqlGen(){
        return new MysqlSqlGen();
    }
    @Bean(name="jdbcDao")
    public JdbcDao getJdbcDao(@Qualifier("dataSource") DataSource dataSource, @Qualifier("sqlGen") BaseSqlGen sqlGen, @Qualifier("queryFactory") QueryFactory factory, @Qualifier("lobHandler") LobHandler lobhandler){
        JdbcDao dao=new JdbcDao(dataSource,lobhandler,factory,sqlGen);
        return dao;
    }


    @Bean(name="springContextHolder")
    @Lazy(false)
    public SpringContextHolder getHolder(){
        return new SpringContextHolder();
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
