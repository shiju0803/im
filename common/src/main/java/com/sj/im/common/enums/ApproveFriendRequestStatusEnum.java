/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 审批好友申请枚举类
 */
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