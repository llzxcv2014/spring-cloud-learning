# Spring Security OAuth2

Spring Security用于用户认证和授权（authentication and authority）。

## 认证方式

### 认证方式

#### 会话

会话：为了避免用户的每次操作都进行认证可以将用户的信息保存在会话中。

#### token

为客户端生成token（令牌），存在客户端介质中（cookie、localstorage），服务端可以校验客户端携带的token

### 授权

检测用户是否对资源拥有当前操作的权限。

## 访问

基于角色的访问和基于资源的访问。推荐基于资源的访问控制，基于权限修改时则会出现修改较多的情况。

## Spring Security

基于Filter
