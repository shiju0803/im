/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.group;

import lombok.Data;

/**
 * 群聊消息分发报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class GroupMessagePack {

    //客户端传的messageId
    private String messageId;

    private String messageKey;

    private String fromId;

    private String groupId;

    private int messageRandom;

    private long messageTime;

    private long messageSequence;

    private String messageBody;
    /**
     * 这个字段缺省或者为 0 表示需要计数，为 1 表示本条消息不需要计数，即右上角图标数字不增加
     */
    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;
}
