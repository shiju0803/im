/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 同意好友枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum AllowFriendTypeEnum {

    NEED(2, "需要验证"),

    NOT_NEED(1, "不需要验证"),

    ;

    private final int code;
    private final String desc;

    AllowFriendTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}