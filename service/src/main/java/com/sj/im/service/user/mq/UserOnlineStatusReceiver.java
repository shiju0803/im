/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.common.enums.command.UserEventCommand;
import com.sj.im.service.user.service.ImUserStatusService;
import com.sj.im.service.user.web.resp.UserStatusChangeNotifyContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 用户在线状态消息监听
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class UserOnlineStatusReceiver {

    @Resource
    private ImUserStatusService imUserStatusService;

    // 监听 RabbitMQ 消息
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = RabbitConstants.IM_2_USER_SERVICE_QUEUE, durable = "true"),
                                     exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE, durable = "true"),
                                     key = RabbitConstants.IM_2_USER_SERVICE_QUEUE), concurrency = "1")
    @RabbitHandler
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws Exception {
        long start = System.currentTimeMillis();
        Thread t = Thread.currentThread();
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("CHAT MSG FROM QUEUE :::::" + msg);
        // deliveryTag 用于回传 RabbitMQ 确认该消息处理成功
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            Integer command = jsonObject.getInt("command");
            if (ObjectUtil.equal(command, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand())) {
                UserStatusChangeNotifyContent content = JSONUtil.toBean(msg, UserStatusChangeNotifyContent.class, true);
                // 处理用户在线状态通知
                imUserStatusService.processUserOnlineStatusNotify(content);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            // 第一个 false 表示不批量拒绝，第二个 false 表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        } finally {
            long end = System.currentTimeMillis();
            log.debug("channel {} basic-Ack ,it costs {} ms,threadName = {},threadId={}", channel, end - start,
                      t.getName(), t.getId());
        }
    }
}
