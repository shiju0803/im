/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.mq;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.message.entry.ImMessageBodyEntity;
import com.sj.im.message.model.DoStoreGroupMessageDto;
import com.sj.im.message.service.StoreMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接收和处理存储群组消息的消息接收器类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Service
public class StoreGroupMessageReceiver {

    @Resource
    private StoreMessageService storeMessageService;

    /**
     * 监听群组消息队列，接收并处理消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstants.STORE_GROUP_MESSAGE_QUEUE, durable = "true"), // 定义队列名称及持久化
            exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE), // 定义交换机名称
            key = RabbitConstants.STORE_GROUP_MESSAGE_QUEUE), // 指定key
                    concurrency = "1") // 设置并发数为1
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("CHAT MSG FORM QUEUE ::: {}", msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            // 解析消息内容
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            DoStoreGroupMessageDto groupMessageDto = jsonObject.toBean(DoStoreGroupMessageDto.class);
            ImMessageBodyEntity messageBody =
                    JSONUtil.toBean(jsonObject.getStr("messageBody"), ImMessageBodyEntity.class);
            groupMessageDto.setImMessageBodyEntity(messageBody);
            // 调用消息存储服务处理群组消息
            storeMessageService.doStoreGroupMessage(groupMessageDto);
            // 手动确认消息已处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            // 拒绝消息并可选择重新入队列
            // 第一个false表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }
    }
}