package com.robin.msf.controller;

import com.google.gson.Gson;
import com.robin.msf.util.RestTemplateUtils;
import com.robin.msf.util.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
public class LoginContorller {
    @Autowired
    private Environment environment;
    private Gson gson = new Gson();
    @Autowired
    protected CacheManager manager;

    @GetMapping("/login")
    String loginPage(ServerWebExchange exchange, final Model model) {
        if (exchange.getRequest().getQueryParams().containsKey("redirect_url")) {
            model.addAttribute("redirectUrl", exchange.getRequest().getQueryParams().getFirst("redirect_url"));
        }
        if(exchange.getRequest().getCookies().getFirst("authCode")!=null){
            String code=exchange.getRequest().getCookies().getFirst("authCode").getValue();

            if (manager.getCache("accessKey").get(code) != null) {
                UserParams params = new UserParams(manager.getCache("accessKey").get(code).get().toString().split("\\|"));
                if(validateAccessToken(exchange,params)) {
                    Map<String, Object> retMap = RestTemplateUtils.getResultFromRest(exchange.getApplicationContext(), "getSession?userId={1}&orgId={2}", new Object[]{params.getUserId(), ""});
                    if (retMap.get("success").equals(true)) {
                        return "redirect:" + getMainUrl(exchange.getRequest().getQueryParams().getFirst("redirect_url"), "index");
                    }
                }
            }
        }
        return "/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Mono<Map> ssoLogin(ServerWebExchange exchange, final UserInfo userInfo) {
        Map<String, Object> retMap = new HashMap<>();
        try {
            String accountName = userInfo.getAccountName();
            String password = userInfo.getPassword();
            Map<String, String> vMap = new HashMap<>();
            vMap.put("username", accountName);
            vMap.put("password", password);
            vMap.put("grant_type", "password");
            vMap.put("client_id", environment.getProperty("login.clientId"));
            vMap.put("client_secret", environment.getProperty("login.clientSecret"));
            Map<String, Object> map = RestTemplateUtils.postFromSsoRest(exchange.getApplicationContext(), "oauth/token", vMap);
            if (map.containsKey("access_token")) {
                //function as /oauth/authorize return a random authorize code to client to authorize
                String authorizeCode = StringUtils.genarateRandomUpperLowerChar(8);
                Map<String, Object> rightMap = RestTemplateUtils.getResultFromSsoRest(exchange.getApplicationContext(), "sso/getuserright?access_token={1}", new Object[]{map.get("access_token")});
                Map<String, Object> sessionMap = (Map<String, Object>) rightMap.get("session");
                if (sessionMap.get("accountType").toString().equals(ACCOUNT_TYPE.ORGUSER.toString()) || !sessionMap.containsKey("orgId")) {
                    retMap.put("selectOrg", true);
                }
                //map.get("access_token").toString() + "|" + map.get("refresh_token").toString()+"|" + sessionMap.get("userId").toString()
                manager.getCache("accessKey").put(authorizeCode, new UserParams(map.get("access_token").toString(), map.get("refresh_token").toString(), sessionMap.get("userId").toString()).getRedisValue());
                ResponseCookie cookie= ResponseCookie.from("authCode",authorizeCode).path(exchange.getRequest().getPath().contextPath().value()+"/").build();
                exchange.getResponse().addCookie(cookie);
                retMap.put("code", authorizeCode);
                retMap.put("success", true);
                retMap.put("userId", sessionMap.get("userId").toString());
            }
        } catch (Exception ex) {
            retMap.put("success", false);
            retMap.put("message", ex.getMessage());
        }
        return Mono.create(sink -> sink.success(retMap));
    }


    @RequestMapping("/refreshToken")
    @ResponseBody
    public Mono refreshToken(ServerWebExchange exchange) {
        if (exchange.getRequest().getQueryParams().containsKey("code")) {
            if (manager.getCache("accessKey").get(exchange.getRequest().getQueryParams().getFirst("code")) != null) {
                UserParams params = new UserParams(manager.getCache("accessKey").get(exchange.getRequest().getQueryParams().getFirst("code")).get().toString().split("\\|"));
                Map<String, Object> retMap = new HashMap<>();
                if(validateAccessToken(exchange,params)) {
                    retMap.put("success", true);
                }else{
                    retMap.put("success", false);
                }
                return Mono.create(sink -> sink.success(retMap));
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @RequestMapping(value = "/getSession", method = RequestMethod.POST)
    @ResponseBody
    public Mono getSession(ServerWebExchange exchange, final SessionReq req) {
        if (req.getCode() != null) {
            if (manager.getCache("accessKey").get(req.getCode()) != null) {
                UserParams params = new UserParams(manager.getCache("accessKey").get(req.getCode()).get().toString().split("\\|"));
                Map<String, Object> retMap = RestTemplateUtils.getResultFromRest(exchange.getApplicationContext(), "getSession?userId={1}&orgId={2}", new Object[]{params.getUserId(), req.getOrgId()});
                retMap.put("userId", params.getUserId());
                return Mono.create(sink -> sink.success(retMap));
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @RequestMapping("/validateCode")
    @ResponseBody
    public Mono validateSession(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");

        if (code != null && !code.isEmpty()) {
            if (manager.getCache("accessKey").get(code) != null) {
                UserParams params = new UserParams(manager.getCache("accessKey").get(code).get().toString().split("\\|"));
                if(validateAccessToken(exchange,params)) {
                    Map<String, Object> retMap = RestTemplateUtils.getResultFromSsoRest(exchange.getApplicationContext(), "users/current?access_token={1}", new Object[]{params.getAccessToken()});
                    return Mono.create(sink -> sink.success(retMap));
                }
            }

        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @RequestMapping("/listorg")
    @ResponseBody
    public Mono listUserOrg(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getQueryParams().getFirst("userId");
        Map<String, Object> retMap = RestTemplateUtils.getResultFromRest(exchange.getApplicationContext(), "system/user/listorg/{1}", new Object[]{userId});
        List<Map<String, Object>> options = (List<Map<String, Object>>) retMap.get("options");
        List<Map<String, Object>> retOptions = new ArrayList<>();
        for (Map<String, Object> tmpMap : options) {
            Map<String, Object> tmpMap1 = new HashMap<>();
            tmpMap1.put("value", tmpMap.get("value"));
            tmpMap1.put("content", tmpMap.get("text"));
            retOptions.add(tmpMap1);
        }
        retMap.put("options", retOptions);
        return Mono.create(sink -> sink.success(retMap));

    }
    @RequestMapping("/logout")
    @ResponseBody
    public Mono logOut(ServerWebExchange exchange){
        String code=exchange.getRequest().getQueryParams().getFirst("code");
        if(code!=null && !code.isEmpty()){
            if (manager.getCache("accessKey").get(code) != null) {
                UserParams params = new UserParams(manager.getCache("accessKey").get(code).get().toString().split("\\|"));
                Map<String, Object> retMap = RestTemplateUtils.getResultFromSsoRest(exchange.getApplicationContext(), "oauth/revoke-token?accessToken={1}&refreshToken={2}", new Object[]{params.getAccessToken(),params.getRefreshToken()});
                manager.getCache("accessKey").evict(code);
                exchange.getResponse().getCookies().set("authCode",ResponseCookie.from("authCode","").maxAge(0).build());
                return Mono.create(sink->sink.success(retMap));
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return exchange.getResponse().setComplete();
    }
    private String getMainUrl(String responseUrl,String mainPage){
        String tmpPath=responseUrl;
        int pos=tmpPath.lastIndexOf("/");
        return  tmpPath.substring(0,pos+1)+mainPage;
    }
    private boolean validateAccessToken(ServerWebExchange exchange,UserParams params){
        boolean validateOk=false;
        try{
            //validate access_token
            RestTemplateUtils.getResultFromSsoRest(exchange.getApplicationContext(), "users/current?access_token={1}", new Object[]{params.getAccessToken()});
            validateOk=true;
        }catch (Exception ex){

        }
        if(!validateOk){
            //if encounter unauthorized error,user refreshToken to refresh
            try{
                Map<String, Object> retMap = RestTemplateUtils.refreshToken(exchange, params.getRefreshToken());
                manager.getCache("accessKey").put(exchange.getRequest().getQueryParams().getFirst("code"), new UserParams(
                        new String[]{retMap.get("access_token").toString(), params.getRefreshToken(), params.getUserId()}).getRedisValue());
                params.setAccessToken(retMap.get("access_token").toString());
                validateOk=true;
            }catch (Exception ex){

            }
        }
        return validateOk;
    }


    @Data
    public static class UserParams {
        String accessToken;
        String refreshToken;
        String userId;

        public UserParams(String[] arrs) {
            this.accessToken = arrs[0];
            this.refreshToken = arrs[1];
            this.userId = arrs[2];
        }

        public UserParams(String accessToken, String refreshToken, String userId) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
        }

        public String getRedisValue() {
            return org.apache.commons.lang.StringUtils.join(Arrays.asList(new String[]{accessToken, refreshToken, userId}), "|");
        }
    }

    public enum ACCOUNT_TYPE {
        SYSUSER(1L),  //System User,can operator system menu
        ORGUSER(2L),  // Org User,can access Org right menu and menu Assign by system user
        FREEUSER(3L); // User not include in Any Org
        private Long value;

        private ACCOUNT_TYPE(Long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public Long getValue() {
            return value;
        }
    }

    @Data
    public static class UserInfo {
        private String accountName;
        private String password;

    }

    @Data
    public static class SessionReq {
        private String userId;
        private String orgId;
        private String accessToken;
        private String code;
    }
}
