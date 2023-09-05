/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.service.group.entry.ImGroupMessageHistoryEntity;
import com.sj.im.service.group.mapper.ImGroupMessageHistoryMapper;
import com.sj.im.service.message.entry.ImMessageBodyEntity;
import com.sj.im.service.message.entry.ImMessageHistoryEntity;
import com.sj.im.service.message.mapper.ImMessageBodyMapper;
import com.sj.im.service.message.mapper.ImMessageHistoryMapper;
import com.sj.im.service.util.SnowflakeIdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息存储服务类，用于将消息存入 Redis 中
 */
@Service
public class MessageStoreService {
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;
    @Resource
    private ImMessageHistoryMapper imMessageHistoryMapper;
    @Resource
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    /**
     * 存储单人聊天消息
     *
     * @param messageContent 聊天消息内容
     * @return void
     */
    @Transactional
    public void storeP2PMessage(MessageContent messageContent) {
        // messageContent转换成messageBody
        ImMessageBodyEntity messageBody = extractMessageBody(messageContent);
        // 插入messageBody
        imMessageBodyMapper.insert(messageBody);
        // 转化成MessageHistory
        List<ImMessageHistoryEntity> history = extractToP2PMessageHistory(messageContent, messageBody);
        // 批量插入
        imMessageHistoryMapper.insertBatchSomeColumn(history);
        messageContent.setMessageKey(messageBody.getMessageKey());
    }

    /**
     * 将消息内容转化为消息体
     *
     * @param messageContent 聊天消息内容
     * @return ImMessageBody 消息体
     */
    public ImMessageBodyEntity extractMessageBody(MessageContent messageContent) {
        ImMessageBodyEntity messageBody = new ImMessageBodyEntity();
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
     * 将消息内容转化为单人聊天消息历史记录
     *
     * @param messageContent      聊天消息内容
     * @param messageBody 消息体
     * @return List<ImMessageHistoryEntity> 消息历史记录列表
     */
    public List<ImMessageHistoryEntity> extractToP2PMessageHistory(MessageContent messageContent, ImMessageBodyEntity messageBody) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        // 构建发送方的消息历史记录实体
        ImMessageHistoryEntity fromHistory = new ImMessageHistoryEntity();
        BeanUtil.copyProperties(messageContent, fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(messageBody.getMessageKey());
        fromHistory.setCreateTime(new Date());

        // 构建接收方的消息历史记录实体
        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageBody.getMessageKey());
        toHistory.setCreateTime(new Date());

        // 将发送方和接收方的消息历史记录实体添加到列表中
        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 存储群聊聊天消息
     *
     * @param messageContent 聊天消息内容
     */
    @Transactional
    public void storeGroupMessage(GroupChatMessageContent messageContent) {
        // messageContent转换成messageBody
        ImMessageBodyEntity messageBody = extractMessageBody(messageContent);
        // 插入messageBody
        imMessageBodyMapper.insert(messageBody);
        // 转化成MessageHistory
        ImGroupMessageHistoryEntity historyEntity = extractToP2PMessageHistory(messageContent, messageBody);
        imGroupMessageHistoryMapper.insert(historyEntity);
        messageContent.setMessageKey(messageBody.getMessageKey());
    }

    /**
     * 将消息内容转化为群聊聊天消息历史记录
     *
     * @param messageContent      聊天消息内容
     * @param messageBody 消息体
     * @return List<ImMessageHistoryEntity> 消息历史记录列表
     */
    public ImGroupMessageHistoryEntity extractToP2PMessageHistory(GroupChatMessageContent messageContent, ImMessageBodyEntity messageBody) {
        // 构建发送方的消息历史记录实体
        ImGroupMessageHistoryEntity history = new ImGroupMessageHistoryEntity();
        BeanUtil.copyProperties(messageContent, history);
        history.setGroupId(messageContent.getGroupId());
        history.setMessageKey(messageBody.getMessageKey());
        history.setCreateTime(new Date());
        return history;
    }
}
