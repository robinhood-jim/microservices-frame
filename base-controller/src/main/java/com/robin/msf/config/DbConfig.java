package com.robin.msf.config;

import com.robin.core.base.dao.JdbcDao;
import com.robin.core.base.spring.SpringContextHolder;
import com.robin.core.base.util.MessageUtils;
import com.robin.core.query.util.QueryFactory;
import com.robin.core.sql.util.BaseSqlGen;
import com.robin.core.sql.util.MysqlSqlGen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DbConfig {
    private Logger logger= LoggerFactory.getLogger(getClass());
    @Value("${core.url}")
    private String coreurl;
    @Value("${core.driver-class-name}")
    private String coredriverClassName;
    @Value("${core.username}")
    private String coreuserName;
    @Value("${core.password}")
    private String corepassword;
    @Value("${core.type}")
    private String coretype;
    @Value("${project.queryConfigPath}")
    private String queryConfigPath;


    @Bean(name = "dataSource")
    @Qualifier("dataSource")
    @Primary
    @DependsOn("springContextHolder")
    public DataSource dataSource(){
        try {
            return DataSourceBuilder.create().type((Class<? extends DataSource>) Class.forName(coretype)).url(coreurl).driverClassName(coredriverClassName).username(coreuserName).password(corepassword).build();
        }catch (Exception ex){
            logger.error("",ex);
        }
        return null;
    }
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
        return MysqlSqlGen.getInstance();
    }

    @Bean(name="springContextHolder")
    @Lazy(false)
    public SpringContextHolder getHolder(){
        return new SpringContextHolder();
    }
    /**
     * DependsOn is required,Otherwise springContextHolder may not initialize
     * @return
     */
    @Bean(name="jdbcDao")
    public JdbcDao getJdbcDao(@Qualifier("dataSource") DataSource dataSource, @Qualifier("sqlGen") BaseSqlGen sqlGen, @Qualifier("queryFactory") QueryFactory factory, @Qualifier("lobHandler") LobHandler lobhandler){
        JdbcDao dao=new JdbcDao(dataSource,lobhandler,factory,sqlGen);
        return dao;
    }
    @Bean
    public MessageUtils getMessageUtils(){
        return new MessageUtils();
    }
    @Bean
    @Primary
    public ResourceBundleMessageSource getMessageSource(){
        ResourceBundleMessageSource messageSource=new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
    @Bean
    BeanPostProcessor getBeanPostProcesser(final BeanFactory beanFactory){
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if(bean instanceof RestTemplate){
                    RestTemplate restTemplate = (RestTemplate) bean;
                    List<ClientHttpRequestInterceptor> interceptors =
                            new ArrayList<>(restTemplate.getInterceptors());
                    //interceptors.add(0, getIntercept());
                    restTemplate.setInterceptors(interceptors);
                }
                return bean;
            }
            /*ClientHttpRequestInterceptor getIntercept(){
                return new RestRequestLimitInterceptor();
            }*/
        };
    }




}
