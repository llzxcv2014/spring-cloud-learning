package com.lin.learn.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class CloudEurekaOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudEurekaOneApplication.class, args);
    }

}
