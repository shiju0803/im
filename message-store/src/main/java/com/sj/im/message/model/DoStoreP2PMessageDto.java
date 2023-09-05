/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.model;

import com.sj.im.common.model.message.MessageContent;
import com.sj.im.message.entry.ImMessageBodyEntity;
import lombok.Data;

@Data
public class DoStoreP2PMessageDto {

    /**
     * 消息内容
     */
    private MessageContent messageContent;

    /**
     * IM 消息体实体
     */
    private ImMessageBodyEntity imMessageBodyEntity;
}