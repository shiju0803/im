/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

import lombok.Getter;

/**
 * 用户管理错误码
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum UserErrorCode implements BaseExceptionEnum {

    IMPORT_SIZE_BEYOND(20000, "导入數量超出上限"),

    USER_IS_NOT_EXIST(20001, "用户不存在"),

    SERVER_GET_USER_ERROR(20002, "服务获取用户失败"),

    MODIFY_USER_ERROR(20003, "更新用户失败"),

    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),

    ;

    private final int code;
    private final String msg;

    UserErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

