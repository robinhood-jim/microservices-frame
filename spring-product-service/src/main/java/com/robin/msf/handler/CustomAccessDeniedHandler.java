package com.robin.msf.handler;

import com.robin.basis.comm.CommonConstant;
import com.robin.msf.comm.utils.HttpResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        httpServletResponse.setStatus(HttpStatus.OK.value());
        map.put("code", CommonConstant.UNAUTHRIZED);
        map.put("msg","没有权限");
        HttpResponseUtils.sendMessage(httpServletResponse, map);
    }
}
