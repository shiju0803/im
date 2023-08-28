/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.demo.chat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Netty事件处理类
 */
public class ChatDiscardServerHandler extends ChannelInboundHandlerAdapter {

    private static final Set<Channel> channelList = new HashSet<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通知其他人 我上线了
        channelList.forEach(channel -> {
            channel.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "上线了");
        });

        channelList.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String message = msg.toString();

        // 分发给聊天室内的所有客户端
        channelList.forEach(channel -> {
            if (channel == ctx.channel()) {
                channel.writeAndFlush("[自己] :  " + message);
            } else {
                channel.writeAndFlush("[客户端] " + ctx.channel().remoteAddress() + ":  " + message);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通知其他客户端 我下线了
        channelList.remove(ctx.channel());
        channelList.forEach(channel -> {
            channel.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "下线了");
        });
    }
}

