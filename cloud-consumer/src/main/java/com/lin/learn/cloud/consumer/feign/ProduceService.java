package com.lin.learn.cloud.consumer.feign;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("cloud-producer")
public interface ProduceService {

    // 声明需要服务容错的方法
    // 线程池隔离
    @HystrixCommand(groupKey = "producer-produce-resultPool",
            // 服务名称，相同名称使用同一个线程池
            commandKey = "selectProductList",
            // 接口名称，默认为方法名
            threadPoolKey = "order-productService-listPool",
            // 线程池名称，相同名称使用同一个线程池
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
            }, fallbackMethod = "resultFallBack")
    @RequestMapping(method = RequestMethod.GET, value = "/api/produce/result")
    String result();

    /**
     * 托底数据
     *
     * @return
     */
    default String resultFallBack() {
        return "result";
    }
}
