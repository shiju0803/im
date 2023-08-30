/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common;

import com.sj.im.common.exception.ApplicationExceptionEnum;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 公共返回错误码
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {

    SUCCESS(200, "success"),
    /**
     * 服务器内部错误
     */
    SYSTEM_ERROR(90000, "服务器内部错误，请联系管理员"),
    /**
     * 参数校验错误
     */
    PARAMETER_ERROR(90001, "参数校验错误"),
    ;

    private final Integer code;
    private final String error;

    BaseErrorCode(Integer code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getError() {
        return error;
    }
}
