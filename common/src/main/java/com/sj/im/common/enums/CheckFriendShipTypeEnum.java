/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 检查好友关系枚举类
 */
@Getter
public enum CheckFriendShipTypeEnum {
    /**
     * 单方校验
     */
    SINGLE(1),

    /**
     * 双方校验
     */
    BOTH(2),

    ;

    private final int type;

    CheckFriendShipTypeEnum(int type) {
        this.type = type;
    }
}