/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.server;

import com.sj.im.codec.MessageDecoder;
import com.sj.im.codec.MessageEncoder;
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
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: LimServer 类，用于启动Netty服务端
 */
@Slf4j
@Component
public class LimServer {

    @Resource
    private TcpConfig tcpConfig;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private FeignMessageService feignMessageService;
    @Resource
    private MqMessageProducer mqMessageProducer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap server;

    @PostConstruct
    public void start() {
        // 创建主和子EventLoopGroup，用于处理客户端连接和I/O事件
        bossGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        workGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());
        // 创建Netty服务端启动器
        server = new ServerBootstrap();
        // 配置启动器参数
        server.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class) // 指定使用NIO模式
                .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点来说是否批量发送数据 开启可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
                // 添加ChannelInitializer，用于初始化SocketChannel的ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加ChannelPipeline的处理器
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder()); // 解码器，用于解码收到的消息
                        pipeline.addLast(new MessageEncoder()); // 编码器，用于编码发送的消息
                        // 用于触发空闲事件的Handler，参数分别为读空闲时间、写空闲时间、读写空闲时间
//                        pipeline.addLast(new IdleStateHandler(0, 0, 10));
                        // 心跳处理器，用于处理客户端发来的心跳消息
                        pipeline.addLast(new HeartBeatHandler(tcpConfig.getHeartBeatTime()));
                        // 业务逻辑处理器
                        pipeline.addLast(new NettyServerHandler(redissonClient, feignMessageService, tcpConfig, mqMessageProducer));
                    }
                });
        // 绑定端口并启动服务端
        server.bind(tcpConfig.getTcpPort());
        log.info("Tcp服务启动成功");
    }
}
