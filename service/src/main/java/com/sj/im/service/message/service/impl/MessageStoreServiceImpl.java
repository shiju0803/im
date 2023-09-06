/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.model.message.*;
import com.sj.im.service.message.service.MessageStoreService;
import com.sj.im.service.util.SnowflakeIdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息存储服务类，用于将消息存入 Redis 中
 */
@Service
public class MessageStoreServiceImpl implements MessageStoreService {
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 存储单人聊天消息
     *
     * @param messageContent 聊天消息内容
     * @return void
     */
    @Override
    public void storeP2PMessage(MessageContent messageContent) {
        // 提取消息体
        ImMessageBody messageBody = extractMessageBody(messageContent);
        // 封装存储点对点消息的数据传输对象
        DoStoreP2PMessageDto dto = new DoStoreP2PMessageDto();
        dto.setMessageContent(messageContent);
        dto.setMessageBody(messageBody);
        // 设置消息的唯一标识
        messageContent.setMessageKey(messageBody.getMessageKey());
        // 发送消息到 RabbitMQ 中，等待消费
        rabbitTemplate.convertAndSend(RabbitConstants.IM_EXCHANGE, RabbitConstants.STORE_P2P_MESSAGE_QUEUE, JSONUtil.toJsonStr(dto));
    }

    /**
     * 将消息内容转化为消息体
     *
     * @param messageContent 聊天消息内容
     * @return ImMessageBody 消息体
     */
    public ImMessageBody extractMessageBody(MessageContent messageContent) {
        ImMessageBody messageBody = new ImMessageBody();
        // 设置应用 ID
        messageBody.setAppId(messageContent.getAppId());
        // 生成消息 ID
        messageBody.setMessageKey(snowflakeIdWorker.nextId());
        // 设置消息创建时间
        messageBody.setCreateTime(new Date());
        // 设置安全密钥
        messageBody.setSecurityKey("");
        // 设置额外数据
        messageBody.setExtra(messageContent.getExtra());
        // 设置删除标志
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        // 设置消息发送时间
        messageBody.setMessageTime(messageContent.getMessageTime());
        // 设置消息内容
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody; // 返回 ImMessageBody 对象
    }

    /**
     * 存储群聊聊天消息
     *
     * @param messageContent 聊天消息内容
     */
    public void storeGroupMessage(GroupChatMessageContent messageContent) {
        // messageContent转换成messageBody
        ImMessageBody messageBody = extractMessageBody(messageContent);
        // 创建存储群聊消息的 DTO 对象
        DoStoreGroupMessageDto dto = new DoStoreGroupMessageDto();
        dto.setMessageBody(messageBody);
        dto.setGroupChatMessageContent(messageContent);
        // 将消息体的消息 ID 赋值给消息内容的消息 ID
        messageContent.setMessageKey(messageBody.getMessageKey());
        // 发送消息到 RabbitMQ 中，等待消费
        rabbitTemplate.convertAndSend(RabbitConstants.IM_EXCHANGE, RabbitConstants.STORE_GROUP_MESSAGE_QUEUE, JSONUtil.toJsonStr(dto));
    }

    /**
     * 将消息存入 Redis 缓存
     *
     * @param appId          应用 ID
     * @param messageId      消息 ID
     * @param messageContent 消息内容
     */
    public void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent) {
        // 拼接 key，格式为：appId:cacheMessage:messageId
        String key = appId + ":" + RedisConstants.CACHE_MESSAGE + ":" + messageId;
        // 将消息内容转换为 JSON 字符串并存入 Redis 缓存中，设置过期时间为 300 秒
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(messageContent), 300, TimeUnit.SECONDS);
    }

    /**
     * 从 Redis 缓存中获取消息
     *
     * @param appId     应用 ID
     * @param messageId 消息 ID
     * @param clazz     消息类型
     * @return T 消息
     */
    public <T> T getMessageFromMessageIdCache(Integer appId, String messageId, Class<T> clazz) {
        // 拼接 key，格式为：appId:cacheMessage:messageId
        String key = appId + ":" + RedisConstants.CACHE_MESSAGE + ":" + messageId;
        // 从 Redis 缓存中获取指定 key 的值
        String msg = stringRedisTemplate.opsForValue().get(key);
        // 如果缓存中不存在指定 key 的值，则返回 null
        if (CharSequenceUtil.isBlank(msg)) {
            return null;
        }
        // 将获取到的消息内容转换为指定类型的对象并返回
        return JSONUtil.toBean(msg, clazz);
    }

    /**
     * 存储单人离线消息
     *
     * @param offlineMessage 离线消息内容
     */
    @Override
    public void storeOfflineMessage(OfflineMessageContent offlineMessage) {

    }

    /**
     * 存储群聊离线消息
     *
     * @param offlineMessage 离线消息内容
     * @param memberIds      群聊成员 ID 列表
     */
    @Override
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessage, List<String> memberIds) {

    }
}
