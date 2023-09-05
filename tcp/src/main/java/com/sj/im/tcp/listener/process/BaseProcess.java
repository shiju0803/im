/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener.process;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 抽象基础处理类，定义了消息处理的基本流程
 */
public abstract class BaseProcess {

    /**
     * 在消息处理之前执行的操作，由具体子类实现
     */
    public abstract void processBefore();

    /**
     * 消息处理的主要流程，先执行processBefore()，再根据消息内容找到对应的NioSocketChannel并发送消息，
     * 最后执行processAfter()
     *
     * @param messagePack 消息对象
     */
    public void process(MessagePack<Object> messagePack) {
        // 执行processBefore()，做一些预处理工作
        processBefore();
        // 根据消息内容找到对应的NioSocketChannel
        NioSocketChannel channel = SessionSocketHolder.get(
                messagePack.getAppId(),
                messagePack.getToId(),
                messagePack.getClientType(),
                messagePack.getImei());

        // 如果找到对应的通道，则将消息发送出去
        if (ObjectUtil.isNotNull(channel)) {
            channel.writeAndFlush(messagePack);
        }
        // 执行processAfter()，做一些后续处理工作
        processAfter();
    }

    /**
     * 在消息处理之后执行的操作，由具体子类实现
     */
    public abstract void processAfter();
}
