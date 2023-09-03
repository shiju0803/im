/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 存储 P2P 消息 DTO 类
 */
@Data
public class DoStoreP2PMessageDto {

    /**
     * 消息内容
     */
    private MessageContent messageContent;

    /**
     * IM 消息体
     */
    private ImMessageBody messageBody;

}