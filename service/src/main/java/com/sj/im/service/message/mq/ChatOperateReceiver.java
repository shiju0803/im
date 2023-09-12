/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.common.model.message.RecallMessageContent;
import com.sj.im.service.message.service.MessageSyncService;
import com.sj.im.service.message.service.P2PMessageService;
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

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 聊天操作接收器
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class ChatOperateReceiver {
    @Resource
    private P2PMessageService p2PMessageService;
    @Resource
    private MessageSyncService messageSyncService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstants.IM_2_MESSAGE_SERVICE_QUEUE, durable = "true"),
            exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE),
            key = RabbitConstants.IM_2_MESSAGE_SERVICE_QUEUE), concurrency = "1")
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("CHAT MSG FROM QUEUE ::: {}", msg);
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            Integer command = jsonObject.getInt("command");

            // 处理私聊消息
            if (ObjectUtil.equal(command, MessageCommand.MSG_P2P.getCommand())) {
                MessageContent messageContent = jsonObject.toBean(MessageContent.class);
                p2PMessageService.process(messageContent);
            }
            // 处理消息接收确认
            if (ObjectUtil.equal(command, MessageCommand.MSG_RECEIVE_ACK.getCommand())) {
                MessageReceiveAckContent ackContent = jsonObject.toBean(MessageReceiveAckContent.class);
                messageSyncService.receiveMark(ackContent);
            }
            // 处理消息已读
            if (ObjectUtil.equal(command, MessageCommand.MSG_READ.getCommand())) {
                MessageReadContent readContent = jsonObject.toBean(MessageReadContent.class);
                messageSyncService.readMark(readContent);
            }
            // 处理消息撤回
            if (ObjectUtil.equal(command, MessageCommand.MSG_RECALL.getCommand())) {
                RecallMessageContent recallContent = jsonObject.toBean(RecallMessageContent.class);
                messageSyncService.recallMessage(recallContent);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
