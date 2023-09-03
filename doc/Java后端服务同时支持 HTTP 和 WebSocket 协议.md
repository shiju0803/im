Java 后端服务可以使用 Spring Boot 和 Spring WebSocket 模块来同时支持 HTTP 和 WebSocket 协议。

首先，您需要在 pom.xml 文件中添加以下依赖：

```xml
<dependencies>
    <!-- Spring Boot Web 和 WebSocket 依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
</dependencies>
```

然后，您需要创建一个 WebSocket 配置类，用于注册 WebSocket 处理器和握手拦截器：

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/ws")
            .setAllowedOrigins("*")
            .addInterceptors(myWebSocketInterceptor());
    }

    @Bean
    public WebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler();
    }

    @Bean
    public WebSocketInterceptor myWebSocketInterceptor() {
        return new MyWebSocketInterceptor();
    }
}
```

在上面的示例中，“MyWebSocketHandler”是您自己实现的 WebSocket 处理器，它用于处理客户端发来的 WebSocket 消息；“MyWebSocketInterceptor”是您自己实现的 WebSocket 握手拦截器，它可以在 WebSocket 连接建立时进行验证等操作。

最后，在您的应用程序中可以通过注入“SimlpeWebSocketHandlerAdapter”来支持 WebSocket 协议。例如，您可以创建一个 RESTful 控制器，用于处理 HTTP 请求，并在其中注入“SimlpeWebSocketHandlerAdapter”：

```java
@RestController
public class MyController {

    @Autowired
    private SimlpeWebSocketHandlerAdapter handlerAdapter;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/ws")
    public void ws(WebSocketSession session) {
        handlerAdapter.handle(session);
    }
}
```

在上面的示例中，“hello()”方法处理 HTTP GET 请求，“ws()”方法处理 WebSocket 连接请求，并通过注入的“SimlpeWebSocketHandlerAdapter”来处理 WebSocket 消息。

需要注意的是，在实现 WebSocket 协议时，您需要注意一些安全性问题，例如授权、跨站点脚本攻击等。此外，WebSocket 连接是一种长连接，您需要对其进行适当的调整，以避免对服务器和客户端资源的不必要占用。