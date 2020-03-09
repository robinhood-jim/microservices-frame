package com.robin.oauth2.config;

import com.robin.oauth2.comm.OauthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;


@Configuration
@EnableResourceServer
//@Order(2)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment environment;
    private String ignoreUrls;
    @Autowired
    private TokenStore tokenStore;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        if(environment.containsProperty("resaccess.ignoreUrls")){
            ignoreUrls=environment.getProperty("resaccess.ignoreUrls");
        }else{
            ignoreUrls= OauthConstant.DEFAULT_IGONOREURLS;
        }
        String[] ignoreUrlArr=ignoreUrls.split(",");
        http.authorizeRequests().antMatchers(ignoreUrlArr).permitAll().and().authorizeRequests().anyRequest().authenticated().and().logout().and().cors().and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("oauth-service");
        resources.tokenStore(tokenStore);
    }
}
