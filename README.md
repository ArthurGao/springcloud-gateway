# welcome our new production registration proxy service to lead away from the current Apigee

# Overview
1. this is a demo routing proxy using [spring cloud gateway](https://spring.io/projects/spring-cloud-gateway/) to offload some workloads from 
an existing apigee proxy DSSupport https://gitlab.admin.arthur.nz/api-proxy-service/ds-support
2. it is proposed to offload many jobs from Apigee, like routing, orchestration, transformation,etc. please refer to the src/main/resources/application.yaml for the routing configs
3. it is replacement of ProductRegistration (https://gitlab.admin.arthur.nz/api-proxy/productregistration.proxy)

# How to run it locally

## run the service
```sh
./gradlew bootRun

023-07-13 11:15:39.205  INFO 40414 --- [           main] n.p.proxy.datastudio.ProxyApplication    : Starting ProxyApplication using Java 11.0.12 on Sigs-MacBook-Pro.local with PID 40414 (/Users/sig/Workspace/arthur/api-proxy-service/ds-support/service/build/classes/java/main started by sig in /Users/sig/Workspace/arthur/api-proxy-service/ds-support/service)
...
2023-07-13 11:15:40.961 DEBUG 40414 --- [           main] o.s.c.g.filter.GatewayMetricsFilter      : New routes count: 7
2023-07-13 11:15:40.965  INFO 40414 --- [           main] n.p.proxy.datastudio.ProxyApplication    : Started ProxyApplication in 2.14 seconds (JVM running for 2.404)

```

## How to request 
send http request to the local proxy service which will route it to the ProductRegistration in cte k8s
```http
curl --location 'http://localhost:8080/productregistration_passthrough/legalname?q=arthur' \
--header 'Accept: application/vnd.arthur+json' \
--header 'Content-Type: application/vnd.arthur+json'
```

Note: Dev not work because of not support arthur-jwt