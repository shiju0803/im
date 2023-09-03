/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 媒体事件命令枚举
 */
public enum MediaEventCommand implements Command {

    CALL_VOICE(6000, "向对方拨打语音 6000"),

    CALL_VIDEO(6001, "向对方拨打视频 6001"),

    ACCEPT_CALL(6002, "同意请求 6002"),

    HANG_UP(6006, "挂断 6006"),

    REJECT_CALL(6007, "拒绝 6007"),

    CANCEL_CALL(6008, "取消呼叫 6008");

    private final Integer command;
    private final String desc;

    /**
     * 构造函数
     *
     * @param command 命令值
     * @param desc    命令描述
     */
    MediaEventCommand(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }

    @Override
    public int getCommand() {
        return this.command;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}