/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 会话事件命令枚举
 */
@Getter
public enum ConversationEventCommand implements Command {

    CONVERSATION_DELETE(5000, "删除会话"),
    CONVERSATION_UPDATE(5001, "更新会话");

    private final int command;
    private final String desc;

    /**
     * 构造函数
     *
     * @param command 命令值
     */
    ConversationEventCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}