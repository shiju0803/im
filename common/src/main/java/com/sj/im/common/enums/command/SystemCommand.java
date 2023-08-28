/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 指令枚举
 */
public enum SystemCommand implements Command {

    /**
     * 登录 9000
     */
    LOGIN(0x2328),

    /**
     * 下线通知 用于多端互斥 9002
     */
    MUTUAL_LOGIN(0x232a),

    /**
     * 退出登录 9003
     */
    LOGOUT(0x232b),

    /**
     * 心跳 9999
     */
    PING(0x270f),
    ;

    private final int command;

    SystemCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return command;
    }
}
