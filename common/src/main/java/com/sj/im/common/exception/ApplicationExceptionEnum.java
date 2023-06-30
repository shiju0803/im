package com.sj.im.common.exception;

/**
 * 全局异常处理的枚举
 * @author shiju
 */

public interface ApplicationExceptionEnum {
    /**
     * 状态码
     *
     * @return 状态码
     */
    Integer getCode();

    /**
     * error信息
     *
     * @return 错误信息
     */
    String getError();
}
