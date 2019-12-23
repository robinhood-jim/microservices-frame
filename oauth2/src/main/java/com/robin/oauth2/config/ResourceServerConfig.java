package com.robin.oauth2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
@Order(2)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Value("${login.ignoreUrls}")
    private String ignoreUrls;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] ignoreUrlArr=ignoreUrls.split(",");
        http.authorizeRequests().antMatchers(ignoreUrlArr).permitAll().and().authorizeRequests().anyRequest().authenticated();
    }

}
