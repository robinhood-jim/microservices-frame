package com.robin.msf.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDiscoveryClient
@EnableCaching
@ComponentScan("com.robin")
public class ApplicationProduct {
    public static  void main(String[] args) throws Exception{
        try {
            SpringApplication.run(ApplicationProduct.class, args);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
