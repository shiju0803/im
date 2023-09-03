## WebSocketServerProtocolHandle
WebSocketServerProtocolHandle是Netty框架提供的WebSocket协议的处理器，它能够帮助我们快速地实现WebSocket服务器端的开发。

在Netty框架中，我们需要通过多个ChannelHandler来处理不同的请求，比如HTTP请求、WebSocket请求等。当我们需要处理WebSocket请求时，我们可以使用WebSocketServerProtocolHandle来实现，它会帮助我们完成WebSocket握手的过程，并且帮助我们处理WebSocket请求。

下面是一个简单的使用WebSocketServerProtocolHandle的示例：

```java
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        // 添加WebSocket协议处理器
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));

        // 添加自定义的处理器
        pipeline.addLast(new WebSocketHandler());
    }

}
```

在这个示例中，我们首先添加了WebSocketServerProtocolHandler，它的参数是一个URI，表示WebSocket请求的URI。在这个示例中，我们设置了URI为“/websocket”。这个处理器会帮助我们完成WebSocket握手的过程，并且会将握手成功后的WebSocket请求转发给下一个处理器。

接下来，我们添加了自定义的处理器WebSocketHandler，用来处理WebSocket请求。在这个处理器中，我们可以根据WebSocket请求的内容，实现我们自己的业务逻辑。

总的来说，WebSocketServerProtocolHandler是一个非常有用的处理器，它可以帮助我们快速地实现WebSocket服务器端的开发。如果你需要使用Netty框架来实现WebSocket服务器，那么你应该学会如何使用WebSocketServerProtocolHandler。

## Snakeyaml解析配置文件
要读取resources下的yaml文件并解析，您可以使用YAML库来帮助您完成此任务。下面是使用SnakeYAML库读取和解析yaml文件的示例代码：

首先，您需要将SnakeYAML库添加到您的项目依赖中。您可以通过添加以下Maven坐标来完成此操作：

```xml
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.29</version>
</dependency>
```

接下来，您可以使用以下代码来读取和解析yaml文件：

```java
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YamlReader {
    public static void main(String[] args) {
        // 读取yaml文件
        InputStream inputStream = YamlReader.class.getClassLoader().getResourceAsStream("your_file_name.yml");

        // 解析yaml文件
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);

        // 输出解析结果
        System.out.println(data);
    }
}
```

在此示例中，您需要将“your_file_name.yml”替换为实际的yaml文件名。此代码将读取文件并将其解析为Map<String, Object>对象。您可以按照您的需求处理解析结果。

## 