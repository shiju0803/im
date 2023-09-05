/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.publish;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.codec.proto.Message;
import com.sj.im.common.constant.RabbitConstants;
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

    private MqMessageProducer() {}

    public static void sendMessage(Message message, Integer command) {
        String channelName = RabbitConstants.IM_EXCHANGE;

        try {
            Channel channel = MqFactory.getChannel(channelName);
            JSONObject jsonObject = JSONUtil.parseObj(message.getMessagePack());
            jsonObject.putOpt("command", command);
            jsonObject.putOpt("clientType", message.getMessageHeader().getClientType());
            jsonObject.putOpt("imei", message.getMessageHeader().getImei());
            jsonObject.putOpt("appId", message.getMessageHeader().getAppId());

            channel.basicPublish(channelName, "", null, jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
