/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

@Getter
public enum ConversationTypeEnum {

    P2P(0, "单聊"),

    GROUP(1, "群聊"),

    ROBOT(2, "机器人"),

    OFFICIAL_ACCOUNT(3, "公众号"),

    ;

    private final int code;
    private final String desc;

    ConversationTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}