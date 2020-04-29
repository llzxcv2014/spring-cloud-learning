# Spring Cloud Gateway

## 参考

[Spring Cloud Gateway：新一代API网关服务](http://www.imooc.com/article/297943)

[Spring Cloud 系列之 Gateway 服务网关（一）](https://www.cnblogs.com/mrhelloworld/p/gateway1.html)

[Spring Cloud 系列之 Gateway 服务网关（二）](https://www.cnblogs.com/mrhelloworld/p/gateway2.html)

[Spring Cloud 系列之 Gateway 服务网关（三）](https://www.cnblogs.com/mrhelloworld/p/gateway3.html)

[Spring Cloud 系列之 Gateway 服务网关（四）](cnblogs.com/mrhelloworld/p/gateway4.html)

## 简介

基于Spring 5，Spring Boot2和Project Reactor构建。
提供路由，过滤，熔断，限流，重试等功能

由于Zuul1的实现是阻塞IO，不支持长连接，而gateway的实现则是响应式

特性：

* 动态路由
* 断言（Predicate）和过滤（filter）
* 集成Hystrix
* 限流
* 路径重写
* spring cloud组件的集成

## 服务网关

API Gateway是在系统边界上的面向API的、串行集中式的强管控服务（边界可以理解为企业级防火墙，起到隔离外部访问与内部系统的作用）

### API网关带来的益处

1. 聚合接口使得对调用者透明
2. 聚合后端服务节省流量，提高性能，提升用户体验
3. 提供安全、流控、缓存、计费、监控等API管理功能

### 网关解决的问题

客户端直接与微服务交互的弊端

1. 客户端多次请求不同的微服务增加客户端的复杂性
2. 存在跨域请求，在某些情况下处理复杂
3. 身份认证
4. 难以重构
5. 某些微服务使用防火墙/浏览器不友好的协议，直接访问困难

网关的优点

1. 易于监控
2. 易于认证
3. 减少客户端与微服务之间的请求次数

### 网关的功能

* 性能：负载均衡，容错机制
* 安全：权限的身份验证、脱敏、流量清洗、后端签名、黑名单
* 日志：日志记录
* 缓存：数据缓存
* 监控：性能监控
* 限流：流浪控制、错峰控制、定义限流规则
* 灰度：线上灰度发布
* 路由：动态路由规则

### 常见的解决方案

1. Nginx+Lua
2. Kong
3. Traefik
4. Spring Cloud Netflix Zuul
5. Spring Cloud Gateway

## 相关概念

* 路由（Route）：基本模块，由ID，目标URI，断言和过滤器组成
* 断言
* 过滤器

