package com.lin.learn.cloud.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
// 该服务将作为OAuth2服务
@EnableAuthorizationServer
public class CloudSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSecurityApplication.class, args);
    }

}
