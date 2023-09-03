/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 离线消息内容类
 */
@Data
public class OfflineMessageContent {

    /**
     * 应用 ID
     */
    private Integer appId;

    /**
     * 消息密钥
     */
    private Long messageKey;

    /**
     * 消息体
     */
    private String messageBody;

    /**
     * 消息时间戳
     */
    private Long messageTime;

    /**
     * 额外信息
     */
    private String extra;

    /**
     * 删除标记
     */
    private Integer delFlag;

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
     * 消息随机数
     */
    private String messageRandom;

    /**
     * 会话类型
     */
    private Integer conversationType;

    /**
     * 会话 ID
     */
    private String conversationId;
}