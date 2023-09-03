/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群状态枚举类
 */
@Getter
public enum GroupStatusEnum {

    /**
     * 1正常 2解散 其他待定比如封禁...
     */
    NORMAL(1),

    DESTROY(2),

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

    GroupStatusEnum(int code) {
        this.code = code;
    }
}