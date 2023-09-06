/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.message.MessageReadPack;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.service.conversation.service.ConversationService;
import com.sj.im.service.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息同步服务
 */
@Slf4j
@Service
public class MessageSyncService {

    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ConversationService conversationService;

    /**
     * 接收消息确认，发送给用户
     *
     * @param receiveAckContent 消息接收确认内容
     */
    public void receiveMark(MessageReceiveAckContent receiveAckContent) {
        messageHelper.sendToUser(receiveAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, receiveAckContent, receiveAckContent.getAppId());
    }

    /**
     * 消息已读。更新会话的seq，通知在线的同步端发送指定command ，发送已读回执通知对方（消息发起方）我已读
     *
     * @param readContent 消息已读内容
     */
    public void readMark(MessageReadContent readContent) {
        conversationService.messageMarkRead(readContent);
        MessageReadPack pack = BeanUtil.toBean(readContent, MessageReadPack.class);
        syncToSender(pack, readContent, MessageCommand.MSG_READ_NOTIFY);
        messageHelper.sendToUser(readContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, readContent, readContent.getAppId());
    }

    /**
     * 将消息读取状态同步到接收者的其他客户端
     *
     * @param pack    消息读取包
     * @param content 消息读取内容
     * @param command 命令
     */
    public void syncToSender(MessageReadPack pack, MessageReadContent content, Command command) {
        // 该方法会向所有与发送者不同的客户端发送消息，以通知它们该消息已被读取
        messageHelper.sendToUserExceptClient(pack.getFromId(), command, pack, content);
    }

    /**
     * 将群组消息标记为已读
     *
     * @param readContent 包含已读消息信息的对象
     */
    public void groupReadMark(MessageReadContent readContent) {
        // 调用conversationService的messageMarkRead方法，将消息标记为已读
        conversationService.messageMarkRead(readContent);
        // 创建MessageReadPack对象，并将messageRead对象的属性复制到messageReadPack对象中
        MessageReadPack messageReadPack = BeanUtil.toBean(readContent, MessageReadPack.class);
        // 调用syncToSender方法，将消息已读状态同步给接收方的其他客户端
        syncToSender(messageReadPack, readContent, GroupEventCommand.MSG_GROUP_READ_NOTIFY);
        // 如果消息发送方和接收方不是同一个人，则调用messageProducer的sendToUser方法，将已读消息发送给发送方
        if (ObjectUtil.notEqual(readContent.getFromId(), readContent.getToId())) {
            List<ClientInfo> clientInfos = messageHelper.sendToUser(messageReadPack.getToId(), GroupEventCommand.MSG_GROUP_READ_RECEIPT, readContent, readContent.getAppId());
            log.info("将已读消息发送给发送方[{}]的所有客户端，发送成功的客户端列表：{}", messageReadPack.getToId(), JSONUtil.toJsonStr(clientInfos));
        }
    }
}
