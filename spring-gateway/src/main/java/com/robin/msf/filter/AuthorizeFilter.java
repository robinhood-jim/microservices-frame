package com.robin.msf.filter;

import com.robin.msf.util.URIUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {
    List<String> ignoreUrlList = null;
    List<String> ignoreResList = null;
    List<String> regexList=null;
    CacheManager cacheManager;
    Long accessExpireTs;
    private TokenStore tokenStore;
    Map<String, Pattern> regexMap=new HashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (ignoreResList == null) {
            Environment environment = exchange.getApplicationContext().getEnvironment();
            cacheManager = exchange.getApplicationContext().getBean(CacheManager.class);
            tokenStore=exchange.getApplicationContext().getBean(TokenStore.class);
            //int expireTs=environment.containsProperty("cookie.expireTs")?Integer.parseInt(environment.getProperty("cookie.expireTs")):0;
            String ignoreUrls = environment.getProperty("login.ignoreUrls");
            String ignoreResources = environment.getProperty("login.ignoreResources");
            List<String> allList=Arrays.asList(ignoreUrls.split(","));
            ignoreUrlList = allList.stream().filter(f->!f.contains("*")).collect(Collectors.toList());
            regexList=allList.stream().filter(f->f.contains("*")).collect(Collectors.toList());
            ignoreResList = Arrays.asList(ignoreResources.split(","));
            accessExpireTs = environment.getProperty("login.accessExpireTs") == null ? 30 * 60 * 1000 : Long.parseLong(environment.getProperty("login.accessExpireTs")) * 60000;
            if(!CollectionUtils.isEmpty(regexList)){
                regexList.forEach(f->regexMap.put(f,Pattern.compile(f)));
            }
        }

        ServerHttpRequest request = exchange.getRequest();
        String requestPath = URIUtils.getRequestPath(request.getURI());
        String nameExtension = URIUtils.getRequestRelativePathOrSuffix(requestPath, request.getPath().contextPath().value());
        String fileName= FilenameUtils.getName(request.getURI().getPath());

        if (ignoreUrlList.contains(fileName) || ignoreUrlList.contains(nameExtension)  || ignoreResList.contains(nameExtension) || matchVerify(request.getURI().getPath())) {
            log.info("request path {} pass!", request.getURI().getPath());
            return chain.filter(exchange);
        } else {
            if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String tokenValue = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (tokenValue!=null && !StringUtils.isEmpty(tokenValue)) {
                    log.info("get token= {}",tokenValue);
                    //jwt decode every request must have valid token
                    OAuth2AccessToken auth2AccessToken=tokenStore.readAccessToken(getToken(tokenValue));
                    if(auth2AccessToken!=null)
                        return chain.filter(exchange);
                }

            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    private boolean matchVerify(String path){
        if(!CollectionUtils.isEmpty(regexMap) && !CollectionUtils.isEmpty(regexList)){
            for (String regex : regexList) {
                if (regexMap.get(regex).matcher(path).find()) {
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
    private String getToken(String tokenValue){
        if(tokenValue.startsWith("Bearer")){
            return tokenValue.substring(7);
        }else {
            return tokenValue;
        }
    }
}
