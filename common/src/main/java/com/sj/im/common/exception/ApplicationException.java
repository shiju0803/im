package com.sj.im.common.exception;

/**
 * 全局异常处理类
 *
 * @author shiju
 */
public class ApplicationException extends RuntimeException {
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * error信息
     */
    private final String error;

    public ApplicationException(Integer code, String message) {
        super(message);
        this.code = code;
        this.error = message;
    }

    public ApplicationException(ApplicationExceptionEnum exceptionEnum) {
        super(exceptionEnum.getError());
        this.code = exceptionEnum.getCode();
        this.error = exceptionEnum.getError();
    }

    public Integer getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
