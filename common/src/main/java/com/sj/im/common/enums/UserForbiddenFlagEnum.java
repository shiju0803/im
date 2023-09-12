/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 用户禁用状态枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum UserForbiddenFlagEnum {

    NORMAL(0, "正常"),

    FORBIDDEN(1, "禁用"),

    ;

    private final int code;
    private final String desc;

    UserForbiddenFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}