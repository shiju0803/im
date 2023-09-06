/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.service.helper.MessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息同步服务
 */
@Service
public class MessageSyncService {

    @Resource
    private MessageHelper messageHelper;

    public void receiveMark(MessageReceiveAckContent ackContent) {
        messageHelper.sendToUser(ackContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, ackContent, ackContent.getAppId());
    }
}
