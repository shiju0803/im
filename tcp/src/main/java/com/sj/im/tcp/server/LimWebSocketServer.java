/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.server;

import com.sj.im.codec.WebSocketMessageDecoder;
import com.sj.im.codec.WebSocketMessageEncoder;
import com.sj.im.tcp.config.TcpConfig;
import com.sj.im.tcp.feign.FeignMessageService;
import com.sj.im.tcp.handler.HeartBeatHandler;
import com.sj.im.tcp.handler.NettyServerHandler;
import com.sj.im.tcp.publish.MqMessageProducer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * WebSocket网关服务
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class LimWebSocketServer {
    @Resource
    private TcpConfig tcpConfig;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private FeignMessageService feignMessageService;
    @Resource
    private MqMessageProducer mqMessageProducer;

    @PostConstruct
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        EventLoopGroup workGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
              .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
              .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
              .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点来说是否批量发送数据 开启可以减少一定的网络开销，但影响消息实时性
              .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
              .childHandler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  protected void initChannel(SocketChannel ch) throws Exception {
                      ChannelPipeline pipeline = ch.pipeline();
                      // websocket 基于http协议，所以要有http编解码器
                      pipeline.addLast("http-codec", new HttpServerCodec());
                      // 对写大数据流的支持
                      pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                      // 几乎在netty中的编程，都会使用到此handler
                      pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                      /**
                       * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
                       * 本handler会帮你处理一些繁重的复杂的事
                       * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
                       * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
                       */
                      pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                      /**
                       * 将 WebSocketMessageDecoder、WebSocketMessageEncoder 和 NettyServerHandler 添加到通道处理器链中
                       */
                      pipeline.addLast(new WebSocketMessageDecoder()); // 添加 WebSocket 消息解码器
                      pipeline.addLast(new WebSocketMessageEncoder()); // 添加 WebSocket 消息编码器
                      pipeline.addLast(new HeartBeatHandler(tcpConfig.getHeartBeatTime())); // 心跳处理器，用于处理客户端发来的心跳消息
                      pipeline.addLast(new NettyServerHandler(redissonClient, feignMessageService, tcpConfig,
                                                              mqMessageProducer)); // 添加 Netty 服务器处理器，用于处理客户端发送的消息
                  }
              });
        server.bind(tcpConfig.getWebSocketPort());
        log.info("WebSocket服务启动成功");
    }
}
