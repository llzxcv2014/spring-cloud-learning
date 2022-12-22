package com.lin.learn.cloud.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

//@EnableEurekaClient
//@EnableCircuitBreaker
@EnableBinding(Source.class)
@SpringBootApplication
public class CloudProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudProducerApplication.class, args);
    }

}
