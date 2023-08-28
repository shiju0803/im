package com.sj.im.common.enums;

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
