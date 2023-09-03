/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

@Getter
public enum MessageCommand implements Command {

    MSG_P2P(0x44F, "单聊消息 1103"),

    MSG_READ(0x452, "发送消息已读 1106"),

    MSG_RECEIVE_ACK(1107, "消息收到ack 1107"),

    MSG_ACK(0x416, "单聊消息ack 1046"),

    MSG_RECALL(0x41A, "消息撤回 1050"),

    MSG_RECALL_ACK(0x41B, "消息撤回回报 1051"),

    MSG_RECALL_NOTIFY(0x41C, "消息撤回通知 1052"),

    MSG_READ_NOTIFY(0x41D, "消息已读通知给同步端 1053"),

    MSG_READ_RECEIPT(0x41E, "消息已读回执，给原消息发送方 1054"),

    ;

    private final int command;
    private final String desc;

    MessageCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}