/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友事件命令枚举
 */
@Getter
public enum FriendshipEventCommand implements Command {

    FRIEND_ADD(0xBB8, "添加好友 3000"),

    FRIEND_UPDATE(0xBB9, "更新好友 3001"),

    FRIEND_DELETE(0xBBA, "删除好友 3002"),

    FRIEND_REQUEST(0xBBB, "好友申请 3003"),

    FRIEND_REQUEST_READ(0xBBC, "好友申请已读 3004"),

    FRIEND_REQUEST_APPROVE(0xBBD, "好友申请审批 3005"),

    FRIEND_BLACK_ADD(0xBBE, "添加黑名单 3006"),

    FRIEND_BLACK_DELETE(0xBBF, "移除黑名单 3007"),

    FRIEND_GROUP_ADD(0xBC0, "新建好友分组 3008"),

    FRIEND_GROUP_DELETE(0xBC1, "删除好友分组 3009"),

    FRIEND_GROUP_MEMBER_ADD(0xBC2, "好友分组添加成员 3010"),

    FRIEND_GROUP_MEMBER_DELETE(0xBC3, "好友分组移除成员 3011"),

    FRIEND_ALL_DELETE(0xBC4, "删除所有好友 3012"),

    ;

    private final int command;

    private final String desc;

    /**
     * 构造函数
     *
     * @param command 命令值
     * @param desc    命令描述
     */
    FriendshipEventCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}