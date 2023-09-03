/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群禁言类型枚举类
 */
@Getter
public enum GroupMuteTypeEnum {

    /**
     * 是否全员禁言，0 不禁言；1 全员禁言。
     */
    NOT_MUTE(0),

    MUTE(1),

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     */
    public static GroupMuteTypeEnum getEnum(Integer ordinal) {
        if (ordinal == null) {
            return null;
        }

        for (int i = 0; i < GroupMuteTypeEnum.values().length; i++) {
            if (GroupMuteTypeEnum.values()[i].getCode() == ordinal) {
                return GroupMuteTypeEnum.values()[i];
            }
        }
        return null;
    }

    private final int code;

    GroupMuteTypeEnum(int code) {
        this.code = code;
    }
}