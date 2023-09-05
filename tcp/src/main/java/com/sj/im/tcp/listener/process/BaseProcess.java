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
 * @description: 基础处理抽象类
 */
public abstract class BaseProcess {

    public abstract void processBefore();

    public void process(MessagePack<Object> messagePack) {
        processBefore();
        NioSocketChannel channel = SessionSocketHolder.get(messagePack.getAppId(),
                messagePack.getToId(), messagePack.getClientType(), messagePack.getImei());

        if (ObjectUtil.isNotNull(channel)) {
            channel.writeAndFlush(messagePack);
        }
        processAfter();
    }

    public abstract void processAfter();
}
