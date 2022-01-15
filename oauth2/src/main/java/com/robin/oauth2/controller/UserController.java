package com.robin.oauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Map<String,Object> getUser(Principal principal) {
        Map<String,Object> map=new HashMap<>();
        map.put("user",((OAuth2Authentication)principal).getPrincipal());
        map.put("auth",((OAuth2Authentication)principal).getAuthorities());
        return map;
    }
    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(value = "/extra")
    public Map<String,Object> getExtraInfo(Authentication auth){
        OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
        Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();
        return details;
    }

}

