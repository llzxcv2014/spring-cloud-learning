package com.lin.cloud.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
// 启用单点登录
@EnableOAuth2Sso
public class CloudSecurityClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSecurityClientApplication.class, args);
    }

}
