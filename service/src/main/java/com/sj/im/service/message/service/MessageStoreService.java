/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.ImMessageBody;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.common.model.message.OfflineMessageContent;

import java.util.List;

public interface MessageStoreService {

    /**
     * 存储单人聊天消息
     *
     * @param messageContent 聊天消息内容
     * @return void
     */
    void storeP2PMessage(MessageContent messageContent);

    /**
     * 将消息内容转化为消息体
     *
     * @param messageContent 聊天消息内容
     * @return ImMessageBody 消息体
     */
    ImMessageBody extractMessageBody(MessageContent messageContent);

    /**
     * 存储群聊消息
     *
     * @param messageContent 群聊消息内容
     */
    void storeGroupMessage(GroupChatMessageContent messageContent);

    /**
     * 将消息存入 Redis 缓存
     *
     * @param appId          应用 ID
     * @param messageId      消息 ID
     * @param messageContent 消息内容
     */
    void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent);

    /**
     * 从 Redis 缓存中获取消息
     *
     * @param appId     应用 ID
     * @param messageId 消息 ID
     * @param clazz     消息类型
     * @return T 消息
     */
    <T> T getMessageFromMessageIdCache(Integer appId, String messageId, Class<T> clazz);

    /**
     * 存储单人离线消息
     *
     * @param offlineMessage 离线消息内容
     */
    void storeOfflineMessage(OfflineMessageContent offlineMessage);

    /**
     * 存储群聊离线消息
     *
     * @param offlineMessage 离线消息内容
     * @param memberIds      群聊成员 ID 列表
     */
    void storeGroupOfflineMessage(OfflineMessageContent offlineMessage, List<String> memberIds);
}
