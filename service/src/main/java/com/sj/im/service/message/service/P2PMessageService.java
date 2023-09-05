/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.service.message.web.rep.SendMessageReq;
import com.sj.im.service.message.web.resp.SendMessageResp;

import java.util.List;

public interface P2PMessageService {

    /**
     * 处理消息
     *
     * @param messageContent 消息内容
     */
    void process(MessageContent messageContent);

    /**
     * 分发消息给在线客户端
     *
     * @param messageContent 消息内容
     * @return 在线客户端列表
     */
    List<ClientInfo> dispatchMessage(MessageContent messageContent);

    /**
     * 发送ack消息
     *
     * @param messageContent 消息内容
     * @param responseVO     响应结果
     */
    void ack(MessageContent messageContent, ResponseVO<Object> responseVO);

    /**
     * 发送接收确认给发送方
     *
     * @param messageContent 消息内容
     */
    void receiverAck(MessageContent messageContent);

    /**
     * 同步消息给发送方在线客户端
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    void syncToSender(MessageContent messageContent, ClientInfo clientInfo);

    /**
     * 检查发送方和接收方的权限
     *
     * @param fromId 发送方ID
     * @param toId   接收方ID
     * @param appId  应用ID
     * @return 响应结果
     */
    void imServerPermissionCheck(String fromId, String toId, Integer appId);

    /**
     * 发送消息
     *
     * @param req 请求参数
     * @return 发送结果
     */
    SendMessageResp send(SendMessageReq req);
}