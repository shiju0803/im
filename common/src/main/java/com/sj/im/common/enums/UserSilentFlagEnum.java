/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 用户禁言状态枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum UserSilentFlagEnum {

    NORMAL(0, "正常"),

    MUTE(1, "禁言"),

    ;

    private final int code;
    private final String desc;

    UserSilentFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}