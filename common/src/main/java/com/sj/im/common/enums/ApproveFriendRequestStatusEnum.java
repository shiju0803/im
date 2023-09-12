/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 审批好友申请枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum ApproveFriendRequestStatusEnum {

    AGREE(1, "同意"),

    REJECT(2, "拒绝"),

    ;

    private final int code;
    private final String desc;

    ApproveFriendRequestStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}