package com.robin.msf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class StatusController {


    @Autowired
    private LoadBalancerClient loadBalancerClient;


    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;



    @GetMapping(value = "/getServices")
    public Object getServices() {
        return discoveryClient.getInstances("test-product-service");
    }

    // 轮询的选择同服务(来自不同的客户注册中心,IP不同)
    @GetMapping(value = "/chooseService")
    public Object chooseService() {
        return loadBalancerClient.choose("test-product-service").getUri().toString();
    }

    // -------------------------- ribbon --------------------------
    @GetMapping(value = "/ribbon-call")
    public String ribbonCall() {
        String method = "health";
        return restTemplate.getForEntity("http://test-product-service/" + method, String.class).getBody();
    }
    @GetMapping("/health")
    public String health(){
        return "health";
    }

}
