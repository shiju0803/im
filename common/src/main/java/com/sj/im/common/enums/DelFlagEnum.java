/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 删除标识枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum DelFlagEnum {

    NORMAL(0, "正常"),

    DELETE(1, "已删除"),

    ;

    private final int code;
    private final String desc;

    DelFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}