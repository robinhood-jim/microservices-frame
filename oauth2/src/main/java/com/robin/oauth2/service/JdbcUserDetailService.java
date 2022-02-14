package com.robin.oauth2.service;

import com.robin.basis.model.user.SysUser;
import com.robin.basis.service.system.LoginService;
import com.robin.core.base.util.StringUtils;
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


public class JdbcUserDetailService implements UserDetailsService {
    @Autowired
    private LoginService loginService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        SysUser sysUser=loginService.getUserInfo(userName);
        if(sysUser!=null){
            List<GrantedAuthority> grants=new ArrayList<>();
            Map<String,Object> resMap=loginService.getUserRights(sysUser.getId());
            List<String> roles=(List<String>)resMap.get("roles");
            roles.forEach(f->{
                grants.add(new SimpleGrantedAuthority("ROLE_"+f));
            });
            if(resMap.containsKey("permission")){
                Map<String,Map<String,Object>> accessResMap=(Map<String,Map<String,Object>>)resMap.get("permission");
                accessResMap.forEach((k,v)->{
                    if(v.containsKey("permission") && !StringUtils.isEmpty(v.get("permission")))
                        grants.add(new SimpleGrantedAuthority(v.get("permission").toString()));
                });
            }
            User.UserBuilder builder=User.withUsername(sysUser.getUserAccount()).password(sysUser.getUserPassword())
                    .authorities(grants);
            UserDetails user= builder.build();
            return user;
        }else{
            throw new UsernameNotFoundException("Invalid username and password");
        }
    }
}
