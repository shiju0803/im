/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.codec.pack.message.ChatMessageAck;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.service.group.service.GroupMessageService;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.web.req.SendGroupMessageReq;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.message.service.MessageStoreService;
import com.sj.im.service.message.web.resp.SendMessageResp;
import com.sj.im.service.message.service.CheckSendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群消息服务类
 */
@Slf4j
@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    @Resource
    private CheckSendMessageService checkSendMessageService;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private MessageStoreService messageStoreService;

    @Override
    public void process(GroupChatMessageContent content) {

    }

    /**
     * 分发群聊消息
     *
     * @param messageContent 群聊消息内容
     */
    @Override
    public void dispatchMessage(GroupChatMessageContent messageContent) {
        // 遍历群成员ID列表
        for (String memberId : messageContent.getMemberId()) {
            // 如果当前成员ID不是消息发送者的ID
            if (ObjectUtil.notEqual(memberId, messageContent.getFromId())) {
                // 将消息发送到当前成员的在线设备
                messageHelper.sendToUser(memberId, GroupEventCommand.MSG_GROUP, messageContent, messageContent.getAppId());
            }
        }
    }

    /**
     * 对消息进行确认操作，即向消息发送方发送确认回执
     *
     * @param messageContent 消息内容
     * @param response       响应数据对象
     */
    @Override
    public void ack(MessageContent messageContent, ResponseVO<Object> response) {
// 创建一个 ChatMessageAck 对象，用于存储确认信息
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        // 将确认信息设置到 RestResponse 对象中
        response.setData(chatMessageAck);
        // 向消息发送方发送确认回执
        messageHelper.sendToUser(messageContent.getFromId(), GroupEventCommand.GROUP_MSG_ACK, response, messageContent);
    }

    /**
     * 将消息同步给发送者
     *
     * @param messageContent 消息内容
     * @param clientInfo     发送消息的客户端信息
     */
    @Override
    public void syncToSender(GroupChatMessageContent messageContent, ClientInfo clientInfo) {
        // 将消息发送给除了发送者以外的所有在线用户
        messageHelper.sendToUserExceptClient(messageContent.getFromId(), GroupEventCommand.MSG_GROUP, messageContent, messageContent);
    }

    /**
     * IM服务器权限检查
     *
     * @param fromId 消息发送者ID
     * @param toId   消息接收者ID
     * @param appId  应用ID
     * @return 权限检查结果
     */
    @Override
    public ResponseVO<Object> imServerPermissionCheck(String fromId, String toId, Integer appId) {
        // checkGroupMessage方法用于检查群聊消息的发送权限，如果检查不通过会抛出异常
        checkSendMessageService.checkGroupMessage(fromId, toId, appId);
        // 返回成功响应结果
        return ResponseVO.successResponse();
    }

    /**
     * 发送群聊消息
     *
     * @param req 发送群聊消息请求对象
     * @return SendMessageResp 发送群聊消息响应对象
     */
    @Override
    public SendMessageResp send(SendGroupMessageReq req) {
        // 创建一个SendMessageResp对象，用于存储发送消息的响应结果
        SendMessageResp sendMessageResp = new SendMessageResp();
        // 创建一个GroupChatMessageContent对象，用于存储群聊消息内容
        GroupChatMessageContent message = new GroupChatMessageContent();
        // 将请求中的属性值拷贝到message对象中
        BeanUtils.copyProperties(req, message);
        // 将群聊消息存储到消息存储服务中
        messageStoreService.storeGroupMessage(message);
        // 将消息的唯一标识设置到响应结果中
        sendMessageResp.setMessageKey(message.getMessageKey());
        // 将消息发送时间设置到响应结果中
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        // 向消息发送者的在线设备发送消息
        syncToSender(message, message);
        // 向群聊中的其他在线设备发送消息
        dispatchMessage(message);
        // 返回发送消息的响应结果
        return sendMessageResp;
    }
}