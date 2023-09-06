/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.codec.pack.message.ChatMessageAck;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.message.MessageContent;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.message.service.CheckSendMessageService;
import com.sj.im.service.message.service.MessageStoreService;
import com.sj.im.service.message.service.P2PMessageService;
import com.sj.im.service.message.web.rep.SendMessageReq;
import com.sj.im.service.message.web.resp.SendMessageResp;
import com.sj.im.service.seq.RedisSeq;
import com.sj.im.service.util.ConversationIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 私聊业务类
 */
@Slf4j
@Service
public class P2PMessageServiceImpl implements P2PMessageService {
    @Resource
    private CheckSendMessageService checkSendMessageService;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private MessageStoreService messageStoreService;
    @Resource
    private RedisSeq redisSeq;

    private final ThreadPoolExecutor threadPoolExecutor;

    {
        /**
         * 创建一个线程池，包含2个核心线程和4个最大线程，线程空闲时间为60秒，
         * 等待队列容量为100，使用LinkedBlockingDeque作为等待队列，线程工厂使用匿名内部类实现，
         * 通过AtomicInteger类保证线程名称的唯一性，线程为守护线程。
         */
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(2, 4, Runtime.getRuntime().availableProcessors(), TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("message-process-thread-" + num.getAndIncrement());
            return thread;
        });
    }

    /**
     * 处理消息
     *
     * @param content 消息内容
     */
    @Override
    public void process(MessageContent content) {
        log.info("消息开始处理：{}", content.getMessageId());
        MessageContent messageCache = messageStoreService.getMessageFromMessageIdCache(content.getAppId(), content.getMessageId(), MessageContent.class);
        if (ObjectUtil.isNotNull(messageCache)) {
            threadPoolExecutor.execute(() -> {
                // 回ack成功给自己
                ack(messageCache, ResponseVO.successResponse());
                // 发消息给同步其他在线端
                syncToSender(messageCache, messageCache);
                // 发消息给对方在线端
                List<ClientInfo> clientInfos = dispatchMessage(messageCache);

                if (ObjectUtil.isEmpty(clientInfos)) {
                    // 发送接收确认给发送方，要带上服务端发送的标识
                    receiverAck(messageCache);
                }
            });
            return;
        }

        // appId + seq + (from + to) groupId
        long seq = redisSeq.doGetSeq(content.getAppId() + ":" + SeqConstants.MESSAGE_SEQ + ":"
                + ConversationIdGenerate.generateP2PId(content.getFromId(), content.getToId()));
        content.setMessageSequence(seq);

        threadPoolExecutor.execute(() -> {
            messageStoreService.storeP2PMessage(content);
            // 回ack成功给自己
            ack(content, ResponseVO.successResponse());
            // 发消息给同步其他在线端
            syncToSender(content, content);
            // 发消息给对方在线端
            List<ClientInfo> clientInfos = dispatchMessage(content);

            // 将messageId缓存到Redis
            messageStoreService.setMessageFromMessageIdCache(content.getAppId(), content.getMessageId(), content);
            if (ObjectUtil.isEmpty(clientInfos)) {
                // 发送接收确认给发送方，要带上服务端发送的标识
                receiverAck(content);
            }
        });
    }

    /**
     * 分发消息给在线客户端
     *
     * @param messageContent 消息内容
     * @return 在线客户端列表
     */
    @Override
    public List<ClientInfo> dispatchMessage(MessageContent messageContent) {
        return messageHelper.sendToUser(messageContent.getToId(), MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
    }

    /**
     * 发送ack消息
     *
     * @param content    消息内容
     * @param responseVO 响应结果
     */
    @Override
    public void ack(MessageContent content, ResponseVO<Object> responseVO) {
        log.info("msg ack, msgId = {}, checkResult = {}", content.getMessageId(), responseVO.getCode());
        ChatMessageAck chatMessageAck = new ChatMessageAck(content.getMessageId(), content.getMessageSequence());
        responseVO.setData(chatMessageAck);
        // 发消息
        messageHelper.sendToUser(content.getFromId(), MessageCommand.MSG_ACK, responseVO, content);
    }

    /**
     * 发送接收确认给发送方
     *
     * @param messageContent 消息内容
     */
    @Override
    public void receiverAck(MessageContent messageContent) {
        // 创建接收确认消息
        MessageReceiveAckContent pack = new MessageReceiveAckContent();
        pack.setFromId(messageContent.getFromId());
        pack.setToId(messageContent.getToId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        pack.setServerSend(true);
        // 发送接收确认消息给发送方
        messageHelper.sendToUser(messageContent.getFromId(), MessageCommand.MSG_RECEIVE_ACK, pack,
                new ClientInfo(messageContent.getAppId(), messageContent.getAppId(), messageContent.getImei()));
    }

    /**
     * 同步消息给发送方在线客户端
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    @Override
    public void syncToSender(MessageContent messageContent, ClientInfo clientInfo) {
        // 发消息
        messageHelper.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_P2P, messageContent, messageContent);
    }

    /**
     * 检查发送方和接收方的权限
     *
     * @param fromId 发送方ID
     * @param toId   接收方ID
     * @param appId  应用ID
     * @return 响应结果
     */
    @Override
    public void imServerPermissionCheck(String fromId, String toId, Integer appId) {
        // 检查发送者是否被禁言或屏蔽
        checkSendMessageService.checkSenderForbiddenAndMute(fromId, appId);
        // 检查好友关系是否正常
        checkSendMessageService.checkFriendShip(fromId, toId, appId);
    }

    /**
     * 发送消息
     *
     * @param req 请求参数
     * @return 发送结果
     */
    @Override
    public SendMessageResp send(SendMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req, message);
        // 插入数据
        messageStoreService.storeP2PMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        // 发消息给同步在线端
        syncToSender(message, message);
        // 发消息给对方在线端
        dispatchMessage(message);
        return sendMessageResp;
    }
}
