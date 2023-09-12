/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.tcp.listener.process.BaseProcess;
import com.sj.im.tcp.listener.process.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息接收器类，用于从RabbitMQ接收消息并进行处理
 * 在聊天系统中，用户会登陆到不同的brokerId的机器上，比如用户1登陆在A服务器上，用户2登陆在B服务器上，A服务器找不到B服务上的channel，
 * 使用路由的方式解决跨服务消息发送，用户登陆的时候会记录用户登陆在那台brokerId机器上，然后在发送消息的时候，
 * 先将消息发送到对应的brokerId上，再由该brokerId转发到目标用户所在的brokerId上，最后再将消息发送到目标用户的channel上。
 * 这样就实现了跨服务的消息发送。同时，为了保证消息的可靠性，还可以在每个brokerId上设置消息队列，将消息先存储在队列中，
 * 等目标用户上线后再发送给其对应的channel。
 */
@Slf4j
@Component
public class MqMessageListener {

    // 监听RabbitMQ队列，接收消息
    @RabbitListener(bindings = @QueueBinding(
            // 定义队列名称及持久化
            value = @Queue(value = RabbitConstants.MESSAGE_SERVICE_2_IM_QUEUE, durable = "true"),
            // 定义交换机名称及持久化
            exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE),
            // 定义路由键
            key = "${netty.brokerId}"), concurrency = "1") // 设置并发数为1
    private void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws Exception {
        try {
            // 将消息内容转换为MessagePack对象
            String msgStr = new String(message.getBody());
            log.info("接收到消息：{}", msgStr);
            MessagePack<Object> messagePack = JSONUtil.toBean(msgStr, MessagePack.class);
            // 获取消息对应的处理器，并进行处理
            BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getCommand());
            messageProcess.process(messagePack);
            // 确认消息已被处理
            channel.basicAck((long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
        } catch (Exception e) {
            // 处理消息异常，重新放回队列等待处理
            log.error("处理消息异常：{}", e.getMessage());
            // 第一个false表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack((long) headers.get(AmqpHeaders.DELIVERY_TAG), false, false);
        }
    }
}
