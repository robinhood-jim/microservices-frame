package com.robin.oauth2.config;

import com.robin.oauth2.comm.OauthConstant;
import com.robin.oauth2.service.JdbcUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;


@Configuration
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private String ignoreUrls;
    @Autowired
    private Environment environment;
    @Autowired
    private JdbcUserDetailService jdbcUserDetailService;

    /*@Override
    public void configure(WebSecurity web) throws Exception {
        String[] ignoreUrlArr=ignoreUrls.split(",");
        web.ignoring().antMatchers(ignoreUrlArr);
    }*/

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                String encode = DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
                boolean res = s.equals( encode );
                return res;
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(environment.containsProperty("resaccess.ignoreUrls")){
            ignoreUrls=environment.getProperty("resaccess.ignoreUrls");
        }else{
            ignoreUrls= OauthConstant.DEFAULT_IGONOREURLS;
        }
        String[] ignoreUrlArr=ignoreUrls.split(",");
        http.requestMatchers().antMatchers("/oauth/**").and().authorizeRequests().antMatchers(ignoreUrlArr).permitAll().anyRequest().authenticated().and().csrf();
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManager();
    }


}
