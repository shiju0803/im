/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.command;

import lombok.Getter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 命令类型枚举类
 */
@Getter
public enum CommandType {

    MESSAGE("1", "消息命令"),

    GROUP("2", "群组命令"),

    FRIEND("3", "好友命令"),

    USER("4", "用户命令"),

    ;

    public static void main(String[] args) {
        System.out.println(Integer.toHexString(1).toUpperCase());
    }

    private final String type;
    private final String desc;

    /**
     * 构造函数
     *
     * @param type 命令类型值
     */
    CommandType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据命令类型值获取对应的命令类型
     *
     * @param ordinal 命令类型值
     * @return 对应的命令类型，如果没有匹配的类型则返回null
     */
    public static CommandType getCommandType(String ordinal) {
        for (int i = 0; i < CommandType.values().length; i++) {
            if (CommandType.values()[i].getType().equals(ordinal)) {
                return CommandType.values()[i];
            }
        }
        return null;
    }
}
