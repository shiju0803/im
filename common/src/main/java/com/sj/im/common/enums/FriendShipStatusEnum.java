package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 好友关系状态枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum FriendShipStatusEnum {

    FRIEND_STATUS_NO_FRIEND(0, "未添加"),

    FRIEND_STATUS_NORMAL(1, "正常"),

    FRIEND_STATUS_DELETE(2, "删除"),

    BLACK_STATUS_NORMAL(1, "正常"),

    BLACK_STATUS_BLACKED(2, "拉黑"),

    ;

    private final int code;
    private final String desc;

    FriendShipStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
