package com.robin.msf.comm.utils;

import com.robin.basis.vo.LoginUserVO;
import com.robin.core.base.spring.SpringContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


public class SecurityContextUtils {
    private SecurityContextUtils(){

    }
    public static LoginUserVO getLoginUserVO(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Long userId = null;
        Long tenantId = null;
        String userName = null;
        LoginUserVO vo=new LoginUserVO();
        if(Objects.nonNull(authentication)){
            String tokenValue=((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
            OAuth2AccessToken token= SpringContextHolder.getBean(TokenStore.class).readAccessToken(tokenValue);
            if(!CollectionUtils.isEmpty(token.getAdditionalInformation())){
                Map<String,Object> additionalMap=token.getAdditionalInformation();
                if (additionalMap.containsKey("userId")) {
                    userId = Long.valueOf(additionalMap.get("userId").toString());
                }

                if (additionalMap.containsKey("tenantId")) {
                    tenantId = Long.valueOf(additionalMap.get("tenantId").toString());
                }
                if(additionalMap.containsKey("userName")){
                    userName=additionalMap.get("userName").toString();
                }
            }
        }
        vo.setUserId(userId);
        vo.setTenantId(tenantId);
        vo.setUserName(userName);
        return vo;
    }
    public static Map<String, Object> getLoginUserMap() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication.getPrincipal() instanceof HashMap) {
            Map<String, Object> userDetail = (Map<String, Object>) authentication.getPrincipal();
            return (Map<String, Object>) userDetail.get("principal");
        } else {
            return null;
        }
    }
    public static Map<String, Object> getLoginInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails auth2AuthenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String, Object> map = SpringContextHolder.getBean(TokenStore.class).readAccessToken(auth2AuthenticationDetails.getTokenValue()).getAdditionalInformation();
        return map;
    }

    public static List<String> getPermission() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
    public static Map<String, Object> getAdditionalInfo() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        OAuth2Authentication authentication = (OAuth2Authentication) securityContext.getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        TokenStore tokenStore = SpringContextHolder.getBean(TokenStore.class);
        return tokenStore.readAccessToken(details.getTokenValue()).getAdditionalInformation();
    }
    public static List<String> getUserRoles() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        List<String> roles =Collections.emptyList();
        if (null != authentication && !CollectionUtils.isEmpty(authentication.getAuthorities())) {
            roles=authentication.getAuthorities().stream().filter(f->f.getAuthority().startsWith("ROLE_")).map(f->f.getAuthority().substring(5)).collect(Collectors.toList());
        }
        return roles;
    }



}
