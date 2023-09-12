/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 群聊消息内容类，继承自消息内容类
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupChatMessageContent extends MessageContent {

    /**
     * 群组 ID
     */
    private String groupId;

    /**
     * 群成员 ID 列表
     */
    private List<String> memberId;
}