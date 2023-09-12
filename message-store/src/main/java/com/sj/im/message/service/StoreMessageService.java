/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.service;

import cn.hutool.core.bean.BeanUtil;
import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.message.entry.ImGroupMessageHistoryEntity;
import com.sj.im.message.entry.ImMessageBodyEntity;
import com.sj.im.message.entry.ImMessageHistoryEntity;
import com.sj.im.message.mapper.ImGroupMessageHistoryMapper;
import com.sj.im.message.mapper.ImMessageBodyMapper;
import com.sj.im.message.mapper.ImMessageHistoryMapper;
import com.sj.im.message.model.DoStoreGroupMessageDto;
import com.sj.im.message.model.DoStoreP2PMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 存储消息的服务类
 *
 * @author ShiJu
 * @version 1.0
 */
@Service
public class StoreMessageService {

    @Resource
    private ImMessageHistoryMapper imMessageHistoryMapper;
    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;
    @Resource
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    /**
     * 执行存储点对点消息的操作
     *
     * @param doStoreP2PMessageDto 存储点对点消息的数据传输对象
     */
    @Transactional
    public void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2PMessageDto) {
        // 存储消息体
        imMessageBodyMapper.insert(doStoreP2PMessageDto.getImMessageBodyEntity());

        // 提取消息内容并转换为点对点消息历史记录实体列表
        List<ImMessageHistoryEntity> imMessageHistoryEntities =
                extractToP2PMessageHistory(doStoreP2PMessageDto.getMessageContent(),
                                           doStoreP2PMessageDto.getImMessageBodyEntity());

        // 批量插入点对点消息历史记录
        imMessageHistoryMapper.insertBatchSomeColumn(imMessageHistoryEntities);
    }

    /**
     * 提取消息内容并转换为点对点消息历史记录实体列表
     *
     * @param messageContent      消息内容
     * @param imMessageBodyEntity 消息体实体
     * @return 点对点消息历史记录实体列表
     */
    public List<ImMessageHistoryEntity> extractToP2PMessageHistory(MessageContent messageContent,
                                                                   ImMessageBodyEntity imMessageBodyEntity) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();

        Date now = new Date();
        // 构造发送方的消息历史记录实体
        ImMessageHistoryEntity fromHistory = BeanUtil.toBean(messageContent, ImMessageHistoryEntity.class);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        fromHistory.setCreateTime(now);
        fromHistory.setSequence(messageContent.getMessageSequence());

        // 构造接收方的消息历史记录实体
        ImMessageHistoryEntity toHistory = BeanUtil.toBean(messageContent, ImMessageHistoryEntity.class);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        toHistory.setCreateTime(now);
        toHistory.setSequence(messageContent.getMessageSequence());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 执行存储群组消息的操作
     *
     * @param doStoreGroupMessageDto 存储群组消息的数据传输对象
     */
    @Transactional
    public void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto) {
        // 存储消息体
        imMessageBodyMapper.insert(doStoreGroupMessageDto.getImMessageBodyEntity());

        // 提取消息内容并转换为群组消息历史记录实体
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity =
                extractToGroupMessageHistory(doStoreGroupMessageDto.getGroupChatMessageContent(),
                                             doStoreGroupMessageDto.getImMessageBodyEntity());

        // 插入群组消息历史记录
        imGroupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
    }

    /**
     * 提取消息内容并转换为群组消息历史记录实体
     *
     * @param messageContent    群组消息内容
     * @param messageBodyEntity 消息体实体
     * @return 群组消息历史记录实体
     */
    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent messageContent,
                                                                     ImMessageBodyEntity messageBodyEntity) {
        ImGroupMessageHistoryEntity result = BeanUtil.toBean(messageContent, ImGroupMessageHistoryEntity.class);
        result.setGroupId(messageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(new Date());
        return result;
    }
}
