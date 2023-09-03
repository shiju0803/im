/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 管道链接状态枚举类
 */
@Getter
public enum ImConnectStatusEnum {

    /**
     * 管道链接状态,1=在线，2=离线。
     */
    ONLINE_STATUS(1),

    OFFLINE_STATUS(2),

    ;

    private final Integer code;

    ImConnectStatusEnum(Integer code) {
        this.code = code;
    }
}