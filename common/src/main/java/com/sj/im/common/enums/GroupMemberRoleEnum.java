/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 群成员角色枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum GroupMemberRoleEnum {

    ORDINARY(0, "普通成员"),

    MANAGER(1, "管理员"),

    OWNER(2, "群主"),

    LEAVE(3, "离开"),

    ;


    private final int code;
    private final String desc;

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

    GroupMemberRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}