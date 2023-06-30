package com.sj.im.common.enums;

import com.sj.im.common.exception.ApplicationExceptionEnum;

/**
 * 公共返回错误码
 *
 * @author shiju
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {
    /**
     * success
     */
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
    public Integer getCode() {
        return code;
    }

    @Override
    public String getError() {
        return error;
    }
}
