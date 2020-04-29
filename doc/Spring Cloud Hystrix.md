# Spring Cloud Hystrix

## 参考

[Spring Cloud 系列之 Netflix Hystrix 服务容错（一）](https://www.cnblogs.com/mrhelloworld/p/hystrix1.html)

[Spring Cloud 系列之 Netflix Hystrix 服务容错（二）](https://www.cnblogs.com/mrhelloworld/p/hystrix2.html)

## 雪崩效应

由于微服务之间的相互依赖，当大量请求涌入造成某个服务宕机，服务瘫痪会传导到其他
服务从而造成关联的服务的故障

原因

1. 服务不可用
2. 重试加大流量
3. 服务消费者不可用

### 解决方案

1. 请求缓存：对相同的结果做缓存处理
2. 请求合并：将相同的请求合并然后调用批处理接口
3. 服务隔离：某一个调用的服务出现问题不会影响其他服务
4. 服务熔断：牺牲局部服务保证整体的可用
5. 服务降级：服务熔断以后返回本地的缺省值

使用Jmeter模拟高并发

#### 请求缓存

使用redis将相同且请求量大的数据进行缓存。

Hystrix缓存的缺点：
1. 本地缓存，集群情况下无法同步
2. 不支持第三方缓存

##### 使用

添加依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

如果使用Lettuce则必须添加commons-pool2的依赖

配置文件

```yaml
spring:
  # redis 缓存
  redis:
    timeout: 10000        # 连接超时时间
    host: redishost  # Redis服务器地址
    port: redisport            # Redis服务器端口
    password: redispassword       # Redis服务器密码
    database: 0           # 选择哪个库，默认0库
    lettuce:
      pool:
        max-active: 1024  # 最大连接数，默认 8
        max-wait: 10000   # 最大连接阻塞等待时间，单位毫秒，默认 -1
        max-idle: 200     # 最大空闲连接，默认 8
        min-idle: 5       # 最小空闲连接，默认 0
```

配置类

```java
@EnableCaching
@Configuration
public class RedisConfig {

    // 重写 RedisTemplate 序列化
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 为 String 类型 key 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 为 String 类型 value 设置序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 为 Hash 类型 key 设置序列化器
        template.setHashKeySerializer(new StringRedisSerializer());
        // 为 Hash 类型 value 设置序列化器
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    // 重写 Cache 序列化
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置默认过期时间 30 min
                .entryTtl(Duration.ofMinutes(30))
                // 设置 key 和 value 的序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getKeySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

}
```

在相关业务调用的地方添加Spring的缓存注解

##### 请求合并

目的：在高并发情况下通信次数增加，导致大量线程处于等待状态，减少通信次数。

缺点：设置请求合并后，响应时间有可能增加

使用Hystrix进行请求合并：

添加Hystrix依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

添加请求合并规则：

```java
 // 处理请求合并的方法一定要支持异步，返回值必须是 Future<T>
    // 合并请求
    @HystrixCollapser(batchMethod = "selectProductListByIds", // 合并请求方法
            scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL, // 请求方式
            collapserProperties = {
                    // 间隔多久的请求会进行合并，默认 10ms
                    @HystrixProperty(name = "timerDelayInMilliseconds", value = "20"),
                    // 批处理之前，批处理中允许的最大请求数
                    @HystrixProperty(name = "maxRequestsInBatch", value = "200")
            })
    @Override
    public Future<Product> selectProductById(Integer id) {
        System.out.println("-----orderService-----selectProductById-----");
        return null;
    }
```

##### 服务分离

没有线程池隔离的项目运行在同一个线程池中，负载高的服务使用线程池隔离

优点：

1. 可以安全的隔离服务
2. 不可用服务再次可用时，线程池立即恢复
3. 独立的线程池提高并发性

缺点：

1. 带来cpu的开销，线程的调度，排队，上下文的切换
2. 涉及到跨线程，存在ThreadLocal数据传递的问题

在调用方法上配置

```java
// 声明需要服务容错的方法
    // 线程池隔离
    @HystrixCommand(groupKey = "order-productService-listPool",// 服务名称，相同名称使用同一个线程池
            commandKey = "selectProductList",// 接口名称，默认为方法名
            threadPoolKey = "order-productService-listPool",// 线程池名称，相同名称使用同一个线程池
            commandProperties = {
                    // 超时时间，默认 1000ms
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "5000")
            },
            threadPoolProperties = {
                    // 线程池大小
                    @HystrixProperty(name = "coreSize", value = "6"),
                    // 队列等待阈值(最大队列长度，默认 -1)
                    @HystrixProperty(name = "maxQueueSize", value = "100"),
                    // 线程存活时间，默认 1min
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    // 超出队列等待阈值执行拒绝策略
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "100")
            }, fallbackMethod = "selectProductListFallback")
@Override
    public List<Product> selectProductList() {
        System.out.println(Thread.currentThread().getName() + "-----selectProductList-----");
        // ResponseEntity: 封装了返回数据
        return restTemplate.exchange(
                "http://product-service/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
    }
    
    // 托底数据
    private List<Product> selectProductListFallback() {
        System.out.println("-----selectProductListFallback-----");
        return Arrays.asList(
                new Product(1, "托底数据-华为手机", 1, 5800D),
                new Product(2, "托底数据-联想笔记本", 1, 6888D),
                new Product(3, "托底数据-小米平板", 5, 2020D)
        );
    }
```

信号量隔离

当前请求进行信号量限制，当信号量大于最大请求数时进行限制，调用fallback的后备方法快速返回

```java
// 声明需要服务容错的方法
    // 信号量隔离
    @HystrixCommand(commandProperties = {
            // 超时时间，默认 1000ms
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = "5000"),
            // 信号量隔离
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY,
                    value = "SEMAPHORE"),
            // 信号量最大并发，调小一些方便模拟高并发
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,
                    value = "6")
    }, fallbackMethod = "selectProductListFallback")
    @Override
    public List<Product> selectProductList() {
        // ResponseEntity: 封装了返回数据
        return restTemplate.exchange(
                "http://product-service/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
    }

    // 托底数据
    private List<Product> selectProductListFallback() {
        System.out.println("-----selectProductListFallback-----");
        return Arrays.asList(
                new Product(1, "托底数据-华为手机", 1, 5800D),
                new Product(2, "托底数据-联想笔记本", 1, 6888D),
                new Product(3, "托底数据-小米平板", 5, 2020D)
        );
    }
```

##### 服务熔断

某个服务出现过载，对服务进行保护

```java
// 声明需要服务容错的方法
    // 服务熔断
    @HystrixCommand(commandProperties = {
            // 10s 内请求数大于 10 个就启动熔断器，当请求符合熔断条件触发 fallbackMethod 默认 20 个
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD,
                    value = "10"),
            // 请求错误率大于 50% 就启动熔断器，然后 for 循环发起重试请求，当请求符合熔断条件触发 fallbackMethod
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE,
                    value = "50"),
            // 熔断多少秒后去重试请求，默认 5s
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS,
                    value = "5000"),
    }, fallbackMethod = "selectProductByIdFallback")
    @Override
    public Product selectProductById(Integer id) {
        System.out.println("-----selectProductById-----"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        // 模拟查询主键为 1 的商品信息会导致异常
        if (1 == id)
            throw new RuntimeException("查询主键为 1 的商品信息导致异常");
        return restTemplate.getForObject("http://product-service/product/" + id, Product.class);
    }

    // 托底数据
    private Product selectProductByIdFallback(Integer id) {
        return new Product(id, "托底数据", 1, 2666D);
    }
```

##### 服务降级

资源不够时，关闭某些服务。

触发条件：

1. 方法抛出非 HystrixBadRequestException 异常；
2. 方法调用超时
3. 熔断器开启拦截调用
4. 线程池/信号量满

```java
// 声明需要服务容错的方法
    // 服务降级
    @HystrixCommand(fallbackMethod = "selectProductByIdFallback")
    @Override
    public Product selectProductById(Integer id) {
        return restTemplate.getForObject("http://product-service/product/" + id, Product.class);
    }

    // 托底数据
    private Product selectProductByIdFallback(Integer id) {
        return new Product(id, "托底数据", 1, 2666D);
    }
```

##### 网关限流

为何限流

1. 计数器算法
2. 漏桶算法
3. 令牌桶算法


###### 计数算法

限定一个时间段内的访问次数

缺陷：

临界问题在相邻的两个时间段，在上一时间段的末尾和本次时间段的开头都发送限定的请求数而压垮服务

###### 漏桶算法

漏桶算法可以粗略的认为就是注水漏水的过程，往桶中以任意速率流入水，
以一定速率流出水，当水超过桶流量则丢弃，因为桶容量是不变的，保证了整体的速率。

漏桶算法基于队列实现

缺陷：

* 漏桶算法主要用途在于保护它人（服务），假设入水量很大，而出水量较慢，则会造成网关的资源堆积可能导致网关瘫痪。

* 目标服务可能是可以处理大量请求的，但是漏桶算法出水量缓慢反而造成服务那边的资源浪费

* 漏桶算法无法应对突发调用

###### 令牌桶算法

令牌桶算法能够在限制调用的平均速率的同时还能允许一定程度的突发调用。

令牌桶算法中存在一个存放固定数量的令牌的桶。算法存在一种机制，以一定的速率向桶中放令牌，每次请求调用前需要先获取令牌，只有
拿到令牌，才有机会继续执行，否则等待可用的令牌或者直接拒绝。放令牌的动作会持续不断的进行，如果桶中令牌的数量达到上限就会丢弃
令牌。

