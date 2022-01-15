package com.robin.msf.filter;

import com.robin.msf.util.URIUtils;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {
    List<String> ignoreUrlList = null;
    List<String> ignoreResList = null;
    CacheManager cacheManager;
    Long accessExpireTs;
    private TokenStore tokenStore;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (ignoreResList == null) {
            Environment environment = exchange.getApplicationContext().getEnvironment();
            cacheManager = exchange.getApplicationContext().getBean(CacheManager.class);
            tokenStore=exchange.getApplicationContext().getBean(TokenStore.class);
            //int expireTs=environment.containsProperty("cookie.expireTs")?Integer.parseInt(environment.getProperty("cookie.expireTs")):0;
            String ignoreUrls = environment.getProperty("login.ignoreUrls");
            String ignoreResources = environment.getProperty("login.ignoreResources");
            ignoreUrlList = Arrays.asList(ignoreUrls.split(","));
            ignoreResList = Arrays.asList(ignoreResources.split(","));
            accessExpireTs = environment.getProperty("login.accessExpireTs") == null ? 30 * 60 * 1000 : Long.parseLong(environment.getProperty("login.accessExpireTs")) * 60000;
        }

        ServerHttpRequest request = exchange.getRequest();
        String requestPath = URIUtils.getRequestPath(request.getURI());
        String resPath = URIUtils.getRequestRelativePathOrSuffix(requestPath, request.getPath().contextPath().value());

        if (ignoreUrlList.contains(resPath) || ignoreResList.contains(resPath)) {
            log.info("request path {} pass!", resPath);
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
