/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

@Getter
public enum CheckFriendShipTypeEnum {
    /**
     * 1 单方校验；2双方校验。
     */
    SINGLE(1),

    BOTH(2),
    ;

    private final int type;

    CheckFriendShipTypeEnum(int type) {
        this.type = type;
    }
}