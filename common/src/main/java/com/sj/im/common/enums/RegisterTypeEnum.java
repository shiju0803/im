/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 用户注册类型枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum RegisterTypeEnum {

    MOBILE(1, "手机号注册"),

    USER_NAME(2, "用户名注册"),

    ;

    private final int code;
    private final String desc;

    RegisterTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}