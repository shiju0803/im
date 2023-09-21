/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

/**
 * 用户错误码枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
public enum AppUserErrorCode implements BaseExceptionEnum {

    REGISTER_ERROR(10000, "注册失败"),

    USER_REGISTERED(10001, "用户已注册"),

    USERNAME_OR_PASSWORD_ERROR(10002, "用户名或密码错误"),

    ;

    private final Integer code;
    private final String msg;

    AppUserErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误代码
     */
    @Override
    public int getCode() {
        return this.code;
    }

    /**
     * 错误提示信息
     */
    @Override
    public String getMsg() {
        return this.msg;
    }
}
