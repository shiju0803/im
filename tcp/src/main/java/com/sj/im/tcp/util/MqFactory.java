/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.util;

import cn.hutool.core.util.ObjectUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sj.im.common.config.BootstrapConfig;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Rabbitmq启动配置类
 */
public class MqFactory {

    private static ConnectionFactory factory = null;

    private static final ConcurrentMap<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    public static void init(BootstrapConfig.Rabbitmq rabbitmq) {
        if (ObjectUtil.isNull(factory)) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = CHANNEL_MAP.get(channelName);
        if (ObjectUtil.isNull(channel)) {
            channel = getConnection().createChannel();
            CHANNEL_MAP.put(channelName, channel);
        }
        return channel;
    }
}
