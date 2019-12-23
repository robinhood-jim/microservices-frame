package com.robin.msf.controller;

import com.robin.feign.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping("/system/user/list")
    @ResponseBody
    public Map<String, Object> listuser() {
        return userService.listUser();
    }
    @RequestMapping("/system/user/login")
    @ResponseBody
    public Map<String, Object> doLogin(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password) {
        return userService.doLogin(userName,password);
    }

}
