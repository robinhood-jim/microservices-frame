package com.robin.oauth2.controller;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Map<String,Object> getUser(Principal principal) {
        Map<String,Object> map=new HashMap<>();
        map.put("user",((OAuth2Authentication)principal).getPrincipal());
        map.put("auth",((OAuth2Authentication)principal).getAuthorities());
        return map;
    }

}

