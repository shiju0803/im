/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.mq;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.message.entry.ImMessageBodyEntity;
import com.sj.im.message.model.DoStoreP2PMessageDto;
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
 * 接收和处理存储点对点消息的消息接收器类
 */
@Slf4j
@Service
public class StoreP2PMessageReceiver {

    @Resource
    private StoreMessageService storeMessageService;

    /**
     * 监听点对点消息队列，接收并处理消息
     *
     * @param message 消息体
     * @param headers 消息头
     * @param channel RabbitMQ 通道
     * @throws Exception 处理消息异常
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstants.STORE_P2P_MESSAGE_QUEUE, durable = "true"), // 定义队列名称及持久化
                    exchange = @Exchange(value = RabbitConstants.IM_EXCHANGE, durable = "true") // 定义交换机名称及持久化
            ), concurrency = "1" // 设置并发数为1
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String,Object> headers, Channel channel) throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("CHAT MSG FORM QUEUE ::: {}", msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            // 解析消息内容
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            DoStoreP2PMessageDto p2PMessageDto = jsonObject.toBean(DoStoreP2PMessageDto.class);
            ImMessageBodyEntity messageBody = JSONUtil.toBean(jsonObject.getStr("messageBody"), ImMessageBodyEntity.class);
            p2PMessageDto.setImMessageBodyEntity(messageBody);
            storeMessageService.doStoreP2PMessage(p2PMessageDto);

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