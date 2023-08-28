package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 连接类型枚举
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
