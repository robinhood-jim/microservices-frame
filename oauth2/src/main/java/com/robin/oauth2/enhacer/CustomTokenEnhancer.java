package com.robin.oauth2.enhacer;

import com.robin.basis.service.system.LoginService;
import com.robin.core.base.spring.SpringContextHolder;
import com.robin.core.web.util.Session;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        String userName=oAuth2Authentication.getUserAuthentication().getName();
        LoginService loginService= SpringContextHolder.getBean(LoginService.class);
        Session session=loginService.ssoGetUser(userName);
        Map<String,Object> additionalMap=new HashMap<>();
        additionalMap.put("userId",session.getUserId());
        additionalMap.put("userName",session.getUserName());
        additionalMap.put("orgName",session.getOrgName());
        additionalMap.put("orgCode",session.getOrgCode());
        additionalMap.put("email",session.getEmail());
        additionalMap.put("mobileNo",session.getMoblie());
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalMap);
        return oAuth2AccessToken;
    }
}
