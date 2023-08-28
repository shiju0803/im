/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.tcp.util.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Rabbitmq消息监听
 */
@Slf4j
public class MqMessageListener {

    private static void startListenerMessage(){
        try {
            Channel channel = MqFactory.getChannel(RabbitConstants.MESSAGE_SERVICE_2_IM);
            channel.queueDeclare(RabbitConstants.MESSAGE_SERVICE_2_IM, true, false, false, null);
            channel.queueBind(RabbitConstants.MESSAGE_SERVICE_2_IM, RabbitConstants.MESSAGE_SERVICE_2_IM, "");
            channel.basicConsume(RabbitConstants.MESSAGE_SERVICE_2_IM, false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            //TODO 消息服务发送的消息
                            String msgStr = new String(body);
                            log.info(msgStr);
                        }
                    });
        } catch (Exception e) {
            log.error("接收mq消息异常：{}", e.getMessage(), e);
        }
    }

    public static void init() {
        startListenerMessage();
    }
}
