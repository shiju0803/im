/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 检查好友关系枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum CheckFriendShipTypeEnum {

    SINGLE(1, "单方校验"),

    BOTH(2, "双方校验"),

    ;

    private final int type;
    private final String desc;

    CheckFriendShipTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}