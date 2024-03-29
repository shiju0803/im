/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.service.group.service.GroupMessageService;
import com.sj.im.service.message.service.impl.MessageSyncServiceImpl;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 群聊消息接收器
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class GroupChatOperateReceiver {
    @Resource
    private GroupMessageService groupMessageService;
    @Resource
    private MessageSyncServiceImpl messageSyncService;

    // 监听RabbitMQ队列，接收消息
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = RabbitConstants.IM_2_GROUP_SERVICE_QUEUE, durable = "true"),
                                     exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE),
                                     key = RabbitConstants.IM_2_GROUP_SERVICE_QUEUE), concurrency = "1")
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws Exception {
        // 将消息体转换为字符串
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("GROUP CHAT MSG FORM QUEUE ::: {}", msg);
        // 获取消息的deliveryTag
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            // 将字符串解析为JSON对象
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            // 获取消息中的command字段
            Integer command = jsonObject.getInt("command");
            if (ObjectUtil.equal(command, GroupEventCommand.MSG_GROUP.getCommand())) {
                // 处理消息
                GroupChatMessageContent messageContent = jsonObject.toBean(GroupChatMessageContent.class);
                groupMessageService.process(messageContent);
            }
            if (ObjectUtil.equal(command, GroupEventCommand.MSG_GROUP_READ.getCommand())) {
                MessageReadContent readContent = jsonObject.toBean(MessageReadContent.class);
                messageSyncService.groupReadMark(readContent);
            }
            channel.basicAck(deliveryTag, false); // 手动确认消息已被消费
        } catch (Exception e) {
            log.error("处理群组消息出现异常：{}", e.getMessage()); // 打印异常信息
            log.error("RMQ_CHAT_TRAN_ERROR", e); // 打印异常堆栈信息
            log.error("NACK_MSG:{}", msg); // 打印未被消费的消息
            // 第一个false表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false); // 手动拒绝消息并且不重回队列
        }
    }
}
