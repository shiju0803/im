/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import com.sj.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 撤回消息内容
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RecallMessageContent extends ClientInfo {

    /**
     * 消息唯一标识
     */
    private Long messageKey;

    /**
     * 发送者ID
     */
    private String fromId;

    /**
     * 接收者ID
     */
    private String toId;

    /**
     * 消息发送时间
     */
    private Long messageTime;

    /**
     * 消息序列号
     */
    private Long messageSequence;

    /**
     * 会话类型
     */
    private Integer conversationType;
}