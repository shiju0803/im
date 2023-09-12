/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service;

import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.message.GroupChatMessageContent;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.service.group.web.req.SendGroupMessageReq;
import com.sj.im.service.message.web.resp.SendMessageResp;

/**
 * 群消息服务类
 *
 * @author ShiJu
 * @version 1.0
 */
public interface GroupMessageService {

    /**
     * 处理群聊消息
     *
     * @param messageContent 群聊消息内容
     */
    void process(GroupChatMessageContent messageContent);

    /**
     * 分发群聊消息
     *
     * @param messageContent 群聊消息内容
     */
    void dispatchMessage(GroupChatMessageContent messageContent);

    /**
     * 对消息进行确认操作，即向消息发送方发送确认回执
     *
     * @param messageContent 消息内容
     * @param response       响应数据对象
     */
    void ack(MessageContent messageContent, ResponseVO<Object> response);

    /**
     * 将消息同步给发送者
     *
     * @param messageContent 消息内容
     * @param clientInfo     发送消息的客户端信息
     */
    void syncToSender(GroupChatMessageContent messageContent, ClientInfo clientInfo);

    /**
     * IM服务器权限检查
     *
     * @param fromId 消息发送者ID
     * @param toId   消息接收者ID
     * @param appId  应用ID
     * @return 权限检查结果
     */
    ResponseVO<Object> imServerPermissionCheck(String fromId, String toId, Integer appId);

    /**
     * 发送群聊消息
     *
     * @param req 发送群聊消息请求对象
     * @return SendMessageResp 发送群聊消息响应对象
     */
    SendMessageResp send(SendGroupMessageReq req);
}
