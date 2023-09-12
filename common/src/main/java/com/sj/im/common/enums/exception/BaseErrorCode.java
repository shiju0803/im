/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

/**
 * 公共返回错误码
 *
 * @author ShiJu
 * @version 1.0
 */
public enum BaseErrorCode implements BaseExceptionEnum {

    SUCCESS(200, "success"),

    SYSTEM_ERROR(90000, "服务器内部错误，请联系管理员"),

    PARAMETER_ERROR(90001, "参数校验错误"),

    ;

    private final Integer code;
    private final String msg;

    BaseErrorCode(Integer code, String msg) {
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
