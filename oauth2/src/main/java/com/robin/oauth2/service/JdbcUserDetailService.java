package com.robin.oauth2.service;

import com.robin.example.service.system.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class JdbcUserDetailService implements UserDetailsService {
    @Autowired
    private LoginService loginService;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Map<String,Object> map= loginService.getUserAndResp(userName);
        if(map!=null){
            List<GrantedAuthority> grants=new ArrayList<>();
            if(map.containsKey("resps")){
                String[] respArr=map.get("resps").toString().split(",");
                for(String resp:respArr){
                    grants.add(new SimpleGrantedAuthority(resp));
                }
            }
            UserDetails user= new User(map.get("accountName").toString(),map.get("password").toString().toLowerCase(),grants);
            return user;
        }else{
            throw new UsernameNotFoundException("Invalid username and password");
        }
    }
}
