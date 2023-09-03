/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

import lombok.Getter;

@Getter
public enum ConversationErrorCode implements BaseExceptionEnum {

    CONVERSATION_UPDATE_PARAM_ERROR(50000, "会话修改参数错误"),

    ;

    private final int code;
    private final String msg;

    ConversationErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}