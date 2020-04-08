package com.lin.learn.cloud.producer.controller;

import com.lin.learn.cloud.producer.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello", consumes = "application/json")
public class HelloController {

    private final HelloService helloService;

    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping(path = "/welcome", produces = "application/json")
    public String hello() {
        return "hello";
    }

    @GetMapping(path = "/greeting", produces = "application/json")
    public String greeting() {
        return helloService.greeting();
    }
}
