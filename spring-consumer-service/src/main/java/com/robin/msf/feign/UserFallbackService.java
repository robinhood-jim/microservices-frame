package com.robin.msf.feign;

import com.robin.feign.UserService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class UserFallbackService implements UserService {

    @Override
    public Map<String, Object> queryByUserId(Long userId) {
        Map<String,Object> map=new HashMap<>();
        map.put("success",false);
        map.put("message",userId+" not exists!");
        return map;
    }

    @Override
    public Map<String, Object> doLogin(String userName, String password) {
        Map<String,Object> map=new HashMap<>();
        map.put("success",false);
        map.put("message","userName or password incorrect!");
        return map;
    }
}
