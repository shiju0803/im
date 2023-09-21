/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 用户登录方式枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum LoginTypeEnum {

    USERNAME_PASSWORD(1, "用户名密码登录"),

    SMS_CODE(2, "短信验证码登录"),

    ;

    private final int code;
    private final String desc;

    LoginTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}