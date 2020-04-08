package com.lin.learn.cloud.consumer.feign;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("cloud-producer")
public interface HelloService {

    @RequestMapping(method = RequestMethod.GET,
            value = "/hello/welcome",
            consumes = "application/json")
    String hello();

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.timeoutMilliseconds",
                    value = "12000")
    },
            fallbackMethod = "buildFallbackGreeting",
            threadPoolKey = "helloGreetingThreadPool", threadPoolProperties = {
            @HystrixProperty(name = "corePoolSize", value = "10"),
            @HystrixProperty(name = "maxQueueSize", value = "100")})
    @RequestMapping(method = RequestMethod.GET,
            value = "/hello/greeting",
            consumes = "application/json")
    String greeting();

    default String buildFallbackGreeting() {
        return "greeting";
    }
}
