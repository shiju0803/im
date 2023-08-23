/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Nio演示类
 */
public class NioServer {
    // NIO 1.0 如果连接数过多，每次都要循环全部连接，找到有数据的进行处理
    // 保存客户端连接
    private static final List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 创建NIO ServerSocketChannel,与BIO的serverSocket类似
        ServerSocketChannel ch = ServerSocketChannel.open();
        ch.socket().bind(new InetSocketAddress(9001));
        // 设置ServerSocketChannel为非阻塞
        ch.configureBlocking(false);
        System.out.println("服务启动成功");

        while (true) {
            // 非阻塞式accept方法不会阻塞，否则会阻塞
            // NIO的非阻塞是由操作系统内部实现的，底层调用了linux内核的accept函数
            SocketChannel socketChannel = ch.accept();
            // 如果有客户端进行连接
            if (socketChannel != null) {
                System.out.println("连接成功");
                // 设置SocketChannel为非阻塞
                socketChannel.configureBlocking(false);
                // 保存客户端连接在List中
                channelList.add(socketChannel);
            }

            // 遍历连接进行数据读取 10w - 1000读写事件
            Iterator<SocketChannel> iterator = channelList.iterator();
            while (iterator.hasNext()) {
                SocketChannel channel = iterator.next();
                ByteBuffer buffer = ByteBuffer.allocate(128);
                // 非阻塞模式read方法不会阻塞，否则会阻塞
                int len = channel.read(buffer);
                // 如果有数据，把数据打印出来
                if (len > 0) {
                    System.out.println(Thread.currentThread().getName() + " 接收到消息: " + new String(buffer.array(), 0, len));
                } else if (len == -1) {
                    // 如果客户端断开，把socket从集合中去掉
                    iterator.remove();
                    System.out.println("客户端断开");
                }
            }
        }
    }
}
