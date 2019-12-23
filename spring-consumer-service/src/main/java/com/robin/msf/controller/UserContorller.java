package com.robin.msf.controller;

import com.robin.feign.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class UserContorller {
    @Autowired
    UserService userService;

    @RequestMapping("/system/user/get/{userId}")
    @ResponseBody
    public Map<String, Object> getUser(@PathVariable Long userId) {
        return userService.queryByUserId(userId);
    }
}
