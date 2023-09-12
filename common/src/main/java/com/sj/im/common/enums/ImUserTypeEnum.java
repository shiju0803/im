/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 用户类型枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum ImUserTypeEnum {

    IM_USER(1, "普通用户"),

    APP_ADMIN(100, "应用管理员"),

    ;

    private final int code;
    private final String desc;

    ImUserTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
