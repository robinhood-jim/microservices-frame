package com.robin.msf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckContorller {
    @GetMapping("/health")
    public String health(){
        return "health";
    }
}
