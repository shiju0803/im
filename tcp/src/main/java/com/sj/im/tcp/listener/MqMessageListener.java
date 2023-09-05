/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.tcp.listener.process.BaseProcess;
import com.sj.im.tcp.listener.process.ProcessFactory;
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

    private static String brokerId;

    private static void startListenerMessage() {
        try {
            Channel channel = MqFactory.getChannel(RabbitConstants.MESSAGE_SERVICE_2_IM_QUEUE + "_" + brokerId);
            // 绑定队列
            channel.queueDeclare(RabbitConstants.MESSAGE_SERVICE_2_IM_QUEUE + "_" + brokerId,
                    true, false, false, null);
            // 绑定交换机
            channel.queueBind(RabbitConstants.MESSAGE_SERVICE_2_IM_QUEUE + "_" + brokerId,
                    RabbitConstants.IM_EXCHANGE, brokerId);
            channel.basicConsume(RabbitConstants.MESSAGE_SERVICE_2_IM_QUEUE + "_" + brokerId, false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            try {
                                // 处理消息服务发送来的消息
                                String msgStr = new String(body);
                                MessagePack messagePack = JSONUtil.toBean(msgStr, MessagePack.class);
                                BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getCommand());
                                messageProcess.process(messagePack);
                                channel.basicAck(envelope.getDeliveryTag(), false);
                            } catch (Exception e) {
                                log.error("处理消息异常: {}", e.getMessage());
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("接收mq消息异常：{}", e.getMessage(), e);
        }
    }

    public static void init() {
        startListenerMessage();
    }

    public static void init(String brokerId) {
        if(CharSequenceUtil.isBlank(MqMessageListener.brokerId)){
            MqMessageListener.brokerId = brokerId;
        }
        startListenerMessage();
    }
}
