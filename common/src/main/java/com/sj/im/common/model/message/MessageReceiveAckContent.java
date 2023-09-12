/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import com.sj.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息接收确认内容类，继承自客户端信息类
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageReceiveAckContent extends ClientInfo {

    /**
     * 消息密钥
     */
    private Long messageKey;

    /**
     * 发送方 ID
     */
    private String fromId;

    /**
     * 接收方 ID
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