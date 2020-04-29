# Spring Cloud分布式追踪

## 参考

[Spring Cloud 系列之 Sleuth 链路追踪（一）](https://www.cnblogs.com/mrhelloworld/p/sleuth1.html)

[Dapper，大规模分布式系统的跟踪系统](http://bigbully.github.io/Dapper-translation/)

## 链路追踪由来

随着业务的拆分，一次请求往往需要设计多个服务。互联网应用构建在不同的软件模块上，不同的模块由不同的团队开发
使用不同的编程语言实现，部署在几千台服务器上，横跨多个数据中心。因此需要帮助理解系统行为，用于分析性能问题的工具，
以便发生故障的时候，能够快速定位和解决问题。

随着服务增多会带来一下的问题

* 如何快速发现问题
* 如何判断故障影响的范围
* 如何梳理服务依赖
* 如何分析链路性能

## Sleuth

功能：

* 链路追踪
* 性能分析
* 数据分析优化链路
* 可视化错误

**Span：**

Span是Sleuth的基本单位，一次单独的调用链称为一个Span。

**Trace**

Trace是一系列Span组成的树状结构，一个Trace认为是一个完整的链路，内部包含多个Span。Trace和Span存在一对多的关系
Span和Span之间存在父子关系。

**Annotation**

用来及时记录一个事件的存在，一些核心的注解定义一个请求的开始和结束

## Zipkin

Zipkin是Twitter公司开发的一款开源的分布式实时数据追踪系统，其基于Google的Dapper的论文设计而来，
功能是聚集各个异构系统的实时监控数据。

### 核心组件

`Collector`：收集器组件，处理从外部系统发送过来的跟踪信息，这些信息会被转为Zipkin内部处理的Span格式

`Storage`：存储组件，处理收集器接收到的跟踪信息

`WebUI`：UI组件，基于API组件实现的上层应用，提供web页面

`Restful API`：API组件，为web界面提供查询存储中数据的接口

