/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.publish;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.Message;
import com.sj.im.codec.proto.MessageHeader;
import com.sj.im.common.constant.RabbitConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Mq消息发送器，用于向RabbitMQ发送消息
 */
@Slf4j
@Component
public class MqMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到消息队列
     *
     * @param message 要发送的消息
     * @param command 消息类型
     */
    public void sendMessage(Message message, Integer command) {
        // 将消息转换成 JSON 格式，并添加消息类型、客户端类型、设备IMEI号和应用ID
        JSONObject jsonObject = JSONUtil.parseObj(message.getMessagePack());
        jsonObject.putOpt("command", command);
        jsonObject.putOpt("clientType", message.getMessageHeader().getClientType());
        jsonObject.putOpt("imei", message.getMessageHeader().getImei());
        jsonObject.putOpt("appId", message.getMessageHeader().getAppId());

        try {
            rabbitTemplate.convertAndSend(RabbitConstants.IM_EXCHANGE, "", jsonObject.toString());
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }

    /**
     * 发送消息到消息队列
     *
     * @param message 要发送的消息
     * @param header  消息头
     * @param command 消息类型
     */
    public JSONObject sendMessage(Object message, MessageHeader header, Integer command) {
        // 将消息转换成 JSON 格式，并添加消息类型、客户端类型、设备IMEI号和应用ID
        JSONObject jsonObject = JSONUtil.parseObj(message);
        jsonObject.putOpt("command", command);
        jsonObject.putOpt("clientType", header.getClientType());
        jsonObject.putOpt("imei", header.getImei());
        jsonObject.putOpt("appId", header.getAppId());
        try {
            // 发送消息到 RabbitMQ
            rabbitTemplate.convertAndSend(RabbitConstants.IM_EXCHANGE, "", jsonObject.toString());
        } catch (Exception e) {
            log.error("发送mq消息异常：{}", e.getMessage());
        }
        return jsonObject;
    }

    private MqMessageProducer() {}
}
