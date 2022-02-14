/*
 * Copyright (c) 2015,robinjim(robinjim@126.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robin.msf.contorller.user;

import com.robin.basis.service.system.LoginService;
import com.robin.core.web.controller.AbstractController;
import com.robin.core.web.util.Session;
import com.robin.msf.comm.utils.SecurityContextUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController extends AbstractController {
    @Resource
    private LoginService loginService;
    @Resource
    private Environment environment;


    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String,Object> requestMap) {
        Map<String, Object> map =new HashMap<>();
        try {
            Assert.notNull(requestMap.get("username"),"userName required");
            Assert.notNull(requestMap.get("password"),"password required");
            Map<String,String> vMap=new HashMap<>();
            vMap.put("username",requestMap.get("username").toString());
            vMap.put("password",requestMap.get("password").toString());
            vMap.put("grant_type","password");
            vMap.put("client_id",environment.getProperty("login.clientId"));
            vMap.put("client_secret",environment.getProperty("login.clientSecret"));
            map = this.loginService.ssoLogin(environment.getProperty("login.oauth2-uri")+"/oauth/token", vMap);
            wrapSuccess(map);
        } catch (Exception ex) {
            wrapFailed(map, ex);
        }
        return map;
    }
    @GetMapping("/user/info")
    public Map<String,Object> userInfo(HttpServletRequest request){
        return SecurityContextUtils.getLoginInfo();
    }

    @RequestMapping(value = "/getSession")
    @ResponseBody
    public Map<String, Object> getSession(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<>();
        try {
            Session session = loginService.ssoGetUserById(Long.valueOf(request.getParameter("userId")));
            if(request.getParameter("orgId")!=null && !request.getParameter("orgId").isEmpty()) {
                session.setOrgId(Long.valueOf(request.getParameter("orgId")));
            }
            loginService.getRights(session);
            retMap.put("session",session);
            wrapSuccess(retMap);
        } catch (Exception ex) {
            wrapFailed(retMap, ex);
        }
        return retMap;
    }


}