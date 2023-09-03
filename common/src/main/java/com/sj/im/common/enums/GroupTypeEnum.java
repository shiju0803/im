/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群类型枚举类
 */
@Getter
public enum GroupTypeEnum {

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    PRIVATE(1),

    PUBLIC(2),

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     */
    public static GroupTypeEnum getEnum(Integer ordinal) {
        if (ordinal == null) {
            return null;
        }

        for (int i = 0; i < GroupTypeEnum.values().length; i++) {
            if (GroupTypeEnum.values()[i].getCode() == ordinal) {
                return GroupTypeEnum.values()[i];
            }
        }
        return null;
    }

    private final int code;

    GroupTypeEnum(int code) {
        this.code = code;
    }
}