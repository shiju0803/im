在网关服务gateway中，可以使用 Spring Cloud Gateway 和 Spring WebSocket 模块来同时支持 HTTP 和 WebSocket 协议的转发。

首先，您需要在 pom.xml 文件中添加以下依赖：

```xml
<dependencies>
    <!-- Spring Cloud Gateway 和 WebSocket 依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
</dependencies>
```

然后，您需要创建一个 Gateway 配置类，用于配置 Gateway 路由规则和 WebSocket 转发：

```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("http_route", r -> r.path("/api/**")
                .uri("http://localhost:8080"))
            .route("ws_route", r -> r.path("/ws/**")
                .uri("ws://localhost:8080"))
            .build();
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService();
    }
}
```
在上面的示例中，“routeLocator()”方法配置了两个路由规则：“http_route”用于转发 HTTP 请求，“ws_route”用于转发 WebSocket 请求。需要注意的是，“ws_route”中的 uri 配置为“ws://localhost:8080”，而不是“http://localhost:8080”，这是因为 WebSocket 使用的是“ws”协议而不是“http”协议。

您也可以直接在 Spring Cloud Gateway 和 Spring WebSocket 的 yml 配置文件中配置路由规则和 WebSocket 转发。以下是一个简单的示例：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: http_route
          uri: http://localhost:8080
          predicates:
            - Path=/api/**
        - id: ws_route
          uri: ws://localhost:8080
          predicates:
            - Path=/ws/**
  websocket:
    path: /ws/**
```

在上面的示例中，“spring.cloud.gateway.routes”用于配置路由规则，包括“http_route”和“ws_route”两个规则。其中，“http_route”用于转发 HTTP 请求，“ws_route”用于转发 WebSocket 请求。需要注意的是，“ws_route”中的 uri 配置为“ws://localhost:8080”，而不是“http://localhost:8080”，这是因为 WebSocket 使用的是“ws”协议而不是“http”协议。同时，在配置 WebSocket 路由时，您需要在“websocket.path”属性中指定 WebSocket 路径。

最后，在您的应用程序中可以通过注入“SimlpeWebSocketHandlerAdapter”来支持 WebSocket 协议。例如，您可以创建一个 WebSocket 控制器，用于处理 WebSocket 消息，并在其中注入“SimlpeWebSocketHandlerAdapter”：

```java
@RestController
public class WebSocketController {

    @Autowired
    private SimlpeWebSocketHandlerAdapter handlerAdapter;

    @GetMapping("/ws")
    public void ws(WebSocketSession session) {
        handlerAdapter.handle(session);
    }
}
```

在上面的示例中，“ws()”方法处理 WebSocket 连接请求，并通过注入的“SimlpeWebSocketHandlerAdapter”来处理 WebSocket 消息。

需要注意的是，在实现 WebSocket 协议时，您需要注意一些安全性问题，例如授权、跨站点脚本攻击等。此外，WebSocket 连接是一种长连接，您需要对其进行适当的调整，以避免对服务器和客户端资源的不必要占用。