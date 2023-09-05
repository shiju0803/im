/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 存储群聊消息 DTO 类
 */
@Data
public class DoStoreGroupMessageDto {

    /**
     * 群聊消息内容
     */
    private GroupChatMessageContent groupChatMessageContent;

    /**
     * IM 消息体
     */
    private ImMessageBody messageBody;
}