/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

import lombok.Getter;

/**
 * 消息错误枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum MessageErrorCode implements BaseExceptionEnum {

    SENDER_IS_MUTE(50001, "发送方被禁言"),

    SENDER_IS_FORBIDDEN(50002, "发送方被禁用"),

    MESSAGE_BODY_IS_NOT_EXIST(50003, "消息体不存在"),

    MESSAGE_RECALL_TIME_OUT(50004, "消息已超过可撤回时间"),

    MESSAGE_IS_RECALLED(50005, "消息已被撤回"),

    ;

    private final int code;
    private final String msg;

    MessageErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}