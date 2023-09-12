/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 管道链接状态枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum ImConnectStatusEnum {

    ONLINE_STATUS(1, "在线"),

    OFFLINE_STATUS(2, "离线"),

    ;

    private final Integer code;
    private final String desc;

    ImConnectStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}