/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户事件命令枚举类
 */
@Getter
public enum UserEventCommand implements Command {

    USER_MODIFY(0xFA0, "用户修改command 4000"),

    USER_ONLINE_STATUS_CHANGE(0xFA1, "用户在线状态改变 4001"),

    USER_ONLINE_STATUS_CHANGE_NOTIFY(0xFA2, "用户在线状态通知报文 4002"),

    USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC(0xFA3, "用户在线状态通知同步报文 4003"),

    USER_ONLINE_STATUS_SET_CHANGE_NOTIFY(0xFA4, "用户自定义状态通知报文 4004"),

    USER_ONLINE_STATUS_SET_CHANGE_NOTIFY_SYNC(0xFA5, "用户自定义状态通知同步报文 4005"),

    ;

    private final int command;
    private final String desc;

    UserEventCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}