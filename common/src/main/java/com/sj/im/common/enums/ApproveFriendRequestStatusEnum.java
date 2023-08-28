/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

@Getter
public enum ApproveFriendRequestStatusEnum {

    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    ApproveFriendRequestStatusEnum(int code) {
        this.code = code;
    }
}