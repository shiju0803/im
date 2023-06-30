package com.sj.im.common.enums;

/**
 * 链接类型枚举
 *
 * @author shiju
 */
public enum ClientType {
    /**
     * 网页Api
     */
    WEBAPI(0, "webApi"),
    /**
     * 网页
     */
    WEB(1, "web"),
    /**
     * ios
     */
    IOS(2, "ios"),
    /**
     * 安卓
     */
    ANDROID(3, "android"),
    /**
     * windows
     */
    WINDOWS(4, "windows"),
    /**
     * mac
     */
    MAC(5, "mac"),
    ;

    private final Integer code;
    private final String error;

    ClientType(Integer code, String error) {
        this.code = code;
        this.error = error;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }
}
