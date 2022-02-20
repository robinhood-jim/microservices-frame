package com.robin.msf.config;



import com.robin.msf.handler.AuthExceptionEntryPoint;
import com.robin.msf.handler.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OauthConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RemoteTokenServices remoteTokenServices;
    @Autowired
    private Environment environment;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public AuthExceptionEntryPoint authExceptionEntryPoint(){
        return new AuthExceptionEntryPoint();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("oauth-service");
        resources.tokenStore(tokenStore()).accessDeniedHandler(accessDeniedHandler()).authenticationEntryPoint(authExceptionEntryPoint());
        remoteTokenServices.setRestTemplate(restTemplate);
        resources.tokenServices(remoteTokenServices);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] ignoreUrlArr={"/login","/logout","/error","/health","/swagger-ui.html","/swagger-ui/**", "/swagger**","/webjars/**","/v2/api-docs/**","/swagger-resources/**", "/captcha"};
        if(environment.containsProperty("login.ignoreUrls")){
            ignoreUrlArr=environment.getProperty("login.ignoreUrls").split(",");
        }
        http.authorizeRequests().antMatchers(ignoreUrlArr).permitAll().anyRequest().authenticated().and().headers().frameOptions().sameOrigin().and()
                .csrf().disable();

    }

    @Bean
    @Primary
    @DependsOn({"springContextHolder"})
    public TokenStore tokenStore(){
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123456");
        return converter;
    }
    @Bean
    @LoadBalanced
    public RestTemplate getTemplate(){
        return new RestTemplate();
    }

}
