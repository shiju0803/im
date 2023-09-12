/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * 群组事件命令枚举
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum GroupEventCommand implements Command {

    JOIN_GROUP(0x7D0, "推送申请入群通知 2000"),

    ADDED_MEMBER(0x7D1, "推送添加群成员，通知给所有管理员和本人 2001"),

    CREATED_GROUP(0x7D2, "推送创建群组通知，通知给所有人 2002"),

    UPDATED_GROUP(0x7D3, "推送更新群组通知，通知给所有人 2003"),

    EXIT_GROUP(0x7D4, "推送退出群组通知，通知给管理员和操作人 2004"),

    UPDATED_MEMBER(0x7D5, "推送修改群成员通知，通知给管理员和被操作人 2005"),

    DELETED_MEMBER(0x7D6, "推送删除群成员通知，通知给所有群成员和被踢人 2006"),

    DESTROY_GROUP(0x7D7, "推送解散群通知，通知所有人 2007"),

    TRANSFER_GROUP(0x7D8, "推送转让群主，通知所有人 2008"),

    MUTE_GROUP(0x7D9, "禁言群，通知所有人 2009"),

    SPEAK_GROUP_MEMBER(0x7DA, "禁言/解禁群成员，通知管理员和被操作人 2010"),

    MSG_GROUP(0x838, "群聊消息收发 2104"),

    MSG_GROUP_READ(0x83A, "发送消息已读 2106"),

    MSG_GROUP_READ_NOTIFY(0x805, "消息已读通知给同步端 2053"),

    MSG_GROUP_READ_RECEIPT(0x806, "消息已读回执，给原消息发送方 2054"),

    GROUP_MSG_ACK(0x7FF, "群聊消息ack 2047"),

    ;

    private final int command;
    private final String desc;

    /**
     * 构造函数
     *
     * @param command 命令值
     */
    GroupEventCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}
