/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.publish;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.tcp.util.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Rabbitmq发送消息
 */
@Slf4j
public class MqMessageProducer {

    public static void sendMessage(Object message) {
        Channel channel = null;
        String channelName = "";

        try {
            channel = MqFactory.getChannel(channelName);
            channel.basicPublish(channelName, "", null, JSONUtil.toJsonStr(message).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
