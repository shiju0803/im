/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common;

import lombok.Getter;

/**
 * 客户端类型枚举
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum ClientType {

    WEBAPI(0, "webApi"),

    WEB(1, "web"),

    IOS(2, "ios"),

    ANDROID(3, "android"),

    WINDOWS(4, "windows"),

    MAC(5, "mac"),

    ;

    private final Integer code;

    private final String error;

    ClientType(Integer code, String error) {
        this.code = code;
        this.error = error;
    }
}
