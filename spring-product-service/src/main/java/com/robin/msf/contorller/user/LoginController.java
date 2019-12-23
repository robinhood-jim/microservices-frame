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

import com.robin.core.base.util.Const;
import com.robin.core.web.controller.BaseController;
import com.robin.core.web.util.Session;
import com.robin.example.service.system.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController extends BaseController {
    @Autowired
    private LoginService loginService;


    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, @RequestParam String accountName, @RequestParam String password) {
        Map<String, Object> map = new HashMap();
        try {
            Session session = this.loginService.doLogin(accountName, password.toUpperCase());
            wrapSuccess(map);
            map.put("session", session);
        } catch (Exception ex) {
            wrapFailed(map, ex);
        }
        return map;
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