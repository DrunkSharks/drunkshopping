package com.drunk.user;

import com.drunk.entity.IdWorker;
import com.drunk.entity.TokenDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.drunk.user.dao")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);
    }

    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }
}
