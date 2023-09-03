/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import com.sj.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 已读消息内容类，继承自客户端信息类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageReadContent extends ClientInfo {

    /**
     * 消息序列号
     */
    private long messageSequence;

    /**
     * 发送方 ID
     */
    private String fromId;

    /**
     * 群组 ID
     */
    private String groupId;

    /**
     * 接收方 ID
     */
    private String toId;

    /**
     * 会话类型
     */
    private Integer conversationType;
}