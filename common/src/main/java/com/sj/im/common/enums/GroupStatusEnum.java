/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 群状态枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum GroupStatusEnum {

    NORMAL(1, "正常"),

    DESTROY(2, "解散"),

    BAN(3, "封禁")

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     */
    public static GroupStatusEnum getEnum(Integer ordinal) {
        if (ordinal == null) {
            return null;
        }

        for (int i = 0; i < GroupStatusEnum.values().length; i++) {
            if (GroupStatusEnum.values()[i].getCode() == ordinal) {
                return GroupStatusEnum.values()[i];
            }
        }
        return null;
    }

    private final int code;
    private final String desc;

    GroupStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}