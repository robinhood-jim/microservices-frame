package com.robin.msf.handler;


import com.robin.core.base.util.MessageUtils;

import com.robin.msf.comm.utils.HttpResponseUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Map<String,Object> map=new HashMap<>();
        httpServletResponse.setStatus(HttpStatus.OK.value());

        if(e instanceof InsufficientAuthenticationException){
            if(NumberUtils.isCreatable(e.getMessage())){
                int code=Integer.parseInt(e.getMessage());
                map.put("code",code);
                map.put("msg","");
            }
        }else if(e instanceof UsernameNotFoundException){
            map.put("code", 500);
            map.put("msg", "");
        }else if(e instanceof AccessDeniedHandler){
            map.put("code", 500);
            map.put("msg","");
        }
        if(!map.containsKey("code")) {
            map.put("code", 401);
            map.put("msg", "");
        }
        HttpResponseUtils.sendMessage(httpServletResponse,map);
    }
}
