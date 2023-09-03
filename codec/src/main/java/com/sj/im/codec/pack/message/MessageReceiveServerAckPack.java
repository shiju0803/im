/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 聊天消息已接收回调
 */
@Data
public class MessageReceiveServerAckPack {

    /**
     * 消息键
     */
    private Long messageKey;

    /**
     * 发送方ID
     */
    private String fromId;

    /**
     * 接收方ID
     */
    private String toId;

    /**
     * 消息序列号
     */
    private Long messageSequence;

    /**
     * 是否是服务端发送
     */
    private Boolean serverSend;
}
