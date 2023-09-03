/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户禁言状态枚举类
 */
@Getter
public enum UserSilentFlagEnum {
    /**
     * 0 正常；1 禁言。
     */
    NORMAL(0),

    MUTE(1),

    ;

    private final int code;

    UserSilentFlagEnum(int code) {
        this.code = code;
    }
}