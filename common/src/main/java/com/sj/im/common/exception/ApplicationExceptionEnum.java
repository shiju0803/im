package com.sj.im.common.exception;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 全局异常处理的枚举
 */
public interface ApplicationExceptionEnum {
    /**
     * 状态码
     *
     * @return 状态码
     */
    int getCode();

    /**
     * error信息
     *
     * @return 错误信息
     */
    String getError();
}
