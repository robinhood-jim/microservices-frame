package com.robin.msf.k8s.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableCaching
@ComponentScan("com.robin")
public class ApplicationKubeProduct {
    public static  void main(String[] args) throws Exception{
        try {
            SpringApplication.run(ApplicationKubeProduct.class, args);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
