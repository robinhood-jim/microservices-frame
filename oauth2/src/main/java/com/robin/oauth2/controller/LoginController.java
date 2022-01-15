package com.robin.oauth2.controller;


import com.robin.core.web.util.Session;
import com.robin.example.service.system.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@Controller
public class LoginController {
    @Autowired
    private ResourceBundleMessageSource messageSource;
    @Autowired
    private LoginService loginService;
    @Autowired
    private TokenStore tokenStore;
    @PreAuthorize("permitAll()")
    @RequestMapping("/login")
    public String login(){
        return "oauth_login";
    }
    @GetMapping("/health")
    @ResponseBody
    @PreAuthorize("permitAll()")
    public String health(){
        return "OK";
    }
    @RequestMapping("/sso/getuserright")
    @PreAuthorize("permitAll()")
    @ResponseBody
    public Map<String,Object> ssoGetUser(HttpServletRequest request,Principal principal){
        Map<String, Object> map = new HashMap();
        try {
            Session session = loginService.ssoGetUser(((OAuth2Authentication)principal).getPrincipal().toString());
            map.put("success",true);

            map.put("session", session);
        }catch (Exception ex){
            map.put("success",false);
            map.put("session", ex.getMessage());
        }
        return map;
    }
    @RequestMapping("/oauth/revoke-token")
    @ResponseBody
    public Map<String,Object> logOut(HttpServletRequest request){
        Map<String, Object> map = new HashMap();
        String token=request.getParameter("accessToken");
        String rToken=request.getParameter("refreshToken");
        map.put("success",false);
        try {
            if(token!=null){
                OAuth2AccessToken accessToken=tokenStore.readAccessToken(token);
                if(accessToken!=null){
                    tokenStore.removeAccessToken(accessToken);
                    OAuth2RefreshToken refreshToken=tokenStore.readRefreshToken(rToken);
                    if(refreshToken!=null){
                        tokenStore.removeRefreshToken(refreshToken);
                    }
                    map.put("success",true);
                }
            }
        }catch (Exception ex){
            map.put("success",false);
            map.put("session", ex.getMessage());
        }
        return map;
    }
    @RequestMapping("/token-validate")
    @ResponseBody
    public Map<String,Object> tokenValidate(HttpServletRequest request){
        Map<String, Object> map = new HashMap();
        String token=request.getParameter("accessToken");
        if(token!=null && !token.isEmpty()){
            OAuth2AccessToken accessToken=tokenStore.readAccessToken(token);
            if(accessToken!=null){
                map.put("success",true);
            }else{
                map.put("success",false);
            }
        }else{
            map.put("success",false);
        }
        return map;
    }
}
