/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 聊天消息回调
 */
@Data
public class ChatMessageAck {

    private String messageId;

    private Long messageSequence;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId, Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }
}