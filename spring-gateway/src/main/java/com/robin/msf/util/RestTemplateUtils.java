package com.robin.msf.util;


import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class RestTemplateUtils {
    public static Map<String,Object> getResultFromRest(ApplicationContext context, String requestUrl, Object[] objects){
        Environment environment= context.getEnvironment();
        String url=environment.getProperty("backgroud.serverUrl")+requestUrl;
        return context.getBean(RestTemplate.class).getForEntity(url,Map.class,objects).getBody();
    }
    public static Map<String,Object> getResultFromSsoRest(ApplicationContext context,String requestUrl,Object[] objects){
        Environment environment= context.getEnvironment();
        String url=environment.getProperty("login.oauth2-uri")+requestUrl;
        return context.getBean(RestTemplate.class).getForEntity(url,Map.class,objects).getBody();
    }
    public static Map<String,Object> postFromSsoRest(ApplicationContext context,String requestUrl,Map<String,String> objectMap){
        Environment environment= context.getEnvironment();
        String url=environment.getProperty("login.oauth2-uri")+requestUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        Iterator<Map.Entry<String,String>> iter=objectMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=iter.next();
            params.add(entry.getKey(),entry.getValue());
        }
        HttpEntity entity=new HttpEntity<MultiValueMap<String,String>>(params,headers);
        return context.getBean(RestTemplate.class).postForEntity(url,entity,Map.class).getBody();
    }
    public static Map<String,Object> postFromRestUrl(ApplicationContext context,String requestUrl,Map<String,String> objectMap,String... configs){
        Environment environment= context.getBean(Environment.class);
        String url=null;
        if(configs!=null &&configs.length>0) {
            url=environment.getProperty(configs[0])+requestUrl;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if(configs!=null && configs.length>1){
            headers.set(HttpHeaders.AUTHORIZATION,configs[1]);
        }
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        Iterator<Map.Entry<String,String>> iter=objectMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=iter.next();
            params.add(entry.getKey(),entry.getValue());
        }
        HttpEntity entity=new HttpEntity<MultiValueMap<String,String>>(params,headers);
        return context.getBean(RestTemplate.class).postForEntity(url,entity,Map.class).getBody();
    }

    public static Object getResultByType(ApplicationContext context,String requestUrl, Map<String,String> objectMap, Class<?> tClass){
        RestTemplate template= context.getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        Iterator<Map.Entry<String,String>> iter=objectMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=iter.next();
            params.add(entry.getKey(),entry.getValue());
        }
        HttpEntity entity=new HttpEntity<MultiValueMap<String,String>>(params,headers);
        return template.postForEntity(getRequestGateWayPrefix(context)+requestUrl,entity,tClass.getClass()).getBody();
    }
    public static List<?> getResultListByType(ApplicationContext context,String requestUrl, Map<String,String> objectMap, Class<?> tClass){
        RestTemplate template= context.getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        Iterator<Map.Entry<String,String>> iter=objectMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=iter.next();
            params.add(entry.getKey(),entry.getValue());
        }
        HttpEntity entity=new HttpEntity<MultiValueMap<String,String>>(params,headers);
        return template.exchange(getRequestGateWayPrefix(context)+requestUrl, HttpMethod.POST,entity,new ParameterizedTypeReference<List<?>>(){}).getBody();
    }
    public static Map<String,Object> deleteByUrl(ApplicationContext context,String deleteUrl,Map<String,String> objectMap){
        RestTemplate template= context.getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        Iterator<Map.Entry<String,String>> iter=objectMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=iter.next();
            params.add(entry.getKey(),entry.getValue());
        }
        HttpEntity entity=new HttpEntity<MultiValueMap<String,String>>(params,headers);
        return template.exchange(deleteUrl, HttpMethod.DELETE,entity,Map.class).getBody();
    }
    public static String getRequestGateWayPrefix(ApplicationContext context){
        Environment environment= context.getEnvironment();
        return environment.getProperty("backgroud.serverUrl");
    }
    public static Map<String, Object> refreshToken(ServerWebExchange exchange, String token) {
        Map<String, String> vMap = new HashMap<>();
        Environment environment = exchange.getApplicationContext().getEnvironment();
        vMap.put("grant_type", "refresh_token");
        vMap.put("refresh_token", token);
        vMap.put("client_id", environment.getProperty("login.clientId"));
        vMap.put("client_secret", environment.getProperty("login.clientSecret"));
        return RestTemplateUtils.postFromSsoRest(exchange.getApplicationContext(),"oauth/token", vMap);
    }
}
