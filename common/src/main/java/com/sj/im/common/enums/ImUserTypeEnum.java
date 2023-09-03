/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户类型枚举类
 */
@Getter
public enum ImUserTypeEnum {

    IM_USER(1),

    APP_ADMIN(100),

    ;

    private final int code;

    ImUserTypeEnum(int code) {
        this.code = code;
    }
}
