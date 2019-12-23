package com.robin.feign;

import com.robin.msf.feign.UserFallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@FeignClient(value = "test-product-service",fallback = UserFallbackService.class)
public interface UserService {
    @RequestMapping(value = "/system/user/get/{userId}",method = RequestMethod.GET)
    Map<String,Object> queryByUserId(@PathVariable(value = "userId") Long userId);
    @RequestMapping("/system/user/login")
    Map<String,Object> doLogin(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password);
    @RequestMapping("/system/user/list")
    Map<String,Object> listUser();
}
