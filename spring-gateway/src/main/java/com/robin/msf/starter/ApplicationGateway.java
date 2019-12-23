package com.robin.msf.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.robin.msf")
public class ApplicationGateway {
    public static  void main(String[] args) throws Exception{
        try {
            SpringApplication.run(ApplicationGateway.class, args);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
