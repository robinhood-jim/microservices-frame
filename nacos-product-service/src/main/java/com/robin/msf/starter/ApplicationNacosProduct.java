package com.robin.msf.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@ComponentScan("com.robin")
public class ApplicationNacosProduct {
    public static  void main(String[] args) throws Exception{
        try {
            SpringApplication.run(ApplicationNacosProduct.class, args);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
