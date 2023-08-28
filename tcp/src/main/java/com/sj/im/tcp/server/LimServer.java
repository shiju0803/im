/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.server;

import com.sj.im.codec.MessageDecoder;
import com.sj.im.codec.config.BootstrapConfig;
import com.sj.im.tcp.handler.HeartBeatHandler;
import com.sj.im.tcp.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ShiJu
 * @version 1.0
 * @description: TCP网关服务
 */
@Slf4j
public class LimServer {
    // 端口号和两个Group的值都是从配置文件中取出来的
    BootstrapConfig.TcpConfig config;
    EventLoopGroup bossGroup;
    EventLoopGroup workGroup;
    ServerBootstrap server;

    public LimServer(BootstrapConfig.TcpConfig config){
        this.config = config;
        bossGroup = new NioEventLoopGroup(config.getBossThreadSize());
        workGroup = new NioEventLoopGroup(config.getWorkThreadSize());
        server = new ServerBootstrap();
        server.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点来说是否批量发送数据 开启可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 私有协议 解码器
                        ch.pipeline().addLast(new MessageDecoder());
                        // 编码器

                        // 心跳检测
                        ch.pipeline().addLast(new IdleStateHandler(
                                0, 0, 1));
                        ch.pipeline().addLast(new HeartBeatHandler(config.getHeartBeatTime()));
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void start() {
        this.server.bind(config.getTcpPort());
    }
}
