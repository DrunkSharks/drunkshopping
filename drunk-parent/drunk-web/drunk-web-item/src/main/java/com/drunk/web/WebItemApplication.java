package com.drunk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.drunk.goods.feign")
public class WebItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebItemApplication.class,args);
    }
}
