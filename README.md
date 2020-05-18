## Spring Cloud Template

- 一套极简的Spring Cloud微服务项目模板，开箱即用，方便扩展
- 基于 Spring Cloud Hoxton、Spring Boot 的微服务项目

## 写在前面

目前将Shiro的session存放到Redis中，实现SSO，Shiro也和Jwt进行整合

注册中心使用了阿里巴巴的Nacos

限流使用了阿里巴巴的Sentinel

## 核心依赖

| 依赖         | 版本          |
| ------------ | ------------- |
| Spring Boot  | 2.2.2.RELEASE |
| Spring Cloud | Hoxton.SR1    |
| Shiro        | 2.3.4.RELEASE |
| Nacos        |               |
| Sentinel     |               |
| Redis        |               |

**cloud-api应该与cloud-authc整合成一个**
