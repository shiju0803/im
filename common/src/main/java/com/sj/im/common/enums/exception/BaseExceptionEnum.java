/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

public interface BaseExceptionEnum {
    /**
     * 错误代码
     */
    int getCode();

    /**
     * 错误提示信息
     */
    String getMsg();
}
