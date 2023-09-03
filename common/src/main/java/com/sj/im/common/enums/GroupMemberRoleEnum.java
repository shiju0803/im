/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群成员角色枚举类
 */
@Getter
public enum GroupMemberRoleEnum {
    /**
     * 普通成员
     */
    ORDINARY(0),

    /**
     * 管理员
     */
    MANAGER(1),

    /**
     * 群主
     */
    OWNER(2),

    /**
     * 离开
     */
    LEAVE(3),

    ;


    private final int code;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     */
    public static GroupMemberRoleEnum getItem(int ordinal) {
        for (int i = 0; i < GroupMemberRoleEnum.values().length; i++) {
            if (GroupMemberRoleEnum.values()[i].getCode() == ordinal) {
                return GroupMemberRoleEnum.values()[i];
            }
        }
        return null;
    }

    GroupMemberRoleEnum(int code) {
        this.code = code;
    }
}