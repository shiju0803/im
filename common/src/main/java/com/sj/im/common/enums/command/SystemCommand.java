/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * 系统命令枚举
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum SystemCommand implements Command {

    LOGIN(0x2328, "登录 9000"),

    LOGIN_ACK(0x2329, "登录ack 9001"),

    MUTUAL_LOGIN(0x232A, "下线通知 用于多端互斥 9002"),

    LOGOUT(0x232B, "退出登录 9003"),

    PING(0x270F, "心跳 9999"),

    ;

    private final int command;
    private final String desc;

    SystemCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}
